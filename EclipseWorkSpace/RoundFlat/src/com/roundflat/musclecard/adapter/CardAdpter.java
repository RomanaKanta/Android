package com.roundflat.musclecard.adapter;

import java.util.ArrayList;
import java.util.List;

import com.roundflat.musclecard.model.TutorialModel;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;


public class CardAdpter extends FragmentStatePagerAdapter {
    private List<TutorialModel> items;
    String imagePatterName = "_A_000.png";
    public CardAdpter(FragmentActivity activity,List<TutorialModel> cards) {
      super(activity.getSupportFragmentManager());

      items = new ArrayList<>(cards.size());
      for(int i = 0; i < cards.size(); i++) {
        items.add(cards.get(i));
      }
    }
    
    public void setList(List<TutorialModel> cards){
    	
    	 items = cards;
    	 notifyDataSetChanged();
    }

    public void insertAt(int position,TutorialModel card) {
      items.add(position, card);
//      for(int i = position + 1; i < items.size(); i++) {
//        items.set(i, items.get(i));
//      }
      System.out.println(items.size());
      notifyDataSetChanged();
    }

    public void removeFrom(int position) {
      items.remove(position);
//      for(int i = position; i < items.size(); i++) {
//    	  items.set(i, items.get(i));
//      }
      System.out.println(items.size());
      notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
      return CardFragment.newInstance(items.get(position).getId()
    		  + imagePatterName);
    }

    @Override
    public int getCount() {
      return items.size();
    }
  }

