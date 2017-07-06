package com.aircast.photobag.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.aircast.photobag.R;
import com.aircast.photobag.api.PBAPIHelper;
import com.aircast.photobag.api.ResponseHandle;
import com.aircast.photobag.api.ResponseHandle.Response;
import com.aircast.photobag.application.PBApplication;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.utils.PBGeneralUtils;
import com.aircast.photobag.utils.PBPreferenceUtils;
import com.aircast.photobag.widget.FButton;
import com.aircast.photobag.widget.actionbar.ActionBar;

/**
 * Confirm the migration code and send code through mail
 * */
public class PBSettingMigrationCodeVerify extends PBAbsActionBarActivity
implements OnClickListener {
	
    private FButton mDoneButton;   
    private FButton mCheckLockButton;
    private static final int MSG_SEND_MIGRATION_CODE = 1;
    private Context cxt;
	private View mLvLoadingWaiting;
	private EditText mEdtMigrationCode;
	private Handler backgroundHandler = new Handler();

	private static String message_response = "";
	private static String resultCode = "";
	private static String honeyCount = "";
	private static String acornCount = "";
	private static String mapleCount = "";
	private static String goldacornCcount = "";
	private static String message_response_after_migration = "";
	private static String message_response_check_lock_status = "";
	private static String resultCode_after_start_migration = "";
	private static String resultCode_check_lock_status = "";

	//private  CharSequence[] items = new String[5];
	List<String> listItems = new ArrayList<String>();
	
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pb_layout_setting_migration_code_verificaton);
        
        ActionBar headerBar = (ActionBar) findViewById(R.id.headerbar);
        setHeader(headerBar, getString(R.string.pb_setting_my_migration_code_verify));
        
        cxt = PBApplication.getBaseApplicationContext();
        
        mLvLoadingWaiting = findViewById(R.id.migration_code_loading_panel_waiting);
		if (mLvLoadingWaiting != null) {
			mLvLoadingWaiting.setVisibility(View.GONE);
		}
		//startInitialLoadingDataProgress(mLvLoadingWaiting);

        
        mDoneButton = (FButton) findViewById(R.id.btn_setting_sendDataMigration);
        mDoneButton.setEnabled(true);
        mDoneButton.setOnClickListener(this);
        
        mCheckLockButton = (FButton) findViewById(R.id.btn_setting_checkDeviceLock);
        mCheckLockButton.setVisibility(View.GONE);
        mCheckLockButton.setEnabled(true);
        mCheckLockButton.setOnClickListener(this);
        mCheckLockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call server API
        		String migrationCode1 = PBPreferenceUtils.getStringPref(
        				PBApplication.getBaseApplicationContext(),
        				PBConstant.PREF_NAME, PBConstant.PREF_MIGRATON_CODE, "");
            	PBApplication.makeToastMsg("Migration code:"+migrationCode1);
            	
            	// TODO Atik get migration code from server
            	
				//PBTaskGetMigrationCode task = new PBTaskGetMigrationCode();
    			/*PBTaskStartDataMigration task = new PBTaskStartDataMigration();
    			task.execute();*/
            	
        		/*new Thread() {
        			@Override
                	public void run(){				
        				handlerMigrationCodeVerifyIntent.sendEmptyMessage(MSG_SEND_DEVICE_LOCK_CHECK);
                	}
        		}.start();*/
            	
            	
            }
        });
        
        
        mEdtMigrationCode = (EditText) findViewById(R.id.edt_migration_code_input);
        // TODO get migration code value from shared preference

		String migrationCode = PBPreferenceUtils.getStringPref(
				PBApplication.getBaseApplicationContext(),
				PBConstant.PREF_NAME, PBConstant.PREF_MIGRATON_CODE, "");
        
		System.out.println("atik migration code"+migrationCode);
		
		// No need to set migration code as person migration code can be possible to do
		/*if(!TextUtils.isEmpty(migrationCode)) {
			mEdtMigrationCode.setText(migrationCode);
		}*/

        // TODO load data from Server
		/*new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				backgroundHandler.post(new Runnable() {
					
					@Override
					public void run() {
						PBTaskGetMigrationCode task = new PBTaskGetMigrationCode();
						task.execute();
					}
				});
			}
		}).start();*/

    }

	private Thread mInitialLoadingThread;
	private void startInitialLoadingDataProgress(View mLvLoadingWaiting2) {
		mLvLoadingWaiting2.setVisibility(View.VISIBLE);
		// Do something long
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				try {
					
					//PBTaskGetMigrationCode task = new PBTaskGetMigrationCode();
					PBTaskMigrationCodeVerify task = new PBTaskMigrationCodeVerify();
					task.execute();
				} catch (Exception e) {
					Log.w(PBConstant.TAG,
							"--initialLoadData: " + e.getMessage());
				}
				backgroundHandler.post(new Runnable() {
					@Override
					public void run() {
						//inflateDataToView();
					}
				});
			}
		};

		mInitialLoadingThread = new Thread(runnable);
		mInitialLoadingThread.setName("loading");
		mInitialLoadingThread.start();
		
	}
	


	@Override
	public void onClick(View view) {
		
        if (!PBApplication.hasNetworkConnection()) {
            PBApplication.makeToastMsg(getString(R.string.pb_network_error_content));
            return;
        }
		
        mDoneButton.setClickable(false);
        
        // Check migration code field empty or not
        if(TextUtils.isEmpty(mEdtMigrationCode.getText())) {
        	System.out.println("Atik No migration code is inputted");
            mDoneButton.setClickable(true);
        	return;
        }
        
        //TODO check migration code is own migration code or not
		String ownMigrationCode = PBPreferenceUtils.getStringPref(
				PBApplication.getBaseApplicationContext(),
				PBConstant.PREF_NAME, PBConstant.PREF_MIGRATON_CODE, "");
		System.out.println("Atik edit migration code:"+mEdtMigrationCode.getText());
		System.out.println("Atik SP migration code:"+ownMigrationCode);

		if(ownMigrationCode.equals(mEdtMigrationCode.getText().toString())) {
			System.out.println("Atik you input same migration code:"+mEdtMigrationCode.getText());
			// Need to show dialog
	        mDoneButton.setClickable(true);
	        mEdtMigrationCode.setText("");
	        PBGeneralUtils.showAlertDialogNoTitleNoIconWithOnClick(PBSettingMigrationCodeVerify.this, 
	        		""+getString(R.string.pb_alert_own_migration_code_insert_kinshi) +"\n", 
	        		getString(R.string.dialog_ok_btn),
	        		mOnClickOkDialogForOwnMigrationCodeAlert);
        	return;
		}
		
        
		new Thread() {
			@Override
        	public void run(){				
				handlerMigrationCodeVerifyIntent.sendEmptyMessage(MSG_SEND_MIGRATION_CODE);
        	}
		}.start();
	}
	
    private Handler handlerMigrationCodeVerifyIntent = new Handler() {
    	@Override
    	public void handleMessage(Message msg){
    		switch (msg.what) {
    		case MSG_SEND_MIGRATION_CODE:
    			mDoneButton.setClickable(true);
    			startInitialLoadingDataProgress(mLvLoadingWaiting);
    			break;
    		default:
    			break;
    		}
    	};
    };
	

	@Override
	protected void handleHomeActionListener() {
		finish();
	}

	@Override
	protected void setLeftBtnsDetailHeader(ActionBar headerBar) {		
	}
	
	/**
	 * Async task class
	 * for started migration code verification 
	 * use API  "https://"+API_HOST+"/2/migration/verify"
	 * @author atikur
	 *
	 */
	private class PBTaskMigrationCodeVerify extends AsyncTask<Void, Void, Void> {
		
		boolean result200Ok = false;
		boolean result400 = false;
		boolean resultOtherError = false;
		
		String message = null;
		@Override
		protected Void doInBackground(Void... params) {
			String token = PBPreferenceUtils.getStringPref(getApplicationContext(), PBConstant.PREF_NAME,
					PBConstant.PREF_NAME_TOKEN, "");
			// TODO need migration code from edit text
			if(TextUtils.isEmpty(token))
				return null;
			
			String ownMigrationCode = PBPreferenceUtils.getStringPref(
					PBApplication.getBaseApplicationContext(),
					PBConstant.PREF_NAME, PBConstant.PREF_MIGRATON_CODE, "");
			//TODO Atik need to get value from shared preference
			String migrationCode = mEdtMigrationCode.getText().toString();
			// Save migration code to shared preference before executing 
	        PBPreferenceUtils.saveStringPref(
	        		cxt, 
	                PBConstant.PREF_NAME, 
	                PBConstant.PREF_MIGRATON_CODE, 
	                ownMigrationCode);
	        
	        
	        PBPreferenceUtils.saveStringPref(
	        		cxt, 
	                PBConstant.PREF_NAME, 
	                PBConstant.PREF_MIGRATON_CODE_INPUT, 
	                migrationCode);
	        
			Log.d("MIGRATION_CODE", "Called in geting - token:"+token);
			Log.d("MIGRATION_CODE", "Called in geting - migration code:"+migrationCode);
			
			System.out.println("atik Called in geting - migration code:"+migrationCode);
			
			Response res = PBAPIHelper.migrationCodeVerification(token,migrationCode);
			System.out.println("atik response:"+res.errorCode);
			Log.d("MIGRATION_CODE", "res: "+res.errorCode + " "+res.decription);
			if(res!=null){
				if(res.errorCode == ResponseHandle.CODE_200_OK){

					result200Ok  = true;
					message = res.decription;
					
					System.out.println("atik MIGRATION_CODE_VERIFICATION_SERVER"+"200 OK:"+res.decription);
					try {
						JSONObject result = new JSONObject(res.decription);
						if(result.has("message")) {
							System.out.println("200 OK message is:"+result.getString("message"));
							message_response = result.getString("message");
							
						} 
						if(result.has("result")) {
							resultCode  = "Result Code:"+ result.getString("result");
							System.out.println("200 OK Result code is:"+resultCode);
						}
						
						if(result.has("honey_count")) {
							honeyCount   = getString(R.string.pb_alert_migration_honey_count)+ result.getString("honey_count");
							System.out.println("200 OK honey count is:"+honeyCount);
						}
						
						if(result.has("acorn_count")) {
							acornCount   = getString(R.string.pb_alert_migration_acorn_count)+ result.getString("acorn_count");
							System.out.println("200 OK accorn count is:"+acornCount);
						}
						
						if(result.has("maple_count")) {
							mapleCount  = getString(R.string.pb_alert_migration_mapple_count)+ result.getString("maple_count");
							System.out.println("200 OK mapple count is:"+mapleCount);
						}
						
						if(result.has("goldacorn_count")) {
							goldacornCcount   = getString(R.string.pb_alert_migration_goldacorn_count)+ result.getString("goldacorn_count");
							System.out.println("200 OK goldcorn count is:"+goldacornCcount);
						}
						
						

					} catch (JSONException e) {
						System.out.println("MIGRATION_CODE"+" Json parse exception occured");
					}

					
				}else if(res.errorCode == ResponseHandle.CODE_400){
					result400  = true;
					try {
						JSONObject result = new JSONObject(res.decription);
						if(result.has("message")) {
							System.out.println("400 error message is:"+result.getString("message"));
							message = result.getString("message");
							
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					System.out.println("MIGRATION_CODE"+":400:"+res.decription);
				} else {
					//  TODO other error occured
				}
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			
			if(result200Ok) {
				updateUISuccessfull(message);	
			} else if (result400) {
				updateUI400(message);	
			} else {
				updateUIWhenOtherError("");
			}
			
		}
		
	}	
		/**
		 * Async task class
		 * for started migration code verification 
		 * use API  "https://"+API_HOST+"/migration/verify"
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
        				PBConstant.PREF_NAME, PBConstant.PREF_MIGRATON_CODE, "");
        		
        		if(TextUtils.isEmpty(migrationCode)) {
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
								resultCode_after_start_migration  = "Result Code:"+ result.getString("result");
								System.out.println("200 OK Result code is:"+resultCode);
							}
							
							

						} catch (JSONException e) {
							System.out.println("MIGRATION_CODE"+" Json parse exception occured");
						}
						
					}else if(res.errorCode == ResponseHandle.CODE_400){
						result400 = true;
						message = res.decription;
						
					} else {
				
					}
				}
				return null;
			}
			
			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				

				
			}
	}
	
	
	/**
	 * Async task class
	 * for started data migration
	 * use API  "https://"+API_HOST+"/2/migration/verified"
	 * @author atikur
	 *
	 */
	private class PBTaskStartDataMigration extends AsyncTask<Void, Void, Void>{
		
		boolean result200Ok = false;
		boolean result400 = false;
		
		String message = null;
		@Override
		protected Void doInBackground(Void... params) {
			String token = PBPreferenceUtils.getStringPref(getApplicationContext(), PBConstant.PREF_NAME,
					PBConstant.PREF_NAME_TOKEN, "");
		
			
			// TODO need migration code from edit text
			if(TextUtils.isEmpty(token))
				return null;
			
			// Need to collect code from shared preference
			String migrationCode = PBPreferenceUtils.getStringPref(
					PBApplication.getBaseApplicationContext(),
					PBConstant.PREF_NAME, PBConstant.PREF_MIGRATON_CODE_INPUT, "");
			
			
			
			Log.d("MIGRATION_CODE", "Called in geting - token start migration:"+token);
			Log.d("MIGRATION_CODE", "Called in geting - migration code start migration:"+migrationCode);
			
			System.out.println("atik Called in geting - migration code start migration:"+migrationCode);
			
			Response res = PBAPIHelper.startDataMigration(token,migrationCode); // 
			
			System.out.println("atik response data migration:"+res.errorCode);
			Log.d("MIGRATION_CODE", "res: "+res.errorCode + " "+res.decription);
			if(res!=null){
				if(res.errorCode == ResponseHandle.CODE_200_OK){

					result200Ok  = true;
					message = res.decription;
					
					System.out.println("atik MIGRATION_CODE_VERIFICATION_SERVER"+"200 OK:"+res.decription);
					try {
						JSONObject result = new JSONObject(res.decription);
						if(result.has("message")) {
							System.out.println("200 OK message is:"+result.getString("message"));
							message_response_after_migration = result.getString("message");
							
						} 
						if(result.has("result")) {
							resultCode_after_start_migration  = "Result Code:"+ result.getString("result");
							System.out.println("200 OK Result code is:"+resultCode);
						}
						
						

					} catch (JSONException e) {
						System.out.println("MIGRATION_CODE"+" Json parse exception occured");
					}

					
				}else if(res.errorCode == ResponseHandle.CODE_400){
					//result400  = true;
					try {
						JSONObject result = new JSONObject(res.decription);
						if(result.has("message")) {
							System.out.println("400 error message is:"+result.getString("message"));
							message_response_after_migration = result.getString("message");
							
						}
						if(result.has("result")) {
							System.out.println("400 error result code is:"+result.getString("result"));
							resultCode_after_start_migration = "Result Code:" +result.getString("result");
							
						}
					} catch (JSONException e) {
						System.out.println("JSON parse exception");
					}

					System.out.println("MIGRATION_CODE"+":400:"+res.decription);
				} else {
					//  TODO other error occured
				}
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);	
			
			if(result200Ok) {
				dataMigrationDone();	
			} else if (result400) {
				dataMigrationError();	
			} else {
				dataMigrationOtherError();
			}
			
		}

		
	}
	
	// Update UI when received merge code successfully
	private void updateUISuccessfull(final String message) {

			final Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
			    @Override
			    public void run() {
			        // Delay dialog display after 1s = 1000ms
					if (mLvLoadingWaiting != null) {
						mLvLoadingWaiting.setVisibility(View.GONE);
					}

			        PBGeneralUtils.showAlertDialogActionWithoutTitle(PBSettingMigrationCodeVerify.this,
			        		message_response +"\n" 
			        		+"\n"+ honeyCount +"\n"+ acornCount +"\n"+ mapleCount
							+"\n"+ goldacornCcount +"\n" +  "\n" + getString(R.string.pb_data_migration_start),
							getString(R.string.dialog_ok_btn),
							getString(R.string.dialog_cancel_btn),
							mOnClickOkDialogMigrationVerified
			        		);
			    }
			}, 1000);

	}
	
	// Update UI when received merge code successfully
	private void dataMigrationDone() {

			final Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
			    @Override
			    public void run() {
			        // Delay dialog display after 1s = 1000ms
					if (mLvLoadingWaiting != null) {
						mLvLoadingWaiting.setVisibility(View.GONE);
					}
			        
					
					//TODO Atik need to set migration code here
					
					// Need to collect code from shared preference
					String migrationCode = PBPreferenceUtils.getStringPref(
							PBApplication.getBaseApplicationContext(),
							PBConstant.PREF_NAME, PBConstant.PREF_MIGRATON_CODE, "");
					
			        PBPreferenceUtils.saveStringPref(
			        		cxt, 
			                PBConstant.PREF_NAME, 
			                PBConstant.PREF_MIGRATON_CODE_SUCCESS, 
			                migrationCode);
			        
			        /*PBGeneralUtils.showAlertDialogNoTitleWithOnClick(PBSettingMigrationCodeVerify.this, 
			        		message_response_after_migration +"\n" +"\n"+ resultCode_after_start_migration);*/
			        PBGeneralUtils.showAlertDialogNoTitleNoIconWithOnClick(PBSettingMigrationCodeVerify.this, 
			        		message_response_after_migration +"\n", 
			        		getString(R.string.dialog_ok_btn),
			        		mOnClickOkDialogDataMigrationDone);
			    }
			}, 1000);

	}
	
	// Update UI when received merge code successfully
	private void dataMigrationError() {

			final Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
			    @Override
			    public void run() {
			        // Delay dialog display after 1s = 1000ms
					if (mLvLoadingWaiting != null) {
						mLvLoadingWaiting.setVisibility(View.GONE);
					}
			        
			        PBGeneralUtils.showAlertDialogNoTitleNoIconWithOnClick(PBSettingMigrationCodeVerify.this, 
			        		message_response_after_migration +"\n", 
			        		getString(R.string.dialog_ok_btn),
			        		mOnClickOkDialogDataMigrationDone);
			    }
			}, 1000);

	}
	
	// Update UI when received merge code successfully
	private void dataMigrationOtherError() {

			final Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
			    @Override
			    public void run() {
			        // Delay dialog display after 1s = 1000ms
					if (mLvLoadingWaiting != null) {
						mLvLoadingWaiting.setVisibility(View.GONE);
					}
			        
			        PBGeneralUtils.showAlertDialogNoTitleWithOnClick(PBSettingMigrationCodeVerify.this, 
			        		getString(R.string.pb_data_migration_other_error));
			    }
			}, 1000);

	}
	
	// Update UI when received response code 400
	private void updateUI400(final String message) {
	
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
		    @Override
		    public void run() {
		        // Delay dialog display after 1s = 1000ms
				if (mLvLoadingWaiting != null) {
					mLvLoadingWaiting.setVisibility(View.GONE);
				}
				PBGeneralUtils.showAlertDialogActionWithOnClick(PBSettingMigrationCodeVerify.this, 
						PBSettingMigrationCodeVerify.this.getString(R.string.pb_title_migration_code_verification_error), 
						message,
						PBSettingMigrationCodeVerify.this.getString(R.string.dialog_ok_btn),
						mOnClickOkDialog);
		    }
		}, 1000);
		

		
	}
	
	private DialogInterface.OnClickListener mOnClickOkDialog = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
		}
	};
	
	private DialogInterface.OnClickListener mOnClickOkDialogMigrationVerified = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {

			// atik Call start data migration task
			PBTaskStartDataMigration task = new PBTaskStartDataMigration();
			task.execute();
			dialog.dismiss();
		}
	};
	
	private DialogInterface.OnClickListener mOnClickOkDialogDataMigrationDone = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {

			finish();
		}
	};
	
	private DialogInterface.OnClickListener mOnClickOkDialogForOwnMigrationCodeAlert = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {

			dialog.dismiss();
		}
	};
	
	// Update UI when received merge code successfully
	private void updateUIWhenOtherError(String message) {

		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
		    @Override
		    public void run() {
		        // Delay dialog display after 1s = 1000ms
				if (mLvLoadingWaiting != null) {
					mLvLoadingWaiting.setVisibility(View.GONE);
				}
				PBGeneralUtils.showAlertDialogActionWithOnClick(PBSettingMigrationCodeVerify.this, 
						PBSettingMigrationCodeVerify.this.getString(R.string.pb_title_migration_code_verification_error), 
						PBSettingMigrationCodeVerify.this.getString(R.string.pb_content_migration_code_error),
						PBSettingMigrationCodeVerify.this.getString(R.string.dialog_ok_btn),
						mOnClickOkDialog);
		    }
		}, 1000);

	}

	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mInitialLoadingThread != null) {
			try {
				mInitialLoadingThread.interrupt();
				mInitialLoadingThread = null;
			} catch (Exception e) {
				Log.w(PBConstant.TAG, e.getMessage());	}
		}}

}
	
