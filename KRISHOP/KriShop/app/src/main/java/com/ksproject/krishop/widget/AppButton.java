package com.ksproject.krishop.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

/**
 * Created by tanvir-android on 8/12/16.
 */
public class AppButton extends AppCompatButton {

    public AppButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public AppButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AppButton(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                "font/Lato-Regular.ttf");

        setTypeface(tf);
    }

}