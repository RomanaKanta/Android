package com.aircast.photobag.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.aircast.photobag.R;
import com.aircast.photobag.activity.PBMainTabBarActivity;
import com.aircast.photobag.application.PBApplication;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.services.UploadService.MediaInfo;
import com.aircast.photobag.utils.PBBitmapUtils;
import com.aircast.photobag.utils.PBGeneralUtils;

/**
 * class contain method support uploading
 * @author lent5
 *
 */
public class UploadServiceUtils {
    /**
     * utils use to check internet and action
     * @param activity
     * @param isActivityDestroy
     * @param showAlert
     * @return
     */
    public static boolean checkInternetConenction(Activity activity,
            boolean showAlert, final boolean isFinish){
        final Activity cxt = activity;
        boolean connected = PBApplication.hasNetworkConnection();
        if (showAlert && !connected ) {
            if(cxt == null) return connected;
            PBGeneralUtils.showAlertDialogActionWithOnClick(cxt,
                    android.R.drawable.ic_dialog_alert,
                    cxt.getString(R.string.dialog_network_error_title),
                    cxt.getString(R.string.dialog_network_error_body),
                    cxt.getString(R.string.upload_confirm_pass_ok_btn),
                    new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    if(isFinish){
                        cxt.finish();
                    }
                }
            }, false);
        }
        return connected;
    }
    /**
     * support for delete cache when cancel upload
     * @param photos
     * @return
     */
    public static void deleteCachePhoto(ArrayList<MediaInfo> photos) throws SecurityException{
        if(photos == null) return;
        File photoFile = null;
        String imgPath = "";
        boolean rs = false;
        synchronized (photos) {
            // Log.w(PBConstant.TAG, "[deleteCachePhoto] --- " + photos.size());        
            for (int i = 0; i < photos.size(); i++) {
                MediaInfo photo = photos.get(i);
                if (photo == null)return;
                // delete photo
                imgPath = PBGeneralUtils.getPathFromCacheFolder(photo.url);
                photoFile = new File(imgPath);
                if (photoFile.exists() ){
                    rs = photoFile.delete();
                    // Log.w(PBConstant.TAG, "[delete][" + rs + "]" + imgPath);
                }

                // delete thumb
                imgPath = PBGeneralUtils.getPathFromCacheFolder(photo.url + "?width=150&height=150");
                photoFile = new File(imgPath);
                if (photoFile.exists() ){
                    rs = photoFile.delete();
                    // Log.w(PBConstant.TAG, "[delete][" + rs + "]" + imgPath);
                }
                photoFile = null;
            }
        }
    }

    /**
     * get compressed file path
     * @param realFile real file's path
     * @return
     */
    public static String getCompressedFilePath(String realFile){
        return realFile;
    }

    /**
     * 
     */
    public static void saveVideoToCacheFolder(String urlResponse, String localVideoFile) {
    	System.out.println("Atik call create thumbnail video");
        // clone video file to cache foler
        urlResponse = urlResponse + PBConstant.VIDEO_FORMAT_3GP;
    	System.out.println("Atik call create thumbnail video response is:"+urlResponse);

        if(!TextUtils.isEmpty(urlResponse) && !TextUtils.isEmpty(localVideoFile)){
            String newVideoFileInCachePath = PBGeneralUtils.getPathFromCacheFolder(urlResponse);
        	System.out.println("Atik call create thumbnail video file in cache path:"+newVideoFileInCachePath);

            boolean copyResult = PBGeneralUtils.copyFileByCommand(localVideoFile, newVideoFileInCachePath);
        	System.out.println("Atik call create thumbnail copy result:"+copyResult);

            Log.i("mapp", ">>upload, copy video file to cache folder ok, " + copyResult);
        }
        
        // extract video thumb and save to cache folder
        Bitmap videoThumb = ThumbnailUtils
                .createVideoThumbnail(localVideoFile, MediaStore.Video.Thumbnails.MINI_KIND);
        if (videoThumb != null) {
            boolean savingVideoThumb = PBBitmapUtils.saveBitmapToSdcard(videoThumb, urlResponse, true, true, true);
            Log.i(PBConstant.TAG, ">>upload, gen video thumb to cache folder ok, " + savingVideoThumb);
        	System.out.println("Atik call create thumbnail saving video thumb:"+savingVideoThumb);

        } else {
            Log.e(PBConstant.TAG, ">>upload, can not extract video thumb!!!");
        	System.out.println("Atik call create thumbnail saving video thumb:"+false);

        }
    }

    /**
     * Save file to cache folder and create thumbnail
     * 
     * @param urlResponse url photo return after upload
     * @param localFile photo's path on local storage
     * @param compressFile compress photo's path 
     */
    public static void savePhotoToCacheFolder(String urlResponse, String localFile, String compressedFile) {
    	
    	System.out.println("Atik call create thumbnail picture");
        // store real photo to cache
        if(!TextUtils.isEmpty(urlResponse) && !TextUtils.isEmpty(compressedFile)){
            String newFile = PBGeneralUtils.getPathFromCacheFolder(urlResponse);
            try {
                UploadServiceUtils.copy(new File(compressedFile), new File(newFile));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        // store thumb to cache
        if(!TextUtils.isEmpty(localFile)){
            Bitmap bmp = PBBitmapUtils.loadThumbnailImage(
            		PBMainTabBarActivity.sMainContext, localFile);
            // 20120326 @lent5 fixed rotate bm thumb upload saving #S
            float rotate = PBBitmapUtils.rotationForImage(PBApplication.getBaseApplicationContext(), localFile);
            try{
                if ((rotate == 90.0f || rotate == 180.0f || rotate == 270.0f) && bmp != null){
                    Matrix mtx = new Matrix();
                    mtx.postRotate(rotate);
                    // Rotating Bitmap
                    bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),bmp.getHeight(), mtx, true);
                }
            }catch (OutOfMemoryError e) {
                Log.d(PBConstant.TAG, e.getMessage());
                Runtime.getRuntime().gc();
            } // 20120326 @lent5 fixed rotate bm thumb upload saving #E
            String thumbImgPath = 
                PBGeneralUtils.getPathFromCacheFolder(urlResponse + "?width=150&height=150");
            try {
                FileOutputStream fos = null;
                if (android.os.Environment.getExternalStorageState().equals(
                        android.os.Environment.MEDIA_MOUNTED)) {
                    fos = new FileOutputStream(new File(thumbImgPath));
                } else {
                    // save on internal memory if sdcard is invalid.
                    fos = PBApplication.getBaseApplicationContext()
                                .openFileOutput(thumbImgPath, 0);
                }
                if (bmp != null) {
                    boolean compressResult =
                    bmp.compress(PBConstant.COMPRESS_FORMAT, PBConstant.DECODE_COMPRESS_PRECENT, fos);
                    Log.i("mapp", ">>>upload service, compress file ok: " + compressResult);
                    bmp.recycle();
                    bmp = null;
                }
                // release resources after saving photo DONE!
                if (fos != null) {
                    fos.flush();
                    fos.close();
                    fos = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Copy file to another location
     * 
     * @param src
     * @param dest
     * @throws IOException
     */
    public static void copy(File src, File dest) throws IOException {
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            inputStream = new FileInputStream(src);
            outputStream = new FileOutputStream(dest);

            byte[] buffer = new byte[8 * 1024];

            while (true) {
                int bytesRead = inputStream.read(buffer);
                if (bytesRead <= 0) {
                    break;
                }
                outputStream.write(buffer, 0, bytesRead);
            }
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) { // failsafe
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) { // failsafe
                    e.printStackTrace();
                }
            }
        }
    }
}
