package com.androhub;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
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
        
        List<String> itensFeed = new ArrayList<String>();

        try {
            String jsonResposta = request.requestHTTP("https://api.github.com/users/" + username + "/received_events/public", "get", new JSONObject());
            JSONArray jsonArray = new JSONArray(jsonResposta);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                
                String type = item.getString("type");
                String actorLogin = item.getJSONObject("actor").getString("login");
                String repoName = item.getJSONObject("repo").getString("name");

                if (type.equals("PushEvent")) {
                    itensFeed.add(actorLogin + " pushed to " + repoName);
                } else if (type.equals("WatchEvent")) {
                    itensFeed.add(actorLogin + " starred " + repoName);
                } else if (type.equals("ReleaseEvent")) {
                    itensFeed.add(actorLogin + " published a press release on " + repoName);
                } else if (type.equals("IssueCommentEvent")) {
                    itensFeed.add(actorLogin + " commented on an issue in " + repoName);
                } else {
                    itensFeed.add(actorLogin + " carried out the action " + type + " em " + repoName);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1, 
            itensFeed
        );
        listFeed.setAdapter(adapter);
    }
}