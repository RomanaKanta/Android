package com.smartmux.photocutter.dialogfragment;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;
import com.smartmux.photocutter.R;
import com.smartmux.photocutter.utils.AlbumStorageDirFactory;
import com.smartmux.photocutter.utils.BaseAlbumDirFactory;
import com.smartmux.photocutter.utils.FroyoAlbumDirFactory;
import com.smartmux.photocutter.utils.PhotoCutterConstants;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PhotoSelectionDialog extends DialogFragment {



//    private String mCurrentPhotoPath;
    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";
    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    RelativeLayout pickFromGallery, captureByCamera;


    public void onResume() {
        super.onResume();

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay()
                .getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;

        getDialog().getWindow().getAttributes().gravity = Gravity.CENTER;
        WindowManager.LayoutParams lp = getDialog().getWindow().getAttributes();
        lp.width = (int) ( width);
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes(lp);

        bottomToMiddleAnimation();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater
                .inflate(R.layout.activity_blurre, container, false);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawableResource(R.color.semi_transparent);
        getDialog().getWindow().setGravity(Gravity.CENTER);
//        getDialog().getWindow()
//                .getAttributes().windowAnimations = R.style.AnimationBottomUpDown;


        pickFromGallery = (RelativeLayout) rootView.findViewById(R.id.pick);
        captureByCamera = (RelativeLayout) rootView.findViewById(R.id.capture);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }

        rootView.findViewById(R.id.transparentLayout).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {


                        middleToTopAnimation();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dismiss();
                            }
                        }, 300);
                    }
                });

        captureByCamera.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                goneToUpAnimation(captureByCamera);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        dispatchTakePictureIntent(PhotoCutterConstants.ACTION_TAKE_PHOTO_B);
                        dismiss();
                    }
                }, 300);

            }
        });
        pickFromGallery.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                goneToUpAnimation(pickFromGallery);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

//                        importGallery();

                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                        photoPickerIntent.setType("image/*");
                        getActivity().startActivityForResult(photoPickerIntent, PhotoCutterConstants.GALLERY_REQUEST);
                        dismiss();

                    }
                }, 300);
            }
        });


        return rootView;
    }

    public void middleToTopAnimation() {

        ObjectAnimator camerabutton = ObjectAnimator.ofFloat(
                getActivity(), "y", 660, -300);

        // //set the view as target

        camerabutton.setDuration(300);
        camerabutton.setTarget(captureByCamera);
        // //start the animation
        camerabutton.start();

        ObjectAnimator galleryButton = ObjectAnimator.ofFloat(
                getActivity(), "y", 660, -300);

        // //set the view as target
        galleryButton.setDuration(200);
        galleryButton.setTarget(pickFromGallery);
        // //start the animation
        galleryButton.start();
    }

    public void bottomToMiddleAnimation() {

        ObjectAnimator camerabutton = ObjectAnimator.ofFloat(
                getActivity(), "y", 1920, 600);

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
                        getActivity(), "y", 600, 660);
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
                getActivity(), "y", 1920, 600);

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
                        getActivity(), "y", 600, 660);
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
                getActivity(), "y", 660, -300);

        // //set the view as target
        camerabutton.setDuration(200);
        camerabutton.setTarget(image);

        // //start the animation
        camerabutton.start();

    }

    private void dispatchTakePictureIntent(int actionCode) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        switch (actionCode) {
            case PhotoCutterConstants.ACTION_TAKE_PHOTO_B:
                File f = null;

                try {
                    f = setUpPhotoFile();
                    PhotoCutterConstants.mCurrentPhotoPath = f.getAbsolutePath();
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(f));
                } catch (IOException e) {
                    e.printStackTrace();
                    f = null;
                    PhotoCutterConstants.mCurrentPhotoPath = null;
                }
                break;

            default:
                break;
        } // switch

        getActivity().startActivityForResult(takePictureIntent, actionCode);
    }

    private File setUpPhotoFile() throws IOException {

        File f = createImageFile();
        PhotoCutterConstants.mCurrentPhotoPath = f.getAbsolutePath();

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


}
