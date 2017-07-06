package com.smartmux.androidapp.adapter;

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

import com.smartmux.androidapp.R;
import com.smartmux.androidapp.model.TutorialModel;

public class TutorialImageAdapter extends PagerAdapter {

	Context context;
	LayoutInflater layoutInflater;
	List<TutorialModel> tutorialList;
	String imagePatterName = "_A_000.png";

	public TutorialImageAdapter(Context context,List<TutorialModel> list) {
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
		return view == ((LinearLayout) object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		View itemView = layoutInflater.inflate(R.layout.pager_item_front, container,
				false);

		ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
		imageView.setImageDrawable(getImage(tutorialList.get(position).getId()
				+ imagePatterName));
		
		

		container.addView(itemView);

		return itemView;
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
