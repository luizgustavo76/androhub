package com.androhub;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

    private EditText edtUsername;
    private EditText edtToken;
    private TextView txtTokenInfo;
    private Button btnSend;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter);

        edtUsername = (EditText) findViewById(R.id.edtUsername);
        edtToken = (EditText) findViewById(R.id.edtToken);
        txtTokenInfo = (TextView) findViewById(R.id.txtTokenInfo);
        btnSend = (Button) findViewById(R.id.btnSend);

        final SharedPreferences prefs = getSharedPreferences("AndroHubPrefs", MODE_PRIVATE);
        String savedUsername = prefs.getString("username", "");
        String savedToken = prefs.getString("token", "");

        if (savedUsername.length() > 0) {
            edtUsername.setText(savedUsername);
        }

        if (savedToken.length() > 0) {
            edtToken.setVisibility(View.GONE);
            txtTokenInfo.setVisibility(View.GONE);
        }

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edtUsername.getText().toString().trim();
                
                String token = "";
                if (edtToken.getVisibility() == View.VISIBLE) {
                    token = edtToken.getText().toString().trim();
                } else {
                    token = prefs.getString("token", "");
                }

                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("username", username);
                if (token.length() > 0) {
                    editor.putString("token", token);
                }
                editor.commit();

                Intent intent = new Intent(MainActivity.this, MainPage.class);
                intent.putExtra("username", username);
                intent.putExtra("token", token);
                startActivity(intent);
            }
        });
    }
}