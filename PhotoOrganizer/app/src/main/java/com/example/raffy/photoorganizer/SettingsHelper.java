package com.example.raffy.photoorganizer;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.storage.FirebaseStorage;

/**
 * Created by Anton on 4.12.2017.
 */

public class SettingsHelper {

    public static final String BACKEND_URL = "https://mcc-fall-2017-g08.appspot.com";

    public static final String BUCKET_SMALL = "gs://mcc-fall-2017-g08_small";
    public static final String BUCKET_LARGE = "gs://mcc-fall-2017-g08_large";

    private static final SettingsHelper ourInstance = new SettingsHelper();

    public static SettingsHelper getInstance() {
        return ourInstance;
    }

    public static final String PREFERENCES = "preferences";
    SharedPreferences sharedPreferences = null;

    private SettingsHelper() {
    }

    public enum ImageQuality {
        SMALL, LARGE, ORIGINAL
    }

    public static ImageQuality getImageQuality(Context context) {
        SettingsHelper instance = getInstance();
        if (instance.sharedPreferences == null) {
            instance.sharedPreferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        }
        // TODO
        return ImageQuality.ORIGINAL;
    }

    public static FirebaseStorage getFirebaseStorage(ImageQuality quality) {
        switch (quality) {
            case ORIGINAL:
                return FirebaseStorage.getInstance();
            case SMALL:
                return FirebaseStorage.getInstance(BUCKET_SMALL);
            case LARGE:
                return FirebaseStorage.getInstance(BUCKET_LARGE);
            default:
                return FirebaseStorage.getInstance();
        }
    }

}
