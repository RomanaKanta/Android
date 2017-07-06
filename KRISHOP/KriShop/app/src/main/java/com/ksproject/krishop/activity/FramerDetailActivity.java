package com.ksproject.krishop.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ksproject.krishop.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FramerDetailActivity extends AppCompatActivity {


    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.name)
    TextView farmerName;

    @Bind(R.id.address)
    TextView farmerAddress;

    @Bind(R.id.phone)
    TextView farmerPhone;

    @OnClick(R.id.phone)
    public void showCallAlart() {

        AlertDialog.Builder alert = new AlertDialog.Builder(
                new ContextThemeWrapper(FramerDetailActivity.this,
                        R.style.popup_theme));

        alert.setTitle(R.string.app_name);
        alert.setMessage(R.string.Call);

        alert.setPositiveButton("Call", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                if (checkWriteExternalPermission()) {
                    String number = farmerPhone.getText().toString();
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:+880" + number));
                    startActivity(callIntent);
                }else {
                    Toast.makeText(FramerDetailActivity.this, "Sorry, CALL_PHONE Permission not available.",Toast.LENGTH_SHORT).show();
                }
                dialog.cancel();
            }

        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alert.show();
    }

    @Bind(R.id.farmer_image)
    ImageView farmerImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_framer_detail);
        ButterKnife.bind(this);
        setActionBar();

        if (getIntent().hasExtra("FName")) {

            String name = getIntent().getExtras().getString("FName");
            farmerName.setText(name);

            String addre = getIntent().getExtras().getString("FAddress");
            farmerAddress.setText(addre);

            String phone = getIntent().getExtras().getString("FPhone");
            farmerPhone.setText(phone);

            String proImage = getIntent().getExtras().getString("FImage");

            if (proImage != null && !TextUtils.isEmpty(proImage)) {
                Glide
                        .with(FramerDetailActivity.this)
                        .load(proImage)
                        .placeholder(R.drawable.krishop_logo)
                        .centerCrop()
                        .into(farmerImage);
            }
        }


    }

    private void setActionBar() {

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

//        title = (TextView) toolbar.findViewById(R.id.action_title);
    }



    private boolean checkWriteExternalPermission()
    {

        String permission = "android.permission.CALL_PHONE";
        int res = this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:

                finish();
                overridePendingTransition(R.anim.push_right_out, R.anim.push_right_in);
        }
        return (super.onOptionsItemSelected(menuItem));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.push_right_out, R.anim.push_right_in);
    }
}
