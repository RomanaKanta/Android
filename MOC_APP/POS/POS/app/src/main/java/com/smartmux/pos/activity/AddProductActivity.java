package com.smartmux.pos.activity;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.smartmux.pos.R;
import com.smartmux.pos.database.DBConstant;
import com.smartmux.pos.database.DBManager;
import com.smartmux.pos.model.Buy;
import com.smartmux.pos.model.Product;
import com.smartmux.pos.model.ProductCategory;
import com.smartmux.pos.model.Supplier;
import com.smartmux.pos.utils.ToastUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddProductActivity extends AppCompatActivity {


    @Bind(R.id.product_name)
    EditText productName;
    @Bind(R.id.product_price)
    EditText productPrice;

    @Bind(R.id.spinner_category)
    MaterialSpinner productCategory;
     @Bind(R.id.spinner_supplier)
     MaterialSpinner productSupplier;
    @Bind(R.id.spinner_unit)
    MaterialSpinner productUnit;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.product_quantity)
    TextView productQuantity;
    @Bind(R.id.total_price)
    TextView totalPrice;

   /* @Bind(R.id.imageButton_plus)
    ImageButton imageButtonPlus;
    @Bind(R.id.imageButton_minus)
    ImageButton imageButtonMinus;*/

    private DBManager dbManager;
    private  List<Supplier> supplierList;
    private  List<ProductCategory> categoryList;
    private List<String> unitList;
    private float totalUnitPrice = 0;
    private int count;
    private int itemQuantity = 1;
    Product product;
    String productID;

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @OnClick(R.id.imageButton_plus)
    public void itemAdd(View view) {

        itemQuantity++;
        productQuantity.setText(String.valueOf(itemQuantity));


        setTotalPrice();

    }

    @OnClick(R.id.imageButton_minus)
    public void itemSubtract(View view) {

        if (itemQuantity > 1) {

            itemQuantity--;
            productQuantity.setText(String.valueOf(itemQuantity));

            setTotalPrice();


        }

    }

    private void setTotalPrice(){

        if(productPrice.getText().toString().length() > 0){
            totalPrice.setText(String.format("%.2f", ((Float.parseFloat(productPrice.getText().toString()) * itemQuantity))) + " TAKA");
        }else{
            totalPrice.setText(String.format("%.2f", ((Float.parseFloat(productPrice.getText().toString()) * itemQuantity))) + " TAKA");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        setTitle(getString(R.string.title_add_product));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        dbManager = new DBManager(this);

        unitList = new ArrayList<>();
        unitList.add("kg");
        unitList.add("set");
        unitList.add("pcs");

        productUnit.setItems(unitList);
        productUnit.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                Snackbar.make(view, "Clicked " + item, Snackbar.LENGTH_LONG).show();
            }
        });


         categoryList = dbManager.getAllCategory();
        List<String> categories = new ArrayList<>();
        for(ProductCategory productCategory : categoryList){
            categories.add(productCategory.getGetProductCategoryName());
        }


        if(categories.size() > 0){
            productCategory.setItems(categories);
        }

        productCategory.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                Snackbar.make(view, "Clicked " + item, Snackbar.LENGTH_LONG).show();
            }
        });


         supplierList = dbManager.getAllSupplier();
         List<String> suppliers = new ArrayList<>();
        for(Supplier supplier : supplierList){
            suppliers.add(supplier.getName());
        }


        if(suppliers.size() > 0){
            productSupplier.setItems(suppliers);
        }

        productSupplier.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                Snackbar.make(view, "Clicked " + item, Snackbar.LENGTH_LONG).show();
            }
        });

        productQuantity.setText(String.valueOf(itemQuantity));
        productPrice.setText(String.valueOf(25));

        productPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(s.toString().length() > 0){
                    setTotalPrice();
                }

            }
        });


        if(getIntent().hasExtra("ProductID")){

             productID = getIntent().getExtras().getString("ProductID");

            product = dbManager.getProductByID(productID);

            productName.setText(product.getProductName());
            productPrice.setText(product.getProductPrice());


            for(int i=0; i<unitList.size(); i++){
                if(unitList.get(i).equals(product.getProductUnit())){
                    productUnit.setSelectedIndex(i);
                }
            }

            for(int i=0; i<categoryList.size(); i++){
                if(categoryList.get(i).getProductCategoryId().equals(product.getProductCategoryID())){

                    productCategory.setSelectedIndex(i);
                }
            }
            for(int i=0; i<supplierList.size(); i++){
                if(supplierList.get(i).getId().equals(product.getProductSupplierID())){

                    productSupplier.setSelectedIndex(i);
                }
            }
//            productName.setEnabled(false);
//            productPrice.setEnabled(false);
//            productUnit.setEnabled(false);
//            productCategory.setEnabled(false);
//            productSupplier.setEnabled(false);

        }

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

                if(!getIntent().hasExtra("ProductID")) {
                    if (TextUtils.isEmpty(productName.getText().toString())) {

                        productName.setError("required");
                        return true;
                    }
                    if (TextUtils.isEmpty(productPrice.getText().toString())) {

                        productPrice.setError("required");
                        return true;
                    }

                    Product product = new Product();
                    product.setProductName(productName.getText().toString());
                    product.setProductPrice(productPrice.getText().toString());
                    product.setProductMinStockQuantity("10");
                    product.setProductCategoryID(categoryList.get(productCategory.getSelectedIndex()).getProductCategoryId());
                    product.setProductSupplierID(supplierList.get(productSupplier.getSelectedIndex()).getId());
                    product.setProductUnit(unitList.get(productUnit.getSelectedIndex()));


                    dbManager.addProduct(product);

                    ToastUtils.showShort("Product Added");
                    Buy buy = new Buy();
                    buy.setProduct(product);
                    Calendar calendar = Calendar.getInstance();
                    buy.setDate(format.format(calendar.getTime()));
                    buy.setQuantity(String.valueOf(itemQuantity));
                    buy.setSupplierId(supplierList.get(productSupplier.getSelectedIndex()).getId());
                    dbManager.buyProduct(buy);

                }else{

//                    Product product = new Product();
//                    product.setProductName(productName.getText().toString());
//                    product.setProductPrice(productPrice.getText().toString());
//                    product.setProductMinStockQuantity("10");
//                    product.setProductCategoryID(categoryList.get(productCategory.getSelectedIndex()).getProductCategoryId());
//                    product.setProductSupplierID(supplierList.get(productSupplier.getSelectedIndex()).getId());
//                    product.setProductUnit(unitList.get(productUnit.getSelectedIndex()));
//                    dbManager.addProduct(product);

                   int quantity =  dbManager.getProductQuantity(DBConstant.TABLE_BUY, productID);

                    ToastUtils.showShort("Product Added");
                    Buy buy = new Buy();
                    buy.setProduct(product);
                    Calendar calendar = Calendar.getInstance();
                    buy.setDate(format.format(calendar.getTime()));
                    buy.setQuantity(String.valueOf(itemQuantity +quantity));
                    buy.setSupplierId(product.getProductSupplierID());
                    dbManager.increaseBuyProduct(buy,  productID);


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
