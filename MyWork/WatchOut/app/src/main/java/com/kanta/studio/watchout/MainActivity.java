package com.kanta.studio.watchout;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.kanta.studio.watchout.levels.DynamicMove;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View mCustomeView = new DynamicMove(this);
        setContentView(mCustomeView);
    }



}
