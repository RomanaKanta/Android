package com.roundflat.musclecard.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.roundflat.musclecard.model.ModelClass;
import com.roundflat.musclecard.model.TutorialModel;

public class Utils {

	public static String loadJSONFromAsset(Context context, String fileName) {
		String json = null;
		try {

			InputStream is = context.getAssets().open(fileName);

			int size = is.available();

			byte[] buffer = new byte[size];

			is.read(buffer);

			is.close();

			json = new String(buffer, "UTF-8");

		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
		return json;

	}

	public static int randInt(int min, int max) {

		Random rand = new Random();
		int randomNum = rand.nextInt((max - min) + 1) + min;

		return randomNum;
	}

	public static List<Integer> getGameModel(int count, String type,
			List<Integer> gameNumberList) {

		int size = count;

		Log.d("size", "" + size);
		Log.d("gameNumberList", "" + gameNumberList);
		List<Integer> sortingListRandomOrder = getRandomArray(size - 1, size);

		Log.d("sortingListRandomOrder", "" + sortingListRandomOrder);

		List<Integer> sortingListOrder = new ArrayList<Integer>();

		/* if easy */
		if (type.equals(Constant.EASY)) {

			int middleIndexValue = sortingListRandomOrder.get(4);
			int index1 = sortingListRandomOrder.indexOf(4);
			int index2 = sortingListRandomOrder.indexOf(middleIndexValue);
			Collections.swap(sortingListRandomOrder, index1, index2);
		}

		sortingListOrder = getListSerials(size);

		for (int i = 0; i < gameNumberList.size(); i++) {
			sortingListOrder.set(sortingListRandomOrder.get(i),
					gameNumberList.get(i));
		}
		if (type.equals(Constant.EASY)) {

			sortingListOrder.set(gameNumberList.size(), -1);
			for (int j = gameNumberList.size() + 1; j < size; j++) {

				sortingListOrder.set(sortingListRandomOrder.get(j),
						gameNumberList.get(j - (gameNumberList.size() + 1)));
			}
		} else {

			for (int j = gameNumberList.size(); j < size; j++) {

				Log.d("j", "" + j);
				sortingListOrder.set(sortingListRandomOrder.get(j),
						gameNumberList.get(j - gameNumberList.size()));
			}
		}

		return sortingListOrder;
	}
	
	
	
	
	public static List<ModelClass>  getGameModelforMixedCard(int count, String type,
			List<Integer> gameList) {

		int size = count;
		
			

		List<Integer> sortingListRandomOrder = getRandomArray(size - 1, size);

		List<ModelClass>  myList=  new ArrayList<ModelClass>();

		/* if easy */
		if (type.equals(Constant.EASY)) {

			int middleIndexValue = sortingListRandomOrder.get(4);
			int index1 = sortingListRandomOrder.indexOf(4);
			int index2 = sortingListRandomOrder.indexOf(middleIndexValue);
			Collections.swap(sortingListRandomOrder, index1, index2);
		}

		myList = getMyListSerials(size);
		
		for(int i =0; i<gameList.size(); i++){
			myList.set(sortingListRandomOrder.get(i),
					new ModelClass(gameList.get(i),"f"));
		}
		
	
		Log.d("myList", "" + myList);
	
		if (type.equals(Constant.EASY)) {

			myList.set(gameList.size(), new ModelClass(gameList.size(), "f"));
			
			for (int j = gameList.size() + 1; j < size; j++) {

				myList.set(sortingListRandomOrder.get(j),
						new ModelClass(gameList.get(j - (gameList.size() + 1)),"r"));
			}
		
		} else {
			
			for (int j = gameList.size(); j < size; j++) {

				Log.d("j", "" + j);
				myList.set(sortingListRandomOrder.get(j),
						new ModelClass(gameList.get(j - gameList.size()),"r"));
			}

		}
		

		return myList;

	}

	
	public static List<Integer> setGameForLessCard(String type,
			List<TutorialModel> tutorialList) {
		List<Integer> indexList = new ArrayList<Integer>();

		for (int k = 0; k < tutorialList.size(); k++) {
			indexList.add(k);
			System.out.println(k);
		}
		
		Collections.shuffle(indexList);
		if (type.equals(Constant.EASY)) {
			if (indexList.size() * 2 > 5)

				indexList.add(-1);
		}

		for (int a = 0; a < tutorialList.size(); a++) {
			indexList.add(a);
			System.out.println(a);
		}

		System.out.println(indexList);
		return indexList;
	}
	
	
	public static List<ModelClass>  setGameForLessMixedCard(String type,
			List<TutorialModel> tutorialList) {

		List<Integer> indexList = new ArrayList<Integer>();
		int s = tutorialList.size() * 2;

		for (int k = 0; k < tutorialList.size(); k++) {
			indexList.add(k);
			System.out.println(k);
		}

		
		List<ModelClass> listWithTag = new ArrayList<ModelClass>();

		

		for (int i = 0; i < indexList.size(); i++) {
			listWithTag.add( new ModelClass(indexList.get(i), "f"));
		}
		
		Collections.shuffle(listWithTag);
		
		for (int i = indexList.size(); i < s; i++) {

			listWithTag.add(new ModelClass());
		}

		if (type.equals(Constant.EASY) && (indexList.size() * 2 > 5)) {

			listWithTag.add(new ModelClass());
			System.out.println(listWithTag.size());

			listWithTag.set(indexList.size(), new ModelClass(-1, "r"));

			for (int j = indexList.size() + 1; j < listWithTag.size(); j++) {

				listWithTag
						.set(j,
								new ModelClass(indexList.get(j
										- (indexList.size() + 1)), "r"));
			}
			System.out.println(listWithTag);

		} else {
			for (int j = indexList.size(); j < s; j++) {

				Log.d("j", "" + j);
				listWithTag
						.set(j,
								new ModelClass(indexList.get(j
										- indexList.size()), "r"));

			}
			System.out.println(listWithTag);
		}
		return listWithTag;
	}
	
	
	
public static List<ModelClass> getMyListSerials(int size) {

	List<ModelClass> list = new ArrayList<ModelClass>();

	for (int i = 0; i < size; i++) {

		list.add(new ModelClass());
	}
	return list;

}

	private static List<Integer> getListSerials(int size) {

		List<Integer> list = new ArrayList<Integer>();

		for (int i = 0; i < size; i++) {

			list.add(i);
		}
		return list;

	}

	private static List<Integer> getRandomArray(int size, int range) {

		List<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < size; i++) {

			int rand = Utils.randInt(0, size);
			if (!list.contains(rand)) {
				list.add(rand);
			}
			if (i == (size - 1) && list.size() != range) {

				i = 0;
			}
		}
		return list;

	}

	public static Drawable getImage(Context context, String name, int WIDTH,
			int HIGHT) {
		try {

			// Decode image size
//			 BitmapFactory.Options bitmapOption = new BitmapFactory.Options();
//			 bitmapOption.inJustDecodeBounds = true;
//			 AssetManager assets = context.getResources().getAssets();
//			 InputStream ims = assets.open("images/" + name);			 
//			 BitmapFactory.decodeStream(ims,null,bitmapOption);
//			
//			
//			 //The new size we want to scale to
//			 final int REQUIRED_WIDTH=WIDTH;
//			 final int REQUIRED_HIGHT=HIGHT;
//			 //Find the correct scale value. It should be the power of 2.
//			 int scale=1;
//			 while(bitmapOption.outWidth/scale/2>=REQUIRED_WIDTH &&
//			 bitmapOption.outHeight/scale/2>=REQUIRED_HIGHT)
//			 scale*=2;
//			
//			 //Decode with inSampleSize
//			 BitmapFactory.Options option = new BitmapFactory.Options();
//			 option.inSampleSize=scale;
//			 Bitmap bitmap = BitmapFactory.decodeStream(ims, null, option);
//			
//			 return new BitmapDrawable(context.getResources(), bitmap);

			InputStream ims = context.getAssets().open("images/" + name);
			Drawable drawable = Drawable.createFromStream(ims, null);

			Bitmap bm = ((BitmapDrawable) drawable).getBitmap();

			return drawable;

		} catch (IOException ex) {
			return null;
		}

	}

}
