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

public class TutorialDataAdapter extends PagerAdapter {

	Context context;
	LayoutInflater layoutInflater;
	List<TutorialModel> tutorialList;

	public TutorialDataAdapter(Context context,List<TutorialModel> list) {
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
		View itemView = layoutInflater.inflate(R.layout.pager_item_back, container,
				false);

		
		TextView txtSubtitle = (TextView) itemView.findViewById(R.id.textView_subtitle);
		TextView txtTitle = (TextView) itemView.findViewById(R.id.textView_title);
		TextView txtJP = (TextView) itemView.findViewById(R.id.textView_jp);
		TextView txtEng = (TextView) itemView.findViewById(R.id.textView_eng);

		txtSubtitle.setText(tutorialList.get(position).getSub_title());
		txtTitle.setText(tutorialList.get(position).getTitle());
		txtJP.setText(tutorialList.get(position).getLabel_japanese());
		txtEng.setText(tutorialList.get(position).getLabel_english());

		if ( tutorialList.get(position).getTitle().equals("NIL")) {

			txtTitle.setText(tutorialList.get(position).getSub_title());
		}
		
		container.addView(itemView);

		return itemView;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((LinearLayout) object);
	}
	

}
