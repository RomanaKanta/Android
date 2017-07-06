package com.smartmux.voicememo.modelclass;

public class CommonItemRow {
	
	int 	image;
	String 	title;
	String 	size;
	String 	dateTime;
	String 	optional;
	
	
	public CommonItemRow(int image,String title,String size,String dateTime,String optional) 
	{
		this.image  	= image;
		this.title 		= title;
		this.size 		= size;
		this.dateTime 	= dateTime;
		this.optional	= optional;
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
}
