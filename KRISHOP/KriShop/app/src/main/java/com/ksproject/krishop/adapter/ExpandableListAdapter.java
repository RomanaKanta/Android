package com.ksproject.krishop.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ksproject.krishop.R;
import com.ksproject.krishop.widget.AnimatedExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;


public class ExpandableListAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {

    private Context mContext;
    private ArrayList<String> mExpandableListHeader;
    private HashMap<String, ArrayList<String>> mExpandableListChild;
    private LayoutInflater mLayoutInflater;
    Typeface regular;
    Typeface bold;

    public ExpandableListAdapter(Context context, ArrayList<String> expandableListTitle,
                                 HashMap<String, ArrayList<String>> expandableListDetail) {
        mContext = context;
        mExpandableListHeader = expandableListTitle;
        mExpandableListChild = expandableListDetail;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         regular = Typeface.createFromAsset(context.getAssets(),
                 "font/Lato-Regular.ttf");
         bold = Typeface.createFromAsset(context.getAssets(),
                 "font/Lato-Bold.ttf");
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return mExpandableListChild.get(mExpandableListHeader.get(listPosition)).get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getRealChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String expandedListText = (String) getChild(groupPosition, childPosition);
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.list_item, null);
        }
        TextView expandedListChildView = (TextView) convertView
                .findViewById(R.id.list_child);
        expandedListChildView.setText(expandedListText);
        expandedListChildView.setTypeface(regular);
        return convertView;
    }

    @Override
    public int getRealChildrenCount(int groupPosition) {
        return mExpandableListChild.get(mExpandableListHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return mExpandableListHeader.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return mExpandableListHeader.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String listTitle = mExpandableListHeader.get(listPosition);
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.list_group, null);
        }
        TextView listParentView = (TextView) convertView
                .findViewById(R.id.listTitle);

//        View devider = (View) convertView.findViewById(R.id.divider);

//        if (listPosition==5){
//            devider.setVisibility(View.VISIBLE);
//        }else {
//            devider.setVisibility(View.GONE);
//        }

        //listTitleTextView.setTypeface(null, Typeface.BOLD);
        listParentView.setText(listTitle);

        if (getChildrenCount(listPosition) > 0) {

            if (isExpanded) {
                listParentView.setTypeface(bold);
                listParentView.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                        R.mipmap.ic_keyboard_arrow_up_black_18dp, 0);
//                listParentView.setCompoundDrawablesWithIntrinsicBounds(mExpandableListHeader.get(listPosition).getIcon(), 0,
//                        R.mipmap.ic_keyboard_arrow_up_black_18dp, 0);

            } else {
                // If group is not expanded then change the text back into normal
                // and change the icon
                listParentView.setTypeface(regular);
                listParentView.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                        R.mipmap.ic_keyboard_arrow_down_black_18dp, 0);
            }
        } else {
            listParentView.setTypeface(regular);
            listParentView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }
}
