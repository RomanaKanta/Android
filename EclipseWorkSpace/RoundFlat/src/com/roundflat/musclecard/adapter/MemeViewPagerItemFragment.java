package com.roundflat.musclecard.adapter;


import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.util.FloatMath;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.roundflat.musclecard.R;
import com.roundflat.musclecard.animition.FlipAnimation;
import com.roundflat.musclecard.fragment.FragmentLifecycle;
import com.roundflat.musclecard.model.TutorialModel;

/**
 * User: tobiasbuchholz
 * Date: 18.09.14 | Time: 11:02
 */
public class MemeViewPagerItemFragment extends Fragment implements FragmentLifecycle{
	
	 Matrix matrix = new Matrix();
	 Matrix savedMatrix = new Matrix();

	 // We can be in one of these 3 states
	 static final int NONE = 0;
	 static final int DRAG = 1;
	 static final int ZOOM = 2;
	 int mode = NONE;

	 // Remember some things for zooming
	 PointF start = new PointF();
	 PointF mid = new PointF();
	 float oldDist = 1f;
	
    private static final String BUNDLE_KEY_IMAGE_RESOURCE_ID    = "bundle_key_image_resource_id";
    private String                 mImageResourceId;
    private static final String BUNDLE_KEY_SUBTITLE                = "bundle_key_subtitle";
    private static final String BUNDLE_KEY_TITLE    = "bundle_key_title";
    private static final String BUNDLE_KEY_LABEL_JP                = "bundle_key_label_jp";
    private static final String BUNDLE_KEY_LABEL_EN    = "bundle_key_label_en";
    String imagePatterName = "_A_000.png";
    private String    sub_title,title,label_japanese,label_english;
	private View view;
	private GestureDetectorCompat motionDetector;

    public static MemeViewPagerItemFragment instantiateWithArgs(final Context context, final TutorialModel card) {
        final MemeViewPagerItemFragment fragment = (MemeViewPagerItemFragment) instantiate(context, MemeViewPagerItemFragment.class.getName());
        final Bundle args = new Bundle();
        args.putString(BUNDLE_KEY_IMAGE_RESOURCE_ID, card.getId());
        args.putString(BUNDLE_KEY_SUBTITLE, card.getSub_title());
        args.putString(BUNDLE_KEY_TITLE, card.getTitle());
        args.putString(BUNDLE_KEY_LABEL_JP, card.getLabel_japanese());
        args.putString(BUNDLE_KEY_LABEL_EN, card.getLabel_english());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initArguments();
    }

    private void initArguments() {
        final Bundle arguments = getArguments();
        if(arguments != null) {
       
            mImageResourceId = arguments.getString(BUNDLE_KEY_IMAGE_RESOURCE_ID);
            sub_title = arguments.getString(BUNDLE_KEY_SUBTITLE);
        	title = arguments.getString(BUNDLE_KEY_TITLE);
        	label_japanese = arguments.getString(BUNDLE_KEY_LABEL_JP);
        	label_english = arguments.getString(BUNDLE_KEY_LABEL_EN);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.pager_item_front, container, false);
        
        
        final GestureDetector gesture = new GestureDetector(getActivity(),
                new GestureDetector.SimpleOnGestureListener() {
        	
        	@Override
        	public boolean onSingleTapUp(MotionEvent e) {
        		flipCard();
        		return true;
        	}
        	
        	

        	 @Override
             public boolean onDown(MotionEvent e) {
                 return true;
             }

        	 
        	 
             @Override
             public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                 float velocityY) {
                 final int SWIPE_MIN_DISTANCE = 120;
                 final int SWIPE_MAX_OFF_PATH = 250;
                 final int SWIPE_THRESHOLD_VELOCITY = 200;
                 try {
                     if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                     {
                         return false;
                     }
                     if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                         && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                         Log.i("Constants.APP_TAG", "Right to Left");
                     } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                         && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                         Log.i("Constants.APP_TAG", "Left to Right");
                     }
                    } catch (Exception e) {
                     // nothing
                 }
//                 if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) {
//                	    if (Math.abs(e1.getX() - e2.getX()) > SWIPE_MAX_OFF_PATH
//                	      || Math.abs(velocityY) < SWIPE_THRESHOLD_VELOCITY) {
//                	     return false;
//                	    }
//                	    if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE) {
////                	      "bottomToTop" 
//                	    	
//                	    	zoomimage(view,  e1);
//                	    } else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE) {
//                	    	
//                	    	zoomimage( view,  e1);
////                	       "topToBottom  " 
//                	    }
//                	   } else {
//                	    if (Math.abs(velocityX) < SWIPE_THRESHOLD_VELOCITY) {
//                	     return false;
//                	    }
//                	    if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE) {
//                	    	 Log.i("Constants.APP_TAG", "Right to Left");
//                	    } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE) {
//                	    	 Log.i("Constants.APP_TAG", "Left to Right");
//                	    }
//                	   }         	 
            	 
                 return super.onFling(e1, e2, velocityX, velocityY);
             }
        	
        });
        
        view.setOnTouchListener(new OnTouchListener(){
            public boolean onTouch(View v, MotionEvent event){
                return gesture.onTouchEvent(event);
            }
        });
        
        initViews();
        return view;
    }
    
    private void zoomimage(View v, MotionEvent e1){
   	 ImageView imageview = new ImageView(getActivity());
   	 imageview.setImageBitmap(viewToBitmap( view));
   	 
   	
   	  dumpEvent(e1);
   	  switch (e1.getAction() & MotionEvent.ACTION_MASK) {

   	  case MotionEvent.ACTION_DOWN:
   	 
   	   break;
   	  case MotionEvent.ACTION_POINTER_DOWN:
   	   oldDist = spacing(e1);
   	   if (oldDist > 10f) {
   	    savedMatrix.set(matrix);
   	    midPoint(mid, e1);
   	    mode = ZOOM;
   	   }
   	   break;
   	  case MotionEvent.ACTION_UP:
   	  case MotionEvent.ACTION_POINTER_UP:
   	   mode = NONE;
   	   break;
   	  case MotionEvent.ACTION_MOVE:
   	    if (mode == ZOOM) {
   	    float newDist = spacing(e1);
   	    if (newDist > 10f) {
   	     matrix.set(savedMatrix);
   	     float scale = newDist / oldDist;
   	     matrix.postScale(scale, scale, mid.x, mid.y);
   	    }
   	   }
   	   break;
   	  
   	  
   	  }
   	 
   	  imageview.setImageMatrix(matrix);

    }
    
    public Bitmap viewToBitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }
    
    public void hideBackPage(){
    	
    	 View cardFace = view.findViewById(R.id.lp_front_content);
		 View cardBack = view.findViewById(R.id.lp_back_content);
		 
		
		 Log.i("cardBack", ""+cardBack.getVisibility());
			if (cardFace.getVisibility() == View.GONE) {
				cardFace.setVisibility(View.VISIBLE);
			}
			
			if (cardBack.getVisibility() == View.VISIBLE) {
				cardBack.setVisibility(View.GONE);
			}
			
			if (cardFace.getVisibility() == View.GONE) {
				 Log.i("cardFace", "GONE "+cardFace.getVisibility());
			}
			
			if (cardFace.getVisibility() == View.VISIBLE) {
				Log.i("cardFace", "VISIBLE "+cardFace.getVisibility());
			}
			
    }
    


    private void initViews() {
       initText();
        initImage();
    }

    private void initText() {
    	 TextView txtSubtitle = (TextView) view.findViewById(R.id.textView_subtitle);
 		TextView txtTitle = (TextView) view.findViewById(R.id.textView_title);
 		TextView txtJP = (TextView) view.findViewById(R.id.textView_jp);
 		TextView txtEng = (TextView) view.findViewById(R.id.textView_eng);

 		txtSubtitle.setText(sub_title);
 		txtTitle.setText(title);
 		txtJP.setText(label_japanese);
 		txtEng.setText(label_english);

 		if ( title.equals("NIL")) {

 			txtTitle.setText(sub_title);
 		}
    }

    private void initImage() {
    	ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
		imageView.setImageDrawable(getImage(mImageResourceId
				+ imagePatterName));
		
//		imageView.setOnTouchListener(new Touch());

    }
    
    private Drawable getImage(String name) {
		try {
			InputStream ims = getActivity().getAssets().open("images/" + name);
			Drawable drawable = Drawable.createFromStream(ims, null);

			return drawable;
		} catch (IOException ex) {
			return null;
		}

	}
    
    private void flipCard() {

		final View cardFace = view.findViewById(R.id.lp_front_content);
		final View cardBack = view.findViewById(R.id.lp_back_content);

		FlipAnimation flipAnimation = new FlipAnimation(cardFace, cardBack);

		if (cardFace.getVisibility() == View.GONE) {
			flipAnimation.reverse();
		}
		view.findViewById(R.id.fl_root_content).startAnimation(flipAnimation);

	}

	@Override
	public void onPauseFragment() {
		Log.i("TAG", "onPauseFragment()");
	}

	@Override
	public void onResumeFragment() {
		Log.i("TAG", "onResumeFragment()");
		
	}
	
//	@Override
//	public void onPauseFragment() {
//		super.onDestroyView();
//		Log.i("TAG", "onPauseFragment()");
//	}
//	
//	@Override
//	public void onPauseFragment() {
//		super.onDestroyView();
//		Log.i("TAG", "onPauseFragment()");
//	}
	
//	@Override
//	public void onDetach() {
//		super.onDetach();
//		Log.i("TAG", title +"  onDetach()");
//		View cardFace = view.findViewById(R.id.lp_front_content);
//		 View cardBack = view.findViewById(R.id.lp_back_content);
//		 
//		
//		 Log.i("cardBack", ""+cardBack.getVisibility());
//			if (cardFace.getVisibility() == View.GONE) {
//				cardFace.setVisibility(View.VISIBLE);
//			}
//			
//			if (cardBack.getVisibility() == View.VISIBLE) {
//				cardBack.setVisibility(View.GONE);
//			}
//			
//			if (cardFace.getVisibility() == View.GONE) {
//				 Log.i("cardFace", "GONE "+cardFace.getVisibility());
//			}
//			
//			if (cardFace.getVisibility() == View.VISIBLE) {
//				Log.i("cardFace", "VISIBLE "+cardFace.getVisibility());
//			}
//	}
//	 @Override
//	    public void onDestroyView() {
//		
//	        Log.i("TAG", "onDestroyView()");
//	        if(view != null){
//	        	  Log.i("TAG", "view");
//	        	hideBackPage();
//	        }
//	        
//	        super.onDestroyView();
//	    }

	 private void dumpEvent(MotionEvent event) {
		  String names[] = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE",
		    "POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?" };
		  StringBuilder sb = new StringBuilder();
		  int action = event.getAction();
		  int actionCode = action & MotionEvent.ACTION_MASK;
		  sb.append("event ACTION_").append(names[actionCode]);
		  if (actionCode == MotionEvent.ACTION_POINTER_DOWN
		    || actionCode == MotionEvent.ACTION_POINTER_UP) {
		   sb.append("(pid ").append(
		     action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
		   sb.append(")");
		  }
		  sb.append("[");
		  for (int i = 0; i < event.getPointerCount(); i++) {
		   sb.append("#").append(i);
		   sb.append("(pid ").append(event.getPointerId(i));
		   sb.append(")=").append((int) event.getX(i));
		   sb.append(",").append((int) event.getY(i));
		   if (i + 1 < event.getPointerCount())
		    sb.append(";");
		  }
		  sb.append("]");
		 }

		 /** Determine the space between the first two fingers */
		 private float spacing(MotionEvent event) {
		  float x = event.getX(0) - event.getX(1);
		  float y = event.getY(0) - event.getY(1);
		  return FloatMath.sqrt(x * x + y * y);
		 }

		 /** Calculate the mid point of the first two fingers */
		 private void midPoint(PointF point, MotionEvent event) {
		  float x = event.getX(0) + event.getX(1);
		  float y = event.getY(0) + event.getY(1);
		  point.set(x / 2, y / 2);
		 }

}
