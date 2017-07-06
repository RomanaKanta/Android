package com.roundflat.musclecard.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import android.app.Activity;

import com.roundflat.musclecard.database.DatabaseHandler;
import com.roundflat.musclecard.model.TutorialModel;

public class ListRamdomClass {
	
	private Activity mActivity;
	private static DatabaseHandler db;
//	private boolean isRandom, IsFav;
	
//	ListRamdomClass(){
//		
//	}
	
public static List<TutorialModel> ListNormal(Activity activity, String rootTitle, boolean fav){
		
		db = new DatabaseHandler(activity);

		
		String queryForList = "SELECT DISTINCT * FROM "
				+ Constant.TABLE_TUTORIAL + " WHERE " + Constant.root_title
				+ " ='" + rootTitle + "' AND title != 'NIL'";

	

		if (rootTitle.equals("All")) {

			queryForList = "SELECT DISTINCT * FROM " + Constant.TABLE_TUTORIAL
					+ " WHERE title != 'NIL'";


		}else if ( !rootTitle.equals("All")) {


			queryForList = "SELECT DISTINCT * FROM " + Constant.TABLE_TUTORIAL
					+ " WHERE " + Constant.root_title + " ='" + rootTitle
					+ "' AND title != 'NIL'";

		}
		List<TutorialModel> tutorialList = new ArrayList<TutorialModel>();
		tutorialList = db.getAllTutorials(queryForList);
		
		return tutorialList;
		
	}
	
	public static List<TutorialModel> ListRamdom(Activity activity, String rootTitle, boolean fav){
		
		db = new DatabaseHandler(activity);

		
		String queryForList = "SELECT DISTINCT * FROM "
				+ Constant.TABLE_TUTORIAL + " WHERE " + Constant.root_title
				+ " ='" + rootTitle + "' AND title != 'NIL'";

	

		if (rootTitle.equals("All")) {

			queryForList = "SELECT DISTINCT * FROM " + Constant.TABLE_TUTORIAL
					+ " WHERE title != 'NIL'";


		}else if ( !rootTitle.equals("All")) {


			queryForList = "SELECT DISTINCT * FROM " + Constant.TABLE_TUTORIAL
					+ " WHERE " + Constant.root_title + " ='" + rootTitle
					+ "' AND title != 'NIL'";

		}
		List<TutorialModel> tutorialList = new ArrayList<TutorialModel>();
		tutorialList = db.getAllTutorials(queryForList);
		
		long seed = System.nanoTime();
		Collections.shuffle(tutorialList, new Random(seed));
		
		return tutorialList;
		
	}

}
