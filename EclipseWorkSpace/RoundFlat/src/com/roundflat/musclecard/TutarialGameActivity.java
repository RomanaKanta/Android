package com.roundflat.musclecard;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ToggleButton;

import com.roundflat.musclecard.adapter.ItemAdapter;
import com.roundflat.musclecard.database.DatabaseHandler;
import com.roundflat.musclecard.model.TutorialModel;
import com.roundflat.musclecard.util.Constant;
import com.roundflat.musclecard.util.PreferenceUtils;

public class TutarialGameActivity extends FragmentActivity implements
		OnClickListener {

	private ToggleButton settingsFavBtn;
	private String[] roots = new String[] { "All", "頭部", "頚部", "胸部", "腹部",
			"背部", "上肢", "下肢" };
	private final String Tag = TutarialGameActivity.class.getName();
	int count = 9;
	String type = Constant.EASY;
	private int cardType = 0;
	private DatabaseHandler db;
	private List<TutorialModel> notFavList = new ArrayList<TutorialModel>();
	private List<TutorialModel> allList = new ArrayList<TutorialModel>();

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_game);

		this.findViewById(R.id.layout_back_content).setOnClickListener(this);

		db = new DatabaseHandler(this);

		GridView gridview = (GridView) this
				.findViewById(R.id.gridView_catagory_item);
		gridview.setAdapter(new ItemAdapter(this, roots));
		gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {

				checking(roots[pos]);

//				Toast.makeText(getApplicationContext(),
//						"not fav = " + notFavList.size(), 4000).show();

				Intent intent = new Intent(TutarialGameActivity.this,
						TutorialGameGridTypeActivity.class);
				intent.putExtra(Constant.ROUTE_TYPE,
						Constant.ROUTE_VIA_1ST_BUTTON);
				intent.putExtra(Constant.GAME_TYPE, type);
				intent.putExtra(Constant.GAME_COUNT, count);
				intent.putExtra(Constant.CARD_TYPE, cardType);
				intent.putExtra(Constant.root_title, roots[pos]);
				 startActivity(intent);
				overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);

			}
		});

		settingsFavBtn = (ToggleButton) this
				.findViewById(R.id.toggleButton_settings_fav_item);
		settingsFavBtn.setOnClickListener(this);

		this.findViewById(R.id.segmented_btn_level_easy).setOnClickListener(
				this);
		this.findViewById(R.id.segmented_btn_level_medium).setOnClickListener(
				this);
		this.findViewById(R.id.segmented_btn_level_difficult)
				.setOnClickListener(this);

		this.findViewById(R.id.segmented_btn_front).setOnClickListener(this);
		this.findViewById(R.id.segmented_btn_rear).setOnClickListener(this);

		boolean IsFAV = PreferenceUtils.getBoolPref(getApplicationContext(),
				Constant.PREF_NAME, Constant.PREF_FAV, false);

		settingsFavBtn.setChecked(IsFAV);
	}

	private void checking(String root) {
		String query;
		String queryall;
		if (root.equals("All")) {

			query = "SELECT DISTINCT * FROM " + Constant.TABLE_TUTORIAL
					+ " WHERE title != 'NIL'" + " AND " + Constant.isFav
					+ "== 1";

		} else {
			query = "SELECT DISTINCT * FROM " + Constant.TABLE_TUTORIAL
					+ " WHERE " + Constant.root_title + " ='" + root
					+ "' AND title != 'NIL'  AND " + Constant.isFav + "!= 1";
		}

		notFavList = db.getAllTutorials(query);

		if (root.equals("All")) {

			queryall = "SELECT DISTINCT * FROM " + Constant.TABLE_TUTORIAL
					+ " WHERE title != 'NIL'";

		} else {
			queryall = "SELECT DISTINCT * FROM " + Constant.TABLE_TUTORIAL
					+ " WHERE " + Constant.root_title + " ='" + root + "' ";
		}

		allList = db.getAllTutorials(queryall);
	}

	@Override
	public void onClick(View v) {

		int id = v.getId();

		if (id == R.id.layout_back_content) {

			finish();
			overridePendingTransition(R.anim.push_right_in,
					R.anim.push_right_out);
		} else if (id == R.id.segmented_btn_level_easy) {

			count = 9;
			type = Constant.EASY;

		} else if (id == R.id.segmented_btn_level_medium) {

			count = 12;
			type = Constant.MEDIUM;

		} else if (id == R.id.segmented_btn_level_difficult) {

			count = 24;
			type = Constant.DIFFICULT;

		} else if (id == R.id.segmented_btn_front) {

			cardType = 0;
		} else if (id == R.id.segmented_btn_rear) {

			cardType = 1;
		} else if (id == R.id.toggleButton_settings_fav_item) {

			PreferenceUtils.saveBoolPref(getApplicationContext(),
					Constant.PREF_NAME, Constant.PREF_FAV,
					settingsFavBtn.isChecked());
		}

	}

	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}

}
