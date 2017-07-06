package com.smartmux.textmemo.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.smartmux.textmemo.R;
import com.smartmux.textmemo.modelclass.TextSizeModel;

public class TextSizeAdapter extends   ArrayAdapter<TextSizeModel>{

	private LayoutInflater mInflater;
	private ArrayList<TextSizeModel> txtSizeList ;
	Context context;
	boolean font;
	Typeface tf;

	public TextSizeAdapter(Context context, ArrayList<TextSizeModel> value,boolean font) {
		super(context, R.layout.text_size_item, value);

		mInflater = (LayoutInflater) getContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		this.context = context;
		this.txtSizeList = value;
		this.font = font;
	
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;

		MatrixHolder holder = null;
		try {
			if (row == null) {
				row = mInflater.inflate(R.layout.text_size_item, parent,
						false);
				holder = new MatrixHolder();
				holder.txtTitle = (TextView) row
						.findViewById(R.id.textsize_title);
				holder.mCheckBox = (CheckBox) row
						.findViewById(R.id.chk_box_txtsize);
				row.setTag(holder);
			} else {
				holder = (MatrixHolder) row.getTag();
			}
			holder.mCheckBox.setId(position);
			holder.mCheckBox.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					for (int i = 0; i < txtSizeList.size(); i++) {
						if (v.getId() == i) {
							txtSizeList.get(i).setmValue(true);
						} else {
							txtSizeList.get(i).setmValue(false);
						}
					}
					notifyDataSetChanged();
				}
			});
			
			if(font){
				if (position==0) {

					holder.txtTitle.setTypeface(Typeface.DEFAULT);

				} else if (position==1) {

					holder.txtTitle.setTypeface(Typeface.MONOSPACE);

				} else if (position==2) {

					holder.txtTitle.setTypeface(Typeface.SANS_SERIF);

				} else if (position==3) {

					holder.txtTitle.setTypeface(Typeface.SERIF);

				}else if (position > 3 && position <= 10) {

					tf = Typeface.createFromAsset(context.getAssets(),
							"fonts/" + txtSizeList.get(position).getmTitle() + ".ttf");
					holder.txtTitle.setTypeface(tf);
					
				}else if (position > 10) {

					tf = Typeface.createFromAsset(context.getAssets(),
							"fonts/" + txtSizeList.get(position).getmTitle() + ".otf");
					holder.txtTitle.setTypeface(tf);
				}
			
			}
			
			holder.txtTitle.setText(txtSizeList.get(position).getmTitle());
			holder.mCheckBox.setChecked(txtSizeList.get(position)
					.ismValue());

		} catch (Exception aEx) {
		}

		return row;
	}

	private class MatrixHolder {
		TextView txtTitle;
		CheckBox mCheckBox;
	}

	@Override
	public int getCount() {

		return txtSizeList.size();
	}

	@Override
	public TextSizeModel getItem(int position) {

		return txtSizeList.get(position);
	}

	@Override
	public long getItemId(int arg0) {

		return arg0;
	}
}
