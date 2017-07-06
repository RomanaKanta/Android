package com.aircast.photobag.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aircast.photobag.R;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.services.CancelUploadingTask;
import com.aircast.photobag.services.UploadService;
import com.aircast.photobag.services.UploadServiceUtils;
import com.aircast.photobag.services.UploadService.MediaInfo;
import com.aircast.photobag.services.UploadService.ServiceUpdateUIListener;
import com.aircast.photobag.utils.PBGeneralUtils;
import com.aircast.photobag.utils.PBPreferenceUtils;
import com.aircast.photobag.widget.FButton;
import com.aircast.photobag.widget.actionbar.ActionBar;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

/**
 * Show upload progress when uploading.
 * */
public class UploadingActivity extends PBAbsActionBarActivity implements OnClickListener {
    private final String TAG = "UPLOADING_ACTIVITY";

    public static Activity mActivity;
    private static String mUploadProgressString;
    private ArrayList<String> mUploadPhotoList;
    private long[] mUploadPhotoTypeList;
    private ArrayList<MediaInfo> mUploadedUrlList;
    private ProgressBar mUploadProgress;
    private TextView mUploadNotifyTextView;
    private TextView mUploadVideoCompressNotifyTextView;
    private FButton mCancelBtn, mRetryBtn, mNotRetryBtn;
    private int mTotalUploadedPhoto = UploadService.COUNT_UPLOAD_DEFAULT;
    private Intent mServiceIntent;
    private final int UPDATE_NUMBER_PHOTO_UPLOADED = 0;
    private final int UPDATE_SCREEN_ERROR = 1;
    private final int UPDATE_PROGRESS_BAR = 2;
    private ActionBar mHeaderBar;
    private boolean onlyImageFileUploaded = true;

    private Handler mHandler = new Handler(new Handler.Callback() {
    private int counter = 1;
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
            case UPDATE_NUMBER_PHOTO_UPLOADED:
                // Toast.makeText(getBaseContext(),
                // "Upload completed " + mTotalUploadedPhoto + "photo",
                // Toast.LENGTH_SHORT).show();
                // FIXME later----- mUploadProgress.setProgress(mTotalUploadedPhoto);
                String progress = mUploadProgressString.replace("$", ""
                        + mUploadPhotoList.size());
                progress = progress.replace("#", "" + mTotalUploadedPhoto);
                mUploadNotifyTextView.setText(progress);
                mUploadVideoCompressNotifyTextView.setText("");
                Log.e(TAG, "Upload completed "
                        + mTotalUploadedPhoto + "photo");
                break;
                
            case UPDATE_PROGRESS_BAR: // TODO update progress bar
                if(mUploadProgress != null){
                    mUploadProgress.setProgress(msg.arg1);
                }
                break;
                
            case PBConstant.UPLOAD_PROCESS_COMPRESS_VIDEO:
            	System.out.println("Atik  called UPLOAD_PROCESS_COMPRESS_VIDEO");
                if (mUploadNotifyTextView != null) {
                	//mUploadNotifyTextView.setText(getText(R.string.pb_compress_resizing)+ ""+UploadService.getSelectedVideoNum()); //Comment by Atik

                	
                	//if(/*!onlyImageFileUploaded*/true) {
                    	mUploadNotifyTextView.setText(getText(R.string.pb_compress_resizing_only_video1)+ ""
                    			+UploadService.getSelectedVideoNum()+getText(R.string.pb_compress_resizing_only_video2)); 
                    	
                    	System.out.println("Atik call video compress ongoing number " + counter);
		                	mUploadVideoCompressNotifyTextView.setText(counter+
		                			""+getText(R.string.pb_compress_resizing_only_video3)+
		                			"1/"+UploadService.getSelectedVideoNum()+""
		                			+getText(R.string.pb_compress_resizing_only_video4));
                	/*} else {
                		
                    	mUploadNotifyTextView.setText(getText(R.string.pb_compress_resizing_only_image1)+ ""
                    			+UploadService.getSelectedVideoNum()+getText(R.string.pb_compress_resizing_only_video2)); 
                    	
                    	System.out.println("Atik call video compress ongoing number " + counter);
                    	
	                	mUploadVideoCompressNotifyTextView.setText(counter+
	                			""+getText(R.string.pb_compress_resizing_only_image3)+
	                			"1/"+UploadService.getSelectedVideoNum()+""
	                			+getText(R.string.pb_compress_resizing_only_video4));
                	}*/
                }
                break;
            
            case PBConstant.UPLOAD_PROCESS_COMPRESS_VIDEO_DONE :
            	System.out.println("Atik  called UPLOAD_PROCESS_COMPRESS_VIDEO_DONE");
            	counter++;
                if (mUploadNotifyTextView != null) {
                    //mUploadNotifyTextView.setText(getText(R.string.pb_compress_resizing)+"atik"+counter); //Comment by Atik
                   //mUploadNotifyTextView.setText("Total file is compressing right now:"+mUploadPhotoList.size());
                	
                	//if(/*!onlyImageFileUploaded*/true) {
                    	mUploadVideoCompressNotifyTextView.setText(counter+
                    			""+getText(R.string.pb_compress_resizing_only_video3)+
                    			counter+"/"+UploadService.getSelectedVideoNum()+""
                    			+getText(R.string.pb_compress_resizing_only_video4));
                	/*} else {
                    	mUploadVideoCompressNotifyTextView.setText(counter+
                    			""+getText(R.string.pb_compress_resizing_only_image3)+
                    			counter+"/"+UploadService.getSelectedVideoNum()+""
                    			+getText(R.string.pb_compress_resizing_only_video4));
                	}*/

                }
                
                break;      
                
            case PBConstant.UPLOAD_FORBIDDEN:
            	PBGeneralUtils.showAlertDialogActionWithOnClick(UploadingActivity.this, 
                        android.R.drawable.ic_dialog_alert, 
                        getString(R.string.pb_password_banned_title),
                        getString(R.string.pb_password_banned_context), 
                        getString(R.string.pb_btn_OK), 
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                                PBMainTabBarActivity.sMainContext.mTabHost
                                        .setCurrentTabByTag(PBConstant.DOWNLOAD_TAG);
                            }
                        }, false);
            	break;
                
            // 20120504 add for suport show dialog when compress error <S>
            case PBConstant.UPLOAD_COMPRESS_ERROR:
                PBGeneralUtils.showAlertDialogActionWithOnClick(UploadingActivity.this, 
                        android.R.drawable.ic_dialog_alert, 
                        null, 
                        getString(R.string.pb_compress_msg_compress_fail), 
                        getString(R.string.pb_btn_OK), 
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                                PBMainTabBarActivity.sMainContext.mTabHost
                                        .setCurrentTabByTag(PBConstant.DOWNLOAD_TAG);
                            }
                        }, false);
                break;
                // 20120504 add for suport show dialog when compress error <E>
                
            case PBConstant.UPLOAD_COMPRESS_FAIL:
                PBGeneralUtils.showAlertDialogActionWithOnClick(UploadingActivity.this, 
                        android.R.drawable.ic_dialog_alert, 
                        getString(R.string.pb_upload_video_msg_file_too_long_title), 
                        getString(R.string.pb_upload_video_msg_file_too_long_msg), 
                        getString(R.string.pb_btn_OK), 
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                                /*if(PBTabBarActivity.sMainContext != null){                                    
                                    PBTabBarActivity.sMainContext.finish();
                                }*/
                                // TODO back to Gallery for choosing diffirent image
                                Intent mIntent = new Intent(UploadingActivity.this, SelectMultipleImageActivity.class);
                                startActivity(mIntent);
                            }
                        }, false);
                break;
                
            case UPDATE_SCREEN_ERROR:
                boolean sdcardExist = PBGeneralUtils.checkSdcard(
                            UploadingActivity.this, (!mIsActivityDestroyed),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            });

                if (!sdcardExist) {
                    cancelUpload(UploadingActivity.this, mUploadedUrlList);
                    break;
                }
                Log.e(TAG, "Handle error when upload...........");
                // @lent5 change title action bar #S
                ActionBar headerBar = (ActionBar) findViewById(R.id.headerbar);
                setHeader(headerBar, getString(R.string.upload_fail_title));
                mRetryBtn.setEnabled(true);
                mNotRetryBtn.setEnabled(true);
                // @lent5 change title action bar #E
                findViewById(R.id.uploading_bear_error).setVisibility(
                        View.VISIBLE);
                findViewById(R.id.uploading_bear_normal).setVisibility(
                        View.GONE);
                findViewById(R.id.uploading_screen_advert).setVisibility(
                        View.GONE);
                findViewById(R.id.btn_cancel_upload).setVisibility(View.GONE);
                findViewById(R.id.error_btn_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.textview_error_layout).setVisibility(
                        View.VISIBLE);
                // setHeader(mHeaderBar, getString(R.string.upload_fail_title));
                break;
            case PBConstant.UPLOAD_FINISH:
                Log.e(TAG, "UPLOADING_ACTIVITY finish() when upload...........");
                // startUploadConfirmActivity();
                System.out.println("Atik called UPLOAD_FINISH ");
                break;
            case PBConstant.UPLOAD_FINISH_COMPLETED:
                Log.e(TAG, "UPLOAD_FINISH_COMPLETED finish() when upload...........");
                System.out.println("Atik called UPLOAD_FINISH_COMPLETED ");
                

                
                startUploadConfirmActivity();
                break;
            default:
                break;
            }
            return false;
        }
    });
   
    private void startUploadConfirmActivity(){
    	
    	//System.out.println("Atik startUploadConfirmActivity called");
    	
        Bundle extras = new Bundle();
        extras.putString(PBConstant.COLLECTION_PAGE_NAME, UploadingActivity.class.getName());
        extras.putString(PBConstant.COLLECTION_ID, 
                PBPreferenceUtils.getStringPref(getBaseContext(),
                        PBConstant.UPLOAD_SERVICE_PREF, PBConstant.PREF_COLLECTION_ID, ""));
        extras.putString(PBConstant.COLLECTION_PASSWORD, 
                getIntent().getStringExtra(PBConstant.INTENT_PASSWORD));
        extras.putLong(PBConstant.COLLECTION_CHARGE_AT,
                PBPreferenceUtils.getLongPref(getBaseContext(),
                        PBConstant.UPLOAD_SERVICE_PREF,PBConstant.PREF_CHARGE_DATE, 0));
        extras.putString(PBConstant.COLLECTION_THUMB, 
                PBPreferenceUtils.getStringPref(getBaseContext(),
                        PBConstant.UPLOAD_SERVICE_PREF, PBConstant.PREF_COLLECTION_THUMB, ""));

        Intent intent = new Intent(getBaseContext(), PBConfirmPasswordActivity.class);
        intent.putExtra("data", extras);
        /*intent.putExtra(PBConstant.IS_OWNER, 
       		 getIntent().getBooleanExtra(PBConstant.IS_OWNER, false));*/
        intent.putExtra(PBConstant.IS_OWNER, 
          		 getIntent().getBooleanExtra(PBConstant.IS_OWNER, true));
        startActivityForResult(intent, PBConstant.REQUEST_CODE_OPEN_CONFIRMPASS);
        finish();
    }
    
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            ((UploadService.LocalBinder) service).getService();
            // Tell the user about this for our demo.
            // Toast.makeText(UploadingActivity.this, "Service connected",
            // Toast.LENGTH_SHORT).show();
        }

        public void onServiceDisconnected(ComponentName className) {
            // Toast.makeText(UploadingActivity.this, "Service disconnected",
            // Toast.LENGTH_SHORT).show();
        }
    };
    private boolean mIsBound;
    private boolean mIsActivityDestroyed = false;

    void doBindService() {
        // Establish a connection with the service. We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).
        bindService(new Intent(this, UploadService.class), mConnection,
                Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // V2 google analytics has been comment out
        /*if (PBTabBarActivity.gaTracker != null) {
        	PBTabBarActivity.gaTracker.trackPageView("UploadingActivity");
        }*/
        
        mIsActivityDestroyed = false;
        
        boolean sdcardExist = true;
        if (!mIsActivityDestroyed) {
            sdcardExist = PBGeneralUtils.checkSdcard(this, true,
                    new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
        }
        if (!sdcardExist) {
            cancelUpload(UploadingActivity.this, mUploadedUrlList);
        }
    }

   /* @Override
    protected void onStop() {
        super.onStop();
        mIsActivityDestroyed = true;
    }
*/
    
    
	 //Added below activity life cycle method for Google analytics
	@Override
    public void onStart() {
	     super.onStart();
	     System.out.println("Atik start Easy Tracker Uploading Activity");
	     EasyTracker.getInstance(this).activityStart(this);
    }
	  
    @Override
    protected void onStop() {
        super.onStop();
        mIsActivityDestroyed = true;
	    System.out.println("Atik stop Easy Tracker Uploading Activity");
	    EasyTracker.getInstance(this).activityStop(this);
    }
    
    
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIsActivityDestroyed = true;
        doUnbindService();
    }
    
    private EasyTracker easyTracker = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIsActivityDestroyed = false;
        mUploadProgressString = getString(R.string.uploading_progress_text);
        mActivity = this;
        setContentView(R.layout.pb_uploading_layout);
        easyTracker = EasyTracker.getInstance(this);
        
//        // check internet
        UploadServiceUtils.checkInternetConenction(
                UploadingActivity.this, (!mIsActivityDestroyed), true);

        mCancelBtn = (FButton) findViewById(R.id.btn_cancel_upload);
        mRetryBtn = (FButton) findViewById(R.id.btn_retry);
        mNotRetryBtn = (FButton) findViewById(R.id.btn_cancel);
        mCancelBtn.setOnClickListener(this);
        mRetryBtn.setOnClickListener(this);
        mNotRetryBtn.setOnClickListener(this);
        // @lent5 add action bar #S
        mHeaderBar = (ActionBar) findViewById(R.id.headerbar);
        setHeader(mHeaderBar, getString(R.string.screen_title_uploading));
        // @lent5 add action bar #E

        mUploadNotifyTextView = (TextView) findViewById(R.id.upload_progress_text);
        mUploadVideoCompressNotifyTextView = (TextView) findViewById(R.id.upload_progress_video_compress_text);
        mUploadProgress = (ProgressBar) findViewById(R.id.seekbar_upload_progress);
        mUploadPhotoList = getIntent().getStringArrayListExtra(
                PBConstant.INTENT_SELECTED_MEDIA);
        mUploadPhotoTypeList = getIntent().getLongArrayExtra(
                PBConstant.INTENT_SELECTED_MEDIA_TYPE);
        mUploadProgress.setMax(mUploadPhotoList.size());
        mTotalUploadedPhoto = UploadService.getUploadedPhotoNum();
        mUploadProgress.setProgress(mTotalUploadedPhoto);
        // 20120509 wait for service update text first <S>
        
        if (PBConstant.COMPRESSORUPDLOAD && !TextUtils.isEmpty(mUploadProgressString)) {
            String progress = mUploadProgressString.replace("$", "" + ""
                    + mUploadPhotoList.size());
            progress = progress.replace("#", "" + mTotalUploadedPhoto);
            mUploadNotifyTextView.setText(progress);
        }
        // 20120509 wait for service update text first <E>
        // TODO update range for progress bar
        if (mUploadPhotoList.size() > 25) {
            mUploadProgress.setMax(mUploadPhotoList.size());
        } else {
            mUploadProgress.setMax(100);
        }

        if (UploadService.getIsStop()) {
            Intent serviceIntent = new Intent(getBaseContext(),
                    UploadService.class);
            serviceIntent.putStringArrayListExtra(
                    PBConstant.INTENT_SELECTED_MEDIA, mUploadPhotoList);
            serviceIntent.putExtra(
                    PBConstant.INTENT_SELECTED_MEDIA_TYPE, mUploadPhotoTypeList);
            startService(serviceIntent);
        }

        UploadService.setUpdateListener(new ServiceUpdateUIListener() {
			@Override
            public void updateUI(int uploadedPhotos,
                    ArrayList<MediaInfo> uploadedUrlPhoto) {

                if (uploadedUrlPhoto != null) {
                    mUploadedUrlList = uploadedUrlPhoto;
                    System.out.println("Atik called upload photo");
                }
                
                if (uploadedPhotos == PBConstant.UPLOAD_FINISH_COMPLETED) {
                    mHandler.sendEmptyMessage(PBConstant.UPLOAD_FINISH_COMPLETED);
                } else if (uploadedPhotos == PBConstant.UPLOAD_FINISH) {
                    mHandler.sendEmptyMessage(PBConstant.UPLOAD_FINISH);
                } else if (uploadedPhotos == PBConstant.UPLOAD_ERROR) {
                    mHandler.sendEmptyMessage(UPDATE_SCREEN_ERROR);
                } else if (uploadedPhotos == PBConstant.UPLOAD_COMPRESS_FAIL) {
                    mHandler.sendEmptyMessage(PBConstant.UPLOAD_COMPRESS_FAIL);
                } else if(uploadedPhotos == PBConstant.UPLOAD_PROCESS_COMPRESS_VIDEO) {
                	//onlyImageFileUploaded = false;
                    mHandler.sendEmptyMessage(PBConstant.UPLOAD_PROCESS_COMPRESS_VIDEO);
                } else if (uploadedPhotos == PBConstant.UPLOAD_COMPRESS_ERROR) {             // 20120504 add for suport show dialog when compress error
                    mHandler.sendEmptyMessage(PBConstant.UPLOAD_COMPRESS_ERROR);
                } else if(uploadedPhotos == PBConstant.UPLOAD_PROCESS_COMPRESS_VIDEO_DONE) {
                    mHandler.sendEmptyMessage(PBConstant.UPLOAD_PROCESS_COMPRESS_VIDEO_DONE);
                } else {// update UI
                    mTotalUploadedPhoto = uploadedPhotos;
                    mHandler.sendEmptyMessage(UPDATE_NUMBER_PHOTO_UPLOADED);
                }
            }

            @Override
            public void updateProgressBar(int uploadProgress) {
                mHandler.sendMessage(mHandler.obtainMessage(
                        UPDATE_PROGRESS_BAR, uploadProgress, 0));
                // Log.i("mapp", "currentLevelOfProgressBar = " + currentLevelOfProgressBar);
            }

            private boolean isProgressbarWasInitialized = false;
            @Override
            public void updateCompressBar(int totalFile, int percent) {
                // TODO code for update copress progress in here!
                if (!isProgressbarWasInitialized) {
                    if (mUploadProgress != null) {
                        if (totalFile > 25) {
                            mUploadProgress.setMax(totalFile);
                        } else {
                            mUploadProgress.setMax(100);
                        }
                        System.out.println("Atik called upload compress from here");
                        mHandler.sendEmptyMessage(PBConstant.UPLOAD_PROCESS_COMPRESS_VIDEO);
                        isProgressbarWasInitialized = true;
                    }
                }
                mHandler.sendMessage(mHandler.obtainMessage(
                        UPDATE_PROGRESS_BAR, percent, 0));
            }

			@Override
			public void onUploadForbidden() {
				mHandler.sendEmptyMessage(PBConstant.UPLOAD_FORBIDDEN);
			}

			@Override
			public void updateAnalytics(String data) {
				// TODO Auto-generated method stub
				 Log.d("main", data);
				 easyTracker.send(MapBuilder.createEvent("ScreenName-> UploadingActivity", "VideoCompression",
						 data, null).build());
			}
        });
        // startService(mServiceIntent);
        // doBindService();
    }

    

    private void createExitConfirmTOCancelUpload() {
        if (!mIsActivityDestroyed) {
            PBGeneralUtils.showAlertDialogAction(this,
                    getString(R.string.dialog_cancel_upload_title),
                    getString(R.string.dialog_cancel_upload_body),
                    getString(R.string.dialog_ok_btn),
                    getString(R.string.uploading_cancel_btn_text),
                    new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // 20120225 @lent fix issue key back during uploading
                    cancelUpload(UploadingActivity.this, mUploadedUrlList);
                    finish();
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mCancelBtn.setEnabled(true);
                    dialog.cancel();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        createExitConfirmTOCancelUpload();
        // super.onBackPressed();
    }

    // @lent5 add action bar #S
    @Override
    protected void handleHomeActionListener() {
        // show alert dialog
        createExitConfirmTOCancelUpload();
    }

    @Override
    protected void setLeftBtnsDetailHeader(ActionBar headerBar) {
        // Do nothing here
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_cancel_upload:
            mCancelBtn.setEnabled(false);
            createExitConfirmTOCancelUpload();
            // cancelUpload();
            break;
        case R.id.btn_cancel:
            mNotRetryBtn.setEnabled(false);
            // 20120225 @lent fix issue key back during uploading
            cancelUpload(UploadingActivity.this, mUploadedUrlList);
            finish();
            break;
        case R.id.btn_retry:
            mRetryBtn.setEnabled(false);
            mCancelBtn.setEnabled(true);
            mServiceIntent = new Intent(getBaseContext(), UploadService.class);
            stopService(mServiceIntent);
            mServiceIntent.putExtra(PBConstant.INTENT_UPLOADED_PHOTO_NUM,
                    mTotalUploadedPhoto);
            mServiceIntent.putStringArrayListExtra(
                    PBConstant.INTENT_SELECTED_MEDIA, mUploadPhotoList);
            mServiceIntent.putExtra(
                    PBConstant.INTENT_SELECTED_MEDIA_TYPE, mUploadPhotoTypeList);
            startService(mServiceIntent);
            findViewById(R.id.uploading_bear_error).setVisibility(View.GONE);
            /*findViewById(R.id.uploading_bear_normal)
            .setVisibility(View.VISIBLE);  // code comment out by Atik
*/            
            findViewById(R.id.uploading_bear_normal)
            .setVisibility(View.GONE);  // code comment out by Atik
            findViewById(R.id.uploading_screen_advert)
            .setVisibility(View.VISIBLE);  // code comment out by Atik
            
            
            findViewById(R.id.btn_cancel_upload).setVisibility(View.VISIBLE);
            findViewById(R.id.error_btn_layout).setVisibility(View.GONE);
            findViewById(R.id.textview_error_layout).setVisibility(View.GONE);
            setHeader(mHeaderBar, getString(R.string.screen_title_uploading));
            break;
        default:
            break;
        }
    }

    // 20120225 @lent fix issue key back during uploading #S
    @SuppressWarnings("unchecked")
    public static void cancelUpload(Context context, ArrayList<MediaInfo> uploadedUrlPhoto) {
        UploadService.stopService();
        context.stopService(new Intent(context, UploadService.class));

        CancelUploadingTask mCancelUploadTask = new CancelUploadingTask(context);
        if (mCancelUploadTask != null
                && mCancelUploadTask.getStatus() != AsyncTask.Status.RUNNING) {
            try {
                mCancelUploadTask.execute();
            } catch (IllegalStateException e) {
                Log.w(PBConstant.TAG, e.getMessage());
            }
        }
        // 20120301 @lent5 avoid ConcurrentModificationException
        if(uploadedUrlPhoto != null){
            ArrayList<MediaInfo> photos = (ArrayList<MediaInfo>) uploadedUrlPhoto.clone();
            UploadServiceUtils.deleteCachePhoto(photos);
            if(photos != null){
                photos.clear();
                photos = null;
            }
        }
        
    }

    // 20120225 @lent fix issue key back during uploading #E
    @Override
    protected void sdcardUnmountChecking() {
        super.sdcardUnmountChecking();
        // 20120228 @lent5 should call before create dialog
        // avoid in case user press back, this cancel could not reached
        cancelUpload(UploadingActivity.this, mUploadedUrlList);

        if (!mIsActivityDestroyed) {
            PBGeneralUtils.checkSdcard(this, true,
                    new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // 20120228 @lent5 should call before create dialog
                    // avoid in case user press back, this cancel could not reached
                    // cancelUpload(UploadingActivity.this);
                    finish();
                }
            });
        }else{
            if(!PBGeneralUtils.checkSdcard(this, false, null)){
                finish();
            }
        }
    }

}
