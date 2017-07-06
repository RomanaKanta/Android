package com.smartmux.videodownloader.fragment;

import java.io.File;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.smartmux.videodownloader.MailSendingActivity;
import com.smartmux.videodownloader.R;
import com.smartmux.videodownloader.VideoPlayerActivity;
import com.smartmux.videodownloader.adapter.FileListAdapter;
import com.smartmux.videodownloader.adapter.PlaylistAdapter;
import com.smartmux.videodownloader.database.FolderDataSource;
import com.smartmux.videodownloader.database.FolderDetailDataSource;
import com.smartmux.videodownloader.database.VideoDataSource;
import com.smartmux.videodownloader.modelclass.FileListModelClass;
import com.smartmux.videodownloader.modelclass.PlayListModelClass;
import com.smartmux.videodownloader.utils.SMConstant;
import com.smartmux.videodownloader.utils.SMSharePref;
import com.smartmux.videodownloader.utils.SMToast;

public class VideosFragment extends Fragment implements OnClickListener {

	String TAG = "VideosFragment";

	FolderDataSource mFolderDataSource;
	FolderDetailDataSource mFolderDetailDataSource;
	PlaylistAdapter mPlaylistAdapter;
	VideoDataSource mVideoDataSource;
	FileListModelClass mFileListModel;
	int mPosition;
	TextView title, vdoDelete;
	ListView mFileList = null;
	ArrayList<FileListModelClass> mModel = new ArrayList<FileListModelClass>();
	ArrayList<PlayListModelClass> mPLModelList = new ArrayList<PlayListModelClass>();
	FileListAdapter mFileListAdapter = null;
	Button a_option, a_file_rename, a_file_cancel, a_file_open, a_file_send,
			a_file_add_palylist = null;
	Animation viewOpen, viewClose;
	// ListView mPlaylist;
	// LinearLayout layout_seclect_playlist;
	// ImageView image_close;
	File folder;
	File[] listOfFiles;
	public TextView tvVdoEdit = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mFolderDataSource = new FolderDataSource(getActivity());
		mVideoDataSource = new VideoDataSource(getActivity());
		mFolderDetailDataSource = new FolderDetailDataSource(getActivity());

		View rootView = inflater.inflate(R.layout.fragment_videos, container,
				false);

		viewOpen = AnimationUtils
				.loadAnimation(getActivity(), R.anim.bottom_up);

		viewClose = AnimationUtils.loadAnimation(getActivity(),
				R.anim.bottom_down);

		createDirectory();

		folder = new File(SMConstant.mediaStorageDir.getPath());

		SMSharePref.saveVideoEditState(getActivity(), "Edit");
		
		initFragmentContent(rootView);
		initActivityContent();

		return rootView;
	}

	void initFragmentContent(View rootView) {

		title = (TextView) rootView.findViewById(R.id.textView_title_file);
		title.setText(R.string.videos);

		vdoDelete = (TextView) rootView.findViewById(R.id.textView_vdo_delete);
		vdoDelete.setVisibility(View.GONE);

		tvVdoEdit = (TextView)rootView.findViewById(R.id.textview_video_edit);
		if(SMSharePref.getVideoEditState(getActivity()).equals("Cancel")){
		tvVdoEdit.setText(SMSharePref.getVideoEditState(getActivity()));
		vdoDelete.setVisibility(View.VISIBLE);
		
		}
		tvVdoEdit.setOnClickListener(this);
		vdoDelete.setOnClickListener(this);
		
		mFileList = (ListView) rootView.findViewById(R.id.listview_filelist);

		// /// Show all downloaded files/////

		mModel = mVideoDataSource.getVideoList();

		Log.e("VIDEO DB LENGTH: ", "" + mVideoDataSource.getVideoList().size());

		mFileListAdapter = new FileListAdapter(getActivity(), mModel);
		mFileList.setAdapter(mFileListAdapter);
		mFileList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {

				String title = mModel.get(position).getmFileName();
				String path = mModel.get(position).getmFileUrl();

				Intent intent = new Intent(getActivity(),
						VideoPlayerActivity.class);
				intent.putExtra("video_path", path);
				intent.putExtra("video_title", title);
				intent.putExtra("Tag", TAG);
				startActivity(intent);
				SMSharePref.setBackCode(getActivity());
				getActivity().overridePendingTransition(R.anim.push_left_in, 0);

			}
		});

		mPLModelList = mFolderDataSource.getFolderList();
		mPlaylistAdapter = new PlaylistAdapter(getActivity(), mPLModelList, TAG);

	}

	void initActivityContent() {

		a_option = (Button) getActivity().findViewById(R.id.button_one);
		a_option.setVisibility(View.VISIBLE);

		a_file_rename = (Button) getActivity().findViewById(R.id.button_two);
		a_file_rename.setText(R.string.rename);
		a_file_rename.setTextColor(Color.parseColor("#2C5BFF"));
		a_file_rename.setVisibility(View.VISIBLE);

		a_file_add_palylist = (Button) getActivity().findViewById(
				R.id.button_three);
		a_file_add_palylist.setText(R.string.add_palylist);
		a_file_add_palylist.setTextColor(Color.parseColor("#2C5BFF"));
		a_file_add_palylist.setVisibility(View.VISIBLE);

		a_file_open = (Button) getActivity().findViewById(
				R.id.button_add_bookmark);
		a_file_open.setText(R.string.open);
		a_file_open.setTextColor(Color.parseColor("#2C5BFF"));

		a_file_send = (Button) getActivity()
				.findViewById(R.id.button_home_page);
		a_file_send.setText(R.string.send_mail);
		a_file_send.setTextColor(Color.parseColor("#2C5BFF"));

		a_file_cancel = (Button) getActivity().findViewById(R.id.button_cancel);

		a_file_rename.setOnClickListener(this);
		a_file_add_palylist.setOnClickListener(this);
		a_file_open.setOnClickListener(this);
		a_file_send.setOnClickListener(this);
		a_file_cancel.setOnClickListener(this);
		getActivity().findViewById(R.id.tab_falseView).setOnClickListener(this);

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.button_two:

			mPosition = SMSharePref.getRowPosition(getActivity());
			showInputDialog(mModel.get(mPosition).getmFileName());

			getActivity().findViewById(R.id.tab_falseView).setVisibility(
					View.GONE);
			getActivity().findViewById(R.id.layout_add_bookmark).setVisibility(
					View.GONE);

			break;

		case R.id.button_three:

			// ////////////###########################\\\\\\\\\\\\\\\\\\

			setPlayList();

			getActivity().findViewById(R.id.tab_falseView).setVisibility(
					View.GONE);
			getActivity().findViewById(R.id.layout_add_bookmark).setVisibility(
					View.GONE);

			break;

		case R.id.button_add_bookmark:

			Toast.makeText(getActivity(), "open in" + "", 1000).show();

			break;

		case R.id.button_home_page:

			Intent mailIntent = new Intent(getActivity(),
					MailSendingActivity.class);
			startActivity(mailIntent);
			
			getActivity().findViewById(R.id.layout_add_bookmark)
					.startAnimation(viewClose);
			getActivity().findViewById(R.id.tab_falseView).setVisibility(
					View.GONE);
			getActivity().findViewById(R.id.layout_add_bookmark).setVisibility(
					View.GONE);
			break;

		case R.id.button_cancel:

			getActivity().findViewById(R.id.layout_add_bookmark)
					.startAnimation(viewClose);
			getActivity().findViewById(R.id.tab_falseView).setVisibility(
					View.GONE);
			getActivity().findViewById(R.id.layout_add_bookmark).setVisibility(
					View.GONE);

			break;

		case R.id.tab_falseView:

			getActivity().findViewById(R.id.layout_add_bookmark)
					.startAnimation(viewClose);
			getActivity().findViewById(R.id.tab_falseView).setVisibility(
					View.GONE);
			getActivity().findViewById(R.id.layout_add_bookmark).setVisibility(
					View.GONE);

			break;

		case R.id.textview_video_edit:

			FileListModelClass mVdoModel = new FileListModelClass();
			
			if (tvVdoEdit.getText().equals("Edit")) {
				
				SMSharePref.saveVideoEditState(getActivity(), "Cancel");
				tvVdoEdit.setText(R.string.cancel);
				vdoDelete.setVisibility(View.VISIBLE);
				vdoDelete.setText(R.string.delete_playlist);
				
				for (int i = 0; i < mModel.size(); i++) {
					mVdoModel = mModel.get(i);
//					mVdoModel.setEdit(true);
					mFileListAdapter.notifyDataSetChanged();

				}
				

			} else {
				
				SMSharePref.saveVideoEditState(getActivity(), "Edit");
				tvVdoEdit.setText(R.string.edit);
				vdoDelete.setVisibility(View.GONE);
				
				for (int i = 0; i < mModel.size(); i++) {
					mVdoModel = mModel.get(i);
//					mVdoModel.setEdit(true);
					mFileListAdapter.notifyDataSetChanged();

				}
			}


			break;

		case R.id.textView_vdo_delete:

			AlertDialog.Builder alertDialog = new AlertDialog.Builder(
					getActivity());

			alertDialog.setMessage(R.string.delete_alart_msg);
			//alertDialog.setCancelable(false);
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

			break;
			
			
		default:
			break;

		}

	}

	protected void showInputDialog(String pTitle) {

		// get prompts.xml view
		LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
		View promptView = layoutInflater.inflate(R.layout.input_dialog, null);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				getActivity());
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
							SMToast.showToast(getActivity(),
									getString(R.string.empty_video));
							dialog.dismiss();

						} else {

							FileListModelClass fileModel = new FileListModelClass();

							fileModel = mModel.get(mPosition);

							int pos = SMSharePref
									.getDataPosition(getActivity());

							String name = mModel.get(mPosition).getmFileName();
							String url = mModel.get(mPosition).getmFileUrl();

							File oldurl = new File(url);
							File newurl = new File(folder + "/"
									+ video_file_name);
							oldurl.renameTo(newurl);
							String duration = mModel.get(mPosition)
									.getmFileDuration();
							String size = mModel.get(mPosition).getmFileSize();
							String date = mModel.get(mPosition).getmFileDate();
							String time = mModel.get(mPosition).getmFileTime();

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

	private void setPlayList() {

		LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
		View promptView = layoutInflater
				.inflate(R.layout.select_palylist, null);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setCustomTitle(promptView);
		// builder.setTitle("Make your selection");
		builder.setAdapter(mPlaylistAdapter,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						// Do something with the selection
						// mDoneButton.setText(items[item]);

						insertItem(item);

					}
				});

		AlertDialog alert = builder.create();
		alert.show();
	}

	private void insertItem(int p) {

		// TODO Auto-generated method stub
		mPosition = SMSharePref.getRowPosition(getActivity());
		final int vdoid = mModel.get(mPosition).getmId();
		final String vdo_name = mModel.get(mPosition).getmFileName();
		final int pID = mPLModelList.get(p).getmPlayListId();
		final String fld_name = mPLModelList.get(p).getmPlayListName();

		LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
		View promptView = layoutInflater.inflate(R.layout.custom_dialog, null);

		final TextView vdo_title = (TextView) promptView
				.findViewById(R.id.textView_add_playlist_video);

		vdo_title.setText(vdo_name);

		final TextView fld_title = (TextView) promptView
				.findViewById(R.id.textView_add_playlist_folder);

		fld_title.setText(fld_name);

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

		alertDialog.setView(promptView);

		alertDialog.setPositiveButton(R.string.yes,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
				
						FileListModelClass pmClass = new FileListModelClass(
								pID, fld_name, vdoid);
					

						long insert = mFolderDetailDataSource
								.addNewVideoinFolder(pmClass);

						if (insert >= 0) {
							Log.d("FOLDER: ", "inserted");
						} else {
							Log.e("FOLDER: ", "insertion fail");
						}

						dialog.dismiss();
					}
				});

		alertDialog.setNegativeButton(R.string.no,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				});

		alertDialog.create();
		alertDialog.show();

	}

}
