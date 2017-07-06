package com.smartux.photocollage.dialogfragment;

import android.app.DialogFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.smartux.photocollage.CollageActivity;
import com.smartux.photocollage.R;
import com.smartux.photocollage.adapter.RecyclerView_ColorAdapter;
import com.smartux.photocollage.model.ColorItem;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.util.ArrayList;
import java.util.Arrays;

public class ColorDialog extends DialogFragment {

	private static RecyclerView colorRecyclerView;
	private ImageView downArrow;
    org.adw.library.widgets.discreteseekbar.DiscreteSeekBar borderSeekbar;
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
		lp.width = (int) (width);
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		getDialog().getWindow().setAttributes(lp);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.color_dialog, container,
				false);

		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		getDialog().getWindow().setBackgroundDrawableResource(
				android.R.color.transparent);
		getDialog().getWindow().setGravity(Gravity.BOTTOM);
		getDialog().getWindow().getAttributes().windowAnimations = R.style.AnimationBottomUpDown;

		setList() ;
		
		colorRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_color_view);
		colorRecyclerView.setHasFixedSize(true);
		colorRecyclerView.setLayoutManager(new LinearLayoutManager(
				getActivity(), LinearLayoutManager.HORIZONTAL,
				false));
		downArrow = (ImageView) rootView.findViewById(R.id.downArrow);

		populatRecyclerView();
		
		borderSeekbar = (org.adw.library.widgets.discreteseekbar.DiscreteSeekBar) rootView.findViewById(R.id.borderSeekbar);

        String color = ((CollageActivity)getActivity()).borderColor;
        borderSeekbar.setRippleColor(Color.parseColor(color));
        borderSeekbar.setScrubberColor(Color.parseColor(color));
        borderSeekbar.setThumbColor(Color.parseColor(color),Color.WHITE);
//        borderSeekbar.setTrackColor(Color.parseColor(color));

		int prog  =((CollageActivity) getActivity()).borderStroke;
		borderSeekbar.setProgress(prog);
        borderSeekbar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar discreteSeekBar, int i, boolean b) {
                ((CollageActivity) getActivity()).setBorderStroke(i);
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar discreteSeekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar discreteSeekBar) {

            }
        });



//		borderSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
//
//			@Override
//			public void onStopTrackingTouch(SeekBar arg0) {
//			}
//
//			@Override
//			public void onStartTrackingTouch(SeekBar arg0) {
//			}
//
//			@Override
//			public void onProgressChanged(SeekBar arg0, int number, boolean arg2) {
//
//				((CollageActivity) getActivity()).setBorderStroke(number);
//			}
//		});

		downArrow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		return rootView;
	}
	
	void setList() {

		if(textarray.size()>0){
			textarray.clear();
			array.clear();
			
			}
		
		String[] text = getResources().getStringArray(R.array.color_array);

		textarray.addAll(Arrays.asList(text));
//		Collections.shuffle(textarray);
		for (int i = 0; i < textarray.size(); i = i + 2) {
			array.add(new ColorItem(textarray.get(i), textarray.get(i + 1),""));
		}

	}
	
	// populate the list view by adding data to arraylist
	private void populatRecyclerView() {
	
		RecyclerView_ColorAdapter adapter = new RecyclerView_ColorAdapter(
				getActivity(), array);
		colorRecyclerView.setAdapter(adapter);// set adapter on recyclerview
		adapter.notifyDataSetChanged();// Notify the adapter

	}


	
	
}
