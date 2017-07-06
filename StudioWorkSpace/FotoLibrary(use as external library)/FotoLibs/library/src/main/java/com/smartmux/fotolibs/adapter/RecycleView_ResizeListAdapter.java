package com.smartmux.fotolibs.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smartmux.fotolibs.R;
import com.smartmux.fotolibs.adapter.RecycleView_ResizeListAdapter.RecyclerResizeViewHolder;
import com.smartmux.fotolibs.modelclass.ListData;

public class RecycleView_ResizeListAdapter extends
		RecyclerView.Adapter<RecyclerResizeViewHolder> {// Recyclerview will
// extend to
// recyclerview adapter
	private ArrayList<ListData> arrayList;
	OnItemClickListener mItemClickListener;
	int pos = 0;

	public RecycleView_ResizeListAdapter(Context context,
			ArrayList<ListData> list) {
		this.arrayList = list;

	}

	public long getItemId(int position) {
		return arrayList.get(position).getMid();
	}
	
	@Override
	public int getItemCount() {
		return (null != arrayList ? arrayList.size() : 0);

	}

	@Override
	public void onBindViewHolder(RecyclerResizeViewHolder holder,
			final int position) {
		final ListData model = arrayList.get(position);

		RecyclerResizeViewHolder resizeHolder = (RecyclerResizeViewHolder) holder;// holder

		resizeHolder.textView.setText(model.getmText());

	}

	@Override
	public RecyclerResizeViewHolder onCreateViewHolder(ViewGroup viewGroup,
			int viewType) {

		// This method will inflate the custom layout and return as viewholder
		LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());

		ViewGroup mainGroup = (ViewGroup) mInflater.inflate(
				R.layout.resize_item, viewGroup, false);
		RecyclerResizeViewHolder listHolder = new RecyclerResizeViewHolder(
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

	public class RecyclerResizeViewHolder extends RecyclerView.ViewHolder
			implements OnClickListener {
		// View holder for gridview recycler view as we used in listview
		public TextView textView;

		public RecyclerResizeViewHolder(View view) {
			super(view);
			// Find all views ids
			this.textView = (TextView) view.findViewById(R.id.size);
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