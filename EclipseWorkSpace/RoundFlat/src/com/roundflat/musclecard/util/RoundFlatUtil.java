package com.roundflat.musclecard.util;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.roundflat.musclecard.R;
import com.roundflat.musclecard.database.DatabaseHandler;
import com.roundflat.musclecard.model.TutorialModel;

public class RoundFlatUtil {
	
	public static void setAllSelectScreen(final Activity mActivity,boolean flag){

		ImageView ImageViewallfav = (ImageView) mActivity
				.findViewById(R.id.imageview_all_selected);
		ImageViewallfav.setVisibility(View.VISIBLE);
		
		TextView txtnumberOfTutorial = (TextView) mActivity
				.findViewById(R.id.textView_numberoftutorial);
		txtnumberOfTutorial.setVisibility(View.GONE);

		if(flag){
		AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

		builder.setMessage(R.string.all_card_mark);

		builder.setCancelable(false);
		// Add the buttons
		builder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						mActivity.finish();
						mActivity.overridePendingTransition(R.anim.push_right_in,
								R.anim.push_right_out);

					}
				});

		// Create the AlertDialog
		builder.show();
		}

	
	}
	private static DatabaseHandler db;
	
	
	public static int getPosition(Activity mActivity,List<TutorialModel> tList,int position){
		db = new DatabaseHandler(mActivity);
		boolean isExistTutorial = db.isFavoriteTutorialExists(tList
				.get(position).getId());
		
		int index = 0;
		
		int currentIndex = 0;
		
		
		
		if (!isExistTutorial) {

			int j = 0;

			for (int i = 0; i <= position; i++) {

				boolean isExist = db.isFavoriteTutorialExists(tList
						.get(i).getId());

				if (isExist) {
					j++;
				}
			}

			currentIndex = position - j;

			
			int k = 0;

			for (int i = 0; i < tList.size(); i++) {

				boolean isExist = db.isFavoriteTutorialExists(tList
						.get(i).getId());

				if (isExist) {
					k++;
				}
			}
			
			int cur= (tList.size()-1) - k;
		
		if(currentIndex==cur){
			
			
			index=0;
			
		}else{
		
		index = currentIndex+1;
		}
		
		}
		
		return index;
		

	}
}
