package com.roundflat.musclecard.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.roundflat.musclecard.R;
import com.roundflat.musclecard.database.DatabaseHandler;
import com.roundflat.musclecard.util.Constant;

public class SearchAdapter extends BaseAdapter implements Filterable {
	private Context ctx;
	private List<String> items;
	private List<String> tmpItems;
	DatabaseHandler dbHandler ;


	public SearchAdapter(Context context, List<String> items) {
		super();
		this.ctx = context;
		this.items = items;
		this.tmpItems = items;
		 dbHandler = new DatabaseHandler(ctx);
	}

	public int getCount() {

		return items.size();
	}

	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}


	public View getView(final int position, View convertView, ViewGroup parent) {
		SearchViewHolder holder;
		if (convertView == null) {

			holder = new SearchViewHolder();
			LayoutInflater inflater = LayoutInflater.from(ctx);
			convertView = inflater.inflate(R.layout.search_item, parent, false);

			holder.txtItem = (TextView) convertView
					.findViewById(R.id.textView_title);

			convertView.setTag(holder);

		} else {

			holder = (SearchViewHolder) convertView.getTag();
		}

		String item = items.get(position);
		holder.txtItem.setText(item);
		return convertView;

	}

	public SearchAdapter(Context context, List<String> items,int i) {
		super();
		this.ctx = context;
		this.items = items;
		this.tmpItems = items;
	}

	
	
	@Override
	public Filter getFilter() {
		Filter filter = new Filter() {

			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint,
					FilterResults results) {

				items = (List<String>) results.values;


				notifyDataSetChanged();
			}

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {

				FilterResults results = new FilterResults();

			List<String> FilteredArrayNames = new ArrayList<String>(items.size());

				if (constraint == null || constraint.length() == 0) {

					results.values = tmpItems;
					results.count = tmpItems.size();
				} else {

					constraint = constraint.toString().toLowerCase();

					for (String title: tmpItems) {


						if (title.toLowerCase().startsWith(
								constraint.toString())) {
							FilteredArrayNames.add(title);
						}

					}

//////ADD NEW

//						String category = "SELECT DISTINCT " + Constant.category + " FROM "
//								+ Constant.TABLE_TUTORIAL + " WHERE " + Constant.category
//								+ " LIKE '" + constraint + "%'";
		
					
					//////////14-09-2015(monday)
					
					String category = "SELECT DISTINCT " + Constant.category + " FROM "
							+ Constant.TABLE_TUTORIAL + " WHERE " + Constant.root_title
							+ " = '" + constraint + "'";

						Cursor cursorcategory = dbHandler.getData(category);
						if (cursorcategory.moveToFirst()) {
							do {

								FilteredArrayNames.add(cursorcategory.getString(cursorcategory
										.getColumnIndex(Constant.category)));
							} while (cursorcategory.moveToNext());
						}

						cursorcategory.close();

//						String sub_category = "SELECT DISTINCT " + Constant.sub_category
//								+ " FROM " + Constant.TABLE_TUTORIAL + " WHERE "
//								+ Constant.sub_category + " LIKE '" + constraint + "%'";
						
						String sub_category = "SELECT DISTINCT " + Constant.sub_category
								+ " FROM " + Constant.TABLE_TUTORIAL + " WHERE "
								+ Constant.root_title + " = '" + constraint + "'";

						Cursor cursorsub_category = dbHandler.getData(sub_category);
						if (cursorsub_category.moveToFirst()) {
							do {

								FilteredArrayNames.add(cursorsub_category
										.getString(cursorsub_category
												.getColumnIndex(Constant.sub_category)));

							} while (cursorsub_category.moveToNext());
						}
						cursorsub_category.close();

//						String title = "SELECT DISTINCT " + Constant.title
//								+ " FROM " + Constant.TABLE_TUTORIAL + " WHERE "
//								+ Constant.title + " LIKE '" + constraint + "%'";
						
//						String title = "SELECT DISTINCT " + Constant.title
//								+ " FROM " + Constant.TABLE_TUTORIAL + " WHERE "
//								+ Constant.root_title + " = '" + constraint + "'";
//
//						Cursor cursortitle = dbHandler.getData(title);
//						if (cursortitle.moveToFirst()) {
//							do {
//
//								FilteredArrayNames.add(cursortitle
//										.getString(cursortitle
//												.getColumnIndex(Constant.title)));
//
//							} while (cursortitle.moveToNext());
//						}
//						cursortitle.close();
//						
//						dbHandler.close();
		
						
					

					results.count = FilteredArrayNames.size();
					results.values = FilteredArrayNames;
					Log.e("VALUES", results.values.toString());

				}

				return results;
			}
		};

		return filter;
	}

	
	
	
	private class SearchViewHolder {
		public TextView txtItem;
	}

}
