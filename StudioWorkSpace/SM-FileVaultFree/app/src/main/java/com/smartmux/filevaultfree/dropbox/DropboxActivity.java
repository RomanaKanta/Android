package com.smartmux.filevaultfree.dropbox;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.DropboxFileInfo;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.TokenPair;
import com.smartmux.filevaultfree.AppMainActivity;
import com.smartmux.filevaultfree.R;
import com.smartmux.filevaultfree.modelclass.CommonItemRow;
import com.smartmux.filevaultfree.util.IabHelper;
import com.smartmux.filevaultfree.util.IabResult;
import com.smartmux.filevaultfree.util.Inventory;
import com.smartmux.filevaultfree.util.Purchase;
import com.smartmux.filevaultfree.utils.AppActionBar;
import com.smartmux.filevaultfree.utils.AppExtra;
import com.smartmux.filevaultfree.utils.AppToast;
import com.smartmux.filevaultfree.utils.FileManager;
import com.smartmux.filevaultfree.utils.ProgressHUD;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class DropboxActivity extends AppMainActivity implements OnClickListener {

	private DropboxAPI<AndroidAuthSession> dropboxApi;
	private FileManager fileManager;
	private RelativeLayout uploadFileBtn, file_up_layer;
	private RelativeLayout downloadFilesBtn;
	private RelativeLayout loginBtn, login_layer;
	private boolean isUserLoggedIn;
	TextView dropbox_title;
	private boolean isPurchased = false;
	IabHelper mHelper;
	IInAppBillingService mService;

	private final static String DROPBOX_NAME = "dropbox_prefs";
	private final static String APP_KEY = "txctm17kb1ecg5s";
	private final static String APP_SECRET = "4dk5x6mkiplqsdx";
	private final static AccessType ACCESS_TYPE = AccessType.APP_FOLDER;
	private ArrayList<CommonItemRow> listItems = new ArrayList<CommonItemRow>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dropbox);

		AppActionBar.changeActionBarFont(getApplicationContext(),
				DropboxActivity.this);

		AppActionBar.updateAppActionBar(getActionBar(), this, true);
		getActionBar().setTitle(getString(R.string.dropbox));

		fileManager = new FileManager();

		isPurchased = fileManager.getPaidStatus(getApplicationContext());

		listItems = fileManager.getFolderNames(getApplicationContext());

		dropbox_title = (TextView) findViewById(R.id.textView_db_header);
		file_up_layer = (RelativeLayout) findViewById(R.id.upload_layer);
		login_layer = (RelativeLayout) findViewById(R.id.login_layer);

		loginBtn = (RelativeLayout) findViewById(R.id.loginBtn);
		loginBtn.setOnClickListener(this);
		uploadFileBtn = (RelativeLayout) findViewById(R.id.uploadFileBtn);
		uploadFileBtn.setOnClickListener(this);
		downloadFilesBtn = (RelativeLayout) findViewById(R.id.downloadFilesBtn);
		downloadFilesBtn.setOnClickListener(this);

		loggedIn(false);

		AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);
		AndroidAuthSession session;

		SharedPreferences prefs = getSharedPreferences(DROPBOX_NAME, 0);
		String key = prefs.getString(APP_KEY, null);
		String secret = prefs.getString(APP_SECRET, null);

		if (key != null && secret != null) {
			AccessTokenPair token = new AccessTokenPair(key, secret);
			session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE, token);
		} else {
			session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE);
		}

		dropboxApi = new DropboxAPI<AndroidAuthSession>(session);
		
		 initPurchase();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.dropbox_menu, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem logout = menu.findItem(R.id.dropbox_logout);

		logout.setVisible(isUserLoggedIn);

		return true;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.dropbox_logout:
			dropboxApi.getSession().unlink();
			loggedIn(false);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		AndroidAuthSession session = dropboxApi.getSession();
		if (session.authenticationSuccessful()) {
			try {
				session.finishAuthentication();

				TokenPair tokens = session.getAccessTokenPair();
				SharedPreferences prefs = getSharedPreferences(DROPBOX_NAME, 0);
				Editor editor = prefs.edit();
				editor.putString(APP_KEY, tokens.key);
				editor.putString(APP_SECRET, tokens.secret);
				editor.commit();

				loggedIn(true);
			} catch (IllegalStateException e) {
				Toast.makeText(this, "Error during Dropbox authentication",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.loginBtn:
			// if (isUserLoggedIn) {
			// dropboxApi.getSession().unlink();
			// loggedIn(false);
			// } else {
			dropboxApi.getSession().startAuthentication(DropboxActivity.this);
			// }
			break;

		case R.id.uploadFileBtn:
			// uploadFileBtn.setBackgroundResource(R.drawable.file_down_btn_bg);
			UploadFile uploadFile = new UploadFile(AppExtra.APP_ROOT_FOLDER);
			uploadFile.execute();

			break;
		case R.id.downloadFilesBtn:
			isPurchased = fileManager.getPaidStatus(getApplicationContext());
			// downloadFilesBtn.setBackgroundResource(R.drawable.file_down_btn_bg);
			DownloadFiles listFiles = new DownloadFiles(
					AppExtra.APP_ROOT_FOLDER);
			listFiles.execute();
			break;
		default:
			break;
		}
	}

	private void loggedIn(boolean userLoggedIn) {
		isUserLoggedIn = userLoggedIn;

		// uploadFileBtn.setEnabled(userLoggedIn);
		// downloadFilesBtn.setEnabled(userLoggedIn);

		if (userLoggedIn) {
			// uploadFileBtn.setVisibility(View.VISIBLE);
			// downloadFilesBtn.setVisibility(View.VISIBLE);
			// login_text.setText("Logout");
			// loginBtn.setBackgroundResource(R.drawable.file_down_btn_bg);

			file_up_layer.setVisibility(View.VISIBLE);
			login_layer.setVisibility(View.GONE);
			dropbox_title.setText("Now Experience storage with Dropbox");

		} else {
			file_up_layer.setVisibility(View.GONE);
			login_layer.setVisibility(View.VISIBLE);
			dropbox_title.setText("Dropbox");

			// uploadFileBtn.setVisibility(View.GONE);
			// downloadFilesBtn.setVisibility(View.GONE);
			// login_text.setText("Log in");
			// loginBtn.setBackgroundResource(R.drawable.file_up_btn_bg);
		}

		invalidateOptionsMenu();

		// uploadFileBtn
		// .setBackgroundColor(userLoggedIn ? Color.BLUE : Color.GRAY);
		//
		//
		// downloadFilesBtn.setBackgroundColor(userLoggedIn ? Color.BLUE
		// : Color.GRAY);
		// login_text.setText(userLoggedIn ? "Logout" : "Log in");
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
		overridePendingTransition(R.anim.push_right_out, R.anim.push_right_in);
		;
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

	public class UploadFile extends AsyncTask<Void, Long, Boolean> {

		ProgressHUD mProgressHUD;
		private String path;

		public UploadFile(String dropboxPath) {
			this.path = dropboxPath;
		}

		@Override
		protected void onPreExecute() {
			mProgressHUD = ProgressHUD.show(DropboxActivity.this,
					"Please wait...", true);
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(Void... params) {

			try {
				File root = new File(path);
				File[] rootfiles = root.listFiles();

				if (rootfiles == null) {
					return null;
				}

				for (File rootItemFile : rootfiles) {
					if (rootItemFile.isDirectory()) {

						File[] catagoryfiles = rootItemFile.listFiles();

						for (File catagoryItemFile : catagoryfiles) {
							if (catagoryItemFile.isDirectory()) {

								File[] subfiles = catagoryItemFile.listFiles();
								for (File subItemFile : subfiles) {

									String realpath = rootItemFile.getName()
											+ "/" + catagoryItemFile.getName()
											+ "/" + subItemFile.getName();
									Log.e("UPLOAD", "" + realpath);
									FileInputStream fileInputStream = new FileInputStream(
											subItemFile);
									dropboxApi.putFile(realpath,
											fileInputStream,
											subItemFile.length(), null, null);
								}

							}
						}
					}

				}
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			} catch (DropboxException e) {
				e.printStackTrace();
			}

			return false;
		}

		@Override
		protected void onProgressUpdate(Long... values) {
			// TODO Auto-generated method stub
			mProgressHUD.setMessage("" + values[0]);
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (mProgressHUD.isShowing()) {

				mProgressHUD.dismiss();
			}

			if (result) {
				Toast.makeText(DropboxActivity.this,
						"File has been successfully uploaded!",
						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(
						DropboxActivity.this,
						"An error occured while processing the upload request.",
						Toast.LENGTH_LONG).show();
			}
		}
	}

	public class DownloadFiles extends AsyncTask<Void, Void, Boolean> {

		// private String root;
		ProgressHUD mProgressHUD;
		boolean shouldCreate = false;
		int limit = 0;

		// private String root = "/";

		public DownloadFiles(String path) {
			// this.root = path;

		}

		@Override
		protected void onPreExecute() {
			mProgressHUD = ProgressHUD.show(DropboxActivity.this,
					"Please wait...", true);
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(Void... params) {

			com.dropbox.client2.DropboxAPI.Entry dirent;
			try {

				dirent = dropboxApi.metadata("/", 1000, null, true, null);

				int i = 0;

				if (!isPurchased) {
					for (com.dropbox.client2.DropboxAPI.Entry rootEntry : dirent.contents) {
						if (rootEntry.isDir) {
							i++;
//							Log.e("DROPBOX", rootEntry.fileName());
						}
					}
					limit = i + listItems.size();

					if (limit > AppExtra.FOLDER_LIMIT) {
						return false;
					} else {
						
//						Log.e("limit", "" + limit);
						for (com.dropbox.client2.DropboxAPI.Entry rootEntry : dirent.contents) {

							if (rootEntry.isDir) {

								com.dropbox.client2.DropboxAPI.Entry rootItem = dropboxApi
										.metadata("/" + rootEntry.fileName(),
												1000, null, true, null);
								shouldCreate = fileManager.createFolder(
										DropboxActivity.this,
										rootEntry.fileName());

								for (com.dropbox.client2.DropboxAPI.Entry catagoryEntry : rootItem.contents) {
									if (catagoryEntry.isDir) {

										com.dropbox.client2.DropboxAPI.Entry catagoryItem = dropboxApi
												.metadata(
														"/"
																+ rootEntry
																		.fileName()
																+ "/"
																+ catagoryEntry
																		.fileName(),
														1000, null, true, null);

										for (com.dropbox.client2.DropboxAPI.Entry subEntry : catagoryItem.contents) {

											String path = rootEntry.fileName()
													+ "/"
													+ catagoryEntry.fileName();

											downloadDropboxFile(shouldCreate,
													path, subEntry);

											 Log.e("Name", "" +
											 rootEntry.fileName()
											 + "/" + catagoryEntry.fileName()
											 + "/" + subEntry.fileName());

										}
									}
								}
							}
						}
					}

				

				} else {

					for (com.dropbox.client2.DropboxAPI.Entry rootEntry : dirent.contents) {

						// downloadDropboxFile(ent);

						if (rootEntry.isDir) {

							com.dropbox.client2.DropboxAPI.Entry rootItem = dropboxApi
									.metadata("/" + rootEntry.fileName(), 1000,
											null, true, null);
							shouldCreate = fileManager.createFolder(
									DropboxActivity.this, rootEntry.fileName());

							for (com.dropbox.client2.DropboxAPI.Entry catagoryEntry : rootItem.contents) {
								if (catagoryEntry.isDir) {

									com.dropbox.client2.DropboxAPI.Entry catagoryItem = dropboxApi
											.metadata(
													"/"
															+ rootEntry
																	.fileName()
															+ "/"
															+ catagoryEntry
																	.fileName(),
													1000, null, true, null);

									for (com.dropbox.client2.DropboxAPI.Entry subEntry : catagoryItem.contents) {

										String path = rootEntry.fileName()
												+ "/"
												+ catagoryEntry.fileName();

										downloadDropboxFile(shouldCreate, path,
												subEntry);

										// Log.e("Name", "" +
										// rootEntry.fileName()
										// + "/" + catagoryEntry.fileName()
										// + "/" + subEntry.fileName());

									}
								}
							}
						}
					}
//					Log.e("DOWNLOAD", "DOwnload" );
				}

				return true;
			} catch (DropboxException e) {
				e.printStackTrace();
			}

			return false;

		}

		@Override
		protected void onProgressUpdate(Void... values) {
			mProgressHUD.setMessage("" + values[0].toString());
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (mProgressHUD.isShowing()) {

				mProgressHUD.dismiss();
			}

			if (result) {
				Toast.makeText(DropboxActivity.this,
						"File has been successfully downloaded!",
						Toast.LENGTH_LONG).show();
			} else {

				if (!isPurchased) {

					if (limit > AppExtra.FOLDER_LIMIT) {
						purchaseDialog();
					} else {
						Toast.makeText(
								DropboxActivity.this,
								"An error occured while processing the download request.",
								Toast.LENGTH_LONG).show();
					}
				} else {

					Toast.makeText(
							DropboxActivity.this,
							"An error occured while processing the download request.",
							Toast.LENGTH_LONG).show();

				}
			}

		}

	}

	private void purchaseDialog() {
		Builder alertDialog = new Builder(
				DropboxActivity.this);
		alertDialog.setTitle("Purchase Required!");
		alertDialog.setMessage(R.string.dropbox_purchase_dialog);
		alertDialog.setPositiveButton("YES",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						 mHelper.queryInventoryAsync(mGotInventoryListener);
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

	private void downloadDropboxFile(boolean shouldCreate, String path,
			Entry fileSelected) {

		File dir = new File(AppExtra.APP_ROOT_FOLDER);
		if (!dir.exists())
			dir.mkdirs();
		try {

			// if (!localFile.exists()) {

			if (shouldCreate) {
				File localFile = new File(dir + "/" + path + "/"
						+ fileSelected.fileName());
				localFile.createNewFile();
				download(fileSelected, localFile);
				// copy(fileSelected, localFile);
			}

			// } else {
			// showFileExitsDialog(fileSelected, localFile);
			// }
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean download(final Entry fileSelected, final File localFile) {
		File file = localFile;
		OutputStream out = null;
		boolean result = false;
		try {
			out = new BufferedOutputStream(new FileOutputStream(file));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			DropboxFileInfo info = dropboxApi.getFile(fileSelected.path, null,
					out, null);
			// Log.i("DbExampleLog", "The file's rev is: " +
			// info.getMetadata().rev);

			result = true;
		} catch (DropboxException e) {
			Log.e("DbExampleLog", "Something went wrong while downloading.");
			file.delete();
			result = false;
		}

		return result;
	}

	private void showFileExitsDialog(final Entry fileSelected,
			final File localFile) {
		Builder alertBuilder = new Builder(DropboxActivity.this);
		alertBuilder
				.setMessage("File name with this name already exists.Do you want to replace this file?");
		alertBuilder.setPositiveButton("Ok",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// copy(fileSelected, localFile);
					}
				});
		alertBuilder.setNegativeButton("Cancel", null);
		alertBuilder.create().show();

	}

	protected static final String TAG = null;
	private void initPurchase(){
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