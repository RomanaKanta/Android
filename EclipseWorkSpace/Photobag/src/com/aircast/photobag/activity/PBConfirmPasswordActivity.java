package com.aircast.photobag.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.List;

import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.aircast.photobag.R;
import com.aircast.photobag.api.PBAPIHelper;
import com.aircast.photobag.api.ResponseHandle;
import com.aircast.photobag.api.ResponseHandle.Response;
import com.aircast.photobag.application.PBApplication;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.database.PBDatabaseManager;
import com.aircast.photobag.facebook.PBFBShareActivity;
import com.aircast.photobag.services.UploadServiceUtils;
import com.aircast.photobag.twitter.MainActivityTwitter;
import com.aircast.photobag.utils.PBGeneralUtils;
import com.aircast.photobag.utils.PBPreferenceUtils;
import com.aircast.photobag.widget.PBCustomDialogMenu;
import com.aircast.photobag.widget.PBCustomMenuItem;
import com.aircast.photobag.widget.PBCustomDialogMenu.CustomMenuItemClickListener;
import com.aircast.photobag.widget.actionbar.ActionBar;

// import com.ad_stir.AdstirTerminate;

/**
 * Show Password page, contains functions share password.
 * */
public class PBConfirmPasswordActivity extends PBAbsActionBarActivity implements
		OnClickListener {
	private final String TAG = "PBConfirmPasswordActivity";

	private TextView mTvCountDown;
	private long mCountDownTime = 0;
	private long mCollectionChargeAt = 0;
	private String mCollectionPwd = "";
	private final long COUNT_DOWN_INTERVAL = 1000;

	private boolean mIsActivityDestroyed = false;
	private String mToken;
	private String mCollectionID;
	private String mPageName = "PBHistoryInboxDetailActivity";
	private PBCounter mCounter;

	private static final int MSG_MENU_CANCEL = 0;
	private static final int MSG_MENU_COPY = 1;
	private static final int MSG_MENU_MAIL = 2;
	private static final int MSG_MENU_NAKAMAP = 3;
	private static final int MSG_MENU_SHARE = 4;

	private View clockView;

	private boolean isOwner;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.pb_layout_confirm_password);
		ActionBar headerBar = (ActionBar) findViewById(R.id.headerbar);
		setHeader(headerBar,
				getString(R.string.screen_title_confirm_pass_upload));

		mTvCountDown = (TextView) findViewById(R.id.tv_free_time);

		PBHistoryInboxDetailActivity.doFinishAtNextResume = true;

		findViewById(R.id.btn_share_twitter).setOnClickListener(this);
		findViewById(R.id.layout_reward_forest).setOnClickListener(this);
		findViewById(R.id.btn_share_facebook).setOnClickListener(this);
		findViewById(R.id.btn_share_line).setOnClickListener(this);
		findViewById(R.id.btn_share_other).setOnClickListener(this);
		clockView = findViewById(R.id.clockButton);
		clockView.setOnClickListener(this);

		Intent intent = getIntent();
		if (intent == null) {
			PBApplication.makeToastMsg("Invalid collection.");
		} else {
			Bundle bundle = intent.getExtras();
			if (bundle != null)
				isOwner = bundle.getBoolean(PBConstant.IS_OWNER, false);
			Bundle extras = intent.getBundleExtra("data");

			if (extras != null) {

				if (extras.containsKey(PBConstant.COLLECTION_PAGE_NAME)) {
					mPageName = extras
							.getString(PBConstant.COLLECTION_PAGE_NAME);
					// from History detail which view pass of collection
					if (mPageName.equals(PBHistoryInboxDetailActivity.class
							.getName())) {
						headerBar
								.setTitle(R.string.screen_title_confirm_pass_hitory);
					} else { // from Upload completed process
						headerBar
								.setTitle(R.string.screen_title_confirm_pass_upload);
					}
				}

				mCollectionChargeAt = extras.getLong(
						PBConstant.COLLECTION_CHARGE_AT, 0);
				System.out.print(mCollectionChargeAt + "  ");
				mCollectionPwd = extras
						.getString(PBConstant.COLLECTION_PASSWORD);
				this.mCollectionID = extras.getString(PBConstant.COLLECTION_ID);
				if (this.mCollectionID == null)
					throw new IllegalArgumentException("Need a collection id");
				// mCollectionThumb = extras
				// .getString(PBConstant.COLLECTION_THUMB);

				initialUI(extras);
			}
		}
		startAnimation();
	}

	private void startAnimation() {

		final View view[] = new View[] { findViewById(R.id.btn_share_twitter),
				findViewById(R.id.btn_share_facebook),
				findViewById(R.id.btn_share_line),
				findViewById(R.id.btn_share_other) };

		long offsetDelay = 100;
		long delay = 300;
		long duration = 1000;

		for (int i = 0; i < view.length; i++) {
			Animation anim = AnimationUtils.loadAnimation(this,
					R.anim.alpha_to_one);
			view[i].setVisibility(View.INVISIBLE);
			final int index = i;
			anim.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
					view[index].setVisibility(View.VISIBLE);

				}

				@Override
				public void onAnimationRepeat(Animation animation) {

				}

				@Override
				public void onAnimationEnd(Animation animation) {
					view[index].setVisibility(View.VISIBLE);

				}
			});
			anim.setDuration(duration);
			anim.setStartOffset(offsetDelay);
			view[i].startAnimation(anim);
			offsetDelay += delay;
		}

		final View slideLayout = findViewById(R.id.layout_center_time_animation);
		Animation animationSlide = AnimationUtils.loadAnimation(this,
				R.anim.time_panel_slide_anim);
		animationSlide.setDuration(800);
		animationSlide.setInterpolator(new DecelerateInterpolator());
		animationSlide.setStartOffset(100 + delay * 2);
		slideLayout.startAnimation(animationSlide);
	}

	/** set value initial for screen */
	private void initialUI(Bundle extras) {
		TextView tvPassword = (TextView) findViewById(R.id.tv_confirm_pass_ur_pass);
		tvPassword.setText(mCollectionPwd);
		PBPreferenceUtils.saveStringPref(getApplicationContext(),
				PBConstant.UPLOAD_SERVICE_PREF, PBConstant.PREF_FOUR_DIGIT,
				"12");
		PBDatabaseManager mDatabaseManager = PBDatabaseManager
				.getInstance(this);
		String secretCode = mDatabaseManager
				.getPassswordSecretDigit(mCollectionID);
		if (secretCode.equals("null")) {

			secretCode = "なし";
		}
		TextView txtSecretCode = (TextView) findViewById(R.id.tv_secret_code);
		if (!secretCode.equals("12")) {

			txtSecretCode.setText("パスワード [ " + secretCode + " ]");
		} else {
			txtSecretCode.setText("パスワード　なし");
		}
		mCountDownTime = (mCollectionChargeAt * 1000)
				- System.currentTimeMillis();
		if (mCountDownTime > 0) {
			mTvCountDown.setText(parseCountDownToTimeSring(mCountDownTime));
		}

		mCounter = new PBCounter(mCountDownTime, COUNT_DOWN_INTERVAL);

		mCounter.start();

		long now = System.currentTimeMillis() / 1000;
		if (!isOwner) {
			clockView.setVisibility(View.INVISIBLE);
			clockView.setOnClickListener(null);
			findViewById(R.id.textNote).setVisibility(View.GONE);
		} else if (now >= mCollectionChargeAt) {

			this.clockView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					PBGeneralUtils.showAlertDialogActionWithOkButton(
							// TODO
							PBConfirmPasswordActivity.this, "有効期限の時間設定",
							"有効期限が0のため、残り時間の設定はできません。", "OK");
				}
			});

		}

	}

	private void updateUI(long timeUntil) {
		if (mCounter != null) {
			mCounter.cancel();
		}

		TextView tvPassword = (TextView) findViewById(R.id.tv_confirm_pass_ur_pass);
		tvPassword.setText(mCollectionPwd);

		mCountDownTime = (timeUntil * 1000) - System.currentTimeMillis();
		if (mCountDownTime > 0) {
			mTvCountDown.setText(parseCountDownToTimeSring(mCountDownTime));
		}

		mCounter = new PBCounter(mCountDownTime, COUNT_DOWN_INTERVAL);

		mCounter.start();

		if (mCountDownTime <= 0) {
			this.clockView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					PBGeneralUtils.showAlertDialogActionWithOkButton(
							// TODO
							PBConfirmPasswordActivity.this, "有効期限の時間設定",
							"有効期限が0のため、残り時間の設定はできません。", "OK");
				}
			});
		}
	}

	/**
	 * parse a number to format hh:mm:ss
	 * 
	 * @param countDownTime
	 * @return
	 */
	private String parseCountDownToTimeSring(long countDownTime) {
		long time = countDownTime / 1000;
		int seconds = (int) (time % 60);
		time = time / 60; // calculate to minutes
		int minutes = (int) (time % 60);
		time = time / 60; // calculate to hours
		int hours = (int) time;

		DecimalFormat formatter = new DecimalFormat("#00");
		DecimalFormat hourFormater;
		if (hours >= 999) {
			hourFormater = new DecimalFormat("#0000");
		} else if (hours > 99) {
			hourFormater = new DecimalFormat("#000");
		} else {
			hourFormater = new DecimalFormat("#00");
		}

		return String.format("%s:%s:%s", hourFormater.format(hours),
				formatter.format(minutes), formatter.format(seconds));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_share_other:
			if (TextUtils.isEmpty(mCollectionPwd)) {
				return;
			}

			showTellFriendMenu();
			break;
		case R.id.btn_share_twitter:

			boolean hasInternetForTwitter = PBApplication
					.hasNetworkConnection();
			if (hasInternetForTwitter) { // Check Internet Connectivity

				tellFriendThroughTwitterApp();
				break;

			} else {

				/*
				 * Toast.makeText(PBConfirmPasswordActivity.this,
				 * getString(R.string.pb_network_not_available_general_message),
				 * 1000).show();
				 */
				Toast toast = Toast
						.makeText(
								PBConfirmPasswordActivity.this,
								getString(R.string.pb_network_not_available_general_message),
								1000);
				TextView v1 = (TextView) toast.getView().findViewById(
						android.R.id.message);
				if (v1 != null)
					v1.setGravity(Gravity.CENTER);
				toast.show();

				break;

			}
		case R.id.layout_reward_forest:
			Intent intentReward = new Intent(PBConfirmPasswordActivity.this,
					PBAcornForestActivity.class);
			startActivity(intentReward);
			break;
		case R.id.btn_share_facebook:

			boolean hasInternetForFacebook = PBApplication
					.hasNetworkConnection();
			if (hasInternetForFacebook) { // Check Internet Connectivity

				tellFriendThroughFacebook();
				break;

			} else {

				/*
				 * Toast.makeText(PBConfirmPasswordActivity.this,
				 * getString(R.string.pb_network_not_available_general_message),
				 * 1000).show();
				 */
				Toast toast = Toast
						.makeText(
								PBConfirmPasswordActivity.this,
								getString(R.string.pb_network_not_available_general_message),
								1000);
				TextView v1 = (TextView) toast.getView().findViewById(
						android.R.id.message);
				if (v1 != null)
					v1.setGravity(Gravity.CENTER);
				toast.show();

				break;

			}

		case R.id.btn_share_line:

			boolean hasInternetForLine = PBApplication.hasNetworkConnection();

			if (hasInternetForLine) { // Check Internet Connectivity

				tellFriendThoughLine();
				break;

			} else {

				/*
				 * Toast.makeText(PBConfirmPasswordActivity.this,
				 * getString(R.string.pb_network_not_available_general_message),
				 * 1000).show();
				 */
				Toast toast = Toast
						.makeText(
								PBConfirmPasswordActivity.this,
								getString(R.string.pb_network_not_available_general_message),
								1000);
				TextView v1 = (TextView) toast.getView().findViewById(
						android.R.id.message);
				if (v1 != null)
					v1.setGravity(Gravity.CENTER);
				toast.show();
				break;

			}

		case R.id.clockButton:
			long now = System.currentTimeMillis() / 1000;
			if (now >= mCollectionChargeAt) {
				PBGeneralUtils.showAlertDialogActionWithOkButton(
						// TODO
						PBConfirmPasswordActivity.this, "有効期限の時間設定",
						"有効期限が0のため、残り時間の設定はできません。", "OK");
				return;
			}

			Intent intent = new Intent(
					PBApplication.getBaseApplicationContext(),
					PBChangeIntervalTimeActivity.class);
			intent.putExtra(PBConstant.COLLECTION_CHARGE_AT,
					mCollectionChargeAt);
			intent.putExtra(PBConstant.COLLECTION_PASSWORD, mCollectionPwd);

			intent.putExtra(PBConstant.COLLECTION_ID, mCollectionID);

			startActivityForResult(intent, CHANGE_TIME_REQ_CODE);
			break;
		default:
			break;
		}
	}

	public final static int CHANGE_TIME_REQ_CODE = 10021;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == CHANGE_TIME_REQ_CODE && resultCode == RESULT_OK) {
			if (data.getExtras() != null) {
				long timeUntil = data.getLongExtra("time_until_in_seconds", -1);

				updateUI(timeUntil);
				mCollectionChargeAt = timeUntil;
			}
		}
	}

	private void destroyLocal() {
		if (mCounter != null) {
			mCounter.cancel();
		}
	}

	@Override
	protected void onStop() {
		mIsActivityDestroyed = true;
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mIsActivityDestroyed = true;
		destroyLocal();
	}

	@Override
	protected void handleHomeActionListener() {
		if (isOwner) {
			Intent finishIntent = new Intent(PBConfirmPasswordActivity.this,
					PBMainTabBarActivity.class);
			PBConstant.IS_HISTORY_TAG = true;
			finishIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			finishIntent.putExtra(PBConstant.TAB_SET_BY_TAG,
					PBConstant.HISTORY_TAG);
			finishIntent.putExtra("OUTBOX", "TRUE");
			startActivity(finishIntent);
		}

		finish();
	}

	@Override
	public void onBackPressed() {

		if (isOwner) {
			Intent finishIntent = new Intent(PBConfirmPasswordActivity.this,
					PBMainTabBarActivity.class);
			PBConstant.IS_HISTORY_TAG = true;
			finishIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			finishIntent.putExtra(PBConstant.TAB_SET_BY_TAG,
					PBConstant.HISTORY_TAG);
			finishIntent.putExtra("OUTBOX", "TRUE");
			startActivity(finishIntent);
		}

		finish();

	}

	@Override
	protected void setLeftBtnsDetailHeader(ActionBar headerBar) {

	}

	/** CountDownTimer is an abstract class, so extend it and fill in methods **/
	public class PBCounter extends CountDownTimer {

		public PBCounter(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			mTvCountDown.setText(R.string.screen_confirm_password_time_default);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			if (millisUntilFinished <= 0)
				return;
			mTvCountDown
					.setText(parseCountDownToTimeSring(millisUntilFinished));
		}

	}

	private void tellFriendThoughLine() {

		Uri uri = Uri.parse("http://line.naver.jp/R/msg/text/?"
				+ Uri.encode(createContentToShareLineOnlyLine()));

		/*
		 * Uri uri = Uri.parse("http://line.naver.jp/R/msg/text/?" +
		 * Uri.encode(createContentToShare()));
		 */

		final Intent lineIntent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(lineIntent);
	}

	private void tellFriendThoughShare() {
		final Intent emailIntent = new Intent(Intent.ACTION_SEND);
		String content = createContentToShare();

		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
				getString(R.string.screen_confirm_password_mail_subject));

		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, content);
		emailIntent.setType("text/plain");
		emailIntent.putExtra("compose_mode", true);

		startActivity(Intent.createChooser(emailIntent,
				getResources().getText(R.string.pb_app_name)));
	}

	private void tellFriendThroughEmail() {
		String content = createContentToShare();

		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
				getString(R.string.screen_confirm_password_mail_subject));
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, content);
		emailIntent.setType("plain/html");
		emailIntent.putExtra("compose_mode", true);

		startActivity(emailIntent);
	}

	private String createContentToShare() {
		if (TextUtils.isEmpty(mCollectionPwd)) {
			return null;
		}
		// mail
		// String strHeaderContent =
		// getString(R.string.screen_confirm_password_mail_content_0);
		String message = getString(R.string.pb_post_twitter_wall_text_new);

		PBDatabaseManager mDatabaseManager = PBDatabaseManager
				.getInstance(this);
		String secretCode = mDatabaseManager
				.getPassswordSecretDigit(mCollectionID);
		if (secretCode.equals("12") || secretCode.equals("null")) {

			secretCode = "なし";
		}
		final String strContent = String.format(message, mCollectionPwd,
				secretCode, mCollectionPwd);
		// StringBuilder content = new StringBuilder(strContent);
		// // content.append(d)
		// String expiresString = PBGeneralUtils
		// .parseDateTimeToString(mCollectionChargeAt * 1000);
		// expiresString = String.format(
		// getString(R.string.screen_confirm_password_mail_content_2),
		// expiresString);
		// if (!TextUtils.isEmpty(expiresString)) {
		// content.append(expiresString);
		// }
		//
		// content.append(getString(R.string.screen_confirm_password_mail_content_3));

		return strContent;
	}

	/*
	 * Content for Line message
	 */
	private String createContentToShareLineOnlyLine() {
		if (TextUtils.isEmpty(mCollectionPwd)) {
			return null;
		}
		// mail
		String strHeaderContent = getString(R.string.screen_confirm_password_mail_content_0);
		String message = getString(R.string.screen_confirm_password_mail_content_1);

		PBDatabaseManager mDatabaseManager = PBDatabaseManager
				.getInstance(this);
		String secretCode = mDatabaseManager
				.getPassswordSecretDigit(mCollectionID);
		if (secretCode.equals("12") || secretCode.equals("null")) {

			secretCode = "なし";
		}
		final String strContent = String.format(message, mCollectionPwd,
				secretCode, mCollectionPwd);
		StringBuilder content = new StringBuilder(strHeaderContent + strContent);
		// content.append(d)
		String expiresString = PBGeneralUtils
				.parseDateTimeToString(mCollectionChargeAt * 1000);
		expiresString = String.format(
				getString(R.string.screen_confirm_password_mail_content_2),
				expiresString);

		if (!TextUtils.isEmpty(expiresString)) {
			content.append(expiresString);
		}

		/*
		 * content.append(getString(R.string.screen_confirm_password_mail_content_3
		 * ));
		 */
		String pass = "";
		try {
			pass = URLEncoder.encode(mCollectionPwd, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		content.append(String.format(
				getString(R.string.screen_confirm_password_mail_content_4),
				pass));
		content.append(getString(R.string.screen_confirm_password_mail_content_5));
		content.append(getString(R.string.screen_confirm_password_mail_content_6));
		content.append(getString(R.string.screen_confirm_password_mail_content_7));
		content.append(getString(R.string.screen_confirm_password_mail_content_8));
		content.append(getString(R.string.screen_confirm_password_mail_content_9));

		// System.out.println("Atik Full Line message is:"+content.toString());

		return content.toString();
	}

	private void tellFriendThroughNakamap() {
		// String message = mCollectionPwd;
		String message = String
				.format(getString(R.string.share_with_nakamap_pre))
				+ mCollectionPwd
				+ "\n"
				+ PBConstant.PHOTOBAG_MAGIC_LINK
				+ mCollectionPwd
				+ String.format(getString(R.string.share_with_nakamap_aft));
		PackageManager packageManager = getPackageManager();
		Intent query = new Intent(Intent.ACTION_SEND);
		query.setType("text/plain");
		List<ResolveInfo> textIntents = packageManager.queryIntentActivities(
				query, PackageManager.MATCH_DEFAULT_ONLY);
		boolean hasNakamap = false;
		for (ResolveInfo info : textIntents) {
			String packageName = info.activityInfo.packageName;
			if ("com.kayac.nakamap".equals(packageName)) {
				hasNakamap = true;

				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setClassName(packageName, info.activityInfo.name);
				intent.setType("text/plain");
				intent.putExtra(Intent.EXTRA_TEXT, message);
				// intent.putExtra(Intent.extra_, value)
				startActivity(intent);
			}
		}

		if (!hasNakamap) {
			// show alert
			showDialogNotifyNakamapNotExist();
		}
	}

	/**
	 * call action post tweet from Twitter app.
	 * 
	 * @param tweetContent
	 *            tweet want to share.
	 */
	private void tellFriendThroughTwitterApp() {

		String message = getString(R.string.twitter_post_text);

		PBDatabaseManager mDatabaseManager = PBDatabaseManager
				.getInstance(this);
		String secretCode = mDatabaseManager
				.getPassswordSecretDigit(mCollectionID);
		if (secretCode.equals("12") || secretCode.equals("null"))  {

			secretCode = "なし";
		}
		final String content = String.format(message, mCollectionPwd,
				secretCode);
		Intent startTwitterActivity = new Intent(
				PBConfirmPasswordActivity.this, MainActivityTwitter.class);
		startTwitterActivity.putExtra("TWIT_MESSAGE", content);

		startActivity(startTwitterActivity);

	}

	private void tellFriendThroughFacebook() {

		String message = getString(R.string.pb_post_twitter_wall_text_new);

		PBDatabaseManager mDatabaseManager = PBDatabaseManager
				.getInstance(this);
		String secretCode = mDatabaseManager
				.getPassswordSecretDigit(mCollectionID);
		if (secretCode.equals("12") || secretCode.equals("null")) {

			secretCode = "なし";
		}
		final String content = String.format(message, mCollectionPwd,
				secretCode, mCollectionPwd);
		Intent facebookStartIntent = new Intent(PBConfirmPasswordActivity.this,
				PBFBShareActivity.class);
		if (getIntent() == null) {
			finish();
			return;
		}
		Bundle extras = getIntent().getBundleExtra("data");
		if (extras == null) {
			finish();
			return;
		}

		facebookStartIntent.putExtra("data", extras);
		facebookStartIntent.putExtra("FB_MESSAGE", content);

		startActivity(facebookStartIntent);
		/*
		 * Intent intent = new Intent(this, PBFacebookPreviewActivity.class);
		 * 
		 * if (getIntent() == null) { finish(); return; } Bundle extras =
		 * getIntent().getBundleExtra("data"); if (extras == null) { finish();
		 * return; }
		 * 
		 * intent.putExtra("data", extras); startActivity(intent);
		 */
	}

	private void showDialogNotifyNakamapNotExist() {
		PBGeneralUtils
				.showAlertDialogAction(
						PBConfirmPasswordActivity.this,
						getString(R.string.screen_confirm_password_nakamap_notify_title),
						getString(R.string.screen_confirm_password_nakamap_notify_message),
						getString(R.string.pb_btn_cancel),
						getString(R.string.pb_btn_OK),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						}, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								// start webview nakamap
								try {
									Uri uriNakamap = Uri
											.parse(getString(R.string.screen_confirm_password_nakamap_url));
									if (uriNakamap != null) {
										Intent browserIntent = new Intent(
												Intent.ACTION_VIEW, uriNakamap);
										startActivity(browserIntent);
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						});
	}

	private void startTwitterMarketApp() {
		try {
			Uri uriTwitter = Uri
					.parse(getString(R.string.pb_twitter_app_market_link));
			if (uriTwitter != null) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW);
				browserIntent.setData(uriTwitter);
				startActivity(browserIntent);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** add Menu */
	private PBCustomDialogMenu mTellFriendMenu;

	private void showTellFriendMenu() {
		if (mTellFriendMenu == null) {
			createTellFriendMenu();
		}

		if (mTellFriendMenu != null) {
			mTellFriendMenu.showMenu();
		}
	}

	private void hideTellFriendMenu() {
		if (mTellFriendMenu != null) {
			mTellFriendMenu.hideMenu();
		}
	}

	private void createTellFriendMenu() {
		mTellFriendMenu = new PBCustomDialogMenu(this);
		// mTellFriendMenu.setupMenuTitle("Hello custom menu \nDONE!");
		mTellFriendMenu.addMenuItem(new PBCustomMenuItem(MSG_MENU_COPY,
				PBCustomMenuItem.MENUITEM_NORMAL,
				getString(R.string.screen_confirm_password_btn_copy)));

		mTellFriendMenu.addMenuItem(new PBCustomMenuItem(MSG_MENU_MAIL,
				PBCustomMenuItem.MENUITEM_NORMAL,
				getString(R.string.screen_confirm_password_btn_mail)));

		// 20120224 @lent5 fix issue Redmine#854
		// 20120301 mod by NhatVT, change menu order, <S>
		// ishti
		// mTellFriendMenu.addMenuItem(new PBCustomMenuItem(MSG_MENU_NAKAMAP,
		// PBCustomMenuItem.MENUITEM_NORMAL,
		// getString(R.string.screen_confirm_password_btn_nakamap)));

		mTellFriendMenu.addMenuItem(new PBCustomMenuItem(MSG_MENU_SHARE,
				PBCustomMenuItem.MENUITEM_NORMAL,
				getString(R.string.screen_confirm_password_btn_share)));
		/*
		 * mTellFriendMenu.addMenuItem(new PBCustomMenuItem(5,
		 * PBCustomMenuItem.MENUITEM_NORMAL,
		 * getString(R.string.screen_confirm_password_btn_nakamap)));
		 */

		// 20120301 mod by NhatVT, change menu order, <E>
		mTellFriendMenu.addMenuItem(new PBCustomMenuItem(MSG_MENU_CANCEL,
				PBCustomMenuItem.MENUITEM_CANCEL,
				getString(R.string.pb_btn_cancel)));

		mTellFriendMenu
				.setOnCustomMenuListener(new CustomMenuItemClickListener() {
					@Override
					public boolean onCustomMenuItemClick(int item) {
						mTellFriendMenu.hideMenu();
						switch (item) {
						case MSG_MENU_COPY:
							// copy
							ClipboardManager mClipBoard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
							mClipBoard.setText(mCollectionPwd);
							PBApplication
									.makeToastMsg(getString(R.string.pb_setting_copytext_to_clipboard));
							break;
						case MSG_MENU_MAIL:
							// send email
							tellFriendThroughEmail();
							break;
						case MSG_MENU_SHARE:
							// share
							tellFriendThoughShare();
							break;
						case MSG_MENU_NAKAMAP:
							// nakamap
							tellFriendThroughNakamap();
							break;
						case MSG_MENU_CANCEL:
							hideTellFriendMenu();
							break;
						}
						return true;
					}
				});
	}

	// 20121406 @HaiNT15 show dialog confirming that users want to public magic
	// phrase thought album pass word <S>
	public void showPublicPassWordDialog() {
		String message = getResources().getString(
				R.string.pb_public_magic_phrase);
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message);
		builder.setNegativeButton(R.string.uploading_cancel_btn_text, null);
		builder.setPositiveButton(R.string.upload_confirm_pass_ok_btn,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						boolean connected = UploadServiceUtils
								.checkInternetConenction(
										PBConfirmPasswordActivity.this,
										(!mIsActivityDestroyed), false);
						if (connected) {
							mToken = PBPreferenceUtils.getStringPref(
									getBaseContext(), PBConstant.PREF_NAME,
									PBConstant.PREF_NAME_TOKEN, "").trim();
							mCollectionID = PBPreferenceUtils.getStringPref(
									getApplicationContext(),
									PBConstant.UPLOAD_SERVICE_PREF,
									PBConstant.PREF_PUBLIC_COLLECTION_ID, "")
									.trim();
							new PublicPasswordTask().execute();
						}
					}
				});
		builder.show();
	}

	private static String mResponseDescription;
	private static int mResponseError;
	private Response mResponse;

	private class PublicPasswordTask extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {

			mResponse = PBAPIHelper.publicPassword(mCollectionID, mToken);
			Log.d(TAG, "[collection id ] " + mCollectionID);
			Log.d(TAG, "[token ] " + mToken);
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

			if (mResponseError != ResponseHandle.CODE_200_OK) { // Handle error
																// here
				Log.d(TAG, "[public password desciption: ] "
						+ mResponseDescription);
				String errorNotifyMes = ResponseHandle
						.getNotifyStringErrorPublicPassword(
								mResponse.errorCode, mResponse.decription);
				if (!mIsActivityDestroyed) {
					PBGeneralUtils.showAlertDialogActionWithOnClick(
							PBConfirmPasswordActivity.this,
							android.R.drawable.ic_dialog_alert,
							getString(R.string.dialog_error_title),
							errorNotifyMes,
							getString(R.string.upload_confirm_pass_ok_btn),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							});
				}
			}
		}
	}
}
