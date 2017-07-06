package com.roundflat.musclecard;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.roundflat.musclecard.adapter.ItemAdapter;
import com.roundflat.musclecard.database.DatabaseHandler;
import com.roundflat.musclecard.model.TutorialModel;
import com.roundflat.musclecard.util.Constant;
import com.roundflat.musclecard.util.PreferenceUtils;

public class ItemCatatoryActivity extends FragmentActivity implements
		OnClickListener {

	private String[] roots = new String[] { "All", "頭部","頚部","胸部","腹部","背部","上肢","下肢" };
	private final String Tag = ItemCatatoryActivity.class.getName();
	private DatabaseHandler db;
	private List<TutorialModel> favList = new ArrayList<TutorialModel>();
	private List<TutorialModel> allList = new ArrayList<TutorialModel>();
	
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_catagory_item);

		this.findViewById(R.id.back_content).setOnClickListener(this);
		
		TextView txtTitle = (TextView) findViewById(R.id.textView_page_title);
		txtTitle.setText(getString(R.string.btn1_title));
		
		db = new DatabaseHandler(this);
		
		GridView gridview = (GridView) this
				.findViewById(R.id.gridView_catagory_item);
		gridview.setAdapter(new ItemAdapter(this, roots));
		gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				checking (roots[pos]);
				
				Intent intent = new Intent(ItemCatatoryActivity.this,
						TutorialActivity.class);
				intent.putExtra(Constant.ROUTE_TYPE,
						Constant.ROUTE_VIA_1ST_BUTTON);
				intent.putExtra(Constant.root_title, roots[pos]);

				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
				if(settingsFavShowBtn.isChecked()){
					if(favList.size()!=allList.size()){
						
						intent.putExtra(Constant.ALL_SELECT,
								"not_ALL_favorite");
						
						startActivity(intent);
						
					}else{
						
						intent.putExtra(Constant.ALL_SELECT,
								"ALL_favorite");
						startActivity(intent);
						
					}
				}else{

					intent.putExtra(Constant.ALL_SELECT,
							"not_ALL_favorite");
					
					startActivity(intent);
				}
				
				
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
	
	
private void checking (String root){
	String query;
	String queryall ;
	if(root.equals("All")){
		
		query = "SELECT DISTINCT * FROM "
				+ Constant.TABLE_TUTORIAL+" WHERE title != 'NIL'"+" AND "+Constant.isFav +"== 1";
		
	}else{
		 query = "SELECT DISTINCT * FROM "
					+ Constant.TABLE_TUTORIAL + " WHERE " + Constant.root_title
					+ " ='" + root + "' AND title != 'NIL'  AND "+Constant.isFav +"== 1";
	}

	favList = db.getAllTutorials(query);
	
	
	if(root.equals("All")){
		
		queryall = "SELECT DISTINCT * FROM "
				+ Constant.TABLE_TUTORIAL+" WHERE title != 'NIL'";
		
	}else{
		 queryall = "SELECT DISTINCT * FROM "
					+ Constant.TABLE_TUTORIAL + " WHERE " + Constant.root_title
					+ " ='" + root +"' ";	
	}

	allList = db.getAllTutorials(queryall);
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
