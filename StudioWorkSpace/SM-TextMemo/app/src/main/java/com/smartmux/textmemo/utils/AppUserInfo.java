package com.smartmux.textmemo.utils;


import java.io.File;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class AppUserInfo {

	private String  QUESTION_TAG 	= "QUESTION_TAG";
	private String  ANSWER_TAG		= "ANSWER_TAG";
	private String  PASSWORD_TAG 	= "PASSWORD_TAG";
	private Context context;
	
	public AppUserInfo(Context context){
		this.context = context;
	}
	
	public void createRootFolder(){
		File file = new File(AppExtra.APP_ROOT_FOLDER);
		if (!file.exists()) {
			file.mkdir();
		}
	}
	
	public String getQuestion() 
	{
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context);
		return sharedPreferences.getString(QUESTION_TAG, "");
	}
	
	public void setQuestion(String question)
	{
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context);
		Editor editor = sharedPreferences.edit();
		editor.putString(QUESTION_TAG, question);
		editor.commit();
	}
	
	public String getAnswer() 
	{
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context);
		return sharedPreferences.getString(ANSWER_TAG, "");
	}
	
	public void setAnswer(String answer) 
	{
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context);
		Editor editor = sharedPreferences.edit();
		editor.putString(ANSWER_TAG, answer);
		editor.commit();
	}
	
	public String getPassword() 
	{
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context);
		return sharedPreferences.getString(PASSWORD_TAG, "");
	}
	
	public void setPassword(String password) 
	{
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context);
		Editor editor = sharedPreferences.edit();
		editor.putString(PASSWORD_TAG, password);
		editor.commit();
	}
	
}