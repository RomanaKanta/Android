package com.smartmux.photocutter.modelclass;

public class ListData {
	private Integer mImage;
	private String mText;
	private Integer mid;
	private String mBackground_color;

	public ListData() {
	}

	public ListData(int id, int image, String text, String bg_color) {
		this.mImage = image;
		this.mText = text;
		this.mid = id;
		this.mBackground_color= bg_color;
	}

    public ListData(int id, int image, String text) {
        this.mImage = image;
        this.mText = text;
        this.mid = id;
    }

	public Integer getmImage() {
		return mImage;
	}

	public void setmImage(Integer mImage) {
		this.mImage = mImage;
	}

	public String getmText() {
		return mText;
	}

	public void setmText(String mText) {
		this.mText = mText;
	}

	public Integer getMid() {
		return mid;
	}

	public void setMid(Integer mid) {
		this.mid = mid;
	}

	public String getmBackground_color() {
		return mBackground_color;
	}

	public void setmBackground_color(String mBackground_color) {
		this.mBackground_color = mBackground_color;
	}



}
