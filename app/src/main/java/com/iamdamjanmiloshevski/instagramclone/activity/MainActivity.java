package com.iamdamjanmiloshevski.instagramclone.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.iamdamjanmiloshevski.instagramclone.R;
import com.parse.ParseAnalytics;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
//        ParseUser user = new ParseUser();
//        user.setUsername("admin");
//        user.setPassword("password");
//        user.signUpInBackground(new SignUpCallback() {
//            @Override
//            public void done(ParseException e) {
//                if(e == null){
//                    Log.i("SignUp","Successful");
//                }else{
//                    Log.i("SignUp","Failed");
//                }
//            }
//        });
//        ParseUser.logInInBackground("admin", "asdaadsad", new LogInCallback() {
//            @Override
//            public void done(ParseUser user, ParseException e) {
//                if (user != null) {
//                    Log.i("Login", "Successful");
//                } else {
//                    Log.i("Login", "Failed "+e.getMessage());
//                }
//            }
//        });

        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }
}
