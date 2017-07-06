package com.smartmux.couriermoc.modelclass;

/**
 * Created by tanvir-android on 7/27/16.
 */
public class HistoryData {

    String orderID;
    String date;
    String status;

    public HistoryData(String orderID, String date, String status) {
        this.orderID = orderID;
        this.date = date;
        this.status = status;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
