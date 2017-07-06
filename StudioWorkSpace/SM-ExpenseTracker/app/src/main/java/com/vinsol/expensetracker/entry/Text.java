/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     


package com.vinsol.expensetracker.entry;

import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.smartmux.expensetracker.R;

public class Text extends EditAbstract {

	private ScrollView fav;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		typeOfEntry = R.string.text;
		typeOfEntryFinished = R.string.unfinished_textentry;
		typeOfEntryUnfinished = R.string.unfinished_textentry;
		editHelper();
		createDatabaseEntry();
		setFavoriteHelper();
		
		 fav=(ScrollView)findViewById(R.id.edit_body);
		fav.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				hideKeyBoard();
				return false;
			}
		});
	}
	@Override
	protected String getTypeOfEntryForFlurry() {
		return getString(R.string.finished_textentry);
	}

	@Override
	protected void setDefaultTitle() {
		if(isFromFavorite) {
			((TextView)findViewById(R.id.header_title)).setText(getString(R.string.edit_favorite)+" "+getString(R.string.finished_textentry));
		} else {
			((TextView)findViewById(R.id.header_title)).setText(getString(R.string.finished_textentry));
		}
	}
	
	@Override
	protected boolean checkFavoriteComplete() {
		if(editAmount != null && !editAmount.getText().toString().equals("") && editTag != null && !editTag.getText().toString().equals("")) {
			return true;
		}
		return false;
	}
	@Override
	public void onBackPressed(){
		finish();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}
	
	public void hideKeyBoard(){
		 InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		    imm.hideSoftInputFromWindow(fav.getWindowToken(), 0);
	}
}
