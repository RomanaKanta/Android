package com.aircast.photobag.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.StaleDataException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aircast.photobag.R;
import com.aircast.photobag.api.PBAPIHelper;
import com.aircast.photobag.api.ResponseHandle;
import com.aircast.photobag.api.ResponseHandle.Response;
import com.aircast.photobag.application.PBApplication;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.database.PBDatabaseManager;
import com.aircast.photobag.services.CancelUploadingTask;
import com.aircast.photobag.services.UploadService;
import com.aircast.photobag.utils.PBBitmapUtils;
import com.aircast.photobag.utils.PBGeneralUtils;
import com.aircast.photobag.utils.PBImageThumbLoaderUtils;
import com.aircast.photobag.utils.PBPreferenceUtils;
import com.aircast.photobag.utils.PBVideoUtils;
import com.aircast.photobag.widget.FButton;
import com.aircast.photobag.widget.PBCustomWaitingProgress;
import com.aircast.photobag.widget.actionbar.ActionBar;

/**
 * Show <b>All</b> media files in sdcard.
 * <p>TODO: Move Adapter and other code to package where they supposed to be.</p>
 * */
public class SelectMultipleImageActivity extends PBAbsActionBarActivity
		implements OnClickListener, OnItemClickListener {
	private int mCount;
	// private boolean[] mThumbnailsselection;
	private HashMap<Integer, MediaData> mThumbnailsSelection = new HashMap<Integer, MediaData>();
	private ArrayList<MediaData> mMediasSource = new ArrayList<MediaData>();
	private ImageAdapter mImageAdapter;
	// private String mAlbumName;
	private int mSelectCount = 0;
	private LinearLayout mLvLoadingWaiting;
	private FButton mSelectBtn;
	private GridView mImageGrid;
	private ActionBar mHeaderBar;
	private PBImageThumbLoaderUtils mThumbLoasUtils;
	private TextView mBtnText;
	private boolean isChecking = false;
	private String mToken;
	
	private boolean mIsFromCamera = false;
	private static int mIDCamera = Integer.MAX_VALUE;
	
	private boolean isComeFromPhotoContest = false;

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mIsActivityDestroyed = false;

		Display display = getWindowManager().getDefaultDisplay();
		int screenWidth = display.getWidth();
		int screenHeight = display.getHeight();
		if (screenHeight >= 960 && screenWidth >= 540) {
			setContentView(R.layout.pb_custom_gallery_layout_960_540);
		} else {
			setContentView(R.layout.pb_custom_gallery_layout);
		}
		boolean sdCardMounted = true;
		if (!mIsActivityDestroyed) {
			sdCardMounted = PBGeneralUtils.checkSdcard(this, true, false,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialoginterface,
								int i) {
							finish();
						}
					});
		}
		if (!sdCardMounted) {
			return;
		}

		
		mToken = PBPreferenceUtils.getStringPref(getBaseContext(),
				PBConstant.PREF_NAME, PBConstant.PREF_NAME_TOKEN, "");
		// 20120217 @lent5 add layout waiting loading
		mLvLoadingWaiting = (LinearLayout) findViewById(R.id.ll_loading_panel_waiting);
		mSelectBtn = (FButton) findViewById(R.id.select_btn);
		
        Spannable buttonLabel = new SpannableString(String.format("  "+
			getString(R.string.pb_gallery_btn_title), mSelectCount));
        Drawable drawable = getResources().getDrawable(R.drawable.btn_ico_up);  
        //drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        int intrinsicHeightWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
                (float) 20, getResources().getDisplayMetrics()); // convert 20 dip  to int
        drawable.setBounds(0, 0, intrinsicHeightWidth,intrinsicHeightWidth);
        ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
        buttonLabel.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mSelectBtn.setText(buttonLabel);
		
		mSelectBtn.setOnClickListener(this);
		/*mBtnText = (TextView) findViewById(R.id.select_btn_text);
		mBtnText.setText(String.format(
				getString(R.string.pb_gallery_btn_title), mSelectCount));*/
		mImageGrid = (GridView) findViewById(R.id.PhoneImageGrid);
		mImageGrid.setClickable(true);
		mImageGrid.setSelected(true);
		mImageGrid.setOnItemClickListener(this);

		mHeaderBar = (ActionBar) findViewById(R.id.headerbar);
		mHeaderBar.setTitle(getString(R.string.upload_select_photo_title));
		/*if(!isComeFromPhotoContest) {
			setHeader(mHeaderBar, getString(R.string.upload_select_photo_title));	
		} else {
			setHeader(mHeaderBar, "Atik Photo contest");
		}*/
		
		setHeader(mHeaderBar, getString(R.string.upload_select_photo_title));	
		
		startInitialLoadingDataProgress(mLvLoadingWaiting);
		boolean clearCache = cancelOldUploadTask();
	}
	

	private static final int UPDATE_COUNT = 0;
	private static final int UPDATE_FORBIDDEN = 1;
	private static final int START_UPLOADING = 2;
	private final Handler mHandler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case UPDATE_COUNT:
					
				  /*mBtnText.setText(String.format(
				   getString(R.string.pb_gallery_btn_title), mSelectCount));*/
				  Spannable buttonLabel = new SpannableString(String.format("  "+
							getString(R.string.pb_gallery_btn_title), mSelectCount));
				 
				  Drawable drawable = getResources().getDrawable(R.drawable.btn_ico_up);  
		          //drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
		          int intrinsicHeightWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
		                (float) 20, getResources().getDisplayMetrics()); // convert 20 dip  to int

		          drawable.setBounds(0, 0, intrinsicHeightWidth,intrinsicHeightWidth);
			      ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
			      buttonLabel.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			      mSelectBtn.setText(buttonLabel);

			   break;
				
				

			case UPDATE_FORBIDDEN:
				if (mLvLoadingWaiting != null) {
					mLvLoadingWaiting.setVisibility(View.GONE);
				}
				
				new AlertDialog.Builder(SelectMultipleImageActivity.this)
	            .setIcon(android.R.drawable.ic_dialog_alert)
	            .setTitle(R.string.pb_password_banned_title)
	            .setMessage(getString(R.string.pb_password_banned_context))
	            .setPositiveButton(R.string.dialog_ok_btn, 
	                    new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog,
	                        int which) {
	                	SelectMultipleImageActivity.this.finish();
	                }
	            }).show();
				
				break;
			case START_UPLOADING:
				startUploading();
				break;
			default:
				break;
			}
			return false;
		}
	});

	private Thread mInitialLoadingThread;
	private boolean mIsActivityDestroyed = false;

	public void startInitialLoadingDataProgress(final View viewWaiting) {
		viewWaiting.setVisibility(View.VISIBLE);
		// Do something long
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				try {
					initialLoadData();
				} catch (Exception e) {
					Log.w(PBConstant.TAG,
							"--initialLoadData: " + e.getMessage());
				}
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						inflateDataToView();
					}
				});
			}
		};

		mInitialLoadingThread = new Thread(runnable);
		mInitialLoadingThread.setName("loading");
		mInitialLoadingThread.start();
	}

	class MediaData {
		public int id;
		public String url;
		public long duration = -1;
		long time;
	}

	/** load data from mediaStore and inflate to view */
	@SuppressWarnings("deprecation")
	private void initialLoadData() throws StaleDataException {
		// final String[] columns = { MediaStore.MediaColumns._ID,
		// MediaStore.MediaColumns.DATA };
		final String orderBy = MediaStore.MediaColumns._ID;

		Cursor imagecursor = managedQuery(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[] {
						MediaStore.MediaColumns._ID,
						MediaStore.MediaColumns.DATA }, buildFilterPhoto(), /* noteWhereParams */
				null, orderBy);
		if (imagecursor != null) {
			this.mCount = imagecursor.getCount();
			// mThumbnailsselection = new boolean[this.mCount];
			for (int i = 0; i < this.mCount; i++) {

				imagecursor.moveToPosition(i);
				MediaData photo = new MediaData();
				photo.id = imagecursor.getInt(imagecursor
						.getColumnIndex(MediaStore.MediaColumns._ID));
				photo.url = imagecursor.getString(imagecursor
						.getColumnIndex(MediaStore.MediaColumns.DATA));

				mMediasSource.add(photo);
			}
			startManagingCursor(imagecursor);
		}

		Cursor videoCursor = managedQuery(
				MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[] {
						MediaStore.MediaColumns._ID,
						MediaStore.MediaColumns.DATA,
						MediaStore.Video.Media.DURATION }, buildFilterVideo(), /* noteWhereParams */
				null, orderBy);
		if (videoCursor != null) {
			this.mCount += videoCursor.getCount();
			// mThumbnailsselection = new boolean[this.mCount];
			int size = videoCursor.getCount();

			for (int i = 0; i < size; i++) {

				videoCursor.moveToPosition(i);
				MediaData video = new MediaData();
				video.id = videoCursor.getInt(videoCursor
						.getColumnIndex(MediaStore.MediaColumns._ID));
				video.url = videoCursor.getString(videoCursor
						.getColumnIndex(MediaStore.MediaColumns.DATA));
				video.duration = videoCursor.getLong(videoCursor
						.getColumnIndex(MediaStore.Video.Media.DURATION));

				File f = new File(video.url);
				if (video.duration == 0) {
					if (PBGeneralUtils.checkVideoIsValid(f.getAbsolutePath())) {
						PBVideoUtils mVideoUtils = new PBVideoUtils();
						video.duration = mVideoUtils.getDuration(f
								.getAbsolutePath());
					}
				}
				mMediasSource.add(video);
			}
			startManagingCursor(videoCursor);
		}
		
		this.mCount++;
		MediaData photo = new MediaData();
		photo.id = mIDCamera;
		photo.url = "camera";
		mMediasSource.add(photo);

		if (mMediasSource != null && mMediasSource.size() > 0) {
			Collections.sort(mMediasSource, new Comparator<MediaData>() {
				@Override
				public int compare(MediaData object1, MediaData object2) {
					return (object1.id >= object2.id ? 1 : -1);
				}
			});
		}
	}

	private String buildFilterVideo() {
		String mediaFilferType = "((mime_type like 'video/mp4') OR (mime_type like 'video/3gpp'))";
		return mediaFilferType;
	}

	@SuppressLint("ParserError")
	private String buildFilterPhoto() {
		String mediaFilterType = "";
		mediaFilterType += "(";
		mediaFilterType += "(" + MediaStore.MediaColumns.MIME_TYPE
				+ " like 'image/jpg'" + ")" + " or ";
		mediaFilterType += "(" + MediaStore.MediaColumns.MIME_TYPE
				+ " like 'image/jpeg'" + ")" + " or ";
		mediaFilterType += "(" + MediaStore.MediaColumns.MIME_TYPE
				+ " like 'image/png'" + ")" + " or ";
		mediaFilterType += "(" + MediaStore.MediaColumns.MIME_TYPE
				+ " like 'image/bmp'" + ")";
		// 2012 removed	judgment of filesize
		//mediaFilterType += ")" + "and (" + MediaStore.MediaColumns.SIZE
		//		+ " > 0)";
		mediaFilterType += ")";
		return mediaFilterType;
	}

	private void inflateDataToView() {
		mThumbLoasUtils = new PBImageThumbLoaderUtils(
				PBApplication.getBaseApplicationContext());
		// inflate data to layout
		mImageAdapter = new ImageAdapter();
		mImageGrid.setAdapter(mImageAdapter);

		// 20120217 @lent5 add layout waiting loading
		if (mLvLoadingWaiting != null) {
			mLvLoadingWaiting.setVisibility(View.GONE);
		}

		// show alert if no photos in camera folder
		// screen_upload_choose_photo_no_photos_title
		if (this.mCount <= 0) {
			if (!mIsActivityDestroyed) {
				PBGeneralUtils.showAlertDialogActionWithOnClick(
						SelectMultipleImageActivity.this,
						android.R.drawable.ic_dialog_alert,
						getString(R.string.pb_app_name),
						getString(R.string.screen_gallery_no_photos_mess),
						getString(R.string.pb_btn_OK),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								finish();
							}
						}, false);
			}
		}

		// 20120312 @lent5 add focus at first position
		if (this.mCount > 1) {
			mImageGrid.requestFocus();
			mImageGrid.requestFocusFromTouch();
			mImageGrid.setSelection(this.mCount - 2);
		}
	}

	/**
	 * compraranle class support sort selected add for sort selected time
	 * 
	 * @author lent5
	 * 
	 */
	public class PhotoComparable implements Comparator<MediaData> {
		@Override
		public int compare(MediaData arg0, MediaData arg1) {
			return (arg0.time > arg1.time ? 1 : (arg0.time == arg1.time ? 0
					: -1));
		}
	}

	/**
	 * ImageAdapter class use inflate gridview to load bitmap item
	 */
	public class ImageAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public ImageAdapter() {
			mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public int getCount() {
			return mCount;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
						
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.custom_gallery_item,
						null);
				holder.imageview = (ImageView) convertView
						.findViewById(R.id.thumbImage);
				holder.checkbox = (ImageView) convertView
						.findViewById(R.id.itemCheckBox);
				// add for support upload video function
				holder.videoIcon = (ImageView) convertView
						.findViewById(R.id.gallery_item_thump_video);
				holder.videoDuration = (TextView) convertView
						.findViewById(R.id.gallery_item_video_duration);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if (holder == null) { // in bad case we get nullpointer
				return convertView;
			}

			MediaData photo = null;
			if (position >= 0 && position < mCount) {
				photo = mMediasSource.get(position);
			}
			// final ImageData photoTmp = photo;

			holder.checkbox.setId(position);
			holder.imageview.setId(position);
			holder.id = position;
			holder.videoIcon.setVisibility(View.GONE);
			holder.videoDuration.setVisibility(View.GONE);
			if (photo != null && photo.duration != -1) { // video
				holder.videoIcon.setVisibility(View.VISIBLE);
				holder.videoDuration.setVisibility(View.VISIBLE);
				holder.videoDuration.setText(PBGeneralUtils.makeTimeString(
						getApplicationContext(), photo.duration / 1000));
			}
			if (mThumbnailsSelection.containsKey(/* photo.id */photo.url
					.hashCode())) {
				holder.checkbox.setBackgroundResource((R.drawable.checkbox_on));
			} else {
				holder.checkbox
						.setBackgroundResource((R.drawable.checkbox_off));
			}
			if (photo != null) {
				boolean isInCache = mThumbLoasUtils.displayImage(
						holder.imageview, photo.url, photo.id,
						(photo.duration != -1));
				if (!isInCache) {
					
					if ((photo.id == mIDCamera)&&(photo.url.equals("camera"))) {
						holder.checkbox.setBackgroundResource(0);
						holder.imageview.setImageDrawable(getResources()
								.getDrawable(R.drawable.btn_camera));				
					}
					else {
						holder.imageview.setImageDrawable(getResources()
							.getDrawable(android.R.drawable.ic_menu_gallery));
					}
				}
			}
			return convertView;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		mIsActivityDestroyed = false;
		if (mImageGrid == null) {
			return;
		}
		
		if (mIsFromCamera) {
			Intent restartIntent = getIntent();
			
			startActivity(restartIntent);
			finish();
		}
		else {
			// 20120312 @lent5 add focus at first position
			final PBCustomWaitingProgress mWaiting = new PBCustomWaitingProgress(
					this);
			// if(this.mCount > 0){
			mWaiting.showWaitingLayout();
			mImageGrid.postDelayed(new Runnable() {
				@Override
				public void run() {
					mImageGrid.requestFocus();
					mImageGrid.requestFocusFromTouch();
					if (mCount > 1) {
						mImageGrid.setSelection(mCount - 2);
					}
					mWaiting.hideWaitingLayout();
				}
			}, 600);
			// }
		}
	}

	// 201210522 check del old item <S>
	private boolean cancelOldUploadTask() {
		boolean result = false;
		// get collection id if have set
		String collectionId = PBPreferenceUtils.getStringPref(getBaseContext(),
				PBConstant.UPLOAD_SERVICE_PREF, PBConstant.PREF_COLLECTION_ID,
				"");
		cancelUploadTask(collectionId);
		return result;
	}

	private void cancelUploadTask(String collectionId) {
		String token = PBPreferenceUtils.getStringPref(getBaseContext(),
				PBConstant.PREF_NAME, PBConstant.PREF_NAME_TOKEN, "");

		// Response response = PBAPIHelper.cancelUploading(token);
		// Log.i("mapp", ">>> select multi image, result when cancel task = "
		// + response.decription + "Code: " + response.errorCode);
		// delete collection if is set
		if (!TextUtils.isEmpty(collectionId)) {
			CancelUploadingTask cancelUpload = new CancelUploadingTask(
					getBaseContext());
			cancelUpload.execute();
		}
	}

	// 201210522 check del old item <E>

	@Override
	protected void onStop() {
		super.onStop();
		mIsActivityDestroyed = true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mIsActivityDestroyed = true;
		if (mInitialLoadingThread != null) {
			try {
				mInitialLoadingThread.interrupt();
				mInitialLoadingThread = null;
			} catch (Exception e) {
				Log.w(PBConstant.TAG, e.getMessage());
			}
		}

		// lent clear thumbs and recycle bitmap $S
		if (mThumbLoasUtils != null) {
			mThumbLoasUtils.recycleAll();
			mThumbLoasUtils = null;
		}
		// lent clear thumbs and recycle bitmap $E

		if (mMediasSource != null) {
			mMediasSource.clear();
		}
		mMediasSource = null;
		// mThumbnailsselection = null;
		if (mThumbnailsSelection != null) {
			mThumbnailsSelection.clear();
			mThumbnailsSelection = null;
		}
	}

	class ViewHolder {
		ImageView imageview;
		ImageView checkbox;
		ImageView videoIcon;
		TextView videoDuration;
		int id;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.select_btn:
			if (isChecking != false)
				return;
			
			if (mThumbnailsSelection == null
				|| mThumbnailsSelection.isEmpty()) {
				if (!mIsActivityDestroyed) {
					PBGeneralUtils.showAlertDialogActionWithOnClick(
							SelectMultipleImageActivity.this,
							android.R.drawable.ic_dialog_alert,
							getString(R.string.pb_app_name),
							getString(R.string.screen_gallery_photo_no_photos_select),
							getString(R.string.pb_btn_OK),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(
										DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							});
					return;
				}
			}
			
			mSelectBtn.setEnabled(false);
			/*
			Bundle getTG = getIntent().getExtras();
			if(getTG != null){
            	Intent intent = new Intent();
            	intent.setAction(Intent.ACTION_MAIN);
            	intent.setType("image/png");
            	intent.setClassName("jp.co.toshiba.tosjetutil", "jp.co.toshiba.transferjetdemo.SendActivity");
            	startActivity(intent);
            	return;
			}
			*/
			
			if (mLvLoadingWaiting != null) {
				mLvLoadingWaiting.setVisibility(View.VISIBLE);
			}
			
			// check internet
			if (!PBApplication.hasNetworkConnection()) {
				/*PBGeneralUtils.showAlertDialogActionWithOnClick(this,
						android.R.drawable.ic_dialog_alert,
						getString(R.string.pb_network_error),
						getString(R.string.pb_network_error_content),
						getString(R.string.pb_btn_OK),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});*/
				
				   
					AlertDialog.Builder	networkErorrDialog =  new AlertDialog.Builder(new ContextThemeWrapper(SelectMultipleImageActivity.this,
						     R.style.popup_theme));
					//exitDialog .setTitle(getString(R.string.pb_chat_message_internet_offline_dialog_title));
					networkErorrDialog .setMessage(getString(R.string.pb_network_not_available_general_message));
					networkErorrDialog .setCancelable(false);
					networkErorrDialog .setPositiveButton(getString(R.string.dialog_ok_btn),
				       new DialogInterface.OnClickListener() {
					        @Override
					        public void onClick(DialogInterface dialog,
					          int which) {
					        	dialog.dismiss();
					        }
						});     
					networkErorrDialog.show();
				
				mSelectBtn.setEnabled(true);
				mLvLoadingWaiting.setVisibility(View.GONE);
				return;
			}
			
			checkIfForbidden();
			break;
		default:
			break;
		}
	}
	
	private void startUploading() {
		if (mThumbnailsSelection == null
			|| mThumbnailsSelection.size() == 0) {
			return;
		}
		
		// reset all upload pref
		PBPreferenceUtils.saveBoolPref(getBaseContext(),
				PBConstant.UPLOAD_SERVICE_PREF,
				PBConstant.PREF_UPLOAD_FINISH, false);
		PBPreferenceUtils.saveStringPref(getBaseContext(),
				PBConstant.UPLOAD_SERVICE_PREF,
				PBConstant.PREF_UPLOAD_PASS, "");
		PBPreferenceUtils.saveStringPref(getBaseContext(),
				PBConstant.UPLOAD_SERVICE_PREF,
				PBConstant.PREF_COLLECTION_ID, "");
		PBPreferenceUtils.saveStringPref(getBaseContext(),
				PBConstant.UPLOAD_SERVICE_PREF,
				PBConstant.PREF_COLLECTION_THUMB, "");
		PBPreferenceUtils.saveBoolPref(getBaseContext(),
				PBConstant.UPLOAD_SERVICE_PREF,
				PBConstant.PREF_INPUT_SEQUENCE_FINISH, false);

		List<MediaData> photos = new ArrayList<MediaData>(
				mThumbnailsSelection.values());
		ArrayList<String> selectedMedias = new ArrayList<String>();
		// ArrayList<Long> selectedTypeMedias = new ArrayList<Long>();
		long[] selectedTypeMedias = new long[photos.size()];
		// sort
		Collections.sort(photos, new PhotoComparable());
		int i = 0;
		for (MediaData photo : photos) {
			selectedMedias.add(photo.url);
			selectedTypeMedias[i++] = photo.duration;
		}
		if (selectedMedias.size() > 0) {

			

			Intent serviceIntent = new Intent(getBaseContext(),
					UploadService.class);
			serviceIntent.putStringArrayListExtra(
					PBConstant.INTENT_SELECTED_MEDIA, selectedMedias);
			serviceIntent.putExtra(PBConstant.INTENT_SELECTED_MEDIA_TYPE,
					selectedTypeMedias);
			serviceIntent
					.putExtra(PBConstant.INTENT_UPLOADED_PHOTO_NUM, -1);

			serviceIntent.putExtra(
					PBConstant.INTENT_START_SERVICE_FROM_SELECT_IMG, true);

			String collectionId = getIntent().getStringExtra(
					PBConstant.COLLECTION_ID);
			if (!TextUtils.isEmpty(collectionId)) { // adding
				PBPreferenceUtils.saveStringPref(getBaseContext(),
						PBConstant.UPLOAD_SERVICE_PREF,
						PBConstant.PREF_COLLECTION_ID, collectionId);
				PBPreferenceUtils.saveStringPref(
						getBaseContext(), PBConstant.UPLOAD_SERVICE_PREF,
						PBConstant.PREF_UPLOAD_PASS, getIntent().getStringExtra(PBConstant.INTENT_PASSWORD));
				PBPreferenceUtils.saveBoolPref(getBaseContext(),
						PBConstant.UPLOAD_SERVICE_PREF,
						PBConstant.PREF_INPUT_SEQUENCE_FINISH, true);
			}

			// start service
			startService(serviceIntent);

			Intent intent;
			if (!TextUtils.isEmpty(collectionId)) {
				// case add photo
				PBDatabaseManager mDatabaseManager = PBDatabaseManager
						.getInstance(this);
				String secretCode = mDatabaseManager.getPassswordSecretDigit(collectionId);
				PBPreferenceUtils.saveStringPref(getApplicationContext(),
                        PBConstant.UPLOAD_SERVICE_PREF,
                        PBConstant.PREF_FOUR_DIGIT, secretCode);
				intent = new Intent(getBaseContext(),
						UploadingActivity.class);
				intent.putExtra(PBConstant.IS_OWNER, 
						getIntent().getBooleanExtra(PBConstant.IS_OWNER, false));
				intent.putExtra(PBConstant.INTENT_PASSWORD,
						getIntent().getStringExtra(PBConstant.INTENT_PASSWORD));
			} else {
				// case upload first
				intent = new Intent(getBaseContext(),
						UploadSetPasswordActivity.class);
			}
			intent.putStringArrayListExtra(
					PBConstant.INTENT_SELECTED_MEDIA, selectedMedias);
			intent.putExtra(PBConstant.INTENT_SELECTED_MEDIA_TYPE,
					selectedTypeMedias);

			startActivity(intent);
			finish();
		} else {
			if (!mIsActivityDestroyed) {
				PBGeneralUtils.showAlertDialogActionWithOnClick(
							SelectMultipleImageActivity.this,
							android.R.drawable.ic_dialog_alert,
							getString(R.string.pb_app_name),
							getString(R.string.screen_gallery_photo_no_photos_select),
							getString(R.string.pb_btn_OK),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(
										DialogInterface dialog,
										int which) {
									dialog.dismiss();										
								}
							});
				if (mLvLoadingWaiting != null) {
					mLvLoadingWaiting.setVisibility(View.GONE);
				}
				mSelectBtn.setEnabled(true);
			}
			// 20120217 @lent5 add layout waiting loading #E

		}
	}
	
	private void checkIfForbidden() {
		new Thread() {
			@Override
			public void run() {
				Response response;
				String collectionId = getIntent().getStringExtra(
						PBConstant.COLLECTION_ID); 
				String currentPassword = getIntent().getStringExtra(
						PBConstant.INTENT_PASSWORD);
				PBPreferenceUtils.saveStringPref(
						getBaseContext(), PBConstant.UPLOAD_SERVICE_PREF,
						PBConstant.PREF_UPLOAD_PASS, currentPassword);
				if (!TextUtils.isEmpty(collectionId)) {
					String password = PBPreferenceUtils.getStringPref(
							getBaseContext(), PBConstant.UPLOAD_SERVICE_PREF,
							PBConstant.PREF_UPLOAD_PASS, "");
					
					response = PBAPIHelper.startAdding(mToken, collectionId, password);
				}
				else {
					response = PBAPIHelper.startUploading(mToken);
				}
				if (response.errorCode == ResponseHandle.CODE_403) {
					mHandler.sendEmptyMessage(UPDATE_FORBIDDEN);
					return;
				}
				
				Log.d("response", response.decription);
				if (!TextUtils.isEmpty(response.decription)) {
					try {
						JSONObject jObject = new JSONObject(response.decription);
						if (jObject.has("collection"))
							collectionId = jObject.getString("collection");
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				if (!TextUtils.isEmpty(collectionId)) {
					PBPreferenceUtils.saveStringPref(getBaseContext(),
							PBConstant.UPLOAD_SERVICE_PREF,
							PBConstant.PREF_COLLECTION_EX_ID, collectionId);
				}
				
				mHandler.sendEmptyMessage(START_UPLOADING);
			}
		}.start();
	}

	// user press key back or not
	// private boolean isBack = false;

	@Override
	protected void handleHomeActionListener() {
		finish();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		

		PBPreferenceUtils.saveBoolPref(getApplicationContext(),
				PBConstant.PREF_NAME, PBConstant.UPLOAD_TAG, false);
	}

	@Override
	protected void setLeftBtnsDetailHeader(ActionBar headerBar) {

	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	private void showDialogForFakeItem() {
		showAlertDialogActionWithOnClick(SelectMultipleImageActivity.this,
				android.R.drawable.ic_dialog_alert, null,
				getString(R.string.pb_choose_file_fake_msg),
				getString(R.string.pb_btn_OK),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						isChecking = false;
					}
				});

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
		MediaData photoTmp = null;
		if (pos >= 0 && pos < mCount) {
			photoTmp = mMediasSource.get(pos);
		}

		if (photoTmp == null) {
			return;
		}
		
		//20121121 add call camera
		if ((photoTmp.id == mIDCamera)&&(photoTmp.url.equals("camera")))
		{
			PackageManager pm = this.getPackageManager();
			
			if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
				mIsFromCamera = true;	
				mLvLoadingWaiting.setVisibility(View.VISIBLE);
				
				Intent cameraIntent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);	
				startActivity(cameraIntent);
			}
			
			return;
		}

		// 20120507 add by NhatVT, check "fake" photo <S>
		if (photoTmp.duration == -1
				&& !PBBitmapUtils.isPhotoValid(photoTmp.url)) {// check fake
																// photo item
			isChecking = true;
			showDialogForFakeItem();
			return;
		} else {
			if (photoTmp.duration != -1) { // check video
				if (!PBGeneralUtils.checkVideoIsValid(photoTmp.url)) { // fake
																		// video
					isChecking = true;
					showDialogForFakeItem();
					return;
				} else { // check video length
					if (photoTmp.duration > PBConstant.VIDEO_LENGTH_LIMIT) {
						isChecking = true;
						showAlertDialogActionWithOnClick(
								SelectMultipleImageActivity.this,
								android.R.drawable.ic_dialog_alert,
								getString(R.string.pb_choose_video_msg_file_too_long_title),
								getString(R.string.pb_choose_video_msg_file_too_long_msg),
								getString(R.string.pb_btn_OK),
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
										isChecking = false;
									}
								});

						return;
					}
					// only check when execute upload video
					if (!PBGeneralUtils.checkExternalStorage(
							SelectMultipleImageActivity.this,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
									isChecking = false;
								}
							})) {
						return;
					}
				}
			}
		}

		boolean checked = mThumbnailsSelection
				.containsKey(/* photoTmp.id */photoTmp.url.hashCode());
		// ImageView cb = (ImageView) arg1.findViewById(R.id.itemCheckBox);
		ViewHolder holder = (ViewHolder) view.getTag();

		if (holder == null || holder.checkbox == null) {
			return;
		}

		if (checked) {
			photoTmp.time = 0;
			mThumbnailsSelection.remove(/* photoTmp.id */photoTmp.url
					.hashCode());
			holder.checkbox.setBackgroundResource(R.drawable.checkbox_off);
			mSelectCount--;
		} else {
			if (photoTmp.duration != -1)
				Toast.makeText(getBaseContext(), getString(R.string.pb_upload_select_video_hint), Toast.LENGTH_SHORT).show();
			photoTmp.time = System.currentTimeMillis();
			mThumbnailsSelection.put(/* photoTmp.id */photoTmp.url.hashCode(),
					photoTmp);
			holder.checkbox.setBackgroundResource(R.drawable.checkbox_on);
			mSelectCount++;
		}

		mHandler.sendEmptyMessage(UPDATE_COUNT);

	}

	public void showAlertDialogActionWithOnClick(Context context, int iconId,
			String title, String message, String buttonName1,
			DialogInterface.OnClickListener buttonClick1) {
		if (context == null)
			return;
		try {
			// PBCustomDialog dialog = new PBCustomDialog(context);
			AlertDialog dialog = new AlertDialog.Builder(context).create();
			dialog.setButton(buttonName1, buttonClick1);
			dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface dialog) {
					isChecking = false;

				}
			});
			if (!TextUtils.isEmpty(title)) {
				dialog.setTitle(title);
			}
			dialog.setIcon(iconId);
			if (!TextUtils.isEmpty(message)) {
				dialog.setMessage(message);
			}
			dialog.show();
		} catch (Exception e) {
			Log.e(PBConstant.TAG, "could not show dialog ...");
		}
	}

	/******************************************************************************
	 *********************** Cache bitmap ********************************
	 ******************************************************************************/

}
