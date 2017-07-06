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

public class FriendListAdapter extends ArrayAdapter<String> {

	// Variable Declaration
	Activity mContext = null;
	ArrayList<String> friendNameList = null;
	ArrayList<String> detailList = null;

	public FriendListAdapter(Activity context, ArrayList<String> nList,
			ArrayList<String> aList) {
		super(context, R.layout.row_of_friend_list, nList);
		this.mContext = context;
		this.friendNameList = nList;
		this.detailList = aList;
		// TODO Auto-generated constructor stub
	}

	@SuppressLint({ "ViewHolder", "InflateParams" }) 
	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater mInflater = mContext.getLayoutInflater();
		View rowView = mInflater.inflate(R.layout.row_of_friend_list, null, true);

		// definition - gives variable a reference
		TextView friendName = (TextView) rowView
				.findViewById(R.id.friend_name);
		TextView friendDetail = (TextView) rowView
				.findViewById(R.id.friend_discription);

		// set text in view
		friendName.setText(friendNameList.get(position));
		friendDetail.setText(detailList.get(position));

		return rowView;
	}
}