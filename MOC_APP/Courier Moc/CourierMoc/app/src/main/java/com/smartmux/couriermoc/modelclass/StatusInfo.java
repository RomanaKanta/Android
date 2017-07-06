package com.smartmux.couriermoc.modelclass;

/**
 * Created by tanvir-android on 8/9/16.
 */
public class StatusInfo {

    String orderID = "";
    String status = "";
    String from = "";
    String to = "";
    String distance = "";
    String cost = "";
    String type = "";
    String weight = "";
    String date = "";
    String comment = "";

    public StatusInfo() {
    }

    public StatusInfo(String orderID, String status, String from, String to, String distance,
                      String cost, String type, String weight, String date, String comment) {
        this.orderID = orderID;
        this.status = status;
        this.from = from;
        this.to = to;
        this.distance = distance;
        this.cost = cost;
        this.type = type;
        this.weight = weight;
        this.date = date;
        this.comment = comment;
    }

    public String getOrderID() {
        return orderID;
    }

    public String getStatus() {
        return status;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getDistance() {
        return distance;
    }

    public String getCost() {
        return cost;
    }

    public String getType() {
        return type;
    }

    public String getWeight() {
        return weight;
    }

    public String getDate() {
        return date;
    }

    public String getComment() {
        return comment;
    }
}
