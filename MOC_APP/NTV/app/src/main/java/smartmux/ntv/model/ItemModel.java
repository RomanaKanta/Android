package smartmux.ntv.model;

/**
 * Created by tanvir-android on 9/7/16.
 */
public class ItemModel {

    String imageTitle;
    String imageUrl;

    public ItemModel(String imageTitle, String imageUrl) {
        this.imageTitle = imageTitle;
        this.imageUrl = imageUrl;
    }

    public String getImageTitle() {
        return imageTitle;
    }

    public void setImageTitle(String imageTitle) {
        this.imageTitle = imageTitle;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
