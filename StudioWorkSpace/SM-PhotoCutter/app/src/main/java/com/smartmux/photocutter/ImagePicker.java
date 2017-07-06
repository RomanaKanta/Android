package com.smartmux.photocutter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;
import com.smartmux.photocutter.dialogfragment.PhotoSelectionDialog;
import com.smartmux.photocutter.utils.PhotoCutterConstants;

import java.util.ArrayList;

public class ImagePicker extends FragmentActivity {

	private ShimmerTextView text;
    private String path;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_pick_image);

        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
//                Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();

                text = (ShimmerTextView) findViewById(R.id.shimmer_tv);
                Shimmer shimmer = new Shimmer();
                shimmer.start(text);

                text.setOnClickListener(new OnClickListener() {

                    @SuppressLint("NewApi")
                    @Override
                    public void onClick(View v) {

//                        BlurBehind.getInstance().execute(ImagePicker.this,
//                                new OnBlurCompleteListener() {
//                                    @Override
//                                    public void onBlurComplete() {
//                                        Intent intent = new Intent(ImagePicker.this,
//                                                BlurredActivity.class);
//                                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                                        intent.putExtra(PhotoCutterConstants.CONTANT, PhotoCutterConstants.IMAGE_CROP);
//                                        startActivity(intent);
////                                        finish();
//                                    }
//                                });

                        new PhotoSelectionDialog().show(getSupportFragmentManager(),"Select");

                    }
                });

            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
//                Toast.makeText(MainActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }


        };

        new TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();
	}


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PhotoCutterConstants.GALLERY_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {

                Uri imageFileUri = data.getData();

                if (imageFileUri != null) {

                    path = getPath(imageFileUri);

                        Intent i = new Intent(this, MainActivity.class);
                        i.putExtra(PhotoCutterConstants.IMAGE_PATH, path);
                        startActivity(i);
                }
            }
        }

        else if (requestCode == PhotoCutterConstants.ACTION_TAKE_PHOTO_B) {

            if (resultCode == RESULT_OK) {
                if (PhotoCutterConstants.mCurrentPhotoPath != null) {
                    Intent i = new Intent(this, MainActivity.class);
                    i.putExtra(PhotoCutterConstants.IMAGE_PATH, PhotoCutterConstants.mCurrentPhotoPath);
                    startActivity(i);
                    PhotoCutterConstants.mCurrentPhotoPath = null;
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
}
