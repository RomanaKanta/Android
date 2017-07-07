package com.smartmux.trackyourkids.mapactivities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Romana on 7/3/17.
 */
public class ModelClass implements Parcelable {

    String name;
    String distance;
    String image_url;
    String lat;
    String lon;

    public ModelClass(String name, String distance, String image_url, String lat, String lon) {
        this.name = name;
        this.distance = distance;
        this.image_url = image_url;
        this.lat = lat;
        this.lon = lon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel write, int flags) {

        write.writeString(name);
        write.writeString(distance);
        write.writeString(image_url);
        write.writeString(lat);
        write.writeString(lon);


    }

    public static final Creator<ModelClass> CREATOR
            = new Creator<ModelClass>() {
        public ModelClass createFromParcel(Parcel in) {
            return new ModelClass(in);
        }

        public ModelClass[] newArray(int size) {
            return new ModelClass[size];
        }
    };

    private ModelClass(Parcel read) {
        this.name           = read.readString();
        this.distance     = read.readString();
        this.image_url            = read.readString();
        this.lat         = read.readString();
        this.lon     = read.readString();

    }


}
