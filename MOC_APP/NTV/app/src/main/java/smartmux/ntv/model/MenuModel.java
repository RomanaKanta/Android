package smartmux.ntv.model;

/**
 * Created by smartmux on 9/1/16.
 */
public class MenuModel {

    private String id;
    private String name;
    private String rssUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRssUrl() {
        return rssUrl;
    }

    public void setRssUrl(String rssUrl) {
        this.rssUrl = rssUrl;
    }
}
