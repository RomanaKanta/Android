package com.smartmux.banglamusic.model;

public class ItemRow {

	String image;
	String title;
	String bengaliTitle;
	String artist;
	String totalSongs;
	
	public ItemRow(String bengaliTitle, String title,String artist,String totalSongs,String image) 
	{
		this.bengaliTitle = bengaliTitle;
		this.image  = image;
		this.title 	= title;
		this.artist = artist;
		this.totalSongs = totalSongs;
	}
	
	
	public String getBengaliTitle() 
	{
		return bengaliTitle;
	}
	
	public void setBengaliTitle(String bengaliTitle) 
	{
		this.bengaliTitle = bengaliTitle;
	}
	public String getArtist() 
	{
		return artist;
	}
	
	public void setArtist(String artist) 
	{
		this.artist = artist;
	}
	public String getTotalSongs() 
	{
		return totalSongs;
	}
	
	public void setTotalSongs(String totalSongs) 
	{
		this.totalSongs = totalSongs;
	}
	public String getTitle() 
	{
		return title;
	}
	
	public void setTitle(String title) 
	{
		this.title = title;
	}
	
	public String getImage() 
	{
		return image;
	}
	
	public void setImage(String image) 
	{
		this.image = image;
	}	
}
