package com.smartmux.textmemo.adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smartmux.textmemo.R;
import com.smartmux.textmemo.modelclass.NoteListItem;
import com.smartmux.textmemo.modelclass.SelectedNoteArray;
import com.smartmux.textmemo.utils.GeneralUtils;

public class NoteListAdapter extends ArrayAdapter<NoteListItem> {

	Context context;
	public ArrayList<NoteListItem> data = new ArrayList<NoteListItem>();
	public ArrayList<NoteListItem> tmpItems = new ArrayList<NoteListItem>();
	TextView no_match;

	private NoteListItem note;
	public int checkedCount = 0;
	public boolean isActionModeShowing;
	boolean select;
	Animation animation1;
	Animation animation2;
	ImageView ivFlip;

	public NoteListAdapter(Context context, ArrayList<NoteListItem> data) {
		super(context, R.layout.common_item_row, data);
		this.context = context;
		this.data = data;
		this.tmpItems = data;

		animation1 = AnimationUtils.loadAnimation(context, R.anim.to_middle);
		animation2 = AnimationUtils.loadAnimation(context, R.anim.from_middle);
		select = false;
		isActionModeShowing = false;
	}

	public int getCount() {

		return data.size();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View row = convertView;
		RegardingTypeHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(R.layout.common_item_row, parent, false);

			holder = new RegardingTypeHolder();

			holder.itemImage = (ImageView) row.findViewById(R.id.itemIcon);
			holder.itemTitle = (TextView) row.findViewById(R.id.itemTitle);
			holder.itemSize = (TextView) row.findViewById(R.id.itemSize);
			holder.itemDateTime = (TextView) row
					.findViewById(R.id.itemDateTime);
			holder.rootView = (LinearLayout) row
					.findViewById(R.id.root_View);
		
//			holder.itemOptinal = (TextView) row.findViewById(R.id.itemOptional);
//			holder.card = (CardView) row.findViewById(R.id.card_view);
			row.setTag(holder);
		} else {
			holder = (RegardingTypeHolder) row.getTag();
		}

		no_match = (TextView) ((Activity) context)
				.findViewById(R.id.textview_not_found);

		final NoteListItem address = data.get(position);

		this.note = data.get(position);
	
		
		if (data.get(position).isChecked()) {

			holder.itemImage.setImageResource(R.drawable.cb_checked);
			holder.rootView.setBackgroundResource(R.drawable.rect_background_selected);

		}else{
			holder.itemImage.setImageResource(address.getImage());
			holder.rootView.setBackgroundResource(R.drawable.rect_background);
		}
		
		holder.itemImage.setTag("" + position);
		holder.itemImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				ivFlip = (ImageView) v;
				ivFlip.clearAnimation();
				ivFlip.setAnimation(animation1);
				ivFlip.startAnimation(animation1);
				// setAnimListners(data.get(Integer
				// .parseInt(v.getTag().toString())));

				setAnimListners(data.get(position));

			}
		});
		String title = GeneralUtils.removeExtension(address.getTitle());
		holder.itemTitle.setText(title);
		holder.itemSize.setText(address.getSize());
		holder.itemDateTime.setText(address.getDateTime());
	
//		holder.itemOptinal.setText(address.getOptional());


		// else {
		// holder.itemImage.setImageResource(R.drawable.thumb_note);
		// }

		return row;
	}

	private void setAnimListners(final NoteListItem item) {

		this.note = item;
		animation1.setAnimationListener(animListner);
		animation2.setAnimationListener(animListner);

	}

	AnimationListener animListner = new AnimationListener() {

		@Override
		public void onAnimationStart(Animation animation) {
			if (animation == animation1) {
				ivFlip.clearAnimation();
				ivFlip.setAnimation(animation2);
				ivFlip.startAnimation(animation2);
			} else {

				note.setIsChecked(!note.isChecked());
				setCount();
				setActionMode();
			}
		}

		@Override
		public void onAnimationRepeat(Animation arg0) {
		}

		@Override
		public void onAnimationEnd(Animation arg0) {
		}
	};

	// Show/Hide action mode
	@SuppressLint("NewApi")
	private void setActionMode() {
		if (checkedCount > 0) {
			if (!isActionModeShowing) {

				SelectedNoteArray.createActionMode(context);
				isActionModeShowing = true;

			}

		} else {
			if (SelectedNoteArray.mMode != null) {
				
				SelectedNoteArray.finishMode();
				isActionModeShowing = false;
				return;
			}

		}

		if (isActionModeShowing) {
			SelectedNoteArray.setModeCount(context, checkedCount);
		}

		notifyDataSetChanged();

	}

	// Set selected count
	private void setCount() {
		if (note.isChecked()) {
			checkedCount++;

			SelectedNoteArray.setSelectedData(data);
			SelectedNoteArray.setCount(checkedCount);

		} else {
			if (checkedCount != 0) {

				checkedCount--;
				SelectedNoteArray.setCount(checkedCount);
				SelectedNoteArray.setSelectedData(data);

			}
		}
		// notifyDataSetChanged();
	}

	public void setCheckedItem(ArrayList<NoteListItem> selectedItem, int count) {
		data.clear();
		data.addAll(selectedItem);
		this.checkedCount = count;
		this.isActionModeShowing = true;
		notifyDataSetChanged();
	}

	public void setRefreshItem(ArrayList<NoteListItem> selectedItem) {
		data.clear();
		data.addAll(selectedItem);
		this.checkedCount = 0;
		this.isActionModeShowing = false;
		notifyDataSetChanged();
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

	@Override
	public Filter getFilter() {

		Filter filter = new Filter() {

			@SuppressLint("DefaultLocale")
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {

				FilterResults results = new FilterResults();
				ArrayList<NoteListItem> FilteredArrayNames = new ArrayList<NoteListItem>();

				if (constraint == null || constraint.length() == 0) {
					results.values = tmpItems;
					results.count = tmpItems.size();

				} else {

					constraint = constraint.toString().toLowerCase();

					for (int i = 0; i < tmpItems.size(); i++) {
						if (tmpItems.get(i).getTitle().toLowerCase()
								.startsWith(constraint.toString())) {
							FilteredArrayNames.add(tmpItems.get(i));
						}

						results.count = FilteredArrayNames.size();
						results.values = FilteredArrayNames;

					}

				}
				return results;
			}

			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint,
					FilterResults results) {

				data = (ArrayList<NoteListItem>) results.values;

				// if(data.size()>0){
				// no_match.setVisibility(View.GONE);
				// }else{
				// no_match.setText(R.string.no_match_found);
				// no_match.setVisibility(View.VISIBLE);
				// }

				notifyDataSetChanged();
			}
		};

		return filter;

	}

	static class RegardingTypeHolder {
		ImageView itemImage;
		TextView itemTitle;
		TextView itemSize;
		TextView itemDateTime;
		LinearLayout rootView;
//		TextView itemOptinal;
//		CardView card;

	}
}
