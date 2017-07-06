package com.artifex.mupdfdemo;

import java.io.InputStream;
import java.util.concurrent.Executor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.smartmux.banglaebook.plus.R;
import com.smartmux.banglaebook.plus.util.Constant;
import com.smartmux.banglaebook.plus.util.EBookPreferenceUtils;

class ThreadPerTaskExecutor implements Executor {
	public void execute(Runnable r) {
		new Thread(r).start();
	}
}

public class MuPDFActivity extends Activity implements FilePicker.FilePickerSupport
{
	/* The core rendering instance */
	enum TopBarMode {Main, Search, Annot, Delete, More, Accept};
	enum AcceptMode {Highlight, Underline, StrikeOut, Ink, CopyText};

	private final int    OUTLINE_REQUEST=0;
	private final int    PRINT_REQUEST=1;
	private final int    FILEPICK_REQUEST=2;
	private MuPDFCore    core;
	private String       mFileName;
	private MuPDFReaderView mDocView;
	private TextView     mPageNumberView;
	private AlertDialog.Builder mAlertBuilder;
	private boolean mAlertsActive= false;
	private AsyncTask<Void,Void,MuPDFAlert> mAlertTask;
	private AlertDialog mAlertDialog;
	private FilePicker mFilePicker;
	public static int dPageMode = MuPDFCore.SINGLE_PAGE_MODE;
	private static int whiteColor = 0xffffffff;
	private static int blackColor = 0xff000000;
	public static int backGroundPage = whiteColor;
	private String mFilePath;
	
	public void createAlertWaiter() {
		mAlertsActive = true;
		// All mupdf library calls are performed on asynchronous tasks to avoid stalling
		// the UI. Some calls can lead to javascript-invoked requests to display an
		// alert dialog and collect a reply from the user. The task has to be blocked
		// until the user's reply is received. This method creates an asynchronous task,
		// the purpose of which is to wait of these requests and produce the dialog
		// in response, while leaving the core blocked. When the dialog receives the
		// user's response, it is sent to the core via replyToAlert, unblocking it.
		// Another alert-waiting task is then created to pick up the next alert.
		if (mAlertTask != null) {
			mAlertTask.cancel(true);
			mAlertTask = null;
		}
		if (mAlertDialog != null) {
			mAlertDialog.cancel();
			mAlertDialog = null;
		}
		mAlertTask = new AsyncTask<Void,Void,MuPDFAlert>() {

			@Override
			protected MuPDFAlert doInBackground(Void... arg0) {
				if (!mAlertsActive)
					return null;

				return core.waitForAlert();
			}

			@Override
			protected void onPostExecute(final MuPDFAlert result) {
				// core.waitForAlert may return null when shutting down
				if (result == null)
					return;
				final MuPDFAlert.ButtonPressed pressed[] = new MuPDFAlert.ButtonPressed[3];
				for(int i = 0; i < 3; i++)
					pressed[i] = MuPDFAlert.ButtonPressed.None;
				DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						mAlertDialog = null;
						if (mAlertsActive) {
							int index = 0;
							switch (which) {
							case AlertDialog.BUTTON1: index=0; break;
							case AlertDialog.BUTTON2: index=1; break;
							case AlertDialog.BUTTON3: index=2; break;
							}
							result.buttonPressed = pressed[index];
							// Send the user's response to the core, so that it can
							// continue processing.
							core.replyToAlert(result);
							// Create another alert-waiter to pick up the next alert.
							createAlertWaiter();
						}
					}
				};
				mAlertDialog = mAlertBuilder.create();
				mAlertDialog.setTitle(result.title);
				mAlertDialog.setMessage(result.message);
				switch (result.iconType)
				{
				case Error:
					break;
				case Warning:
					break;
				case Question:
					break;
				case Status:
					break;
				}
				switch (result.buttonGroupType)
				{
				case OkCancel:
					mAlertDialog.setButton(AlertDialog.BUTTON2, getString(R.string.cancel), listener);
					pressed[1] = MuPDFAlert.ButtonPressed.Cancel;
				case Ok:
					mAlertDialog.setButton(AlertDialog.BUTTON1, getString(R.string.okay), listener);
					pressed[0] = MuPDFAlert.ButtonPressed.Ok;
					break;
				case YesNoCancel:
					mAlertDialog.setButton(AlertDialog.BUTTON3, getString(R.string.cancel), listener);
					pressed[2] = MuPDFAlert.ButtonPressed.Cancel;
				case YesNo:
					mAlertDialog.setButton(AlertDialog.BUTTON1, getString(R.string.yes), listener);
					pressed[0] = MuPDFAlert.ButtonPressed.Yes;
					mAlertDialog.setButton(AlertDialog.BUTTON2, getString(R.string.no), listener);
					pressed[1] = MuPDFAlert.ButtonPressed.No;
					break;
				}
				mAlertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
					public void onCancel(DialogInterface dialog) {
						mAlertDialog = null;
						if (mAlertsActive) {
							result.buttonPressed = MuPDFAlert.ButtonPressed.None;
							core.replyToAlert(result);
							createAlertWaiter();
						}
					}
				});

				mAlertDialog.show();
			}
		};

		mAlertTask.executeOnExecutor(new ThreadPerTaskExecutor());
	}

	public void destroyAlertWaiter() {
		mAlertsActive = false;
		if (mAlertDialog != null) {
			mAlertDialog.cancel();
			mAlertDialog = null;
		}
		if (mAlertTask != null) {
			mAlertTask.cancel(true);
			mAlertTask = null;
		}
	}

	
	private MuPDFCore openBuffer(byte buffer[]) {
		System.out.println("Trying to open byte buffer");
		try {
			core = new MuPDFCore(this, buffer);
			// New file: drop the old outline data
			OutlineActivityData.set(null);
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
		return core;
	}
	
	private MuPDFCore openFile(String path)
	{
		int lastSlashPos = path.lastIndexOf('/');
		mFileName = new String(lastSlashPos == -1
					? path
					: path.substring(lastSlashPos+1));
		System.out.println("Trying to open "+path);
		try
		{
			core = new MuPDFCore(this, path);
			// New file: drop the old outline data
			OutlineActivityData.set(null);
		}
		catch (Exception e)
		{
			System.out.println(e);
			return null;
		}
		return core;
	}

	private MuPDFCore openBuffer(byte buffer[], String magic)
	{
		System.out.println("Trying to open byte buffer");
		try
		{
			core = new MuPDFCore(this, buffer);
			// New file: drop the old outline data
			OutlineActivityData.set(null);
		}
		catch (Exception e)
		{
			System.out.println(e);
			return null;
		}
		return core;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);

		mAlertBuilder = new AlertDialog.Builder(this);

		if (core == null) {
			// core = (MuPDFCore) getLastNonConfigurationInstance();

			if (savedInstanceState != null) {
				if (savedInstanceState.containsKey("FileName"))
					mFileName = savedInstanceState.getString("FileName");
				if (savedInstanceState.containsKey("FilePath"))
					mFilePath = savedInstanceState.getString("FilePath");
			}

		}
		if (core == null) {
			Intent intent = getIntent();
			byte buffer[] = null;
			if ("mupdf".equals(intent.getAction())) {
				Uri uri = intent.getData();
				if (uri.toString().startsWith("content://")) {
					String reason = null;
					try {
						InputStream is = getContentResolver().openInputStream(
								uri);
						int len = is.available();
						buffer = new byte[len];
						is.read(buffer, 0, len);
						is.close();
					} catch (OutOfMemoryError e) {
						System.out
								.println("Out of memory during buffer reading");
						reason = e.toString();
					} catch (Exception e) {
						System.out.println("Exception reading from stream: "
								+ e);

						// Handle view requests from the Transformer Prime's
						// file manager
						// Hopefully other file managers will use this same
						// scheme, if not
						// using explicit paths.
						// I'm hoping that this case below is no longer
						// needed...but it's
						// hard to test as the file manager seems to have
						// changed in 4.x.
						try {
							Cursor cursor = getContentResolver().query(uri,
									new String[] { "_data" }, null, null, null);
							if (cursor.moveToFirst()) {
								String str = cursor.getString(0);
								if (str == null) {
									reason = "Couldn't parse data in intent";
								} else {
									uri = Uri.parse(str);
								}
							}
						} catch (Exception e2) {
							System.out
									.println("Exception in Transformer Prime file manager code: "
											+ e2);
							reason = e2.toString();
						}
					}
					if (reason != null) {
						buffer = null;
						Resources res = getResources();
						AlertDialog alert = mAlertBuilder.create();
						setTitle(String
								.format(res
										.getString(R.string.cannot_open_document_Reason),
										reason));
						alert.setButton(AlertDialog.BUTTON_POSITIVE,
								getString(R.string.dismiss),
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										finish();
									}
								});
						alert.show();
						return;
					}
				}

				mFilePath = Uri.decode(uri.getEncodedPath());
				if (buffer != null) {
					core = openBuffer(buffer);
				} else {
					core = openFile(mFilePath);
				}

				SearchTaskResult.set(null);

				if (core != null && core.needsPassword()) {
					String password = getIntent().getExtras().getString(
							"password");
					System.out.println(password);
					// Toast.makeText(getApplicationContext(), password,
					// 200).show();
					core.authenticatePassword(password);
				}
			}
		}
		if (core == null) {
			AlertDialog alert = mAlertBuilder.create();
			alert.setTitle(R.string.cannot_open_document);
			alert.setButton(AlertDialog.BUTTON_POSITIVE,
					getString(R.string.dismiss),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					});
			alert.show();
			return;
		}

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		createUI(savedInstanceState);

		PackageManager m = getPackageManager();
		String s = getPackageName();
		try {
			PackageInfo p = m.getPackageInfo(s, 0);
			s = p.applicationInfo.dataDir;
		} catch (NameNotFoundException e) {
			Log.w("yourtag", "Error Package name not found ", e);
		}

	}
	
	
	
//	{
//		super.onCreate(savedInstanceState);
//
//		mAlertBuilder = new AlertDialog.Builder(this);
//
//		if (core == null) {
//			core = (MuPDFCore)getLastNonConfigurationInstance();
//
//			if (savedInstanceState != null && savedInstanceState.containsKey("FileName")) {
//				mFileName = savedInstanceState.getString("FileName");
//			}
//		}
//		
//		if (core == null) {
//			Intent intent = getIntent();
//			byte buffer[] = null;
//			if (Intent.ACTION_VIEW.equals(intent.getAction())) {
//				Uri uri = intent.getData();
//				System.out.println("URI to open is: " + uri);
//				if (uri.toString().startsWith("content://")) {
//					String reason = null;
//					try {
//						InputStream is = getContentResolver().openInputStream(uri);
//						int len = is.available();
//						buffer = new byte[len];
//						is.read(buffer, 0, len);
//						is.close();
//					}
//					catch (java.lang.OutOfMemoryError e) {
//						System.out.println("Out of memory during buffer reading");
//						reason = e.toString();
//					}
//					catch (Exception e) {
//						System.out.println("Exception reading from stream: " + e);
//
//						// Handle view requests from the Transformer Prime's file manager
//						// Hopefully other file managers will use this same scheme, if not
//						// using explicit paths.
//						// I'm hoping that this case below is no longer needed...but it's
//						// hard to test as the file manager seems to have changed in 4.x.
//						try {
//							Cursor cursor = getContentResolver().query(uri, new String[]{"_data"}, null, null, null);
//							if (cursor.moveToFirst()) {
//								String str = cursor.getString(0);
//								if (str == null) {
//									reason = "Couldn't parse data in intent";
//								}
//								else {
//									uri = Uri.parse(str);
//								}
//							}
//						}
//						catch (Exception e2) {
//							System.out.println("Exception in Transformer Prime file manager code: " + e2);
//							reason = e2.toString();
//						}
//					}
//					if (reason != null) {
//						buffer = null;
//						Resources res = getResources();
//						AlertDialog alert = mAlertBuilder.create();
//						setTitle(String.format(res.getString(R.string.cannot_open_document_Reason), reason));
//						alert.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.dismiss),
//								new DialogInterface.OnClickListener() {
//									public void onClick(DialogInterface dialog, int which) {
//										finish();
//									}
//								});
//						alert.show();
//						return;
//					}
//				}
//				if (buffer != null) {
//					core = openBuffer(buffer, intent.getType());
//				} else {
//					core = openFile(Uri.decode(uri.getEncodedPath()));
//				}
//				SearchTaskResult.set(null);
//			}
//			
//			if (core != null && core.countDocumentPages() == 0)
//			{
//				core = null;
//			}
//		}
//		if (core == null)
//		{
//			AlertDialog alert = mAlertBuilder.create();
//			alert.setTitle(R.string.cannot_open_document);
//			alert.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.dismiss),
//					new DialogInterface.OnClickListener() {
//						public void onClick(DialogInterface dialog, int which) {
//							finish();
//						}
//					});
//			alert.setOnCancelListener(new OnCancelListener() {
//
//				@Override
//				public void onCancel(DialogInterface dialog) {
//					finish();
//				}
//			});
//			alert.show();
//			return;
//		}
//
//		createUI(savedInstanceState);
//	}

	
	
	public void createUI(Bundle savedInstanceState) {
		if (core == null)
			return;
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.book_view);
		// Now create the UI.
		// First create the document view
		
		if (getIntent().getExtras() != null) {

			
			String banglaTitle = getIntent().getExtras().getString(Constant.TAG_BTITLE);
			TextView bookTitle = (TextView) findViewById(R.id.bookTitle);
			bookTitle.setText(banglaTitle);
		}
		mPageNumberView = (TextView)findViewById(R.id.book_pagenumber);
	
		mDocView = new MuPDFReaderView(this) {
			
			@Override
			protected void onMoveToChild(int i) {
				if (core == null)
					return;
				mPageNumberView.setText(String.format("%d / %d", i + 1,
						core.countDisplayPage()));
				//mPageSlider.setMax((core.countPages() - 1) * mPageSliderRes);
				//mPageSlider.setProgress(i * mPageSliderRes);
				super.onMoveToChild(i);
			}

		};

		RelativeLayout layout = (RelativeLayout) this.findViewById(R.id.bookcontent);
		layout.addView(mDocView);
		mDocView.setAdapter(new MuPDFPageAdapter(this, core));


		// Reenstate last state if it was recorded
		SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
		mDocView.setDisplayedViewIndex(prefs.getInt("page"+mFileName, 0));
		
		

		///if (savedInstanceState == null || !savedInstanceState.getBoolean("ButtonsHidden", false))
			//showButtons();

		//if(savedInstanceState != null && savedInstanceState.getBoolean("SearchMode", false))
		//	searchModeOn();

		//if(savedInstanceState != null && savedInstanceState.getBoolean("ReflowMode", false))
		//	reflowModeSet(true);

		// Stick the document view and the buttons overlay into a parent view
//		RelativeLayout layout = new RelativeLayout(this);
//		layout.addView(mDocView);
//		layout.addView(mButtonsView);
//		setContentView(layout);
	}
	
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case OUTLINE_REQUEST:
			if (resultCode >= 0)
				mDocView.setDisplayedViewIndex(resultCode);
			break;
		case PRINT_REQUEST:
			if (resultCode == RESULT_CANCELED)
				//showInfo(getString(R.string.print_failed));
			break;
		case FILEPICK_REQUEST:
			if (mFilePicker != null && resultCode == RESULT_OK)
				mFilePicker.onPick(data.getData());
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public Object onRetainNonConfigurationInstance()
	{
		MuPDFCore mycore = core;
		core = null;
		return mycore;
	}

	private void reflowModeSet(boolean reflow)
	{
		//mReflow = reflow;
		//mDocView.setAdapter(mReflow ? new MuPDFReflowAdapter(this, core) : new MuPDFPageAdapter(this, this, core));
		//mReflowButton.setColorFilter(mReflow ? Color.argb(0xFF, 172, 114, 37) : Color.argb(0xFF, 255, 255, 255));
		//setButtonEnabled(mAnnotButton, !reflow);
		//setButtonEnabled(mSearchButton, !reflow);
		//if (reflow) setLinkHighlight(false);
		//setButtonEnabled(mLinkButton, !reflow);
		//setButtonEnabled(mMoreButton, !reflow);
		//mDocView.refresh(mReflow);
	}

//	private void toggleReflow() {
//		reflowModeSet(!mReflow);
//		showInfo(mReflow ? getString(R.string.entering_reflow_mode) : getString(R.string.leaving_reflow_mode));
//	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		if (mFileName != null && mDocView != null) {
			outState.putString("FileName", mFileName);

			// Store current page in the prefs against the file name,
			// so that we can pick it up each time the file is loaded
			// Other info is needed only for screen-orientation change,
			// so it can go in the bundle
			SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
			SharedPreferences.Editor edit = prefs.edit();
			edit.putInt("page"+mFileName, mDocView.getDisplayedViewIndex());
			edit.commit();
		}

	//	if (!mButtonsVisible)
	//		outState.putBoolean("ButtonsHidden", true);

		//if (mTopBarMode == TopBarMode.Search)
		//	outState.putBoolean("SearchMode", true);

		//if (mReflow)
		//	outState.putBoolean("ReflowMode", true);
	}

	@Override
	protected void onPause() {
		super.onPause();

		//if (mSearchTask != null)
		//	mSearchTask.stop();

		if (mFileName != null && mDocView != null) {
			SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
			SharedPreferences.Editor edit = prefs.edit();
			edit.putInt("page"+mFileName, mDocView.getDisplayedViewIndex());
			edit.commit();
		}
	}

	public void onDestroy()
	{
		if (mDocView != null) {
			mDocView.applyToChildren(new ReaderView.ViewMapper() {
				void applyToView(View view) {
					((MuPDFView)view).releaseBitmaps();
				}
			});
		}
		if (core != null)
			core.onDestroy();
		if (mAlertTask != null) {
			mAlertTask.cancel(true);
			mAlertTask = null;
		}
		core = null;
		super.onDestroy();
	}


	@Override
	protected void onStart() {
		if (core != null)
		{
			core.startAlerts();
			createAlertWaiter();
		}

		super.onStart();
	}

	@Override
	protected void onStop() {
		if (core != null)
		{
			destroyAlertWaiter();
			core.stopAlerts();
		}

		super.onStop();
	}

	@Override
	public void onBackPressed() {
		
		EBookPreferenceUtils.saveIntPref(getApplicationContext(), Constant.PREF_NAME, mFileName, mDocView.getDisplayedViewIndex());
		if (core != null && core.hasChanges()) {
			DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					if (which == AlertDialog.BUTTON_POSITIVE)
						core.save();

					finish();
					//overridePendingTransition (R.anim.push_right_out, R.anim.push_right_in);
				}
			};
			AlertDialog alert = mAlertBuilder.create();
			alert.setTitle(getString(R.string.app_name));
			alert.setMessage(getString(R.string.document_has_changes_save_them_));
			alert.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.yes), listener);
			alert.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.no), listener);
			alert.show();
		} else {
			
			super.onBackPressed();
			//overridePendingTransition (R.anim.push_right_out, R.anim.push_right_in);
		}
	}

	@Override
	public void performPickFor(FilePicker picker) {
		mFilePicker = picker;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public int getDisplayPage(int documentPage) {
		if (core != null && core.getDoubleMode()) {
			if (core.getCoverPageMode()) {
				return (documentPage + 1) / 2;
			} else
				return documentPage / 2;
		} else
			return documentPage;
	}
	
}
