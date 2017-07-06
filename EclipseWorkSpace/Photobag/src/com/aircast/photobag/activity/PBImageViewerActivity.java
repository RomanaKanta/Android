package com.aircast.photobag.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ViewFlipper;

import com.google.analytics.tracking.android.EasyTracker;
import com.aircast.photobag.R;
import com.aircast.photobag.application.PBApplication;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.database.PBDatabaseDefinition;
import com.aircast.photobag.database.PBDatabaseManager;
import com.aircast.photobag.model.PBHistoryPhotoModel;
import com.aircast.photobag.utils.PBBitmapUtils;
import com.aircast.photobag.utils.PBGeneralUtils;
import com.aircast.photobag.utils.PBPreferenceUtils;
import com.aircast.photobag.utils.PBRewardUtils;
import com.aircast.photobag.widget.PBCustomImageView;
import com.aircast.photobag.widget.PBCustomVideoView;
import com.aircast.photobag.widget.PBCustomWaitingProgress;
import com.aircast.photobag.widget.PBVideoControllerView;

/**
 * Show photo/video.
 * */
public class PBImageViewerActivity extends Activity{

    private static final String TAG = "PBImageViewerActivity";
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private GestureDetector gestureDetector;
    View.OnTouchListener gestureListener;
    private Animation slideLeftIn;
    private Animation slideLeftOut;
    private Animation slideRightIn;
    private Animation slideRightOut;
    private ViewFlipper viewFlipper;
    // private int currentView = 0;
    // List<String> ImageList;

    private int currentIndex = 0;
    private String mCollectionId;
    private int mSaveMark = 1;
    private String mAdLink = "";
    private String mPassword = "";
    // private String mCollectionPassword;
    private ArrayList<PBHistoryPhotoModel> mPhotosList;

    private int maxIndex = 0;

    private HashMap<Integer, Bitmap> mImageBitmapSoftList;

    private FrameLayout mHeaderMenuLayout;
    private Button mMenuShowPrevPhoto;
    private Button mMenuShowNextPhoto;
    private Button mMenuSavePhoto;
    private Button mMenuSharePhoto;
    private Button mMenuReportPhoto;
    // private ImageView mVideoIcon;
    
    private ImageView mPlayVideoBtn;

    /** Variable for controlling header menu.*/
    private boolean mIsShowing = true;
    /** Time for showing header menu on the screen. */
    public static final int sDefaultTimeout = 5000;

    /** The Constant FADE OUT */
    protected static final int MSG_FADE_OUT = 1;
    protected static final int MSG_SHOW_PROGRESS = 2;
    protected static final int MSG_HIDE_PROGRESS = 3;
    protected static final int MSG_SHOW_PLAY_VIDEO_BTN = 4;

//    private boolean mIsSwipeToLeft = false;
//    private boolean mIsSwipeToRight = false;

    private boolean mIsSwipeAnimationPlaying = false;
    
    // 20120417 added by NhatVT for support video playback function
    private PBCustomVideoView mVideoView;
    private PBVideoControllerView mVideoControllerView;
    private boolean mIsInVideoPlayback = false;
    
    /**
     * Waiting progress dialog.
     */
    private PBCustomWaitingProgress mCustomWaitingLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.pb_image_viewer);

        mHeaderMenuLayout = (FrameLayout) findViewById(R.id.menu_imv_layout);

        mMenuShowPrevPhoto = (Button) findViewById(R.id.btn_view_prev_photo);
        mMenuShowNextPhoto = (Button) findViewById(R.id.btn_view_next_photo);
        mMenuSavePhoto = (Button) findViewById(R.id.btn_view_set_photo);
        mMenuSharePhoto = (Button) findViewById(R.id.btn_view_share_photo);
        mMenuReportPhoto = (Button) findViewById(R.id.btn_view_report_photo);
        mMenuShowNextPhoto.setOnClickListener(mOnClickListener);
        mMenuShowPrevPhoto.setOnClickListener(mOnClickListener);
        mMenuSavePhoto.setOnClickListener(mOnClickListener);
        mMenuSharePhoto.setOnClickListener(mOnClickListener);
        mMenuReportPhoto.setOnClickListener(mOnClickListener);
        // mVideoIcon = (ImageView) findViewById(R.id.image_thump_video);
        mVideoView = (PBCustomVideoView) findViewById(R.id.video_surface);
        mVideoControllerView = (PBVideoControllerView) findViewById(R.id.video_controller_id);
        mVideoControllerView.setVideoView(mVideoView);
        mVideoView.addVideoController(mVideoControllerView);
        
        mVideoView.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (mVideoControllerView != null) {
                    mVideoControllerView.setVisibilityOfController(View.GONE);
                }
                mVideoView.setVisibility(View.GONE);
                viewFlipper.setVisibility(View.VISIBLE);
                mIsInVideoPlayback = false;
                mHandler.sendEmptyMessage(MSG_SHOW_PLAY_VIDEO_BTN);
            }
        });
        
        mPlayVideoBtn = (ImageView) findViewById(R.id.btn_play_video);
        mPlayVideoBtn.setOnClickListener(mOnClickListener);

        // handling error while start or playing video
        mVideoView.setOnErrorListener(new OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                PBApplication.makeToastMsg(getString(R.string.video_err));
                /*mVideoView.setVisibility(View.GONE);
                viewFlipper.setVisibility(View.VISIBLE);*/
                return true;
            }
        });
        
        // get data from calling Intent.
        Intent receiveIntent = getIntent();
        mCollectionId = "";
        if (receiveIntent != null) {
            currentIndex = receiveIntent.getIntExtra("PHOTO_ID", 0);
            mCollectionId = receiveIntent.getStringExtra("COLECTION_ID");
            mSaveMark = receiveIntent.getIntExtra("COLECTION_SAVEMARK", 1);
            mPassword = receiveIntent.getStringExtra("COLECTION_PASSWORD");
            // mCollectionPassword = receiveIntent.getStringExtra("COLECTION_PASSWORD");
            
            if (mSaveMark == -1) {            	
            	findViewById(R.id.layout_image_save_area).setVisibility(View.GONE);
            	findViewById(R.id.layout_image_share_area).setVisibility(View.GONE);
            }
        }
        mPhotosList = PBDatabaseManager.getInstance(this).getPhotos(mCollectionId);
        if (mPhotosList == null || mPhotosList.size() == 0) {
            PBApplication.makeToastMsg("Cannot view this photo list, please try again!");//TODO
            return;
        }
        maxIndex = mPhotosList.size() - 1;
        mAdLink = receiveIntent.getStringExtra("COLECTION_ADLINK");

        mImageBitmapSoftList = new HashMap<Integer, Bitmap>(3);

        // initResourceForFlipperView();

        // setup ViewFlipper and Animation for it
        viewFlipper = (ViewFlipper) findViewById(R.id.flipper);

        slideLeftIn = AnimationUtils.loadAnimation(this, R.anim.slide_left_in);
        slideLeftOut = AnimationUtils.loadAnimation(this, R.anim.slide_left_out);
        slideRightIn = AnimationUtils.loadAnimation(this, R.anim.slide_right_in);
        slideRightOut = AnimationUtils.loadAnimation(this, R.anim.slide_right_out);

        slideLeftIn.setAnimationListener(mAnimationListener);
        slideLeftOut.setAnimationListener(mAnimationListener);
        slideRightIn.setAnimationListener(mAnimationListener);
        slideRightOut.setAnimationListener(mAnimationListener);

        initResourceForFlipperView();

        // setup screen gesture detector.
        gestureDetector = new GestureDetector(new FlipperGestureDetector());
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (gestureDetector.onTouchEvent(event)) {
                    return true;
                }
                return false;
            }
        };
        showHeaderMenu();
        
        if (mCustomWaitingLayout == null) {
            mCustomWaitingLayout = new PBCustomWaitingProgress(this);
        }
        
        mHandler.sendEmptyMessage(MSG_SHOW_PLAY_VIDEO_BTN);
        
    }

    private void decodeAndPutCache(String photoPath, String photoUrl,
            int putToPos) {
        if (TextUtils.isEmpty(photoPath)) {
            return;
        }
        Options mOptions = new Options();
        // 20120313 mod by NhatVT, no need using sample size in this case
        mOptions.inSampleSize = /*PBBitmapUtils.sampleSizeNeeded(photoPath,
                PBConstant.MAXIMUM_IMAGE_SIZE_UPLOAD)*/1;
        if (mImageBitmapSoftList != null) {
            try {
                Bitmap bmpCached = BitmapFactory.decodeFile(photoPath,
                        mOptions);
                if (bmpCached == null) {
                    if (!PBBitmapUtils.isPhotoValid(photoPath)) {
                        // TODO download file if sdcard was changed
                        // createDownloadTask(photoUrl);
                        // update spec: show msg when file is missing!
                        PBApplication.makeToastMsg(getString(R.string.pb_file_not_found));
                    }
                }
                // remove from cache first
                if (mImageBitmapSoftList.get(putToPos) != null) {
                    Bitmap bmpTmp = mImageBitmapSoftList.get(putToPos);
                    if (bmpTmp != null) {
                        bmpTmp.recycle();
                        bmpTmp = null;
                    }
                }

                mImageBitmapSoftList.put(putToPos, (bmpCached));
            } catch (OutOfMemoryError e) {
                Log.e(TAG, ">>> OOM when prepare image for caching!");
            }
        }
    }
    
    /**
     * Init resource for ViewFlipper when first view on screen.
     */
    private void initResourceForFlipperView() {
        String photoPath = mPhotosList.get(currentIndex).getPhoto();
        photoPath = PBGeneralUtils.getPathFromCacheFolder(photoPath); 

        // decodeAndPutCache(photoPath, mPhotosList.get(currentIndex).getPhoto(), 0);
        
        PBCustomImageView imageview = ((PBCustomImageView) viewFlipper.getChildAt(0));
        
        if (mPhotosList.get(currentIndex).getMediaType() == PBDatabaseDefinition.MEDIA_VIDEO) {

            // will get video thumb was cached from server when cannot extract thumb from video 
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            // BitmapFactory.decodeFile(photoPath, options);
            String pathTmp = photoPath + PBConstant.VIDEO_THUMB_STR;
            BitmapFactory.decodeFile(/*photoPath*/pathTmp, options);
            if (options.outHeight <= 0) {
                /*photoPath*/pathTmp = PBGeneralUtils.getPathFromCacheFolder(mPhotosList.get(currentIndex).getThumb());
            }
            decodeAndPutCache(/*photoPath*/pathTmp, mPhotosList.get(currentIndex).getThumb(), 0);

            imageview.setScaleType(ScaleType.CENTER_INSIDE);
            imageview.setItemCanPlay(true);
            // mVideoIcon.setVisibility(View.VISIBLE);
        } else {
            // mVideoIcon.setVisibility(View.GONE);
            decodeAndPutCache(photoPath, mPhotosList.get(currentIndex).getPhoto(), 0);
            imageview.setItemCanPlay(false);
        }
        imageview.setImageBitmap(mImageBitmapSoftList.get(0));
        
        viewFlipper.setDisplayedChild(0);
        // currentView = 0;
        checkShowHideButton();
    }

    /**
     * Support playing swipe animation.
     */
    private AnimationListener mAnimationListener = new AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            mIsSwipeAnimationPlaying = true;
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            mIsSwipeAnimationPlaying = false;
            mHandler.sendEmptyMessage(MSG_SHOW_PLAY_VIDEO_BTN);
        }
    };

    /**
     * Views OnClickListener.
     */
    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            case R.id.btn_view_prev_photo:
                if (!mIsSwipeAnimationPlaying) {
                    showPrevPhoto();
                }
                break;

            case R.id.btn_view_next_photo:
                if (!mIsSwipeAnimationPlaying) {
                    showNextPhoto();
                }
                break;

            case R.id.btn_view_set_photo:
                showHeaderMenu();
                if (!PBGeneralUtils.checkSdcard(
                        PBImageViewerActivity.this,
                        true, null)) {
                    return;
                }
                
                // pause playback when saving file to sdcard
                if (mIsInVideoPlayback) {
                    if (mVideoView != null && mVideoView.isPlaying()) {
                        mVideoView.pausePlayback();
                    }
                }
                
                // PBApplication.makeToastMsg("save photo");
                // 20120313 mod by NhatVT, save image directly <S> 
                /*Bitmap bmpToSave = mImageBitmapSoftList.get(currentView);
                if (bmpToSave != null) {
                    PBGeneralUtils.saveInCameraRoll(
                            PBImageViewerActivity.this,
                            bmpToSave);
                    // Log.i("mapp", ">>> Save image to Camera folder OK!");
                } else {
                    // Log.i("mapp", ">>> Cannot save image to Camera folder!");
                }*/
                mHandler.sendEmptyMessage(MSG_SHOW_PROGRESS);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String photoPath = mPhotosList.get(currentIndex).getPhoto();
                        photoPath = PBGeneralUtils.getPathFromCacheFolder(photoPath);
                        if (!TextUtils.isEmpty(photoPath)) {
                            PBGeneralUtils.saveInCameraRoll(
                                    PBImageViewerActivity.this,
                                    photoPath,
                                    mPhotosList.get(currentIndex).getMediaType(),
                                    mPhotosList.get(currentIndex).getVideoDuration());
                            Log.i("mapp", ">>> Save image to Camera folder OK!");
                        } else {
                            Log.i("mapp", ">>> Cannot save image to Camera folder!");
                        }
                        mHandler.sendEmptyMessage(MSG_HIDE_PROGRESS);
                    }
                }).start();
                
                // 20120313 mod by NhatVT, save image directly <E> 
                break;

            case R.id.btn_view_share_photo:
                showHeaderMenu();
                if (!PBGeneralUtils.checkSdcard(
                        PBImageViewerActivity.this,
                        true, null)) {
                    return;
                }
                
                // pause playback when sharing file
                if (mIsInVideoPlayback) {
                    if (mVideoView != null && mVideoView.isPlaying()) {
                        mVideoView.pausePlayback();
                    }
                }
                
                mHandler.sendEmptyMessage(MSG_SHOW_PROGRESS);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // PBApplication.makeToastMsg("share photo");
                        String filePath = mPhotosList.get(currentIndex).getPhoto();
                        filePath = PBGeneralUtils.getPathFromCacheFolder(filePath);
                        // String filePath = mViewFileName[currentView];
                        if (!TextUtils.isEmpty(filePath)) {
                            try {
                                String desFilePath = PBGeneralUtils.getCacheFolderPath()
                                		+ PBConstant.SPLASH_CHAR + filePath.hashCode();
                                /*+ PBConstant.CACHE_SHARE_FILE_NAME;*/
                                if (new File(desFilePath).mkdirs()) {
                                    Log.i("mapp", "> Create temp folder for sharing OK!");
                                }
                                
                                String ext = "";
                                if (mPhotosList.get(currentIndex).getMediaType() == PBDatabaseDefinition.MEDIA_PHOTO) {
                                    ext = PBConstant.MEDIA_PHOTO_EXT_JPG;
                                } else {
                                    ext = PBConstant.MEDIA_VIDEO_EXT_3GP;
                                }
                                desFilePath = desFilePath 
                                        + PBConstant.SPLASH_CHAR
                                        + PBConstant.CACHE_SHARE_FILE_NAME + ext;
                                // if (copy(filePath, desFilePath)) {
                                if (PBGeneralUtils.copyFile(filePath, desFilePath)) {
                                    // Log.i("mapp", ">>> file path want to share: " + filePath);
                                    PBGeneralUtils.photoViewerSharing(
                                            PBImageViewerActivity.this, 
                                            desFilePath);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                Log.e(TAG, ">>> cannot create and share file!");
                            }
                        }
                        mHandler.sendEmptyMessage(MSG_HIDE_PROGRESS);
                    }
                }).start();
                break;

            case R.id.btn_view_report_photo:
           	 showHeaderMenu();
           	 
               if (!PBGeneralUtils.checkSdcard(
                       PBImageViewerActivity.this,
                       true, null)) {
                   return;
               }
         
               // pause playback when sharing file
               if (mIsInVideoPlayback) {
                   if (mVideoView != null && mVideoView.isPlaying()) {
                       mVideoView.pausePlayback();
                   }
               }
               //send Report message
               final Intent intentSend = new Intent(android.content.Intent.ACTION_SEND);
           	String[] recipients = new String[] {};
           	intentSend.putExtra(android.content.Intent.EXTRA_EMAIL, recipients);
           	intentSend.putExtra(android.content.Intent.EXTRA_SUBJECT,
                       getString(R.string.pb_app_name));
           	String sendMessage = "Password : " + mPassword;
           	
           	sendMessage += "\n" + getString(R.string.pb_report_it_msg);
           	sendMessage += String.format(getString(
                       R.string.pb_setting_contactweb_send_message,
                       PBPreferenceUtils.getStringPref(PBApplication
                               .getBaseApplicationContext(), PBConstant.PREF_NAME,
                               PBConstant.PREF_NAME_UID, getString(R.string.pb_setting_contact_code))));
           	sendMessage +="";
               sendMessage += "\nMobile Type: " + Build.MODEL; 
               sendMessage += "\nOs: Android v" + Build.VERSION.RELEASE;
               sendMessage += "\nAPI lv:" + Build.VERSION.SDK_INT;
               try {
   				sendMessage += "\nApp version:"
   							+ getPackageManager().getPackageInfo(getPackageName(),0).versionName;
   			} catch (NameNotFoundException e) {
   				e.printStackTrace();
   			}
               sendMessage += "\nLanguage :" + Locale.getDefault().getDisplayLanguage();
               
               String[] emailRecipients = new String[] { getString(R.string.pb_setting_contact_mail)};
               intentSend.putExtra(android.content.Intent.EXTRA_EMAIL,
                       emailRecipients);
               intentSend.putExtra(android.content.Intent.EXTRA_TEXT, sendMessage);
               intentSend.setType("plain/text");
               
               new Thread(new Runnable() {
                   @Override
                   public void run() {
                       // PBApplication.makeToastMsg("share photo");
                       String filePath = mPhotosList.get(currentIndex).getPhoto();
                       filePath = PBGeneralUtils.getPathFromCacheFolder(filePath);
                       // String filePath = mViewFileName[currentView];
                       if (!TextUtils.isEmpty(filePath)) {
                           try {
                               String desFilePath = PBGeneralUtils.getCacheFolderPath()
                               		+ PBConstant.SPLASH_CHAR + filePath.hashCode();
                               /*+ PBConstant.CACHE_SHARE_FILE_NAME;*/
                               if (new File(desFilePath).mkdirs()) {
                                   Log.i("mapp", "> Create temp folder for reporting OK!");
                               }
                               
                               String ext = "";
                               if (mPhotosList.get(currentIndex).getMediaType() == PBDatabaseDefinition.MEDIA_PHOTO) {
                                   ext = PBConstant.MEDIA_PHOTO_EXT_JPG;
                               } else {
                                   ext = PBConstant.MEDIA_VIDEO_EXT_3GP;
                               }
                               desFilePath = desFilePath 
                                       + PBConstant.SPLASH_CHAR
                                       + PBConstant.CACHE_SHARE_FILE_NAME + ext;
                               // if (copy(filePath, desFilePath)) {
                               if (PBGeneralUtils.copyFile(filePath, desFilePath)) {
                                   // Log.i("mapp", ">>> file path want to share: " + filePath);
                               	File photo = new File(desFilePath); 
                               	intentSend.putExtra(android.content.Intent.EXTRA_STREAM, Uri.fromFile(photo));
                               	intentSend.setType("image/png");
                               	startActivity(intentSend);
                               }
                           } catch (IOException e) {
                               e.printStackTrace();
                               Log.e(TAG, ">>> cannot create and report file!");
                           }
                       }
                   }
               }).start();
                   
           break;
    
                
            case R.id.btn_play_video:
                // TODO play button click
                if (mPhotosList.get(currentIndex).getMediaType() == PBDatabaseDefinition.MEDIA_VIDEO) {
                    if (mVideoView != null && !mIsInVideoPlayback) {
                        hideHeaderMenu();
                        // mVideoView.setVisibility(View.VISIBLE);
                        viewFlipper.setVisibility(View.GONE);
                        String videoFilePath = PBGeneralUtils.getPathFromCacheFolder(mPhotosList.get(currentIndex).getPhoto());
                        mVideoView.setCanAutoPlayWhenResumeVideoView(true); // 20120509 disable autoplay function
                        mVideoView.setVideoPath(videoFilePath);
                        mVideoView.updateSeekBar();
                        mIsInVideoPlayback = true;
                        showHeaderMenu();
                        // show all menu again!
                        mPlayVideoBtn.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mVideoView.setVisibility(View.VISIBLE);
                                showHeaderMenu();
                            }
                        }, 250);
                        // showHeaderMenu();
                        // hide play icon
                        mPlayVideoBtn.setVisibility(View.GONE);
                        
                        // saving playback state
                        PBPreferenceUtils.saveBoolPref(
                                getApplicationContext(), 
                                PBConstant.PREF_NAME, 
                                PBConstant.PREF_IS_VIDEO_PLAYING, 
                                true);
                    }
                }
                break;
                
            default:
                break;
            }
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        
        releaseResource();
    };

    /**
     * Method for release unused resource. 
     */
    private void releaseResource() {
        Log.e(TAG, ">>> release unused resources for photoviewer!");
        int listSize = mImageBitmapSoftList.size();
        for (int i = 0; i < listSize; i++) {
            if (mImageBitmapSoftList.get(i) != null) {
                mImageBitmapSoftList.get(i).recycle();
            }
        }
        mImageBitmapSoftList.clear();
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // show all menu item when rotate screen!
        showHeaderMenu();
    }

    @Override
    protected void onResume() {
        super.onResume();

        hideHeaderMenu();
        if (mVideoView != null && isActivityPaused && mIsInVideoPlayback) {
            mVideoView.postDelayed(new Runnable() {
                @Override
                public void run() {
                        showHeaderMenu();
                }
            }, 250);
        }
        showHeaderMenu();
        isActivityPaused = false;
    }
    
	//Added below activity life cycle method for Google analytics
	@Override
    public void onStart() {
 	    super.onStart();
	    System.out.println("Atik start Easy Tracker for PBImageViewerActivity");
	    EasyTracker.getInstance(this).activityStart(this);
    }
  
    //Added below activity life cycle method for Google analytics
    @Override
    public void onStop() {
	    super.onStop();
	    System.out.println("Atik stop Easy Tracker for PBImageViewerActivity");
	    EasyTracker.getInstance(this).activityStop(this);
    }
    
    private boolean isActivityPaused = false;
    
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(PBConstant.TAG, "Image viewer ===>>>>> onPause()");
        if (mIsInVideoPlayback) {
            if (mVideoView != null && mVideoView.isPlaying()) {
                mVideoView.setCanAutoPlayWhenResumeVideoView(false);
                mVideoView.pausePlayback();
            }
            mVideoView.setCanAutoPlayWhenResumeVideoView(false);
            isActivityPaused = true;
        }
    }
    
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
            case MSG_FADE_OUT:
                hideHeaderMenu();
                break;
                
            case MSG_SHOW_PROGRESS:
                if (mCustomWaitingLayout != null) {
                    mCustomWaitingLayout.showWaitingLayout();
                }
                break;
                
            case MSG_HIDE_PROGRESS:
                if (mCustomWaitingLayout != null) {
                    mCustomWaitingLayout.hideWaitingLayout();
                }
                break;
                
            case MSG_SHOW_PLAY_VIDEO_BTN:
                try{
                    if (mPhotosList != null && mPlayVideoBtn != null) {
                        if (mPhotosList.get(currentIndex).getMediaType() == PBDatabaseDefinition.MEDIA_VIDEO) {
                            mPlayVideoBtn.setVisibility(View.VISIBLE);
                        } else {
                            mPlayVideoBtn.setVisibility(View.GONE);
                        }
                    }
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
                break;
                
            default:
                break;
            }
        };
    };

    /**
     * Method for checking the visibility of header menu.
     * @return return <b>true</b> if the header menu is visible, otherwise.
     */
    public boolean isHeaderMenuShowing(){
        if (mHeaderMenuLayout != null) {
            if (mHeaderMenuLayout.getVisibility() == View.GONE) {
                return false;
            }
        }
        return true;
    }

    /** Hide the header menu. */
    public void hideHeaderMenu() {
        if (mIsShowing) {
            mIsShowing = false;
            if (mHeaderMenuLayout != null) {
                mHeaderMenuLayout.setVisibility(View.GONE);
            }
            if (mIsInVideoPlayback && mVideoControllerView != null) {
                mVideoControllerView.setVisibilityOfController(View.GONE);
            }
        }
    }

    /**
     * Show the header menu on screen. It will go away automatically after 3
     * seconds of inactivity.
     */
    public void showHeaderMenu() {
        if (mVideoControllerView != null) {
            mVideoControllerView.updateSeekBarInfo();
        }
        show(sDefaultTimeout);
    }

    /**
     * Show the header menu on screen. It will go away
     * automatically after 'timeout' milliseconds of inactivity.
     * @param timeout The timeout in milliseconds. Use 0 to show
     * the controller until hide() is called.
     */
    public void show(int timeout) {
        if (!mIsShowing) {
            if (mHeaderMenuLayout != null)
                mHeaderMenuLayout.setVisibility(View.VISIBLE);
            mIsShowing = true;
            if (mIsInVideoPlayback && mVideoControllerView != null) {
                mVideoControllerView.setVisibilityOfController(View.VISIBLE);
            }
        }

        if (mIsShowing) {
            // remove FADE_OUT message and re-send again if timeout isn't empty.
            Message msg = mHandler.obtainMessage(MSG_FADE_OUT);
            mHandler.removeMessages(MSG_FADE_OUT);
            if (timeout != 0) {
                mHandler.sendMessageDelayed(msg, timeout);
            }
        }
    }

    /**
     * 
     * @author NhatVT
     *
     */
    class FlipperGestureDetector extends SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
        	if (!TextUtils.isEmpty(mAdLink) && currentIndex == maxIndex) {
				PBGeneralUtils.showAlertDialogAction(PBImageViewerActivity.this, 
						null,
						PBImageViewerActivity.this.getString(R.string.pb_msg_detail_ad_link_confirm), 
						PBImageViewerActivity.this.getString(R.string.dialog_ok_btn),
						PBImageViewerActivity.this.getString(R.string.dialog_cancel_btn),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								if (mAdLink.startsWith("photobagin://forest")) {
									if (mAdLink.contains("adways")) {
										PBImageViewerActivity.this.startActivity(
												PBRewardUtils.getAppdriverIntent(PBImageViewerActivity.this));
										return;
									}
									if (mAdLink.contains("gree")) {
										PBImageViewerActivity.this.startActivity(
												PBRewardUtils.getGreeIntent(PBImageViewerActivity.this));
										return;
									}
									PBImageViewerActivity.this.startActivity(
											new Intent(PBImageViewerActivity.this, PBAcornForestActivity.class));
								} else {
									Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mAdLink));
									PBImageViewerActivity.this.startActivity(intent);
								}
								
							    // V2 google analytics has been comment out 	 
								/*if (PBTabBarActivity.gaTracker != null) {
									PBTabBarActivity.gaTracker.trackPageView("PBADAchievementsClick");
								}*/
							}
						});
				return true;
			}
        	
            if (isHeaderMenuShowing()) {
                hideHeaderMenu();
            } else {
                showHeaderMenu();
            }
            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                float velocityY) {

            if (mIsSwipeAnimationPlaying) {
                return true;
            }

            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) {
                    return false;
                }
                // right to left swipe
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    if (maxIndex < 1) {
                        return true;
                    } else {
                        if (currentIndex == 0) {
                            showNextPhoto();
                        } else {
                            if (currentIndex == maxIndex) {
                                return true;
                            } else {
                                showNextPhoto();
                            }
                        }
                    }
                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    if (maxIndex < 1) {
                        return true;
                    } else {
                        if (currentIndex == 0) {
                            return true;
                        } else {
                            if (currentIndex == maxIndex) {
                                showPrevPhoto();
                            } else {
                                showPrevPhoto();
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    private void checkShowHideButton() {
        showHeaderMenu();
        if (maxIndex < 1) {
            mMenuShowPrevPhoto.setVisibility(View.GONE);
            mMenuShowNextPhoto.setVisibility(View.GONE);
            return;
        } else {
            if (currentIndex == 0) {
                mMenuShowNextPhoto.setVisibility(View.VISIBLE);
                mMenuShowPrevPhoto.setVisibility(View.GONE);
            } else {
                if (currentIndex == maxIndex) {
                    mMenuShowNextPhoto.setVisibility(View.GONE);
                    mMenuShowPrevPhoto.setVisibility(View.VISIBLE);
                } else {
                    mMenuShowNextPhoto.setVisibility(View.VISIBLE);
                    mMenuShowPrevPhoto.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    /**
     * Change to next photo in photo list.
     */
    protected void showNextPhoto() {
        // TODO swipe to LEFT
        viewFlipper.setInAnimation(slideLeftIn);
        viewFlipper.setOutAnimation(slideLeftOut);
        
        if (currentIndex + 1 > maxIndex) {
            currentIndex = maxIndex;
        } else {
            currentIndex = currentIndex + 1;
        }
        mPlayVideoBtn.setVisibility(View.GONE);
        setImageToViewFlipper(currentIndex);
    }

    /**
     * Change to prev photo in photo list.
     */
    protected void showPrevPhoto() {
        // TODO swipe to RIGHT
        viewFlipper.setInAnimation(slideRightIn);
        viewFlipper.setOutAnimation(slideRightOut);
        
        if (currentIndex - 1 < 0) {
            currentIndex = 0;
        } else {
            currentIndex = currentIndex - 1;
        }
        mPlayVideoBtn.setVisibility(View.GONE);
        setImageToViewFlipper(currentIndex);
    }
    
    private void setImageToViewFlipper(int photoIndex) {
        String photoPath = mPhotosList.get(photoIndex).getPhoto();
        photoPath = PBGeneralUtils.getPathFromCacheFolder(photoPath);

        // stop video playback if any
        if (mIsInVideoPlayback) {
            if (mVideoView != null) {
                if (mVideoView.isPlaying()) {
                    mVideoView.pausePlayback();
                    // mVideoView.resetSeekbarAndVideoPlayerToZero();
                }
                mVideoView.resetSeekbarAndVideoPlayerToZero();
            }
            if (mVideoControllerView != null) {
                mVideoControllerView.setVisibilityOfController(View.GONE);
            }
            mVideoView.setVisibility(View.GONE);
            viewFlipper.setVisibility(View.VISIBLE);
            mIsInVideoPlayback = false;
        }
                
        int displayedChild = viewFlipper.getDisplayedChild();
        
        PBCustomImageView imageview = ((PBCustomImageView) viewFlipper.getChildAt((displayedChild+1)%2)); 
        
        // TODO process display video icon
        if (mPhotosList.get(photoIndex).getMediaType() == PBDatabaseDefinition.MEDIA_VIDEO) {
            imageview.setScaleType(ScaleType.CENTER_INSIDE);
            
            // will get video thumb was cached from server when cannot extract thumb from video
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            String pathTmp = photoPath + PBConstant.VIDEO_THUMB_STR;
            BitmapFactory.decodeFile(/*photoPath*/pathTmp, options);
            if (options.outHeight <= 0) {
                /*photoPath*/pathTmp = PBGeneralUtils.getPathFromCacheFolder(mPhotosList.get(currentIndex).getThumb());
            }
            decodeAndPutCache(/*photoPath*/pathTmp, mPhotosList.get(photoIndex).getPhoto(), (displayedChild+1)%2);
            
            imageview.setItemCanPlay(true);
        } else {
            imageview.setScaleType(ScaleType.FIT_CENTER);
            decodeAndPutCache(photoPath, mPhotosList.get(photoIndex).getPhoto(), (displayedChild+1)%2);
            // mVideoIcon.setVisibility(View.GONE);
            imageview.setItemCanPlay(false);
        }
        
        imageview.setImageBitmap(mImageBitmapSoftList.get((displayedChild+1)%2));
        viewFlipper.setDisplayedChild((displayedChild+1)%2);
        
        // show hide next/prev button
        checkShowHideButton();
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gestureDetector.onTouchEvent(event)) {
            return true;
        } else {
            return false;
        }
    }
}
