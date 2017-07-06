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

import com.smartmux.androidapp.adapter.ItemAdapter;
import com.smartmux.androidapp.util.Constant;

public class TutarialGameActivity extends FragmentActivity implements
		OnClickListener {

	private String[] roots = new String[] { "All", "頭部", "頚部", "胸部", "腹部",
			"背部", "上肢", "下肢" };
	private final String Tag = TutarialGameActivity.class.getName();
	int count = 9;
	String type = Constant.EASY;
	private int cardType = 0;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);

		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		getActionBar().setCustomView(R.layout.tutorial_actionbar);
		this.findViewById(R.id.Button_list).setVisibility(View.GONE);
		this.findViewById(R.id.back_content).setOnClickListener(this);

		GridView gridview = (GridView) this
				.findViewById(R.id.gridView_catagory_item);
		gridview.setAdapter(new ItemAdapter(this, roots));
		gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {

				Intent intent = new Intent(TutarialGameActivity.this,
						TutorialGameGridTypeActivity.class);
				intent.putExtra(Constant.GAME_TYPE, type);
				intent.putExtra(Constant.GAME_COUNT, count);
				intent.putExtra(Constant.CARD_TYPE, cardType);
				intent.putExtra(Constant.root_title, roots[pos]);
				startActivity(intent);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

			}
		});

		this.findViewById(R.id.segmented_btn_level_easy).setOnClickListener(
				this);
		this.findViewById(R.id.segmented_btn_level_medium).setOnClickListener(
				this);
		this.findViewById(R.id.segmented_btn_level_difficult)
				.setOnClickListener(this);

		this.findViewById(R.id.segmented_btn_front).setOnClickListener(this);
		this.findViewById(R.id.segmented_btn_rear).setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {

		int id = v.getId();

		if (id == R.id.back_content) {

			finish();
			overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
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
		}

	}
	
	@Override
	public void onBackPressed(){
		finish();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}

}
