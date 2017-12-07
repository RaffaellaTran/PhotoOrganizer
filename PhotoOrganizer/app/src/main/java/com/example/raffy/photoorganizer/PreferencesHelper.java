package com.example.raffy.photoorganizer;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by oscarstigzelius on 05/12/2017.
 */

public class PreferencesHelper {

    // SharedPreferences
    public static final String PREFERENCES = "preferences";
    SharedPreferences sharedpreferences;


    public PreferencesHelper(Context activity) {
        this.sharedpreferences = activity.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
    }

    public String getWifiImageQuality() {
        return this.sharedpreferences.getString("wifiQuality", "high");
    }

    public String getMobileImageQuality() {
        return this.sharedpreferences.getString("mobileQuality", "high");
    }
}
