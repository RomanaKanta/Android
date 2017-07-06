package com.smartmux.shopsy.activity;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.appyvet.rangebar.RangeBar;
import com.smartmux.shopsy.R;
import com.smartmux.shopsy.adapter.ProductBrandAdapter;
import com.smartmux.shopsy.adapter.ProductColorAdapter;
import com.smartmux.shopsy.adapter.ProductSizeAdapter;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FilterActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.rangeSeekbar)
    RangeBar rangebar;

    //    @Bind(R.id.min_range)
//    TextView minRange;
//
//    @Bind(R.id.max_range)
//    TextView maxRange;
    @Bind(R.id.recyclerViewColorList)
    RecyclerView colorList;

    @Bind(R.id.recyclerViewSizeList)
    RecyclerView sizeList;

    @Bind(R.id.recyclerViewBrandList)
    RecyclerView brandList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);


        ButterKnife.bind(this);
        setActionBar();

        ArrayList<String> colorlist = new ArrayList<>();
        colorlist.add("#d54b4f");
        colorlist.add("#2b2b2b");
        colorlist.add("#41bbc8");
        colorlist.add("#898c91");
        colorlist.add("#4ed964");


        ArrayList<String> sizelist = new ArrayList<>();
        sizelist.add("S");
        sizelist.add("M");
        sizelist.add("L");
        sizelist.add("XL");
        sizelist.add("XXL");


        ArrayList<String> brandlist = new ArrayList<>();
        brandlist.add("Sparx");
        brandlist.add("Adidas");
        brandlist.add("Puma");
        brandlist.add("Crocs");
        brandlist.add("Reebok");

        setPriceRangeBar();
        setColorList(colorlist);
        setSizeList(sizelist);
        setBrandList(brandlist);

//        new SM_AsyncTaskForFilterInfo().execute();

    }

    private void setActionBar() {

        setSupportActionBar(toolbar);
//        getSupportActionBar().setIcon(R.drawable.drawer);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        title.setText("Filter");

    }

    private void setPriceRangeBar() {

        rangebar.setTemporaryPins(false);
        rangebar.setDrawTicks(true);
//        rangebar.setTickStart(start);
//        rangebar.setTickEnd(end);
        rangebar.setTickHeight(0.5f);

        rangebar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex,
                                              int rightPinIndex,
                                              String leftPinValue, String rightPinValue) {
//                minRange.setText("Min = " + leftPinValue);
//                maxRange.setText("Max = " + rightPinValue);

            }

        });

    }

    private void setColorList(ArrayList<String> list) {

        colorList.setHasFixedSize(true);
        colorList.setLayoutManager(new LinearLayoutManager(
                FilterActivity.this, LinearLayoutManager.HORIZONTAL,
                false));

        colorList.setAdapter(new ProductColorAdapter(
                FilterActivity.this, list));// set adapter on recyclerview

    }

    private void setSizeList(ArrayList<String> list) {


        sizeList.setHasFixedSize(true);
        sizeList.setLayoutManager(new LinearLayoutManager(
                FilterActivity.this, LinearLayoutManager.HORIZONTAL,
                false));

        sizeList.setAdapter(new ProductSizeAdapter(
                FilterActivity.this, list));// set adapter on recyclerview

    }

    private void setBrandList(ArrayList<String> list) {


        brandList.setLayoutManager(new GridLayoutManager(FilterActivity.this, 1));
        final int gridMargin = getResources().getDimensionPixelOffset(R.dimen.grid_margin);
        brandList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                       RecyclerView.State state) {
                outRect.set(gridMargin, gridMargin, gridMargin, gridMargin);
            }
        });

        brandList.setPadding(gridMargin, gridMargin, gridMargin, gridMargin);

        brandList.setAdapter(new ProductBrandAdapter(FilterActivity.this, list));

    }

   /* class SM_AsyncTaskForFilterInfo extends AsyncTask<String, String, Boolean> {

        ProgressHUD mProgressHUD;
        String error = "";

        float max = 10;
        float min = 5;

        HashSet<String> productSize = new HashSet<>();
        HashSet<String> productColor = new HashSet<>();
        HashSet<String> productBrands = new HashSet<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressHUD = ProgressHUD.show(FilterActivity.this,
                    "Loading", true);
        }

        @Override
        protected Boolean doInBackground(String... params) {

            String catagory = getIntent().getExtras().getString(Constant.CATAGORY);

            if (catagory.equals(Constant.MENS)) {

                if (AppData.mensProduct != null && AppData.mensProduct.size() > 0) {

                    max = Float.parseFloat(AppData.mensProduct.get(0).getProductPrice());
                    min = Float.parseFloat(AppData.mensProduct.get(0).getProductPrice());

                    for (int i = 0; i < AppData.mensProduct.size(); i++) {

                        ProductModelClass product = AppData.mensProduct.get(i);

                        float price = Float.parseFloat(product.getProductPrice());

                        if (price < min) min = price;
                        if (price > max) max = price;

                        productBrands.add(product.getBrand());
                        productSize.addAll(product.getProductSize());
                        productColor.addAll(product.getProductColor());

                    }

                    return true;
                }

            } else if (catagory.equals(Constant.WOMENS)) {
                if (AppData.womensProduct != null && AppData.womensProduct.size() > 0) {

                    max = Float.parseFloat(AppData.womensProduct.get(0).getProductPrice());
                    min = Float.parseFloat(AppData.womensProduct.get(0).getProductPrice());

                    for (int i = 0; i < AppData.womensProduct.size(); i++) {

                        ProductModelClass product = AppData.womensProduct.get(i);

                        float price = Float.parseFloat(product.getProductPrice());

                        if (price < min) min = price;
                        if (price > max) max = price;

                        productBrands.add(product.getBrand());
                        productSize.addAll(product.getProductSize());
                        productColor.addAll(product.getProductColor());

                    }

                    return true;
                }

            } else {
            }


            return false;
        }

        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            if (result) {

//                setPriceRangeBar(min, max);
//                setColorList(productColor);
//                setSizeList(productSize);
//                setBrandList(productBrands);
            }


            if (mProgressHUD.isShowing()) {
                mProgressHUD.dismiss();

            }


        }
    }
*/
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.nothing,
                        R.anim.push_down_out);
        }
        return (super.onOptionsItemSelected(menuItem));
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.nothing,
                R.anim.push_down_out);
    }
}
