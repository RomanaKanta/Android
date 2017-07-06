package com.aircast.photobag.fragment;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aircast.koukaibukuro.MainTabActivity;
import com.aircast.photobag.R;
import com.aircast.photobag.activity.PBAcornForestActivity;
import com.aircast.photobag.activity.PBMainTabBarActivity;
import com.aircast.photobag.activity.PBWebViewPhotoContestActivity;
import com.aircast.photobag.activity.SelectMultipleImageActivity;
import com.aircast.photobag.activity.UploadSetPasswordActivity;
import com.aircast.photobag.api.PBAPIContant;
import com.aircast.photobag.api.PBAPIHelper;
import com.aircast.photobag.api.ResponseHandle;
import com.aircast.photobag.api.ResponseHandle.Response;
import com.aircast.photobag.application.PBApplication;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.database.PBDatabaseManager;
import com.aircast.photobag.model.PBTimelineHistoryModel;
import com.aircast.photobag.services.UploadService;
import com.aircast.photobag.utils.PBGeneralUtils;
import com.aircast.photobag.utils.PBPreferenceUtils;
import com.aircast.photobag.widget.FButton;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;

/**
 * Upload main , click upload to select photo.
 * */
public class PBUploadMainFragment extends Fragment {
    ArrayList<String> mAlbumList;
    private LinearLayout mUploadAD;
    private FButton mBtnDlInputUploadImg;
	protected Dialog dialog = null;
    private static String message_response_check_lock_status = "";
	private static String titte_after_start_migration = "";

	private static String resultCode = "";
    private boolean isDeviceLock = false;
    private boolean isFromMori = false;
    
	private WebView webview;
	private String webviewUploadScreenURL;
	private View view;
	
	private static final String TAG = PBUploadMainFragment.class.getName();
	
	public static PBUploadMainFragment createInstance(){
        return new PBUploadMainFragment();
    }

	
	 @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
	        return inflater.inflate(R.layout.pb_layout_upload_main, container, false);
	    }

	    @Override
	    public void onViewCreated(View view, Bundle savedInstanceState) {
	        super.onViewCreated(view, savedInstanceState);

	        // and here our view is loaded

	        super.onCreate(savedInstanceState);
	        this.view=view;
	        
	      
//	        Bundle bundle=getArguments(); 
//	        String tag=bundle.getString(PBConstant.UPLOAD_TAG);

	      
	        
	        PBPreferenceUtils.saveBoolPref(getActivity().getApplicationContext(), PBConstant.PREF_NAME, PBConstant.UPLOAD_TAG, false);
			
	       // Toast.makeText(getActivity().getApplicationContext(),tag, 1000).show();
	        mUploadAD = (LinearLayout) view.findViewById(R.id.layout_upload_ad);
	        
	        mAlbumList = new ArrayList<String>();
	        
	        mBtnDlInputUploadImg = (FButton) view.findViewById(R.id.btn_send_pictures);
	        Spannable buttonLabel = new SpannableString("  "+getString(R.string.upload_main_btn_text));
	        Drawable drawable = getResources().getDrawable(R.drawable.btn_ico_up);  
	        int intrinsicHeightWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
	                (float) 20, getResources().getDisplayMetrics()); // convert 20 dip  to int
	        drawable.setBounds(0, 0, intrinsicHeightWidth,intrinsicHeightWidth);
	        ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
	        buttonLabel.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	        mBtnDlInputUploadImg.setText(buttonLabel);
	        
	         mBtnDlInputUploadImg.setOnClickListener(
	                new OnClickListener() {
	                    @Override
	                    public void onClick(View v) {
	                        boolean sdCardMounted = PBGeneralUtils.checkSdcard(
	                               getActivity(), true,
	                                new DialogInterface.OnClickListener() {
	                                    @Override
	                                    public void onClick(
	                                            DialogInterface dialoginterface,
	                                            int i) {
	                                        // finish();
	                                    }
	                                });
	                        if (sdCardMounted) {
	                        	
	                        	String deviceUUID = PBPreferenceUtils
	                					.getStringPref(PBApplication
	                							.getBaseApplicationContext(),
	                							PBConstant.PREF_NAME,
	                							PBConstant.PREF_NAME_UID, "0");
	                            	
	                	              System.out.println("Atik Easy Tracker is called upon on upload button press");
	                	          	  EasyTracker easyTracker = EasyTracker.getInstance(getActivity());
	                	          	  easyTracker.set(Fields.SCREEN_NAME, ""+deviceUUID+":KBUploadMainFragment");
	                	          	  // MapBuilder.createEvent().build() returns a Map of event fields and values
	                	          	  // that are set and sent with the hit.
	                	          	  easyTracker.send(MapBuilder
	                	          	      .createEvent("ui_action",     // Event category (required)
	                	          	                   "button_press_for_upload_pictures",  // Event action (required)
	                	          	                    "button_sent_pictures",   // Event label
	                	          	                   null)            // Event value
	                	          	      .build()
	                	          	  );
	                	         
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
	                                PBTaskCheckDeviceLockStatus task = new PBTaskCheckDeviceLockStatus();
	                                task.execute();    // Need to set the URL from constants  
	            	          	}	
	                        }
	                    }
	                });
	       
	        view.findViewById(R.id.btn_dialog_video_compress_setting).setOnClickListener(
	                new OnClickListener() {
	                    @Override
	                    public void onClick(View v) {
	                    	// show the video compress dialog
	                		LayoutInflater inf = LayoutInflater.from( getActivity());
	                		View viewDialogVideoSetting = inf.inflate(R.layout.pb_dialog_video_setting_layout, null);
	                		dialog = new Dialog(getActivity());
	                		dialog.getWindow().setBackgroundDrawable(
	                				new ColorDrawable(android.graphics.Color.TRANSPARENT));
	                		
	                		dialog.setCanceledOnTouchOutside(true);
	                		
	                		boolean videoCompressHighSetting = PBPreferenceUtils.getBoolPref(
	                        		getActivity(), PBConstant.PREF_NAME, 
	                    			PBConstant.PREF_VIDEO_COMPRESS_HIGH_QUALITY, false);
	                		
	                		boolean videoCompressMediumSetting = PBPreferenceUtils.getBoolPref(
	                        		getActivity(), PBConstant.PREF_NAME, 
	                    			PBConstant.PREF_VIDEO_COMPRESS_MEDIUM_QUALITY, false);
	                		
	                		
	                		boolean videoCompressLowSetting = PBPreferenceUtils.getBoolPref(
	                        		getActivity(), PBConstant.PREF_NAME, 
	                    			PBConstant.PREF_VIDEO_COMPRESS_LOW_QUALITY, false);
	                		
	                		View highLayout = (LinearLayout)viewDialogVideoSetting.findViewById(R.id.layout_checkbox_video_compress_high);
	                		System.out.println("high layout:"+highLayout);
	                		ImageView imageHigh = (ImageView) highLayout.findViewById(R.id.icon_checkbox_video_compress_high);
	                		
	                		
	                		View mediumLayout = (LinearLayout)viewDialogVideoSetting.findViewById(R.id.layout_checkbox_video_compress_medium);		
	                		ImageView imageMedium = (ImageView) mediumLayout.findViewById(R.id.icon_checkbox_video_compress_medium);
	                		
	                		View LowLayout = (LinearLayout)viewDialogVideoSetting.findViewById(R.id.layout_checkbox_video_compress_low);
	                		ImageView imageLow = (ImageView) LowLayout.findViewById(R.id.icon_checkbox_video_compress_low);
	                		
	                		
	                    	// change background image
	                		Drawable checkBoxOn = getResources().getDrawable(R.drawable.checkbox_on);
	                		Drawable checkBoxOff = getResources().getDrawable(R.drawable.checkbox_off);

	                		
	                		if(videoCompressHighSetting) {
	                			imageHigh.setImageDrawable(checkBoxOn);
	                			imageMedium.setImageDrawable(checkBoxOff);
	                			imageLow.setImageDrawable(checkBoxOff);
	                		} else if(videoCompressMediumSetting) {
	                			imageMedium.setImageDrawable(checkBoxOn);
	                			imageHigh.setImageDrawable(checkBoxOff);
	                			imageLow.setImageDrawable(checkBoxOff);
	                		} else if(videoCompressLowSetting){
	                			imageLow.setImageDrawable(checkBoxOn);
	                			imageHigh.setImageDrawable(checkBoxOff);
	                			imageMedium.setImageDrawable(checkBoxOff);
	                		}
	                		
	                		dialog.setContentView(viewDialogVideoSetting);
	                		dialog.show();
	                    }
	                });
	        
	        
	    	boolean hasInternet = PBApplication.hasNetworkConnection();
			if (hasInternet && !PBDownloadFragment.isKoukaibukuroDisabled) {

				System.out.println("Atik upload koukaibukuro status:"+PBDownloadFragment.isKoukaibukuroDisabled);
				/*if(!PBDownloadFragment.isKoukaibukuroDisabled) {*/
					webview = (WebView) view.findViewById(R.id.webview_upload_screen_for_kuma);
					try {
						String versionName = getActivity().getPackageManager().getPackageInfo(
								getActivity().getPackageName(), 0).versionName;
						
						webviewUploadScreenURL = PBAPIContant.PB_UPLOAD_WEBVIEW_URL + versionName ; 
						webview.loadUrl(webviewUploadScreenURL);
					} catch (NameNotFoundException e) {
						e.printStackTrace();
					}
					loadOpenPage(webview);
				/*} else {
					findViewById(R.id.webview_upload_screen_layout_for_kuma).setVisibility(View.GONE);
					findViewById(R.id.view_kuma_no_internet_upload).setVisibility(
							View.VISIBLE);
				}*/

			} else {
				System.out.println("Atik upload koukaibukuro status:"+PBDownloadFragment.isKoukaibukuroDisabled);
				view.findViewById(R.id.webview_upload_screen_layout_for_kuma).setVisibility(View.GONE);
				view.findViewById(R.id.view_kuma_no_internet_upload).setVisibility(
						View.VISIBLE);
			}
	        
	       

	        Intent intent = getActivity().getIntent();
	        if (intent.getStringExtra(PBConstant.ACTION) != null &&
	        		((intent.getStringExtra(PBConstant.ACTION)).equals(Intent.ACTION_SEND_MULTIPLE) ||
	        				(intent.getStringExtra(PBConstant.ACTION)).equals(Intent.ACTION_SEND))){
	        	handleSendMultipleImages(intent);
	        }
	        
	        mUploadAD.setVisibility(hasInternet ? View.VISIBLE : View.GONE);
	        
	        if (!hasInternet || PBDownloadFragment.isKoukaibukuroDisabled) {
	            
	            view.findViewById(R.id.webview_upload_screen_layout_for_kuma).setVisibility(View.GONE);
	      view.findViewById(R.id.view_kuma_no_internet_upload).setVisibility(
	        View.VISIBLE);
	         
	        } else {
		         webview = (WebView) view.findViewById(R.id.webview_upload_screen_for_kuma);
		         try {
			       String versionName = getActivity().getPackageManager().getPackageInfo(
			         getActivity().getPackageName(), 0).versionName;
			       
			       webviewUploadScreenURL = PBAPIContant.PB_UPLOAD_WEBVIEW_URL + versionName ; 
			       webview.loadUrl(webviewUploadScreenURL);
		         } catch (NameNotFoundException e) {
		        	 e.printStackTrace();
		         }
		         loadOpenPage(webview);
		         view.findViewById(R.id.webview_upload_screen_layout_for_kuma).setVisibility(View.VISIBLE);
		      view.findViewById(R.id.view_kuma_no_internet_upload).setVisibility(
		        View.GONE);
	         
	        }
	       
	    }
	
	
	
	
	
	


    @SuppressLint("SetJavaScriptEnabled")
	protected void loadOpenPage(final WebView wv) {

		wv.setBackgroundColor(0x00000000);
		wv.setVerticalScrollBarEnabled(false);
		wv.setHorizontalScrollBarEnabled(false);
		wv.getSettings().setUseWideViewPort(true);
		wv.getSettings().setJavaScriptEnabled(true);
		wv.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				String decodeUrl = decodeURIComponent(url);
				
				if (decodeUrl.equals("https://play.google.com/store/apps/details?id=com.kayac.photobag")){

					 Intent intentAndroidMarket = new Intent(Intent.ACTION_VIEW);
					 intentAndroidMarket.setData(Uri.parse("market://details?id=com.kayac.photobag"));
					 startActivity(intentAndroidMarket);
					
					 (new Thread(new Runnable() {
						 public void run() {
						 wv.loadUrl(webviewUploadScreenURL);
						 }
						 })).run();
				}

				
				if ((decodeUrl.contains("//")) && (decodeUrl.contains("photocon"))) {

					System.out.println("Atik tapped URL in layout: new URL" +decodeUrl.toString() );
					if ((decodeUrl.contains("//")) && (decodeUrl.contains("photocon"))) {
						
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
	        	          		
	    	          	}  else {
							System.out.println("Atik tapped URL in layout: new URL in Right URL" +decodeUrl.toString() );
							// Go TO  webview 
							Intent intent = new Intent(getActivity(),
									PBWebViewPhotoContestActivity.class);
							//intent.putExtra(PBAPIContant.PB_SETTING_EXTRA_URL, "http://192.168.0.113/test/button.html");
							//intent.putExtra(PBAPIContant.PB_SETTING_EXTRA_URL, PBAPIContant.PB_PHOTO_CONTEST_URL);
							intent.putExtra(PBAPIContant.PB_SETTING_EXTRA_URL, decodeUrl);
							intent.putExtra(PBAPIContant.PB_SETTING_EXTRA_TITLE, getString(R.string.pb_PBWebViewPhotoContestActivity_screen_titile));
							startActivity(intent);
	    	          	}
						

					}
				}

				
				
				
				if ((decodeUrl.contains("//")) && (decodeUrl.contains("?"))) {

					String webviewAction = decodeUrl.substring(
							decodeUrl.indexOf("//") + 2, decodeUrl.indexOf("?"));
					String webviewData = decodeUrl.substring(
							decodeUrl.indexOf("?") + 1, decodeUrl.length());

					if (webviewAction.equals(PBAPIContant.PB_WEBVIEW_URL_PARSE_REWARD)) {

						if (webviewData.equals(PBAPIContant.PB_WEBVIEW_URL_PARSE_ACORN_FOREST)) {

							isFromMori = true;
		                    PBTaskCheckDeviceLockStatus taskFromMori = new PBTaskCheckDeviceLockStatus();
		                    taskFromMori.execute();
							
							 (new Thread(new Runnable() {
							 public void run() {
							 wv.loadUrl(webviewUploadScreenURL);
							 }
							 })).run();

						}  else if (webviewData.equals(PBAPIContant.PB_WEBVIEW_URL_PARSE_ACORN_OPENPAGE)) {


			        		 /// rifat add ///
			        		
			        		 int kumaMemoUpdateNumber = 0;
					         int acronsCounter = PBPreferenceUtils.getIntPref(
					        		 getActivity().getApplicationContext(), PBConstant.PREF_NAME,
							 PBConstant.PREF_DONGURI_COUNT, 0);
							
							 int mapleCounter = PBPreferenceUtils.getIntPref(
					        		 getActivity().getApplicationContext(), PBConstant.PREF_NAME,
							 PBConstant.PREF_MAPLE_COUNT, 0);
							
							 int otameshiHoneyCounter = PBPreferenceUtils.getIntPref(
					        		 getActivity().getApplicationContext(), PBConstant.PREF_NAME,
							 PBConstant.PREF_HONEY_BONUS, 0);
							
							
							 int goldCounter = PBPreferenceUtils.getIntPref(
					        		 getActivity().getApplicationContext(), PBConstant.PREF_NAME,
							 PBConstant.PREF_GOLD_COUNT, 0);
							 
							 
							  int newHoney = PBPreferenceUtils.getIntPref(getActivity().getApplicationContext(),
							           PBConstant.PREF_NAME,
							           PBConstant.PREF_NAME_NOTIF_HONEY_NEW, 0);
							         int newMaple = PBPreferenceUtils.getIntPref(getActivity().getApplicationContext(),
							           PBConstant.PREF_NAME,
							           PBConstant.PREF_NAME_NOTIF_MAPLE_NEW, 0);
							         int newDonguri = PBPreferenceUtils.getIntPref(getActivity().getApplicationContext(),
							           PBConstant.PREF_NAME,
							           PBConstant.PREF_NAME_NOTIF_DONGURI_NEW, 0);
							         int newGold = PBPreferenceUtils.getIntPref(getActivity().getApplicationContext(),
							           PBConstant.PREF_NAME,
							           PBConstant.PREF_NAME_NOTIF_GOLD_NEW, 0);
							         
							         if (newHoney + newMaple + newDonguri + newGold > 0) { 
							          kumaMemoUpdateNumber = newHoney + newMaple + newDonguri + newGold;
							         } else {
							          kumaMemoUpdateNumber = 0;
							         }
							         
							         
							         boolean isShowKumaMemo = false;
							         PBTimelineHistoryModel	 newestTimelineData = PBDatabaseManager.getInstance(
							        		 getActivity().getApplicationContext()).getNewestTimelineHistory();
									 if (newestTimelineData != null) {
										 
										 isShowKumaMemo = true;
									 }
						
									 String token =  PBPreferenceUtils.getStringPref(getActivity(), PBConstant.PREF_NAME, 
							                    PBConstant.PREF_NAME_TOKEN,"");
									   String deviceUUID = PBPreferenceUtils
										         .getStringPref(PBApplication
										           .getBaseApplicationContext(),
										           PBConstant.PREF_NAME,
										           PBConstant.PREF_NAME_UID, "0");
									   
								     boolean hasInternet = PBApplication.hasNetworkConnection();
								     
							         Intent intent = new
							        		 //MainTab
									 Intent(getActivity(),MainTabActivity.class);
									 intent.putExtra("acronsCounter", acronsCounter);
									 intent.putExtra("mapleCounter", mapleCounter);
									 intent.putExtra("otameshiHoneyCounter",
									 otameshiHoneyCounter);
									 intent.putExtra("goldCounter", goldCounter);
									 intent.putExtra("kumaMemoUpdateNumber",
									 kumaMemoUpdateNumber);
									 intent.putExtra("isShowKumaMemo",
							        		   isShowKumaMemo);
									 intent.putExtra("token",
											 token);
									 intent.putExtra("uid", deviceUUID);
									 if(hasInternet) {
						        	   
										 startActivity(intent);   
						        	   
									 } else {
						        	   
										 /*Toast.makeText(KBUploadMainFragment.this, 
												 getString(R.string.pb_network_not_available_general_message), 1000).show();*/
										   Toast toast = Toast.makeText(getActivity().getApplicationContext(), getString(R.string.pb_network_not_available_general_message), 
													1000);
										   TextView v1 = (TextView) toast.getView().findViewById(android.R.id.message);
										   if( v1 != null) v1.setGravity(Gravity.CENTER);
										   toast.show();
									 }
							           

						
	
						             if (hasInternet) {
						              
						              (new Thread(new Runnable() {
						                  public void run() {
						                   wv.loadUrl(webviewUploadScreenURL);
						                  }
						                  })).run();
						              
						             } else {

						              view.findViewById(R.id.webview_upload_screen_layout_for_kuma);
						             }
						             
						             
			        		
					        /*(new Thread(new Runnable() {
					        public void run() {
					        	wv.loadUrl(webviewUploadScreenURL);
					        }
					        })).run();*/
					   } else {
						   // Nothing will happened if openpage URL is not found
					   }


					} 
				   
				}  else {

				}
				return true;			
		
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				Log.e("TAG", String.format(
						"[onReceivedError] error:%d , desc:%s , url:%s",
						errorCode, description, failingUrl));
			}

			@Override
			public void onReceivedSslError(WebView view,
					SslErrorHandler handler, SslError error) {
				Log.e("TAG", "[onReceivedSslError]");
			}
		});

		
	}
    
   
    
    private String decodeURIComponent(String string) {
		try {
			return URLDecoder.decode(string, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}
    

    
    void handleSendMultipleImages(Intent intent) {
    	ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
    	if (imageUris != null) {
    		ArrayList<String> selectedMedias = new ArrayList<String>();
    		long[] selectedTypeMedias = new long[imageUris.size()];
    		int i = 0;
    		for (Uri uri : imageUris){
    			if (uri == null) {
    				continue;
    			}
    			String[] columns = { MediaStore.Images.Media.DATA };
    			Cursor cursor = getActivity().getContentResolver().query(uri, columns, null, null, null);
    			if (cursor != null) {
	    			cursor.moveToFirst();
	    			String path = cursor.getString(0);
	    			cursor.close();
	    			Log.i("handleSendMultipleImages > raw path", path);
	    			selectedMedias.add(path);
	    			selectedTypeMedias[i++] = -1; // image
    			}
    		}
    		
    		
    		PBPreferenceUtils.saveBoolPref(getActivity().getBaseContext(),
					PBConstant.UPLOAD_SERVICE_PREF,
					PBConstant.PREF_UPLOAD_FINISH, false);
			PBPreferenceUtils.saveStringPref(getActivity().getBaseContext(),
					PBConstant.UPLOAD_SERVICE_PREF,
					PBConstant.PREF_UPLOAD_PASS, "");
			PBPreferenceUtils.saveStringPref(getActivity().getBaseContext(),
					PBConstant.UPLOAD_SERVICE_PREF,
					PBConstant.PREF_COLLECTION_ID, "");
			PBPreferenceUtils.saveStringPref(getActivity().getBaseContext(),
					PBConstant.UPLOAD_SERVICE_PREF,
					PBConstant.PREF_COLLECTION_THUMB, "");
			PBPreferenceUtils.saveBoolPref(getActivity().getBaseContext(),
					PBConstant.UPLOAD_SERVICE_PREF,
					PBConstant.PREF_INPUT_SEQUENCE_FINISH, false);

    		Intent serviceIntent = new Intent(getActivity().getBaseContext(),
    				UploadService.class);
    		serviceIntent.putStringArrayListExtra(
    				PBConstant.INTENT_SELECTED_MEDIA, selectedMedias);
    		serviceIntent.putExtra(PBConstant.INTENT_SELECTED_MEDIA_TYPE,
    				selectedTypeMedias);
    		serviceIntent
    		.putExtra(PBConstant.INTENT_UPLOADED_PHOTO_NUM, -1);
    		serviceIntent.putExtra(
    				PBConstant.INTENT_START_SERVICE_FROM_SELECT_IMG, true);
    		getActivity().startService(serviceIntent);

    		intent = new Intent(getActivity().getBaseContext(),
    				UploadSetPasswordActivity.class);
    		intent.putStringArrayListExtra(
    				PBConstant.INTENT_SELECTED_MEDIA, selectedMedias);
    		intent.putExtra(PBConstant.INTENT_SELECTED_MEDIA_TYPE,
    				selectedTypeMedias);
    		startActivity(intent);
           
    		PBMainTabBarActivity.sMainContext.mTabHost.setCurrentTabByTag(PBConstant.UPLOAD_TAG);
    	}
    }
        
    @Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);

		if (!hidden) {
			
			
			
			PBPreferenceUtils.saveBoolPref(getActivity().getApplicationContext(), PBConstant.PREF_NAME, PBConstant.UPLOAD_TAG, false);
			
			
			

			boolean hasInternet = PBApplication.hasNetworkConnection();
	        mUploadAD.setVisibility(hasInternet ? View.VISIBLE : View.GONE);
	        
	        if (!hasInternet || PBDownloadFragment.isKoukaibukuroDisabled) {
	            
	            view.findViewById(R.id.webview_upload_screen_layout_for_kuma).setVisibility(View.GONE);
	      view.findViewById(R.id.view_kuma_no_internet_upload).setVisibility(
	        View.VISIBLE);
	         
	        } else {
		         webview = (WebView) view.findViewById(R.id.webview_upload_screen_for_kuma);
		         try {
			       String versionName = getActivity().getPackageManager().getPackageInfo(
			         getActivity().getPackageName(), 0).versionName;
			       
			       webviewUploadScreenURL = PBAPIContant.PB_UPLOAD_WEBVIEW_URL + versionName ; 
			       webview.loadUrl(webviewUploadScreenURL);
		         } catch (NameNotFoundException e) {
		        	 e.printStackTrace();
		         }
		         loadOpenPage(webview);
		         
		         System.out.println("ishti   "+PBDownloadFragment.isKoukaibukuroDisabled);
		         view.findViewById(R.id.webview_upload_screen_layout_for_kuma).setVisibility(View.VISIBLE);
		      view.findViewById(R.id.view_kuma_no_internet_upload).setVisibility(
		        View.GONE);
	         
	        }
		}

	}
    
    @Override
	public void onResume() {
        super.onResume();
        
        
        System.out.println("ishti   "+PBDownloadFragment.isKoukaibukuroDisabled);
    }
    
    @Override
	public void onDestroy() {
    	super.onDestroy();
    	
    }
    
    
	public void onCrossImageClik(View view) {
		
		if(dialog != null ) {
			dialog.dismiss();
		}
		
	}
	
	public void onVideoCompressSettingHigh(View view) {
		
		ImageView image = (ImageView) view.findViewById(R.id.icon_checkbox_video_compress_high);
		
		View mediumLayout = (LinearLayout)dialog.findViewById(R.id.layout_checkbox_video_compress_medium);		
		ImageView imageMedium = (ImageView) mediumLayout.findViewById(R.id.icon_checkbox_video_compress_medium);
		
		View LowLayout = (LinearLayout)dialog.findViewById(R.id.layout_checkbox_video_compress_low);
		ImageView imageLow = (ImageView) LowLayout.findViewById(R.id.icon_checkbox_video_compress_low);
		
		
		boolean videoCompressHighSetting = PBPreferenceUtils.getBoolPref(
        		getActivity(), PBConstant.PREF_NAME, 
    			PBConstant.PREF_VIDEO_COMPRESS_HIGH_QUALITY, false);
        
        if(!videoCompressHighSetting) {
        	// change background image
    		Drawable checkBoxOn = getResources().getDrawable(R.drawable.checkbox_on);
    		Drawable checkBoxOff = getResources().getDrawable(R.drawable.checkbox_off);
    		
    		image.setImageDrawable(checkBoxOn);
    		imageMedium.setImageDrawable(checkBoxOff);
    		imageLow.setImageDrawable(checkBoxOff);
    		
        	// update shared preference value
        	PBPreferenceUtils.saveBoolPref(
        			getActivity(), 
        			PBConstant.PREF_NAME, 
        			PBConstant.PREF_VIDEO_COMPRESS_HIGH_QUALITY, 
        			true);
        	
        	// update shared preference value
        	PBPreferenceUtils.saveBoolPref(
        			getActivity(), 
        			PBConstant.PREF_NAME, 
        			PBConstant.PREF_VIDEO_COMPRESS_MEDIUM_QUALITY, 
        			false);
        	
        
        	// update shared preference value
        	PBPreferenceUtils.saveBoolPref(
        			getActivity(), 
        			PBConstant.PREF_NAME, 
        			PBConstant.PREF_VIDEO_COMPRESS_LOW_QUALITY, 
        			false);

        }
		
	}
	

	
	public void onVideoCompressSettingMedium(View view) {
		
		ImageView image = (ImageView) view.findViewById(R.id.icon_checkbox_video_compress_medium);
        
		View highLayout = (LinearLayout)dialog.findViewById(R.id.layout_checkbox_video_compress_high);
		ImageView imageHigh = (ImageView) highLayout.findViewById(R.id.icon_checkbox_video_compress_high);
		
		View LowLayout = (LinearLayout)dialog.findViewById(R.id.layout_checkbox_video_compress_low);
		ImageView imageLow = (ImageView) LowLayout.findViewById(R.id.icon_checkbox_video_compress_low);
		
		boolean videoCompressMediumSetting = PBPreferenceUtils.getBoolPref(
        		getActivity(), PBConstant.PREF_NAME, 
    			PBConstant.PREF_VIDEO_COMPRESS_MEDIUM_QUALITY, false);
        
        if(!videoCompressMediumSetting) {
        	// change background image
    		Drawable checkBoxOn = getResources().getDrawable(R.drawable.checkbox_on);
    		Drawable checkBoxOff = getResources().getDrawable(R.drawable.checkbox_off);
    		
    		image.setImageDrawable(checkBoxOn);
    		imageHigh.setImageDrawable(checkBoxOff);
    		imageLow.setImageDrawable(checkBoxOff);
    		
        	// update shared preference value
        	PBPreferenceUtils.saveBoolPref(
        			getActivity(), 
        			PBConstant.PREF_NAME, 
        			PBConstant.PREF_VIDEO_COMPRESS_MEDIUM_QUALITY, 
        			true);
        	
        	// update shared preference value
        	PBPreferenceUtils.saveBoolPref(
        			getActivity(), 
        			PBConstant.PREF_NAME, 
        			PBConstant.PREF_VIDEO_COMPRESS_HIGH_QUALITY, 
        			false);
        	
        
        	// update shared preference value
        	PBPreferenceUtils.saveBoolPref(
        			getActivity(), 
        			PBConstant.PREF_NAME, 
        			PBConstant.PREF_VIDEO_COMPRESS_LOW_QUALITY, 
        			false);
        } 
	}
	
	public void onVideoCompressSettingLow(View view) {
		
		ImageView image = (ImageView) view.findViewById(R.id.icon_checkbox_video_compress_low);
        
		View highLayout = (LinearLayout)dialog.findViewById(R.id.layout_checkbox_video_compress_high);
		ImageView imageHigh = (ImageView) highLayout.findViewById(R.id.icon_checkbox_video_compress_high);
		
		View mediumLayout = (LinearLayout)dialog.findViewById(R.id.layout_checkbox_video_compress_medium);
		ImageView imageMedium = (ImageView) mediumLayout.findViewById(R.id.icon_checkbox_video_compress_medium);
		
		boolean videoCompressLowSetting = PBPreferenceUtils.getBoolPref(
        		getActivity(), PBConstant.PREF_NAME, 
    			PBConstant.PREF_VIDEO_COMPRESS_LOW_QUALITY, false);
        
        if(!videoCompressLowSetting) {
        	// change background image
    		Drawable checkBoxOn = getResources().getDrawable(R.drawable.checkbox_on);
    		Drawable checkBoxOff = getResources().getDrawable(R.drawable.checkbox_off);
    		
    		image.setImageDrawable(checkBoxOn);
    		imageHigh.setImageDrawable(checkBoxOff);
    		imageMedium.setImageDrawable(checkBoxOff);
    		
        	// update shared preference value
        	PBPreferenceUtils.saveBoolPref(
        			getActivity(), 
        			PBConstant.PREF_NAME, 
        			PBConstant.PREF_VIDEO_COMPRESS_LOW_QUALITY, 
        			true);
        	
        	// update shared preference value
        	PBPreferenceUtils.saveBoolPref(
        			getActivity(), 
        			PBConstant.PREF_NAME, 
        			PBConstant.PREF_VIDEO_COMPRESS_HIGH_QUALITY, 
        			false);
        	
        
        	// update shared preference value
        	PBPreferenceUtils.saveBoolPref(
        			getActivity(), 
        			PBConstant.PREF_NAME, 
        			PBConstant.PREF_VIDEO_COMPRESS_MEDIUM_QUALITY, 
        			false);
        }
	}
    
    
    
    /**
 	 * Async task class
 	 * for checking whether device is locked or not
 	 * use API  "https://"+API_HOST+"/info_migration"
 	 * @author atikur
 	 *
 	 */
 	private class PBTaskCheckDeviceLockStatus extends AsyncTask<Void, Void, Void>{
 		
 		boolean result200Ok = false;
 		boolean result400 = false;
 		boolean resultOtherError = false;
 		
 		String message = null;
 		@Override
 		protected Void doInBackground(Void... params) {

     		String migrationCode = PBPreferenceUtils.getStringPref(
     				PBApplication.getBaseApplicationContext(),
     				PBConstant.PREF_NAME, PBConstant.PREF_MIGRATON_CODE, ""); // Atik  modified this line of code. It will not work when there is no migration code.
     		
     		if(TextUtils.isEmpty(migrationCode)) {
    			System.out.println("Atik no migration code is set");
     			return null;
     		}
     		
 			Response res = PBAPIHelper.checkDeviceLockForDeviceChange(migrationCode);
 			System.out.println("atik response:"+res.errorCode);
 			Log.d("MIGRATION_CODE", "res: "+res.errorCode + " "+res.decription);
 			if(res!=null){
 				if(res.errorCode == ResponseHandle.CODE_200_OK) {

 					result200Ok  = true;
 					message = res.decription;
 					System.out.println("atik MIGRATION_CHECK_LOCK_STATUS"+"200 OK:"+res.decription);
 					
 					try {
 						JSONObject result = new JSONObject(res.decription);
 						if(result.has("message")) {
 							System.out.println("200 OK message is:"+result.getString("message"));
 							message_response_check_lock_status = result.getString("message");
 							
 						} 
 						if(result.has("result")) {
 							result.getString("result");
 							System.out.println("200 OK Result code is:"+resultCode);
 						}
 						
 						if(result.has("title")) {
 							titte_after_start_migration  = result.getString("title");
 							System.out.println("200 OK title  is:"+resultCode);
 						}
 						
 						
 						System.out.println("Atik device lock status is 200 OK");
 						isDeviceLock = true;
 						//System.out.println("Atik device set lock status 200"+isDeviceLock);

 						

 					} catch (JSONException e) {
 						System.out.println("MIGRATION_CODE"+" Json parse exception occured");
 					}
 					
 				}else if(res.errorCode == ResponseHandle.CODE_400){
 					result400 = true;
 					message = res.decription;
 					System.out.println("Atik device lock status is 400");

 					isDeviceLock = false;
 					//System.out.println("Atik device set lock status false"+isDeviceLock);

 				} else {
 			
 				}
 			}
 			return null;
 		}
 		
 		@Override
 		protected void onPostExecute(Void result) {
 			super.onPostExecute(result);
 			
 			if(result200Ok) {
             	//PBApplication.makeToastMsg(message_response_check_lock_status);
             	updateUISuccessfull(message_response_check_lock_status,titte_after_start_migration);
             	isDeviceLock = true;
 				System.out.println("Atik device set lock status 200"+isDeviceLock);
				System.out.println("Atik inside else block of aysn task inside 200 ");
 				//return;
 			} else if(result400) {
 				isDeviceLock = false;
 				System.out.println("Atik device set lock status false"+isDeviceLock);
				System.out.println("Atik inside else block of aysn task inside 400 ");
 				updateUIWhenDeviceIsNotLocked();
 				
 			} else {
				System.out.println("Atik inside else block of aysn task other ");
 				updateUIWhenDeviceIsNotLocked();
 			}

 			
 		}
 	}
 	
 	// Update UI when received merge code successfully
 	private void updateUISuccessfull(final String message, final String title) {

 			final Handler handler = new Handler();
 			handler.postDelayed(new Runnable() {
 			    @Override
 			    public void run() {
 			        // Delay dialog display after 1s = 1000ms
 					PBGeneralUtils.showAlertDialogActionWithOnClick(getActivity(), 
 							title, 
 							message,
 							getString(R.string.dialog_ok_btn),
 							mOnClickOkDialogMigrationVerified);
 					
 			    }
 			}, 1000);

 	}
 	
 	
 	// Update UI when received response code 400
 	private void updateUIWhenDeviceIsNotLocked() {
 	
 		if(isFromMori) {
 			isFromMori = false;
 			Intent intentReward = new Intent(getActivity(),
 					PBAcornForestActivity.class);
 			startActivity(intentReward);
 		} else {
 	        Intent intent = new Intent();
 	        intent.setClass(getActivity(),
 	                SelectMultipleImageActivity.class);
 	        startActivity(intent);

 		}

 		
 	}
 	
 	private DialogInterface.OnClickListener mOnClickOkDialogMigrationVerified = new DialogInterface.OnClickListener() {
 		@Override
 		public void onClick(DialogInterface dialog, int which) {

 			// atik Call start data migration task
 			dialog.dismiss();
 			
 		}
 	};
 	

 	 
 
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
 	
	  // Added below activity life cycle method for Google analytics 
	  @Override
	  public void onStart() {
		  
		    super.onStart();
		    System.out.println("Atik start Easy Tracker");
		    EasyTracker.getInstance(getActivity()).activityStart(getActivity());
	    }
	  
	   //Added below activity life cycle method for Google analytics
	    
	   @Override
	   public void onStop() {
		    super.onStop();
		    System.out.println("Atik stop Easy Tracker");
		    EasyTracker.getInstance(getActivity()).activityStop(getActivity());
	   }

}
