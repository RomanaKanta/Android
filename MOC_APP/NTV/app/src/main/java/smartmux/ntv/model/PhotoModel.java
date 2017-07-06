package smartmux.ntv.model;

import java.util.ArrayList;

/**
 * Created by tanvir-android on 9/7/16.
 */
public class PhotoModel {

    String title;
    ArrayList<ItemModel> item = new ArrayList<>();

    public PhotoModel(String title, ArrayList<ItemModel> item) {
        this.title = title;
        this.item = item;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<ItemModel> getItem() {
        return item;
    }

    public void setItem(ArrayList<ItemModel> item) {
        this.item = item;
    }
}


