package com.smartmux.filevaultfree.photo;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ViewFlipper;

import com.smartmux.filevaultfree.AppMainActivity;
import com.smartmux.filevaultfree.LoginWindowActivity;
import com.smartmux.filevaultfree.R;
import com.smartmux.filevaultfree.modelclass.CommonItemRow;
import com.smartmux.filevaultfree.utils.AppExtra;
import com.smartmux.filevaultfree.utils.FileManager;

public class SMImageViewerActivity extends AppMainActivity {

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
	 private boolean mIsShowing = true;
	// private int currentView = 0;
	// List<String> ImageList;

	private int currentIndex = 0;
	// private String mCollectionPassword;
	//private ArrayList<PBHistoryPhotoModel> mPhotosList;

	private int maxIndex = 0;

	private HashMap<Integer, Bitmap> mImageBitmapSoftList;

	private Button mMenuShowPrevPhoto;
	private Button mMenuShowNextPhoto;

	/** Variable for controlling header menu. */
	/** Time for showing header menu on the screen. */
	public static final int sDefaultTimeout = 5000;

	/** The Constant FADE OUT */
	protected static final int MSG_FADE_OUT = 1;
	protected static final int MSG_SHOW_PROGRESS = 2;
	protected static final int MSG_HIDE_PROGRESS = 3;
	protected static final int MSG_SHOW_PLAY_VIDEO_BTN = 4;

	private boolean mIsSwipeAnimationPlaying = false;
	private String path;
	private String folderName;
	private ArrayList<CommonItemRow> listItems = null;
	private FileManager fileManager;
	private String photoPath;
	private Button mShareButton;
	private FrameLayout mHeaderMenuLayout;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.photo_viewer);

		mHeaderMenuLayout = (FrameLayout) findViewById(R.id.menu_imv_layout);
		mMenuShowPrevPhoto = (Button) findViewById(R.id.btn_view_prev_photo);
		mMenuShowNextPhoto = (Button) findViewById(R.id.btn_view_next_photo);
		mShareButton=(Button) findViewById(R.id.share);
		mMenuShowNextPhoto.setOnClickListener(mOnClickListener);
		mMenuShowPrevPhoto.setOnClickListener(mOnClickListener);
		mShareButton.setOnClickListener(mOnClickListener);
		Intent receiveIntent = getIntent();
		if (receiveIntent != null) {
			currentIndex = receiveIntent.getIntExtra("PHOTO_ID", 0);
			folderName = receiveIntent.getStringExtra("folder");
			photoPath=receiveIntent.getStringExtra("filepath");
		}
		fileManager		= new FileManager();
		fileManager.setBackCode(getApplicationContext());
		// do mPhotosList =
		// PBDatabaseManager.getInstance(this).getPhotos(mCollectionId);
//		if (folderName == null || folderName.size() == 0) {
//			// PBApplication.makeToastMsg("Cannot view this photo list, please try again!");//TODO
//			return;
//		}
		

		mImageBitmapSoftList = new HashMap<Integer, Bitmap>(3);
		listItems = fileManager.getAllPhotos(getApplicationContext(), folderName);
		maxIndex = listItems.size() - 1;
		// initResourceForFlipperView();

		// setup ViewFlipper and Animation for it
		viewFlipper = (ViewFlipper) findViewById(R.id.flipper);

		slideLeftIn = AnimationUtils.loadAnimation(this, R.anim.slide_left_in);
		slideLeftOut = AnimationUtils
				.loadAnimation(this, R.anim.slide_left_out);
		slideRightIn = AnimationUtils
				.loadAnimation(this, R.anim.slide_right_in);
		slideRightOut = AnimationUtils.loadAnimation(this,
				R.anim.slide_right_out);


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
	}

	private void decodeAndPutCache(String photoPath,
			int putToPos) {
		if (TextUtils.isEmpty(photoPath)) {
			return;
		}
		Options mOptions = new Options();
		// 20120313 mod by NhatVT, no need using sample size in this case
		mOptions.inSampleSize = /*
								 * PBBitmapUtils.sampleSizeNeeded(photoPath,
								 * PBConstant.MAXIMUM_IMAGE_SIZE_UPLOAD)
								 */1;
		if (mImageBitmapSoftList != null) {
			try {
				Bitmap bmpCached = BitmapFactory
						.decodeFile(photoPath, mOptions);
				if (bmpCached == null) {
//					if (!PBBitmapUtils.isPhotoValid(photoPath)) {
//						// TODO download file if sdcard was changed
//						// createDownloadTask(photoUrl);
//						// update spec: show msg when file is missing!
//						PBApplication
//								.makeToastMsg(getString(R.string.pb_file_not_found));
//					}
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
		String photoPath = listItems.get(currentIndex).getFilePath();
		//photoPath = PBGeneralUtils.getPathFromCacheFolder(photoPath);


		ImageView imageview = ((ImageView) viewFlipper.getChildAt(0));

		// mVideoIcon.setVisibility(View.GONE);
		decodeAndPutCache(photoPath,
				0);
		imageview.setImageBitmap(mImageBitmapSoftList.get(0));

		viewFlipper.setDisplayedChild(0);
		// currentView = 0;
		checkShowHideButton();
	}

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
			case R.id.share:
				
					Bitmap bitmap = BitmapFactory.decodeFile(photoPath);

					Intent emailIntent = new Intent(
							Intent.ACTION_SEND);
					emailIntent.setType("image/jpeg");

					emailIntent.putExtra(Intent.EXTRA_SUBJECT,
							"Image");
					emailIntent.putExtra(Intent.EXTRA_TEXT,
							"Enjoy this image");
					String pathofBmp = Images.Media.insertImage(getApplicationContext()
							.getContentResolver(), bitmap, "title", null);
					Uri bmpUri = Uri.parse(pathofBmp);
					emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					emailIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
					startActivity(
							Intent.createChooser(emailIntent, "Send mail...")); 
			//	AppToast.show(getApplicationContext(), "share");
				
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
	protected void onResume() {
		super.onResume();

		int event_code = fileManager.getReturnCode(getApplicationContext());
		if(event_code == AppExtra.HOME_CODE ){
			Intent i = new Intent(SMImageViewerActivity.this, LoginWindowActivity.class);
	        startActivity(i);
		}
	}
	
	@Override
	 public boolean onKeyDown(int keyCode, KeyEvent event) {
	        if (keyCode == KeyEvent.KEYCODE_BACK){   
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


	@Override
	protected void onPause() {
		super.onPause();

	}

	
	
	
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
	            
	        }
	    }

	    /**
	     * Show the header menu on screen. It will go away automatically after 3
	     * seconds of inactivity.
	     */
	    public void showHeaderMenu() {
	       
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
		setImageToViewFlipper(currentIndex);
	}

	private void setImageToViewFlipper(int photoIndex) {
		
		 photoPath = listItems.get(photoIndex).getFilePath();
		// photoPath = PBGeneralUtils.getPathFromCacheFolder(photoPath);
		//AppToast.show(getApplicationContext(), photoPath);

		int displayedChild = viewFlipper.getDisplayedChild();

		ImageView imageview = ((ImageView) viewFlipper
				.getChildAt((displayedChild + 1) % 2));

		imageview.setScaleType(ScaleType.FIT_CENTER);
		decodeAndPutCache(photoPath,
				(displayedChild + 1) % 2);
		// mVideoIcon.setVisibility(View.GONE);

		imageview.setImageBitmap(mImageBitmapSoftList
				.get((displayedChild + 1) % 2));
		viewFlipper.setDisplayedChild((displayedChild + 1) % 2);

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
