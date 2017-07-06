package com.smartux.photocollage.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by tanvir-android on 7/12/16.
 */
public class AppButtonView extends Button {

    public AppButtonView(Context context) {
        super( context );
        setFont();

    }

    public AppButtonView(Context context, AttributeSet attrs) {
        super( context, attrs );
        setFont();
    }

    public AppButtonView(Context context, AttributeSet attrs, int defStyle) {
        super( context, attrs, defStyle );
        setFont();
    }

    private void setFont() {
//        Typeface normal = Typeface.createFromAsset(getContext().getAssets(),"fonts/steelfish_rg.otf");
//        setTypeface( normal, Typeface.NORMAL );
//
//        Typeface bold = Typeface.createFromAsset( getContext().getAssets(), "fonts/steelfish_bd.otf" );
//        setTypeface( normal, Typeface.BOLD );
        Typeface tf = Typeface.createFromAsset(getContext()
                .getAssets(), "Oduda-Bold-Demo.otf");

        setTypeface(tf);

        setTextColor(Color.WHITE);
        setTextSize(16);
    }


}