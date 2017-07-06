package com.smartux.photocollage.dialogfragment;

import java.util.ArrayList;
import java.util.Arrays;

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

import com.smartux.photocollage.R;
import com.smartux.photocollage.adapter.RecyclerView_EffectAdapter;
import com.smartux.photocollage.model.ColorItem;

public class EffectDialog extends DialogFragment{

	private static RecyclerView effectRecyclerView;

	ArrayList<String> textarray = new ArrayList<String>();
	ArrayList<ColorItem> array = new ArrayList<ColorItem>();

	public void onResume() {
		super.onResume();

		DisplayMetrics displaymetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay()
				.getMetrics(displaymetrics);
		int width = displaymetrics.widthPixels;

		getDialog().getWindow().getAttributes().gravity = Gravity.BOTTOM;
		WindowManager.LayoutParams lp = getDialog().getWindow().getAttributes();
		lp.width = (int) ( width);
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		getDialog().getWindow().setAttributes(lp);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater
				.inflate(R.layout.effect_dialog, container, false);

		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		getDialog().getWindow().setBackgroundDrawableResource(
				android.R.color.transparent);
		getDialog().getWindow().setGravity(Gravity.BOTTOM);
		 getDialog().getWindow()
		    .getAttributes().windowAnimations = R.style.AnimationBottomUpDown;
		 
			setList() ;
		 
		 effectRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_color_effect_view);
		 effectRecyclerView.setHasFixedSize(true);
		 effectRecyclerView.setLayoutManager(new LinearLayoutManager(
					getActivity(), LinearLayoutManager.HORIZONTAL,
					false));

			populatRecyclerView();


		return rootView;
	}
	
	void setList() {
		
		if(textarray.size()>0){
		textarray.clear();
		array.clear();
		}

		String[] text = getResources().getStringArray(R.array.effects_color_array);
		textarray.addAll(Arrays.asList(text));
//		Collections.shuffle(textarray);
		for (int i = 0; i < textarray.size(); i = i + 3) {
			array.add(new ColorItem(textarray.get(i), textarray.get(i + 1),textarray.get(i + 2)));
		}
		

	}
	
	// populate the list view by adding data to arraylist
	private void populatRecyclerView() {
	
		RecyclerView_EffectAdapter adapter = new RecyclerView_EffectAdapter(
				getActivity(), array);
		effectRecyclerView.setAdapter(adapter);// set adapter on recyclerview
		adapter.notifyDataSetChanged();// Notify the adapter

	}

	
	
}
