package com.smartmux.photocutter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.smartmux.photocutter.modelclass.BitmapHolder;
import com.smartmux.photocutter.utils.ClassCompressImage;
import com.smartmux.photocutter.utils.PhotoCutterConstants;
import com.smartmux.photocutter.view.CustomImageView;

/**
 * Created by tanvir-android on 5/27/16.
 */
public class BackgroundSettingActivity extends Activity implements View.OnClickListener{

    RelativeLayout bg_layer;
    ImageView bg_image,close_image;
    TextView done_text;
    SeekBar seekbar_alpha;
    RelativeLayout.LayoutParams viewParams;

    ClassCompressImage compressPhoto;
    BitmapHolder mBitmapHolder = null;
    Bitmap bg_image_bitmap;

    CustomImageView m_viewFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_backgroung_setting);

        mBitmapHolder = new BitmapHolder();
        compressPhoto = new ClassCompressImage(BackgroundSettingActivity.this);

        init();
        setImage();
        changeImageAlpha();


    }

    private void init(){

        bg_layer = (RelativeLayout) findViewById(R.id.bg_image_layer);
        bg_image = (ImageView) findViewById(R.id.bg_imageview);
//        crop_image = (ImageView) findViewById(R.id.add_crop_imageview);
        close_image = (ImageView) findViewById(R.id.ImageView_bg_close);
        done_text = (TextView) findViewById(R.id.TextView_bg_done);
        seekbar_alpha = (SeekBar) findViewById(R.id.seekBar_alpha);


        close_image.setOnClickListener(this);
        done_text.setOnClickListener(this);

    }

    private void setImage(){

        String path = getIntent().getExtras().getString(PhotoCutterConstants.IMAGE_PATH);
        bg_image_bitmap = compressPhoto.compressImage(path);

        if(bg_image_bitmap!=null) {
            bg_image.setImageBitmap(bg_image_bitmap);

        }

        m_viewFrame = new CustomImageView(this);
        if(mBitmapHolder.getBm()!=null) {

            viewParams = new RelativeLayout.LayoutParams( RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            viewParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            m_viewFrame.setLayoutParams(viewParams);
            m_viewFrame.setBackgroundColor(Color.TRANSPARENT);
            m_viewFrame.setBitmap(mBitmapHolder.getBm());

            m_viewFrame.setActivity(this);
            m_viewFrame.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

            bg_layer.addView(m_viewFrame);
        }

    }

    private void changeImageAlpha(){

        seekbar_alpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                m_viewFrame.setBitmapAlpha(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

    }

     @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.ImageView_bg_close:
                finish();
                break;

            case R.id.TextView_bg_done:

                generateBitmap();

                Intent intent = new Intent(BackgroundSettingActivity.this,
                        MainActivity.class);
                startActivity(intent);
                finish();

                break;
            default:
                break;
        }

        }

    private void generateBitmap() {

        bg_layer.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(bg_layer.getDrawingCache());
        bg_layer.setDrawingCacheEnabled(false);
        mBitmapHolder.setBm(bitmap);
    }


}
