package com.iamdamjanmiloshevski.instagramclone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.iamdamjanmiloshevski.instagramclone.R;
import com.iamdamjanmiloshevski.instagramclone.utility.SessionManagement;
import com.iamdamjanmiloshevski.instagramclone.utility.Utility;
import com.parse.ParseUser;

public class Splash extends AppCompatActivity {
    private ParseUser user;
    private SessionManagement session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        session = new SessionManagement(this);
        if (session.getLanguage() != null) {
            Utility.setApplicationLanguage(session.getLanguage(), this);
        }
        user = ParseUser.getCurrentUser();
        //ParseAnalytics.trackAppOpenedInBackground(getIntent());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (user == null) {
                    Intent gotoLogin = new Intent(Splash.this, LoginActivity.class);
                    startActivity(gotoLogin);
                    finish();
                } else {
                    Intent gotoLogin = new Intent(Splash.this, MainActivity.class);
                    startActivity(gotoLogin);
                    finish();
                }
            }
        }, 1000);
    }
}
