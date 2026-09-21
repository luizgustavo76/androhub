package com.androhub;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
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

        ListView listView = (ListView) findViewById(R.id.listTree);
        TextView txtRepoName = (TextView) findViewById(R.id.txtRepoName);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        repoName = intent.getStringExtra("repo_name");
        token = intent.getStringExtra("token");

        if (txtRepoName != null && username != null && repoName != null) {
            txtRepoName.setText(username + " / " + repoName);
        }

        List<RepoItem> itemList = new ArrayList<>();

        if (username != null && !username.equals("") && repoName != null && !repoName.equals("")) {
            try {
                String responseText = request.requestHTTP("https://api.github.com/repos/" + username + "/" + repoName + "/contents", "get", new JSONObject(), token);
                if (responseText != null && !responseText.equals("")) {
                    JSONArray contentsArray = new JSONArray(responseText);

                    for (int i = 0; i < contentsArray.length(); i++) {
                        JSONObject repoObject = contentsArray.getJSONObject(i);
                        String name = repoObject.optString("name", "");
                        String type = repoObject.optString("type", "");

                        itemList.add(new RepoItem(name, type));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (listView != null) {
            listView.setAdapter(new RepoAdapter(itemList));
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