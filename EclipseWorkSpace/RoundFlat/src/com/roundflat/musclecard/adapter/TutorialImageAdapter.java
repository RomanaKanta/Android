package com.roundflat.musclecard.adapter;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.roundflat.musclecard.R;
import com.roundflat.musclecard.model.TutorialModel;
import com.roundflat.musclecard.view.MyViewPager;

public class TutorialImageAdapter extends PagerAdapter {

	Context context;
	LayoutInflater layoutInflater;
	List<TutorialModel> tutorialList;
	String imagePatterName = "_A_000.png";
	int max = 8;
	
	public TutorialImageAdapter(Context context) {
		this.context = context;
		layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	public TutorialImageAdapter(Context context,List<TutorialModel> list) {
		this.context = context;
		layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.tutorialList = list;
	}

	@Override
	public int getCount() {
		return tutorialList.size() ;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == ((LinearLayout) object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		View itemView = layoutInflater.inflate(R.layout.pager_item_front, container,
				false);
		
//		 if (position > tutorialList.size() - 1)
//		 {
//			 position = 0;
//		 }
//		 if (position < 0 )
//		 {
//			 position = tutorialList.size() - 1;
//		 }
		
//		position = MyViewPager.setPosition(position,tutorialList);


		ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
		imageView.setImageDrawable(getImage(tutorialList.get(position).getId()
				+ imagePatterName));

		
			 container.addView(itemView);

	
		return itemView;
	}
	
	
	
	@Override
	public void notifyDataSetChanged() {
		// TODO Auto-generated method stub
		super.notifyDataSetChanged();
	}
	
	

	public void removedItem(int position){
		
		int p = position;
		
		tutorialList.remove(position);
		notifyDataSetChanged();
	}
	public void addItem(int position,TutorialModel model){
		
		tutorialList.add(position, model);
		notifyDataSetChanged();
	}

	public int getItemPosition(Object object){
	     return POSITION_NONE;
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((LinearLayout) object);
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

}
