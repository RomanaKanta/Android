package com.smartmux.androidapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.smartmux.androidapp.adapter.TutorialDataAdapter;
import com.smartmux.androidapp.adapter.TutorialImageAdapter;
import com.smartmux.androidapp.adapter.TitleAdapter;
import com.smartmux.androidapp.animition.FlipAnimation;
import com.smartmux.androidapp.database.DatabaseHandler;
import com.smartmux.androidapp.model.TutorialModel;
import com.smartmux.androidapp.util.Constant;
import com.smartmux.androidapp.util.PreferenceUtils;
import com.smartmux.androidapp.util.Utils;
import com.smartmux.androidapp.view.ActionItem;
import com.smartmux.androidapp.view.MyViewPager;
import com.smartmux.androidapp.view.QuickAction;

public class TutorialActivity extends FragmentActivity implements
		OnClickListener, GestureDetector.OnGestureListener,OnItemClickListener {

	private final String Tag = TutorialActivity.class.getName();
	private GestureDetectorCompat motionDetector;
	private List<TutorialModel> tutorialList = new ArrayList<TutorialModel>();
    private int currentIndex = 0;
	private TextView  txtnumberOfTutorial;
	private ActionItem optItem;
	private static final int OptOne = 1;
	private static final int OptTwo = 2;
	private static final int Optthree = 3;
	private static final int OptFour = 4;
	private boolean isRandom, IsFav;
	private SlidingMenu menuRight;
	private Button btnOpt1,btnOpt2,btnOpt3,btnOpt4;
	private FrameLayout framelayout;
	private MyViewPager pagerFront,pagerBack;
	private ToggleButton btnMemorize;
	private DatabaseHandler db;
	private int actionBarHeight = 40;
	private LinearLayout layoutBottomContent;

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tutorial);

		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		getActionBar().setCustomView(R.layout.tutorial_actionbar);
		
		TypedValue tv = new TypedValue();
		if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
		{
		    actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
		}
		 layoutBottomContent = (LinearLayout)findViewById(R.id.option_bottom_content);
		LayoutParams params = layoutBottomContent.getLayoutParams();
		params.height = actionBarHeight;
		params.width = LayoutParams.MATCH_PARENT;

		db = new DatabaseHandler(this);
		
		IsFav = PreferenceUtils.getBoolPref(getApplicationContext(),
				Constant.PREF_NAME, Constant.PREF_FAV, false);
		isRandom = PreferenceUtils.getBoolPref(getApplicationContext(),
				Constant.PREF_NAME, Constant.PREF_RANDOM, false);
		
		framelayout = (FrameLayout) this.findViewById(R.id.framecontent);
		pagerFront = (MyViewPager) this.findViewById(R.id.pager_front);
		pagerBack = (MyViewPager) this.findViewById(R.id.pager_back);
		
		motionDetector = new GestureDetectorCompat(this, this);

		
		this.findViewById(R.id.back_content).setOnClickListener(this);
		
		if (getIntent().hasExtra(Constant.ROUTE_TYPE)) {

			String routeType = getIntent().getExtras().getString(
					Constant.ROUTE_TYPE);

			if (routeType.equals(Constant.ROUTE_VIA_1ST_BUTTON)) {
				
				
				String rootTitle = getIntent().getExtras().getString(Constant.root_title);
				TextView txtTitle = (TextView) findViewById(R.id.textView_numberoftutorial);
				txtTitle.setText(rootTitle);
				String query = "SELECT DISTINCT * FROM "
						+ Constant.TABLE_TUTORIAL + " WHERE " + Constant.root_title
						+ " ='" + rootTitle + "' AND title != 'NIL'";
				
				if(rootTitle.equals("All")){
					
					query = "SELECT DISTINCT * FROM "
							+ Constant.TABLE_TUTORIAL+" WHERE title != 'NIL'";
					
				}
				
				if(IsFav && rootTitle.equals("All")){
					
					
					query = "SELECT DISTINCT * FROM "
							+ Constant.TABLE_TUTORIAL+" WHERE title != 'NIL'"+" AND "+Constant.isFav +"!= 1";
				}else if(IsFav && !rootTitle.equals("All")){
					
					
					query = "SELECT DISTINCT * FROM "
							+ Constant.TABLE_TUTORIAL + " WHERE " + Constant.root_title
							+ " ='" + rootTitle + "' AND title != 'NIL'  AND "+Constant.isFav +"!= 1";
				}
				
				tutorialList = db.getAllTutorials(query);
				
//				query = "SELECT DISTINCT "+Constant.title+" FROM "
//						+ Constant.TABLE_TUTORIAL + " WHERE " + Constant.root_title
//						+ " ='" + rootTitle + "' AND title != 'NIL'";
//				
				
				menuRight = new SlidingMenu(this);
				menuRight.setMode(SlidingMenu.RIGHT);
				menuRight.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
				menuRight.setShadowWidthRes(R.dimen.shadow_width);
				menuRight.setShadowDrawable(R.drawable.shadow);
				menuRight.setBehindOffsetRes(R.dimen.slidingmenu_offset);
				menuRight.setFadeDegree(0.35f);
				menuRight.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
				menuRight.setMenu(R.layout.tutorial_menu);

				this.findViewById(R.id.Button_list)
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {

								menuRight.toggle();

							}
						});
				
				
				
				ListView listviewTitle = (ListView) this
						.findViewById(R.id.listView_tutorial_title);

				listviewTitle.setAdapter(new TitleAdapter(getApplicationContext(),
						tutorialList));

				listviewTitle.setOnItemClickListener(this);

			} else if (routeType.equals(Constant.ROUTE_VIA_3RD_BUTTON)) {

				String tutorialID = getIntent().getExtras().getString(
						Constant.id);
				this.findViewById(R.id.Button_list).setVisibility(View.GONE);
				

				String query = "SELECT DISTINCT * FROM "
						+ Constant.TABLE_TUTORIAL + " WHERE " + Constant.id
						+ " ='" + tutorialID + "'";

				tutorialList = db.getAllTutorials(query);
				
				Log.d("tutorialList", " "+tutorialList);
			}

		}

		if (isRandom) {
			if (tutorialList.size() != 1) {

				//currentIndex = Utils.randInt(0, tutorialList.size() - 1);
				long seed = System.nanoTime();
				Collections.shuffle(tutorialList, new Random(seed));
			}

		}
		

		 btnOpt1 = (Button) findViewById(R.id.button_opt_1);
		 btnOpt1.setOnClickListener(this);
		 btnOpt2 = (Button) findViewById(R.id.button_opt_2);
		 btnOpt2.setOnClickListener(this);
		 btnOpt3 = (Button) findViewById(R.id.button_opt_3);
		 btnOpt3.setOnClickListener(this);
		 btnOpt4 = (Button) findViewById(R.id.button_opt_4);
		 btnOpt4.setOnClickListener(this);
		 
		 btnMemorize  = (ToggleButton) findViewById(R.id.ToggleButton_memorize);
		 btnMemorize.setOnClickListener(this);

		txtnumberOfTutorial = (TextView) this
				.findViewById(R.id.textView_numberoftutorial);
		txtnumberOfTutorial.setText((currentIndex + 1) + "/"
				+ tutorialList.size());
		
		boolean isExistTutorial = db.isFavoriteTutorialExists(tutorialList.get(currentIndex).getId());
		if(isExistTutorial){
			
			btnMemorize.setChecked(true);
		}else{
			
			btnMemorize.setChecked(false);
		}
		TutorialImageAdapter imagePagerAdapter = new TutorialImageAdapter(getApplicationContext(),tutorialList);
		TutorialDataAdapter dataPagerAdapter = new TutorialDataAdapter(getApplicationContext(), tutorialList);
		
		
		pagerFront.setAdapter(imagePagerAdapter);
		pagerFront.setGestureDetector(motionDetector);
		pagerFront.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				Log.d("id", tutorialList.get(position).getId());
				boolean isExistTutorial = db.isFavoriteTutorialExists(tutorialList.get(position).getId());
				if(isExistTutorial){
					
					btnMemorize.setChecked(true);
				}else{
					
					btnMemorize.setChecked(false);
				}
//				 if (isRandom) {
//						if (tutorialList.size() != 1) {
//
//							position = Utils.randInt(0, tutorialList.size() - 1);
//						}
//
//					}
				currentIndex = position;
				txtnumberOfTutorial.setText((position + 1) + "/"
						+ tutorialList.size());
				if (pagerFront.getCurrentItem() != position)
					pagerFront.setCurrentItem(position);
				
				if (pagerBack.getCurrentItem() != position)
					pagerBack.setCurrentItem(position);
				
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		pagerBack.setAdapter(dataPagerAdapter);
		pagerBack.setGestureDetector(motionDetector);
		pagerBack.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				Log.d("id", tutorialList.get(position).getId());
				boolean isExistTutorial = db.isFavoriteTutorialExists(tutorialList.get(position).getId());
				if(isExistTutorial){
					
					btnMemorize.setChecked(true);
				}else{
					
					btnMemorize.setChecked(false);
				}
//				 if (isRandom) {
//						if (tutorialList.size() != 1) {
//
//							position = Utils.randInt(0, tutorialList.size() - 1);
//						}
//
//					}
				currentIndex = position;
				txtnumberOfTutorial.setText((position + 1) + "/"
						+ tutorialList.size());
				if (pagerBack.getCurrentItem() != position)
					pagerBack.setCurrentItem(position);
				
				if (pagerFront.getCurrentItem() != position)
					pagerFront.setCurrentItem(position);
				
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});


	}
	
	

	@Override
	public void onClick(View view) {

		int id = view.getId();
		if (id == R.id.button_opt_1) {
			optItem = new ActionItem(OptOne, tutorialList.get(currentIndex)
					.getOption_1());
			
			btnOpt1.setTextColor(Color.RED);
			btnOpt2.setTextColor(getResources().getColor(R.color.actionbar_text_color));
			btnOpt3.setTextColor(getResources().getColor(R.color.actionbar_text_color));
			btnOpt4.setTextColor(getResources().getColor(R.color.actionbar_text_color));

		} else if (id == R.id.button_opt_2) {
			optItem = new ActionItem(OptTwo, tutorialList.get(currentIndex)
					.getOption_2());
			btnOpt2.setTextColor(Color.RED);
			btnOpt1.setTextColor(getResources().getColor(R.color.actionbar_text_color));
			btnOpt3.setTextColor(getResources().getColor(R.color.actionbar_text_color));
			btnOpt4.setTextColor(getResources().getColor(R.color.actionbar_text_color));

		} else if (id == R.id.button_opt_3) {
			optItem = new ActionItem(Optthree, tutorialList.get(currentIndex)
					.getOption_3());
			btnOpt3.setTextColor(Color.RED);
			btnOpt2.setTextColor(getResources().getColor(R.color.actionbar_text_color));
			btnOpt1.setTextColor(getResources().getColor(R.color.actionbar_text_color));
			btnOpt4.setTextColor(getResources().getColor(R.color.actionbar_text_color));

		} else if (id == R.id.button_opt_4) {

			optItem = new ActionItem(OptFour, tutorialList.get(currentIndex)
					.getOption_4());
			btnOpt4.setTextColor(Color.RED);
			btnOpt2.setTextColor(getResources().getColor(R.color.actionbar_text_color));
			btnOpt3.setTextColor(getResources().getColor(R.color.actionbar_text_color));
			btnOpt1.setTextColor(getResources().getColor(R.color.actionbar_text_color));
		}else if (id == R.id.ToggleButton_memorize){
			if(btnMemorize.isChecked()){
				
				db.updateFavoriteTutorial(tutorialList.get(currentIndex).getId(),"1");
			}else{
				
				db.updateFavoriteTutorial(tutorialList.get(currentIndex).getId(),"0");
			}
			
			
			return;
			
		}else if (id == R.id.back_content){
			
			finish();
			overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
			return;
			
		}

		QuickAction mQuickAction = new QuickAction(TutorialActivity.this);

		mQuickAction.addActionItem(optItem);
		mQuickAction.setOnDismissListener(new QuickAction.OnDismissListener() {
			@Override
			public void onDismiss() {
			}
		});

		mQuickAction.show(layoutBottomContent);
		mQuickAction.setAnimStyle(QuickAction.ANIM_GROW_FROM_CENTER);
	}

	

	private void flipCard() {

		

		final View cardFace = findViewById(R.id.pager_front);
		final View cardBack = findViewById(R.id.pager_back);
		
		FlipAnimation flipAnimation = new FlipAnimation(cardFace, cardBack);
		
		if (cardFace.getVisibility() == View.GONE) {
			flipAnimation.reverse();
		}
		framelayout.startAnimation(flipAnimation);
		
		
	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {
		this.motionDetector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		float sensitvity = 50;

		if ((e1.getX() - e2.getX()) > sensitvity) {
			//showPrevoius();

		} else if ((e2.getX() - e1.getX()) > sensitvity) {
			//showNext();
		}

		return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		flipCard();
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		menuRight.toggle();
		currentIndex = position;

		pagerFront.setCurrentItem(position);
		pagerBack.setCurrentItem(position);
		txtnumberOfTutorial.setText((currentIndex + 1) + "/"
				+ tutorialList.size());
		
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		db.close();
		
	}
	@Override
	public void onBackPressed(){
		finish();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}
}
