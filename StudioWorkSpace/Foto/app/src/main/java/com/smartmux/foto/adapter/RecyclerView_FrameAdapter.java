package com.smartmux.foto.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.smartmux.foto.R;
import com.smartmux.foto.adapter.RecyclerView_FrameAdapter.RecyclerFrameViewHolder;
import com.smartmux.foto.modelclass.FrameModel;
import com.smartmux.foto.utils.BitmapUtils;
import com.smartmux.foto.utils.PhotoUtil;

import java.util.ArrayList;


public class RecyclerView_FrameAdapter extends
		RecyclerView.Adapter<RecyclerFrameViewHolder> {// Recyclerview will
														// extend to
	// recyclerview adapter
	private ArrayList<FrameModel> arrayList;
	private Context context;
	public int selectedPosition = -1;
	 OnItemClickListener mItemClickListener;
		Bitmap bitmap = null;

	public RecyclerView_FrameAdapter(Context context,
			ArrayList<FrameModel> list, Bitmap bitmap) {
		this.context = context;
		this.arrayList = list;
		this.bitmap = bitmap;

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
		final FrameModel model = arrayList.get(position);

		RecyclerFrameViewHolder frameHolder = (RecyclerFrameViewHolder) holder;// holder

		
		if (PhotoUtil.getImageOrientation(bitmap) == PhotoUtil.PROTRATE) {

			frameHolder.thumbImage.setImageBitmap(BitmapUtils
					.scaleBitmap(BitmapFactory
							.decodeResource(context.getResources(), model.getThumb()),60, 96));

			frameHolder.selectdImage.setImageBitmap(BitmapUtils.scaleBitmap(bitmap, 60, 96));
		} else if (PhotoUtil.getImageOrientation(bitmap) == PhotoUtil.LANDSCAPE) {

			frameHolder.thumbImage.setImageBitmap(BitmapUtils
					.scaleBitmap(BitmapFactory
							.decodeResource(context.getResources(), model.getThumb()), 96, 60));

			frameHolder.selectdImage.setImageBitmap(BitmapUtils.scaleBitmap(bitmap, 96, 60));
		} else {

			frameHolder.thumbImage.setImageBitmap(BitmapUtils
					.scaleBitmap(BitmapFactory
							.decodeResource(context.getResources(), model.getThumb()), 60, 60));

			frameHolder.selectdImage.setImageBitmap(BitmapUtils.scaleBitmap(bitmap, 60, 60));
		}
	}

	@Override
	public RecyclerFrameViewHolder onCreateViewHolder(ViewGroup viewGroup,
			int viewType) {

		// This method will inflate the custom layout and return as viewholder
		LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());

		ViewGroup mainGroup = (ViewGroup) mInflater.inflate(
				R.layout.frame_list, viewGroup, false);
		RecyclerFrameViewHolder listHolder = new RecyclerFrameViewHolder(
				mainGroup);
		return listHolder;

	}

	public class RecyclerFrameViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
		// View holder for gridview recycler view as we used in listview
		public ImageView thumbImage, selectdImage;

		public RecyclerFrameViewHolder(View view) {
			super(view);
			// Find all views ids
			this.thumbImage = (ImageView) view.findViewById(R.id.thumb_list);
			this.selectdImage = (ImageView) view.findViewById(R.id.image_list);
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

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }


}