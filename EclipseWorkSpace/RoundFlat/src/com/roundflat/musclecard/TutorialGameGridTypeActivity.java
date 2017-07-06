package com.roundflat.musclecard;

import java.util.ArrayList;
import java.util.List;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Chronometer;
import android.widget.Chronometer.OnChronometerTickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.easyandroidanimations.library.PuffOutAnimation;
import com.roundflat.musclecard.adapter.GameAdapter;
import com.roundflat.musclecard.animition.AnimationFactory;
import com.roundflat.musclecard.animition.AnimationFactory.FlipDirection;
import com.roundflat.musclecard.database.DatabaseHandler;
import com.roundflat.musclecard.model.ModelClass;
import com.roundflat.musclecard.model.TutorialModel;
import com.roundflat.musclecard.util.Constant;
import com.roundflat.musclecard.util.PreferenceUtils;
import com.roundflat.musclecard.util.Utils;

public class TutorialGameGridTypeActivity extends FragmentActivity implements
		OnClickListener {

	private final String Tag = TutorialGameGridTypeActivity.class.getName();
	private long timeWhenStopped = 0;
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
	List<TutorialModel> list;
	List<TutorialModel> mylist;
	List<String> myStringlist = new ArrayList<String>();
	private int version;
	private Chronometer chronometer;
	private boolean isClick = true;
	private boolean flag = true;
	private boolean flip = true;
	private int pairMatch = 0;
	private boolean isFav;
	String routeType;
	private boolean isFirstClick = false;
	private String rootTitle;
	Animation zooming, translate;
	ImageView middelImage, imageAllMarked = null;

	private Animator mCurrentAnimator, mCurrentAnimator1, mCurrentAnimator2;

	private int mShortAnimationDuration = 1000;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_gametype);

		this.findViewById(R.id.back_content).setOnClickListener(this);
		this.findViewById(R.id.textView_game_reset).setOnClickListener(this);

		zooming = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.zoom_in);

		middelImage = (ImageView) findViewById(R.id.imageView_zoom);
		imageAllMarked = (ImageView) findViewById(R.id.imageview_all_select);

		// translate = new TranslateAnimation(0, 100, 0, 100);
		// translate.setFillAfter(true);
		// translate.setDuration(1000);

		chronometer = (Chronometer) findViewById(R.id.chronometer);
		chronometer.setVisibility(View.VISIBLE);

		chronometer
				.setOnChronometerTickListener(new OnChronometerTickListener() {
					@Override
					public void onChronometerTick(Chronometer cArg) {
						long time = SystemClock.elapsedRealtime()
								- cArg.getBase();
						int h = (int) (time / 3600000);
						int m = (int) (time - h * 3600000) / 60000;
						int s = (int) (time - h * 3600000 - m * 60000) / 1000;
						String hh = h < 10 ? "0" + h : h + "";
						String mm = m < 10 ? "0" + m : m + "";
						String ss = s < 10 ? "0" + s : s + "";
						cArg.setText(mm + "分 " + ss + "秒");
					}
				});

		// chronometer.start();

		db = new DatabaseHandler(this);

		isFav = PreferenceUtils.getBoolPref(getApplicationContext(),
				Constant.PREF_NAME, Constant.PREF_FAV, false);

		version = android.os.Build.VERSION.SDK_INT;

		if (getIntent().getExtras() != null) {

			count = getIntent().getExtras().getInt(Constant.GAME_COUNT, 9);
			type = getIntent().getExtras().getString(Constant.GAME_TYPE,
					Constant.EASY);

			cardType = getIntent().getExtras().getInt(Constant.CARD_TYPE, 0);

			Log.d("getIntent ", count + " " + type);

			rootTitle = getIntent().getExtras().getString(Constant.root_title);

			if (getIntent().hasExtra(Constant.ROUTE_TYPE)) {

				routeType = getIntent().getExtras().getString(
						Constant.ROUTE_TYPE);

				if (routeType.equals(Constant.ROUTE_VIA_1ST_BUTTON)) {

					String query = "SELECT DISTINCT * FROM "
							+ Constant.TABLE_TUTORIAL + " WHERE "
							+ Constant.root_title + " ='" + rootTitle + "'";
					if (rootTitle.equals("All")) {

						query = "SELECT DISTINCT * FROM "
								+ Constant.TABLE_TUTORIAL;

					}
					if (isFav && rootTitle.equals("All")) {

						query = "SELECT DISTINCT * FROM "
								+ Constant.TABLE_TUTORIAL + " WHERE  "
								+ Constant.isFav + "!= 1";
					} else if (isFav && !rootTitle.equals("All")) {

						query = "SELECT DISTINCT * FROM "
								+ Constant.TABLE_TUTORIAL + " WHERE "
								+ Constant.root_title + " ='" + rootTitle
								+ "'  AND " + Constant.isFav + "!= 1";
					}

					tutorialList = db.getAllTutorials(query);
				} else {

					String query = "SELECT DISTINCT * FROM "
							+ Constant.TABLE_TUTORIAL + " WHERE "
							+ Constant.root_title + " ='" + rootTitle + "'";

					if (rootTitle.equals("All")) {

						query = "SELECT DISTINCT * FROM "
								+ Constant.TABLE_TUTORIAL;

					}

					tutorialList = db.getAllTutorials(query);
				}
			}

		}

		gridview = (GridView) findViewById(R.id.gridView_game_type);
		gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					final int position, long id) {

				TextView txtID = (TextView) v.findViewById(R.id.textView_id);
				final String itemID = txtID.getText().toString();

				if (type.equals(Constant.EASY) && position == 4) {

					return;
				}

				if (isClick && flag && flip) {

					if (!isFirstClick) {
						chronometer.setBase(SystemClock.elapsedRealtime());
						chronometer.start();
						isFirstClick = true;

					}

					isClick = false;

					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {

							isClick = true;

						}
					}, 1200);

					View view = gridview.getChildAt(position
							- gridview.getFirstVisiblePosition());

					final ViewAnimator viewAnimator = (ViewAnimator) view
							.findViewById(R.id.viewFlipper);

					if (position != gameCardFirstIndex) {
						AnimationFactory.flipTransition(viewAnimator,
								FlipDirection.LEFT_RIGHT);

					}
					if (position == gameCardFirstIndex) {

						flag = false;

						zoomImageFromThumb(viewAnimator, itemID);

					}

					if (gameCardFirstIndex != position) {

						clickCount++;

					}

					if (clickCount == 2) {

						isClick = false;

						flip = false;

						new Handler().postDelayed(new Runnable() {

							@Override
							public void run() {

								isClick = true;

							}
						}, 1200);

						final View viewFirstIndex = gridview
								.getChildAt(gameCardFirstIndex
										- gridview.getFirstVisiblePosition());
						final View viewSecondIndex = gridview
								.getChildAt(position
										- gridview.getFirstVisiblePosition());

						ImageView imgFront1 = (ImageView) viewFirstIndex
								.findViewById(R.id.imageView_front);
						ImageView imgFront2 = (ImageView) viewSecondIndex
								.findViewById(R.id.imageView_front);

						if (imgFront1.getTag().toString()
								.equals(imgFront2.getTag().toString())) {

							final TutorialModel imgtitle = db
									.getDetail(imgFront1.getTag().toString());
							// showToast(imgtitle.getTitle().toString());

							pairMatch++;
							System.out.println(pairMatch + "  "
									+ gridview.getChildCount());

							final Handler handler = new Handler();
							handler.postDelayed(new Runnable() {
								public void run() {
									flag = true;
									zoomImageFromMatch(viewFirstIndex,
											viewSecondIndex);
									// new
									// PuffOutAnimation(viewFirstIndex).animate();
									// new
									// PuffOutAnimation(viewSecondIndex).animate();
									// viewFirstIndex
									// .setVisibility(View.INVISIBLE);
									// viewSecondIndex
									// .setVisibility(View.INVISIBLE);

									showToast(imgtitle.getTitle().toString());
									handler.postDelayed(afterExe, 0);
								}
							}, 1200);

							clickCount = 0;
							gameCardFirstIndex = -1;

							return;
						}

						final ViewAnimator viewAnimator1 = (ViewAnimator) viewFirstIndex
								.findViewById(R.id.viewFlipper);
						final ViewAnimator viewAnimator2 = (ViewAnimator) viewSecondIndex
								.findViewById(R.id.viewFlipper);

						final Handler handler = new Handler();
						handler.postDelayed(new Runnable() {
							public void run() {
								AnimationFactory.flipTransition(viewAnimator1,
										FlipDirection.LEFT_RIGHT);

								AnimationFactory.flipTransition(viewAnimator2,
										FlipDirection.LEFT_RIGHT);

								gameCardFirstIndex = -1;
								handler.postDelayed(afterExe, 0);
							}
						}, 1500);

						clickCount = 0;
						gameCardFirstIndex = -1;

					}
					gameCardFirstIndex = position;

				}
			}

		});

	
		if (tutorialList.size() < 1) {

			imageAllMarked.setVisibility(View.VISIBLE);

			AlertDialog.Builder builder = new AlertDialog.Builder(
					TutorialGameGridTypeActivity.this);

			builder.setMessage(R.string.all_card_mark);

			builder.setCancelable(false);
			// Add the buttons
			builder.setPositiveButton(R.string.ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							finish();
							overridePendingTransition(R.anim.push_right_in,
									R.anim.push_right_out);

						}
					});

			// Create the AlertDialog
			builder.show();

		} else {

			setGameList();
		}
	}

	final Runnable afterExe = new Runnable() {
		public void run() {
			flip = true;

			if (type.equals(Constant.EASY) && (gridview.getChildCount() > 5)) {
				if ((pairMatch * 2) == gridview.getChildCount() - 1) {
					chronometer.stop();
					pairMatch = 200;
					// showToast("Game Complete");
				}

			} else {

				if ((pairMatch * 2) == gridview.getChildCount()) {
					chronometer.stop();
					pairMatch = 200;
					// showToast("Game Complete");
				}
			}

			if (pairMatch == 200) {
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						Intent intent = new Intent(
								TutorialGameGridTypeActivity.this,
								GameResultActivity.class);
						intent.putExtra("time", chronometer.getText()
								.toString());
						intent.putExtra(Constant.GAME_TYPE, type);
						intent.putExtra(Constant.GAME_COUNT, count);
						intent.putExtra(Constant.CARD_TYPE, cardType);
						intent.putExtra(Constant.root_title, rootTitle);
						intent.putExtra(Constant.ROUTE_TYPE, routeType);
						startActivity(intent);
						finish();
					}
				}, 2000);
			}
		}
	};

	private void showToast(String mgs) {

		LayoutInflater inflater = getLayoutInflater();
		View layouttoast = inflater.inflate(R.layout.toastcustom,
				(ViewGroup) findViewById(R.id.toastcustom));
		((TextView) layouttoast.findViewById(R.id.texttoast)).setText(mgs);

		Toast mytoast = new Toast(getBaseContext());
		mytoast.setView(layouttoast);
		mytoast.setGravity(Gravity.CENTER, 0, 0);
		mytoast.setDuration(500);
		mytoast.show();

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

			imageHeight = ((height - 240) / 3);

			imageWidth = (int) (imageHeight * 0.70);

			range = 4;

		} else if (type.equals(Constant.MEDIUM)) {
			imageHeight = ((height - 240) / 4);

			imageWidth = (int) (imageHeight * 0.70);

			range = 6;
		} else if (type.equals(Constant.DIFFICULT)) {
			imageHeight = ((height - 240) / 6);

			imageWidth = (int) (imageHeight * 0.70);

			range = 12;
		}

		if (type.equals(Constant.EASY) || type.equals(Constant.MEDIUM)) {

			gridview.setNumColumns(3);

		} else if (type.equals(Constant.DIFFICULT)) {

			gridview.setNumColumns(4);
		}
		Log.d("Total", "" + tutorialList.size());
		List<Integer> sortingOrder;

		if (cardType == 0) {

			if (tutorialList.size() <= range) {

				List<Integer> indexList = Utils.setGameForLessCard(type,
						tutorialList);

				list = new ArrayList<TutorialModel>();

				for (int i = 0; i < indexList.size(); i++) {

					if (tutorialList.size() == 4) {

						if (indexList.get(i) != -1) {

							list.add(tutorialList.get(indexList.get(i)));
						} else {

							list.add(tutorialList.get(indexList.get(0)));
						}

					} else {

						if (indexList.get(i) != -1) {

							list.add(tutorialList.get(indexList.get(i)));
						} else {

							list.add(tutorialList.get(indexList.get(4)));
						}
					}
				}

				count = list.size();
				Log.d("ids", "" + list);
				gridview.setAdapter(new GameAdapter(this, count, type,
						imageWidth, imageHeight, cardType, list));

			}

			else {

				sortingOrder = Utils.getGameModel(count, type,
						getRandomArray(tutorialList.size() - 1, range));
				Log.d("sortingOrder", "" + sortingOrder);

				list = new ArrayList<TutorialModel>();
				for (int i = 0; i < sortingOrder.size(); i++) {

					if (sortingOrder.get(i) != -1) {

						list.add(tutorialList.get(sortingOrder.get(i)));
					} else {

						list.add(tutorialList.get(sortingOrder.get(0)));
					}
				}

				Log.d("ids", "" + list);

				gridview.setAdapter(new GameAdapter(this, count, type,
						imageWidth, imageHeight, cardType, list));

			}
		}

		if (cardType == 1) {

			if (tutorialList.size() <= range) {

				List<ModelClass> listWithTag = Utils.setGameForLessMixedCard(
						type, tutorialList);
				mylist = new ArrayList<TutorialModel>();
				for (int i = 0; i < listWithTag.size(); i++) {
					
					if (tutorialList.size() == 4) {
						
						if (listWithTag.get(i).getNum() != -1) {

							int k = listWithTag.get(i).getNum();

							mylist.add(tutorialList.get(k));

							String m = listWithTag.get(i).getFlag();
							myStringlist.add(m);

							System.out.println("K = " + k + "   M=" + m);

						} else {

							mylist.add(tutorialList
									.get(listWithTag.get(0).getNum()));
							myStringlist.add("r");

						}
						
					}else{

					if (listWithTag.get(i).getNum() != -1) {

						int k = listWithTag.get(i).getNum();

						mylist.add(tutorialList.get(k));

						String m = listWithTag.get(i).getFlag();
						myStringlist.add(m);

						System.out.println("K = " + k + "   M=" + m);

					} else {

						mylist.add(tutorialList
								.get(listWithTag.get(4).getNum()));
						myStringlist.add("r");

					}
					}
				}

				count = mylist.size();
				gridview.setAdapter(new GameAdapter(this, count, type,
						imageWidth, imageHeight, cardType, mylist, myStringlist));

			} else {

				List<ModelClass> mynewList = Utils.getGameModelforMixedCard(
						count, type,
						getRandomArray(tutorialList.size() - 1, range));

				mylist = new ArrayList<TutorialModel>();
				for (int i = 0; i < mynewList.size(); i++) {

					if (mynewList.get(i).getNum() != -1) {

						int k = mynewList.get(i).getNum();

						mylist.add(tutorialList.get(k));

						String m = mynewList.get(i).getFlag();
						myStringlist.add(m);

						System.out.println("K = " + k + "   M=" + m);

					} else {

						mylist.add(tutorialList.get(mynewList.get(0).getNum()));
						myStringlist.add(mynewList.get(0).getFlag());

					}
				}

				gridview.setAdapter(new GameAdapter(this, count, type,
						imageWidth, imageHeight, cardType, mylist, myStringlist));
			}
		}

	}

	@Override
	protected void onResume() {
		super.onResume();

//		if (tutorialList.size() < 1) {
//
//			imageAllMarked.setVisibility(View.VISIBLE);
//
//			AlertDialog.Builder builder = new AlertDialog.Builder(
//					TutorialGameGridTypeActivity.this);
//
//			builder.setMessage(R.string.all_card_mark);
//
//			builder.setCancelable(false);
//			// Add the buttons
//			builder.setPositiveButton(R.string.ok,
//					new DialogInterface.OnClickListener() {
//						public void onClick(DialogInterface dialog, int id) {
//							finish();
//							overridePendingTransition(R.anim.push_right_in,
//									R.anim.push_right_out);
//
//						}
//					});
//
//			// Create the AlertDialog
//			builder.show();
//
//		} else {
//
//			setGameList();
//		}
	}

	@Override
	public void onClick(View v) {

		int id = v.getId();

		if (id == R.id.back_content) {

			if (isFirstClick) {
				
				timeWhenStopped = chronometer.getBase()
						- SystemClock.elapsedRealtime();
				chronometer.stop();
				
				AlertDialog.Builder responseDialog = new AlertDialog.Builder(
						TutorialGameGridTypeActivity.this);

				responseDialog.setMessage(R.string.quit_game);
				responseDialog.setCancelable(false);
				responseDialog.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								finish();
								overridePendingTransition(R.anim.push_right_in,
										R.anim.push_right_out);

							}
						});

				responseDialog.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								chronometer.setBase(SystemClock.elapsedRealtime()
										+ timeWhenStopped);
								chronometer.start();
								dialog.dismiss();

							}
						});

				responseDialog.show();

			} else {

				finish();
				overridePendingTransition(R.anim.push_right_in,
						R.anim.push_right_out);
			}
		} else if (id == R.id.textView_game_reset) {

			timeWhenStopped = chronometer.getBase()
					- SystemClock.elapsedRealtime();
			chronometer.stop();

			AlertDialog.Builder builder = new AlertDialog.Builder(
					TutorialGameGridTypeActivity.this);
			builder.setMessage(R.string.reset_game);

			builder.setCancelable(false);
			// Add the buttons
			builder.setNegativeButton(R.string.cancel,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							chronometer.setBase(SystemClock.elapsedRealtime()
									+ timeWhenStopped);
							chronometer.start();
							dialog.dismiss();
						}
					});

			builder.setPositiveButton(R.string.ok,
					new DialogInterface.OnClickListener() {
						public void onClick(final DialogInterface dialog, int id) {

							isClick = true;
							chronometer.setBase(SystemClock.elapsedRealtime());
							clickCount = 0;
							pairMatch = 0;
							isFirstClick = false;
							gameCardFirstIndex = -1;
							setGameList();

						}
					});

			// Create the AlertDialog
			builder.show();

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
	public void onBackPressed() {

		if (isFirstClick) {
			
			timeWhenStopped = chronometer.getBase()
					- SystemClock.elapsedRealtime();
			chronometer.stop();
			
			AlertDialog.Builder responseDialog = new AlertDialog.Builder(
					TutorialGameGridTypeActivity.this);
			responseDialog.setTitle("");
			responseDialog.setMessage("ゲームを終了しますか？");
			responseDialog.setCancelable(false);
			responseDialog.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							finish();
							overridePendingTransition(R.anim.push_right_in,
									R.anim.push_right_out);

						}
					});

			responseDialog.setNegativeButton("キャンセル",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
							chronometer.setBase(SystemClock.elapsedRealtime()
									+ timeWhenStopped);
							chronometer.start();
							
							dialog.dismiss();

						}
					});

			responseDialog.show();

		} else {

			finish();
			overridePendingTransition(R.anim.push_right_in,
					R.anim.push_right_out);
		}

	}

	String imagePatterName = "_A_000.png";

	@SuppressLint("NewApi")
	private void zoomImageFromThumb(final View thumbView, String position) {

		thumbView.setDrawingCacheEnabled(true);

		thumbView.buildDrawingCache();

		Bitmap bm = thumbView.getDrawingCache();

		// If there's an animation in progress, cancel it immediately and
		// proceed with this one.
		if (mCurrentAnimator != null) {
			mCurrentAnimator.cancel();
		}

		// Load the high-resolution "zoomed-in" image.
		final ImageView expandedImageView = (ImageView) this
				.findViewById(R.id.imageView_zoom);

		// expandedImageView.setBackground(Utils.getImage(this,
		// position+ imagePatterName,thumbView.getWidth(),
		// thumbView.getHeight()));

		expandedImageView.setImageBitmap(bm);

		// Calculate the starting and ending bounds for the zoomed-in image.
		// This step
		// involves lots of math. Yay, math.
		final Rect startBounds = new Rect();
		final Rect finalBounds = new Rect();
		final Point globalOffset = new Point();

		// The start bounds are the global visible rectangle of the thumbnail,
		// and the
		// final bounds are the global visible rectangle of the container view.
		// Also
		// set the container view's offset as the origin for the bounds, since
		// that's
		// the origin for the positioning animation properties (X, Y).
		thumbView.getGlobalVisibleRect(startBounds);
		RelativeLayout mLayout = (RelativeLayout) findViewById(R.id.container);

		mLayout.getGlobalVisibleRect(finalBounds, globalOffset);

		startBounds.offset(-globalOffset.x, -globalOffset.y);
		// finalBounds.offset(-globalOffset.x, -globalOffset.y);

		int dx = (expandedImageView.getWidth() / 2)
				- (expandedImageView.getWidth() / 4);
		int dy = (expandedImageView.getHeight() / 2)
				- (expandedImageView.getHeight() / 3);
		finalBounds.offset(dx, dy);

		// Adjust the start bounds to be the same aspect ratio as the final
		// bounds using the
		// "center crop" technique. This prevents undesirable stretching during
		// the animation.
		// Also calculate the start scaling factor (the end scaling factor is
		// always 1.0).
		float startScale;
		if ((float) finalBounds.width() / finalBounds.height() > (float) startBounds
				.width() / startBounds.height()) {
			// Extend start bounds horizontally
			startScale = (float) startBounds.height() / finalBounds.height();
			float startWidth = startScale * (finalBounds.width());
			float deltaWidth = (startWidth - startBounds.width()) / 2;
			startBounds.left -= deltaWidth;
			startBounds.right += deltaWidth;
		} else {
			// Extend start bounds vertically
			startScale = (float) startBounds.width() / finalBounds.width();
			float startHeight = startScale * finalBounds.height();
			float deltaHeight = (startHeight - startBounds.height()) / 2;
			startBounds.top -= deltaHeight;
			startBounds.bottom += deltaHeight;
		}

		// Hide the thumbnail and show the zoomed-in view. When the animation
		// begins,
		// it will position the zoomed-in view in the place of the thumbnail.
		thumbView.setAlpha(0f);
		expandedImageView.setVisibility(View.VISIBLE);

		// Set the pivot point for SCALE_X and SCALE_Y transformations to the
		// top-left corner of
		// the zoomed-in view (the default is the center of the view).
		expandedImageView.setPivotX(0f);
		expandedImageView.setPivotY(0f);

		// Construct and run the parallel animation of the four translation and
		// scale properties
		// (X, Y, SCALE_X, and SCALE_Y).
		AnimatorSet set = new AnimatorSet();
		set.play(
				ObjectAnimator.ofFloat(expandedImageView, View.X,
						startBounds.left, finalBounds.left))
				.with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
						startBounds.top, finalBounds.top))
				.with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
						startScale, 0.5f))
				.with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y,
						startScale, 0.5f));
		set.setDuration(mShortAnimationDuration);
		set.setInterpolator(new DecelerateInterpolator());
		set.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				mCurrentAnimator = null;
			}

			@Override
			public void onAnimationCancel(Animator animation) {
				mCurrentAnimator = null;
			}
		});
		set.start();
		mCurrentAnimator = set;

		// Upon clicking the zoomed-in image, it should zoom back down to the
		// original bounds
		// and show the thumbnail instead of the expanded image.
		final float startScaleFinal = startScale;

		expandedImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (mCurrentAnimator != null) {
					mCurrentAnimator.cancel();
				}

				// Animate the four positioning/sizing properties in parallel,
				// back to their
				// original values.
				AnimatorSet set = new AnimatorSet();
				set.play(
						ObjectAnimator.ofFloat(expandedImageView, View.X,
								startBounds.left))
						.with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
								startBounds.top))
						.with(ObjectAnimator.ofFloat(expandedImageView,
								View.SCALE_X, startScaleFinal))
						.with(ObjectAnimator.ofFloat(expandedImageView,
								View.SCALE_Y, startScaleFinal));
				set.setDuration(mShortAnimationDuration);
				set.setInterpolator(new DecelerateInterpolator());
				set.addListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
						thumbView.setAlpha(1f);
						expandedImageView.setVisibility(View.GONE);
						mCurrentAnimator = null;
					}

					@Override
					public void onAnimationCancel(Animator animation) {
						thumbView.setAlpha(1f);
						expandedImageView.setVisibility(View.GONE);
						mCurrentAnimator = null;
					}
				});
				set.start();
				mCurrentAnimator = set;
				flag = true;
			}

		});
	}

	@SuppressLint("NewApi")
	private void zoomImageFromMatch(final View firstView, final View secondView) {

		firstView.setDrawingCacheEnabled(true);

		firstView.buildDrawingCache();

		Bitmap bm1 = firstView.getDrawingCache();

		secondView.setDrawingCacheEnabled(true);

		secondView.buildDrawingCache();

		Bitmap bm2 = secondView.getDrawingCache();

		// If there's an animation in progress, cancel it immediately and
		// proceed with this one.
		if (mCurrentAnimator1 != null || mCurrentAnimator2 != null) {
			mCurrentAnimator1.cancel();
			mCurrentAnimator2.cancel();
		}

		// Load the high-resolution "zoomed-in" image.
		final ImageView expandedFirstImage = (ImageView) this
				.findViewById(R.id.imageView_zoom);

		final ImageView expandedSecontImage = (ImageView) this
				.findViewById(R.id.imageView_zoom_second_item);

		// expandedImageView.setBackground(Utils.getImage(this,
		// position+ imagePatterName,thumbView.getWidth(),
		// thumbView.getHeight()));
		expandedFirstImage.setImageBitmap(bm1);

		expandedSecontImage.setImageBitmap(bm2);

		// Calculate the starting and ending bounds for the zoomed-in image.
		// This step
		// involves lots of math. Yay, math.
		final Rect startBounds1 = new Rect();
		final Rect startBounds2 = new Rect();
		final Rect finalBounds1 = new Rect();
		final Rect finalBounds2 = new Rect();
		final Point globalOffset = new Point();

		// The start bounds are the global visible rectangle of the thumbnail,
		// and the
		// final bounds are the global visible rectangle of the container view.
		// Also
		// set the container view's offset as the origin for the bounds, since
		// that's
		// the origin for the positioning animation properties (X, Y).
		firstView.getGlobalVisibleRect(startBounds1);
		secondView.getGlobalVisibleRect(startBounds2);
		RelativeLayout mLayout = (RelativeLayout) findViewById(R.id.container);

		mLayout.getGlobalVisibleRect(finalBounds1, globalOffset);
		mLayout.getGlobalVisibleRect(finalBounds2, globalOffset);

		startBounds1.offset(-globalOffset.x, -globalOffset.y);
		startBounds2.offset(-globalOffset.x, -globalOffset.y);
		// finalBounds.offset(-globalOffset.x, -globalOffset.y);

		// int dx1 = (expandedSecontImage.getWidth() / 2)
		// - (expandedSecontImage.getWidth() / 4);
		int dx1 = 0;
		int dy1 = (expandedSecontImage.getHeight() / 2)
				- (expandedSecontImage.getHeight() / 3);
		finalBounds1.offset(dx1, dy1);

		int dx2 = (expandedSecontImage.getWidth() / 2) + 5;

		int dy2 = (expandedSecontImage.getHeight() / 2)
				- (expandedSecontImage.getHeight() / 3);
		finalBounds2.offset(dx2, dy2);

		// Adjust the start bounds to be the same aspect ratio as the final
		// bounds using the
		// "center crop" technique. This prevents undesirable stretching during
		// the animation.
		// Also calculate the start scaling factor (the end scaling factor is
		// always 1.0).
		float startScale;
		if ((float) finalBounds1.width() / finalBounds1.height() > (float) startBounds1
				.width() / startBounds1.height()) {
			// Extend start bounds horizontally
			startScale = (float) startBounds1.height() / finalBounds1.height();
			float startWidth = startScale * (finalBounds1.width());
			float deltaWidth = (startWidth - startBounds1.width()) / 2;
			startBounds1.left -= deltaWidth;
			startBounds1.right += deltaWidth;
		} else {
			// Extend start bounds vertically
			startScale = (float) startBounds1.width() / finalBounds1.width();
			float startHeight = startScale * finalBounds1.height();
			float deltaHeight = (startHeight - startBounds1.height()) / 2;
			startBounds1.top -= deltaHeight;
			startBounds1.bottom += deltaHeight;
		}

		if ((float) finalBounds2.width() / finalBounds2.height() > (float) startBounds2
				.width() / startBounds2.height()) {
			// Extend start bounds horizontally
			startScale = (float) startBounds2.height() / finalBounds2.height();
			float startWidth = startScale * (finalBounds2.width());
			float deltaWidth = (startWidth - startBounds2.width()) / 2;
			startBounds2.left -= deltaWidth;
			startBounds2.right += deltaWidth;
		} else {
			// Extend start bounds vertically
			startScale = (float) startBounds2.width() / finalBounds2.width();
			float startHeight = startScale * finalBounds2.height();
			float deltaHeight = (startHeight - startBounds2.height()) / 2;
			startBounds2.top -= deltaHeight;
			startBounds2.bottom += deltaHeight;
		}

		// Hide the thumbnail and show the zoomed-in view. When the animation
		// begins,
		// it will position the zoomed-in view in the place of the thumbnail.
		firstView.setAlpha(0f);
		expandedFirstImage.setVisibility(View.VISIBLE);

		secondView.setAlpha(0f);
		expandedSecontImage.setVisibility(View.VISIBLE);

		// Set the pivot point for SCALE_X and SCALE_Y transformations to the
		// top-left corner of
		// the zoomed-in view (the default is the center of the view).
		expandedFirstImage.setPivotX(0f);
		expandedFirstImage.setPivotY(0f);

		expandedSecontImage.setPivotX(0f);
		expandedSecontImage.setPivotY(0f);

		// Construct and run the parallel animation of the four translation and
		// scale properties
		// (X, Y, SCALE_X, and SCALE_Y).
		AnimatorSet set1 = new AnimatorSet();
		set1.play(
				ObjectAnimator.ofFloat(expandedFirstImage, View.X,
						startBounds1.left, finalBounds1.left))
				.with(ObjectAnimator.ofFloat(expandedFirstImage, View.Y,
						startBounds1.top, finalBounds1.top))
				.with(ObjectAnimator.ofFloat(expandedFirstImage, View.SCALE_X,
						startScale, 0.5f))
				.with(ObjectAnimator.ofFloat(expandedFirstImage, View.SCALE_Y,
						startScale, 0.5f));
		set1.setDuration(mShortAnimationDuration);
		set1.setInterpolator(new DecelerateInterpolator());
		set1.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
//				expandedFirstImage.startAnimation(zooming);
				new PuffOutAnimation(expandedFirstImage).animate();
				mCurrentAnimator1 = null;
			}

			@Override
			public void onAnimationCancel(Animator animation) {
				mCurrentAnimator1 = null;
			}
		});
		set1.start();
		mCurrentAnimator1 = set1;

		AnimatorSet set2 = new AnimatorSet();
		set2.play(
				ObjectAnimator.ofFloat(expandedSecontImage, View.X,
						startBounds2.left, finalBounds2.left))
				.with(ObjectAnimator.ofFloat(expandedSecontImage, View.Y,
						startBounds2.top, finalBounds2.top))
				.with(ObjectAnimator.ofFloat(expandedSecontImage, View.SCALE_X,
						startScale, 0.5f))
				.with(ObjectAnimator.ofFloat(expandedSecontImage, View.SCALE_Y,
						startScale, 0.5f));
		set2.setDuration(mShortAnimationDuration);
		set2.setInterpolator(new DecelerateInterpolator());
		set2.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {

				new PuffOutAnimation(expandedSecontImage).animate();
				mCurrentAnimator2 = null;
			}

			@Override
			public void onAnimationCancel(Animator animation) {
				mCurrentAnimator2 = null;
			}
		});
		set2.start();
		mCurrentAnimator2 = set2;

		flag = true;

	}

	// private void zoomImageAnimation(final View firstView, ImageView expImage,
	// int x, int y) {
	//
	// firstView.setDrawingCacheEnabled(true);
	//
	// firstView.buildDrawingCache();
	//
	// Bitmap bm1 = firstView.getDrawingCache();
	//
	//
	//
	// // If there's an animation in progress, cancel it immediately and
	// // proceed with this one.
	// if (mCurrentAnimator1 != null) {
	// mCurrentAnimator1.cancel();
	// }
	//
	// // Load the high-resolution "zoomed-in" image.
	// final ImageView expandedFirstImage = expImage;
	//
	//
	// // expandedImageView.setBackground(Utils.getImage(this,
	// // position+ imagePatterName,thumbView.getWidth(),
	// // thumbView.getHeight()));
	// expandedFirstImage.setImageBitmap(bm1);
	//
	// // Calculate the starting and ending bounds for the zoomed-in image.
	// // This step
	// // involves lots of math. Yay, math.
	// final Rect startBounds1 = new Rect();
	// final Rect finalBounds1 = new Rect();
	// final Point globalOffset = new Point();
	//
	// // The start bounds are the global visible rectangle of the thumbnail,
	// // and the
	// // final bounds are the global visible rectangle of the container view.
	// // Also
	// // set the container view's offset as the origin for the bounds, since
	// // that's
	// // the origin for the positioning animation properties (X, Y).
	// firstView.getGlobalVisibleRect(startBounds1);
	//
	// RelativeLayout mLayout = (RelativeLayout) findViewById(R.id.container);
	//
	// mLayout.getGlobalVisibleRect(finalBounds1, globalOffset);
	//
	// startBounds1.offset(-globalOffset.x, -globalOffset.y);
	//
	// // finalBounds.offset(-globalOffset.x, -globalOffset.y);
	//
	// // int dx1 = (expandedSecontImage.getWidth() / 2)
	// // - (expandedSecontImage.getWidth() / 4);
	// int dx1 = x;
	// int dy1 = y;
	//
	//
	// // Adjust the start bounds to be the same aspect ratio as the final
	// // bounds using the
	// // "center crop" technique. This prevents undesirable stretching during
	// // the animation.
	// // Also calculate the start scaling factor (the end scaling factor is
	// // always 1.0).
	// float startScale;
	// if ((float) finalBounds1.width() / finalBounds1.height() > (float)
	// startBounds1
	// .width() / startBounds1.height()) {
	// // Extend start bounds horizontally
	// startScale = (float) startBounds1.height() / finalBounds1.height();
	// float startWidth = startScale * (finalBounds1.width());
	// float deltaWidth = (startWidth - startBounds1.width()) / 2;
	// startBounds1.left -= deltaWidth;
	// startBounds1.right += deltaWidth;
	// } else {
	// // Extend start bounds vertically
	// startScale = (float) startBounds1.width() / finalBounds1.width();
	// float startHeight = startScale * finalBounds1.height();
	// float deltaHeight = (startHeight - startBounds1.height()) / 2;
	// startBounds1.top -= deltaHeight;
	// startBounds1.bottom += deltaHeight;
	// }
	//
	//
	//
	// // Hide the thumbnail and show the zoomed-in view. When the animation
	// // begins,
	// // it will position the zoomed-in view in the place of the thumbnail.
	// firstView.setAlpha(0f);
	// expandedFirstImage.setVisibility(View.VISIBLE);
	//
	//
	// // Set the pivot point for SCALE_X and SCALE_Y transformations to the
	// // top-left corner of
	// // the zoomed-in view (the default is the center of the view).
	// expandedFirstImage.setPivotX(0f);
	// expandedFirstImage.setPivotY(0f);
	//
	//
	// // Construct and run the parallel animation of the four translation and
	// // scale properties
	// // (X, Y, SCALE_X, and SCALE_Y).
	// AnimatorSet set1 = new AnimatorSet();
	// set1.play(
	// ObjectAnimator.ofFloat(expandedFirstImage, View.X,
	// startBounds1.left, finalBounds1.left))
	// .with(ObjectAnimator.ofFloat(expandedFirstImage, View.Y,
	// startBounds1.top, finalBounds1.top))
	// .with(ObjectAnimator.ofFloat(expandedFirstImage, View.SCALE_X,
	// startScale, 0.5f))
	// .with(ObjectAnimator.ofFloat(expandedFirstImage, View.SCALE_Y,
	// startScale, 0.5f));
	// set1.setDuration(mShortAnimationDuration);
	// set1.setInterpolator(new DecelerateInterpolator());
	// set1.addListener(new AnimatorListenerAdapter() {
	// @Override
	// public void onAnimationEnd(Animator animation) {
	//
	// // new PuffOutAnimation(expandedFirstImage).animate();
	// mCurrentAnimator1 = null;
	// }
	//
	// @Override
	// public void onAnimationCancel(Animator animation) {
	// mCurrentAnimator1 = null;
	// }
	// });
	// set1.start();
	// mCurrentAnimator1 = set1;
	//
	//
	//
	// }

}
