package com.ksproject.krishop.modelclass;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by tanvir-android on 8/11/16.
 */
public class ProductModel implements Parcelable{

          String Caterogy;
          ArrayList<Products>  productList;


    public ProductModel(){}

    public ProductModel(String branch_id, ArrayList<Products> productList) {
        this.Caterogy = branch_id;
        this.productList = productList;
    }

    public String getCaterogy() {
        return Caterogy;
    }

    public void setCaterogy(String caterogy) {
        this.Caterogy = caterogy;
    }

    public ArrayList<Products> getProductList() {
        return productList;
    }

    public void setProductList(ArrayList<Products> productList) {
        this.productList = productList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel write, int flags) {

        write.writeString(Caterogy);

        write.writeArray(productList.toArray());
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

        this.Caterogy = read.readString();

        final ArrayList<Products> tmpList =
                read.readArrayList(Products.class.getClassLoader());
        this.productList = new ArrayList<Products>();
        for (int loopIndex=0;loopIndex != tmpList.size();loopIndex++) {
            productList.add(tmpList.get(loopIndex));
        }

    }
}
