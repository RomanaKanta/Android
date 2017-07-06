package com.aircast.photobag.activity;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aircast.photobag.R;
import com.aircast.photobag.api.PBAPIHelper;
import com.aircast.photobag.api.PBTaskGetExchangeRate;
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
 * Show honey exchange page
 * */
public class PBMyPageHoneyExchangeActivity extends PBAbsActionBarActivity
		implements OnClickListener {
	private ActionBar mHomeBar;
	private TextView tvTotalDonguri, tvMaxExchangeDonguri, tvRemainderDonguri;
	private TextView tvMaxExchangeDonguriRed;
	private FButton buttonExchangeDonguri;
	private FButton mBtnGetDonguri;
	private TextView tvExchangeRate;

	private int mTotalDonguri;
	private int mapleExchangeRate;

	private View dropDownDonguri;
	private TextView tvPopUp;
	private ImageView imagePopUp;
	private LinearLayout mask;

	private View pickerView;
	private WheelView wheelViewPicker;
	private DonguriAdapter wheelAdapter;
	private boolean isWheelScrolling;

	// private ScrollView scrollView;
	private View moveableView;
	private int prevIndex;

	// custom waiting layout
	private PBCustomWaitingProgress mCustomWaitingLayout;
	
	private boolean mIsUseGold;

	private void updateDataFromPreference() {
		if (mIsUseGold) {
			mTotalDonguri = PBPreferenceUtils.getIntPref(
					getApplicationContext(), PBConstant.PREF_NAME,
					PBConstant.PREF_GOLD_COUNT, 0);
			mapleExchangeRate = PBPreferenceUtils.getIntPref(
					getApplicationContext(), PBConstant.PREF_NAME,
					PBConstant.PREF_DONGURI_GOLD_EXCHANGE_RATE, 15);
		} 
		else { 
			mTotalDonguri = PBPreferenceUtils.getIntPref(
					getApplicationContext(), PBConstant.PREF_NAME,
					PBConstant.PREF_DONGURI_COUNT, 0);
			mapleExchangeRate = PBPreferenceUtils.getIntPref(
					getApplicationContext(), PBConstant.PREF_NAME,
					PBConstant.PREF_DONGURI_HONEY_EXCHANGE_RATE, 30);
		}
		setTotalDonguri(this.mTotalDonguri);
	}
	
	private void updateInterface() {
		String acorn = getString(R.string.pb_my_page_text_acorn);
		String gold = getString(R.string.pb_my_page_text_gold);
		
		((TextView)findViewById(R.id.text_exchange_honey_rate_post)).setText(String.format(
				getString(R.string.pb_exchange_honey_text_first_post), 
				mIsUseGold ? gold : acorn));
		((TextView)findViewById(R.id.text_exchange_honey_count_first)).setText(String.format(
				getString(R.string.pb_exchange_honey_text_calculate_first), 
				mIsUseGold ? gold : acorn));
		((TextView)findViewById(R.id.text_exchange_honey_count_second)).setText(String.format(
				getString(R.string.pb_exchange_honey_text_calculate_second), 
				mIsUseGold ? gold : acorn));
		((TextView)findViewById(R.id.text_exchange_honey_count_third)).setText(String.format(
				getString(R.string.pb_exchange_honey_text_calculate_third), 
				mIsUseGold ? gold : acorn));
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pb_layout_my_page_exchange_acrons);
		
		try {
			mIsUseGold = getIntent().getBooleanExtra("IS_GOLD", false);
		} catch (Exception e) { mIsUseGold = false; }
		updateInterface();
		
		mainScreen = findViewById(R.id.mainScreen);
		// scrollView = (ScrollView) findViewById(R.id.scrollView);
		mask = (LinearLayout) findViewById(R.id.mask);
		mHomeBar = (ActionBar) findViewById(R.id.actionBar);
		mHomeBar.setTitle(getString(R.string.pb_exchange_honey_title));
		setHeader(mHomeBar, getString(R.string.pb_exchange_honey_title));

		moveableView = findViewById(R.id.scrollView);
		pickerView = findViewById(R.id.pickerView);
		wheelViewPicker = (WheelView) findViewById(R.id.wheelView1);
		tvExchangeRate = (TextView) findViewById(R.id.textViewExchangeRateHoney);
		tvTotalDonguri = (TextView) findViewById(R.id.tvTotalDonguri);
		tvMaxExchangeDonguri = (TextView) findViewById(R.id.tvMaxExchangeDonguri);
		tvRemainderDonguri = (TextView) findViewById(R.id.tvRemainderDonguri);
		tvMaxExchangeDonguriRed = (TextView) findViewById(R.id.tvMaxExchangeDonguriRed);
		buttonExchangeDonguri = (FButton) findViewById(R.id.buttonHoneyExchange);
		dropDownDonguri = findViewById(R.id.popUpItem);
		mBtnGetDonguri = (FButton) findViewById(R.id.buttonGetFreeDonguri);
		
		mBtnGetDonguri.setText(mIsUseGold
				? getString(R.string.pb_exchange_honey_button_get_gold)
				: getString(R.string.pb_exchange_honey_button_get_donguri));

		tvPopUp = (TextView) findViewById(R.id.textPopUpItem);
		imagePopUp = (ImageView) findViewById(R.id.imageViewPopUp);
		mask.setBackgroundColor(Color.argb(0, 0, 0, 0));

		((ImageView)findViewById(R.id.image_acorn_thumb)).setImageResource(
				mIsUseGold ? R.drawable.gold_acorns : R.drawable.my_page_02_acrons_ten);
		pickerView.setVisibility(View.INVISIBLE);
		buttonExchangeDonguri.setOnClickListener(this);
		dropDownDonguri.setOnClickListener(this);
		imagePopUp.setOnClickListener(this);
		mBtnGetDonguri.setOnClickListener(this);
		wheelAdapter = new DonguriAdapter(this, 
				mIsUseGold ? R.layout.item_spinner_gold : R.layout.item_spinner_donguri,
				R.id.text1);
		wheelViewPicker.setCyclic(false);
		wheelViewPicker.setViewAdapter(wheelAdapter);
		wheelViewPicker.setInterpolator(new AnticipateInterpolator());
		wheelViewPicker.addChangingListener(new OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				if (!isWheelScrolling)
					setHoneyClick(wheel.getCurrentItem()
							+ Math.min(1, mTotalDonguri / mapleExchangeRate));
			}
		});
		wheelViewPicker.addScrollingListener(new OnWheelScrollListener() {

			@Override
			public void onScrollingStarted(WheelView wheel) {
				isWheelScrolling = true;
			}

			@Override
			public void onScrollingFinished(WheelView wheel) {
				isWheelScrolling = false;
				setHoneyClick(wheel.getCurrentItem()
						+ Math.min(1, mTotalDonguri / mapleExchangeRate));
			}
		});
		this.mCustomWaitingLayout = new PBCustomWaitingProgress(this);
		/*PBTaskGetExchangeItems getRateTask = new PBTaskGetExchangeItems();
		getRateTask.setHandler(mHandler);
		getRateTask.setWaitingLayout(mCustomWaitingLayout);
		getRateTask.execute();*/
		updateDataFromPreference();
		refreshRate();
		
	}
	
	private PBTaskGetExchangeRate getRateTask; 

	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == PBTaskGetExchangeRate.UPDATE_UI) {
				updateDataFromPreference();
			}
		}
	};
	
	private void refreshRate(){
		if(getRateTask == null || getRateTask.getStatus() != AsyncTask.Status.RUNNING){
			this.getRateTask = new PBTaskGetExchangeRate(mHandler, mCustomWaitingLayout);
			this.getRateTask.execute();
		}
	}	

	private int[] innerPosition, outerPosition;
	private int yTranslate;
	private DisplayMetrics displaymetrics;
	private View mainScreen;
	private int[] offsetError;
	private int[] originalPos;

	private void showPicker() {
		wheelViewPicker.setCurrentItem(prevIndex);
		if (this.pickerView.getVisibility() == View.VISIBLE)
			return;
		this.pickerView.setVisibility(View.VISIBLE);
		if (this.innerPosition == null) {
			displaymetrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

			this.offsetError = new int[2];
			this.offsetError[0] = displaymetrics.widthPixels
					- mainScreen.getMeasuredWidth();
			this.offsetError[1] = displaymetrics.heightPixels
					- mainScreen.getMeasuredHeight();

			this.innerPosition = new int[2];
			this.pickerView.getLocationOnScreen(this.innerPosition);
			this.innerPosition[0] -= offsetError[0];
			this.innerPosition[1] -= offsetError[1];

			this.outerPosition = new int[2];
			this.outerPosition[0] = this.innerPosition[0];
			this.outerPosition[1] = mainScreen.getMeasuredHeight() + 2
					* this.pickerView.getHeight();
			readLocation(this.pickerView, mainScreen, "Picker View in Init");

			android.widget.RelativeLayout.LayoutParams lParam = ((android.widget.RelativeLayout.LayoutParams) this.wheelViewPicker
					.getLayoutParams());
			lParam.width = (int) (this.dropDownDonguri.getWidth() + 20 * displaymetrics.density);

			this.wheelViewPicker.setLayoutParams(lParam);

			originalPos = new int[2];
			this.moveableView.getLocationOnScreen(this.originalPos);

		}
		int[] positionOfDropdown = new int[2];
		this.dropDownDonguri.getLocationOnScreen(positionOfDropdown);
		positionOfDropdown[0] -= offsetError[0];
		positionOfDropdown[1] -= offsetError[1];

		yTranslate = (int) (innerPosition[1] - positionOfDropdown[1]
				- this.dropDownDonguri.getHeight() - 10 * displaymetrics.density);

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
				pickerView.setVisibility(View.VISIBLE);
			}
		});
		pickerView.startAnimation(anim);
		animateTranslate(this.moveableView, 0, 0, 0, yTranslate,
				new AnticipateInterpolator(), 400, null, true);
	}

	private void readLocation(View child, View mainScreen, String status) {
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		int offsetX = displayMetrics.widthPixels - mainScreen.getWidth();
		int offsetY = displayMetrics.heightPixels - mainScreen.getHeight();

		int[] locationInWindow = new int[2];
		child.getLocationInWindow(locationInWindow);
		int[] locationOnScreen = new int[2];
		child.getLocationOnScreen(locationOnScreen);

		Log.d("AGUNG", status + "\ngetLocationInWindow() - "
				+ locationInWindow[0] + " : " + locationInWindow[1] + "\n"
				+ "getLocationOnScreen() - " + locationOnScreen[0] + " : "
				+ locationOnScreen[1] + "\n" + "Offset x: y - " + offsetX
				+ " : " + offsetY);

	}

	private void hidePicker() {
		if (this.pickerView.getVisibility() != View.VISIBLE)
			return;

		final Animation anim = AnimationUtils.loadAnimation(this,
				R.anim.slide_out_bottom_anim);
		anim.setFillAfter(false);
		anim.setInterpolator(new AccelerateInterpolator());
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
		pickerView.startAnimation(anim);
		int nowPos[] = new int[2];
		this.moveableView.getLocationOnScreen(nowPos);
		yTranslate = this.originalPos[1] - nowPos[1];

		animateTranslate(moveableView, 0, 0, 0, yTranslate,
				new AnticipateInterpolator(), 400, null, true);
	}

	private void setHoneyClick(int honey) {
		if (honey * this.mapleExchangeRate > this.mTotalDonguri)
			return;
		tvPopUp.setText("" + honey);

		tvMaxExchangeDonguri.setText("" + (honey * this.mapleExchangeRate));
		tvRemainderDonguri.setText(""
				+ (this.mTotalDonguri - honey * this.mapleExchangeRate));
		tvTotalDonguri.setText("" + this.mTotalDonguri);

	}

	private void setTotalDonguri(int total) {
		this.mTotalDonguri = total;
		
		this.wheelAdapter.setMaxNumber(this.mTotalDonguri
				/ this.mapleExchangeRate);
		this.wheelAdapter.setMinNumber(Math.min(1, this.mTotalDonguri
				/ this.mapleExchangeRate));

		tvMaxExchangeDonguri.setText(""
				+ (this.mTotalDonguri / this.mapleExchangeRate * this.mapleExchangeRate));
		tvRemainderDonguri.setText(""
				+ (this.mTotalDonguri % this.mapleExchangeRate));
		tvTotalDonguri.setText("" + this.mTotalDonguri);
		tvMaxExchangeDonguriRed.setText("" + this.mTotalDonguri
				/ this.mapleExchangeRate);
		tvExchangeRate.setText("" + this.mapleExchangeRate);

		// set the maximum
		tvPopUp.setText("" + this.mTotalDonguri / this.mapleExchangeRate);

		prevIndex = this.mTotalDonguri / this.mapleExchangeRate - 1;
		if(prevIndex < 0) prevIndex = 0;
		wheelViewPicker.setCurrentItem(prevIndex, false);
		Log.d("AGUNG", "PrevChoose: " + prevIndex);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
	}

	@Override
	protected void onDestroy() {
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
		case R.id.buttonGetFreeDonguri:
			
			if(mIsUseGold) {
				Intent intentReward = new Intent(PBMyPageHoneyExchangeActivity.this,
						PBAcornForestActivity.class);
				startActivity(intentReward);	
			} else {
	        	boolean hasInternet = PBApplication.hasNetworkConnection();
	        	if (hasInternet) {
					// Atik modified the specification , will go to 
					PBGeneralUtils.openAcornWebview(PBMyPageHoneyExchangeActivity.this);
	        	} else {
	           	 	/*Toast.makeText(PBMyPageHoneyExchangeActivity.this, 
	           			 getString(R.string.pb_network_not_available_general_message), 1000).show();*/
				   Toast toast = Toast.makeText(PBMyPageHoneyExchangeActivity.this, getString(R.string.pb_network_not_available_general_message), 
							1000);
				   TextView v1 = (TextView) toast.getView().findViewById(android.R.id.message);
				   if( v1 != null) v1.setGravity(Gravity.CENTER);
				   toast.show();
	            }
			}

			break;
		case R.id.buttonHoneyExchange:
			final int exchange = Integer.parseInt(tvPopUp.getText().toString());
			if (exchange <= 0) {
				PBGeneralUtils.showAlertDialogActionWithOkButton(PBMyPageHoneyExchangeActivity.this, 
						getString(R.string.pb_exchange_honey_title), 
						getString(mIsUseGold 
								? R.string.pb_exchange_honey_not_enough_gold
								: R.string.pb_exchange_honey_not_enough_acorn), 
						getString(R.string.upload_confirm_pass_ok_btn));
			} else {
				String title = getString(R.string.pb_exchange_honey_dialog_title);
				String description = getString(R.string.pb_exchange_honey_dialog_message＿pre)
						+ " "
						+ exchange
						+ " "
						+ getString(R.string.pb_exchange_honey_dialog_message＿post);
				String cancel = getString(R.string.dialog_cancel_btn);
				String ok = getString(R.string.dialog_ok_btn);
				PBGeneralUtils.showAlertDialogAction(this, title, description,
						ok, cancel, new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								PBTaskExchangeHoney taskExchangeHon = new PBTaskExchangeHoney(exchange * mapleExchangeRate, mCustomWaitingLayout);
								if(taskExchangeHon.getStatus() != AsyncTask.Status.RUNNING){
									taskExchangeHon.execute();
								}
							}
						});
			}

			break;
		case R.id.btnPickerOk:
			hidePicker();
			prevIndex = this.wheelViewPicker.getCurrentItem();
			wheelViewPicker.setCurrentItem(prevIndex, false);
			break;
		case R.id.btnPickerCancel:
			hidePicker();
			wheelViewPicker.setCurrentItem(prevIndex, false);

			setHoneyClick(prevIndex
					+ Math.min(1, mTotalDonguri / this.mapleExchangeRate));
			break;
		case R.id.popUpItem:
		case R.id.imageViewPopUp:
			wheelViewPicker.setCurrentItem(prevIndex, false);
			showPicker();
			break;
		default:
			break;
		}
	}

	@Override
	protected void handleHomeActionListener() {
		finish();
	}

	@Override
	protected void setLeftBtnsDetailHeader(ActionBar headerBar) {

	}

	private class DonguriAdapter extends AbstractWheelTextAdapter {
		private int minNumber, maxNumber;

		protected DonguriAdapter(Context context, int itemResource,
				int itemTextResource) {
			super(context, itemResource, itemTextResource);

		}

		@Override
		public int getItemsCount() {
			return maxNumber - minNumber + 1;
		}

		@Override
		protected CharSequence getItemText(int index) {
			return "" + (index + minNumber);
		}

		public void setMaxNumber(int maxNumber) {
			this.maxNumber = maxNumber;
		}

		public void setMinNumber(int minNumber) {
			this.minNumber = minNumber;
		}

		@Override
		public View getItem(int index, View convertView, ViewGroup parent) {
			return super.getItem(index, convertView, parent);
		}

	}

	private void animateTranslate(View view, float xFrom, float xTo,
			float yFrom, float yTo, Interpolator interpolator, long duration,
			final Runnable onFinished, boolean fillAfter) {
		TranslateAnimation animation = new TranslateAnimation(
				TranslateAnimation.ABSOLUTE, xFrom,
				TranslateAnimation.ABSOLUTE, xTo, TranslateAnimation.ABSOLUTE,
				yFrom, TranslateAnimation.ABSOLUTE, yTo);
		if (interpolator != null)
			animation.setInterpolator(interpolator);
		animation.setDuration(duration);
		animation.setFillAfter(fillAfter);

		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				if (onFinished != null)
					onFinished.run();
				animation.setAnimationListener(null);
			}
		});
		view.startAnimation(animation);
	}
	
	private class PBTaskExchangeHoney extends AsyncTask<Void, Void, Response>{
		private PBCustomWaitingProgress waitingProgress;
		private int donguriCountToExchanged;
		
		public PBTaskExchangeHoney(int donguriCountToExchanged, PBCustomWaitingProgress waitingProgress){
			this.donguriCountToExchanged = donguriCountToExchanged;
			this.waitingProgress = waitingProgress;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if(this.waitingProgress!=null)
				this.waitingProgress.showWaitingLayout();
		}
		
		@Override
		protected Response doInBackground(Void... params) {
			int rate = PBPreferenceUtils.getIntPref(PBMyPageHoneyExchangeActivity.this, 
					PBConstant.PREF_NAME, 
					mIsUseGold ? PBConstant.PREF_DONGURI_GOLD_EXCHANGE_RATE : PBConstant.PREF_DONGURI_HONEY_EXCHANGE_RATE, 
					0);
			String token = PBPreferenceUtils.getStringPref(PBMyPageHoneyExchangeActivity.this, 
					PBConstant.PREF_NAME, 
					PBConstant.PREF_NAME_TOKEN, 
					"");
			Response response = null;
			if(!TextUtils.isEmpty(token)){
				response = PBAPIHelper.exchangeMaple(token,
						donguriCountToExchanged, 
						this.donguriCountToExchanged / rate, 
						mIsUseGold);
			}
			return response;	
		}
		
		@Override
		protected void onPostExecute(Response result) {
			super.onPostExecute(result);
			if(this.waitingProgress!=null)
				this.waitingProgress.hideWaitingLayout();
			if(result!=null){
				if(result.errorCode == ResponseHandle.CODE_200_OK){
					try {
						JSONObject json = new JSONObject(result.decription);
						if(json.optString("item", "").equals("maple")){
							onSuccessTradingMaple(json.optInt("acorn_count", 0), json.optInt("item_count", 0));
						}
						
					} catch (JSONException e) {
						e.printStackTrace();
						onFailedTradingMaple(result.errorCode+ ": "+e);
					}
				}else{
					Log.d("AGUNG", "PBTaskExchangeHoney Error Code:"+result.errorCode+":"+result.decription);
					onFailedTradingMaple(result.errorCode+ ": "+result.decription);
				}
			}else{
				onFailedTradingMaple("No reply from server");
			}
		}
		
	}
	
	private void onSuccessTradingMaple(int decreasingDonguri, int gotMaple){
		this.mTotalDonguri -= decreasingDonguri;
		int totalMaple = PBPreferenceUtils.getIntPref(this, PBConstant.PREF_NAME, PBConstant.PREF_MAPLE_COUNT, 0) + gotMaple;
		
		if(this.mTotalDonguri < 0){
			throw new IllegalStateException("Donguri is negative, which is not possible");
		}
		PBPreferenceUtils.saveIntPref(this, PBConstant.PREF_NAME, PBConstant.PREF_DONGURI_COUNT, this.mTotalDonguri);
		PBPreferenceUtils.saveIntPref(this, PBConstant.PREF_NAME, PBConstant.PREF_MAPLE_COUNT, totalMaple);

		Intent intent = new Intent(
				PBMyPageHoneyExchangeActivity.this,
				PBMyPageHoneyExchangeSuccessActivity.class);
		Bundle extras = getIntent().getExtras();
		
		intent.putExtra("EXCHANGE_DONGURI",
				decreasingDonguri);
		if(extras!=null)
			intent.putExtra(PBConstant.PREV_PAGE, extras.getInt(PBConstant.PREV_PAGE, -1));
		startActivity(intent);
		
		finish();
	}
	
	private void onFailedTradingMaple(String errorDesc) {
		Log.e("AGUNG",errorDesc);//TODO
		PBGeneralUtils.showAlertDialogActionWithOkButton(this, "予期せぬエラー", "予期せぬエラーが起きました。申し訳ありませんが、再度お試しください。", "OK");
	}

}
