package com.smartux.photocollage.model;

public class ColorItem {
	String firstColor, secondColor, thirdColor;
	
	public ColorItem() {
	}

	public ColorItem(String firstColor,String secondColor,String thirdColor) {
		this.firstColor = firstColor;
		this.secondColor = secondColor;
		this.thirdColor =thirdColor;
	}

	public String getFirstColor() {
		return firstColor;
	}

	public void setFirstColor(String firstColor) {
		this.firstColor = firstColor;
	}

	public String getSecondColor() {
		return secondColor;
	}

	public void setSecondColor(String secondColor) {
		this.secondColor = secondColor;
	}

	public String getThirdColor() {
		return thirdColor;
	}

	public void setThirdColor(String thirdColor) {
		this.thirdColor = thirdColor;
	}

	

}
