package com.roundflat.musclecard.adapter;

import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;

import java.util.List;

import com.roundflat.musclecard.model.TutorialModel;

/**
 * User: tobiasbuchholz
 * Date: 18.09.14 | Time: 13:18
 */
public abstract class BaseCircularViewPagerAdapter extends FragmentStatePagerAdapter {
 
	
	private List<TutorialModel> mItems;

    public BaseCircularViewPagerAdapter( FragmentManager fragmentManager,  List<TutorialModel> items) {
        super(fragmentManager);
        mItems = items;
    }

    protected abstract Fragment getFragmentForItem(final TutorialModel item);

    @Override
    public Fragment getItem(final int position) {
        final int itemsSize = mItems.size();
        if(position == 0) {
            return getFragmentForItem(mItems.get(itemsSize - 1));
        } else if(position == itemsSize + 1) {
            return getFragmentForItem(mItems.get(0));
        }else if(position == itemsSize + 1) {
            return getFragmentForItem(mItems.get(0));
        } else {
        	System.out.println("getItem "+(position - 1));
            return getFragmentForItem(mItems.get(position - 1));
        }
    }

    @Override
    public int getCount() {
        final int itemsSize = mItems.size();
        return itemsSize > 1 ? itemsSize + 2 : itemsSize;
    }

    public int getCountWithoutFakePages() {
        return mItems.size();
    }

    public void setItems(final List<TutorialModel> items) {
        mItems = items;
        notifyDataSetChanged();
    }
    
//public void removedItem(int position){
//		
//		mItems.remove(position);
//		notifyDataSetChanged();
//	}
    
    public int removeView (ViewPager pager, int position)
    {
      // ViewPager doesn't have a delete method; the closest is to set the adapter
      // again.  When doing so, it deletes all its views.  Then we can delete the view
      // from from the adapter and finally set the adapter to the pager again.  Note
      // that we set the adapter to null before removing the view from "views" - that's
      // because while ViewPager deletes all its views, it will call destroyItem which
      // will in turn cause a null pointer ref.
      pager.setAdapter (null);
      mItems.remove (position);
      pager.setAdapter (this);

      return position;
    }
    
    public void hideBackContent(int position){
    	
    	 MemeViewPagerItemFragment page = (MemeViewPagerItemFragment) getItem(position);
    	 if (page != null) {
	    	 Log.i("page", "got it");
	          ((MemeViewPagerItemFragment)page).hideBackPage();   
	     } else{
	    	 
	    	 Log.i("page", "not got it");
	     }
    }

public void insertAt(int position,TutorialModel card) {
	mItems.add(position, card);
    notifyDataSetChanged();
  }
}
