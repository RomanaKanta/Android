package com.roundflat.musclecard.model;

public class ModelClass {

	Integer num;
	String flag;

	public ModelClass() {

	}

	public ModelClass(Integer num, String flag) {

		this.num = num;
		this.flag = flag;
	}

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}
}
