package com.smartmux.filevaultfree.photo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
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

import com.android.vending.billing.IInAppBillingService;
import com.smartmux.filevaultfree.AppMainActivity;
import com.smartmux.filevaultfree.LoginWindowActivity;
import com.smartmux.filevaultfree.R;
import com.smartmux.filevaultfree.adapter.RecycleView_PhotoAdapter;
import com.smartmux.filevaultfree.adapter.RecycleView_PhotoAdapter.OnItemClickListener;
import com.smartmux.filevaultfree.modelclass.CommonItemRow;
import com.smartmux.filevaultfree.util.IabHelper;
import com.smartmux.filevaultfree.util.IabResult;
import com.smartmux.filevaultfree.util.Inventory;
import com.smartmux.filevaultfree.util.Purchase;
import com.smartmux.filevaultfree.utils.AlbumStorageDirFactory;
import com.smartmux.filevaultfree.utils.AppActionBar;
import com.smartmux.filevaultfree.utils.AppExtra;
import com.smartmux.filevaultfree.utils.AppToast;
import com.smartmux.filevaultfree.utils.BaseAlbumDirFactory;
import com.smartmux.filevaultfree.utils.FileManager;
import com.smartmux.filevaultfree.utils.FileManagerListener;
import com.smartmux.filevaultfree.utils.FroyoAlbumDirFactory;
import com.smartmux.filevaultfree.utils.GeneralUtils;
import com.smartmux.filevaultfree.utils.ProgressHUD;
import com.smartmux.filevaultfree.widget.FloatingActionButton;
import com.smartmux.filevaultfree.widget.FloatingActionsMenu;
import com.smartmux.filevaultfree.widget.FloatingActionsMenu.OnFloatingActionsMenuUpdateListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PhotoListActivity extends AppMainActivity {

	private String folderName;
	private String subFolderName;

	private FileManager fileManager;

	IabHelper mHelper;
	IInAppBillingService mService;

	protected static final String TAG = null;

	private ArrayList<CommonItemRow> listItems = null;
//	private PhotoAdapter listAdapter;
	
	private RecycleView_PhotoAdapter listAdapter;
	

	private static int LOAD_PHOTO_FROM_GALLERY = 1;
	private static int LOAD_PHOTO_THROUGH_CAMERA = 2;

	private UpdateReceiver mReceiver = new UpdateReceiver();
//	private ListView listView;
	private static RecyclerView listView;
	private FloatingActionsMenu actionMenu;
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
		if (mReceiver != null) {
			try {
				unregisterReceiver(mReceiver);
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
		setContentView(R.layout.photo_list_activity);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }

		actionMenu = (FloatingActionsMenu) findViewById(R.id.multiple_actions);
		transparentView = (LinearLayout) findViewById(R.id.transparentLayout);
		// #88676767
		actionMenu
				.setOnFloatingActionsMenuUpdateListener(new OnFloatingActionsMenuUpdateListener() {

					@Override
					public void onMenuExpanded() {
						// TODO Auto-generated method stub
						listView.setEnabled(false);
						transparentView.setVisibility(View.VISIBLE);
						// actionMenu.setExpanded(false);
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
	

		String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjLiWilouJZ0ZWBXOxGzHJrRbrKQYSlXbWwSDEE8qWGZnkNfYtfUjD1tTrO8Unt5UXe3kqq9Y5R2BDCjixTfsPFhYmx752L+aAg0Od3X7KGlx8xSYFKY7JjVz9J10U19R+LE9OtEeTa54bVei5NgJKur7NqHKbDMx4A4g5Pbq/GncwyQaBGTbbq1sS7NgjruQxV7kVee98DKc5n5bH3IYCL03TdbPItAuwM27VXMQxWMqpqpsRFQwgTJRHunwieBYFzo75DKsX/UrX9gv+5U/QwBC8mBdCxYrN7WVyhJAtyh0Y0TMuH4z+Z9kiexnx48oLdYQkAbpcbmfrtgoa67uiQIDAQAB";

		mHelper = new IabHelper(this, base64EncodedPublicKey);
		if (mHelper != null) {
			mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
				public void onIabSetupFinished(IabResult result) {
					if (!result.isSuccess()) {
						Log.d(TAG, "In-app Billing setup failed: " + result);
					} else {
						Log.d(TAG, "In-app Billing is set up OK");
					}
				}
			});
		}
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

//		listView = (ListView) findViewById(R.id.listviewPhotoList);
		
		listView = (RecyclerView) 
				findViewById(R.id.listviewPhotoList);
		listView.setHasFixedSize(true);
		listView.setLayoutManager(new LinearLayoutManager(
				this, LinearLayoutManager.VERTICAL, false));
		
		listAdapter = new RecycleView_PhotoAdapter(this, listItems,
				false);
		listView.setAdapter(listAdapter);// set adapter on recyclerview
		listAdapter.notifyDataSetChanged();
		
		
//		listAdapter = new PhotoAdapter(this, R.layout.photo_row, listItems,
//				false);
//		listView.setAdapter(listAdapter);

		listAdapter.SetOnItemClickListener(new OnItemClickListener() {

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
		

		if (listItems.size() == 0) {
			AppToast.show(getApplicationContext(), "Please add photos");
			return;
		}
	}

	public void setAdapter() {
		if (listItems != null && listItems.size() > 0) {

			listItems.clear();
		}
		listItems = fileManager.getAllPhotos(getApplicationContext(),
				folderName);
		if (listItems != null) {
			
			listAdapter = new RecycleView_PhotoAdapter(PhotoListActivity.this, listItems,
					false);
			listView.setAdapter(listAdapter);// set adapter on recyclerview
			
//			listAdapter = new PhotoAdapter(this, R.layout.photo_row, listItems,
//					false);
//			listView.setAdapter(listAdapter);
		}
		listAdapter.notifyDataSetChanged();
	}

	public void ClickEvent(View view) {

		boolean isPurchased = fileManager
				.getPaidStatus(getApplicationContext());
		if (!isPurchased) {
			int totalPhotos = listItems.size();
			if (totalPhotos >= AppExtra.FILE_LIMIT) {
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(
						PhotoListActivity.this);
				alertDialog.setTitle("Purchase Required!");
				alertDialog.setMessage(R.string.purchase_dialog);
				alertDialog.setPositiveButton("YES",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								mHelper.queryInventoryAsync(mGotInventoryListener);
								dialog.cancel();
							}
						});

				alertDialog.setNegativeButton("NO",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
								actionMenu.collapse();
							}
						});
				alertDialog.show();
			}

			else {

				switch (view.getId()) {
				case R.id.action_pick_photo: {
					// fileManager.setBackCode(getApplicationContext());
					// Intent i = new
					// Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					// startActivityForResult(i, LOAD_PHOTO_FROM_GALLERY);
					if (GeneralUtils.checkSdcard()) {
						Intent i = new Intent("ACTION_MULTIPLE_PICK");
						startActivityForResult(i, 200);
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

		} else {
			switch (view.getId()) {
			case R.id.action_pick_photo: {
				// fileManager.setBackCode(getApplicationContext());
				// Intent i = new
				// Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				// startActivityForResult(i, LOAD_PHOTO_FROM_GALLERY);
				if (GeneralUtils.checkSdcard()) {
					Intent i = new Intent("ACTION_MULTIPLE_PICK");
					startActivityForResult(i, 200);
					overridePendingTransition(R.anim.push_left_in,
							R.anim.push_left_out);
				} else {
					AppToast.show(getApplicationContext(), "No SD card found");
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
//                    if (GeneralUtils.checkSdcard()) {
//                    importCamera();

				} else {
					AppToast.show(getApplicationContext(), "camera not found");
				}
				break;
			}
			default:
				break;
			}

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
		} else if (requestCode == 200 && resultCode == Activity.RESULT_OK) {
			String[] all_path = data.getStringArrayExtra("all_path");

			boolean isPurchased = fileManager
					.getPaidStatus(getApplicationContext());
			if (!isPurchased) {
				int totalPhotos = listItems.size() + all_path.length;
				if (totalPhotos > AppExtra.FILE_LIMIT) {
					AlertDialog.Builder alertDialog = new AlertDialog.Builder(
							PhotoListActivity.this);
					alertDialog.setTitle("Purchase Required!");
					alertDialog.setMessage(R.string.purchase_dialog);
					alertDialog.setPositiveButton("YES",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									mHelper.queryInventoryAsync(mGotInventoryListener);
									dialog.cancel();
								}
							});

					alertDialog.setNegativeButton("NO",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();
								}
							});
					alertDialog.show();
					//AppToast.show(getApplicationContext(), "size big");
				} else {

					MultipleImageSaveTask task = new MultipleImageSaveTask(
							PhotoListActivity.this, all_path, folderName);

					task.execute();

				}

			} else {

				MultipleImageSaveTask task = new MultipleImageSaveTask(
						PhotoListActivity.this, all_path, folderName);

				task.execute();

			}

		}

//        else if (requestCode == CAMERA_REQUEST) {
//            if (resultCode == Activity.RESULT_OK) {
//                if (mCurrentPhotoPath != null) {
//                    fileManager.savePhoto(getApplicationContext(), folderName,
//                            (Bitmap) extras.get("data"));
//                }
//            }
//        }

		// fileManager.setBackCode(getApplicationContext());
		// viewSwitcher.setDisplayedChild(0);
		// adapter.addAll(dataT);

	}

	@Override
	protected void onUserLeaveHint() {
		fileManager.setHomeCode(getApplicationContext());
		super.onUserLeaveHint();
	}

	IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {

		@Override
		public void onQueryInventoryFinished(IabResult result,
				Inventory inventory) {
			if (result.isFailure()) {
				AppToast.show(getApplicationContext(),
						"Purchasing info could not be retrieved");
				return;
			} else {
				if (inventory != null
						&& inventory.hasPurchase(AppExtra.ITEM_SKU)) {
					AppToast.show(
							getApplicationContext(),
							"You have already bought this privilege! Enjoy creating unlimited files and folders");
					postPurchaseOperation();
				} else {
					startPurchase();
				}
			}
		}
	};

	IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
		public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
			if (result.isFailure()) {
				AppToast.show(getApplicationContext(),
						"Purchasing is failed at this time!");
				return;
			} else if (purchase.getSku().equals(AppExtra.ITEM_SKU)) {
				postPurchaseOperation();

			}

		}
	};

	private void startPurchase() {
		mHelper.launchPurchaseFlow(this, AppExtra.ITEM_SKU, 10005,
				mPurchaseFinishedListener, "");

	}

	private void postPurchaseOperation() {
		fileManager.setPaidStatus(getApplicationContext());
	}

	@Override
	protected void onResume() {
		super.onResume();
		IntentFilter intent = new IntentFilter();
		intent.addAction("file modify");
		registerReceiver(mReceiver, intent);
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
			// BlurBehind.getInstance().execute(PhotoListActivity.this,
			// new OnBlurCompleteListener() {
			// @Override
			// public void onBlurComplete() {
			// Intent intent = new Intent(PhotoListActivity.this,
			// LoginWindowActivity.class);
			// intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			//
			// startActivity(intent);
			//
			// }
			// });
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

					Log.d("path", path);
					fileManager.copyPhoto(ctx.getApplicationContext(),
							folderName, path);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
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

			listItems = fileManager.getAllPhotos(getApplicationContext(),
					folderName);
			
			listAdapter = new RecycleView_PhotoAdapter(PhotoListActivity.this, listItems,
					false);
			listView.setAdapter(listAdapter);// set adapter on recyclerview
			listAdapter.notifyDataSetChanged();
			
//			listAdapter = new PhotoAdapter(PhotoListActivity.this,
//					R.layout.photo_row, listItems, false);
//			listView.setAdapter(listAdapter);
//			listAdapter.notifyDataSetChanged();

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

//			menu.add("Delete").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
//			menu.add("Rename").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
			//menu.add("Share").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
			menu.add("Delete").setIcon(R.drawable.delete);
			menu.add("Rename").setIcon(R.drawable.rename);
			menu.add("Share").setIcon(R.drawable.share_icon);
			// menu.add("Mark unread").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
			// menu.add("Move").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
			// menu.add("Remove star").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

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
								for (int i = 0; i < selectedFileName.size(); i++) {
									((PhotoListActivity) ctx).removeFile(selectedFileName
											.get(i));
									// System.out.println(selectedFolderName.get(i)+""+selectedIndex.get(i));
				
								}
				
								((PhotoListActivity) ctx).runDeleteTask();
								dialog.dismiss();
							}
						});

				// create an alert dialog
			alertDialogBuilder.create().show();

			

//				for (int i = 0; i < selectedFileName.size(); i++) {
//					((PhotoListActivity) ctx).removeFile(selectedFileName
//							.get(i));
//					// System.out.println(selectedFolderName.get(i)+""+selectedIndex.get(i));
//
//				}
//
//				((PhotoListActivity) ctx).runDeleteTask();
				
//				LayoutInflater layoutInflater = null;
//				View contentView = ((PhotoListActivity) ctx)
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
//											for (int i = 0; i < selectedFileName.size(); i++) {
//											((PhotoListActivity) ctx).removeFile(selectedFileName
//													.get(i));
//											// System.out.println(selectedFolderName.get(i)+""+selectedIndex.get(i));
//						
//										}
//						
//										((PhotoListActivity) ctx).runDeleteTask();
//											dialog.dismiss();
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

					AppToast.show(ctx, SelectedForRename);
					((PhotoListActivity) ctx)
							.showRenameFileDialog(SelectedForRename);
				}

			} else {

				 if (selectedListItems.size() > 1) {
				 toast = Toast.makeText(ctx,
				 "Can not share multiple image at a time",
				 Toast.LENGTH_LONG);
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
			// Toast.makeText(ctx, "Action mode closed",
			// Toast.LENGTH_SHORT).show();
		}
	}

	public void removeFile(String fileName) {

		fileManager.deleteAnyFile(getApplicationContext(), folderName,
				subFolderName, fileName);
	}

	public void shareFile(String fileName) {

		String fileFullPath = AppExtra.APP_ROOT_FOLDER + "/" + folderName + "/"
				+ subFolderName + "/" + fileName;
		Bitmap bitmap = BitmapFactory.decodeFile(fileFullPath);

		Intent emailIntent = new Intent(Intent.ACTION_SEND);
		emailIntent.setType("image/jpeg");

		emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Image");
		emailIntent.putExtra(Intent.EXTRA_TEXT,
				"Enjoy this image");
		String pathofBmp = Images.Media.insertImage(getApplicationContext()
				.getContentResolver(), bitmap, "title", null);
		Uri bmpUri = Uri.parse(pathofBmp);
		emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		emailIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
		startActivity(Intent.createChooser(emailIntent, "Send mail..."));

	}

	public void runDeleteTask() {
		DeleteTask del = new DeleteTask();
		del.execute();

	}

	public void showRenameFileDialog(String selectedFileName) {
		  View contentView = getLayoutInflater().inflate(R.layout.dialog_screen_rename, null);
		fileManager.renameAnyFile(PhotoListActivity.this, folderName,
				subFolderName, selectedFileName,contentView);
		listAdapter.notifyDataSetChanged();

	}

	public void deletePhoto() {

	}

	public class DeleteTask extends AsyncTask<Void, Void, Void> {
		// ProgressDialog dialog;
		ProgressHUD mProgressHUD = new ProgressHUD(PhotoListActivity.this);

		@Override
		protected void onPreExecute() {

			mProgressHUD.show(true);
			// dialog = new ProgressDialog(PhotoListActivity.this);
			// dialog.setTitle("Deleting Folder...");
			// dialog.setMessage("Please wait...");
			// dialog.setIndeterminate(true);
			// dialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			// TODO Auto-generated method stub
			listItems.clear();
			listItems = fileManager.getAllPhotos(getApplicationContext(),
					folderName);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			setAdapter();
			if (mProgressHUD.isShowing()) {

				mProgressHUD.dismiss();
			}
			// dialog.dismiss();
		}

	}
    private String mCurrentPhotoPath;
    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";
    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    final int CAMERA_REQUEST = 111;
    private void importCamera() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File f = null;

        try {
            f = setUpPhotoFile();
            mCurrentPhotoPath = f.getAbsolutePath();
            takePictureIntent
                    .putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        } catch (IOException e) {
            e.printStackTrace();
            f = null;
            mCurrentPhotoPath = null;
        }

        startActivityForResult(takePictureIntent, CAMERA_REQUEST);

    }

    private File setUpPhotoFile() throws IOException {

        File f = createImageFile();
        mCurrentPhotoPath = f.getAbsolutePath();

        return f;
    }

    @SuppressLint("SimpleDateFormat")
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File albumF = getAlbumDir();
        File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX,
                albumF);
        return imageF;
    }

    @SuppressLint("SimpleDateFormat")
    private File getAlbumDir() {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {

            storageDir = mAlbumStorageDirFactory
                    .getAlbumStorageDir("CameraSample");

            if (storageDir != null) {
                if (!storageDir.mkdirs()) {
                    if (!storageDir.exists()) {
                        Log.d("CameraSample", "failed to create directory");
                        return null;
                    }
                }
            }

        } else {
            Log.v(getString(R.string.app_name),
                    "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }

}
