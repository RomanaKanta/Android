package com.smartmux.pos.activity;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.smartmux.pos.R;
import com.smartmux.pos.database.DBManager;
import com.smartmux.pos.utils.ToastUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AddCategoryActivity extends AppCompatActivity {

    @Bind(R.id.category_name)
    EditText categoryName;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    private DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        setTitle(getString(R.string.title_add_category));

        dbManager = new DBManager(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_product_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_done).getIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:

                if(!TextUtils.isEmpty(categoryName.getText().toString())){

                    dbManager.addCategory(categoryName.getText().toString(),"");
                    ToastUtils.showShort("Category Added");
                }else{

                    categoryName.setError("required");
                }
                return true;

            case R.id.action_close:
                finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                return true;

            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                return true;

            default:
                return false;
        }
    }

}
