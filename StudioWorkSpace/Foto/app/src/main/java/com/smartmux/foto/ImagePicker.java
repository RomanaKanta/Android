package com.smartmux.foto;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.faradaj.blurbehind.BlurBehind;
import com.faradaj.blurbehind.OnBlurCompleteListener;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;

import java.util.ArrayList;

public class ImagePicker extends Activity {

	private ShimmerTextView text;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_pick_image);

        text = (ShimmerTextView) findViewById(R.id.shimmer_tv);
        Shimmer shimmer = new Shimmer();
        shimmer.start(text);

        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
//                Toast.makeText(ImagePicker.this, "Permission Granted", Toast.LENGTH_SHORT).show();

                text.setOnClickListener(new View.OnClickListener() {

                    @SuppressLint("NewApi")
                    @Override
                    public void onClick(View v) {
                        BlurBehind.getInstance().execute(ImagePicker.this,
                                new OnBlurCompleteListener() {
                                    @Override
                                    public void onBlurComplete() {
                                        Intent intent = new Intent(ImagePicker.this,
                                                BlurredActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

                                        startActivity(intent);
                                    }
                                });
                    }
                });

            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
//                Toast.makeText(ImagePicker.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }


        };


        new TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();


	}
}
