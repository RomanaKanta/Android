package com.roundflat.musclecard.util;

import java.util.List;

import com.roundflat.musclecard.model.TutorialModel;

public class DataWrapper {

	private static List<TutorialModel> randomList;
	private static List<TutorialModel> imageList;
//	private static List<TutorialModel> normalList;

	public static void holdList(List<TutorialModel> tutorialList) {
		randomList = tutorialList;
	}

	public static List<TutorialModel> getList() {
		return randomList;
	}

	public static void holdImageList(List<TutorialModel> list) {
		imageList = list;
	}

	public static List<TutorialModel> getImageList() {
		return imageList;
	}

//	public static void holdNormalList(List<TutorialModel> tlist) {
//		normalList = tlist;
//	}
//
//	public List<TutorialModel> getNormalList() {
//		return normalList;
//	}

}