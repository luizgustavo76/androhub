package com.androhub;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class request {

    public static String requestHTTP(String urlParam, String method, JSONObject json_body) {
        HttpURLConnection connection = null;
        try {
            System.setProperty("http.keepAlive", "false");
            URL url = new URL(urlParam);
            connection = (HttpURLConnection) url.openConnection();
            method = method.toUpperCase();
            
            connection.setConnectTimeout(8000); 
            connection.setReadTimeout(8000);
            connection.setRequestMethod(method);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            if (method.equals("POST") || method.equals("PUT")) {
                connection.setDoOutput(true);
                OutputStream os = connection.getOutputStream();
                if (json_body != null) {
                    os.write(json_body.toString().getBytes("UTF-8"));
                }
                os.flush();
                os.close();
            }

            int responseCode = connection.getResponseCode();

            if (responseCode >= 200 && responseCode < 300) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();
                return response.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return ""; 
    }
}