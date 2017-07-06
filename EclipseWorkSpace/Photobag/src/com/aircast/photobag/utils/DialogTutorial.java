package com.aircast.photobag.utils;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageView;

import com.aircast.photobag.R;

/**
 * チュートリアルを表示するダイアログクラス
 */
public class DialogTutorial extends Dialog implements android.view.View.OnClickListener{
	
	private ArrayList<Integer> imageId_;
	private ImageView imageView_;
	private int cnt_ = 0;
	private tutotialDialogListener listener_;
	private boolean fromTutorial_ = false;
	
	public interface tutotialDialogListener {
		void onDismiss();
	}
	
	public void setTutorialDialogListener(tutotialDialogListener listener) {
		listener_ = listener;
	}
	
	public DialogTutorial(Context context, ArrayList<Integer> imageId, boolean fromTutorial) {
		super(context);
		imageId_ = imageId;
		fromTutorial_ = fromTutorial;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		setContentView(R.layout.kb_dialog_tutorial);
		getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		
		if (imageId_.size() != 0) {
			imageView_ = (ImageView) findViewById(R.id.imageView1);
			imageView_.setImageResource(imageId_.get(0));
			imageView_.setOnClickListener(this);
		}
	}

	@Override
	public void onClick(View v) {
		cnt_++;
		if (cnt_ < imageId_.size()) {
			imageView_.setImageResource(imageId_.get(cnt_));
		} else {
			dismiss();
		}
	}
	
	@Override
	public void dismiss() {
		super.dismiss();
		if (fromTutorial_) {
			listener_.onDismiss();
		}
	}
}
