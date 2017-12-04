package com.example.raffy.photoorganizer;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Anton on 13.11.2017.
 */

public class GalleryActivity extends AppCompatActivity {

    List<GalleryAlbum> albums = new ArrayList<GalleryAlbum>();
    GridView gridView;
    TextView infoText;

    int imageWidth;
    int imageHeight;

    // Get a reference to the database service
    FirebaseDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_grid);

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
        infoText.setText("Nothing to display \nYou don't belong in any groups!");

        // Get all FireBase related variables
        db = FirebaseDatabase.getInstance();
        db.getReference("groups/").addChildEventListener(groupListener);
    }

    static class Group {
        // A simple class for easier parsing of Firebase Groups (Is this necessary?)
        Map<String, String> users = new HashMap<>();
        public Group() {}
    }

    ChildEventListener groupListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            // Add a new album for this group if current user is in the group
            Group group = dataSnapshot.getValue(Group.class);
            String uid = FirebaseAuth.getInstance().getUid();
            if (group.users.containsKey(uid)) {
                addAlbum(dataSnapshot.getKey());
            }
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {}

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.d("GalleryGroupListener", databaseError.toString());
        }
    };

    void addAlbum(String name) {
        // Adds a new album to the grid view
        GalleryAlbum album = new GalleryAlbum(name);
        albums.add(album);

        // Add a listener for all images in this album
        AlbumListener listener = new AlbumListener(album, gridView.getAdapter());
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

        public Object getItem(int position) {
            return albums.get(position);
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.album_view, null);
                holder = new ViewHolder();
                holder.txtTitle = (TextView) convertView.findViewById(R.id.name);
                holder.txtImages = (TextView) convertView.findViewById(R.id.numImages);
                holder.imageView = (ImageView) convertView.findViewById(R.id.image);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.imageView.setImageResource(R.mipmap.ic_launcher);



            GalleryAlbum album = (GalleryAlbum) gridView.getItemAtPosition(position);
            if (album.images.size() > 0) {
                // Display the first image as thumbnail
                Uri imageUri = album.images.get(0).downloadUri;
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
