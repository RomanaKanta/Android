package com.smartux.photocollage.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.smartux.photocollage.CollageActivity;
import com.smartux.photocollage.R;
import com.smartux.photocollage.adapter.RecyclerView_ColorAdapter.RecyclerViewHolder;
import com.smartux.photocollage.model.ColorItem;

public class RecyclerView_ColorAdapter extends
		RecyclerView.Adapter<RecyclerViewHolder> {// Recyclerview will extend to
// recyclerview adapter
	private ArrayList<ColorItem> arrayList;
	private Context context;

	public RecyclerView_ColorAdapter(Context context,
			ArrayList<ColorItem> arrayList) {
		this.context = context;
		this.arrayList = arrayList;

	}

	@Override
	public int getItemCount() {
		return (null != arrayList ? arrayList.size() : 0);

	}

	@Override
	public void onBindViewHolder(RecyclerViewHolder holder, final int position) {
		final ColorItem model = arrayList.get(position);

		RecyclerViewHolder mainHolder = (RecyclerViewHolder) holder;// holder

		// setting title
		// mainHolder.colorCircleView1.setText(model.getTitle());

		GradientDrawable drawable = (GradientDrawable) mainHolder.colorCircleView1
				.getBackground();
		drawable.setColor(Color.parseColor(model.getFirstColor()));

		GradientDrawable secdrawable = (GradientDrawable) mainHolder.colorCircleView2
				.getBackground();
		secdrawable.setColor(Color.parseColor(model.getSecondColor()));

		mainHolder.colorCircleView1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String color = arrayList.get(position).getFirstColor();
				((CollageActivity) context).setBorderColor(color);
			}
		});

		mainHolder.colorCircleView2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String color = arrayList.get(position).getSecondColor();
				((CollageActivity) context).setBorderColor(color);
			}
		});

	}

	@Override
	public RecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup,
			int viewType) {

		// This method will inflate the custom layout and return as viewholder
		LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());

		ViewGroup mainGroup = (ViewGroup) mInflater.inflate(
				R.layout.item_color, viewGroup, false);
		RecyclerViewHolder listHolder = new RecyclerViewHolder(mainGroup);
		return listHolder;

	}

	public class RecyclerViewHolder extends RecyclerView.ViewHolder {
		// View holder for gridview recycler view as we used in listview
		public View colorCircleView1, colorCircleView2;

		public RecyclerViewHolder(View view) {
			super(view);
			// Find all views ids
			this.colorCircleView1 = (View) view.findViewById(R.id.view_color);
			this.colorCircleView2 = (View) view.findViewById(R.id.view_color2);
		}
	}

}