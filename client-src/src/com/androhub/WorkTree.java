package com.androhub;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class WorkTree extends Activity {
    private String username = "";
    private String repoName = "";
    private String token = "";
    private String path = "";
    private String url = "";
    private ListView listView;
    private List<RepoItem> itemList = new ArrayList<RepoItem>();

    public static class RepoItem {
        String name;
        String type;

        public RepoItem(String name, String type) {
            this.name = name;
            this.type = type;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.work_tree);

        listView = (ListView) findViewById(R.id.listTree);
        TextView txtRepoName = (TextView) findViewById(R.id.txtRepoName);

        Intent intent = getIntent();
        if (intent != null) {
            username = intent.getStringExtra("username") != null ? intent.getStringExtra("username") : "";
            repoName = intent.getStringExtra("repo_name") != null ? intent.getStringExtra("repo_name") : "";
            token = intent.getStringExtra("token") != null ? intent.getStringExtra("token") : "";
            path = intent.getStringExtra("path") != null ? intent.getStringExtra("path") : "";
        }

        if (!path.equals("")) {
            url = "https://api.github.com/repos/" + username + "/" + repoName + "/contents/" + path;
        } else {
            url = "https://api.github.com/repos/" + username + "/" + repoName + "/contents";
        }

        if (txtRepoName != null && !username.equals("") && !repoName.equals("")) {
            txtRepoName.setText(username + " / " + repoName);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RepoItem itemClicked = (RepoItem) parent.getItemAtPosition(position);
                
                if ("dir".equals(itemClicked.type) || "tree".equals(itemClicked.type)) {
                    String newPath = path.equals("") ? itemClicked.name : path + "/" + itemClicked.name;
                    Intent intentTree = new Intent(WorkTree.this, WorkTree.class);
                    intentTree.putExtra("username", username);
                    intentTree.putExtra("repo_name", repoName);
                    intentTree.putExtra("token", token);
                    intentTree.putExtra("path", newPath);
                    startActivity(intentTree);
                }
                if ("file".equals(itemClicked.type)){
                    Intent intentFile = new Intent(WorkTree.this, ViewFile.class);
                    intentFile.putExtra("RepoName", repoName);
                    intentFile.putExtra("token", token);
                    intentFile.putExtra("username", username);
                    intentFile.putExtra("fileName", itemClicked.name);
                    intentFile.putExtra("path", path);
                    startActivity(intentFile);
                }
            }
        });

        if (!username.equals("") && !repoName.equals("")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    fetchContents();
                }
            }).start();
        }
    }

    private void fetchContents() {
        try {
            String responseText = request.requestHTTP(url, "get", new JSONObject(), token);
            if (responseText != null && !responseText.equals("")) {
                JSONArray contentsArray = new JSONArray(responseText);

                itemList.clear();
                for (int i = 0; i < contentsArray.length(); i++) {
                    JSONObject repoObject = contentsArray.getJSONObject(i);
                    String name = repoObject.optString("name", "");
                    String type = repoObject.optString("type", "");

                    itemList.add(new RepoItem(name, type));
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (listView != null) {
                            listView.setAdapter(new RepoAdapter(itemList));
                        }
                    }
                });
            }
        } catch (Exception e) {
            Log.e("WorkTreeError", "Erro ao buscar dados da API: " + e.getMessage());
        }
    }

    private class RepoAdapter extends BaseAdapter {
        private final List<RepoItem> items;

        public RepoAdapter(List<RepoItem> items) {
            this.items = items;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout itemLayout;
            TextView typeTextView;
            TextView nameTextView;

            if (convertView == null) {
                itemLayout = new LinearLayout(parent.getContext());
                itemLayout.setOrientation(LinearLayout.HORIZONTAL);
                itemLayout.setPadding(32, 24, 32, 24);

                typeTextView = new TextView(parent.getContext());
                typeTextView.setTextSize(14);
                typeTextView.setPadding(0, 0, 24, 0);
                typeTextView.setTextColor(0xFF8B949E);

                nameTextView = new TextView(parent.getContext());
                nameTextView.setTextSize(16);
                nameTextView.setTextColor(0xFFC9D1D9);

                itemLayout.addView(typeTextView);
                itemLayout.addView(nameTextView);
            } else {
                itemLayout = (LinearLayout) convertView;
                typeTextView = (TextView) itemLayout.getChildAt(0);
                nameTextView = (TextView) itemLayout.getChildAt(1);
            }

            RepoItem item = items.get(position);
            typeTextView.setText("[" + item.type.toUpperCase() + "]");
            nameTextView.setText(item.name);

            return itemLayout;
        }
    }
}