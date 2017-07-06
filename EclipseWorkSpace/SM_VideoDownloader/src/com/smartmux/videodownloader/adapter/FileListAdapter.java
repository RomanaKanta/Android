package com.smartmux.videodownloader.adapter;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.ThumbnailUtils;
import android.provider.MediaStore.Video.Thumbnails;
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
import com.smartmux.videodownloader.modelclass.FileListModelClass;
import com.smartmux.videodownloader.utils.SMConstant;
import com.smartmux.videodownloader.utils.SMSharePref;

public class FileListAdapter extends ArrayAdapter<FileListModelClass> {
	Activity mContext;
	FileListModelClass mFileModel;
	ArrayList<FileListModelClass> mFileObject;
	Animation viewOpen;
	Bitmap bmThumbnail;

	public FileListAdapter(Activity context,
			ArrayList<FileListModelClass> objectArray) {
		super(context, R.layout.filelist_row, objectArray);
		this.mContext = context;
		this.mFileObject = objectArray;
		this.viewOpen = AnimationUtils
				.loadAnimation(mContext, R.anim.bottom_up);
	}

	// holder Class to contain inflated xml file elements
	static class ViewHolder {
		public TextView file_title;
		public TextView file_duration;
		public TextView file_size;
		public TextView file_date;
		public TextView file_time;
		public TextView file_id;
		public ImageView file_info, file_thumb;
		CheckBox checkBox_delete_vdo;
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
		mFileModel = mFileObject.get(position);
		ViewHolder mVHolder = null;

		View rowView = convertView;
		if (convertView == null) {

			// Layout inflater to call external xml layout ()
			LayoutInflater inflater = mContext.getLayoutInflater();

			rowView = inflater.inflate(R.layout.filelist_row, parent, false);

			mVHolder = new ViewHolder();
			mVHolder.file_title = (TextView) rowView
					.findViewById(R.id.textview_file_name);
			mVHolder.file_duration = (TextView) rowView
					.findViewById(R.id.textview_file_duration);
			mVHolder.file_size = (TextView) rowView
					.findViewById(R.id.textview_file_size);
			mVHolder.file_date = (TextView) rowView
					.findViewById(R.id.textview_file_date);
			mVHolder.file_time = (TextView) rowView
					.findViewById(R.id.textview_file_time);
			mVHolder.file_info = (ImageView) rowView
					.findViewById(R.id.image_file_info);
			mVHolder.file_thumb = (ImageView) rowView
					.findViewById(R.id.image_filethumb);

			mVHolder.checkBox_delete_vdo = (CheckBox) rowView
					.findViewById(R.id.checkBox_vdo);

			rowView.setTag(mVHolder);
		} else {
			mVHolder = (ViewHolder) rowView.getTag();
		}

		mVHolder.file_duration
				.setText(mFileModel.getmFileDuration().toString());
		mVHolder.file_size.setText(mFileModel.getmFileSize().toString());
		mVHolder.file_date.setText(mFileModel.getmFileDate().toString());
		mVHolder.file_time.setText(mFileModel.getmFileTime().toString());

		if (SMSharePref.getVideoEditState(mContext).equals("Cancel")) {
			mVHolder.file_info.setVisibility(View.GONE);
			mVHolder.checkBox_delete_vdo.setVisibility(View.VISIBLE);
		} else {
			mVHolder.file_info.setVisibility(View.VISIBLE);
			mVHolder.checkBox_delete_vdo.setVisibility(View.GONE);
		}

		String capital = SMSharePref.getCapitalize(mContext);
		if (capital.equals(SMConstant.on)) {
			// mVHolder.file_title.setInputType(InputType.TYPE_CLASS_TEXT |
			// InputType.TYPE_TEXT_FLAG_CAP_WORDS);
			// String capTitle =
			// WordUtils.capitalize(mFileModel.getmFileName().toString());

			String capTitle = capitalized(mFileModel.getmFileName().toString());

			mVHolder.file_title.setText(capTitle);

		} else {
			mVHolder.file_title.setText(mFileModel.getmFileName().toString());
		}

		String thumbnails = SMSharePref.getThumbnails(mContext);
		if (thumbnails.equals(SMConstant.on)) {
			bmThumbnail = ThumbnailUtils.createVideoThumbnail(
					mFileObject.get(position).getmFileUrl(),
					Thumbnails.MICRO_KIND);

			mVHolder.file_thumb.setImageBitmap(bmThumbnail);

		} else {
			mVHolder.file_thumb.setImageResource(R.drawable.tab_icon_file_up);
			mVHolder.file_thumb.setColorFilter(Color.parseColor("#FFFFFF"),
					PorterDuff.Mode.SRC_ATOP);
		}

		mVHolder.file_info.setImageResource(R.drawable.info_button);
		mVHolder.file_info.setColorFilter(Color.parseColor("#FFFFFF"),
				PorterDuff.Mode.SRC_ATOP);

		mVHolder.file_info.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				SMSharePref.saveRowPosition(mContext, position);
				SMSharePref.saveDataPosition(mContext, mFileObject
						.get(position).getmId());

				Button a_option = (Button) mContext
						.findViewById(R.id.button_one);
				a_option.setText("["
						+ mFileObject.get(position).getmFileName().toString()
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

	private String capitalized(String str) {
		StringBuffer stringbf = new StringBuffer();
		Matcher m = Pattern
				.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(
						str);
		while (m.find()) {
			m.appendReplacement(stringbf, m.group(1).toUpperCase()
					+ m.group(2).toLowerCase());
		}
		return m.appendTail(stringbf).toString();
	}
}