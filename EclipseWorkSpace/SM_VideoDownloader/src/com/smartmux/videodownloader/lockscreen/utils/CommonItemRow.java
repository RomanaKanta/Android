package com.smartmux.videodownloader.lockscreen.utils;

public class CommonItemRow {
	
	int 	image;
	String 	title;
	String 	size;
	String 	dateTime;
	String 	optional;
	String filePath;
	private boolean isChecked = false;
	
	
	public CommonItemRow(int image,String title,String size,String dateTime,String optional,String filePath) 
	{
		this.image  	= image;
		this.title 		= title;
		this.size 		= size;
		this.dateTime 	= dateTime;
		this.optional	= optional;
		this.filePath    = filePath;
	}
	
	public CommonItemRow(String title,String size,String dateTime,String optional,String filePath) 
	{
		this.title 		= title;
		this.size 		= size;
		this.dateTime 	= dateTime;
		this.optional	= optional;
		this.filePath    = filePath;
	}
	
	public int getImage() 
	{
		return image;
	}
	
	public void setImage(int image) 
	{
		this.image = image;
	}	
	
	public String getTitle() 
	{
		return title;
	}
	
	public void setTitle(String title) 
	{
		this.title = title;
	}
	
	public String getSize() 
	{
		return size;
	}
	
	public void setSize(String size) 
	{
		this.size = size;
	}
	
	public String getDateTime() 
	{
		return dateTime;
	}
	
	public void setDateTime(String dateTime) 
	{
		this.dateTime = dateTime;
	}
	
	public String getOptional() 
	{
		return optional;
	}
	
	public void setOptional(String optional) 
	{
		this.optional = optional;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	public boolean isChecked() {
		return isChecked;
	}

	public void setIsChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

}
