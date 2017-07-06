package com.smartmux.banglaebook.plus.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartmux.banglaebook.plus.R;
import com.smartmux.banglaebook.plus.model.ItemRow;
import com.smartmux.banglaebook.plus.util.Constant;
import com.smartmux.banglaebook.plus.util.EBookPreferenceUtils;
import com.smartmux.banglaebook.plus.util.ImageLoader;
import com.smartmux.banglaebook.plus.util.ProgressWheel;

import java.io.File;
import java.util.List;

/**
 * Created by tanvir-android on 4/22/16.
 */
public class MyBookListAdapter extends ArrayAdapter<ItemRow> {

    Context context;
    List<ItemRow> items;
    protected ImageLoader mImageLoader;
    String filePath;
    SharedPreferences prefs;
    int resourceId;

    public MyBookListAdapter(Context context, int resourceId, List<ItemRow> items) {
        super(context, resourceId, items);
        this.context = context;
        this.items = items;
        this.resourceId = resourceId;
        mImageLoader = new ImageLoader(context);
        prefs = ((Activity) context).getPreferences(Context.MODE_PRIVATE);
    }

    @Override
    public ItemRow getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final ItemRow rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(resourceId, null);
            holder = new ViewHolder();
            holder.authorname = (TextView) convertView
                    .findViewById(R.id.authorname);
            holder.txtTitle = (TextView) convertView.findViewById(R.id.title);
            holder.txtBengTitle = (TextView) convertView
                    .findViewById(R.id.bengaliTitle);
            holder.txtPage = (TextView) convertView.findViewById(R.id.pdfPages);
            holder.thumbImage = (ImageView) convertView
                    .findViewById(R.id.thumbRadio);
            holder.infoImage = (ImageView) convertView
                    .findViewById(R.id.icon_book_info);
            holder.readNumberOfPages = (TextView) convertView
                    .findViewById(R.id.textview_read_numberofpages);
            holder.progressDownload = (ProgressWheel) convertView
                    .findViewById(R.id.progressBar_Downloding);
            holder.row = rowItem;
            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();
            holder.row.setProgressBar(null);
            holder.row = rowItem;
            holder.row.setProgressBar(holder.progressDownload);
        }

        File file = new File(Environment.getExternalStorageDirectory()
                .toString(), "Books/" + rowItem.getTitle() + ".pdf");
        if (file.exists()) {

        holder.authorname.setText(rowItem.getAurthor());
        holder.txtTitle.setText(rowItem.getTitle());
        holder.txtBengTitle.setText(rowItem.getBengaliTitle());
        holder.txtPage.setText(rowItem.getPage() + " Pages" + " ("
                + rowItem.getsize() + ")");
        String imageUrl = rowItem.getImage();

        if (!TextUtils.isEmpty(imageUrl)) {

            mImageLoader.DisplayImage(imageUrl, holder.thumbImage);

        }

            holder.readNumberOfPages.setVisibility(View.VISIBLE);
            holder.readNumberOfPages.setText(String.format(context
                            .getString(R.string.read_number_of_pages),
                    (EBookPreferenceUtils.getIntPref(
                            context.getApplicationContext(),
                            Constant.PREF_NAME,
                            rowItem.getTitle() + ".pdf", 0) +1), rowItem
                            .getPage()));

            holder.progressDownload.setVisibility(View.INVISIBLE);
            return convertView;
        } else {
//            holder.readNumberOfPages.setVisibility(View.GONE);
            return null;
        }

//        holder.progressDownload.setProgress(rowItem.getProgress());
//        rowItem.setProgressBar(holder.progressDownload);
//
//        if (rowItem.getDownloadState().equals(ItemRow.DownloadState.DOWNLOADING)) {
//
//            holder.progressDownload.setVisibility(View.VISIBLE);
//        } else {
//
//
//
//            holder.progressDownload.setVisibility(View.INVISIBLE);
//        }


    }

    /* private view holder class */
    private class ViewHolder {
        TextView authorname;
        TextView txtTitle;
        TextView txtBengTitle;
        TextView txtPage;
        ImageView thumbImage;
        ImageView infoImage;
        TextView readNumberOfPages;
        ProgressWheel progressDownload;
        ItemRow row;
    }

}
