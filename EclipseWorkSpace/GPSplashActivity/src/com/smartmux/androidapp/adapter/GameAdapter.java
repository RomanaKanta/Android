package com.smartmux.androidapp.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewAnimator;

import com.smartmux.androidapp.R;
import com.smartmux.androidapp.model.TutorialModel;
import com.smartmux.androidapp.util.Constant;
import com.smartmux.androidapp.util.PreferenceUtils;
import com.smartmux.androidapp.util.Utils;

public class GameAdapter extends BaseAdapter {

	private int count;
	private int cardType;
	private Context context;
	private String type;
	private int imageWidth, imageHight;
	private List<TutorialModel> tutorialList;
	//private boolean isRandom, IsFav;
	private String imagePatterName = "_A_000.png";
	private int version = android.os.Build.VERSION.SDK_INT;

	public GameAdapter(Context context, int count, String type, int imageWidth,
			int imageHight, int cardType,List<TutorialModel> tutorialList) {
		super();
		this.context = context;
		this.count = count;
		this.cardType = cardType;
		this.type = type;
		this.imageWidth = imageWidth;
		this.imageHight = imageHight;
		this.tutorialList = tutorialList;

//		IsSubtitleShow = PreferenceUtils.getBoolPref(context,
//				Constant.PREF_NAME, Constant.PREF_SUBTITILE, false);
//		isRandom = PreferenceUtils.getBoolPref(context, Constant.PREF_NAME,
//				Constant.PREF_RANDOM, false);

	}

	public int getCount() {

		return count;
	}

	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public View getView(final int position, View convertView, ViewGroup parent) {

		final ViewHolder holder;
		if (convertView == null) {

			holder = new ViewHolder();
			LayoutInflater inflater = LayoutInflater.from(context);

			convertView = inflater.inflate(R.layout.item, parent, false);

			holder.viewAnimator = (ViewAnimator) convertView
					.findViewById(R.id.viewFlipper);

			holder.content1 = (RelativeLayout) convertView
					.findViewById(R.id.content1);

			holder.imgFront = (ImageView) convertView
					.findViewById(R.id.imageView_front);

			holder.content2 = (RelativeLayout) convertView
					.findViewById(R.id.content2);
			holder.imgBack = (ImageView) convertView
					.findViewById(R.id.imageView_back);

			holder.content3 = (RelativeLayout) convertView
					.findViewById(R.id.content3);
			holder.viewAnimator.removeAllViews();

			if (cardType == 0) {

				holder.viewAnimator.addView(holder.content1, 0);
				holder.viewAnimator.addView(holder.content2, 1);
				holder.content2.setLayoutParams(new FrameLayout.LayoutParams(
						imageWidth, imageHight));
				holder.imgBack.setClickable(false);
				holder.imgBack.setFocusable(false);
				holder.imgBack.setFocusableInTouchMode(false);
			} else {

				holder.viewAnimator.addView(holder.content1, 0);
				holder.viewAnimator.addView(holder.content3, 1);
				holder.content3.setLayoutParams(new FrameLayout.LayoutParams(
						imageWidth, imageHight));
				holder.txtTitle = (TextView) convertView
						.findViewById(R.id.textView_title);
				holder.txtSubtitle = (TextView) convertView
						.findViewById(R.id.textView_subtitle);
				holder.txtJP = (TextView) convertView
						.findViewById(R.id.textView_jp);
				holder.txtEngl = (TextView) convertView
						.findViewById(R.id.textView_eng);
				
				
			}
			holder.txtID = (TextView) convertView
					.findViewById(R.id.textView_id);
			holder.content1.setLayoutParams(new FrameLayout.LayoutParams(
					imageWidth, imageHight));

			holder.imgFront.setClickable(false);
			holder.imgFront.setFocusable(false);
			holder.imgFront.setFocusableInTouchMode(false);

			holder.imgFront.setTag(tutorialList.get(position).getId());

			convertView.setTag(holder);

		} else {

			holder = (ViewHolder) convertView.getTag();
		}
		holder.txtID.setText(tutorialList.get(position).getId());
		
		if (type.equals(Constant.EASY)) {

			if (position == 4) {

				holder.txtID.setVisibility(View.INVISIBLE);
				holder.imgFront
						.setBackgroundResource(R.drawable.img_easy_transparent);
			} else {
				holder.txtID.setVisibility(View.VISIBLE);
				holder.imgFront.setBackgroundResource(R.drawable.img_easy);
			}

		} else if (type.equals(Constant.MEDIUM)) {
			holder.imgFront.setBackgroundResource(R.drawable.img_medium);

		} else if (type.equals(Constant.DIFFICULT)) {
			holder.imgFront.setBackgroundResource(R.drawable.img_difficult);

		}

		if (cardType == 0) {
			if (version >= 16) {

				holder.imgBack.setBackground(Utils.getImage(context,
						tutorialList.get(position).getId() + imagePatterName));
			} else {
				holder.imgBack.setBackgroundDrawable(Utils.getImage(context,
						tutorialList.get(position).getId() + imagePatterName));

			}
		}else{
			
			holder.txtTitle.setText(tutorialList.get(position).getTitle());
			holder.txtSubtitle.setText(tutorialList.get(position).getSub_title());
			holder.txtJP.setText(tutorialList.get(position).getLabel_japanese());
			holder.txtEngl.setText(tutorialList.get(position).getLabel_english());
		}

		

		return convertView;

	}

	private class ViewHolder {

		public ImageView imgFront;
		public ImageView imgBack;
		public ViewAnimator viewAnimator;
		public RelativeLayout content1;
		public RelativeLayout content2;
		public RelativeLayout content3;
		public TextView txtSubtitle;
		public TextView txtTitle;
		public TextView txtJP;
		public TextView txtEngl;
		public TextView txtID;
	}

}
