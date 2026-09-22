package com.androhub;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import org.json.JSONObject;
import org.json.JSONException;
import java.io.UnsupportedEncodingException;

public class ViewFile extends Activity {
    private static final String TAG = "AndroHub_ViewFile";
    private String path = "";
    private String RepoName = "";
    private String fileName = "";
    private String token = "";
    private String content = "";
    private String username = "";
    private Button btnBack;
    private Button btnDownload;
    private Button btnCopy;
    private TextView txtCode;
    private String targetUrl = "";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_file);

        txtCode = (TextView) findViewById(R.id.txtCode);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnDownload = (Button) findViewById(R.id.btnDownload);
        btnCopy = (Button) findViewById(R.id.btnCopy);

        if (getIntent() != null) {
            if (getIntent().hasExtra("RepoName")) RepoName = getIntent().getStringExtra("RepoName");
            if (getIntent().hasExtra("fileName")) fileName = getIntent().getStringExtra("fileName");
            if (getIntent().hasExtra("username")) username = getIntent().getStringExtra("username");
            if (getIntent().hasExtra("token")) token = getIntent().getStringExtra("token");
            if (getIntent().hasExtra("path")) path = getIntent().getStringExtra("path");
        }
        if (!path.equals("")){
            targetUrl = "https://api.github.com/repos/" + username + "/" + RepoName + "/contents/" + path + "/" + fileName;
        }else{
            targetUrl = "https://api.github.com/repos/" + username + "/" + RepoName + "/contents/" + fileName;
        }
        try {
            String jsonRaw = request.requestHTTP(targetUrl, "get", new JSONObject(), token);

            JSONObject jsonResponse = new JSONObject(jsonRaw);
            if (jsonResponse.has("content")) {
                String base64Clean = jsonResponse.getString("content").replaceAll("\\s+", "");
                byte[] data = decodeBase64(base64Clean);
                content = new String(data, "UTF-8");
            } else {
                StringBuilder debugInfo = new StringBuilder();
                debugInfo.append("--- DEBUG DIAGNOSTIC ---\n");
                debugInfo.append("URL Chamada: ").append(targetUrl).append("\n\n");
                debugInfo.append("Payload Retornado pelo GitHub:\n");
                debugInfo.append(jsonRaw);

                content = debugInfo.toString();
            }

        } catch (JSONException e) {
            content = "Error JSON: " + e.getMessage();
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "Erro de Encodagem: ", e);
            content = "Erro UTF-8: " + e.getMessage();
        }

        txtCode.setText(content);
    }

    private static byte[] decodeBase64(String input) {
        String base64Chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
        java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream();
        
        int b = 0, buff = 0;
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == '=') break;
            int v = base64Chars.indexOf(c);
            if (v < 0) continue;
            
            buff = (buff << 6) | v;
            b += 6;
            if (b >= 8) {
                b -= 8;
                buffer.write((buff >> b) & 0xFF);
            }
        }
        return buffer.toByteArray();
    }
}