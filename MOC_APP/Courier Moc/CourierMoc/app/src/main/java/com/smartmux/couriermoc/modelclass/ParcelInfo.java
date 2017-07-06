package com.smartmux.couriermoc.modelclass;

/**
 * Created by tanvir-android on 8/4/16.
 */
public class ParcelInfo {

    String senderName;
    String senderAddress;
    String receiverName;
    String receiverAddress;
    String distance;
    String cost;
    String type;
    String weight;
    String date;

    public ParcelInfo(String senderName,
                      String senderAddress,
                      String receiverName,
                      String receiverAddress,
                      String distance,
                      String cost,
                      String type,
                      String weight,
                      String date) {

        this.senderName = senderName;
        this.senderAddress = senderAddress;
        this.receiverName = receiverName;
        this.receiverAddress = receiverAddress;
        this.distance = distance;
        this.cost = cost;
        this.type = type;
        this.weight = weight;
        this.date = date;

    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
