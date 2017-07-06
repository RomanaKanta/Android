package com.smartmux.photocutter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.smartmux.billing.v3.BillingProcessor;
import com.smartmux.billing.v3.TransactionDetails;
import com.smartmux.photocutter.cropper.CropImageView;
import com.smartmux.photocutter.dialogfragment.CustomCropShapeDialog;
import com.smartmux.photocutter.dialogfragment.EditDialog;
import com.smartmux.photocutter.dialogfragment.FilterDialog;
import com.smartmux.photocutter.dialogfragment.PhotoSelectionDialog;
import com.smartmux.photocutter.modelclass.BitmapHolder;
import com.smartmux.photocutter.util.ShapeUtil;
import com.smartmux.photocutter.utils.ClassCompressImage;
import com.smartmux.photocutter.utils.PhotoCutterConstants;
import com.smartmux.photocutter.widget.SA_ArbitraryCropDraw;
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

import cn.Ragnarok.BitmapFilter;

@SuppressLint({ "ResourceAsColor", "NewApi" })
public class MainActivity extends FragmentActivity implements OnClickListener,
		CropImageView.OnGetCroppedImageCompleteListener,
		OnMenuItemClickListener {

	ImageView image_crop_shape, image_filer, image_custom,
			image_crop_close, image_new, image_edit, image_background;

	TextView textViewDone;

	ClassCompressImage compressPhoto;
	BitmapHolder mBitmapHolder = null;

	Bitmap bmp_main, crop_bitmap;
	CropImageView crop_image;
	ImageView main_image;
	boolean perform = false;
	boolean special = false;
	View view;
	FrameLayout flayout;
	SA_ArbitraryCropDraw cropDraw;

    //dialog fragment
	private FragmentManager fragmentManager;
	private DialogFragment mMenuDialogFragment;
    private CustomCropShapeDialog mCustomCropShapeDialog;
    private EditDialog mEditDialog;
    private FilterDialog mFilterDialog;

	// In app purchase
	public boolean isPurchasedIC = false;
	SharedPreferences sharedPreferences = null;

	public BillingProcessor billingProcessor;

	public boolean readyToPurchase = false;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (!billingProcessor.handleActivityResult(requestCode, resultCode,
				data))
			;

        if (requestCode == PhotoCutterConstants.GALLERY_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {

                Uri imageFileUri = data.getData();

                if (imageFileUri != null) {

                    path = getPath(imageFileUri);

//                    if(contant.equals(PhotoCutterConstants.IMAGE_CROP)) {
//                    Intent i = new Intent(this, MainActivity.class);
//                    i.putExtra(PhotoCutterConstants.IMAGE_PATH, path);
//                    startActivity(i);
//                        finish();
//                    }
//                    if(contant.equals(PhotoCutterConstants.IMAGE_BACKGROUND)) {
                        Intent i = new Intent(this, BackgroundSettingActivity.class);
                        i.putExtra(PhotoCutterConstants.IMAGE_PATH, path);
                        startActivity(i);
//                        finish();
//                    }

                }
            }
        }

        else if (requestCode == PhotoCutterConstants.ACTION_TAKE_PHOTO_B) {

            if (resultCode == RESULT_OK) {
                handleBigCameraPhoto();
            }

        }
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (billingProcessor != null)
			billingProcessor.release();
	}

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

		billingProcessor = new BillingProcessor(this, PhotoCutterConstants.LICENSE_KEY,
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
						
						for(String sku : billingProcessor.listOwnedProducts()){
							
							Log.d("item ", sku);
						}
						
						if(billingProcessor.isPurchased(PhotoCutterConstants.ITEM_SKU)){
							

							isPurchasedIC = sharedPreferences.getBoolean(PhotoCutterConstants.IS_UPGRADE, false);

							if (isPurchasedIC) {
								special = true;

							} else {
								special = false;
							}

						}

					}

					@Override
					public void onProductPurchased(String productId,
							TransactionDetails details) {

						doButtonUnlock();
						
					}
				});

		sharedPreferences = getSharedPreferences(PhotoCutterConstants.PREFS_NAME, 0);
		isPurchasedIC = sharedPreferences.getBoolean(PhotoCutterConstants.IS_UPGRADE, false);
//		isPurchasedIC =true;
		if (isPurchasedIC) {
			special = true;

		} else {
			special = false;
		}

		setContentView(R.layout.photo_cutter);

		fragmentManager = getSupportFragmentManager();
		mBitmapHolder = new BitmapHolder();
		compressPhoto = new ClassCompressImage(MainActivity.this);

		init();
        initAllDialog();
		setPhoto();
		initMenuFragment();
	}

    private void initAllDialog() {
       mCustomCropShapeDialog = new CustomCropShapeDialog();
        mEditDialog = new EditDialog();
        mFilterDialog = new FilterDialog();
    }

	private void init() {

		flayout = (FrameLayout) findViewById(R.id.crop_container);
		cropDraw = new SA_ArbitraryCropDraw(this);

		textViewDone = (TextView) findViewById(R.id.TextView_done);

		image_crop_close = (ImageView) findViewById(R.id.ImageView_crop_close);
		image_new = (ImageView) findViewById(R.id.ImageView_logo);

		image_crop_shape = (ImageView) findViewById(R.id.ImageView_crop_shape);
		image_filer = (ImageView) findViewById(R.id.ImageView_filter);
		image_custom = (ImageView) findViewById(R.id.ImageView_free_hand);

		image_edit = (ImageView) findViewById(R.id.ImageView_edit);
        image_background = (ImageView) findViewById(R.id.ImageView_background);

		if (special) {
			image_custom.setImageResource(R.drawable.custom_crop);
		} else {
			image_custom.setImageResource(R.drawable.custom_crop_locked);
		}

		image_new.setOnClickListener(this);
		image_crop_close.setOnClickListener(this);
		textViewDone.setOnClickListener(this);
		image_crop_shape.setOnClickListener(this);
		image_filer.setOnClickListener(this);
		image_custom.setOnClickListener(this);

		image_edit.setOnClickListener(this);
        image_background.setOnClickListener(this);

	}

	public void setPhoto() {

		crop_image = (CropImageView) findViewById(R.id.crop_imageview);
		main_image = (ImageView) findViewById(R.id.main_imageview);

        if(getIntent().hasExtra(PhotoCutterConstants.IMAGE_PATH)) {
            String path = getIntent().getExtras().getString(PhotoCutterConstants.IMAGE_PATH);

            if(path!=null) {
                bmp_main = compressPhoto.compressImage(path);

                mBitmapHolder.setBm(bmp_main);
            }
        }

        if(mBitmapHolder.getBm()!=null) {
            main_image.setImageBitmap(mBitmapHolder.getBm());
        }

	}

	private void initMenuFragment() {
		MenuParams menuParams = new MenuParams();
		menuParams.setActionBarSize((int) getResources().getDimension(
				R.dimen.list_item_image_wt));
		menuParams.setMenuObjects(getMenuObjects());
		menuParams.setClosableOutside(false);
		mMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams);
	}

	private List<MenuObject> getMenuObjects() {

		List<MenuObject> menuObjects = new ArrayList<MenuObject>();

		MenuObject close = new MenuObject();
		close.setResource(R.drawable.menu_icon_close);
		close.setBgColor(R.color.black);

		MenuObject email = new MenuObject("SHARE");
		BitmapDrawable bd = new BitmapDrawable(getResources(),
				BitmapFactory.decodeResource(getResources(),
						R.drawable.email_button));
		email.setDrawable(bd);
		email.setBgColor(R.color.black);

		MenuObject save = new MenuObject("SAVE");
		save.setResource(R.drawable.menu_icon_save);
		save.setBgColor(R.color.black);

		menuObjects.add(close);
		menuObjects.add(email);
		menuObjects.add(save);
		return menuObjects;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.ImageView_logo:
            exitDialog();

			break;

		case R.id.ImageView_crop_close:

			flayout.setVisibility(View.GONE);
			flayout.removeAllViews();
			crop_image.setVisibility(View.GONE);
			main_image.setVisibility(View.VISIBLE);
//			setIconColorFilter();
			setDoneTag();

			break;

		case R.id.TextView_done:
			if (textViewDone.getTag().equals(PhotoCutterConstants.CROP)) {

				if (perform) {

					if (crop_image.getVisibility() == View.VISIBLE) {

						crop_image.getCroppedImageAsync(
								crop_image.getCropShape(), 0, 0);

					} else {

						generateBitmap();
					}
				}

				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {

						flayout.setVisibility(View.GONE);
						flayout.removeAllViews();

						crop_image.setVisibility(View.GONE);

						main_image.setImageBitmap(mBitmapHolder.getBm());
						main_image.setVisibility(View.VISIBLE);
						setDoneTag();
					}
				}, 500);

//				setIconColorFilter();

			} else {

				if (fragmentManager
						.findFragmentByTag(ContextMenuDialogFragment.TAG) == null) {
					mMenuDialogFragment.show(fragmentManager,
							ContextMenuDialogFragment.TAG);
				}

			}
			break;
//
//		case R.id.ImageView_oval:
//
//			if (editLayer.getVisibility() == View.VISIBLE) {
//				openEditMenu();
//			}
//			setIconColorFilter();
//
//			image_oval.setColorFilter(R.color.selected);
//
//			setCropTag();
//			perform = true;
//			if (main_image.getVisibility() == View.VISIBLE
//					|| flayout.getVisibility() == View.VISIBLE) {
//				flayout.setVisibility(View.GONE);
//				flayout.removeAllViews();
//
//				crop_image.setImageBitmap(mBitmapHolder.getBm());
//				crop_image.setVisibility(View.VISIBLE);
//				main_image.setVisibility(View.GONE);
//
//			}
//
//			crop_image.setCropShape(CropImageView.CropShape.OVAL);
////			hexa = false;
//
//			break;

		case R.id.ImageView_crop_shape:

//            setIconColorFilter();
//            image_crop_shape.setColorFilter(R.color.selected);

            mCustomCropShapeDialog.show(fragmentManager, "Crop_Dialog_Fragment");



//			if (editLayer.getVisibility() == View.VISIBLE) {
//				openEditMenu();
//			}
//			setIconColorFilter();
//
//			image_crop_shape.setColorFilter(R.color.selected);
//
//			setCropTag();
//			perform = true;
//			if (main_image.getVisibility() == View.VISIBLE
//					|| flayout.getVisibility() == View.VISIBLE) {
//				flayout.setVisibility(View.GONE);
//				flayout.removeAllViews();
//
//				crop_image.setImageBitmap(mBitmapHolder.getBm());
//				crop_image.setVisibility(View.VISIBLE);
//				main_image.setVisibility(View.GONE);
//
//			}
//
//			crop_image.setCropShape(CropImageView.CropShape.RECTANGLE);
////			hexa = false;

			break;

		case R.id.ImageView_filter:

            if (textViewDone.getTag().equals(PhotoCutterConstants.DONE)) {

//                setIconColorFilter();
//                image_filer.setColorFilter(R.color.selected);
                mFilterDialog.show(fragmentManager, "Filter_Dialog_Fragment");
//				openEditMenu();
            } else {
                Toast.makeText(MainActivity.this, R.string.edit_toast,
                        Toast.LENGTH_SHORT).show();
            }

//			if (editLayer.getVisibility() == View.VISIBLE) {
//				openEditMenu();
//			}
//			setIconColorFilter();
//
//			image_filer.setColorFilter(R.color.selected);
//
//			perform = true;
//			setCropTag();
//			if (main_image.getVisibility() == View.VISIBLE
//					|| flayout.getVisibility() == View.VISIBLE) {
//				flayout.setVisibility(View.GONE);
//				flayout.removeAllViews();
//
//				crop_image.setImageBitmap(mBitmapHolder.getBm());
//				crop_image.setVisibility(View.VISIBLE);
//				main_image.setVisibility(View.GONE);
//
//			}
//
//			crop_image.setCropShape(CropImageView.CropShape.HEXAGON);
////			hexa = true;

			break;

//            case R.id.ImageView_octagon:
//
//                if (editLayer.getVisibility() == View.VISIBLE) {
//                    openEditMenu();
//                }
//                setIconColorFilter();
//
//                image_filer.setColorFilter(R.color.selected);
//
//                perform = true;
//                setCropTag();
//                if (main_image.getVisibility() == View.VISIBLE
//                        || flayout.getVisibility() == View.VISIBLE) {
//                    flayout.setVisibility(View.GONE);
//                    flayout.removeAllViews();
//
//                    crop_image.setImageBitmap(mBitmapHolder.getBm());
//                    crop_image.setVisibility(View.VISIBLE);
//                    main_image.setVisibility(View.GONE);
//
//                }
//
//                crop_image.setCropShape(CropImageView.CropShape.OCTAGON);
////			hexa = true;
//
//                break;
//
//            case R.id.ImageView_pentagon:
//
//                if (editLayer.getVisibility() == View.VISIBLE) {
//                    openEditMenu();
//                }
//                setIconColorFilter();
//
//                image_filer.setColorFilter(R.color.selected);
//
//                perform = true;
//                setCropTag();
//                if (main_image.getVisibility() == View.VISIBLE
//                        || flayout.getVisibility() == View.VISIBLE) {
//                    flayout.setVisibility(View.GONE);
//                    flayout.removeAllViews();
//
//                    crop_image.setImageBitmap(mBitmapHolder.getBm());
//                    crop_image.setVisibility(View.VISIBLE);
//                    main_image.setVisibility(View.GONE);
//
//                }
//
//                crop_image.setCropShape(CropImageView.CropShape.PENTAGON);
////			hexa = true;
//
//                break;
//
//            case R.id.ImageView_diamond:
//
//                if (editLayer.getVisibility() == View.VISIBLE) {
//                    openEditMenu();
//                }
//                setIconColorFilter();
//
//                image_filer.setColorFilter(R.color.selected);
//
//                perform = true;
//                setCropTag();
//                if (main_image.getVisibility() == View.VISIBLE
//                        || flayout.getVisibility() == View.VISIBLE) {
//                    flayout.setVisibility(View.GONE);
//                    flayout.removeAllViews();
//
//                    crop_image.setImageBitmap(mBitmapHolder.getBm());
//                    crop_image.setVisibility(View.VISIBLE);
//                    main_image.setVisibility(View.GONE);
//
//                }
//
//                crop_image.setCropShape(CropImageView.CropShape.DIAMOND);
////			hexa = true;
//
//                break;

		case R.id.ImageView_edit:

			if (textViewDone.getTag().equals(PhotoCutterConstants.DONE)) {

                mEditDialog.show(fragmentManager, "Edit_Dialog_Fragment");

			} else {
				Toast.makeText(MainActivity.this, R.string.edit_toast,
						Toast.LENGTH_SHORT).show();
			}



			break;

            case R.id.ImageView_background:

                new PhotoSelectionDialog().show(fragmentManager,"Select");

//                            BlurBehind.getInstance().execute(MainActivity.this,
//                    new OnBlurCompleteListener() {
//                        @Override
//                        public void onBlurComplete() {
//                            Intent intent = new Intent(MainActivity.this,
//                                    BlurredActivity.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                            intent.putExtra(PhotoCutterConstants.CONTANT, PhotoCutterConstants.IMAGE_BACKGROUND);
//                            startActivity(intent);
//                        }
//                    });

                break;

		case R.id.ImageView_free_hand:
			
			isPurchasedIC = sharedPreferences.getBoolean(PhotoCutterConstants.IS_UPGRADE, false);
//			isPurchasedIC =true;
			
			
			if (isPurchasedIC) {
				special = true;
			} else {
				special = false;
			}


			if (special) {

				setCropTag();
				perform = true;
				if (crop_image.getVisibility() == View.VISIBLE
						|| main_image.getVisibility() == View.VISIBLE) {
					crop_image.setVisibility(View.GONE);
					main_image.setVisibility(View.GONE);

					flayout.setVisibility(View.VISIBLE);

					view = getLayoutInflater().inflate(R.layout.draw, flayout,
							false);
					cropDraw.setBitmap(mBitmapHolder.getBm());
					flayout.addView(view);

				}

			} else {

                purchaseDialog();

			}

			break;

		default:
			break;
		}
	}

	public void onEditMenuItemClick(int positon) {

		Bitmap main_bitmap = mBitmapHolder.getBm();

//		if (tips.equals(PhotoCutterConstants.ROTATE_LEFT)) {
        if(positon==0){
			Matrix rotateLeft = new Matrix();
			rotateLeft.preRotate(-90);
			Bitmap lSprite = Bitmap.createBitmap(main_bitmap, 0, 0,
					main_bitmap.getWidth(), main_bitmap.getHeight(),
					rotateLeft, true);

			main_bitmap = lSprite;
			main_image.setImageBitmap(main_bitmap);

			mBitmapHolder.setBm(main_bitmap);

		} else if(positon==1){
//        if (tips.equals(PhotoCutterConstants.ROTATE_RIGHT)) {
			Matrix rotateRight = new Matrix();
			rotateRight.preRotate(90);
			Bitmap rSprite = Bitmap.createBitmap(main_bitmap, 0, 0,
					main_bitmap.getWidth(), main_bitmap.getHeight(),
					rotateRight, true);

			main_bitmap = rSprite;
			main_image.setImageBitmap(main_bitmap);

			mBitmapHolder.setBm(main_bitmap);

		} else if(positon==2){
//        if (tips.equals(PhotoCutterConstants.FLIP)) {

			Bitmap alteredBitmap = Bitmap.createBitmap(main_bitmap.getWidth(),
					main_bitmap.getHeight(), main_bitmap.getConfig());
			Canvas canvas = new Canvas(alteredBitmap);
			Paint paint = new Paint();
			Matrix matrix = new Matrix();

			matrix.setScale(-1, 1);
			matrix.postTranslate(main_bitmap.getWidth(), 0);

			canvas.drawBitmap(main_bitmap, matrix, paint);

			main_bitmap = alteredBitmap;
			main_image.setImageBitmap(main_bitmap);

			mBitmapHolder.setBm(main_bitmap);
		}else{

        }
	}

	private void setCropTag() {

		image_crop_close.setVisibility(View.VISIBLE);
		image_new.setVisibility(View.GONE);

		textViewDone.setText("Crop");
		textViewDone.setTag(PhotoCutterConstants.CROP);
	}

	private void setDoneTag() {

		image_crop_close.setVisibility(View.GONE);
		image_new.setVisibility(View.VISIBLE);

		textViewDone.setText("Done");
		textViewDone.setTag(PhotoCutterConstants.DONE);
	}

	private void generateBitmap() {

		if (cropDraw.getLength() > 1) {

			crop_bitmap = ShapeUtil.getFreehandShape(getApplicationContext(),
					mBitmapHolder.getBm());
			mBitmapHolder.setBm(crop_bitmap);

		} else {
			// mBitmapHolder.setBm(main_bitmap);
		}

	}

	@Override
	public void onGetCroppedImageComplete(CropImageView view, Bitmap bitmap,
			Exception error) {
		if (error == null) {

//			if (hexa) {
//
//				mBitmapHolder.setBm(CustomShapeCrop
//						.getHexaShapeCroppedBitmap(bitmap));
//
//			} else {

				mBitmapHolder.setBm(bitmap);

//			}

		} else {
			Toast.makeText(getApplicationContext(),
					"Image crop failed: " + error.getMessage(),
					Toast.LENGTH_LONG).show();
		}

	}

	@Override
	protected void onStart() {
		super.onStart();
		crop_image.setOnGetCroppedImageCompleteListener(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		crop_image.setOnGetCroppedImageCompleteListener(null);
	}


	private void doButtonUnlock() {

		sharedPreferences = getSharedPreferences(PhotoCutterConstants.PREFS_NAME, 0);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putBoolean(PhotoCutterConstants.IS_UPGRADE, true);
		editor.commit();
		
		image_custom.setImageResource(R.drawable.custom_crop);

	}

	@Override
	public void onMenuItemClick(View clickedView, int position) {

		switch (position) {

		case 1: // EMAIL

			ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo net = cm.getActiveNetworkInfo();
			if (net == null) {
				Toast.makeText(getApplicationContext(), R.string.net_con, Toast.LENGTH_LONG)
						.show();

			} else {

				emailService();

			}
			break;

		case 2: // SAVE
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

	private void save() {

		String root = Environment.getExternalStorageDirectory().toString()
				+ "/Photo Cutter/";

		File directory = new File(root);
		directory.mkdirs();

		long newdate = new Date().getTime();
		String fileName = newdate + ".png";
		File m_imagePath = new File(directory + "/", fileName);
		// getBitmap();
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

			Toast.makeText(MainActivity.this, R.string.save_toast,
					Toast.LENGTH_SHORT).show();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
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

		String body = "PhotoCutter is the easiest to use picture cropping app in the store!"
				+ "<br>It's free, easy to use, just try it!"
				+ "<br><br><a href='https://play.google.com/store/apps/details?id=com.smartmux.photocutter'"
				+ " target='_blank'>https://play.google.com/store/apps/details?id=com.smartmux.photocutter</a> ";

		Intent emailIntent = new Intent(Intent.ACTION_SEND);
		emailIntent.setType("message/rfc822");
		emailIntent.setType("image/png");
		emailIntent
				.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
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

    @Override
    public void onBackPressed() {
        exitDialog();
    }

    private void exitDialog(){
        AlertDialog.Builder alert = new AlertDialog.Builder(
                new ContextThemeWrapper(MainActivity.this,
                        R.style.popup_theme));
        alert.setTitle("Photo Cutter");
        alert.setMessage("Do you want to start with new PHOTO?");

        alert.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

//                        Intent i = new Intent(MainActivity.this,
//                                ImagePicker.class);
//                        startActivity(i);

                        finish();

                        dialog.cancel();
                    }

                });

        alert.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alert.show();
    }

    private void purchaseDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                new ContextThemeWrapper(MainActivity.this,
                        R.style.popup_theme));
        alertDialog.setTitle(getString(R.string.purch_alart_title));
        alertDialog.setMessage(getString(R.string.purch_alart_msg));

        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {

								billingProcessor.purchase(MainActivity.this,
                                        PhotoCutterConstants.ITEM_SKU);

//                        doButtonUnlock();

                        dialog.cancel();

                    }


                });

        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }

    public void setCropShape(int position){

        setCropTag();
        perform = true;
        if (main_image.getVisibility() == View.VISIBLE
                || flayout.getVisibility() == View.VISIBLE) {
            flayout.setVisibility(View.GONE);
            flayout.removeAllViews();

            crop_image.setImageBitmap(mBitmapHolder.getBm());
            crop_image.setVisibility(View.VISIBLE);
            main_image.setVisibility(View.GONE);

        }

        if(position==0){

			crop_image.setCropShape(CropImageView.CropShape.RECTANGLE);

        }else if(position==1){

            crop_image.setCropShape(CropImageView.CropShape.OVAL);

        }else if(position==2){

            crop_image.setCropShape(CropImageView.CropShape.HEXAGON);

        }else if(position==3){

            crop_image.setCropShape(CropImageView.CropShape.OCTAGON);

        }else if(position==4){

            crop_image.setCropShape(CropImageView.CropShape.PENTAGON);

        }else if(position==5){

            crop_image.setCropShape(CropImageView.CropShape.DIAMOND);

        }else{

            crop_image.setCropShape(CropImageView.CropShape.RECTANGLE);

        }

    }

    Bitmap changeBitmap;

    private void applyStyle(int styleNo) {
        switch (styleNo) {

            case 0:

                changeBitmap = mBitmapHolder.getBm();
                break;

            case BitmapFilter.LIGHT_STYLE:
                int width = mBitmapHolder.getBm().getWidth();
                int height = mBitmapHolder.getBm().getHeight();
                changeBitmap = BitmapFilter.changeStyle(mBitmapHolder.getBm(),
                        BitmapFilter.LIGHT_STYLE, width / 3, height / 2, width / 2);
                break;

            case BitmapFilter.NEON_STYLE:
                changeBitmap = BitmapFilter.changeStyle(mBitmapHolder.getBm(),
                        BitmapFilter.NEON_STYLE, 200, 100, 50);
                break;

            default:
                changeBitmap = BitmapFilter.changeStyle(mBitmapHolder.getBm(), styleNo);
                break;
        }
    }

    public class ApplyFilter extends AsyncTask<Void, Void, Void> {

        private ProgressDialog progress = null;
        int position;

        public ApplyFilter(int pos) {

            position = pos;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            progress = ProgressDialog.show(MainActivity.this, null,
                    "Filtering");

        }

        @Override
        protected Void doInBackground(Void... params) {
            applyStyle(position);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progress.dismiss();

            main_image.setImageBitmap(changeBitmap);

        }
    }


    private String path;
    public String getPath(Uri uri) {
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, filePathColumn, null,
                null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        return cursor.getString(columnIndex);
    }

    private void handleBigCameraPhoto() {

        if (PhotoCutterConstants.mCurrentPhotoPath != null) {
                Intent i = new Intent(this, BackgroundSettingActivity.class);
                i.putExtra(PhotoCutterConstants.IMAGE_PATH, PhotoCutterConstants.mCurrentPhotoPath);
                startActivity(i);
            PhotoCutterConstants.mCurrentPhotoPath = null;
        }

    }

}
