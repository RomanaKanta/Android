package com.smartmux.videodownloader.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.smartmux.videodownloader.R;
import com.smartmux.videodownloader.modelclass.VisitedSiteListModelClass;

public class BookmarkListAdapter extends ArrayAdapter<VisitedSiteListModelClass> {
	Activity mContext;
	VisitedSiteListModelClass mBookmarkModel;
	ArrayList<VisitedSiteListModelClass> mArrayObject;

	public BookmarkListAdapter(Activity context,
			ArrayList<VisitedSiteListModelClass> objectArray) {
		super(context, R.layout.bookmark_list_row, objectArray);
		this.mContext = context;
		this.mArrayObject = objectArray;
	}

	// holder Class to contain inflated xml file elements
	static class ViewHolder {
		public TextView bookmark_title;
		public TextView bookmark_url;
		public TextView bookmark_id;
	}

	@Override
	public void notifyDataSetChanged() {
		// TODO Auto-generated method stub
		super.notifyDataSetChanged();
	}

	// Create ListView row
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// Get Model object from Array list
		mBookmarkModel = mArrayObject.get(position);
		ViewHolder mVHolder = null;

		View rowView = convertView;
		if (convertView == null) {

			// Layout inflater to call external xml layout ()
			LayoutInflater inflater = mContext.getLayoutInflater();

			rowView = inflater.inflate(R.layout.bookmark_list_row, parent, false);

			mVHolder = new ViewHolder();
			mVHolder.bookmark_id = (TextView) rowView.findViewById(R.id.textview_bookmark_id);
			mVHolder.bookmark_title = (TextView) rowView.findViewById(R.id.textview_bookmark_name);
			mVHolder.bookmark_url = (TextView) rowView
					.findViewById(R.id.textview_bookmark_url);
			rowView.setTag(mVHolder);
		} else
			mVHolder = (ViewHolder) rowView.getTag();

		mVHolder.bookmark_id.setText(mBookmarkModel.getmVsiteId().toString());
		mVHolder.bookmark_url.setText(mBookmarkModel.getmVsiteUrl().toString());
		mVHolder.bookmark_title.setText(mBookmarkModel.getmVsiteTitle().toString());

		return rowView;
	}
}