package com.androhub;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class ViewReleases extends Activity {

    private String repoName = "";
    private String token = "";
    private ListView listView;
    private ReleaseAdapter adapter;
    private List<ReleaseAsset> assetList = new ArrayList<ReleaseAsset>();

    private static class ReleaseAsset {
        String name;
        String downloadUrl;
        long size;

        ReleaseAsset(String name, String downloadUrl, long size) {
            this.name = name;
            this.downloadUrl = downloadUrl;
            this.size = size;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT));

        listView = new ListView(this);
        layout.addView(listView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT));

        setContentView(layout);

        if (getIntent() != null) {
            if (getIntent().hasExtra("RepoName")) repoName = getIntent().getStringExtra("RepoName");
            if (getIntent().hasExtra("token")) token = getIntent().getStringExtra("token");
        }

        adapter = new ReleaseAdapter();
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ReleaseAsset asset = assetList.get(position);
                new DownloadTask().execute(asset.downloadUrl, asset.name);
            }
        });

        loadReleases();
    }

    private void loadReleases() {
        new Thread(new Runnable() {
            public void run() {
                String targetUrl = "https://api.github.com/repos/" + repoName + "/releases";
                String jsonRaw = request.requestHTTP(targetUrl, "get", new JSONObject(), token);

                try {
                    if (jsonRaw != null && jsonRaw.length() > 0) {
                        JSONArray releasesArray = new JSONArray(jsonRaw);
                        assetList.clear();

                        for (int i = 0; i < releasesArray.length(); i++) {
                            JSONObject release = releasesArray.getJSONObject(i);
                            JSONArray assets = release.optJSONArray("assets");

                            if (assets != null) {
                                for (int j = 0; j < assets.length(); j++) {
                                    JSONObject asset = assets.getJSONObject(j);
                                    String name = asset.optString("name", "Unknown File");
                                    String downloadUrl = asset.optString("browser_download_url", "");
                                    long size = asset.optLong("size", 0);

                                    if (downloadUrl.length() > 0) {
                                        assetList.add(new ReleaseAsset(name, downloadUrl, size));
                                    }
                                }
                            }
                        }

                        runOnUiThread(new Runnable() {
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                } catch (Exception e) {
                    Log.e("ViewReleases", "Error parsing releases", e);
                }
            }
        }).start();
    }

    private class ReleaseAdapter extends BaseAdapter {

        public int getCount() {
            return assetList.size();
        }

        public Object getItem(int position) {
            return assetList.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout itemLayout;
            TextView txtName;
            TextView txtSize;

            if (convertView == null) {
                itemLayout = new LinearLayout(ViewReleases.this);
                itemLayout.setOrientation(LinearLayout.VERTICAL);
                itemLayout.setPadding(15, 15, 15, 15);

                txtName = new TextView(ViewReleases.this);
                txtName.setTextSize(16);
                txtName.setTag("txtName");

                txtSize = new TextView(ViewReleases.this);
                txtSize.setTextSize(12);
                txtSize.setTag("txtSize");

                itemLayout.addView(txtName);
                itemLayout.addView(txtSize);
            } else {
                itemLayout = (LinearLayout) convertView;
                txtName = (TextView) itemLayout.findViewWithTag("txtName");
                txtSize = (TextView) itemLayout.findViewWithTag("txtSize");
            }

            ReleaseAsset asset = assetList.get(position);
            txtName.setText(asset.name);
            if (asset.size >  1048576){
                txtSize.setText((asset.size / 1048576) + " MB");
            }else{
                txtSize.setText((asset.size / 1024) + " KB");
            }
            return itemLayout;
        }
    }

    private class DownloadTask extends AsyncTask<String, Integer, String> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ViewReleases.this);
            progressDialog.setMessage("Downloading file...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String fileUrl = params[0];
            String fileName = params[1];
            InputStream input = null;
            FileOutputStream output = null;
            HttpURLConnection connection = null;

            try {
                URL url = new URL(fileUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("User-Agent", "AndroHubApp/1.0 (Android 1.5)");
                connection.setInstanceFollowRedirects(true);
                connection.connect();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode();
                }

                int fileLength = connection.getContentLength();
                input = connection.getInputStream();
                File downloadsDir = new File(Environment.getExternalStorageDirectory(), "Download");
                if (!downloadsDir.exists()) {
                    downloadsDir.mkdirs();
                }

                File file = new File(downloadsDir, fileName);
                output = new FileOutputStream(file);

                byte[] data = new byte[4096];
                long total = 0;
                int count;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    if (fileLength > 0) {
                        publishProgress((int) (total * 100 / fileLength));
                    }
                    output.write(data, 0, count);
                }

                output.flush();
                return "Saved to: " + file.getAbsolutePath();

            } catch (Exception e) {
                return "Download failed: " + e.getMessage();
            } finally {
                try {
                    if (output != null) output.close();
                    if (input != null) input.close();
                } catch (Exception ignored) {
                }
                if (connection != null) connection.disconnect();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            Toast.makeText(ViewReleases.this, result, Toast.LENGTH_LONG).show();
        }
    }
}