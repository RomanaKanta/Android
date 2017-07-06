package com.smartmux.filevaultfree.video;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.smartmux.filevaultfree.AppMainActivity;
import com.smartmux.filevaultfree.LoginWindowActivity;
import com.smartmux.filevaultfree.R;
import com.smartmux.filevaultfree.adapter.RecycleView_VideoAdapter;
import com.smartmux.filevaultfree.adapter.RecycleView_VideoAdapter.OnItemClickListener;
import com.smartmux.filevaultfree.modelclass.CommonItemRow;
import com.smartmux.filevaultfree.utils.AppActionBar;
import com.smartmux.filevaultfree.utils.AppExtra;
import com.smartmux.filevaultfree.utils.AppToast;
import com.smartmux.filevaultfree.utils.FileManager;
import com.smartmux.filevaultfree.utils.FileManagerListener;
import com.smartmux.filevaultfree.utils.GeneralUtils;
import com.smartmux.filevaultfree.utils.ProgressHUD;
import com.smartmux.filevaultfree.widget.FloatingActionButton;
import com.smartmux.filevaultfree.widget.FloatingActionsMenu;
import com.smartmux.filevaultfree.widget.FloatingActionsMenu.OnFloatingActionsMenuUpdateListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;

public class VideoListActivity extends AppMainActivity {

	private String folderName;
	private String subFolderName;

	private FileManager fileManager;

	private ArrayList<CommonItemRow> listItems = null;
	private RecycleView_VideoAdapter listAdapter;
	private RecyclerView listView;

	private static int LOAD_VIDEO_FROM_GALLERY = 1;
	private static int LOAD_VIDEO_THROUGH_CAMERA = 2;

	private FloatingActionsMenu actionMenu;

	private UpdateReceiver mReceiver = new UpdateReceiver();
	private LinearLayout transparentView;

	public class UpdateReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			Log.d("call", "file modify");
			if (intent != null) {

				if (intent.getAction().equals("file modify")) {
					Log.d("call", "setAdapter");
					setAdapter();
				}

			}
		}

	};

	@Override
	public void onPause() {
		super.onPause();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (mReceiver != null) {
			try {
				unregisterReceiver(mReceiver);
				mReceiver = null;
			} catch (Exception e) {
				// do nothing
			}
		}
	}

	private boolean isDeviceSupportCamera() {
		if (getApplicationContext().getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			// this device has a camera
			return true;
		} else {
			// no camera on this device
			return false;
		}
	}

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_listview);
		actionMenu = (FloatingActionsMenu) findViewById(R.id.multiple_actions);
		actionMenu.setVisibility(View.VISIBLE);
		transparentView = (LinearLayout) findViewById(R.id.transparentLayout);

		actionMenu
				.setOnFloatingActionsMenuUpdateListener(new OnFloatingActionsMenuUpdateListener() {

					@Override
					public void onMenuExpanded() {
						// TODO Auto-generated method stub
						listView.setEnabled(false);
						transparentView.setVisibility(View.VISIBLE);
						// transparentView.setBackgroundColor(Color.parseColor("#88676767"));
						// transparentView.bringToFront();
					}

					@Override
					public void onMenuCollapsed() {
						// TODO Auto-generated method stub
						listView.setEnabled(true);
						transparentView.setVisibility(View.GONE);

						// transparentView.setBackgroundColor(Color.parseColor(""));
					}
				});
		transparentView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				// listView.setEnabled(true);
				transparentView.setVisibility(View.GONE);
				actionMenu.collapse();

				return false;
			}
		});

		// Floating Button
		final FloatingActionButton actionB = (FloatingActionButton) findViewById(R.id.action_first);
		actionB.setSize(FloatingActionButton.SIZE_MINI);
		actionB.setIcon(R.drawable.videocapture);

		final FloatingActionButton actionA = (FloatingActionButton) findViewById(R.id.action_second);
		actionA.setSize(FloatingActionButton.SIZE_MINI);
		actionA.setIcon(R.drawable.videogallery);

		fileManager = new FileManager();
		fileManager.setBackCode(getApplicationContext());
		fileManager.removeGalleryCode(getApplicationContext());
		fileManager.setListener(new FileManagerListener() {
			public void videoSaved(CommonItemRow row) {
				listItems.add(row);
				listAdapter.notifyDataSetChanged();
			}

			@Override
			public void noteSaved(CommonItemRow row) {
			}

			@Override
			public void audioSaved(CommonItemRow row) {
			}

			@Override
			public void photoSaved(CommonItemRow row) {
			}

		});
		AppActionBar.changeActionBarFont(getApplicationContext(),
				VideoListActivity.this);
		AppActionBar.updateAppActionBar(getActionBar(), this, true);

		Bundle bundle = getIntent().getExtras();
		folderName = bundle.getString(AppExtra.FOLDER_NAME);
		subFolderName = bundle.getString(AppExtra.SUB_FOLDER_NAME);

		if ((folderName != null) && (subFolderName != null)) {
			getActionBar().setTitle(folderName + "/" + subFolderName);
		}

		listItems = fileManager.getAllVideos(getApplicationContext(),
				folderName);

		listView = (RecyclerView) findViewById(R.id.common_listview);
		listView.setHasFixedSize(true);
		listView.setLayoutManager(new LinearLayoutManager(
				this, LinearLayoutManager.VERTICAL, false));
		
		
		if (listItems != null) {
		listAdapter = new RecycleView_VideoAdapter(this,
				listItems, true);
		listView.setAdapter(listAdapter);
		}

		listAdapter.SetOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(View view, int position) {
				fileManager.setBackCode(getApplicationContext());
				String fileName = listItems.get(position).getTitle();
				File file = new File(AppExtra.APP_ROOT_FOLDER + "/"
						+ folderName + "/" + subFolderName + "/" + fileName);
				Intent intent = new Intent(VideoListActivity.this,
						VideoViewActivity.class);
				intent.putExtra("filePath", file.getAbsolutePath());
				startActivity(intent);
				overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);
			}
		});

		if (listItems.size() == 0) {
			AppToast.show(getApplicationContext(), "Please add Videos");
		}

		IntentFilter intent = new IntentFilter();
		intent.addAction("file modify");
		registerReceiver(mReceiver, intent);
	}

	public void setAdapter() {
		if (listItems.size() > 0) {

			listItems.clear();
		}
		listItems = fileManager.getAllVideos(getApplicationContext(),
				folderName);

		if (listItems != null) {
			listAdapter = new RecycleView_VideoAdapter(this, 
					listItems, true);
			listView.setAdapter(listAdapter);
		}
		listAdapter.notifyDataSetChanged();
	}

	public void ClickEvent(View view) {

		switch (view.getId()) {
		case R.id.action_first: {
			if (isDeviceSupportCamera()) {
				fileManager.setBackCode(getApplicationContext());
				Intent i = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
				startActivityForResult(i, LOAD_VIDEO_THROUGH_CAMERA);

			}
			break;

		}
		case R.id.action_second: {
			fileManager.setBackCode(getApplicationContext());
			Intent i = new Intent(Intent.ACTION_PICK, null);
			i.setType("video/*");
			startActivityForResult(i, LOAD_VIDEO_FROM_GALLERY);

			break;

		}
		default:
			break;
		}

	}

	@Override
	protected void onActivityResult(final int requestCode,
			final int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == LOAD_VIDEO_FROM_GALLERY && resultCode == RESULT_OK
				&& null != data) {

			AppToast.show(getApplicationContext(), "Video Picking Started");
			AssetFileDescriptor videoAsset;
			try {
				videoAsset = getContentResolver().openAssetFileDescriptor(
						data.getData(), "r");
				FileInputStream fis = videoAsset.createInputStream();
				// fileManager.saveVideo(getApplicationContext(), folderName,
				// fis);
				// fileManager.setBackCode(getApplicationContext());
				new SaveTask(folderName, fis).execute();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else if (requestCode == LOAD_VIDEO_THROUGH_CAMERA
				&& resultCode == RESULT_OK) {
			AssetFileDescriptor videoAsset;
			try {
				videoAsset = getContentResolver().openAssetFileDescriptor(
						data.getData(), "r");
				FileInputStream fis = videoAsset.createInputStream();
				// fileManager.saveVideo(getApplicationContext(), folderName,
				// fis);
				// fileManager.setBackCode(getApplicationContext());
				new SaveTask(folderName, fis).execute();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	protected void onUserLeaveHint() {
		fileManager.setHomeCode(getApplicationContext());
		super.onUserLeaveHint();
	}

	@Override
	protected void onResume() {
		super.onResume();
		actionMenu.collapse();

		int event_code = fileManager.getReturnCode(getApplicationContext());
		int gallery_code = fileManager.getGalleryCode(getApplicationContext());
		boolean isBackground = GeneralUtils
				.isApplicationBroughtToBackground(getApplicationContext());
		// AppToast.show(getApplicationContext(), ""+isBackground);
		if (event_code == AppExtra.HOME_CODE
				&& gallery_code != AppExtra.BACK_CODE && !isBackground) {
			actionMenu.collapse();
			Intent i = new Intent(VideoListActivity.this,
					LoginWindowActivity.class);
			startActivity(i);
		}
		fileManager.removeGalleryCode(getApplicationContext());
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			fileManager.setBackCode(getApplicationContext());
			finish();
			overridePendingTransition(R.anim.push_right_out,
					R.anim.push_right_in);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public static final class AnActionModeOfEpicProportions implements
			ActionMode.Callback {

		Context ctx;
		private TextView headerTitle;

		public AnActionModeOfEpicProportions(Context ctx) {
			this.ctx = ctx;
		}

		@SuppressLint("NewApi")
		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {

			menu.add("Delete").setIcon(R.drawable.delete);
			menu.add("Rename").setIcon(R.drawable.rename);
			menu.add("Share").setIcon(R.drawable.share_icon);

			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

			Toast toast = null;

			ArrayList<CommonItemRow> selectedListItems = new ArrayList<CommonItemRow>();

			StringBuilder selectedItems = new StringBuilder();

			// get items selected
			List<Integer> selectedIndex = new ArrayList<Integer>();
			final ArrayList<String> selectedFileName = new ArrayList<String>();
			ArrayList<String> selectedSubFolderName = new ArrayList<String>();
			String SelectedForRename = null;
			int indexnum = 0;
			for (CommonItemRow i : ((VideoListActivity) ctx).listAdapter.data) {
				if (i.isChecked()) {
					selectedListItems.add(i);
					selectedItems.append(i.getTitle()).append(", ");
					SelectedForRename = i.getTitle();
					selectedIndex.add(indexnum);
					selectedFileName.add(i.getTitle());

				}
				indexnum++;
			}

			if (item.getTitle().equals("Delete")) {

				// Delete
				// get prompts.xml view
				LayoutInflater layoutInflater = LayoutInflater.from((VideoListActivity) ctx);
				View promptView = layoutInflater.inflate(R.layout.dialog_screen_delete, null);
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder((VideoListActivity) ctx);
				alertDialogBuilder.setView(promptView);
				
//				headerTitle = (TextView) promptView.findViewById(R.id.dialog_delete_header);
//				headerTitle.setText("Delete");
				
				TextView alertText = (TextView) promptView
						.findViewById(R.id.alert_text);
				alertText.setText(R.string.delete_files);
				
				// setup a dialog window
				alertDialogBuilder.setCancelable(true);

				
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
								for (int i = 0; i < selectedFileName
										.size(); i++) {
									((VideoListActivity) ctx)
											.removeFile(selectedFileName
													.get(i));

								}
								((VideoListActivity) ctx)
										.runDeleteTask();
								dialog.dismiss();
							}
						});

				// create an alert dialog
			alertDialogBuilder.create().show();

			

				// for(int i=0;i<selectedFileName.size();i++){
				// ((VideoListActivity)ctx).removeFile(selectedFileName.get(i));
				//
				// }
				// ((VideoListActivity)ctx).runDeleteTask();

//				LayoutInflater layoutInflater = null;
//				View contentView = ((VideoListActivity) ctx)
//						.getLayoutInflater().inflate(R.layout.alert_content,
//								null);
//				Holder holder = new ViewHolder(contentView);
//				TextView alertText = (TextView) contentView
//						.findViewById(R.id.alert_text);
//				alertText.setText(R.string.delete_text);
//				DialogPlus dialogPlus = DialogPlus
//						.newDialog(ctx)
//						.setContentHolder(holder)
//						.setGravity(Gravity.CENTER)
//						.setHeader(R.layout.header_alert)
//
//						.setOnClickListener(
//								new com.orhanobut.dialogplus.OnClickListener() {
//
//									@Override
//									public void onClick(DialogPlus dialog,
//											View view) {
//
//										// TODO Auto-generated method stub
//										switch (view.getId()) {
//
//										case R.id.yes_button:
//
//											for (int i = 0; i < selectedFileName
//													.size(); i++) {
//												((VideoListActivity) ctx)
//														.removeFile(selectedFileName
//																.get(i));
//
//											}
//											((VideoListActivity) ctx)
//													.runDeleteTask();
//											dialog.dismiss();
//
//										case R.id.no_button:
//
//											dialog.dismiss();
//
//										}
//
//									}
//								})
//						.setOnDismissListener(new OnDismissListener() {
//							@Override
//							public void onDismiss(DialogPlus dialog) {
//							}
//						}).setOnBackPressListener(new OnBackPressListener() {
//							@Override
//							public void onBackPressed(DialogPlus dialog) {
//
//							}
//						}).setCancelable(true).create();
//				View headerView = dialogPlus.getHeaderView();
//				headerTitle = (TextView) headerView
//						.findViewById(R.id.header_title);
//				headerTitle.setText("Delete");
//				dialogPlus.show();

			} else if (item.getTitle().equals("Rename")) {
				// Archive
				if (selectedListItems.size() > 1) {
					toast = Toast.makeText(ctx,
							"Can not rename multiple folder at a time",
							Toast.LENGTH_LONG);
				} else {

					((VideoListActivity) ctx)
							.showRenameFileDialog(SelectedForRename);
					// toast = Toast.makeText(ctx, "Archive: " +
					// selectedItems.toString(), Toast.LENGTH_SHORT);
				}

			} else {
				if (selectedListItems.size() > 1) {
					toast = Toast.makeText(ctx,
							"Can not share multiple file at a time",
							Toast.LENGTH_LONG);
				} else {
					((VideoListActivity) ctx).shareFile(SelectedForRename);
				}
			}
			if (toast != null) {
				toast.show();
			}
			mode.finish();
			return true;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			// Action mode is finished reset the list and 'checked count' also
			// set all the list items checked states to false
			((VideoListActivity) ctx).listAdapter.checkedCount = 0;
			((VideoListActivity) ctx).listAdapter.isActionModeShowing = false;
			// set list items states to false
			for (CommonItemRow item : ((VideoListActivity) ctx).listAdapter.data) {
				item.setIsChecked(false);
			}
			((VideoListActivity) ctx).listAdapter.notifyDataSetChanged();
			// Toast.makeText(ctx, "Action mode closed",
			// Toast.LENGTH_SHORT).show();
		}
	}

	public void showRenameFileDialog(String selectedFileName) {
		//AppToast.show(getApplicationContext(), "" + selectedFileName);

		View contentView = getLayoutInflater().inflate(R.layout.dialog_screen_rename, null);
		fileManager.renameAnyFile(VideoListActivity.this, folderName,
				subFolderName, selectedFileName, contentView);
		listAdapter.notifyDataSetChanged();
	}

	public void removeFile(String fileName) {

		fileManager.deleteAnyFile(getApplicationContext(), folderName,
				subFolderName, fileName);
	}

	public void shareFile(String fileName) {

		String fileFullPath = AppExtra.APP_ROOT_FOLDER + "/" + folderName + "/"
				+ subFolderName + "/" + fileName;

		Intent sendIntent = new Intent(Intent.ACTION_SEND);
		sendIntent.setType("video/3gp");
		sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Video");
		sendIntent.putExtra(Intent.EXTRA_STREAM,
				Uri.parse("file://" + fileFullPath));
		sendIntent.putExtra(Intent.EXTRA_TEXT, "Enjoy the Video");
		startActivity(Intent.createChooser(sendIntent, "Email:"));

	}

	public void runDeleteTask() {
		DeleteTask del = new DeleteTask();
		del.execute();

	}

	public class DeleteTask extends AsyncTask<Void, Void, Void> {
		// ProgressDialog dialog;
		ProgressHUD mProgressHUD ;
		
		

		public DeleteTask() {
			super();
			
			mProgressHUD = new ProgressHUD(VideoListActivity.this);
		}

		@Override
		protected void onPreExecute() {
			mProgressHUD.show(true);
		}

		@Override
		protected Void doInBackground(Void... params) {

			// TODO Auto-generated method stub
			listItems.clear();
			listItems = fileManager.getAllVideos(getApplicationContext(),
					folderName);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			
			// listAdapter.notifyDataSetChanged();
			if (mProgressHUD.isShowing()) {

				mProgressHUD.dismiss();
			}
			
			setAdapter();
		}

	}

	public class SaveTask extends AsyncTask<Void, Void, Boolean> {
		// ProgressDialog dialog;
		ProgressHUD mProgressHUD ;
		String fName;
		FileInputStream fileinputstream;

		public SaveTask(String folderName, FileInputStream fileinputstream) {
			super();
			this.fName = folderName;
			this.fileinputstream = fileinputstream;
			mProgressHUD = new ProgressHUD(VideoListActivity.this);
		}

		@Override
		protected void onPreExecute() {
			mProgressHUD.show(true);
		}

		@Override
		protected Boolean doInBackground(Void... params) {

			
			
			return fileManager.saveVideo(getApplicationContext(), fName,
					fileinputstream);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			
			// listAdapter.notifyDataSetChanged();
			if (mProgressHUD.isShowing()) {

				mProgressHUD.dismiss();
			}
			
			if(!result){
				
				AppToast.show(getApplicationContext(), "Not Exist :" + folderName);
			}
			
			setAdapter();
			
			fileManager.setBackCode(getApplicationContext());
		}

	}

}
