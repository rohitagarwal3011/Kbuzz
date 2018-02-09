package com.app.rohit.campk12_drawnshare;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;


public class SplashActivity extends AppCompatActivity {

    Animation slide_up;
    ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();
        setContentView(R.layout.activity_splash);
        slide_up= AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_up);
        logo=(ImageView)findViewById(R.id.genie);

    }

    @Override
    protected void onResume() {
        super.onResume();
        logo.startAnimation(slide_up);
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {

                        Intent intent = new Intent(SplashActivity.this,Drawings.class);
                        startActivity(intent);
                        finish();

                    }
                },
                4000
        );
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}