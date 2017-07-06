package com.roundflat.musclecard.adapter;

import java.util.ArrayList;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import com.roundflat.musclecard.R;
import com.roundflat.musclecard.TutorialActivity;
import com.roundflat.musclecard.database.DatabaseHandler;
import com.roundflat.musclecard.model.TutorialModel;
import com.roundflat.musclecard.util.Constant;
import com.roundflat.musclecard.util.RoundFlatUtil;

public class TitleAdapter extends BaseAdapter implements
		StickyListHeadersAdapter, SectionIndexer {

	public List<TutorialModel> list;
	// private Context context;
	private DatabaseHandler db;
	String title;
	private int[] mSectionIndices;
	private Character[] mSectionLetters;
	private LayoutInflater inflater;
	boolean fav;
	Activity mActivity;
	String rootTitle;

	private String[] roots = new String[] { "頭部", "頚部", "胸部", "腹部", "背部", "上肢",
			"下肢" };

	
	public  TitleAdapter(List<TutorialModel> list){
		this.list = list;
	}
	
	public TitleAdapter(Activity a,

			boolean f, String root) {
		super();
		// this.context = context;
		this.fav = f;
		this.rootTitle = root;
		this.mActivity = a;
		db = new DatabaseHandler(mActivity);
		inflater = LayoutInflater.from(mActivity);
		mSectionIndices = getSectionIndices();
		mSectionLetters = getSectionLetters();
	}
	public TitleAdapter(Activity a, List<TutorialModel> list,
			boolean f, String root) {
		super();
		// this.context = context;
		this.list = list;
		this.fav = f;
		this.rootTitle = root;
		this.mActivity = a;
		db = new DatabaseHandler(mActivity);
		inflater = LayoutInflater.from(mActivity);
		mSectionIndices = getSectionIndices();
		mSectionLetters = getSectionLetters();
	}

	private int[] getSectionIndices() {
		ArrayList<Integer> sectionIndices = new ArrayList<Integer>();
		char lastFirstChar = roots[0].charAt(0);
		sectionIndices.add(0);
		for (int i = 1; i < roots.length; i++) {
			if (roots[i].charAt(0) != lastFirstChar) {
				lastFirstChar = roots[i].charAt(0);
				sectionIndices.add(i);
			}
		}
		int[] sections = new int[sectionIndices.size()];
		for (int i = 0; i < sectionIndices.size(); i++) {
			sections[i] = sectionIndices.get(i);
		}
		return sections;
	}

	private Character[] getSectionLetters() {
		Character[] letters = new Character[mSectionIndices.length];
		for (int i = 0; i < mSectionIndices.length; i++) {
			letters[i] = roots[mSectionIndices[i]].charAt(0);
		}
		return letters;
	}

	public int getCount() {

		return list.size();
	}

	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {

		final ViewHolder holder;
		if (convertView == null) {

			holder = new ViewHolder();

			convertView = inflater.inflate(R.layout.title_item, parent, false);

			holder.titleName = (TextView) convertView
					.findViewById(R.id.textView_title);

			holder.iconFav = (ImageView) convertView
					.findViewById(R.id.imageView_memorize);

			convertView.setTag(holder);

		} else {

			holder = (ViewHolder) convertView.getTag();
		}

		 boolean isExistTutorial = db.isFavoriteTutorialExists(list.get(
				position).getId());
		if (isExistTutorial) {

			holder.iconFav.setImageResource(R.drawable.memorize);
			if (fav) {
				holder.titleName.setTextColor(Color.parseColor("#979698"));

			}
		} else {

			holder.iconFav.setImageResource(R.drawable.memorize_not);
			holder.titleName.setTextColor(Color.parseColor("#000000"));

		}

		holder.titleName.setText(list.get(position).getTitle());

		holder.iconFav.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Log.d("iconFav", "onClick");
				boolean isAdd = false;
				String id = list.get(
						position).getId();
				Log.d("id", id);
				if (!db.isFavoriteTutorialExists(list.get(
						position).getId())) {

					isAdd = false;
					db.updateFavoriteTutorial(list.get(position).getId(), "1");

					if (fav) {

						holder.titleName.setTextColor(Color
								.parseColor("#979698"));
					}

				} else {

					isAdd = true;
					db.updateFavoriteTutorial(list.get(position).getId(), "0");
					holder.titleName.setTextColor(Color.parseColor("#000000"));

				}
				Intent intent = new Intent(
						"ButtonClick");
				intent.putExtra(Constant.position, position);
				intent.putExtra("item", isAdd);
				intent.putExtra("item_id", id);
				mActivity.sendBroadcast(intent);
				notifyDataSetChanged();

			}
		});
		return convertView;

	}

	@Override
	public View getHeaderView(int position, View convertView, ViewGroup parent) {

		HeaderViewHolder holder;
		if (convertView == null) {
			holder = new HeaderViewHolder();
			convertView = inflater.inflate(R.layout.header, parent, false);
			holder.text = (TextView) convertView
					.findViewById(R.id.text_header_title);
			convertView.setTag(holder);
		} else {
			holder = (HeaderViewHolder) convertView.getTag();
		}

		CharSequence headerChar = list.get(position).getRoot_title();

		holder.text.setText(headerChar);

		return convertView;
	}

	@Override
	public void notifyDataSetChanged() {
		// TODO Auto-generated method stub
		super.notifyDataSetChanged();
	}

	private class ViewHolder {

		public TextView titleName;
		public ImageView iconFav;
	}

	class HeaderViewHolder {
		TextView text;
	}

	@Override
	public Object[] getSections() {

		return mSectionLetters;
	}

	@Override
	public int getPositionForSection(int section) {
		if (mSectionIndices.length == 0) {
			return 0;
		}

		if (section >= mSectionIndices.length) {
			section = mSectionIndices.length - 1;
		} else if (section < 0) {
			section = 0;
		}
		return mSectionIndices[section];

	}

	@Override
	public int getSectionForPosition(int position) {
		for (int i = 0; i < mSectionIndices.length; i++) {
			if (position < mSectionIndices[i]) {
				return i - 1;
			}
		}
		return mSectionIndices.length - 1;

	}

	@Override
	public long getHeaderId(int position) {

		long pos = list.get(position).getRoot_title().subSequence(0, 1)
				.charAt(0);

		return pos;
	}

}
