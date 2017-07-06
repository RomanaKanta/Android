package com.smartmux.banglamusic.model;

public class RowItemForSong {
	
	String titleSong;
	String artist;
	
	public RowItemForSong(String titleSong, String artist) 
	{
		this.titleSong = titleSong;
		this.artist = artist;
	}
	public String getTitleSong() 
	{
		return titleSong;
	}
	
	public void setTitleSong(String titleSong) 
	{
		this.titleSong = titleSong;
	}
	
	public String getSongArtist() 
	{
		return artist;
	}
	
	public void setSongArtist(String artist) 
	{
		this.artist = artist;
	}
	
}

