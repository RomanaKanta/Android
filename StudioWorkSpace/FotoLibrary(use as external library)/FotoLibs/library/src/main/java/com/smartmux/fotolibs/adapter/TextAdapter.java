package com.smartmux.fotolibs.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smartmux.fotolibs.R;
import com.smartmux.fotolibs.fragment.FragmentText;
import com.smartmux.fotolibs.modelclass.ListData;

public class TextAdapter extends ArrayAdapter<ListData> {

	private LayoutInflater mInflater;
	ArrayList<ListData> values;
	int pos = 0;
	boolean isSelect = false;
	FragmentText fragment = null;

	public TextAdapter(Context context, ArrayList<ListData> value,
			boolean select , FragmentText fragment) {
		super(context, R.layout.text_list_item, value);

		mInflater = (LayoutInflater) getContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		this.values = value;
		this.isSelect = select;
		this.fragment = fragment;
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
			convertView = mInflater.inflate(R.layout.text_list_item, parent,
					false);

			// Create and save off the holder in the tag so we get quick access
			// to inner fields
			// This must be done for performance reasons
			holder = new Holder();
		
			holder.layout = (LinearLayout) convertView
					.findViewById(R.id.text_layout);
			
			holder.textView = (TextView) convertView
					.findViewById(R.id.text_title);
			holder.imageView = (ImageView) convertView
					.findViewById(R.id.text_icon);

			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

		// Populate the text
		holder.textView.setText(values.get(position).getmText());
		holder.imageView.setImageResource(values.get(position).getmImage());
		holder.layout.setBackgroundColor(Color.parseColor(values.get(position)
				.getmBackground_color()));

		if(fragment.textNum()==0){
		
			if (position == 0) {
				holder.textView.setTextColor(Color.parseColor("#FFFFFF"));
				holder.imageView.setColorFilter(Color.parseColor("#FFFFFF"),
						PorterDuff.Mode.SRC_ATOP);
			}
			else{
				holder.textView.setTextColor(Color.parseColor("#787878"));
				holder.imageView.setColorFilter(Color.parseColor("#787878"),
						PorterDuff.Mode.SRC_ATOP);
			}
		}else{
			holder.textView.setTextColor(Color.parseColor("#FFFFFF"));
			holder.imageView.setColorFilter(Color.parseColor("#FFFFFF"),
					PorterDuff.Mode.SRC_ATOP);
		}

//		if (position == 5) {
//			holder.layout.setBackgroundColor(Color.parseColor("#272727"));
//		}

		return convertView;
	}
	

	public void setSelected(int p) {
		this.pos = p;
	}

	/** View holder for the views we need access to */
	private static class Holder {
		public TextView textView;
		public ImageView imageView;
		public LinearLayout layout;
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

}