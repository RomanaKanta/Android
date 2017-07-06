package com.smartux.photocollage.dialogfragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.smartux.photocollage.CollageActivity;
import com.smartux.photocollage.R;
import com.smartux.photocollage.adapter.VarFrameAdapter;
import com.smartux.photocollage.adapter.VarFrameAdapter.OnItemClickListener;
import com.smartux.photocollage.model.FrameJsonAndThumb;
import com.smartux.photocollage.utils.Constant;
import com.smartux.photocollage.utils.FrameSelection;

import java.util.ArrayList;

public class FrameDialog extends DialogFragment {

	private static RecyclerView frameRecyclerView;
	private ArrayList<FrameJsonAndThumb> frameAndThumbList;
	private int numOfPhotos, selectedPosition;
	Bundle mArgs;
	VarFrameAdapter adapter;
	boolean isPurchasedIC;

	public void onResume() {
		super.onResume();

		DisplayMetrics displaymetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay()
				.getMetrics(displaymetrics);
		int hight = displaymetrics.heightPixels;

		getDialog().getWindow().getAttributes().gravity = Gravity.RIGHT;
		WindowManager.LayoutParams lp = getDialog().getWindow().getAttributes();
		lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
		lp.height = (int) (hight);
		getDialog().getWindow().setAttributes(lp);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.frame_dialog, container,
				false);

		getDialog().getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		getDialog().getWindow().setBackgroundDrawableResource(
				android.R.color.transparent);
		getDialog().getWindow().setGravity(Gravity.RIGHT);
		getDialog().getWindow().getAttributes().windowAnimations = R.style.AnimationBottomRightIn;

		mArgs = getArguments();
		numOfPhotos = mArgs.getInt(Constant.FRAME_NUMBER);
		selectedPosition = mArgs.getInt(Constant.SELECTED_POSITION);
		isPurchasedIC = mArgs.getBoolean(Constant.PURCHASE);

		setList();

		frameRecyclerView = (RecyclerView) rootView
				.findViewById(R.id.recycler_frame_view);
		frameRecyclerView.setHasFixedSize(true);
		frameRecyclerView.setLayoutManager(new LinearLayoutManager(
				getActivity(), LinearLayoutManager.VERTICAL, false));

		adapter = new VarFrameAdapter(getActivity(),
				frameAndThumbList, isPurchasedIC,numOfPhotos);
		adapter.setSelectedPosition(selectedPosition);
		frameRecyclerView.setAdapter(adapter);// set adapter on recyclerview
		adapter.notifyDataSetChanged();

		adapter.SetOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(View view, int position) {

				if (isPurchasedIC) {
					
					((CollageActivity) getActivity()).setFrameLayer(position);
					adapter.setSelectedPosition(position);
					adapter.notifyDataSetChanged();
					dismiss();
					
				} else {
					
					if (numOfPhotos>2 && position > 9) {
						((CollageActivity) getActivity()).purchaseDialog(getString(R.string.purch_alart_msg1));
						dismiss();
					} else {
						((CollageActivity) getActivity())
								.setFrameLayer(position);
						adapter.setSelectedPosition(position);
						adapter.notifyDataSetChanged();
						dismiss();
					}
					
				}

			}
		});

		return rootView;
	}

	private void setList() {
		
//		if(frameAndThumbList.size()>0){
//			frameAndThumbList.clear();
//		}

		if (numOfPhotos == 1) {
			frameAndThumbList = FrameSelection.getOnePhotoFrame();
		} else if (numOfPhotos == 2) {
			frameAndThumbList = FrameSelection.getTwoPhotoFrame();
		} else if (numOfPhotos == 3) {
			frameAndThumbList = FrameSelection.getThreePhotoFrame();
		} else if (numOfPhotos == 4) {
			frameAndThumbList = FrameSelection.getFourPhotoFrame();
		} else if (numOfPhotos == 5) {
			frameAndThumbList = FrameSelection.getFivePhotoFrame();
		} else if (numOfPhotos == 6) {
			frameAndThumbList = FrameSelection.getSixPhotoFrame();
		} else if (numOfPhotos == 7) {
			frameAndThumbList = FrameSelection.getSevenPhotoFrame();
		} else if (numOfPhotos == 8) {
			frameAndThumbList = FrameSelection.getEightPhotoFrame();
		} else if (numOfPhotos == 9) {
			frameAndThumbList = FrameSelection.getNinePhotoFrame();
		} else if (numOfPhotos == 10) {
			frameAndThumbList = FrameSelection.getTenPhotoFrame();
		}
	}

}
