package com.smartux.photocollage.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.smartux.photocollage.R;
import com.smartux.photocollage.adapter.RecyclerView_FrameAdapter.RecyclerFrameViewHolder;
import com.smartux.photocollage.model.FrameJsonAndThumb;

public class RecyclerView_FrameAdapter extends
		RecyclerView.Adapter<RecyclerFrameViewHolder> {// Recyclerview will
														// extend to
	// recyclerview adapter
	public ArrayList<FrameJsonAndThumb> arrayList;
	public int selectedPosition = -1;
	 OnItemClickListener mItemClickListener;
	 boolean isPurchase;
	 int photoNo;

	public RecyclerView_FrameAdapter(Context context,
			ArrayList<FrameJsonAndThumb> list,boolean isPur, int photoNo) {
		this.arrayList = list;
		this.isPurchase = isPur;
		this.photoNo = photoNo;

	}

	public boolean isPurchase() {
		return isPurchase;
	}

	public void setPurchase(boolean isPur) {
		this.isPurchase = isPur;
	}

	@Override
	public int getItemCount() {
		return (null != arrayList ? arrayList.size() : 0);

	}

	public int getSelectedPosition() {
		return selectedPosition;
	}

	public void setSelectedPosition(int selectedPosition) {
		this.selectedPosition = selectedPosition;
	}

	@Override
	public void onBindViewHolder(RecyclerFrameViewHolder holder,
			final int position) {
		final FrameJsonAndThumb model = arrayList.get(position);

		RecyclerFrameViewHolder frameHolder = (RecyclerFrameViewHolder) holder;// holder

		frameHolder.frameImage.setImageResource(model.getFrameThumb());
	        if(selectedPosition==position){
	        	frameHolder.frameImage.setBackgroundColor(Color.parseColor("#80000000"));
	        }else{
	        	frameHolder.frameImage.setBackgroundColor(Color.parseColor("#20eeeeee"));
	        }
	        
	        if(isPurchase){
	          	frameHolder.image_lock.setVisibility(View.GONE);
	        }else{
	        if(photoNo>2 && position>9){
	         	frameHolder.image_lock.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
//	        	frameHolder.image_lock.setColorFilter(Color.parseColor("#989C9F"), PorterDuff.Mode.SRC_ATOP);
	         	frameHolder.image_lock.setVisibility(View.VISIBLE);
	        }else{
	        	frameHolder.image_lock.setVisibility(View.GONE);
	        }
	        }

	}

	@Override
	public RecyclerFrameViewHolder onCreateViewHolder(ViewGroup viewGroup,
			int viewType) {

		// This method will inflate the custom layout and return as viewholder
		LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());

		ViewGroup mainGroup = (ViewGroup) mInflater.inflate(
				R.layout.item_horizontal_frame, viewGroup, false);
		RecyclerFrameViewHolder listHolder = new RecyclerFrameViewHolder(
				mainGroup);
		return listHolder;

	}

	public class RecyclerFrameViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
		// View holder for gridview recycler view as we used in listview
		public ImageView frameImage, image_lock;

		public RecyclerFrameViewHolder(View view) {
			super(view);
			// Find all views ids
			this.frameImage = (ImageView) view.findViewById(R.id.frame_row_imageView);
			this.image_lock = (ImageView) view.findViewById(R.id.frame_row_lock);
			 view.setOnClickListener(this);
		}

		@SuppressWarnings("deprecation")
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

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }


}