package com.kanta.studio.watchout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import com.kanta.studio.watchout.utils.ConstantString;
import com.kanta.studio.watchout.utils.PreferenceUtils;

public class LevelActivity extends Activity implements OnClickListener {

	Button btnLevel1, btnLevel2, btnLevel3, btnLevel4, btnLevel5 = null;
	int mLevel = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_level);

		mLevel = PreferenceUtils.getIntPref(getApplicationContext(),
				ConstantString.PREF_NAME, ConstantString.level, 0);

		btnLevel1 = (Button) findViewById(R.id.level1);
		btnLevel2 = (Button) findViewById(R.id.level2);
		btnLevel3 = (Button) findViewById(R.id.level3);
		btnLevel4 = (Button) findViewById(R.id.level4);
		btnLevel5 = (Button) findViewById(R.id.level5);

		if (mLevel == 1) {

			btnLevel2.setVisibility(View.VISIBLE);

		} else if (mLevel == 2) {

			btnLevel2.setVisibility(View.VISIBLE);
			btnLevel3.setVisibility(View.VISIBLE);

		} else if (mLevel == 3) {

			btnLevel2.setVisibility(View.VISIBLE);
			btnLevel3.setVisibility(View.VISIBLE);
			btnLevel4.setVisibility(View.VISIBLE);
		} else if (mLevel == 4 || mLevel>4) {
			btnLevel2.setVisibility(View.VISIBLE);
			btnLevel3.setVisibility(View.VISIBLE);
			btnLevel4.setVisibility(View.VISIBLE);
			btnLevel5.setVisibility(View.VISIBLE);
		}

		btnLevel1.setOnClickListener(this);
		btnLevel2.setOnClickListener(this);
		btnLevel3.setOnClickListener(this);
		btnLevel4.setOnClickListener(this);
		btnLevel5.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();

		switch (id) {
		case R.id.level1:

			Intent intentLevel1 = new Intent(getBaseContext(),
					GameActivity.class);
			intentLevel1.putExtra(ConstantString.active_level, 1);
			startActivity(intentLevel1);
			finish();

			break;

		case R.id.level2:
			
			Intent intentLevel2 = new Intent(getBaseContext(),
					GameActivity.class);
			intentLevel2.putExtra(ConstantString.active_level, 2);
			startActivity(intentLevel2);
			finish();
			
			break;

		case R.id.level3:
			
			Intent intentLevel3 = new Intent(getBaseContext(),
					GameActivity.class);
			intentLevel3.putExtra(ConstantString.active_level, 3);
			startActivity(intentLevel3);
			finish();
			
			break;

		case R.id.level4:
			
			Intent intentLevel4 = new Intent(getBaseContext(),
					GameActivity.class);
			intentLevel4.putExtra(ConstantString.active_level, 4);
			startActivity(intentLevel4);
			finish();
			
			break;

		case R.id.level5:
			
			Intent intentLevel5 = new Intent(getBaseContext(),
					GameActivity.class);
			intentLevel5.putExtra(ConstantString.active_level, 5);
			startActivity(intentLevel5);
			finish();

			break;

		default:
			break;

		}

	}
}
