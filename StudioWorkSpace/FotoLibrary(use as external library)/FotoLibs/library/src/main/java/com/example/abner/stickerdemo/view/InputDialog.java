package com.example.abner.stickerdemo.view;

import android.content.Context;
import android.graphics.Typeface;



public class InputDialog {
	
	BubbleTextView bubbleTextView;
	Context mContext;
	
	public InputDialog(){
	}
	
	public InputDialog(Context context){
		super();
		mContext = context;
	}
	
public void setBubbleView(BubbleTextView bubbleText){
		this.bubbleTextView =bubbleText;
	}

public void setBubbleText(String str){
	((BubbleTextView) bubbleTextView).setText(str);
}

public void setBubbleTextColor(int color){
	((BubbleTextView) bubbleTextView).setTextColor(color);
}

public void setBubbleTextTypeFace(Typeface typeface){
	((BubbleTextView) bubbleTextView).setTextFont(typeface);
}

public void setBubbleTextAlign(int align){
	((BubbleTextView) bubbleTextView).setTextAlign(align);
}
	
}
