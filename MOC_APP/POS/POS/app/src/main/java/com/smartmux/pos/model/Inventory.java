package com.smartmux.pos.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by smartmux on 6/1/16.
 */
public class Inventory {

    List<Buy> inventoryList = new ArrayList<>();

    public List<Buy> getInventoryList() {
        return inventoryList;
    }

    public void setInventoryList(List<Buy> inventoryList) {
        this.inventoryList = inventoryList;
    }
}
