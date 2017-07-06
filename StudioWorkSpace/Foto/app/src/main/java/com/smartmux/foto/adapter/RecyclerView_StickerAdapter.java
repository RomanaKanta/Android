package com.smartmux.foto.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.smartmux.foto.R;
import com.smartmux.foto.adapter.RecyclerView_StickerAdapter.RecyclerStickerViewHolder;
import com.smartmux.foto.modelclass.ListData;
import com.smartmux.foto.utils.BitmapUtils;

import java.util.ArrayList;

public class RecyclerView_StickerAdapter extends
		RecyclerView.Adapter<RecyclerStickerViewHolder> {// Recyclerview will
														// extend to
	// recyclerview adapter
	private ArrayList<ListData> arrayList;
	private Context context;
	public int selectedPosition = -1;
	 OnItemClickListener mItemClickListener;

	public RecyclerView_StickerAdapter(Context context,
			ArrayList<ListData> list) {
		this.context = context;
		this.arrayList = list;

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
	public void onBindViewHolder(RecyclerStickerViewHolder holder,
			final int position) {
		final ListData model = arrayList.get(position);

		RecyclerStickerViewHolder frameHolder = (RecyclerStickerViewHolder) holder;// holder

		frameHolder.stickerImage.setImageBitmap(BitmapUtils
				.decodeSampledBitmapFromResource(context.getResources(), model.getmImage(), 60, 60));

	}

	@Override
	public RecyclerStickerViewHolder onCreateViewHolder(ViewGroup viewGroup,
			int viewType) {

		// This method will inflate the custom layout and return as viewholder
		LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());

		ViewGroup mainGroup = (ViewGroup) mInflater.inflate(
				R.layout.sticker_item, viewGroup, false);
		RecyclerStickerViewHolder listHolder = new RecyclerStickerViewHolder(
				mainGroup);
		return listHolder;

	}

	public class RecyclerStickerViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
		// View holder for gridview recycler view as we used in listview
		public ImageView stickerImage;

		public RecyclerStickerViewHolder(View view) {
			super(view);
			// Find all views ids
			this.stickerImage = (ImageView) view.findViewById(R.id.image_sticker_item);
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