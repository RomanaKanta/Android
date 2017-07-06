package com.aircast.photobag.activity;

import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

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
public class PBSettingGetMigrationCode extends PBAbsActionBarActivity
implements OnClickListener {
	
    private FButton mDoneButton;    
    private static final int MSG_SEND_MIGRATION_CODE = 1;
    private static String urlForPC;
    private TextView migrationCode;
    private Context cxt;
	private View mLvLoadingWaiting;
	private Handler backgroundHandler = new Handler();

    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pb_layout_setting_migration_code_confirm);
        
        ActionBar headerBar = (ActionBar) findViewById(R.id.headerbar);
        setHeader(headerBar, getString(R.string.pb_setting_my_migration_code));
        
        cxt = PBApplication.getBaseApplicationContext();
        
        mLvLoadingWaiting = findViewById(R.id.migration_code_loading_panel_waiting);
		if (mLvLoadingWaiting != null) {
			mLvLoadingWaiting.setVisibility(View.GONE);
		}
		startInitialLoadingDataProgress(mLvLoadingWaiting);

        migrationCode = (TextView) findViewById(R.id.own_merge_code);
        migrationCode.setVisibility(View.GONE);
        
        mDoneButton = (FButton) findViewById(R.id.btn_setting_sendMergeCodeByMail);
        mDoneButton.setEnabled(false);
        mDoneButton.setOnClickListener(this);
        
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
					PBTaskGetMigrationCode task = new PBTaskGetMigrationCode();
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
		new Thread() {
			@Override
        	public void run(){				
				handlerEmailIntent.sendEmptyMessage(MSG_SEND_MIGRATION_CODE);
        	}
		}.start();
	}
	
    private Handler handlerEmailIntent = new Handler() {
    	@Override
    	public void handleMessage(Message msg){
    		switch (msg.what) {
    		case MSG_SEND_MIGRATION_CODE:
    			mDoneButton.setClickable(true);
    			
    			Intent sendIntent = new Intent(Intent.ACTION_SEND);
    	    	sendIntent.putExtra(Intent.EXTRA_EMAIL, "");
    	    	

    	    	sendIntent.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.pb_setting_mail_title));
    	    	
    	    	// Get the merge code and set it as mail content atik
    	    	String mergeCode = "";
    	    	mergeCode = getString(R.string.pb_migration_code)+":"
    	    			+ migrationCode.getText().toString()+"\n"+"\n"+"\n"+
    	    			getString(R.string.pb_mail_migration_code_1)+"\n"+
    	    			getString(R.string.pb_mail_migration_code_2)+"\n"+
    	    			getString(R.string.pb_mail_migration_code_3)+"\n"+
    	    			getString(R.string.pb_mail_migration_code_4);
    	    	
    			if (!TextUtils.isEmpty(mergeCode)) {
    				sendIntent.putExtra(Intent.EXTRA_TEXT, mergeCode);
    			}
    			else {
    				sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.pb_setting_mailtext_error));
    			}
    	    	
    	    	// for real phone
    	    	sendIntent.setType(PBConstant.INTENT_TYPE_SENDMAIL);
    	    	// for test in emulator
    	    	//sendIntent.setType(PBConstant.INTENT_TYPE_SENDMAIL_TEST);
    	    	
    	        PackageManager packageManager = cxt.getPackageManager();
    	        List<ResolveInfo> list = packageManager.queryIntentActivities(sendIntent,
    	                PackageManager.GET_ACTIVITIES);
    	        if (list.size() != 0){    	
    	    	    startActivity(sendIntent);
    	        }
    			
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
	 * for getting migration code from server 
	 * use API  "https://"+API_HOST+"/2/migration"
	 * @author atikur
	 *
	 */
	private class PBTaskGetMigrationCode extends AsyncTask<Void, Void, Void>{
		
		boolean result200Ok = false;
		boolean result400 = false;
		boolean resultOtherError = false;
		
		String mergeCode = null;
		@Override
		protected Void doInBackground(Void... params) {
			String token = PBPreferenceUtils.getStringPref(getApplicationContext(), PBConstant.PREF_NAME,
					PBConstant.PREF_NAME_TOKEN, "");
			
			if(TextUtils.isEmpty(token))
				return null;
			
			Log.d("MIGRATION_CODE", "Called in geting - token:"+token);
			Response res = PBAPIHelper.getMigrationCode(token);
			System.out.println("atik response:"+res.errorCode);
			
			Log.d("MIGRATION_CODE", "res: "+res.errorCode + " "+res.decription);
			if(res!=null){
				if(res.errorCode == ResponseHandle.CODE_200_OK){
					result200Ok  = true;
					// Need to save migration code to Shared preference
					mergeCode = res.decription;
					if(!TextUtils.isEmpty(mergeCode)) {
						// Save migration code to shared preference atik
				        PBPreferenceUtils.saveStringPref(
				        		cxt, 
				                PBConstant.PREF_NAME, 
				                PBConstant.PREF_MIGRATON_CODE, 
				                mergeCode);
					}
					// atik no need to save migration code to shared preference from here
					/*if(!TextUtils.isEmpty(mergeCode)) {
						// Save migration code to shared preference atik
				        PBPreferenceUtils.saveStringPref(
				        		cxt, 
				                PBConstant.PREF_NAME, 
				                PBConstant.PREF_MIGRATON_CODE, 
				                mergeCode);
					}*/
										
					System.out.println("MIGRATION_CODE"+"200 OK:"+res.decription);

					
				}else if(res.errorCode == ResponseHandle.CODE_400){
					result400  = true;
					mergeCode = res.decription;
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
				updateUISuccessfull(mergeCode);	
			} else if (result400) {
				updateUI400(mergeCode);	
			} else {
				updateUIWhenOtherError("");
			}
			
		}

		
	}
	
	
	// Update UI when received merge code successfully
	private void updateUISuccessfull(String mergeCode) {

		if (mLvLoadingWaiting != null) {
	        migrationCode.setVisibility(View.VISIBLE);
			mLvLoadingWaiting.setVisibility(View.GONE);
			mDoneButton.setEnabled(true);
		}
		
		// Set the migration code in textview
	    migrationCode.setText(mergeCode);

	}
	
	// Update UI when received response code 400
	private void updateUI400(String mergeCode) {
	
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
		    @Override
		    public void run() {
		        // Delay dialog display after 1s = 1000ms
				if (mLvLoadingWaiting != null) {
					mLvLoadingWaiting.setVisibility(View.GONE);
				}
				PBGeneralUtils.showAlertDialogActionWithOnClick(PBSettingGetMigrationCode.this, 
						PBSettingGetMigrationCode.this.getString(R.string.pb_title_migration_code_error), 
						PBSettingGetMigrationCode.this.getString(R.string.pb_content_migration_code_error),
						PBSettingGetMigrationCode.this.getString(R.string.dialog_ok_btn),
						mOnClickOkDialog);
		    }
		}, 1000);
		

		
	}
	
	private DialogInterface.OnClickListener mOnClickOkDialog = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			finish();
		}
	};
	
	// Update UI when received merge code successfully
	private void updateUIWhenOtherError(String mergeCode) {

		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
		    @Override
		    public void run() {
		        // Delay dialog display after 1s = 1000ms
				if (mLvLoadingWaiting != null) {
					mLvLoadingWaiting.setVisibility(View.GONE);
				}
				PBGeneralUtils.showAlertDialogActionWithOnClick(PBSettingGetMigrationCode.this, 
						PBSettingGetMigrationCode.this.getString(R.string.pb_title_migration_code_error), 
						PBSettingGetMigrationCode.this.getString(R.string.pb_content_migration_code_error),
						PBSettingGetMigrationCode.this.getString(R.string.dialog_ok_btn),
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
				Log.w(PBConstant.TAG, e.getMessage());
			}
		}
	}

}
