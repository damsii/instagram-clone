package com.iamdamjanmiloshevski.instagramclone.utility;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private static final String USERNAMES = "usernames";
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

    public void addUsernames(List<String> usernames) {
        Set<String> users = new HashSet<>();
        users.addAll(usernames);
        editor.putStringSet(USERNAMES, users);
        editor.commit();
    }

    public Set<String> getUsernames() {
        return prefs.getStringSet(USERNAMES, null);
    }

    public String getLanguage() {
        return prefs.getString(LANGUAGE, null);
    }
}
