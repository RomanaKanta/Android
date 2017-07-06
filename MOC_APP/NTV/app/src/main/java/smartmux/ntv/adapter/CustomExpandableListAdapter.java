/*
package smartmux.ntv.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import smartmux.ntv.R;
import smartmux.ntv.model.MenuModel;

public class CustomExpandableListAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private List<MenuModel> mExpandableListTitle;
    private Map<MenuModel, List<String>> mExpandableListDetail;
    private LayoutInflater mLayoutInflater;

    public CustomExpandableListAdapter(Context context, List<MenuModel> expandableListTitle,
                                       Map<MenuModel, List<String>> expandableListDetail) {
        mContext = context;
        mExpandableListTitle = expandableListTitle;
        mExpandableListDetail = expandableListDetail;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return mExpandableListDetail.get(mExpandableListTitle.get(listPosition)).get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final String expandedListText = (String) getChild(listPosition, expandedListPosition);
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.list_item, null);
        }
        TextView expandedListChildView = (TextView) convertView
            .findViewById(R.id.list_child);
        expandedListChildView.setText(expandedListText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return mExpandableListDetail.get(mExpandableListTitle.get(listPosition))
            .size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return mExpandableListTitle.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return mExpandableListTitle.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String listTitle = ((MenuModel) getGroup(listPosition)).getName();
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.list_group, null);
        }
        TextView listParentView = (TextView) convertView
            .findViewById(R.id.listTitle);
        //listTitleTextView.setTypeface(null, Typeface.BOLD);
        listParentView.setText(listTitle);

        if(getChildrenCount(listPosition)>0) {

            if (isExpanded) {
                listParentView.setTypeface(null, Typeface.BOLD);
                listParentView.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                        R.drawable.ic_keyboard_arrow_up_black_18dp, 0);
            } else {
                // If group is not expanded then change the text back into normal
                // and change the icon
                listParentView.setTypeface(null, Typeface.NORMAL);
                listParentView.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                        R.drawable.ic_keyboard_arrow_down_black_18dp, 0);
            }
        }else {
            listParentView.setTypeface(null, Typeface.NORMAL);
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
*/
