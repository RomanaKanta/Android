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
import com.smartux.photocollage.adapter.RecyclerView_EffectAdapter.RecyclerEffectViewHolder;
import com.smartux.photocollage.model.ColorItem;

public class RecyclerView_EffectAdapter extends
		RecyclerView.Adapter<RecyclerEffectViewHolder> {// Recyclerview will
														// extend to
	// recyclerview adapter
	private ArrayList<ColorItem> arrayList;
	private Context context;

	public RecyclerView_EffectAdapter(Context context,
			ArrayList<ColorItem> arrayList) {
		this.context = context;
		this.arrayList = arrayList;

	}

	@Override
	public int getItemCount() {
		return (null != arrayList ? arrayList.size() : 0);

	}

	@Override
	public void onBindViewHolder(RecyclerEffectViewHolder holder,
			final int position) {
		final ColorItem model = arrayList.get(position);

		RecyclerEffectViewHolder mainHolder = (RecyclerEffectViewHolder) holder;// holder

		// setting title
		// mainHolder.colorCircleView1.setText(model.getTitle());

		GradientDrawable drawable = (GradientDrawable) mainHolder.colorView1
				.getBackground();
		drawable.setColor(Color.parseColor(model.getFirstColor()));

		GradientDrawable secdrawable = (GradientDrawable) mainHolder.colorView2
				.getBackground();
		secdrawable.setColor(Color.parseColor(model.getSecondColor()));

		GradientDrawable thrdrawable = (GradientDrawable) mainHolder.colorView3
				.getBackground();
		thrdrawable.setColor(Color.parseColor(model.getThirdColor()));

		mainHolder.colorView1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String color = arrayList.get(position).getFirstColor();
				((CollageActivity) context).setColorEffect(color);
			}
		});

		mainHolder.colorView2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String color = arrayList.get(position).getSecondColor();
				((CollageActivity) context).setColorEffect(color);
			}
		});

		mainHolder.colorView3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String color = arrayList.get(position).getThirdColor();
				((CollageActivity) context).setColorEffect(color);
			}
		});

	}

	@Override
	public RecyclerEffectViewHolder onCreateViewHolder(ViewGroup viewGroup,
			int viewType) {

		// This method will inflate the custom layout and return as viewholder
		LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());

		ViewGroup mainGroup = (ViewGroup) mInflater.inflate(
				R.layout.effect_row, viewGroup, false);
		RecyclerEffectViewHolder listHolder = new RecyclerEffectViewHolder(
				mainGroup);
		return listHolder;

	}

	public class RecyclerEffectViewHolder extends RecyclerView.ViewHolder {
		// View holder for gridview recycler view as we used in listview
		public View colorView1, colorView2, colorView3;

		public RecyclerEffectViewHolder(View view) {
			super(view);
			// Find all views ids
			this.colorView1 = (View) view.findViewById(R.id.view_effect_color1);
			this.colorView2 = (View) view.findViewById(R.id.view_effect_color2);
			this.colorView3 = (View) view.findViewById(R.id.view_effect_color3);
		}
	}

}