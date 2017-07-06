package com.smartmux.videodownloader;

import java.io.File;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.smartmux.videodownloader.adapter.FileListAdapter;
import com.smartmux.videodownloader.database.FolderDetailDataSource;
import com.smartmux.videodownloader.database.VideoDataSource;
import com.smartmux.videodownloader.lockscreen.utils.AppExtra;
import com.smartmux.videodownloader.modelclass.FileListModelClass;
import com.smartmux.videodownloader.modelclass.PlayListModelClass;
import com.smartmux.videodownloader.utils.SMConstant;
import com.smartmux.videodownloader.utils.SMSharePref;
import com.smartmux.videodownloader.utils.SMToast;

public class PlayListItemActivity extends FragmentActivity implements
		OnItemClickListener, OnClickListener {

	int mPosition;
	TextView title, back, vdoEdit;
	ListView mVdoList = null;
	PlayListModelClass pmClass;
	FolderDetailDataSource mFolderDetailDataSource;
	VideoDataSource mVideoDataSource;
	ArrayList<FileListModelClass> mFLModel = new ArrayList<FileListModelClass>();
	ArrayList<Integer> vID = new ArrayList<Integer>();
	ArrayList<FileListModelClass> mVDOModel = new ArrayList<FileListModelClass>();
	FileListAdapter mFileListAdapter;
	File folder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		setContentView(R.layout.activity_playlist_video);

		createDirectory();
		SMSharePref.saveVideoEditState(PlayListItemActivity.this, "Edit");
		folder = new File(SMConstant.mediaStorageDir.getPath());
		mVideoDataSource = new VideoDataSource(this);
		mFolderDetailDataSource = new FolderDetailDataSource(this);
		mVdoList = (ListView) findViewById(R.id.listview_playlistvdolist);

		if (getIntent().hasExtra("PLid")) {

			int plID = getIntent().getExtras().getInt("PLid");

			mFLModel = mFolderDetailDataSource.getSelectedFolderVdoList(plID);

			for (int i = 0; i < mFLModel.size(); i++) {

				int vdoID = mFLModel.get(i).getmFileId();

				mVDOModel.add(mVideoDataSource.getVideoDetail(vdoID));

			}

			// int vdoID = mFLModel.

			mFileListAdapter = new FileListAdapter(this, mVDOModel);
			mVdoList.setAdapter(mFileListAdapter);
			mVdoList.setOnItemClickListener(this);

		}

		// // mFileListAdapter = new FileListAdapter(this, mPLModel);
		// mVdoList.setAdapter(mFileListAdapter);

		title = (TextView) findViewById(R.id.textView_title_file);
		back = (TextView) findViewById(R.id.textView_back_text);
		vdoEdit = (TextView) findViewById(R.id.textview_video_edit);

		vdoEdit.setOnClickListener(this);
		back.setOnClickListener(this);

		this.findViewById(R.id.tab_falseView).setOnClickListener(this);
		this.findViewById(R.id.button_two).setOnClickListener(this);
		this.findViewById(R.id.button_home_page).setOnClickListener(this);
		this.findViewById(R.id.button_add_bookmark).setOnClickListener(this);
		this.findViewById(R.id.button_cancel).setOnClickListener(this);

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		// TODO Auto-generated method stub

		String title = mVDOModel.get(position).getmFileName();
		String path = mVDOModel.get(position).getmFileUrl();

		Intent intent = new Intent(this, VideoPlayerActivity.class);
		intent.putExtra("video_path", path);
		intent.putExtra("video_title", title);
		startActivity(intent);
		overridePendingTransition(R.anim.push_left_in, 0);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tab_falseView:
			this.findViewById(R.id.tab_falseView).setVisibility(View.GONE);
			this.findViewById(R.id.layout_add_bookmark)
					.setVisibility(View.GONE);
			break;

		case R.id.button_two:

			mPosition = SMSharePref.getRowPosition(this);
			showInputDialog(mVDOModel.get(mPosition).getmFileName());

			this.findViewById(R.id.tab_falseView).setVisibility(View.GONE);
			this.findViewById(R.id.layout_add_bookmark)
					.setVisibility(View.GONE);

			break;

		case R.id.button_home_page:

			Toast.makeText(this, "send via mail" + "", 1000).show();

			Intent mailIntent = new Intent(PlayListItemActivity.this,
					MailSendingActivity.class);
			startActivity(mailIntent);
			this.findViewById(R.id.tab_falseView).setVisibility(View.GONE);
			this.findViewById(R.id.layout_add_bookmark)
					.setVisibility(View.GONE);
			break;

		case R.id.button_add_bookmark:

			Toast.makeText(this, "open in" + "", 1000).show();

			this.findViewById(R.id.tab_falseView).setVisibility(View.GONE);
			this.findViewById(R.id.layout_add_bookmark)
					.setVisibility(View.GONE);
			break;

		case R.id.button_cancel:
			this.findViewById(R.id.tab_falseView).setVisibility(View.GONE);
			this.findViewById(R.id.layout_add_bookmark)
					.setVisibility(View.GONE);
			break;

		case R.id.textView_back_text:

			if (back.getText().equals("Delete")) {

				AlertDialog.Builder alertDialog = new AlertDialog.Builder(
						PlayListItemActivity.this);

				alertDialog.setMessage(R.string.delete_alart_msg);
				// alertDialog.setCancelable(false);
				alertDialog.setPositiveButton(R.string.yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
							}
						});

				alertDialog.setNegativeButton(R.string.no,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
							}
						});
				alertDialog.show();

			} else {
				Intent eventsIntent = new Intent(PlayListItemActivity.this,
						MainActivity.class);

				eventsIntent.putExtra("settab", "Playlist");
				startActivity(eventsIntent);
				overridePendingTransition(R.anim.push_right_in,
						R.anim.push_right_out);
				SMSharePref.setBackCode(getApplicationContext());
			}

			break;

		case R.id.textview_video_edit:

			FileListModelClass mVdoModel = new FileListModelClass();

			if (vdoEdit.getText().equals("Edit")) {

				SMSharePref.saveVideoEditState(PlayListItemActivity.this,
						"Cancel");
				vdoEdit.setText(R.string.cancel);
				back.setText(R.string.delete_playlist);

				for (int i = 0; i < mVDOModel.size(); i++) {
					mVdoModel = mVDOModel.get(i);
					mFileListAdapter.notifyDataSetChanged();

				}

			} else {

				SMSharePref.saveVideoEditState(PlayListItemActivity.this,
						"Edit");
				vdoEdit.setText(R.string.edit);
				back.setText(R.string.back);


				for (int i = 0; i < mVDOModel.size(); i++) {
					mVdoModel = mVDOModel.get(i);
					mFileListAdapter.notifyDataSetChanged();

				}
			}

			break;

		default:
			break;

		}
	}

	protected void showInputDialog(String pTitle) {

		// get prompts.xml view
		LayoutInflater layoutInflater = LayoutInflater
				.from(PlayListItemActivity.this);
		View promptView = layoutInflater.inflate(R.layout.input_dialog, null);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				PlayListItemActivity.this);
		alertDialogBuilder.setView(promptView);

		final TextView title = (TextView) promptView
				.findViewById(R.id.textView_playlist_title);

		title.setText(pTitle);

		final TextView subtitle = (TextView) promptView
				.findViewById(R.id.textView_playlist_sub_title);

		subtitle.setVisibility(View.VISIBLE);

		final EditText editText = (EditText) promptView
				.findViewById(R.id.edittext_playlist_title);
		// setup a dialog window
		alertDialogBuilder.setCancelable(false);
		// Add the buttons
		alertDialogBuilder.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				});

		alertDialogBuilder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog, int id) {

						String video_file_name = editText.getText().toString();

						if (video_file_name.equals("")) {
							SMToast.showToast(PlayListItemActivity.this,
									getString(R.string.empty_video));
							dialog.dismiss();

						} else {

							FileListModelClass fileModel = new FileListModelClass();

							fileModel = mVDOModel.get(mPosition);

							int pos = SMSharePref
									.getDataPosition(PlayListItemActivity.this);

							String name = mVDOModel.get(mPosition)
									.getmFileName();
							String url = mVDOModel.get(mPosition).getmFileUrl();

							File oldurl = new File(url);
							File newurl = new File(folder + "/"
									+ video_file_name);
							oldurl.renameTo(newurl);
							String duration = mVDOModel.get(mPosition)
									.getmFileDuration();
							String size = mVDOModel.get(mPosition)
									.getmFileSize();
							String date = mVDOModel.get(mPosition)
									.getmFileDate();
							String time = mVDOModel.get(mPosition)
									.getmFileTime();

							fileModel.setmFileName(video_file_name);
							fileModel.setmFileUrl(newurl.toString());

							fileModel = new FileListModelClass(video_file_name,
									duration, size, date, time, newurl
											.toString());

							long updated = mVideoDataSource.updateVideoData(
									pos, fileModel);
							if (updated >= 0) {
								Log.d("FOLDER: ", "updated");
							} else {
								Log.e("FOLDER: ", "update fail");
							}

							//
							// if (!video_file_name.trim().endsWith(".plist")) {
							// video_file_name = video_file_name.trim() +
							// ".plist";
							// }

							// FileListModelClass fileModel = new
							// FileListModelClass();
							//
							// fileModel = mFLModel.get(mPosition);
							//
							// fileModel.setmFileName(video_file_name);
							//
							// int pos = SMSharePref
							// .getDataPosition(PlayListItemActivity.this);
							//
							// int plID =
							// mFLModel.get(mPosition).getmPlayListId();
							//
							// String fld_name = mFLModel.get(mPosition)
							// .getmPlayListName();
							//
							// int vdoid = mFLModel.get(mPosition).getmFileId();
							//
							// String name = mFLModel.get(mPosition)
							// .getmFileName();
							// String url =
							// mFLModel.get(mPosition).getmFileUrl();
							//
							// File oldurl = new File(url);
							// File newurl = new File(folder + "/"
							// + video_file_name);
							// oldurl.renameTo(newurl);
							// String duration = mFLModel.get(mPosition)
							// .getmFileDuration();
							// String size = mFLModel.get(mPosition)
							// .getmFileSize();
							// String date = mFLModel.get(mPosition)
							// .getmFileDate();
							// String time = mFLModel.get(mPosition)
							// .getmFileTime();
							//
							// fileModel = new FileListModelClass(plID,
							// fld_name, eVdoID)
							// FileListModelClass pmClass = new
							// FileListModelClass(
							// pID, fld_name, vdo_name,
							// vdo_dur, vdo_size, vdo_date, vdo_time, vdo_url);
							// long updated = mFolderDetailDataSource
							// .updateFolderVideoData(pos, fileModel);
							// if (updated >= 0) {
							// Log.d("FOLDER: ", "updated");
							// } else {
							// Log.e("FOLDER: ", "update fail");
							// }

						}

						mFileListAdapter.notifyDataSetChanged();

					}
				});

		// create an alert dialog
		AlertDialog alert = alertDialogBuilder.create();
		alert.show();
	}

	static void createDirectory() {
		if (!SMConstant.mediaStorageDir.exists()) {
			if (!SMConstant.mediaStorageDir.mkdirs()) {
				Log.d(SMConstant.FOLDER_NAME, "Oops! Failed create "
						+ SMConstant.FOLDER_NAME + " directory");

			}
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		String security = SMSharePref.getSecurity(PlayListItemActivity.this);
		int event_code = SMSharePref.getReturnCode(getApplicationContext());
		if (security.equals(SMConstant.on) && event_code == AppExtra.HOME_CODE) {
//			Toast.makeText(getApplicationContext(),
//					"event_code" + event_code, 1000).show();
			
		Intent i = new Intent(PlayListItemActivity.this, AppLockActivity.class);
		i.putExtra("passcode", "password_match");
        startActivity(i);
        overridePendingTransition(R.anim.bottom_up, 0);
	}
		
	}

	 @Override
		protected void onUserLeaveHint() {
		 SMSharePref.setHomeCode(getApplicationContext());
			super.onUserLeaveHint();
	}

	
}
