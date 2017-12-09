package com.example.raffy.photoorganizer;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anton on 13.11.2017.
 */

public class GalleryAlbumPrivate {
    String name;

    List<Bitmap> privateImages;


    public GalleryAlbumPrivate(String name) {
        this.name = name;
        privateImages = new ArrayList<>();
    }



}


