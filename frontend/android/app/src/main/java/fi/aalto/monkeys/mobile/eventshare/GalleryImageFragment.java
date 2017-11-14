package fi.aalto.monkeys.mobile.eventshare;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by Anton on 13.11.2017.
 */

public class GalleryImageFragment extends Fragment {



    GridView gridView;
    int imageWidth;
    int imageHeight;
    int columns = 3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.image_grid, container, false);

        gridView = rootView.findViewById(R.id.grid);
        gridView.setAdapter(new ImageAdapter(getContext()));
        gridView.setPadding(0,0,0,0);
        gridView.setNumColumns(columns);

        // Get a suitable image height and width for filling the screen
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        imageWidth = size.x / columns;
        imageHeight = imageWidth;

        return rootView;
    }


    public class ImageAdapter extends BaseAdapter {



        private Context context;

        public ImageAdapter(Context c) {
            context = c;
        }

        public int getCount() {
            return 5;
        }

        public Object getItem(int position) {
            return null;
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
            String url = "http://i.imgur.com/DvpvklR.png";

            Picasso.with(context).load(url)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_background)
                    .resize(imageWidth, imageHeight)
                    .centerCrop()
                    .into(imageView);
            //imageView.setImageResource(R.mipmap.empty_folder);
            return imageView;
        }


    }
}
