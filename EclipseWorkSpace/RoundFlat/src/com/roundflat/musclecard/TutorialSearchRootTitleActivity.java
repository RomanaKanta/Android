package com.roundflat.musclecard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
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

public class TutorialSearchRootTitleActivity extends FragmentActivity implements
		OnClickListener, OnItemClickListener {

	private List<String> tutorialList = new ArrayList<String>();
	private List<String> tutorialCatagory = new ArrayList<String>();
	private List<String> tutorialSubCatagory = new ArrayList<String>();
	private List<HashMap<String, String>> tutorialRootTitle = new ArrayList<HashMap<String, String>>();
	private final String Tag = TutorialSearchRootTitleActivity.class.getName();
	private boolean seacrh = false;
	private boolean flag = false;

	EditText searchKey = null;
	ListView listview = null;
	DatabaseHandler db;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_search_item);

		db = new DatabaseHandler(this);

		TextView txtActionBarTitle = (TextView) this
				.findViewById(R.id.textView_search_root_title);
		
//		txtActionBarTitle.setText(getString(R.string.root_title));
	
		String str = getString(R.string.root_title);
		if(str.length()>9){
			str = str.substring(0,9) + "...";
		}
		
		txtActionBarTitle.setText(str);
		
		
		this.findViewById(R.id.back_search_content).setOnClickListener(this);

		tutorialList.add("頭部");
		tutorialList.add("頚部");
		tutorialList.add("胸部");
		tutorialList.add("腹部");
		tutorialList.add("背部");
		tutorialList.add("上肢");
		tutorialList.add("下肢");

		for (int i = 0; i < tutorialList.size(); i++) {
			HashMap<String, String> map = new HashMap<String, String>();

			map.put(Constant.row_title, tutorialList.get(i));
			map.put(Constant.row_sub_title, "false");
			map.put(Constant.row_tag, "root");
			map.put(Constant.row_root, tutorialList.get(i));
			map.put(Constant.row_catagory, "null");

			tutorialRootTitle.add(map);
		}
		final ItemSearchAdapter adapter = new ItemSearchAdapter(this,
				tutorialRootTitle);

		// final SearchAdapter adapter = (new SearchAdapter(this,
		// tutorialRootTitle));
		listview = (ListView) this.findViewById(R.id.listView_searchitem);
		listview.setAdapter(adapter);

		listview.setOnItemClickListener(this);

		searchKey = (EditText) this.findViewById(R.id.editText_item_search);

		searchKey.setCursorVisible(true);
		searchKey.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2,
					int arg3) {
//				cs = "内側翼突筋";
			
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

	@Override
	public void onClick(View v) {

		int id = v.getId();

		if (id == R.id.back_search_content) {

			finish();
			overridePendingTransition(R.anim.push_right_in,
					R.anim.push_right_out);
		}

	}

	// [頚部, 浅頚筋, 深頚筋, 広頚筋, 胸鎖乳突筋, 舌骨上筋群, 舌骨下筋群, 斜角筋群, 椎前筋群]

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position,
			long arg3) {

		TextView txtItem = (TextView) view
				.findViewById(R.id.textView_row_title);

		if (seacrh) {

			String text = txtItem.getText().toString();

			TextView txtItemTag = (TextView) view
					.findViewById(R.id.textView_row_tag);
			String rowTag = txtItemTag.getText().toString();

			String catagory;

			if (rowTag.equals("root")) {

				Intent intent = new Intent(
						TutorialSearchRootTitleActivity.this,
						TutorialSearchCatagoryTitleActivity.class);

				intent.putExtra(Constant.root_title, text);

				startActivity(intent);
				overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);

			} else if (rowTag.equals("catagory")
					|| rowTag.equals("subcatagory")) {

				TextView txtrootItem = (TextView) view
						.findViewById(R.id.textView_row_root);

				String root = txtrootItem.getText().toString();

				String query = "SELECT DISTINCT " + Constant.category
						+ " FROM " + Constant.TABLE_TUTORIAL + " WHERE "
						+ Constant.root_title + " ='" + root + "'";

				Cursor cursor = db.getData(query);
				if (cursor.moveToFirst()) {
					do {

						if (text.equals(cursor.getString(cursor
								.getColumnIndex(Constant.category)))) {
							flag = false;

							Intent intent = new Intent(
									TutorialSearchRootTitleActivity.this,
									TutorialSearchSubCatagoryActivity.class);
							intent.putExtra(Constant.root_title, root);
							intent.putExtra(Constant.category, text);
							startActivity(intent);
							overridePendingTransition(R.anim.push_left_in,
									R.anim.push_left_out);
							break;

						} else {
							tutorialCatagory.add(cursor.getString(cursor
									.getColumnIndex(Constant.category)));
						}

					} while (cursor.moveToNext());
				}

				flag = true;
				cursor.close();

				if (flag) {

					for (int j = 0; j < tutorialCatagory.size(); j++) {
						catagory = tutorialCatagory.get(j).toString();

						String subquery = "SELECT DISTINCT "
								+ Constant.sub_category + " FROM "
								+ Constant.TABLE_TUTORIAL + " WHERE "
								+ Constant.root_title + " ='" + root + "' AND "
								+ Constant.category + " ='" + catagory + "'";

						Cursor subcursor = db.getData(subquery);
						if (subcursor.moveToFirst()) {
							do {

								if (text.equals(subcursor.getString(subcursor
										.getColumnIndex(Constant.sub_category)))) {

									Intent intent = new Intent(
											TutorialSearchRootTitleActivity.this,
											TutorialSearchTitleActivity.class);
									intent.putExtra(Constant.root_title, root);
									intent.putExtra(Constant.category, catagory);
									intent.putExtra(
											Constant.sub_category,
											subcursor.getString(subcursor
													.getColumnIndex(Constant.sub_category)));
									startActivity(intent);
									overridePendingTransition(
											R.anim.push_left_in,
											R.anim.push_left_out);
									break;

								} 

							} while (subcursor.moveToNext());
						}

						subcursor.close();

					}
				}
				db.close();

			} else if (rowTag.equals("title")) {

				TextView txtItemid = (TextView) view
						.findViewById(R.id.textView_row_root);

				String id = txtItemid.getText().toString();

				Intent intent = new Intent(
						TutorialSearchRootTitleActivity.this,
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

			Intent intent = new Intent(TutorialSearchRootTitleActivity.this,
					TutorialSearchCatagoryTitleActivity.class);

			intent.putExtra(Constant.root_title, txtItem.getText());

			startActivity(intent);

			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
		}
	}

	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}

}
