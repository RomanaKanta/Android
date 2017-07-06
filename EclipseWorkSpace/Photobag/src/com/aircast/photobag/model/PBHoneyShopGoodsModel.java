package com.aircast.photobag.model;

import com.aircast.photobag.R;

public class PBHoneyShopGoodsModel {
	
	private String mGoodsTitle;
	private int mGoodsIcon;
	private int mCount;
	private String mGoodsId;
	
	// This data save in local is not good 
	// better to change get this from server.
	private final int[] icons = new int[] {
		R.drawable.honey_1,
		R.drawable.honey_3,
		R.drawable.honey_6,
	};
	
	// encrypted but really useless, only causes trouble when merge them_(:3 」∠)_
	private final String[] names = new String[] {
		"ハバン斈存>ダノバニ/＞/倄148376",
		"ハバン斈存=ダノバニ/</倄148376",
		"ハバン斈存<ダノバニ/9/倄144977",
	};
	
	private final String[] goods = new String[] {
		"{j|{[Yg`ajv>151054",
		"{j|{[Zg`ajv<162336",
		"{j|{[Wg`ajv9150980",
	};
	
	public PBHoneyShopGoodsModel(int id, String title, int icon, String goodsId) {
		mCount = id;
		mGoodsTitle = title;
		mGoodsIcon = icon;
		mGoodsId = goodsId;
	}
	
	public PBHoneyShopGoodsModel(int id) {
		if (id >= 0 && id <= 2) {
			mCount = id;
			mGoodsTitle = names[id];
			mGoodsIcon = icons[id];
			mGoodsId = goods[id];
		}
	}
	
	public int getCount() {
		return mCount;
	}
	
	public int getIcon() {
		return mGoodsIcon;
	}
	
	public String getTitle() {
		return decrypt(mGoodsTitle);
	}
	
	public String getGoodsId() {
		return decrypt(mGoodsId);
	}
	
	private String decrypt(String msg) {
		try {
			char[] chars = msg.substring(6, msg.length() - 6).toCharArray();
			
			int i = 0;
			for (char c : chars) {
				chars[i++] = (char) ( 0x0F ^ c);
			}			
			return String.valueOf(chars);

		} catch (Exception e) {
			e.printStackTrace();
		} 		
		return null;
	}
	
	/*
	private String encrypt(String msg) {
		try {
			char[] chars = msg.toCharArray();
			
			int i = 0;
			for (char c : chars) {
				chars[i++] = (char) ( 0x0F ^ c);
			}
			
			return randomStringLenth6
				+ String.valueOf(chars)
				+ randomStringLenth6;

		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		return null;
	}
	*/
}