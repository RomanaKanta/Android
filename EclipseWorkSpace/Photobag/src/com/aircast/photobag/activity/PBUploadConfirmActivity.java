package com.aircast.photobag.activity;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.aircast.photobag.R;
import com.aircast.photobag.api.PBAPIHelper;
import com.aircast.photobag.api.ResponseHandle;
import com.aircast.photobag.api.ResponseHandle.Response;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.services.UploadServiceUtils;
import com.aircast.photobag.utils.PBGeneralUtils;
import com.aircast.photobag.utils.PBPreferenceUtils;
import com.aircast.photobag.widget.FButton;
import com.aircast.photobag.widget.actionbar.ActionBar;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.Holder;
import com.orhanobut.dialogplus.OnBackPressListener;
import com.orhanobut.dialogplus.OnDismissListener;
import com.orhanobut.dialogplus.ViewHolder;

/**
 * "You sure want to upload with this password （´ ・(ｪ)・` ）クマ ?"
 * */
public class PBUploadConfirmActivity extends PBAbsActionBarActivity
		implements OnClickListener {
	
	
    private final String TAG = "PBUploadConfirmActivity";
    private ArrayList<String> mSelectedMedia;
    private long[] mSelectedTypeMedia;
    private String mPassword, mLongPas;
    private String mSavePassword;

    private boolean isPasswordPublic = false;
    private TextView mChoosenPassword;
    private FButton mConfirmPassBtn, mMakeLongPassBtn;
    private String mToken;
    private String mCollectionId;
    private boolean mIsActivityDestroyed = false;
    private final int UPDATE_LONG_PASSWORD = 0;
    boolean passwordDifficultFistTime = false;
	private String mPasswordForAgain;
    private Response mErrorDescription = null;
    private EditText mFourDigitTextBox;
    private boolean isSecretCode = false;
    private final int SHOW_ERROR_DIALOG = 1;
    private Handler mHandler = new Handler(new Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
            
            case UPDATE_LONG_PASSWORD:
            	System.out.println("Atik called after post exectuion");
                mChoosenPassword.setText(mPassword);                
                break;
            case SHOW_ERROR_DIALOG:
            	System.out.println("Atik enter into error dialog");
                if (mErrorDescription == null)
                    return false;
                /*if(mLvWaitingLayout != null){
                    mLvWaitingLayout.setVisibility(View.GONE);
                }*/
                if (!mIsActivityDestroyed) {
                PBGeneralUtils.showAlertDialogActionWithOnClick(
                    		PBUploadConfirmActivity.this, 
                        android.R.drawable.ic_dialog_alert,
                        getString(R.string.dialog_error_title),
                        ResponseHandle.getNotifyStringErrorRelatedPassword(
                                mErrorDescription.errorCode, mErrorDescription.decription),
                                getString(R.string.upload_confirm_pass_ok_btn),
                                new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                // @lent fix issue Bug: B-03 screen: delete password text
                                /*if (mErrorDescription.decription.contains("password")
                                        && mPasswordInput != null) {
                                    mPasswordInput.setText("");
                                }*/
                            	
                                dialog.dismiss();
                            }
                        });
                }
                break;  

            default:
                break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIsActivityDestroyed = false;
        Intent intent = getIntent();
        mPassword = intent.getStringExtra(PBConstant.INTENT_PASSWORD);
        mSavePassword = intent.getStringExtra(PBConstant.INTENT_PASSWORD);;
        mPasswordForAgain = intent.getStringExtra(PBConstant.INTENT_PASSWORD);
        mLongPas = intent.getStringExtra(PBConstant.INTENT_LONG_PASSWORD);
        mSelectedMedia = intent
        .getStringArrayListExtra(PBConstant.INTENT_SELECTED_MEDIA);
        mSelectedTypeMedia = intent
        .getLongArrayExtra(PBConstant.INTENT_SELECTED_MEDIA_TYPE);
        isPasswordPublic = intent.getBooleanExtra(PBConstant.INTENT_IS_PUBLIC_PASSWORD, false);
        
        setContentView(R.layout.pb_upload_confirm_password_layout);
        // @lent5 add action bar #S
        ActionBar headerBar = (ActionBar) findViewById(R.id.headerbar);
        setHeader(headerBar, getString(R.string.upload_confirm_pass_title));
        // @lent5 add action bar #E

        mChoosenPassword = (TextView) findViewById(R.id.choosen_password_textview);
        mChoosenPassword.setText(mPassword);
        mConfirmPassBtn = (FButton) findViewById(R.id.btn_confirm_password);
        mConfirmPassBtn.setOnClickListener(this);
        mMakeLongPassBtn = (FButton) findViewById(R.id.btn_lock);
        mMakeLongPassBtn.setOnClickListener(this);
        // Disable make password difficult button
        if(isPasswordPublic) {
        	mMakeLongPassBtn.setVisibility(View.GONE);
        }
      
        // @lent add progress bar
        mLvWaiting = (LinearLayout) findViewById(R.id.ll_loading_panel_waiting);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsActivityDestroyed = false;
        // @lent add progress bar
        mLvWaiting.setVisibility(View.GONE);

        // check internet
        UploadServiceUtils.checkInternetConenction(
                PBUploadConfirmActivity.this, (!mIsActivityDestroyed), true);
    }

    // @lent add progress bar
    private LinearLayout mLvWaiting;

    @Override
    public void onClick(View v) {
		switch (v.getId()) {
        case R.id.btn_confirm_password:

            mLvWaiting.setVisibility(View.VISIBLE);
            mConfirmPassBtn.setEnabled(false);

            boolean connected = UploadServiceUtils.checkInternetConenction(
                                PBUploadConfirmActivity.this, 
                                (!mIsActivityDestroyed), false);
            if (connected) {
            	mCollectionId = PBPreferenceUtils.getStringPref(getBaseContext(),
            			PBConstant.UPLOAD_SERVICE_PREF, PBConstant.PREF_COLLECTION_ID, "");
                mToken = PBPreferenceUtils.getStringPref(getBaseContext(),
                        PBConstant.PREF_NAME, PBConstant.PREF_NAME_TOKEN, "");
                new SetPasswordTask().execute();
            } else {
                mLvWaiting.setVisibility(View.GONE);
                mConfirmPassBtn.setEnabled(true);
            }
            break;
        case R.id.btn_lock:
        	Holder holder = new ViewHolder(
					R.layout.upload_password_four_digit_dialog);
			// mEdtPwdSecretDigit.setVisibility(View.VISIBLE);
			
        	isSecretCode = true;
        	final DialogPlus	 dialogPlus = DialogPlus
					.newDialog(PBUploadConfirmActivity.this)
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
									
									dialog.dismiss();

								}
							})
					// .setOnCancelListener(cancelListener)
					// .setExpanded(expanded)
					.setCancelable(true).create();
        	
			dialogPlus.show();
        	
        	  mFourDigitTextBox = (EditText) dialogPlus.findViewById(R.id.editText_upload_four_digit_input);
        	  mFourDigitTextBox.setHint("数字４桁を入力");
//        	  mFourDigitTextBox.addTextChangedListener(new TextWatcher() {
//				
//				@Override
//				public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
//					// TODO Auto-generated method stub
//					
//				}
//				
//				@Override
//				public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
//						int arg3) {
//					// TODO Auto-generated method stub
//					
//				}
//				
//				@Override
//				public void afterTextChanged(Editable arg0) {
//
//			        if(mFourDigitTextBox.getText().toString().length() == 4){
//			        	dialogPlus.findViewById(R.id.button_st_dialog_ok).setEnabled(true);
//		        }else{
//		        	
//		        	dialogPlus.findViewById(R.id.button_st_dialog_ok).setEnabled(false);
//		        }
//					
//				}
//			});
        	  dialogPlus.findViewById(R.id.button_st_dialog_cancle).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {

					dialogPlus.dismiss();
					isSecretCode = false;
					
				}
			});
        	  
        	  mFourDigitTextBox.setOnEditorActionListener(new OnEditorActionListener() {
        	        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        	            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
        	            	
        	            	hideSoftKeyboard();
//        	            
        	            }    
        	            return false;
        	        }
        	    });
        	  dialogPlus.findViewById(R.id.button_st_dialog_ok).setOnClickListener(new OnClickListener() {
  				
  				@Override
  				public void onClick(View v) {

  					dialogPlus.dismiss();
//  					if(mFourDigitTextBox.getText().toString().equals("")){
//  		    			
//  		    			Toast.makeText(getApplicationContext(), "Please set a password, or remove the check box.", Toast.LENGTH_SHORT).show();
//  		    			return;
//  		    		}
  		    		if(mFourDigitTextBox.getText().toString().length() < 4){
  		    			
  		    			Toast.makeText(getApplicationContext(), "4桁の数字を入力してください。", Toast.LENGTH_SHORT).show();
  		    			return;
  		    		}
  		        	
  		    		 mLvWaiting.setVisibility(View.VISIBLE);
  		             mConfirmPassBtn.setEnabled(false);
  		             boolean connect = UploadServiceUtils.checkInternetConenction(
  		                     PBUploadConfirmActivity.this, 
  		                     (!mIsActivityDestroyed), false);
  		             if (connect) {
  		             		
  		            		 PBPreferenceUtils.saveStringPref(getBaseContext(),
  		                             PBConstant.UPLOAD_SERVICE_PREF,
  		                             PBConstant.PREF_FOUR_DIGIT, mFourDigitTextBox.getText().toString());
  		            	 
  		             	mCollectionId = PBPreferenceUtils.getStringPref(getBaseContext(),
  		             			PBConstant.UPLOAD_SERVICE_PREF, PBConstant.PREF_COLLECTION_ID, "");
  		                 mToken = PBPreferenceUtils.getStringPref(getBaseContext(),
  		                         PBConstant.PREF_NAME, PBConstant.PREF_NAME_TOKEN, "");
  		                 new SetPasswordTask().execute();
  		             } else {
  		                 mLvWaiting.setVisibility(View.GONE);
  		                 mConfirmPassBtn.setEnabled(true);
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
		imm.hideSoftInputFromWindow(mFourDigitTextBox.getWindowToken(), 0);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}
    private static String mResponseDescription;
    private static int mResponseError;
    private Response mResponse;

    private class SetPasswordTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
        	
        	if(isSecretCode){
        		
        		 mResponse = PBAPIHelper.setPassword(mPassword, mCollectionId, mToken,true,mFourDigitTextBox.getText().toString());
        	}else{
        		 mResponse = PBAPIHelper.setPassword(mPassword, mCollectionId, mToken,false,"");
        		
        	}
           
            if (mResponse != null) {
                mResponseDescription = mResponse.decription;
                mResponseError = mResponse.errorCode;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // go to uploading screen
            super.onPostExecute(result);
            // @lent add progress bar
            mLvWaiting.setVisibility(View.GONE);
            mConfirmPassBtn.setEnabled(true);

            if (mResponseError != ResponseHandle.CODE_200_OK) { // Handle error here
                Log.d(TAG, "[set password] " + mResponseDescription);
                String errorNotifyMes = ResponseHandle
                .getNotifyStringErrorRelatedPassword(
                        mResponse.errorCode, mResponse.decription);

               // mMakeLongPassBtn.setEnabled(false);
                if (!mIsActivityDestroyed) {
                    PBGeneralUtils.showAlertDialogActionWithOnClick(
                            PBUploadConfirmActivity.this,
                            android.R.drawable.ic_dialog_alert,
                            getString(R.string.dialog_error_title),
                            errorNotifyMes,
                            getString(R.string.upload_confirm_pass_ok_btn),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                }
            } else { // set password successful
                String passwordId = null;
                JSONObject jObject;
                if (!TextUtils.isEmpty(mResponseDescription)
                        && mResponseDescription.contains("collection")) {
                    try {
                        jObject = new JSONObject(mResponseDescription);
                        if (jObject != null) {
                            if (jObject.has("collection")) {
                                JSONObject jObjCollection = jObject
                                .getJSONObject("collection");
                                if (jObjCollection.has("id")) {
                                    passwordId = jObjCollection.getString("id");
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                PBPreferenceUtils.saveStringPref(getBaseContext(),
                        PBConstant.UPLOAD_SERVICE_PREF,
                        PBConstant.PREF_COLLECTION_ID, passwordId);
                PBPreferenceUtils.saveStringPref(getBaseContext(),
                        PBConstant.UPLOAD_SERVICE_PREF,
                        PBConstant.PREF_UPLOAD_PASS, mPassword);
                PBPreferenceUtils.saveStringPref(getBaseContext(),
                        PBConstant.UPLOAD_SERVICE_PREF,
                        PBConstant.PREF_PUBLIC_COLLECTION_ID, passwordId);
                
                startSelectAddibilityActivity(mPassword, passwordId, mSelectedMedia, mSelectedTypeMedia);
            }
        }
    }
    
    private void startSelectAddibilityActivity(String password, String passwordId, ArrayList<String> selectedImages, long[] selectedTypeImages){
        Intent intent = new Intent(getBaseContext(), PBUploadSelectAddibilityActivity.class);
        intent.putExtra(PBConstant.INTENT_PASSWORD, password/*mPassword*/);
        intent.putExtra(PBConstant.INTENT_PASSWORD_ID, passwordId/*passwordId*/);
        intent.putExtra(PBConstant.INTENT_SELECTED_MEDIA, selectedImages/*mSelectedImages*/);
        intent.putExtra(PBConstant.INTENT_SELECTED_MEDIA_TYPE, selectedTypeImages/*mSelectedImages*/);
        intent.putExtra(PBConstant.INTENT_LONG_PASSWORD, mLongPas);
        if(isPasswordPublic) {
            intent.putExtra(PBConstant.INTENT_IS_PUBLIC_PASSWORD, true);
        }
        
        startActivity(intent);
        finish();
    }

    @Override
    protected void handleHomeActionListener() {
        
    		Intent intent = new Intent(getBaseContext(),
                    UploadSetPasswordActivity.class);
            intent.putStringArrayListExtra(PBConstant.INTENT_SELECTED_MEDIA,
                    mSelectedMedia);
            intent.putExtra(PBConstant.INTENT_SELECTED_MEDIA_TYPE,
                    mSelectedTypeMedia);
            intent.putExtra(PBConstant.INTENT_PASSWORD, mPassword);
            intent.putExtra(PBConstant.INTENT_LONG_PASSWORD, mLongPas);
            startActivity(intent);
            finish();

    }

    @Override
    protected void setLeftBtnsDetailHeader(ActionBar headerBar) {
        // Do nothing here
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIsActivityDestroyed = true;
    }

    @Override
    protected void onStop() {
        mIsActivityDestroyed = true;
        super.onStop();
    }
    private AlertDialog.Builder passwordConfirmDialog;
    private AlertDialog.Builder passwordConfirmDialogInitial;
    private class ConfirmPasswordTaskAgain extends AsyncTask<Void, Void, Void> {

    	@Override
    	protected void onPreExecute() {
    		//mProgressHUD.show(true);
    		//mHandler.sendEmptyMessage(DISABLE_MAKEPASSWORD_DIFFICULT_BUTTON);
    		super.onPreExecute();
    	}
    	
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            
            if(mErrorDescription != null) {
            	mHandler.sendEmptyMessage(SHOW_ERROR_DIALOG);
            } else {
               // System.out.println("Atik New long password is:"+mLongPas);
            	int alphaLen = mLongPas.length();
            	if(alphaLen > PBConstant.PASSWORD_LENGTH){
            		mPassword = mLongPas.substring(0, PBConstant.PASSWORD_LENGTH);
            	}else{
            		mPassword = mLongPas;
            	}

                if (!mIsActivityDestroyed) {
                	
                	passwordConfirmDialog = new AlertDialog.Builder(
        					new ContextThemeWrapper(PBUploadConfirmActivity.this,
        							R.style.popup_theme));
                	passwordConfirmDialog.setTitle(getString(R.string.dialog_change_pass_title));
                	passwordConfirmDialog.setMessage("[ " + mPassword + " ]");
                	passwordConfirmDialog.setCancelable(false);
                	passwordConfirmDialog.setPositiveButton(
        					getString(R.string.dialog_ok_btn),
        					new DialogInterface.OnClickListener() {
        						@Override
        						public void onClick(DialogInterface dialog, int which) {
						        	 mChoosenPassword.setText(mPassword);
        							// Execute below code after 1 seconds have passed
        						    Handler handler = new Handler(); 
        						    handler.postDelayed(new Runnable() { 
        						         public void run() { 
        						        	 //System.out.println("Inside the thread");
        						        	 mChoosenPassword.setText(mPassword);
        						        	 mMakeLongPassBtn.setTextColor(getResources().getColor(R.color.white));
        						        	 mMakeLongPassBtn.setEnabled(true); 
        						         } 
        						    }, 1000);
        							//mHandler.sendEmptyMessage(UPDATE_LONG_PASSWORD_REPEAT);
        							//dialog.dismiss();
        						}
        					});
                	
                	passwordConfirmDialog.show();
        			
                    
                }
                
            }

        }

        @Override
        protected Void doInBackground(Void... params) {
        	mCollectionId = PBPreferenceUtils.getStringPref(getBaseContext(),
        			PBConstant.UPLOAD_SERVICE_PREF, PBConstant.PREF_COLLECTION_ID, "");
            mToken = PBPreferenceUtils.getStringPref(getBaseContext(),
                    PBConstant.PREF_NAME, PBConstant.PREF_NAME_TOKEN, "");
            Response result = PBAPIHelper.confirmPassword(mPasswordForAgain, mCollectionId, mToken);
            if (result != null) {
                if (result.errorCode == ResponseHandle.CODE_200_OK) {// password
                    // is valid
                    String jsonResult = result.decription;
                    mLongPas = parseLongPassword(jsonResult);

                    
                } else {// Handle error
                    // mErrorDescription = result.decription;
                    mErrorDescription = result;
                   // mHandler.sendEmptyMessage(SHOW_ERROR_DIALOG);
                }
            }
            return null;
        }

        private String parseLongPassword(String jsonResult) {
            JSONObject jObject;
            if (!TextUtils.isEmpty(jsonResult) 
                    && jsonResult.contains("longer_password")) {
                try {
                    jObject = new JSONObject(jsonResult);
                    if (jObject != null && jObject.has("longer_password")) {
                            return jObject.getString("longer_password");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        
    }
}
