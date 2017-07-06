package com.smartmux.videodownloader.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.smartmux.videodownloader.R;
import com.smartmux.videodownloader.modelclass.DownloadListModelClass;

public class DownloadListAdapter extends ArrayAdapter<DownloadListModelClass> {
	Activity mContext;
	DownloadListModelClass mlistModel;
	ArrayList<DownloadListModelClass> mArrayObject;

	public DownloadListAdapter(Activity context,
			ArrayList<DownloadListModelClass> objectArray) {
		super(context, R.layout.dwnlist_row, objectArray);
		this.mContext = context;
		this.mArrayObject = objectArray;

	}

	// holder Class to contain inflated xml file elements
	static class ViewHolder {
		public TextView rowName;
		public ProgressBar mProgressbar;
		public TextView id;
		public TextView mProgress;
		public TextView mTotalSize;
		public TextView mRemainingTime;
	}

	@Override
	public void notifyDataSetChanged() {
		// TODO Auto-generated method stub
		super.notifyDataSetChanged();
	}
	
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return super.getCount();
	}


	@Override
	public DownloadListModelClass getItem(int position) {
		// TODO Auto-generated method stub
		return mArrayObject.get(position);
	}


	@Override
	public long getItemId(int position) {
		return position;
	}

	// Create ListView row
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// Get Model object from Array list
		mlistModel = mArrayObject.get(position);
		ViewHolder mVHolder = null;

		View rowView = convertView;
		if (convertView == null) {

			// Layout inflater to call external xml layout ()
			LayoutInflater inflater = mContext.getLayoutInflater();

			rowView = inflater.inflate(R.layout.dwnlist_row, parent, false);

			mVHolder = new ViewHolder();
			mVHolder.id = (TextView) rowView.findViewById(R.id.textView_id);
			mVHolder.rowName = (TextView) rowView.findViewById(R.id.textView_download_title);
			mVHolder.mTotalSize =  (TextView) rowView.findViewById(R.id.textView_total_size);
			mVHolder.mRemainingTime = (TextView)rowView.findViewById(R.id.textView_remaining_time);
			mVHolder.mProgress =  (TextView) rowView.findViewById(R.id.textView_progress);
			mVHolder.mProgressbar = (ProgressBar) rowView
					.findViewById(R.id.download_progressBar);
			rowView.setTag(mVHolder);
		} else{
			mVHolder = (ViewHolder) rowView.getTag();
		}

		mVHolder.mProgressbar.setProgress(Integer.parseInt(mlistModel.getmProgress()));
		mVHolder.mTotalSize.setText("0");
		mVHolder.id.setText(mlistModel.getmSerialID().toString() );
		mVHolder.rowName.setText(mlistModel.getmRowTitle().toString());
		mVHolder.mProgress.setText(mlistModel.getmProgress().toString() + " %");

		return rowView;
	}
}
