package com.smartmux.androidapp;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.smartmux.androidapp.adapter.SearchAdapter;
import com.smartmux.androidapp.util.Constant;

public class TutorialSearchRootTitleActivity extends FragmentActivity
		implements OnClickListener, OnItemClickListener {

	private List<String> tutorialTitle = new ArrayList<String>();
	private final String Tag = TutorialSearchRootTitleActivity.class.getName();

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_item);

		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		getActionBar().setCustomView(R.layout.tutorial_actionbar);

		TextView txtActionBarTitle = (TextView) this
				.findViewById(R.id.textView_numberoftutorial);
		txtActionBarTitle.setText(getString(R.string.root_title));
		this.findViewById(R.id.Button_list).setVisibility(View.GONE);
		this.findViewById(R.id.back_content).setOnClickListener(this);
		
		TextView txtTitle = (TextView) findViewById(R.id.textView_numberoftutorial);
		txtTitle.setText("筋肉リスト");
		
		tutorialTitle.add("頭部");
		tutorialTitle.add("頚部");
		tutorialTitle.add("胸部");
		tutorialTitle.add("腹部");
		tutorialTitle.add("背部");
		tutorialTitle.add("上肢");
		tutorialTitle.add("下肢");
		
		final SearchAdapter adapter = (new SearchAdapter(this, tutorialTitle));
		ListView listview = (ListView) this
				.findViewById(R.id.listView_searchitem);
		listview.setAdapter(adapter);

		listview.setOnItemClickListener(this);

		EditText searchKey = (EditText) this
				.findViewById(R.id.editText_item_search);

		searchKey.setCursorVisible(false);
		searchKey.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2,
					int arg3) {
				adapter.getFilter().filter(cs.toString());

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {

			}
		});


	}

	@Override
	public void onClick(View v) {
		
		int id = v.getId();
		
		if(id == R.id.back_content){
			
			
			finish();
			overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
		}

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position,
			long arg3) {

		Intent intent = new Intent(TutorialSearchRootTitleActivity.this,
				TutorialSearchCatagoryTitleActivity.class);
		intent.putExtra(Constant.root_title, tutorialTitle.get(position));
		startActivity(intent);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}
	@Override
	public void onBackPressed(){
		finish();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}

}
