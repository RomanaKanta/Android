package com.smartmux.pos.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.smartmux.pos.R;
import com.smartmux.pos.adapter.TempProductAdapter;
import com.smartmux.pos.database.DBManager;
import com.smartmux.pos.model.Product;
import com.smartmux.pos.utils.ItemClickSupport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by tanvir-android on 7/18/16.
 */
public class TemporaryActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    String catagoryID,catagoryTitle;
    @Bind(R.id.catagory_product_recycleView)
    RecyclerView recyclerView;
    private List<Product> productList;
    private DBManager dbManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tempory);
        ButterKnife.bind(this);

        if(getIntent().getExtras()!=null) {
            catagoryID = getIntent().getExtras().getString("CatagoryID");
            catagoryTitle = getIntent().getExtras().getString("Catagory");
        }

        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(catagoryTitle);

        dbManager = new DBManager(TemporaryActivity.this);
//        saveDB();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLongClickable(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(TemporaryActivity.this, 1);
        recyclerView.setLayoutManager(layoutManager);
        final int gridMargin = getResources().getDimensionPixelOffset(R.dimen.spacing);

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                       RecyclerView.State state) {
                outRect.set(gridMargin, gridMargin, gridMargin, gridMargin);
            }
        });

        recyclerView.setPadding(gridMargin , gridMargin,gridMargin,gridMargin);

        ItemClickSupport itemClick = ItemClickSupport
                .addTo(recyclerView);
        itemClick
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {

                        Intent intent = new Intent(TemporaryActivity.this, AddProductActivity.class);
                        intent.putExtra("ProductID",productList.get(position).getProductID());
                        startActivity(intent);
                        overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
                    }
                });

    }

    @Override
    protected void onResume() {
        super.onResume();
        setData();
    }

    private void setData(){

        productList = dbManager.getCategoryProduct(catagoryID);

        recyclerView.setAdapter(new TempProductAdapter(TemporaryActivity.this,productList));
    }


    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {


            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                return true;

            default:
                return false;
        }
    }







    private void saveDB(){

        try {
            InputStream myInput = new FileInputStream("/data/data/com.smartmux.pos/databases/pos.db");

            File file = new File(Environment.getExternalStorageDirectory().getPath()+"/pos.db");
            if (!file.exists()){
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    Log.i("FO", "File creation failed for " + file);
                }
            }

            OutputStream myOutput = new FileOutputStream(Environment.getExternalStorageDirectory().getPath()+"/pos.db");

            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer))>0){
                myOutput.write(buffer, 0, length);
            }

            //Close the streams
            myOutput.flush();
            myOutput.close();
            myInput.close();
            Log.i("FO","copied");

        } catch (Exception e) {
            Log.i("FO","exception="+e);
        }

    }

}
