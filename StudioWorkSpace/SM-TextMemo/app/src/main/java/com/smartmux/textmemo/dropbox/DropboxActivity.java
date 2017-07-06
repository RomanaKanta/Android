package com.smartmux.textmemo.dropbox;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
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
import com.smartmux.textmemo.AppMainActivity;
import com.smartmux.textmemo.R;
import com.smartmux.textmemo.modelclass.NoteListItem;
import com.smartmux.textmemo.util.IabHelper;
import com.smartmux.textmemo.util.IabResult;
import com.smartmux.textmemo.util.Inventory;
import com.smartmux.textmemo.util.ProgressHUD;
import com.smartmux.textmemo.util.Purchase;
import com.smartmux.textmemo.utils.AppActionBar;
import com.smartmux.textmemo.utils.AppExtra;
import com.smartmux.textmemo.utils.AppToast;
import com.smartmux.textmemo.utils.FileManager;

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
	Typeface tf;
	private boolean isPurchased = false;
	public ArrayList<NoteListItem> listItems;

	private final static String DROPBOX_NAME = "dropbox_prefs";
	private final static String APP_KEY = "d73msltjvsroyvt";
	private final static String APP_SECRET = "op6zra855l70frt";

	private final static AccessType ACCESS_TYPE = AccessType.DROPBOX;

	// inAppPurchase
	IabHelper mHelper;
	IInAppBillingService mService;
	static final String ITEM_SKU = "com.smartmux.textmemo.upgrade";
	// static final String ITEM_SKU = "android.test.purchased";
	protected static final String TAG = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dropbox);

		AppActionBar.changeActionBarFont(getApplicationContext(),
				DropboxActivity.this);

		tf = Typeface.createFromAsset(getAssets(), AppExtra.AVENIRLSTD_BLACK);

		// AppActionBar.updateAppActionBar(getActionBar(), this, true);
		// getActionBar().setTitle(getString(R.string.dropbox));
		fileManager = new FileManager();

		isPurchased = fileManager.getPaidStatus(getApplicationContext());
		listItems = fileManager.getAllNotes(getApplicationContext());

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
		createCutomActionBarTitle(R.string.dropbox);

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

	private void createCutomActionBarTitle(int text) {
		this.getActionBar().setDisplayShowCustomEnabled(true);
		this.getActionBar().setDisplayShowTitleEnabled(false);
		this.getActionBar().setDisplayShowHomeEnabled(false);

		LayoutInflater inflator = LayoutInflater.from(this);
		View v = inflator.inflate(R.layout.custom_action_bar_sec, null);

		TextView title = (TextView) v.findViewById(R.id.textView_tit);
		title.setTypeface(tf);
		title.setText(text);

		ImageView logout = (ImageView) v.findViewById(R.id.imageView_save);
		logout.setImageResource(R.drawable.logout);

		if (isUserLoggedIn) {
			logout.setVisibility(View.VISIBLE);
		} else {
			logout.setVisibility(View.INVISIBLE);
		}

		logout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				dropboxApi.getSession().unlink();
				loggedIn(false);
			}
		});

		ImageView back = (ImageView) v.findViewById(R.id.imageView_back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				finish();
				overridePendingTransition(R.anim.push_down_in,
						R.anim.push_down_out);
			}
		});

		// assign the view to the actionbar
		this.getActionBar().setCustomView(v);
	}

	@Override
	protected void onResume() {
		super.onResume();

        listItems = fileManager.getAllNotes(getApplicationContext());

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
			dropbox_title.setText("Now Experience strage with Dropbox");

		} else {
			file_up_layer.setVisibility(View.GONE);
			login_layer.setVisibility(View.VISIBLE);
			dropbox_title.setText("Dropbox");

			// uploadFileBtn.setVisibility(View.GONE);
			// downloadFilesBtn.setVisibility(View.GONE);
			// login_text.setText("Log in");
			// loginBtn.setBackgroundResource(R.drawable.file_up_btn_bg);
		}

		createCutomActionBarTitle(R.string.dropbox);

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
		overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			finish();
			overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
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

					String realpath = rootItemFile.getName();

					// Log.e("UPLOAD", "" + realpath);

					FileInputStream fileInputStream = new FileInputStream(
							rootItemFile);
					dropboxApi.putFile(realpath, fileInputStream,
							rootItemFile.length(), null, null);
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

		ProgressHUD mProgressHUD;
		int limit = 0;

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
							continue;

						} else {
							i++;
//							Log.e("DROPBOX", rootEntry.fileName());
						}
					}

                    if(listItems != null) {

                        limit = i + listItems.size();
//                        Log.e("LIMIT", "" + i);

                        if (limit > AppExtra.TOTAL_TEXT_LIMIT) {
                            return false;
                        } else {

                            for (com.dropbox.client2.DropboxAPI.Entry rootEntry : dirent.contents) {
                                if (rootEntry.isDir) {
                                    continue;

                                } else {
                                    // Log.e("DOWNLOAD", "" + rootEntry.fileName());
                                    downloadDropboxFile(rootEntry);
                                }

                            }
                        }
                    }else{
                        AppToast.show(DropboxActivity.this, DropboxActivity.this.getString(R.string.dropbox_download_error));
                    }
				} else {
					for (com.dropbox.client2.DropboxAPI.Entry rootEntry : dirent.contents) {

						// Log.e("DOWNLOAD", "" + rootEntry.fileName());
						if (rootEntry.isDir) {
							continue;

						} else {
							downloadDropboxFile(rootEntry);
						}

					}
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

					if (limit > AppExtra.TOTAL_TEXT_LIMIT) {
						showPurchaseDialog();
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

	private void downloadDropboxFile(Entry fileSelected) {

		File dir = new File(AppExtra.APP_ROOT_FOLDER);
		if (!dir.exists())
			dir.mkdirs();
		try {

			File localFile = new File(dir + "/" + fileSelected.fileName());
			localFile.createNewFile();
			download(fileSelected, localFile);

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
			e1.printStackTrace();
		}
		try {
			DropboxFileInfo info = dropboxApi.getFile(fileSelected.path, null,
					out, null);

			result = true;
		} catch (DropboxException e) {
			Log.e("DbExampleLog", "Something went wrong while downloading.");
			file.delete();
			result = false;
		}

		return result;
	}

	protected void showPurchaseDialog() {

		// get prompts.xml view
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		View promptView = layoutInflater
				.inflate(R.layout.dialog_purchase, null);

		TextView msg = (TextView) promptView
				.findViewById(R.id.custom_dialog_msg);
		msg.setText(R.string.dropbox_purchase_dialog);

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

	private void initPurchase() {
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

		// if (requestCode == REQUEST_LINK_TO_DBX) {
		// if (resultCode == Activity.RESULT_OK) {
		// DropboxTask task = new DropboxTask();
		// task.execute();
		// } else {
		// Log.d("dropbox error",
		// "Link to Dropbox failed or was cancelled.");
		// }
		// }

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mHelper != null)
			mHelper.dispose();
		mHelper = null;

	}

}