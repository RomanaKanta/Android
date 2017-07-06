package com.smartmux.outofmilk.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.smartmux.outofmilk.R;
import com.smartmux.outofmilk.modelclass.UserInformation;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.textview_name)
    TextView txtName;

    @OnClick(R.id.log_out_button)
    public void logout(){

        if(getIntent().hasExtra("Acc")) {
            if(getIntent().getExtras().getString("Acc").equals("facebook")) {
                LoginManager.getInstance().logOut();
            }else if(getIntent().getExtras().getString("Acc").equals("google")) {



            }else{

            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if(getIntent().hasExtra("User")){

            UserInformation userInfo = getIntent().getExtras().getParcelable("User");

            txtName.setText("Name : " + userInfo.getName() + "   Email : " + userInfo.getEmail());

        }

//        if(getIntent().hasExtra("SocialUser")){
//
//            SocialUser socialUser = (SocialUser)getIntent().getExtras().getSerializable("SocialUser");
//
//            txtName.setText("Name : " + socialUser.getName() + "   Email : " + socialUser.getEmail());
//
//        }
    }
}
