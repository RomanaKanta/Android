package com.smartmux.fotolibs.modelclass;

public class FrameModel {
	private  int number;
	private int thumb;
	private int frame;
	private int landscape;
	public FrameModel(int number, int thumb, int frame) {
		super();
		this.number = number;
		this.thumb = thumb;
		this.frame = frame;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public int getThumb() {
		return thumb;
	}
	public void setThumb(int thumb) {
		this.thumb = thumb;
	}
	public int getFrame() {
		return frame;
	}
	public void setFrame(int frame) {
		this.frame = frame;
	}
	
	

}
