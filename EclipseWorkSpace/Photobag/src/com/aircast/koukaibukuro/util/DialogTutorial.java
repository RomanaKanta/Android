package com.aircast.koukaibukuro.util;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.LinearLayout;

import com.aircast.photobag.R;

/**
 * Tutorial screen
 */
public class DialogTutorial extends Dialog implements android.view.View.OnClickListener{
	
	private ArrayList<Integer> imageId_;
	//private ImageView imageView_;
	private LinearLayout tutarialcontent; 
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
		setContentView(R.layout.kb_new_dialog_tutorial);
		getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		findViewById(R.id.imageView_dialog_close).setOnClickListener(this);
		if (imageId_.size() != 0) {
			tutarialcontent = (LinearLayout) findViewById(R.id.tutorial_content);
			//imageView_.setImageResource(imageId_.get(0));
			tutarialcontent.setBackgroundResource(imageId_.get(0));
			tutarialcontent.setOnClickListener(this);
		}
		
		//final SwipeDetector swipeDetector = new SwipeDetector();
		//findViewById(R.id.tutorial_dialog_layout)
		
		SwipeDetectorForDialog mySwipDetector = new SwipeDetectorForDialog();
		findViewById(R.id.tutorial_content).setOnTouchListener(mySwipDetector);
		
		
	}

	@Override
	public void onClick(View v) {
		
		if(v.getId() == R.id.tutorial_content){
			/*cnt_++;
			if (cnt_ < imageId_.size()) {
				tutarialcontent.setBackgroundResource(imageId_.get(cnt_));
			} else {
				dismiss();
			}*/
			
		}else if(v.getId() == R.id.imageView_dialog_close){
			
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
	
	private  class SwipeDetectorForDialog implements View.OnTouchListener {



	    private static final String logTag = "SwipeDetector";
	    private static final int MIN_DISTANCE = 100;
	    private float downX, downY, upX, upY;


	    @Override
	    public boolean onTouch(View v, MotionEvent event) {
	        switch (event.getAction()) {
	        case MotionEvent.ACTION_DOWN:
	            downX = event.getX();
	            downY = event.getY();
	            //mSwipeDetected = Action.None;
	            return false; // allow other events like Click to be processed
	        case MotionEvent.ACTION_UP:
	            upX = event.getX();
	            upY = event.getY();

	            float deltaX = downX - upX;
	            float deltaY = downY - upY;

	            // horizontal swipe detection
	            if (Math.abs(deltaX) > MIN_DISTANCE) {
	                // left or right
	                if (deltaX < 0) {
	                	
	             
	                	if(cnt_ > 0 ) {
	                		System.out.println("Atik cnt value is left :"+cnt_);
	                		cnt_--;
	                		tutarialcontent.setBackgroundResource(imageId_.get(cnt_));
	                	}
	            
	                	
	                	/*if (cnt_ < imageId_.size()) {
	                		
	                	}*/
	                	
	                	
	                    Log.i(logTag, "Swipe Left to Right");
	                    //mSwipeDetected = Action.LR;
	                    return false;
	                }
	                if (deltaX > 0) {
	                	if (cnt_ < imageId_.size()) {
	                		
	                		if(cnt_ == imageId_.size()-1 ) {
	                			
	                		} else {
		                		cnt_++;
		                		tutarialcontent.setBackgroundResource(imageId_.get(cnt_));
	                		}

	                	}  else {
	        				//dismiss();
	        			}
	                    Log.i(logTag, "Swipe Right to Left");
	                    //mSwipeDetected = Action.RL;
	                    return false;
	                }
	            } else if (Math.abs(deltaY) > MIN_DISTANCE) { // vertical swipe
	                                                            // detection
	                // top or down
	                if (deltaY < 0) {
	                    Log.i(logTag, "Swipe Top to Bottom");
	                    //mSwipeDetected = Action.TB;
	                    return false;
	                }
	                if (deltaY > 0) {
	                    Log.i(logTag, "Swipe Bottom to Top");
	                   // mSwipeDetected = Action.BT;
	                    return false;
	                }
	            }
	            return false;
	        }
	        return false;
	    }
	}
	
}
