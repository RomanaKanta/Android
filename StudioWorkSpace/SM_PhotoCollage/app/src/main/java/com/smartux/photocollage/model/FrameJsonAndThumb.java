package com.smartux.photocollage.model;

public class FrameJsonAndThumb {
	private String frameJson;
	private int frameThumb;
	public FrameJsonAndThumb(String frameJson, int frameThumb) {
		super();
		this.frameJson = frameJson;
		this.frameThumb = frameThumb;
	}
	public String getFrameJson() {
		return frameJson;
	}
	public void setFrameJson(String frameJson) {
		this.frameJson = frameJson;
	}
	public int getFrameThumb() {
		return frameThumb;
	}
	public void setFrameThumb(int frameThumb) {
		this.frameThumb = frameThumb;
	}
	
	

}
