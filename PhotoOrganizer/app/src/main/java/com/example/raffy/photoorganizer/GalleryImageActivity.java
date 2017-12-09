package com.example.raffy.photoorganizer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class GalleryImageActivity extends AppCompatActivity {

    static final String INTENT_IMAGE_PATH = "image_path";

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
                imagePath = extras.getString(INTENT_IMAGE_PATH, "");
            }
        } else {
            imagePath = (String) savedInstanceState.getSerializable(INTENT_IMAGE_PATH);
        }

        try {
            Picasso.with(getApplicationContext()).load(imagePath)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_background)
                    .into(imageView);
        }
        catch (Exception exception) {
            Log.e("Picasso", "Failed to load image: " + exception.toString());
            Toast.makeText(getApplicationContext(), "Error while loading image! \n" + exception.toString(), Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Save album path when this activity is restarted
        outState.putString(INTENT_IMAGE_PATH, imagePath);

        // call superclass to save any view hierarchy
        super.onSaveInstanceState(outState);
    }
}
