package com.example.raffy.photoorganizer;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

import com.google.firebase.storage.FirebaseStorage;

import java.io.File;

import static com.example.raffy.photoorganizer.SettingsHelper.ImageQuality.*;

/**
 * Created by oscarstigzelius on 05/12/2017.
 */

public class SettingsHelper {

    // Strings
    public static final String WIFI_IMAGE_QUALITY = "WifiImageQuality";
    public static final String MOBILE_IMAGE_QUALITY = "MobileImageQuality";
    public static final String BACKEND_URL = "https://mcc-fall-2017-g08.appspot.com";
    public static final String BUCKET_SMALL = "gs://mcc-fall-2017-g08_small";
    public static final String BUCKET_LARGE = "gs://mcc-fall-2017-g08_large";

    // SharedPreferences
    public static final String PREFERENCES = "preferences";
    SharedPreferences sharedpreferences;
    Context context;

    // File storage
    static File getPrivateImageFolder(Context context) {
        //String path = context.getFilesDir() + "/private/";
        String path = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/private/";
        File folder = new File(path);
        return folder;
    }

    public enum ImageQuality {
        LOW, HIGH, FULL
    }

    public SettingsHelper(Context context) {
        this.context = context;
        this.sharedpreferences = this.context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
    }

    public String getString(String key, String defaultValue) {
        return this.sharedpreferences.getString(key, defaultValue);
    }

    public String getWifiImageQuality() {
        return this.getString(WIFI_IMAGE_QUALITY, HIGH.toString());
    }

    public String getMobileImageQuality() {
        return this.getString(MOBILE_IMAGE_QUALITY, HIGH.toString());
    }

    // Returns the right ImageQuality, depending if you are connected to Wifi or not
    public String getImageQuality() {
        ConnectivityManager connManager = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi != null && mWifi.isConnected()) {
            return getWifiImageQuality();
        }

        return getMobileImageQuality();
    }

    public void editString(String key, String value) {
        SharedPreferences.Editor editor = this.sharedpreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void editWifiImageQuality(String value) {
        this.editString(WIFI_IMAGE_QUALITY, value);
    }

    public void editMobileImageQuality(String value) {
        this.editString(MOBILE_IMAGE_QUALITY, value);
    }

    public FirebaseStorage getFirebaseStorage(String quality) {
        switch (quality) {
            case "FULL":
                return FirebaseStorage.getInstance();
            case "LOW":
                return FirebaseStorage.getInstance(BUCKET_SMALL);
            case "HIGH":
                return FirebaseStorage.getInstance(BUCKET_LARGE);
            default:
                return FirebaseStorage.getInstance();
        }
    }
}