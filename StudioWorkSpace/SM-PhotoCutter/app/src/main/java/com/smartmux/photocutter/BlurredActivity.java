package com.smartmux.photocutter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

import com.faradaj.blurbehind.BlurBehind;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;
import com.smartmux.photocutter.utils.AlbumStorageDirFactory;
import com.smartmux.photocutter.utils.BaseAlbumDirFactory;
import com.smartmux.photocutter.utils.FroyoAlbumDirFactory;
import com.smartmux.photocutter.utils.PhotoCutterConstants;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressLint("NewApi")
public class BlurredActivity extends Activity {

	final int GALLERY_REQUEST = 100;
	private String path;
	private static final int ACTION_TAKE_PHOTO_B = 1;
	private String mCurrentPhotoPath;
	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";
	private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
	RelativeLayout pickFromGallery, captureByCamera;
	Boolean isButtonAppeared = true;
    private String contant;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_blurre);

        if(getIntent().hasExtra(PhotoCutterConstants.CONTANT)) {

            contant = getIntent().getExtras().getString(PhotoCutterConstants.CONTANT);
            // blur background
            BlurBehind.getInstance().withAlpha(100).setBackground(this);

            pickFromGallery = (RelativeLayout) findViewById(R.id.pick);
            captureByCamera = (RelativeLayout) findViewById(R.id.capture);
            bottomToMiddleAnimation();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
                mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
            } else {
                mAlbumStorageDirFactory = new BaseAlbumDirFactory();
            }

            this.findViewById(R.id.transparentLayout).setOnClickListener(
                    new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            middleToTopAnimation();
                            finish();
                        }
                    });

            captureByCamera.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {

                    goneToUpAnimation(captureByCamera);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            dispatchTakePictureIntent(ACTION_TAKE_PHOTO_B);

                        }
                    }, 300);

                }
            });
            pickFromGallery.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {

                    goneToUpAnimation(pickFromGallery);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            importGallery();

                        }
                    }, 300);
                }
            });

        }else{
            finish();
        }
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (isButtonAppeared) {

			bottomToMiddleAnimation();

		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();

		middleToTopAnimation();
		finish();

	}

	private void importGallery() {

		Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		photoPickerIntent.setType("image/*");
		startActivityForResult(photoPickerIntent, GALLERY_REQUEST);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == GALLERY_REQUEST) {
			if (resultCode == Activity.RESULT_OK) {

				Uri imageFileUri = data.getData();

				if (imageFileUri != null) {

					path = getPath(imageFileUri);

                    if(contant.equals(PhotoCutterConstants.IMAGE_CROP)) {
                        Intent i = new Intent(this, MainActivity.class);
                        i.putExtra(PhotoCutterConstants.IMAGE_PATH, path);
                        startActivity(i);
                        finish();
                    }
                    if(contant.equals(PhotoCutterConstants.IMAGE_BACKGROUND)) {
                        Intent i = new Intent(this, BackgroundSettingActivity.class);
                        i.putExtra(PhotoCutterConstants.IMAGE_PATH, path);
                        startActivity(i);
                        finish();
                    }

				}
			}
		}

		else if (requestCode == ACTION_TAKE_PHOTO_B) {

			if (resultCode == RESULT_OK) {
				handleBigCameraPhoto();
			}

		}
	}

	private void handleBigCameraPhoto() {

		if (mCurrentPhotoPath != null) {

            if(contant.equals(PhotoCutterConstants.IMAGE_CROP)) {
                Intent i = new Intent(this, MainActivity.class);
                i.putExtra(PhotoCutterConstants.IMAGE_PATH, mCurrentPhotoPath);
                startActivity(i);
                mCurrentPhotoPath = null;
                finish();
            }
            if(contant.equals(PhotoCutterConstants.IMAGE_BACKGROUND)) {
                Intent i = new Intent(this, BackgroundSettingActivity.class);
                i.putExtra(PhotoCutterConstants.IMAGE_PATH, mCurrentPhotoPath);
                startActivity(i);
                mCurrentPhotoPath = null;
                finish();
            }

		}

	}

	private void dispatchTakePictureIntent(int actionCode) {

		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		switch (actionCode) {
		case ACTION_TAKE_PHOTO_B:
			File f = null;

			try {
				f = setUpPhotoFile();
				mCurrentPhotoPath = f.getAbsolutePath();
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
						Uri.fromFile(f));
			} catch (IOException e) {
				e.printStackTrace();
				f = null;
				mCurrentPhotoPath = null;
			}
			break;

		default:
			break;
		} // switch

		startActivityForResult(takePictureIntent, actionCode);
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

	public String getPath(Uri uri) {
		String[] filePathColumn = { MediaStore.Images.Media.DATA };
		Cursor cursor = getContentResolver().query(uri, filePathColumn, null,
				null, null);
		cursor.moveToFirst();
		int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		return cursor.getString(columnIndex);
	}

	public void middleToTopAnimation() {

		ObjectAnimator camerabutton = ObjectAnimator.ofFloat(
				getApplicationContext(), "y", 660, -300);

		// //set the view as target

		camerabutton.setDuration(300);
		camerabutton.setTarget(captureByCamera);
		// //start the animation
		camerabutton.start();

		ObjectAnimator galleryButton = ObjectAnimator.ofFloat(
				getApplicationContext(), "y", 660, -300);

		// //set the view as target
		galleryButton.setDuration(200);
		galleryButton.setTarget(pickFromGallery);
		// //start the animation
		galleryButton.start();
	}

	public void bottomToMiddleAnimation() {

		ObjectAnimator camerabutton = ObjectAnimator.ofFloat(
				getApplicationContext(), "y", 1920, 600);

		// //set the view as target
		camerabutton.setInterpolator(new LinearInterpolator());
		camerabutton.setDuration(300);
		camerabutton.setTarget(captureByCamera);
		camerabutton.setInterpolator(new LinearInterpolator());
		camerabutton.addListener(new Animator.AnimatorListener() {

			@Override
			public void onAnimationStart(Animator arg0) {

			}

			@Override
			public void onAnimationRepeat(Animator arg0) {

			}

			@Override
			public void onAnimationEnd(Animator arg0) {
				ObjectAnimator camerabutton = ObjectAnimator.ofFloat(
						getApplicationContext(), "y", 600, 660);
				camerabutton.setDuration(300);
				camerabutton.setTarget(captureByCamera);
				camerabutton.start();
			}

			@Override
			public void onAnimationCancel(Animator arg0) {

			}
		});
		// //start the animation

		camerabutton.start();

		ObjectAnimator galleryButton = ObjectAnimator.ofFloat(
				getApplicationContext(), "y", 1920, 600);

		// //set the view as target
		galleryButton.setInterpolator(new LinearInterpolator());
		galleryButton.setDuration(300);
		galleryButton.setTarget(pickFromGallery);
		// //start the animation
		galleryButton.setInterpolator(new LinearInterpolator());
		galleryButton.addListener(new Animator.AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {

			}

			@Override
			public void onAnimationRepeat(Animator animation) {

			}

			@Override
			public void onAnimationEnd(Animator animation) {
				ObjectAnimator camerabutton = ObjectAnimator.ofFloat(
						getApplicationContext(), "y", 600, 660);
				camerabutton.setDuration(300);
				camerabutton.setTarget(pickFromGallery);
				camerabutton.start();
			}

			@Override
			public void onAnimationCancel(Animator animation) {

			}
		});

		galleryButton.start();
	}

	public void goneToUpAnimation(RelativeLayout image) {

		ObjectAnimator camerabutton = ObjectAnimator.ofFloat(
				getApplicationContext(), "y", 660, -300);

		// //set the view as target
		camerabutton.setDuration(200);
		camerabutton.setTarget(image);

		// //start the animation
		camerabutton.start();

	}

}
