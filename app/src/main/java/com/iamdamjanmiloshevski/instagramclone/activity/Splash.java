package com.iamdamjanmiloshevski.instagramclone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.iamdamjanmiloshevski.instagramclone.R;
import com.parse.ParseUser;

public class Splash extends AppCompatActivity {
    ParseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();

        user = ParseUser.getCurrentUser();
        //ParseAnalytics.trackAppOpenedInBackground(getIntent());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (user == null) {
                    Intent gotoLogin = new Intent(Splash.this, LoginActivity.class);
                    startActivity(gotoLogin);
                    Toast.makeText(Splash.this, "Please login to continue", Toast.LENGTH_SHORT)
                            .show();
                    finish();
                } else {
                    Intent gotoLogin = new Intent(Splash.this, MainActivity.class);
                    startActivity(gotoLogin);
                    finish();
                }
            }
        }, 3000);
    }
}