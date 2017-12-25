package com.iamdamjanmiloshevski.instagramclone.utility;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * --------------------------------------------
 * Author: Damjan Miloshevski
 * E-mail: damjan.milosevski@korvus.mk
 * Date: 25.12.2017
 * Project: instagram-clone
 * © Corvus Info d.o.o Zagreb, Croatia
 * --------------------------------------------
 */

public class SessionManagement {
    private static final String PREFS_NAME = "instagram_prefs";
    private static final String LANGUAGE = "language";
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public SessionManagement(Context context) {
        int PRIVATE_MODE = 0;
        prefs = context.getSharedPreferences(PREFS_NAME, PRIVATE_MODE);
        editor = prefs.edit();
    }

    public void addLanguage(String language) {
        editor.putString(LANGUAGE, language);
        editor.commit();
    }

    public String getLanguage() {
        return prefs.getString(LANGUAGE, null);
    }
}
