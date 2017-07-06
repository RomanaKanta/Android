package com.aircast.koukaibukuro.util;

import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.aircast.photobag.R;

/**
 * Utilities class support cached thumb/bitmap for gallery/list/grid view
 * @author lent5
 *
 */
public class ImageLoaderUtils {
	/** Cache to store decoded bitmap */
    private MemoryCache mMemoryCache = new MemoryCache();
    /** Cache to store bitmap */
    private Map<ImageView, String> mImageViews = Collections
    .synchronizedMap(new WeakHashMap<ImageView, String>());
    /** the service to run background support decoding */
    private ExecutorService mExecutorService;
    /** properties for load photo with resize or not */
    private boolean mIsResize;

    /**
     * Constructor PBImageLoaderUtils
     * @param context the context
     * @param isResize has resized bitmap or not
     */
    public ImageLoaderUtils(Context context, boolean isResize) {
        mExecutorService = Executors.newFixedThreadPool(5);
        mIsResize = isResize;
    }

    final int stub_id = R.drawable.placeholder;

    /**
     * set Bitmap decode from url to imageview
     */
    public void displayImage(String url, ImageView imageView) {
        if(imageView == null || TextUtils.isEmpty(url)) return;
        
        mImageViews.put(imageView, url);
        Bitmap bitmap = mMemoryCache.get(url);
        if (bitmap != null)
            imageView.setImageBitmap(bitmap);
        else {
            queuePhoto(url, imageView);
            //imageView.setImageResource(stub_id);
        }
    }
    
    /**
     * set Bitmap decode from url to imageview
     */
    public void displayImageForHistoryThumb(String url, ImageView imageView) {
        if(imageView == null || TextUtils.isEmpty(url)) return;
        
        mImageViews.put(imageView, url);
        Bitmap bitmap = mMemoryCache.get(url);
        if (bitmap != null)
            imageView.setImageBitmap(bitmap);
        else {
            queuePhotoForHistoryThumb(url, imageView);
            //imageView.setImageResource(stub_id);
        }
    }

    /**
     * put url to queue waiting for decoding bitmap
     * @param url of file image
     * @param imageView view to display bitmap
     */
    private void queuePhoto(String url, ImageView imageView) {
        PhotoToLoad p = new PhotoToLoad(url, imageView);
        mExecutorService.submit(new PhotosLoader(p));
    }
    
    /**
     * put url to queue waiting for decoding bitmap
     * @param url of file image
     * @param imageView view to display bitmap
     */
    private void queuePhotoForHistoryThumb(String url, ImageView imageView) {
    	PhotoToLoadForHistoryThumb p = new PhotoToLoadForHistoryThumb(url, imageView);
        mExecutorService.submit(new PhotosLoaderForHistoryThumb(p));
    }

    /**
     * decode bitmap from url
     * @param url the path of bitmap
     * @return bitmap
     */
    protected Bitmap getBitmap(String url) {
        Bitmap bm = decodeFile(url);
        if (mIsResize) {
            if(bm == null) return null; 
            if (bm.getWidth() > Constant.HISTORY_THUMB_WIDTH
                    || bm.getHeight() > Constant.HISTORY_THUMB_HEIGHT) {
                bm = BitmapUtils.matrixResize(bm,
                		Constant.HISTORY_THUMB_WIDTH,
                		Constant.HISTORY_THUMB_HEIGHT);
            }
        }
        return bm;
    }

    /**
     * Decodes image and scales it to reduce memory consumption
     * 
     * @param f
     * @return
     */
    private static Bitmap decodeFile(String path) {
        try {
            // decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, o);

            // Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE = 70;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE
                        || height_tmp / 2 < REQUIRED_SIZE) {
                    break;
                }
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeFile(path, o2);
        } catch (Exception e) {
            e.printStackTrace();
        }catch(OutOfMemoryError ex){
            Log.e(Constant.TAG, ">>> Create thumb file, OOM when decode image " + path);
        }
        return null;
    }


    /** Task for the queue */
    private class PhotoToLoad {
        /** photo's url */
        private String mUrl;
        /** view to show photo */
        private ImageView mImageView;

        /** constructor of PhotoToLoad */
        public PhotoToLoad(String u, ImageView i) {
            mUrl = u;
            mImageView = i;
        }

        /** get photo's url */
        public String getUrl() {
            return mUrl;
        }

        /** get photo's view to display */
        public ImageView getImageView() {
            return mImageView;
        }
    }
    
    /** Task for the queue */
    private class PhotoToLoadForHistoryThumb {
        /** photo's url */
        private String mUrl;
        /** view to show photo */
        private ImageView mImageView;

        /** constructor of PhotoToLoad */
        public PhotoToLoadForHistoryThumb(String u, ImageView i) {
            mUrl = u;
            mImageView = i;
        }

        /** get photo's url */
        public String getUrl() {
            return mUrl;
        }

        /** get photo's view to display */
        public ImageView getImageView() {
            return mImageView;
        }
    }

    /**
     * class Photo load use for running background to decode bitmap
     * @author lent5
     */
    class PhotosLoader implements Runnable {
        private PhotoToLoad mPhotoToLoad;

        PhotosLoader(PhotoToLoad photoToLoad) {
            this.mPhotoToLoad = photoToLoad;
        }

        @Override
        public void run() {
            //if (imageViewReused(photoToLoad))
            //    return;
            Bitmap bmp = getBitmap(mPhotoToLoad.getUrl());
            
            //if(bmp == null) return;
            if(bmp != null)
                mMemoryCache.put(mPhotoToLoad.getUrl(), bmp);
            //if (imageViewReused(photoToLoad))
            //    return;
            BitmapDisplayer bd = new BitmapDisplayer(bmp, mPhotoToLoad);
            Activity a = (Activity) mPhotoToLoad.getImageView().getContext();
            if(a != null){
                a.runOnUiThread(bd);
            }
        }
    }

    

    /**
     * class Photo load use for running background to decode bitmap
     * @author lent5
     */
    class PhotosLoaderForHistoryThumb implements Runnable {
        private PhotoToLoadForHistoryThumb mPhotoToLoad;

        PhotosLoaderForHistoryThumb(PhotoToLoadForHistoryThumb photoToLoad) {
            this.mPhotoToLoad = photoToLoad;
        }

        @Override
        public void run() {
            //if (imageViewReused(photoToLoad))
            //    return;
            Bitmap bmp = getBitmap(mPhotoToLoad.getUrl());
        	bmp = getRoundedCornerBitmap(bmp);
            
            //if(bmp == null) return;
            if(bmp != null)
                mMemoryCache.put(mPhotoToLoad.getUrl(), bmp);
            //if (imageViewReused(photoToLoad))
            //    return;
            BitmapDisplayerForHistoryThumb bd = new BitmapDisplayerForHistoryThumb(bmp, mPhotoToLoad);
            Activity a = (Activity) mPhotoToLoad.getImageView().getContext();
            if(a != null){
                a.runOnUiThread(bd);
            }
        }
    }
    
    /*boolean imageViewReused(PhotoToLoad photoToLoad) {
        String tag = mImageViews.get(photoToLoad.imageView);
        if (tag == null || !tag.equals(photoToLoad.url))
            return true;
        return false;
    }*/

    // Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable {
        private Bitmap mBitmap;
        private PhotoToLoad mPhotoToLoad;

        /** Constructor BitmapDisplater */
        public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
            mBitmap = b;
            mPhotoToLoad = p;
        }
        
        @Override
        public void run() {
            //if (imageViewReused(photoToLoad))
            //    return;
            if (mBitmap != null 
                    && mPhotoToLoad.getImageView() != null){
                mPhotoToLoad.getImageView().setImageBitmap(mBitmap);
            }
            else {
                mPhotoToLoad.getImageView().setImageResource(stub_id);
            }
        }
    }
    
    // Used to display bitmap in the UI thread
    class BitmapDisplayerForHistoryThumb implements Runnable {
        private Bitmap mBitmap;
        private PhotoToLoadForHistoryThumb mPhotoToLoad;

        /** Constructor BitmapDisplater */
        public BitmapDisplayerForHistoryThumb(Bitmap b, PhotoToLoadForHistoryThumb p) {
            mBitmap = b;
            mPhotoToLoad = p;
        }
        
        @Override
        public void run() {
            //if (imageViewReused(photoToLoad))
            //    return;
            if (mBitmap != null 
                    && mPhotoToLoad.getImageView() != null){
                mPhotoToLoad.getImageView().setImageBitmap(mBitmap);
            }
            else {
                mPhotoToLoad.getImageView().setImageResource(stub_id);
            }
        }
    }

    /**
     * cache use to store decoded bitmap with softReference
     * @author lent5
     */
    class MemoryCache {
        private Map<String, SoftReference<Bitmap>> mCache = Collections
        .synchronizedMap(new HashMap<String, SoftReference<Bitmap>>());

        /**
         * get bitmap from cache with id
         * @param id 
         * @return bitmap
         */
        public Bitmap get(String id) {
            if (!mCache.containsKey(id))
                return null;
            SoftReference<Bitmap> ref = mCache.get(id);
            return (ref==null) ? null : ref.get();
        }

        /**
         * put bitmap to cache
         * @param id
         * @param bitmap
         */
        public void put(String id, Bitmap bitmap) {
            if(TextUtils.isEmpty(id) || bitmap == null) return;
            
            mCache.put(id, new SoftReference<Bitmap>(bitmap));
        }
        
        /** clear cache         */
        public void clear() {
            if(mCache != null){
                mCache.clear();
            }
        }

        // 20120221 @lent recycle bitmap before release #S
        /**
         * recycle all bitmap in cache and clear all local collection to gc
         */
        public void recycleAll(){
            if(mCache == null) return;
            
            try {//In version above android 4 the os will auto recycle bmp so
            	// here may cause crash.
	            for(SoftReference<Bitmap> cacheItem : mCache.values()){
	                Bitmap bm = cacheItem.get();
	                if(bm != null){
	                    bm.recycle();
	                    bm = null;
	                }
	            }
            } catch (Exception e) {}
            
            if(mCache != null){
                mCache.clear();
                mCache = null;
            }
        }
        // 20120221 @lent recycle bitmap before release #E  
    }

    /**
     * Clear all bitmaps in cache
     */
    public void clearCache() {
        if(mMemoryCache != null){
            mMemoryCache.clear();
        }
    }
    
    /**
     * recycle all bitmap in cache and clear all local collection to gc
     * and shutdown Executor when not use longer
     */
    public void recycleAll() {
        if(mExecutorService != null){
            try{
                mExecutorService.shutdown();
                mExecutorService = null;
            }catch (Exception e) {
                Log.w(Constant.TAG, e.getMessage());
            }
        }
        if(mMemoryCache != null){
            mMemoryCache.recycleAll();
        }
    }
    
    /**
     * Method for  round corner bitmap
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
    	
    	
    	Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
	            Bitmap.Config.ARGB_8888);
	    Canvas canvas = new Canvas(output);
//
	   int borderSizePx = 2;
	   int  cornerSizePx = 12;
	    final Paint paint = new Paint();
	    final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
	    final RectF rectF = new RectF(rect);

	    // prepare canvas for transfer
	    paint.setAntiAlias(true);
	    paint.setColor(0xFFFFFFFF);
	    paint.setStyle(Paint.Style.FILL);
	    canvas.drawARGB(0, 0, 0, 0);
	    canvas.drawRoundRect(rectF, cornerSizePx, cornerSizePx, paint);

	    // draw bitmap
	    paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	    canvas.drawBitmap(bitmap, rect, rect, paint);

	    // draw border
	    paint.setColor(Color.LTGRAY);
	    paint.setStyle(Paint.Style.STROKE);
	    paint.setStrokeWidth((float) borderSizePx);
	    canvas.drawRoundRect(rectF, cornerSizePx, cornerSizePx, paint);

	    return output;
      }
}
