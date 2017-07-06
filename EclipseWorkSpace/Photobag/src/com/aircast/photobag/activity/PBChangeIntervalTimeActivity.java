package com.aircast.photobag.activity;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
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
import com.aircast.photobag.widget.PBCustomWaitingProgress;
import com.aircast.photobag.widget.actionbar.ActionBar;

/**
 * Show wheel picker that can change expire time of password.
 * */
public class PBChangeIntervalTimeActivity extends PBAbsActionBarActivity
		implements OnClickListener {
	private ActionBar mHomeBar;
	private View mainScreen;
	private WheelView wheelViewHour, wheelViewMinute;
	private PickerAdapter wheelHourAdapter, wheelMinuteAdapter;
	private View pickerView;
	private TextView tvTime;

	private int hour, minutes, seconds;
	private long prevTime;
	private int[] innerPosition;
	private int[] outerPosition;
	private int yTranslate;
	private int[] offsetError;
	private DisplayMetrics displayMetrics;

	private View anchorView;
	private View moveableView;
	private TextView textPassword;

	private long mTimeUntil;
	private String mPassword;

	private int[] originalPosMoveableView;

	private String mCollectionId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pb_layout_send_01_change_valid_interval);

		mainScreen = findViewById(R.id.mainScreen);
		mHomeBar = (ActionBar) findViewById(R.id.actionBar);
		mHomeBar.setTitle(getString(R.string.pb_change_interval_photo_title));
		setHeader(mHomeBar, getString(R.string.pb_change_interval_photo_title));
		
		
		TextView textViewTimeSettings = (TextView) findViewById(R.id.textviewTimeSettings);
		TextView textViewTimeDuration = (TextView) findViewById(R.id.textviewTimeDuration);
		TextView textViewHoneyInfo = (TextView) findViewById(R.id.textviewHoneyInfo);
		TextView textViewCoution = (TextView) findViewById(R.id.textviewCoution);
		
		textViewTimeSettings.setText(getString(R.string.pb_send_interval_time_settings));
		textViewTimeDuration.setText(getString(R.string.pb_send_interval_time_duration));
		textViewHoneyInfo.setText(getString(R.string.pb_send_interval_honey_info));
		textViewCoution.setText(getString(R.string.pb_send_interval_caution));
				
		tvTime = (TextView) findViewById(R.id.tv_free_time);
		pickerView = findViewById(R.id.pickerView);
		anchorView = findViewById(R.id.anchorView);
		moveableView = findViewById(R.id.moveableView);
		textPassword = (TextView) findViewById(R.id.textPassword);

		wheelViewHour = (WheelView) findViewById(R.id.wheelHours);
		wheelViewMinute = (WheelView) findViewById(R.id.wheelMinutes);
		wheelViewHour.setCyclic(true);
		wheelViewMinute.setCyclic(true);
		wheelViewHour.setVisibleItems(5);
		wheelViewMinute.setVisibleItems(5);

		Bundle extras = getIntent().getExtras();
		if (extras == null)
			finish();
		this.mTimeUntil = extras.getLong(PBConstant.COLLECTION_CHARGE_AT);
		this.mPassword = extras.getString(PBConstant.COLLECTION_PASSWORD);
		this.mCollectionId = extras.getString(PBConstant.COLLECTION_ID);
		if (this.mCollectionId == null)
			throw new IllegalArgumentException("Need a collection id");
		this.textPassword.setText(this.mPassword);

		this.setTimeFromCurrentTimeLeft();

		this.pickerView.setVisibility(View.INVISIBLE);

		wheelHourAdapter = new PickerAdapter(this, this.hour);
		wheelMinuteAdapter = new PickerAdapter(this, 59);

		wheelViewHour.setViewAdapter(wheelHourAdapter);
		wheelViewMinute.setViewAdapter(wheelMinuteAdapter);

		wheelViewHour.setCurrentItem(hour);
		wheelViewMinute.setCurrentItem(minutes);

		wheelViewHour.setInterpolator(new AnticipateInterpolator());
		wheelViewMinute.setInterpolator(new AnticipateInterpolator());

		wheelViewHour.addChangingListener(new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				Log.d("AGUNG", "OnCHANGED HOUR");
				if (!hourScrolling) {
					updateTime(newValue, minutes);
				}
			}
		});

		wheelViewHour.addScrollingListener(new OnWheelScrollListener() {
			public void onScrollingStarted(WheelView wheel) {
				hourScrolling = true;
			}

			public void onScrollingFinished(WheelView wheel) {
				hourScrolling = false;
				updateTime(wheelViewHour.getCurrentItem(), minutes);
			}
		});

		wheelViewMinute.addChangingListener(new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				Log.d("AGUNG", "OnCHANGED MINUTES");
				if (!minuteScrolling) {
					updateTime(hour, newValue);
				}
			}
		});

		wheelViewMinute.addScrollingListener(new OnWheelScrollListener() {
			public void onScrollingStarted(WheelView wheel) {
				minuteScrolling = true;
			}

			public void onScrollingFinished(WheelView wheel) {
				minuteScrolling = false;
				updateTime(hour, wheelViewMinute.getCurrentItem());
			}

		});

		handler = new Handler();

		long left = this.mTimeUntil - System.currentTimeMillis() / 1000;
		if (left > 0) {
			updateTimeTextViewThread = new Thread(updateTimeRunnable);
			updateTimeTextViewThread.start();
		} else {
			FButton btnChangeInterval = (FButton) findViewById(R.id.buttonChangeInterval);
			//btnChangeInterval.setText(getString(R.string.pb_send_interval_change_interval));
			btnChangeInterval.setEnabled(false);
		}

	}

	private Thread updateTimeTextViewThread;

	void updateTime(int currentHour, int currentMinute) {
		Log.d("AGUNG", "Update time : " + currentHour + ":" + currentMinute);
		this.hour = currentHour;
		this.minutes = currentMinute;

		String str = String.format("%02d:%02d:%02d", hour, minutes, seconds);
		tvTime.setText(str);
	}

	void setTimeFromCurrentTimeLeft() {
		long now = System.currentTimeMillis() / 1000;
		if (now > this.mTimeUntil) {
			this.hour = 0;
			this.minutes = 0;
			this.seconds = 0;
			this.isStopped = true;
			this.tvTime.setText("00:00:00");

		} else {
			long time = (this.mTimeUntil * 1000 - System.currentTimeMillis()) / 1000;
			this.seconds = (int) (time % 60);
			time = time / 60; // calculate to minutes
			this.minutes = (int) (time % 60);
			time = time / 60; // calculate to hours
			this.hour = (int) time;

			prevTime = this.mTimeUntil;

		}

		this.wheelViewHour.setCurrentItem(this.hour);
		this.wheelViewMinute.setCurrentItem(this.minutes);
	}

	void updateTimeFromClock() {

		this.mTimeUntil = System.currentTimeMillis() / 1000
				+ ((((this.hour * 60) + this.minutes) * 60) + this.seconds);
		prevTime = this.mTimeUntil;
	}

	private boolean hourScrolling, minuteScrolling;
	private Handler handler;
	private boolean isStopped;

	private void onTick() {
		if (hour == 0 && minutes == 0 && seconds == 0){
			isStopped = true;
			
			return;
		}
		seconds--;
		boolean flagM = false;
		boolean flagH = false;
		if (seconds < 0) {
			seconds = 59;
			flagM = true;
		}
		if (flagM) {
			minutes--;
			if (minutes < 0) {
				flagH = true;
				minutes = 59;
			}
		}

		if (flagH) {
			hour--;
		}
		if (hour <= 0) {
			hour = 0;
			if (minutes <= 0) {
				minutes = 0;
				if (seconds <= 0) {
					seconds = 0;
				}
			}
		}

		final String str = String.format("%02d:%02d:%02d", hour, minutes,
				seconds);
		this.tvTime.setText(str);
	}

	private Runnable updateTimeRunnable = new Runnable() {

		@Override
		public void run() {
			while (!isStopped) {

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (isStopped)
					break;

				handler.post(new Runnable() {
					@Override
					public void run() {
						if (!isStopped)
							onTick();
					}
				});

			}
		}
	};

	private void showTimePicker() {
		long time = this.mTimeUntil * 1000 - System.currentTimeMillis();
		long timeH = (time / 1000) / 3600;
		// PBApplication.makeToastMsg("Show picker hour:"+timeH);
		this.wheelHourAdapter.setNumber((int) timeH);

		animeteIn();

	}

	private void animeteIn() {
		if (this.pickerView.getVisibility() == View.VISIBLE)
			return;
		this.pickerView.setVisibility(View.VISIBLE);
		if (this.innerPosition == null) {
			displayMetrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

			this.offsetError = new int[2];
			this.offsetError[0] = displayMetrics.widthPixels
					- mainScreen.getWidth();
			this.offsetError[1] = displayMetrics.heightPixels
					- mainScreen.getHeight();

			this.innerPosition = new int[2];
			this.pickerView.getLocationOnScreen(this.innerPosition);
			this.innerPosition[0] -= this.offsetError[0];
			this.innerPosition[1] -= this.offsetError[1];

			this.outerPosition = new int[2];
			this.outerPosition[0] = this.innerPosition[0];
			this.outerPosition[1] = mainScreen.getHeight() + 2
					* this.pickerView.getHeight();

			this.originalPosMoveableView = new int[2];

			this.moveableView.getLocationOnScreen(this.originalPosMoveableView);
			this.originalPosMoveableView[0] -= this.offsetError[0];
			this.originalPosMoveableView[1] -= this.offsetError[1];

		}
		int[] positionOfDropdown = new int[2];
		this.anchorView.getLocationOnScreen(positionOfDropdown);
		positionOfDropdown[0] -= offsetError[0];
		positionOfDropdown[1] -= offsetError[1];

		yTranslate = (int) (innerPosition[1] - positionOfDropdown[1]
				- this.anchorView.getHeight() - 10 * displayMetrics.density);

		final Animation anim = AnimationUtils.loadAnimation(this,
				R.anim.slide_in_up_anim);
		anim.setFillAfter(true);
		anim.setInterpolator(new DecelerateInterpolator());
		anim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				pickerView.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				wheelViewHour.setCurrentItem(hour);
				wheelViewMinute.setCurrentItem(minutes);
				pickerView.setVisibility(View.VISIBLE);
				// anim.setAnimationListener(null);
			}
		});
		pickerView.startAnimation(anim);
		// animateTranslate(moveableView, 0, 0, 0, yTranslate,
		// new AnticipateInterpolator(), 400, null, true);
	}

	private void animateOut() {
		if (this.pickerView.getVisibility() != View.VISIBLE)
			return;

		final Animation anim = AnimationUtils.loadAnimation(this,
				R.anim.slide_out_bottom_anim);
		anim.setFillAfter(false);
		anim.setInterpolator(new LinearInterpolator());
		anim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				pickerView.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				pickerView.setVisibility(View.INVISIBLE);
				anim.setAnimationListener(null);
			}
		});
		int[] nowPos = new int[2];
		this.moveableView.getLocationOnScreen(nowPos);
		nowPos[0] -= this.offsetError[0];
		nowPos[1] -= this.offsetError[1];
		yTranslate = this.originalPosMoveableView[1] - nowPos[1];

		pickerView.startAnimation(anim);
		// animateTranslate(moveableView, 0, 0, 0, yTranslate,
		// new LinearInterpolator(), 400, null, true);
	}

	private void hideTimePicker() {
		animateOut();
		// pickerView.setVisibility(View.INVISIBLE);
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	protected void onDestroy() {
		isStopped = true;
		super.onDestroy();

// 		try {
// 			AdstirTerminate.init(this);
// 		} catch (Exception e) {}
	}

	@Override
	protected void onPause() {

		super.onPause();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnPickerOk:
			if (hourScrolling || minuteScrolling)
				return;
			final long after = System.currentTimeMillis() / 1000 + hour * 3600
					+ minutes * 60 + 0;// seconds;
			if (after > mTimeUntil) {//TODO
				PBGeneralUtils.showAlertDialogAction(
						PBChangeIntervalTimeActivity.this, "操作エラー",
						"現在の有効期限以上の時間設定はできません。", "OK", null);

				setTimeFromCurrentTimeLeft();
			} else {
				this.prevTime = this.mTimeUntil;
				this.mTimeUntil = after;

				hideTimePicker();
				PBCustomWaitingProgress waitingProg = new PBCustomWaitingProgress(
						PBChangeIntervalTimeActivity.this);
				PBTaskUpdateChargesAtTime task = new PBTaskUpdateChargesAtTime(
						mCollectionId, after, waitingProg);
				task.execute();
			}

			break;
		case R.id.buttonChangeInterval:
			final long timeUntil = System.currentTimeMillis() / 1000 + hour
					* 3600 + minutes * 60 + 0;// seconds;
			if (timeUntil < mTimeUntil) {
				showTimePicker();
			} else {
				PBGeneralUtils.showAlertDialogAction(//TODO
						PBChangeIntervalTimeActivity.this, "有効期限の時間設定",
						"有効期限が0のため、残り時間の設定はできません。", "OK", null);
			}
			break;
		case R.id.btnPickerCancel:
			hideTimePicker();
			this.mTimeUntil = this.prevTime;
			setTimeFromCurrentTimeLeft();
			break;
		default:
			break;
		}
	}

	void onErrorUpdateChargesTime(String errorTitle, String errorDescription) {
		PBGeneralUtils.showAlertDialogActionWithOkButton(this, errorTitle,
				errorDescription, "OK");
		this.mTimeUntil = this.prevTime;
		setTimeFromCurrentTimeLeft();
	}

	void onSuccessUpdateChargesTime() {//TODO
		PBGeneralUtils.showAlertDialogNoTitleWithOnClick(this,
				getString(R.string.pb_settings_expiration_completed));
		setTimeFromCurrentTimeLeft();

		updateTime(hour, minutes);
	}

	@Override
	protected void handleHomeActionListener() {
		updateTimeFromClock();
		Intent i = getIntent();
		i.putExtra("time_until_in_seconds", this.mTimeUntil);
		if (getParent() == null) {
		    setResult(RESULT_OK, i);
		} else {
		    getParent().setResult(RESULT_OK, i);
		}
		finish();
	}

	@Override
	public void onBackPressed() {
		handleHomeActionListener();
	}

	@Override
	protected void setLeftBtnsDetailHeader(ActionBar headerBar) {

	}

	private class PickerAdapter extends AbstractWheelTextAdapter {
		private int number;

		protected PickerAdapter(Context context, int number) {
			super(context, R.layout.text_item, NO_RESOURCE);
			setItemTextResource(R.id.textView1);
			this.number = number;
		}

		@Override
		public int getItemsCount() {
			return number + 1;
		}

		@Override
		protected CharSequence getItemText(int index) {
			return "" + (index);
		}

		@Override
		public View getItem(int index, View convertView, ViewGroup parent) {
			return super.getItem(index, convertView, parent);

		}

		public void setNumber(int number) {
			if (this.number != number) {
				this.number = number;
				this.notifyDataChangedEvent();
			}

		}

	}

	private class PBTaskUpdateChargesAtTime extends
			AsyncTask<Void, Void, Response> {
		private PBCustomWaitingProgress waitingProgress;
		private String collectionID;
		private long chargesAt;

		public PBTaskUpdateChargesAtTime(String collectionID, long chargesAt,
				PBCustomWaitingProgress waitingProgress) {
			this.collectionID = collectionID;
			this.chargesAt = chargesAt;
			this.waitingProgress = waitingProgress;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (waitingProgress != null)
				waitingProgress.showWaitingLayout();
		}

		@Override
		protected Response doInBackground(Void... params) {
			String token = PBPreferenceUtils.getStringPref(
					PBApplication.getBaseApplicationContext(),
					PBConstant.PREF_NAME, PBConstant.PREF_NAME_TOKEN, "");

			if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(collectionID)) {
				Response response = PBAPIHelper.updateChargesTime(collectionID,
						chargesAt, token);
				return response;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Response result) {
			super.onPostExecute(result);
			if (waitingProgress != null)
				waitingProgress.hideWaitingLayout();

			if (result != null) {
				if (result.errorCode == ResponseHandle.CODE_200_OK) {
					onSuccessUpdateChargesTime();
				} else {
					try {
						JSONObject obj = new JSONObject(result.decription);
						onErrorUpdateChargesTime(obj.getString("error"),
								obj.getString("error_description"));
					} catch (Exception e) {//TODO
						onErrorUpdateChargesTime("操作エラー",
								"正しく設定することができませんでした。再度お試しください。");
						e.printStackTrace();
						Log.e("AGUNG", "Error :" + result.errorCode + " -> "
								+ result.decription);
					}
				}
			} else {
				onErrorUpdateChargesTime("Unknown Error", "");
			}
		}

	}

}
