package com.smartmux.trackfriends.adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.smartmux.trackfriends.R;


@SuppressLint("InflateParams")
public class EventListAdapter extends ArrayAdapter<String> {

	// Variable Declaration
	Activity mContext = null;
	ArrayList<String> eventNameList = null;
	ArrayList<String> numberList = null;

	public EventListAdapter(Activity context, ArrayList<String> nList,
			ArrayList<String> aList) {
		super(context, R.layout.row_of_event_list, nList);
		this.mContext = context;
		this.eventNameList = nList;
		this.numberList = aList;
		// TODO Auto-generated constructor stub
	}

	@SuppressLint("ViewHolder") 
	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater mInflater = mContext.getLayoutInflater();
		View rowView = mInflater.inflate(R.layout.row_of_event_list, null, true);

		// definition - gives variable a reference
		TextView eventName = (TextView) rowView
				.findViewById(R.id.event_name);
		TextView member = (TextView) rowView
				.findViewById(R.id.member_no);

		// set text in view
		eventName.setText(eventNameList.get(position));
		member.setText(numberList.get(position));

		return rowView;
	}
}

//public class EventListAdapter extends ArrayAdapter<EventModelClass> {
//	Activity mContext;
//	EventModelClass mEventModelClass;
//	ArrayList<EventModelClass> mArrayObject;
//	public EventListAdapter(Activity context) {
//		super(context, R.layout.row_of_event_list);
//		this.mContext = context;
//	}
//
//	public EventListAdapter(Activity context,
//			ArrayList<EventModelClass> objectArray) {
//		super(context, R.layout.row_of_event_list, objectArray);
//		this.mContext = context;
//		this.mArrayObject = objectArray;
//	}
//
//	// holder Class to contain inflated xml file elements
//	static class ViewHolder {
//		public TextView eventname;
//		public TextView membernum;
//		
//		public ImageView image;
//	}
//
//	// Create ListView row
//	@Override
//	public View getView(int position, View convertView, ViewGroup parent) {
//
//		// Get Model object from Array list
//		mEventModelClass = mArrayObject.get(position);
//		ViewHolder mVHolder = null;
//
//		View rowView = convertView;
//		if (convertView == null) {
//
//			// Layout inflater to call external xml layout ()
//			LayoutInflater inflater = mContext.getLayoutInflater();
//
//			rowView = inflater.inflate(R.layout.row_of_event_list, parent, false);
//
//			mVHolder = new ViewHolder();
//			
//			mVHolder.eventname = (TextView) rowView.findViewById(R.id.event_name);
//			mVHolder.membernum = (TextView) rowView
//					.findViewById(R.id.member_no);
//			rowView.setTag(mVHolder);
//		} else
//			mVHolder = (ViewHolder) rowView.getTag();
//
////		mVHolder.id.setText(mNoteModel.getmNoteId().toString());
////		mVHolder.startDate.setText(mNoteModel.getmDate().toString());
////		mVHolder.name.setText(mNoteModel.getmNote().toString());
//
//		return rowView;
//	}
//}