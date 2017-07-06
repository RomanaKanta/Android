package com.roundflat.musclecard.adapter;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewAnimator;

import com.roundflat.musclecard.R;
import com.roundflat.musclecard.animition.FlipAnimation;
import com.roundflat.musclecard.model.TutorialModel;

public class TutorialPagerAdapter extends PagerAdapter  implements
 GestureDetector.OnGestureListener{

	Context context;
	LayoutInflater layoutInflater;
	List<TutorialModel> tutorialList;
	String imagePatterName = "_A_000.png";
	int max = 8;

	public TutorialPagerAdapter(Context context) {
		this.context = context;
		layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	public TutorialPagerAdapter(Context context, List<TutorialModel> list) {
		this.context = context;
		layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.tutorialList = list;
	}

	@Override
	public int getCount() {
		return tutorialList.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == ((FrameLayout) object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {

		ViewHolder holder;

		View itemView = layoutInflater.inflate(R.layout.pager_item, container,
				false);

		holder = new ViewHolder();

		holder.viewFlip = (FrameLayout) itemView
				.findViewById(R.id.pageframecontent);

		holder.imageContent = (RelativeLayout) itemView
				.findViewById(R.id.imageContent);

		holder.dataContent = (RelativeLayout) itemView
				.findViewById(R.id.dataContent);


		holder.dataContent.setLayoutParams(new FrameLayout.LayoutParams(holder.imageContent.getWidth(),
				holder.imageContent.getHeight()));
		holder.dataContent.setLayoutParams(new FrameLayout.LayoutParams(holder.imageContent.getWidth(),
				holder.imageContent.getHeight()));

		holder.imgFront = (ImageView) itemView.findViewById(R.id.pageimageView);

		holder.txtSubtitle = (TextView) itemView
				.findViewById(R.id.textView_subtitle_pager);
		holder.txtTitle = (TextView) itemView
				.findViewById(R.id.textView_title_pager);
		holder.txtJP = (TextView) itemView.findViewById(R.id.textView_jp_pager);
		holder.txtEng = (TextView) itemView
				.findViewById(R.id.textView_eng_pager);

		holder.imgFront.setImageDrawable(getImage(tutorialList.get(position)
				.getId() + imagePatterName));

		holder.txtSubtitle.setText(tutorialList.get(position).getSub_title());
		holder.txtTitle.setText(tutorialList.get(position).getTitle());
		holder.txtJP.setText(tutorialList.get(position).getLabel_japanese());
		holder.txtEng.setText(tutorialList.get(position).getLabel_english());

		if (tutorialList.get(position).getTitle().equals("NIL")) {

			holder.txtTitle.setText(tutorialList.get(position).getSub_title());
		}

		container.addView(itemView);

		return itemView;
	}

	private class ViewHolder {

		public ImageView imgFront;
		public FrameLayout viewFlip;
		public RelativeLayout imageContent;
		public RelativeLayout dataContent;
		public TextView txtSubtitle;
		public TextView txtTitle;
		public TextView txtJP;
		public TextView txtEng;
	}
	
	private void flipCard() {
		ViewHolder holder;

		holder = new ViewHolder();
		final View cardFace = holder.imageContent;
		final View cardBack = holder.dataContent;


		FlipAnimation flipAnimation = new FlipAnimation(cardFace, cardBack);

		if (cardFace.getVisibility() == View.GONE) {
			flipAnimation.reverse();
		}
		holder.viewFlip.startAnimation(flipAnimation);

	}

	@Override
	public void notifyDataSetChanged() {
		// TODO Auto-generated method stub
		super.notifyDataSetChanged();
	}

	public void removedItem(int position) {

		int p = position;

		tutorialList.remove(position);
		notifyDataSetChanged();
	}

	public void addItem(int position, TutorialModel model) {

		tutorialList.add(position, model);
		notifyDataSetChanged();
	}

	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((FrameLayout) object);
	}

	private Drawable getImage(String name) {
		try {
			InputStream ims = context.getAssets().open("images/" + name);
			Drawable drawable = Drawable.createFromStream(ims, null);

			return drawable;
		} catch (IOException ex) {
			return null;
		}

	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		flipCard();
		return true;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		return false;
	}

}
