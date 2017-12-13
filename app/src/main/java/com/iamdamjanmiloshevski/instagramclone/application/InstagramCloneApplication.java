package com.iamdamjanmiloshevski.instagramclone.application;


import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;

/**
 * --------------------------------------------
 * Author: Damjan Miloshevski
 * Email: d.miloshevski@gmail.com
 * Date: 12.12.2017 20:33
 * Project: instagram-clone
 * --------------------------------------------
 */

public class InstagramCloneApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        // Add your initialization code here
        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId("f08753b12926ce211739c2f46d4d88292594882c")
                .clientKey("5547e0b3e93a079d8243479b1dc7cee38040eff9")
                .server("http://18.217.247.44:80/parse/")
                .build()
        );

        //ParseUser.enableAutomaticUser();

        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);

    }
}
