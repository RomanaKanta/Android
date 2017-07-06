package com.smartmux.filevaultfree;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.smartmux.filevaultfree.lockscreen.KeyboardButtonClickedListener;
import com.smartmux.filevaultfree.lockscreen.KeyboardButtonEnum;
import com.smartmux.filevaultfree.lockscreen.KeyboardView;
import com.smartmux.filevaultfree.lockscreen.PinCodeRoundView;
import com.smartmux.filevaultfree.utils.AppUserInfo;
import com.smartmux.filevaultfree.utils.FileManager;


/**
 * Created by stoyan and olivier on 1/13/15.
 * The activity that appears when the password needs to be set or has to be asked.
 * Call this activity in normal or singleTop mode (not singleTask or singleInstance, it does not work
 * with {@link android.app.Activity#startActivityForResult(android.content.Intent, int)}).
 */
public abstract class AppLockActivity extends AppMainActivity implements KeyboardButtonClickedListener, View.OnClickListener {

    public static final String TAG = AppLockActivity.class.getSimpleName();
    public static final String ACTION_CANCEL = TAG + ".actionCancelled";
    private static final int DEFAULT_PIN_LENGTH = 4;

    protected TextView mStepTextView;
    protected TextView mForgotTextView;
    protected PinCodeRoundView mPinCodeRoundView;
    protected KeyboardView mKeyboardView;
//   / protected LockManager mLockManager;

   // protected int mType = AppLock.UNLOCK_PIN;
    protected int mAttempts = 1;
    protected String mPinCode;
    protected String mOldPinCode;
    private FileManager fileManager;
    private AppUserInfo appUserinfo;
	private String user_password;

    /**
     * First creation
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fileManager		= new FileManager();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(getContentView());
        initLayout(getIntent());
    }

    /**
     * If called in singleTop mode
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        initLayout(intent);
    }

    /**
     * Init completely the layout, depending of the extra {@link com.github.orangegangsters.lollipin.lib.managers.AppLock#EXTRA_TYPE}
     */
	private void initLayout(Intent intent) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
            //Animate if greater than 2.3.3
            overridePendingTransition(R.anim.nothing, R.anim.nothing);
        }
        mPinCode = "";
        mOldPinCode = "";


        mStepTextView = (TextView) this.findViewById(R.id.pin_code_step_textview);
        mPinCodeRoundView = (PinCodeRoundView) this.findViewById(R.id.pin_code_round_view);
        mPinCodeRoundView.setPinLength(this.getPinLength());
        mForgotTextView = (TextView) this.findViewById(R.id.pin_code_forgot_textview);
        mForgotTextView.setOnClickListener(this);
        mKeyboardView = (KeyboardView) this.findViewById(R.id.pin_code_keyboard_view);
        mKeyboardView.setKeyboardButtonClickedListener(this);
        
        ImageView imageLogo = (ImageView) this.findViewById(R.id.pin_code_logo_imageview);
        imageLogo.setImageResource(R.drawable.icon);

        mForgotTextView.setText(getForgotText());
        
        appUserinfo = new AppUserInfo(this);
		appUserinfo.createRootFolder();
		user_password = appUserinfo.getPassword();
		
		if(user_password==null){
			user_password = "1111";
		}

    }


    /**
     * Gets the {@link String} to be used in the {@link #mStepTextView} based on {@link #mType}
     * @param reason The {@link #mType} to return a {@link String} for
     * @return The {@link String} for the {@link com.smartmux.filevaultfree.AppLockActivity}
     */
    public String getStepText(int reason) {
        String msg = null;
        msg = getString(R.string.pin_code_step_change, this.getPinLength());
        return msg;
    }

    public String getForgotText() {
        return getString(R.string.pin_code_forgot_text);
    }

    /**
     * Overrides to allow a slide_down animation when finishing
     */
	@Override
    public void finish() {
        super.finish();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
            //Animate if greater than 2.3.3
            overridePendingTransition(R.anim.nothing, R.anim.slide_down);
        }
    }

    /**
     * Add the button clicked to {@link #mPinCode} each time.
     * Refreshes also the {@link com.smartmux.filevaultfree.lockscreen.PinCodeRoundView}
     */
    @SuppressLint("NewApi")
	@Override
    public void onKeyboardClick(KeyboardButtonEnum keyboardButtonEnum) {
    	
    
      
            int value = keyboardButtonEnum.getButtonValue();
            if (value == KeyboardButtonEnum.BUTTON_CLEAR.getButtonValue()) {
                if (!mPinCode.isEmpty()) {
                    setPinCode(mPinCode.substring(0, mPinCode.length() - 1));
                } else {
                    setPinCode("");
                }
            } else {
            	  if (mPinCode.length() < this.getPinLength()) {
                setPinCode(mPinCode + value);
            	  }
            }
            
            if (mPinCode.length() == this.getPinLength()) {
            	
            	if(user_password.equals(mPinCode)){
    				fileManager.setBackCode(getApplicationContext());
    				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        			finish();
    			}else{
    				Handler handler = new Handler();
    		        handler.postDelayed(new Runnable() {
    		           @Override
    		           public void run() {
    		        	   
    		        	   Animation shake = AnimationUtils.loadAnimation(AppLockActivity.this, R.anim.shake);
    	    			    findViewById(R.id.loginContent).startAnimation(shake);
    	    			    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    	    			    vibrator.vibrate(1000);
    	    			    setPinCode("");
    		           }
    		    }, 100);
    				
    			}
            	
            	
            }
    	
    }

    /**
     * Called at the end of the animation of the {@link com.andexert.library.RippleView}
     * Calls {@link #onPinCodeInputed} when {@link #mPinCode}
     */
    @Override
    public void onRippleAnimationEnd() {
       
    }

    /**
     * Displays the information dialog when the user clicks the
     * {@link #mForgotTextView}
     */
    public abstract void showForgotDialog();

    /**
     * Run a shake animation when the password is not valid.
     */
    protected void onPinCodeError() {
        onPinFailure(mAttempts++);
        Thread thread = new Thread() {
            public void run() {
                mPinCode = "";
                mPinCodeRoundView.refresh(mPinCode.length());
                Animation animation = AnimationUtils.loadAnimation(
                        AppLockActivity.this, R.anim.shake);
                mKeyboardView.startAnimation(animation);
            }
        };
        runOnUiThread(thread);
    }

    protected void onPinCodeSuccess() {
        onPinSuccess(mAttempts);
        mAttempts = 1;
    }

    /**
     * Set the pincode and refreshes the {@link com.smartmux.filevaultfree.lockscreen.PinCodeRoundView}
     */
    public void setPinCode(String pinCode) {
        mPinCode = pinCode;
        mPinCodeRoundView.refresh(mPinCode.length());
    }

    /**
     * Returns the type of this {@link com.github.orangegangsters.lollipin.lib.managers.AppLockActivity}
     */
//    public int getType() {
//        return mType;
//    }

    /**
     * When we click on the {@link #mForgotTextView} handle the pop-up
     * dialog
     *
     * @param view {@link #mForgotTextView}
     */
    @Override
    public void onClick(View view) {
        showForgotDialog();
    }

    /**
     * When the user has failed a pin challenge
     * @param attempts the number of attempts the user has used
     */
    public abstract void onPinFailure(int attempts);

    /**
     * When the user has succeeded at a pin challenge
     * @param attempts the number of attempts the user had used
     */
    public abstract void onPinSuccess(int attempts);

    /**
     * Gets the resource id to the {@link android.view.View} to be set with {@link #setContentView(int)}.
     * The custom layout must include the following:
     * - {@link android.widget.TextView} with an id of pin_code_step_textview
     * - {@link android.widget.TextView} with an id of pin_code_forgot_textview
     * - {@link PinCodeRoundView} with an id of pin_code_round_view
     * - {@link KeyboardView} with an id of pin_code_keyboard_view
     * @return the resource id to the {@link android.view.View}
     */
    public int getContentView() {
        return R.layout.activity_pin_code;
    }

    /**
     * Gets the number of digits in the pin code.  Subclasses can override this to change the
     * length of the pin.
     * @return the number of digits in the PIN
     */
    public int getPinLength() {
        return AppLockActivity.DEFAULT_PIN_LENGTH;
    }
    
    @Override
	 public boolean onKeyDown(int keyCode, KeyEvent event) {
	        if (keyCode == KeyEvent.KEYCODE_BACK){   
	        	Intent intent = new Intent(getApplicationContext(), FolderListActivity.class);
	        	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        	intent.putExtra("EXIT", true);
	        	startActivity(intent);
	            return true;
	        }
	        return super.onKeyDown(keyCode, event);
	 }
}
