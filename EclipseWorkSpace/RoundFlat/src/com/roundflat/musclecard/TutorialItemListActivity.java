package com.roundflat.musclecard;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView.OnStickyHeaderChangedListener;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView.OnStickyHeaderOffsetChangedListener;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class TutorialItemListActivity extends FragmentActivity implements OnItemClickListener, OnStickyHeaderChangedListener,
OnStickyHeaderOffsetChangedListener{

	@Override
	public void onStickyHeaderOffsetChanged(StickyListHeadersListView l,
			View header, int offset) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStickyHeaderChanged(StickyListHeadersListView l, View header,
			int itemPosition, long headerId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}

}
