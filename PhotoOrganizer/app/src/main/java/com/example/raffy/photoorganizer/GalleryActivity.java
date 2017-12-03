package com.example.raffy.photoorganizer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anton on 13.11.2017.
 */

public class GalleryActivity extends AppCompatActivity {

    List<GalleryAlbum> albums = new ArrayList<GalleryAlbum>();
    GridView gridView;

    // Get a reference to the database service
    FirebaseDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_grid);

        GalleryAlbum test = new GalleryAlbum("test");
        albums.add(test);
        albums.add(test);
        albums.add(test);
        albums.add(test);
        albums.add(test);
        albums.add(test);

        gridView = findViewById(R.id.folder_grid);
        gridView.setAdapter(new FolderAdapter(getApplicationContext()));

        gridView.setOnItemClickListener(clickListener);

        db = FirebaseDatabase.getInstance();

        DatabaseReference picturesRef = db.getReference("pictures/group1");
        AlbumListener testListener = new AlbumListener(test);
        picturesRef.addChildEventListener(testListener);

    }

    class AlbumListener implements ChildEventListener {

        GalleryAlbum album;
        public AlbumListener(GalleryAlbum album) {
            this.album = album;
        }

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            GalleryImage img = dataSnapshot.getValue(GalleryImage.class);
            Log.d("Gallery", img.owner + img.bucket_identifier + img.faces);
            album.images.add(img);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) { }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) { }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) { }

        @Override
        public void onCancelled(DatabaseError databaseError) { }
    }


    AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            // Start the album viewer for selected album
            Intent intent = new Intent(getApplicationContext(), GalleryAlbumActivity.class);
            GalleryAlbum album = (GalleryAlbum) gridView.getItemAtPosition(position);
            intent.putExtra("group_name", album.name);
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
            holder.txtTitle.setText(album.name);
            Integer numImages = album.images.size();
            holder.txtImages.setText(numImages.toString());
            return convertView;
        }

        private class ViewHolder {
            ImageView imageView;
            TextView txtTitle;
            TextView txtImages;
        }

    }
}
