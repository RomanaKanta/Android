package com.aircast.photobag.utils;

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
import android.graphics.Matrix;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.aircast.photobag.application.PBApplication;
import com.aircast.photobag.application.PBConstant;
/**
 * class support cache thumb use softRefernce and Executor service for decoding
 * extends {@link PBImageLoaderUtils}
 * @author lent5
 */
public class PBImageThumbLoaderUtils{


    /*  @Override
    public void clearCache() {

        super.clearCache();
    }*/

    /** Cache to store decoded bitmap */
    private MemoryCache mMemoryCache = new MemoryCache();
    /** Cache to store bitmap */
    private Map<ImageView, String> mImageViews = Collections
    .synchronizedMap(new WeakHashMap<ImageView, String>());
    /** the service to run background support decoding */
    private ExecutorService mExecutorService;
    /** properties for load photo with resize or not */

    //final int stub_id = -1;
    /** COntructor PBImageThumbLoaderUtils */
    public PBImageThumbLoaderUtils(Context context) {
        mExecutorService = Executors.newFixedThreadPool(5);
    }

    /**
     * set Bitmap decode from url to imageview
     */
    public boolean displayImage(ImageView imageView, String url, int idThumb, boolean isVideo) {
        if(imageView == null || TextUtils.isEmpty(url)) return false;

        // 20120316 @lent5
        if(imageView != null){
            imageView.setTag(url);
        }
        mImageViews.put(imageView, url);
        Bitmap bitmap = mMemoryCache.get(url);
        if (bitmap != null){
            imageView.setImageBitmap(bitmap);
            return true;
        }else {
            queuePhoto(url, idThumb, imageView, isVideo);
            return false;
        }
    }

    /**
     * put url to queue waiting for decoding bitmap
     * @param url of file image
     * @param imageView view to display bitmap
     */
    private void queuePhoto(String url, int idThumb, ImageView imageView, boolean isVideo) {
        PhotoToLoad p = new PhotoToLoad(url, idThumb, imageView, isVideo);
        mExecutorService.submit(new PhotosLoader(p));
    }

    /**
     * decode bitmap from url
     * @param url the path of bitmap
     * @return bitmap
     */
    protected Bitmap getBitmap(String url, int idThumb, boolean isVideo) {
        if(PBApplication.getBaseApplicationContext() == null) return null;
        Bitmap bm = null;
        if(isVideo){
            bm = MediaStore.Video.Thumbnails.getThumbnail(
                    PBApplication.getBaseApplicationContext().getContentResolver(), 
                    idThumb,
                    MediaStore.Video.Thumbnails.MICRO_KIND, null);
            System.out.println("Atik call called getBitmap"+bm);

        }else{
            bm = MediaStore.Images.Thumbnails.getThumbnail(
                    PBApplication.getBaseApplicationContext().getContentResolver(), 
                    idThumb,
                    MediaStore.Images.Thumbnails.MICRO_KIND, null);
            float rotate = PBBitmapUtils.rotationForImage(PBApplication.getBaseApplicationContext(), url);
            try{
                if ((rotate == 90.0f || rotate == 180.0f || rotate == 270.0f) && bm != null){
                    Matrix mtx = new Matrix();
                    mtx.postRotate(rotate);
                    // Rotating Bitmap
                    bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),bm.getHeight(), mtx, true);
                }
            }catch (OutOfMemoryError e) {
                Log.d(PBConstant.TAG, e.getMessage());
                Runtime.getRuntime().gc();
            }
        }
        return bm;
    }

    /** Task for the queue */
    private class PhotoToLoad {
        private int mIdThumb;
        /** photo's url */
        private String mUrl;
        /** view to show photo */
        private ImageView mImageView;
        public boolean isVideo = false;
        /** constructor of PhotoToLoad */
        public PhotoToLoad(String u, int idThumb, ImageView i, boolean isVideo) {
            mUrl = u;
            mIdThumb = idThumb;
            mImageView = i;
            this.isVideo = isVideo;
        }

        /** get photo's url */
        public String getUrl() {
            return mUrl;
        }

        /** get thumb's id */
        public int getIdThumb(){
            return mIdThumb;
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
            if (imageViewReused(mPhotoToLoad)){
                // Log.d("lent5", " ----- resued -----------");
                return;
            }
            // Log.d("lent5", " ----- none ---- resued -----------");
            Bitmap bmp = getBitmap(mPhotoToLoad.getUrl(), mPhotoToLoad.getIdThumb(), mPhotoToLoad.isVideo);
            System.out.println("Atik call called photoloaded"+bmp);
            if(bmp == null) return;

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

    boolean imageViewReused(PhotoToLoad photoToLoad) {
        String tag = mImageViews.get(photoToLoad.mImageView);
        if (tag == null || !tag.equals(photoToLoad.mUrl))
            return true;
        return false;
    }

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
            //else
            //    photoToLoad.imageView.setImageResource(stub_id);
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

        // 20120221 @lent recycle bitmap before release #S
        /**
         * recycle all bitmap in cache and clear all local collection to gc
         */
        public void recycleAll(){
            if(mCache == null) return;

            for(SoftReference<Bitmap> cacheItem : mCache.values()){
                Bitmap bm = cacheItem.get();
                if(bm != null){
                    bm.recycle();
                    bm = null;
                }
            }

            if(mCache != null){
                mCache.clear();
            }
        }
        // 20120221 @lent recycle bitmap before release #E  
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
                Log.w(PBConstant.TAG, e.getMessage());
            }
        }
        if(mMemoryCache != null){
        	try {
        		mMemoryCache.recycleAll();
        	} catch (Exception e) {}
        }
    }
}
