package com.example.hexagoncropexample;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImageView;


public class MainActivity extends Activity implements CropImageView.OnGetCroppedImageCompleteListener {
	
	CropImageView crop_image;
	ImageView main_image;
	  Bitmap bmp, crop_bitmap;
	  boolean hexa = false;
	  SeekBar  blurBar;
	   
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        main_image =(ImageView)findViewById(R.id.main_imageview);
        crop_image = (CropImageView)findViewById(R.id.crop_imageview);
         bmp = BitmapFactory.decodeResource(getResources(), R.drawable.splash);
         crop_image.setImageBitmap(bmp);
//         crop_image.setCropShape(CropImageView.CropShape.RECTANGLE);
        
     
        
  this.findViewById(R.id.crop).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			
				crop_image.getCroppedImageAsync(crop_image.getCropShape(), 0, 0);
			}
		});
  this.findViewById(R.id.oval_crop).setOnClickListener(new OnClickListener() {
		
			@Override
			public void onClick(View v) {
			
				hexa = false;
				  crop_image.setCropShape(CropImageView.CropShape.OVAL);
			}
		});
  this.findViewById(R.id.hexa_crop).setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			hexa = true;
			 crop_image.setCropShape(CropImageView.CropShape.HEXA);
		}
	});
  
  this.findViewById(R.id.rect_crop).setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			hexa = false;
			  crop_image.setCropShape(CropImageView.CropShape.RECTANGLE);
		}
	});
  
  this.findViewById(R.id.custom_crop).setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
		
			hexa = false;
			Toast.makeText(getApplicationContext(), "custom", 500).show();
		}
	});
  
  
  blurBar = (SeekBar)findViewById(R.id.blurBar);
  
  blurBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
	
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		
	}
	
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		main_image.setImageBitmap(processingBitmap_Blur(crop_bitmap, progress+1));
	}
});
  
    }

    private Bitmap processingBitmap_Blur(Bitmap src, int blurValue){
	     int width = src.getWidth();
	        int height = src.getHeight();
	         
	     BlurMaskFilter blurMaskFilter;
	     Paint paintBlur = new Paint();
	      
	     Bitmap dest = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
	     Canvas canvas = new Canvas(dest); 
	      
	     //Create background in White
	     Bitmap alpha = src.extractAlpha();
	     paintBlur.setColor(0xFFd54b4f);
	     canvas.drawBitmap(alpha, 0, 0, paintBlur);
	      
	     //Create outer blur, in White
	     blurMaskFilter = new BlurMaskFilter(blurValue, BlurMaskFilter.Blur.OUTER);
	     paintBlur.setMaskFilter(blurMaskFilter);
	     canvas.drawBitmap(alpha, 0, 0, paintBlur);
	      
	     //Create inner blur
	     blurMaskFilter = new BlurMaskFilter(blurValue, BlurMaskFilter.Blur.INNER);
	     paintBlur.setMaskFilter(blurMaskFilter);
	     canvas.drawBitmap(src, 0, 0, paintBlur);
	      
	      
	      
	     return dest;
	    }
    
    public  Bitmap getRoundedCornerBitmap(Bitmap bitmap, int color,   /*for round corner ( int  cornerDips, )*/  int borderDips) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int borderSizePx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) borderDips,
               getResources().getDisplayMetrics());
        
        /*for round corner*/
//        final int cornerSizePx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) cornerDips,
//                getResources().getDisplayMetrics());
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        // prepare canvas for transfer
        paint.setAntiAlias(true);
        paint.setColor(0xFFFFFFFF);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawARGB(0, 0, 0, 0);
        
        /*for round corner*/
//        canvas.drawRoundRect(rectF, cornerSizePx, cornerSizePx, paint);
        canvas.drawRect(rectF, paint);

        // draw bitmap
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        // draw border
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth((float) borderSizePx);
        
        /*for round corner*/
//        canvas.drawRoundRect(rectF, cornerSizePx, cornerSizePx, paint);
        canvas.drawRect(rectF, paint);

        return output;
    }  
    
    
    public  Bitmap getHexaShapeCroppedBitmap(Bitmap bitmap) {
        Bitmap finalBitmap =bitmap ;
//        if (bitmap.getWidth() != radius || bitmap.getHeight() != radius)
//               finalBitmap = Bitmap.createScaledBitmap(bitmap, radius, radius,
//                            false);
//        else
//               finalBitmap = bitmap;
        Bitmap output = Bitmap.createBitmap(finalBitmap.getWidth(),
                     finalBitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, finalBitmap.getWidth(),
                     finalBitmap.getHeight());

 
        int x1 = finalBitmap.getWidth()/2;
        int y1 = 0;
        
        int x2 =0;
        int y2 =  finalBitmap.getHeight()/4;
        
        int x3 = 0;
        int y3 = finalBitmap.getHeight()-y2;
        
        int x4 = finalBitmap.getWidth()/2;
        int y4 =  finalBitmap.getHeight();
        
        int x6 = finalBitmap.getWidth();
        int y6 = finalBitmap.getHeight()/4;
        
        int x5 = finalBitmap.getWidth();
        int y5 =  finalBitmap.getHeight()-y6;
        
        
        
        Point point1_draw = new Point(x1, y1);
        Point point2_draw = new Point(x2, y2);
        Point point3_draw = new Point(x3, y3);
        Point point4_draw = new Point(x4, y4);
        Point point5_draw = new Point(x5, y5);
        Point point6_draw = new Point(x6, y6);

        Path path = new Path();
        path.moveTo(point1_draw.x, point1_draw.y);
        path.lineTo(point2_draw.x, point2_draw.y);
        path.lineTo(point3_draw.x, point3_draw.y);
        path.lineTo(point4_draw.x, point4_draw.y);
        path.lineTo(point5_draw.x, point5_draw.y);
        path.lineTo(point6_draw.x, point6_draw.y);

        path.close();
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#BAB399"));
        canvas.drawPath(path, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(finalBitmap, rect, rect, paint);

        return output;
 }

    @Override
	public void onGetCroppedImageComplete(CropImageView view, Bitmap bitmap,
			Exception error) {
		  if (error == null) {
			  
			  if(hexa){
				  crop_bitmap = getHexaShapeCroppedBitmap( bitmap);
			  main_image.setImageBitmap(crop_bitmap);
			  }else{
				  
				  crop_bitmap = bitmap;
				  main_image.setImageBitmap(crop_bitmap);
			  }
	
	        } else {
	            Toast.makeText(getApplicationContext(), "Image crop failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
	        }
		
	}
    
    @Override
    protected void onStart() {
        super.onStart();
        crop_image.setOnGetCroppedImageCompleteListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        crop_image.setOnGetCroppedImageCompleteListener(null);
    }
    
}
