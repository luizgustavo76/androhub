package com.androhub;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class MainPage extends Activity {

    private ListView listFeed;
    private String username = "";
    private String token = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);
        
        listFeed = (ListView) findViewById(R.id.listFeed);

        username = getIntent().getStringExtra("username");
        if (username != null) {
            username = username.trim();
        }

        SharedPreferences prefs = getSharedPreferences("AndroHubPrefs", MODE_PRIVATE);
        token = prefs.getString("token", "");
        if (token.length() == 0) {
            String intentToken = getIntent().getStringExtra("token");
            if (intentToken != null) {
                token = intentToken.trim();
            }
        }

        // Executa a busca em background sem travar a interface
        new FetchFeedTask().execute();
    }

    private class FetchFeedTask extends AsyncTask<Void, Void, List<FeedItem>> {
        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(MainPage.this);
            dialog.setMessage("Loading feed...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected List<FeedItem> doInBackground(Void... params) {
            List<FeedItem> itensFeed = new ArrayList<FeedItem>();

            try {
                String urlFeed = "https://api.github.com/users/" + username + "/received_events/public";
                String jsonResponse = request.requestHTTP(urlFeed, "GET", null, token);

                if (jsonResponse != null && jsonResponse.startsWith("[")) {
                    JSONArray jsonArray = new JSONArray(jsonResponse);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject item = jsonArray.getJSONObject(i);
                        
                        String type = item.optString("type", "");
                        
                        JSONObject actor = item.optJSONObject("actor");
                        String actorLogin = (actor != null) ? actor.optString("login", "") : "";
                        String avatarUrl = (actor != null) ? actor.optString("avatar_url", "") : "";
                        
                        JSONObject repo = item.optJSONObject("repo");
                        String repoName = (repo != null) ? repo.optString("name", "") : "";
                        
                        String createdAt = item.optString("created_at", "");
                        String language = "";
                        String description = "";
                        String stars = "★ 0";

                        if (repoName.length() > 0) {
                            try {
                                String urlRepo = "https://api.github.com/repos/" + repoName;
                                String repoResp = request.requestHTTP(urlRepo, "GET", null, token);
                                if (repoResp != null && repoResp.startsWith("{")) {
                                    JSONObject jsonRepo = new JSONObject(repoResp);
                                    description = jsonRepo.optString("description", "");
                                    
                                    if (!jsonRepo.isNull("language")) {
                                        language = jsonRepo.optString("language", "");
                                    }
                                    
                                    int stargazers = jsonRepo.optInt("stargazers_count", 0);
                                    stars = "★ " + stargazers;
                                }
                            } catch (Exception e) {
                                description = "";
                            }
                        }

                        String actionText = "";
                        if (type.equals("PushEvent")) {
                            actionText = "pushed to";
                        } else if (type.equals("WatchEvent")) {
                            actionText = "starred the repository";
                        } else if (type.equals("CreateEvent")) {
                            actionText = "created a repository";
                        } else if (type.equals("ForkEvent")) {
                            actionText = "forked";
                        } else if (type.equals("ReleaseEvent")) {
                            actionText = "published a release on";
                        } else if (type.equals("IssueCommentEvent")) {
                            actionText = "commented on an issue in";
                        } else {
                            actionText = type;
                        }

                        FeedItem feedItem = new FeedItem(
                            actorLogin,
                            actionText,
                            createdAt, 
                            repoName,
                            description,
                            language,
                            stars,
                            avatarUrl
                        );

                        itensFeed.add(feedItem);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return itensFeed;
        }

        @Override
        protected void onPostExecute(List<FeedItem> result) {
            super.onPostExecute(result);
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }

            // Atualiza a lista na UI Thread
            FeedAdapter adapter = new FeedAdapter(MainPage.this, result);
            listFeed.setAdapter(adapter);
        }
    }
}