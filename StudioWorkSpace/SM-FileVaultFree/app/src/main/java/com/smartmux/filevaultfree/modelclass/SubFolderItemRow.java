package com.smartmux.filevaultfree.modelclass;

public class SubFolderItemRow {

	int image;
	String title;
	String subTitle;

	public SubFolderItemRow(String title, String subTitle, int image) {
		this.image = image;
		this.title = title;
		this.subTitle = subTitle;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubTitle() {
		return subTitle;
	}

	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

	public int getImage() {
		return image;
	}

	public void setImage(int image) {
		this.image = image;
	}
}
