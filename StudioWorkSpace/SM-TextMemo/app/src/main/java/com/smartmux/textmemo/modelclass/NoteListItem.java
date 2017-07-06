package com.smartmux.textmemo.modelclass;

public class NoteListItem {
	
	int 	image;
	String 	title;
	String 	size;
	String 	dateTime;
	String 	optional;
	private boolean isChecked = false;
	long fileSizeInLong;
	
	public NoteListItem(int image,String title,String size,String dateTime,String optional) 
	{
		this.image  	= image;
		this.title 		= title;
		this.size 		= size;
		this.dateTime 	= dateTime;
		this.optional	= optional;
		
	}
	
	public NoteListItem(int image,String title,String size,String dateTime,String optional,boolean check,long fileSizeInLong) 
	{
		this.image  	= image;
		this.title 		= title;
		this.size 		= size;
		this.dateTime 	= dateTime;
		this.optional	= optional;
		this.isChecked = check;
		this.fileSizeInLong=fileSizeInLong;
		
	}
	
	public long getFileSizeInLong() {
		return fileSizeInLong;
	}

	public void setFileSizeInLong(long fileSizeInLong) {
		this.fileSizeInLong = fileSizeInLong;
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
	public boolean isChecked() {
		return isChecked;
	}

	public void setIsChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
}
