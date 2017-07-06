/*
 * Copyright (C) 2013 Tomáš Procházka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.smartmux.photocutter.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.smartmux.photocutter.modelclass.BitmapHolder;

public class MovableImageView extends View {
    Bitmap bitmap;
    float centreX =  500;
    float centreY = 500;
    BitmapHolder mBitmapHolder = null;

    public MovableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mBitmapHolder = new BitmapHolder();

        bitmap = mBitmapHolder.getBm();
//        if(bitmap!=null){
//            centreX = (canvas.getWidth() - bitmap.getWidth()) / 2;
//
//            centreY = (canvas.getHeight() - bitmap.getHeight()) / 2;
//        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                centreX = event.getX();
                centreY = event.getY();
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                centreX = event.getX();
                centreY = event.getY();
                invalidate();
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(bitmap!=null) {

            canvas.drawBitmap(bitmap, centreX, centreX, null);
        }
    }
}