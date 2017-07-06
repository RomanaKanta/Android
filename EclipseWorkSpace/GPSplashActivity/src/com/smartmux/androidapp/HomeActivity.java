package com.smartmux.androidapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

import com.smartmux.androidapp.database.DatabaseHandler;
import com.smartmux.androidapp.model.TutorialModel;
import com.smartmux.androidapp.util.Constant;
import com.smartmux.androidapp.util.Utils;

public class HomeActivity extends Activity implements OnClickListener {

	private final String Tag = HomeActivity.class.getName();
	private String fileNmae = "data.json";
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_home);

		//getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.darker_gray)));
		//getActionBar().setTitle(getString(R.string.home));
		//getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionbar_background)));

		this.findViewById(R.id.button_item_one).setOnClickListener(this);
		this.findViewById(R.id.button_item_two).setOnClickListener(this);
		this.findViewById(R.id.button_item_three).setOnClickListener(this);
		this.findViewById(R.id.button_info).setOnClickListener(this);
		
		DatabaseHandler db = new DatabaseHandler(this);
		
		
		if(db.getRowCount() < 1){
			
			try {
				JSONArray jTutorialArray = new JSONArray(Utils.loadJSONFromAsset(
						this, fileNmae));

						for (int i = 0; i < jTutorialArray.length(); i++) {

							JSONObject obj = jTutorialArray.getJSONObject(i);
							TutorialModel tutorial = TutorialModel.fromJson(obj);
							if(!tutorial.getSub_title().equals("NIL")
									&&!tutorial.getSub_title().equals("NIL")
									&&!tutorial.getLabel_english().equals("NIL")
									&&!tutorial.getLabel_japanese().equals("NIL")
									&&!tutorial.getOption_1().equals("NIL")
									&&!tutorial.getOption_2().equals("NIL")
									&&!tutorial.getOption_3().equals("NIL")
									&&!tutorial.getOption_4().equals("NIL")
									){
								
								String title = tutorial.getTitle();
								 if (title.equals("NIL")) {
				                        //NSLog(@"%d = [%@]:[%@]",index++,_sub_category,_id);
									 title = tutorial.getSub_category();
				                    }
								 
								 tutorial.setTitle(title);
								 db.addTutorial(tutorial);
							}
							

							}
							
							

			} catch (JSONException e) {

				e.printStackTrace();
			}
		}
		
		
	}

	public void onClick(View v) {

		int id = v.getId();

		if (id == R.id.button_item_one) {

			Intent intentItemOne = new Intent(HomeActivity.this,
					ItemCatatoryActivity.class);
			startActivity(intentItemOne);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

		} else if (id == R.id.button_item_two) {
			
			
			Intent intentItemGame = new Intent(HomeActivity.this,
					TutarialGameActivity.class);
			startActivity(intentItemGame);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
		} else if (id == R.id.button_item_three) {

			Intent intentItemthree = new Intent(HomeActivity.this,
					TutorialSearchRootTitleActivity.class);
			startActivity(intentItemthree);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

		} else if (id == R.id.button_info) {

			Intent intentInfo= new Intent(HomeActivity.this,
					TutorialInfoActivity.class);
			startActivity(intentInfo);
			overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);

		}

	}
	
	
}
