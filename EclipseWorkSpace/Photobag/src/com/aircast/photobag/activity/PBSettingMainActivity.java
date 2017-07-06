package com.aircast.photobag.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aircast.koukaibukuro.util.ApiHelper;
import com.aircast.koukaibukuro.util.ApiHelper.Response;
import com.aircast.koukaibukuro.util.Constant;
import com.aircast.koukaibukuro.util.SPreferenceUtils;
import com.aircast.koukaibukuro.util.Util;
import com.aircast.photobag.R;
import com.aircast.photobag.api.PBAPIContant;
import com.aircast.photobag.application.PBApplication;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.database.PBDatabaseDefinition;
import com.aircast.photobag.database.PBDatabaseManager;
import com.aircast.photobag.model.PBHistoryEntryModel;
import com.aircast.photobag.utils.PBGeneralUtils;
import com.aircast.photobag.utils.PBPreferenceUtils;
import com.aircast.photobag.widget.actionbar.ActionBar;
import com.google.analytics.tracking.android.EasyTracker;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.Holder;
import com.orhanobut.dialogplus.OnBackPressListener;
import com.orhanobut.dialogplus.OnDismissListener;
import com.orhanobut.dialogplus.ViewHolder;

/**
 * Setting page.
 * <p><b>TODO: </b>Too many deprecated or never used code here. remove them.
 * Notice that deprecated not only exists here but also in the layout file.
 * Some of the deprecated elements only set the visibility to gone but code still remains.</p>
 */
public class PBSettingMainActivity extends PBAbsActionBarActivity implements OnClickListener {
    @SuppressWarnings("unused")
	private static final String TAG = "PBSettingMainActivity";

    private LinearLayout mLvWaiting;
     
    //20120601 Bac Setting new UI
    RelativeLayout mHoneyViewValid;
    RelativeLayout mHoneyViewUnValid;
    TextView mHoneyCountView;
    private ActionBar mHomeBar;
    private PBDatabaseManager mDatabaseManager;
    private Cursor mHistoryCursor;
    
    public static PBSettingMainActivity sSettingContext;
    
    private EditText nickName;
	private TextView txtNickName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        sSettingContext = this;
        
        setContentView(R.layout.pb_layout_settings_main);
        mDatabaseManager = PBDatabaseManager
        .getInstance(PBMainTabBarActivity.sMainContext);
        mHomeBar = (ActionBar)findViewById(R.id.actionBar);
        mHomeBar.setTitle(getString(R.string.tab_mnu_my_page_main));
        setHeader(mHomeBar, getString(R.string.main_setting_title));
        
        findViewById(R.id.pb_tutorial_honey_webview).setOnClickListener(this);
        findViewById(R.id.pb_mail_to_pc).setOnClickListener(this);
        findViewById(R.id.pb_migration_code).setOnClickListener(this);  // atik added migration code
        findViewById(R.id.pb_migration_code_verify).setOnClickListener(this); // atik added migration code verify
        findViewById(R.id.pb_setting_faqFAQ).setOnClickListener(this);
        findViewById(R.id.pb_setting_clear_cache).setOnClickListener(this);
        findViewById(R.id.pb_setting_license_info).setOnClickListener(this);

        findViewById(R.id.pb_setting_contact).setOnClickListener(this);
        findViewById(R.id.pb_setting_terms).setOnClickListener(this);
        findViewById(R.id.pb_setting_version).setOnClickListener(this); 
        findViewById(R.id.pb_setting_switch_notification).setOnClickListener(this);
        findViewById(R.id.buyMapelFromShop).setOnClickListener(this);

        mLvWaiting = (LinearLayout) findViewById(R.id.ll_loading_panel_waiting);
        mLvWaiting.setVisibility(View.GONE);
        
        boolean noti = PBPreferenceUtils.getBoolPref(
    			PBSettingMainActivity.this, PBConstant.PREF_NAME, 
    			PBConstant.PREF_SETTING_SHOW_NOTIFICATION, true);        
        findViewById(R.id.pb_setting_switch_notification_on).setVisibility(noti ? View.VISIBLE : View.INVISIBLE);
    	findViewById(R.id.pb_setting_switch_notification_off).setVisibility(!noti ? View.VISIBLE : View.INVISIBLE);
    	
    	txtNickName = (TextView) findViewById(R.id.kb_settings_nick_name);
		
		String nickname = SPreferenceUtils.getStringPref(getApplicationContext(),
				Constant.PREF_NAME,
				Constant.KB_NICKNAME,
				"");
		
		txtNickName.setText(nickname);
		
		findViewById(R.id.content_kb_settings_nickname).setOnClickListener(this);
		
		if (Util.isOnline(PBSettingMainActivity.this)) {
			PBTaskGetNickName task = new PBTaskGetNickName();
			task.execute();
		}
    }
    
    private class PBTaskGetNickName extends AsyncTask<Void, Void, Response> {

		@Override
		protected Response doInBackground(Void... params) {
			String uid = SPreferenceUtils.getStringPref(getApplicationContext(),
					Constant.PREF_NAME, Constant.UID, "");
			Response response = ApiHelper.getNickName(uid);
			return response;
		}

		@Override
		protected void onPostExecute(Response response) {
			super.onPostExecute(response);

			if (response != null) {
				int response_code = response.errorCode;
				String response_body = response.decription;

				try {
					JSONObject jObject;
					jObject = new JSONObject(response_body);

					if (response_code == 200) {
						jObject = new JSONObject(response_body);

						if (jObject != null) {
							if (jObject.has("message")) {

								
								/*
								 * defaultNickName =
								 * jObject.getString("message");
								 */
								SPreferenceUtils.saveStringPref(getApplicationContext(),
										Constant.PREF_NAME,
										Constant.KB_NICKNAME,
										jObject.getString("message"));
								
								
								txtNickName.setText(jObject.getString("message"));
							}

						}
					} else {
						// Toast when error occured
						jObject = new JSONObject(response_body);

						if (jObject != null) {
							if (jObject.has("message")) {

								// show toast
//								Toast.makeText(getApplicationContext(),
//										jObject.getString("message"),
//										Toast.LENGTH_SHORT).show();

							}

						}
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	private class PBTaskNickNameRegistration extends
			AsyncTask<Void, String, Response> {

		String name;
		Context ctx;
		//ProgressHUD mProgressHUD;

		public PBTaskNickNameRegistration(Context context, String name) {
			super();
			this.name = name;
			this.ctx = context;
			//mProgressHUD = new ProgressHUD(getApplicationContext());
		}

		@Override
		protected void onPreExecute() {
			//mProgressHUD.show(true);
			super.onPreExecute();
		}

		@Override
		protected Response doInBackground(Void... params) {

			String uid = SPreferenceUtils.getStringPref(ctx,
					Constant.PREF_NAME, Constant.UID, "");

			Response response = ApiHelper.nickNameRegistration(uid, name);
			return response;
		}

		// @Override
		// protected void onProgressUpdate(String... values) {
		// mProgressHUD.setMessage(values[0]);
		// super.onProgressUpdate(values);
		// }

		@Override
		protected void onPostExecute(Response response) {
			super.onPostExecute(response);
//			if (mProgressHUD.isShowing()) {
//
//				mProgressHUD.dismiss();
//			}

			if (response != null) {
				int response_code = response.errorCode;
				String response_body = response.decription;

				try {
					JSONObject jObject;
					jObject = new JSONObject(response_body);

					if (response_code == 200) {
						jObject = new JSONObject(response_body);

						if (jObject != null) {
							if (jObject.has("message")) {
								Toast.makeText(ctx,
										jObject.getString("message"),
										Toast.LENGTH_SHORT).show();

								SPreferenceUtils.saveStringPref(getApplicationContext(),
										Constant.PREF_NAME,
										Constant.KB_NICKNAME, nickName
												.getText().toString().trim());
								
								txtNickName.setText(nickName
										.getText().toString().trim());

							}

						}
					} else {
						// Toast when error occured
						jObject = new JSONObject(response_body);

						if (jObject != null) {
							if (jObject.has("message")) {

								// show toast
								Toast.makeText(ctx,
										jObject.getString("message"),
										Toast.LENGTH_SHORT).show();

								String default_nickName = SPreferenceUtils
										.getStringPref(getApplicationContext(),
												Constant.PREF_NAME,
												Constant.KB_NICKNAME, "");

								nickName.setText(default_nickName);
								txtNickName.setText(default_nickName);

							}

						}
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

    private void updateSettingInfo() {
        TextView tvVersion = (TextView) findViewById(R.id.tvVersion);
        tvVersion.setText(PBApplication.getAppVersion());
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // V2 google analytics has been comment out
        /*if (PBTabBarActivity.gaTracker != null) {
        	PBTabBarActivity.gaTracker.trackPageView("PBSettingMainActivity");
        }*/
        
        updateSettingInfo();
    }

    @Override
    protected void onPause() {    
        super.onPause();
    }
    
    
	//Added below activity life cycle method for Google analytics
	@Override
    public void onStart() {
 	    super.onStart();
	    System.out.println("Atik start Easy Tracker for PBSettingMainActivity");
	    EasyTracker.getInstance(this).activityStart(this);
    }
  
    //Added below activity life cycle method for Google analytics
    @Override
    public void onStop() {
	    super.onStop();
	    System.out.println("Atik stop Easy Tracker for PBSettingMainActivity");
	    EasyTracker.getInstance(this).activityStop(this);
    }
    

    /**
     * start activity webview with title bar
     * @param url
     * @param title
     * @param requestCode
     */
    private void startWebViewActivity(String title, String url, int requestCode) { 
        Intent intent = new Intent(PBSettingMainActivity.this, PBWebViewActivity.class);
        intent.putExtra(PBAPIContant.PB_SETTING_EXTRA_URL, url);
        intent.putExtra(PBAPIContant.PB_SETTING_EXTRA_TITLE, title);
        startActivityForResult(intent, requestCode); 
    }

    @Override
    public void onClick(View v) {
        Intent intentView;
        switch (v.getId()) {
        case R.id.getFreeDonguri:
        	/*
        	Intent appdriverIntent = PBGeneralUtils.getAppdriverIntent(this);
        	startActivity(appdriverIntent);*/
        	boolean hasInternet = PBApplication.hasNetworkConnection();
        	if (hasInternet) {
            	PBGeneralUtils.openAcornWebview(PBSettingMainActivity.this);
            	break;
        	} else {
           	 	/*Toast.makeText(PBSettingMainActivity.this, 
           			 getString(R.string.pb_network_not_available_general_message), 1000).show();*/
			   Toast toast = Toast.makeText(PBSettingMainActivity.this, getString(R.string.pb_network_not_available_general_message), 
						1000);
			   TextView v1 = (TextView) toast.getView().findViewById(android.R.id.message);
			   if( v1 != null) v1.setGravity(Gravity.CENTER);
			   toast.show();
            	break;        		
            }


        case R.id.pb_setting_contact:
        case R.id.pb_setting_report_violation_term:
            intentView = new Intent(android.content.Intent.ACTION_SEND);
            String[] recipients = new String[] {};
            intentView.putExtra(android.content.Intent.EXTRA_EMAIL, recipients);
            if(v.getId() == R.id.pb_setting_contact)
            	intentView.putExtra(android.content.Intent.EXTRA_SUBJECT,
                        getString(R.string.pb_app_name));
            else
            	intentView.putExtra(android.content.Intent.EXTRA_SUBJECT,
                        "Report");
            String manufacturer = Build.MANUFACTURER;
            String newLine = manufacturer.equals(PBConstant.MANUFACTURER_AMAZON) ? "<br/>" : "\n";
            
            String sendMessage = String.format(getString(
                    R.string.pb_setting_contactweb_send_message,
                    PBPreferenceUtils.getStringPref(PBApplication
                            .getBaseApplicationContext(), PBConstant.PREF_NAME,
                            PBConstant.PREF_NAME_UID, getString(R.string.pb_setting_contact_code))));
            sendMessage += newLine + "Mobile Type: " + Build.MODEL; 
            sendMessage += newLine + "Os: Android v" + Build.VERSION.RELEASE;
            sendMessage += newLine + "API lv:" + Build.VERSION.SDK_INT;
            try {
				sendMessage += newLine + "App version:"
							+ getPackageManager().getPackageInfo(getPackageName(),0).versionName;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
            
            sendMessage += newLine + "Language :" + Locale.getDefault().getDisplayLanguage();
            
            String[] emailRecipients = new String[] { getString(R.string.pb_setting_contact_mail)};
            intentView.putExtra(android.content.Intent.EXTRA_EMAIL,
                    emailRecipients);
            intentView.putExtra(android.content.Intent.EXTRA_TEXT, sendMessage);
            intentView.setType("plain/text");

            startActivity(intentView);

            break;

        case R.id.pb_setting_faqFAQ:
        	boolean hasInternetFromFaq = PBApplication.hasNetworkConnection();
        	if (hasInternetFromFaq) { // Check Internet Activity
                // 20120220 @lent use string localization for url
                startWebViewActivity(
                        getString(R.string.pb_faq),
                        String.format(getString(R.string.pb_setting_url_faq), "&", "&"),
                        PBConstant.REQUEST_VIEW_WEB_PAGE);

                break;
        	} else {
           	 	/*Toast.makeText(PBSettingMainActivity.this, 
           			 getString(R.string.pb_network_not_available_general_message), 1000).show();*/
			   Toast toast = Toast.makeText(PBSettingMainActivity.this, getString(R.string.pb_network_not_available_general_message), 
						1000);
			   TextView v1 = (TextView) toast.getView().findViewById(android.R.id.message);
			   if( v1 != null) v1.setGravity(Gravity.CENTER);
			   toast.show();
               break;
        	}


        case R.id.pb_setting_terms:   
        	boolean hasInternetFromTerms = PBApplication.hasNetworkConnection();
        	if (hasInternetFromTerms) { // Check Internet Activity
                // 20120220 @lent use string localization for url            
                startWebViewActivity(
                        getString(R.string.pb_terms),
                        getString(R.string.pb_setting_url_term_of_use),
                        PBConstant.REQUEST_VIEW_WEB_PAGE);

                break;
        	} else {
           	 	/*Toast.makeText(PBSettingMainActivity.this, 
           			 getString(R.string.pb_network_not_available_general_message), 1000).show();*/
 			   Toast toast = Toast.makeText(PBSettingMainActivity.this, getString(R.string.pb_network_not_available_general_message), 
 						1000);
 			   TextView v1 = (TextView) toast.getView().findViewById(android.R.id.message);
 			   if( v1 != null) v1.setGravity(Gravity.CENTER);
 			   toast.show(); 
               break;
        	}

        case R.id.pb_setting_version:
            String urlMarketUrl = PBPreferenceUtils.getStringPref(sSettingContext, PBConstant.PREF_NAME, 
                    PBConstant.PREF_NAME_MARKET_URL, "");
            if(TextUtils.isEmpty(urlMarketUrl)){;
                urlMarketUrl = "https://market.android.com/details?id=com.kayac.photobag";
            }
            intentView = new Intent(Intent.ACTION_VIEW, Uri.parse(urlMarketUrl));
            startActivity(intentView);
            break;
        case R.id.pb_tutorial_honey_webview:
            startWebViewActivity(
                    getString(R.string.pb_how_to_get_your_honey),
                    getString(R.string.pb_url_below_honey),
                    PBConstant.REQUEST_VIEW_WEB_PAGE);
            //20120607 @HaiNT15: fix bug: wrong url #S
            break;
        case R.id.pb_mail_to_pc:
            intentView = new Intent(PBSettingMainActivity.this,
                    PBSettingMailToPCActivity.class);
            startActivity(intentView);
        	break;
        	
        case R.id.pb_migration_code:
        	
            intentView = new Intent(PBSettingMainActivity.this,
            		PBSettingGetMigrationCode.class);
            startActivity(intentView);
        	break;

        case R.id.pb_migration_code_verify:
        	
            intentView = new Intent(PBSettingMainActivity.this,
            		PBSettingMigrationCodeVerify.class);
            startActivity(intentView);
        	break;	
        	
        case R.id.pb_setting_switch_notification:
        	View on = findViewById(R.id.pb_setting_switch_notification_on);
        	View off = findViewById(R.id.pb_setting_switch_notification_off);
        	boolean noti;
        	
        	if (on.getVisibility() == View.VISIBLE) {
        		on.setVisibility(View.INVISIBLE);
        		off.setVisibility(View.VISIBLE);
        		noti = false;
        	}else {
        		on.setVisibility(View.VISIBLE);
        		off.setVisibility(View.INVISIBLE);
        		noti = true;
        	}
        	PBPreferenceUtils.saveBoolPref(
        			PBSettingMainActivity.this, 
        			PBConstant.PREF_NAME, 
        			PBConstant.PREF_SETTING_SHOW_NOTIFICATION, 
        			noti);
        	System.out.println(noti);
        	break;
        case R.id.pb_setting_clear_cache:
		PBGeneralUtils.showAlertDialogAction(sSettingContext, 
				sSettingContext.getString(R.string.pb_title_cache_clear),
				sSettingContext.getString(R.string.pb_content_cache_clear), 
				sSettingContext.getString(R.string.dialog_ok_btn),
				sSettingContext.getString(R.string.dialog_cancel_btn),
				mOnClickOkDialog);
        	break;
       
        case R.id.pb_setting_license_info:
        	Intent licenseIntent = new Intent(PBSettingMainActivity.this,
        			PBSettingLicenseActivity.class);
			startActivity(licenseIntent);
        	break; 	
        	
        case R.id.buyMapelFromShop:
        	boolean hasInternetBeforeGoingToMatomegai = PBApplication.hasNetworkConnection();
        	if (hasInternetBeforeGoingToMatomegai) {
            	Intent honeyIntent = new Intent(PBSettingMainActivity.this,
    					PBPurchaseActivity.class);
    			startActivity(honeyIntent);
            	
            	break;
        	} else {
        		
				AlertDialog.Builder	exitDialog =  new AlertDialog.Builder(new ContextThemeWrapper(PBSettingMainActivity.this,
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
        	
        case  R.id.content_kb_settings_nickname:
			Holder holder = new ViewHolder(
					R.layout.kb_mypage_nickname);
			
			final DialogPlus dialogPlus = DialogPlus
					.newDialog(PBSettingMainActivity.this)
					.setContentHolder(holder)
					.setGravity(Gravity.CENTER)
					// .setAdapter(null)
					// .setOnItemClickListener(itemClickListener)
					.setOnDismissListener(
							new OnDismissListener() {
								@Override
								public void onDismiss(
										DialogPlus dialog) {
								}
							})
					.setOnBackPressListener(
							new OnBackPressListener() {
								@Override
								public void onBackPressed(
										DialogPlus dialog) {
									
									

								}
							})
					// .setOnCancelListener(cancelListener)
					// .setExpanded(expanded)
					.setCancelable(true).create();

			dialogPlus.show();
			
			String name = SPreferenceUtils.getStringPref(getApplicationContext(),
					Constant.PREF_NAME,
					Constant.KB_NICKNAME,
					"");
			nickName = (EditText) dialogPlus.findViewById(R.id.editText_nickname);
			nickName.setText(name);
			
			dialogPlus.findViewById(R.id.button_nickname_registration_cancle).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dialogPlus.dismiss();
					
				}
			});
			
			dialogPlus.findViewById(R.id.button_nickname_registration).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					
					dialogPlus.dismiss();
					
					if (nickName.getText().toString().length() > 0) {

						hideSoftKeyboard();

						boolean isWhitespace;

						isWhitespace = false;
						isWhitespace = nickName.getText().toString().startsWith(" ");

						isWhitespace = nickName.getText().toString().endsWith(" ");
						if (isWhitespace) {

							Toast.makeText(getApplicationContext(),
									R.string.kb_search_whitespace, Toast.LENGTH_SHORT)
									.show();
						} else {
							String default_nickName = SPreferenceUtils.getStringPref(
									getApplicationContext(), Constant.PREF_NAME,
									Constant.KB_NICKNAME, "");

							if (!nickName.getText().toString()
									.equalsIgnoreCase(default_nickName)) {

								if (Util.isOnline(PBSettingMainActivity.this)) {
									PBTaskNickNameRegistration task = new PBTaskNickNameRegistration(
											getApplicationContext(), nickName.getText()
													.toString());
									task.execute();
								} else {

									Toast toast = Toast
											.makeText(
													getApplicationContext(),
													getString(R.string.pb_network_not_available_general_message),
													1000);
									TextView v1 = (TextView) toast.getView()
											.findViewById(android.R.id.message);
									if (v1 != null)
										v1.setGravity(Gravity.CENTER);
									toast.show();
								}
							} else {

								Toast.makeText(getApplicationContext(),
										getString(R.string.kb_nickname_exist),
										Toast.LENGTH_SHORT).show();
							}

						}

					} else {

						Toast.makeText(getApplicationContext(),
								getString(R.string.kb_empty_nickname_warning),
								Toast.LENGTH_SHORT).show();
					}
				}
			});
		break;
        	
        default:
            break;
        }
    }
    
	private void hideSoftKeyboard() {
		InputMethodManager imm = (InputMethodManager) 
				getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(nickName.getWindowToken(), 0);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

	}
    
	private DialogInterface.OnClickListener mOnClickOkDialog = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			
			/*ArrayList<String> test;
			ArrayList<String> test1;
			test = testMethod();
			
			for(int i=0;i<test.size();i++) {
				System.out.println("Collection ID"+test.get(i));
			}

			test1 = testMethod1();
			
			for(int i=0;i<test1.size();i++) {
				System.out.println("Collection ID for All"+test1.get(i));
			}*/
			//
			clearCacheData();
			//PBApplication.makeToastMsg("キャッシュクリアしました。");
			PBApplication.makeToastMsg(getString(R.string.cache_clear_toast));
			
		}
	};
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

	@Override
	protected void handleHomeActionListener() {
		finish();
	}

	@Override
	protected void setLeftBtnsDetailHeader(ActionBar headerBar) {
		
	}
	
	
	private ArrayList<String> testMethod() {
		System.out.println("Entered test method");
		
		ArrayList<String> testData = new ArrayList<String>();
		
    	List<PBHistoryEntryModel> historyArray = mDatabaseManager.getHistories(PBDatabaseDefinition.HISTORY_INBOX);
    	List<PBHistoryEntryModel> historyArray1 = mDatabaseManager.getHistories(PBDatabaseDefinition.HISTORY_SENT);
    	Map<String, PBHistoryEntryModel> histories = new HashMap<String, PBHistoryEntryModel>();
    	for (PBHistoryEntryModel history : historyArray){
    		String collectionId = history.getCollectionId();
			testData.add(collectionId);

    	}
    	
    	for (PBHistoryEntryModel history1 : historyArray1){
    		String collectionId = history1.getCollectionId();
			testData.add(collectionId);
    	}
    	return testData;
	}
	
	
	private ArrayList<String> testMethod1() {
		System.out.println("Entered test method");
		
		ArrayList<String> testData = new ArrayList<String>();
		
    	List<String> historyArray = mDatabaseManager.getHistories1();
    	//List<PBHistoryEntryModel> historyArray1 = mDatabaseManager.getHistories(PBDatabaseDefinition.HISTORY_SENT);
    	//Map<String, PBHistoryEntryModel> histories = new HashMap<String, PBHistoryEntryModel>();
    	/*for (String history : historyArray){
    		String collectionId = history.ge;
			testData.add(collectionId);

    	}*/
    	
    	for(int i=0;i<historyArray.size();i++) {
    		testData.add(historyArray.get(i));
    		//System.out.println("Get All data"+historyArray.get(i);
    	}
    	

    	return testData;
	}
	
	// Method for clearing cache data for the application
	private boolean clearCacheData(){
		
		ArrayList<String> listCollectionID = new ArrayList<String>();
		ArrayList<String> listItem = new ArrayList<String>();
		ArrayList<String> listItemDataInboxSent = new ArrayList<String>();
		ArrayList<String> localStorageFileList = new ArrayList<String>();
			
		mHistoryCursor=mDatabaseManager.getHistoriesCursorAllData();
		
		boolean eof1 = mHistoryCursor.moveToFirst();
		while (eof1) {
			listCollectionID.add(mHistoryCursor.getString(1));
			System.out.println("All C_ID:"+mHistoryCursor.getString(0));
			System.out.println("AllCollection ID:"+mHistoryCursor.getString(1));
			System.out.println("All C_PASSWORD ID:"+mHistoryCursor.getString(2));
			eof1 = mHistoryCursor.moveToNext();
		}
			
			
		//listItem = mDatabaseManager.getAllValidPhotoVideoListInboxSent(listCollectionID, listItem);
		
		
		// Generate all valid data of sent and outbox
		for (int i=0; i<listCollectionID.size(); i++){
	    	/*PBDatabaseManager.getInstance(getBaseContext()).deleteHistory(
					String.valueOf(history.getEntryId()), listCollectionID.get(i));*/
				System.out.println("File name:"+listCollectionID.get(i));
				listItem = mDatabaseManager.generateAllValidDataInboxSent(listCollectionID.get(i));
				
				for(int j=0;j<listItem.size();j++) {
					listItemDataInboxSent.add(listItem.get(j));
					System.out.println("List of Item in Inbox & Sent:"+listItem.get(j));
				}
				
		}
		
		
		//Collect all the file stored in storage
		localStorageFileList = PBGeneralUtils.getAllCacheFileFromStorage();

		
		
		/*for(int i=0;i<listItem2.size();i++) {
			System.out.println("Only item in inbox-sent Atik :"+listItem2.get(i));
		}
		
		for(int i=0;i<localStorageFileList.size();i++) {
			System.out.println("Only storage :"+localStorageFileList.get(i));
		}*/
		

		
		for(int i=0;i<localStorageFileList.size();i++) {
			
			//System.out.println("Inside duplicate:"+localStorageFileList.get(i));
			if((!listItemDataInboxSent.contains(localStorageFileList.get(i)))) {
				
				System.out.println("Only Matched for delete:"+localStorageFileList.get(i));
				// TODO delete that specific file from cache folder

				  
	            if (localStorageFileList.get(i).contains("/video") && !localStorageFileList.get(i).contains("?width")) {
	                // delete video thumb
	                File file = new File(localStorageFileList.get(i) + PBConstant.VIDEO_THUMB_STR);
	                if (file.exists()) {
	                    file.delete();
	                }
	            }
	            File filePhoto = new File(localStorageFileList.get(i));
	            if (filePhoto.exists()) {
	            	filePhoto.delete();
	            	System.out.println("Deleting : " + filePhoto.getAbsolutePath());
	            }
				 
			}

		}
			
		return true;
	}
	
}
