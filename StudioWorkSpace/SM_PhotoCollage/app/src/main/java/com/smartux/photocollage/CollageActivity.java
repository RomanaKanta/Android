package com.smartux.photocollage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.util.LruCache;
import android.text.Html;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.smartmux.billing.v3.BillingProcessor;
import com.smartmux.billing.v3.TransactionDetails;
import com.smartmux.fotolibs.MainEditorActivity;
import com.smartux.photocollage.dialogfragment.ColorDialog;
import com.smartux.photocollage.dialogfragment.EffectDialog;
import com.smartux.photocollage.dialogfragment.FrameDialog;
import com.smartux.photocollage.dialogfragment.StickerDialog;
import com.smartux.photocollage.dialogfragment.TextDialog;
import com.smartux.photocollage.model.Boxes;
import com.smartux.photocollage.model.FrameJsonAndThumb;
import com.smartux.photocollage.model.SizeHolder;
import com.smartux.photocollage.utils.AlbumStorageDirFactory;
import com.smartux.photocollage.utils.BaseAlbumDirFactory;
import com.smartux.photocollage.utils.BitmapUtils;
import com.smartux.photocollage.utils.Constant;
import com.smartux.photocollage.utils.FrameSelection;
import com.smartux.photocollage.utils.FroyoAlbumDirFactory;
import com.smartux.photocollage.utils.JsonManager;
import com.smartux.photocollage.utils.ProgressHUD;
import com.smartux.photocollage.utils.ViewUtil;
import com.smartux.photocollage.widget.CircleImageView;
import com.smartux.photocollage.widget.MyView;
import com.smartux.photocollage.widget.PinterestView;
import com.smartux.photocollage.widget.SAFrameFunPhotoView;
import com.stickerdemo.view.BubbleTextView;
import com.stickerdemo.view.InputDialog;
import com.stickerdemo.view.StickerView;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yuku.ambilwarna.AmbilWarnaDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//import com.smartmux.fotolibs.MainEditorActivity;

public class CollageActivity extends AppMainActivity implements
		com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener,
		OnClickListener {

	// for callback event
	final int PHOTO_EDIT_REQUEST = 123;
	final int GALLERY_REQUEST = 231;
	final int CAMERA_REQUEST = 312;

	RelativeLayout topBar;

	// for frame
	public static String selectedBorderColor = "#ffffff";
	public ArrayList<Boxes> boxArrayList;
	private ArrayList<FrameJsonAndThumb> frameAndThumbList;
//	private static Context context;
	private ArrayList<Point> pathpointsArray;
	private Path path;
	private int numOfPhotos, selectedPosition;
	public RelativeLayout collageLayout;

	private FragmentManager fragmentManager;
	private DialogFragment mMenuDialogFragment;
	private ImageView shareImage;
	public static String selectedJson = "two/image_2_template_14.json";
	public String borderColor = "#ffffff";
	String effectColor = null;
	public int borderStroke = 20;

	public static ArrayList<Bitmap> bitmapList;
	public static ArrayList<SAFrameFunPhotoView> customViewList = new ArrayList<SAFrameFunPhotoView>();
	private int selectedThumb;
	public static LruCache<String, Bitmap> mMemoryCache;
	private RelativeLayout finalImage,bgLayer;
	public String[] paths;

	public ArrayList<String> imagePaths = new ArrayList<String>();
	private RelativeLayout collageOverlay;
	MyView custonView; // draw fram
	FrameLayout rootLayout;

    //bottom palen
    private ImageView colorPanel;
    private ImageView effectPanel,stickerPanel,textPanel,framePanel,shuffleImage, backButton;

	// DialogFragment
	ColorDialog colorDialog;
	EffectDialog effectDialog;
	FrameDialog frameDialog;
    StickerDialog stickerDialog;
    TextDialog textDialog;

	// for pinterestview
	public PinterestView pinterestView;
	SAFrameFunPhotoView SAview;
    MotionEvent mEvent;
	int edit_id, imageIndex;
	public static final String edit = "EDIT";
	public static final String camera = "CAMERA";
	public static final String photo = "PHOTO";

	// for camera
	private String mCurrentPhotoPath;
	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";
	private AlbumStorageDirFactory mAlbumStorageDirFactory = null;

	// In app purchase
	public boolean isPurchased = false;
	SharedPreferences sharedPreferences = null;
	public BillingProcessor billingProcessor;
	public boolean readyToPurchase = false;
    JsonManager mJsonManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//				WindowManager.LayoutParams.FLAG_FULLSCREEN);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);

		isPurchased = getIntent().getExtras().getBoolean(Constant.PURCHASE);

		if (!isPurchased) {
			purchaseInfo();
		}

        mJsonManager = new JsonManager(CollageActivity.this);

		bitmapList = new ArrayList<Bitmap>();
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

		// Use 1/8th of the available memory for this memory cache.
		final int cacheSize = maxMemory / 8;

		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				// The cache size will be measured in kilobytes rather than
				// number of items.
				return bitmap.getByteCount() / 1024;
			}
		};

		paths = getIntent().getExtras().getStringArray(Constant.PATHS);

		for (int i = 0; i < paths.length; i++) {

            if(paths[i]!=null) {
                imagePaths.add(paths[i]);
            }

		}

		for (int i = 0; i < imagePaths.size(); i++) {
			addBitmapToMemoryCache(String.valueOf(imagePaths.get(i)),
					BitmapUtils.generateBitmap(imagePaths.get(i)));

		}

		numOfPhotos = imagePaths.size();

		if (numOfPhotos == 1) {
			frameAndThumbList = FrameSelection.getOnePhotoFrame();
		} else if (numOfPhotos == 2) {
			frameAndThumbList = FrameSelection.getTwoPhotoFrame();
		} else if (numOfPhotos == 3) {
			frameAndThumbList = FrameSelection.getThreePhotoFrame();
		} else if (numOfPhotos == 4) {
			frameAndThumbList = FrameSelection.getFourPhotoFrame();
		} else if (numOfPhotos == 5) {
			frameAndThumbList = FrameSelection.getFivePhotoFrame();
		} else if (numOfPhotos == 6) {
			frameAndThumbList = FrameSelection.getSixPhotoFrame();
		} else if (numOfPhotos == 7) {
			frameAndThumbList = FrameSelection.getSevenPhotoFrame();
		} else if (numOfPhotos == 8) {
			frameAndThumbList = FrameSelection.getEightPhotoFrame();
		} else if (numOfPhotos == 9) {
			frameAndThumbList = FrameSelection.getNinePhotoFrame();
		} else if (numOfPhotos == 10) {
			frameAndThumbList = FrameSelection.getTenPhotoFrame();
		}

		selectedPosition = getIntent().getExtras().getInt(
				Constant.SELECTED_POSITION);

		selectedJson = getIntent().getExtras()
				.getString(Constant.SELECTED_JSON);

//        Log.e("json", ""+ selectedJson);
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.canvas_collage_activity, null);

//		this.context = getApplicationContext();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
			mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
		} else {
			mAlbumStorageDirFactory = new BaseAlbumDirFactory();
		}



		setContentView(v);
        setCollageLayout(v);
		initAllDialog();
		initView();

		initMenuFragment();
		fragmentManager = getFragmentManager();

		initPinterest();

	}

	private void initView() {

		topBar = (RelativeLayout) findViewById(R.id.topbar);
		backButton = (ImageView) findViewById(R.id.back);
		shuffleImage = (ImageView) findViewById(R.id.shuffleImage);
		shareImage = (ImageView) findViewById(R.id.share);
		effectPanel = (ImageView) findViewById(R.id.colorEffect);
        stickerPanel = (ImageView) findViewById(R.id.stickerImage);
        textPanel = (ImageView) findViewById(R.id.TextImage);
		framePanel = (ImageView) findViewById(R.id.frame_image);
		colorPanel = (ImageView) findViewById(R.id.colorPanel);

		backButton.setOnClickListener(this);
		shuffleImage.setOnClickListener(this);
		shareImage.setOnClickListener(this);
		effectPanel.setOnClickListener(this);
        stickerPanel.setOnClickListener(this);
        textPanel.setOnClickListener(this);
		framePanel.setOnClickListener(this);
		colorPanel.setOnClickListener(this);

		framePanel.setImageResource(frameAndThumbList.get(selectedPosition)
				.getFrameThumb());
	}

	private void initAllDialog() {
		effectDialog = new EffectDialog();
		frameDialog = new FrameDialog();
		colorDialog = new ColorDialog();
        stickerDialog = new StickerDialog();
        textDialog = new TextDialog();
	}

	public void setFrameLayer(int position) {

		collageLayout.removeAllViewsInLayout();

		boxArrayList = mJsonManager.getDataFromJson(frameAndThumbList.get(
                position).getFrameJson(), false);
		// collageLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
		collageLayout = (RelativeLayout) ViewUtil.collageView(
				getApplicationContext(), CollageActivity.this, collageLayout,
				boxArrayList, imagePaths, effectColor);
		selectedJson = frameAndThumbList.get(position).getFrameJson();
		selectedThumb = frameAndThumbList.get(position).getFrameThumb();

		framePanel.setImageResource(selectedThumb);
		selectedPosition = position;

	}

	public void setBorderColor(String color) {
        borderColor = color;
		custonView.setBorderColor(color);
		collageLayout.setBackgroundColor(Color.parseColor(color));
		for (int i = 0; i < customViewList.size(); i++) {

			customViewList.get(i).setBorderColor(color);
		}
		colorPanel.setColorFilter(ViewUtil.getFilter(color));

		framePanel.setColorFilter(ViewUtil.getFilter(color));
		colorDialog.dismiss();
	}

	public void setBorderStroke(int number) {
		borderStroke = number;

		custonView.setBorderStroke(number);
		for (int i = 0; i < customViewList.size(); i++) {

			customViewList.get(i).setBorderStroke(number);
		}
	}

	public void setColorEffect(String color) {

		effectColor = color;
	
		if(color.equals("#00000000"))
		{
			color=null;
		}
		for (int i = 0; i < CollageActivity.customViewList.size(); i++) {
			CollageActivity.customViewList.get(i).setColorFilter(color);
		}
		effectDialog.dismiss();

	}

    private void setCollageLayout(View v) {

		finalImage = (RelativeLayout) v.findViewById(R.id.collageFinalImage);
		collageLayout = (RelativeLayout) v.findViewById(R.id.collageLayout);
		collageOverlay = (RelativeLayout) v.findViewById(R.id.collageOverlay);
		collageOverlay.setBackgroundColor(Color.TRANSPARENT);

		finalImage.getLayoutParams().height = SizeHolder.getHt();

//        bgLayer = (RelativeLayout) v.findViewById(R.id.collageBGLayout);
//        bgLayer.addView(new BackgroundView(context));

		custonView = new MyView(CollageActivity.this);
		collageOverlay.addView(custonView);

		boxArrayList = mJsonManager.getDataFromJson(selectedJson,false);

//        boxArrayList = mJsonManager.getDataFromJson("two/image_2_template_5.json",false);

		// Log.e("SCREEN" , "ht "+
		// ScreenManager.getScreenHeight(getApplicationContext()) +
		// "    wt"+ScreenManager.getScreenWidht(getApplicationContext()));

		for (int i = 0; i < boxArrayList.size(); i++) {
			pathpointsArray = boxArrayList.get(i).getPathPoints();

			RelativeLayout.LayoutParams viewParams = new RelativeLayout.LayoutParams(
					boxArrayList.get(i).getBoxWidth(), boxArrayList.get(i)
							.getBoxHeight());
			if (imagePaths.size() > 1) {
				if (i > 0) {
					viewParams.setMargins(boxArrayList.get(i).getLeftMargin(),
							boxArrayList.get(i).getTopMargin(), boxArrayList
									.get(i).getRightMargin(), 0);

					// Log.e("setCollageLayout", "margin " + i + "   left"
					// + boxArrayList.get(i).getLeftMargin() + "    top"
					// + boxArrayList.get(i).getTopMargin() + "  right"
					// + boxArrayList.get(i).getRightMargin()
					// + "   bottom" + 0);
				}
			} else {
				viewParams.addRule(RelativeLayout.CENTER_IN_PARENT);
			}

			SAFrameFunPhotoView m_viewFrame = new SAFrameFunPhotoView(this);
			m_viewFrame.setId(i + 100);
			path = new Path();

			path.moveTo(pathpointsArray.get(0).x, pathpointsArray.get(0).y);

			for (int j = 0; j < pathpointsArray.size(); j++) {

				path.lineTo(pathpointsArray.get(j).x, pathpointsArray.get(j).y);

			}
			path.lineTo(pathpointsArray.get(0).x, pathpointsArray.get(0).y);

			m_viewFrame.setLayoutParams(viewParams);
			m_viewFrame.setBackgroundColor(Color.TRANSPARENT);
//            m_viewFrame.setBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.neon));

			m_viewFrame.setBitmap(mMemoryCache.get(imagePaths.get(i)));
			m_viewFrame.setImagePath(imagePaths.get(i));
			m_viewFrame.setImageIndex(i);
			m_viewFrame.setPath(path);
			m_viewFrame.setBox(boxArrayList.get(i));

			m_viewFrame.setActivity(this);
			m_viewFrame.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
			customViewList.add(m_viewFrame);
			// collageLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
			collageLayout.addView(m_viewFrame);

		}
	}

	public void setImageinFrame(String path, int id) {

		if (SAview.getId() == id) {
			SAview.setBitmap(BitmapUtils.generateBitmap(path));
			SAview.setImagePath(path);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		String imagepath;

		// inapp purchase
		if (!billingProcessor.handleActivityResult(requestCode, resultCode,
				data))
			;
		// ////////////////////

		if (requestCode == PHOTO_EDIT_REQUEST) {
			if (data.getExtras().containsKey("EDIT_DONE")) {

				imagepath = data.getExtras().getString("IMAGE_PATH");
				setImageinFrame(imagepath, edit_id);

				imagePaths.set(imageIndex, imagepath);

			}
		} else if (requestCode == GALLERY_REQUEST) {
			if (resultCode == Activity.RESULT_OK) {

				Uri imageFileUri = data.getData();

				if (imageFileUri != null) {

					imagepath = getPath(imageFileUri);
					setImageinFrame(imagepath, edit_id);

					imagePaths.set(imageIndex, imagepath);
				}
			}
		} else if (requestCode == CAMERA_REQUEST) {
			if (resultCode == Activity.RESULT_OK) {
				if (mCurrentPhotoPath != null) {
					setImageinFrame(mCurrentPhotoPath, edit_id);

					imagePaths.set(imageIndex, mCurrentPhotoPath);
				}
			}
		}

	}

	public String getPath(Uri uri) {
		String[] filePathColumn = { MediaStore.Images.Media.DATA };
		Cursor cursor = getContentResolver().query(uri, filePathColumn, null,
				null, null);
		cursor.moveToFirst();
		int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		return cursor.getString(columnIndex);
	}

	private void initPinterest() {
		pinterestView = (PinterestView) findViewById(R.id.item_layout);
		/**
		 * add item view into pinterestView
		 */
		pinterestView.addShowView(45,
				createChildView(R.drawable.menu_icon_close, ""),
				createChildView(R.drawable.photo, photo),
				createChildView(R.drawable.camera, camera),
				createChildView(R.drawable.edit, edit));

		CircleImageView imageView = (CircleImageView) findViewById(R.id.image);
		imageView.setFillColor(getResources().getColor(R.color.colorAccent));

	}

	public void showPinterestView(final SAFrameFunPhotoView view,
			MotionEvent event) {

            if (mBubbleTextView != null && mBubbleTextView.getInEdit()) {

                mBubbleTextView.setInEdit(false);

            if (mStickerView != null && mStickerView.getInEdit()) {

                mStickerView.setInEdit(false);

            }
            }else  if (mStickerView != null && mStickerView.getInEdit()) {

                mStickerView.setInEdit(false);

                if (mBubbleTextView != null && mBubbleTextView.getInEdit()) {

                    mBubbleTextView.setInEdit(false);

                }
            }else {
                SAview = view;
                mEvent = event;
                pinterestView.dispatchTouchEvent(event);

            }
            pinterestView
                    .setPinClickListener(new PinterestView.PinMenuClickListener() {

                        @Override
                        public void onMenuItemClick(int childAt) {
                            String tips = (String) pinterestView
                                    .getChildAt(childAt).getTag();

                            if (tips.equals(edit)) {
                                //
                                // Toast.makeText(ActivityCollage.this,
                                // "Edit clicked!  " + view.getId(),
                                // Toast.LENGTH_SHORT).show();

                                imageIndex = view.getImageIndex();
                                edit_id = view.getId();

                                Intent intent = new Intent(CollageActivity.this,
                                        MainEditorActivity.class);
                                intent.putExtra("path", view.getImagePath());
                                startActivityForResult(intent, PHOTO_EDIT_REQUEST);

                            } else if (tips.equals(camera)) {

                                // Toast.makeText(ActivityCollage.this,
                                // "camera clicked!  " + view.getId(),
                                // Toast.LENGTH_SHORT).show();

                                imageIndex = view.getImageIndex();
                                edit_id = view.getId();
                                importCamera();

                            } else if (tips.equals(photo)) {

                                // Toast.makeText(ActivityCollage.this,
                                // "photo clicked!   " + view.getId(),
                                // Toast.LENGTH_SHORT).show();

                                imageIndex = view.getImageIndex();
                                edit_id = view.getId();
                                importGallery();

                            }
                        }

                        @Override
                        public void onPreViewClick() {
                        }
                    });


	}

	// create child view for pinterest view
	public View createChildView(int imageId, String tip) {
		CircleImageView imageView = new CircleImageView(this);
		imageView.setBorderWidth(0);
		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		imageView.setFillColor(getResources().getColor(R.color.colorAccent));
		imageView.setImageResource(imageId);
		// just for save Menu item tips
		imageView.setTag(tip);
		return imageView;
	}

	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemCache(key) == null) {
			mMemoryCache.put(key, bitmap);
		}
	}

	public Bitmap getBitmapFromMemCache(String key) {
		return mMemoryCache.get(key);
	}

	private void initMenuFragment() {
		MenuParams menuParams = new MenuParams();
		menuParams.setActionBarSize((int) getResources().getDimension(
				R.dimen.contextmenu_size));
		menuParams.setMenuObjects(getMenuObjects());
		menuParams.setClosableOutside(false);
		mMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams);
	}

	private List<MenuObject> getMenuObjects() {

		List<MenuObject> menuObjects = new ArrayList<MenuObject>();

		MenuObject close = new MenuObject();
		close.setResource(R.drawable.menu_icon_close);
		close.setBgColor(getResources().getColor(R.color.black));
		close.setDividerColor(Color.BLACK);

		MenuObject facebook = new MenuObject("FACEBOOK");
		facebook.setResource(R.drawable.fb_button);
		facebook.setBgColor(getResources().getColor(R.color.black));
		facebook.setDividerColor(Color.BLACK);

		MenuObject email = new MenuObject("EMAIL");
		BitmapDrawable bd = new BitmapDrawable(getResources(),
				BitmapFactory.decodeResource(getResources(),
						R.drawable.email_button));
		email.setDrawable(bd);
		email.setBgColor(getResources().getColor(R.color.black));
		email.setDividerColor(Color.BLACK);

		MenuObject save = new MenuObject("SAVE");
		save.setResource(R.drawable.save_button);
		save.setBgColor(getResources().getColor(R.color.black));
		save.setDividerColor(Color.BLACK);

		menuObjects.add(close);
		menuObjects.add(facebook);
		// menuObjects.add(twitter);
		menuObjects.add(email);
		menuObjects.add(save);

		return menuObjects;
	}

	@Override
	public void onClick(View view) {

        closeAllFocus();

        if(pinterestView !=null && mEvent !=null) {
            pinterestView.closePinterestView(mEvent);
        }

		switch (view.getId()) {

		case R.id.back:

			showExitAlart();

			break;

		case R.id.shuffleImage:

            Animation startRotateAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.android_rotate_animation);
            shuffleImage.startAnimation(startRotateAnimation);

			new ShuffleTask().execute();

			break;

		case R.id.share:

			if (fragmentManager
					.findFragmentByTag(ContextMenuDialogFragment.TAG) == null) {
				mMenuDialogFragment.show(fragmentManager,
						ContextMenuDialogFragment.TAG);

			}

			break;

		case R.id.colorEffect:

			effectDialog.show(fragmentManager, "Effect_Dialog_Fragment");

			break;

            case R.id.stickerImage:

                Bundle argus = new Bundle();
                argus.putBoolean(Constant.PURCHASE, isPurchased);
                stickerDialog.setArguments(argus);
                stickerDialog.show(fragmentManager,"Sticker_Dialog_Fragment");
                break;

            case R.id.TextImage:
//                addText();
                Bundle argu = new Bundle();
                argu.putInt("TEXT",mTextViewsArray.size());
                textDialog.setArguments(argu);
                textDialog.show(fragmentManager,"Text_Dialog_Fragment");
                break;

		case R.id.frame_image:

			Bundle args = new Bundle();
			args.putInt(Constant.FRAME_NUMBER, numOfPhotos);
			args.putInt(Constant.SELECTED_POSITION, selectedPosition);
			args.putBoolean(Constant.PURCHASE, isPurchased);

			frameDialog.setArguments(args);
			frameDialog.show(fragmentManager, "Frame_Dialog_Fragment");

			break;

		case R.id.colorPanel:

			colorDialog.show(fragmentManager, "Color_Dialog_Fragment");

			break;

		default:
			break;
		}
	}

    private void closeAllFocus(){

           if (mStickerView != null) {
               mStickerView.setInEdit(false);
           }
           if (mBubbleTextView != null) {
               mBubbleTextView.setInEdit(false);
           }
    }

//	public static Context getContext() {
//		return context;
//	}

	private Bitmap generateBitmap() {

		finalImage.setDrawingCacheEnabled(true);
		Bitmap bitmap = Bitmap.createBitmap(finalImage.getDrawingCache());
		finalImage.setDrawingCacheEnabled(false);
		return bitmap;

	}

	public class ShuffleTask extends AsyncTask<Void, Void, Void> {
		// private String[] random;
		private ArrayList<String> random;
//		private ProgressDialog progress = null;
        ProgressHUD mProgressHUD;

		public ShuffleTask() {
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
            mProgressHUD = ProgressHUD.show(CollageActivity.this,
					"Shuffling",true);

		}

		@Override
		protected Void doInBackground(Void... params) {
			random = ViewUtil.Randomize(imagePaths);

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			super.onPostExecute(result);
			for (int i = 0; i < random.size(); i++) {

				customViewList.get(i).setBitmap(
						BitmapUtils.generateBitmap(random.get(i)));

				customViewList.get(i).setImagePath(random.get(i));

			}

            if (mProgressHUD.isShowing()) {

                mProgressHUD.dismiss();
            }

        }
	}

	@Override
	public void onBackPressed() {
		showExitAlart();
	}

	@Override
	public void onMenuItemClick(View clickedView, int position) {

		switch (position) {

		case 1: // FACEBOOK
			ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo net = cm.getActiveNetworkInfo();
			if (net == null) {
				Toast.makeText(getApplicationContext(), R.string.net_con,
						Toast.LENGTH_SHORT).show();

			} else {

				Handler h = new Handler();
				h.postDelayed(new Runnable() {
					public void run() {

						boolean installed = appInstalledOrNot(Constant.FB_PACKAGE);
						if (installed) {

//							new FacebookTask().execute();

//							saveInTampFile();
							shareImage();

						} else {
							Toast.makeText(
									getApplicationContext(),
									"Sorry, This Service not found in your device",
									Toast.LENGTH_LONG).show();
						}

					}
				}, 1000);
			}
			break;

		case 2: // EMAIL

			ConnectivityManager connection = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo network = connection.getActiveNetworkInfo();
			if (network == null) {
				Toast.makeText(getApplicationContext(), R.string.net_con,
						Toast.LENGTH_SHORT).show();

			} else {

				emailService();

			}
			break;

		case 3: // SAVE
			Boolean isSDPresent = Environment
					.getExternalStorageState().equals(
							Environment.MEDIA_MOUNTED);

			if (isSDPresent) {

				save();

			} else {
				Toast.makeText(getApplicationContext(),
						"sd card not available", Toast.LENGTH_SHORT).show();
			}
			break;

		default:
			break;
		}
	}

	private void deleteTempFile() {
		String root = Environment.getExternalStorageDirectory().toString()
				+ "/Photo_Collage/Edit_Photo";

		File fdelete = new File(root);
		if (deleteDirectory(fdelete)) {
			System.out.println("file Deleted :");
		} else {
			System.out.println("file not Deleted :");
		}

	}

	public static boolean deleteDirectory(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			if (files == null) {
				return true;
			}
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}

	private void save() {

		String root = Environment.getExternalStorageDirectory().toString()
				+ "/Photo_Collage/";

		File directory = new File(root);
		directory.mkdirs();

		long newdate = new Date().getTime();
		String fileName = newdate + ".png";
		File m_imagePath = new File(directory + "/", fileName);
		FileOutputStream fOut;
		try {
			fOut = new FileOutputStream(m_imagePath);
			generateBitmap().compress(Bitmap.CompressFormat.PNG, 90, fOut);
			fOut.flush();
			fOut.close();

			if (Build.VERSION.SDK_INT > 18) {

				sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
						Uri.parse("file://"
								+ Environment.getExternalStorageDirectory())));

			} else {

				sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
						Uri.parse("file://"
								+ Environment.getExternalStorageDirectory())));

			}

			Toast.makeText(getApplicationContext(), R.string.save,
					Toast.LENGTH_SHORT).show();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void emailService() {
		ContentValues values = new ContentValues();
		values.put(MediaStore.Images.Media.TITLE, "");
		values.put(MediaStore.Images.Media.DESCRIPTION, "");
		values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
		Uri uri = this.getContentResolver().insert(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
		OutputStream stream;
		try {
			stream = this.getContentResolver().openOutputStream(uri);
			generateBitmap().compress(Bitmap.CompressFormat.PNG, 100, stream);
			stream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String body = "My cool image is attached";

		Intent emailIntent = new Intent(Intent.ACTION_SEND);
		emailIntent.setType("message/rfc822");
		emailIntent.setType("image/png");
		emailIntent
				.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.checkout));
		emailIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(body));
		emailIntent.putExtra(Intent.EXTRA_STREAM, uri);

		try {
			this.startActivity(Intent.createChooser(emailIntent,
					getString(R.string.app_name)));
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(getApplicationContext(), R.string.email_client,
					Toast.LENGTH_SHORT).show();
		}

	}

	private void importGallery() {

		Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		photoPickerIntent.setType("image/*");
		startActivityForResult(photoPickerIntent, GALLERY_REQUEST);

	}

	private void importCamera() {

		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		File f = null;

		try {
			f = setUpPhotoFile();
			mCurrentPhotoPath = f.getAbsolutePath();
			takePictureIntent
					.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
		} catch (IOException e) {
			e.printStackTrace();
			f = null;
			mCurrentPhotoPath = null;
		}

		startActivityForResult(takePictureIntent, CAMERA_REQUEST);

	}

	private File setUpPhotoFile() throws IOException {

		File f = createImageFile();
		mCurrentPhotoPath = f.getAbsolutePath();

		return f;
	}

	@SuppressLint("SimpleDateFormat")
	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
		File albumF = getAlbumDir();
		File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX,
				albumF);
		return imageF;
	}

	@SuppressLint("SimpleDateFormat")
	private File getAlbumDir() {
		File storageDir = null;

		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {

			storageDir = mAlbumStorageDirFactory
					.getAlbumStorageDir("CameraSample");

			if (storageDir != null) {
				if (!storageDir.mkdirs()) {
					if (!storageDir.exists()) {
						Log.d("CameraSample", "failed to create directory");
						return null;
					}
				}
			}

		} else {
			Log.v(getString(R.string.app_name),
					"External storage is not mounted READ/WRITE.");
		}

		return storageDir;
	}

	@Override
	protected void onResume() {
		super.onResume();
		topBar.setVisibility(View.VISIBLE);
		// System.out.println("From Collage Free memory : "
		// + Runtime.getRuntime().freeMemory());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
//        if (progress != null) {
//            progress.dismiss();
//            progress = null;
//        }
		if (billingProcessor != null) {
			billingProcessor.release();
		}

		deleteTempFile();
		Runtime.getRuntime().gc();
	}

	private void showExitAlart() {

		AlertDialog.Builder alert = new AlertDialog.Builder(
				new ContextThemeWrapper(CollageActivity.this,
						R.style.popup_theme));

		alert.setTitle(R.string.app_name);
		alert.setMessage(R.string.start_new);

		alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				// overridePendingTransition(R.anim.push_right_in,
				// R.anim.push_right_out);

				for (int i = 0; i < customViewList.size(); i++) {

					customViewList.get(i).setBorderColor("#FFFFFF");
					customViewList.get(i).setBorderStroke(20);
				}

				custonView.setBorderStroke(20);
				custonView.setBorderColor("#FFFFFF");

				// Intent intent = new
				// Intent(getString(R.string.action_image_pic));
				// intent.putExtra(Constant.PURCHASE, isPurchasedIC);
				// startActivity(intent);
				customViewList.clear();
				finish();

				dialog.cancel();
			}

		});

		alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		alert.show();
	}

	private void purchaseInfo() {

		billingProcessor = new BillingProcessor(this, Constant.LICENSE_KEY,
				new BillingProcessor.IBillingHandler() {
					@Override
					public void onBillingError(int errorCode, Throwable error) {
						// showToast("onBillingError: "
						// + Integer.toString(errorCode));

					}

					@Override
					public void onBillingInitialized() {
						// showToast("onBillingInitialized");
						readyToPurchase = true;
						billingProcessor.loadOwnedPurchasesFromGoogle();

					}

					@Override
					public void onPurchaseHistoryRestored() {

						for (String sku : billingProcessor.listOwnedProducts()) {

							Log.d("item ", sku);

						}

						if (billingProcessor.isPurchased(Constant.ITEM_SKU)) {

							sharedPreferences = getSharedPreferences(
									Constant.PREFS_NAME, 0);
							SharedPreferences.Editor editor = sharedPreferences
									.edit();
							editor.putBoolean(Constant.PREFS_KEY, true);
							editor.commit();

							isPurchased = sharedPreferences.getBoolean(
									Constant.PREFS_KEY, false);

						}

						if (isPurchased) {

							// if (list_Adapter != null) {
							// list_Adapter.setPurchase(isPurchasedIC);
							// list_Adapter.notifyDataSetChanged();
							// }

						}
					}

					@Override
					public void onProductPurchased(String productId,
							TransactionDetails details) {

						doButtonUnlock();

					}
				});

		sharedPreferences = getSharedPreferences(Constant.PREFS_NAME, 0);
		isPurchased = sharedPreferences.getBoolean(Constant.PREFS_KEY, false);
		// isPurchasedIC =true;

	}

	private void doButtonUnlock() {

		sharedPreferences = getSharedPreferences(Constant.PREFS_NAME, 0);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putBoolean(Constant.PREFS_KEY, true);
		editor.commit();

		isPurchased = sharedPreferences.getBoolean(Constant.PREFS_KEY, false);
		CustomGalleryActivity.setPurchase(sharedPreferences.getBoolean(
				Constant.PREFS_KEY, false));
		// if (isPurchasedIC) {
		//
		// // if (list_Adapter != null) {
		// // list_Adapter.setPurchase(isPurchasedIC);
		// // list_Adapter.notifyDataSetChanged();
		// // }
		//
		// }

	}

	public void purchaseDialog(String msg) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				new ContextThemeWrapper(CollageActivity.this,
						R.style.popup_theme));

		alertDialog.setTitle(getString(R.string.purch_alart_title));
		alertDialog.setMessage(msg);

		alertDialog.setPositiveButton("YES",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						if (!readyToPurchase) {
							// showToast("Billing not initialized.");
							return;
						}

						billingProcessor.purchase(CollageActivity.this,
								Constant.ITEM_SKU);

						// doButtonUnlock();

						dialog.cancel();

					}

				});

		alertDialog.setNegativeButton("NO",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		alertDialog.show();

	}

	private boolean appInstalledOrNot(String uri) {
		PackageManager pm = getPackageManager();
		boolean app_installed;
		try {
			pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
			app_installed = true;
		} catch (PackageManager.NameNotFoundException e) {
			app_installed = false;
		}
		return app_installed;
	}

	private void shareImage() {

        ProgressDialog progressDialog = new ProgressDialog(CollageActivity.this);
        progressDialog.setMessage("Processing...");

        progressDialog.show();

		String root = Environment.getExternalStorageDirectory().toString()
				+ "/Photo_Collage/Edit_Photo";

        File directory = new File(root);
        directory.mkdirs();

        long newdate = new Date().getTime();
        String fileName = newdate + ".png";
        File m_imagePath = new File(directory + "/", fileName);
        FileOutputStream fOut;
        try {
            fOut = new FileOutputStream(m_imagePath);
            generateBitmap().compress(Bitmap.CompressFormat.PNG, 90, fOut);
            fOut.flush();
            fOut.close();

            if (Build.VERSION.SDK_INT > 18) {

                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                        Uri.parse("file://"
                                + Environment.getExternalStorageDirectory())));

            } else {

                sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                        Uri.parse("file://"
                                + Environment.getExternalStorageDirectory())));

            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent share = new Intent(Intent.ACTION_SEND);

        // If you want to share a png image only, you can do:
        // setType("image/png"); OR for jpeg: setType("image/jpeg");
        share.setType("image/png");

        Uri uri = Uri.fromFile(m_imagePath);
        share.putExtra(Intent.EXTRA_STREAM, uri);
        share.setPackage(Constant.FB_PACKAGE);
        progressDialog.dismiss();
        startActivity(share);
	}

    private StickerView mStickerView;
    private ArrayList<View> mStickerViewsArray = new ArrayList<View>();
    public void addStickerView(int resID) {

        final StickerView stickerView = new StickerView(CollageActivity.this);
        stickerView.setImageResource(resID);
        stickerView.setOperationListener(new StickerView.OperationListener() {
            @Override
            public void onDeleteClick() {
                mStickerViewsArray.remove(stickerView);
                finalImage.removeView(stickerView);
            }

            @Override
            public void onEdit(StickerView stickerView) {
                // if (mCurrentEditTextView != null) {
                // mCurrentEditTextView.setInEdit(false);
                // }
                mStickerView.setInEdit(false);
                mStickerView = stickerView;
                mStickerView.setInEdit(true);
            }

            @Override
            public void onTop(StickerView stickerView) {
                int position = mStickerViewsArray.indexOf(stickerView);
                if (position == mStickerViewsArray.size() - 1) {
                    return;
                }
                StickerView stickerTemp = (StickerView) mStickerViewsArray.remove(position);
                mStickerViewsArray.add(mStickerViewsArray.size(), stickerTemp);
            }
        });
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        finalImage.addView(stickerView, lp);
        mStickerViewsArray.add(stickerView);

        if (mStickerView != null) {
            mStickerView.setInEdit(false);
        }
        // if (mCurrentEditTextView != null) {
        // mCurrentEditTextView.setInEdit(false);
        // }
        mStickerView = stickerView;
        stickerView.setInEdit(true);
    }

    private InputDialog mInputDialog= new InputDialog(CollageActivity.this);
    private BubbleTextView mBubbleTextView;
    private ArrayList<View> mTextViewsArray = new ArrayList<View>();
    public void addText() {

        final BubbleTextView bubbleTextView = new BubbleTextView(CollageActivity.this,
                Color.BLACK, 0);
        mInputDialog.setBubbleView(bubbleTextView);
        mInputDialog.setBubbleText("Text");
        bubbleTextView.setImageResource(R.drawable.fake_img);
        bubbleTextView
                .setOperationListener(new BubbleTextView.OperationListener() {
                    @Override
                    public void onDeleteClick() {
                        mTextViewsArray.remove(bubbleTextView);
                        finalImage.removeView(bubbleTextView);
                    }

                    @Override
                    public void onEdit(BubbleTextView bubbleTextView) {
                        mBubbleTextView.setInEdit(false);
                        mBubbleTextView = bubbleTextView;
                        mBubbleTextView.setInEdit(true);
                        mInputDialog.setBubbleView(bubbleTextView);
                    }

                    @Override
                    public void onClick(BubbleTextView bubbleTextView) {

                        mInputDialog.setBubbleView(bubbleTextView);
                    }

                    @Override
                    public void onTop(BubbleTextView bubbleTextView) {
                        int position = mTextViewsArray.indexOf(bubbleTextView);
                        if (position == mTextViewsArray.size() - 1) {
                            return;
                        }
                        BubbleTextView textView = (BubbleTextView) mTextViewsArray
                                .remove(position);
                        mTextViewsArray.add(mTextViewsArray.size(), textView);

                    }

                    @Override
                    public void onLongClick(BubbleTextView bubbleTextView) {

                        mInputDialog.setBubbleView(bubbleTextView);
                        Bundle argu = new Bundle();
                        argu.putString("TEXT","current");
                        textDialog.setArguments(argu);
                        textDialog.show(fragmentManager,"Text_Dialog_Fragment");
                    }

                });


        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        finalImage.addView(bubbleTextView, lp);
        mTextViewsArray.add(bubbleTextView);

        if (mBubbleTextView != null) {
            mBubbleTextView.setInEdit(false);
        }
        mBubbleTextView = bubbleTextView;
        mBubbleTextView.setInEdit(true);

    }

    public void setEditText(String str) {

                if (str.equals("")) {
                    mInputDialog.setBubbleText("Text");
                } else {
                    mInputDialog.setBubbleText(str);
                }



    }

    public void setFont(int position,String typeface) {

          Typeface tf = null;

                if (position <= 7) {

                    tf = Typeface.createFromAsset(this.getAssets(),
                            "fonts/" + typeface + ".ttf");
                }

                if (position > 7) {

                    tf = Typeface.createFromAsset(this.getAssets(),
                            "fonts/" + typeface + ".otf");
                }

                mInputDialog.setBubbleTextTypeFace(tf);
            }

    public void setColor() {

        AmbilWarnaDialog colorDialog = new AmbilWarnaDialog(this,
                R.color.black_semi_transparent,
                new AmbilWarnaDialog.OnAmbilWarnaListener() {

                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {

                        mInputDialog.setBubbleTextColor(color);

                        // Log.d("getColor", "" + color);
                    }

                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {

                    }
                });

        colorDialog.show();
    }

    public void setAlign(int position){

        mInputDialog.setBubbleTextAlign(position);
    }

//    public class SaveLoader extends AsyncTask<Void, Void, Void> {
//
//        //        private ProgressDialog progress = null;
//        ProgressHUD mProgressHUD;
//        public SaveLoader() {
//        }
//
//        @Override
//        protected void onPreExecute() {
////            progress = ProgressDialog.show(MainEditorActivity.this, null,
////                    "Saving");
//            mProgressHUD = ProgressHUD.show(CollageActivity.this,
//                    "Saving",true);
//
//            super.onPreExecute();
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            save();
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void result) {
////            progress.dismiss();
//            if (mProgressHUD.isShowing()) {
//
//                mProgressHUD.dismiss();
//            }
//
//            super.onPostExecute(result);
//
//
//        }
//    }
}
