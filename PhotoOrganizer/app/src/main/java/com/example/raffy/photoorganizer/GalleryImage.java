package com.example.raffy.photoorganizer;

import android.net.Uri;

/**
 * Created by Anton on 2.12.2017.
 */

public class GalleryImage {
    String owner = "";
    String bucket_identifier = "";
    Boolean faces = false;

    private Uri downloadUriOriginal = null;
    private Uri downloadUriSmall = null;
    private Uri downloadUriLarge = null;

    Uri getDownloadUri(SettingsHelper.ImageQuality quality) {
        switch (quality) {
            case ORIGINAL:
                return downloadUriOriginal;
            case LARGE:
                return downloadUriLarge;
            case SMALL:
                return downloadUriSmall;
            default:
                return downloadUriOriginal;
        }
    }

    void setDownloadUri(SettingsHelper.ImageQuality quality, Uri uri) {
        switch (quality) {
            case ORIGINAL:
                downloadUriOriginal = uri;
                break;
            case LARGE:
                downloadUriLarge = uri;
                break;
            case SMALL:
                downloadUriSmall = uri;
                break;
            default:
                downloadUriOriginal = uri;
                break;
        }
    }
}
