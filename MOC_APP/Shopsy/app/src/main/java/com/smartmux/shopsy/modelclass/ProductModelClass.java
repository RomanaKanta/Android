//package com.smartmux.shopsy.modelclass;
//
//import android.os.Parcel;
//import android.os.Parcelable;
//
//import java.util.ArrayList;
//
///**
// * Created by tanvir-android on 8/11/16.
// */
//public class ProductModelClass implements Parcelable{
//
//    String productID;
//    String productName;
//    String productDescription;
//    String productPrice;
//    String productThumb;
//    String productType;
//    String productImage;
//    ArrayList<String> productSize;
//    ArrayList<String> productColor;
//
//    String brand_id;
//    String brand;
//    String tag;
//
//    String category;
//    String sub_category;
//
//    public ProductModelClass(){}
//
//    public ProductModelClass(String str){
//
//        this.brand_id = str;
//        this.brand = str;
//        this.category = str;
//        this.sub_category = str;
//        this.productID = str;
//        this.productName = str;
//        this.productPrice = str;
//        this.productType = str;
//        this.productThumb = str;
//        this.productDescription = str;
//        this.productImage = str;
//        this.tag = str;
//        this.productColor = null;
//        this.productSize = null;
//
//    }
//
//    public ProductModelClass(String brand_id, String brand, String category,
//                             String sub_category, String productID, String productName,
//                             String productPrice, String productType, String productThumb,
//                             String productDescription, String productImage, String tag,
//                             ArrayList<String> productColor, ArrayList<String> productSize) {
//        this.brand_id = brand_id;
//        this.brand = brand;
//        this.category = category;
//        this.sub_category = sub_category;
//        this.productID = productID;
//        this.productName = productName;
//        this.productPrice = productPrice;
//        this.productType = productType;
//        this.productThumb = productThumb;
//        this.productDescription = productDescription;
//        this.productImage = productImage;
//        this.tag = tag;
//        this.productColor = productColor;
//        this.productSize = productSize;
//    }
//
//    public String getTag() {
//        return tag;
//    }
//
//    public void setTag(String tag) {
//        this.tag = tag;
//    }
//
//    public String getBrand() {
//        return brand;
//    }
//
//    public void setBrand(String brand) {
//        this.brand = brand;
//    }
//
//    public String getBrand_id() {
//        return brand_id;
//    }
//
//    public void setBrand_id(String brand_id) {
//        this.brand_id = brand_id;
//    }
//
//    public String getCategory() {
//        return category;
//    }
//
//    public void setCategory(String category) {
//        this.category = category;
//    }
//
//    public ArrayList<String> getProductColor() {
//        return productColor;
//    }
//
//    public void setProductColor(ArrayList<String> productColor) {
//        this.productColor = productColor;
//    }
//
//    public String getProductDescription() {
//        return productDescription;
//    }
//
//    public void setProductDescription(String productDescription) {
//        this.productDescription = productDescription;
//    }
//
//    public String getProductID() {
//        return productID;
//    }
//
//    public void setProductID(String productID) {
//        this.productID = productID;
//    }
//
//    public String getProductImage() {
//        return productImage;
//    }
//
//    public void setProductImage(String productImage) {
//        this.productImage = productImage;
//    }
//
//    public String getProductName() {
//        return productName;
//    }
//
//    public void setProductName(String productName) {
//        this.productName = productName;
//    }
//
//    public String getProductPrice() {
//        return productPrice;
//    }
//
//    public void setProductPrice(String productPrice) {
//        this.productPrice = productPrice;
//    }
//
//    public ArrayList<String> getProductSize() {
//        return productSize;
//    }
//
//    public void setProductSize(ArrayList<String> productSize) {
//        this.productSize = productSize;
//    }
//
//    public String getProductThumb() {
//        return productThumb;
//    }
//
//    public void setProductThumb(String productThumb) {
//        this.productThumb = productThumb;
//    }
//
//    public String getProductType() {
//        return productType;
//    }
//
//    public void setProductType(String productType) {
//        this.productType = productType;
//    }
//
//    public String getSub_category() {
//        return sub_category;
//    }
//
//    public void setSub_category(String sub_category) {
//        this.sub_category = sub_category;
//    }
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel write, int flags) {
//
//        write.writeString(productID);
//        write.writeString(productName);
//        write.writeString(productDescription);
//        write.writeString(productPrice);
//        write.writeString(productThumb);
//
//        write.writeString(productType);
//        write.writeString(productImage);
//
//        write.writeString(brand_id);
//        write.writeString(brand);
//
//        write.writeString(category);
//        write.writeString(sub_category);
//
//        write.writeList(productSize);
//        write.writeList(productColor);
//    }
//
//    public static final Creator<ProductModelClass> CREATOR
//            = new Creator<ProductModelClass>() {
//        public ProductModelClass createFromParcel(Parcel in) {
//            return new ProductModelClass(in);
//        }
//
//        public ProductModelClass[] newArray(int size) {
//            return new ProductModelClass[size];
//        }
//    };
//
//    private ProductModelClass(Parcel read) {
//        this.productID = read.readString();
//        this.productName = read.readString();
//        this.productDescription = read.readString();
//        this.productPrice = read.readString();
//        this.productThumb = read.readString();
//        this.productType = read.readString();
//        this.productImage = read.readString();
//        this.brand_id = read.readString();
//        this.brand = read.readString();
//
//        this.category = read.readString();
//        this.sub_category = read.readString();
//
//        this.productColor = read.readArrayList(null);
//        this.productSize = read.readArrayList(null);
//
//
//    }
//}
