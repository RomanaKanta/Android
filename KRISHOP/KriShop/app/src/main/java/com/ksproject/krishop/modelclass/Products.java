package com.ksproject.krishop.modelclass;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by tanvir-android on 8/26/16.
 */
public class Products implements Parcelable {

      String  catagory_id;
      String  catagory ;
      String  product_id ;
      String  product_name;
      String  description;
      String  price;
      String  currency;
      String  product_image;
      String  quantity;
      String  harvest_time;
      String  product_area;
      String  farmer_id;
      String  farmer_name;
      String  farmer_image;
      String  farmer_address;
      String  farmer_phone;

    public Products(){

    }

    public Products(String catagory_id, String catagory, String product_id, String product_name,
                    String description, String price, String currency, String product_image,
                    String quantity, String harvest_time, String product_area, String farmer_id,
                    String farmer_name, String farmer_image, String farmer_address,
                    String farmer_phone) {
        this.catagory_id = catagory_id;
        this.catagory = catagory;
        this.product_id = product_id;
        this.product_name = product_name;
        this.description = description;
        this.price = price;
        this.currency = currency;
        this.product_image = product_image;
        this.quantity = quantity;
        this.harvest_time = harvest_time;
        this.product_area = product_area;
        this.farmer_id = farmer_id;
        this.farmer_name = farmer_name;
        this.farmer_image = farmer_image;
        this.farmer_address = farmer_address;
        this.farmer_phone = farmer_phone;
    }

    public String getCatagory_id() {
        return catagory_id;
    }

    public void setCatagory_id(String catagory_id) {
        this.catagory_id = catagory_id;
    }

    public String getCatagory() {
        return catagory;
    }

    public void setCatagory(String catagory) {
        this.catagory = catagory;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getProduct_image() {
        return product_image;
    }

    public void setProduct_image(String product_image) {
        this.product_image = product_image;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getHarvest_time() {
        return harvest_time;
    }

    public void setHarvest_time(String harvest_time) {
        this.harvest_time = harvest_time;
    }

    public String getProduct_area() {
        return product_area;
    }

    public void setProduct_area(String product_area) {
        this.product_area = product_area;
    }

    public String getFarmer_id() {
        return farmer_id;
    }

    public void setFarmer_id(String farmer_id) {
        this.farmer_id = farmer_id;
    }

    public String getFarmer_name() {
        return farmer_name;
    }

    public void setFarmer_name(String farmer_name) {
        this.farmer_name = farmer_name;
    }

    public String getFarmer_image() {
        return farmer_image;
    }

    public void setFarmer_image(String farmer_image) {
        this.farmer_image = farmer_image;
    }

    public String getFarmer_address() {
        return farmer_address;
    }

    public void setFarmer_address(String farmer_address) {
        this.farmer_address = farmer_address;
    }

    public String getFarmer_phone() {
        return farmer_phone;
    }

    public void setFarmer_phone(String farmer_phone) {
        this.farmer_phone = farmer_phone;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel write, int flags) {

       write.writeString(   catagory_id    );
       write.writeString(   catagory       );
       write.writeString(   product_id     );
       write.writeString(   product_name   );
       write.writeString(   description    );
       write.writeString(   price          );
       write.writeString(   currency       );
       write.writeString(   product_image  );
       write.writeString(   quantity       );
       write.writeString(   harvest_time   );
       write.writeString(   product_area   );
       write.writeString(   farmer_id      );
       write.writeString(   farmer_name    );
       write.writeString(   farmer_image   );
       write.writeString(   farmer_address );
       write.writeString(   farmer_phone   );
    }

    public static final Creator<Products> CREATOR
            = new Creator<Products>() {
        public Products createFromParcel(Parcel in) {
            return new Products(in);
        }

        public Products[] newArray(int size) {
            return new Products[size];
        }
    };

    private Products(Parcel read) {

       this.catagory_id    = read.readString();
       this.catagory       = read.readString();
       this.product_id     = read.readString();
       this.product_name   = read.readString();
       this.description    = read.readString();
       this.price          = read.readString();
       this.currency       = read.readString();
       this.product_image  = read.readString();
       this.quantity       = read.readString();
       this.harvest_time   = read.readString();
       this.product_area   = read.readString();
       this.farmer_id      = read.readString();
       this.farmer_name    = read.readString();
       this.farmer_image   = read.readString();
       this.farmer_address = read.readString();
       this.farmer_phone   = read.readString();



    }
}
