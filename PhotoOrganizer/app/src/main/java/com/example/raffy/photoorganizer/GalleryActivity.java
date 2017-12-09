package com.example.raffy.photoorganizer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anton on 13.11.2017.
 */

public class GalleryActivity extends AppCompatActivity {

    List<GalleryAlbum> albums = new ArrayList<GalleryAlbum>();
    GridView gridView;
    TextView infoText;
    private ProgressDialog progressDialog;

    int imageWidth;
    int imageHeight;

    // Get a reference to the database service
    FirebaseDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        setTitle(getString(R.string.gallery));

        // Get a suitable image height and width for filling the screen
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int columns = 2;
        imageWidth = size.x / columns;
        imageHeight = imageWidth;

        // Setup the grid view
        gridView = findViewById(R.id.folder_grid);
        gridView.setAdapter(new FolderAdapter(getApplicationContext()));
        gridView.setOnItemClickListener(clickListener);

        // Set info text
        infoText = findViewById(R.id.info);
        infoText.setText("Nothing to display \nYou don't belong to any groups!");

        // Get all FireBase related variables
        progressDialog = ApiHttp.getProgressDialog(this);
        db = FirebaseDatabase.getInstance();
        db.getReference("users/" + FirebaseAuth.getInstance().getUid()).addValueEventListener(groupListener);
    }

    ValueEventListener groupListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (progressDialog.isShowing()) progressDialog.dismiss();
            // Add a new album for this group if current user is in the group
            User user = dataSnapshot.getValue(User.class);
            if (user != null && user.getGroup() != null && user.getGroup().length() > 0) {
                addAlbum(user.getGroup());
            }
            // TODO Remove previous group's album
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            if (progressDialog.isShowing()) progressDialog.dismiss();
            Log.d("GalleryGroupListener", databaseError.toString());
            Toast.makeText(getApplicationContext(), "Firebase error occurred!", Toast.LENGTH_SHORT).show();
        }
    };

    void addAlbum(String name) {
        // Adds a new album to the grid view
        final GalleryAlbum album = new GalleryAlbum(name);
        albums.add(album);

        // Create listener for adding new images to the album
        GalleryAlbumListener.AlbumEventListener onNewImage = new GalleryAlbumListener.AlbumEventListener() {
            @Override
            public void callback(GalleryImage image) {
                album.images.add(image);
            }
        };

        // Create listener for updating imageViews when the image URL becomes available
        GalleryAlbumListener.AlbumEventListener onImageUri = new GalleryAlbumListener.AlbumEventListener() {
            @Override
            public void callback(GalleryImage image) {
                ((BaseAdapter) gridView.getAdapter()).notifyDataSetChanged();
            }
        };

        // Start listening for all FireBase events for this album
        GalleryAlbumListener listener = new GalleryAlbumListener(onNewImage, onImageUri, getApplicationContext());
        db.getReference("pictures/" + name).addChildEventListener(listener);

        // Hide info text
        infoText.setVisibility(View.GONE);
    }


    AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            // Start the album viewer for selected album
            Intent intent = new Intent(getApplicationContext(), GalleryAlbumActivity.class);
            GalleryAlbum album = (GalleryAlbum) gridView.getItemAtPosition(position);
            intent.putExtra("album", album.name);
            startActivity(intent);
        }
    };

    public class FolderAdapter extends BaseAdapter {
        private Context mContext;

        public FolderAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return albums.size();
        }

        public GalleryAlbum getItem(int position) {
            return albums.get(position);
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.gallery_album, null);
                holder = new ViewHolder();
                holder.txtTitle = (TextView) convertView.findViewById(R.id.name);
                holder.txtImages = (TextView) convertView.findViewById(R.id.numImages);
                holder.imageView = (ImageView) convertView.findViewById(R.id.image);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.imageView.setImageResource(R.mipmap.ic_launcher);



            GalleryAlbum album = getItem(position);
            if (album.images.size() > 0) {
                // Display the first image as thumbnail
                Uri imageUri = getItem(position).images.get(0).downloadUri;
                try {
                    Picasso.with(mContext).load(imageUri)
                            .placeholder(R.mipmap.ic_launcher)
                            .error(R.mipmap.ic_launcher)
                            .resize(imageWidth, imageHeight)
                            .centerCrop()
                            .into(holder.imageView);
                } catch (IllegalArgumentException exception) {
                    Log.d("Picasso", exception.toString());
                }
            }
            holder.txtTitle.setText(album.name);
            Integer numImages = album.images.size();
            holder.txtImages.setText(numImages.toString());
            holder.imageView.getLayoutParams().height = imageHeight;
            holder.imageView.getLayoutParams().width = imageWidth;
            return convertView;
        }

        private class ViewHolder {
            ImageView imageView;
            TextView txtTitle;
            TextView txtImages;
        }

    }
}
