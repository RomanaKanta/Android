package com.roundflat.musclecard;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.roundflat.musclecard.util.Constant;
import com.roundflat.musclecard.util.PreferenceUtils;

public class GameResultActivity extends Activity implements OnClickListener {

	private String type, time;
	String result1, result2, result3;
	private int count;
	private int cardType;
	private String rootTitle, routeType;
	TextView textView_result_duration, textview_level, txtResult1, txtResult2, txtResult3;
	List<String> resultList = new ArrayList<String>();
	ImageView resultImage;
	ValueAnimator colorAnim = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.game_result);

		resultImage = (ImageView) findViewById(R.id.imageView_result);
		
		textview_level = (TextView) findViewById(R.id.textView_result_level);

		if (getIntent().hasExtra("time")) {

			type = getIntent().getExtras().getString(Constant.GAME_TYPE);
			count = getIntent().getExtras().getInt(Constant.GAME_COUNT);
			cardType = getIntent().getExtras().getInt(Constant.CARD_TYPE);
			rootTitle = getIntent().getExtras().getString(Constant.root_title);
			routeType = getIntent().getExtras().getString(Constant.ROUTE_TYPE);

			time = getIntent().getExtras().getString("time");

			textView_result_duration = (TextView) findViewById(R.id.textView_result_duration);
			textView_result_duration.setText("時間: " + time);

			txtResult1 = (TextView) findViewById(R.id.textView_result_first);

			txtResult2 = (TextView) findViewById(R.id.textView_result_second);

			txtResult3 = (TextView) findViewById(R.id.textView_result_third);

			if (type.equals(Constant.EASY)) {

				textview_level.setText(R.string.easy);
				
				setResult(Constant.RESULTEASY1, Constant.RESULTEASY2,
						Constant.RESULTEASY3, time);

			} else if (type.equals(Constant.MEDIUM)) {

				textview_level.setText(R.string.medium);
				
				setResult(Constant.RESULTMEDIUM1, Constant.RESULTMEDIUM2,
						Constant.RESULTMEDIUM3, time);

			} else if (type.equals(Constant.DIFFICULT)) {
				
				textview_level.setText(R.string.diffcult);

				setResult(Constant.RESULTDIFFICULT1, Constant.RESULTDIFFICULT2,
						Constant.RESULTDIFFICULT3, time);

			}
		}
		findViewById(R.id.button_result_one).setOnClickListener(this);
		findViewById(R.id.button_result_second).setOnClickListener(this);
		findViewById(R.id.button_result_third).setOnClickListener(this);

	}

	private void blink(final TextView txt, int color) {

	 colorAnim = ObjectAnimator.ofInt(txt, "textColor", color);
		colorAnim.setDuration(1000);
		colorAnim.setEvaluator(new ArgbEvaluator());
		colorAnim.setRepeatCount(ValueAnimator.INFINITE);
		colorAnim.setRepeatMode(ValueAnimator.REVERSE);
		colorAnim.start();

	}

	public void setResult(final String r1, final String r2, final String r3,
			String t) {

		SimpleDateFormat sdf = new SimpleDateFormat("mm分 ss秒");

		result1 = PreferenceUtils.getStringPref(getApplicationContext(),
				Constant.PREF_FAV, r1, "");

		result2 = PreferenceUtils.getStringPref(getApplicationContext(),
				Constant.PREF_FAV, r2, "");

		result3 = PreferenceUtils.getStringPref(getApplicationContext(),
				Constant.PREF_FAV, r3, "");

		if (result1.equals("") || result2.equals("") || result3.equals("")) {

			if (result1.equals("")) {

				PreferenceUtils.saveStringPref(getApplicationContext(),
						Constant.PREF_FAV, r1, t);

				txtResult1.setText(t);

				txtResult1.setTextAppearance(getApplicationContext(),
						android.R.style.TextAppearance_Large);
				txtResult1.setTypeface(null, Typeface.BOLD);
				blink(txtResult1, Color.parseColor("#ffc0392b"));
				
				resultImage
				.setImageResource(R.drawable.result_image);

			} else if (result2.equals("")) {

				try {
					Date dresult1 = sdf.parse(result1);
					Date dtime = sdf.parse(t);

					if (dtime.getTime() <= dresult1.getTime()) {

						PreferenceUtils.saveStringPref(getApplicationContext(),
								Constant.PREF_FAV, r1, t);
						PreferenceUtils.saveStringPref(getApplicationContext(),
								Constant.PREF_FAV, r2, result1);

						txtResult1.setTextAppearance(getApplicationContext(),
								android.R.style.TextAppearance_Large);
						txtResult1.setTypeface(null, Typeface.BOLD);
						blink(txtResult1, Color.parseColor("#ffc0392b"));
						
						resultImage
						.setImageResource(R.drawable.result_image);

					} else {
						PreferenceUtils.saveStringPref(getApplicationContext(),
								Constant.PREF_FAV, r2, t);

						txtResult2.setTextAppearance(getApplicationContext(),
								android.R.style.TextAppearance_Large);
						txtResult2.setTypeface(null, Typeface.BOLD);
						blink(txtResult2, Color.parseColor("#2B65F0"));

					}
//					resultImage
//							.setImageResource(R.drawable.result_image);
					result1 = PreferenceUtils.getStringPref(
							getApplicationContext(), Constant.PREF_FAV, r1, "");

					result2 = PreferenceUtils.getStringPref(
							getApplicationContext(), Constant.PREF_FAV, r2, "");

					resultList.add(result1);
					resultList.add(result2);

					Collections.sort(resultList);

					txtResult1.setText(resultList.get(0));
					txtResult2.setText(resultList.get(1));

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else if (result3.equals("")) {
				try {
					Date dresult1 = sdf.parse(result1);
					Date dresult2 = sdf.parse(result2);
					Date dtime = sdf.parse(t);

					if (dtime.getTime() <= dresult1.getTime()) {

						PreferenceUtils.saveStringPref(getApplicationContext(),
								Constant.PREF_FAV, r1, t);

						txtResult1.setTextAppearance(getApplicationContext(),
								android.R.style.TextAppearance_Large);
						txtResult1.setTypeface(null, Typeface.BOLD);
						blink(txtResult1, Color.parseColor("#ffc0392b"));
						
						resultImage
						.setImageResource(R.drawable.result_image);

//						if (dresult1.getTime() < dresult2.getTime()) {

							PreferenceUtils.saveStringPref(
									getApplicationContext(), Constant.PREF_FAV,
									r2, result1);
							PreferenceUtils.saveStringPref(
									getApplicationContext(), Constant.PREF_FAV,
									r3, result2);
//						}

					} else if (dtime.getTime() <= dresult2.getTime()
							&& dtime.getTime() > dresult1.getTime()) {

						PreferenceUtils.saveStringPref(getApplicationContext(),
								Constant.PREF_FAV, r2, t);

						PreferenceUtils.saveStringPref(getApplicationContext(),
								Constant.PREF_FAV, r3, result2);

						txtResult2.setTextAppearance(getApplicationContext(),
								android.R.style.TextAppearance_Large);
						txtResult2.setTypeface(null, Typeface.BOLD);
						blink(txtResult2, Color.parseColor("#2B65F0"));

					} else {

						PreferenceUtils.saveStringPref(getApplicationContext(),
								Constant.PREF_FAV, r3, t);

						txtResult3.setTextAppearance(getApplicationContext(),
								android.R.style.TextAppearance_Large);
						txtResult3.setTypeface(null, Typeface.BOLD);
						blink(txtResult3, Color.parseColor("#2B65F0"));

					}
					result1 = PreferenceUtils.getStringPref(
							getApplicationContext(), Constant.PREF_FAV, r1, "");

					result2 = PreferenceUtils.getStringPref(
							getApplicationContext(), Constant.PREF_FAV, r2, "");

					result3 = PreferenceUtils.getStringPref(
							getApplicationContext(), Constant.PREF_FAV, r3, "");

					resultList.add(result1);
					resultList.add(result2);
					resultList.add(result3);
					Collections.sort(resultList);

					txtResult1.setText(resultList.get(0));
					txtResult2.setText(resultList.get(1));
					txtResult3.setText(resultList.get(2));

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			
//			resultImage.setImageResource(R.drawable.result_image_for_top);
			
		} else {

			result1 = PreferenceUtils.getStringPref(getApplicationContext(),
					Constant.PREF_FAV, r1, "");

			result2 = PreferenceUtils.getStringPref(getApplicationContext(),
					Constant.PREF_FAV, r2, "");

			result3 = PreferenceUtils.getStringPref(getApplicationContext(),
					Constant.PREF_FAV, r3, "");

			try {
				Date dresult1 = sdf.parse(result1);
				Date dresult2 = sdf.parse(result2);
				Date dresult3 = sdf.parse(result3);
				Date dtime = sdf.parse(t);

				if (dtime.getTime() <= dresult1.getTime()) {

					PreferenceUtils.saveStringPref(getApplicationContext(),
							Constant.PREF_FAV, r1, t);

					txtResult1.setTextAppearance(getApplicationContext(),
							android.R.style.TextAppearance_Large);
					txtResult1.setTypeface(null, Typeface.BOLD);
					blink(txtResult1, Color.parseColor("#ffc0392b"));
					
					
					PreferenceUtils.saveStringPref(
							getApplicationContext(), Constant.PREF_FAV,
							r2, result1);
					PreferenceUtils.saveStringPref(
							getApplicationContext(), Constant.PREF_FAV,
							r3, result2);
					
					resultImage.setImageResource(R.drawable.result_image);

				} else if (dtime.getTime() <= dresult2.getTime()
						&& dtime.getTime() > dresult1.getTime()) {

					PreferenceUtils.saveStringPref(getApplicationContext(),
							Constant.PREF_FAV, r2, t);

					txtResult2.setTextAppearance(getApplicationContext(),
							android.R.style.TextAppearance_Large);
					txtResult2.setTypeface(null, Typeface.BOLD);
					blink(txtResult2, Color.parseColor("#2B65F0"));
					
					PreferenceUtils.saveStringPref(
							getApplicationContext(), Constant.PREF_FAV,
							r3, result2);
					
//					resultImage.setImageResource(R.drawable.result_image);
					
				} else if (dtime.getTime() <= dresult3.getTime()
						&& dtime.getTime() > dresult2.getTime()) {

					PreferenceUtils.saveStringPref(getApplicationContext(),
							Constant.PREF_FAV, r3, t);

					txtResult3.setTextAppearance(getApplicationContext(),
							android.R.style.TextAppearance_Large);
					txtResult3.setTypeface(null, Typeface.BOLD);
					blink(txtResult3, Color.parseColor("#2B65F0"));
					
//					resultImage.setImageResource(R.drawable.result_image);

				}

				result1 = PreferenceUtils.getStringPref(
						getApplicationContext(), Constant.PREF_FAV, r1, "");

				result2 = PreferenceUtils.getStringPref(
						getApplicationContext(), Constant.PREF_FAV, r2, "");

				result3 = PreferenceUtils.getStringPref(
						getApplicationContext(), Constant.PREF_FAV, r3, "");

				resultList.add(result1);
				resultList.add(result2);
				resultList.add(result3);
				Collections.sort(resultList);

				txtResult1.setText(resultList.get(0));
				txtResult2.setText(resultList.get(1));
				txtResult3.setText(resultList.get(2));

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

//		if (!result1.equals("") && result2.equals("")) {
//			resultImage.setImageResource(R.drawable.result_image_for_top);
//		}
//
//		if (!result2.equals("") || !result3.equals("")) {
//			resultImage.setImageResource(R.drawable.result_image);
//		}
	}

	public static class MyObject implements Comparable<MyObject> {

		private Date dateTime;

		public Date getDateTime() {
			return dateTime;
		}

		public void setDateTime(Date datetime) {
			this.dateTime = datetime;
		}

		@Override
		public int compareTo(MyObject o) {
			return getDateTime().compareTo(o.getDateTime());
		}
	}

	@Override
	public void onClick(View view) {

		int id = view.getId();

		if (id == R.id.button_result_one) {

			Intent intent = new Intent(GameResultActivity.this,
					TutorialGameGridTypeActivity.class);
			intent.putExtra(Constant.GAME_TYPE, type);
			intent.putExtra(Constant.GAME_COUNT, count);
			intent.putExtra(Constant.CARD_TYPE, cardType);
			intent.putExtra(Constant.root_title, rootTitle);
			intent.putExtra(Constant.ROUTE_TYPE, routeType);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

		} else if (id == R.id.button_result_second) {
			Intent intent = new Intent(GameResultActivity.this,
					TutarialGameActivity.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
		} else if (id == R.id.button_result_third) {

			Intent intent = new Intent(GameResultActivity.this,
					HomeActivity.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
		}
	}

	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}

}
