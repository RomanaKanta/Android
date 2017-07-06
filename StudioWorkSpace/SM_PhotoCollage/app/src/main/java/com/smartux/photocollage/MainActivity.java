package com.smartux.photocollage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by tanvir-android on 6/7/16.
 */
public class MainActivity extends AppMainActivity{

    Button btnCollage,btnMagazine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        btnCollage = (Button) findViewById(R.id.button_collage);
        btnMagazine = (Button) findViewById(R.id.button_magazine);

        btnCollage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,CustomGalleryActivity.class);
                intent.setAction(getString(R.string.action_multiple_image_pic));
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in,
                        R.anim.push_left_out);
//                finish();
            }
        });

        btnMagazine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this,CustomGalleryActivity.class);
                intent.setAction(getString(R.string.action_single_image_pic));
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in,
                        R.anim.push_left_out);
            }
        });


    }
}
