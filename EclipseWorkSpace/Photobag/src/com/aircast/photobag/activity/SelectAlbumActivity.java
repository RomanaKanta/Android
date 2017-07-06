package com.aircast.photobag.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aircast.photobag.R;
import com.aircast.photobag.widget.FButton;

/**
 * this Activity display all album in SD card
 * Temporally it is not use because we go directly to Camera folder 
 * @author FPT_HaiVT
 *
 */
public class SelectAlbumActivity extends Activity implements OnClickListener {
    private ListView mAlbumList;
    private ArrayList<String> mAlbumNameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pb_select_album_layout);
        View header = findViewById(R.id.tabsHeader);
        TextView title = (TextView) header.findViewById(R.id.tabsTittle);
        title.setText("Select Album");
        FButton btnLeft = (FButton) header.findViewById(R.id.btnLeftBtn);
        FButton btnRight = (FButton) header.findViewById(R.id.btnRightBtn);
        btnLeft.setVisibility(View.GONE);
        btnRight.setText("Cancel");
        btnRight.setOnClickListener(this);
        mAlbumNameList = new ArrayList<String>();
        createAlbumList();
        mAlbumList = (ListView) findViewById(R.id.album_list_listview);
        mAlbumList.setAdapter(new MyAdapter());
        mAlbumList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                    int position, long arg3) {
                String albumName = mAlbumNameList.get(position);
                Intent intent = new Intent();
                intent.putExtra("SELECTED_ALBUM", albumName);
                intent.setClass(getBaseContext(),
                        SelectMultipleImageActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    protected class MyAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public MyAdapter() {
            mInflater = LayoutInflater.from(getBaseContext());
        }

        @Override
        public int getCount() {
            return mAlbumNameList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.album_list_item, null);

                holder = new ViewHolder();
                holder.albumThumbnail = (ImageView) convertView
                        .findViewById(R.id.album_thumnail);
                holder.albumName = (TextView) convertView
                        .findViewById(R.id.album_name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.albumName.setText(mAlbumNameList.get(position));
            return convertView;
        }

        class ViewHolder {
            ImageView albumThumbnail;
            TextView albumName;
        }
    }

    private void createAlbumList() {

        mAlbumNameList.clear();
        // which image properties are we querying
        String[] projection = new String[] { MediaStore.Images.Media._ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.DISPLAY_NAME };

        // Get the base URI for the People table in the Contacts content
        // provider.
        Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        // Make the query.
        Cursor cur = managedQuery(images, projection, // Which columns to return
                "", // Which rows to return (all rows)
                null, // Selection arguments (none)
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME // Ordering
        );

        Log.i("ListingImages", " query count=" + cur.getCount());
        String listAlbum = "";
        if (cur.moveToFirst()) {
            String bucket = "";
            String date;
            String name;
            int bucketColumn = cur
                    .getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

            int dateColumn = cur
                    .getColumnIndex(MediaStore.Images.Media.DATE_TAKEN);
            int nameColumn = cur
                    .getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);

            do {
                // Get the field values
                if (!bucket.equals(cur.getString(bucketColumn))) {
                    listAlbum = listAlbum + "  " + cur.getString(bucketColumn);
                    mAlbumNameList.add(cur.getString(bucketColumn));
                }
                bucket = cur.getString(bucketColumn);
                date = cur.getString(dateColumn);
                name = cur.getString(nameColumn);
                // Do something with the values.
                Log.i("ListingImages", " bucket=" + bucket + "  date_taken="
                        + date + "name = " + name);
            } while (cur.moveToNext());

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btnRightBtn:
            finish();
            break;

        default:
            break;
        }
    }
}
