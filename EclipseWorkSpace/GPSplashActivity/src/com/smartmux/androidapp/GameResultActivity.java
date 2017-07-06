package com.smartmux.androidapp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.smartmux.androidapp.util.Constant;
import com.smartmux.androidapp.util.PreferenceUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class GameResultActivity extends Activity implements OnClickListener {

	private String type;
	private int count;
	private int cardType;
	private String rootTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.game_result);

		if (getIntent().hasExtra("time")) {

			type = getIntent().getExtras().getString(Constant.GAME_TYPE);
			count = getIntent().getExtras().getInt(Constant.GAME_COUNT);
			cardType = getIntent().getExtras().getInt(Constant.CARD_TYPE);
			rootTitle = getIntent().getExtras().getString(Constant.root_title);

			String time = getIntent().getExtras().getString("time");

			TextView textView_result_duration = (TextView) findViewById(R.id.textView_result_duration);
			textView_result_duration.setText("Duration: " + time);

			String result1 = PreferenceUtils.getStringPref(
					getApplicationContext(), Constant.PREF_FAV,
					Constant.RESULT1, "");

			String result2 = PreferenceUtils.getStringPref(
					getApplicationContext(), Constant.PREF_FAV,
					Constant.RESULT2, "");

			String result3 = PreferenceUtils.getStringPref(
					getApplicationContext(), Constant.PREF_FAV,
					Constant.RESULT3, "");

			if (result1.equals("")) {

				PreferenceUtils.saveStringPref(getApplicationContext(),
						Constant.PREF_FAV, Constant.RESULT1, time);
			} else if (result2.equals("")) {

				PreferenceUtils.saveStringPref(getApplicationContext(),
						Constant.PREF_FAV, Constant.RESULT2, time);
			} else if (result3.equals("")) {
				PreferenceUtils.saveStringPref(getApplicationContext(),
						Constant.PREF_FAV, Constant.RESULT3, time);

			}

			result1 = PreferenceUtils.getStringPref(getApplicationContext(),
					Constant.PREF_FAV, Constant.RESULT1, "");

			result2 = PreferenceUtils.getStringPref(getApplicationContext(),
					Constant.PREF_FAV, Constant.RESULT2, "");

			result3 = PreferenceUtils.getStringPref(getApplicationContext(),
					Constant.PREF_FAV, Constant.RESULT3, "");

			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
			try {
				Date dresult1 = sdf.parse(result1);
				Date dresult2 = sdf.parse(result2);
				Date dresult3 = sdf.parse(result3);
				Date dtime = sdf.parse(time);
				
				if(dtime.getTime()<dresult1.getTime()){
					
					PreferenceUtils.saveStringPref(getApplicationContext(),
							Constant.PREF_FAV, Constant.RESULT1, time);
				}else if(dtime.getTime()<dresult2.getTime()){
					
					PreferenceUtils.saveStringPref(getApplicationContext(),
							Constant.PREF_FAV, Constant.RESULT2, time);
				}else if(dtime.getTime()<dresult2.getTime()){
					
					PreferenceUtils.saveStringPref(getApplicationContext(),
							Constant.PREF_FAV, Constant.RESULT3, time);
				}
				result1 = PreferenceUtils.getStringPref(getApplicationContext(),
						Constant.PREF_FAV, Constant.RESULT1, "");

				result2 = PreferenceUtils.getStringPref(getApplicationContext(),
						Constant.PREF_FAV, Constant.RESULT2, "");

				result3 = PreferenceUtils.getStringPref(getApplicationContext(),
						Constant.PREF_FAV, Constant.RESULT3, "");
				List<Date> resultList = new ArrayList<Date>();
				resultList.add(dresult1);
				resultList.add(dresult2);
				resultList.add(dresult3);
				Collections.sort(resultList);
				
				TextView txtResult1 = (TextView) findViewById(R.id.textView_result_first);
				TextView txtResult2 = (TextView) findViewById(R.id.textView_result_second);
				TextView txtResult3 = (TextView) findViewById(R.id.textView_result_third);
				
				txtResult1.setText(sdf.format(resultList.get(0)));
				txtResult2.setText(sdf.format(resultList.get(1)));
				txtResult3.setText(sdf.format(resultList.get(2)));
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		findViewById(R.id.button_result_one).setOnClickListener(this);
		findViewById(R.id.button_result_second).setOnClickListener(this);
		findViewById(R.id.button_result_third).setOnClickListener(this);

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
	public void onBackPressed(){
		finish();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}

}
