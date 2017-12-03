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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anton on 13.11.2017.
 */

public class GalleryActivity extends AppCompatActivity {

    List<GalleryAlbum> albums = new ArrayList<GalleryAlbum>();
    GridView gridView;

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

        GalleryAlbum test = new GalleryAlbum("group1");
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
        AlbumListener testListener = new AlbumListener(test, gridView.getAdapter());
        picturesRef.addChildEventListener(testListener);

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
            return convertView;
        }

        private class ViewHolder {
            ImageView imageView;
            TextView txtTitle;
            TextView txtImages;
        }

    }
}
