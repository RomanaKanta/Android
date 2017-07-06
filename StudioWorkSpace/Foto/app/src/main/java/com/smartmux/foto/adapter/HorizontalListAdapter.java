package com.smartmux.foto.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.smartmux.foto.R;
import com.smartmux.foto.modelclass.ListData;

import java.util.ArrayList;

public class HorizontalListAdapter extends ArrayAdapter<ListData> {

	private LayoutInflater mInflater;
	ArrayList<ListData> values;
	int pos = 0;
	boolean isSelect = false;
	Context context;

	public HorizontalListAdapter(Context context, ArrayList<ListData> value,
			boolean select) {
		super(context, R.layout.listitem, value);

		mInflater = (LayoutInflater) getContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		this.values = value;
		this.isSelect = select;
		this.context = context;
	}

	@Override
	public int getCount() {
		return values.size();
	}

	public long getItemId(int position) {
		return values.get(position).getMid();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final Holder holder;

		if (convertView == null) {
			// Inflate the view since it does not exist
			convertView = mInflater.inflate(R.layout.listitem, parent, false);

			// Create and save off the holder in the tag so we get quick access
			// to inner fields
			// This must be done for performance reasons
			holder = new Holder();
			holder.layout = (RelativeLayout) convertView
					.findViewById(R.id.id_layout);
			holder.textView = (TextView) convertView.findViewById(R.id.title);
			holder.imageView = (ImageView) convertView.findViewById(R.id.image);
			holder.imageView.setColorFilter(Color.parseColor("#FFFFFF"),
					PorterDuff.Mode.SRC_ATOP);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

		// highlightItem(position, convertView);
		// Populate the text
		holder.textView.setText(values.get(position).getmText());
		holder.layout.setBackgroundColor(Color.parseColor(values.get(position)
				.getmBackground_color()));
		 holder.imageView.setImageResource(values.get(position).getmImage());

		if (isSelect && position == pos) {
			holder.layout.setBackgroundColor(Color.parseColor("#000000"));
		}

		// Set the color
		// convertView.setBackgroundColor(getItem(position).getBackgroundColor());

		return convertView;
	}

	public void setSelected(int p) {
		this.pos = p;
	}

	/** View holder for the views we need access to */
	private static class Holder {
		public TextView textView;
		public ImageView imageView;
        RelativeLayout layout;
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

}