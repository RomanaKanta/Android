package com.smartmux.androidapp.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.smartmux.androidapp.R;
import com.smartmux.androidapp.util.Constant;

public class ItemSearchAdapter extends BaseAdapter implements Filterable {
	private Context ctx;
	private List<HashMap<String,String>> items;
	private List<HashMap<String,String>> tmpItems;

	public ItemSearchAdapter(Context context,List<HashMap<String,String>> items) {
		super();
		this.ctx = context;
		this.items = items;
		this.tmpItems = items;
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
					.findViewById(R.id.textView_title);
			
			holder.txtSubTitle = (TextView) convertView
					.findViewById(R.id.textView_subtitle);

			holder.txtSubTitle.setVisibility(View.VISIBLE);
			convertView.setTag(holder);

		} else {

			holder = (SearchViewHolder) convertView.getTag();
		}

		Log.d("values ", items.get(position).get(Constant.title)+"  "+items.get(position).get(Constant.sub_title));
		holder.txtTitle.setText(items.get(position).get(Constant.title));
		holder.txtSubTitle.setText(items.get(position).get(Constant.sub_title));
		
		
		return convertView;

	}

	@Override
	public Filter getFilter() {
		Filter filter = new Filter() {

			@Override
			protected void publishResults(CharSequence constraint,
					FilterResults results) {

				items = (List<HashMap<String, String>>) results.values;

				notifyDataSetChanged();
			}

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {

				FilterResults results = new FilterResults();
				List<HashMap<String, String>> FilteredArrayNames = new ArrayList<HashMap<String, String>>(items.size());

				if (constraint == null || constraint.length() == 0) {
					results.values = tmpItems;
					results.count = tmpItems.size();
				} else {

					constraint = constraint.toString().toLowerCase();

					for (int i = 0; i < tmpItems.size(); i++) {

						if (tmpItems.get(i).get(Constant.title).toLowerCase().startsWith(
								constraint.toString())) {
							FilteredArrayNames.add(tmpItems.get(i));
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
	}

}

