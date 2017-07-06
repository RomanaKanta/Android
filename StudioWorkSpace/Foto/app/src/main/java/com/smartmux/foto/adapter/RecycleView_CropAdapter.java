package com.smartmux.foto.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.smartmux.foto.R;
import com.smartmux.foto.adapter.RecycleView_CropAdapter.RecyclerCropViewHolder;
import com.smartmux.foto.modelclass.ListData;
import com.smartmux.foto.utils.BitmapUtils;
import com.smartmux.foto.utils.RoundCorner;

import java.util.ArrayList;

public class RecycleView_CropAdapter extends
		RecyclerView.Adapter<RecyclerCropViewHolder> {// Recyclerview will
	// extend to
	// recyclerview adapter
	private ArrayList<ListData> arrayList;
	private Context context;
	OnItemClickListener mItemClickListener;
	int pos = 0;

	public RecycleView_CropAdapter(Context context, ArrayList<ListData> list) {
		this.context = context;
		this.arrayList = list;

	}

	@Override
	public int getItemCount() {
		return (null != arrayList ? arrayList.size() : 0);

	}

	@Override
	public void onBindViewHolder(RecyclerCropViewHolder holder,
			final int position) {
		final ListData model = arrayList.get(position);

		RecyclerCropViewHolder cropHolder = (RecyclerCropViewHolder) holder;// holder

		cropHolder.textView.setText(model.getmText());
		cropHolder.layout.setBackgroundColor(Color.parseColor(model
				.getmBackground_color()));

		cropHolder.imageView.setImageBitmap(RoundCorner
				.getRoundedCornerBitmap(BitmapUtils
						.decodeSampledBitmapFromResource(
								context.getResources(), model.getmImage(), 60,
								60)));

		if (position == pos) {
			cropHolder.layout.setBackgroundColor(Color.parseColor("#272727"));
		}else{
			cropHolder.layout.setBackgroundColor(Color.parseColor("#373737"));
		}
		
	}

	@Override
	public RecyclerCropViewHolder onCreateViewHolder(ViewGroup viewGroup,
			int viewType) {

		// This method will inflate the custom layout and return as viewholder
		LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());

		ViewGroup mainGroup = (ViewGroup) mInflater.inflate(
				R.layout.crop_row_item, viewGroup, false);
		RecyclerCropViewHolder listHolder = new RecyclerCropViewHolder(
				mainGroup);
		return listHolder;

	}

	public void setValue(ArrayList<ListData> value) {
		this.arrayList = value;
		notifyDataSetChanged();
	}

	public void setSelected(int p) {
		this.pos = p;
		notifyDataSetChanged();
	}

	public class RecyclerCropViewHolder extends RecyclerView.ViewHolder
			implements OnClickListener {
		// View holder for gridview recycler view as we used in listview
		public TextView textView;
		public ImageView imageView;
		RelativeLayout layout;

		public RecyclerCropViewHolder(View view) {
			super(view);
			// Find all views ids

			this.layout = (RelativeLayout) view
					.findViewById(R.id.crop_row_layout);
			this.textView = (TextView) view.findViewById(R.id.crop_row_title);
			this.imageView = (ImageView) view.findViewById(R.id.crop_row_image);
			view.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			if (mItemClickListener != null) {
				mItemClickListener.onItemClick(v, getPosition());
			}

		}
	}

	public interface OnItemClickListener {
		public void onItemClick(View view, int position);
	}

	public void SetOnItemClickListener(
			final OnItemClickListener mItemClickListener) {
		this.mItemClickListener = mItemClickListener;
	}

}