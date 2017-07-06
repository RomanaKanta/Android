package com.smartmux.videodownloader.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartmux.videodownloader.R;
import com.smartmux.videodownloader.database.FolderDetailDataSource;
import com.smartmux.videodownloader.modelclass.PlayListModelClass;
import com.smartmux.videodownloader.utils.SMSharePref;

public class PlaylistAdapter extends ArrayAdapter<PlayListModelClass> {
	
	Activity mContext;
	PlayListModelClass mPlaylistModel;
	ArrayList<PlayListModelClass> mListObject;
	// ArrayList<Integer> mDeleteItems = new ArrayList<Integer>();
	Animation viewOpen = null;
	String tag;
	FolderDetailDataSource mFolderDetailDataSource;
	

	public PlaylistAdapter(Activity context,
			ArrayList<PlayListModelClass> objectArray, String etag) {
		super(context, R.layout.playlist_row, objectArray);
		this.mContext = context;
		this.mListObject = objectArray;
		this.tag = etag;
		this.viewOpen = AnimationUtils
				.loadAnimation(mContext, R.anim.bottom_up);
		mFolderDetailDataSource = new FolderDetailDataSource(context);
	}

	// holder Class to contain inflated xml file elements
	static class ViewHolder {
		public TextView playlistName;
		public TextView songNo;
		public TextView id;
		public ImageView playlist_icon, info_icon;
		CheckBox checkBox_delete;
	}

	@Override
	public PlayListModelClass getItem(int position) {
		// TODO Auto-generated method stub
		return super.getItem(position);
	}

	public int getCount() {

		return mListObject.size();
	}

	// public ArrayList<Integer> getDeleteItems() {
	//
	// return mDeleteItems;
	//
	// }

	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void notifyDataSetChanged() {
		// TODO Auto-generated method stub
		super.notifyDataSetChanged();
	}

	// Create ListView row
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		// Get Model object from Array list
		mPlaylistModel = mListObject.get(position);
		ViewHolder mVHolder = null;

		View rowView = convertView;
		if (convertView == null) {

			// Layout inflater to call external xml layout ()
			LayoutInflater inflater = mContext.getLayoutInflater();

			rowView = inflater.inflate(R.layout.playlist_row, parent, false);

			mVHolder = new ViewHolder();

			mVHolder.id = (TextView) rowView
					.findViewById(R.id.textview_playlist_id);
			mVHolder.playlistName = (TextView) rowView
					.findViewById(R.id.textview_playlist_name);
			mVHolder.songNo = (TextView) rowView
					.findViewById(R.id.textview_song_no);

			mVHolder.playlist_icon = (ImageView) rowView
					.findViewById(R.id.image_playlist);
			mVHolder.info_icon = (ImageView) rowView
					.findViewById(R.id.image_info);

			mVHolder.checkBox_delete = (CheckBox) rowView
					.findViewById(R.id.checkBox_playlist);

			rowView.setTag(mVHolder);
		} else {
			mVHolder = (ViewHolder) rowView.getTag();
		}

		int plid = mPlaylistModel.getmPlayListId();
		int snum = mFolderDetailDataSource.getSelectedFolderVdoList(plid)
				.size();
		mVHolder.songNo.setText("" + snum);
		// mVHolder.songNo.setText(mListObject.get(position).getmSong().toString());

		mVHolder.id.setText(mPlaylistModel.getmPlayListId().toString());

		mVHolder.playlistName.setText(mPlaylistModel.getmPlayListName()
				.toString());

		mVHolder.playlist_icon.setImageResource(R.drawable.video_playlist_icon);
		mVHolder.playlist_icon.setColorFilter(Color.parseColor("#FFFFFF"),
				PorterDuff.Mode.SRC_ATOP);

		mVHolder.info_icon.setImageResource(R.drawable.info_button);
		mVHolder.info_icon.setColorFilter(Color.parseColor("#FFFFFF"),
				PorterDuff.Mode.SRC_ATOP);

		if (tag.equals("VideosFragment")) {
			mVHolder.info_icon.setVisibility(View.GONE);
			mVHolder.checkBox_delete.setVisibility(View.GONE);

		} else {

			if (SMSharePref.getPlaylistEditState(mContext).equals("Cancel")) {
				mVHolder.info_icon.setVisibility(View.GONE);
				mVHolder.checkBox_delete.setVisibility(View.VISIBLE);
			} else {
				mVHolder.info_icon.setVisibility(View.VISIBLE);
				mVHolder.checkBox_delete.setVisibility(View.GONE);
			}

		}

		if (mVHolder.checkBox_delete.isChecked()) {

			mListObject.get(position).setIsChecked(true);

		} else {
			mListObject.get(position).setIsChecked(false);

		}

		mVHolder.info_icon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				SMSharePref.saveRowPosition(mContext, position);
				SMSharePref.saveDataPosition(mContext, mListObject
						.get(position).getmPlayListId());

				Button a_option = (Button) mContext
						.findViewById(R.id.button_add_bookmark);
				a_option.setText("["
						+ mPlaylistModel.getmPlayListName().toString()
						+ "] Options");
				a_option.setTextColor(Color.parseColor("#8E8E8F"));

				mContext.findViewById(R.id.layout_add_bookmark).startAnimation(
						viewOpen);
				mContext.findViewById(R.id.tab_falseView).setVisibility(
						View.VISIBLE);
				mContext.findViewById(R.id.layout_add_bookmark).setVisibility(
						View.VISIBLE);

			}
		});

		return rowView;
	}

}