package com.smartmux.textmemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.vending.billing.IInAppBillingService;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.smartmux.textmemo.dialogfragment.FileVaultBannarDialog;
import com.smartmux.textmemo.dropbox.DropboxActivity;
import com.smartmux.textmemo.fragment.FragmentNoteListSort;
import com.smartmux.textmemo.modelclass.BannerItem;
import com.smartmux.textmemo.modelclass.NoteListItem;
import com.smartmux.textmemo.modelclass.SelectedNoteArray;
import com.smartmux.textmemo.util.IabHelper;
import com.smartmux.textmemo.util.IabResult;
import com.smartmux.textmemo.util.Inventory;
import com.smartmux.textmemo.util.Purchase;
import com.smartmux.textmemo.utils.AppExtra;
import com.smartmux.textmemo.utils.AppToast;
import com.smartmux.textmemo.utils.FileManager;
import com.smartmux.textmemo.utils.ImageLoader;
import com.smartmux.textmemo.widget.FloatingActionButton;

import java.util.ArrayList;

//import com.dropbox.sync.android.DbxAccountManager;
//import com.dropbox.sync.android.DbxFile;
//import com.dropbox.sync.android.DbxFileSystem;
//import com.dropbox.sync.android.DbxPath;

@SuppressLint("NewApi")
public class NoteListActivity extends AppMainActivity implements
		OnClickListener {

	private FileManager fileManager;
	public ArrayList<NoteListItem> listItems;

	IabHelper mHelper;
	IInAppBillingService mService;
	static final String ITEM_SKU = "com.smartmux.textmemo.upgrade";
	// static final String ITEM_SKU = "android.test.purchased";
	private boolean isPurchased = false;
	protected static final String TAG = null;

	public static boolean isPrefOpen = false;
    BannerItem bannerItem;
//	private static final String appKey = "d73msltjvsroyvt";
//	private static final String appSecret = "op6zra855l70frt";
//
//	private static final int REQUEST_LINK_TO_DBX = 0;

//	private DbxAccountManager mDbxAcctMgr;

	private FloatingActionButton addButton;

	public ViewPager viewPager ;
	public SmartTabLayout viewPagerTab ;
	FragmentPagerItems pages ;
	public FragmentPagerItemAdapter pagerAdapter;
	Typeface tf;


	RelativeLayout searchLayer ;
	Animation searchLayerOpen, searchLayerClose ;

	@Override
	protected void onResume() {
		super.onResume();

		if (!isPrefOpen) {
			int event_code = fileManager.getReturnCode(getApplicationContext());
			if (event_code == AppExtra.MAIN_ACTIVITY_CODE) {
				Intent i = new Intent(NoteListActivity.this,
						LoginWindowActivity.class);
				startActivity(i);
			}
		}
		if (isPrefOpen) {

			isPrefOpen = false;
		}

//		int listsize = listItems.size();

		if(isGranted){

			listItems = fileManager.getAllNotes(getApplicationContext());
		}

		
//		if(listItems.size()>listsize){
//			FileVaultAdBannar();
//		}
		if (bannerItem.getNotifyShow()) {
			notifyDialog();

            bannerItem.setNotifyShow(false);
		}
	}



	private boolean isGranted = false;

	@Override
	protected void onUserLeaveHint() {
		fileManager.setMainActivityCode(getApplicationContext());
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_note_list);

		if (getIntent().getBooleanExtra("EXIT", false)) {
			finish();
			overridePendingTransition(R.anim.push_right_out,
					R.anim.push_right_in);
		} else {
            bannerItem = new BannerItem();
			fileManager = new FileManager();
			fileManager.setDefaultCode(getApplicationContext());

			createCutomActionBarTitle();

//			listItems = fileManager.getAllNotes(getApplicationContext());
			viewPager = (ViewPager) findViewById(R.id.viewpager);

			viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);

			pages = new FragmentPagerItems(this);


			viewPagerTab.setOnPageChangeListener(new OnPageChangeListener() {

				@Override
				public void onPageSelected(int pos) {
					FragmentNoteListSort frag = (FragmentNoteListSort) pagerAdapter
							.getPage(pos);

					if (frag != null) {
						frag.setSelectedNotes(pos);

					}

				}

				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {
				}

				@Override
				public void onPageScrollStateChanged(int arg0) {
				}
			});

			String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAv8Xx97weT8W6USbDVfohZaGxCofJ7skttWdwTXqsr7hG7UbAPE2i9/rYjA6rprALAcqQoDB5rsNlXdkomswJPAztfEIBcvj15XQMiqiJI2BoOjguNdPlvLp45fn/vW1aRDk/DwXPgWUZCZypXOKRsZwFzVg2xrHZrPrv5284q0ASGwJU+mOE73PlFcsBbhjEGucFqEiQvF1xI2w/ViMhNKm8Otzz8hAnehKWU5iiI4PTaTAhlw/WdiK65juRPBu7uBkFfJOLt1/4GIBhIrpDyNgz7yhONBB7Oqw+B97YAvHt95DsLlK6geZGiEteP7HBT5dDs8vS6t8OAFQlil1V/QIDAQAB";

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

		}

		PermissionListener permissionlistener = new PermissionListener() {
			@Override
			public void onPermissionGranted() {
				//Toast.makeText(NoteListActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
				listItems = fileManager.getAllNotes(getApplicationContext());
				isGranted = true;
				pages.add(FragmentPagerItem.of("By Title",
						FragmentNoteListSort.class));
				pages.add(FragmentPagerItem.of("By Date",
						FragmentNoteListSort.class));
				pages.add(FragmentPagerItem.of("By Size",
						FragmentNoteListSort.class));

				pagerAdapter = new FragmentPagerItemAdapter(
						getSupportFragmentManager(), pages);

				viewPager.setAdapter(pagerAdapter);
				viewPager.setCurrentItem(1);
				viewPagerTab.setViewPager(viewPager);
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
				.setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
				,Manifest.permission.WAKE_LOCK,Manifest.permission.VIBRATE)
				.check();


		searchLayerOpen = AnimationUtils.loadAnimation(this,
				R.anim.slide_right_in);
		searchLayerClose = AnimationUtils.loadAnimation(this,
				R.anim.slide_right_out);
		tf = Typeface.createFromAsset(getAssets(), AppExtra.AVENIRLSTD_BLACK);

		searchLayer = (RelativeLayout) findViewById(R.id.layout_search);

		addButton = (FloatingActionButton) findViewById(R.id.fab_add_button);
		addButton.setOnClickListener(this);
		addButton.setVisibility(View.VISIBLE);
		addButton.setIcon(R.drawable.icon_edit);



//		mDbxAcctMgr = DbxAccountManager.getInstance(getApplicationContext(),
//				appKey, appSecret);
		Intent i = new Intent(NoteListActivity.this, LoginWindowActivity.class);
		startActivity(i);

	}

	private void createCutomActionBarTitle() {
		this.getActionBar().setDisplayShowCustomEnabled(true);
		this.getActionBar().setDisplayShowTitleEnabled(false);
		this.getActionBar().setDisplayShowHomeEnabled(false);

		LayoutInflater inflator = LayoutInflater.from(this);
		View v = inflator.inflate(R.layout.custom_action_bar, null);

		Typeface tf = Typeface.createFromAsset(getAssets(),
				AppExtra.AVENIRLSTD_BLACK);
		TextView title = (TextView) v.findViewById(R.id.textView_title);
		title.setTypeface(tf);

		ImageView drop_box = (ImageView) v.findViewById(R.id.imageView_dropbox);
		ImageView setting = (ImageView) v.findViewById(R.id.imageView_setting);
		ImageView search = (ImageView) v.findViewById(R.id.imageView_search);

		drop_box.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isPrefOpen = true;
				Intent wifiintent = new Intent(NoteListActivity.this,
						DropboxActivity.class);
				startActivity(wifiintent);
				overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
//				mDbxAcctMgr.startLink((Activity) NoteListActivity.this,
//						REQUEST_LINK_TO_DBX);
			}
		});

		setting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isPrefOpen = true;
							
				Intent i = new Intent(NoteListActivity.this,
						SettingsActivity.class);
				startActivity(i);
				overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
			}
		});

		search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (searchLayer.getVisibility() == View.GONE) {
					searchLayer.startAnimation(searchLayerOpen);
					searchLayer.setVisibility(View.VISIBLE);
					addButton.setVisibility(View.GONE);
				} else {
					searchLayer.startAnimation(searchLayerClose);
					searchLayer.setVisibility(View.GONE);

					// Check if no view has focus:
//					View view = NoteListActivity.this.getCurrentFocus();
					if (searchLayer != null) {
						InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(searchLayer.getWindowToken(), 0);
					}

					addButton.setVisibility(View.VISIBLE);
				}

			}
		});

		this.getActionBar().setCustomView(v);
	}

	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);

	}

	IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {

		@Override
		public void onQueryInventoryFinished(IabResult result,
				Inventory inventory) {
			if (result.isFailure()) {
				AppToast.show(getApplicationContext(),
						getString(R.string.Purchasing_info));
				return;
			} else {
				if (inventory != null && inventory.hasPurchase(ITEM_SKU)) {
					AppToast.show(getApplicationContext(),
							getString(R.string.have_upgrade));
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
						getString(R.string.purchase_fail));
				return;
			} else if (purchase.getSku().equals(ITEM_SKU)) {
				postPurchaseOperation();

			}

		}
	};

	private void startPurchase() {
		mHelper.launchPurchaseFlow(this, ITEM_SKU, 10002,
				mPurchaseFinishedListener, "");

	}

	private void postPurchaseOperation() {
		fileManager.setPaidStatus(getApplicationContext());
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
			super.onActivityResult(requestCode, resultCode, data);
		}

//		if (requestCode == REQUEST_LINK_TO_DBX) {
//			if (resultCode == Activity.RESULT_OK) {
//				DropboxTask task = new DropboxTask();
//				task.execute();
//			} else {
//				Log.d("dropbox error",
//						"Link to Dropbox failed or was cancelled.");
//			}
//		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mHelper != null)
			mHelper.dispose();
		mHelper = null;

	}

//	public class DropboxTask extends AsyncTask<String, String, String> {
//
//		ProgressHUD mProgressHUD;
//
//		@Override
//		protected void onPreExecute() {
//			mProgressHUD = ProgressHUD.show(NoteListActivity.this,
//					"Please wait...", true);
//			super.onPreExecute();
//		}
//
//		@Override
//		protected String doInBackground(String... params) {
//
//			try {
//
//				File dirPhoto = new File(AppExtra.APP_ROOT_FOLDER);
//				File[] files = dirPhoto.listFiles();
//
//				for (File inFile : files) {
//					
//					DbxPath testPath = new DbxPath(DbxPath.ROOT,
//							inFile.getName());
//					DbxFileSystem dbxFs = DbxFileSystem.forAccount(mDbxAcctMgr
//							.getLinkedAccount());
//					
//					
//					if (!dbxFs.exists(testPath)) {
//						DbxFile testFile = dbxFs.create(testPath);
//						try {
//							
//							
//							testFile.writeString(fileManager
//									.getNoteContent(inFile.getName()));
//						} finally {
//							testFile.close();
//						}
//					}
//				}
//
//			} catch (IOException e) {
//				Log.d("dropbox error", "Dropbox data save failed");
//			}
//
//			return null;
//		}
//
//		@Override
//		protected void onProgressUpdate(String... values) {
//			mProgressHUD.setMessage(values[0]);
//			super.onProgressUpdate(values);
//		}
//
//		@Override
//		protected void onPostExecute(String result) {
//			if (mProgressHUD.isShowing()) {
//
//				mProgressHUD.dismiss();
//			}
//			AppToast.show(getApplicationContext(),
//					getString(R.string.file_sync));
//		}
//
//	}
	
	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.fab_add_button: {
			isPurchased = fileManager.getPaidStatus(getApplicationContext());
			if (isPurchased) {
				Intent i = new Intent(this, NoteEditorActivity.class);
				i.putExtra(AppExtra.NOTE_MODE, AppExtra.NOTE_MODE_CREATE);
				startActivity(i);
				overridePendingTransition(R.anim.goto_right_next,
						R.anim.close_main);
			} else {

				int totalRow = 0;
				listItems = fileManager.getAllNotes(getApplicationContext());
				if (listItems != null)
					totalRow = listItems.size();
				if (totalRow >= AppExtra.TOTAL_TEXT_LIMIT) {
					showPurchaseDialog();

				} else {
					Intent i = new Intent(this, NoteEditorActivity.class);
					i.putExtra(AppExtra.NOTE_MODE, AppExtra.NOTE_MODE_CREATE);
					startActivity(i);
					overridePendingTransition(R.anim.push_left_in,
							R.anim.push_left_out);
				}
			}
			SelectedNoteArray.finishMode();

			break;
		}
		default:

			break;
		}
	}

	protected void showPurchaseDialog() {

		// get prompts.xml view
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		View promptView = layoutInflater.inflate(R.layout.dialog_purchase, null);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setView(promptView);

		// setup a dialog window
		alertDialogBuilder.setCancelable(true);
		// Add the buttons
		alertDialogBuilder.setNegativeButton("NO",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				});

		alertDialogBuilder.setPositiveButton("YES",
				new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog, int id) {

						mHelper.queryInventoryAsync(mGotInventoryListener);
						dialog.dismiss();
					}
				});

		// create an alert dialog
		final AlertDialog alert = alertDialogBuilder.create();

		alert.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface arg0) {

				alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTypeface(tf);
				alert.getButton(AlertDialog.BUTTON_POSITIVE).setTypeface(tf);
				alert.getButton(AlertDialog.BUTTON_NEGATIVE)
						.setBackgroundColor(Color.parseColor("#FFFFFF"));
				alert.getButton(AlertDialog.BUTTON_POSITIVE)
						.setBackgroundColor(Color.parseColor("#FFFFFF"));

				// alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
				// Color.BLUE);
				//
				// alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
				// Color.BLUE);
			}
		});
		alert.show();
	}

	private void notifyDialog() {

//		mImageLoader = new ImageLoader(this);

		// get prompts.xml view
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		View promptView = layoutInflater.inflate(R.layout.dialog_notification,
				null);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(NoteListActivity.this);
		alertDialogBuilder.setView(promptView);

		ImageView thumb = (ImageView) promptView
				.findViewById(R.id.notify_thumb);
        ImageLoader mImageLoader = new ImageLoader(NoteListActivity.this);

		if (!TextUtils.isEmpty(bannerItem.getThumbUrl())) {

			mImageLoader.DisplayImage(bannerItem.getThumbUrl(), thumb);
//
//			 URL url;
//			try {
//				url = new URL(bannerItem.getThumbUrl());
//				 Bitmap bmp =
//						 BitmapFactory.decodeStream(url.openConnection().getInputStream());
//						 thumb.setImageBitmap(bmp);
//			} catch (MalformedURLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			

		}


		// setup a dialog window
		alertDialogBuilder.setCancelable(true);

		alertDialogBuilder.setPositiveButton(bannerItem.getText(),
				new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog, int id) {

						if (!TextUtils.isEmpty(bannerItem.getActionUrl())) {
							Intent i = new Intent(Intent.ACTION_VIEW, Uri
									.parse(bannerItem.getActionUrl()));
							startActivity(i);
						}

						dialog.dismiss();

					}
				});

		// alertDialogBuilder.setOnDismissListener(new OnDismissListener() {
		//
		// @Override
		// public void onDismiss(DialogInterface dialog) {
		// BannerItem.thumbUrl = "";
		// BannerItem.text = "";
		// BannerItem.actionUrl= "";
		//
		// }
		// });
		// create an alert dialog
		alertDialogBuilder.create().show();

	}

	
	private String FILEVAULT_PKG = "com.smartmux.filevaultfree";
	private void FileVaultAdBannar() {
	

		boolean installed = appInstalledOrNot(FILEVAULT_PKG);
		if (installed) {
//			ad_Show = true;
		} else {
			
			FragmentManager fragmentManager = getFragmentManager();
			FileVaultBannarDialog bannar = new FileVaultBannarDialog();
			bannar.show(fragmentManager, "Ad Bannar Dialog Fragment");

		}

	}

	private boolean appInstalledOrNot(String uri) {
		PackageManager pm = getPackageManager();
		boolean app_installed;
		try {
			pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
			app_installed = true;
		} catch (PackageManager.NameNotFoundException e) {
			app_installed = false;
		}
		return app_installed;
	}
	
}
