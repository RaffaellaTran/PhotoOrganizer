package com.example.raffy.photoorganizer;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anton on 13.11.2017.
 */

public class GalleryAlbum {
    String name;
    List<GalleryImage> images;
    List<Bitmap> privateImages;

    public GalleryAlbum(String name) {
        this.name = name;
        images = new ArrayList<>();
    }

    public void Private(String name){
        this.name = name;
        privateImages = new ArrayList<>();

    }


}


