package com.aircast.photobag.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aircast.photobag.R;
import com.aircast.photobag.activity.PBMainTabBarActivity;
import com.aircast.photobag.activity.PBMyPageAcornExchangeListActivity;
import com.aircast.photobag.activity.PBMyPageHoneyNotification;
import com.aircast.photobag.activity.PBPurchaseActivity;
import com.aircast.photobag.activity.PBSettingMainActivity;
import com.aircast.photobag.activity.PBWebViewActivity;
import com.aircast.photobag.activity.PBMainTabBarActivity.TimelineNotificationListener;
import com.aircast.photobag.api.PBAPIContant;
import com.aircast.photobag.api.PBTaskGetFreePeriod;
import com.aircast.photobag.api.ResponseHandle;
import com.aircast.photobag.application.PBApplication;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.application.PBNetwork;
import com.aircast.photobag.database.PBDatabaseManager;
import com.aircast.photobag.model.PBTimelineHistoryModel;
import com.aircast.photobag.model.PBTimelineHistoryModel.Type;
import com.aircast.photobag.utils.PBGeneralUtils;
import com.aircast.photobag.utils.PBPreferenceUtils;
import com.aircast.photobag.widget.FButton;

/**
 * Show user's profile page. 
 * */
public class PBMyPageMainFragment extends Fragment implements OnClickListener,
		TimelineNotificationListener {
	private View notificationAreaView;
	private View newNotifArea;
	private FButton honeyExchangeButton;
	private TextView acronsCounterTextView, totalHoneyCounterTextView,
			otameshiHoneyCounterTextView, goldCounterTextView;
	private int mapleCounter, otameshiHoneyCounter, acronsCounter, goldCounter;
	
	private TextView notificationTextView;
	private PBTimelineHistoryModel newestTimelineData;
	private LinearLayout mMyPageAD;
	private View view;
	
	
	
	public static PBMyPageMainFragment createInstance(){
        return new PBMyPageMainFragment();
    }

	
	 @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	      
	        return inflater.inflate(R.layout.pb_layout_my_page, container, false);
	    }

	    @Override
	    public void onViewCreated(View view, Bundle savedInstanceState) {
	        super.onViewCreated(view, savedInstanceState);

	        // and here our view is loaded

		
			
			
			
			//*****************************使い方の実装*******************************************//
			// 初回表示だったらチュートリアル表示
			
			/*boolean appFirstBootMyPage = PBPreferenceUtils.getBoolPref(
	                getApplicationContext(), 
	                PBConstant.PREF_NAME, 
	                PBConstant.APP_FIRST_BOOT_MY_PAGE, 
	                false);
			if(!appFirstBootMyPage) {
				
				ArrayList<Integer> imageId = new ArrayList<Integer>();
				imageId.add(R.drawable.tut_itembox_1);
				imageId.add(R.drawable.tut_itembox_2);
				
				DialogTutorial dialog = new DialogTutorial(PBMyPageMainFragment.this, imageId, false);
				dialog.show();
				
				// チュートリアル表示フラグを立てる
				PBPreferenceUtils.saveBoolPref(getApplicationContext(),
						PBConstant.PREF_NAME, PBConstant.APP_FIRST_BOOT_MY_PAGE,
						true);
			}*/
			
			//*****************************使い方の実装終わり*******************************************//
			
	        this.view=view;
			mMyPageAD = (LinearLayout) view.findViewById(R.id.layout_mypage_ad);
			
			honeyExchangeButton = (FButton) view.findViewById(R.id.buttonGoToHoneyExchange);
			notificationAreaView = view.findViewById(R.id.my_page_notification_layout);
			notificationTextView = (TextView) view.findViewById(R.id.my_page_notification_text);
			acronsCounterTextView = (TextView) view.findViewById(R.id.text_acorn_count_my);
			newNotifArea = view.findViewById(R.id.left_side);
			totalHoneyCounterTextView = (TextView) view.findViewById(R.id.text_honey_count_my);
			otameshiHoneyCounterTextView = (TextView) view.findViewById(R.id.text_otameshi_count_my);
			goldCounterTextView = (TextView) view.findViewById(R.id.text_gold_count_my);
			// honeyOldCounter = (TextView)
			newNotifArea.setVisibility(View.INVISIBLE);
			notificationAreaView.setVisibility(View.GONE);

			updateDataFromPreferenceAndDB();

			honeyExchangeButton.setOnClickListener(this);
			notificationAreaView.setOnClickListener(this);

			view.findViewById(R.id.btn_my_page_setting).setOnClickListener(this);
			view.findViewById(R.id.text_get_honey_my).setOnClickListener(this);
			view.findViewById(R.id.text_get_gold_acorn_my).setOnClickListener(this);
			view.findViewById(R.id.text_get_acorn_my).setOnClickListener(this);
			view.findViewById(R.id.img_hint_get_honey_my).setOnClickListener(this);
			view.findViewById(R.id.img_hint_get_gold_acorn_my).setOnClickListener(this);
			view.findViewById(R.id.img_hint_get_acorn_my).setOnClickListener(this);
			view.findViewById(R.id.buttonGoToHoneyShop).setOnClickListener(this);
	        
	        boolean isJapan = new PBNetwork().isJapan((TelephonyManager)getActivity().getSystemService(Context.TELEPHONY_SERVICE));
	        if(!isJapan){
	        	RelativeLayout layoutGolderAcorn = (RelativeLayout)view.findViewById(R.id.sub_layout_golden_acorn);
	        	layoutGolderAcorn.setVisibility(View.GONE);
	        }
		
	        boolean hasInternet = PBApplication.hasNetworkConnection();
	        mMyPageAD.setVisibility(hasInternet ? View.VISIBLE : View.GONE);
	        
	        //////////////PBTabBar
	        
			if (PBMainTabBarActivity.sMainContext != null) {
				PBMainTabBarActivity.sMainContext.registerNotificationListener(this);
				PBMainTabBarActivity.sMainContext.requestCheckTimelineNotification();
			}

			PBTaskGetFreePeriod meInfoFetchTask = new PBTaskGetFreePeriod();
			meInfoFetchTask.setHandler(fetchMeInfoHandler);
			meInfoFetchTask.execute();
			
			updateDataFromPreferenceAndDB();
			updateUI();
			String uid = PBPreferenceUtils.getStringPref(
					PBApplication.getBaseApplicationContext(),
					PBConstant.PREF_NAME, PBConstant.PREF_NAME_UID, "");
			Log.d("AGUNG", "uid : " + uid);
	    }


	private void updateDataFromPreferenceAndDB() {
		acronsCounter = PBPreferenceUtils.getIntPref(
				getActivity().getApplicationContext(), PBConstant.PREF_NAME,
				PBConstant.PREF_DONGURI_COUNT, 0);
		mapleCounter = PBPreferenceUtils.getIntPref(
				getActivity().getApplicationContext(), PBConstant.PREF_NAME,
				PBConstant.PREF_MAPLE_COUNT, 0);
		otameshiHoneyCounter = PBPreferenceUtils.getIntPref(
				getActivity().getApplicationContext(), PBConstant.PREF_NAME,
				PBConstant.PREF_HONEY_BONUS, 0);
		goldCounter = PBPreferenceUtils.getIntPref(
				getActivity().getApplicationContext(), PBConstant.PREF_NAME,
				PBConstant.PREF_GOLD_COUNT, 0);

		if (mHandler != null) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					newestTimelineData = PBDatabaseManager.getInstance(
							getActivity().getApplicationContext()).getNewestTimelineHistory();
					mHandler.post(new Runnable() {

						@Override
						public void run() {
							updateUI();
						}
					});
				}
			}).start();
		}
	}

	private void updateUI() {
		
		// DEBUG Code
		/*
		String str1 = getString(R.string.pb_my_page_text_count);
		int i1 = mapleCounter;
		int i2 = otameshiHoneyCounter;
		int i3 = acronsCounter;
		int i4 = goldCounter;
		*/
		
		totalHoneyCounterTextView.setText(mapleCounter + getString(R.string.pb_my_page_text_count));
		otameshiHoneyCounterTextView.setText(otameshiHoneyCounter + getString(R.string.pb_my_page_text_count));
		acronsCounterTextView.setText(acronsCounter + getString(R.string.pb_my_page_text_count));
		goldCounterTextView.setText(goldCounter + getString(R.string.pb_my_page_text_count));

		if (newestTimelineData != null) {
			if (newestTimelineData.getType() == Type.ACORN) {
				notificationTextView.setText(R.string.pb_my_page_acorn_get);
			} else if (newestTimelineData.getType() == Type.HONEY) {
				notificationTextView.setText(R.string.pb_my_page_sp_honey_get);
			} else if (newestTimelineData.getType() == Type.MAPLE) {
				notificationTextView.setText(R.string.pb_my_page_honey_get);
			} else if (newestTimelineData.getType() == Type.GOLDACORN) {
				notificationTextView.setText(R.string.pb_my_page_gold_get);
			}
			
			if (this.notificationAreaView.getVisibility() != View.VISIBLE) {
				Animation anim = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),
						R.anim.scale_top_to_bottom);
				anim.setFillAfter(true);
				anim.setInterpolator(new LinearInterpolator());
				anim.setDuration(300);
				anim.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationStart(Animation animation) {

					}

					@Override
					public void onAnimationRepeat(Animation animation) {

					}

					@Override
					public void onAnimationEnd(Animation animation) {
						notificationAreaView.setVisibility(View.VISIBLE);
					}
				});

				this.notificationAreaView.startAnimation(anim);
			}
			
			this.notificationAreaView.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);

		if (!hidden) {

		

	        boolean hasInternet = PBApplication.hasNetworkConnection();
	        mMyPageAD.setVisibility(hasInternet ? View.VISIBLE : View.GONE);
	        
	        //////////////PBTabBar
	        
			if (PBMainTabBarActivity.sMainContext != null) {
				PBMainTabBarActivity.sMainContext.registerNotificationListener(this);
				PBMainTabBarActivity.sMainContext.requestCheckTimelineNotification();
			}

			PBTaskGetFreePeriod meInfoFetchTask = new PBTaskGetFreePeriod();
			meInfoFetchTask.setHandler(fetchMeInfoHandler);
			meInfoFetchTask.execute();
			
			updateDataFromPreferenceAndDB();
			updateUI();
			String uid = PBPreferenceUtils.getStringPref(
					PBApplication.getBaseApplicationContext(),
					PBConstant.PREF_NAME, PBConstant.PREF_NAME_UID, "");
			Log.d("AGUNG", "uid : " + uid);
		
			
		}

	}
	@Override
	public void onResume() {
		super.onResume();


	}

	@Override
	public void onDestroy() {
//		if (PBTabBarActivity.sMainContext != null)
//			PBTabBarActivity.sMainContext.unregisterNotificationListener(this);
		super.onDestroy();

// 		try {
// 			AdstirTerminate.init(this);
// 		} catch (Exception e) {}
	}

	@Override
	public void onPause() {
//		if (PBTabBarActivity.sMainContext != null) {
//			PBTabBarActivity.sMainContext.unregisterNotificationListener(this);
//		}
		super.onPause();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buttonGoToHoneyExchange:
			// PBNotificationTabUtil.hideNotificationAt(3);
			
			// Atik device lock status check
			//PBTabBar
			if (!PBMainTabBarActivity.isDeviceLock) {
				Intent honeyExchangeAct = new Intent(getActivity(),PBMyPageAcornExchangeListActivity.class);
				startActivity(honeyExchangeAct);
			} else { // Atik show the device lock status message
				 updateUISuccessfull(PBMainTabBarActivity.message_response_check_lock_status,PBMainTabBarActivity.titte_after_start_migration);
			 }
			break;
		case R.id.my_page_notification_layout:
			Intent notificationIntent = new Intent(getActivity(),
					PBMyPageHoneyNotification.class);
			startActivity(notificationIntent);
			newNotifArea.setVisibility(View.INVISIBLE);
			// notificationAreaView.setVisibility(View.GONE);
			
			//PBTabBar
			PBMainTabBarActivity.sMainContext.hideNotificationTab(3);
			break;
		case R.id.img_hint_get_honey_my:
			startWebViewActivity(getString(R.string.pb_faq),
					getString(R.string.pb_setting_url_what_is_honey));
			break;
		case R.id.img_hint_get_gold_acorn_my:
			startWebViewActivity(getString(R.string.pb_faq),
					getString(R.string.pb_setting_url_what_is_gold));
			break;
		case R.id.img_hint_get_acorn_my:
			startWebViewActivity(getString(R.string.pb_faq),
					getString(R.string.pb_setting_url_what_is_acorn));
			break;
		case R.id.text_get_honey_my:
		case R.id.text_get_gold_acorn_my:
		case R.id.text_get_acorn_my:		
        	boolean hasInternet = PBApplication.hasNetworkConnection();
        	if (hasInternet) {
    			PBGeneralUtils.openAcornWebview(getActivity());
    			break;
        	} else {
           	 	/*Toast.makeText(PBMyPageMainFragment.this, 
           			 getString(R.string.pb_network_not_available_general_message), 1000).show();*/
				   Toast toast = Toast.makeText(getActivity().getApplicationContext(), getString(R.string.pb_network_not_available_general_message), 
							1000);
				   TextView v1 = (TextView) toast.getView().findViewById(android.R.id.message);
				   if( v1 != null) v1.setGravity(Gravity.CENTER);
				   toast.show();
            	break;        		
            }
		case R.id.btn_my_page_setting:
			Intent settingIntent = new Intent(getActivity(),
					PBSettingMainActivity.class);
			startActivity(settingIntent);
			break;
			
		case R.id.buttonGoToHoneyShop:
			//////PBTabBar
			// Atik device lock status check
			if (!PBMainTabBarActivity.isDeviceLock) {
		     	boolean hasInternetBeforeGoingToMatomegai = PBApplication.hasNetworkConnection();
	        	if (hasInternetBeforeGoingToMatomegai) {
	    			Intent honeyIntent = new Intent(getActivity(),
	    					PBPurchaseActivity.class);
	    			startActivity(honeyIntent);
	    			break;
	        	} else {
	        		
					    AlertDialog.Builder	 exitDialog =  new AlertDialog.Builder(new ContextThemeWrapper(getActivity(),
						     R.style.popup_theme));
						exitDialog .setMessage(getString(R.string.pb_network_not_available_general_message));
						exitDialog .setCancelable(false);
						exitDialog .setPositiveButton(getString(R.string.dialog_ok_btn),
					       new DialogInterface.OnClickListener() {
					        @Override
					        public void onClick(DialogInterface dialog,
					          int which) {
					        	
					        	
					        	dialog.dismiss();
					        }
						});
						         
						exitDialog.show();
						break;        		
	             }
			 } else { // Atik show the device lock status message
				 updateUISuccessfull(PBMainTabBarActivity.message_response_check_lock_status,PBMainTabBarActivity.titte_after_start_migration);
			 }

			
		default:
			break;
		}
	}
	
	private DialogInterface.OnClickListener mOnClickOkDialogMigrationVerified = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
		}
	};
	
	// Update UI when received merge code successfully
	private void updateUISuccessfull(final String message, final String title) {

			final Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
			    @Override
			    public void run() {
			        // Delay dialog display after 1s = 1000ms
					/*PBGeneralUtils.showAlertDialogActionWithOnClick(PBMyPageMainFragment.this, 
							title, 
							message,
							getString(R.string.dialog_ok_btn),
							mOnClickOkDialogMigrationVerified);
					
			    }
			}, 1000);
			    	PBGeneralUtils.showAlertDialogActionWithOnClick(PBMyPageMainFragment.this, 
							"警告！！", 
							"別の端末へ移行済みです。",
							getString(R.string.dialog_ok_btn),
							mOnClickOkDialogMigrationVerified);*/
			    	
			    	PBGeneralUtils.showAlertDialogActionWithOnClick(getActivity(), 
							title, 
							message,
							getString(R.string.dialog_ok_btn),
							mOnClickOkDialogMigrationVerified);
			    	//Toast.makeText(PBMyPageMainFragment.this, "別の端末へ移行済みです。", Toast.LENGTH_LONG).show();
					
			    }
			}, 1000);
			

	}
	
	private void startWebViewActivity(String title, String url) {
		
        //show error dialog when no Internet available  	  
      	if (!PBApplication.hasNetworkConnection()) {
			AlertDialog.Builder	networkErorrDialog =  new AlertDialog.Builder(new ContextThemeWrapper(getActivity(),
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
          		
      	} else {
    		Intent intent = new Intent(getActivity(),
    				PBWebViewActivity.class);
    		intent.putExtra(PBAPIContant.PB_SETTING_EXTRA_URL, url);
    		intent.putExtra(PBAPIContant.PB_SETTING_EXTRA_TITLE, title);
    		startActivity(intent);
      	}
		

	}

	private Handler fetchMeInfoHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == PBConstant.MSG_UPDATE_UI) {
				Log.e("AGUNG_ME", "Success fetch /me info :" + msg.arg1);
				
				if (msg.arg1 == ResponseHandle.CODE_200_OK) {					
					updateDataFromPreferenceAndDB();
					updateUI();
				} 
			}
		};
	};
	
	@Override
	public void onReceiveNotification(int newHoney, int newMaple,
			int newDonguri, int newGold, String notification) {
		// String success = "new Honey :"+newHoney +", newMaple = "+newMaple +
		// ", newDonguri:"+newDonguri;
		if (newHoney + newMaple + newDonguri + newGold > 0) {
			notificationAreaView.setVisibility(View.VISIBLE);
			newNotifArea.setVisibility(View.VISIBLE);
			Animation anim = AnimationUtils.loadAnimation(getActivity(),
					R.anim.scale_0_to_1);
			anim.setDuration(1000);
			anim.setInterpolator(new BounceInterpolator());
			this.newNotifArea.startAnimation(anim);
			this.notificationTextView.setText(notification);
		} else if (mHandler != null) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					newestTimelineData = PBDatabaseManager.getInstance(
							getActivity().getApplicationContext())
							.getNewestTimelineHistory();
					mHandler.post(new Runnable() {

						@Override
						public void run() {
							updateUI();
						}
					});
				}
			}).start();
		}
	}

	private final Handler mHandler = new Handler();
	
 
//	 public void onBackPressed() {
//	  
//	  AlertDialog.Builder exitDialog =  new AlertDialog.Builder(new ContextThemeWrapper(getActivity(),
//	        R.style.popup_theme));
//	   exitDialog    .setTitle("");
//	   exitDialog .setMessage(getString(R.string.app_exit_message));
//	   exitDialog .setCancelable(false);
//	   exitDialog .setPositiveButton(getString(R.string.dialog_ok_btn),
//	          new DialogInterface.OnClickListener() {
//	           @Override
//	           public void onClick(DialogInterface dialog,
//	             int which) {
//	            
//	      getActivity().finish();
//	           }
//	     });
//	   exitDialog .setNegativeButton(getString(R.string.dialog_cancel_btn),
//	     new DialogInterface.OnClickListener() {
//	      public void onClick(DialogInterface dialog,
//	        int which) {
//
//	      }
//	     });
//	            
//	        exitDialog.show();
//	 }
}
