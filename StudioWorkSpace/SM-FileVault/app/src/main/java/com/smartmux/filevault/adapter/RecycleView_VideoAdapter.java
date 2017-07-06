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
import com.smartmux.filevault.photo.PhotoListActivity;
import com.smartmux.filevault.utils.GeneralUtils;
import com.smartmux.filevault.utils.SDImageLoader;
import com.smartmux.filevault.video.VideoListActivity;

import org.apache.commons.io.FilenameUtils;

import java.util.ArrayList;

public class RecycleView_VideoAdapter extends
RecyclerView.Adapter<RecycleView_VideoAdapter.VideoViewHolder> {// Recyclerview will
	// extend to
	// recyclerview adapter
		Context context;
		public ArrayList<CommonItemRow> data = new ArrayList<CommonItemRow>();
		SDImageLoader imageLoder;
		public boolean isVideo = false;
		public int checkedCount = 0;
		public boolean isActionModeShowing;

		Animation animation1;
		Animation animation2;
		ImageView ivFlip;
		public ActionMode mMode;
		
		 OnItemClickListener mItemClickListener;

		public RecycleView_VideoAdapter(Context context,
				ArrayList<CommonItemRow> data, boolean isVideo) {
			this.context = context;
			this.data = data;
			this.isVideo = isVideo;
			imageLoder = new SDImageLoader();

			animation1 = AnimationUtils.loadAnimation(context, R.anim.to_middle);
			animation2 = AnimationUtils.loadAnimation(context, R.anim.from_middle);

		}

		@Override
		public int getItemCount() {
			return (null != data ? data.size() : 0);

		}

		@Override
		public void onBindViewHolder(VideoViewHolder holder, final int position) {
			CommonItemRow address = data.get(position);


			holder.itemImage.setTag("" + position);
			holder.itemImage.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					ivFlip = (ImageView) v;
					ivFlip.clearAnimation();
					ivFlip.setAnimation(animation1);
					ivFlip.startAnimation(animation1);
					setAnimListners(data.get(Integer
							.parseInt(v.getTag().toString())));
				}
			});

			// Bitmap thumb =
			// ThumbnailUtils.createVideoThumbnail(address.getFilePath(),
			// MediaStore.Images.Thumbnails.MICRO_KIND);
			//
			// holder.itemImage.setImageBitmap(thumb);

			holder.itemImage.setImageResource(R.drawable.thumb_video);

			holder.itemTitle.setText(FilenameUtils.removeExtension(address
					.getTitle()));
			holder.itemSize.setText(address.getSize());
			holder.itemDateTime.setText(address.getDateTime());
			holder.itemOptinal.setText(address.getOptional());

			if (data.get(position).isChecked()) {

				holder.itemImage.setImageResource(R.drawable.cb_checked);
				// holder.selectBox.setImageResource(R.drawable.cb_checked);
				// holder.check.setVisibility(View.VISIBLE);
				// row.setBackgroundColor(context.getResources().getColor(R.color.list_highlight));
				holder.card.setCardBackgroundColor(context.getResources().getColor(
						R.color.transparent));
			} else {
				// holder.selectBox.setImageDrawable(drawable);
				// //holder.selectBox.setImageResource(R.drawable.cb_unchecked);
				// holder.check.setVisibility(View.INVISIBLE);
				// row.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.list_selector));
				holder.card.setCardBackgroundColor(context.getResources().getColor(
						R.color.white));
			}

		}

		@Override
		public VideoViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

			// This method will inflate the custom layout and return as viewholder
			LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());

			ViewGroup mainGroup = (ViewGroup) mInflater.inflate(R.layout.common_item_row,
					viewGroup, false);
			VideoViewHolder listHolder = new VideoViewHolder(mainGroup);
			return listHolder;

		}

		public class VideoViewHolder extends RecyclerView.ViewHolder implements
				OnClickListener {
			// View holder for gridview recycler view as we used in listview
			ImageView itemImage;
			TextView itemTitle;
			TextView itemSize;
			TextView itemDateTime;
			TextView itemOptinal;
			CardView card;

			public VideoViewHolder(View row) {
				super(row);
				// Find all views ids
				itemTitle = (TextView) row.findViewById(R.id.itemTitle);
				itemSize = (TextView) row.findViewById(R.id.itemSize);
				itemDateTime = (TextView) row
						.findViewById(R.id.itemDateTime);
				itemOptinal = (TextView) row.findViewById(R.id.itemOptional);
				itemImage = (ImageView) row.findViewById(R.id.itemIcon);
				card = (CardView) row.findViewById(R.id.card_view);
				
				
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
							// ivFlip.setImageResource(R.drawable.cb_unchecked);
						} else {
							// ivFlip.setImageResource(R.drawable.cb_checked);
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
				@SuppressLint("NewApi")
				private void setActionMode() {
					if (checkedCount > 0) {
						if (!isActionModeShowing) {
							if (isVideo) {
								mMode = ((VideoListActivity) context)
										.startActionMode(new VideoListActivity.AnActionModeOfEpicProportions(
												context));
							} else {
								mMode = ((PhotoListActivity) context)
										.startActionMode(new PhotoListActivity.AnActionModeOfEpicProportions(
												context));

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
						titleView = GeneralUtils.setModeTitleColor(context,
								checkedCount);

					mMode.setCustomView(titleView);

					notifyDataSetChanged();

				}

				@Override
				public void onAnimationRepeat(Animation arg0) {
				}

				@Override
				public void onAnimationEnd(Animation arg0) {
				}
			};

			animation1.setAnimationListener(animListner);
			animation2.setAnimationListener(animListner);

		}
	}