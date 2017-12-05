package com.example.raffy.photoorganizer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class GalleryImageActivity extends AppCompatActivity {

    String imagePath;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_full_screen);
        imageView = findViewById(R.id.imageView);

        imagePath = "";
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras != null) {
                imagePath = extras.getString("image_path", "");
            }
        } else {
            imagePath = (String) savedInstanceState.getSerializable("image_path");
        }

        Picasso.with(getApplicationContext()).load(imagePath)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_background)
                .into(imageView);
    }
}
