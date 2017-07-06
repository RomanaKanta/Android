package com.aircast.photobag.services;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.aircast.photobag.R;
import com.aircast.photobag.activity.PBSplashScreenActivity;
import com.aircast.photobag.activity.PBUploadSelectAddibilityActivity;
import com.aircast.photobag.activity.UploadingActivity;
import com.aircast.photobag.api.PBAPIHelper;
import com.aircast.photobag.api.ResponseHandle;
import com.aircast.photobag.api.PBAPIHelper.ProgressManagerListener;
import com.aircast.photobag.api.ResponseHandle.Response;
import com.aircast.photobag.application.PBApplication;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.database.PBDatabaseDefinition;
import com.aircast.photobag.database.PBDatabaseManager;
import com.aircast.photobag.gcm.ScreenReceiverForService;
import com.aircast.photobag.model.PBHistoryEntryModel;
import com.aircast.photobag.utils.PBBitmapUtils;
import com.aircast.photobag.utils.PBGeneralUtils;
import com.aircast.photobag.utils.PBPreferenceUtils;
import com.aircast.photobag.utils.PBProgress;
import com.aircast.photobag.utils.PBVideoUtils;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

/**
 * This class is a service that do upload photo to server in the background
 * 
 * @author FPT-HaiVT
 * 
 */
public class UploadService extends Service {
	private final String TAG = "UPLOAD_SERVICE";
	private static ArrayList<String> mLocalMediaList;
	private static long[] mLocalMediaTypeList;
	private static ArrayList<MediaInfo> mUploadUrlList;
	private String mToken;
	// private String mPhotoListPassword;
	private static Boolean mIsStop = false;
	private long mChargeDate = 0;
	private static int mUploadedPhotoNum = -1;
	private/* static */double time;

	// private String mCollectionId;
	// Unique Identification Number for the Notification.
	// We use it on Notification start, and to cancel it.
	// private int NOTIFICATION = R.string.local_service_started;
	private static PBVideoUtils pbVideoUtils;
	public static ServiceUpdateUIListener mUiUpdateListener;
	private BroadcastReceiver mReceiver = null;
	private boolean screenOff = false;
	private static FFmpeg ffmpeg;
	private String uploadAnalyticsData = "";

	public static void finishUpload(String collectionId, String password) {

	}

	/**
	 * Set an update UI listener for the upload service to show it what to do
	 * 
	 * @param l
	 *            a ServiceUpdateUIListenter that do update UI as user expected
	 */
	public static void setUpdateListener(ServiceUpdateUIListener l) {
		mUiUpdateListener = l;
	}

	/**
	 * This function is call form the activity that start service to notify that
	 * user has cancel upload process
	 */
	public static void stopService() {
		Log.d(PBConstant.TAG, "Upload service stop!");
		mIsStop = true;

		if (mUploadTask != null
				&& mUploadTask.getStatus() == AsyncTask.Status.RUNNING) {
			mUploadTask.cancel(true);
			mUploadTask = null;
		}
		// stop compress task if any
		if (mCompressTask != null
				&& mCompressTask.getStatus() == AsyncTask.Status.RUNNING) {
			mCompressTask.cancel(true);
			mCompressTask.doWhenCancelTask(true);
			mCompressTask.releaseVideoCompression();
			mCompressTask = null;
		}
		
		 if (ffmpeg != null) {
			 boolean isKill = ffmpeg.killRunningProcesses();
			 if (isKill) {
			 ffmpeg = null;
			
			 }else{
				 
				 ffmpeg.killRunningProcesses();
			 }
			
			 }
	}

	/**
	 * Class for clients to access. Because we know this service always runs in
	 * the same process as its clients, we don't need to deal with IPC.
	 */
	public class LocalBinder extends Binder {
		public UploadService getService() {
			return UploadService.this;
		}
	}

	// public void initData(ArrayList<String> photoList){
	// if(mUploadPhotoList == null){
	// mUploadPhotoList = new ArrayList<String>();
	// }
	// mUploadPhotoList.addAll(photoList);
	// }
	@Override
	public void onCreate() {

		// REGISTER RECEIVER THAT HANDLES SCREEN ON AND SCREEN OFF LOGIC
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		mReceiver = new ScreenReceiverForService();
		registerReceiver(mReceiver, filter);

	}

	public static int getUploadedPhotoNum() {
		return mUploadedPhotoNum;
	}

	public static int getSelectedVideoNum() {

		int totalFile = mLocalMediaList.size();
		int totalVideoChoose = 0;
		// get total video file will be uploaded
		for (int i = 0; i < totalFile; i++) {
			if (mLocalMediaTypeList[i] != -1) {

				totalVideoChoose++;
			}
		}
		return totalVideoChoose;
	}

	/**
	 * call this function to upload photo in mUploadPhotoList to server
	 * 
	 * @param mLocalMediaList
	 */
	private void doUpload(ArrayList<String> mLocalMediaList) {
		// new UploadTask().execute();
		// stopSelf();
		if (mUploadedPhotoNum != COUNT_UPLOAD_DEFAULT) { // this condition will
															// not be true
			if (mUploadTask == null
					|| (mUploadTask != null && mUploadTask.getStatus() != AsyncTask.Status.RUNNING)) {
				if (mLocalMediaList != null) {
					mUploadTask = new UploadTask(mLocalMediaList);
					mUploadTask.execute();
				}
			} else {
				Log.e(PBConstant.TAG, ">>> CAN NOT START UPLOAD TASK!!!");
			}
		} else {
			if (mCompressTask == null
					|| (mCompressTask != null && mCompressTask.getStatus() != AsyncTask.Status.RUNNING)) {
				if (mLocalMediaList != null) {
					mCompressTask = new CompressTask(mLocalMediaList);
					mCompressTask.execute();
				} else {
					Log.e(PBConstant.TAG,
							">>> CAN NOT START COMPRESS TASK!!! Media list is EMPTY!!!");
				}
			} else {
				Log.e(PBConstant.TAG, ">>> CAN NOT START COMPRESS TASK!!!");
			}
		}
		// 20120503 mod by NhatVT, check for video compress first and upload
		// later <E>
	}

	private static UploadTask mUploadTask;
	private static CompressTask mCompressTask; // 20120502 add for support
												// compress video function!
	public static final int COUNT_UPLOAD_DEFAULT = -1;

	

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		ffmpeg = FFmpeg.getInstance(UploadService.this);
		loadFFMpegBinary();
		
		mToken = PBPreferenceUtils.getStringPref(getBaseContext(),
				PBConstant.PREF_NAME, PBConstant.PREF_NAME_TOKEN, "");
		mIsStop = false;
		if (intent != null) {

			// Atik screen on/off check within Service
			screenOff = intent.getBooleanExtra("screen_state", false);
			if (screenOff) {

			} else { // Normal flow
				if (intent.hasExtra(PBConstant.INTENT_UPLOADED_PHOTO_NUM)) {
					mUploadedPhotoNum = intent.getIntExtra(
							PBConstant.INTENT_UPLOADED_PHOTO_NUM,
							COUNT_UPLOAD_DEFAULT); // default = -1, #0
				}
				// when resuming from an uncompleted upload
				if (mUploadedPhotoNum == COUNT_UPLOAD_DEFAULT
						&& mUploadUrlList != null) {
					mUploadUrlList.clear();
				}

				mToken = PBPreferenceUtils.getStringPref(getBaseContext(),
						PBConstant.PREF_NAME, PBConstant.PREF_NAME_TOKEN, "");
				// mPhotoListPassword =
				// intent.getStringExtra(PBConstant.INTENT_PASSWORD);
				Log.i("LocalService", "Received start id " + startId + ": "
						+ intent);

				// 20120508 not update mLocalMediaList when not start from
				// SelectMultipleImageActivity <S>
				if (intent.getBooleanExtra(
						PBConstant.INTENT_START_SERVICE_FROM_SELECT_IMG, false)) {
					if (mLocalMediaList != null) {
						mLocalMediaList.clear();
					}
					mLocalMediaList = intent
							.getStringArrayListExtra(PBConstant.INTENT_SELECTED_MEDIA);
					mLocalMediaTypeList = intent
							.getLongArrayExtra(PBConstant.INTENT_SELECTED_MEDIA_TYPE);

					for (int i = 0; i < mLocalMediaList.size(); i++) {

						long duration = -1;
						String mediaPath = mLocalMediaList.get(i);

						if (PBGeneralUtils.checkVideoIsValid(mediaPath)) {
							PBVideoUtils mVideoUtils = new PBVideoUtils();
							duration = mVideoUtils
									.getDuration(mediaPath);
						}

						mLocalMediaTypeList[i] = duration;

					}
				}
				// 20120508 not update mLocalMediaList when not start from
				// SelectMultipleImageActivity <E>

				if (mLocalMediaList == null) {
					mLocalMediaList = new ArrayList<String>();
				}

				Log.w("mapp",
						">>> onStartCommand >>>>>>>>>> mLocalMediaList size = "
								+ mLocalMediaList.size());
				if (mLocalMediaTypeList == null) {
					mLocalMediaTypeList = new long[10];
				}
				if (mUploadUrlList == null) {
					mUploadUrlList = new ArrayList<MediaInfo>();
				}
				// mCollectionId =
				// intent.getStringExtra(PBConstant.INTENT_PASSWORD_ID);
				doUpload(mLocalMediaList);
			}
			/*
			 * if (screenOff) { // YOUR CODE String password =
			 * PBPreferenceUtils.getStringPref( getBaseContext(),
			 * PBConstant.UPLOAD_SERVICE_PREF, PBConstant.PREF_UPLOAD_PASS, "");
			 * //String message = password+"アップロードを完了しました。"; // Need password
			 * name String message =
			 * "『"+password+getResources().getString(R.string
			 * .password_notification_local_upload); // Need password name
			 * 
			 * generateNotificationForAdvertise(getApplicationContext(),message);
			 * 
			 * }
			 */

		} else {
			if (mUiUpdateListener != null) {
				mUiUpdateListener.updateUI(PBConstant.UPLOAD_ERROR,
						mUploadUrlList);
			}
		}
		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		// Cancel the persistent notification.
		// mNM.cancel(NOTIFICATION);
		/*
		 * if(mUploadPhotoList != null){ mUploadPhotoList.clear(); }
		 * if(mPhotoUrlList != null){ mPhotoUrlList.clear(); }
		 */

		// Tell the user we stopped.
		mIsStop = true;
		Log.d(TAG, "Upload service stop!");

		// Unregister broadcast receiver for screen on/off
		if (mReceiver != null) {
			unregisterReceiver(mReceiver);
			mReceiver = null;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	// This is the object that receives interactions from clients. See
	// RemoteService for a more complete example.
	private final IBinder mBinder = new LocalBinder();

	/**
	 * Implement this interface to update UI everytime an image is uploaded
	 */
	public interface ServiceUpdateUIListener {

		public void updateUI(int uploadedPhotos,
				ArrayList<MediaInfo> uploadedUrlPhoto);

		public void updateProgressBar(int percent);

		public void updateCompressBar(int totalFile, int percent);

		public void onUploadForbidden();
		public void updateAnalytics(String data);
	}// end interface ServiceUpdateUIListener

	/**
	 * String array for tracking video compress temp file.
	 */
	private String[] mCachedVideoPath;

	private void deleteCachedVideoFile(String[] videoList) {
		if (videoList == null) {
			return;
		}
		int count = videoList.length;
		File fileTemp;
		for (int i = 0; i < count; i++) {
			if (!TextUtils.isEmpty(videoList[i])) {
				fileTemp = new File(videoList[i]);
				if (fileTemp.exists()) {
					try {
						Log.i("mapp", "delete cached file: " + videoList[i]);
						fileTemp.delete();
					} catch (SecurityException e) {
						e.printStackTrace();
					}
				}
			}
		}
		// reset mCachedVideoPath if any
		if (mCachedVideoPath != null) {
			mCachedVideoPath = null;
		}
	}

	/**
	 * AsyncTask for compressing video
	 * 
	 * @author NhatVT
	 */
	private class CompressTask extends AsyncTask<String[], Integer, Boolean> {

		/**
		 * list of file for uploading.
		 */
		private ArrayList<String> mListFilePath;
		private int totalVideoChoose = 0;
		private PBProgress mPbProgress;

		Map<Integer,String> map = new HashMap<Integer,String>();
		Pattern timePattern = Pattern.compile("(?<=time=)[\\d:.]*");
		long aduration;
		int pecentOfCompress;
		int numberOfFilesCompress = 0;
		String filepath = "";
		String cachedVideoPath = "";
		List<Integer> videoIndexValues = new ArrayList<Integer>();
		long startTime;
		long endTime;

		// private CompressTask mTask;

		public CompressTask(ArrayList<String> medialist) {
			super();
			// this.mTask = this;
			this.mListFilePath = medialist;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			PBConstant.COMPRESSORUPDLOAD = false;
			// Atik modified for bug fix ...Video progress message displayed
			// during photo selection
			if (mUiUpdateListener != null) {
				System.out
						.println("Atik called UPLOAD_PROCESS_COMPRESS_VIDEO from upload service 5");
				mUiUpdateListener.updateUI(
						PBConstant.UPLOAD_PROCESS_COMPRESS_VIDEO, null);
			}

			if (pbVideoUtils == null) {
				pbVideoUtils = new PBVideoUtils();
			}
		}

	

		@Override
		protected Boolean doInBackground(String[]... params) {
			if (mListFilePath == null) {
				return false;
			}
			if (mLocalMediaTypeList == null) {
				return false;
			}

			int totalFile = mListFilePath.size();

			// get total video file will be uploaded
			for (int i = 0; i < totalFile; i++) {
				if (mLocalMediaTypeList[i] != -1) {
					totalVideoChoose++;
					
					///new modification
					videoIndexValues.add(i);
					map.put(i, mListFilePath.get(i));
				}
			}

			// Atik need to send total number of selected video
			// UploadingActivity
			// Atik modified for bug fix ...Video progress message displayed
			// during photo selection
			/* if(totalVideoChoose > 0 ) { */

			System.out.println("Atik number of video" + totalVideoChoose);
			if (mUiUpdateListener != null) {
				System.out
						.println("Atik called UPLOAD_PROCESS_COMPRESS_VIDEO from upload service 4");

				mUiUpdateListener.updateUI(
						PBConstant.UPLOAD_PROCESS_COMPRESS_VIDEO_DONE, null);
			}
			// }

			// start upload
			String collectionExId = PBPreferenceUtils.getStringPref(
					getBaseContext(), PBConstant.UPLOAD_SERVICE_PREF,
					PBConstant.PREF_COLLECTION_EX_ID, "");
			
			if (!TextUtils.isEmpty(collectionExId)) {
				PBPreferenceUtils.saveStringPref(getBaseContext(),
						PBConstant.UPLOAD_SERVICE_PREF,
						PBConstant.PREF_COLLECTION_ID, collectionExId);

				PBPreferenceUtils.removePref(getBaseContext(),
						PBConstant.UPLOAD_SERVICE_PREF,
						PBConstant.PREF_COLLECTION_EX_ID);
			} else {
				Response response;
				String collectionId = PBPreferenceUtils.getStringPref(
						getBaseContext(), PBConstant.UPLOAD_SERVICE_PREF,
						PBConstant.PREF_COLLECTION_ID, "");
				if (!TextUtils.isEmpty(collectionId)) {
					String password = PBPreferenceUtils.getStringPref(
							getBaseContext(), PBConstant.UPLOAD_SERVICE_PREF,
							PBConstant.PREF_UPLOAD_PASS, "");
					response = PBAPIHelper.startAdding(mToken, collectionId,
							password);
				} else {
					response = PBAPIHelper.startUploading(mToken);
				}
				if (response == null)
					return false;
				if (response.errorCode == ResponseHandle.CODE_403) {
					if (mUiUpdateListener != null) {
						mUiUpdateListener.onUploadForbidden();
					}
				}
				if (!TextUtils.isEmpty(response.decription)) {
					try {
						JSONObject jObject = new JSONObject(response.decription);
						if (!TextUtils.isEmpty(collectionId)) {
							if (jObject.has("error")
									&& jObject.getString("error").equals(
											"start adding failed"))
								return false;
						}
						if (!jObject.has("collection"))
							return false;
						collectionId = jObject.getString("collection");
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				if (TextUtils.isEmpty(collectionId))
					return false;
				PBPreferenceUtils.saveStringPref(getBaseContext(),
						PBConstant.UPLOAD_SERVICE_PREF,
						PBConstant.PREF_COLLECTION_ID, collectionId);
			}

			Log.d("totalVideoChoose ", ""+totalVideoChoose);
			if (totalVideoChoose == 0) {
				// start calling upload task directly
				if (mUploadTask == null
						|| (mUploadTask != null && mUploadTask.getStatus() != AsyncTask.Status.RUNNING)) {
					if (mLocalMediaList != null) {
						mUploadTask = new UploadTask(mLocalMediaList);
						mUploadTask.execute();
					}
				} else {
					Log.e(PBConstant.TAG, ">>> CAN NOT START UPLOAD TASK!!!");
				}
				return true;
			}

			mPbProgress = new PBProgress(totalVideoChoose);
              /////new modification/////////
			
//			String filepath = "";
//			int idx = 0;

			mCachedVideoPath = new String[totalVideoChoose];
			
			filepath = map.get(videoIndexValues.get(numberOfFilesCompress));
			
			if (filepath.toLowerCase().contains(PBConstant.MEDIA_VIDEO_EXT_MP4)
					|| filepath.toLowerCase().contains(
							PBConstant.MEDIA_VIDEO_EXT_3GP)) {
				Log.d(TAG, "---- START COMPRESS ----- " + numberOfFilesCompress);

				mPbProgress.resetBeforeStartNewProcess();

				// @TinhNH 7-5-2012 START

				if (pbVideoUtils != null) {
					// pbVideoUtils.clearData();
					try {
				        Thread.sleep(5000);         
				    } catch (InterruptedException e) {
				       e.printStackTrace();
				    }
					cachedVideoPath = pbVideoUtils.compressVideo(filepath);

					// added by Atik when starting compress video

					final String command = pbVideoUtils.getCommand();

					if (!TextUtils.isEmpty(command)) {
						if(ffmpeg == null){
							
							ffmpeg = FFmpeg.getInstance(UploadService.this);
							loadFFMpegBinary();
							
							 try {
								Thread.sleep(5000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}     
						}
						aduration = pbVideoUtils.getDuration(filepath);
						File f = new File(filepath);
						String uid = PBPreferenceUtils.getStringPref(getApplicationContext(), PBConstant.PREF_NAME, PBConstant.PREF_NAME_UID, "0");
						uploadAnalyticsData = "UID:[ "+uid+ "]"+ " videoname:["+ f.getName()+ "]" +" sourcesize: ["+f.length()+" byte ]";
						execFFmpegBinary(command);
					}


				}
			}

			return true;
		}

		/// new modification///
		
		int idx = 0;

		
		private void execFFmpegBinary(final String command) {
			pecentOfCompress = 0;
			try {
				ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {
					@Override
					public void onFailure(String s) {

						Log.d("TAG", "FAILED with output " + s);
						if (mUiUpdateListener != null) {
							mUiUpdateListener.updateUI(
									PBConstant.UPLOAD_COMPRESS_ERROR, null);
						}
						mIsStop = true;
					}

					@Override
					public void onSuccess(String s) {
					}

					@Override
					public void onProgress(String s) {
						Scanner sc = new Scanner(s);
						String match;
						Log.d("TAG", "onProgress command : ffmpeg " + s);
						String[] matchSplit;
						while (null != (match = sc.findWithinHorizon(
								timePattern, 0))) {
							matchSplit = match.split(":");

							time = Integer.parseInt(matchSplit[0]) * 3600
									+ Integer.parseInt(matchSplit[1]) * 60
									+ Double.parseDouble(matchSplit[2]);
						}

						if (mUiUpdateListener != null && time > 0) {

							Log.d(TAG, "---- aduration ----- " + (int) (time));
							if (mPbProgress != null) {
								pecentOfCompress = mPbProgress
										.getPercentOfProcessWithOneFile((int) (time * 100 * 1000 / aduration));
							}
							mUiUpdateListener.updateCompressBar(
									totalVideoChoose, pecentOfCompress);
						}
					}

					@Override
					public void onStart() {

						startTime = System.currentTimeMillis();
						Log.d("TAG", "Started command : ffmpeg " + command);
						if (pbVideoUtils != null) {
							time = 0;

						}
					}

					@Override
					public void onFinish() {
						
						endTime = System.currentTimeMillis();
						
					
						Log.d("TAG", "onFinish command : ffmpeg " + command);
						time = 0;
						if (mUiUpdateListener != null) {
							System.out
									.println("Atik called UPLOAD_PROCESS_COMPRESS_VIDEO from upload service 3");

							mUiUpdateListener
									.updateUI(
											PBConstant.UPLOAD_PROCESS_COMPRESS_VIDEO_DONE,
											null);
						}
						File f = new File(cachedVideoPath);
						System.out.println("FileSize after compressing video"
								+ f.getAbsolutePath() + "  " + f.length()); // atik
						
						long second = (startTime / 1000) % 60;
						long minute = (startTime / (1000 * 60)) % 60;
						long hour = (startTime / (1000 * 60 * 60)) % 24;

						String sTime = String.format("%02d:%02d:%02d:", hour, minute, second);
						
						 second = (endTime / 1000) % 60;
						 minute = (endTime / (1000 * 60)) % 60;
						 hour = (endTime / (1000 * 60 * 60)) % 24;
						
						String eTime = String.format("%02d:%02d:%02d", hour, minute, second);
						long targetTime = endTime - startTime;
						 second = (targetTime / 1000) % 60;
						 minute = (targetTime / (1000 * 60)) % 60;
						 hour = (targetTime / (1000 * 60 * 60)) % 24;
						
						String tTime = String.format("%02d:%02d:%02d", hour, minute, second);
						
						uploadAnalyticsData = uploadAnalyticsData +" starttime: ["+ sTime+ "]"
								+"endtime: ["+eTime+ " ]"+" targetsize: ["+f.length()+" byte ] "+" timetaken: [ "+tTime+" ]";
						
						mUiUpdateListener.updateAnalytics(uploadAnalyticsData);
						
						
						
						if (!f.exists() && f.length() > 0) {
							mIsStop = true;
							return;
						}

						boolean canupload = PBGeneralUtils
								.checkFileSizeCanUpload(cachedVideoPath,
										PBConstant.VIDEO_SIZE_LIMIT);
						Log.i(TAG, ">>> file size can be uploaded to server: "
								+ canupload);

						if (!TextUtils.isEmpty(cachedVideoPath)) {
							// update file cache list
							mCachedVideoPath[idx] = cachedVideoPath;
							idx++;

							// update local media list
							mLocalMediaList.set(videoIndexValues.get(numberOfFilesCompress),
									cachedVideoPath);
							// cachedVideoPath = null;

							// 20120510 update duration by using ffmpeg <S>
							if ((videoIndexValues.get(numberOfFilesCompress) < mLocalMediaTypeList.length)
									&& (mLocalMediaTypeList[videoIndexValues.get(numberOfFilesCompress)] == 0)) {
								mLocalMediaTypeList[videoIndexValues.get(numberOfFilesCompress)] = pbVideoUtils
										.getDuration(mListFilePath
												.get(videoIndexValues.get(numberOfFilesCompress)));
							}
							// 20120510 update duration by using ffmpeg <E>
						}

						if (!canupload) {
							Log.e(TAG, "--> Compress video failed!");
							mIsStop = true;

							// cancel current uploading info!!!
							String collectionId = PBPreferenceUtils
									.getStringPref(getBaseContext(),
											PBConstant.UPLOAD_SERVICE_PREF,
											PBConstant.PREF_COLLECTION_ID, "");
							cancelUploadTask(collectionId);

							if (mUiUpdateListener != null) {
								mUiUpdateListener.updateUI(
										PBConstant.UPLOAD_COMPRESS_FAIL, null);
							}
							return;
						}

						if (numberOfFilesCompress != (videoIndexValues.size() - 1)) {

							numberOfFilesCompress++;
							filepath = map.get(videoIndexValues.get(numberOfFilesCompress));
							if (filepath.toLowerCase().contains(
									PBConstant.MEDIA_VIDEO_EXT_MP4)
									|| filepath.toLowerCase().contains(
											PBConstant.MEDIA_VIDEO_EXT_3GP)) {
								Log.d(TAG, "---- START COMPRESS ----- "
										+ numberOfFilesCompress);

								mPbProgress.resetBeforeStartNewProcess();

								// @TinhNH 7-5-2012 START

								if (pbVideoUtils != null) {
									// pbVideoUtils.clearData();
									cachedVideoPath = pbVideoUtils
											.compressVideo(filepath);

									final String command = pbVideoUtils
											.getCommand();

									if (!TextUtils.isEmpty(command)) {

										//ffmpeg = FFmpeg
										//		.getInstance(UploadService.this);
										//loadFFMpegBinary();
										uploadAnalyticsData = "";
										File file = new File(mListFilePath
												.get(videoIndexValues.get(numberOfFilesCompress)));
										String uid = PBPreferenceUtils.getStringPref(getApplicationContext(), PBConstant.PREF_NAME, PBConstant.PREF_NAME_UID, "0");
										uploadAnalyticsData = "UID:[ "+uid+ "]"+ " videoname:["+ file.getName()+ "]" +" sourcesize: ["+file.length()+" byte ]";
										aduration = pbVideoUtils.getDuration(mListFilePath
												.get(videoIndexValues.get(numberOfFilesCompress)));

										execFFmpegBinary(command);
									}

								}

							}
						} else {

							if (mUploadTask == null
									|| (mUploadTask != null && mUploadTask
											.getStatus() != AsyncTask.Status.RUNNING)) {
								if (mLocalMediaList != null) {
									mUploadTask = new UploadTask(
											mLocalMediaList);
									mUploadTask.execute();
								}
							} 
							else {
								Log.w(PBConstant.TAG,
										">>> CAN NOT START UPLOAD TASK!!!");
								if (mUiUpdateListener != null) {
									System.out
											.println("Atik called UPLOAD_PROCESS_COMPRESS_VIDEO from upload service 2");

									mUiUpdateListener
											.updateUI(
													PBConstant.UPLOAD_PROCESS_COMPRESS_VIDEO,
													null);
									mUiUpdateListener.updateUI(
											PBConstant.UPLOAD_COMPRESS_FAIL,
											null);
								}
							}
						}
					}
				});
			} catch (FFmpegCommandAlreadyRunningException e) {
				// do nothing for now
			}
		}
		
		
		
		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			if (mUiUpdateListener != null) {
				// Log.i(TAG, "-- compress percent = " + values[1]);
				mUiUpdateListener.updateCompressBar(values[0], values[1]);
			}
		}

		public void doWhenCancelTask(boolean stopService) {

			PBAPIHelper.cancelUploading(PBPreferenceUtils.getStringPref(
					getApplicationContext(), PBConstant.UPLOAD_SERVICE_PREF,
					PBConstant.PREF_COLLECTION_ID, ""), PBPreferenceUtils
					.getStringPref(getApplicationContext(),
							PBConstant.PREF_NAME, PBConstant.PREF_NAME_TOKEN,
							""));

			// Reset all upload service preference
			PBPreferenceUtils.saveBoolPref(getApplicationContext(),
					PBConstant.UPLOAD_SERVICE_PREF,
					PBConstant.PREF_UPLOAD_FINISH, false);
			PBPreferenceUtils.saveStringPref(getApplicationContext(),
					PBConstant.UPLOAD_SERVICE_PREF,
					PBConstant.PREF_UPLOAD_PASS, "");
			PBPreferenceUtils.saveStringPref(getApplicationContext(),
					PBConstant.UPLOAD_SERVICE_PREF,
					PBConstant.PREF_COLLECTION_ID, "");
			PBPreferenceUtils.saveStringPref(getApplicationContext(),
					PBConstant.UPLOAD_SERVICE_PREF,
					PBConstant.PREF_COLLECTION_THUMB, "");

			// remove cached compressed video file
			deleteCachedVideoFile(mCachedVideoPath);

			// stop service
			if (stopService) {
				stopSelf();
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (result) {
				// 20120508 add by NhatVT, should reset progress bar when
				// compress finish <S>
				if (mUiUpdateListener != null) {
					mUiUpdateListener.updateUI(0, null);
					mUiUpdateListener.updateCompressBar(0, 0);
				}
				// 20120508 add by NhatVT, should reset progress bar when
				// compress finish <E>
			}
		}

		private void releaseVideoCompression() {

			Log.d("releaseVideoCompression", "releaseVideoCompression");
			if (ffmpeg != null) {
				// ffmpeg.release();
				boolean isKill = ffmpeg.killRunningProcesses();
				if (isKill) {
					ffmpeg = null;

				}

			}
			if (pbVideoUtils != null) {
				pbVideoUtils = null;
			}
		
		}
//			if (pbVideoUtils != null) {
//				pbVideoUtils.cancelCompression();
//				pbVideoUtils = null;
//			}
//		}

	}

	/**
	 * This AsyncTask to uploading selected images to server
	 */
	private class UploadTask extends AsyncTask<String, Void, Void> {
		private final int STATUS_NONE = 100;
		private final String TAG = "UPLOAD_TASK";
		private int mStatus = STATUS_NONE;
		// private int currentLevelOfProgressBar = 0;
		// private int mCurrentProgress = 0;
		// private int mSeekbarProgress = 0;
		// private int percent = 0;

		private ArrayList<String> mLocalMediaList;
		private int medialistSize = 0;
		private UploadTask mUploadTask;

		public UploadTask(ArrayList<String> medialist) {
			this.mLocalMediaList = medialist;
			if (this.mLocalMediaList != null) {
				this.medialistSize = this.mLocalMediaList.size();
			}
			this.mUploadTask = this;
			// should reset [mUploadedPhotoNum] in some case
			if (mUploadedPhotoNum == COUNT_UPLOAD_DEFAULT) {
				mUploadedPhotoNum = 0;
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			PBConstant.COMPRESSORUPDLOAD = true;
			if (mUploadUrlList == null) {
				mUploadUrlList = new ArrayList<MediaInfo>();
			}

			// 20120503 should update UI for uploading when compress finish!
			if (mUiUpdateListener != null) {
				mUiUpdateListener.updateUI(0, mUploadUrlList);
			}
		}

		@Override
		protected Void doInBackground(String... params) {
			String photoPath = null;
			// mSeekbarProgress = 0;
			// currentLevelOfProgressBar = 0;
			// 20120302 avoid server remember file upload before #S
			if (mUploadedPhotoNum < 0) {
				mUploadedPhotoNum = 0;
			}
			String collectionId = PBPreferenceUtils.getStringPref(
					getBaseContext(), PBConstant.UPLOAD_SERVICE_PREF,
					PBConstant.PREF_COLLECTION_ID, "");
			// 20120302 avoid server remeber file upload before #E
			// 20120503 mod by NhatVT <S>
			final PBProgress mPbProgress = new PBProgress(medialistSize);
			// 20120503 mod by NhatVT <E>
			// Do upload selected photos
			for (int i = mUploadedPhotoNum; i < medialistSize/*
															 * mLocalMediaList.size
															 * ()
															 */; i++) {
				if (UploadingActivity.mActivity != null) {
					PBGeneralUtils.checkSdcard(UploadingActivity.mActivity,
							true, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									mIsStop = true;
								}
							});
				}
				if (mIsStop) {
					stopSelf();
					mStatus = STATUS_NONE;
					return null;
				}

				Log.d(TAG, "start upload photo...............: " + (i + 1)
						+ "th");

				photoPath = mLocalMediaList.get(i);
				if (TextUtils.isEmpty(photoPath))
					continue;

				int media = PBDatabaseDefinition.MEDIA_PHOTO;
				long duration = 0;
				if (mLocalMediaTypeList != null && i < medialistSize/*
																	 * mLocalMediaList
																	 * .size()
																	 */) {
					duration = mLocalMediaTypeList[i];
					media = (duration != -1) ? PBDatabaseDefinition.MEDIA_VIDEO
							: PBDatabaseDefinition.MEDIA_PHOTO;
				}

				// @lent5 compress path file before upload #S
				// only compress in case photo upload
				if (media == PBDatabaseDefinition.MEDIA_PHOTO) {
					photoPath = PBBitmapUtils.compressBitmap(photoPath);
				} else {
					// 20120427 added for checking video size before upload <S>
					boolean canupload = PBGeneralUtils.checkFileSizeCanUpload(
							photoPath, PBConstant.VIDEO_SIZE_LIMIT);
					Log.i(TAG, "--> can upload to server: " + canupload);
					if (!canupload) {
						Log.w(TAG, "--> Compress video failed!");
						mIsStop = true;
						if (mUiUpdateListener != null) {
							System.out
									.println("Atik called UPLOAD_PROCESS_COMPRESS_VIDEO from upload service 1");
							mUiUpdateListener.updateUI(
									PBConstant.UPLOAD_PROCESS_COMPRESS_VIDEO,
									mUploadUrlList);
							mUiUpdateListener.updateUI(
									PBConstant.UPLOAD_COMPRESS_FAIL,
									mUploadUrlList);
						}
						mStatus = STATUS_NONE;
						return null;
					}
					// 20120427 added for checking video size before upload <E>
				}
				// @lent5 compress path file before upload #E
				// mCurrentProgress = 0;
				mPbProgress.resetBeforeStartNewProcess();
				// 20120426 added by NhatVT, support display progress of
				// downloading <S>
				Response respond = PBAPIHelper.uploadMedia(photoPath,
						collectionId, mToken, media,
						new ProgressManagerListener() {
							@Override
							public void updateProgressStatus(int percentUploaded) {
								if (mUiUpdateListener != null) {
									mUiUpdateListener.updateProgressBar(mPbProgress
											.getPercentOfProcessWithOneFile(percentUploaded));
								}
							}

							@Override
							public boolean cancelDownloadTask() {
								return mUploadTask.isCancelled();
								// return mIsStop;
							}
						});
				// 20120426 added by NhatVT, support display progress of
				// downloading <E>

				if (respond == null)
					continue;
				// 20120301 @lent5 check compress photo failed #E
				if (respond.errorCode == ResponseHandle.CODE_200_OK) {
					String description = respond.decription;
					mUploadedPhotoNum = i + 1;
					String url = null;
					int type = PBDatabaseDefinition.MEDIA_PHOTO;
					JSONObject jObject;
					if (!TextUtils.isEmpty(description)) {
						try {
							jObject = new JSONObject(description);
							if (jObject != null && jObject.has("photo")) {
								type = PBDatabaseDefinition.MEDIA_PHOTO;
								JSONObject jObjCollection = jObject
										.getJSONObject("photo");
								if (jObjCollection != null
										&& jObjCollection.has("url")) {
									url = jObjCollection.getString("url");
								}
								// 20120312 @lent support upload/download video
								// #S
							} else if (jObject != null && jObject.has("video")) {
								type = PBDatabaseDefinition.MEDIA_VIDEO;
								JSONObject jObjCollection = jObject
										.getJSONObject("video");
								if (jObjCollection != null
										&& jObjCollection.has("url")) {
									url = jObjCollection.getString("url");
								}
							}
							// 20120312 @lent support upload/download video #E
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					if (type == PBDatabaseDefinition.MEDIA_PHOTO) {
						mUploadUrlList.add(new MediaInfo(url, type));
					} else {
						mUploadUrlList.add(new MediaInfo(url, type, duration));
					}
					// store to cache

					if (i < mLocalMediaList.size()) {
						// 20120312 @lent support upload/download video
						if (type == PBDatabaseDefinition.MEDIA_PHOTO) {
							// String compressedFile =
							// UploadServiceUtils.getCompressedFilePath(mLocalMediaList.get(i));
							UploadServiceUtils
									.savePhotoToCacheFolder(url,
											mLocalMediaList.get(i), photoPath/* compressedFile */);
						} else {
							UploadServiceUtils.saveVideoToCacheFolder(url,
									mLocalMediaList.get(i));
						}
					}

					if (mUiUpdateListener != null) {
						mUiUpdateListener.updateUI(i + 1, mUploadUrlList);
					}
					// Log.e(TAG, "Uploaded.: " + (i + 1) + " $ " +
					// respond.decription);
				} else if (respond.errorCode == ResponseHandle.CODE_FILE_COMPRESS_FAIL) {
					if (mUiUpdateListener != null) {
						mUiUpdateListener
								.updateUI(PBConstant.UPLOAD_COMPRESS_FAIL,
										mUploadUrlList);
					}
					mStatus = PBConstant.UPLOAD_COMPRESS_FAIL;
					return null;
				} else {
					// Log.e(TAG, "Upload error....! Error code: " +
					// respond.errorCode);
					if (mUiUpdateListener != null) {
						mUiUpdateListener.updateUI(PBConstant.UPLOAD_ERROR,
								mUploadUrlList);
					}
					mStatus = PBConstant.UPLOAD_ERROR;
					return null;
				}
			}
			mStatus = PBConstant.UPLOAD_FINISH;
			return null;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			// reset progress bar
			// currentLevelOfProgressBar = 0;

			// get collection id if have set
			String collectionId = PBPreferenceUtils.getStringPref(
					getBaseContext(), PBConstant.UPLOAD_SERVICE_PREF,
					PBConstant.PREF_COLLECTION_ID, "");
			mIsStop = false;
			cancelUploadTask(collectionId);
			stopSelf(); // stop service
		}

		// @SuppressWarnings("unchecked")
		@Override
		protected void onPostExecute(Void result) {
			// Upload finish
			super.onPostExecute(result);

			// get collection id if have set
			String collectionId = PBPreferenceUtils.getStringPref(
					getBaseContext(), PBConstant.UPLOAD_SERVICE_PREF,
					PBConstant.PREF_COLLECTION_ID, "");

			// Toast.makeText(getApplicationContext(), collectionId,
			// Toast.LENGTH_LONG).show();

			if (mIsStop) { // cancel upload task
				mIsStop = false;
				cancelUploadTask(collectionId);
				stopSelf(); // stop service

			} else { // handle upload finish, save to database.
				Boolean isInputSequenceFinished = PBPreferenceUtils
						.getBoolPref(getBaseContext(),
								PBConstant.UPLOAD_SERVICE_PREF,
								PBConstant.PREF_INPUT_SEQUENCE_FINISH, false);
				if (isInputSequenceFinished
						&& mStatus == PBConstant.UPLOAD_FINISH) {

					// Toast.makeText(getApplicationContext(), "UPLOAD_FINISH",
					// Toast.LENGTH_LONG).show();
					// finish upload after set password, has CollectionId
					Response response = PBAPIHelper.finishUploading(
							collectionId, mToken, PBPreferenceUtils.getIntPref(
									getBaseContext(),
									PBConstant.UPLOAD_SERVICE_PREF,
									PBConstant.PREF_ADDIBILITY, 0) == 1 ? true
									: false);
					// an error when upload
					if (response.errorCode != ResponseHandle.CODE_200_OK) {
						if (mUiUpdateListener != null) {
							mUiUpdateListener.updateUI(PBConstant.UPLOAD_ERROR,
									mUploadUrlList);
						}
						cancelUploadTask(collectionId); // 20120504
						stopSelf();
						return;
					} else { // upload success complete
						handleUploadSuccessComplete(collectionId, response);

						// 20120504 remove cached compressed video file
						deleteCachedVideoFile(mCachedVideoPath); // comment by
																	// atik

						//
						// Toast.makeText(getApplicationContext(),
						// "isAppBacground", Toast.LENGTH_LONG).show();
						boolean isAppBacground = PBGeneralUtils
								.isApplicationBroughtToBackground(getApplicationContext());
						System.out
								.println("Appliction is in background with collection ID:"
										+ isAppBacground + ":" + collectionId);
						if (isAppBacground) {

							/*
							 * //通知の準備 NotificationManager notificationManager =
							 * (NotificationManager)
							 * getSystemService(Context.NOTIFICATION_SERVICE);
							 * Notification notification = new Notification();
							 * 
							 * // クリック時に発行するインテント Intent clickIntent = new
							 * Intent(); PendingIntent pendingIntent =
							 * PendingIntent
							 * .getActivity(getApplicationContext(), 0,
							 * clickIntent, 0);
							 * 
							 * //通知の設定 String title
							 * =getResources().getString(R.string.app_name);
							 * String password =
							 * PBPreferenceUtils.getStringPref(
							 * getBaseContext(), PBConstant.UPLOAD_SERVICE_PREF,
							 * PBConstant.PREF_UPLOAD_PASS, "");
							 * //getResources()
							 * .getString(getResources().getIdentifier
							 * ("app_name", "id", getPackageName())); String
							 * message = password+"アップロードを完了しました。"; // Need
							 * password name notification.icon =
							 * getResources().getIdentifier("icon", "drawable",
							 * getPackageName());
							 * notification.setLatestEventInfo
							 * (getApplicationContext(),title,message,
							 * pendingIntent);
							 * 
							 * notification.flags =
							 * Notification.FLAG_AUTO_CANCEL;
							 * notification.tickerText = message;
							 * 
							 * int identifierId = 1;
							 * notificationManager.notify(identifierId,
							 * notification);
							 */
							String password = PBPreferenceUtils.getStringPref(
									getBaseContext(),
									PBConstant.UPLOAD_SERVICE_PREF,
									PBConstant.PREF_UPLOAD_PASS, "");
							String message = "『"
									+ password
									+ getResources()
											.getString(
													R.string.password_notification_local_upload); // Need
																									// password
																									// name
							generateNotificationForAdvertise(
									getApplicationContext(), message);

						} else {

							PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
							WakeLock wakeLock = powerManager.newWakeLock(
									PowerManager.PARTIAL_WAKE_LOCK,
									"MyWakelockTag");
							wakeLock.acquire();

							boolean screenOn = false;
							if (Build.VERSION.SDK_INT >= 21) {
								screenOn = powerManager.isInteractive();
							} else {
								screenOn = powerManager.isScreenOn();
							}

							if (!screenOn) {
								// Screen is still on, so do your thing here
								String password = PBPreferenceUtils
										.getStringPref(getBaseContext(),
												PBConstant.UPLOAD_SERVICE_PREF,
												PBConstant.PREF_UPLOAD_PASS, "");
								// String message = password+"アップロードを完了しました。";
								// // Need password name
								String message = "『"
										+ password
										+ getResources()
												.getString(
														R.string.password_notification_local_upload); // Need
																										// password
																										// name

								generateNotificationForAdvertise(
										getApplicationContext(), message);
							}

						}

						// Atik save shared preference so that history can
						// update when upload is done
						System.out
								.println("Atik call upload done, set shared preference for download photo from server");
						PBPreferenceUtils
								.saveBoolPref(
										getApplicationContext(),
										PBConstant.PREF_NAME,
										PBConstant.PREF_NEED_REFRESH_HISTORY_SCREEN_FOR_UPLOAD,
										true);
					}
				} else {
					// an error occurred in upload progress
					if (mStatus == PBConstant.UPLOAD_ERROR
							|| mUploadUrlList == null
							|| mUploadUrlList.size() < mLocalMediaList.size()
							|| mLocalMediaList.size() == 0) {
						if (mUiUpdateListener != null) {
							mUiUpdateListener.updateUI(PBConstant.UPLOAD_ERROR,
									mUploadUrlList);
						}
						stopSelf();
					} else { // finish upload before set password, no
								// collectionID
						PBUploadSelectAddibilityActivity
								.setPhotoUrlList(mUploadUrlList);
						PBPreferenceUtils.saveBoolPref(getBaseContext(),
								PBConstant.UPLOAD_SERVICE_PREF,
								PBConstant.PREF_UPLOAD_FINISH, true);
						PBPreferenceUtils.saveStringPref(getBaseContext(),
								PBConstant.UPLOAD_SERVICE_PREF,
								PBConstant.PREF_COLLECTION_THUMB,
								mUploadUrlList.get(0).url
										+ "?width=150&height=150");
						stopSelf();
					}
				}
			}
		}

		private void handleUploadSuccessComplete(String collectionId,
				Response responseFinishTask) {
			
			PBConstant.doUpdate = true;

			//System.out.println("Atik successfully uploaded materials");

			// Atik whether application is in background or not and then
			// based on that send push notification

			PBPreferenceUtils.saveBoolPref(getBaseContext(),
					PBConstant.UPLOAD_SERVICE_PREF,
					PBConstant.PREF_UPLOAD_FINISH, true);
			PBPreferenceUtils.saveStringPref(getBaseContext(),
					PBConstant.UPLOAD_SERVICE_PREF,
					PBConstant.PREF_COLLECTION_THUMB, mUploadUrlList.get(0).url
							+ "?width=150&height=150");

			JSONObject jObject;
			if (!TextUtils.isEmpty(responseFinishTask.decription)
					&& responseFinishTask.decription.contains("collection")) {
				try {
					jObject = new JSONObject(responseFinishTask.decription);
					if (jObject != null && jObject.has("collection")) {
						JSONObject jObjCollection = jObject
								.getJSONObject("collection");
						if (jObjCollection.has("charges_at")) {
							mChargeDate = jObjCollection.getLong("charges_at");
							PBPreferenceUtils.saveLongPref(getBaseContext(),
									PBConstant.UPLOAD_SERVICE_PREF,
									PBConstant.PREF_CHARGE_DATE, mChargeDate);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			// remove uploaded bags which have same collection id. (it may occur
			// when adding)
			// * the part of this part should be executed with proper sql
			// statements. (which has "where" clauses about collection id)
			ArrayList<PBHistoryEntryModel> histories = PBDatabaseManager
					.getInstance(getBaseContext()).getHistories(
							PBDatabaseDefinition.HISTORY_SENT);
			for (PBHistoryEntryModel history : histories) {
				if (!history.getCollectionId().equals(collectionId)) {
					continue;
				}
				PBDatabaseManager.getInstance(getBaseContext()).deleteHistory(
						String.valueOf(history.getEntryId()), null);
			}

			// save DB, after completed, notify to UI
			SaveDBTask saveDatabase = new SaveDBTask(
					PBApplication.getBaseApplicationContext(), mLocalMediaList,
					mUploadUrlList, new ServiceUpdateUIListener() {
						@Override
						public void updateUI(int uploadedPhotos,
								ArrayList<MediaInfo> uploadedUrlPhoto) {
							if (uploadedPhotos == PBConstant.UPLOAD_FINISH_COMPLETED
									&& mUiUpdateListener != null) {
								mUiUpdateListener.updateUI(
										PBConstant.UPLOAD_FINISH_COMPLETED,
										mUploadUrlList);
							}
						}

						@Override
						public void updateProgressBar(int percent) {
						}

						@Override
						public void updateCompressBar(int totalFile, int percent) {
						}

						@Override
						public void onUploadForbidden() {
						}

						@Override
						public void updateAnalytics(String data) {
							// TODO Auto-generated method stub
							
						}
					});
			saveDatabase.execute();

			System.out.println("Atik handle compete done");

			// Log.d(TAG, "Finish upload respond code: " +
			// responseFinishTask.errorCode);
			if (mUiUpdateListener != null) {
				mUiUpdateListener.updateUI(PBConstant.UPLOAD_FINISH,
						mUploadUrlList);
			}
		}
	}

	private void cancelUploadTask(String collectionId) {
		String token = PBPreferenceUtils.getStringPref(getBaseContext(),
				PBConstant.PREF_NAME, PBConstant.PREF_NAME_TOKEN, "");

		Response response = PBAPIHelper.cancelUploading(collectionId, token);
		Log.e(TAG, "Cancel upload when stop service" + response.decription
				+ "Code: " + response.errorCode);
		// delete collection if is set
		if (!TextUtils.isEmpty(collectionId)) {
			CancelUploadingTask cancelUpload = new CancelUploadingTask(
					getBaseContext());
			cancelUpload.execute();
		}
		 PBPreferenceUtils.saveStringPref(getBaseContext(),
                 PBConstant.UPLOAD_SERVICE_PREF,
                 PBConstant.PREF_FOUR_DIGIT, "12");
		// delete cache file
		if (mUploadUrlList != null) {
			@SuppressWarnings("unchecked")
			ArrayList<MediaInfo> photos = (ArrayList<MediaInfo>) mUploadUrlList
					.clone();
			UploadServiceUtils.deleteCachePhoto(mUploadUrlList);
			if (photos != null) {
				photos.clear();
				photos = null;
			}
		}

		// remove cached video file if any
		deleteCachedVideoFile(mCachedVideoPath);
	}

	/**
	 * 
	 * @return is service stop or not
	 */
	public static boolean getIsStop() {
		return mIsStop;
	}

	public static class MediaInfo {
		public String url;
		public int type = PBDatabaseDefinition.MEDIA_PHOTO;
		public long duration = 0;

		public MediaInfo(String url, int type) {
			this.url = url;
			this.type = type;
		}

		public MediaInfo(String url, int type, long duration) {
			this.url = url;
			this.type = type;
			this.duration = duration;
		}
	}

	/**
	 * Issues a notification to inform the user that server has sent a message.
	 */
	private static void generateNotificationForAdvertise(final Context context,
			final String message) {

		ActivityManager am = (ActivityManager) context
				.getSystemService(ACTIVITY_SERVICE);
		List<RunningTaskInfo> taskInfo = am.getRunningTasks(1);
		ComponentName componentInfo = taskInfo.get(0).topActivity;

		final String title = context.getString(R.string.pb_app_name);

		@SuppressWarnings("rawtypes")
		Class topClass = null;
		if (componentInfo.getPackageName().equalsIgnoreCase(
				PBConstant.PREF_NAME)) {
			// Activity in foreground, broadcast intent
			topClass = componentInfo.getClass();
		} else {
			// Activity Not Running,so Generate Notification
			topClass = PBSplashScreenActivity.class;
		}

		Handler h = new Handler(Looper.getMainLooper());
		h.post(new Runnable() {
			public void run() {
				/*
				 * Intent i = new Intent(context,PushDialogActivity.class);
				 * i.putExtra("TITLE",title); i.putExtra("MESSAGE",message);
				 * i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
				 * Intent.FLAG_ACTIVITY_NEW_TASK); context.startActivity(i);
				 */
				// Toast.makeText(context, "『"+title+"』 "+message,
				// Toast.LENGTH_LONG).show();
				Toast.makeText(context, title + "" + message, Toast.LENGTH_LONG)
						.show();

			}
		});

		// int icon = R.drawable.icon_small;
		int icon_small = R.drawable.icon_small_for_push;
		// int icon = R.drawable.icon;
		Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.icon_small_test);

		Intent notificationIntent = new Intent(context, topClass);

		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);

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
		notification.defaults |= Notification.DEFAULT_SOUND;//
		notification.defaults |= Notification.DEFAULT_LIGHTS;
		notification.vibrate = new long[] { 0, 200, 100, 50, 100, 50, 100, 200 };

		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(1, notification);
	}
	private void loadFFMpegBinary() {
		try {
			ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
				@Override
				public void onFailure() {
					// showUnsupportedExceptionDialog();
					//Toast.makeText(getApplicationContext(), "onFailure", 200).show();
				}
			});
		} catch (FFmpegNotSupportedException e) {
			// showUnsupportedExceptionDialog();
			//Toast.makeText(getApplicationContext(), "FFmpegNotSupportedException", 200).show();
		}
	}
}
