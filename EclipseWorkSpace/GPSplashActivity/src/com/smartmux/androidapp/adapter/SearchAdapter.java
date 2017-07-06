package com.smartmux.androidapp.adapter;

import java.util.ArrayList;
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

public class SearchAdapter extends BaseAdapter implements Filterable {
	private Context ctx;
	private List<String> items;
	private List<String> tmpItems;

	public SearchAdapter(Context context, List<String> items) {
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
