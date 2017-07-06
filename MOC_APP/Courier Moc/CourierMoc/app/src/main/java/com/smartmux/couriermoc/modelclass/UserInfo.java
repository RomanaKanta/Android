package com.smartmux.couriermoc.modelclass;

/**
 * Created by tanvir-android on 7/26/16.
 */
public class UserInfo {

    String imageUrl;
    String name;
    String phone;
    String address;
    String email;
    String password;
    String country;
    String zipCode;
    String state;
    String city;

    public UserInfo(){}

    public UserInfo(String imageUrl, String name, String phone, String address, String password, String email) {
        this.imageUrl = imageUrl;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.password = password;
        this.email = email;
    }

    public UserInfo(String imageUrl, String name, String phone, String email, String password, String country, String zipCode, String state, String city, String address) {
        this.imageUrl = imageUrl;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.email = email;
        this.password = password;
        this.country = country;
        this.zipCode = zipCode;
        this.state = state;
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
