package com.roundflat.musclecard.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.List;

import com.roundflat.musclecard.model.TutorialModel;

/**
 * User: tobiasbuchholz
 * Date: 18.09.14 | Time: 10:59
 */
public class MemeCircularViewPagerAdapter extends BaseCircularViewPagerAdapter {
    private final Context mContext;

    public MemeCircularViewPagerAdapter(final Context context,  FragmentManager fragmentManager,  List<TutorialModel> cards) {
        super(fragmentManager, cards);
        mContext = context;
    }

    @Override
    protected Fragment getFragmentForItem(final TutorialModel card) {
        return MemeViewPagerItemFragment.instantiateWithArgs(mContext, card);
    }
}
