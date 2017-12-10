package com.example.raffy.photoorganizer;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anton on 13.11.2017.
 */

public class GalleryAlbum {
    String name;
    List<GalleryImage> images;
    Boolean isPrivate;

    public GalleryAlbum(String name) {
        this.name = name;
        images = new ArrayList<>();
        isPrivate = false;
    }

    static GalleryAlbum createPrivateAlbum(Context context, String name) {
        GalleryAlbum album = new GalleryAlbum(name);
        album.isPrivate = true;
        File folder = SettingsHelper.getPrivateImageFolder(context);
        album.addImagesFromFolder(folder);
        return album;
    }

    void addImagesFromFolder(File folder) {
        //File folder = new File(path);
        if (!folder.exists()) {
            Log.e("GalleryActivity", "Invalid folder for private images!");
            return;
        }
        for (File file : folder.listFiles()) {
            // TODO: Do we need to check if the file is actually an image?
            Log.d("PrivateFolder", file.getName());
            GalleryImage image = new GalleryImage();
            image.faces = false; // All private images has a barcode, no need to check faces...?
            image.owner = User.getUid();
            image.downloadUri = android.net.Uri.parse(file.toURI().toString()); // Silly conversion between android and java URI
            images.add(image);
        }

    }


}


