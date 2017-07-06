package com.smartmux.videodownloader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.Holder;
import com.orhanobut.dialogplus.OnBackPressListener;
import com.orhanobut.dialogplus.OnDismissListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.smartmux.filevaultfree.lockscreen.KeyboardButtonClickedListener;
import com.smartmux.filevaultfree.lockscreen.KeyboardButtonEnum;
import com.smartmux.filevaultfree.lockscreen.KeyboardView;
import com.smartmux.filevaultfree.lockscreen.PinCodeRoundView;
import com.smartmux.videodownloader.lockscreen.utils.AppExtra;
import com.smartmux.videodownloader.lockscreen.utils.AppUserInfo;
import com.smartmux.videodownloader.utils.SMConstant;
import com.smartmux.videodownloader.utils.SMSharePref;

/**
 * Created by stoyan and olivier on 1/13/15. The activity that appears when the
 * password needs to be set or has to be asked. Call this activity in normal or
 * singleTop mode (not singleTask or singleInstance, it does not work with
 * {@link android.app.Activity#startActivityForResult(android.content.Intent, int)}
 * ).
 */
public class AppLockActivity extends Activity implements
		KeyboardButtonClickedListener, View.OnClickListener {

	public static final String TAG = AppLockActivity.class.getSimpleName();
	public static final String ACTION_CANCEL = TAG + ".actionCancelled";
	private static final int DEFAULT_PIN_LENGTH = 4;
	protected TextView mCancleTextView;
	protected TextView mStepTextView;
	protected TextView mForgotTextView;
	protected PinCodeRoundView mPinCodeRoundView;
	protected KeyboardView mKeyboardView;
	// / protected LockManager mLockManager;

	// protected int mType = AppLock.UNLOCK_PIN;
	protected int mAttempts = 1;
	protected String mPinCode;
	protected String mRePinCode;
	protected String mOldPinCode;
	private AppUserInfo appUserinfo;
	private String user_password;
	private String mgetpasscode;
	private String tag;

	EditText hints;
	private Object dialogPlus;
	private String _oldAnswer;
	private EditText answer;
	private TextView question;
	private TextView showPassword;
	private Button mSubmitButton;

	/**
	 * First creation
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(getContentView());

		appUserinfo = new AppUserInfo(this);
		mgetpasscode = appUserinfo.getPassword();

		initLayout(getIntent());

		if (getIntent().hasExtra("passcode")) {

			tag = getIntent().getExtras().getString("passcode");
			if (tag.equals("password_match")) {
			mCancleTextView.setVisibility(View.GONE);
			}
			if (tag.equals("change")) {
				mStepTextView.setText(R.string.change_passcode);

			} else {

				mStepTextView.setText(R.string.lock_info);
			}

		}
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
	 * Init completely the layout, depending of the extra
	 * {@link com.github.orangegangsters.lollipin.lib.managers.AppLock#EXTRA_TYPE}
	 */
	private void initLayout(Intent intent) {
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
			// Animate if greater than 2.3.3
			overridePendingTransition(R.anim.nothing, R.anim.nothing);
		}
		mPinCode = "";
		mOldPinCode = "";
		mRePinCode = "";
		mCancleTextView = (TextView) this
				.findViewById(R.id.textView_cancle_lock);
		mCancleTextView.setOnClickListener(this);
		mStepTextView = (TextView) this
				.findViewById(R.id.pin_code_step_textview);
		mPinCodeRoundView = (PinCodeRoundView) this
				.findViewById(R.id.pin_code_round_view);
		mPinCodeRoundView.setPinLength(this.getPinLength());
		mForgotTextView = (TextView) this
				.findViewById(R.id.pin_code_forgot_textview);
		mForgotTextView.setOnClickListener(this);
		mKeyboardView = (KeyboardView) this
				.findViewById(R.id.pin_code_keyboard_view);
		mKeyboardView.setKeyboardButtonClickedListener(this);

		ImageView imageLogo = (ImageView) this
				.findViewById(R.id.pin_code_logo_imageview);
		imageLogo.setImageResource(R.drawable.ic_launcher);

		mForgotTextView.setText(getForgotText());

		// if (mgetpasscode == null || mgetpasscode.length() == 0) {
		// // appUserinfo.setPassword(passwordText);
		// }else{
		// // appUserinfo.createRootFolder();
		// user_password = appUserinfo.getPassword();
		// }
		//

	}

	/**
	 * Gets the {@link String} to be used in the {@link #mStepTextView} based on
	 * {@link #mType}
	 * 
	 * @param reason
	 *            The {@link #mType} to return a {@link String} for
	 * @return The {@link String} for the {@link AppLockActivity}
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
	// @Override
	// public void finish() {
	// super.finish();
	// if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
	// //Animate if greater than 2.3.3
	// overridePendingTransition(R.anim.nothing, R.anim.slide_down);
	// }
	// }

	/**
	 * Add the button clicked to {@link #mPinCode} each time. Refreshes also the
	 * {@link com.smartmux.filevaultfree.lockscreen.PinCodeRoundView}
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

			if (tag.equals("password_on")) {

				if (mRePinCode.equals("")) {
					mRePinCode = mPinCode;
					mStepTextView.setText(R.string.re_enter);

					setPinCode("");

				} else {

					if (mRePinCode.equals(mPinCode)) {
						SMSharePref.setBackCode(getApplicationContext());
						appUserinfo.setPassword(mPinCode);

						SMSharePref.saveSecurity(getApplicationContext(),
								SMConstant.on);
						mRePinCode = "";

						overridePendingTransition(R.anim.push_left_in,
								R.anim.push_left_out);
						finish();

//						Toast.makeText(getApplicationContext(),
//								"new password set", 1000).show();
					} else {

						Toast.makeText(getApplicationContext(),
								"re-enter password not matching", 1000).show();
						setPinCode("");
					}

				}
			} else if (tag.equals("password_match")) {

				user_password = appUserinfo.getPassword();

				if (user_password.equals(mPinCode)) {
					SMSharePref.setBackCode(getApplicationContext());

//					Toast.makeText(getApplicationContext(), "password match",
//							1000).show();

					overridePendingTransition(R.anim.push_left_in,
							R.anim.push_left_out);
					finish();

				} else {
					setPinCode("");
					Toast.makeText(getApplicationContext(), "wrong password",
							1000).show();
				}

			} else if (tag.equals("password_off")) {
				user_password = appUserinfo.getPassword();

				if (user_password.equals(mPinCode)) {
					SMSharePref.setBackCode(getApplicationContext());
					SMSharePref.saveSecurity(getApplicationContext(),
							SMConstant.off);

					Toast.makeText(getApplicationContext(), "security off",
							1000).show();

					overridePendingTransition(R.anim.push_left_in,
							R.anim.push_left_out);
					finish();

				} else {
					setPinCode("");
					Toast.makeText(getApplicationContext(), "wrong password",
							1000).show();
				}

			} else if (tag.equals("change")) {

				if (mOldPinCode.equals("")) {
					mOldPinCode = mPinCode;
					mStepTextView.setText(R.string.new_pswd);

					setPinCode("");

				} else {

					if (mRePinCode.equals("")) {
						mRePinCode = mPinCode;
						mStepTextView.setText(R.string.re_enter_new_pswd);

						setPinCode("");

					} else {

						if (mRePinCode.equals(mPinCode)) {
							SMSharePref.setBackCode(getApplicationContext());
							appUserinfo.setPassword(mPinCode);

							SMSharePref.saveSecurity(getApplicationContext(),
									SMConstant.on);
							mRePinCode = "";

							overridePendingTransition(R.anim.push_left_in,
									R.anim.push_left_out);
							finish();

							Toast.makeText(getApplicationContext(),
									"new password set", 1000).show();
						} else {

							Toast.makeText(getApplicationContext(),
									"re-enter password not matching", 1000)
									.show();
							setPinCode("");
						}
					}
				}
			}
		}

	}

	/**
	 * Called at the end of the animation of the
	 * {@link com.andexert.library.RippleView} Calls {@link #onPinCodeInputed}
	 * when {@link #mPinCode}
	 */
	@Override
	public void onRippleAnimationEnd() {

	}

	/**
	 * Displays the information dialog when the user clicks the
	 * {@link #mForgotTextView}
	 */
	public void showForgotDialog() {

		appUserinfo = new AppUserInfo(this);
		String _oldQeustion = appUserinfo.getQuestion();
		_oldAnswer = appUserinfo.getAnswer();

		View contentView = getLayoutInflater().inflate(
				R.layout.forgot_password_dialog, null);
		Holder holder = new ViewHolder(contentView);
		Typeface font = Typeface.createFromAsset(getAssets(),
				AppExtra.AVENIRLSTD_LIGHT);
		Typeface font_black = Typeface.createFromAsset(getAssets(),
				AppExtra.AVENIRLSTD_BLACK);
		showPassword = (TextView) contentView.findViewById(R.id.showPassword);
		question = (TextView) contentView
				.findViewById(R.id.user_secret_question);
		showPassword.setTypeface(font_black);
		question.setTypeface(font);
		question.setText("Secrect Question : " + _oldQeustion);
		answer = (EditText) contentView
				.findViewById(R.id.user_secret_question_answer);

		OnFocusChangeListener ofcListener = new MyFocusChangeListener();
		answer.setOnFocusChangeListener(ofcListener);
		answer.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int keyCode,
					KeyEvent event) {
				// TODO Auto-generated method stub

				// AppToast.show(getApplicationContext(), "done Pressed");
				getWindow()
						.setSoftInputMode(
								WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
				return false;
			}
		});

		answer.setHint("answer...");
		answer.setTypeface(font);
		mSubmitButton = (Button) contentView.findViewById(R.id.user_info);
		mSubmitButton.setTypeface(font_black);
		// mEdtPwdSecretDigit.setVisibility(View.VISIBLE);
		DialogPlus dialogPlus = DialogPlus.newDialog(this)
				.setContentHolder(holder).setGravity(Gravity.CENTER)
				.setHeader(R.layout.header)

				.setOnDismissListener(new OnDismissListener() {
					@Override
					public void onDismiss(DialogPlus dialog) {
					}
				}).setOnBackPressListener(new OnBackPressListener() {
					@Override
					public void onBackPressed(DialogPlus dialog) {

					}
				}).setCancelable(true).create();
		View headerView = dialogPlus.getHeaderView();
		TextView headerTitle = (TextView) headerView
				.findViewById(R.id.header_title);
		headerTitle.setText("Forgot Password");
		dialogPlus.show();

		mSubmitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				if (!answer.getText().toString().trim().equals(_oldAnswer)) {
					// AppToast.show(getApplicationContext(),
					// getApplicationContext().getString(R.string.wrong_password));

					showPassword.setText("wrong_password");
					showPassword.setTextColor(Color.RED);
					showPassword.setVisibility(View.VISIBLE);
					return;
				} else {

					final String _oldPassword = appUserinfo.getPassword();
					// AppToast.show(getApplicationContext(), _oldPassword);

					showPassword.setText("Password is " + _oldPassword);
					showPassword.setVisibility(View.VISIBLE);
					mSubmitButton.setVisibility(View.GONE);

				}

			}
		});

	}

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
	 * Set the pincode and refreshes the
	 * {@link com.smartmux.filevaultfree.lockscreen.PinCodeRoundView}
	 */
	public void setPinCode(String pinCode) {
		mPinCode = pinCode;
		mPinCodeRoundView.refresh(mPinCode.length());
	}

	/**
	 * Returns the type of this
	 * {@link com.github.orangegangsters.lollipin.lib.managers.AppLockActivity}
	 */
	// public int getType() {
	// return mType;
	// }

	/**
	 * When we click on the {@link #mForgotTextView} handle the pop-up dialog
	 * 
	 * @param view
	 *            {@link #mForgotTextView}
	 */
	@Override
	public void onClick(View view) {

		switch (view.getId()) {

		case R.id.pin_code_forgot_textview:
			// showForgotDialog();
			break;

		case R.id.textView_cancle_lock:
			Intent eventsIntent = new Intent(AppLockActivity.this,
					MainActivity.class);

			eventsIntent.putExtra("settab", "Settings");
			startActivity(eventsIntent);
			finish();
			overridePendingTransition(R.anim.nothing, R.anim.slide_down);
			break;

		default:
			break;
		}
	}

	/**
	 * When the user has failed a pin challenge
	 * 
	 * @param attempts
	 *            the number of attempts the user has used
	 */
	public void onPinFailure(int attempts) {

	}

	/**
	 * When the user has succeeded at a pin challenge
	 * 
	 * @param attempts
	 *            the number of attempts the user had used
	 */
	public void onPinSuccess(int attempts) {

	}

	/**
	 * Gets the resource id to the {@link View} to be set with
	 * {@link #setContentView(int)}. The custom layout must include the
	 * following: - {@link TextView} with an id of pin_code_step_textview -
	 * {@link TextView} with an id of pin_code_forgot_textview -
	 * {@link PinCodeRoundView} with an id of pin_code_round_view -
	 * {@link KeyboardView} with an id of pin_code_keyboard_view
	 * 
	 * @return the resource id to the {@link View}
	 */
	public int getContentView() {
		return R.layout.activity_pin_code;
	}

	/**
	 * Gets the number of digits in the pin code. Subclasses can override this
	 * to change the length of the pin.
	 * 
	 * @return the number of digits in the PIN
	 */
	public int getPinLength() {
		return AppLockActivity.DEFAULT_PIN_LENGTH;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(getApplicationContext(),
					MainActivity.class);
			intent.putExtra("settab", "Settings");
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra("EXIT", true);
			startActivity(intent);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		int itemId = item.getItemId();
		switch (itemId) {
		case android.R.id.home:
			onKeyDown(KeyEvent.KEYCODE_BACK, null);
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Intent eventsIntent = new Intent(AppLockActivity.this,
				MainActivity.class);

		eventsIntent.putExtra("settab", "Settings");
		startActivity(eventsIntent);
		overridePendingTransition(R.anim.nothing, R.anim.slide_down);
		finish();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
	}

	private class MyFocusChangeListener implements OnFocusChangeListener {

		public void onFocusChange(View v, boolean hasFocus) {

			if (v.getId() == R.id.user_secret_question_answer && !hasFocus) {

				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

			}
		}
	}

}
