package com.example.raffy.photoorganizer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomGridAdapter extends BaseAdapter {

    private Context context;
    private final String[] gridValues;

    //Constructor to initialize values
    CustomGridAdapter(Context context, String[] gridValues) {
        this.context        = context;
        this.gridValues     = gridValues;
    }

    @Override
    public int getCount() {
        // Number of times getView method call depends upon gridValues.length
        return gridValues.length;
    }

    @Override
    public Object getItem(int position) {
        return null;    // TODO possibly to be implemented
    }

    @Override
    public long getItemId(int position) {
        return 0;       // TODO possibly to be implemented
    }

    // Number of times getView method call depends upon gridValues.length
    public View getView(int position, View convertView, ViewGroup parent) {
        View gridView;
        if (convertView == null) {
            // get layout from grid_item.xml ( Defined Below )
            gridView = LayoutInflater.from(context).inflate( R.layout.grid_item , parent, false);

            // set value into textview
            TextView textView = gridView.findViewById(R.id.text);
            textView.setText(gridValues[position]);
            textView.setLines(2);

            // set image based on selected text
            ImageView imageView = gridView.findViewById(R.id.imageButton);

            String arrLabel = gridValues[position];
            switch (arrLabel) {
                case "Take photo":
                    imageView.setImageResource(R.drawable.camera);
                    break;
                case "Gallery":
                    imageView.setImageResource(R.drawable.gallery);
                    break;
                case "Group management":
                    imageView.setImageResource(R.drawable.partner);
                    break;
                case "Settings":
                    imageView.setImageResource(R.drawable.settings);
                    break;
            }
        } else {
            gridView = convertView;
        }
        return gridView;
    }
}