package com.smartmux.couriermoc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.smartmux.couriermoc.R;
import com.smartmux.couriermoc.ServiceActivity.CourierServiceActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class TempActivity extends AppCompatActivity {

    @OnClick(R.id.btn_user)
    public void setUserPortion(){
        startActivity(new Intent(this, MainActivity.class));
    }

    @OnClick(R.id.btn_driver)
    public void setDriverPortion(){
        startActivity(new Intent(this, CourierServiceActivity.class));
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_temp);

        ButterKnife.bind(this);
    }

}

