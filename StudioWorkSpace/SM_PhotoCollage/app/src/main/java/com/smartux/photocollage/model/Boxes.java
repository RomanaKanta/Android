package com.smartux.photocollage.model;

import android.graphics.Point;

import java.util.ArrayList;

public class Boxes {
	private ArrayList<Point> points;
	private ArrayList<Point> pathPoints;
	private int leftMargin;
	private int topMargin;
	private int rightMargin;
	private int bottmMargin;
	private int boxWidth;
	private int boxHeight;
	private int max_X = 0;
	private int max_Y = 0;
	private int min_X = 0;
	private int min_Y = 0;
    private String position;
	
	

	public Boxes(int leftMargin,int topMargin,int rightMargin,int bottmMargin) {
		this.leftMargin=leftMargin;
		this.topMargin=topMargin;
		this.rightMargin=rightMargin;
		this.bottmMargin=bottmMargin;
	}

    public Boxes(String position, int leftMargin,int topMargin,int rightMargin,int bottmMargin) {
        this.position = position;
        this.leftMargin=leftMargin;
        this.topMargin=topMargin;
        this.rightMargin=rightMargin;
        this.bottmMargin=bottmMargin;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setPoints(ArrayList<Point> points) {
		this.points = points;
	}

	public int getLeftMargin() {
		return leftMargin;
	}

	public int getTopMargin() {
		return topMargin;
	}

	public int getRightMargin() {
		return rightMargin;
	}

	public int getBottmMargin() {
		return bottmMargin;
	}

	public ArrayList<Point> getPoints() {
		return points;
	}
	
	
	public int getBoxWidth() {
		return boxWidth;
	}

	public void setBoxWidth(int boxWidth) {
		this.boxWidth = boxWidth;
	}

	public int getBoxHeight() {
		return boxHeight;
	}

	public void setBoxHeight(int boxHeight) {
		this.boxHeight = boxHeight;
	}

	public int getMax_X() {
		return max_X;
	}

	public void setMax_X(int max_X) {
		this.max_X = max_X;
	}

	public int getMax_Y() {
		return max_Y;
	}

	public void setMax_Y(int max_Y) {
		this.max_Y = max_Y;
	}

	public int getMin_X() {
		return min_X;
	}

	public void setMin_X(int min_X) {
		this.min_X = min_X;
	}

	public int getMin_Y() {
		return min_Y;
	}

	public void setMin_Y(int min_Y) {
		this.min_Y = min_Y;
	}
	
	public void setPathPoints(ArrayList<Point> points) {
		this.pathPoints = points;
	}
	public ArrayList<Point> getPathPoints() {
		
		
		return pathPoints;
		
	}




}
