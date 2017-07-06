package com.smartmux.textmemo.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.smartmux.textmemo.R;
import com.smartmux.textmemo.SettingsActivity;
import com.smartmux.textmemo.modelclass.ColorItem;

public class ColorListAdapter extends ArrayAdapter<ColorItem> {

	private LayoutInflater mInflater;
	ArrayList<ColorItem> values;
	boolean txtColor = false;
	Context context;

	public ColorListAdapter(Context context, ArrayList<ColorItem> value, boolean textcolor) {
		super(context, R.layout.color_list_row, value);

		mInflater = (LayoutInflater) getContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		this.context = context;
		this.values = value;
		this.txtColor = textcolor;
	
	}

	@Override
	public int getCount() {
		return values.size();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final MatrixHolder holder;

		if (convertView == null) {
			// Inflate the view since it does not exist
			convertView = mInflater.inflate(R.layout.color_list_row, parent,
					false);

			// Create and save off the holder in the tag so we get quick access
			// to inner fields
			// This must be done for performance reasons
			holder = new MatrixHolder();

			holder.colorView = (View) convertView.findViewById(R.id.view_color);
			holder.secColorView = (View) convertView.findViewById(R.id.view_color2);
			
			convertView.setTag(holder);
		} else {
			holder = (MatrixHolder) convertView.getTag();
		}

		holder.colorView.setBackgroundResource(R.drawable.circuler_background);

		GradientDrawable drawable = (GradientDrawable) holder.colorView.getBackground();
		  drawable.setColor(Color.parseColor(values
					.get(position).getFirstColor()));
	
		  
			holder.secColorView.setBackgroundResource(R.drawable.circuler_background);

			GradientDrawable secdrawable = (GradientDrawable) holder.secColorView.getBackground();
			secdrawable.setColor(Color.parseColor(values
						.get(position).getSecondColor()));


			holder.colorView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					if(txtColor){
						if(context instanceof SettingsActivity){
							
			                ((SettingsActivity)context).setTextColor(Color.parseColor(values
					.get(position).getFirstColor()));
			            }
						}
						
						if(!txtColor){
							if(context instanceof SettingsActivity){
								
				                ((SettingsActivity)context).setBackgroundColor(Color.parseColor(values
						.get(position).getFirstColor()));
				            }
						}	
				}
			});
			
	holder.secColorView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					if(txtColor){
						if(context instanceof SettingsActivity){
							
							
			                ((SettingsActivity)context).setTextColor(Color.parseColor(values
					.get(position).getSecondColor()));
			            }
						}
						
						if(!txtColor){
							if(context instanceof SettingsActivity){
								
				                ((SettingsActivity)context).setBackgroundColor(Color.parseColor(values
						.get(position).getSecondColor()));
				            }
						}	
				}
			});
			
		return convertView;
	}

	/** View holder for the views we need access to */
	private static class MatrixHolder {
		public View colorView,secColorView;
	}
}