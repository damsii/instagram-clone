package com.iamdamjanmiloshevski.instagramclone.utility;

/**
 * --------------------------------------------
 * Author: Damjan Miloshevski
 * Email: d.miloshevski@gmail.com
 * Date: 25.12.2017 00:23
 * Project: instagram-clone
 * --------------------------------------------
 */

public enum Lang {
    MK("mk_MK"),
    EN("en_US");

    private String language;

    Lang(String language) {
        this.language = language;
    }

    public String getLanguage() {
        return language;
    }
}
