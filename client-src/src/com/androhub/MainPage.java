package com.androhub;
import android.content.Intent;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
public class MainPage extends Activity{
    private String username = "";
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Intent intentMain = getIntent();
        username = intentMain.getStringExtra("username");
        setContentView(R.layout.main_page);
    }
}