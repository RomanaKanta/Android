package com.smartmux.androidapp.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.smartmux.androidapp.util.Constant;

public class TutorialModel {
	
	private String id ;
	private String root_title ;
	private String category;
	private String sub_category ;
	private String title ;
	private String sub_title ;
	private String label_english;
	private String label_japanese;
	private String option_1 ;
	private String option_2;
	private String option_3 ;
	private String option_4;
	private String isFav;
	
	public TutorialModel() {
		super();
	}
	
	  public static TutorialModel fromJson(JSONObject jsonObject) {
		  TutorialModel tutorial = new TutorialModel();
	        
	  	try {
	  		tutorial.id = jsonObject.getString(Constant.id);
	  		tutorial.root_title = jsonObject.getString(Constant.root_title);
	  		tutorial.category = jsonObject.getString(Constant.category);
	  		tutorial.sub_category = jsonObject.getString(Constant.sub_category);
	  		tutorial.title = jsonObject.getString(Constant.title);
	  		tutorial.sub_title = jsonObject.getString(Constant.sub_title);
	  		tutorial.label_english = jsonObject.getString(Constant.label_english);
	  		tutorial.label_japanese = jsonObject.getString(Constant.label_japanese);
	  		tutorial.option_1 = jsonObject.getString(Constant.option_1);
	  		tutorial.option_2 = jsonObject.getString(Constant.option_2);
	  		tutorial.option_3 = jsonObject.getString(Constant.option_3);
	  		tutorial.option_4 = jsonObject.getString(Constant.option_4);
	  		tutorial.isFav = "0";
	        } catch (JSONException e) {
	            e.printStackTrace();
	            return null;
	        }
	  	return tutorial;
	  }
	  
	  
	  

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRoot_title() {
		return root_title;
	}

	public void setRoot_title(String root_title) {
		this.root_title = root_title;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getSub_category() {
		return sub_category;
	}

	public void setSub_category(String sub_category) {
		this.sub_category = sub_category;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}


	public String getSub_title() {
		return sub_title;
	}

	public void setSub_title(String sub_title) {
		this.sub_title = sub_title;
	}

	public String getLabel_english() {
		return label_english;
	}

	public void setLabel_english(String label_english) {
		this.label_english = label_english;
	}

	public String getLabel_japanese() {
		return label_japanese;
	}

	public void setLabel_japanese(String label_japanese) {
		this.label_japanese = label_japanese;
	}

	public String getOption_1() {
		return option_1;
	}

	public void setOption_1(String option_1) {
		this.option_1 = option_1;
	}

	public String getOption_2() {
		return option_2;
	}

	public void setOption_2(String option_2) {
		this.option_2 = option_2;
	}

	public String getOption_3() {
		return option_3;
	}

	public void setOption_3(String option_3) {
		this.option_3 = option_3;
	}

	public String getOption_4() {
		return option_4;
	}

	public void setOption_4(String option_4) {
		this.option_4 = option_4;
	}

	public String getIsFav() {
		return isFav;
	}

	public void setIsFav(String isFav) {
		this.isFav = isFav;
	}
	  
	  

}
