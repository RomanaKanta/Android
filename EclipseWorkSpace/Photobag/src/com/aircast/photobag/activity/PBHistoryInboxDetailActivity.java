/*package com.kayac.photobag.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.concurrent.RejectedExecutionException;

import org.apache.http.HttpStatus;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;




import com.google.analytics.tracking.android.EasyTracker;
// import com.ad_stir.AdstirTerminate;
import com.kayac.photobag.R;
import com.kayac.photobag.adapter.PBHistoryDetailAdapter;
import com.kayac.photobag.api.PBAPIHelper;
import com.kayac.photobag.api.ResponseHandle;
import com.kayac.photobag.api.ResponseHandle.Response;
import com.kayac.photobag.application.PBApplication;
import com.kayac.photobag.application.PBConstant;
import com.kayac.photobag.database.PBDatabaseDefinition;
import com.kayac.photobag.database.PBDatabaseManager;
import com.kayac.photobag.model.PBHistoryPhotoModel;
import com.kayac.photobag.utils.PBBitmapUtils;
import com.kayac.photobag.utils.PBGeneralUtils;
import com.kayac.photobag.utils.PBPreferenceUtils;
import com.kayac.photobag.widget.FButton;
import com.kayac.photobag.widget.PBCustomWaitingProgress;
import com.kayac.photobag.widget.actionbar.ActionBar;
import com.kayac.photobag.widget.actionbar.ActionBar.Action;

*//**
 * Show photos in password selected.
 * *//*
public class PBHistoryInboxDetailActivity extends PBAbsActionBarActivity {

	private LinearLayout mLayoutInfoDownload;
	private ProgressBar mProgressDownload;
	private FButton mBtnTopLeft;
	private FButton mBtnTopRight;
	private FButton mBtnMiddle;
	private FButton mBtnButtom;
	private Context mContext;
	private boolean mIsInInbox;
	private boolean mIsDeleteMode;
	private ActionBar mHeaderBar;
	private String mCollectionId, mPassword, mCollectionThumb;
	private int mAddibility;
	private int mSaveMark;
	private String mAdLink;
	private long mChargeDate;
	private long mHistoryId;
	private ArrayList<PBHistoryPhotoModel> mPhotos;
	private ArrayList<String> mDeleteSelection;
	private String mToken;
	private TextView mInfoDownload;
	private PBHistoryDetailAdapter mAdapter;
	private static final int PB_HANDLER_DELETE_HISTORY_SUCCESS = 1;
	private static final int PB_HANDLER_DELETE_HISTORY_FAIL = 2;
	private static final int TIME_SLEEP = 200;
	private boolean mIsUpdateThump = false;
	private final int REQUEST_PHOTO_VIEW = 110;
	private GridView mThumbGrid;
	private boolean isInBoxDeleteMode = false;
	boolean isComeFromLibrary =  false;
	//    private RelativeLayout homeActionBar;
	*//**
	 * Waiting progress dialog.
	 *//*
	private PBCustomWaitingProgress mCustomWaitingLayout;

	public static boolean doFinishAtNextResume;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.pb_layout_history_detail);
		
		mContext = this;
		mToken = PBPreferenceUtils.getStringPref(mContext,
				PBConstant.PREF_NAME, PBConstant.PREF_NAME_TOKEN, "");

		doFinishAtNextResume = false;

		if (getIntent() == null) {
			finish();
			return;
		}
		Bundle extras = getIntent().getBundleExtra("data");
		if (extras == null) {
			finish();
			return;
		}

		mIsInInbox = extras.getBoolean(PBConstant.HISTORY_CATEGORY_INBOX);
		mPassword = extras.getString(PBConstant.HISTORY_PASSWORD);
		mHistoryId = extras.getLong(PBConstant.HISTORY_ITEM_ID);
		mCollectionId = extras.getString(PBConstant.HISTORY_COLLECTION_ID);
		mChargeDate = extras.getLong(PBConstant.HISTORY_CHARGE_DATE);
		mCollectionThumb = extras.getString(PBConstant.COLLECTION_THUMB);
		mAddibility = extras.getInt(PBConstant.HISTORY_ADDIBILITY);
		mSaveMark = extras.getInt(PBConstant.HISTORY_SAVE_MARK);
		mAdLink = extras.getString(PBConstant.HISTORY_AD_LINK);

		mPhotos = PBDatabaseManager.getInstance(this).getPhotos(mCollectionId);
		
		for (int i = 0; i < mPhotos.size(); i++) { 
			PBHistoryPhotoModel modelLocal = mPhotos.get(i);
			System.out.println("photos of history:"+PBGeneralUtils.getPathFromCacheFolder(modelLocal.getPhoto()));
		}
		
		mBtnTopLeft = (FButton) findViewById(R.id.btn_detail_top_left);
		mBtnTopLeft.setOnClickListener(new OnClickListenerTopLeft());

		mBtnTopRight = (FButton) findViewById(R.id.btn_detail_top_right);
		mBtnTopRight.setOnClickListener(new OnClickListenerTopRight());

		mBtnButtom = (FButton) findViewById(R.id.btn_detail_buttom);
		mBtnButtom.setOnClickListener(new OnClickListenerButtom());
		
		mBtnMiddle = (FButton) findViewById(R.id.btn_detail_middle);
		mBtnMiddle.setOnClickListener(new OnClickListenerMiddle());

		mIsDeleteMode = false;
		setOutboxUI(mIsDeleteMode);
		
		mLayoutInfoDownload = (LinearLayout) findViewById(R.id.layout_info_download);
		mProgressDownload = (ProgressBar) findViewById(R.id.progress_download);
		mProgressDownload.setMax(mPhotos.size());
		mProgressDownload.setProgress(0);

		mInfoDownload = (TextView) findViewById(R.id.txt_info_download);
		// Add header bar layout
		mHeaderBar = (ActionBar) findViewById(R.id.headerbar);
		setHeader(mHeaderBar, mPassword);
//		homeActionBar = (RelativeLayout) findViewById(R.id.home_topbar);
//		
//		 if(getIntent().hasExtra(PBConstant.PREF_PASSWORD_FROM_LIBRARY)){
//	       	 isFromLibrary = getIntent().getExtras().getBoolean(PBConstant.PREF_PASSWORD_FROM_LIBRARY);
//	       	
//	       	homeActionBar.setVisibility(View.VISIBLE);
//	       	mHeaderBar.setVisibility(View.GONE);
//	       }else{
//	    	   
//	    	   homeActionBar.setVisibility(View.GONE);
//		       	mHeaderBar.setVisibility(View.VISIBLE);
//	       }
		
		// Atik checking photobag password
		if(mPassword != null) {
			if(!mPassword.isEmpty() && mPassword.equalsIgnoreCase(getString
					(R.string.pb_dl_firstclick_magicphrase_defaulse))
					|| mPassword.equalsIgnoreCase(getString
							(R.string.pb_dl_firstclick_magicphrase_defaulse_other))) {
				System.out.println("Atik no need to show chat option");
			} else {
				System.out.println("Atik display chat option");
		        final boolean hasInternet = PBApplication.hasNetworkConnection();

				// Chat button 
				final Action chatAction = new ActionBar.ViewAction(this,
						new ActionBar.PerformActionListener() {
							@Override
							public void performAction(View view) {

								//String userNickName = PBPreferenceUtils.getStringPref(
								//mContext, PBConstant.PREF_NAME,
							    //		PBConstant.PREF_NICK_NAME, "");
								//if (userNickName.equals("")) {
		
							    //	setUserRegistration();
		
								//	} else {
									if(hasInternettrue) {
										Bundle extras = new Bundle();
										extras.putLong(PBConstant.HISTORY_ITEM_ID,
												mHistoryId);
										extras.putString(PBConstant.HISTORY_COLLECTION_ID,
												mCollectionId);
										extras.putString(PBConstant.HISTORY_PASSWORD,
												mPassword);
										extras.putLong(PBConstant.HISTORY_CHARGE_DATE,
												mChargeDate);
										extras.putString(PBConstant.COLLECTION_THUMB,
												mCollectionThumb);
										extras.putInt(PBConstant.HISTORY_ADDIBILITY,
												mAddibility);
										extras.putBoolean(
												PBConstant.HISTORY_CATEGORY_INBOX,
												mIsInInbox);
										Intent detail = new Intent(
												PBHistoryInboxDetailActivity.this,
												PBChatActivity.class);
										detail.putExtra("data", extras);
										mContext.startActivity(detail);
									} else {
										// No Internet connection available
										Toast.makeText(PBHistoryInboxDetailActivity.this, 
												getString(R.string.pb_network_error_content), 
												Toast.LENGTH_SHORT).show();
									}
//								}

							}

						}, R.drawable.btn_chat);
				chatAction.setBackground(getResources().getDrawable(
						R.drawable.actionbar_home_btn));
				mHeaderBar.addAction(chatAction);
			}
			
			if(getIntent().hasExtra(PBConstant.PREF_PASSWORD_FROM_LIBRARY)){
		       	 boolean isFromLibrary = getIntent().getExtras().getBoolean(PBConstant.PREF_PASSWORD_FROM_LIBRARY);
			 if(isFromLibrary){
				    isComeFromLibrary = true;
					mHeaderBar.setHomeLogo(R.drawable.openbag_actionbar_icon);
			 }
			 
			}
		}
	
		
		final Action otherAction = new ActionBar.ViewAction(this,
				new ActionBar.PerformActionListener() {
					@Override
					public void performAction(View view) {
						
						System.out.println("Atik mIsInInbox" + mIsInInbox);
						
						if (mIsInInbox) {
							//showDeleteAllConfirm();
							deleteInboxItem(isInBoxDeleteMode);
							
						} else {
							setOutboxUI(!mIsDeleteMode);		
						}
						
						
						if (mDeleteSelection != null) {
							mDeleteSelection.clear();
						}
						mDeleteSelection = new ArrayList<String>();
						if (mAdapter != null) {
							mAdapter.setCheckBoxStatus(mIsDeleteMode);
							mAdapter.resetAdapter(mPhotos, mDeleteSelection);
						}
					}

					
				}, R.drawable.icon_delete);
		otherAction.setBackground(getResources().getDrawable(
				R.drawable.actionbar_home_btn));
		mHeaderBar.addAction(otherAction);

		if (mIsInInbox) {
			mBtnTopLeft.setText(R.string.pb_add_photos);
			mBtnTopRight.setText(R.string.pb_save_all_picture);
			mBtnMiddle.setVisibility(View.GONE);
			if (mAddibility == 0) {
				
				mBtnTopLeft.setButtonColor(getResources().getColor(R.color.fbutton_color_inactive));
				//mBtnTopLeft.setBackgroundResource(R.drawable.pb_button_inactive);
				mBtnTopLeft.setTextColor(0xff666666);
			}
			if (mSaveMark == -1) {
				mBtnTopRight.setButtonColor(getResources().getColor(R.color.fbutton_color_inactive));
				//mBtnTopRight.setBackgroundResource(R.drawable.pb_button_inactive);
				mBtnTopRight.setTextColor(0xff666666);
			}

		} else {
			mBtnTopLeft.setText(R.string.pb_btn_detail_delete_all);
			mBtnTopRight.setText(R.string.pb_btn_detail_delete_part);
			
			mBtnMiddle.setVisibility(View.VISIBLE);
			if (mAddibility == 0) {
				mBtnMiddle.setButtonColor(getResources().getColor(R.color.fbutton_color_inactive));
				//mBtnMiddle.setBackgroundResource(R.drawable.pb_button_inactive);
				mBtnMiddle.setTextColor(0xff666666);
			}
		}

		// 20120221 mod by NhatVT, change Button by using LinearLayout <E>
		mAdapter = new PBHistoryDetailAdapter(this);
		mAdapter.resetAdapter(mPhotos, null);
		mThumbGrid = (GridView) findViewById(R.id.gv_history_detail);
		mThumbGrid.setAdapter(mAdapter);
		mThumbGrid.setSelected(true);
		mThumbGrid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {				
				if (mIsDeleteMode) {
					String url = mPhotos.get(arg2).getPhoto();
					if (mDeleteSelection.contains(url)) {
						mDeleteSelection.remove(url);
					}
					else {
						mDeleteSelection.add(url);
					}
					mAdapter.resetAdapter(mPhotos, mDeleteSelection);
				}
				else {
					mSelected = arg2;
					Intent intent = new Intent(mContext,
							PBImageViewerActivity.class);
					intent.putExtra("PHOTO_ID", arg2);
					intent.putExtra(PBConstant.IS_OWNER, !mIsInInbox);
					intent.putExtra("COLECTION_ID", mCollectionId);
					intent.putExtra("COLECTION_PASSWORD", mPassword);
					intent.putExtra("COLECTION_SAVEMARK", mSaveMark);
					intent.putExtra("COLECTION_ADLINK", mAdLink);
					startActivityForResult(intent, REQUEST_PHOTO_VIEW);
				}
			}
		});

		// @lent5 add to avoid overflow asyncTask
		if (mCheckDownloadPhoto == null
				|| (mCheckDownloadPhoto != null && mCheckDownloadPhoto
						.getStatus() != AsyncTask.Status.RUNNING)) {
			mCheckDownloadPhoto = new CheckDownloadPhoto();
			try {
				mCheckDownloadPhoto.execute();
			} catch (RejectedExecutionException e) {
				e.printStackTrace();
			}
		}
		
		// new CheckDownloadPhoto().execute();
		mCustomWaitingLayout = new PBCustomWaitingProgress(this);
	}

	
	private void deleteInboxItem(boolean status) {

	
		

		if(status){
			mBtnTopLeft.setText(R.string.pb_add_photos);
			mBtnTopRight.setText(R.string.pb_save_all_picture);
			//mBtnMiddle.setVisibility(View.GONE);
			if (mAddibility == 0) {
				
				mBtnTopLeft.setButtonColor(getResources().getColor(R.color.fbutton_color_inactive));
				//mBtnTopLeft.setBackgroundResource(R.drawable.pb_button_inactive);
				mBtnTopLeft.setTextColor(0xff666666);
			}
			if (mSaveMark == -1) {
				mBtnTopRight.setButtonColor(getResources().getColor(R.color.fbutton_color_inactive));
				//mBtnTopRight.setBackgroundResource(R.drawable.pb_button_inactive);
				mBtnTopRight.setTextColor(0xff666666);
			}
			
			if(!isComeFromLibrary) {
				setHeader(mHeaderBar, mPassword);

			} else {
				setHeader(mHeaderBar, mPassword);
				mHeaderBar.setHomeLogo(R.drawable.openbag_actionbar_icon);
			}
			
		}else{
			
			
			
			mBtnTopLeft.setButtonColor(getResources().getColor(R.color.fbutton_color_red_bg));
			//mBtnTopLeft.setBackgroundResource(R.drawable.pb_red_button_bg_black);
			mBtnTopLeft.setTextColor(Color.WHITE);
			mBtnTopRight.setButtonColor(getResources().getColor(R.color.fbutton_color_red_bg));
			//mBtnTopRight.setBackgroundResource(R.drawable.pb_red_button_bg_black);
			mBtnTopRight.setTextColor(Color.WHITE);
			
			mBtnMiddle.setVisibility(View.GONE);
			
			
			mBtnTopLeft.setText(R.string.pb_btn_detail_delete_all);
			mBtnTopRight.setText(R.string.pb_btn_detail_delete_part);
			
			if(!isComeFromLibrary) {
				setHeader(mHeaderBar, mContext.getString(R.string.pb_title_detail_select));

			} else {
				setHeader(mHeaderBar, mContext.getString(R.string.pb_title_detail_select));
				mHeaderBar.setHomeLogo(R.drawable.openbag_actionbar_icon);

			}
		}
		
		
		isInBoxDeleteMode = !isInBoxDeleteMode;
		mIsDeleteMode = isInBoxDeleteMode;
		
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	// @lent5 add to avoid overflow asyncTask
	private CheckDownloadPhoto mCheckDownloadPhoto;
	private DeleteCollectionHistory mDeleteCollectionHistory;
	private ProgressSavePhoto mProgressSavePhoto;
	
	private void showDeleteAllConfirm() {
		String title = mContext.getString(R.string.pb_content_delete_photo_inbox);
		if (!mIsInInbox) {
			title = mContext.getString(R.string.pb_content_delete_photo_outbox);
		}
		PBGeneralUtils.showAlertDialogAction(mContext, 
				mContext.getString(R.string.pb_title_delete_photo),
				title, 
				mContext.getString(R.string.dialog_ok_btn),
				mContext.getString(R.string.dialog_cancel_btn),
				mOnClickOkDialog);
	}

	private DialogInterface.OnClickListener mOnClickOkDialog = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (mIsInInbox) {
				if (PBDatabaseManager
						.getInstance(mContext)
						.getHistoriesWithCollectionId(
								mIsInInbox ? PBDatabaseDefinition.HISTORY_INBOX
										: PBDatabaseDefinition.HISTORY_SENT,
								mCollectionId).size() == 1) {
					(new Thread(new Runnable() {
						public void run() {
							PBAPIHelper.deleteDownloadedCollection(
									mCollectionId, mToken);
							
							
						}
					})).start();
				}
				mHistoryHandler.sendEmptyMessage(PB_HANDLER_DELETE_HISTORY_SUCCESS);
			
			} else {
				// @lent5 add to avoid overflow asyncTask
				// new DeleteCollectionHistory(mContext,
				// mCollectionId).execute();
				if (mDeleteCollectionHistory == null
					|| (mDeleteCollectionHistory != null 
						&& mDeleteCollectionHistory.getStatus() != AsyncTask.Status.RUNNING)) {
					mDeleteCollectionHistory = new DeleteCollectionHistory(
							mContext, mCollectionId);
					try {
						mDeleteCollectionHistory.execute();
					} catch (RejectedExecutionException e) {
						e.printStackTrace();
					}
				}
			}
		}
	};

	private int mSelected = 0;

	protected void onResume() {
		super.onResume();
	    // V2 google analytics has been comment out 
		if (PBTabBarActivity.gaTracker != null) {
			PBTabBarActivity.gaTracker.trackPageView("PBHistoryInboxDetailActivity");
		}

		if (doFinishAtNextResume) {
			doFinishAtNextResume = false;
			finish();
			return;
		}

		final PBCustomWaitingProgress mWaiting = new PBCustomWaitingProgress(this);
		mWaiting.showWaitingLayout();
		mThumbGrid.postDelayed(new Runnable() {
			@Override
			public void run() {
				mThumbGrid.setSelection(mSelected);
				mThumbGrid.requestFocus();
				mThumbGrid.requestFocusFromTouch();
				mThumbGrid.setSelection(mSelected);
				mThumbGrid.setSelected(true);
				mWaiting.hideWaitingLayout();
			}
		}, 600);
	};
	
	
	//Added below activity life cycle method for Google analytics
	@Override
    public void onStart() {
 	    super.onStart();
	    System.out.println("Atik start Easy Tracker for PBHistoryInboxDetailActivity");
	    EasyTracker.getInstance(this).activityStart(this);
    }
  
    //Added below activity life cycle method for Google analytics
    @Override
    public void onStop() {
	    super.onStop();
	    System.out.println("Atik stop Easy Tracker for PBHistoryInboxDetailActivity");
	    EasyTracker.getInstance(this).activityStop(this);
    }
	

	@Override
	protected void onPause() {
		mAdapter.clearCache();
		super.onPause();
	}

	// @lent5 add to avoid overflow asysntask #S
	private void cancelAsysnTask() {
		if (mCheckDownloadPhoto != null
				&& mCheckDownloadPhoto.getStatus() == AsyncTask.Status.RUNNING) {
			try {
				mCheckDownloadPhoto.cancel(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		mCheckDownloadPhoto = null;
	}

	// @lent5 add to avoid overflow asysntask #E

	@Override
	protected void onDestroy() {
		cancelAsysnTask();

		if (mAdapter != null) {
			mAdapter.recycleAll();
		}
		super.onDestroy();
		
// 		try {
// 			AdstirTerminate.init(this);
// 		} catch (Exception e) {}
	}
	
	private void setOutboxUI(boolean status) {
		if (mIsInInbox) {
		return;
			
			
		}
		
		mIsDeleteMode = status;
		if (mIsDeleteMode) {
			mBtnTopLeft.setButtonColor(getResources().getColor(R.color.fbutton_color_red_bg));
			//mBtnTopLeft.setBackgroundResource(R.drawable.pb_red_button_bg_black);
			mBtnTopLeft.setTextColor(Color.WHITE);
			mBtnTopRight.setButtonColor(getResources().getColor(R.color.fbutton_color_red_bg));
			//mBtnTopRight.setBackgroundResource(R.drawable.pb_red_button_bg_black);
			mBtnTopRight.setTextColor(Color.WHITE);
			
			mBtnMiddle.setVisibility(View.GONE);
			
			if(!isComeFromLibrary) {
				setHeader(mHeaderBar, mContext.getString(R.string.pb_title_detail_select));

			} else {
				setHeader(mHeaderBar, mContext.getString(R.string.pb_title_detail_select));
				mHeaderBar.setHomeLogo(R.drawable.openbag_actionbar_icon);

			}
			
			
			
		} else {
			mBtnTopLeft.setButtonColor(getResources().getColor(R.color.fbutton_color_inactive));
			//mBtnTopLeft.setBackgroundResource(R.drawable.pb_button_inactive);
			mBtnTopLeft.setTextColor(0xff666666);
			mBtnTopRight.setButtonColor(getResources().getColor(R.color.fbutton_color_inactive));
			//mBtnTopRight.setBackgroundResource(R.drawable.pb_button_inactive);
			mBtnTopRight.setTextColor(0xff666666);
			
			mBtnMiddle.setVisibility(View.VISIBLE);
			
			
			if(!isComeFromLibrary) {
				setHeader(mHeaderBar, mPassword);

			} else {
				setHeader(mHeaderBar, mPassword);
				mHeaderBar.setHomeLogo(R.drawable.openbag_actionbar_icon);

			}
		}
	}

	private class OnClickListenerButtom implements
			View.OnClickListener {
		@Override
		public void onClick(View v) {
			openConfirmScreen();
		}
	}
	
	private class OnClickListenerMiddle implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			doAddPhoto();
		}
	}

	private class OnClickListenerTopRight implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			if (mIsInInbox) {
				
				if(!isInBoxDeleteMode){
					doSavePhoto();
					
				}else{
					
					if(mDeleteSelection.size() > 0) {
						
						
						PBGeneralUtils.showAlertDialogAction(mContext, 
								mContext.getString(R.string.pb_title_detail_delete_confirm),
								String.format(mContext.getString(R.string.pb_msg_detail_delete_confirm), 
										mDeleteSelection.size()), 
								mContext.getString(R.string.dialog_ok_btn),
								mContext.getString(R.string.dialog_cancel_btn),
								new DialogInterface.OnClickListener() {									
									@Override
									public void onClick(DialogInterface dialog, int which) {
										
										
										System.out.println("Atik photo size:"+mPhotos.size());
										if(mPhotos.size() == mDeleteSelection.size()) {
											new Thread(new Runnable() {
												public void run() {
													PBAPIHelper.deleteDownloadedCollection(
															mCollectionId, mToken);
													//((Activity)mContext).finish();
													
												}
											}).start();
										
									        mHistoryHandler.sendEmptyMessage(PB_HANDLER_DELETE_HISTORY_SUCCESS);
											
										} else {
											
											PBDatabaseManager.getInstance(mContext).deletePhotos(
													mCollectionId, 
													mDeleteSelection);
											
											
											PBGeneralUtils.showAlertDialogActionWithOnClick(
													mContext,
													android.R.drawable.ic_dialog_alert,
													getString(R.string.pb_title_detail_delete_complete),
													getString(R.string.pb_msg_detail_delete_complete),
													getString(R.string.pb_btn_OK),
													new DialogInterface.OnClickListener() {
														public void onClick(DialogInterface dialog,
																int whichButton) {
															System.out.println("Atik go to delete here");
															//((Activity)mContext).finish();
															mPhotos = PBDatabaseManager.getInstance(getApplicationContext()).getPhotos(mCollectionId);
															//mAdapter.resetAdapter(mPhotos, null);
															
															
															// After successfull deletion need to clear the button flag
															deleteInboxItem(isInBoxDeleteMode);
															if (mDeleteSelection != null) {
																mDeleteSelection.clear();
															}
															
															mDeleteSelection = new ArrayList<String>();
															if (mAdapter != null) {
																mAdapter.setCheckBoxStatus(mIsDeleteMode);
																mAdapter.resetAdapter(mPhotos, mDeleteSelection);
															}
															//mAdapter.notifyDataSetChanged();
														}
													});
										}
										

										
									}
								});
						
						
					} else {
						Toast.makeText(mContext, 
								R.string.pb_toast_detail_no_selection, 
								Toast.LENGTH_SHORT).show();
					}
					
					
				}
			}
			else {
				if (mIsDeleteMode) {
					if (mDeleteSelection.isEmpty()) {
						Toast.makeText(mContext, 
								R.string.pb_toast_detail_no_selection, 
								Toast.LENGTH_SHORT).show();
					}
					else if (mDeleteSelection.size() == mPhotos.size()) {
						showDeleteAllConfirm();
					}
					else {
						PBGeneralUtils.showAlertDialogAction(mContext, 
								mContext.getString(R.string.pb_title_detail_delete_confirm),
								String.format(mContext.getString(R.string.pb_msg_detail_delete_confirm), 
										mDeleteSelection.size()), 
								mContext.getString(R.string.dialog_ok_btn),
								mContext.getString(R.string.dialog_cancel_btn),
								new DialogInterface.OnClickListener() {									
									@Override
									public void onClick(DialogInterface dialog, int which) {
										deleteSelectedPhotos();
									}
								});
					}
				}
			}
		}
	}

	private class OnClickListenerTopLeft implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			if (mIsInInbox) {
				if(!isInBoxDeleteMode){
					doAddPhoto();
					
				}else{
					
					showDeleteAllConfirm();
					
				}
				
			}
			else {
				if (mIsDeleteMode) {
					showDeleteAllConfirm();
				}
			}
		}
	}
	
	private void doSavePhoto() {
		if (mSaveMark != -1) {
			mLayoutInfoDownload.setVisibility(View.VISIBLE);
			mBtnButtom.setVisibility(View.GONE);
			mInfoDownload.setText("0 of " + mPhotos.size());

			// @lent5 add to avoid overflow asyncTask
			// new ProgressSavePhoto().execute();
			if (mProgressSavePhoto == null
					|| (mProgressSavePhoto != null && mProgressSavePhoto
							.getStatus() != AsyncTask.Status.RUNNING)) {
				mProgressSavePhoto = new ProgressSavePhoto();
				try {
					mProgressSavePhoto.execute();
				} catch (RejectedExecutionException e) {
					e.printStackTrace();
				}
			}
		} else {
			Toast.makeText(mContext,
					mContext.getString(R.string.pb_msg_detail_cannot_save),
					Toast.LENGTH_SHORT).show();
		}
	}
	
	private void doAddPhoto() {
		if (mAddibility == 1) {
			// PBAPIHelper.startAdding(mToken, mCollectionId);
			Intent intent;
			if (mIsInInbox) {
				intent = new Intent(mContext,
					PBUploadWarningActivity.class);
			} else {
				intent = new Intent(mContext,
					SelectMultipleImageActivity.class);
			}
			intent.putExtra(PBConstant.IS_OWNER, !mIsInInbox);
			intent.putExtra(PBConstant.COLLECTION_ID, mCollectionId);
			intent.putExtra(PBConstant.INTENT_PASSWORD, mPassword);
			startActivity(intent);
		} else {
			Toast.makeText(
					mContext,
					mContext.getString(R.string.pb_msg_detail_cannot_add),
					Toast.LENGTH_SHORT).show();
		}
	}

	private void openConfirmScreen() {
		Bundle extras = new Bundle();
		extras.putString(PBConstant.COLLECTION_PAGE_NAME,
				PBHistoryInboxDetailActivity.class.getName());
		extras.putString(PBConstant.COLLECTION_ID, mCollectionId);
		extras.putString(PBConstant.COLLECTION_PASSWORD, mPassword);
		extras.putLong(PBConstant.COLLECTION_CHARGE_AT, mChargeDate);
		extras.putString(PBConstant.COLLECTION_THUMB, mCollectionThumb);
		Intent intent = new Intent(PBHistoryInboxDetailActivity.this,
				PBConfirmPasswordActivity.class);
		intent.putExtra("data", extras);
		intent.putExtra(PBConstant.IS_OWNER, !mIsInInbox);
		startActivityForResult(intent, PBConstant.REQUEST_CODE_OPEN_CONFIRMPASS);
	}

	*//**
	 * Download photo if file is not exist in sdcard
	 *//*
	private void downloadPhoto() {
		// Check sdcard exist or not
		boolean sdcardMounted = PBGeneralUtils.checkSdcard(mContext, true,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialoginterface, int i) {
						finish();
					}
				});
		if (!sdcardMounted)
			return;

		for (int i = 0; i < mPhotos.size(); i++) {
			PBHistoryPhotoModel model = mPhotos.get(i);
			String pathThum = PBGeneralUtils.getPathFromCacheFolder(model.getThumb());
			String pathPhoto = PBGeneralUtils.getPathFromCacheFolder(model.getPhoto());
			if (!PBGeneralUtils.checkExistFile(pathPhoto)) {
				try {
					// Get photo from server to save local
					boolean isSavePhotoSuccess = PBAPIHelper.savePhoto(mToken,
							model.getPhoto(), mPassword, (mSaveMark!=0), null);

					createThumpFile(model.getPhoto(), model.getThumb(), true);
					if ((isSavePhotoSuccess)
							&& (!PBGeneralUtils.checkExistFile(pathThum))) {
						// Get thumb from server to save local if thump not
						// found and create failed
						PBAPIHelper.savePhoto(mToken, model.getPhoto(),
								mPassword, (mSaveMark!=0), null);
					}
					mIsUpdateThump = true;
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	private void createThumpFile(String urlPhoto, String urlThump,
			boolean isCentered) {
		createThumpFile(urlPhoto, urlThump, PBConstant.PHOTO_THUMB_WIDTH,
				PBConstant.PHOTO_THUMB_HEIGHT, isCentered);
	}

	private void createThumpFile(String urlPhoto, String urlThump, int width,
			int height, boolean isCentered) {
		Log.i("mapp", ">>> process crop real image to create thumb!");
		String realImgPath = PBGeneralUtils.getPathFromCacheFolder(urlPhoto);
		String thumbImgPath = PBGeneralUtils.getPathFromCacheFolder(urlThump);
		Bitmap bmp = null;
		try {
			Options mOptions = new Options();

			mOptions.inSampleSize = PBBitmapUtils.sampleSizeNeeded(realImgPath,
					PBConstant.MAXIMUM_IMAGE_SIZE_UPLOAD);
			if (isCentered) {
				bmp = PBBitmapUtils.centerCropImage(
						BitmapFactory.decodeFile(realImgPath, mOptions), width,
						height);
			} else {

				bmp = PBBitmapUtils.matrixResize(
						BitmapFactory.decodeFile(realImgPath, mOptions), width,
						height);
				Log.i("AGUNG", bmp.getWidth() + " " + bmp.getHeight() + " "
						+ mOptions.inSampleSize);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} catch (OutOfMemoryError oom) {
			Log.e("PBHistoryInboxDetailActivity",
					">>> Create thumb file, OOM when decode image "
							+ realImgPath);
		}
		FileOutputStream fos = null;
		try {			
			if (android.os.Environment.getExternalStorageState().equals(
					android.os.Environment.MEDIA_MOUNTED)) {
				fos = new FileOutputStream(new File(thumbImgPath));
			} else {
				// save on internal memory if sdcard is invalid.
				fos = PBApplication.getBaseApplicationContext().openFileOutput(
						String.valueOf(urlThump.hashCode()), 0);
			}
			if (bmp != null) {
				bmp.compress(PBConstant.COMPRESS_FORMAT,
						PBConstant.DECODE_COMPRESS_PRECENT, fos);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fos.flush();
				fos.close();
				fos = null;
				
				bmp.recycle();
				bmp = null;
			} catch (Exception e) {}
		}
	}

	private class ProgressSavePhoto extends AsyncTask<Void, Void, Void> {
		private int mProgress;
		private int mIndexPhoto;

		@Override
		protected void onPostExecute(Void result) {
			if (mLayoutInfoDownload != null) {
				mLayoutInfoDownload.setVisibility(View.GONE);
			}
			if (mBtnButtom != null) {
				mBtnButtom.setVisibility(View.VISIBLE);
			}
			if (mCustomWaitingLayout != null) {
				mCustomWaitingLayout.hideWaitingLayout();
			}
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			mProgress = 0;
			if (mProgressDownload != null) {
				mProgressDownload.setProgress(mProgress);
			}
			if (mCustomWaitingLayout != null) {
				mCustomWaitingLayout.showWaitingLayout();
			}
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			// Check sdcard exist or not
			boolean sdcardMounted = PBGeneralUtils.checkSdcard(mContext, true,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialoginterface,
								int i) {
							finish();
						}
					});
			if (!sdcardMounted)
				return null;

			for (int i = 0; i < mPhotos.size(); i++) {
				mIndexPhoto = i + 1;
				PBHistoryPhotoModel photo = mPhotos.get(i);
				String srcPath = PBGeneralUtils.getPathFromCacheFolder(photo.getPhoto());
				// Bitmap bm = null;
				if (!PBGeneralUtils.checkExistFile(srcPath)) {
					try {
						// Get photo from server to save local
						PBAPIHelper.savePhoto(mToken, photo.getPhoto(),
								mPassword, (mSaveMark!=0), null);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}

				try {
					if (!TextUtils.isEmpty(srcPath)) {
						PBGeneralUtils.saveInCameraRoll(mContext,
								srcPath, 
								photo.getMediaType(),
								photo.getVideoDuration());
						// 20120313 mod by NhatVT, save image directly <E>
					}
					mProgress++;
					if (mProgressDownload != null) {
						mProgressDownload.setProgress(mProgress);
					}

					if (mContext != null) {
						((Activity) mContext).runOnUiThread(new Runnable() {
							@Override
							public void run() {
								mInfoDownload.setText(mIndexPhoto + " of "
										+ mPhotos.size());
							}
						});
					}
					Thread.sleep(TIME_SLEEP);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return null;
		}

	}

	private Handler mHistoryHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case PB_HANDLER_DELETE_HISTORY_SUCCESS:
				// lent5 added because should delete on asynctask
				// in case user press back in delete period
				// this function could not reach
				if (mIsInInbox) {
					PBDatabaseManager.getInstance(mContext).deleteHistory(
							String.valueOf(mHistoryId), mCollectionId);
				}
				PBPreferenceUtils.saveBoolPref(PBHistoryInboxDetailActivity.this, PBConstant.PREF_NAME, 
		    			PBConstant.ISDOWNLOAD, true);
				finish();
				break;
			case PB_HANDLER_DELETE_HISTORY_FAIL:
				PBGeneralUtils.showAlertDialogAction(
							mContext,
							mContext.getString(R.string.dialog_network_error_title),
							mContext.getString(R.string.dialog_network_error_body),
							null, 
							mContext.getString(R.string.dialog_ok_btn),
							null);
				break;
			case ResponseHandle.CODE_200_OK:
				break;
			case ResponseHandle.CODE_HTTP_FAIL:
			case ResponseHandle.CODE_INVALID_PARAMS:
			case ResponseHandle.CODE_403:
			case ResponseHandle.CODE_404:
			case ResponseHandle.CODE_400:
				break;
			default:
				break;
			}
		};
	};
	
	private void deleteSelectedPhotos() {
		final ProgressDialog progressDelete = new ProgressDialog(mContext);
		if (mContext != null) {
			progressDelete.setMessage(mContext
					.getString(R.string.pb_connecting));
			progressDelete.show();
		}
		
		new Thread() {
			@Override
			public void run() {				
				try {					
					Response response = PBAPIHelper.deletePartsCollection(
							mCollectionId, 
							mToken, 
							mDeleteSelection);
					
					if (response.errorCode == HttpStatus.SC_OK||
							response.errorCode == HttpStatus.SC_NOT_FOUND) {  // Bug fixes , when data deleted from server below block will be 
						  // executed even if response code is 404
						PBDatabaseManager.getInstance(mContext).deletePhotos(
								mCollectionId, 
								mDeleteSelection);
						
						mHistoryHandler.post(new Runnable() {
							@Override
							public void run() {
								if (mContext != null && progressDelete != null) {
									progressDelete.cancel();
								}
								
								PBGeneralUtils.showAlertDialogActionWithOnClick(
										mContext,
										android.R.drawable.ic_dialog_alert,
										getString(R.string.pb_title_detail_delete_complete),
										getString(R.string.pb_msg_detail_delete_complete),
										getString(R.string.pb_btn_OK),
										new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog,
													int whichButton) {
												System.out.println("Atik Inbox selected item deleted");
												mPhotos = PBDatabaseManager.getInstance(getApplicationContext()).getPhotos(mCollectionId);
												//mAdapter.resetAdapter(mPhotos, null);
												
												//((Activity)mContext).finish();
												
												
												// After successfull deletion need to clear the button flag
												
												setOutboxUI(!mIsDeleteMode);
												if (mDeleteSelection != null) {
													mDeleteSelection.clear();
												}
												mDeleteSelection = new ArrayList<String>();
												if (mAdapter != null) {
													mAdapter.setCheckBoxStatus(mIsDeleteMode);
													mAdapter.resetAdapter(mPhotos, mDeleteSelection);
												}
											}
										});
							}					
						});
						
						return;
					}
					if (response.errorCode == HttpStatus.SC_NOT_FOUND) {
						mHistoryHandler.post(new Runnable() {
							@Override
							public void run() {
								if (mContext != null && progressDelete != null) {
									progressDelete.cancel();
								}
								
								PBGeneralUtils.showAlertDialogActionWithOkButton(
										mContext, 
										mContext.getString(R.string.pb_title_detail_delete_fail), 
										mContext.getString(R.string.pb_msg_detail_delete_impossible), 
										mContext.getString(R.string.pb_btn_OK));					
							}					
						});
						
						return;
					}
				} catch (Exception e) {}
				
				mHistoryHandler.post(new Runnable() {
					@Override
					public void run() {
						if (mContext != null && progressDelete != null) {
							progressDelete.cancel();
						}
						
						PBGeneralUtils.showAlertDialogActionWithOkButton(
								mContext, 
								mContext.getString(R.string.pb_title_detail_delete_fail), 
								mContext.getString(R.string.pb_msg_detail_delete_fail), 
								mContext.getString(R.string.pb_btn_OK));					
					}					
				});
			}
		}.start();
	}

	*//**
	 * Delete collection id in history send
	 *//*
	public class DeleteCollectionHistory extends AsyncTask<Void, Void, Void> {

		private String mCollectionId;
		private Context mContext;
		private Response mResponse;
		private ProgressDialog mProgressDelete;

		public DeleteCollectionHistory(Context context, String collectionId) {
			mContext = context;
			mCollectionId = collectionId;
		}

		@Override
		protected Void doInBackground(Void... params) {
			String token = PBPreferenceUtils.getStringPref(mContext,
					PBConstant.PREF_NAME, PBConstant.PREF_NAME_TOKEN, "");
			mResponse = PBAPIHelper.deleteUploadedCollection(mCollectionId,
					token);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			System.out.println("Come here for result code"+mResponse.errorCode);
			if (mResponse.errorCode == HttpStatus.SC_OK) {
				// add to avoid user press back in delete period
				PBDatabaseManager.getInstance(mContext).deleteHistory(
						String.valueOf(mHistoryId), mCollectionId);
				if (mHistoryHandler != null) {
					mHistoryHandler
							.sendEmptyMessage(PB_HANDLER_DELETE_HISTORY_SUCCESS);
				}
			} else if(mResponse.errorCode == HttpStatus.SC_NOT_FOUND) { // Bug fixes , when data deleted from server below block will be 
																		// executed even if response code is 404
				//System.out.println("Come here for result code"+mResponse.errorCode+"L"+HttpStatus.SC_NOT_FOUND);
				System.out.println("Atik response code photo delete:"+mResponse.errorCode);
				// add to avoid user press back in delete period
				PBDatabaseManager.getInstance(mContext).deleteHistory(
						String.valueOf(mHistoryId), mCollectionId);
				if (mHistoryHandler != null) {
					mHistoryHandler
							.sendEmptyMessage(PB_HANDLER_DELETE_HISTORY_SUCCESS);
				}
			} else {
				if (mHistoryHandler != null) {
					mHistoryHandler
							.sendEmptyMessage(PB_HANDLER_DELETE_HISTORY_FAIL);
				}
			}

			if (mProgressDelete != null && mProgressDelete.isShowing()) {
				mProgressDelete.dismiss();
			}

			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			if (mContext != null) {
				mProgressDelete = new ProgressDialog(mContext);
				mProgressDelete.setMessage(mContext
						.getString(R.string.pb_connecting));
				mProgressDelete.show();
			}
			super.onPreExecute();
		}
	}

	@Override
	protected void handleHomeActionListener() {
		finish();
	}

	@Override
	protected void setLeftBtnsDetailHeader(ActionBar headerBar) {

	}

	public class CheckDownloadPhoto extends AsyncTask<Void, Void, Void> {
		// private ProgressDialog mProgressDialog;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			downloadPhoto();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// mProgressDialog.dismiss();
			if (mIsUpdateThump) {
				mAdapter.notifyDataSetChanged();
				mThumbGrid.requestFocusFromTouch();
				mThumbGrid.setSelection(0);
			}
			super.onPostExecute(result);
		}
	}

	private void setUserRegistration() {

		final Dialog dialog = new Dialog(mContext);
		dialog.setContentView(R.layout.nick_name_dialog);
		dialog.setTitle("Registration");
		// Grab the window of the dialog, and change the width
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		Window window = dialog.getWindow();
		lp.copyFrom(window.getAttributes());
		// This makes the dialog take up the full width
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		window.setAttributes(lp);

		final EditText nickName = (EditText) dialog
				.findViewById(R.id.editText_nickname);

		Button dialogButton = (Button) dialog
				.findViewById(R.id.button_name_confirm);
		dialogButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if(nickName.getText().toString().length() > 0){
					
					dialog.dismiss();
					PBCustomWaitingProgress waitingProg = new PBCustomWaitingProgress(
							PBHistoryInboxDetailActivity.this);
					PBTaskUserRegistration task = new PBTaskUserRegistration(nickName.getText().toString(),waitingProg);
					task.execute();
				}else{
					
					Toast.makeText(mContext, "nick name cannot be blank", Toast.LENGTH_SHORT).show();
				}
				
			}
		});

		dialog.show();

	}

	private class PBTaskUserRegistration extends
			AsyncTask<Void, Void, Response> {
		private PBCustomWaitingProgress waitingProgress;
		private String nickName;
		public PBTaskUserRegistration(String name,
				PBCustomWaitingProgress waitingProgress) {
			this.nickName = name;
			this.waitingProgress = waitingProgress;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (waitingProgress != null)
				waitingProgress.showWaitingLayout();
		}

		@Override
		protected Response doInBackground(Void... params) {
			String token = PBPreferenceUtils.getStringPref(
					PBApplication.getBaseApplicationContext(),
					PBConstant.PREF_NAME, PBConstant.PREF_NAME_TOKEN, "");
			String deviceUUID =  PBPreferenceUtils.getStringPref(PBApplication
	                .getBaseApplicationContext(), PBConstant.PREF_NAME,
	                PBConstant.PREF_NAME_UID, "0");
			String manufacturer = Build.MANUFACTURER;
			String model = Build.MODEL;
			String deviceName;
			if (model.startsWith(manufacturer)) {
				deviceName = model.toUpperCase();
			} else {
				deviceName = manufacturer.toUpperCase() + " " + model.toUpperCase();
			}
			
			if (!TextUtils.isEmpty(token)) {
				Response response = PBAPIHelper.nickNameRegistration(token, nickName, deviceUUID, "Android", deviceName);
				return response;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Response result) {
			super.onPostExecute(result);
			if (waitingProgress != null)
				waitingProgress.hideWaitingLayout();
			
			Log.d("response", result.decription);

			if (result != null) {
				if (result.errorCode == ResponseHandle.CODE_200_OK) {
					Toast.makeText(mContext, "OK", Toast.LENGTH_SHORT).show();
					PBPreferenceUtils.saveStringPref(mContext, PBConstant.PREF_NAME,
							PBConstant.PREF_NICK_NAME, nickName);

						Bundle extras = new Bundle();
						extras.putLong(PBConstant.HISTORY_ITEM_ID,
								mHistoryId);
						extras.putString(PBConstant.HISTORY_COLLECTION_ID,
								mCollectionId);
						extras.putString(PBConstant.HISTORY_PASSWORD,
								mPassword);
						extras.putLong(PBConstant.HISTORY_CHARGE_DATE,
								mChargeDate);
						extras.putString(PBConstant.COLLECTION_THUMB,
								mCollectionThumb);
						extras.putInt(PBConstant.HISTORY_ADDIBILITY,
								mAddibility);
						extras.putBoolean(
								PBConstant.HISTORY_CATEGORY_INBOX,
								mIsInInbox);
						Intent detail = new Intent(
								PBHistoryInboxDetailActivity.this,
								PBChatActivity.class);
						detail.putExtra("data", extras);
						mContext.startActivity(detail);
						

				} else if(result.errorCode == ResponseHandle.CODE_403){
					
					PBGeneralUtils.showAlertDialogNoTitleWithOnClick(mContext,
							"Already Exist");
				}
				
				else {
					try {
						Log.e("AGUNG", "Error :" + result.errorCode + " -> "
								+ result.decription);
					} catch (Exception e) {// TODO
						e.printStackTrace();
						Log.e("AGUNG", "Error :" + result.errorCode + " -> "
								+ result.decription);
					}
				}
			}

		}

	}
	
}
*/

package com.aircast.photobag.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.concurrent.RejectedExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.aircast.photobag.R;
import com.aircast.photobag.adapter.PBHistoryDetailAdapter;
import com.aircast.photobag.api.PBAPIHelper;
import com.aircast.photobag.api.ResponseHandle;
import com.aircast.photobag.api.ResponseHandle.Response;
import com.aircast.photobag.application.PBApplication;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.database.ChatDatabaseHandler;
import com.aircast.photobag.database.PBDatabaseDefinition;
import com.aircast.photobag.database.PBDatabaseManager;
import com.aircast.photobag.model.PBHistoryPhotoModel;
import com.aircast.photobag.utils.PBBitmapUtils;
import com.aircast.photobag.utils.PBGeneralUtils;
import com.aircast.photobag.utils.PBPreferenceUtils;
import com.aircast.photobag.widget.FButton;
import com.aircast.photobag.widget.PBCustomWaitingProgress;
import com.aircast.photobag.widget.actionbar.ActionBar;
import com.aircast.photobag.widget.actionbar.ActionBar.Action;

/**
 * Show photos in password selected.
 * */
public class PBHistoryInboxDetailActivity extends PBAbsActionBarActivity {

	private LinearLayout mLayoutInfoDownload;
	private ProgressBar mProgressDownload;
	private FButton mBtnTopLeft;
	private FButton mBtnTopRight;
	private FButton mBtnMiddle;
	private FButton mBtnButtom;
	private Context mContext;
	private boolean mIsInInbox;
	private boolean mIsDeleteMode;
	private ActionBar mHeaderBar;
	private String mCollectionId, mPassword, mCollectionThumb;
	private int mAddibility;
	private int mSaveMark;
	private String mAdLink;
	private long mChargeDate;
	private long mHistoryId;
	private ArrayList<PBHistoryPhotoModel> mPhotos;
	private ArrayList<String> mDeleteSelection;
	private String mToken;
	private TextView mInfoDownload;
	private PBHistoryDetailAdapter mAdapter;
	private static final int PB_HANDLER_DELETE_HISTORY_SUCCESS = 1;
	private static final int PB_HANDLER_DELETE_HISTORY_FAIL = 2;
	private static final int TIME_SLEEP = 200;
	private boolean mIsUpdateThump = false;
	private final int REQUEST_PHOTO_VIEW = 110;
	private GridView mThumbGrid;
	private boolean isInBoxDeleteMode = false;
	boolean isComeFromLibrary = false;
	boolean isDefaultPassword = false;
	// private RelativeLayout homeActionBar;
	/**
	 * Waiting progress dialog.
	 */
	private PBCustomWaitingProgress mCustomWaitingLayout;
	private TextView numberOfUnreadMGS;

	public static boolean doFinishAtNextResume;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.pb_layout_history_detail);

		mContext = this;
		mToken = PBPreferenceUtils.getStringPref(mContext,
				PBConstant.PREF_NAME, PBConstant.PREF_NAME_TOKEN, "");

		doFinishAtNextResume = false;

		if (getIntent() == null) {
			finish();
			return;
		}
		Bundle extras = getIntent().getBundleExtra("data");
		if (extras == null) {
			finish();
			return;
		}

		mIsInInbox = extras.getBoolean(PBConstant.HISTORY_CATEGORY_INBOX);
		mPassword = extras.getString(PBConstant.HISTORY_PASSWORD);
		Pattern pattern = Pattern.compile("\\s");

		Matcher matcher = pattern.matcher(mPassword);

		mPassword = matcher.find() ? mPassword.substring(0,mPassword.indexOf(' ')) : mPassword;

		mHistoryId = extras.getLong(PBConstant.HISTORY_ITEM_ID);
		mCollectionId = extras.getString(PBConstant.HISTORY_COLLECTION_ID);
		mChargeDate = extras.getLong(PBConstant.HISTORY_CHARGE_DATE);
		mCollectionThumb = extras.getString(PBConstant.COLLECTION_THUMB);
		mAddibility = extras.getInt(PBConstant.HISTORY_ADDIBILITY);
		mSaveMark = extras.getInt(PBConstant.HISTORY_SAVE_MARK);
		mAdLink = extras.getString(PBConstant.HISTORY_AD_LINK);

		mPhotos = PBDatabaseManager.getInstance(this).getPhotos(mCollectionId);

		for (int i = 0; i < mPhotos.size(); i++) {
			PBHistoryPhotoModel modelLocal = mPhotos.get(i);
			System.out.println("photos of history:"
					+ PBGeneralUtils.getPathFromCacheFolder(modelLocal
							.getPhoto()));
		}

		mBtnTopLeft = (FButton) findViewById(R.id.btn_detail_top_left);
		mBtnTopLeft.setOnClickListener(new OnClickListenerTopLeft());

		mBtnTopRight = (FButton) findViewById(R.id.btn_detail_top_right);
		mBtnTopRight.setOnClickListener(new OnClickListenerTopRight());

		mBtnButtom = (FButton) findViewById(R.id.btn_detail_buttom);
		mBtnButtom.setOnClickListener(new OnClickListenerButtom());

		mBtnMiddle = (FButton) findViewById(R.id.btn_detail_middle);
		mBtnMiddle.setOnClickListener(new OnClickListenerMiddle());

		mIsDeleteMode = false;
		setOutboxUI(mIsDeleteMode);

		mLayoutInfoDownload = (LinearLayout) findViewById(R.id.layout_info_download);
		mProgressDownload = (ProgressBar) findViewById(R.id.progress_download);
		mProgressDownload.setMax(mPhotos.size());
		mProgressDownload.setProgress(0);

		mInfoDownload = (TextView) findViewById(R.id.txt_info_download);
		// Add header bar layout
		mHeaderBar = (ActionBar) findViewById(R.id.headerbar);
		setHeader(mHeaderBar, mPassword);
		
		if (mPassword != null) {
			if (!mPassword.isEmpty()
					&& mPassword
							.equalsIgnoreCase(getString(R.string.pb_dl_firstclick_magicphrase_defaulse))
					|| mPassword
							.equalsIgnoreCase(getString(R.string.pb_dl_firstclick_magicphrase_defaulse_other))) {
			//	System.out.println("Atik no need to show chat option");
				isDefaultPassword  = true;
				
			} else {
				//System.out.println("Atik display chat option");
				final boolean hasInternet = PBApplication
						.hasNetworkConnection();

				// Chat button
				final Action chatAction = new ActionBar.ViewAction(this,
						new ActionBar.PerformActionListener() {
							@Override
							public void performAction(View view) {


								Bundle extras = new Bundle();
								extras.putLong(PBConstant.HISTORY_ITEM_ID,
										mHistoryId);
								extras.putString(
										PBConstant.HISTORY_COLLECTION_ID,
										mCollectionId);
								extras.putString(PBConstant.HISTORY_PASSWORD,
										mPassword);
								extras.putLong(PBConstant.HISTORY_CHARGE_DATE,
										mChargeDate);
								extras.putString(PBConstant.COLLECTION_THUMB,
										mCollectionThumb);
								extras.putInt(PBConstant.HISTORY_ADDIBILITY,
										mAddibility);
								extras.putBoolean(
										PBConstant.HISTORY_CATEGORY_INBOX,
										mIsInInbox);
								Intent detail = new Intent(
										PBHistoryInboxDetailActivity.this,
										PBChatActivity.class);
								detail.putExtra("data", extras);
								mContext.startActivity(detail);

							}

						}, R.drawable.btn_chat);
				chatAction.setBackground(getResources().getDrawable(
						R.drawable.actionbar_home_btn));
				mHeaderBar.addAction(chatAction);

				//View view = mHeaderBar.getActionview();
				View view = mHeaderBar.getActionview();
				
				numberOfUnreadMGS = (TextView) view
						.findViewById(R.id.textView_number_of_unread_mgs); // Atik comment out

			}

			if (getIntent().hasExtra(PBConstant.PREF_PASSWORD_FROM_LIBRARY)) {
				boolean isFromLibrary = getIntent().getExtras().getBoolean(
						PBConstant.PREF_PASSWORD_FROM_LIBRARY);
				if (isFromLibrary) {
					isComeFromLibrary = true;
					mHeaderBar.setHomeLogo(R.drawable.openbag_actionbar_icon);
				}

			}
		}

		final Action otherAction = new ActionBar.ViewAction(this,
				new ActionBar.PerformActionListener() {
					@Override
					public void performAction(View view) {

						System.out.println("Atik mIsInInbox" + mIsInInbox);

						if (mIsInInbox) {
							// showDeleteAllConfirm();
							deleteInboxItem(isInBoxDeleteMode);

						} else {
							setOutboxUI(!mIsDeleteMode);
						}

						if (mDeleteSelection != null) {
							mDeleteSelection.clear();
						}
						mDeleteSelection = new ArrayList<String>();
						if (mAdapter != null) {
							mAdapter.setCheckBoxStatus(mIsDeleteMode);
							mAdapter.resetAdapter(mPhotos, mDeleteSelection);
						}
					}

				}, R.drawable.icon_delete);
		otherAction.setBackground(getResources().getDrawable(
				R.drawable.actionbar_home_btn));
		mHeaderBar.addAction(otherAction);

		if (mIsInInbox) {
			mBtnTopLeft.setText(R.string.pb_add_photos);
			mBtnTopRight.setText(R.string.pb_save_all_picture);
			mBtnMiddle.setVisibility(View.GONE);
			if (mAddibility == 0) {

				mBtnTopLeft.setButtonColor(getResources().getColor(
						R.color.fbutton_color_inactive));
				// mBtnTopLeft.setBackgroundResource(R.drawable.pb_button_inactive);
				mBtnTopLeft.setTextColor(0xff666666);
			}
			if (mSaveMark == -1) {
				mBtnTopRight.setButtonColor(getResources().getColor(
						R.color.fbutton_color_inactive));
				// mBtnTopRight.setBackgroundResource(R.drawable.pb_button_inactive);
				mBtnTopRight.setTextColor(0xff666666);
			}

		} else {
			mBtnTopLeft.setText(R.string.pb_btn_detail_delete_all);
			mBtnTopRight.setText(R.string.pb_btn_detail_delete_part);

			mBtnMiddle.setVisibility(View.VISIBLE);
			if (mAddibility == 0) {
				mBtnMiddle.setButtonColor(getResources().getColor(
						R.color.fbutton_color_inactive));
				// mBtnMiddle.setBackgroundResource(R.drawable.pb_button_inactive);
				mBtnMiddle.setTextColor(0xff666666);
			}
		}

		// 20120221 mod by NhatVT, change Button by using LinearLayout <E>
		mAdapter = new PBHistoryDetailAdapter(this);
		mAdapter.resetAdapter(mPhotos, null);
		mThumbGrid = (GridView) findViewById(R.id.gv_history_detail);
		mThumbGrid.setAdapter(mAdapter);
		mThumbGrid.setSelected(true);
		mThumbGrid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (mIsDeleteMode) {
					String url = mPhotos.get(arg2).getPhoto();
					if (mDeleteSelection.contains(url)) {
						mDeleteSelection.remove(url);
					} else {
						mDeleteSelection.add(url);
					}
					mAdapter.resetAdapter(mPhotos, mDeleteSelection);
				} else {
					mSelected = arg2;
					Intent intent = new Intent(mContext,
							PBImageViewerActivity.class);
					intent.putExtra("PHOTO_ID", arg2);
					intent.putExtra(PBConstant.IS_OWNER, !mIsInInbox);
					intent.putExtra("COLECTION_ID", mCollectionId);
					intent.putExtra("COLECTION_PASSWORD", mPassword);
					intent.putExtra("COLECTION_SAVEMARK", mSaveMark);
					intent.putExtra("COLECTION_ADLINK", mAdLink);
					startActivityForResult(intent, REQUEST_PHOTO_VIEW);
				}
			}
		});

		// @lent5 add to avoid overflow asyncTask
		if (mCheckDownloadPhoto == null
				|| (mCheckDownloadPhoto != null && mCheckDownloadPhoto
						.getStatus() != AsyncTask.Status.RUNNING)) {
			mCheckDownloadPhoto = new CheckDownloadPhoto();
			try {
				mCheckDownloadPhoto.execute();
			} catch (RejectedExecutionException e) {
				e.printStackTrace();
			}
		}

		// new CheckDownloadPhoto().execute();
		mCustomWaitingLayout = new PBCustomWaitingProgress(this);
	}

	private void deleteInboxItem(boolean status) {

		if (status) {
			mBtnTopLeft.setText(R.string.pb_add_photos);
			mBtnTopRight.setText(R.string.pb_save_all_picture);
			// mBtnMiddle.setVisibility(View.GONE);
			if (mAddibility == 0) {

				mBtnTopLeft.setButtonColor(getResources().getColor(
						R.color.fbutton_color_inactive));
				// mBtnTopLeft.setBackgroundResource(R.drawable.pb_button_inactive);
				mBtnTopLeft.setTextColor(0xff666666);
			}
			if (mSaveMark == -1) {
				mBtnTopRight.setButtonColor(getResources().getColor(
						R.color.fbutton_color_inactive));
				// mBtnTopRight.setBackgroundResource(R.drawable.pb_button_inactive);
				mBtnTopRight.setTextColor(0xff666666);
			}

			if (!isComeFromLibrary) {
				setHeader(mHeaderBar, mPassword);

			} else {
				setHeader(mHeaderBar, mPassword);
				mHeaderBar.setHomeLogo(R.drawable.openbag_actionbar_icon);
			}

		} else {

			mBtnTopLeft.setButtonColor(getResources().getColor(
					R.color.fbutton_color_red_bg));
			// mBtnTopLeft.setBackgroundResource(R.drawable.pb_red_button_bg_black);
			mBtnTopLeft.setTextColor(Color.WHITE);
			mBtnTopRight.setButtonColor(getResources().getColor(
					R.color.fbutton_color_red_bg));
			// mBtnTopRight.setBackgroundResource(R.drawable.pb_red_button_bg_black);
			mBtnTopRight.setTextColor(Color.WHITE);

			mBtnMiddle.setVisibility(View.GONE);

			mBtnTopLeft.setText(R.string.pb_btn_detail_delete_all);
			mBtnTopRight.setText(R.string.pb_btn_detail_delete_part);

			if (!isComeFromLibrary) {
				setHeader(mHeaderBar,
						mContext.getString(R.string.pb_title_detail_select));

			} else {
				setHeader(mHeaderBar,
						mContext.getString(R.string.pb_title_detail_select));
				mHeaderBar.setHomeLogo(R.drawable.openbag_actionbar_icon);

			}
		}

		isInBoxDeleteMode = !isInBoxDeleteMode;
		mIsDeleteMode = isInBoxDeleteMode;

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	// @lent5 add to avoid overflow asyncTask
	private CheckDownloadPhoto mCheckDownloadPhoto;
	private DeleteCollectionHistory mDeleteCollectionHistory;
	private ProgressSavePhoto mProgressSavePhoto;

	private void showDeleteAllConfirm() {
		String title = mContext
				.getString(R.string.pb_content_delete_photo_inbox);
		if (!mIsInInbox) {
			title = mContext.getString(R.string.pb_content_delete_photo_outbox);
		}
		PBGeneralUtils.showAlertDialogAction(mContext,
				mContext.getString(R.string.pb_title_delete_photo), title,
				mContext.getString(R.string.dialog_ok_btn),
				mContext.getString(R.string.dialog_cancel_btn),
				mOnClickOkDialog);
	}

	private DialogInterface.OnClickListener mOnClickOkDialog = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (mIsInInbox) {
				if (PBDatabaseManager
						.getInstance(mContext)
						.getHistoriesWithCollectionId(
								mIsInInbox ? PBDatabaseDefinition.HISTORY_INBOX
										: PBDatabaseDefinition.HISTORY_SENT,
								mCollectionId).size() == 1) {
					(new Thread(new Runnable() {
						public void run() {
							PBAPIHelper.deleteDownloadedCollection(
									mCollectionId, mToken);

						}
					})).start();
				}
				mHistoryHandler
						.sendEmptyMessage(PB_HANDLER_DELETE_HISTORY_SUCCESS);

			} else {
				// @lent5 add to avoid overflow asyncTask
				// new DeleteCollectionHistory(mContext,
				// mCollectionId).execute();
				if (mDeleteCollectionHistory == null
						|| (mDeleteCollectionHistory != null && mDeleteCollectionHistory
								.getStatus() != AsyncTask.Status.RUNNING)) {
					mDeleteCollectionHistory = new DeleteCollectionHistory(
							mContext, mCollectionId);
					try {
						mDeleteCollectionHistory.execute();
					} catch (RejectedExecutionException e) {
						e.printStackTrace();
					}
				}
			}
		}
	};

	private int mSelected = 0;

	protected void onResume() {
		super.onResume();
		// V2 google analytics has been comment out
		/*
		 * if (PBTabBarActivity.gaTracker != null) {
		 * PBTabBarActivity.gaTracker.trackPageView
		 * ("PBHistoryInboxDetailActivity"); }
		 */

		if (doFinishAtNextResume) {
			doFinishAtNextResume = false;
			finish();
			return;
		}

		final PBCustomWaitingProgress mWaiting = new PBCustomWaitingProgress(
				this);
		mWaiting.showWaitingLayout();
		mThumbGrid.postDelayed(new Runnable() {
			@Override
			public void run() {
				mThumbGrid.setSelection(mSelected);
				mThumbGrid.requestFocus();
				mThumbGrid.requestFocusFromTouch();
				mThumbGrid.setSelection(mSelected);
				mThumbGrid.setSelected(true);
				mWaiting.hideWaitingLayout();
			}
		}, 600);

		//Toast.makeText(getApplicationContext(), "on resume call", 1000).show();
		if (mPassword != null && mCollectionId != null && numberOfUnreadMGS != null) {

			//if(!isDefaultPassword) { 
				new PBTaskGetUnreadMessage().execute();
			//}
		}
	}

	// Added below activity life cycle method for Google analytics
	@Override
	public void onStart() {
		super.onStart();
//		System.out
//				.println("Atik start Easy Tracker for PBHistoryInboxDetailActivity");
		EasyTracker.getInstance(this).activityStart(this);
	}

	// Added below activity life cycle method for Google analytics
	@Override
	public void onStop() {
		super.onStop();
//		System.out
//				.println("Atik stop Easy Tracker for PBHistoryInboxDetailActivity");
		EasyTracker.getInstance(this).activityStop(this);
	}

	@Override
	protected void onPause() {
		mAdapter.clearCache();
		super.onPause();
	}

	// @lent5 add to avoid overflow asysntask #S
	private void cancelAsysnTask() {
		if (mCheckDownloadPhoto != null
				&& mCheckDownloadPhoto.getStatus() == AsyncTask.Status.RUNNING) {
			try {
				mCheckDownloadPhoto.cancel(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		mCheckDownloadPhoto = null;
	}

	// @lent5 add to avoid overflow asysntask #E

	@Override
	protected void onDestroy() {
		cancelAsysnTask();

		if (mAdapter != null) {
			mAdapter.recycleAll();
		}
		super.onDestroy();

		// try {
		// AdstirTerminate.init(this);
		// } catch (Exception e) {}
	}

	private void setOutboxUI(boolean status) {
		if (mIsInInbox) {
			return;

		}

		mIsDeleteMode = status;
		if (mIsDeleteMode) {
			mBtnTopLeft.setButtonColor(getResources().getColor(
					R.color.fbutton_color_red_bg));
			// mBtnTopLeft.setBackgroundResource(R.drawable.pb_red_button_bg_black);
			mBtnTopLeft.setTextColor(Color.WHITE);
			mBtnTopRight.setButtonColor(getResources().getColor(
					R.color.fbutton_color_red_bg));
			// mBtnTopRight.setBackgroundResource(R.drawable.pb_red_button_bg_black);
			mBtnTopRight.setTextColor(Color.WHITE);

			mBtnMiddle.setVisibility(View.GONE);

			if (!isComeFromLibrary) {
				setHeader(mHeaderBar,
						mContext.getString(R.string.pb_title_detail_select));

			} else {
				setHeader(mHeaderBar,
						mContext.getString(R.string.pb_title_detail_select));
				mHeaderBar.setHomeLogo(R.drawable.openbag_actionbar_icon);

			}

		} else {
			mBtnTopLeft.setButtonColor(getResources().getColor(
					R.color.fbutton_color_inactive));
			// mBtnTopLeft.setBackgroundResource(R.drawable.pb_button_inactive);
			mBtnTopLeft.setTextColor(0xff666666);
			mBtnTopRight.setButtonColor(getResources().getColor(
					R.color.fbutton_color_inactive));
			// mBtnTopRight.setBackgroundResource(R.drawable.pb_button_inactive);
			mBtnTopRight.setTextColor(0xff666666);

			mBtnMiddle.setVisibility(View.VISIBLE);

			if (!isComeFromLibrary) {
				setHeader(mHeaderBar, mPassword);

			} else {
				setHeader(mHeaderBar, mPassword);
				mHeaderBar.setHomeLogo(R.drawable.openbag_actionbar_icon);

			}
		}
	}

	private class OnClickListenerButtom implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			openConfirmScreen();
		}
	}

	private class OnClickListenerMiddle implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			doAddPhoto();
		}
	}

	private class OnClickListenerTopRight implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			if (mIsInInbox) {

				if (!isInBoxDeleteMode) {
					doSavePhoto();

				} else {

					if (mDeleteSelection.size() > 0) {

						PBGeneralUtils
								.showAlertDialogAction(
										mContext,
										mContext.getString(R.string.pb_title_detail_delete_confirm),
										String.format(
												mContext.getString(R.string.pb_msg_detail_delete_confirm),
												mDeleteSelection.size()),
										mContext.getString(R.string.dialog_ok_btn),
										mContext.getString(R.string.dialog_cancel_btn),
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {

												System.out
														.println("Atik photo size:"
																+ mPhotos
																		.size());
												if (mPhotos.size() == mDeleteSelection
														.size()) {
													new Thread(new Runnable() {
														public void run() {
															PBAPIHelper
																	.deleteDownloadedCollection(
																			mCollectionId,
																			mToken);
															// ((Activity)mContext).finish();

														}
													}).start();

													mHistoryHandler
															.sendEmptyMessage(PB_HANDLER_DELETE_HISTORY_SUCCESS);

												} else {

													PBDatabaseManager
															.getInstance(
																	mContext)
															.deletePhotos(
																	mCollectionId,
																	mDeleteSelection);

													PBGeneralUtils
															.showAlertDialogActionWithOnClick(
																	mContext,
																	android.R.drawable.ic_dialog_alert,
																	getString(R.string.pb_title_detail_delete_complete),
																	getString(R.string.pb_msg_detail_delete_complete),
																	getString(R.string.pb_btn_OK),
																	new DialogInterface.OnClickListener() {
																		public void onClick(
																				DialogInterface dialog,
																				int whichButton) {
																			System.out
																					.println("Atik go to delete here");
																			// ((Activity)mContext).finish();
																			mPhotos = PBDatabaseManager
																					.getInstance(
																							getApplicationContext())
																					.getPhotos(
																							mCollectionId);
																			// mAdapter.resetAdapter(mPhotos,
																			// null);

																			// After
																			// successfull
																			// deletion
																			// need
																			// to
																			// clear
																			// the
																			// button
																			// flag
																			deleteInboxItem(isInBoxDeleteMode);
																			if (mDeleteSelection != null) {
																				mDeleteSelection
																						.clear();
																			}

																			mDeleteSelection = new ArrayList<String>();
																			if (mAdapter != null) {
																				mAdapter.setCheckBoxStatus(mIsDeleteMode);
																				mAdapter.resetAdapter(
																						mPhotos,
																						mDeleteSelection);
																			}
																			// mAdapter.notifyDataSetChanged();
																		}
																	});
												}

											}
										});

					} else {
						Toast.makeText(mContext,
								R.string.pb_toast_detail_no_selection,
								Toast.LENGTH_SHORT).show();
					}

				}
			} else {
				if (mIsDeleteMode) {
					if (mDeleteSelection.isEmpty()) {
						Toast.makeText(mContext,
								R.string.pb_toast_detail_no_selection,
								Toast.LENGTH_SHORT).show();
					} else if (mDeleteSelection.size() == mPhotos.size()) {
						showDeleteAllConfirm();
					} else {
						PBGeneralUtils
								.showAlertDialogAction(
										mContext,
										mContext.getString(R.string.pb_title_detail_delete_confirm),
										String.format(
												mContext.getString(R.string.pb_msg_detail_delete_confirm),
												mDeleteSelection.size()),
										mContext.getString(R.string.dialog_ok_btn),
										mContext.getString(R.string.dialog_cancel_btn),
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												deleteSelectedPhotos();
											}
										});
					}
				}
			}
		}
	}

	private class OnClickListenerTopLeft implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			if (mIsInInbox) {
				if (!isInBoxDeleteMode) {
					doAddPhoto();

				} else {

					showDeleteAllConfirm();

				}

			} else {
				if (mIsDeleteMode) {
					showDeleteAllConfirm();
				}
			}
		}
	}

	private void doSavePhoto() {
		if (mSaveMark != -1) {
			mLayoutInfoDownload.setVisibility(View.VISIBLE);
			mBtnButtom.setVisibility(View.GONE);
			mInfoDownload.setText("0 of " + mPhotos.size());

			// @lent5 add to avoid overflow asyncTask
			// new ProgressSavePhoto().execute();
			if (mProgressSavePhoto == null
					|| (mProgressSavePhoto != null && mProgressSavePhoto
							.getStatus() != AsyncTask.Status.RUNNING)) {
				mProgressSavePhoto = new ProgressSavePhoto();
				try {
					mProgressSavePhoto.execute();
				} catch (RejectedExecutionException e) {
					e.printStackTrace();
				}
			}
		} else {
			Toast.makeText(mContext,
					mContext.getString(R.string.pb_msg_detail_cannot_save),
					Toast.LENGTH_SHORT).show();
		}
	}

	private void doAddPhoto() {
		if (mAddibility == 1) {
			// PBAPIHelper.startAdding(mToken, mCollectionId);
			Intent intent;
			if (mIsInInbox) {
				intent = new Intent(mContext, PBUploadWarningActivity.class);
			} else {
				intent = new Intent(mContext, SelectMultipleImageActivity.class);
			}
			PBConstant.doUpdate = true;
			intent.putExtra(PBConstant.IS_OWNER, !mIsInInbox);
			intent.putExtra(PBConstant.COLLECTION_ID, mCollectionId);
			intent.putExtra(PBConstant.INTENT_PASSWORD, mPassword);
			startActivity(intent);
		} else {
			Toast.makeText(mContext,
					mContext.getString(R.string.pb_msg_detail_cannot_add),
					Toast.LENGTH_SHORT).show();
		}
	}

	private void openConfirmScreen() {
		Bundle extras = new Bundle();
		extras.putString(PBConstant.COLLECTION_PAGE_NAME,
				PBHistoryInboxDetailActivity.class.getName());
		extras.putString(PBConstant.COLLECTION_ID, mCollectionId);
		extras.putString(PBConstant.COLLECTION_PASSWORD, mPassword);
		extras.putLong(PBConstant.COLLECTION_CHARGE_AT, mChargeDate);
		extras.putString(PBConstant.COLLECTION_THUMB, mCollectionThumb);
		Intent intent = new Intent(PBHistoryInboxDetailActivity.this,
				PBConfirmPasswordActivity.class);
		intent.putExtra("data", extras);
		intent.putExtra(PBConstant.IS_OWNER, !mIsInInbox);
		startActivityForResult(intent, PBConstant.REQUEST_CODE_OPEN_CONFIRMPASS);
	}

	/**
	 * Download photo if file is not exist in sdcard
	 */
	private void downloadPhoto() {
		// Check sdcard exist or not
		boolean sdcardMounted = PBGeneralUtils.checkSdcard(mContext, true,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialoginterface, int i) {
						finish();
					}
				});
		if (!sdcardMounted)
			return;

		for (int i = 0; i < mPhotos.size(); i++) {
			PBHistoryPhotoModel model = mPhotos.get(i);
			String pathThum = PBGeneralUtils.getPathFromCacheFolder(model
					.getThumb());
			String pathPhoto = PBGeneralUtils.getPathFromCacheFolder(model
					.getPhoto());
			if (!PBGeneralUtils.checkExistFile(pathPhoto)) {
				try {
					// Get photo from server to save local
					boolean isSavePhotoSuccess = PBAPIHelper
							.savePhoto(mToken, model.getPhoto(), mPassword,
									(mSaveMark != 0), null);

					createThumpFile(model.getPhoto(), model.getThumb(), true);
					if ((isSavePhotoSuccess)
							&& (!PBGeneralUtils.checkExistFile(pathThum))) {
						// Get thumb from server to save local if thump not
						// found and create failed
						PBAPIHelper.savePhoto(mToken, model.getPhoto(),
								mPassword, (mSaveMark != 0), null);
					}
					mIsUpdateThump = true;
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	private void createThumpFile(String urlPhoto, String urlThump,
			boolean isCentered) {
		createThumpFile(urlPhoto, urlThump, PBConstant.PHOTO_THUMB_WIDTH,
				PBConstant.PHOTO_THUMB_HEIGHT, isCentered);
	}

	private void createThumpFile(String urlPhoto, String urlThump, int width,
			int height, boolean isCentered) {
		Log.i("mapp", ">>> process crop real image to create thumb!");
		String realImgPath = PBGeneralUtils.getPathFromCacheFolder(urlPhoto);
		String thumbImgPath = PBGeneralUtils.getPathFromCacheFolder(urlThump);
		Bitmap bmp = null;
		try {
			Options mOptions = new Options();

			mOptions.inSampleSize = PBBitmapUtils.sampleSizeNeeded(realImgPath,
					PBConstant.MAXIMUM_IMAGE_SIZE_UPLOAD);
			if (isCentered) {
				bmp = PBBitmapUtils.centerCropImage(
						BitmapFactory.decodeFile(realImgPath, mOptions), width,
						height);
			} else {

				bmp = PBBitmapUtils.matrixResize(
						BitmapFactory.decodeFile(realImgPath, mOptions), width,
						height);
				Log.i("AGUNG", bmp.getWidth() + " " + bmp.getHeight() + " "
						+ mOptions.inSampleSize);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} catch (OutOfMemoryError oom) {
			Log.e("PBHistoryInboxDetailActivity",
					">>> Create thumb file, OOM when decode image "
							+ realImgPath);
		}
		FileOutputStream fos = null;
		try {
			if (android.os.Environment.getExternalStorageState().equals(
					android.os.Environment.MEDIA_MOUNTED)) {
				fos = new FileOutputStream(new File(thumbImgPath));
			} else {
				// save on internal memory if sdcard is invalid.
				fos = PBApplication.getBaseApplicationContext().openFileOutput(
						String.valueOf(urlThump.hashCode()), 0);
			}
			if (bmp != null) {
				bmp.compress(PBConstant.COMPRESS_FORMAT,
						PBConstant.DECODE_COMPRESS_PRECENT, fos);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fos.flush();
				fos.close();
				fos = null;

				bmp.recycle();
				bmp = null;
			} catch (Exception e) {
			}
		}
	}

	private class ProgressSavePhoto extends AsyncTask<Void, Void, Void> {
		private int mProgress;
		private int mIndexPhoto;

		@Override
		protected void onPostExecute(Void result) {
			if (mLayoutInfoDownload != null) {
				mLayoutInfoDownload.setVisibility(View.GONE);
			}
			if (mBtnButtom != null) {
				mBtnButtom.setVisibility(View.VISIBLE);
			}
			if (mCustomWaitingLayout != null) {
				mCustomWaitingLayout.hideWaitingLayout();
			}
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			mProgress = 0;
			if (mProgressDownload != null) {
				mProgressDownload.setProgress(mProgress);
			}
			if (mCustomWaitingLayout != null) {
				mCustomWaitingLayout.showWaitingLayout();
			}
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			// Check sdcard exist or not
			boolean sdcardMounted = PBGeneralUtils.checkSdcard(mContext, true,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialoginterface,
								int i) {
							finish();
						}
					});
			if (!sdcardMounted)
				return null;

			for (int i = 0; i < mPhotos.size(); i++) {
				mIndexPhoto = i + 1;
				PBHistoryPhotoModel photo = mPhotos.get(i);
				String srcPath = PBGeneralUtils.getPathFromCacheFolder(photo
						.getPhoto());
				// Bitmap bm = null;
				if (!PBGeneralUtils.checkExistFile(srcPath)) {
					try {
						// Get photo from server to save local
						PBAPIHelper.savePhoto(mToken, photo.getPhoto(),
								mPassword, (mSaveMark != 0), null);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}

				try {
					if (!TextUtils.isEmpty(srcPath)) {
						PBGeneralUtils.saveInCameraRoll(mContext, srcPath,
								photo.getMediaType(), photo.getVideoDuration());
						// 20120313 mod by NhatVT, save image directly <E>
					}
					mProgress++;
					if (mProgressDownload != null) {
						mProgressDownload.setProgress(mProgress);
					}

					if (mContext != null) {
						((Activity) mContext).runOnUiThread(new Runnable() {
							@Override
							public void run() {
								mInfoDownload.setText(mIndexPhoto + " of "
										+ mPhotos.size());
							}
						});
					}
					Thread.sleep(TIME_SLEEP);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return null;
		}

	}

	private Handler mHistoryHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case PB_HANDLER_DELETE_HISTORY_SUCCESS:
				// lent5 added because should delete on asynctask
				// in case user press back in delete period
				// this function could not reach
				if (mIsInInbox) {
					PBDatabaseManager.getInstance(mContext).deleteHistory(
							String.valueOf(mHistoryId), mCollectionId);
				}
				PBPreferenceUtils.saveBoolPref(
						PBHistoryInboxDetailActivity.this,
						PBConstant.PREF_NAME, PBConstant.ISDOWNLOAD, true);
				finish();
				break;
			case PB_HANDLER_DELETE_HISTORY_FAIL:
				PBGeneralUtils
						.showAlertDialogAction(
								mContext,
								mContext.getString(R.string.dialog_network_error_title),
								mContext.getString(R.string.dialog_network_error_body),
								null, mContext
										.getString(R.string.dialog_ok_btn),
								null);
				break;
			case ResponseHandle.CODE_200_OK:
				break;
			case ResponseHandle.CODE_HTTP_FAIL:
			case ResponseHandle.CODE_INVALID_PARAMS:
			case ResponseHandle.CODE_403:
			case ResponseHandle.CODE_404:
			case ResponseHandle.CODE_400:
				break;
			default:
				break;
			}
		};
	};

	private void deleteSelectedPhotos() {
		final ProgressDialog progressDelete = new ProgressDialog(mContext);
		if (mContext != null) {
			progressDelete.setMessage(mContext
					.getString(R.string.pb_connecting));
			progressDelete.show();
		}

		new Thread() {
			@Override
			public void run() {
				try {
					Response response = PBAPIHelper.deletePartsCollection(
							mCollectionId, mToken, mDeleteSelection);

					if (response.errorCode == HttpStatus.SC_OK
							|| response.errorCode == HttpStatus.SC_NOT_FOUND) { // Bug
																				// fixes
																				// ,
																				// when
																				// data
																				// deleted
																				// from
																				// server
																				// below
																				// block
																				// will
																				// be
						// executed even if response code is 404
						PBDatabaseManager.getInstance(mContext).deletePhotos(
								mCollectionId, mDeleteSelection);

						mHistoryHandler.post(new Runnable() {
							@Override
							public void run() {
								if (mContext != null && progressDelete != null) {
									progressDelete.cancel();
								}

								PBGeneralUtils
										.showAlertDialogActionWithOnClick(
												mContext,
												android.R.drawable.ic_dialog_alert,
												getString(R.string.pb_title_detail_delete_complete),
												getString(R.string.pb_msg_detail_delete_complete),
												getString(R.string.pb_btn_OK),
												new DialogInterface.OnClickListener() {
													public void onClick(
															DialogInterface dialog,
															int whichButton) {
														System.out
																.println("Atik Inbox selected item deleted");
														mPhotos = PBDatabaseManager
																.getInstance(
																		getApplicationContext())
																.getPhotos(
																		mCollectionId);
														// mAdapter.resetAdapter(mPhotos,
														// null);

														// ((Activity)mContext).finish();

														// After successfull
														// deletion need to
														// clear the button flag

														setOutboxUI(!mIsDeleteMode);
														if (mDeleteSelection != null) {
															mDeleteSelection
																	.clear();
														}
														mDeleteSelection = new ArrayList<String>();
														if (mAdapter != null) {
															mAdapter.setCheckBoxStatus(mIsDeleteMode);
															mAdapter.resetAdapter(
																	mPhotos,
																	mDeleteSelection);
														}
													}
												});
							}
						});

						return;
					}
					if (response.errorCode == HttpStatus.SC_NOT_FOUND) {
						mHistoryHandler.post(new Runnable() {
							@Override
							public void run() {
								if (mContext != null && progressDelete != null) {
									progressDelete.cancel();
								}

								PBGeneralUtils.showAlertDialogActionWithOkButton(
										mContext,
										mContext.getString(R.string.pb_title_detail_delete_fail),
										mContext.getString(R.string.pb_msg_detail_delete_impossible),
										mContext.getString(R.string.pb_btn_OK));
							}
						});

						return;
					}
				} catch (Exception e) {
				}

				mHistoryHandler.post(new Runnable() {
					@Override
					public void run() {
						if (mContext != null && progressDelete != null) {
							progressDelete.cancel();
						}

						PBGeneralUtils.showAlertDialogActionWithOkButton(
								mContext,
								mContext.getString(R.string.pb_title_detail_delete_fail),
								mContext.getString(R.string.pb_msg_detail_delete_fail),
								mContext.getString(R.string.pb_btn_OK));
					}
				});
			}
		}.start();
	}

	/**
	 * Delete collection id in history send
	 */
	public class DeleteCollectionHistory extends AsyncTask<Void, Void, Void> {

		private String mCollectionId;
		private Context mContext;
		private Response mResponse;
		private ProgressDialog mProgressDelete;

		public DeleteCollectionHistory(Context context, String collectionId) {
			mContext = context;
			mCollectionId = collectionId;
		}

		@Override
		protected Void doInBackground(Void... params) {
			String token = PBPreferenceUtils.getStringPref(mContext,
					PBConstant.PREF_NAME, PBConstant.PREF_NAME_TOKEN, "");
			mResponse = PBAPIHelper.deleteUploadedCollection(mCollectionId,
					token);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			System.out.println("Come here for result code"
					+ mResponse.errorCode);
			if (mResponse.errorCode == HttpStatus.SC_OK) {
				// add to avoid user press back in delete period
				PBDatabaseManager.getInstance(mContext).deleteHistory(
						String.valueOf(mHistoryId), mCollectionId);
				if (mHistoryHandler != null) {
					mHistoryHandler
							.sendEmptyMessage(PB_HANDLER_DELETE_HISTORY_SUCCESS);
				}
			} else if (mResponse.errorCode == HttpStatus.SC_NOT_FOUND) { // Bug
																			// fixes
																			// ,
																			// when
																			// data
																			// deleted
																			// from
																			// server
																			// below
																			// block
																			// will
																			// be
																			// executed
																			// even
																			// if
																			// response
																			// code
																			// is
																			// 404
				// System.out.println("Come here for result code"+mResponse.errorCode+"L"+HttpStatus.SC_NOT_FOUND);
				System.out.println("Atik response code photo delete:"
						+ mResponse.errorCode);
				// add to avoid user press back in delete period
				PBDatabaseManager.getInstance(mContext).deleteHistory(
						String.valueOf(mHistoryId), mCollectionId);
				if (mHistoryHandler != null) {
					mHistoryHandler
							.sendEmptyMessage(PB_HANDLER_DELETE_HISTORY_SUCCESS);
				}
			} else {
				if (mHistoryHandler != null) {
					mHistoryHandler
							.sendEmptyMessage(PB_HANDLER_DELETE_HISTORY_FAIL);
				}
			}

			if (mProgressDelete != null && mProgressDelete.isShowing()) {
				mProgressDelete.dismiss();
			}

			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			if (mContext != null) {
				mProgressDelete = new ProgressDialog(mContext);
				mProgressDelete.setMessage(mContext
						.getString(R.string.pb_connecting));
				mProgressDelete.show();
			}
			super.onPreExecute();
		}
	}

	@Override
	protected void handleHomeActionListener() {
		finish();
	}

	@Override
	protected void setLeftBtnsDetailHeader(ActionBar headerBar) {

	}

	public class CheckDownloadPhoto extends AsyncTask<Void, Void, Void> {
		// private ProgressDialog mProgressDialog;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			downloadPhoto();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// mProgressDialog.dismiss();
			if (mIsUpdateThump) {
				mAdapter.notifyDataSetChanged();
				mThumbGrid.requestFocusFromTouch();
				mThumbGrid.setSelection(0);
			}
			super.onPostExecute(result);
		}
	}

	private class PBTaskGetUnreadMessage extends
			AsyncTask<Void, Void, Response> {

		@Override
		protected Response doInBackground(Void... params) {
			String token = PBPreferenceUtils.getStringPref(
					PBApplication.getBaseApplicationContext(),
					PBConstant.PREF_NAME, PBConstant.PREF_NAME_TOKEN, "");
			String deviceUUID = PBPreferenceUtils.getStringPref(
					PBApplication.getBaseApplicationContext(),
					PBConstant.PREF_NAME, PBConstant.PREF_NAME_UID, "0");

			ChatDatabaseHandler db = new ChatDatabaseHandler(
					PBHistoryInboxDetailActivity.this);

			int last_message_count = db.getChatRowCount(mCollectionId);

			if (!TextUtils.isEmpty(token)) {
				Response response = PBAPIHelper.getUnreadMessageCount(token,
						deviceUUID,mPassword, last_message_count);
				return response;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Response result) {
			super.onPostExecute(result);

			Log.d("response", result.decription);
			numberOfUnreadMGS.setVisibility(View.GONE);
			if (result != null) {
				if (result.errorCode == ResponseHandle.CODE_200_OK) {

					try {
						JSONObject obj = new JSONObject(result.decription);
						String message = obj.getString("message");
						numberOfUnreadMGS.setVisibility(View.VISIBLE);
						LayoutParams params = numberOfUnreadMGS.getLayoutParams();
						if(message.length() == 1){
							
							params.height = getResources().getDimensionPixelSize(R.dimen.text_view_notification_size_for_onedigit);
							params.width = getResources().getDimensionPixelSize(R.dimen.text_view_notification_size_for_onedigit);
						}else if(message.length() == 2){
							
							params.height = getResources().getDimensionPixelSize(R.dimen.text_view_notification_size_for_twodigit);
							params.width = getResources().getDimensionPixelSize(R.dimen.text_view_notification_size_for_twodigit);
						}else if(message.length() == 3){
							
							params.height = getResources().getDimensionPixelSize(R.dimen.text_view_notification_size_for_threedigit);
							params.width = getResources().getDimensionPixelSize(R.dimen.text_view_notification_size_for_threedigit);
						}else{
							
							message = message.substring(0, 3);
							params.height = getResources().getDimensionPixelSize(R.dimen.text_view_notification_size_for_threedigit);
							params.width = getResources().getDimensionPixelSize(R.dimen.text_view_notification_size_for_threedigit);
						}
						
						numberOfUnreadMGS.setText(message);
						numberOfUnreadMGS.setLayoutParams(params);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else if (result.errorCode == ResponseHandle.CODE_403) {

					numberOfUnreadMGS.setVisibility(View.GONE);
				}

				else {
					try {
						Log.e("AGUNG", "Error :" + result.errorCode + " -> "
								+ result.decription);
					} catch (Exception e) {// TODO
						e.printStackTrace();
						Log.e("AGUNG", "Error :" + result.errorCode + " -> "
								+ result.decription);
					}
				}
			}

		}

	}

}
