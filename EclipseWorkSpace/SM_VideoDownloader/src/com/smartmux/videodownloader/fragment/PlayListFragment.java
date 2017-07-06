package com.smartmux.videodownloader.fragment;

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

import com.smartmux.videodownloader.PlayListItemActivity;
import com.smartmux.videodownloader.R;
import com.smartmux.videodownloader.adapter.PlaylistAdapter;
import com.smartmux.videodownloader.database.FolderDataSource;
import com.smartmux.videodownloader.modelclass.PlayListModelClass;
import com.smartmux.videodownloader.utils.SMSharePref;
import com.smartmux.videodownloader.utils.SMToast;

public class PlayListFragment extends Fragment implements OnClickListener, OnItemClickListener  {
	
	String TAG = "PlayListFragment";
	FolderDataSource mFolderDataSource;
	PlayListModelClass mPlayListModel;
	int position;
	ListView mPlayList = null;
	Button btnCreatePlayList = null;
	public TextView tvEdit, tvDelete = null;
	// ImageView info_icon,delete_icon = null;
	Button a_option, a_playlist_rename, a_playlist_cancel = null;
	PlaylistAdapter mPlaylistAdapter;
	ArrayList<PlayListModelClass> mPLModelList = new ArrayList<PlayListModelClass>();
	Animation viewOpen, viewClose;
	public ArrayList<Integer> mDeleteItems = new ArrayList<Integer>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_playlist, container,
				false);
		mFolderDataSource = new FolderDataSource(getActivity());
		viewOpen = AnimationUtils
				.loadAnimation(getActivity(), R.anim.bottom_up);

		viewClose = AnimationUtils.loadAnimation(getActivity(),
				R.anim.bottom_down);

		SMSharePref.savePlaylistEditState(getActivity(), "Edit");
		
		initFragmentContent(rootView);
		initActivityContent();

		return rootView;
	}

	void initFragmentContent(View rootView) {

		tvEdit = (TextView) rootView.findViewById(R.id.textview_playlist_edit);
		
		tvDelete = (TextView)rootView.findViewById(R.id.textView_delete);
		
		if(SMSharePref.getPlaylistEditState(getActivity()).equals("Cancel")){
			tvEdit.setText(SMSharePref.getPlaylistEditState(getActivity()));
			tvDelete.setVisibility(View.VISIBLE);
		
		}
		
		mPlayList = (ListView) rootView.findViewById(R.id.listview_playlist);

		btnCreatePlayList = (Button) rootView
				.findViewById(R.id.button_create_playlist);

		btnCreatePlayList.setOnClickListener(this);
		tvEdit.setOnClickListener(this);
		tvDelete.setOnClickListener(this);

		mPLModelList = mFolderDataSource.getFolderList();
		mPlaylistAdapter = new PlaylistAdapter(getActivity(), mPLModelList,TAG);
		mPlayList.setAdapter(mPlaylistAdapter);
		mPlayList.setOnItemClickListener(this);
	}

	void initActivityContent() {

		getActivity().findViewById(R.id.button_one).setVisibility(View.GONE);

		getActivity().findViewById(R.id.button_two).setVisibility(View.GONE);

		getActivity().findViewById(R.id.button_three).setVisibility(View.GONE);

		a_option = (Button) getActivity()
				.findViewById(R.id.button_add_bookmark);
		a_playlist_rename = (Button) getActivity().findViewById(
				R.id.button_home_page);
		a_playlist_rename.setText(R.string.playlist_rename);
		a_playlist_rename.setTextColor(Color.parseColor("#EE2623"));
		a_playlist_cancel = (Button) getActivity().findViewById(
				R.id.button_cancel);

		a_option.setOnClickListener(this);
		a_playlist_rename.setOnClickListener(this);
		a_playlist_cancel.setOnClickListener(this);
		getActivity().findViewById(R.id.tab_falseView).setOnClickListener(this);

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// PlayListModelClass mPlayModel = new PlayListModelClass();
		// for (int i = 0; i < mModel.size(); i++) {
		// mPlayModel = mModel.get(i);
		// mPlayModel.setEdit(false);
		// mPlaylistAdapter.notifyDataSetChanged();
		//
		// }
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.button_create_playlist:

			showInputDialog("Enter Playlist Title", false);

			break;

		case R.id.textView_delete:

			AlertDialog.Builder alertDialog = new AlertDialog.Builder(
					getActivity());

			alertDialog.setMessage(R.string.delete_alart_msg);
			//alertDialog.setCancelable(false);
			alertDialog.setPositiveButton(R.string.yes,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int which) {
//							ArrayList<Integer> mDeleteItems = mPlaylistAdapter.getDeleteItems();
//							Toast.makeText(getActivity(), "id " + mDeleteItems.size(),
//									1000).show();
//							for(int i = 0; i < mDeleteItems.size(); i++){
//								
//							}
							
							
//							dialog.cancel();
							
//							 PlayListModelClass mPlayModel = new PlayListModelClass();
//							 for (int i = 0; i < mPLModelList.size(); i++) {
//							 mPlayModel = mPLModelList.get(i);
//							 if(mPlayModel.isChecked()){
//								 Toast.makeText(getActivity(), "id " + mPlayModel.getmPlayListId(),
//											1000).show();
//							 }
//							 mPlaylistAdapter.notifyDataSetChanged();
//							
//							 }
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
			
		case R.id.textview_playlist_edit:

			PlayListModelClass mPlayModel = new PlayListModelClass();

			if (tvEdit.getText().equals("Edit")) {

				tvDelete.setVisibility(View.VISIBLE);
				SMSharePref.savePlaylistEditState(getActivity(), "Cancel");
				tvEdit.setText(R.string.cancel);

				for (int i = 0; i < mPLModelList.size(); i++) {
					mPlayModel = mPLModelList.get(i);
					mPlayModel.setEdit(true);
					mPlaylistAdapter.notifyDataSetChanged();

				}

				// info_icon.setVisibility(View.GONE);
				// delete_icon.setVisibility(View.VISIBLE);

			} else {
				
				tvDelete.setVisibility(View.GONE);
				SMSharePref.savePlaylistEditState(getActivity(), "Edit");
				tvEdit.setText(R.string.edit);

				for (int i = 0; i < mPLModelList.size(); i++) {
					mPlayModel = mPLModelList.get(i);
					mPlayModel.setEdit(false);
					mPlaylistAdapter.notifyDataSetChanged();

				}
			}

			break;

		case R.id.button_home_page:

			position = SMSharePref.getRowPosition(getActivity());
			showInputDialog(mPLModelList.get(position).getmPlayListName(), true);

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

		default:
			break;

		}

	}

	protected void showInputDialog(String pTitle, final boolean flag) {

		// get prompts.xml view
		LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
		View promptView = layoutInflater.inflate(R.layout.input_dialog, null);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				getActivity());
		alertDialogBuilder.setView(promptView);

		final TextView title = (TextView) promptView
				.findViewById(R.id.textView_playlist_title);

		title.setText(pTitle);

		if (flag) {
			final TextView subtitle = (TextView) promptView
					.findViewById(R.id.textView_playlist_sub_title);

			subtitle.setVisibility(View.VISIBLE);
		}

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

						String folder_name = editText.getText().toString();

						if (folder_name.equals("")) {
							SMToast.showToast(getActivity(),
									getString(R.string.empty_folder));
							dialog.dismiss();

						} else {

							if (!folder_name.trim().endsWith(".plist")) {
								folder_name = folder_name.trim() + ".plist";
							}

							if (flag) {

								PlayListModelClass playModel = new PlayListModelClass();

								playModel = mPLModelList.get(position);
								playModel.setmPlayListName(folder_name);

								int pos = SMSharePref
										.getDataPosition(getActivity());
								mPlayListModel = new PlayListModelClass(
										folder_name, "0");
								long updated = mFolderDataSource
										.updateFolderData(pos, mPlayListModel);
								if (updated >= 0) {
									Log.d("FOLDER: ", "updated");
								} else {
									Log.e("FOLDER: ", "update fail");
								}

							} else {
								
								mPlayListModel = new PlayListModelClass(
										folder_name, "0");
								mPLModelList.add(mPlayListModel);

								long inserted = mFolderDataSource
										.addNewFolder(mPlayListModel);
								if (inserted >= 0) {

									Log.d("FOLDER: ", "inserted");

								} else {

									Log.e("FOLDER: ", "insertion fail");

								}
							}
						}

						mPlaylistAdapter.notifyDataSetChanged();

					}
				});

		// create an alert dialog
		AlertDialog alert = alertDialogBuilder.create();
		alert.show();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
		// TODO Auto-generated method stub

//		String name = mPLModelList.get(position).getmPlayListId();
//		getVideoCount
		 int pID = mPLModelList.get(pos).getmPlayListId();
		 
		   Intent intent = new Intent(getActivity(), PlayListItemActivity.class);      
		   intent.putExtra("PLid", pID);
           startActivity(intent);	
           SMSharePref.setBackCode(getActivity());
          getActivity().overridePendingTransition(R.anim.push_left_in, 0);
	}

}
