package com.smartmux.filevault.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartmux.filevault.R;
import com.smartmux.filevault.modelclass.SubFolderItemRow;

import java.util.ArrayList;

public class RecycleView_SubFolderListAdapter extends
RecyclerView.Adapter<RecycleView_SubFolderListAdapter.SubFolderViewHolder> {// Recyclerview will
	// extend to
	// recyclerview adapter
	Context context;
     ArrayList<SubFolderItemRow> data=new ArrayList<SubFolderItemRow>();
		
		 OnItemClickListener mItemClickListener;

		public RecycleView_SubFolderListAdapter(Context context,ArrayList<SubFolderItemRow> data) {
			this.context = context;
			this.data = data;
			

		}

		@Override
		public int getItemCount() {
			return (null != data ? data.size() : 0);

		}

		@Override
		public void onBindViewHolder(SubFolderViewHolder holder, final int position) {
			  SubFolderItemRow address = data.get(position);


			 holder.itemTitle.setText(address.getTitle());
		        holder.itemSubTitle.setText(address.getSubTitle());
		        holder.itemImage.setImageResource(address.getImage());

		}

		@Override
		public SubFolderViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

			// This method will inflate the custom layout and return as viewholder
			LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());

			ViewGroup mainGroup = (ViewGroup) mInflater.inflate(R.layout.item_subfolder,
					viewGroup, false);
			SubFolderViewHolder listHolder = new SubFolderViewHolder(mainGroup);
			return listHolder;

		}

		public class SubFolderViewHolder extends RecyclerView.ViewHolder implements
				OnClickListener {
			// View holder for gridview recycler view as we used in listview
			 TextView 	itemTitle;
		        TextView 	itemSubTitle;
		        ImageView	itemImage;

			public SubFolderViewHolder(View view) {
				super(view);
				// Find all views ids
				
				  itemTitle=(TextView)view.findViewById(R.id.subFolderTitle);
		            itemSubTitle=(TextView)view.findViewById(R.id.subFolderSubTitle);
		            itemImage= (ImageView)view.findViewById(R.id.subFolderIcon);
				
				
				// this.frameImage = (ImageView) view
				// .findViewById(R.id.frame_Vrow_imageView);
				// this.image_lock = (ImageView) view
				// .findViewById(R.id.frame_Vrow_lock);
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

		public void SetOnItemClickListener(
				final OnItemClickListener mItemClickListener) {
			 this.mItemClickListener = mItemClickListener;
		}


	}