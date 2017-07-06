package com.ksproject.krishop.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.ecloud.pulltozoomview.PullToZoomScrollViewEx;
import com.ksproject.krishop.R;
import com.ksproject.krishop.modelclass.Products;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProductDetailActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.scroll_view)
    PullToZoomScrollViewEx scrollView;

    @Bind(R.id.action_title)
    TextView title;

    @Bind(R.id.headerText)
    TextView headerText;

    @Bind(R.id.headerImage)
    ImageView placeImage;

    @Bind(R.id.headerroundImage)
    ImageView roundImage;

    Products product;

    @OnClick(R.id.headerroundImage)
    public void farmerDetail(){

        if(product!=null) {

            Intent intent = new Intent(ProductDetailActivity.this, FramerDetailActivity.class);
            intent.putExtra("FName", product.getFarmer_name());
            intent.putExtra("FAddress", product.getFarmer_address());
            intent.putExtra("FPhone", product.getFarmer_phone());
            intent.putExtra("FImage", product.getFarmer_image());
            startActivity(intent);
            overridePendingTransition(R.anim.push_left_in,
                    R.anim.push_left_out);
        }
    }

    @Bind(R.id.pro_Description)
    TextView proDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parallax);

        loadViewForCode();
        ButterKnife.bind(this);

        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        int mScreenHeight = localDisplayMetrics.heightPixels;
        int mScreenWidth = localDisplayMetrics.widthPixels;
        LinearLayout.LayoutParams localObject = new LinearLayout.LayoutParams(mScreenWidth, (int) (9.0F * (mScreenWidth / 16.0F)));
        scrollView.setHeaderLayoutParams(localObject);

        setActionBar();
        if (getIntent().hasExtra("Product")) {

            product = getIntent().getExtras().getParcelable("Product");
            setInfo(product);
        }

    }

    private void setActionBar() {

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

//        title = (TextView) toolbar.findViewById(R.id.action_title);
    }

    private void setInfo(Products products) {

        String proImage = products.getProduct_image();


        if (proImage != null && !TextUtils.isEmpty(proImage)) {
            Glide
                    .with(ProductDetailActivity.this)
                    .load(proImage)
                    .placeholder(R.drawable.placeholder)
                    .centerCrop()
                    .into(placeImage);
        }

        String framerImage = products.getFarmer_image();
        if (framerImage != null && !TextUtils.isEmpty(framerImage)) {

            Glide.with(ProductDetailActivity.this)
                    .load(framerImage)
                    .asBitmap()
                    .centerCrop()
                    .into(new BitmapImageViewTarget(roundImage) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            roundImage.setImageDrawable(circularBitmapDrawable);
                        }
                    });
        }

        title.setText(products.getProduct_name());

        headerText.setText(products.getFarmer_name());

        proDescription.setText(products.getDescription());

    }

    private void loadViewForCode() {

        PullToZoomScrollViewEx scrollView = (PullToZoomScrollViewEx) findViewById(R.id.scroll_view);
        View headView = LayoutInflater.from(this).inflate(R.layout.contant_head_view, null, false);
        View zoomView = LayoutInflater.from(this).inflate(R.layout.contant_zoom_view, null, false);
        View contentView = LayoutInflater.from(this).inflate(R.layout.contant_place_detail, null, false);
        scrollView.setHeaderView(headView);
        scrollView.setZoomView(zoomView);
        scrollView.setScrollContentView(contentView);
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
