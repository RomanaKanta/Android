package com.smartmux.pos.model;

/**
 * Created by smartmux on 6/1/16.
 */
public class ProductCategory {

    String productCategoryId;
    String getProductCategoryName;
    String getProductCategoryType;


    public String getProductCategoryId() {
        return productCategoryId;
    }

    public void setProductCategoryId(String productCategoryId) {
        this.productCategoryId = productCategoryId;
    }

    public String getGetProductCategoryName() {
        return getProductCategoryName;
    }

    public void setGetProductCategoryName(String getProductCategoryName) {
        this.getProductCategoryName = getProductCategoryName;
    }

    public String getGetProductCategoryType() {
        return getProductCategoryType;
    }

    public void setGetProductCategoryType(String getProductCategoryType) {
        this.getProductCategoryType = getProductCategoryType;
    }
}
