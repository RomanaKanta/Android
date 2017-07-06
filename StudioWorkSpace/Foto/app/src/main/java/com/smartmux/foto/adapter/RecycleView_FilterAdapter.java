package com.smartmux.foto.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
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
import com.smartmux.foto.adapter.RecycleView_FilterAdapter.RecyclerFilterViewHolder;
import com.smartmux.foto.modelclass.ListData;
import com.smartmux.foto.utils.RoundCorner;

import java.util.ArrayList;

public class RecycleView_FilterAdapter extends
RecyclerView.Adapter<RecyclerFilterViewHolder> {// Recyclerview will
	// extend to
	// recyclerview adapter
		private ArrayList<ListData> arrayList;
		OnItemClickListener mItemClickListener;
		Context context;
		int pos = 0;

		public RecycleView_FilterAdapter(Context context,
				ArrayList<ListData> list) {
			this.arrayList = list;
			this.context = context;
		}

		public long getItemId(int position) {
			return arrayList.get(position).getMid();
		}
		
		@Override
		public int getItemCount() {
			return (null != arrayList ? arrayList.size() : 0);

		}

		@Override
		public void onBindViewHolder(RecyclerFilterViewHolder holder,
				final int position) {
			final ListData model = arrayList.get(position);

			RecyclerFilterViewHolder textHolder = (RecyclerFilterViewHolder) holder;// holder

			
			textHolder.textView.setText(model.getmText());
			textHolder.layout.setBackgroundColor(Color.parseColor(model
					.getmBackground_color()));


			holder.imageView.setImageBitmap(RoundCorner.getRoundedCornerBitmap(BitmapFactory.decodeResource
					(context.getResources(), model.getmImage())));
			
			if ( position == pos) {
				holder.layout.setBackgroundColor(Color.parseColor("#272727"));
			}else{
				holder.layout.setBackgroundColor(Color.parseColor("#373737"));
			}

		}

		@Override
		public RecyclerFilterViewHolder onCreateViewHolder(ViewGroup viewGroup,
				int viewType) {

			// This method will inflate the custom layout and return as viewholder
			LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());

			ViewGroup mainGroup = (ViewGroup) mInflater.inflate(
					R.layout.listitem, viewGroup, false);
			RecyclerFilterViewHolder listHolder = new RecyclerFilterViewHolder(
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

		public class RecyclerFilterViewHolder extends RecyclerView.ViewHolder
				implements OnClickListener {
			// View holder for gridview recycler view as we used in listview
			public TextView textView;
			public ImageView imageView;
			public RelativeLayout layout;

			public RecyclerFilterViewHolder(View view) {
				super(view);
				// Find all views ids
				this.layout = (RelativeLayout) view
						.findViewById(R.id.id_layout);
				this.textView = (TextView) view.findViewById(R.id.title);
				this.imageView = (ImageView) view.findViewById(R.id.image);
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