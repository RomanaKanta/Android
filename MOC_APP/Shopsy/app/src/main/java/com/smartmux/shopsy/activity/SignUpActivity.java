package com.smartmux.shopsy.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.smartmux.shopsy.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUpActivity extends AppCompatActivity {

    @OnClick(R.id.btn_create_account)
    public void createAccount(){
        overridePendingTransition(R.anim.push_right_out, R.anim.push_right_in);
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(R.anim.push_right_out, R.anim.push_right_in);
        finish();
    }
}
