package com.smartmux.filevaultfree;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.smartmux.filevaultfree.adapter.RecycleView_FolderListAdapter;
import com.smartmux.filevaultfree.adapter.RecycleView_FolderListAdapter.OnItemClickListener;
import com.smartmux.filevaultfree.modelclass.CommonItemRow;
import com.smartmux.filevaultfree.util.IabHelper;
import com.smartmux.filevaultfree.util.IabResult;
import com.smartmux.filevaultfree.util.Inventory;
import com.smartmux.filevaultfree.util.Purchase;
import com.smartmux.filevaultfree.utils.AppActionBar;
import com.smartmux.filevaultfree.utils.AppExtra;
import com.smartmux.filevaultfree.utils.AppToast;
import com.smartmux.filevaultfree.utils.FileManager;
import com.smartmux.filevaultfree.utils.FileUtil;
import com.smartmux.filevaultfree.utils.GeneralUtils;
import com.smartmux.filevaultfree.utils.ProgressHUD;
import com.smartmux.filevaultfree.widget.AddFloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FolderListActivity extends AppMainActivity {

	private FileManager fileManager;
//	private ListView listView;
	private RecyclerView folderListView;
//	private CommonListAdapter listAdapter;
	private RecycleView_FolderListAdapter folderListAdapter;
	
	private ArrayList<CommonItemRow> listItems = new ArrayList<CommonItemRow>();
	IabHelper mHelper;
//	IInAppBillingService mService;
	private boolean isPurchased = false;
	private AddFloatingActionButton floatingButton;
	private EditText newNameEditText;
	private static TextView headerTitle;
	protected static final String TAG = null;
    private boolean isGranted = false;
	// private EasyTracker easyTracker = null;

	@SuppressLint("NewApi")
	@Override
	protected void onResume() {
		super.onResume();

		int event_code = fileManager.getReturnCode(getApplicationContext());
		if (event_code != AppExtra.BACK_CODE) {
			Intent i = new Intent(FolderListActivity.this,
					LoginWindowActivity.class);
			startActivity(i);
		}

//		if (folderListAdapter != null) {
//			folderListAdapter = null;
//		}


        if(isGranted) {

            listItems = fileManager.getFolderNames(getApplicationContext());

		if (listItems != null) {
			folderListAdapter = new RecycleView_FolderListAdapter(this,
					listItems, false);
			folderListView.setAdapter(folderListAdapter);
		

		
		folderListAdapter.SetOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(View view, int position) {
				
				String folderName = listItems.get(position).getTitle();
				Intent i = new Intent(FolderListActivity.this,
						SubFolderListActivity.class);
				i.putExtra(AppExtra.FOLDER_NAME, folderName);
				startActivity(i);
				overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);
			}
		});
		}
        }
		
	}

	@Override
	public void onStart() {
		super.onStart();
		// EasyTracker.getInstance(this).activityStart(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		// EasyTracker.getInstance(this).activityStop(this);
	}


	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.folder_list_activity);

		if (GeneralUtils.checkSdcard(getApplicationContext(), true)) {
			floatingButton = (AddFloatingActionButton) findViewById(R.id.fab_image_button);

			if (getIntent().getBooleanExtra("EXIT", false)) {
				finish();
				overridePendingTransition(R.anim.push_right_out,
						R.anim.push_right_in);
				;
			} else {

				// easyTracker =
				// EasyTracker.getInstance(FolderListActivity.this);
				//
				// easyTracker.send(MapBuilder.createEvent("your_action",
				// "envet_name", "button_name/id", null).build());
				// easyTracker.send(MapBuilder.createException(
				// new StandardExceptionParser(FolderListActivity.this, null)
				// .getDescription(Thread.currentThread().getName(), "e"),
				// false).build());

                AppActionBar.changeActionBarFont(getApplicationContext(),
                        FolderListActivity.this);
                AppActionBar.updateAppActionBar(getActionBar(), this, false);
				fileManager = new FileManager();
				fileManager.setBackCode(getApplicationContext());
//				listItems = fileManager.getFolderNames(getApplicationContext());
				
				folderListView = (RecyclerView) findViewById(R.id.listviewFolderList);
				folderListView.setHasFixedSize(true);
				folderListView.setLayoutManager(new LinearLayoutManager(
						this, LinearLayoutManager.VERTICAL, false));


                PermissionListener permissionlistener = new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        //Toast.makeText(NoteListActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();

                        isGranted = true;
                        setAdapter();
                        if (listItems == null || listItems.size() == 0) {
                            showCreateNewFolderDialog();
                        }

                    }

                    @Override
                    public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                        //	Toast.makeText(NoteListActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                        isGranted = false;
                    }


                };


                new TedPermission(this)
                        .setPermissionListener(permissionlistener)
                                //	.setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                        .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO
                               ,Manifest.permission.CAMERA ,Manifest.permission.WAKE_LOCK,Manifest.permission.VIBRATE)
                        .check();

				
			


				String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjLiWilouJZ0ZWBXOxGzHJrRbrKQYSlXbWwSDEE8qWGZnkNfYtfUjD1tTrO8Unt5UXe3kqq9Y5R2BDCjixTfsPFhYmx752L+aAg0Od3X7KGlx8xSYFKY7JjVz9J10U19R+LE9OtEeTa54bVei5NgJKur7NqHKbDMx4A4g5Pbq/GncwyQaBGTbbq1sS7NgjruQxV7kVee98DKc5n5bH3IYCL03TdbPItAuwM27VXMQxWMqpqpsRFQwgTJRHunwieBYFzo75DKsX/UrX9gv+5U/QwBC8mBdCxYrN7WVyhJAtyh0Y0TMuH4z+Z9kiexnx48oLdYQkAbpcbmfrtgoa67uiQIDAQAB";

				mHelper = new IabHelper(this, base64EncodedPublicKey);
				if (mHelper != null) {
					mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
						public void onIabSetupFinished(IabResult result) {
							if (!result.isSuccess()) {
								Log.d(TAG, "In-app Billing setup failed: "
										+ result);
							} else {
								Log.d(TAG, "In-app Billing is set up OK");
							}
						}
					});
				}

				Intent i = new Intent(FolderListActivity.this,
						LoginWindowActivity.class);
				startActivity(i);

			}

			floatingButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {

					isPurchased = fileManager
							.getPaidStatus(getApplicationContext());
					if (isPurchased) {
						showCreateNewFolderDialog();
					} else {
						int totalFolders = 0;
						if (listItems != null)
							totalFolders = listItems.size();
						if (totalFolders >= AppExtra.FOLDER_LIMIT) {
							AlertDialog.Builder alertDialog = new AlertDialog.Builder(
									FolderListActivity.this);
							alertDialog.setTitle("Purchase Required!");
							alertDialog.setMessage(R.string.purchase_dialog);
							alertDialog.setPositiveButton("YES",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											mHelper.queryInventoryAsync(mGotInventoryListener);
											dialog.cancel();
										}
									});

							alertDialog.setNegativeButton("NO",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.cancel();
										}
									});
							alertDialog.show();
						} else {
							showCreateNewFolderDialog();
						}

					}
				}
			});
		} else {
			Toast.makeText(getApplicationContext(), "sd card not available",
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.settings_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_setting:

			Intent intent = new Intent(FolderListActivity.this,
					SettingActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			// AppToast.show(getApplicationContext(), "Settings");
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void removeFolder(final String folderName) {
		String path = AppExtra.APP_ROOT_FOLDER + "/" + folderName;

		File dir = new File(path);
		// fileManager.deleteFolder(getApplicationContext(),folderName);
		fileManager.deleteDir(dir);

	}

	private void showRenameFolderDialog(final String folderName) {
		
	
		// get prompts.xml view
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		View promptView = layoutInflater.inflate(R.layout.dialog_screen_rename, null);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setView(promptView);
		
		headerTitle = (TextView) promptView.findViewById(R.id.dialog_header);
		headerTitle.setText("Rename Folder");
		
		newNameEditText = (EditText) promptView.findViewById(R.id.newName);
		OnFocusChangeListener ofcListener = new MyFocusChangeListener();
		newNameEditText.setOnFocusChangeListener(ofcListener);

		newNameEditText.setHint(folderName + " Rename to..");
		
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
						 String newName = newNameEditText.getText()
								.toString().trim();
						if (newName.length() == 0) {
							// AppToast.show(getApplicationContext(),
							// folderName+"Rename to..");
							return;
						}
						fileManager.renameFolder(
								getApplicationContext(),
								folderName, newName);
						setAdapter();
						dialog.dismiss();
					}
				});

		// create an alert dialog
	alertDialogBuilder.create().show();


	}

	public void setAdapter() {
		if (listItems.size() > 0) {

			listItems.clear();
		}
		listItems = fileManager.getFolderNames(getApplicationContext());
		if (listItems != null) {
			folderListAdapter = new RecycleView_FolderListAdapter(this, 
					listItems, false);
			folderListView.setAdapter(folderListAdapter);
		}
//		folderListAdapter.notifyDataSetChanged();
		
	
	}

	private void showCreateNewFolderDialog() {

		// get prompts.xml view
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		View promptView = layoutInflater.inflate(R.layout.dialog_screen_rename, null);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setView(promptView);
		
		headerTitle = (TextView) promptView.findViewById(R.id.dialog_header);
		headerTitle.setText("Create New Folder");
		
		newNameEditText = (EditText) promptView.findViewById(R.id.newName);
		OnFocusChangeListener ofcListener = new MyFocusChangeListener();
		newNameEditText.setOnFocusChangeListener(ofcListener);

		newNameEditText.setHint("Folder Name");
		
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

						String folderName = newNameEditText
								.getText().toString().trim();

						// AppToast.show(getApplicationContext(),
						// folderName);
						createFolder(folderName);
						dialog.dismiss();
					}
				});

		// create an alert dialog
	alertDialogBuilder.create().show();
	}

	private void createFolder(String folderName){
		boolean shouldCreate = fileManager
				.createNewFolder(
						getApplicationContext(),
						folderName);
		if (shouldCreate) {
			Context context = getApplicationContext();
			String str = context
					.getString(R.string.photos_0)
					+ ","
					+ context
							.getString(R.string.videos_0)
					+ ","
					+ context
							.getString(R.string.notes_0)
					+ ","
					+ context
							.getString(R.string.audios_0);
			CommonItemRow item = new CommonItemRow(
					R.drawable.folder_icon,
					folderName,
					FileUtil.getSizeInFormat(0),
					FileUtil.getJustCurrentDateTime(),
					str, folderName);
			listItems.add(item);
			folderListAdapter.notifyDataSetChanged();
		}
	}
	
	@Override
	protected void onUserLeaveHint() {
		fileManager.setDefaultCode(getApplicationContext());
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
		overridePendingTransition(R.anim.push_right_out, R.anim.push_right_in);
		;
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

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mHelper != null)
			mHelper.dispose();
		mHelper = null;

	}

	public static final class AnActionModeOfEpicProportions implements
			ActionMode.Callback {

		Context ctx;

		public AnActionModeOfEpicProportions(Context ctx) {
			this.ctx = ctx;
		}

		@SuppressLint("NewApi")
		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			menu.add("Delete").setIcon(R.drawable.delete);
			menu.add("Rename").setIcon(R.drawable.rename);

			// menu.add("Share").setIcon(R.drawable.share_icon);

			// menu.add("Delete").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
			// menu.add("Rename").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
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
			final List<Integer> selectedIndex = new ArrayList<Integer>();
			final ArrayList<String> selectedFolderName = new ArrayList<String>();
			String SelectedForRename = null;
			int indexnum = 0;
			for (CommonItemRow i : ((FolderListActivity) ctx).folderListAdapter.data) {
				if (i.isChecked()) {
					selectedListItems.add(i);
					selectedItems.append(i.getTitle()).append(", ");
					SelectedForRename = i.getTitle();
					selectedIndex.add(indexnum);
					selectedFolderName.add(i.getTitle());

				}
				indexnum++;
			}

			if (item.getTitle().equals("Delete")) {
				// Delete



				// get prompts.xml view
				LayoutInflater layoutInflater = LayoutInflater.from((FolderListActivity) ctx);
				View promptView = layoutInflater.inflate(R.layout.dialog_screen_delete, null);
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder((FolderListActivity) ctx);
				alertDialogBuilder.setView(promptView);
				
//				headerTitle = (TextView) promptView.findViewById(R.id.dialog_delete_header);
//				headerTitle.setText("Delete");
				
				TextView alertText = (TextView) promptView
						.findViewById(R.id.alert_text);
				alertText.setText(R.string.delete_folder_dialog);
				
				// setup a dialog window
				alertDialogBuilder.setCancelable(true);

				
				// Add the buttons
				alertDialogBuilder.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.dismiss();
							}
						});

				alertDialogBuilder.setPositiveButton(R.string.done,
						new DialogInterface.OnClickListener() {
							public void onClick(final DialogInterface dialog, int id) {
								for (int i = 0; i < selectedFolderName
										.size(); i++) {
									((FolderListActivity) ctx)
											.removeFolder(selectedFolderName
													.get(i));
									System.out.println(selectedFolderName
											.get(i)
											+ ""
											+ selectedIndex.get(i));

								}

								((FolderListActivity) ctx)
										.runDeleteTask();
								dialog.dismiss();
							}
						});

				// create an alert dialog
			alertDialogBuilder.create().show();

			} else if (item.getTitle().equals("Rename")) {
				// Archive
				if (selectedListItems.size() > 1) {
					toast = Toast.makeText(ctx,
							"Can not rename multiple folder at a time",
							Toast.LENGTH_SHORT);
				} else {
					((FolderListActivity) ctx)
							.showRenameFolderDialog(SelectedForRename);
					// toast = Toast.makeText(ctx, "Archive: " +
					// selectedItems.toString(), Toast.LENGTH_SHORT);
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
			((FolderListActivity) ctx).folderListAdapter.checkedCount = 0;
			((FolderListActivity) ctx).folderListAdapter.isActionModeShowing = false;
			// set list items states to false
			for (CommonItemRow item : ((FolderListActivity) ctx).folderListAdapter.data) {
				item.setIsChecked(false);
			}
			((FolderListActivity) ctx).folderListAdapter.notifyDataSetChanged();
			// Toast.makeText(ctx, "Action mode closed",
			// Toast.LENGTH_SHORT).show();
		}
	}

	public void runDeleteTask() {
		DeleteTask del = new DeleteTask();
		del.execute();

	}

	public class DeleteTask extends AsyncTask<Void, Void, Void> {
		// ProgressDialog dialog;
		ProgressHUD mProgressHUD = new ProgressHUD(FolderListActivity.this);

		@Override
		protected void onPreExecute() {
			mProgressHUD.show(true);
			// dialog = new ProgressDialog(FolderListActivity.this);
			// dialog.setTitle("Deleting Folder...");
			// dialog.setMessage("Please wait...");
			// dialog.setIndeterminate(true);
			// dialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			listItems.clear();
			listItems = fileManager.getFolderNames(getApplicationContext());
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			setAdapter();
			// listAdapter.notifyDataSetChanged();
			if (mProgressHUD.isShowing()) {

				mProgressHUD.dismiss();
			}
		}

	}

	private class MyFocusChangeListener implements OnFocusChangeListener {

		public void onFocusChange(View v, boolean hasFocus) {

			if (v.getId() == R.id.newName && !hasFocus) {

				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				// AppToast.show(getApplicationContext(), "focus cange");
			}
		}
	}
}
