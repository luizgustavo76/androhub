package com.androhub;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

public class MainPage extends Activity {

    private ListView listFeed;
    private String username = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);
        
        username = getIntent().getStringExtra("username");
        listFeed = (ListView) findViewById(R.id.listFeed);
        
        List<FeedItem> itensFeed = new ArrayList<FeedItem>();

        try {
            String jsonResposta = request.requestHTTP("https://api.github.com/users/" + username + "/received_events/public", "get", new JSONObject());
            JSONArray jsonArray = new JSONArray(jsonResposta);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                
                String type = item.getString("type");
                JSONObject actor = item.getJSONObject("actor");
                String actorLogin = actor.getString("login");
                String avatarUrl = actor.has("avatar_url") ? actor.getString("avatar_url") : "";
                
                String repoName = item.getJSONObject("repo").getString("name");
                String createdAt = item.has("created_at") ? item.getString("created_at") : "";

                String actionText = "";

                if (type.equals("PushEvent")) {
                    actionText = "pushed to";
                } else if (type.equals("WatchEvent")) {
                    actionText = "starred";
                } else if (type.equals("ReleaseEvent")) {
                    actionText = "published a release on";
                } else if (type.equals("IssueCommentEvent")) {
                    actionText = "commented on an issue in";
                } else {
                    actionText = "action " + type + " in";
                }

                FeedItem feedItem = new FeedItem(
                    actorLogin,
                    actionText,
                    createdAt, 
                    repoName,
                    "Java",
                    "★ 0",
                    avatarUrl
                );

                itensFeed.add(feedItem);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        FeedAdapter adapter = new FeedAdapter(this, itensFeed);
        listFeed.setAdapter(adapter);
    }
}