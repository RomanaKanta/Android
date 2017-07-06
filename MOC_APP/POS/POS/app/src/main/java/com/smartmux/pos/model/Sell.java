package com.smartmux.pos.model;

/**
 * Created by smartmux on 6/1/16.
 */
public class Sell {

    Product product;
    String quantity;
    String date;
    String customerId;
    String invoicID;


    public Sell() {

    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getInvoicID() {
        return invoicID;
    }

    public void setInvoicID(String invoicID) {
        this.invoicID = invoicID;
    }


}
