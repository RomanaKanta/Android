package com.smartmux.foto;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ortiz.touch.TouchImageView;
import com.smartmux.billing.v3.BillingProcessor;
import com.smartmux.billing.v3.TransactionDetails;
import com.smartmux.foto.adapter.RecycleView_HorizontalListAdapter;
import com.smartmux.foto.adapter.RecycleView_HorizontalListAdapter.OnItemClickListener;
import com.smartmux.foto.fragment.FragmentAdjustment;
import com.smartmux.foto.fragment.FragmentBlur;
import com.smartmux.foto.fragment.FragmentCrop;
import com.smartmux.foto.fragment.FragmentDraw;
import com.smartmux.foto.fragment.FragmentEffect;
import com.smartmux.foto.fragment.FragmentFilter;
import com.smartmux.foto.fragment.FragmentFrame;
import com.smartmux.foto.fragment.FragmentResize;
import com.smartmux.foto.fragment.FragmentRotate;
import com.smartmux.foto.fragment.FragmentSticker;
import com.smartmux.foto.fragment.FragmentText;
import com.smartmux.foto.fragment.FragmentTransparent;
import com.smartmux.foto.modelclass.BitmapHolder;
import com.smartmux.foto.modelclass.ListData;
import com.smartmux.foto.utils.AddItems;
import com.smartmux.foto.utils.ClassCompressImage;
import com.smartmux.foto.utils.Constant;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainEditorActivity extends FragmentActivity implements
		OnClickListener, OnMenuItemClickListener {

	RecycleView_HorizontalListAdapter list_Adapter;
	private static RecyclerView bottom_listview;

	private static Bitmap bmp_main;
	private TouchImageView image;

	private ArrayList<ListData> mBottomListData = new ArrayList<ListData>();

	String strtext = "";
	Animation downDrawerOpen, downDrawerClose = null;
	ImageView imageview_logo, imageview_done, imageview_close;
	TextView textview_title, textview_done;
	RelativeLayout footer = null;
	private FragmentManager fragmentManager;
	private DialogFragment mMenuDialogFragment;
	ClassCompressImage compressPhoto;
	TouchImageView mTouchImageView;
	BitmapHolder mBitmapHolder = null;

	// In app purchase
	public boolean isPurchasedIC = false;
	private static final String TAG = "com.smartmux.foto";
	SharedPreferences sharedPreferences = null;
	public static final String PREFS_NAME = "MyPrefsFile";
	static final String ITEM_SKU = "com.smartmux.foto.upgrade";
//	static final String ITEM_SKU = "android.test.purchased";

	public BillingProcessor billingProcessor;
	private static final String LICENSE_KEY = null;
	public boolean readyToPurchase = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		purchaseInfo();

		setContentView(R.layout.activity_main_editor);

		mBitmapHolder = new BitmapHolder();

		compressPhoto = new ClassCompressImage(MainEditorActivity.this);

		// kanta
		fragmentManager = getFragmentManager();
		setHeader();
		setFooter();

		// ishtiaq
		image = (TouchImageView) findViewById(R.id.editor_imageView);
		// setPhoto();

		new LoadImage().execute();

	}

	public void setHeader() {

		footer = (RelativeLayout) findViewById(R.id.horizontalLayout);

		imageview_logo = (ImageView) findViewById(R.id.imageview_logo);
		imageview_done = (ImageView) findViewById(R.id.imageview_done);
		imageview_close = (ImageView) findViewById(R.id.imageview_close);
		textview_title = (TextView) findViewById(R.id.textview_header);
		textview_done = (TextView) findViewById(R.id.textview_done);

		imageview_logo.setOnClickListener(this);
		textview_done.setOnClickListener(this);

		initMenuFragment();

	}

	public void setFooter() {

		downDrawerOpen = AnimationUtils.loadAnimation(this, R.anim.bottom_up);
		downDrawerClose = AnimationUtils
				.loadAnimation(this, R.anim.bottom_down);

		mBottomListData = AddItems.getFirstListItems();

		bottom_listview = (RecyclerView) findViewById(R.id.horizontal_listview);
		bottom_listview.startAnimation(downDrawerOpen);
		bottom_listview.setHasFixedSize(true);
		bottom_listview.setLayoutManager(new LinearLayoutManager(this,
				LinearLayoutManager.HORIZONTAL, false));

		list_Adapter = new RecycleView_HorizontalListAdapter(this,
				mBottomListData, isPurchasedIC);
		bottom_listview.setAdapter(list_Adapter);// set adapter on
													// recyclerview
		// list_Adapter.notifyDataSetChanged();

		list_Adapter.SetOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(View view, int position) {

				if (isPurchasedIC) {

					footer.startAnimation(downDrawerClose);
					footer.setVisibility(View.INVISIBLE);
					setItem(mBottomListData.get(position).getMid());

				} else {

					if (position == 2 || position == 9 || position == 10) {
						purchaseDialog();
					} else {

						footer.startAnimation(downDrawerClose);
						footer.setVisibility(View.INVISIBLE);
						setItem(mBottomListData.get(position).getMid());
					}

				}
			}
		});

	}

	public class LoadImage extends AsyncTask<Void, Void, Void> {

		private ProgressDialog progress = null;

		public LoadImage() {
		}

		@Override
		protected void onPreExecute() {

			super.onPreExecute();
			progress = ProgressDialog.show(MainEditorActivity.this, null,
					"Photo Loading");

		}

		@Override
		protected Void doInBackground(Void... params) {
			String path = getIntent().getExtras().getString("path");

			bmp_main = compressPhoto.compressImage(path);

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			mBitmapHolder.setBm(bmp_main);
			image.setImageBitmap(bmp_main);
			progress.dismiss();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (billingProcessor != null) {
			billingProcessor.release();
		}

		if (bmp_main != null) {

			bmp_main.recycle();
			bmp_main = null;
		}

		mBitmapHolder.drop();
		Runtime.getRuntime().gc();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.imageview_logo:
			showLogoDialog();
			break;

		case R.id.textview_done:

			if (fragmentManager
					.findFragmentByTag(ContextMenuDialogFragment.TAG) == null) {
				mMenuDialogFragment.show(fragmentManager,
						ContextMenuDialogFragment.TAG);
			}

			break;

		default:
			break;
		}
	}

	protected void showLogoDialog() {

		// get prompts.xml view
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		View promptView = layoutInflater.inflate(R.layout.logo_dialog, null);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setView(promptView);

		// setup a dialog window
		alertDialogBuilder.setCancelable(false);
		// Add the buttons
		alertDialogBuilder.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				});

		alertDialogBuilder.setPositiveButton(R.string.yes,
				new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog, int id) {

						Intent i = new Intent(MainEditorActivity.this,
								ImagePicker.class);
						startActivity(i);
						finish();
						dialog.dismiss();
					}
				});

		// create an alert dialog
		final AlertDialog alert = alertDialogBuilder.create();

		alert.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface arg0) {

				Typeface tf = Typeface.createFromAsset(getApplicationContext()
						.getAssets(), "fonts/" + "SinkinSans-300Light.otf");
				alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTypeface(tf);
				alert.getButton(AlertDialog.BUTTON_POSITIVE).setTypeface(tf);

				alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
						Color.BLUE);

				alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
						Color.BLUE);
			}
		});
		alert.show();
	}

	private void setItem(int id) {

		if (id == 0) {

			FragmentFilter frag = new FragmentFilter();
			replaceFrag(frag, Constant.FILTER);

		} else if (id == 1) {

			FragmentFrame frag = new FragmentFrame();
			replaceFrag(frag, Constant.FRAME);

		} else if (id == 2) {

			FragmentEffect frag = new FragmentEffect();
			replaceFrag(frag, Constant.EFFECT);

		} else if (id == 3) {

			FragmentBlur frag = new FragmentBlur();
			replaceFrag(frag, Constant.BLUR);

		}
		if (id == 4) {

			FragmentAdjustment frag = new FragmentAdjustment();
			replaceFrag(frag, Constant.ADJUST);

		} else if (id == 5) {

			FragmentRotate frag = new FragmentRotate();
			replaceFrag(frag, Constant.ROTATE);

		} else if (id == 6) {

			FragmentDraw frag = new FragmentDraw();
			replaceFrag(frag, Constant.DRAW);

		} else if (id == 7) {

			FragmentCrop frag = new FragmentCrop();
			replaceFrag(frag, Constant.CROP);

		} else if (id == 8) {

			FragmentResize frag = new FragmentResize();
			replaceFrag(frag, Constant.RESIZE);

		} else if (id == 9) {

			FragmentSticker frag = new FragmentSticker();
			replaceFrag(frag, Constant.STICKER);

		} else if (id == 10) {

			FragmentText frag = new FragmentText();
			replaceFrag(frag, Constant.TEXT);

		}
	}

	private void replaceFrag(Fragment frag, String tag) {

		FragmentTransaction fragTransaction = getFragmentManager()
				.beginTransaction();
		fragTransaction.replace(R.id.fragment_main, frag, tag);
		fragTransaction.addToBackStack(tag);
		fragTransaction.commit();

	}

	private void initMenuFragment() {
		MenuParams menuParams = new MenuParams();
		menuParams.setActionBarSize((int) getResources().getDimension(
				R.dimen.list_item_image_wt));
		menuParams.setMenuObjects(getMenuObjects());
		menuParams.setClosableOutside(false);
		mMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams);
	}

	@SuppressLint("ResourceAsColor")
	private List<MenuObject> getMenuObjects() {

		List<MenuObject> menuObjects = new ArrayList<MenuObject>();

		MenuObject close = new MenuObject();
		close.setResource(R.drawable.menu_icon_close);
		close.setBgColor(R.color.black);

		MenuObject facebook = new MenuObject("FACEBOOK");
		facebook.setResource(R.drawable.fb_button);
		facebook.setBgColor(R.color.black);

		MenuObject twitter = new MenuObject("TWITTER");
		Bitmap b = BitmapFactory.decodeResource(getResources(),
				R.drawable.twitter_button);
		twitter.setBitmap(b);
		twitter.setBgColor(R.color.black);

		MenuObject email = new MenuObject("EMAIL");
		BitmapDrawable bd = new BitmapDrawable(getResources(),
				BitmapFactory.decodeResource(getResources(),
						R.drawable.email_button));
		email.setDrawable(bd);
		email.setBgColor(R.color.black);

		MenuObject save = new MenuObject("SAVE");
		save.setResource(R.drawable.save_button);
		save.setBgColor(R.color.black);

		menuObjects.add(close);
		menuObjects.add(facebook);
		// menuObjects.add(twitter);
		menuObjects.add(email);
		menuObjects.add(save);
		return menuObjects;
	}

	@Override
	public void onBackPressed() {

		FragmentTransparent test = (FragmentTransparent) fragmentManager
				.findFragmentByTag(Constant.MAIN);
		if (test != null && test.isVisible()) {
			showLogoDialog();
		} else {

			addMainFragment();
		}

	}

	private void addMainFragment() {
		Bundle bundle = new Bundle();
		bundle.putString("message", "From Activity");

		FragmentTransparent frag = new FragmentTransparent();
		frag.setArguments(bundle);
		replaceFrag(frag, Constant.MAIN);
	}

	public void removeCurrentFragment() {
		FragmentTransaction transaction = fragmentManager.beginTransaction();

		Fragment currentFrag = fragmentManager
				.findFragmentById(R.id.fragment_main);

		@SuppressWarnings("unused")
		String fragName = "NONE";

		if (currentFrag != null)
			fragName = currentFrag.getClass().getSimpleName();

		if (currentFrag != null)
			transaction.remove(currentFrag);

		transaction.commit();

	}

	@Override
	public void onMenuItemClick(View clickedView, int position) {

		switch (position) {

		case 1: // FACEBOOK
//			ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//			NetworkInfo net = cm.getActiveNetworkInfo();
//			if (net == null) {
//				Toast.makeText(getApplicationContext(), R.string.net_con, 2000)
//						.show();
//
//			} else {
//
//				Handler h = new Handler();
//				h.postDelayed(new Runnable() {
//					public void run() {
//
//						Intent intent = new Intent(getApplicationContext(),
//								SMFBShareActivity.class);
//						startActivity(intent);
//						overridePendingTransition(R.anim.push_up_in,
//								R.anim.push_up_out);
//
//					}
//				}, 1000);
//			}
//			break;

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

                            new FacebookTask().execute();

                            // saveInTampFile();
                            // shareImage();

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

		// case 2: // TWITTER
		//
		// Intent intent = new Intent(MainEditorActivity.this,
		// MainTwitterActivity.class);
		// startActivity(intent);
		// overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
		//
		// break;

		case 2: // EMAIL

			ConnectivityManager connection = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo network = connection.getActiveNetworkInfo();
			if (network == null) {
				Toast.makeText(getApplicationContext(), R.string.net_con, Toast.LENGTH_SHORT)
						.show();

			} else {

				emailService();

			}
			break;

		case 3: // SAVE
			Boolean isSDPresent = Environment
					.getExternalStorageState().equals(
							Environment.MEDIA_MOUNTED);

			if (isSDPresent) {

				// save();
				showSaveDialog();

			} else {
				Toast.makeText(getApplicationContext(),
						"sd card not available", Toast.LENGTH_SHORT).show();
			}
			break;

		default:
			break;
		}

	}

	private void showSaveDialog() {

		// get prompts.xml view
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		View promptView = layoutInflater.inflate(R.layout.save_dialog, null);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setView(promptView);

		alertDialogBuilder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog, int id) {

						save();
						dialog.dismiss();
					}
				});

		// create an alert dialog
		final AlertDialog alert = alertDialogBuilder.create();

		alert.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface arg0) {

				Typeface tf = Typeface.createFromAsset(getApplicationContext()
						.getAssets(), "fonts/" + "SinkinSans-300Light.otf");
				alert.getButton(AlertDialog.BUTTON_POSITIVE).setTypeface(tf);
				alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
						Color.BLUE);
			}
		});
		alert.show();

	}

	private void getBitmap() {

		image.setDrawingCacheEnabled(true);
		bmp_main = Bitmap.createBitmap(image.getDrawingCache());
		image.setDrawingCacheEnabled(false);

	}

	private void save() {

		String root = Environment.getExternalStorageDirectory().toString()
				+ "/Foto/";

		File directory = new File(root);
		directory.mkdirs();

		long newdate = new Date().getTime();
		String fileName = newdate + ".png";
		File m_imagePath = new File(directory + "/", fileName);
		getBitmap();
		FileOutputStream fOut;
		try {
			fOut = new FileOutputStream(m_imagePath);
			mBitmapHolder.getBm().compress(Bitmap.CompressFormat.PNG, 90, fOut);
			fOut.flush();
			fOut.close();

			if (android.os.Build.VERSION.SDK_INT > 18) {

				sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
						Uri.parse("file://"
								+ Environment.getExternalStorageDirectory())));

			} else {

				sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
						Uri.parse("file://"
								+ Environment.getExternalStorageDirectory())));

			}

			Toast.makeText(MainEditorActivity.this, "Saved", Toast.LENGTH_SHORT)
					.show();
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
			mBitmapHolder.getBm().compress(Bitmap.CompressFormat.PNG, 100,
					stream);
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

	private void purchaseInfo() {
		
		billingProcessor = new BillingProcessor(this, LICENSE_KEY,
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

						if (billingProcessor.isPurchased(ITEM_SKU)) {
							
							sharedPreferences = getSharedPreferences(PREFS_NAME, 0);
							SharedPreferences.Editor editor = sharedPreferences.edit();
							editor.putBoolean("isUpgrade", true);
							editor.commit();
							
							isPurchasedIC = sharedPreferences.getBoolean(
									"isUpgrade", false);

						}

						if (isPurchasedIC) {

							if (list_Adapter != null) {
								list_Adapter.setPurchase(isPurchasedIC);
								list_Adapter.notifyDataSetChanged();
							}

						}
					}

					@Override
					public void onProductPurchased(String productId,
							TransactionDetails details) {

						doButtonUnlock();

					}
				});

		sharedPreferences = getSharedPreferences(PREFS_NAME, 0);
		isPurchasedIC = sharedPreferences.getBoolean("isUpgrade", false);
		// isPurchasedIC =true;

	}

	private void doButtonUnlock() {

		sharedPreferences = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putBoolean("isUpgrade", true);
		editor.commit();
		
		
		isPurchasedIC = sharedPreferences.getBoolean(
				"isUpgrade", false);

	

	if (isPurchasedIC) {

		if (list_Adapter != null) {
			list_Adapter.setPurchase(isPurchasedIC);
			list_Adapter.notifyDataSetChanged();
		}

	}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (!billingProcessor.handleActivityResult(requestCode, resultCode,
				data))
			;
	}

	private void purchaseDialog() {
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		View promptView = layoutInflater
				.inflate(R.layout.purchase_dialog, null);
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setView(promptView);

		alertDialog.setPositiveButton("YES",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						
						if (!readyToPurchase) {
							// showToast("Billing not initialized.");
							return;
						}


						billingProcessor.purchase(MainEditorActivity.this,
								ITEM_SKU);

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

    File m_imagePath = null;
    private void saveInTampFile() {

        String root = Environment.getExternalStorageDirectory().toString()
                + "/Photo_Collage/Edit_Photo";

        File directory = new File(root);
        directory.mkdirs();

        long newdate = new Date().getTime();
        String fileName = newdate + ".png";
        m_imagePath = new File(directory + "/", fileName);
        FileOutputStream fOut;
        try {
            fOut = new FileOutputStream(m_imagePath);
            mBitmapHolder.getBm().compress(Bitmap.CompressFormat.PNG, 90, fOut);
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
    }

    private void shareImage() {

        Intent share = new Intent(Intent.ACTION_SEND);

        // If you want to share a png image only, you can do:
        // setType("image/png"); OR for jpeg: setType("image/jpeg");
        share.setType("image/png");

        Uri uri = Uri.fromFile(m_imagePath);
        share.putExtra(Intent.EXTRA_STREAM, uri);
        share.setPackage(Constant.FB_PACKAGE);

        startActivity(share);

    }

    public class FacebookTask extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progress = null;

        public FacebookTask() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = ProgressDialog.show(MainEditorActivity.this, null,
                    "processing");

        }

        @Override
        protected Void doInBackground(Void... params) {
            saveInTampFile();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            super.onPostExecute(result);
//            topBar.setVisibility(View.INVISIBLE);
            progress.dismiss();
            shareImage();

        }
    }


}