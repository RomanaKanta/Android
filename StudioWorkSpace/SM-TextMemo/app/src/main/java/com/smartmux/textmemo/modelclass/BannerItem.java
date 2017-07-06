package com.smartmux.textmemo.modelclass;

/**
 * Created by smartmux on 3/21/16.
 */
public class BannerItem {

	public  String text="";
	public  String thumbUrl="";
	public  String actionUrl="";
	public  Boolean notifyShow=false;

	public BannerItem() {
	}

	public BannerItem(String text, String thumbUrl,
			String actionUrl,Boolean notify) {
		this.text = text;
		this.thumbUrl = thumbUrl;
		this.actionUrl = actionUrl;
        this.notifyShow = notify;
	}


	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getThumbUrl() {
		return thumbUrl;
	}

	public void setThumbUrl(String thumbUrl) {
		this.thumbUrl = thumbUrl;
	}

	public String getActionUrl() {
		return actionUrl;
	}

	public void setActionUrl(String actionUrl) {
		this.actionUrl = actionUrl;
	}

    public Boolean getNotifyShow() {
        return notifyShow;
    }

    public void setNotifyShow(Boolean notifyShow) {
        this.notifyShow = notifyShow;
    }
}
