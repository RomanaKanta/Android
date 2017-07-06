package com.smartmux.shopsy.modelclass;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by tanvir-android on 8/11/16.
 */
public class ProductModel implements Parcelable{

    String productID;
    String productName;
    String productDescription;
    String productPrice;
    String currency;
    String productThumbSmall;
    String sub_catarogy;
    String type;

    String quantity;
    String stock_status;
    String href;

    String category_name;
    ArrayList<String> imageArray;

    public ProductModel(String str){

        this.productID = str;
        this.productName = str;
        this.productDescription = str;
        this.productPrice = str;
        this.currency = str;
        this.productThumbSmall = str;
        this.stock_status = str;
        this.quantity = str;
        this.href = str;
        this.category_name = str;
        this.sub_catarogy = str;
        this.type = str;
        this.imageArray = null;

    }

    public ProductModel(String productID, String productName, String productDescription,
                        String productPrice, String currency, String productThumbSmall,
                        String stock_status, String quantity, String href, String category_name,
                        String sub_catarogy, String type, ArrayList<String> imageArray) {
        this.productID = productID;
        this.productName = productName;
        this.productDescription = productDescription;
        this.productPrice = productPrice;
        this.currency = currency;
        this.productThumbSmall = productThumbSmall;
        this.stock_status = stock_status;
        this.quantity = quantity;
        this.href = href;
        this.category_name = category_name;
        this.sub_catarogy = sub_catarogy;
        this.type = type;
        this.imageArray = imageArray;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public ArrayList<String> getImageArray() {
        return imageArray;
    }

    public void setImageArray(ArrayList<String> imageArray) {
        this.imageArray = imageArray;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductThumbSmall() {
        return productThumbSmall;
    }

    public void setProductThumbSmall(String productThumbSmall) {
        this.productThumbSmall = productThumbSmall;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getStock_status() {
        return stock_status;
    }

    public void setStock_status(String stock_status) {
        this.stock_status = stock_status;
    }

    public String getSub_catarogy() {
        return sub_catarogy;
    }

    public void setSub_catarogy(String sub_catarogy) {
        this.sub_catarogy = sub_catarogy;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel write, int flags) {

        write.writeString(productID);
        write.writeString(productName);
        write.writeString(productDescription);
        write.writeString(productPrice);
        write.writeString(currency);
        write.writeString(productThumbSmall);
        write.writeString(type);


        write.writeString(sub_catarogy);
        write.writeString(quantity);
        write.writeString(stock_status);

        write.writeString(href);

        write.writeString(category_name);

        write.writeList(imageArray);
    }

    public static final Creator<ProductModel> CREATOR
            = new Creator<ProductModel>() {
        public ProductModel createFromParcel(Parcel in) {
            return new ProductModel(in);
        }

        public ProductModel[] newArray(int size) {
            return new ProductModel[size];
        }
    };

    private ProductModel(Parcel read) {
        this.productID = read.readString();
        this.productName = read.readString();
        this.productDescription = read.readString();
        this.productPrice = read.readString();
        this.currency = read.readString();
        this.productThumbSmall = read.readString();
        this.type = read.readString();
        this.sub_catarogy = read.readString();
        this.quantity = read.readString();
        this.stock_status = read.readString();
        this.href = read.readString();

        this.category_name = read.readString();

        this.imageArray = read.readArrayList(null);
    }
}
