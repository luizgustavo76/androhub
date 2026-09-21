package com.androhub;

import android.content.Intent;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.view.View;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class ReposActivity extends Activity {
    private ListView listRepos;
    private String username = "";
    private String token = "";
    private Button btnFeed;
    
    private List<FeedItem> itemsRepo = new ArrayList<FeedItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);

        btnFeed = (Button) findViewById(R.id.btnFeed);
        if (btnFeed != null) {
            btnFeed.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    Intent intent = new Intent(ReposActivity.this, MainPage.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                }
            });
        }

        listRepos = (ListView) findViewById(R.id.listFeed); 
        
        if (listRepos != null) {
            listRepos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position < itemsRepo.size()) {
                        FeedItem clickedRepo = itemsRepo.get(position);
                        Intent intent = new Intent(ReposActivity.this, WorkTree.class);
                        intent.putExtra("repo_name", clickedRepo.getRepoName()); 
                        intent.putExtra("username", username);
                        intent.putExtra("token", token);
                        startActivity(intent);
                    }
                }
            });
        }

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

        new FetchReposTask().execute();
    }

    private class FetchReposTask extends AsyncTask<Void, Void, List<FeedItem>> {
        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(ReposActivity.this);
            dialog.setMessage("Loading repos...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected List<FeedItem> doInBackground(Void... params) {
            List<FeedItem> fetchedItems = new ArrayList<FeedItem>();

            try {
                String urlRepos = "https://api.github.com/users/" + username + "/repos";
                
                String jsonResponse = request.requestHTTP(urlRepos, "GET", null, token);
                
                if (jsonResponse != null && jsonResponse.startsWith("[")) {
                    JSONArray jsonArray = new JSONArray(jsonResponse);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonRepo = jsonArray.getJSONObject(i);
                        String repoName = jsonRepo.optString("name", "");
                        String description = jsonRepo.optString("description", "");
                        JSONObject owner = jsonRepo.optJSONObject("owner");
                        String avatarUrl = (owner != null) ? owner.optString("avatar_url", "") : "";
                        
                        FeedItem repoItem = new FeedItem(
                            username,    
                            "",          
                            "",          
                            repoName,    
                            description, 
                            "",          
                            "",          
                            avatarUrl    
                        );
                        fetchedItems.add(repoItem);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return fetchedItems;
        }

        @Override
        protected void onPostExecute(List<FeedItem> result) {
            super.onPostExecute(result);
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }

            itemsRepo.clear();
            if (result != null) {
                itemsRepo.addAll(result);
            }

            FeedAdapter adapter = new FeedAdapter(ReposActivity.this, itemsRepo);
            if (listRepos != null) {
                listRepos.setAdapter(adapter);
            }
        }
    }
}