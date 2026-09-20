package com.androhub;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class request {

    public static String requestHTTP(String urlParam, String method, JSONObject json_body, String token) {
        HttpURLConnection connection = null;
        try {
            System.setProperty("http.keepAlive", "false");
            URL url = new URL(urlParam);
            connection = (HttpURLConnection) url.openConnection();
            
            method = method.toUpperCase();
            
            connection.setConnectTimeout(10000); 
            connection.setReadTimeout(10000);
            connection.setRequestMethod(method);
            connection.setInstanceFollowRedirects(true);
            
            connection.setRequestProperty("Accept", "application/vnd.github.v3+json");
            connection.setRequestProperty("User-Agent", "AndroHubApp/1.0 (Android 1.5)");
            connection.setRequestProperty("Content-Type", "application/json");

            // Insere o token de autenticação se ele tiver sido informado
            if (token != null && token.trim().length() > 0) {
                connection.setRequestProperty("Authorization", "token " + token.trim());
            }

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

            InputStream is;
            if (responseCode >= 200 && responseCode < 300) {
                is = connection.getInputStream();
            } else {
                is = connection.getErrorStream();
            }

            if (is != null) {
                BufferedReader in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();

                if (responseCode >= 400) {
                    return ""; 
                }

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