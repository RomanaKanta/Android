package com.ksproject.krishop.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.ksproject.krishop.R;
import com.ksproject.krishop.fragment.FragmentLogin;

import butterknife.ButterKnife;

public class SingUpActivity extends AppCompatActivity {


    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        fragmentManager = getSupportFragmentManager();

        setFragment( new FragmentLogin(), 0, 0);

    }
    public void setFragment( Fragment fragment, int enter, int exit) {

        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(enter,
                exit);
        fragmentTransaction.replace(R.id.content, fragment);
        fragmentTransaction.commit();
    }

//
//    @Override
//    public void onBackPressed() {
//        Fragment f = fragmentManager.findFragmentById(R.id.content);
//        if (f instanceof FragmentSignUpOption) {
//            finish();
//            overridePendingTransition(R.anim.push_right_out, R.anim.push_right_in);
//        } else {
//            setFragment(new FragmentSignUpOption(), R.anim.push_right_out, R.anim.push_right_in);
//
//        }
//    }

}
