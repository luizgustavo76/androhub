package com.androhub;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class FeedAdapter extends BaseAdapter {

    private Context context;
    private List<FeedItem> items;
    private Handler handler = new Handler();

    public FeedAdapter(Context context, List<FeedItem> items) {
        this.context = context;
        this.items = items;
    }

    public int getCount() {
        return items.size();
    }

    public Object getItem(int position) {
        return items.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_feed, null);
        }

        FeedItem item = items.get(position);

        TextView txtUser = (TextView) convertView.findViewById(R.id.txtUser);
        TextView txtAction = (TextView) convertView.findViewById(R.id.txtAction);
        TextView txtTime = (TextView) convertView.findViewById(R.id.txtTime);
        TextView txtRepoName = (TextView) convertView.findViewById(R.id.txtRepoName);
        TextView txtLang = (TextView) convertView.findViewById(R.id.txtLang);
        TextView txtStars = (TextView) convertView.findViewById(R.id.txtStars);
        final ImageView feedAvatar = (ImageView) convertView.findViewById(R.id.feedAvatar);

        txtUser.setText(item.getUser());
        txtAction.setText(item.getAction());
        txtTime.setText(item.getTime());
        txtRepoName.setText(item.getRepoName());
        txtLang.setText(item.getLang());
        txtStars.setText(item.getStars());

        feedAvatar.setImageResource(android.R.drawable.ic_menu_gallery);

        String urlAvatar = item.getAvatarUrl();
        if (urlAvatar != null && urlAvatar.length() > 0) {
            loadImageFromUrl(urlAvatar, feedAvatar);
        }

        return convertView;
    }

    private void loadImageFromUrl(final String urlStr, final ImageView imageView) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    URL url = new URL(urlStr);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    
                    InputStream input = connection.getInputStream();
                    final Bitmap bitmap = BitmapFactory.decodeStream(input);
                    input.close();

                    handler.post(new Runnable() {
                        public void run() {
                            if (bitmap != null) {
                                imageView.setImageBitmap(bitmap);
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}