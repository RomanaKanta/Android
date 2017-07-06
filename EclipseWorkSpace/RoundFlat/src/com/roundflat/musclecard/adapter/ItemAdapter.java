package com.roundflat.musclecard.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;

import com.roundflat.musclecard.R;

public class ItemAdapter extends BaseAdapter {

	private Context context;
	private final String[] items;
	private Integer[] images = new Integer[]{
		
			R.drawable.btn_all,R.drawable.btn_head,R.drawable.btn_neck,R.drawable.btn_chest,
			R.drawable.btn_abdominal,R.drawable.btn_back,R.drawable.btn_upperlimb,R.drawable.btn_lowerlimb
			
	};

	public ItemAdapter(Context context, String[] items) {
		this.context = context;
		this.items = items;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View gridView;
		if (convertView == null) {
			gridView = new View(context);
			gridView = inflater.inflate(R.layout.item_button, parent, false);
			ImageView button = (ImageView) gridView.findViewById(R.id.button_item);
			//button.setText(items[position]);
			button.setImageResource(images[position]);
		} else {
			gridView = (View) convertView;
		}

		return gridView;
	}

	@Override
	public int getCount() {
		return items.length;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

}
