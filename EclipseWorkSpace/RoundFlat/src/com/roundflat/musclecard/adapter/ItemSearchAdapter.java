package com.roundflat.musclecard.adapter;

import java.util.ArrayList;
import java.util.HashMap;
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
import android.widget.Toast;

import com.roundflat.musclecard.R;
import com.roundflat.musclecard.database.DatabaseHandler;
import com.roundflat.musclecard.util.Constant;

public class ItemSearchAdapter extends BaseAdapter implements Filterable {
	private Context ctx;
	private List<HashMap<String, String>> items;
	private List<HashMap<String, String>> tmpItems;
	DatabaseHandler dbHandler;

	public ItemSearchAdapter(Context context,
			List<HashMap<String, String>> items) {
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

			holder.txtTitle = (TextView) convertView
					.findViewById(R.id.textView_row_title);

			holder.txtSubTitle = (TextView) convertView
					.findViewById(R.id.textView_row_subtitle);

			holder.txtTag = (TextView) convertView
					.findViewById(R.id.textView_row_tag);

			holder.txtRoot = (TextView) convertView
					.findViewById(R.id.textView_row_root);
			
			holder.txtCatagory = (TextView) convertView
					.findViewById(R.id.textView_row_catagory);

			convertView.setTag(holder);

		} else {

			holder = (SearchViewHolder) convertView.getTag();
		}

		if ((items.get(position).get(Constant.row_sub_title)).equals("false")) {
			holder.txtSubTitle.setVisibility(View.GONE);
		} else {
			holder.txtSubTitle.setVisibility(View.VISIBLE);
		}

			// Log.d("values ",
			// items.get(position).get(Constant.title)+"  "+items.get(position).get(Constant.sub_title));
			holder.txtTitle
					.setText(items.get(position).get(Constant.row_title));
			holder.txtSubTitle.setText(items.get(position).get(
					Constant.row_sub_title));
			holder.txtTag.setText(items.get(position).get(Constant.row_tag));
			holder.txtRoot.setText(items.get(position).get(Constant.row_root));
			holder.txtCatagory.setText(items.get(position).get(Constant.row_catagory));

		

		return convertView;

	}

	@Override
	public Filter getFilter() {
		Filter filter = new Filter() {

			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint,
					FilterResults results) {

				items = (List<HashMap<String, String>>) results.values;

				notifyDataSetChanged();
			}

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {

				FilterResults results = new FilterResults();
				List<HashMap<String, String>> FilteredArrayNames = new ArrayList<HashMap<String, String>>();

				if (constraint == null || constraint.length() == 0) {
					results.values = tmpItems;
					results.count = tmpItems.size();
				} else {

					constraint = constraint.toString().toLowerCase();

					for (int i = 0; i < tmpItems.size(); i++) {

						if (tmpItems.get(i).get(Constant.row_title)
								.toLowerCase()
								.startsWith(constraint.toString())) {
							FilteredArrayNames.add(tmpItems.get(i));

						

					String root = FilteredArrayNames.get(0)
							.get(Constant.row_root).toString();

					String rowtag = FilteredArrayNames.get(0)
							.get(Constant.row_tag).toString();

					if (rowtag.equals("root")) {
						
						FilteredArrayNames.addAll(getCatagory(root));

						FilteredArrayNames.addAll(getSubCatagory(root,rowtag,null));

						FilteredArrayNames.addAll(getTitle(root,rowtag,null,null));
						
					} else if (rowtag.equals("catagory")) {

						String catagory = FilteredArrayNames.get(0)
								.get(Constant.row_title).toString();
						
						FilteredArrayNames.addAll(getSubCatagory(root,rowtag,catagory));

						FilteredArrayNames.addAll(getTitle(root,rowtag,catagory,null));
						
					} else if (rowtag.equals("subcatagory")) {
						
						String subcatagory = FilteredArrayNames.get(0)
								.get(Constant.row_title).toString();
						
						FilteredArrayNames.addAll(getTitle(root,rowtag,null,subcatagory));

					}
						}

					}
				

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
		public TextView txtTitle;
		public TextView txtSubTitle;
		public TextView txtTag;
		public TextView txtRoot;
		public TextView txtCatagory;
	}

	private List<HashMap<String, String>> getCatagory(String root) {

		List<HashMap<String, String>> FilteredArray = new ArrayList<HashMap<String, String>>();

		String category = "SELECT DISTINCT " + Constant.category + " FROM "
				+ Constant.TABLE_TUTORIAL + " WHERE " + Constant.root_title
				+ " = '" + root + "'";

		Cursor cursorcategory = dbHandler.getData(category);
		if (cursorcategory.moveToFirst()) {
			do {

				HashMap<String, String> map = new HashMap<String, String>();

				map.put(Constant.row_title, cursorcategory
						.getString(cursorcategory
								.getColumnIndex(Constant.category)));
				map.put(Constant.row_sub_title, "false");
				map.put(Constant.row_tag, "catagory");
				map.put(Constant.row_root, root);

				FilteredArray.add(map);
			} while (cursorcategory.moveToNext());
		}

		cursorcategory.close();
		dbHandler.close();

		return FilteredArray;

	}

	private List<HashMap<String, String>> getSubCatagory(String root, String tag, String catagory) {

		List<HashMap<String, String>> FilteredArray = new ArrayList<HashMap<String, String>>();
		
		String sub_category = "";
		
		if (tag.equals("root")) {
		 sub_category = "SELECT DISTINCT " + Constant.sub_category
				+ " FROM " + Constant.TABLE_TUTORIAL + " WHERE "
				+ Constant.root_title + " = '" + root + "'";
		}else if (tag.equals("catagory")) {
			
			 sub_category = "SELECT DISTINCT " + Constant.sub_category
						+ " FROM " + Constant.TABLE_TUTORIAL + " WHERE "
						+ Constant.root_title + " = '" + root + "' AND "
								+ Constant.category + " ='" + catagory+ "'";
			
		}

		Cursor cursorsub_category = dbHandler.getData(sub_category);
		if (cursorsub_category.moveToFirst()) {
			do {

				HashMap<String, String> map = new HashMap<String, String>();

				map.put(Constant.row_title, cursorsub_category
						.getString(cursorsub_category
								.getColumnIndex(Constant.sub_category)));
				map.put(Constant.row_sub_title, "false");
				map.put(Constant.row_tag, "subcatagory");
				map.put(Constant.row_root, root);
				map.put(Constant.row_catagory, catagory);

				FilteredArray.add(map);

			} while (cursorsub_category.moveToNext());
		}
		cursorsub_category.close();
		dbHandler.close();

		return FilteredArray;
	}

	private List<HashMap<String, String>> getTitle(String root,String tag, String catagory, String subcatagory) {
		List<HashMap<String, String>> FilteredArray = new ArrayList<HashMap<String, String>>();
		
		String title = "";
		
		if (tag.equals("root")) {
		 title = "SELECT DISTINCT " + Constant.id + ", " + Constant.title
				+ ", " + Constant.sub_title + " FROM "
				+ Constant.TABLE_TUTORIAL + " WHERE " + Constant.root_title
				+ " = '" + root + "'";
		}else if (tag.equals("catagory")) {
			
			 title = "SELECT DISTINCT " + Constant.id + ", " + Constant.title
						+ ", " + Constant.sub_title + " FROM "
						+ Constant.TABLE_TUTORIAL + " WHERE " + Constant.root_title
						+ " = '" + root + "' AND "
								+ Constant.category + " ='" + catagory+ "'";
			
			
		}else if (tag.equals("subcatagory")) {
			
			 title = "SELECT DISTINCT " + Constant.id + ", " + Constant.title
						+ ", " + Constant.sub_title + " FROM "
						+ Constant.TABLE_TUTORIAL + " WHERE " + Constant.root_title
						+ " = '" + root + "' AND "
								+ Constant.sub_category + " ='" + subcatagory+ "'";
			
		}

		Cursor cursortitle = dbHandler.getData(title);
		if (cursortitle.moveToFirst()) {
			do {

				HashMap<String, String> map = new HashMap<String, String>();

				map.put(Constant.row_title, cursortitle.getString(cursortitle
						.getColumnIndex(Constant.title)));
				map.put(Constant.row_sub_title, cursortitle
						.getString(cursortitle
								.getColumnIndex(Constant.sub_title)));

				map.put(Constant.row_tag, "title");
				map.put(Constant.row_root, cursortitle.getString(cursortitle
						.getColumnIndex(Constant.id)));

				FilteredArray.add(map);

			} while (cursortitle.moveToNext());
		}
		cursortitle.close();

		dbHandler.close();

		return FilteredArray;
	}

}
