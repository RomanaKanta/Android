package com.smartmux.outofmilk.modelclass;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by tanvir-android on 8/25/16.
 */
public class UserInformation implements Parcelable{

    String name;
    String email;

    public UserInformation() {

    }

    public UserInformation(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel write, int flags) {
        write.writeString(name);
        write.writeString(email);
    }

    public static final Parcelable.Creator<UserInformation> CREATOR
            = new Parcelable.Creator<UserInformation>() {
        public UserInformation createFromParcel(Parcel in) {
            return new UserInformation(in);
        }

        public UserInformation[] newArray(int size) {
            return new UserInformation[size];
        }
    };

    private UserInformation(Parcel read) {
        this.name = read.readString();
        this.email = read.readString();
    }
}
