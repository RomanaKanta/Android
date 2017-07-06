package com.smartux.photocollage.model;

/**
 * Created by tanvir-android on 7/13/16.
 */
public class StickerCatagory {

    Integer icon;
    String title;
    String color;

    public StickerCatagory(Integer icon, String title, String color) {
        this.icon = icon;
        this.title = title;
        this.color = color;
    }

    public Integer getIcon() {
        return icon;
    }

    public void setIcon(Integer icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
