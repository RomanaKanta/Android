package com.smartmux.shopsy.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smartmux.shopsy.R;
import com.smartmux.shopsy.widget.AppButton;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PaymentActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.card_info)
    LinearLayout cardInfo;

    @Bind(R.id.total_item)
    TextView itemNum;

    @Bind(R.id.amount)
    TextView totalAmount;

    @Bind(R.id.btn_buy)
    AppButton btnBuy;

    float mTotal;

    @OnClick(R.id.btn_pay_card)
    public void paywithCard(){
        cardInfo.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        ButterKnife.bind(this);
        setActionBar();
        setInfo();
    }

    private void setInfo(){

        if(getIntent().hasExtra("Items")){

            mTotal = getIntent().getFloatExtra("Total", 0.0f);

            itemNum.setText(getIntent().getExtras().getString("Items") + " Items");
            totalAmount.setText("" + mTotal +" "+ getIntent().getExtras().getString("Currency"));
            btnBuy.setText("Buy - "+ mTotal +" "+ getIntent().getExtras().getString("Currency"));


        }
    }

    private void setActionBar() {

        setSupportActionBar(toolbar);
//        getSupportActionBar().setIcon(R.drawable.drawer);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        title.setText("Payment");

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
        finish();
        overridePendingTransition(R.anim.push_right_out, R.anim.push_right_in);
    }

}
