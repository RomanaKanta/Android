package com.smartmux.filevault;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.smartmux.filevault.dropbox.DropboxActivity;
import com.smartmux.filevault.utils.AppActionBar;
import com.smartmux.filevault.utils.FileManager;
import com.smartmux.filevault.wifitransfer.ServerControlActivity;

public class SettingActivity extends AppMainActivity implements
OnClickListener{

    private FileManager fileManager;
    private RelativeLayout change_pass, wifi_transfer, dropbox;
    private ImageView image_password,image_wifi,image_dropbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        AppActionBar.changeActionBarFont(getApplicationContext(),
                SettingActivity.this);

        AppActionBar.updateAppActionBar(getActionBar(), this, true);
        getActionBar().setTitle(getString(R.string.setting_title));
        fileManager = new FileManager();

        image_password = (ImageView)findViewById(R.id.image_passworw);
        image_password.setColorFilter(R.color.app_main_color, PorterDuff.Mode.SRC_ATOP);
        image_wifi= (ImageView)findViewById(R.id.image_wifi);
        image_wifi.setColorFilter(R.color.app_main_color, PorterDuff.Mode.SRC_ATOP);
        image_dropbox = (ImageView)findViewById(R.id.image_dropbox);
        image_dropbox.setColorFilter(R.color.app_main_color, PorterDuff.Mode.SRC_ATOP);

        change_pass = (RelativeLayout) findViewById(R.id.change_password);
        change_pass.setOnClickListener(this);

        wifi_transfer = (RelativeLayout) findViewById(R.id.wifi_transfer);
        wifi_transfer.setOnClickListener(this);

        dropbox = (RelativeLayout) findViewById(R.id.dropbox);
        dropbox.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.change_password:

                Intent password_change = new Intent(SettingActivity.this, PasswordChangeActivity.class);
                password_change.putExtra("type", "change");
                startActivity(password_change);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                // AppToast.show(getApplicationContext(), "Settings");
                // finish();
                break;

            case R.id.wifi_transfer:

                Intent wifitrns = new Intent(SettingActivity.this,
                        ServerControlActivity.class);
                startActivity(wifitrns);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                // AppToast.show(getApplicationContext(), "Settings");
                // finish();
                break;

            case R.id.dropbox:

                Intent dropbox = new Intent(SettingActivity.this,
                        DropboxActivity.class);
                startActivity(dropbox);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                // AppToast.show(getApplicationContext(), "Settings");
                // finish();
                break;

            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.push_right_out, R.anim.push_right_in);
        ;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            fileManager.setBackCode(getApplicationContext());
            finish();
            overridePendingTransition(R.anim.push_right_out,
                    R.anim.push_right_in);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
