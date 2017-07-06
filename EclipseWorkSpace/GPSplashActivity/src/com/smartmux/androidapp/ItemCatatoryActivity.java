package com.smartmux.androidapp;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.smartmux.androidapp.adapter.ItemAdapter;
import com.smartmux.androidapp.util.Constant;
import com.smartmux.androidapp.util.PreferenceUtils;

public class ItemCatatoryActivity extends FragmentActivity implements
		OnClickListener {

	private String[] roots = new String[] { "All", "頭部","頚部","胸部","腹部","背部","上肢","下肢" };
	private final String Tag = ItemCatatoryActivity.class.getName();

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_catagory_item);

		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		getActionBar().setCustomView(R.layout.tutorial_actionbar);
		this.findViewById(R.id.Button_list).setVisibility(View.GONE);
		this.findViewById(R.id.back_content).setOnClickListener(this);
		
		TextView txtTitle = (TextView) findViewById(R.id.textView_numberoftutorial);
		txtTitle.setText(getString(R.string.btn1_title));
		
		GridView gridview = (GridView) this
				.findViewById(R.id.gridView_catagory_item);
		gridview.setAdapter(new ItemAdapter(this, roots));
		gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				
					Intent intent = new Intent(ItemCatatoryActivity.this,
							TutorialActivity.class);
					intent.putExtra(Constant.ROUTE_TYPE,
							Constant.ROUTE_VIA_1ST_BUTTON);
					intent.putExtra(Constant.root_title, roots[pos]);
					startActivity(intent);
					overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
				
				
			}
		});

		
		settingsFavShowBtn = (ToggleButton) this.findViewById(R.id.toggleButton_settings_item_two);
		settingsFavShowBtn.setOnClickListener(this);
		settingsRandomBtn =	(ToggleButton) this.findViewById(R.id.toggleButton_settings_item_one);
		settingsRandomBtn.setOnClickListener(this);
		
		boolean IsFAVShow = PreferenceUtils.getBoolPref(getApplicationContext(),
				Constant.PREF_NAME, Constant.PREF_FAV, false);
		boolean isRandom = PreferenceUtils.getBoolPref(getApplicationContext(),
				Constant.PREF_NAME, Constant.PREF_RANDOM, false);
		
		settingsFavShowBtn.setChecked(IsFAVShow);
		settingsRandomBtn.setChecked(isRandom);

	}

	private ToggleButton settingsRandomBtn,settingsFavShowBtn;
	@Override
	public void onClick(View v) {
		
		int id = v.getId();
		
		if(id == R.id.toggleButton_settings_item_two){
			
			PreferenceUtils.saveBoolPref(getApplicationContext(), Constant.PREF_NAME, Constant.PREF_FAV, settingsFavShowBtn.isChecked());
		}else if (id == R.id.toggleButton_settings_item_one){
			PreferenceUtils.saveBoolPref(getApplicationContext(), Constant.PREF_NAME, Constant.PREF_RANDOM, settingsRandomBtn.isChecked());
			
		}else if (id == R.id.back_content) {

			finish();
			overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
		}

	}
	@Override
	public void onBackPressed(){
		finish();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}
}
