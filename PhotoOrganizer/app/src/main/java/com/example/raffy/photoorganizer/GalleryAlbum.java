package com.example.raffy.photoorganizer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anton on 13.11.2017.
 */

public class GalleryAlbum {
    String name;
    List<GalleryImage> images;

    public GalleryAlbum(String name) {
        this.name = name;
        images = new ArrayList<>();
    }
}


