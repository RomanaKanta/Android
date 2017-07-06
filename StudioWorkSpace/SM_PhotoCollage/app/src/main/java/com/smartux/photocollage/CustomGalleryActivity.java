package com.smartux.photocollage;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.smartmux.billing.v3.BillingProcessor;
import com.smartmux.billing.v3.TransactionDetails;
import com.smartux.photocollage.adapter.GalleryAdapter;
import com.smartux.photocollage.adapter.RecyclerView_FrameAdapter;
import com.smartux.photocollage.adapter.RecyclerView_FrameAdapter.OnItemClickListener;
import com.smartux.photocollage.adapter.SelectedImageListAdapter;
import com.smartux.photocollage.model.FrameJsonAndThumb;
import com.smartux.photocollage.utils.Constant;
import com.smartux.photocollage.utils.CustomGallery;
import com.smartux.photocollage.utils.FrameSelection;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class CustomGalleryActivity extends Activity {

	GridView gridGallery;
	GalleryAdapter galleryAdapter;
	Button btnGalleryOk, buttonSelectedNumber;
	String action;
	private ArrayList<FrameJsonAndThumb> frameList;
	private int selectedNumber = 0;
	private ImageLoader imageLoader;
	private TextView slectedNumberText,selectedFrameCountText;
	private ArrayList<CustomGallery> selectedDataList;

	LinearLayout bottomPanel;
	Animation animViewOpen, animViewClose = null;
	
	private static RecyclerView frame_listview, selectedList;
	static RecyclerView_FrameAdapter frameAdapter;

	SelectedImageListAdapter selectdAdapter;

	// In app purchase
	private static boolean isPurchasedIC = false;
	SharedPreferences sharedPreferences = null;
	public BillingProcessor billingProcessor;
	public boolean readyToPurchase = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		if (getIntent().hasExtra(Constant.PURCHASE)) {
			isPurchasedIC = getIntent().getExtras().getBoolean(
					Constant.PURCHASE);
		}

		if (!isPurchasedIC) {
			purchaseInfo();
		}

		setContentView(R.layout.image_picker);

		animViewOpen = AnimationUtils.loadAnimation(this,
				R.anim.bottom_up);
		animViewClose = AnimationUtils.loadAnimation(this, R.anim.bottom_down);
		
		action = getIntent().getAction();
		if (action == null) {
			finish();
		}

        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {

                initImageLoader();
                init();
                setGallery();
//                Toast.makeText(CustomGalleryActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
//                Toast.makeText(CustomGalleryActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }


        };


        new TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();


	}
	
	public static  void setPurchase(boolean pur){
		isPurchasedIC = pur;
		frameAdapter.setPurchase(pur);
		frameAdapter.notifyDataSetChanged();
	}
	
	public void setFrameClick() {
		frameAdapter.SetOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(View view, int position) {

				if (isPurchasedIC) {
					setIntent(position);
				} else {
					if (selectedNumber >2 && position > 9) {
						purchaseDialog();
					} else {
						setIntent(position);
					}
				}
			}
		});
	}

	private void setIntent(int position) {

        Log.e("json", ""+ frameList.get(position)
                .getFrameJson());

		Intent intent = new Intent(CustomGalleryActivity.this,
				CollageActivity.class);
		intent.putExtra(Constant.PURCHASE, isPurchasedIC);
		intent.putExtra(Constant.PATHS, getSelectedPaths());
		intent.putExtra(Constant.SELECTED_JSON, frameList.get(position)
				.getFrameJson());
		intent.putExtra(Constant.SELECTED_POSITION, position);
		startActivity(intent);
//		finish();
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}

	private void initImageLoader() {
		try {
			String CACHE_DIR = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/.temp_tmp";
			new File(CACHE_DIR).mkdirs();

			File cacheDir = StorageUtils.getOwnCacheDirectory(getBaseContext(),
					CACHE_DIR);

			DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
					.cacheOnDisc(true).imageScaleType(ImageScaleType.EXACTLY)
					.bitmapConfig(Bitmap.Config.RGB_565).build();
			ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(
					getBaseContext())
					.defaultDisplayImageOptions(defaultOptions)
					.discCache(new UnlimitedDiscCache(cacheDir))
					.memoryCache(new WeakMemoryCache());

			ImageLoaderConfiguration config = builder.build();
			imageLoader = ImageLoader.getInstance();
			imageLoader.init(config);

		} catch (Exception e) {

		}
	}

	private void init() {

		bottomPanel = (LinearLayout)findViewById(R.id.bottomHiddenPanel);
		
		gridGallery = (GridView) findViewById(R.id.gridGallery);
		gridGallery.setFastScrollEnabled(true);

		frame_listview = (RecyclerView) findViewById(R.id.horizontal_listview_frame);
		frame_listview.setHasFixedSize(true);
		frame_listview.setLayoutManager(new LinearLayoutManager(this,
				LinearLayoutManager.HORIZONTAL, false));

		slectedNumberText = (TextView) findViewById(R.id.selected_number_text);
		selectedFrameCountText = (TextView)findViewById(R.id.selected_frame_text);

		selectedList = (RecyclerView) findViewById(R.id.horizontal_listview_seleced);
		selectedList.setHasFixedSize(true);
		selectedList.setLayoutManager(new LinearLayoutManager(this,
				LinearLayoutManager.HORIZONTAL, false));

	}

	private void setGallery() {

		galleryAdapter = new GalleryAdapter(getApplicationContext(),
				imageLoader);
		PauseOnScrollListener listener = new PauseOnScrollListener(imageLoader,
				true, true);
		gridGallery.setOnScrollListener(listener);

		if (action.equalsIgnoreCase(getString(R.string.action_multiple_image_pic))) {

			gridGallery.setOnItemClickListener(mItemMulClickListener);
			galleryAdapter.setMultiplePick(true);

		}
        if (action.equalsIgnoreCase(getString(R.string.action_single_image_pic))) {

            gridGallery.setOnItemClickListener(mItemSingleClickListener);
            galleryAdapter.setMultiplePick(false);

        }

		gridGallery.setAdapter(galleryAdapter);

		galleryAdapter.addAll(getGalleryPhotos());

	}

	AdapterView.OnItemClickListener mItemMulClickListener = new AdapterView.OnItemClickListener() {

		private ArrayList<CustomGallery> galleryList;

		@Override
		public void onItemClick(AdapterView<?> l, View v, int position, long id) {
			
			if (selectedNumber > 9) {

				if (galleryAdapter.data.get(position).isSeleted) {
					galleryAdapter.changeSelection(v, position);
				} else {
				Toast.makeText(getApplicationContext(), R.string.photo_alart, Toast.LENGTH_LONG).show();
				}

			} else {

				galleryAdapter.changeSelection(v, position);
				selectedDataList = galleryAdapter.selectedData;
				orderDescending(selectedDataList);

			}

			galleryList = galleryAdapter.getSelected();
			selectedNumber = galleryList.size();
			slectedNumberText.setText("" + selectedNumber + " PHOTOS SELECTED");

			new FrmeaePickTask(selectedNumber).execute();
			if (selectdAdapter == null) {

				selectdAdapter = new SelectedImageListAdapter(
						CustomGalleryActivity.this, selectedDataList);
			}
			selectedList.setAdapter(selectdAdapter);

		}
	};

	AdapterView.OnItemClickListener mItemSingleClickListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> l, View v, int position, long id) {
			CustomGallery item = galleryAdapter.getItem(position);
			Intent data = new Intent(CustomGalleryActivity.this,MagazineActivity.class);
            data.putExtra("single_path", item.sdcardPath);
            startActivity(data);
//			setResult(RESULT_OK, data);
			finish();
		}
	};

	public static void orderDescending(final ArrayList<CustomGallery> list) {
		Collections.sort(list, new Comparator<CustomGallery>() {
			public int compare(CustomGallery s1, CustomGallery s2) {
				Integer i1 = list.indexOf(s1);
				Integer i2 = list.indexOf(s2);
				return i2.compareTo(i1);
			}
		});
	}

	public void refreshList(int position, int id, View v) {

		galleryAdapter.selectedData.remove(position);

		selectdAdapter.notifyDataSetChanged();

		galleryAdapter.data.get(id).isSeleted = false;
		galleryAdapter.deselectItem(v);

		new FrmeaePickTask(galleryAdapter.selectedData.size()).execute();

		slectedNumberText.setText("" + galleryAdapter.selectedData.size()
				+ " PHOTOS SELECTED");
		selectedNumber = galleryAdapter.selectedData.size();
	}

	private ArrayList<CustomGallery> getGalleryPhotos() {
		ArrayList<CustomGallery> galleryList = new ArrayList<CustomGallery>();

		Cursor imageCursor = null;
		try {
			final String[] columns = { MediaStore.Images.Media.DATA,
					MediaStore.Images.ImageColumns.ORIENTATION };
			final String orderBy = MediaStore.Images.Media.DATE_ADDED + " DESC";

			imageCursor = getApplicationContext().getContentResolver().query(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
					null, null, orderBy);
			while (imageCursor.moveToNext()) {
				CustomGallery item = new CustomGallery();
				int dataColumnIndex = imageCursor
						.getColumnIndex(MediaStore.Images.Media.DATA);

				item.sdcardPath = imageCursor.getString(dataColumnIndex);

				galleryList.add(item);

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (imageCursor != null && !imageCursor.isClosed()) {
				imageCursor.close();
			}
		}
		return galleryList;
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (!billingProcessor.handleActivityResult(requestCode, resultCode,
				data))
			;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (billingProcessor != null) {
			billingProcessor.release();
		}
	}

	public ArrayList<FrameJsonAndThumb> getFrameByPhotoNumber(int photoNumber) {

		if (photoNumber == 1) {
			frameList = FrameSelection.getOnePhotoFrame();
		} else if (photoNumber == 2) {
			frameList = FrameSelection.getTwoPhotoFrame();
		} else if (photoNumber == 3) {
			frameList = FrameSelection.getThreePhotoFrame();
		} else if (photoNumber == 4) {
			frameList = FrameSelection.getFourPhotoFrame();
		} else if (photoNumber == 5) {
			frameList = FrameSelection.getFivePhotoFrame();
		} else if (photoNumber == 6) {
			frameList = FrameSelection.getSixPhotoFrame();
		} else if (photoNumber == 7) {
			frameList = FrameSelection.getSevenPhotoFrame();
		} else if (photoNumber == 8) {
			frameList = FrameSelection.getEightPhotoFrame();
		} else if (photoNumber == 9) {
			frameList = FrameSelection.getNinePhotoFrame();
		} else {
			frameList = FrameSelection.getTenPhotoFrame();
		}

		return frameList;
	}

	class FrmeaePickTask extends AsyncTask<Void, Void, Void> {

		int photoNumber;

		public FrmeaePickTask(int photoNumber) {
			this.photoNumber = photoNumber;
		}

		@Override
		protected Void doInBackground(Void... params) {

			if (photoNumber != 0) {
				frameList = getFrameByPhotoNumber(photoNumber);
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (photoNumber == 0) {
				bottomPanel.startAnimation(animViewClose);
				bottomPanel.setVisibility(View.GONE);
//				frame_listview.setVisibility(View.INVISIBLE);
				
			} else {

				if (bottomPanel.getVisibility() == View.GONE) {
					bottomPanel.startAnimation(animViewOpen);
					bottomPanel.setVisibility(View.VISIBLE);
				}
				selectedFrameCountText.setText( "SELECT COLLAGE (" + frameList.size() + ")");
				frameAdapter = new RecyclerView_FrameAdapter(
						getApplicationContext(), frameList, isPurchasedIC,photoNumber);

				frame_listview.setAdapter(frameAdapter);
				setFrameClick();
			}
		}
	}

	public String[] getSelectedPaths() {

		ArrayList<CustomGallery> selected = galleryAdapter.getSelected();

		String[] allPath = new String[selected.size()];
		for (int i = 0; i < allPath.length; i++) {
			allPath[i] = selected.get(i).sdcardPath;
		}
		return allPath;
	}

	private void purchaseDialog() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				new ContextThemeWrapper(CustomGalleryActivity.this,
						R.style.popup_theme));

		alertDialog.setTitle(getString(R.string.purch_alart_title));
		alertDialog.setMessage(getString(R.string.purch_alart_msg1));

		alertDialog.setPositiveButton("YES",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						if (!readyToPurchase) {
							// showToast("Billing not initialized.");
							return;
						}

						billingProcessor.purchase(CustomGalleryActivity.this,
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

							isPurchasedIC = sharedPreferences.getBoolean(
									Constant.PREFS_KEY, false);

						}

						if (isPurchasedIC) {

							if (frameAdapter != null) {
								frameAdapter.setPurchase(isPurchasedIC);
								frameAdapter.notifyDataSetChanged();
							}

						}
					}

					@Override
					public void onProductPurchased(String productId,
							TransactionDetails details) {

						doButtonUnlock();

					}
				});

		sharedPreferences = getSharedPreferences(Constant.PREFS_NAME, 0);
		isPurchasedIC = sharedPreferences.getBoolean(Constant.PREFS_KEY, false);
		// isPurchasedIC =true;

	}

	private void doButtonUnlock() {

		sharedPreferences = getSharedPreferences(Constant.PREFS_NAME, 0);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putBoolean(Constant.PREFS_KEY, true);
		editor.commit();

		isPurchasedIC = sharedPreferences.getBoolean(Constant.PREFS_KEY, false);

		if (isPurchasedIC) {

			if (frameAdapter != null) {
				frameAdapter.setPurchase(isPurchasedIC);
				frameAdapter.notifyDataSetChanged();
			}

		}

	}

}
