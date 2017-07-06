package com.smartmux.filevaultfree;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.android.vending.billing.IInAppBillingService;
import com.smartmux.filevaultfree.adapter.RecycleView_SubFolderListAdapter;
import com.smartmux.filevaultfree.adapter.RecycleView_SubFolderListAdapter.OnItemClickListener;
import com.smartmux.filevaultfree.audio.AudioListActivity;
import com.smartmux.filevaultfree.modelclass.SubFolderItemRow;
import com.smartmux.filevaultfree.note.NoteListActivity;
import com.smartmux.filevaultfree.photo.PhotoListActivity;
import com.smartmux.filevaultfree.util.IabHelper;
import com.smartmux.filevaultfree.util.IabResult;
import com.smartmux.filevaultfree.util.Inventory;
import com.smartmux.filevaultfree.util.Purchase;
import com.smartmux.filevaultfree.utils.AppActionBar;
import com.smartmux.filevaultfree.utils.AppExtra;
import com.smartmux.filevaultfree.utils.AppToast;
import com.smartmux.filevaultfree.utils.FileManager;
import com.smartmux.filevaultfree.utils.GeneralUtils;
import com.smartmux.filevaultfree.video.VideoListActivity;

import java.util.ArrayList;

public class SubFolderListActivity extends AppMainActivity {

	private String folderName;
	private FileManager fileManager;
	private RecycleView_SubFolderListAdapter listAdapter;
	private RecyclerView listView;
	private ArrayList<SubFolderItemRow> listItems;
	private ArrayList<String> listItemsProperNames;
//	private EasyTracker easyTracker = null;

	IabHelper mHelper;
	IInAppBillingService mService;
	private boolean isPurchased = false;
	protected static final String TAG = null;

	@SuppressLint({ "NewApi", "DefaultLocale" })
	@Override
	public void onStart() {
		super.onStart();
//		EasyTracker.getInstance(this).activityStart(this);
	}

	@Override
	public void onStop() {
		super.onStop();
//		EasyTracker.getInstance(this).activityStop(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		boolean isBackground = GeneralUtils
				.isApplicationBroughtToBackground(getApplicationContext());

		int event_code = fileManager.getReturnCode(getApplicationContext());
		if (event_code == AppExtra.HOME_CODE && !isBackground) {
			Intent i = new Intent(SubFolderListActivity.this,
					LoginWindowActivity.class);
			startActivity(i);
			// Toast.makeText(getApplicationContext(), "test", 1000).show();
			// BlurBehind.getInstance().execute(SubFolderListActivity.this, new
			// OnBlurCompleteListener() {
			// @Override
			// public void onBlurComplete() {
			// Intent intent = new Intent(SubFolderListActivity.this,
			// LoginWindowActivity.class);
			// intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			// startActivity(intent);
			//
			// }
			// });

		}

		listItems.clear();
		listItems.add(new SubFolderItemRow(getApplicationContext().getString(
				R.string.photos), fileManager.getFileCount(folderName,
				AppExtra.FOLDER_PHOTOS)
				+ " "
				+ getApplicationContext().getString(R.string.photos),
				R.drawable.subfolder_photo));
		listItems.add(new SubFolderItemRow(getApplicationContext().getString(
				R.string.videos), fileManager.getFileCount(folderName,
				AppExtra.FOLDER_VIDEOS)
				+ " "
				+ getApplicationContext().getString(R.string.videos),
				R.drawable.subfolder_video));
		listItems.add(new SubFolderItemRow(getApplicationContext().getString(
				R.string.notes), fileManager.getFileCount(folderName,
				AppExtra.FOLDER_NOTES)
				+ " "
				+ getApplicationContext().getString(R.string.notes),
				R.drawable.subfolder_note));
		listItems.add(new SubFolderItemRow(getApplicationContext().getString(
				R.string.audios), fileManager.getFileCount(folderName,
				AppExtra.FOLDER_AUDIOS)
				+ " "
				+ getApplicationContext().getString(R.string.audios),
				R.drawable.subfolder_audio));
		listAdapter.notifyDataSetChanged();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_HOME) {
			fileManager.setHomeCode(getApplicationContext());
			AppActivityManager.setLastActivity(this);
		} else if (keyCode == KeyEvent.KEYCODE_BACK) {
			fileManager.setBackCode(getApplicationContext());
			finish();
			overridePendingTransition(R.anim.push_right_out,
					R.anim.push_right_in);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onUserLeaveHint() {
		fileManager.setHomeCode(getApplicationContext());
		super.onUserLeaveHint();
	}

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sub_folder_list_activity);

		fileManager = new FileManager();
		AppActionBar.changeActionBarFont(getApplicationContext(),
				SubFolderListActivity.this);
		AppActionBar.updateAppActionBar(getActionBar(), this, true);
		Bundle bundle = getIntent().getExtras();
		folderName = bundle.getString(AppExtra.FOLDER_NAME);
		if (folderName != null) {
			getActionBar().setTitle(folderName);
		}

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

		listItems = new ArrayList<SubFolderItemRow>();
		listItems.add(new SubFolderItemRow("Photos", fileManager.getFileCount(
				folderName, AppExtra.FOLDER_PHOTOS) + " Photos",
				R.drawable.subfolder_photo));
		listItems.add(new SubFolderItemRow("Videos", fileManager.getFileCount(
				folderName, AppExtra.FOLDER_VIDEOS) + " Videos",
				R.drawable.subfolder_video));
		listItems.add(new SubFolderItemRow("Notes", fileManager.getFileCount(
				folderName, AppExtra.FOLDER_NOTES) + " Notes",
				R.drawable.subfolder_note));
		listItems.add(new SubFolderItemRow("Audios", fileManager.getFileCount(
				folderName, AppExtra.FOLDER_AUDIOS) + " Audios",
				R.drawable.subfolder_audio));

		listItemsProperNames = new ArrayList<String>();
		listItemsProperNames.add(AppExtra.FOLDER_PHOTOS);
		listItemsProperNames.add(AppExtra.FOLDER_VIDEOS);
		listItemsProperNames.add(AppExtra.FOLDER_NOTES);
		listItemsProperNames.add(AppExtra.FOLDER_AUDIOS);

		listView = (RecyclerView) findViewById(R.id.listviewSubFolderList);
		listView.setHasFixedSize(true);
		listView.setLayoutManager(new LinearLayoutManager(
				this, LinearLayoutManager.VERTICAL, false));
		

		listAdapter = new RecycleView_SubFolderListAdapter(this, 
				listItems);
		listView.setAdapter(listAdapter);

//		easyTracker = EasyTracker.getInstance(SubFolderListActivity.this);
		//
		listAdapter.SetOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(View view, int position) {
				String subFolderName = listItemsProperNames.get(position)
						.toLowerCase();

				switch (position) {
				case 0:// Photos
				{

//					easyTracker.send(MapBuilder.createEvent("Photos",
//							"onItemClick", "listView/listviewSubFolderList",
//							null).build());
					fileManager.setBackCode(getApplicationContext());
					Intent i = new Intent(SubFolderListActivity.this,
							PhotoListActivity.class);
					i.putExtra(AppExtra.FOLDER_NAME, folderName);
					i.putExtra(AppExtra.SUB_FOLDER_NAME, subFolderName);
					startActivity(i);
					overridePendingTransition(R.anim.push_left_in,
							R.anim.push_left_out);
				}
					break;
				case 1:// Videos
				{

//					easyTracker.send(MapBuilder.createEvent("Videos",
//							"onItemClick", "listView/listviewSubFolderList",
//							null).build());
					 isPurchased =
					 fileManager.getPaidStatus(getApplicationContext());

					// testing purpose
//					isPurchased = true;
					if (isPurchased) {
						fileManager.setBackCode(getApplicationContext());
						Intent i = new Intent(SubFolderListActivity.this,
								VideoListActivity.class);
						i.putExtra(AppExtra.FOLDER_NAME, folderName);
						i.putExtra(AppExtra.SUB_FOLDER_NAME, subFolderName);
						startActivity(i);
						overridePendingTransition(R.anim.push_left_in,
								R.anim.push_left_out);
					} else {
						showPurchaseDialog();
					}
				}
					break;
				case 2:// Notes
				{

//					easyTracker.send(MapBuilder.createEvent("Notes",
//							"onItemClick", "listView/listviewSubFolderList",
//							null).build());
					fileManager.setBackCode(getApplicationContext());
					Intent i = new Intent(SubFolderListActivity.this,
							NoteListActivity.class);
					i.putExtra(AppExtra.FOLDER_NAME, folderName);
					i.putExtra(AppExtra.SUB_FOLDER_NAME, subFolderName);
					startActivity(i);
					overridePendingTransition(R.anim.push_left_in,
							R.anim.push_left_out);
				}
					break;
				case 3:// Audio
				{

//					easyTracker.send(MapBuilder.createEvent("Audio",
//							"onItemClick", "listView/listviewSubFolderList",
//							null).build());
					 isPurchased =
					 fileManager.getPaidStatus(getApplicationContext());

					// testing purpose
//					isPurchased = true;
					if (isPurchased) {
						fileManager.setBackCode(getApplicationContext());
						Intent i = new Intent(SubFolderListActivity.this,
								AudioListActivity.class);
						i.putExtra(AppExtra.FOLDER_NAME, folderName);
						i.putExtra(AppExtra.SUB_FOLDER_NAME, subFolderName);
						startActivity(i);
						overridePendingTransition(R.anim.push_left_in,
								R.anim.push_left_out);
					} else {
						showPurchaseDialog();
					}
				}
					break;
				default:
					break;
				}
			}
		});
	}

	private void showPurchaseDialog() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				SubFolderListActivity.this);
		alertDialog.setTitle("Purchase Required!");
		alertDialog.setMessage(R.string.purchase_dialog);
		alertDialog.setPositiveButton("YES",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
//						mHelper.queryInventoryAsync(mGotInventoryListener);
                        fileManager.setPaidStatus(getApplicationContext());
						dialog.cancel();
					}
				});

		alertDialog.setNegativeButton("NO",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		alertDialog.show();
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
}
