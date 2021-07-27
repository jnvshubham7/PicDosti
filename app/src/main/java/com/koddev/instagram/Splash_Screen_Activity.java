package com.koddev.instagram;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jaeger.library.StatusBarUtil;
import com.koddev.instagram.Start_Activity;
public class Splash_Screen_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
      //  getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        StatusBarUtil.setTransparent(this);
        Handler handler = new Handler();
        Runnable runnable = () -> {
            Intent intent = new Intent(Splash_Screen_Activity.this, Start_Activity.class);
            startActivity(intent);
        };
        handler.postDelayed(runnable, 500);
    }
}