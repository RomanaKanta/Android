package com.aircast.photobag.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aircast.photobag.R;
import com.aircast.photobag.api.PBAPIContant;
import com.aircast.photobag.api.PBAPIHelper;
import com.aircast.photobag.api.PBAPIHelper.ProgressManagerListener;
import com.aircast.photobag.application.PBApplication;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.database.PBDatabaseDefinition;
import com.aircast.photobag.database.PBDatabaseManager;
import com.aircast.photobag.gcm.ScreenReceiver;
import com.aircast.photobag.log.SdcardException;
import com.aircast.photobag.model.PBHistoryEntryModel;
import com.aircast.photobag.model.PBHistoryPhotoModel;
import com.aircast.photobag.utils.PBBitmapUtils;
import com.aircast.photobag.utils.PBGeneralUtils;
import com.aircast.photobag.utils.PBPreferenceUtils;
import com.aircast.photobag.widget.FButton;
import com.aircast.photobag.widget.actionbar.ActionBar;
import com.google.analytics.tracking.android.EasyTracker;

/**
 * Show downloading progress when downloading photos. <b>too much code</b>
 */
public class PBDownloadDownloadingActivity extends PBAbsActionBarActivity {

	private static final String TAG = "PBDownloadDownloadingActivity";

	private FButton mBtnDlDownloadingCancel;
	private FButton mBtnDlDownloadErrorCancel;
	private FButton mBtnDlRetry;
	private TextView mTvDownloadProgress;
	private ProgressBar mProgressBar;
	private ActionBar mHeaderBar;
	
	private static final int MSG_DOWNLOAD_DONE = 1000;
	private static final int MSG_DOWNLOAD_ERROR = 1001;
	private static final int MSG_SDCARD_INVALID = 1002;
	private static final int MSG_SDCARD_FULL = 1003;
	private static final int DIALOG_CANCEL_DOWNLOADING = 2000;
	private static final int DIALOG_SDCARD_MISSING = 2001;
	private static final int DIALOG_SDCARD_FULL = 2002;

	private static DownloadPhotoTask mDownloadTask;

	private RelativeLayout mFrameLayoutForDownloadScreen;
	private RelativeLayout mFrameLayoutForDownloadErrorScreen;

	private Animation mAnimPushLeft;
	private Animation mAnimPushRight;

	private boolean isFromKoukaibukuro;
	private BroadcastReceiver mReceiver = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pb_layout_download_downloading);

		mHeaderBar = (ActionBar) findViewById(R.id.headerbar);
		setHeader(mHeaderBar,
				getString(R.string.screen_title_dowload_downloading));

		mTvDownloadProgress = (TextView) findViewById(R.id.tv_downloading_status);
		mProgressBar = (ProgressBar) findViewById(R.id.progress_bar_downloading_progress);
		
		 // Need to setup flag again false for bug fix
		 PBPreferenceUtils.saveBoolPref(getApplicationContext(),
					PBConstant.PREF_NAME, PBConstant.PREF_NO_NEED_DELETE,
					false);
		
		
	   	// INITIALIZE RECEIVER for screen on/off
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter);
        
		

		// 20120314 added by NhatVT, reset textView in somecase <S>
		CharSequence restoreChar = (CharSequence) getLastNonConfigurationInstance();
		if (!TextUtils.isEmpty(restoreChar)) {
			mTvDownloadProgress
					.setText((CharSequence) getLastNonConfigurationInstance());
		} else {
			mTvDownloadProgress.setText("");
		}
		// 20120314 added by NhatVT, reset textView in somecase <S>

		// start asynctask for downloading photo list
		if (mDownloadTask == null
				|| (mDownloadTask != null && mDownloadTask.getStatus() != AsyncTask.Status.RUNNING)) {
			mDownloadTask = new DownloadPhotoTask(getApplicationContext(),
					mHandler);
			try {
				mDownloadTask.addViewsForUpdateDownloadProgress(
						mTvDownloadProgress, mProgressBar, mHandler);
				mDownloadTask.execute(0);
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
		} else {
			mDownloadTask.addViewsForUpdateDownloadProgress(
					mTvDownloadProgress, mProgressBar, mHandler);
		}

		mBtnDlDownloadingCancel = (FButton) findViewById(R.id.btn_dl_downloading_cancel);
		mBtnDlDownloadingCancel.setOnClickListener(mOnClickListener);

		mBtnDlDownloadErrorCancel = (FButton) findViewById(R.id.btn_dl_download_error_cancel);
		mBtnDlDownloadErrorCancel.setOnClickListener(mOnClickListener);

		mBtnDlRetry = (FButton) findViewById(R.id.btn_dl_downloading_retry);
		mBtnDlRetry.setOnClickListener(mOnClickListener);

		mFrameLayoutForDownloadScreen = (RelativeLayout) findViewById(R.id.layout_downloading);
		mFrameLayoutForDownloadErrorScreen = (RelativeLayout) findViewById(R.id.layout_downloading_error);

		mAnimPushLeft = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.slide_in_left_anim);
		mAnimPushRight = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.slide_in_right_anim);

		mFrameLayoutForDownloadScreen.setVisibility(View.VISIBLE);
		
		isFromKoukaibukuro = PBPreferenceUtils.getBoolPref(getApplicationContext(),
				PBConstant.PREF_NAME,
				PBConstant.PREF_PASSWORD_FROM_LIBRARY, false);
		 if(isFromKoukaibukuro){
			 
				mHeaderBar.setHomeLogo(R.drawable.openbag_actionbar_icon);
		 }
		 

	}

	@Override
	protected void onResume() {
		super.onResume();
		
		// V2 google analytics has been comment out 
		/*if (PBTabBarActivity.gaTracker != null) {
			PBTabBarActivity.gaTracker.trackPageView("PBDownloadDownloadingActivity");
		}*/
		
		
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public Object onRetainCustomNonConfigurationInstance() {
		return mTvDownloadProgress.getText();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
		
// 		AdstirTerminate.init(this);
	};
	
	
	//Added below activity life cycle method for Google analytics
	@Override
    public void onStart() {
 	    super.onStart();
	    System.out.println("Atik start Easy Tracker for PBDownloadDownloadingActivity");
	    EasyTracker.getInstance(this).activityStart(this);
    }
  
    //Added below activity life cycle method for Google analytics
    @Override
    public void onStop() {
	    super.onStop();
	    System.out.println("Atik stop Easy Tracker for PBDownloadDownloadingActivity");
	    EasyTracker.getInstance(this).activityStop(this);
    }

	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_DOWNLOAD_DONE:
				
				PBConstant.doUpdate = true;
				if (isUpdate()){
					// atik code commented
					removeUpdatedBag();
					
					Intent intent = new Intent(PBDownloadDownloadingActivity.this,
							PBMainTabBarActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.putExtra(PBConstant.TAB_SET_BY_TAG, PBConstant.HISTORY_TAG);
					startActivity(intent);
				}
				Intent intent = new Intent(PBDownloadDownloadingActivity.this,
						PBDisplayCompleteActivity.class);
				intent.putExtra(PBConstant.START_DOWNLOAD_COMPLETE, true);
				startActivity(intent);
				finish();
				
				boolean isAppBacground = PBGeneralUtils.isApplicationBroughtToBackground(getApplicationContext());
				if(isAppBacground) {
					
				
					
					String password = PBPreferenceUtils
							.getStringPref(getBaseContext(), PBConstant.PREF_NAME,
									PBConstant.PREF_DL_PHOTOLIST_PASS, "");
					//String message = password+"ダウンロードを完了しました。"; // Need password name
					String message = "『"+password+getResources().getString(R.string.password_notification_local_download); // Need password name

					generateNotificationForAdvertise(getApplicationContext(),message);
					
					
					
					
				} else  {
					
					
					
					if (/*!screenOn*/!ScreenReceiver.wasScreenOn) {
						//System.out.println("Atik insert into screen off mode and send push");
					    // Screen is still on, so do your thing here
						String password = PBPreferenceUtils
								.getStringPref(getBaseContext(), PBConstant.PREF_NAME,
										PBConstant.PREF_DL_PHOTOLIST_PASS, "");
						//String message = password+"ダウンロードを完了しました。"; // Need password name
						String message = "『"+password+getResources().getString(R.string.password_notification_local_download); // Need password name
						generateNotificationForAdvertise(getApplicationContext(),message);
						ScreenReceiver.wasScreenOn = true;
						
					}
						  
				}
				break;

			case MSG_DOWNLOAD_ERROR:
				showDownloadErrorLayout();
				break;

			case MSG_SDCARD_INVALID: // 20120306 added by NhatVT, support show
										// sdcard missing
				cancelOfDownloadingTask();
				showDialog(DIALOG_SDCARD_MISSING);
				break;
			case MSG_SDCARD_FULL:
				
				cancelOfDownloadingTask();
				showDialog(DIALOG_SDCARD_FULL);
				break;
			default:
				// no default case
				break;
			}
		};
	};
	
	private boolean isUpdate(){
		
		//
		
		// Atik this below code block is no long necessary for new implem
		long oldBagId = getIntent().getLongExtra(PBConstant.DOWNLOAD_UPDATE_OLD_BAG_ID, -1);
		String collectionId = getIntent().getStringExtra(PBConstant.COLLECTION_ID);
		
		boolean isExist = false;
		boolean isExistInbox= false;
		if(collectionId !=null) {
			String histroyID = null;
			String password = null;
			PBDatabaseManager dbMgr = PBDatabaseManager
					.getInstance(PBApplication.getBaseApplicationContext());
			Cursor cursor = dbMgr.getHistoriesCursor(PBDatabaseDefinition.HISTORY_SENT);
			if (cursor.moveToFirst()){
				   while(!cursor.isAfterLast()){
					  String PASSWORD = cursor.getString(cursor.getColumnIndex(PBDatabaseDefinition.HistoryData.C_COLECTION_ID));
					  if(PASSWORD!=null) {
					  if(collectionId.equalsIgnoreCase(PASSWORD)) {
					    	  //collection_ID = cursor.getString(cursor.getColumnIndex(PBDatabaseDefinition.HistoryData.C_COLECTION_ID));
					    	   isExist  = true;

							   histroyID = cursor.getString(cursor.getColumnIndex(PBDatabaseDefinition.HistoryData.C_ID));
							   password = cursor.getString(cursor.getColumnIndex(PBDatabaseDefinition.HistoryData.C_PASSWORD));
					      }
					  }
				      cursor.moveToNext();
				   }
			}
		    cursor.close();
		    
			PBDatabaseManager dbMgrInbox = PBDatabaseManager
					.getInstance(PBApplication.getBaseApplicationContext());
			Cursor cursorInbox = dbMgrInbox.getHistoriesCursor(PBDatabaseDefinition.HISTORY_INBOX);
			if (cursorInbox.moveToFirst()){
				   while(!cursorInbox.isAfterLast()){
					  String PASSWORD = cursorInbox.getString(cursorInbox.getColumnIndex(PBDatabaseDefinition.HistoryData.C_COLECTION_ID));
					  if(PASSWORD!=null) {
					  if(collectionId.equalsIgnoreCase(PASSWORD)) {
					    	  //collection_ID = cursor.getString(cursor.getColumnIndex(PBDatabaseDefinition.HistoryData.C_COLECTION_ID));
						  isExistInbox  = true;

					      }
					  }
					  cursorInbox.moveToNext();
				   }
			}
			cursorInbox.close();
			
		    
			if(isExist || isExistInbox) {
				  PBPreferenceUtils.saveBoolPref(getApplicationContext(),
							PBConstant.PREF_NAME, PBConstant.PREF_NO_NEED_DELETE,
							true);
				
			} else {
				  PBPreferenceUtils.saveBoolPref(getApplicationContext(),
							PBConstant.PREF_NAME, PBConstant.PREF_NO_NEED_DELETE,
							false);
			}
		}
		
		return (oldBagId == -1 || collectionId == null) ? false : true;
		//return false;
	}
	
	// remove corresponding old bag if this downloading is update of it
	private boolean removeUpdatedBag(){
		long oldBagId = getIntent().getLongExtra(PBConstant.DOWNLOAD_UPDATE_OLD_BAG_ID, -1);
		String collectionId = getIntent().getStringExtra(PBConstant.COLLECTION_ID);
		if (oldBagId == -1 || collectionId == null){
			return false;
		}
		
        if(PBPreferenceUtils.getBoolPref(
        		PBApplication.getBaseApplicationContext(),
        		PBConstant.PREF_NAME, 
        		PBConstant.PREF_NO_NEED_DELETE,
        		false)) { 
    		
			PBPreferenceUtils.saveBoolPref(getApplicationContext(),
					PBConstant.PREF_NAME, PBConstant.PREF_NO_NEED_DELETE,
					false);
        	
        } else {
			//deleteRemovedPhoto(jsonPhotosArray, dbMgr);
			PBDatabaseManager.getInstance(getBaseContext()).deleteHistory(
					String.valueOf(oldBagId), collectionId);
        }
		

		return true;
	}

	private void showDownloadErrorLayout() {
		// setup display info
		String mCurrentProgressTxt = mTvDownloadProgress.getText().toString();
		((TextView) mFrameLayoutForDownloadErrorScreen
				.findViewById(R.id.tv_download_error_status))
				.setText(mCurrentProgressTxt);
		int mCurrentProgress = mProgressBar.getProgress();
		((ProgressBar) mFrameLayoutForDownloadErrorScreen
				.findViewById(R.id.progress_bar_download_error_progress))
				.setProgress(mCurrentProgress);
		// update title text
		mHeaderBar.setTitle(R.string.screen_title_dowload_downloading_error);
		// show download error screen
		mFrameLayoutForDownloadErrorScreen.setVisibility(View.VISIBLE);
		mFrameLayoutForDownloadErrorScreen.startAnimation(mAnimPushLeft);
		mFrameLayoutForDownloadScreen.setVisibility(View.GONE);
	}

	private void showDownloadingLayout() {
		if (mDownloadTask != null
				&& (mDownloadTask.getStatus() == AsyncTask.Status.PENDING || mDownloadTask
						.getStatus() == AsyncTask.Status.FINISHED)) {

			mDownloadTask = new DownloadPhotoTask(getApplicationContext(),
					mHandler);
			int resumeDownloadPos = PBPreferenceUtils.getIntPref(
					PBApplication.getBaseApplicationContext(),
					PBConstant.PREF_NAME,
					PBConstant.PREF_RESUME_CURRENT_DOWNLOAD_POS, 0);
			int resumeProgressPos = PBPreferenceUtils.getIntPref(
					PBApplication.getBaseApplicationContext(),
					PBConstant.PREF_NAME,
					PBConstant.PREF_RESUME_CURRENT_PROGRESS, 0);
			mDownloadTask.addViewsForUpdateDownloadProgress(
					mTvDownloadProgress, mProgressBar, mHandler);
			mProgressBar.setProgress(resumeProgressPos);
			mDownloadTask.execute(resumeDownloadPos);
		}
		// update title text
		mHeaderBar.setTitle(R.string.screen_title_dowload_downloading);
		// show download layout again.
		mFrameLayoutForDownloadScreen.setVisibility(View.VISIBLE);
		mFrameLayoutForDownloadScreen.startAnimation(mAnimPushRight);
		mFrameLayoutForDownloadErrorScreen.setVisibility(View.GONE);
	}

	@Override
	public boolean dispatchKeyEvent(android.view.KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			showDialog(DIALOG_CANCEL_DOWNLOADING);
			return true;
		}
		return super.dispatchKeyEvent(event);
	};

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_CANCEL_DOWNLOADING:
			return new AlertDialog.Builder(this)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle(R.string.dl_alert_confirm_cancel_download_title)
					.setMessage(
							getString(R.string.dl_alert_confirm_cancel_download_msg))
					.setPositiveButton(R.string.dialog_ok_btn,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									
									// Bug fix history download screen
									// When cancel need to false this flag
									PBPreferenceUtils.saveBoolPref(getApplicationContext(),
											PBConstant.PREF_NAME, PBConstant.PREF_NO_NEED_DELETE,
											false);
									
									cancelOfDownloadingTask();
									finish();
									return;
								}
							})
					.setNegativeButton(R.string.dialog_cancel_btn,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									return;
								}
							}).create();

		case DIALOG_SDCARD_MISSING:
			return new AlertDialog.Builder(PBDownloadDownloadingActivity.this)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle(R.string.dl_alert_sdcard_missing_title)
					.setMessage(getString(R.string.dl_alert_sdcard_missing_msg))
					.setCancelable(false)
					.setPositiveButton(R.string.dialog_ok_btn,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									finish();
									return;
								}
							}).create();
		case DIALOG_SDCARD_FULL:
			return new AlertDialog.Builder(PBDownloadDownloadingActivity.this)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle(R.string.dl_alert_sdcard_full_title)
			.setMessage(getString(R.string.dl_alert_sdcard_full_msg))
			.setCancelable(false)
			.setPositiveButton(R.string.dialog_ok_btn,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int which) {
							finish();
							return;
						}
					}).create();
		default:
			// do nothing in this case
			break;
		}
		return null;
	}

	private void cancelOfDownloadingTask() {
		if (mDownloadTask != null
				&& mDownloadTask.getStatus() == AsyncTask.Status.RUNNING) {
			mDownloadTask.cancel(true);
			mDownloadTask = null;
		}
	}

	private OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {

			case R.id.btn_dl_download_error_cancel:
			case R.id.btn_dl_downloading_cancel:
				showDialog(DIALOG_CANCEL_DOWNLOADING);
				break;

			case R.id.btn_dl_downloading_retry:
				showDownloadingLayout();
				break;

			default:
				// do nothing.
				break;
			}
		}
	};

	/**
	 * Support class for saving photo from server!
	 * 
	 * @author NhatVT
	 */
	private class DownloadPhotoTask extends AsyncTask<Integer, Integer, Void> {
		/** result json return from server */
		private String mJsonPhotoList = "";
		/** password using to download collection info */
		private String mPhotoListPassword = "";
		/** token key of device */
		private String mTokenKey = "";
		/** the context */
		private Context mContext;
		/** the handle using update UI */
		private Handler mHandler;
		/** total photo of collection */
		private int mTotalPhotos = 0;
		/** variable for checking download photo task. */
		private boolean mIsTaskWasCanceled = false;
		/** Current download progress percent. */
		private int mCurrentProgress = 0;

		private TextView mDownloadProgressTextView;
		private ProgressBar mDownloadProgressBar;

		private DownloadPhotoTask mCurrentTask;

		private int mCurrentLevelOfProgressBar = 0;

		/**
		 * Constructer for this helper class.
		 * 
		 * @param context
		 *            Context.
		 * @param handler
		 *            Handler for handling Message.
		 */
		public DownloadPhotoTask(Context context, Handler handler) {
			this.mContext = context;
			this.mHandler = handler;
			this.mCurrentTask = this;
		}

		public void addViewsForUpdateDownloadProgress(TextView downloadText,
				ProgressBar downloadProgress, Handler handler) {
			mDownloadProgressTextView = downloadText;
			mDownloadProgressBar = downloadProgress;
			if (handler != null) {
				this.mHandler = handler;
			}
			if (this.mDownloadProgressBar != null) {
				this.mDownloadProgressBar
						.setProgress(mCurrentLevelOfProgressBar);
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Log.d(TAG, "onPreExecute " + 0);

			mIsTaskWasCanceled = false;
			mCurrentProgress = 0;

			mJsonPhotoList = PBPreferenceUtils.getStringPref(this.mContext,
					PBConstant.PREF_NAME,
					PBConstant.PREF_DL_PHOTOLIST_JSON_DATA, "");
			mPhotoListPassword = PBPreferenceUtils
					.getStringPref(this.mContext, PBConstant.PREF_NAME,
							PBConstant.PREF_DL_PHOTOLIST_PASS, "");
			mTokenKey = PBPreferenceUtils.getStringPref(
					getApplicationContext(), PBConstant.PREF_NAME,
					PBConstant.PREF_NAME_TOKEN, "");
			
			if(PBAPIContant.DEBUG){
				
				Log.d(TAG, "mJsonPhotoList " + mJsonPhotoList);
			}

			JSONObject jsonRoot;
			try {
				jsonRoot = new JSONObject(mJsonPhotoList);
				JSONArray jsonPhotosArray;
				jsonPhotosArray = jsonRoot.getJSONArray("photos");
				mTotalPhotos = jsonPhotosArray.length();
				if (/* mProgressBar */this.mDownloadProgressBar != null) {
					if (mTotalPhotos > 25) {
						/* mProgressBar */this.mDownloadProgressBar
								.setMax(mTotalPhotos);
					}
				}
				int currentDownloadPos = PBPreferenceUtils.getIntPref(
						PBApplication.getBaseApplicationContext(),
						PBConstant.PREF_NAME,
						PBConstant.PREF_RESUME_CURRENT_DOWNLOAD_POS, 0);
				if (this.mDownloadProgressTextView != null) {
					/* mTvDownloadProgress */this.mDownloadProgressTextView
							.setText(getString(
									R.string.dl_downloading_process_text,
									currentDownloadPos, mTotalPhotos));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}

		@Override
		protected Void doInBackground(Integer... currentPosOfDownloadPhoto) {
			String deviceUUID = PBPreferenceUtils
			         .getStringPref(PBApplication
			           .getBaseApplicationContext(),
			           PBConstant.PREF_NAME,
			           PBConstant.PREF_NAME_UID, "0");
			int counter = 0;
			JSONObject jsonRoot;
		    try {
				ArrayList<String> localStorageFileList = new ArrayList<String>();
				localStorageFileList = PBGeneralUtils.getAllCacheFileFromStorage();
				jsonRoot = new JSONObject(mJsonPhotoList);
				JSONArray jsonPhotosArray;
				jsonPhotosArray = jsonRoot.getJSONArray("photos");
				//System.out.println("Inside doInBackground:"+Arrays.toString(currentPosOfDownloadPhoto));
				ArrayList<String> stringArray = new ArrayList<String>();
				for(int i = 0, count = jsonPhotosArray.length(); i< count; i++)
				{
				        JSONObject jsonObject = jsonPhotosArray.getJSONObject(i);
				        String imageURL = jsonObject.optString("url").toString();
						System.out.println("Photo URL atik:"+imageURL);
				        String thumbURL = jsonObject.optString("thumb").toString();
						System.out.println("Thumb URL atik:"+thumbURL);
						
						//Atik count number of exists pictures from server to history 
						
						
						PBDatabaseManager dbMgr = PBDatabaseManager
								.getInstance(PBApplication.getBaseApplicationContext());
						Cursor cursor = dbMgr.getHistoriesCursor(PBDatabaseDefinition.HISTORY_INBOX);
						//System.out.println("Atik number of Inbox cursor is:"+cursor.getCount());
						String collection_Id = null;
						if (cursor.moveToFirst()){
							   while(!cursor.isAfterLast()){
								  String PASSWORD = cursor.getString(cursor.getColumnIndex(PBDatabaseDefinition.HistoryData.C_PASSWORD));
							      if(mPhotoListPassword.equalsIgnoreCase(PASSWORD)) {
							    	  collection_Id = cursor.getString(cursor.getColumnIndex(PBDatabaseDefinition.HistoryData.C_COLECTION_ID));
							      }
							      
							      cursor.moveToNext();
							   }
							}
						
						if (cursor != null && !cursor.isClosed())
							cursor.close();	
					  
					  
					  if(collection_Id != null) {
						  ArrayList<PBHistoryPhotoModel> mLocalPhotos = dbMgr.getPhotos(collection_Id);
						  for (int j = 0; j < mLocalPhotos.size(); j++) {
								PBHistoryPhotoModel modelLocal = mLocalPhotos.get(j);
								
								if(modelLocal.getPhoto().equalsIgnoreCase(imageURL) ||
										modelLocal.getThumb().equalsIgnoreCase(thumbURL)) {
									counter ++;
								}
						  }						  
					  }

			        
					  System.out.println("Atik number of Inbox exist counter value  is:"+counter);
					  
					  
				 } 
				
	        	  
		    } catch (Exception e) {
		    	
				        e.printStackTrace();
		    }

			parsePhotoCollectionInfo(mJsonPhotoList,
					currentPosOfDownloadPhoto[0]/* this.mResumeDownloadPostion */,counter);
			return null;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			mCurrentLevelOfProgressBar = 0;
			// remove all cache file if this task was cancelled.
			// mIsTaskWasCanceled = true;
			Log.d("mapp", "Download photo task was canceled!");
			// reset current photo position
			savingCurrentDownloadInfo(0, 0);
		}

		// private int progressWillBeShown = 0;

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			if (values[0] > 0) {
				if (this.mDownloadProgressTextView != null) {
					/* mTvDownloadProgress */this.mDownloadProgressTextView
							.setText(getString(
									R.string.dl_downloading_process_text,
									values[0], mTotalPhotos));
				}
			}

			if (values[1] > 0 && values[2] > 0) {
				int percent = 0;
				if (mTotalPhotos > 25) {
					if (values[1] == 100) {
						if (/* mProgressBar */this.mDownloadProgressBar != null) {
							this.mDownloadProgressBar.incrementProgressBy(1);
							mCurrentLevelOfProgressBar = mDownloadProgressBar
									.getProgress();
						}
					}
				} else {
					percent = values[1] / (mTotalPhotos/*
														 * - this.
														 * mResumeDownloadPostion
														 */);
					if (percent >= mCurrentProgress) {
						if (/* mProgressBar */this.mDownloadProgressBar != null) {
							this.mDownloadProgressBar
									.incrementProgressBy(percent
											- mCurrentProgress);
							mCurrentProgress = percent;
							mCurrentLevelOfProgressBar = mDownloadProgressBar
									.getProgress();
						}
					} else {
						if (/* mProgressBar */this.mDownloadProgressBar != null) {
							this.mDownloadProgressBar
									.incrementProgressBy(percent);
							mCurrentProgress = percent;
							mCurrentLevelOfProgressBar = mDownloadProgressBar
									.getProgress();
						}
					}
				}
			}
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			Log.d(TAG, "onPostExecute " + 0);
			// saving all data to db and change to download complete screen.
			if (mDownloadSuccess) {
				mCurrentLevelOfProgressBar = 0;
				if (this.mHandler != null) {
					this.mHandler.sendEmptyMessage(MSG_DOWNLOAD_DONE);
				}
				// reset current photo position
				savingCurrentDownloadInfo(0, 0);
			} else {
				if (this.mHandler != null) {
					this.mHandler.sendEmptyMessage(MSG_DOWNLOAD_ERROR);
				}
			}
		}

		private ProgressManagerListener mDownloadProgressManagerListener = new ProgressManagerListener() {
			@Override
			public void updateProgressStatus(int percent) {
				if (!mIsTaskWasCanceled) {
					// Log.d("mapp", "[photo] downloaded: " + percent + "%");
					publishProgress(-1, percent, mPhotoIdx);
				}
			}

			@Override
			public boolean cancelDownloadTask() {
				if (mCurrentTask.isCancelled())
					return true;
				return false;
			}
		};

		private int mPhotoIdx = 0;
		private boolean mDownloadSuccess = false;
		
		// add 20121112 
		/**
		 * Delete photos exist in database and sdcard but not in jsonlist.
		 * 
		 * @param jsonPhotosArray
		 *            Photo list got From server.
		 */
		private void deleteRemovedPhoto(JSONArray jsonPhotosArray, PBDatabaseManager dbManager){
			
			String collectionId = getIntent().getStringExtra(PBConstant.COLLECTION_ID);			
			if (collectionId == null) {
				return;
			}
			
			ArrayList<PBHistoryPhotoModel> mLocalPhotos = dbManager.getPhotos(collectionId);
			if(mLocalPhotos.size() == 0) {
				return;
			}
			
			boolean mIsRemoved;
			
			for (int i = 0; i < mLocalPhotos.size(); i++) {
				mIsRemoved = true;
				PBHistoryPhotoModel modelLocal = mLocalPhotos.get(i);
				
				for (int j = 0; j < jsonPhotosArray.length(); j++) {
					try {

						if (modelLocal.getPhoto().equals(jsonPhotosArray.getJSONObject(j).getString("url"))) {
							mIsRemoved = false;
							break;
						}
						
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				
				if (mIsRemoved) {
					dbManager.deleteSinglePhoto(modelLocal.getPhoto());
				}
			}
		}

		/**
		 * Util for parsing JSON string.
		 * 
		 * @param jsonString
		 *            JSON string want to parse.
		 * @param counter 
		 */
		private void parsePhotoCollectionInfo(String jsonString,
				int startDownloadPos, int counter) {
			
//			System.out.println("Atik enter for photo download for honey");
//			System.out.println("Atik total exist image " + counter);
			if (TextUtils.isEmpty(jsonString)) {
				return;
			}

			boolean mIsCanSaveDataToDB = true;
			FileInputStream fis = null;

			PBDatabaseManager dbMgr = PBDatabaseManager
					.getInstance(PBApplication.getBaseApplicationContext());
			Log.d(TAG, "Start download photo from server HERE");
			// parse JSON data
			try {
				JSONObject jsonRoot = new JSONObject(jsonString);

				String historyId = null;
				String photoUrl = null;
				String thumb = null;
				int mediaType = 0;
				long createDate = 0;
				long chargeDate = 0;
				int addibility = 0;
				long updatedAt = 0;
				int saveMark = 1;
				int numberDownlaod = 1;
				int localSave = 0;
				String adLink = "";
				mPhotoIdx = 1;
				String thumpForChecking;
				String photoUrlForChecking;
				int numberOfExistImage=0;
				int numberOfUpdatedImage=0;
				int isPublic  = 0;
				int accepted = 0;

				if (jsonRoot != null && jsonRoot.has("id")) {
					historyId = jsonRoot.getString("id");
				}
				if (jsonRoot.has("created_at")) {
					createDate = jsonRoot.getLong("created_at");
				}
				if (jsonRoot.has("charges_at")) {
					chargeDate = jsonRoot.getLong("charges_at");
				}
				if (jsonRoot.has("downloaded_users_count")) {
					numberDownlaod = jsonRoot.getInt("downloaded_users_count");
				}
				if (jsonRoot.has("can_add")) {
					addibility = jsonRoot.getInt("can_add");
				}
				if (jsonRoot.has("updated_at")) {
					updatedAt = jsonRoot.getLong("updated_at");
				}
				if (jsonRoot.has("can_save")) {
					saveMark = jsonRoot.getInt("can_save");
				}
				if (jsonRoot.has("client_keep_days")) {
					localSave = jsonRoot.getInt("client_keep_days");
				}
				if (jsonRoot.has("last_photo_link")) {
					adLink = jsonRoot.getString("last_photo_link");
				}
				if (jsonRoot.has("is_public")) {
					isPublic = jsonRoot.getInt("is_public");
				}
				if (jsonRoot.has("accepted")) {
					accepted = jsonRoot.getInt("accepted");
				}

				
				//Log.d("DDMA", jsonRoot.toString());
				// parse "photos" tag
				JSONArray jsonPhotosArray = jsonRoot.getJSONArray("photos");
				int numOfPhoto = jsonPhotosArray.length();
				Integer.toString(numOfPhoto);
				int photoDownloadedCount = PBPreferenceUtils.getIntPref(
						PBApplication.getBaseApplicationContext(),
						PBConstant.PREF_NAME,
						PBConstant.PREF_RESUME_CURRENT_DOWNLOAD_POS, 0);
				if(counter > 0) {

					photoDownloadedCount = counter;
				}
				
				boolean saveImgThumbResult = false;
				boolean saveRealImgResult = false;
				String mDefaultPhotoListThumbUrl = jsonPhotosArray
						.getJSONObject(0).getString("thumb");
				
				// add 20121112 check deleted photos
				//Atik code commented
				if (isUpdate()) {
	                if(PBPreferenceUtils.getBoolPref(
	                		PBApplication.getBaseApplicationContext(),
	                		PBConstant.PREF_NAME, 
	                		PBConstant.PREF_NO_NEED_DELETE,
	                		false)) { 
	                	
	                } else {

						deleteRemovedPhoto(jsonPhotosArray, dbMgr);

	                }
				} 

				Options mOptions = new Options();
				if (photoDownloadedCount > 0) {
				//	System.out.println("Atik photo download count" + photoDownloadedCount );
					startDownloadPos = photoDownloadedCount;
				}
				if (startDownloadPos > numOfPhoto || startDownloadPos < 0) {
					startDownloadPos = 0;
				}
				
				new ArrayList<String>();
				PBGeneralUtils.getAllCacheFileFromStorage();
				
			    boolean isPasswordExistOrNotInIbox  = false; 	
			    isPasswordExistOrNotInIbox  =  dbMgr.isPasswordExistsInHistoryInbox(historyId);
			    
			    boolean isPasswordExistOrNotInSent  = false; 	
			    isPasswordExistOrNotInSent  =  dbMgr.isPasswordExistsInHistorySent(historyId);
				
				for (int i = startDownloadPos; i < numOfPhoto; i++) {
					if (this.isCancelled()) {
						//Log.d("mapp", ">>> CANCEL CURRENT DOWNLOAD TASK!");
						mIsTaskWasCanceled = true;
						return;
					}
				//	Log.w("mapp", ">>> start down pos:" + i);
					JSONObject jsonObject = jsonPhotosArray.getJSONObject(i);
					
					// Added Atik code
					thumpForChecking = jsonObject.getString("thumb");
				//	System.out.println("Thumb URL atik:"+thumpForChecking);
					
					photoUrlForChecking = jsonObject.getString("url");
				//	System.out.println("Photo URL atik:"+photoUrlForChecking);
					
					PBGeneralUtils
							.getPathFromCacheFolder(saveMark != 0 ? photoUrlForChecking : photoUrlForChecking + "?can_save=0");
					
					
				  boolean isFileExistOrNot = false;
				  if(isPasswordExistOrNotInIbox ||
						  isPasswordExistOrNotInSent) {
					  
					  ArrayList<PBHistoryPhotoModel> mLocalPhotos = dbMgr.getPhotos(historyId);
					  for (int j = 0; j < mLocalPhotos.size(); j++) {
							PBHistoryPhotoModel modelLocal = mLocalPhotos.get(j);
							
							if(modelLocal.getPhoto().equalsIgnoreCase(photoUrlForChecking) ||
									modelLocal.getThumb().equalsIgnoreCase(thumpForChecking)) {
								isFileExistOrNot = true;
							}
					  }
				  }
				  

					
				  //  Atik skip download when photo or video already exists
			      if(/*!localStorageFileList.contains(realImgPathForCheecking)*/!isFileExistOrNot) {
						// parse "thumb" for image thumb
						if (jsonObject != null && jsonObject.has("thumb")) {
							thumb = jsonObject.getString("thumb");
							try {
								saveImgThumbResult = PBAPIHelper.savePhoto(
										this.mTokenKey, thumb,
										this.mPhotoListPassword, 
										(saveMark != 0),
										null);
								

							} catch (Exception e) {
								Log.w("mapp", "[download] getting thumb from server error: "
												+ e.getMessage());
							}
							Log.i("mapp", "save image thumb OK --> "
									+ saveImgThumbResult);
						}
						
						// parse "url" for real image
						if (jsonObject.has("url")) {
							photoUrl = jsonObject.getString("url");
							// 20120521 add support download format with video file
							// <S>
							if (photoUrl.contains("video")) {
								photoUrl = photoUrl + PBConstant.VIDEO_FORMAT_3GP;
							}
							try {
								saveRealImgResult = PBAPIHelper.savePhoto(
										this.mTokenKey, photoUrl,
										this.mPhotoListPassword,
										(saveMark != 0),
										this.mDownloadProgressManagerListener);
								if (mDownloadProgressManagerListener
										.cancelDownloadTask()) {
									mIsCanSaveDataToDB = false;
								}
							} catch (IOException e) {
								mDownloadSuccess = false;
								// 20120608 added by NhatVT, check sdcard in this
								// case <S>
								boolean isSdcardValid = PBGeneralUtils.checkSdcard(
										PBDownloadDownloadingActivity.this, false,
										null);
								if (!isSdcardValid) {
									mIsCanSaveDataToDB = false;
									if (this.mHandler != null) {
										this.mHandler.sendEmptyMessage(MSG_SDCARD_INVALID);
									}
									return;
								}							
								// 20120608 added by NhatVT, check sdcard in this
								// case <E>
								boolean isSdcardNotFull = PBGeneralUtils.checkExternalStorageFull(
										PBDownloadDownloadingActivity.this, null);
								if (!isSdcardNotFull) {
									mIsCanSaveDataToDB = false;
									if (this.mHandler != null) {
										this.mHandler.sendEmptyMessage(MSG_SDCARD_FULL);
									}
									return;
								}
								
								/*createRestorePoint(photoDownloadedCount,
										mTotalPhotos); // Atik code modified */
								System.out.println("Atik create restore point value " + photoDownloadedCount+counter );
								createRestorePoint(photoDownloadedCount,
										mTotalPhotos);
								
								mIsCanSaveDataToDB = false;
								if (this.mHandler != null) {
									this.mHandler.sendEmptyMessage(MSG_DOWNLOAD_ERROR);
								}
								return;
							} catch (SdcardException e) {
								mIsCanSaveDataToDB = false;
								if (this.mHandler != null) {
									this.mHandler.sendEmptyMessage(MSG_SDCARD_INVALID);
								}
								return;
							}
//							Log.i("mapp", "save real image OK --> "
//									+ saveRealImgResult);
							if (!saveRealImgResult) {
								// sdcard full don't throw error.
								boolean isSdcardNotFull = PBGeneralUtils.checkExternalStorageFull(
										PBDownloadDownloadingActivity.this, null);
								if (!isSdcardNotFull) {
									mIsCanSaveDataToDB = false;
									if (this.mHandler != null) {
										this.mHandler.sendEmptyMessage(MSG_SDCARD_FULL);
									}
									return;
								}
								
								mIsCanSaveDataToDB = false;
								mDownloadSuccess = false;

								/*createRestorePoint(photoDownloadedCount,
										mTotalPhotos);*/
								System.out.println("Atik create restore point value 1 " + photoDownloadedCount+counter );
								createRestorePoint(photoDownloadedCount,
										mTotalPhotos);

								if (this.mHandler != null) {
									this.mHandler.sendEmptyMessage(MSG_DOWNLOAD_ERROR);
								}
								return;
							}
						}
						
						// 20120809 added by NhatVT, support saving photo or video
						// <S>
						// if (jsonObject.has("media")) { change API
						if (!TextUtils.isEmpty(photoUrl)
								&& photoUrl.contains("video")) {
							mediaType = PBDatabaseDefinition.MEDIA_VIDEO;
						} else { // default in this case is a photo.
							mediaType = PBDatabaseDefinition.MEDIA_PHOTO;
						}
						// 20120809 added by NhatVT, support saving photo or video
						// <E>

						// 20120215 add by NhatVT <S>
						// if we cannot get thumb from server -> crop real image to
						// 150x150
						// 20120419 moved realImgPath from if clause by TinhNH1 <S>
						String realImgPath = PBGeneralUtils
								.getPathFromCacheFolder(saveMark != 0 ? photoUrl : photoUrl + "?can_save=0");
						
						
						if (!saveImgThumbResult && mediaType == PBDatabaseDefinition.MEDIA_PHOTO) {
							Log.i("mapp", ">>> process crop real image to create thumb!");
							String thumbImgPath = PBGeneralUtils
									.getPathFromCacheFolder(thumb);
							if (PBBitmapUtils.isPhotoValid(realImgPath)) {
								mOptions.inSampleSize = PBBitmapUtils
										.sampleSizeNeeded(
												realImgPath,
												PBConstant.MAXIMUM_IMAGE_SIZE_UPLOAD);
								Bitmap bmp = PBBitmapUtils.centerCropImage(
										BitmapFactory.decodeFile(realImgPath,
												mOptions),
										PBConstant.PHOTO_THUMB_WIDTH,
										PBConstant.PHOTO_THUMB_HEIGHT);
								try {
									FileOutputStream fos = null;
									if (android.os.Environment
											.getExternalStorageState()
											.equals(android.os.Environment.MEDIA_MOUNTED)) {
										fos = new FileOutputStream(new File(
												thumbImgPath));
									} else {
										mIsCanSaveDataToDB = false;
										if (this.mHandler != null) {
											this.mHandler.sendEmptyMessage(MSG_SDCARD_INVALID);
										}
										return;
									}
									bmp.compress(PBConstant.COMPRESS_FORMAT,
											PBConstant.DECODE_COMPRESS_PRECENT, fos);
									// release resources after saving photo DONE!
									if (fos != null) {
										fos.flush();
										fos.close();
										fos = null;
									}
									if (bmp != null) {
										bmp.recycle();
										bmp = null;
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							} else {
								Log.w(TAG, "cannot create thumb from photo, photo is invalid!");
							}
							// 20120215 add by NhatVT <S>
						}
						
						if (mIsCanSaveDataToDB && (!this.isCancelled())) {
							long duration = 0;
							if (mediaType == PBDatabaseDefinition.MEDIA_VIDEO) {
								try {
									File media = new File(realImgPath);
						            fis = new FileInputStream(media);
									MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
									mediaMetadataRetriever.setDataSource(fis.getFD());
									
									duration = Long.parseLong(mediaMetadataRetriever.extractMetadata(
											MediaMetadataRetriever.METADATA_KEY_DURATION));
									Bitmap thumb_video;
									thumb_video = ThumbnailUtils.createVideoThumbnail(
													realImgPath,
													MediaStore.Video.Thumbnails.MINI_KIND);

									if (thumb_video != null) {
										Log.i("mapp",
												"> downloader > process saving video frame thumb! "
														+ thumb_video.getWidth()
														+ "x"
														+ thumb_video.getHeight());
										boolean savingResult = PBBitmapUtils
												.saveBitmapToSdcard(thumb_video,
														photoUrl, true, true, true);
										Log.i("mapp",
												"> downloader > process saving video frame thumb! "
														+ savingResult);
									} else {
										Log.e("mapp", "> downloader > CAN NOT extract video thumbnail");
									}
								} catch (Exception e) {
									Log.e(TAG,
											"Cannot get video information, cause: "
													+ e.toString());
								}
								// 20120427 only get video frame when this file is a
								// video file <E>
							}
							// 20120416 <E>
							photoUrl = saveMark != 0 ? photoUrl : photoUrl + "?can_save=0";
							thumb = saveMark != 0 ? thumb : thumb + "?can_save=0";
							dbMgr.setPhoto(new PBHistoryPhotoModel(photoUrl,
									thumb, historyId, mediaType, 
									mediaType == PBDatabaseDefinition.MEDIA_VIDEO?duration:0),
									PBDatabaseDefinition.HISTORY_INBOX);
							
							
							
						}
						
					
						
						//photoDownloadedCount
						photoDownloadedCount++;
						publishProgress(photoDownloadedCount, -1);

						mPhotoIdx++;
						
						
			        } else {
						photoDownloadedCount++;
						//publishProgress(photoDownloadedCount, -1);
						publishProgress(photoDownloadedCount, -1);

						mPhotoIdx++;
			        }
					
				}			

				if (mIsCanSaveDataToDB && (!this.isCancelled())) {
					mDefaultPhotoListThumbUrl = saveMark != 0 
							? mDefaultPhotoListThumbUrl 
							: mDefaultPhotoListThumbUrl + "?can_save=0";
					PBHistoryEntryModel entry = new PBHistoryEntryModel(
							System.currentTimeMillis(), historyId,
							mPhotoListPassword, createDate, chargeDate,
							numOfPhoto, numberDownlaod, mDefaultPhotoListThumbUrl, addibility, 
							updatedAt, saveMark, localSave,isPublic,accepted);
					
				
					
					entry.setEntryAdLink(adLink);
					
					
					entry.setFourDigit(PBPreferenceUtils.getStringPref(getApplicationContext(),
			                 PBConstant.UPLOAD_SERVICE_PREF,
			                 PBConstant.PREF_FOUR_DIGIT, "12"));
			        if(PBPreferenceUtils.getBoolPref(
			        		PBApplication.getBaseApplicationContext(),
			        		PBConstant.PREF_NAME, 
			        		PBConstant.PREF_NO_NEED_DELETE,
			        		false)) { 
			        	
//			        	String password = entry.getEntryPassword();
//						  if(dbMgr.isPasswordExistsInHistoryInbox(entry.getCollectionId())){
//							  
//							  password = dbMgr.getPassord(entry.getCollectionId(), PBDatabaseDefinition.HISTORY_INBOX);
//						  }
//						  
//						  if(dbMgr.isPasswordExistsInHistorySent(entry.getCollectionId())){
//							  
//							  password = dbMgr.getPassord(entry.getCollectionId(), PBDatabaseDefinition.HISTORY_SENT);
//						  }
//						  entry.setEntryPassword(password);
						dbMgr.setHistory(
								entry, PBDatabaseDefinition.HISTORY_INBOX);
			        } else {
			        	
			        	//do this for password numbering
			        	int dublicateCounter = 0;
						Cursor cursor = dbMgr.getHistoriesCursor(PBDatabaseDefinition.HISTORY_INBOX);
						//
						
						String currentCollectionId = entry.getCollectionId();
						if(PBAPIContant.DEBUG){
							
							System.out.println("Atik number of Inbox cursor is:"+cursor.getCount());
							System.out.println("currentCollectionId:"+currentCollectionId);
						}
						if (cursor.moveToFirst()){
							   while(!cursor.isAfterLast()){
								  String collection_Id = cursor.getString(cursor.getColumnIndex(PBDatabaseDefinition.HistoryData.C_COLECTION_ID));
								  String password = cursor.getString(cursor.getColumnIndex(PBDatabaseDefinition.HistoryData.C_PASSWORD));
								  System.out.println("collection_Id:"+collection_Id);
								  if(!collection_Id.equalsIgnoreCase(currentCollectionId) && password.equalsIgnoreCase(entry.getEntryPassword())) {
							    	  dublicateCounter++;
							      }
							      
							      cursor.moveToNext();
							   }
							}
						
						if(dublicateCounter > 0){
							
							String tmpPassword = entry.getEntryPassword();
							entry.setEntryPassword(tmpPassword+" ("+dublicateCounter+")");
						}
						if(PBAPIContant.DEBUG){
							
							System.out.println(dublicateCounter);
							System.out.println(entry.getEntryPassword());
						}
						
						dbMgr.insertHistory(entry, PBDatabaseDefinition.HISTORY_INBOX);

			        }
			        PBPreferenceUtils.saveStringPref(getApplicationContext(),
			                 PBConstant.UPLOAD_SERVICE_PREF,
			                 PBConstant.PREF_FOUR_DIGIT, "12");
					
				

				}
				mDownloadSuccess = true;
				

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try { 
					if(fis!=null) {
						fis.close();
					}
					fis = null;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		/**
		 * Method for saving downloaded info to pref.
		 * 
		 * @param photoDownloadedCount
		 * @param progressBarPos
		 */
		private void savingCurrentDownloadInfo(int photoDownloadedCount,
				int progressBarPos) {
			// save current photo position
			PBPreferenceUtils.saveIntPref(
					PBApplication.getBaseApplicationContext(),
					PBConstant.PREF_NAME,
					PBConstant.PREF_RESUME_CURRENT_DOWNLOAD_POS,
					photoDownloadedCount);
			// save current progress dialog pos
			PBPreferenceUtils.saveIntPref(
					PBApplication.getBaseApplicationContext(),
					PBConstant.PREF_NAME,
					PBConstant.PREF_RESUME_CURRENT_PROGRESS, progressBarPos);
		}

		/**
		 * Method for saving information for display resume download info on
		 * screen.
		 * 
		 * @param photoDownloadedCount
		 * @param totalPhoto
		 */
		private void createRestorePoint(int photoDownloadedCount, int totalPhoto) {
			int progressPos = 0;
			if (mTotalPhotos > 25) {
				progressPos = photoDownloadedCount - 1;
			} else {
				if (totalPhoto > 0) {
					progressPos = (int) (photoDownloadedCount * (100 / totalPhoto));
				}
			}
			savingCurrentDownloadInfo(photoDownloadedCount, progressPos);
		}

	}

	@Override
	protected void handleHomeActionListener() {
		showDialog(DIALOG_CANCEL_DOWNLOADING);
	}

	@Override
	protected void setLeftBtnsDetailHeader(ActionBar headerBar) {
		// do nothing in this case!
	}
	
	/**
	 * Issues a notification to inform the user that server has sent a message.
	 */
	private static void generateNotificationForAdvertise(final Context context, final String message) {
		
		ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
		List<RunningTaskInfo> taskInfo = am.getRunningTasks(1);
		ComponentName componentInfo = taskInfo.get(0).topActivity;
		
		final String title = context.getString(R.string.pb_app_name);
		
		@SuppressWarnings("rawtypes")
		Class topClass = null;
		if(componentInfo.getPackageName().equalsIgnoreCase(PBConstant.PREF_NAME)){
		    //Activity in foreground, broadcast intent
			topClass = componentInfo.getClass();
		} 
		else{
		    //Activity Not Running,so Generate Notification
			topClass = PBSplashScreenActivity.class;
		}
		
		Handler h = new Handler(Looper.getMainLooper());
        h.post(new Runnable(){
        	public void run() {
        		Toast.makeText(context, title+""+message, Toast.LENGTH_LONG).show();
            }
        });
        
        int icon_small 	 = R.drawable.icon_small_for_push;
        
		//int largeIcon = R.drawable.a_itember_acron;
		Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_small_test);
		//Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_small);

        Intent notificationIntent = new Intent(context, topClass);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

		Builder builder = new NotificationCompat.Builder(context);
		builder.setTicker(message);
		builder.setContentTitle(title);
		builder.setContentText(message);
		builder.setContentInfo("info");
		builder.setWhen(System.currentTimeMillis());
		builder.setContentIntent(pendingIntent);
		builder.setSmallIcon(icon_small);
		builder.setLargeIcon(largeIcon);
		Notification notification = builder.build();

		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.defaults |= Notification.DEFAULT_LIGHTS;
		notification.vibrate = new long[]{0,200,100,50,100,50,100,200}; 

		NotificationManager notificationManager = (NotificationManager)
			context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(2, notification);
	}
}
