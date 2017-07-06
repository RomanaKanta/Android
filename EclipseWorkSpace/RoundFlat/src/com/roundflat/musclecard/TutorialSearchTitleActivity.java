package com.roundflat.musclecard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.roundflat.musclecard.adapter.ItemSearchAdapter;
import com.roundflat.musclecard.database.DatabaseHandler;
import com.roundflat.musclecard.util.Constant;

public class TutorialSearchTitleActivity extends Activity implements
		OnItemClickListener, OnClickListener {
	
	private List<HashMap<String, String>> tutorialTitleList = new ArrayList<HashMap<String, String>>();
	private String rootTitle;
	private String catagory;
	private String subCatagory;
	ListView listview;
	TextView backTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_search_item);

//		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//		getActionBar().setCustomView(R.layout.tutorial_actionbar);
//
//		TextView txtActionBarTitle = (TextView) this
//				.findViewById(R.id.textView_numberoftutorial);
//		txtActionBarTitle.setText(getString(R.string.title));
//		this.findViewById(R.id.Button_list).setVisibility(View.GONE);
		this.findViewById(R.id.textView_top_right).setVisibility(View.VISIBLE);
		this.findViewById(R.id.textView_top_right).setOnClickListener(this);
		this.findViewById(R.id.back_search_content).setOnClickListener(this);
		backTitle = (TextView)findViewById(R.id.TextView_back_title);
		

		if (getIntent().hasExtra(Constant.root_title)
				&& getIntent().hasExtra(Constant.category)
				&& getIntent().hasExtra(Constant.sub_category)) {

			rootTitle = getIntent().getExtras().getString(Constant.root_title);
			catagory = getIntent().getExtras().getString(Constant.category);
			
//			backTitle.setText(catagory);
			
			subCatagory = getIntent().getExtras().getString(
					Constant.sub_category);
			TextView txtTitle = (TextView) findViewById(R.id.textView_search_root_title);
//			txtTitle.setText(subCatagory);
			
			String str = subCatagory;
			if(str.length()>9){
				str = str.substring(0,9) + "...";
			}
			
			txtTitle.setText(str);
			
			
			DatabaseHandler db = new DatabaseHandler(this);

			String query = "SELECT DISTINCT " + Constant.id + ", "
					+ Constant.title + ", " + Constant.sub_title + " FROM "
					+ Constant.TABLE_TUTORIAL + " WHERE " + Constant.root_title
					+ " ='" + rootTitle + "' AND " + Constant.category + " ='"
					+ catagory + "' AND " + Constant.sub_category + " ='"
					+ subCatagory + "'";

			Cursor cursor = db.getData(query);
			if (cursor.moveToFirst()) {
				do {

					HashMap<String, String> map = new HashMap<String, String>();

					map.put(Constant.id, cursor.getString(cursor
							.getColumnIndex(Constant.id)));
					map.put(Constant.title, cursor.getString(cursor
							.getColumnIndex(Constant.title)));
					map.put(Constant.sub_title, cursor.getString(cursor
							.getColumnIndex(Constant.sub_title)));
				
					map.put(Constant.row_title, cursor.getString(cursor
							.getColumnIndex(Constant.title)));
					map.put(Constant.row_sub_title, cursor.getString(cursor
							.getColumnIndex(Constant.sub_title)));
					map.put(Constant.row_tag, "title");
					map.put(Constant.row_root, rootTitle);
					map.put(Constant.row_catagory, catagory);
					
					tutorialTitleList.add(map);
				} while (cursor.moveToNext());
			}

			db.close();
			cursor.close();

			final ItemSearchAdapter adapter = new ItemSearchAdapter(this,
					tutorialTitleList);
			 listview = (ListView) this
					.findViewById(R.id.listView_searchitem);
			listview.setAdapter(adapter);

			listview.setOnItemClickListener(this);

			EditText searchKey = (EditText) this
					.findViewById(R.id.editText_item_search);
			searchKey.setCursorVisible(true);
			searchKey.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence cs, int arg1, int arg2,
						int arg3) {
					

//					cs = "内側翼突筋";
					
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
	}

	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {

		Intent intent = new Intent(TutorialSearchTitleActivity.this,
				TutorialActivity.class);
		
		intent.putExtra(Constant.back_title,subCatagory);
		intent.putExtra(Constant.id,
				tutorialTitleList.get(position).get(Constant.id));
		intent.putExtra(Constant.ROUTE_TYPE, Constant.ROUTE_VIA_3RD_BUTTON);
		intent.putExtra(Constant.ALL_SELECT,
				"not_ALL_favorite");
		startActivity(intent);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();

		if (id == R.id.back_search_content) {

			finish();
			overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
		}else  if (id == R.id.textView_top_right) {

			Intent intent = new Intent(TutorialSearchTitleActivity.this,
					TutorialSearchRootTitleActivity.class);
			startActivity(intent);
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
