package com.example.raffy.photoorganizer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

/**
 * Created by Anton on 13.11.2017.
 */

public class GalleryAlbumActivity extends AppCompatActivity {

    GalleryAlbum album;

    FirebaseDatabase db;
    FirebaseStorage storage;

    GridView gridView;
    int imageWidth;
    int imageHeight;
    int columns = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_grid);

        // Get a suitable image height and width for filling the screen
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        imageWidth = size.x / columns;
        imageHeight = imageWidth;

        // Get album path from intent
        String albumPath = "";
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras != null) {
                albumPath = extras.getString("album", "");
            }
        } else {
            albumPath = (String) savedInstanceState.getSerializable("album");
        }
        album = new GalleryAlbum(albumPath);

        // Set up image grid
        gridView = findViewById(R.id.grid);
        gridView.setAdapter(new ImageAdapter(getApplicationContext()));
        gridView.setPadding(0,0,0,0);
        gridView.setNumColumns(columns);
        gridView.setOnItemClickListener(clickListener);

        // Start synchronizing album images from firebase
        db = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        DatabaseReference picturesRef = db.getReference("pictures/" + albumPath);
        AlbumListener testListener = new AlbumListener(album, gridView.getAdapter());
        picturesRef.addChildEventListener(testListener);
    }

    AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            // Start the fullscreen viewer for selected image
            Intent intent = new Intent(getApplicationContext(), GalleryImageActivity.class);
            String image = "http://i.imgur.com/DvpvklR.png";
            intent.putExtra("image_path", image);
            startActivity(intent);
        }
    };


    public class ImageAdapter extends BaseAdapter {
        private Context context;

        public ImageAdapter(Context c) {
            context = c;
        }

        public int getCount() {
            return album.images.size();
        }

        public GalleryImage getItem(int position) {
            return album.images.get(position);
        }

        //public GalleryImage getItem(int position) { return (GalleryImage) getItem(position); }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView = (ImageView) convertView;
            if (imageView == null) {
                imageView = new ImageView(context);
                //imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
                //imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                //imageView.setPadding(8, 8, 8, 8);
            }
            Uri imageUri = getItem(position).downloadUri;
            try {
                Picasso.with(context).load(imageUri)
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .error(R.drawable.ic_launcher_background)
                        .resize(imageWidth, imageHeight)
                        .centerCrop()
                        .into(imageView);
            } catch (IllegalArgumentException exception) {
                Log.d("Picasso", exception.toString());
            }

            return imageView;
        }
    }
}
