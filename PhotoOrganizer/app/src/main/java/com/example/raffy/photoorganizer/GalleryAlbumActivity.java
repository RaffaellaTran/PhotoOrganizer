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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Anton on 13.11.2017.
 */

public class GalleryAlbumActivity extends AppCompatActivity {

    enum SortOption {
        FACES, AUTHORS
    }

    SortOption sortedBy;

    GalleryAlbum mainAlbum; // Redundant?

    FirebaseDatabase db;
    FirebaseStorage storage;
    FirebaseStorage storageSmall;
    FirebaseStorage storageLarge;

    Map<String, ImageAdapter> imageAdapterMap;

    LinearLayout layout;
    TextView title;
    int imageWidth;
    int imageHeight;
    int columns = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_album);
        layout = findViewById(R.id.linearLayout);
        title = findViewById(R.id.albumName);

        // TODO: Get sorting style from shared preferences?
        sortedBy = SortOption.FACES;

        imageAdapterMap = new HashMap<>();

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
        mainAlbum = new GalleryAlbum(albumPath);
        title.setText(mainAlbum.name);

        // Start synchronizing album images from firebase
        db = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        DatabaseReference picturesRef = db.getReference("pictures/" + albumPath);
        AlbumListener albumListener = new AlbumListener(onNewImage, onImageUri);
        picturesRef.addChildEventListener(albumListener);
    }

    AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            // Start the fullscreen viewer for selected image
            Intent intent = new Intent(getApplicationContext(), GalleryImageActivity.class);
            GalleryImage img = (GalleryImage) parent.getItemAtPosition(position);
            Uri imageUri = img.getDownloadUri(SettingsHelper.getImageQuality(getApplicationContext()));
            intent.putExtra("image_path", imageUri.toString());
            startActivity(intent);
        }
    };

    // Create listener for adding new images to the album
    AlbumListener.AlbumEventListener onNewImage = new AlbumListener.AlbumEventListener() {
        @Override
        public void callback(GalleryImage image) {
            // Add image to the main album (redundant?)
            mainAlbum.images.add(image);

            // Add the new image under the correct title
            final String sortedTitle = getSortedName(image, sortedBy);
            if (!imageAdapterMap.containsKey(sortedTitle))
                addGridViewForAlbum(new GalleryAlbum(sortedTitle));
            imageAdapterMap.get(sortedTitle).addImage(image);
        }
    };

    // Create listener for updating imageViews when the image URL becomes available
    AlbumListener.AlbumEventListener onImageUri = new AlbumListener.AlbumEventListener() {
        @Override
        public void callback(GalleryImage image) {
            String sortedTitle = getSortedName(image, sortedBy);
            imageAdapterMap.get(sortedTitle).notifyDataSetChanged();  // Update the gridview
        }
    };

    void addGridViewForAlbum(GalleryAlbum album) {
        // Set up image grid
        View view = getLayoutInflater().inflate(R.layout.image_grid, null);
        GridView grid = view.findViewById(R.id.grid);
        ImageAdapter adapter = new ImageAdapter(getApplicationContext(), album);
        grid.setAdapter(adapter);
        grid.setPadding(0,0,0,0);
        grid.setNumColumns(columns);
        grid.setOnItemClickListener(clickListener);
        TextView title = view.findViewById(R.id.title);
        title.setText(album.name);
        layout.addView(view);

        imageAdapterMap.put(album.name, adapter);
    }

    String getSortedName(GalleryImage image, SortOption sortBy) {
        // Returns the name of the sortgroup in which given image belongs
        if (sortBy == SortOption.FACES) {
            if (image.faces == true)
                return "People";
            else
                return "No People";
        }
        else {
            // TODO: Get user name instead of uid
            if (image.owner != null && !image.owner.equals(""))
                return image.owner;
            else
                return "Unknown user";
        }
    }

    public class ImageAdapter extends BaseAdapter {
        private Context context;
        GalleryAlbum album;

        public ImageAdapter(Context c, GalleryAlbum album) {
            context = c;
            this.album = album;
        }

        public void addImage(GalleryImage image) {
            album.images.add(image);
            notifyDataSetChanged();
        }

        public int getCount() {
            return album.images.size();
        }

        public GalleryImage getItem(int position) {
            return album.images.get(position);
        }

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
            Uri imageUri = getItem(position).getDownloadUri(SettingsHelper.getImageQuality(getApplicationContext()));
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
