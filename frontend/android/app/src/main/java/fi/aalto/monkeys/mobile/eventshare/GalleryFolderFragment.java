package fi.aalto.monkeys.mobile.eventshare;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
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

public class GalleryFolderFragment extends Fragment {

    List<GalleryFolder> folders = new ArrayList<GalleryFolder>();
    GalleryFolder selectedFolder;
    GridView gridView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.folder_grid, container, false);
        GalleryFolder test = new GalleryFolder();
        test.name = "test";
        folders.add(test);
        folders.add(test);
        folders.add(test);
        folders.add(test);
        folders.add(test);
        folders.add(test);

        gridView = rootView.findViewById(R.id.folder_grid);
        gridView.setAdapter(new FolderAdapter(getContext()));

        gridView.setOnItemClickListener(clickListener);

        return rootView;
    }

    AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            selectedFolder = folders.get(position);
            ((Gallery)getActivity()).mPager.setCurrentItem(1);
        }
    };

    public class FolderAdapter extends BaseAdapter {



        private Context mContext;

        public FolderAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return folders.size();
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
                convertView = View.inflate(mContext, R.layout.folder_view, null);
                holder = new ViewHolder();
                holder.txtTitle = (TextView) convertView.findViewById(R.id.name);
                holder.imageView = (ImageView) convertView.findViewById(R.id.image);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.imageView.setImageResource(R.mipmap.empty_folder);
            holder.txtTitle.setText(folders.get(position).name);
            return convertView;
        }

        private class ViewHolder {
            ImageView imageView;
            TextView txtTitle;
        }

    }
}
