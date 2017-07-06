package com.ksproject.krishop.dialogfragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.ksproject.krishop.R;
import com.ksproject.krishop.modelclass.AppData;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class FilterDialog extends DialogFragment {

	public void onResume() {
		super.onResume();

		DisplayMetrics displaymetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay()
				.getMetrics(displaymetrics);
		int hight = displaymetrics.heightPixels;
		int width = displaymetrics.widthPixels;

		getDialog().getWindow().getAttributes().gravity = Gravity.CENTER;
		WindowManager.LayoutParams lp = getDialog().getWindow().getAttributes();
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.MATCH_PARENT;
//		lp.height = (int) (hight);
//		lp.width = (int) (width);
		getDialog().getWindow().setAttributes(lp);

	}

	@OnClick(R.id.txtType)
	public void setType(){

//		new CustomizeDialog(
//				getActivity(), "Type",new ArrayList<String>()).show();

	}

	@OnClick(R.id.txtArea)
	public void setArea(){

		new CustomizeDialog(
				getActivity(), "Area", AppData.area).show();

	}

	@OnClick(R.id.txtTime)
	public void setTime(){

		new CustomizeDialog(
				getActivity(), "Time",AppData.harvest_time).show();

	}

	@OnClick(R.id.btn_filter)
	public void setFilter(){

		dismiss();

	}

	@OnClick(R.id.back)
	public void back(){
		dismiss();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.filter_dialog, container,
				false);

//		getDialog().getWindow().setFlags(
//				WindowManager.LayoutParams.FLAG_FULLSCREEN,
//				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		getDialog().getWindow().setBackgroundDrawableResource(
				android.R.color.black);
//		getDialog().getWindow().setGravity(Gravity.RIGHT);
		getDialog().getWindow().getAttributes().windowAnimations = R.style.AnimationBottomRightIn;

		ButterKnife.bind(this, rootView);

		return rootView;
	}


}
