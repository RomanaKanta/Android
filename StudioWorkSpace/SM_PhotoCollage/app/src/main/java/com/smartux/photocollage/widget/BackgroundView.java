//package com.smartux.photocollage.widget;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.BitmapShader;
//import android.graphics.Canvas;
//import android.graphics.Paint;
//import android.graphics.Shader;
//import android.view.View;
//
//import com.smartux.photocollage.R;
//
///**
// * Created by tanvir-android on 6/2/16.
// */
//public class BackgroundView extends View {
//
//    Bitmap fillBMP;
//    BitmapShader fillBMPshader;
//
//    public BackgroundView(Context context) {
//        super(context);
//
//        fillBMP = BitmapFactory.decodeResource(context.getResources(), R.drawable.circle7);
//        //Initialize the BitmapShader with the Bitmap object and set the texture tile mode
//        fillBMPshader = new BitmapShader(fillBMP, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
//    }
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        int x = getWidth();
//        int y = getHeight();
//
//        Paint bg = new Paint();
//        bg.setShader(fillBMPshader);
//        canvas.drawRect(0,0,x,y,bg);
//
//    }
//
//}
