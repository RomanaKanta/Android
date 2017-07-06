package com.aircast.photobag.activity;
//package com.kayac.photobag.activity;
//
//import java.util.ArrayList;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import android.app.AlertDialog;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.os.Handler.Callback;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.ContextThemeWrapper;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.kayac.photobag.R;
//import com.kayac.photobag.activity.PBAbsActionBarActivity;
//import com.kayac.photobag.api.PBAPIHelper;
//import com.kayac.photobag.api.ResponseHandle;
//import com.kayac.photobag.api.ResponseHandle.Response;
//import com.kayac.photobag.application.PBConstant;
//import com.kayac.photobag.fragment.PBDownloadFragment;
//import com.kayac.photobag.services.UploadServiceUtils;
//import com.kayac.photobag.utils.PBGeneralUtils;
//import com.kayac.photobag.utils.PBPreferenceUtils;
//import com.kayac.photobag.widget.FButton;
//import com.kayac.photobag.widget.actionbar.ActionBar;
//
//public class PBFourDigitActivity extends PBAbsActionBarActivity
//		implements OnClickListener {
//	
//	
//    private final String TAG = "PBUploadConfirmActivity";
//    private ArrayList<String> mSelectedMedia;
//    private long[] mSelectedTypeMedia;
//    private String mPassword, mLongPas;
//    private String mSavePassword;
//
//    private boolean isPasswordPublic = false;
//    private TextView mChoosenPassword;
//    private FButton mConfirmPassBtn, mMakeLongPassBtn;
//    private String mToken;
//    private String mCollectionId;
//    private boolean mIsActivityDestroyed = false;
//    private final int UPDATE_LONG_PASSWORD = 0;
//    boolean passwordDifficultFistTime = false;
//	private String mPasswordForAgain;
//    private Response mErrorDescription = null;
//    
//    
//    private final int SHOW_ERROR_DIALOG = 1;
//    private final int DISABLE_MAKEPASSWORD_DIFFICULT_BUTTON = 2;
//    private final int UPDATE_LONG_PASSWORD_REPEAT = 3;
//
//    private ImageView mCheckBox;	
//	private boolean mIsConfirmed;
//	private EditText mFourDigitTextBox;
//
//    private Handler mHandler = new Handler(new Callback() {
//
//        @Override
//        public boolean handleMessage(Message msg) {
//            switch (msg.what) {
//            
//            case UPDATE_LONG_PASSWORD:
//            	System.out.println("Atik called after post exectuion");
//                mChoosenPassword.setText(mPassword);                
//                break;
//            case SHOW_ERROR_DIALOG:
//            	System.out.println("Atik enter into error dialog");
//                if (mErrorDescription == null)
//                    return false;
//                /*if(mLvWaitingLayout != null){
//                    mLvWaitingLayout.setVisibility(View.GONE);
//                }*/
//                if (!mIsActivityDestroyed) {
//                PBGeneralUtils.showAlertDialogActionWithOnClick(
//                		PBFourDigitActivity.this, 
//                        android.R.drawable.ic_dialog_alert,
//                        getString(R.string.dialog_error_title),
//                        ResponseHandle.getNotifyStringErrorRelatedPassword(
//                                mErrorDescription.errorCode, mErrorDescription.decription),
//                                getString(R.string.upload_confirm_pass_ok_btn),
//                                new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog,
//                                    int which) {
//                                // @lent fix issue Bug: B-03 screen: delete password text
//                                /*if (mErrorDescription.decription.contains("password")
//                                        && mPasswordInput != null) {
//                                    mPasswordInput.setText("");
//                                }*/
//                            	
//                                dialog.dismiss();
//                            }
//                        });
//                }
//                break;  
//
//            default:
//                break;
//            }
//            return false;
//        }
//    });
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        mIsActivityDestroyed = false;
//        Intent intent = getIntent();
//        mPassword = intent.getStringExtra(PBConstant.INTENT_PASSWORD);
//        mSavePassword = intent.getStringExtra(PBConstant.INTENT_PASSWORD);;
//        mPasswordForAgain = intent.getStringExtra(PBConstant.INTENT_PASSWORD);
//        mLongPas = intent.getStringExtra(PBConstant.INTENT_LONG_PASSWORD);
//        mSelectedMedia = intent
//        .getStringArrayListExtra(PBConstant.INTENT_SELECTED_MEDIA);
//        mSelectedTypeMedia = intent
//        .getLongArrayExtra(PBConstant.INTENT_SELECTED_MEDIA_TYPE);
//        isPasswordPublic = intent.getBooleanExtra(PBConstant.INTENT_IS_PUBLIC_PASSWORD, false);
//        
//        setContentView(R.layout.pb_upload_four_digit_koukaibukuro_setting_layout);
//        // @lent5 add action bar #S
//        ActionBar headerBar = (ActionBar) findViewById(R.id.headerbar);
//        setHeader(headerBar, getString(R.string.upload_confirm_pass_title));
//        // @lent5 add action bar #E
//
//        mChoosenPassword = (TextView) findViewById(R.id.textView_upload_koukai_password);
//        mChoosenPassword.setText(mPassword);
//        mConfirmPassBtn = (FButton) findViewById(R.id.btn_four_digit_confirm);
//        mConfirmPassBtn.setOnClickListener(this);
//        mMakeLongPassBtn = (FButton) findViewById(R.id.btn_lock);
//        mMakeLongPassBtn.setOnClickListener(this);
//        // Disable make password difficult button
//        if(isPasswordPublic) {
//        	mMakeLongPassBtn.setVisibility(View.GONE);
//        }
//
//        mFourDigitTextBox = (EditText) findViewById(R.id.editText_upload_four_digit_input);
//        mFourDigitTextBox.setEnabled(false);
//        // @lent add progress bar
//        mLvWaiting = (LinearLayout) findViewById(R.id.ll_loading_panel_waiting);
//        
//        mIsConfirmed  = false;
//        
//        mCheckBox = (ImageView) findViewById(R.id.icon_checkbox_confirm_koukaisetting);
//        mCheckBox.setOnClickListener(this);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        mIsActivityDestroyed = false;
//        // @lent add progress bar
//        mLvWaiting.setVisibility(View.GONE);
//
//        // check internet
//        UploadServiceUtils.checkInternetConenction(
//        		PBFourDigitActivity.this, (!mIsActivityDestroyed), true);
//    }
//
//    // @lent add progress bar
//    private LinearLayout mLvWaiting;
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//        case R.id.btn_four_digit_confirm:
//        	
//        	if(mIsConfirmed) {
//        		
//        		if(mFourDigitTextBox.getText().toString().equals("")){
//        			
//        			Toast.makeText(getApplicationContext(), "Please set a password, or remove the check box.", Toast.LENGTH_SHORT).show();
//        			return;
//        		}
//        		if(mFourDigitTextBox.getText().toString().length() < 4){
//        			
//        			Toast.makeText(getApplicationContext(), "You must enter 4 numeric characters.", Toast.LENGTH_SHORT).show();
//        			return;
//        		}
//        	}
//
//        	 mLvWaiting.setVisibility(View.VISIBLE);
//             mConfirmPassBtn.setEnabled(false);
// 
//             boolean connected = UploadServiceUtils.checkInternetConenction(
//             		PBFourDigitActivity.this, 
//                                 (!mIsActivityDestroyed), false);
//             if (connected) {
//            	 if(mIsConfirmed) {
//             		
//            		 PBPreferenceUtils.saveIntPref(getBaseContext(),
//                             PBConstant.UPLOAD_SERVICE_PREF,
//                             PBConstant.PREF_FOUR_DIGIT, Integer.valueOf(mFourDigitTextBox.getText().toString()));
//             	}
//            	 
//             	mCollectionId = PBPreferenceUtils.getStringPref(getBaseContext(),
//             			PBConstant.UPLOAD_SERVICE_PREF, PBConstant.PREF_COLLECTION_ID, "");
//                 mToken = PBPreferenceUtils.getStringPref(getBaseContext(),
//                         PBConstant.PREF_NAME, PBConstant.PREF_NAME_TOKEN, "");
//                 new SetPasswordTask(mIsConfirmed).execute();
//             } else {
//                 mLvWaiting.setVisibility(View.GONE);
//                 mConfirmPassBtn.setEnabled(true);
//             }
//
//            break;
//        case R.id.btn_lock:
//        	
//        	
//        	// Here add the Specific range as needed,MAX 36 characters
//        	int alphaLen = 0;
//        	if(!passwordDifficultFistTime) {
//        		//System.out.println("Atik tap make password difficult in first time");
//        		passwordDifficultFistTime = true;
//        		alphaLen = mLongPas.length();
//             	//int alphaLen = mLongPas.length();
//             	if(alphaLen > PBConstant.PASSWORD_LENGTH){
//             		mPassword = mLongPas.substring(0, PBConstant.PASSWORD_LENGTH);
//             	} else {
//             		mPassword = mLongPas;
//             	}
//                if (!mIsActivityDestroyed) {
//                	/*PBGeneralUtils.showAlertDialogActionWithOnClick(this,
//                			getString(R.string.dialog_change_pass_title), 
//                			"[ " + mPassword + " ]",
//                			getString(R.string.upload_confirm_pass_ok_btn),
//                			new DialogInterface.OnClickListener() {
//		                     @Override
//		                     public void onClick(DialogInterface dialog,
//		                             int which) {
//		                    	 mHandler.sendEmptyMessage(UPDATE_LONG_PASSWORD);
//		                         dialog.dismiss();
//		                         
//		                     }
//                     });*/
//                	
//                	passwordConfirmDialogInitial = new AlertDialog.Builder(
//        					new ContextThemeWrapper(PBFourDigitActivity.this,
//        							R.style.popup_theme));
//                	passwordConfirmDialogInitial.setTitle(getString(R.string.dialog_change_pass_title));
//                	passwordConfirmDialogInitial.setMessage("[ " + mPassword + " ]");
//                	passwordConfirmDialogInitial.setCancelable(false);
//                	passwordConfirmDialogInitial.setPositiveButton(
//        					getString(R.string.dialog_ok_btn),
//        					new DialogInterface.OnClickListener() {
//        						@Override
//        						public void onClick(DialogInterface dialog, int which) {
//        							mHandler.sendEmptyMessage(UPDATE_LONG_PASSWORD);
//   		                         	dialog.dismiss();
//        						}
//        					});
//                	
//                	passwordConfirmDialogInitial.show();
//                 }
//        	} else {
//        		//System.out.println("Atik tap make password difficult in from second time");
//        		//System.out.println("Atik button is disabled when tap make password ");
//        		mMakeLongPassBtn.setEnabled(false);
//        		mMakeLongPassBtn.setTextColor(getResources().getColor(R.color.gray));
//        		new ConfirmPasswordTaskAgain().execute();
//        	} 
//
//            break;
//            
// case R.id.icon_checkbox_confirm_koukaisetting:
//        	
//        	mIsConfirmed = !mIsConfirmed;
//        	mCheckBox.setImageResource(mIsConfirmed
//					? R.drawable.checkbox_red_on_flat
//					: R.drawable.checkbox_red_off_flat);
//        	
////        	mMakePasswordPublic.setButtonColor(mIsConfirmed?
////					   (getResources().getColor(R.color.flatbutton_default_color)):
////					   (getResources().getColor(R.color.fbutton_color_hard)));
//        	
//        	
//        	
//        	if(mIsConfirmed) {
//        		mFourDigitTextBox.setEnabled(true);
////        		mMakePasswordPublic.setEnabled(true);
////        		mMakePasswordNotPublic.setEnabled(false);
////        		mMakePasswordNotPublic.setBackgroundColor(getResources().getColor(R.color.fbutton_color_hard));
//        	} else {
////        		mMakePasswordPublic.setEnabled(false);
////        		mMakePasswordNotPublic.setEnabled(true);
////        		mMakePasswordNotPublic.setBackgroundColor(getResources().getColor(R.color.fbutton_color_shape_orange));
//        		mFourDigitTextBox.setEnabled(false);
//        	}
//        	
//        	break; 	
//        default:
//            break;
//        }
//    }
//
//    private static String mResponseDescription;
//    private static int mResponseError;
//    private Response mResponse;
//
//    private class SetPasswordTask extends AsyncTask<String, Void, Void> {
//
//    	boolean isFourDigit = false;
//    	
//        public SetPasswordTask(boolean isFourDigit) {
//			super();
//			this.isFourDigit = isFourDigit;
//		}
//
//		@Override
//        protected Void doInBackground(String... params) {
////			if(isFourDigit){
////				 mResponse = PBAPIHelper.setPassword(mPassword, mCollectionId, mToken);
////			}else{
////				 mResponse = PBAPIHelper.setPassword(mPassword, mCollectionId, mToken);
////			}
//			 mResponse = PBAPIHelper.setPassword(mPassword, mCollectionId, mToken,isFourDigit,mFourDigitTextBox.getText().toString());
//            if (mResponse != null) {
//                mResponseDescription = mResponse.decription;
//                mResponseError = mResponse.errorCode;
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void result) {
//            // go to uploading screen
//            super.onPostExecute(result);
//            // @lent add progress bar
//            mLvWaiting.setVisibility(View.GONE);
//            mConfirmPassBtn.setEnabled(true);
//
//            if (mResponseError != ResponseHandle.CODE_200_OK) { // Handle error here
//                Log.d(TAG, "[set password] " + mResponseDescription);
//                String errorNotifyMes = ResponseHandle
//                .getNotifyStringErrorRelatedPassword(
//                        mResponse.errorCode, mResponse.decription);
//
//               // mMakeLongPassBtn.setEnabled(false);
//                if (!mIsActivityDestroyed) {
//                    PBGeneralUtils.showAlertDialogActionWithOnClick(
//                    		PBFourDigitActivity.this,
//                            android.R.drawable.ic_dialog_alert,
//                            getString(R.string.dialog_error_title),
//                            errorNotifyMes,
//                            getString(R.string.upload_confirm_pass_ok_btn),
//                            new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                }
//                            });
//                }
//            } else { // set password successful
//                String passwordId = null;
//                JSONObject jObject;
//                if (!TextUtils.isEmpty(mResponseDescription)
//                        && mResponseDescription.contains("collection")) {
//                    try {
//                        jObject = new JSONObject(mResponseDescription);
//                        if (jObject != null) {
//                            if (jObject.has("collection")) {
//                                JSONObject jObjCollection = jObject
//                                .getJSONObject("collection");
//                                if (jObjCollection.has("id")) {
//                                    passwordId = jObjCollection.getString("id");
//                                }
//                            }
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//                PBPreferenceUtils.saveStringPref(getBaseContext(),
//                        PBConstant.UPLOAD_SERVICE_PREF,
//                        PBConstant.PREF_COLLECTION_ID, passwordId);
//                PBPreferenceUtils.saveStringPref(getBaseContext(),
//                        PBConstant.UPLOAD_SERVICE_PREF,
//                        PBConstant.PREF_UPLOAD_PASS, mPassword);
//                PBPreferenceUtils.saveStringPref(getBaseContext(),
//                        PBConstant.UPLOAD_SERVICE_PREF,
//                        PBConstant.PREF_PUBLIC_COLLECTION_ID, passwordId);
//                
//                startSelectAddibilityActivity(mPassword, passwordId, mSelectedMedia, mSelectedTypeMedia);
//            }
//        }
//    }
//    
//    private void startSelectAddibilityActivity(String password, String passwordId, ArrayList<String> selectedImages, long[] selectedTypeImages){
//        Intent intent = new Intent(getBaseContext(), PBUploadSelectAddibilityActivity.class);
//        intent.putExtra(PBConstant.INTENT_PASSWORD, password/*mPassword*/);
//        intent.putExtra(PBConstant.INTENT_PASSWORD_ID, passwordId/*passwordId*/);
//        intent.putExtra(PBConstant.INTENT_SELECTED_MEDIA, selectedImages/*mSelectedImages*/);
//        intent.putExtra(PBConstant.INTENT_SELECTED_MEDIA_TYPE, selectedTypeImages/*mSelectedImages*/);
//        intent.putExtra(PBConstant.INTENT_LONG_PASSWORD, mLongPas);
//        if(isPasswordPublic) {
//            intent.putExtra(PBConstant.INTENT_IS_PUBLIC_PASSWORD, true);
//        }
//        
//        startActivity(intent);
//        finish();
//    }
//
//    @Override
//    protected void handleHomeActionListener() {
//        
//    	/*boolean isOpenbagDisabled = PBPreferenceUtils.getBoolPref(PBUploadConfirmActivity.this, PBConstant.PREF_NAME, 
//    					PBConstant.PREF_KOUKAIBUKURO_IS_DISABLED, false);*/
//    	
////    	if(!PBDownloadFragment.isKoukaibukuroDisabled) {
////            Intent intent = new Intent(getBaseContext(),
////            		PBUploadKoukaibukuroActivity.class);
////            intent.putStringArrayListExtra(PBConstant.INTENT_SELECTED_MEDIA,
////                    mSelectedMedia);
////            intent.putExtra(PBConstant.INTENT_SELECTED_MEDIA_TYPE,
////                    mSelectedTypeMedia);
////            intent.putExtra(PBConstant.INTENT_PASSWORD, mSavePassword);
////            intent.putExtra(PBConstant.INTENT_LONG_PASSWORD, mLongPas);
////            startActivity(intent);
////            finish();    		
////    	} else {
//    		Intent intent = new Intent(getBaseContext(),
//                    UploadSetPasswordActivity.class);
//            intent.putStringArrayListExtra(PBConstant.INTENT_SELECTED_MEDIA,
//                    mSelectedMedia);
//            intent.putExtra(PBConstant.INTENT_SELECTED_MEDIA_TYPE,
//                    mSelectedTypeMedia);
//            intent.putExtra(PBConstant.INTENT_PASSWORD, mPassword);
//            intent.putExtra(PBConstant.INTENT_LONG_PASSWORD, mLongPas);
//            startActivity(intent);
//            finish();
//    //	}
//
//    }
//
//    @Override
//    protected void setLeftBtnsDetailHeader(ActionBar headerBar) {
//        // Do nothing here
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        mIsActivityDestroyed = true;
//    }
//
//    @Override
//    protected void onStop() {
//        mIsActivityDestroyed = true;
//        super.onStop();
//    }
//    private AlertDialog.Builder passwordConfirmDialog;
//    private AlertDialog.Builder passwordConfirmDialogInitial;
//    private class ConfirmPasswordTaskAgain extends AsyncTask<Void, Void, Void> {
//
//    	@Override
//    	protected void onPreExecute() {
//    		//mProgressHUD.show(true);
//    		//mHandler.sendEmptyMessage(DISABLE_MAKEPASSWORD_DIFFICULT_BUTTON);
//    		super.onPreExecute();
//    	}
//    	
//        @Override
//        protected void onPostExecute(Void result) {
//            super.onPostExecute(result);
//            
//            if(mErrorDescription != null) {
//            	mHandler.sendEmptyMessage(SHOW_ERROR_DIALOG);
//            } else {
//               // System.out.println("Atik New long password is:"+mLongPas);
//            	int alphaLen = mLongPas.length();
//            	if(alphaLen > PBConstant.PASSWORD_LENGTH){
//            		mPassword = mLongPas.substring(0, PBConstant.PASSWORD_LENGTH);
//            	}else{
//            		mPassword = mLongPas;
//            	}
//
//                if (!mIsActivityDestroyed) {
//                	
//                	passwordConfirmDialog = new AlertDialog.Builder(
//        					new ContextThemeWrapper(PBFourDigitActivity.this,
//        							R.style.popup_theme));
//                	passwordConfirmDialog.setTitle(getString(R.string.dialog_change_pass_title));
//                	passwordConfirmDialog.setMessage("[ " + mPassword + " ]");
//                	passwordConfirmDialog.setCancelable(false);
//                	passwordConfirmDialog.setPositiveButton(
//        					getString(R.string.dialog_ok_btn),
//        					new DialogInterface.OnClickListener() {
//        						@Override
//        						public void onClick(DialogInterface dialog, int which) {
//						        	 mChoosenPassword.setText(mPassword);
//        							// Execute below code after 1 seconds have passed
//        						    Handler handler = new Handler(); 
//        						    handler.postDelayed(new Runnable() { 
//        						         public void run() { 
//        						        	 //System.out.println("Inside the thread");
//        						        	 mChoosenPassword.setText(mPassword);
//        						        	 mMakeLongPassBtn.setTextColor(getResources().getColor(R.color.white));
//        						        	 mMakeLongPassBtn.setEnabled(true); 
//        						         } 
//        						    }, 1000);
//        							//mHandler.sendEmptyMessage(UPDATE_LONG_PASSWORD_REPEAT);
//        							//dialog.dismiss();
//        						}
//        					});
//                	
//                	passwordConfirmDialog.show();
//        			
//                	
//                    //mChoosenPassword.setText(mPassword);
//                    /*PBGeneralUtils.showAlertDialogActionWithOnClick(PBUploadConfirmActivity.this,
//                            getString(R.string.dialog_change_pass_title), 
//                            "[ " + mPassword + " ]",
//                            getString(R.string.upload_confirm_pass_ok_btn),
//                            new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog,
//                                int which) {
//                        	//mMakeLongPassBtn.setEnabled(true);
//                        
//                           // Added by Atik for bug fixing
//                            //dialog.dismiss();
//                            //mMakeLongPassBtn.setEnabled(true);
//                            //mChoosenPassword.setText(mPassword);
//                            //mHandler.sendEmptyMessage(UPDATE_LONG_PASSWORD);
//                            mHandler.sendEmptyMessage(UPDATE_LONG_PASSWORD_REPEAT);
//                            
//                        }
//                    });*/
//                    
//                }
//                
//            }
//
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//        	mCollectionId = PBPreferenceUtils.getStringPref(getBaseContext(),
//        			PBConstant.UPLOAD_SERVICE_PREF, PBConstant.PREF_COLLECTION_ID, "");
//            mToken = PBPreferenceUtils.getStringPref(getBaseContext(),
//                    PBConstant.PREF_NAME, PBConstant.PREF_NAME_TOKEN, "");
//            Response result = PBAPIHelper.confirmPassword(mPasswordForAgain, mCollectionId, mToken);
//            if (result != null) {
//                if (result.errorCode == ResponseHandle.CODE_200_OK) {// password
//                    // is valid
//                    String jsonResult = result.decription;
//                    mLongPas = parseLongPassword(jsonResult);
//
//                    
//                } else {// Handle error
//                    // mErrorDescription = result.decription;
//                    mErrorDescription = result;
//                   // mHandler.sendEmptyMessage(SHOW_ERROR_DIALOG);
//                }
//            }
//            return null;
//        }
//
//        private String parseLongPassword(String jsonResult) {
//            JSONObject jObject;
//            if (!TextUtils.isEmpty(jsonResult) 
//                    && jsonResult.contains("longer_password")) {
//                try {
//                    jObject = new JSONObject(jsonResult);
//                    if (jObject != null && jObject.has("longer_password")) {
//                            return jObject.getString("longer_password");
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//            return null;
//        }
//        
//    }
//
//}
