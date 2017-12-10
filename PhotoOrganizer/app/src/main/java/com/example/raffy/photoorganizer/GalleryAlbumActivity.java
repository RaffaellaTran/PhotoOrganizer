package com.example.raffy.photoorganizer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Anton on 13.11.2017.
 */

public class GalleryAlbumActivity extends AppCompatActivity {

    enum SortOption {
        FACES, AUTHORS
    }

    static final String GALLERY_SORTING = "sortGalleryBy";

    SortOption sortedBy;

    GalleryAlbum mainAlbum; // Redundant?
    String albumPath;
    Boolean isPrivateAlbum;

    FirebaseDatabase db;
    FirebaseStorage storage;
    SettingsHelper settings;


    Map<String, ImageAdapter> imageAdapterMap;

    LinearLayout layout;
    TextView title;
    TextView infoText;

    int imageWidth;
    int imageHeight;
    int columns = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_album);
        layout = findViewById(R.id.albumLayout);
        title = findViewById(R.id.albumName);
        imageAdapterMap = new HashMap<>();

        // Set info text
        infoText = findViewById(R.id.info);
        infoText.setText("Nothing to display \nThis album has no images!");

        // Get sorting style from shared preferences
        settings = new SettingsHelper(getApplicationContext());
        String sort = settings.getString(GALLERY_SORTING, "FACES");
        if (sort.equalsIgnoreCase(SortOption.AUTHORS.toString()))
            sortedBy = SortOption.AUTHORS;
        else
            sortedBy = SortOption.FACES;

        // Get a suitable image height and width for filling the screen
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        imageWidth = size.x / columns;
        imageHeight = imageWidth;

        // Get album path from intent
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras != null) {
                albumPath = extras.getString("album", "");
                isPrivateAlbum = extras.getBoolean("private_album", false);
            }
        } else {
            albumPath = (String) savedInstanceState.getSerializable("album");
            isPrivateAlbum = savedInstanceState.getBoolean("private_album");
        }
        if (isPrivateAlbum) {
            mainAlbum = GalleryAlbum.createPrivateAlbum(getApplicationContext(), "Private");
            title.setText("Private");
            addGridViewForAlbum(mainAlbum);
        } else {
            mainAlbum = new GalleryAlbum(albumPath);
            title.setText(mainAlbum.name);


            // Start synchronizing album images from firebase
            db = FirebaseDatabase.getInstance();
            storage = FirebaseStorage.getInstance();

            DatabaseReference picturesRef = db.getReference("pictures/" + albumPath);
            GalleryAlbumListener albumListener = new GalleryAlbumListener(onNewImage, onImageUri, getApplicationContext());
            picturesRef.addChildEventListener(albumListener);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Save album path when this activity is restarted
        outState.putString("album", albumPath);
        outState.putBoolean("private_album", isPrivateAlbum);

        // call superclass to save any view hierarchy
        super.onSaveInstanceState(outState);
    }

    AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            // Start the fullscreen viewer for selected image
            Intent intent = new Intent(getApplicationContext(), GalleryImageActivity.class);
            GalleryImage img = (GalleryImage) parent.getItemAtPosition(position);
            Uri imageUri = img.downloadUri;
            intent.putExtra(GalleryImageActivity.INTENT_IMAGE_PATH, imageUri.toString());
            startActivity(intent);
        }
    };

    // Create listener for adding new images to the album
    GalleryAlbumListener.AlbumEventListener onNewImage = new GalleryAlbumListener.AlbumEventListener() {
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
    GalleryAlbumListener.AlbumEventListener onImageUri = new GalleryAlbumListener.AlbumEventListener() {
        @Override
        public void callback(GalleryImage image) {
            String sortedTitle = getSortedName(image, sortedBy);
            imageAdapterMap.get(sortedTitle).notifyDataSetChanged();  // Update the gridview
        }
    };

    void addGridViewForAlbum(GalleryAlbum album) {
        // Hide infotext
        infoText.setVisibility(View.GONE);

        // Set up image grid
        View view = getLayoutInflater().inflate(R.layout.image_grid, null);
        GridView grid = view.findViewById(R.id.grid);
        ImageAdapter adapter = new ImageAdapter(getApplicationContext(), album, grid);
        grid.setAdapter(adapter);
        grid.setPadding(0,0,0,0);
        grid.setNumColumns(columns);
        grid.setOnItemClickListener(clickListener);
        grid.setScrollContainer(false);
        //grid.getLayoutParams().height = 500;
        TextView title = view.findViewById(R.id.title);
        title.setText(album.name);
        layout.addView(view);
        adapter.updateHeight();

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
        View parent;

        public ImageAdapter(Context c, GalleryAlbum album, View parent) {
            context = c;
            this.album = album;
            this.parent = parent;
        }

        public void updateHeight() {
            Integer images = album.images.size();
            Integer rows = images / columns+1;
            Integer height = rows*imageHeight;
            parent.getLayoutParams().height = height;
        }

        public void addImage(GalleryImage image) {
            album.images.add(image);
            updateHeight();
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
            Uri imageUri = getItem(position).downloadUri;
            try {
                Picasso.with(context).load(imageUri)
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .error(R.drawable.ic_launcher_background)
                        .resize(imageWidth, imageHeight)
                        .centerCrop()
                        .into(imageView);
            } catch (Exception exception) {
                Log.e("Picasso", "Failed to load image: " + exception.toString());
                Toast.makeText(getApplicationContext(), "Error while loading image! \n" + exception.toString(), Toast.LENGTH_LONG).show();
            }

            return imageView;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.gallery_menu, menu);
        MenuItem sort = menu.findItem(R.id.sort);
        if (sortedBy == SortOption.AUTHORS)
            sort.setTitle("Sort by people");
        else
            sort.setTitle("Sort by authors");
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.sort:
                // Switch sorting option
                if (sortedBy == SortOption.FACES)
                    sortedBy = SortOption.AUTHORS;
                else
                    sortedBy = SortOption.FACES;

                // Save selected option to shared preferences and reload this activity
                settings.editString(GALLERY_SORTING, sortedBy.toString());
                recreate();

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
