package com.kanta.studio.watchout.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import com.kanta.studio.watchout.LevelActivity;
import com.kanta.studio.watchout.R;


@SuppressLint("NewApi") public class AlartMessage {

	public AlartMessage(final Activity mActivity,final int i) {
		// TODO Auto-generated constructor stub
		AlertDialog.Builder builder = new AlertDialog.Builder(
				mActivity);

		builder.setTitle(R.string.win);
		builder.setIcon(R.drawable.winface);

		builder.setCancelable(false);
		// Add the buttons

		builder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(
							final DialogInterface dialog,
							int id) {
						int level = PreferenceUtils.getIntPref
								(mActivity.getApplicationContext(), 
										ConstantString.PREF_NAME,
										ConstantString.level, 0);
						
						if(i>level){
						PreferenceUtils.saveIntPref(mActivity.getApplicationContext(), ConstantString.PREF_NAME, ConstantString.level, i);
						}
						Intent intentLevel = new Intent(mActivity,
								LevelActivity.class);
						mActivity.startActivity(intentLevel);
						
						mActivity.finish();
						dialog.dismiss();

					}
				});

		// Create the AlertDialog
		builder.show();

	}
	
	
	public AlartMessage(final Activity mActivity) {
		// TODO Auto-generated constructor stub
		AlertDialog.Builder builder = new AlertDialog.Builder(
				mActivity);

		builder.setTitle(R.string.lost);
		builder.setIcon(R.drawable.lostface);

		builder.setCancelable(false);
		// Add the buttons

		builder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(
							final DialogInterface dialog,
							int id) {
						
						Intent intentLevel = new Intent(mActivity,
								LevelActivity.class);
						intentLevel.putExtra(ConstantString.active_level, 1);
						mActivity.startActivity(intentLevel);
						
						mActivity.finish();
						dialog.dismiss();

					}
				});

		// Create the AlertDialog
		builder.show();

	}
}

	
