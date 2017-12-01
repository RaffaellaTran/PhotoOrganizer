package com.example.raffy.photoorganizer;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anton on 13.11.2017.
 */

public class GalleryActivity extends AppCompatActivity {

    List<GalleryAlbum> album = new ArrayList<GalleryAlbum>();
    GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_grid);

        GalleryAlbum test = new GalleryAlbum();
        test.name = "test";
        album.add(test);
        album.add(test);
        album.add(test);
        album.add(test);
        album.add(test);
        album.add(test);

        gridView = findViewById(R.id.folder_grid);
        gridView.setAdapter(new FolderAdapter(getApplicationContext()));

        gridView.setOnItemClickListener(clickListener);
    }


    AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

        }
    };

    public class FolderAdapter extends BaseAdapter {



        private Context mContext;

        public FolderAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return album.size();
        }

        public Object getItem(int position) {
            return null;
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
                holder.imageView = (ImageView) convertView.findViewById(R.id.image);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.imageView.setImageResource(R.mipmap.ic_launcher);
            holder.txtTitle.setText(album.get(position).name);
            return convertView;
        }

        private class ViewHolder {
            ImageView imageView;
            TextView txtTitle;
        }

    }
}
