package com.smartmux.pos.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.smartmux.pos.model.Buy;
import com.smartmux.pos.model.Product;
import com.smartmux.pos.model.ProductCategory;
import com.smartmux.pos.model.Sell;
import com.smartmux.pos.model.Supplier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by smartmux on 6/1/16.
 */
public class DBManager extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "pos.db";
    private static final int DATABASE_VERSION = 2;

    public DBManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

setForcedUpgrade(DATABASE_VERSION);
    }

    public void addSupplier(Supplier supplier) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBConstant.supplierName, supplier.getName());
        values.put(DBConstant.supplierPhone, supplier.getPhoneNumber());
        values.put(DBConstant.supplierAddress, supplier.getAddress());
        db.insert(DBConstant.TABLE_SUPPLIER, null, values);

        db.close();

    }

    public void addCategory(String name, String type) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DBConstant.productCategoryName, name);
        values.put(DBConstant.productCategoryType, type);
        db.insert(DBConstant.TABLE_CATEGORY, null, values);

        db.close();

    }

    public void addProduct(Product product) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DBConstant.productName, product.getProductName());
        values.put(DBConstant.productPrice, product.getProductPrice());
        values.put(DBConstant.productMinStock, product.getProductMinStockQuantity());
        values.put(DBConstant.productCategoryID, product.getProductCategoryID());
        values.put(DBConstant.productSupplierId, product.getProductSupplierID());
        values.put(DBConstant.productUnit, product.getProductUnit());
        db.insert(DBConstant.TABLE_PRODUCT, null, values);

        db.close();

    }


    public void sellProduct(Sell sell, int currentQuantity) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DBConstant.productId, sell.getProduct().getProductID());
        values.put(DBConstant.categoryId, sell.getProduct().getProductCategoryID());
        values.put(DBConstant.supplierId, sell.getProduct().getProductSupplierID());
        values.put(DBConstant.quantity, sell.getQuantity());
        values.put(DBConstant.invoiceId, sell.getInvoicID());
        values.put(DBConstant.date, sell.getDate());
        values.put(DBConstant.customerID, sell.getCustomerId());
        db.insert(DBConstant.TABLE_SELL, null, values);

        values = new ContentValues();

        values.put(DBConstant.quantity, currentQuantity - Integer.parseInt(sell.getQuantity()));
        db.update(DBConstant.TABLE_BUY, values, DBConstant.productId + " = ?",
                new String[]{sell.getProduct().getProductID()});

        db.close();

    }

    public void buyProduct(Buy buy) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DBConstant.quantity, buy.getQuantity());
        values.put(DBConstant.date, buy.getDate());
        values.put(DBConstant.supplierId, buy.getSupplierId());
        db.insert(DBConstant.TABLE_BUY, null, values);

        db.close();

    }

    public void increaseBuyProduct(Buy buy, String pro_id) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();


        values.put(DBConstant.quantity, buy.getQuantity());
        values.put(DBConstant.date, buy.getDate());
        values.put(DBConstant.supplierId, buy.getSupplierId());

        db.update(DBConstant.TABLE_BUY, values, DBConstant.productId + "='"+ pro_id + "'", null);
        db.close();

    }

    public int getRowCount(String table) {

        String countQuery = "SELECT  * FROM " + table;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();

        return rowCount;
    }

    public List<Product> getProduct(String query) {

        List<Product> list = new ArrayList<Product>();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {

                Product product = new Product();
                product.setProductID(cursor.getString(cursor.getColumnIndex(DBConstant.productID)));
                product.setProductName(cursor.getString(cursor.getColumnIndex(DBConstant.productName)));
                product.setProductCategoryID(cursor.getString(cursor.getColumnIndex(DBConstant.productCategoryID)));
                product.setProductPrice(cursor.getString(cursor.getColumnIndex(DBConstant.productPrice)));
                product.setProductUnit(cursor.getString(cursor.getColumnIndex(DBConstant.productUnit)));
                product.setProductSupplierID(cursor.getString(cursor.getColumnIndex(DBConstant.productSupplierId)));

                list.add(product);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return list;
    }

    public Product getProductByID(String id) {

        Product product = new Product();

        SQLiteDatabase db = this.getWritableDatabase();

        String query = "SELECT * FROM " + DBConstant.TABLE_PRODUCT + " WHERE " + DBConstant.productID + " = " + id;

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {


                product.setProductID(cursor.getString(cursor.getColumnIndex(DBConstant.productID)));
                product.setProductName(cursor.getString(cursor.getColumnIndex(DBConstant.productName)));
                product.setProductCategoryID(cursor.getString(cursor.getColumnIndex(DBConstant.productCategoryID)));
                product.setProductPrice(cursor.getString(cursor.getColumnIndex(DBConstant.productPrice)));
                product.setProductUnit(cursor.getString(cursor.getColumnIndex(DBConstant.productUnit)));
                product.setProductSupplierID(cursor.getString(cursor.getColumnIndex(DBConstant.productSupplierId)));

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return product;
    }

    public List<Product> getAllProduct() {

        List<Product> list = new ArrayList<Product>();
        String query = "SELECT * FROM " + DBConstant.TABLE_PRODUCT;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {

                Product product = new Product();
                String productID = cursor.getString(cursor.getColumnIndex(DBConstant.productID));
                product.setProductID(productID);
                product.setProductName(cursor.getString(cursor.getColumnIndex(DBConstant.productName)));
                product.setProductCategoryID(cursor.getString(cursor.getColumnIndex(DBConstant.productCategoryID)));
                product.setProductPrice(cursor.getString(cursor.getColumnIndex(DBConstant.productPrice)));
                product.setProductUnit(cursor.getString(cursor.getColumnIndex(DBConstant.productUnit)));
                product.setProductSupplierID(cursor.getString(cursor.getColumnIndex(DBConstant.productSupplierId)));

                String subQuery = "SELECT quantity FROM Buy WHERE product_id =" + productID;

                Cursor cursorProduct = db.rawQuery(subQuery, null);
                if (cursorProduct.moveToFirst()) {
                    product.setProductQuantity(cursorProduct.getString(cursorProduct.getColumnIndex(DBConstant.quantity)));
                }

                list.add(product);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return list;
    }

    public List<Product> getCategoryProduct(String categoryID) {

        List<Product> list = new ArrayList<Product>();
        String query = "SELECT * FROM " + DBConstant.TABLE_PRODUCT + " WHERE " + DBConstant.productCategoryID + " = " + categoryID;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {

                Product product = new Product();
                String productID = cursor.getString(cursor.getColumnIndex(DBConstant.productID));
                product.setProductID(productID);
                product.setProductName(cursor.getString(cursor.getColumnIndex(DBConstant.productName)));
                product.setProductCategoryID(cursor.getString(cursor.getColumnIndex(DBConstant.productCategoryID)));
                product.setProductPrice(cursor.getString(cursor.getColumnIndex(DBConstant.productPrice)));
                product.setProductUnit(cursor.getString(cursor.getColumnIndex(DBConstant.productUnit)));
                product.setProductSupplierID(cursor.getString(cursor.getColumnIndex(DBConstant.productSupplierId)));

                String subQuery = "SELECT quantity FROM Buy WHERE product_id =" + productID;

                Cursor cursorProduct = db.rawQuery(subQuery, null);
                if (cursorProduct.moveToFirst()) {
                    product.setProductQuantity(cursorProduct.getString(cursorProduct.getColumnIndex(DBConstant.quantity)));
                }

                list.add(product);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return list;
    }

    public List<ProductCategory> getAllCategory() {

        List<ProductCategory> list = new ArrayList<ProductCategory>();
        String query = "SELECT * FROM " + DBConstant.TABLE_CATEGORY;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {

                ProductCategory productCategory = new ProductCategory();
                productCategory.setProductCategoryId(cursor.getString(cursor.getColumnIndex(DBConstant.productCategoryId)));
                productCategory.setGetProductCategoryName(cursor.getString(cursor.getColumnIndex(DBConstant.productCategoryName)));

                System.out.println(productCategory.getProductCategoryId() + " " + productCategory.getGetProductCategoryName());
                list.add(productCategory);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return list;
    }


    public List<Supplier> getAllSupplier() {

        List<Supplier> list = new ArrayList<Supplier>();
        String query = "SELECT * FROM " + DBConstant.TABLE_SUPPLIER;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {

                Supplier supplier = new Supplier();
                supplier.setId(cursor.getString(cursor.getColumnIndex(DBConstant.supplierID)));
                supplier.setName(cursor.getString(cursor.getColumnIndex(DBConstant.supplierName)));
                supplier.setAddress(cursor.getString(cursor.getColumnIndex(DBConstant.supplierPhone)));
                supplier.setPhoneNumber(cursor.getString(cursor.getColumnIndex(DBConstant.supplierAddress)));

                list.add(supplier);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return list;
    }


    public List<HashMap<String, String>> getAllProducts() {

        List<HashMap<String, String>> list = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "select * from Category";



        Cursor cursor = db.rawQuery(query, null);
        Log.d("cursor ", "" + cursor.getCount());
        /*String[] columnNames = cursor.getColumnNames();*/

        if (cursor.moveToFirst()) {
            do {

                HashMap<String, String> map = new HashMap<>();

                String catagoryID=cursor.getString(cursor.getColumnIndex(DBConstant.productCategoryId));

                map.put("categoryId", catagoryID);
                map.put("productCategoryName", cursor.getString(cursor.getColumnIndex(DBConstant.productCategoryName)));


                String subQuery = "SELECT COUNT(DISTINCT product_id) AS NumberOfItem FROM Buy WHERE catregory_id = " + catagoryID;

                Cursor cursorProduct = db.rawQuery(subQuery, null);
                if (cursorProduct.moveToFirst()) {
                    map.put("bq", cursorProduct.getString(cursorProduct.getColumnIndex("NumberOfItem")));
                }

                list.add(map);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        Log.d("list", "" + list);
        return list;
    }


    public List<Sell> getAllSell(String query) {

        List<Sell> list = new ArrayList<Sell>();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        System.out.println(query + "  " + cursor.getColumnNames());
        if (cursor.moveToFirst()) {
            do {
                Sell sell = new Sell();
                String productID = cursor.getString(cursor.getColumnIndex(DBConstant.productId));
                String categoryID = cursor.getString(cursor.getColumnIndex(DBConstant.categoryId));
                String subQuery = "select * from " + DBConstant.TABLE_PRODUCT + " where " + DBConstant.TABLE_PRODUCT + "." + DBConstant.productID + " = '" + productID + "'"
                        + " and " + DBConstant.TABLE_PRODUCT + "." + DBConstant.productCategoryID + " = '" + categoryID + "'";
                Product product = new Product();
                Cursor cursorProduct = db.rawQuery(subQuery, null);
                if (cursorProduct.moveToFirst()) {
                    product.setProductID(cursorProduct.getString(cursorProduct.getColumnIndex(DBConstant.productID)));
                    product.setProductName(cursorProduct.getString(cursorProduct.getColumnIndex(DBConstant.productName)));
                    product.setProductCategoryID(cursorProduct.getString(cursorProduct.getColumnIndex(DBConstant.productCategoryID)));
                    product.setProductPrice(cursorProduct.getString(cursorProduct.getColumnIndex(DBConstant.productPrice)));
                    product.setProductUnit(cursorProduct.getString(cursorProduct.getColumnIndex(DBConstant.productUnit)));
                    product.setProductSupplierID(cursorProduct.getString(cursorProduct.getColumnIndex(DBConstant.productSupplierId)));
                }
                sell.setProduct(product);
                sell.setCustomerId(cursor.getString(cursor.getColumnIndex(DBConstant.customerID)));
                sell.setDate(cursor.getString(cursor.getColumnIndex(DBConstant.date)));
                sell.setQuantity(cursor.getString(cursor.getColumnIndex(DBConstant.quantity)));


                list.add(sell);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
//        System.out.println(list);
        return list;
    }


    public List<String> getAllSellInvoice() {

        List<String> list = new ArrayList<String>();
        String query = "select distinct " + DBConstant.invoiceId + " from " + DBConstant.TABLE_SELL;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {

                String invoice = cursor.getString(cursor.getColumnIndex(DBConstant.invoiceId));

                list.add(invoice);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return list;
    }

    public List<String> getSellInvoice(String supplierID, String productID) {

        List<String> list = new ArrayList<String>();
        String query = "select distinct " + DBConstant.invoiceId + " from " + DBConstant.TABLE_SELL + " where " + DBConstant.supplierId + " = " + supplierID;

        if(!productID.equals("")){
            query = "select distinct " + DBConstant.invoiceId + " from " + DBConstant.TABLE_SELL + " where " + DBConstant.supplierId + " = " + supplierID
            +" and " + DBConstant.productId + " = " + productID;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {

                String invoice = cursor.getString(cursor.getColumnIndex(DBConstant.invoiceId));

                list.add(invoice);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return list;
    }


    public List<String> getBuyDate(String supplierID, String productID) {

        List<String> list = new ArrayList<String>();
        String query = "select distinct " + DBConstant.date + " from " + DBConstant.TABLE_BUY + " where " + DBConstant.supplierId + " = " + supplierID;

//        if(!productID.equals("")){
//            query = "select distinct " + DBConstant.invoiceId + " from " + DBConstant.TABLE_SELL + " where " + DBConstant.supplierId + " = " + supplierID
//                    +" and " + DBConstant.productId + " = " + productID;
//        }

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {

                String invoice = cursor.getString(cursor.getColumnIndex(DBConstant.date));


                list.add(invoice);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return list;
    }

    public List<Buy> getbuyProduct(String query) {

        List<Buy> list = new ArrayList<Buy>();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        System.out.println(query + "  " + cursor.getColumnNames());
        if (cursor.moveToFirst()) {
            do {
                Buy buy = new Buy();
                String productID = cursor.getString(cursor.getColumnIndex(DBConstant.productId));
                String categoryID = cursor.getString(cursor.getColumnIndex(DBConstant.categoryId));
                String subQuery = "select * from " + DBConstant.TABLE_PRODUCT + " where " + DBConstant.TABLE_PRODUCT + "." + DBConstant.productID + " = '" + productID + "'"
                        + " and " + DBConstant.TABLE_PRODUCT + "." + DBConstant.productCategoryID + " = '" + categoryID + "'";
                Product product = new Product();
                Cursor cursorProduct = db.rawQuery(subQuery, null);
                if (cursorProduct.moveToFirst()) {
                    product.setProductID(cursorProduct.getString(cursorProduct.getColumnIndex(DBConstant.productID)));
                    product.setProductName(cursorProduct.getString(cursorProduct.getColumnIndex(DBConstant.productName)));
                    product.setProductCategoryID(cursorProduct.getString(cursorProduct.getColumnIndex(DBConstant.productCategoryID)));
                    product.setProductPrice(cursorProduct.getString(cursorProduct.getColumnIndex(DBConstant.productPrice)));
                    product.setProductUnit(cursorProduct.getString(cursorProduct.getColumnIndex(DBConstant.productUnit)));
                    product.setProductSupplierID(cursorProduct.getString(cursorProduct.getColumnIndex(DBConstant.productSupplierId)));
                }
                buy.setProduct(product);
                buy.setQuantity(cursor.getString(cursor.getColumnIndex(DBConstant.quantity)));


                list.add(buy);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
//        System.out.println(list);
        return list;
    }


    public int getProductQuantity(String table, String productId) {

        String query = "select quantity from " + table + " where product_id ='" + productId + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        int quantity = 0;
        if (cursor.moveToFirst()) {

            quantity = Integer.parseInt(cursor.getString(cursor.getColumnIndex(DBConstant.quantity)));

        }

        cursor.close();
        db.close();
        return quantity;
    }

}
