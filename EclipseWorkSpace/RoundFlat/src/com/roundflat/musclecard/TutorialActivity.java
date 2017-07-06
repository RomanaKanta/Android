package com.roundflat.musclecard;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.roundflat.musclecard.adapter.CircularViewPagerHandler;
import com.roundflat.musclecard.adapter.MemeCircularViewPagerAdapter;
import com.roundflat.musclecard.adapter.TitleAdapter;
import com.roundflat.musclecard.database.DatabaseHandler;
import com.roundflat.musclecard.model.TutorialModel;
import com.roundflat.musclecard.util.Constant;
import com.roundflat.musclecard.util.DataWrapper;
import com.roundflat.musclecard.util.ListRamdomClass;
import com.roundflat.musclecard.util.PreferenceUtils;
import com.roundflat.musclecard.util.RoundFlatUtil;
import com.roundflat.musclecard.view.ActionItem;

public class TutorialActivity extends FragmentActivity implements
		OnClickListener {

	private final String Tag = TutorialActivity.class.getName();

	RelativeLayout bottom_layout, fake_bottom_layout = null;
	TextView text = null;
	boolean option_On = true;
	boolean count_Op1, count_Op2,count_Op3,count_Op4= true;

	private List<TutorialModel> tutorialImageList = new ArrayList<TutorialModel>();
	private List<TutorialModel> tutorialList = new ArrayList<TutorialModel>();
	private int currentIndex = 0;
	// private TextView txtnumberOfTutorial;
	private ActionItem optItem;
	private static final int OptOne = 1;
	private static final int OptTwo = 2;
	private static final int Optthree = 3;
	private static final int OptFour = 4;
	private boolean isRandom, IsFav;
	private Button btnOpt1, btnOpt2, btnOpt3, btnOpt4;
	private FrameLayout framelayout;
	private ViewPager pagerFront;
	// private CircularViewPager circularViewPager;
	private DatabaseHandler db;
	private LinearLayout layoutBottomContent;
	private String all_favorite = "";
	ImageView ImageViewallfav;
	// RearCardAdapter dataPagerAdapter;
	private String rootTitle;
	private String routeType;
	boolean isRefresh = false;
	private ClickHandlerReceiver messageReceiver = new ClickHandlerReceiver();
	int prevListSize = 0;
	private ToggleButton btnMemorize;
	private TextView txtnumberOfTutorial;
	private MemeCircularViewPagerAdapter cardAdapter;

	public class ClickHandlerReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			Log.d("new action", "click");
			Log.d("intent.getAction()", intent.getAction());

			if (intent != null) {

				if (intent.getAction().equals("ListItemClick")) {

					int modifyPosition = intent.getExtras().getInt(
							Constant.position);
					currentIndex = modifyPosition;
					System.out.println("pos " + currentIndex);
					setCardItem(currentIndex);
				}

				if (intent.getAction().equals("ButtonClick")) {
					Log.d("new action", "ButtonClick");
					// String id = intent.getExtras().getString("item_id");
					int modifyPosition = intent.getExtras().getInt(
							Constant.position, 0);
					
					String itemid = intent.getExtras().getString("item_id", "");
					// currentIndex = modifyPosition;
					boolean isAdd = intent.getExtras()
							.getBoolean("item", false);
					// currentIndex = 0;
					// setViewFor1stButton();

					System.out.println("pos " + modifyPosition + " isAdd "
							+ isAdd);
					
//					if(isRandom){
//					refreshRandom( modifyPosition,  isAdd, itemid);
//					}else{
					
					refreshAdapter(modifyPosition, isAdd);
//					}

				}

				if (intent.getAction().equals("reset")) {
					Log.d("new action", "reset");
					refreshAdapterForReset();
				}
			}

		}

	};

	private void refreshRandom(int position, boolean isAdd,String itemid){


		// if ( (position & 1) != 0 ) {
		//
		// position = (position * 2) - position;
		// }
		
		TutorialModel temp = db.getDetail(itemid);
		
		List<TutorialModel> demotutorialIList = new ArrayList<TutorialModel>();
		List<TutorialModel> demotutorialmageIList = new ArrayList<TutorialModel>();
		demotutorialIList=	DataWrapper.getList();
		
		demotutorialIList.set(position, temp);
		
		if(IsFav){
		for (int i = 0; i < demotutorialIList.size(); i++) {

			TutorialModel mItem = demotutorialIList.get(i);

			
			if (!mItem.getIsFav().equals("1")) {

				demotutorialmageIList.add(mItem);

			}
			}
		tutorialImageList = demotutorialmageIList;

		}else{
			tutorialImageList=demotutorialIList;
		}
		
			Toast.makeText(getApplicationContext(), "image  " +tutorialImageList.size() + "  hold "  +demotutorialIList .size(), 4000).show();
			
		
			
			DataWrapper.holdList(demotutorialIList);
			DataWrapper.holdImageList(tutorialImageList);

		

		if (IsFav) {
			if (tutorialImageList.size() == 0) {

				RoundFlatUtil.setAllSelectScreen(this, false);
				return;
			} else {

				findViewById(R.id.imageview_all_selected).setVisibility(
						View.GONE);

				findViewById(R.id.textView_numberoftutorial).setVisibility(
						View.VISIBLE);
			}

		}

		// dataPagerAdapter = (new RearCardAdapter(this,
		// getFragmentManager(), tutorialImageList));
		// pagerBack.setAdapter(null);
		// pagerBack.setAdapter(dataPagerAdapter);

		pagerFront.setAdapter(null);
		cardAdapter = (new MemeCircularViewPagerAdapter(this,
				getSupportFragmentManager(), tutorialImageList));
		pagerFront.setAdapter(cardAdapter);

		int pos = PreferenceUtils.getIntPref(getApplicationContext(),
				Constant.PREF_NAME, Constant.ITEM_POSITION, 0);
		System.out.println("pos " + pos);
		if (pos == position) {
			currentIndex = position;
			System.out.println("pos==position " + currentIndex);
		} else {

			if (pos > position) {
				// currentIndex = pos;
				if (isAdd) {
					currentIndex = currentIndex + 1;
				} else {
					currentIndex = currentIndex - 1;
				}
				System.out.println("pos > position " + currentIndex);
			}

		}

		System.out.println("no condition " + currentIndex);
		pagerFront.setCurrentItem(currentIndex + 1, true);

		boolean isExistTutorial = db.isFavoriteTutorialExists(tutorialImageList
				.get(currentIndex).getId());
		if (isExistTutorial) {

			btnMemorize.setChecked(true);

		} else {

			btnMemorize.setChecked(false);
		}
		txtnumberOfTutorial.setText((currentIndex + 1) + "/"
				+ tutorialImageList.size());
	
	}
	
	private void refreshAdapter(int position, boolean isAdd) {

		// if ( (position & 1) != 0 ) {
		//
		// position = (position * 2) - position;
		// }

		String queryForImage = "SELECT DISTINCT * FROM "
				+ Constant.TABLE_TUTORIAL + " WHERE " + Constant.root_title
				+ " ='" + rootTitle + "' AND title != 'NIL'";

		String queryForList = queryForImage;

		if (rootTitle.equals("All")) {

			queryForImage = "SELECT DISTINCT * FROM " + Constant.TABLE_TUTORIAL
					+ " WHERE title != 'NIL'";

			queryForList = queryForImage;

		}

		if (IsFav && rootTitle.equals("All")) {

			queryForImage = "SELECT DISTINCT * FROM " + Constant.TABLE_TUTORIAL
					+ " WHERE title != 'NIL'" + " AND " + Constant.isFav
					+ "!= 1";

			queryForList = "SELECT DISTINCT * FROM " + Constant.TABLE_TUTORIAL
					+ " WHERE title != 'NIL'";

		} else if (IsFav && !rootTitle.equals("All")) {

			queryForImage = "SELECT DISTINCT * FROM " + Constant.TABLE_TUTORIAL
					+ " WHERE " + Constant.root_title + " ='" + rootTitle
					+ "' AND title != 'NIL'  AND " + Constant.isFav + "!= 1";

			queryForList = "SELECT DISTINCT * FROM " + Constant.TABLE_TUTORIAL
					+ " WHERE " + Constant.root_title + " ='" + rootTitle
					+ "' AND title != 'NIL'";

		}

		tutorialImageList = db.getAllTutorials(queryForImage);
		tutorialList = db.getAllTutorials(queryForList);
	
//		List<TutorialModel> demotutorialIList = new ArrayList<TutorialModel>();
//		
//		demotutorialIList= db.getAllTutorials(queryForList);
//
//			tutorialImageList.clear();
//
//		List<TutorialModel> ttutorialList = 	DataWrapper.getList();
//		List<TutorialModel> tutorialIList = new ArrayList<TutorialModel>();
//		List<String> listIndex = new ArrayList<String>();
//
//			for (int i = 0; i < ttutorialList.size(); i++) {
//
//				TutorialModel mItem = ttutorialList.get(i);
//				
//				listIndex.add(mItem.getId());
//
//			}
//
//			for(int i=0;i<tutorialList.size();i++){
//				
//				String j = listIndex.get(i);
//				TutorialModel mItem = ttutorialList.get(i);
//				if(j.equals(mItem.getId())){
//			
//				
//				tutorialImageList.add(listIndex.indexOf(j), mItem);
//				}
//				
//				
//			}
//			
//			Toast.makeText(getApplicationContext(), "image  " +tutorialImageList.size() + "  hold "  +tutorialIList .size(), 4000).show();
			
			DataWrapper.holdList(tutorialList);
			DataWrapper.holdImageList(tutorialImageList);

		

		if (IsFav) {
			if (tutorialImageList.size() == 0) {

				RoundFlatUtil.setAllSelectScreen(this, false);
				
				option_On = false;
				
				
				return;
			} else {

				option_On = true;
				findViewById(R.id.imageview_all_selected).setVisibility(
						View.GONE);

				findViewById(R.id.textView_numberoftutorial).setVisibility(
						View.VISIBLE);
			}

		}

		// dataPagerAdapter = (new RearCardAdapter(this,
		// getFragmentManager(), tutorialImageList));
		// pagerBack.setAdapter(null);
		// pagerBack.setAdapter(dataPagerAdapter);

		pagerFront.setAdapter(null);
		cardAdapter = (new MemeCircularViewPagerAdapter(this,
				getSupportFragmentManager(), tutorialImageList));
		pagerFront.setAdapter(cardAdapter);

		int pos = PreferenceUtils.getIntPref(getApplicationContext(),
				Constant.PREF_NAME, Constant.ITEM_POSITION, 0);
		System.out.println("pos " + pos);
		if (pos == position) {
			currentIndex = position;
			System.out.println("pos==position " + currentIndex);
		} else {

			if (pos > position) {
				// currentIndex = pos;
				if (isAdd) {
					currentIndex = currentIndex + 1;
				} else {
					currentIndex = currentIndex - 1;
				}
				System.out.println("pos > position " + currentIndex);
			}

		}

		System.out.println("no condition " + currentIndex);
		pagerFront.setCurrentItem(currentIndex + 1, true);

		boolean isExistTutorial = db.isFavoriteTutorialExists(tutorialImageList
				.get(currentIndex).getId());
		if (isExistTutorial) {

			btnMemorize.setChecked(true);

		} else {

			btnMemorize.setChecked(false);
		}
		txtnumberOfTutorial.setText((currentIndex + 1) + "/"
				+ tutorialImageList.size());
	}

	private void refreshAdapterForReset() {

		 String queryForImage = "SELECT DISTINCT * FROM "
		 + Constant.TABLE_TUTORIAL + " WHERE " + Constant.root_title
		 + " ='" + rootTitle + "' AND title != 'NIL'";
		
		 String queryForList = queryForImage;
		
		 if (rootTitle.equals("All")) {
		
		 queryForImage = "SELECT DISTINCT * FROM " + Constant.TABLE_TUTORIAL
		 + " WHERE title != 'NIL'";
		
		 queryForList = queryForImage;
		
		 }
		
		 if (IsFav && rootTitle.equals("All")) {
		
		 queryForImage = "SELECT DISTINCT * FROM " + Constant.TABLE_TUTORIAL
		 + " WHERE title != 'NIL'" + " AND " + Constant.isFav
		 + "!= 1";
		
		 queryForList = "SELECT DISTINCT * FROM " + Constant.TABLE_TUTORIAL
		 + " WHERE title != 'NIL'";
		
		 } else if (IsFav && !rootTitle.equals("All")) {
		
		 queryForImage = "SELECT DISTINCT * FROM " + Constant.TABLE_TUTORIAL
		 + " WHERE " + Constant.root_title + " ='" + rootTitle
		 + "' AND title != 'NIL'  AND " + Constant.isFav + "!= 1";
		
		 queryForList = "SELECT DISTINCT * FROM " + Constant.TABLE_TUTORIAL
		 + " WHERE " + Constant.root_title + " ='" + rootTitle
		 + "' AND title != 'NIL'";
		
		 }
		
		 tutorialImageList = db.getAllTutorials(queryForImage);
		 tutorialList = db.getAllTutorials(queryForList);

		if (IsFav) {
			if (tutorialImageList.size() == 0) {

				RoundFlatUtil.setAllSelectScreen(this, false);
				
				option_On = false;
				return;
			} else {
				
				option_On = true;

				findViewById(R.id.imageview_all_selected).setVisibility(
						View.GONE);

				findViewById(R.id.textView_numberoftutorial).setVisibility(
						View.VISIBLE);
			}

		}

		pagerFront.setAdapter(null);
		cardAdapter = (new MemeCircularViewPagerAdapter(this,
				getSupportFragmentManager(), tutorialImageList));
		pagerFront.setAdapter(cardAdapter);

		int pos = PreferenceUtils.getIntPref(getApplicationContext(),
				Constant.PREF_NAME, Constant.ITEM_POSITION, 0);
		pagerFront.setCurrentItem(pos + 1, true);
		txtnumberOfTutorial.setText((pos + 1) + "/" + tutorialImageList.size());
	}

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_tutorial);

		bottom_layout = (RelativeLayout) findViewById(R.id.layout_bottom);
		bottom_layout.setOnClickListener(this);
		text = (TextView) findViewById(R.id.tv_bottom_text);
		fake_bottom_layout = (RelativeLayout) findViewById(R.id.fake_layout_bottom);
		fake_bottom_layout.setOnClickListener(this);

		rootTitle = getIntent().getExtras().getString(Constant.root_title);

		IntentFilter i = new IntentFilter();
		i.addAction("ListItemClick");
		i.addAction("ButtonClick");
		registerReceiver(messageReceiver, i);

		all_favorite = getIntent().getExtras().getString(Constant.ALL_SELECT);

		db = new DatabaseHandler(this);

		IsFav = PreferenceUtils.getBoolPref(getApplicationContext(),
				Constant.PREF_NAME, Constant.PREF_FAV, false);
		isRandom = PreferenceUtils.getBoolPref(getApplicationContext(),
				Constant.PREF_NAME, Constant.PREF_RANDOM, false);

		framelayout = (FrameLayout) this.findViewById(R.id.framecontent);

		pagerFront = (ViewPager) this.findViewById(R.id.pager);
		// pagerFront.setGestureDetector(motionDetector);
		pagerFront.setPageMargin(20);
		pagerFront.setPageMarginDrawable(R.color.white);

		// pagerBack = (MyViewPager) this.findViewById(R.id.pager_back);
		// pagerBack.setGestureDetector(motionDetector);
		// pagerBack.setPageMargin(20);
		// pagerBack.setPageMarginDrawable(R.color.white);

		layoutBottomContent = (LinearLayout) this
				.findViewById(R.id.option_bottom_content);

		btnOpt1 = (Button) findViewById(R.id.button_opt_1);
		btnOpt1.setOnClickListener(this);
		btnOpt2 = (Button) findViewById(R.id.button_opt_2);
		btnOpt2.setOnClickListener(this);
		btnOpt3 = (Button) findViewById(R.id.button_opt_3);
		btnOpt3.setOnClickListener(this);
		btnOpt4 = (Button) findViewById(R.id.button_opt_4);
		btnOpt4.setOnClickListener(this);

		this.findViewById(R.id.back_content).setOnClickListener(this);

		btnMemorize = (ToggleButton) findViewById(R.id.ToggleButton_memorize);
		btnMemorize.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean check) {

				if (check) {
					Log.d("check ", ""
							+ tutorialImageList.get(currentIndex).getId());
					db.updateFavoriteTutorial(
							tutorialImageList.get(currentIndex).getId(), "1");

					if (routeType.equals(Constant.ROUTE_VIA_1ST_BUTTON)) {
						List<TutorialModel> favList = new ArrayList<TutorialModel>();
						String query;
						if (rootTitle.equals("All") && IsFav) {

							query = "SELECT DISTINCT * FROM "
									+ Constant.TABLE_TUTORIAL
									+ " WHERE title != 'NIL'" + " AND "
									+ Constant.isFav + "== 1";

						} else {
							query = "SELECT DISTINCT * FROM "
									+ Constant.TABLE_TUTORIAL + " WHERE "
									+ Constant.root_title + " ='" + rootTitle
									+ "' AND title != 'NIL'  AND "
									+ Constant.isFav + "== 1";
						}

						favList = db.getAllTutorials(query);
						if (IsFav) {
							if (favList.size() == tutorialList.size()) {

								RoundFlatUtil.setAllSelectScreen(
										TutorialActivity.this, false);
								
								option_On = false;
							}else{
								option_On = true;
							}

						}

					}

				} else {
					Log.d("uncheck ", ""
							+ tutorialImageList.get(currentIndex).getId());
					db.updateFavoriteTutorial(
							tutorialImageList.get(currentIndex).getId(), "0");

				}
			}
		});
		txtnumberOfTutorial = (TextView) this
				.findViewById(R.id.textView_numberoftutorial);

		if (all_favorite.equals("ALL_favorite")) {

			RoundFlatUtil.setAllSelectScreen(this, true);
		}

		if (all_favorite.equals("not_ALL_favorite")) {

			if (getIntent().hasExtra(Constant.ROUTE_TYPE)) {

				routeType = getIntent().getExtras().getString(
						Constant.ROUTE_TYPE);

				if (routeType.equals(Constant.ROUTE_VIA_1ST_BUTTON)) {

					if (isRandom) {

						if (isRandom && IsFav) {
							setViewFor1stButtonFavRandom();
						}else{
						setViewFor1stButtonRandom();
						}
						
					} else{
						setViewFor1stButton();
					}

					this.findViewById(R.id.Button_list)
							.setOnClickListener(this);

				} else if (routeType.equals(Constant.ROUTE_VIA_3RD_BUTTON)) {

					setViewFor3rdButton();
					this.findViewById(R.id.textView_search_home).setVisibility(
							View.VISIBLE);
					this.findViewById(R.id.textView_search_home)
							.setOnClickListener(this);
				}

			}

			// if (isRandom) {
			// if (tutorialImageList.size() != 1) {
			//
			// long seed = System.nanoTime();
			// Collections.shuffle(tutorialImageList, new Random(seed));
			// }
			//
			// }

		}
	}

	private ViewPager.OnPageChangeListener createOnPageChangeListener() {
		return new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(final int position,
					final float positionOffset, final int positionOffsetPixels) {
			}

			@Override
			public void onPageSelected(int position) {
				if (position < 0 || position == tutorialImageList.size()) {

					position = 0;
				}

				if (currentIndex == position) {

					return;
				}

				for (int i = 0; i < pagerFront.getChildCount(); i++) {

					View view = pagerFront.getChildAt(i);
					if (view != null) {
						Log.d("view", " not null " + i);
						view.findViewById(R.id.lp_front_content).setVisibility(
								View.VISIBLE);
						view.findViewById(R.id.lp_back_content).setVisibility(
								View.GONE);
					} else {
						Log.d("view", "  null " + i);
					}
				}

				Log.d("set index", currentIndex + " to " + position);

				// View view = pagerFront.getChildAt(currentIndex);
				//
				// if(view != null){
				//
				// View cardFace = view.findViewById(R.id.lp_front_content);
				// View cardBack = view.findViewById(R.id.lp_back_content);
				//
				// cardFace.setVisibility(View.VISIBLE);
				// cardBack.setVisibility(View.GONE);
				// }
				//
				// View view1 = pagerFront.getChildAt(position);
				//
				// if(view1 != null){
				//
				// View cardFace = view1.findViewById(R.id.lp_front_content);
				// View cardBack = view1.findViewById(R.id.lp_back_content);
				//
				// cardFace.setVisibility(View.VISIBLE);
				// cardBack.setVisibility(View.GONE);
				// }
				//
				//

				setImagePage(position, currentIndex);

			}

			@Override
			public void onPageScrollStateChanged(final int state) {

			}
		};
	}

	public void setImagePage(int position, int current) {
		if (IsFav) {

			if (current < tutorialImageList.size()) {

				boolean isPrevExist = db
						.isFavoriteTutorialExists(tutorialImageList
								.get(current).getId());
				if (isPrevExist) {
					// tutorialImageList.remove(current);
					int removedPosition = cardAdapter.removeView(pagerFront,
							current);
					Log.d(" removedPosition ", "" + removedPosition);

					if (current < position) {

						position = current;
						Log.d("current < position ", "" + position);

					} else {
						if ((current + 1) <= position) {
							position = position - 1;
							Log.d("(current+1) <= position ", "" + position);
						}
						Log.d(" position ", "" + position);
					}
					cardAdapter.notifyDataSetChanged();
					// dataPagerAdapter.notifyDataSetChanged();
					pagerFront.setCurrentItem(position + 1, true);

				}
			}

		}

		currentIndex = position;
		boolean isExistTutorial = db.isFavoriteTutorialExists(tutorialImageList
				.get(currentIndex).getId());
		if (isExistTutorial) {

			btnMemorize.setChecked(true);

		} else {

			btnMemorize.setChecked(false);
		}

		txtnumberOfTutorial.setText((currentIndex + 1) + "/"
				+ tutorialImageList.size());

	}

	private boolean isExistonFav(int pos) {

		return db.isFavoriteTutorialExists(tutorialImageList.get(pos).getId());
	}

	private void setCardFav(boolean fav) {

		btnMemorize.setChecked(fav);
	}

	private void setCardItem(int index) {

		// Toast.makeText(getApplicationContext(), ""+index, 200).show();
		if (index != INVALID_INDEX) {

			if ((index) == tutorialImageList.size()) {
				pagerFront.setCurrentItem(index, true);
			} else {

				pagerFront.setCurrentItem(index + 1, true);
			}

		}

		boolean isExistTutorial = db.isFavoriteTutorialExists(tutorialImageList
				.get(currentIndex).getId());
		if (isExistTutorial) {

			btnMemorize.setChecked(true);

		} else {

			btnMemorize.setChecked(false);
		}
		txtnumberOfTutorial.setText((currentIndex + 1) + "/"
				+ tutorialImageList.size());

	}

	public static final int INVALID_INDEX = -1;

	private void setRefreshValues() {

		if (IsFav) {
			if (tutorialImageList.size() == 0) {

				RoundFlatUtil.setAllSelectScreen(this, false);
				return;
			} else {

				findViewById(R.id.imageview_all_selected).setVisibility(
						View.GONE);

				findViewById(R.id.textView_numberoftutorial).setVisibility(
						View.VISIBLE);
			}

		}

		txtnumberOfTutorial.setText((currentIndex + 1) + "/"
				+ tutorialImageList.size());

		setCardFav(isExistonFav(currentIndex));

		pagerFront.setAdapter(null);
		cardAdapter = (new MemeCircularViewPagerAdapter(this,
				getSupportFragmentManager(), tutorialImageList));
		pagerFront.setAdapter(cardAdapter);
		final CircularViewPagerHandler circularViewPagerHandler = new CircularViewPagerHandler(
				pagerFront);
		circularViewPagerHandler
				.setOnPageChangeListener(createOnPageChangeListener());
		pagerFront.addOnPageChangeListener(circularViewPagerHandler);

	}

	public void setViewFor1stButtonRandom() {
		tutorialImageList = tutorialList = ListRamdomClass.ListRamdom(this,
				rootTitle, IsFav);

		DataWrapper.holdList(tutorialList);
		DataWrapper.holdImageList(tutorialImageList);
		Log.d("tutorialImageList", "" + tutorialImageList.size());
		Log.d("tutorialList", "" + tutorialList.size());

		setRefreshValues();
	}

	public void setViewFor1stButtonFavRandom() {
		tutorialList = ListRamdomClass.ListRamdom(this, rootTitle, IsFav);

		for (int i = 0; i < tutorialList.size(); i++) {

			TutorialModel mItem = tutorialList.get(i);

			if (!mItem.getIsFav().equals("1")) {

				tutorialImageList.add(mItem);

			}

		}

		DataWrapper.holdList(tutorialList);
		DataWrapper.holdImageList(tutorialImageList);

		Log.d("tutorialImageList", "" + tutorialImageList.size());
		Log.d("tutorialList", "" + tutorialList.size());

		setRefreshValues();
	}

	public void setViewFor1stButton() {

		String queryForImage = "SELECT DISTINCT * FROM "
				+ Constant.TABLE_TUTORIAL + " WHERE " + Constant.root_title
				+ " ='" + rootTitle + "' AND title != 'NIL'";

		String queryForList = queryForImage;

		if (rootTitle.equals("All")) {

			queryForImage = "SELECT DISTINCT * FROM " + Constant.TABLE_TUTORIAL
					+ " WHERE title != 'NIL'";

			queryForList = queryForImage;

		}

		if (IsFav && rootTitle.equals("All")) {

			queryForImage = "SELECT DISTINCT * FROM " + Constant.TABLE_TUTORIAL
					+ " WHERE title != 'NIL'" + " AND " + Constant.isFav
					+ "!= 1";

			queryForList = "SELECT DISTINCT * FROM " + Constant.TABLE_TUTORIAL
					+ " WHERE title != 'NIL'";

		} else if (IsFav && !rootTitle.equals("All")) {

			queryForImage = "SELECT DISTINCT * FROM " + Constant.TABLE_TUTORIAL
					+ " WHERE " + Constant.root_title + " ='" + rootTitle
					+ "' AND title != 'NIL'  AND " + Constant.isFav + "!= 1";

			queryForList = "SELECT DISTINCT * FROM " + Constant.TABLE_TUTORIAL
					+ " WHERE " + Constant.root_title + " ='" + rootTitle
					+ "' AND title != 'NIL'";

		}

		tutorialImageList = db.getAllTutorials(queryForImage);
		tutorialList = db.getAllTutorials(queryForList);

		DataWrapper.holdList(tutorialList);
		DataWrapper.holdImageList(tutorialImageList);

		Log.d("tutorialImageList", "" + tutorialImageList.size());
		Log.d("tutorialList", "" + tutorialList.size());

		setRefreshValues();

	}

	public void setViewFor3rdButton() {

		TextView backTitle = (TextView) findViewById(R.id.TextView_back);
		backTitle.setText(R.string.go_back);
		
//		backTitle.setText(getIntent().getExtras()
//				.getString(Constant.back_title).toString());
//		
//		backTitle.setVisibility(View.GONE);

		String tutorialID = getIntent().getExtras().getString(Constant.id);
		this.findViewById(R.id.Button_list).setVisibility(View.GONE);

		String query = "SELECT DISTINCT * FROM " + Constant.TABLE_TUTORIAL
				+ " WHERE " + Constant.id + " ='" + tutorialID + "'";

		tutorialImageList = db.getAllTutorials(query);

		setRefreshValues();

	}

	@Override
	public void onClick(View view) {

//			if (IsFav && tutorialImageList.size() == 0) {
//
//				RoundFlatUtil.setAllSelectScreen(this, false);
//				return;
//			} else {
//
//				findViewById(R.id.imageview_all_selected).setVisibility(
//						View.GONE);
//
//				findViewById(R.id.textView_numberoftutorial).setVisibility(
//						View.VISIBLE);
//		
//			}
	
		
		int id = view.getId();
		if (id == R.id.button_opt_1) {
//			 optItem = new ActionItem(OptOne, tutorialImageList
//			 .get(currentIndex).getOption_1());
			if(option_On){
			text.setText(tutorialImageList.get(currentIndex).getOption_1());

			if ( bottom_layout.getVisibility() == View.GONE) {
				bottom_layout.setVisibility(View.VISIBLE);
				fake_bottom_layout.setVisibility(View.VISIBLE);
				
			}
			

			btnOpt1.setTextColor(Color.RED);
			btnOpt2.setTextColor(getResources().getColor(
					R.color.actionbar_text_color));
			btnOpt3.setTextColor(getResources().getColor(
					R.color.actionbar_text_color));
			btnOpt4.setTextColor(getResources().getColor(
					R.color.actionbar_text_color));
			
		}

		} else if (id == R.id.button_opt_2) {
			// optItem = new ActionItem(OptTwo, tutorialImageList
			// .get(currentIndex).getOption_2());

			if(option_On){
			text.setText(tutorialImageList.get(currentIndex).getOption_2());

			if ( bottom_layout.getVisibility() == View.GONE) {
				bottom_layout.setVisibility(View.VISIBLE);
				fake_bottom_layout.setVisibility(View.VISIBLE);
				
			}

			btnOpt2.setTextColor(Color.RED);
			btnOpt1.setTextColor(getResources().getColor(
					R.color.actionbar_text_color));
			btnOpt3.setTextColor(getResources().getColor(
					R.color.actionbar_text_color));
			btnOpt4.setTextColor(getResources().getColor(
					R.color.actionbar_text_color));
		}

		} else if (id == R.id.button_opt_3) {
			// optItem = new ActionItem(Optthree, tutorialImageList.get(
			// currentIndex).getOption_3());
			
			if(option_On){
			text.setText(tutorialImageList.get(currentIndex).getOption_3());

			if ( bottom_layout.getVisibility() == View.GONE) {
				bottom_layout.setVisibility(View.VISIBLE);
				fake_bottom_layout.setVisibility(View.VISIBLE);
				
			}

			btnOpt3.setTextColor(Color.RED);
			btnOpt2.setTextColor(getResources().getColor(
					R.color.actionbar_text_color));
			btnOpt1.setTextColor(getResources().getColor(
					R.color.actionbar_text_color));
			btnOpt4.setTextColor(getResources().getColor(
					R.color.actionbar_text_color));
		}

		} else if (id == R.id.button_opt_4) {

			// optItem = new ActionItem(OptFour, tutorialImageList.get(
			// currentIndex).getOption_4());
			
			if(option_On){

			text.setText(tutorialImageList.get(currentIndex).getOption_4());

			if (bottom_layout.getVisibility() == View.GONE) {
				bottom_layout.setVisibility(View.VISIBLE);
				fake_bottom_layout.setVisibility(View.VISIBLE);
			
			}

			btnOpt4.setTextColor(Color.RED);
			btnOpt2.setTextColor(getResources().getColor(
					R.color.actionbar_text_color));
			btnOpt3.setTextColor(getResources().getColor(
					R.color.actionbar_text_color));
			btnOpt1.setTextColor(getResources().getColor(
					R.color.actionbar_text_color));
			}

		} else if (id == R.id.layout_bottom) {
			bottom_layout.setVisibility(View.VISIBLE);

		} else if (id == R.id.fake_layout_bottom) {
			if (bottom_layout.getVisibility() == View.VISIBLE) {
				dismiss();
			}
			return;

		} else if (id == R.id.back_content) {

			finish();
			overridePendingTransition(R.anim.push_right_in,
					R.anim.push_right_out);
			return;

		} else if (id == R.id.textView_search_home) {

			Intent intent = new Intent(TutorialActivity.this,
					TutorialSearchRootTitleActivity.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.push_right_in,
					R.anim.push_right_out);
			return;

		} else if (id == R.id.Button_list) {
			int cPosition = 0;
			if (tutorialImageList.size() != 0) {

				for (int i = 0; i < tutorialList.size(); i++) {

					if (tutorialList
							.get(i)
							.getId()
							.equals(tutorialImageList.get(currentIndex).getId())) {
						cPosition = i;
					}
				}

				System.out.println("item " + cPosition);
			}

			PreferenceUtils.saveIntPref(getApplicationContext(),
					Constant.PREF_NAME, Constant.ITEM_POSITION, cPosition);
			Intent intent = new Intent(TutorialActivity.this,
					RightMenuActivity.class);
			// if(isRandom || (isRandom&& IsFav)){
			// DataWrapper.holdRandomList(tutorialList);
			// }

			intent.putExtra(Constant.root_title, rootTitle);
			startActivity(intent);
			// overridePendingTransition(R.anim.push_left_in,
			// R.anim.push_left_out);
			overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
			return;

		}


//		 QuickAction mQuickAction = new QuickAction(TutorialActivity.this);
		//
		// mQuickAction.addActionItem(optItem);
		// mQuickAction.setOnDismissListener(new QuickAction.OnDismissListener()
		// {
		// @Override
		// public void onDismiss() {
		//
		// btnOpt1.setTextColor(getResources().getColor(
		// R.color.actionbar_text_color));
		// btnOpt2.setTextColor(getResources().getColor(
		// R.color.actionbar_text_color));
		// btnOpt3.setTextColor(getResources().getColor(
		// R.color.actionbar_text_color));
		// btnOpt4.setTextColor(getResources().getColor(
		// R.color.actionbar_text_color));
		// }
		// });
		//
//		 mQuickAction.show(layoutBottomContent);
//		 mQuickAction.setAnimStyle(QuickAction.ANIM_GROW_FROM_CENTER);
	}

	void dismiss() {
		bottom_layout.setVisibility(View.GONE);
		fake_bottom_layout.setVisibility(View.GONE);
		btnOpt1.setTextColor(getResources().getColor(
				R.color.actionbar_text_color));
		btnOpt2.setTextColor(getResources().getColor(
				R.color.actionbar_text_color));
		btnOpt3.setTextColor(getResources().getColor(
				R.color.actionbar_text_color));
		btnOpt4.setTextColor(getResources().getColor(
				R.color.actionbar_text_color));
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		PreferenceUtils.saveIntPref(getApplicationContext(),
				Constant.PREF_NAME, Constant.ITEM_POSITION, 0);
		db.close();
		if (messageReceiver != null) {
			try {
				unregisterReceiver(messageReceiver);
				messageReceiver = null;
			} catch (Exception e) {
				// do nothing
			}
		}

	}

	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}

}
