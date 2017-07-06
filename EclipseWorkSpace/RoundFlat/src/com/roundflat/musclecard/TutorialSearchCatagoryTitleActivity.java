package com.roundflat.musclecard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.roundflat.musclecard.adapter.ItemSearchAdapter;
import com.roundflat.musclecard.adapter.SearchAdapter;
import com.roundflat.musclecard.database.DatabaseHandler;
import com.roundflat.musclecard.util.Constant;

public class TutorialSearchCatagoryTitleActivity extends Activity implements
		OnItemClickListener, OnClickListener {

	private List<HashMap<String, String>> tutorialCatagoryTitle = new ArrayList<HashMap<String, String>>();
	private String rootTitle;
	ListView listview;
	TextView backTitle;
	private boolean seacrh = false;
	DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_search_item);

		// getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		// getActionBar().setCustomView(R.layout.tutorial_actionbar);

		// TextView txtActionBarTitle = (TextView) this
		// .findViewById(R.id.textView_numberoftutorial);
		// txtActionBarTitle.setText(getString(R.string.category));
		// this.findViewById(R.id.Button_list).setVisibility(View.GONE);

		this.findViewById(R.id.textView_top_right).setVisibility(View.VISIBLE);
		this.findViewById(R.id.textView_top_right).setOnClickListener(this);
		this.findViewById(R.id.back_search_content).setOnClickListener(this);
		backTitle = (TextView) findViewById(R.id.TextView_back_title);
		// backTitle.setText(getString(R.string.root_title));

		if (getIntent().hasExtra(Constant.root_title)) {
			rootTitle = getIntent().getExtras().getString(Constant.root_title);

			TextView txtTitle = (TextView) findViewById(R.id.textView_search_root_title);
			// txtTitle.setText(rootTitle);

			String str = rootTitle;
			if (str.length() > 9) {
				str = str.substring(0, 9) + "...";
			}

			txtTitle.setText(str);

			db = new DatabaseHandler(this);

			String query = "SELECT DISTINCT " + Constant.id + ", "
					+ Constant.category + ", " + Constant.sub_category + ", "
					+ Constant.title + ", " + Constant.sub_title + " FROM "
					+ Constant.TABLE_TUTORIAL + " WHERE " + Constant.root_title
					+ " ='" + rootTitle + "'";

			// String query = "SELECT DISTINCT " + Constant.category + " FROM "
			// + Constant.TABLE_TUTORIAL + " WHERE " + Constant.root_title
			// + " ='" + rootTitle + "'";

			Cursor cursor = db.getData(query);
			if (cursor.moveToFirst()) {
				do {

					HashMap<String, String> map = new HashMap<String, String>();
					
					

//					map.put(Constant.row_title, cursor.getString(cursor
//							.getColumnIndex(Constant.category)));
//					map.put(Constant.row_sub_title, "false");
//					map.put(Constant.row_tag, "catagory");
//					map.put(Constant.row_root, rootTitle);
//					map.put(Constant.row_catagory, cursor.getString(cursor
//							.getColumnIndex(Constant.category)));
					
					String cat  =  cursor.getString(cursor
							.getColumnIndex(Constant.category));
					
					
					String subCat = cursor.getString(cursor
							.getColumnIndex(Constant.sub_category));

					String title = cursor.getString(cursor
							.getColumnIndex(Constant.title));

					String subTitle = cursor.getString(cursor
							.getColumnIndex(Constant.sub_title));

					map.put(Constant.id, cursor.getString(cursor
							.getColumnIndex(Constant.id)));

					map.put(Constant.row_title, cat);
					
					map.put(Constant.row_root, rootTitle);
					map.put(Constant.row_catagory,cat);
//					map.put(Constant.title, title);
					map.put(Constant.sub_title, subTitle);

					if (subCat.equals(cat)) {
						map.put(Constant.row_tag, "title");
						map.put(Constant.row_sub_title, subTitle);
					} else {

						map.put(Constant.row_tag, "catagory");
						map.put(Constant.row_sub_title, "false");
					}

					tutorialCatagoryTitle.add(map);
				} while (cursor.moveToNext());
			}

			db.close();
			cursor.close();
			
			
			 for(int i=0;i<tutorialCatagoryTitle.size();i++){
			        for(int j=i+1;j<tutorialCatagoryTitle.size();j++){
			            if(tutorialCatagoryTitle.get(i).get(Constant.row_title).equals(tutorialCatagoryTitle.get(j).get(Constant.row_title))){
			            	tutorialCatagoryTitle.remove(j);
			                j--;
			            }
			        }
			    }
			

			final ItemSearchAdapter adapter = new ItemSearchAdapter(this,
					tutorialCatagoryTitle);

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

			TextView txtrootItem = (TextView) view
					.findViewById(R.id.textView_row_root);

			String root = txtrootItem.getText().toString();

			if (rowTag.equals("catagory")) {
				Intent intent = new Intent(
						TutorialSearchCatagoryTitleActivity.this,
						TutorialSearchSubCatagoryActivity.class);
				intent.putExtra(Constant.root_title, root);
				intent.putExtra(Constant.category, text);
				startActivity(intent);
				overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);
			} else if (rowTag.equals("subcatagory")) {

				TextView txtcatagoryItem = (TextView) view
						.findViewById(R.id.textView_row_catagory);

				String catagory = txtcatagoryItem.getText().toString();

				String subquery = "SELECT DISTINCT " + Constant.sub_category
						+ " FROM " + Constant.TABLE_TUTORIAL + " WHERE "
						+ Constant.root_title + " ='" + root + "' AND "
						+ Constant.category + " ='" + catagory + "'";

				Cursor subcursor = db.getData(subquery);
				if (subcursor.moveToFirst()) {
					do {

						if (text.equals(subcursor.getString(subcursor
								.getColumnIndex(Constant.sub_category)))) {

							Intent intent = new Intent(
									TutorialSearchCatagoryTitleActivity.this,
									TutorialSearchTitleActivity.class);
							intent.putExtra(Constant.root_title, root);
							intent.putExtra(Constant.category, catagory);
							intent.putExtra(
									Constant.sub_category,
									subcursor.getString(subcursor
											.getColumnIndex(Constant.sub_category)));
							startActivity(intent);
							overridePendingTransition(R.anim.push_left_in,
									R.anim.push_left_out);
							break;

						}

					} while (subcursor.moveToNext());
				}

				subcursor.close();

			} else if (rowTag.equals("title")) {

				TextView txtItemid = (TextView) view
						.findViewById(R.id.textView_row_root);

				String id = txtItemid.getText().toString();

				Intent intent = new Intent(
						TutorialSearchCatagoryTitleActivity.this,
						TutorialActivity.class);
				intent.putExtra(Constant.id, id);

				intent.putExtra(Constant.ROUTE_TYPE,
						Constant.ROUTE_VIA_3RD_BUTTON);
				intent.putExtra(Constant.ALL_SELECT, "not_ALL_favorite");
				startActivity(intent);
				overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);

			}
			//
			// if (rowTag.equals("root")) {
			//
			// Intent intent = new Intent(
			// TutorialSearchCatagoryTitleActivity.this,
			// TutorialSearchCatagoryTitleActivity.class);
			//
			// intent.putExtra(Constant.root_title, text);
			//
			// startActivity(intent);
			// overridePendingTransition(R.anim.push_left_in,
			// R.anim.push_left_out);
			//
			// } else if (rowTag.equals("catagory")
			// || rowTag.equals("subcatagory")) {
			//
			// TextView txtrootItem = (TextView) view
			// .findViewById(R.id.textView_row_root);
			//
			// String root = txtrootItem.getText().toString();
			//
			// String query = "SELECT DISTINCT " + Constant.category
			// + " FROM " + Constant.TABLE_TUTORIAL + " WHERE "
			// + Constant.root_title + " ='" + root + "'";
			//
			// Cursor cursor = db.getData(query);
			// if (cursor.moveToFirst()) {
			// do {
			//
			// if (text.equals(cursor.getString(cursor
			// .getColumnIndex(Constant.category)))) {
			// flag = false;
			//
			// Intent intent = new Intent(
			// TutorialSearchCatagoryTitleActivity.this,
			// TutorialSearchSubCatagoryActivity.class);
			// intent.putExtra(Constant.root_title, root);
			// intent.putExtra(Constant.category, text);
			// startActivity(intent);
			// overridePendingTransition(R.anim.push_left_in,
			// R.anim.push_left_out);
			// break;
			//
			// } else {
			// tutorialCatagory.add(cursor.getString(cursor
			// .getColumnIndex(Constant.category)));
			// }
			//
			// } while (cursor.moveToNext());
			// }
			//
			// flag = true;
			// cursor.close();
			//
			// if (flag) {
			//
			// for (int j = 0; j < tutorialCatagory.size(); j++) {
			// catagory = tutorialCatagory.get(j).toString();
			//
			// String subquery = "SELECT DISTINCT "
			// + Constant.sub_category + " FROM "
			// + Constant.TABLE_TUTORIAL + " WHERE "
			// + Constant.root_title + " ='" + root + "' AND "
			// + Constant.category + " ='" + catagory + "'";
			//
			// Cursor subcursor = db.getData(subquery);
			// if (subcursor.moveToFirst()) {
			// do {
			//
			// if (text.equals(subcursor.getString(subcursor
			// .getColumnIndex(Constant.sub_category)))) {
			//
			// Intent intent = new Intent(
			// TutorialSearchCatagoryTitleActivity.this,
			// TutorialSearchTitleActivity.class);
			// intent.putExtra(Constant.root_title, root);
			// intent.putExtra(Constant.category, catagory);
			// intent.putExtra(
			// Constant.sub_category,
			// subcursor.getString(subcursor
			// .getColumnIndex(Constant.sub_category)));
			// startActivity(intent);
			// overridePendingTransition(
			// R.anim.push_left_in,
			// R.anim.push_left_out);
			// break;
			//
			// } else {
			// tutorialSubCatagory
			// .add(subcursor.getString(cursor
			// .getColumnIndex(Constant.category)));
			// }
			//
			// } while (subcursor.moveToNext());
			// }
			//
			// subcursor.close();
			//
			// }
			// }
			// db.close();
			//
			// } else if (rowTag.equals("title")) {
			//
			// TextView txtItemid = (TextView) view
			// .findViewById(R.id.textView_row_root);
			//
			// String id = txtItemid.getText().toString();
			//
			// Intent intent = new Intent(
			// TutorialSearchCatagoryTitleActivity.this,
			// TutorialActivity.class);
			// intent.putExtra(Constant.id, id);
			//
			// intent.putExtra(Constant.ROUTE_TYPE,
			// Constant.ROUTE_VIA_3RD_BUTTON);
			// intent.putExtra(Constant.ALL_SELECT, "not_ALL_favorite");
			// startActivity(intent);
			// overridePendingTransition(R.anim.push_left_in,
			// R.anim.push_left_out);
			//
			// }
			//
			//
		} else {

			
			if (rowTag.equals("catagory")) {
				
			Intent intent = new Intent(
					TutorialSearchCatagoryTitleActivity.this,
					TutorialSearchSubCatagoryActivity.class);
			intent.putExtra(Constant.root_title, rootTitle);
			intent.putExtra(Constant.category,
					tutorialCatagoryTitle.get(position).get(Constant.row_title));
			startActivity(intent);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			} else if (rowTag.equals("title")) {
				
				Intent intent = new Intent(
						TutorialSearchCatagoryTitleActivity.this,
						TutorialActivity.class);

				intent.putExtra(Constant.back_title, Constant.sub_category);
				intent.putExtra(Constant.id,
						tutorialCatagoryTitle.get(position).get(Constant.id));
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

		if (id == R.id.textView_top_right || id == R.id.back_search_content) {

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
