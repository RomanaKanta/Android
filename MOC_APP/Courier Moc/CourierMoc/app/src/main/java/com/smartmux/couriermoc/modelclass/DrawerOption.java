package com.smartmux.couriermoc.modelclass;

/**
 * Created by tanvir-android on 7/26/16.
 */
public class DrawerOption {

    Integer icon;
    String title;

    public DrawerOption(Integer icon, String title) {
        this.icon = icon;
        this.title = title;
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
}
