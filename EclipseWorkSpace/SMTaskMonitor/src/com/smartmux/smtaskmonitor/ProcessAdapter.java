package com.smartmux.smtaskmonitor;

import java.util.List;
import java.util.Map;

import com.smartmux.smtaskmonitor.utils.SMConstant;

import android.app.Activity;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ProcessAdapter extends BaseAdapter {
	private List<Map<String, Object>> processList;

	private Activity context;


	static class ViewHolder {
		public TextView processName ;
		public TextView processId;
		public TextView cpuUsage;
		public TextView memoryUsage;
		public ImageView image;
		/*
		 * public TextView name,address; public ImageView image;
		 */
	}

	public ProcessAdapter(Activity context, List<Map<String, Object>> processList) {
		super();
		this.processList = processList;
		this.context = context;
		
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return processList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return processList.get(position);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	
		ViewHolder viewHolder;
		if (convertView == null) {
			// LayoutInflater
			// inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			LayoutInflater inflater = context.getLayoutInflater();
			convertView = inflater.inflate(R.layout.process_row, parent, false);
			 viewHolder = new ViewHolder();
			viewHolder.processName = (TextView) convertView
					.findViewById(R.id.textView_pName);

			viewHolder.processId = (TextView) convertView
					.findViewById(R.id.textView_pId);

			viewHolder.cpuUsage = (TextView) convertView
					.findViewById(R.id.textView_cpuUsage);
			viewHolder.memoryUsage = (TextView) convertView
					.findViewById(R.id.textView_memoryUsage);

			viewHolder.image = (ImageView) convertView
					.findViewById(R.id.imageView_icon);
			convertView.setTag(viewHolder);
		}else{
			
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		Drawable d = null;
		try {
			d = context.getPackageManager().getApplicationIcon(
					(String) processList.get(position).get(SMConstant.pPackage));
		} catch (NameNotFoundException e) {
		}

		Log.d("imagename", (String) processList.get(position).get(SMConstant.pPackage));
		viewHolder.image.setImageDrawable(d);

            
		viewHolder.processName.setText(processList.get(position).get(SMConstant.pName).toString());
		int colour = (Integer) processList.get(position).get(SMConstant.pColour);

		
		viewHolder.processName.setText((String) processList.get(position).get(SMConstant.pAppName));
		viewHolder.processName.setTextColor(colour);

		
		viewHolder.processId.setText("Pid: " + processList.get(position).get(SMConstant.pId));

		
		viewHolder.cpuUsage.setTextColor(colour);
		viewHolder.memoryUsage.setTextColor(colour);
		return convertView;

	}

	
}