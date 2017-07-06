package com.roundflat.musclecard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

public class TutorialSearchSubCatagoryActivity extends Activity implements
		OnItemClickListener, OnClickListener {

	// private List<String> tutorialSubCatagoryTitle = new ArrayList<String>();
	private List<HashMap<String, String>> tutorialSubCatagoryTitle = new ArrayList<HashMap<String, String>>();
	ListView listview;
	TextView backTitle;
	private String rootTitle, catagory;
	private boolean seacrh = false;
	DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_search_item);

		// getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		// getActionBar().setCustomView(R.layout.tutorial_actionbar);
		//
		// TextView txtActionBarTitle = (TextView) this
		// .findViewById(R.id.textView_numberoftutorial);
		// txtActionBarTitle.setText(getString(R.string.sub_category));
		// this.findViewById(R.id.Button_list).setVisibility(View.GONE);

		this.findViewById(R.id.textView_top_right).setVisibility(View.VISIBLE);
		this.findViewById(R.id.textView_top_right).setOnClickListener(this);
		this.findViewById(R.id.back_search_content).setOnClickListener(this);
		backTitle = (TextView) findViewById(R.id.TextView_back_title);

		if (getIntent().hasExtra(Constant.root_title)
				&& getIntent().hasExtra(Constant.category)) {

			rootTitle = getIntent().getExtras().getString(Constant.root_title);
			catagory = getIntent().getExtras().getString(Constant.category);
			db = new DatabaseHandler(this);

//			backTitle.setText(rootTitle);

			TextView txtTitle = (TextView) findViewById(R.id.textView_search_root_title);
			// txtTitle.setText(rootTitle);
//			txtTitle.setText(catagory);
			String str = catagory;
			if(str.length()>9){
				str = str.substring(0,9) + "...";
			}
			
			txtTitle.setText(str);


			String query = "SELECT DISTINCT " + Constant.id + ", "
					+ Constant.sub_category + ", " + Constant.title + ", "
					+ Constant.sub_title + " FROM " + Constant.TABLE_TUTORIAL
					+ " WHERE " + Constant.root_title + " ='" + rootTitle
					+ "' AND " + Constant.category + " ='" + catagory + "'";

			// String query = "SELECT DISTINCT " + Constant.sub_category
			// + " FROM " + Constant.TABLE_TUTORIAL + " WHERE "
			// + Constant.root_title + " ='" + rootTitle + "' AND "
			// + Constant.category + " ='" + catagory + "'";

			Cursor cursor = db.getData(query);
			if (cursor.moveToFirst()) {
				do {

					HashMap<String, String> map = new HashMap<String, String>();

					// map.put(Constant.row_title, cursor.getString(cursor
					// .getColumnIndex(Constant.sub_category)));
					// map.put(Constant.row_sub_title, "false");
					// map.put(Constant.row_tag, "subcatagory");
					// map.put(Constant.row_root, rootTitle);
					// map.put(Constant.row_catagory, catagory);
					//

					String subCat = cursor.getString(cursor
							.getColumnIndex(Constant.sub_category));

					String title = cursor.getString(cursor
							.getColumnIndex(Constant.title));

					String subTitle = cursor.getString(cursor
							.getColumnIndex(Constant.sub_title));

					map.put(Constant.id, cursor.getString(cursor
							.getColumnIndex(Constant.id)));

					map.put(Constant.row_title, subCat);
					// map.put(Constant.row_sub_title, "false");
					// map.put(Constant.row_tag, "subcatagory");
					map.put(Constant.row_root, rootTitle);
					map.put(Constant.row_catagory, catagory);
					map.put(Constant.title, title);
					map.put(Constant.sub_title, subTitle);

					if (title.equals(subCat)) {
						map.put(Constant.row_tag, "title");
						map.put(Constant.row_sub_title, subTitle);
					} else {

						map.put(Constant.row_tag, "subcatagory");
						map.put(Constant.row_sub_title, "false");
					}
					
					Log.d("item ", map.toString());
					tutorialSubCatagoryTitle.add(map);
				} while (cursor.moveToNext());
			}

			db.close();
			cursor.close();

			
			 for(int i=0;i<tutorialSubCatagoryTitle.size();i++){
			        for(int j=i+1;j<tutorialSubCatagoryTitle.size();j++){
			            if(tutorialSubCatagoryTitle.get(i).get(Constant.row_title).equals(tutorialSubCatagoryTitle.get(j).get(Constant.row_title))){
			            	tutorialSubCatagoryTitle.remove(j);
			                j--;
			            }
			        }
			    }
			
		        
			final ItemSearchAdapter adapter = new ItemSearchAdapter(this,
					tutorialSubCatagoryTitle);
			

			listview = (ListView) this.findViewById(R.id.listView_searchitem);
			listview.setAdapter(adapter);

			listview.setOnItemClickListener(this);

			EditText searchKey = (EditText) this
					.findViewById(R.id.editText_item_search);
			searchKey.setCursorVisible(true);
			searchKey.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence cs, int arg1, int arg2,
						int arg3) {

					// cs = "内側翼突筋";

					seacrh = true;
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
	public void onItemClick(AdapterView<?> arg0, View view, int position,
			long arg3) {
		TextView txtItem = (TextView) view
				.findViewById(R.id.textView_row_title);
		TextView txtItemTag = (TextView) view
				.findViewById(R.id.textView_row_tag);
		String rowTag = txtItemTag.getText().toString();

		if (seacrh) {

			String text = txtItem.getText().toString();

			if (rowTag.equals("subcatagory")) {

				TextView txtrootItem = (TextView) view
						.findViewById(R.id.textView_row_root);

				String root = txtrootItem.getText().toString();

				TextView txtcatagoryItem = (TextView) view
						.findViewById(R.id.textView_row_catagory);

				String catagory = txtcatagoryItem.getText().toString();

				Intent intent = new Intent(
						TutorialSearchSubCatagoryActivity.this,
						TutorialSearchTitleActivity.class);
				intent.putExtra(Constant.root_title, root);
				intent.putExtra(Constant.category, catagory);
				intent.putExtra(Constant.sub_category, text);
				startActivity(intent);
				overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);

			} else if (rowTag.equals("title")) {

				TextView txtItemid = (TextView) view
						.findViewById(R.id.textView_row_root);

				String id = txtItemid.getText().toString();

				Intent intent = new Intent(
						TutorialSearchSubCatagoryActivity.this,
						TutorialActivity.class);
				intent.putExtra(Constant.id, id);

				intent.putExtra(Constant.ROUTE_TYPE,
						Constant.ROUTE_VIA_3RD_BUTTON);
				intent.putExtra(Constant.ALL_SELECT, "not_ALL_favorite");
				startActivity(intent);
				overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);

			}
		} else {
			if (rowTag.equals("subcatagory")) {
				
				Intent intent = new Intent(
						TutorialSearchSubCatagoryActivity.this,
						TutorialSearchTitleActivity.class);
				intent.putExtra(Constant.root_title, rootTitle);
				intent.putExtra(Constant.category, catagory);
				intent.putExtra(Constant.sub_category, tutorialSubCatagoryTitle
						.get(position).get(Constant.row_title));
				startActivity(intent);
				overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);
				
			} else if (rowTag.equals("title")) {
				
				Intent intent = new Intent(
						TutorialSearchSubCatagoryActivity.this,
						TutorialActivity.class);

				intent.putExtra(Constant.back_title, Constant.sub_category);
				intent.putExtra(Constant.id,
						tutorialSubCatagoryTitle.get(position).get(Constant.id));
				intent.putExtra(Constant.ROUTE_TYPE,
						Constant.ROUTE_VIA_3RD_BUTTON);
				intent.putExtra(Constant.ALL_SELECT, "not_ALL_favorite");
				startActivity(intent);
				overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);
				
			}
		}
	}

	@Override
	public void onClick(View v) {

		int id = v.getId();

		if (id == R.id.back_search_content) {

			finish();
			overridePendingTransition(R.anim.push_right_in,
					R.anim.push_right_out);
		} else if (id == R.id.textView_top_right) {

			Intent intent = new Intent(TutorialSearchSubCatagoryActivity.this,
					TutorialSearchRootTitleActivity.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.push_right_in,
					R.anim.push_right_out);
		}

	}

	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}

}
