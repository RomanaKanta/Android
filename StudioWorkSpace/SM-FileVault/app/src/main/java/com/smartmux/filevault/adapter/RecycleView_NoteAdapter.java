package com.smartmux.filevault.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartmux.filevault.R;
import com.smartmux.filevault.modelclass.CommonItemRow;
import com.smartmux.filevault.note.NoteListActivity;
import com.smartmux.filevault.utils.GeneralUtils;

import org.apache.commons.io.FilenameUtils;

import java.util.ArrayList;

public class RecycleView_NoteAdapter extends
RecyclerView.Adapter<RecycleView_NoteAdapter.NoteViewHolder> {// Recyclerview will
	// extend to
	// recyclerview adapter
	Context context;
	public int checkedCount = 0;
	public boolean isActionModeShowing;
    public ArrayList<CommonItemRow> data=new ArrayList<CommonItemRow>();
    Animation animation1;
	Animation animation2;
	ImageView ivFlip;
	public ActionMode mMode;
		
		 OnItemClickListener mItemClickListener;

		public RecycleView_NoteAdapter(Context context, ArrayList<CommonItemRow> data) {
	        this.context = context;
	        this.data = data;
	      
	        
	        animation1 = AnimationUtils.loadAnimation(context, R.anim.to_middle);
			animation2 = AnimationUtils.loadAnimation(context, R.anim.from_middle);

			isActionModeShowing = false;
	    }

		@Override
		public int getItemCount() {
			return (null != data ? data.size() : 0);

		}

		@Override
		public void onBindViewHolder(NoteViewHolder holder, final int position) {
			CommonItemRow address = data.get(position);


			   holder.itemImage.setImageResource(address.getImage());
		    	holder.itemImage.setTag("" + position);
		        holder.itemImage.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						ivFlip = (ImageView) v;
						ivFlip.clearAnimation();
						ivFlip.setAnimation(animation1);
						ivFlip.startAnimation(animation1);
						setAnimListners(data.get(Integer.parseInt(v.getTag().toString())));
					}
				});
		        
		        
		        holder.itemTitle.setText(FilenameUtils.removeExtension(address.getTitle()));
		        holder.itemSize.setText(address.getSize());
		        holder.itemDateTime.setText(address.getDateTime());
//		        if(isNote){
//		        	AppToast.show(getContext(), "this is note");
//		        }
		      
		        	
		        	holder.itemOptinal.setText(address.getOptional());
		        
		if (data.get(position).isChecked()) {
					
					holder.itemImage.setImageResource(R.drawable.cb_checked);
					  holder.card.setCardBackgroundColor(context.getResources().getColor(R.color.transparent    ));
				} else {
		          holder.card.setCardBackgroundColor(context.getResources().getColor(R.color.white));
				}


		}

		@Override
		public NoteViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

			// This method will inflate the custom layout and return as viewholder
			LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());

			ViewGroup mainGroup = (ViewGroup) mInflater.inflate(R.layout.common_item_row,
					viewGroup, false);
			NoteViewHolder listHolder = new NoteViewHolder(mainGroup);
			return listHolder;

		}

		public class NoteViewHolder extends RecyclerView.ViewHolder implements
				OnClickListener {
			// View holder for gridview recycler view as we used in listview

		  	ImageView	itemImage;
	        TextView 	itemTitle;
	        TextView 	itemSize;
	        TextView 	itemDateTime;
	        TextView 	itemOptinal;
	        CardView card;

			public NoteViewHolder(View row) {
				super(row);
				// Find all views ids
				  itemImage= (ImageView)row.findViewById(R.id.itemIcon);
		            itemTitle=(TextView)row.findViewById(R.id.itemTitle);
		            itemSize=(TextView)row.findViewById(R.id.itemSize);
		            itemDateTime=(TextView)row.findViewById(R.id.itemDateTime);
		            itemOptinal=(TextView)row.findViewById(R.id.itemOptional);
		            card=(CardView)row.findViewById(R.id.card_view);
				
				
				// this.frameImage = (ImageView) view
				// .findViewById(R.id.frame_Vrow_imageView);
				// this.image_lock = (ImageView) view
				// .findViewById(R.id.frame_Vrow_lock);
		            row.setOnClickListener(this);
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
		
		  private void setAnimListners(final CommonItemRow data) {
				AnimationListener animListner;
				animListner = new AnimationListener() {

					@Override
					public void onAnimationStart(Animation animation) {
						if (animation == animation1) {
							if (data.isChecked()) {
								//ivFlip.setImageResource(R.drawable.cb_unchecked);
							} else {
								//ivFlip.setImageResource(R.drawable.cb_checked);
							}
							ivFlip.clearAnimation();
							ivFlip.setAnimation(animation2);
							ivFlip.startAnimation(animation2);
						} else {
							data.setIsChecked(!data.isChecked());
							setCount();
							setActionMode();
						}
					}

					// Set selected count
					private void setCount() {
						if (data.isChecked()) {
							checkedCount++;
						} else {
							if (checkedCount != 0) {
								checkedCount--;
							}
						}

					}

					// Show/Hide action mode
					@SuppressLint("NewApi") private void setActionMode() {
						if (checkedCount > 0) {
							if (!isActionModeShowing) {
								
									mMode = ((NoteListActivity) context).startActionMode(new NoteListActivity.AnActionModeOfEpicProportions(context));
									
							
								}
								isActionModeShowing = true;
							
						} else if (mMode != null) {
							mMode.finish();
							isActionModeShowing = false;
						}

						// Set action mode title
						
						TextView titleView = null;
					    // Set action mode title
					    if (mMode != null)
					     titleView=GeneralUtils.setModeTitleColor(context,checkedCount);
					   
					    mMode.setCustomView(titleView);

						notifyDataSetChanged();

					}

					@Override
					public void onAnimationRepeat(Animation arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onAnimationEnd(Animation arg0) {
						// TODO Auto-generated method stub

					}
				};

				animation1.setAnimationListener(animListner);
				animation2.setAnimationListener(animListner);

			}
}
