package com.androhub;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PullRequests extends Activity {

    private String repoName = "";
    private String token = "";
    private ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listView = new ListView(this);
        setContentView(listView);

        if (getIntent() != null) {
            if (getIntent().hasExtra("RepoName")) repoName = getIntent().getStringExtra("RepoName");
            if (getIntent().hasExtra("token")) token = getIntent().getStringExtra("token");
        }

        loadPRs();
    }

    private void loadPRs() {
        new Thread(new Runnable() {
            public void run() {
                String targetUrl = "https://api.github.com/repos/" + repoName + "/pulls?state=open";
                String jsonRaw = request.requestHTTP(targetUrl, "get", new JSONObject(), token);

                final List<String> prList = new ArrayList<String>();

                try {
                    if (jsonRaw != null && jsonRaw.trim().length() > 0) {
                        String trimmed = jsonRaw.trim();

                        if (trimmed.startsWith("{")) {
                            JSONObject errObj = new JSONObject(trimmed);
                            String msg = errObj.optString("message", "Error loading PRs");
                            prList.add("Error: " + msg);
                        } else if (trimmed.startsWith("[")) {
                            JSONArray prArray = new JSONArray(trimmed);

                            if (prArray.length() == 0) {
                                prList.add("No open Pull Requests found.");
                            } else {
                                for (int i = 0; i < prArray.length(); i++) {
                                    JSONObject pr = prArray.getJSONObject(i);
                                    int number = pr.optInt("number", 0);
                                    String title = pr.optString("title", "No Title");

                                    JSONObject user = pr.optJSONObject("user");
                                    String author = (user != null) ? user.optString("login", "unknown") : "unknown";

                                    prList.add("#" + number + " - " + title + " (" + author + ")");
                                }
                            }
                        }
                    } else {
                        prList.add("Empty response from server.");
                    }
                } catch (Exception e) {
                    Log.e("ViewPRs", "Error parsing PRs", e);
                    prList.add("Parse error: " + e.getMessage());
                }

                runOnUiThread(new Runnable() {
                    public void run() {
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                            PullRequests.this,
                            android.R.layout.simple_list_item_1,
                            prList
                        );
                        listView.setAdapter(adapter);
                    }
                });
            }
        }).start();
    }
}