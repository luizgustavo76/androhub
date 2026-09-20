package com.androhub;
import android.content.Intent;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
public class MainActivity extends Activity {
    private EditText edtUsername;
    private Button btnSend;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter);
        edtUsername = (EditText) findViewById(R.id.edtUsername);
        btnSend = (Button) findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, MainPage.class);
                intent.putExtra("username", edtUsername.getText().toString());
                startActivity(intent);
            }
        });
    }
}
