package com.example.raffy.photoorganizer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Raffy on 15/11/2017.
 */

public class GalleryActivity extends AppCompatActivity {

    ImageView selectedImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_layout);

        ImageView imageView = (ImageView)findViewById(R.id.selectedImage);
    }

}
