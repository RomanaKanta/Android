package com.smartmux.shopsy.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ortiz.touch.TouchImageView;
import com.smartmux.shopsy.R;
import com.smartmux.shopsy.adapter.ProductPagerAdapter;
import com.smartmux.shopsy.modelclass.ProductModel;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProductDetailActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.imageView_zoom)
    TouchImageView expandedImageView;

    @Bind(R.id.container)
    RelativeLayout mLayout;

    ProductModel mProduct;

//    @Bind(R.id.product_image)
//    ImageView productImage;
    @Bind(R.id.product_description)
    TextView productDescription;
    @Bind(R.id.product_name)
    TextView productName;
    @Bind(R.id.product_price)
    TextView productPrice;

    TextView pageTitle;

    @Bind(R.id.pager)
    ViewPager pager;
    @Bind(R.id.viewPagerCountDots)
    LinearLayout pager_indicator;
    private int dotsCount;
    private ImageView[] dots;
    //    ViewPager pager;
    ProductPagerAdapter adapter;

    int page = 1;

    float BIG_SCALE = 1.0f;
    float SMALL_SCALE = 0.7f;
    float DIFF_SCALE = BIG_SCALE - SMALL_SCALE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        ButterKnife.bind(this);
        setActionBar();
        setData();

    }

    private void setData(){

        if(getIntent().hasExtra("PRODUCT")){

            mProduct = getIntent().getExtras().getParcelable("PRODUCT") ;

            pageTitle.setText(mProduct.getSub_catarogy());

            productName.setText(mProduct.getProductName());

            // for nested HTML tags
            productDescription.setText(Html.fromHtml(Html.fromHtml(mProduct.getProductDescription()).toString()));

            productPrice.setText(mProduct.getProductPrice() + " " + mProduct.getCurrency());

            setViewPager(mProduct.getImageArray());

//            setAutoScrollBanner(mProduct.getImage());

//            String imageURL = mProduct.getImageArray().get(0);
//
//            if (imageURL != null && !TextUtils.isEmpty(imageURL)) {
//
//
//                String url = Constant.IMAGE_HTTP + imageURL;
//                Glide
//                        .with(this)
//                        .load(url)
//                        .placeholder(R.drawable.placeholder)
//                        .crossFade()
//                        .into(productImage);

//                Picasso.with(this)
//                        .load(url)
//                        .config(Bitmap.Config.RGB_565)
//                        .placeholder(R.drawable.product_placeholder)
//                        .into(productImage,
//                                new Callback() {
//                                    @Override
//                                    public void onSuccess() {
//                                        productImage.setVisibility(View.VISIBLE);
//                                        imageSet = true;
//                                    }
//
//                                    @Override
//                                    public void onError() {
//                                        productImage.setImageResource(R.drawable.product_placeholder);
//                                    }
//                                }
//                        );
//        }


        }
    }

    private void setActionBar() {

        setSupportActionBar(toolbar);
//        getSupportActionBar().setIcon(R.drawable.drawer);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        pageTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);


    }


    private void setViewPager(ArrayList<String> images) {



//        pager = mContainer.getViewPager();
         adapter = new ProductPagerAdapter(ProductDetailActivity.this, images);
        pager.setAdapter(adapter);
        //Necessary or the pager will only have one extra page to show
        // make this at least however many pages you can see
        pager.setOffscreenPageLimit(adapter.getCount());
        //A little space between pages
        pager.setPageMargin(15);

        //If hardware acceleration is enabled, you should also remove
        // clipping on the pager for its children.
        pager.setClipChildren(false);


        dotsCount = images.size();
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(ProductDetailActivity.this);
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(4, 0, 4, 0);

            pager_indicator.addView(dots[i], params);
        }

        dots[0].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));


        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                page = position;

                position = position % dotsCount;

                for (int i = 0; i < dotsCount; i++) {
                    dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));
                }

                dots[position].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


    private int getRelativeLeft(View myView) {
        if (myView.getParent() == myView.getRootView())
            return myView.getLeft();
        else
            return myView.getLeft() + getRelativeLeft((View) myView.getParent());
    }

    private int getRelativeTop(View myView) {
        if (myView.getParent() == myView.getRootView())
            return myView.getTop();
        else
            return myView.getTop() + getRelativeTop((View) myView.getParent());
    }

    private int getRelativeRight(View myView) {
        if (myView.getParent() == myView.getRootView())
            return myView.getRight();
        else
            return myView.getRight() + getRelativeRight((View) myView.getParent());
    }

    private int getRelativeBottom(View myView) {
        if (myView.getParent() == myView.getRootView())
            return myView.getBottom();
        else
            return myView.getBottom() + getRelativeBottom((View) myView.getParent());
    }

    private Animator mCurrentAnimator;

    private int mShortAnimationDuration = 200;
    public void zoomImageFromThumb(final View thumbView) {
        // If there's an animation in progress, cancel it immediately and
        // proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        thumbView.setDrawingCacheEnabled(true);

        thumbView.buildDrawingCache();

        Bitmap bm = thumbView.getDrawingCache();
        expandedImageView.setImageBitmap(bm);

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step
        // involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the
        // final bounds are the global visible rectangle of the container view.
        // Also
        // set the container view's offset as the origin for the bounds, since
        // that's
        // the origin for the positioning animation properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        mLayout.getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the
        // "center crop" technique. This prevents undesirable stretching during
        // the animation.
        // Also calculate the start scaling factor (the end scaling factor is
        // always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height() > (float) startBounds
                .width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
//            startBounds.left = getRelativeLeft(thumbView);
//            startBounds.right = getRelativeRight(thumbView);
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
//            startBounds.top = getRelativeTop(thumbView);
//            startBounds.bottom = getRelativeBottom(thumbView);
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins,
        // it will position the zoomed-in view in the place of the thumbnail.
        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations to the
        // top-left corner of
        // the zoomed-in view (the default is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties
        // (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set.play(
                ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y,
                        startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down to the
        // original bounds
        // and show the thumbnail instead of the expanded image.
        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel,
                // back to their
                // original values.
                AnimatorSet set = new AnimatorSet();
                set.play(
                        ObjectAnimator.ofFloat(expandedImageView, View.X,
                                startBounds.left))
                        .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                                startBounds.top))
                        .with(ObjectAnimator.ofFloat(expandedImageView,
                                View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator.ofFloat(expandedImageView,
                                View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });
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
