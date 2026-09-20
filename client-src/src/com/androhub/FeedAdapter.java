package com.androhub;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
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

    private int getLanguageColor(String lang) {
        if (lang == null) return 0xFF8B949E;

        if (lang.equalsIgnoreCase("C++")) return 0xFFF34B7D;
        if (lang.equalsIgnoreCase("Java")) return 0xFFB07289;
        if (lang.equalsIgnoreCase("JavaScript")) return 0xFFF1E05A;
        if (lang.equalsIgnoreCase("Python")) return 0xFF3572A5;
        if (lang.equalsIgnoreCase("C")) return 0xFF555555;
        if (lang.equalsIgnoreCase("C#")) return 0xFF178600;
        if (lang.equalsIgnoreCase("HTML")) return 0xFFE34C26;
        if (lang.equalsIgnoreCase("CSS")) return 0xFF563D7C;
        if (lang.equalsIgnoreCase("Go")) return 0xFF00ADD8;
        if (lang.equalsIgnoreCase("Rust")) return 0xFFDEA584;

        return 0xFF8B949E;
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
        TextView txtDescription = (TextView) convertView.findViewById(R.id.txtDescription);
        TextView txtLang = (TextView) convertView.findViewById(R.id.txtLang);
        TextView txtStars = (TextView) convertView.findViewById(R.id.txtStars);
        final ImageView feedAvatar = (ImageView) convertView.findViewById(R.id.feedAvatar);

        txtUser.setText(item.getUser());
        txtAction.setText(item.getAction());
        txtTime.setText(item.getTime());
        txtRepoName.setText(item.getRepoName());

        if (txtDescription != null) {
            String desc = item.getDescription();
            if (desc != null && desc.length() > 0) {
                txtDescription.setText(desc);
                txtDescription.setVisibility(View.VISIBLE);
            } else {
                txtDescription.setVisibility(View.GONE);
            }
        }

        if (txtLang != null) {
            String lang = item.getLang();
            if (lang != null && lang.length() > 0 && !lang.equalsIgnoreCase("N/A") && !lang.equalsIgnoreCase("null")) {
                txtLang.setText(lang);
                txtLang.setVisibility(View.VISIBLE);

                // Desenha a bolinha usando ShapeDrawable (Totalmente compatível com Android 1.5)
                ShapeDrawable circle = new ShapeDrawable(new OvalShape());
                circle.getPaint().setColor(getLanguageColor(lang));

                int sizeInPx = (int) (10 * context.getResources().getDisplayMetrics().density);
                circle.setIntrinsicWidth(sizeInPx);
                circle.setIntrinsicHeight(sizeInPx);
                circle.setBounds(0, 0, sizeInPx, sizeInPx);

                txtLang.setCompoundDrawables(circle, null, null, null);
                txtLang.setCompoundDrawablePadding((int) (6 * context.getResources().getDisplayMetrics().density));
            } else {
                txtLang.setVisibility(View.GONE);
            }
        }

        if (txtStars != null) {
            String stars = item.getStars();
            if (stars != null && stars.length() > 0) {
                txtStars.setText(stars);
                txtStars.setVisibility(View.VISIBLE);
            } else {
                txtStars.setVisibility(View.GONE);
            }
        }

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