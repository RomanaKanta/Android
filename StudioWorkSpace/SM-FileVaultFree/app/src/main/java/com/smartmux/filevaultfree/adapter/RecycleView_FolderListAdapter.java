package com.smartmux.filevaultfree.adapter;

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

import com.smartmux.filevaultfree.FolderListActivity;
import com.smartmux.filevaultfree.R;
import com.smartmux.filevaultfree.adapter.RecycleView_FolderListAdapter.FolderViewHolder;
import com.smartmux.filevaultfree.audio.AudioListActivity;
import com.smartmux.filevaultfree.modelclass.CommonItemRow;
import com.smartmux.filevaultfree.note.NoteListActivity;
import com.smartmux.filevaultfree.utils.GeneralUtils;

import org.apache.commons.io.FilenameUtils;

import java.util.ArrayList;

public class RecycleView_FolderListAdapter extends
RecyclerView.Adapter<FolderViewHolder> {// Recyclerview will
	// extend to
	// recyclerview adapter
	Context context;
	public int checkedCount = 0;
	public boolean isActionModeShowing;
    public ArrayList<CommonItemRow> data=new ArrayList<CommonItemRow>();
    Animation animation1;
	Animation animation2;
	ImageView ivFlip;
	ActionMode mMode;
	public boolean isNote=false;
    boolean isAudio = false;

		 OnItemClickListener mItemClickListener;

		public RecycleView_FolderListAdapter(Context context, ArrayList<CommonItemRow> data,boolean isAudio) {
		        this.context = context;
		        this.data = data;
		        this.isAudio = isAudio;

		        animation1 = AnimationUtils.loadAnimation(context, R.anim.to_middle);
				animation2 = AnimationUtils.loadAnimation(context, R.anim.from_middle);

				isActionModeShowing = false;

		}

		@Override
		public int getItemCount() {
			return (null != data ? data.size() : 0);

		}

		@Override
		public void onBindViewHolder(FolderViewHolder holder, final int position) {
			CommonItemRow address = data.get(position);

			FolderViewHolder folderHolder = (FolderViewHolder) holder;// holder

			folderHolder.itemImage.setImageResource(address.getImage());
			folderHolder.itemImage.setTag("" + position);
			folderHolder.itemImage.setOnClickListener(new OnClickListener() {

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


			folderHolder.itemTitle.setText(FilenameUtils.removeExtension(address.getTitle()));
			folderHolder.itemSize.setText(address.getSize());
			folderHolder.itemDateTime.setText(address.getDateTime());
//		        if(isNote){
//		        	AppToast.show(getContext(), "this is note");
//		        }
		        if(isAudio){

		        	folderHolder.itemOptinal.setText(address.getOptional()+" mins");
		        }else{

		        	folderHolder.itemOptinal.setText(address.getOptional());
		        }
		if (data.get(position).isChecked()) {

			folderHolder.itemImage.setImageResource(R.drawable.cb_checked);
					//holder.itemImage.setBackgroundColor(R.color.light_gray);
					//holder.selectBox.setImageResource(R.drawable.cb_checked);
					//holder.check.setVisibility(View.VISIBLE);
				//row.setBackgroundColor(context.getResources().getColor(R.color.list_highlight));
			folderHolder.card.setCardBackgroundColor(context.getResources().getColor(R.color.transparent    ));
				} else {
//					holder.selectBox.setImageDrawable(drawable);
//					//holder.selectBox.setImageResource(R.drawable.cb_unchecked);
//					holder.check.setVisibility(View.INVISIBLE);
					//row.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.list_selector));
					folderHolder.card.setCardBackgroundColor(context.getResources().getColor(R.color.white));
				}

		}

		@Override
		public FolderViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

			// This method will inflate the custom layout and return as viewholder
			LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());

			ViewGroup mainGroup = (ViewGroup) mInflater.inflate(R.layout.common_item_row,
					viewGroup, false);
			FolderViewHolder listHolder = new FolderViewHolder(mainGroup);
			return listHolder;

		}

		public class FolderViewHolder extends RecyclerView.ViewHolder implements
				OnClickListener {
			// View holder for gridview recycler view as we used in listview
		  	ImageView	itemImage;
	        TextView 	itemTitle;
	        TextView 	itemSize;
	        TextView 	itemDateTime;
	        TextView 	itemOptinal;
	        CardView card;

			public FolderViewHolder(View view) {
				super(view);
				// Find all views ids
				   itemImage= (ImageView)view.findViewById(R.id.itemIcon);
		            itemTitle=(TextView)view.findViewById(R.id.itemTitle);
		            itemSize=(TextView)view.findViewById(R.id.itemSize);
		            itemDateTime=(TextView)view.findViewById(R.id.itemDateTime);
		            itemOptinal=(TextView)view.findViewById(R.id.itemOptional);
		            card=(CardView)view.findViewById(R.id.card_view);


				// this.frameImage = (ImageView) view
				// .findViewById(R.id.frame_Vrow_imageView);
				// this.image_lock = (ImageView) view
				// .findViewById(R.id.frame_Vrow_lock);
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
								if(isAudio){
									mMode = ((AudioListActivity) context).startActionMode(new AudioListActivity.AnActionModeOfEpicProportions(context));

								}else if(isNote){
									mMode = ((NoteListActivity) context).startActionMode(new NoteListActivity.AnActionModeOfEpicProportions(context));

								}else{
									mMode = ((FolderListActivity) context).startActionMode(new FolderListActivity.AnActionModeOfEpicProportions(context));

								}
								isActionModeShowing = true;
							}
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