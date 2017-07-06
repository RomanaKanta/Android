package com.smartmux.androidapp;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Chronometer;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.smartmux.androidapp.adapter.GameAdapter;
import com.smartmux.androidapp.animition.AnimationFactory;
import com.smartmux.androidapp.animition.AnimationFactory.FlipDirection;
import com.smartmux.androidapp.database.DatabaseHandler;
import com.smartmux.androidapp.model.TutorialModel;
import com.smartmux.androidapp.util.Constant;
import com.smartmux.androidapp.util.Utils;

public class TutorialGameGridTypeActivity extends FragmentActivity implements
		OnClickListener {

	private final String Tag = TutorialGameGridTypeActivity.class.getName();

	private int count = 9;
	private String type = Constant.EASY;
	private int imageWidth, imageHeight;
	private GridView gridview;
	private DatabaseHandler db;
	private int range = 4;
	private int gameCardFirstIndex = -1;
	private int clickCount = 0;
	private int cardType = 0;
	private List<TutorialModel> tutorialList = new ArrayList<TutorialModel>();
	private int version;
	private Chronometer chronometer;
	private boolean isClick = true;
	private int pairMatch = 0;

	private String rootTitle;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gametype);

		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		getActionBar().setCustomView(R.layout.tutorial_actionbar);
		this.findViewById(R.id.Button_list).setVisibility(View.GONE);
		this.findViewById(R.id.back_content).setOnClickListener(this);
		
		 chronometer = (Chronometer) findViewById(R.id.chronometer);
		 chronometer.setVisibility(View.VISIBLE);
		 chronometer.setBase(SystemClock.elapsedRealtime());
         chronometer.start();

		db = new DatabaseHandler(this);

		version = android.os.Build.VERSION.SDK_INT;

		if (getIntent().getExtras() != null) {
			count = getIntent().getExtras().getInt(Constant.GAME_COUNT, 9);
			type = getIntent().getExtras().getString(Constant.GAME_TYPE,
					Constant.EASY);
			
			cardType = getIntent().getExtras().getInt(Constant.CARD_TYPE, 0);

			Log.d("getIntent ", count + " " + type);

			 rootTitle = getIntent().getExtras().getString(
					Constant.root_title);

			String query = "SELECT DISTINCT * FROM " + Constant.TABLE_TUTORIAL
					+ " WHERE " + Constant.root_title + " ='" + rootTitle + "'";

			if (rootTitle.equals("All")) {

				query = "SELECT DISTINCT * FROM " + Constant.TABLE_TUTORIAL;

			}

			tutorialList = db.getAllTutorials(query);

		}

		gridview = (GridView) findViewById(R.id.gridView_game_type);
		gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					final int position, long id) {
				
				if(type.equals(Constant.EASY) && position == 4){
					
					return;
				}
				
				if(isClick){
					
					isClick = false;
					
					new Handler().postDelayed(new Runnable() {
						 
						 
			            @Override
			            public void run() {
			            	
			            	
			            	isClick = true;
							
			               
			            }
			        }, 1200);
					
					View view = gridview.getChildAt(position
							- gridview.getFirstVisiblePosition());

					ViewAnimator viewAnimator = (ViewAnimator) view
							.findViewById(R.id.viewFlipper);

					AnimationFactory.flipTransition(viewAnimator,
							FlipDirection.LEFT_RIGHT);
					if (gameCardFirstIndex != position) {

						
						clickCount++;
					}

					if (clickCount == 2) {
						
						isClick = false;
						
						new Handler().postDelayed(new Runnable() {
							 
							 
				            @Override
				            public void run() {
				            	
				            	isClick = true;
								
				               
				            }
				        }, 1200);
						
						final View viewFirstIndex = gridview
								.getChildAt(gameCardFirstIndex
										- gridview.getFirstVisiblePosition());
						final View viewSecondIndex = gridview.getChildAt(position
								- gridview.getFirstVisiblePosition());
						
						
						ImageView imgFront1 = (ImageView) viewFirstIndex
									.findViewById(R.id.imageView_front);
						ImageView imgFront2 = (ImageView) viewSecondIndex
									.findViewById(R.id.imageView_front);
							if(imgFront1.getTag().toString().equals(imgFront2.getTag().toString())){
								
								Toast.makeText(getApplicationContext(), "pair match", 500).show();
								pairMatch++;
								System.out.println(pairMatch+"  "+gridview.getChildCount());
								if(type.equals(Constant.EASY) ){
									if((pairMatch*2) == gridview.getChildCount()-1){
										
										pairMatch = 200;
										Toast.makeText(getApplicationContext(), "Game Complete", 500).show();
									}
									
								}else{
									
									if((pairMatch*2) == gridview.getChildCount()){
										pairMatch = 200;
										Toast.makeText(getApplicationContext(), "Game Complete", 500).show();
									}
								}
								
								new Handler().postDelayed(new Runnable() {
									 
									 
						            @Override
						            public void run() {
						            	viewFirstIndex.setVisibility(View.INVISIBLE);
										viewSecondIndex.setVisibility(View.INVISIBLE);
										
						               
						            }
						        }, 1200);
								
								

								clickCount = 0;
								gameCardFirstIndex = -1;
								
								
								if(pairMatch == 200){
									new Handler().postDelayed(new Runnable() {
										 
										 
							            @Override
							            public void run() {
									Intent intent = new Intent(TutorialGameGridTypeActivity.this,
											GameResultActivity.class);
									intent.putExtra("time", chronometer.getText().toString());
									intent.putExtra(Constant.GAME_TYPE, type);
									intent.putExtra(Constant.GAME_COUNT, count);
									intent.putExtra(Constant.CARD_TYPE, cardType);
									intent.putExtra(Constant.root_title, rootTitle);
									startActivity(intent);
									finish();
							            }
							        }, 1300);
								}
								
								return;
							}

						final ViewAnimator viewAnimator1 = (ViewAnimator) viewFirstIndex
								.findViewById(R.id.viewFlipper);
						final ViewAnimator viewAnimator2 = (ViewAnimator) viewSecondIndex
								.findViewById(R.id.viewFlipper);
						new Handler().postDelayed(new Runnable() {
							 
				 
				            @Override
				            public void run() {
				            	
				            	AnimationFactory.flipTransition(viewAnimator1,
										FlipDirection.LEFT_RIGHT);

								AnimationFactory.flipTransition(viewAnimator2,
										FlipDirection.LEFT_RIGHT);
								
				               
				            }
				        }, 1200);

						clickCount = 0;
						gameCardFirstIndex = -1;

					}
					gameCardFirstIndex = position;
				}

				

			}

		});

	}

	@SuppressLint("NewApi")
	private void setGameList() {

		Display display = getWindowManager().getDefaultDisplay();

		final int width;
		final int height;
		if (version >= 13) {
			Point size = new Point();
			display.getSize(size);
			width = size.x;
			height = size.y;
		} else {
			width = display.getWidth();
			height = display.getHeight();
		}
		if (type.equals(Constant.EASY)) {

			imageWidth = (width) / 3;
			imageHeight = (height - 220) / 3;
			range = 4;

		} else if (type.equals(Constant.MEDIUM)) {

			imageWidth = (width) / 3;
			imageHeight = (height - 220) / 4;
			range = 6;
		} else if (type.equals(Constant.DIFFICULT)) {

			imageWidth = (width) / 4;
			imageHeight = (height - 220) / 6;
			range = 12;
		}

		if (type.equals(Constant.EASY) || type.equals(Constant.MEDIUM)) {

			gridview.setNumColumns(3);

		} else if (type.equals(Constant.DIFFICULT)) {

			gridview.setNumColumns(4);
		}
		Log.d("Total", "" + tutorialList.size());
		List<Integer> sortingOrder;
		if(tutorialList.size() < range){
			
			range = tutorialList.size() - 2;
			count = range * 2;
	
		}
		sortingOrder = Utils.getGameModel(count,type,
				getRandomArray(tutorialList.size() - 1, range));
		List<TutorialModel> list = new ArrayList<TutorialModel>();
		for (int i = 0; i < sortingOrder.size(); i++) {

			if (sortingOrder.get(i) != -1) {

				list.add(tutorialList.get(sortingOrder.get(i)));
			} else {

				list.add(tutorialList.get(sortingOrder.get(0)));
			}
		}

		Log.d("ids", "" + list);
		gridview.setAdapter(new GameAdapter(this, count, type, imageWidth,
				imageHeight,cardType,list));

	}

	@Override
	protected void onResume() {
		super.onResume();

		setGameList();
	}

	@Override
	public void onClick(View v) {

		int id = v.getId();

		if (id == R.id.back_content) {

			finish();
			overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
		}

	}


	private List<Integer> getRandomArray(int size, int range) {

		List<Integer> list = new ArrayList<Integer>();

		while (list.size() < range) {
			int randomnumber = (int) Math.ceil(Math.random() * size);
			boolean found = false;
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i) == randomnumber) {
					found = true;

					break;
				}
			}
			if (!found) {
				list.add(list.size(), randomnumber);
			}
		}
		return list;

	}

	@Override
	public void onBackPressed(){
		finish();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}
}
