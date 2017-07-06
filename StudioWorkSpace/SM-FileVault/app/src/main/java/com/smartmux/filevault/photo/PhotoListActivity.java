package com.smartmux.filevault.photo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import com.smartmux.filevault.AppMainActivity;
import com.smartmux.filevault.LoginWindowActivity;
import com.smartmux.filevault.R;
import com.smartmux.filevault.adapter.RecycleView_PhotoAdapter;
import com.smartmux.filevault.modelclass.CommonItemRow;
import com.smartmux.filevault.utils.AppActionBar;
import com.smartmux.filevault.utils.AppExtra;
import com.smartmux.filevault.utils.AppToast;
import com.smartmux.filevault.utils.FileManager;
import com.smartmux.filevault.utils.FileManagerListener;
import com.smartmux.filevault.utils.GeneralUtils;
import com.smartmux.filevault.utils.ProgressHUD;
import com.smartmux.filevault.widget.FloatingActionButton;
import com.smartmux.filevault.widget.FloatingActionsMenu;
import com.smartmux.filevault.widget.FloatingActionsMenu.OnFloatingActionsMenuUpdateListener;

import java.io.IOException;
import java.util.ArrayList;

public class PhotoListActivity extends AppMainActivity {

	private String folderName;
	private String subFolderName;

	private FileManager fileManager;


	protected static final String TAG = null;

	private ArrayList<CommonItemRow> listItems = null;
	private RecycleView_PhotoAdapter listAdapter;

	private static int LOAD_PHOTO_FROM_GALLERY = 1;
	private static int LOAD_PHOTO_THROUGH_CAMERA = 2;

//	private UpdateReceiver mReceiver = new UpdateReceiver();
	private RecyclerView listView;
	private FloatingActionsMenu actionMenu;
	private LinearLayout transparentView;
    
//	public class UpdateReceiver extends BroadcastReceiver {
//
//		@Override
//		public void onReceive(Context context, Intent intent) {
//
//			Log.d("call", "file modify");
//			if (intent != null) {
//
//				if (intent.getAction().equals("file modify")) {
//					Log.d("call", "setAdapter");
//					setAdapter();
//				}
//
//			}
//		}
//
//	};

	@Override
	public void onPause() {
		super.onPause();
//		if (mReceiver != null) {
//			try {
//				unregisterReceiver(mReceiver);
//			} catch (Exception e) {
//				// do nothing
//			}
//		}

	}

	private boolean isDeviceSupportCamera() {
		if (getApplicationContext().getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA_ANY)) {
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
		setContentView(R.layout.activity_photo_list);
		actionMenu = (FloatingActionsMenu) findViewById(R.id.multiple_actions);
		transparentView = (LinearLayout) findViewById(R.id.transparentLayout);
		// #88676767
		actionMenu
				.setOnFloatingActionsMenuUpdateListener(new OnFloatingActionsMenuUpdateListener() {

					@Override
					public void onMenuExpanded() {
						listView.setEnabled(false);
						transparentView.setVisibility(View.VISIBLE);
						 if(listAdapter.isActionModeShowing){
				    	        listAdapter.mMode.finish();
				    	        }
					}

					@Override
					public void onMenuCollapsed() {
						listView.setEnabled(true);
						transparentView.setVisibility(View.GONE);

					}
				});
		transparentView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				transparentView.setVisibility(View.GONE);
				actionMenu.collapse();

				return false;
			}
		});
		fileManager = new FileManager();
		fileManager.setBackCode(getApplicationContext());
		fileManager.removeGalleryCode(getApplicationContext());
		fileManager.setListener(new FileManagerListener() {
			public void photoSaved(CommonItemRow row) {
				listItems.add(row);
				listAdapter.notifyDataSetChanged();
			}

			@Override
			public void noteSaved(CommonItemRow row) {
			}

			@Override
			public void videoSaved(CommonItemRow row) {
			}

			@Override
			public void audioSaved(CommonItemRow row) {
			}

		});
		// Floating Button
		final FloatingActionButton actionB = (FloatingActionButton) findViewById(R.id.action_pick_photo);
		actionB.setSize(FloatingActionButton.SIZE_MINI);
		actionB.setIcon(R.drawable.gal);


		final FloatingActionButton actionA = (FloatingActionButton) findViewById(R.id.action_take_photo);
		actionA.setSize(FloatingActionButton.SIZE_MINI);
		actionA.setIcon(R.drawable.cam);

		AppActionBar.changeActionBarFont(getApplicationContext(),
				PhotoListActivity.this);
		AppActionBar.updateAppActionBar(getActionBar(), this, true);

		Bundle bundle = getIntent().getExtras();

		folderName = bundle.getString(AppExtra.FOLDER_NAME);
		subFolderName = bundle.getString(AppExtra.SUB_FOLDER_NAME);
		if ((folderName != null) && (subFolderName != null)) {
			getActionBar().setTitle(folderName + "/" + subFolderName);
		}

		listItems = fileManager.getAllPhotos(getApplicationContext(),
				folderName);

		listView = (RecyclerView) findViewById(R.id.listviewPhotoList);
        listView.setHasFixedSize(true);
        listView.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.VERTICAL, false));

		listAdapter = new RecycleView_PhotoAdapter(this, listItems,
				false);
		listView.setAdapter(listAdapter);



		if (listItems.size() == 0) {
			AppToast.show(getApplicationContext(), "Please add photos");
			return;
		}
	}


	public void setAdapter() {
		if (listItems.size() > 0) {

			listItems.clear();
		}
		listItems = fileManager.getAllPhotos(getApplicationContext(),
				folderName);
		if (listItems != null) {
			listAdapter = new RecycleView_PhotoAdapter(this, listItems,
					false);
			listView.setAdapter(listAdapter);

        listAdapter.SetOnItemClickListener(new RecycleView_PhotoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {


                String fileName = listItems.get(position).getTitle();
                String fileFullPath = AppExtra.APP_ROOT_FOLDER + "/"
                        + folderName + "/" + subFolderName + "/" + fileName;
                Intent intent = new Intent(PhotoListActivity.this,
                        SMImageViewerActivity.class);
                intent.putExtra("PHOTO_ID", position);
                intent.putExtra("folder", folderName);
                intent.putExtra("filepath", fileFullPath);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in,
                        R.anim.push_left_out);
            }
        });
        }
		listAdapter.notifyDataSetChanged();
	}

	public void ClickEvent(View view) {


				switch (view.getId()) {
				case R.id.action_pick_photo: {

					if (GeneralUtils.checkSdcard()) {
						Intent i = new Intent("ACTION_MULTIPLE_IMAGE_PICK");
						startActivityForResult(i, 210);
						overridePendingTransition(R.anim.push_left_in,
								R.anim.push_left_out);
					} else {
						AppToast.show(getApplicationContext(),
								"No SD card found");
					}
					break;
				}
				case R.id.action_take_photo: {
					if (isDeviceSupportCamera() && GeneralUtils.checkSdcard()) {
						// fileManager.setBackCode(getApplicationContext());
						Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
						startActivityForResult(i, LOAD_PHOTO_THROUGH_CAMERA);
						overridePendingTransition(R.anim.push_left_in,
								R.anim.push_left_out);

					} else {
						AppToast.show(getApplicationContext(),
								"camera not found");
					}
					break;
				}
				default:
					break;

		}

	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == LOAD_PHOTO_FROM_GALLERY && resultCode == RESULT_OK
				&& null != data) {

			Uri selectedImage = data.getData();
			String[] filePathColumn = { Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();
			fileManager.savePhoto(getApplicationContext(), folderName,
					BitmapFactory.decodeFile(picturePath));
			// fileManager.setBackCode(getApplicationContext());
		} else if (requestCode == LOAD_PHOTO_THROUGH_CAMERA
				&& resultCode == RESULT_OK) {
			Bundle extras = data.getExtras();
			fileManager.savePhoto(getApplicationContext(), folderName,
					(Bitmap) extras.get("data"));
			// fileManager.setBackCode(getApplicationContext());
		} else if (requestCode == 210 && resultCode == Activity.RESULT_OK) {
			String[] all_path = data.getStringArrayExtra("all_path");

				MultipleImageSaveTask task = new MultipleImageSaveTask(
						PhotoListActivity.this, all_path, folderName);

				task.execute();


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
//		IntentFilter intent = new IntentFilter();
//		intent.addAction("file modify");
//		registerReceiver(mReceiver, intent);
		actionMenu.collapse();
		int event_code = fileManager.getReturnCode(getApplicationContext());
		int gallery_code = fileManager.getGalleryCode(getApplicationContext());
		boolean isBackground=GeneralUtils.isApplicationBroughtToBackground(getApplicationContext());
	//	AppToast.show(getApplicationContext(), ""+isBackground);
	
		if (event_code == AppExtra.HOME_CODE
				&& gallery_code != AppExtra.BACK_CODE && !isBackground) {
			Intent i = new Intent(PhotoListActivity.this,
					LoginWindowActivity.class);
			startActivity(i);
		}
		fileManager.removeGalleryCode(getApplicationContext());

        setAdapter();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			fileManager.setBackCode(getApplicationContext());
			finish();
			overridePendingTransition(R.anim.push_right_out,
					R.anim.push_right_in);
			;
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private class MultipleImageSaveTask extends AsyncTask<Void, String, String> {

		Context ctx;
		ProgressHUD mProgressHUD;
		String[] all_path;
		private FileManager fileManager;
		private String folderName;

		public MultipleImageSaveTask(Context ctx, String[] all_path,
				String folderName) {
			super();
			this.ctx = ctx;
			this.mProgressHUD = new ProgressHUD(ctx);
			this.all_path = all_path;
			this.fileManager = new FileManager();
			this.folderName = folderName;
		}

		@Override
		protected void onPreExecute() {
			mProgressHUD.show(true);
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... params) {
			try {
				for (String path : all_path) {

//					Log.d("path", path);
					fileManager.copyPhoto(ctx.getApplicationContext(),
							folderName, path);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (mProgressHUD.isShowing()) {

				mProgressHUD.dismiss();
			}
            setAdapter();


		}

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
			final ArrayList<String> selectedFileName = new ArrayList<String>();
			String SelectedForRename = null;
			int indexnum = 0;
			for (CommonItemRow i : ((PhotoListActivity) ctx).listAdapter.data) {
				if (i.isChecked()) {
					selectedListItems.add(i);
					selectedItems.append(i.getTitle()).append(", ");
					SelectedForRename = i.getTitle();
					selectedFileName.add(i.getTitle());

				}
				indexnum++;
			}

			if (item.getTitle().equals("Delete")) {
				// Delete
// get prompts.xml view
                LayoutInflater layoutInflater = LayoutInflater.from((PhotoListActivity) ctx);
                View promptView = layoutInflater.inflate(R.layout.dialog_screen_delete, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder((PhotoListActivity) ctx);
                alertDialogBuilder.setView(promptView);

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
                                for (int i = 0; i < selectedFileName.size(); i++) {
                                    ((PhotoListActivity) ctx).removeFile(selectedFileName
                                            .get(i));


                                }

                                ((PhotoListActivity) ctx).runDeleteTask();
                                dialog.dismiss();
                            }
                        });

                // create an alert dialog
                alertDialogBuilder.create().show();
				
			} else if (item.getTitle().equals("Rename")) {
				// Archive
				if (selectedListItems.size() > 1) {
					toast = Toast.makeText(ctx,
							"Can not rename multiple files at a time",
							Toast.LENGTH_SHORT);
				} else {

					//AppToast.show(ctx, SelectedForRename);
					((PhotoListActivity) ctx)
							.showRenameFileDialog(SelectedForRename);
				}

			} else {

				 if (selectedListItems.size() > 1) {
				 toast = Toast.makeText(ctx,
				 "Can not share multiple images at a time",
				 Toast.LENGTH_SHORT);
				 } else {
				 ((PhotoListActivity) ctx).shareFile(SelectedForRename);
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
			((PhotoListActivity) ctx).listAdapter.checkedCount = 0;
			((PhotoListActivity) ctx).listAdapter.isActionModeShowing = false;
			// set list items states to false
			for (CommonItemRow item : ((PhotoListActivity) ctx).listAdapter.data) {
				item.setIsChecked(false);
			}
			((PhotoListActivity) ctx).listAdapter.notifyDataSetChanged();

		}
	}

	public void removeFile(String fileName) {

		fileManager.deleteAnyFile(getApplicationContext(), folderName,
				subFolderName, fileName);
	}

	public void shareFile(String fileName) {

//		String fileFullPath = AppExtra.APP_ROOT_FOLDER + "/" + folderName + "/"
//				+ subFolderName + "/" + fileName;
//		Bitmap bitmap = BitmapFactory.decodeFile(fileFullPath);
//
//        String pathofBmp = Images.Media.insertImage(getApplicationContext()
//                .getContentResolver(), bitmap, "title", null);
//        Uri bmpUri = Uri.parse(pathofBmp);
//        if(bmpUri!=null) {
//
//            Intent emailIntent = new Intent(Intent.ACTION_SEND);
//            emailIntent.setType("image/jpeg");
//            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Image");
//            emailIntent.putExtra(Intent.EXTRA_TEXT,
//                    "Enjoy this image");
//            emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            emailIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
//            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
//        }else{
//            AppToast.show(getApplicationContext(), "Some Error Occur in Sharing. Please try again.");
//        }

        try {

            String fileFullPath = AppExtra.APP_ROOT_FOLDER + "/" + folderName + "/"
                    + subFolderName + "/" + fileName;

            String pathofBmp =  MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), fileFullPath, "title", null);

            Uri bmpUri = Uri.parse(pathofBmp);

            if(bmpUri!=null) {

                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("image/jpeg");
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Image");
                emailIntent.putExtra(Intent.EXTRA_TEXT,
                        "Enjoy this image");
                emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                emailIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            }else{
                AppToast.show(getApplicationContext(), "Some Error Occur in Sharing. Please try again.");
            }
        }catch (Exception e){
            AppToast.show(getApplicationContext(), "Some Error Occur in Sharing. Please try again.");
        }



	}

	public void runDeleteTask() {
		DeleteTask del = new DeleteTask();
		del.execute();

	}

	public void showRenameFileDialog(String selectedFileName) {

		fileManager.renameAnyFile(PhotoListActivity.this, folderName,
				subFolderName, selectedFileName);
		listAdapter.notifyDataSetChanged();

	}

	public class DeleteTask extends AsyncTask<Void, Void, Void> {
		// ProgressDialog dialog;
		ProgressHUD mProgressHUD = new ProgressHUD(PhotoListActivity.this);

		@Override
		protected void onPreExecute() {

			mProgressHUD.show(true);

		}

		@Override
		protected Void doInBackground(Void... params) {

			listItems.clear();
			listItems = fileManager.getAllPhotos(getApplicationContext(),
					folderName);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			setAdapter();
			if (mProgressHUD.isShowing()) {

				mProgressHUD.dismiss();
			}
		}

	}
	
	

}
