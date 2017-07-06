package com.aircast.koukaibukuro.util;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;


/**
 * Utilities related bitmap: decode/save/scrop 
 * @author NhatVT
 *
 */
public class BitmapUtils {

    // private static final float STROKE_WIDTH = 2.0f;
    public static final int TEMPLATE_H = 800;
    public static final int TEMPLATE_W = 600;
    
//    public static boolean saveBitmapToSdcard(Bitmap bm, String imageUrl,
//            boolean isVideo, boolean needResizeImage, boolean needRecycleBitmap) {
//        if (bm == null) {
//            return false;
//        }
//        
//        if (TextUtils.isEmpty(imageUrl)) {
//            return false;
//        }
//        
//        boolean result = false;
//        File dir = new File (PBGeneralUtils.getCacheFolderPath());
//        if(!dir.exists()){
//            dir.mkdirs();
//        }
//        // save with md5 code from url
//        File iconFile;
//        if (isVideo) {
//            iconFile = new File(dir, String.valueOf(imageUrl.hashCode()) + PBConstant.VIDEO_THUMB_STR);
//        } else {
//            iconFile = new File(dir, String.valueOf(imageUrl.hashCode()));
//        }
//        try {
//            // resize video thumb image if needed
//            if (needResizeImage && isVideo) {
//                int screenW = PBConstant.MAXIMUM_IMAGE_SIZE_UPLOAD;
//                try {
//                    DisplayMetrics dm = new DisplayMetrics();
//                    ((WindowManager) PBApplication.getBaseApplicationContext()
//                            .getSystemService(Context.WINDOW_SERVICE))
//                            .getDefaultDisplay().getMetrics(dm);
//                    screenW = dm.widthPixels;
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                
//                // resize bitmap first in case width or height larger than 960 px
//                final int realBmpW = bm.getWidth();
//                final int realBmpH = bm.getHeight();
//                
//                if (realBmpW == -1 || realBmpH == -1) {
//                    return false;
//                }
//                int desireW = realBmpW;
//                int desireH = realBmpH;
//                
//                /*if (realBmpW > PBConstant.MAXIMUM_IMAGE_SIZE_UPLOAD 
//                        || realBmpH > PBConstant.MAXIMUM_IMAGE_SIZE_UPLOAD) {
//                    if (realBmpW > realBmpH) {
//                        desireW = PBConstant.MAXIMUM_IMAGE_SIZE_UPLOAD;
//                        desireH = (realBmpH * PBConstant.MAXIMUM_IMAGE_SIZE_UPLOAD) / realBmpW;
//                    } else {
//                        if (realBmpW < realBmpH) {
//                            desireH = PBConstant.MAXIMUM_IMAGE_SIZE_UPLOAD;
//                            desireW = (realBmpW * PBConstant.MAXIMUM_IMAGE_SIZE_UPLOAD) / realBmpH;
//                        } else {
//                            desireW = desireH = PBConstant.MAXIMUM_IMAGE_SIZE_UPLOAD;
//                        }
//                    }
//                }*/
//                desireW = screenW;
//                desireH = (realBmpH * screenW) / realBmpW;
//                bm = matrixResize(bm, desireW, desireH);
//            }
//            
//            // save image to disk
//            OutputStream outStream = new FileOutputStream(iconFile);
//            bm.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
//            if (outStream != null) {
//                outStream.flush();
//                outStream.close();
//            }
//            if (needRecycleBitmap) {
//                bm.recycle();
//                bm = null;
//            }
//            result = true;
//            
//          // Atik tell media to scan for saved video thumb  
//          /*System.out.println("Atik call send brodcast to media");  
//      	  Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//          System.out.println("Atik call send brodcast to media path"+iconFile.getAbsolutePath());  
//      	  File f = new File(iconFile.getAbsolutePath());
//      	  if(f.exists()) {
//              System.out.println("Atik call send brodcast to media path exist"+iconFile.getAbsolutePath());  
//
//      	  }
//      	  Uri contentUri = Uri.fromFile(f);
//      	  mediaScanIntent.setData(contentUri);
//      	  PBApplication.getBaseApplicationContext().sendBroadcast(mediaScanIntent);*/
//      	  
//      	  
//            
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (OutOfMemoryError e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        
//        return result;
//    }
//
//
//    public static boolean checkCompressedFile(String realFile){
//        File imageFile = new File(realFile/*dir, tempFilePath*/);
//        if (!imageFile.exists()) {
//            return false;
//        }
//        int[] imageSize = getOriginalSize(imageFile.getAbsolutePath());
//        final int realBmpW = imageSize[0];
//        final int realBmpH = imageSize[1];
//
//        if (realBmpW == -1 || realBmpH == -1) {
//            // decode error
//            return false;
//        }
//        return true;
//    }
//    /**
//     * Compress file source to temp file on sdcard.
//     * @param fileSourcePath image source path.
//     * @return file path to temp image if the compress is ok.
//     */
//    public static String compressBitmap(String fileSourcePath) {
//        if (TextUtils.isEmpty(fileSourcePath)) {
//            return fileSourcePath;
//        }
//        
//        // check cache directory on sdcard first; 
//        File dir = new File(PBGeneralUtils.getCacheFolderPath());
//        if(!dir.exists()){
//            dir.mkdirs();
//        }
//        
//        String tempFilePath = "uploadTemp.jpg";
//        File imageFile = new File(dir, tempFilePath);
//        if (imageFile.exists()) {
//            Log.i("mapp", ">>> already have old temp file, remove it first!");
//            imageFile.delete();
//        }
//        // resize bitmap first in case width or height larger than 960 px
//        int[] imageSize = getOriginalSize(fileSourcePath);
//        final int realBmpW = imageSize[0];
//        final int realBmpH = imageSize[1];
//        
//        if (realBmpW == -1 || realBmpH == -1) {
//            // decode error
//            return fileSourcePath;
//        }
//        
//        Log.i("mapp", ">compress image realW=" + realBmpW + "  realH=" + realBmpH);
//                
//        int desireW = realBmpW;
//        int desireH = realBmpH;
//        
//        if (realBmpW > PBConstant.MAXIMUM_IMAGE_SIZE_UPLOAD 
//                || realBmpH > PBConstant.MAXIMUM_IMAGE_SIZE_UPLOAD) {
//            if (realBmpW > realBmpH) {
//                desireW = PBConstant.MAXIMUM_IMAGE_SIZE_UPLOAD;
//                desireH = (realBmpH * PBConstant.MAXIMUM_IMAGE_SIZE_UPLOAD) / realBmpW;
//            } else {
//                if (realBmpW < realBmpH) {
//                    desireH = PBConstant.MAXIMUM_IMAGE_SIZE_UPLOAD;
//                    desireW = (realBmpW * PBConstant.MAXIMUM_IMAGE_SIZE_UPLOAD) / realBmpH;
//                } else {
//                    desireW = desireH = PBConstant.MAXIMUM_IMAGE_SIZE_UPLOAD;
//                }
//            }
//        }
//        
//        FileOutputStream fos = null;
//        try {
//            fos = new FileOutputStream(imageFile);
//            // Bitmap mBitmap = BitmapFactory.decodeFile(fileSourcePath, mOptions);
//            Options mOptions = new Options();
//            // Log.d("mapp", ">>>  mOptions.inSampleSize = " +  mOptions.inSampleSize);
//            mOptions.inSampleSize = sampleSizeNeeded(fileSourcePath,
//                    PBConstant.MAXIMUM_IMAGE_SIZE_UPLOAD);
//            // Log.i("mapp", ">compress image desireW=" + desireW + "  desireH="
//            //        + desireH + " with sample size=" + mOptions.inSampleSize);
//            // no need compress image if its size is too small!
//            if (mOptions.inSampleSize < 2) {
//            	if (fos != null) {
//                    fos.flush();
//                    fos.close();
//                    fos = null;
//                }
//                return fileSourcePath;
//            }
//            Bitmap mBitmap = BitmapFactory.decodeFile(fileSourcePath, mOptions);
//            // add to avoid resize if no need in case bitmap small
//            if(!(realBmpH <= desireH && realBmpW <= desireW &&  mOptions.inSampleSize < 2)){
//                mBitmap = matrixResize(mBitmap, desireW, desireH);
//            }
//            // 20120227 added by NhatVT, fix issue of image orientation on some phone <S>
//            try {
//                float rotateImg = 0.0f;
//                rotateImg = rotationForImage(null, fileSourcePath);
//                // Log.d("mapp", ">>> rotate image = " + rotateImg);
//                if (rotateImg != 0) {
//                    Matrix mtx = new Matrix();
//                    mtx.postRotate(rotateImg);
//                    // Rotating Bitmap
//                    if (mBitmap != null) {
//                        mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap
//                                .getWidth(), mBitmap.getHeight(), mtx, true);
//                    }
//                }
//            } catch (OutOfMemoryError oom) {
//                Log.e(PBConstant.TAG, ">>> Cannot rotate image file! not enough memory!");
//                Runtime.getRuntime().gc();
//            } catch (IllegalArgumentException e) {
//                Log.e(PBConstant.TAG, ">>> Cannot rotate image file! IllegalArgumentException!");
//                Runtime.getRuntime().gc();
//            }
//            // 20120227 added by NhatVT, fix issue of image orientation on some phone <E>
//
//            if (mBitmap != null) {
//                mBitmap.compress(PBConstant.COMPRESS_FORMAT, PBConstant.COMPRESS_PRECENT, fos);
//                // release resource
//                if (fos != null) {
//                    fos.flush();
//                    fos.close();
//                    fos = null;
//                }
//                if (mBitmap != null) {
//                    mBitmap.recycle();
//                    mBitmap = null;
//                }
//                return imageFile.getAbsolutePath();
//            } else {
//            	if (fos != null) {
//                    fos.flush();
//                    fos.close();
//                    fos = null;
//                }
//                Log.e(PBConstant.TAG, ">>> cannot compress image file! Decode fail!");
//                return fileSourcePath;
//            }
//        } catch (FileNotFoundException e) {
//            Log.e(PBConstant.TAG, ">>> Cannot compress image file! File not found!");
//        } catch (IOException e) {
//            Log.e(PBConstant.TAG, ">>> Cannot compress image file! File not found!");
//        } catch (OutOfMemoryError oom) {
//            Log.e(PBConstant.TAG, ">>> Cannot compress image file! not enough memory!");
//            Runtime.getRuntime().gc();
//        }
//        return fileSourcePath;
//    }

    public static Bitmap centerCropImage(Bitmap inputBmp, int desireWidth,
            int desireHeight) {

        if (inputBmp == null) {
            return null;
        }

        int mOrgWidth = inputBmp.getWidth();
        int mOrgHeight = inputBmp.getHeight();

        if (mOrgWidth <= 0 || mOrgHeight <= 0) {
            return null;
        }

        boolean mNeedToCrop = true;

        int scaleWidth = mOrgWidth;
        int scaleHeight = mOrgHeight;
        if (mOrgWidth > mOrgHeight) {
            scaleWidth = desireHeight * mOrgWidth / mOrgHeight;
            scaleHeight = desireHeight;
        } else {
            if (mOrgHeight > mOrgWidth) {
                scaleWidth = desireWidth;
                scaleHeight = desireWidth * mOrgHeight / mOrgWidth;
            } else {
                if (mOrgHeight == mOrgWidth) {
                    scaleWidth = desireWidth;
                    scaleHeight = desireHeight;
                    mNeedToCrop = false;
                }
            }
        }

        // Log.d("mapp", ">>>oW=" + mOrgWidth + "  oH=" + mOrgHeight + "  sW=" +
        // scaleWidth + "  sH" + scaleHeight);

        inputBmp = matrixResize(inputBmp, scaleWidth, scaleHeight);
        if (mNeedToCrop) {
            mOrgWidth = inputBmp.getWidth();
            mOrgHeight = inputBmp.getHeight();
            // Log.i("mapp", ">>>oW=" + mOrgWidth + "  oH=" + mOrgHeight +
            // "  sW=" + scaleWidth + "  sH" + scaleHeight);

            // create a matrix for the manipulation
            Matrix matrix = new Matrix();
            matrix.postScale(1, 1);

            int dx = 0;
            int dy = 0;
            if (scaleWidth > scaleHeight) {
                dx = (scaleWidth - desireWidth) / 2;
            } else {
                if (scaleHeight > scaleWidth) {
                    dy = (scaleHeight - desireHeight) / 2;
                }
            }
            inputBmp = Bitmap.createBitmap(inputBmp, dx, dy, desireWidth,
                    desireHeight, matrix, true);
        }

        // long t2 = System.currentTimeMillis();
        // Log.d("mapp", ">>> crop image cost:" + (t2 - t1));
        if (inputBmp != null) {
            return inputBmp;
        }
        return null;
    }

    /**
     * Resize bitmap to specific size by using Matrix.
     * 
     * @param source
     *            bitmap want to resize.
     * @param newWidth
     *            new width.
     * @param newHeight
     *            new height
     * @return bitmap has been resized of null if an error occurred
     */
    public static Bitmap matrixResize(Bitmap source, int newWidth, int newHeight) {
        if (source == null) {
            return null;
        }
        Matrix matrix = new Matrix();
        matrix.postScale((float) newWidth / source.getWidth(),
                (float) newHeight / source.getHeight());
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source
                .getHeight(), matrix, true);
    }

    /**
     * 
     * @param imagePath
     * @return
     */
    public static int[] getOriginalSize(String imagePath) {
        if (TextUtils.isEmpty(imagePath)) {
            return null;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        int[] result = new int[2];
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        // Log.d("mapp", ">>> original bitmap with w=" + options.outWidth +
        // "  h="+options.outHeight);
        result[0] = options.outWidth;
        result[1] = options.outHeight;
        return result;
    }

    /**
     * Util for checking the valid of image.
     * 
     * @param photoFile
     *            photo file path that want to check.
     * @return return <b>false</b> if file is corrupt.
     */
    public static boolean isPhotoValid(String photoFilePath) {
        if (TextUtils.isEmpty(photoFilePath)) {
            return false;
        }
        // 20120628 change solution for checking corrupt image <S>
        /*Options mOptions = new Options();
        mOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoFilePath, mOptions);
        if (mOptions.outWidth <= 0) {
            return false;
        }
        return true;
        */
        Options mOptions = new Options();
        mOptions.inSampleSize = 8;
        Bitmap bmp = BitmapFactory.decodeFile(photoFilePath, mOptions);
        if (bmp != null) {
            bmp.recycle();
            bmp = null;
            return true;
        }
        // 20120628 change solution for checking corrupt image <E>
        return false;
    }

    /**
     * 
     */
    private static final int UNCONSTRAINED = -1;

    /**
     * get sample size for decoding image to avoid OOM issue in Android.
     * @param fileName image file want to calculate.
     * @param maxSize maximum width and height of image.
     * @return sample size for decoding.
     */
//    public static int sampleSizeNeeded(String fileName, int maxSize) {
//        if (TextUtils.isEmpty(fileName)) {
//            return 1;
//        }
//
//        int sample = 1;
//
//        // resize bitmap first in case width or height larger than 960 px
//        BitmapFactory.Options options = new Options();
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(fileName, options);
//        
//        if (options.outWidth == -1 || options.outHeight == -1) {
//            // decode error
//            return 1;
//        }
//
//        final int realBmpW = options.outWidth;
//        final int realBmpH = options.outHeight;
//        
//        if (realBmpW == 0 || realBmpH == 0) {
//            // invalid file or decode error
//            return 1;
//        }
//
//        int desireW = realBmpW;
//        int desireH = realBmpH;
//
//        // calculate scale image
//        if (realBmpW > maxSize || realBmpH > maxSize) {
//            if (realBmpW > realBmpH) {
//                desireW = maxSize;
//                desireH = (realBmpH * maxSize) / realBmpW;
//            } else {
//                if (realBmpW < realBmpH) {
//                    desireH = maxSize;
//                    desireW = (realBmpW * maxSize) / realBmpH;
//                } else {
//                    desireW = desireH = maxSize;
//                }
//            }
//        }
//        
//        if (realBmpW <= desireH && realBmpH <= desireH) {
//            sample = 1;
//        }
//        sample = Math.min(realBmpW / desireW, realBmpH / desireH);
//        if (((realBmpW * realBmpH) / (sample * sample)) > PBConstant.MAX_SINGLE_IMAGE_PIX) {
//            sample = ((int) Math.ceil(Math.sqrt((realBmpW * realBmpH)
//                    / (double) PBConstant.MAX_SINGLE_IMAGE_PIX)));
//        }
//        Log.i("mapp", "[samplesize] sample image realW=" + realBmpW + "  realH=" + realBmpH);
//        Log.i("mapp", "[samplesize] desire image desireW=" + desireW + "  desireH=" + desireH + " >>> sample=" + sample);
//        return sample;
//    }
    
    /**
     * Compute the sample size as a function of minSideLength and
     * maxNumOfPixels. minSideLength is used to specify that minimal width or
     * height of a bitmap. maxNumOfPixels is used to specify the maximal size in
     * pixels that is tolerable in terms of memory usage.
     * 
     * The function returns a sample size based on the constraints. Both size
     * and minSideLength can be passed in as IImage.UNCONSTRAINED, which
     * indicates no care of the corresponding constraint. The functions prefers
     * returning a sample size that generates a smaller bitmap, unless
     * minSideLength = IImage.UNCONSTRAINED.
     * 
     * Also, the function rounds up the sample size to a power of 2 or multiple
     * of 8 because BitmapFactory only honors sample size this way. For example,
     * BitmapFactory downsamples an image by 2 even though the request is 3. So
     * we round up the sample size to avoid OOM.
     */
    public static int computeSampleSize(String filename, int maxResolutionX,
            int maxResolutionY) {
        if (TextUtils.isEmpty(filename))
            return 1;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // BitmapFactory.decodeStream(stream, null, options);
        // BitmapFactory.decodeFile(byteIn, options);
        // BitmapFactory.decodeByteArray(byteIn, 0, byteIn.length);
        BitmapFactory.decodeFile(filename, options);
        /*Log.i("mapp", "org bitmap width=" + options.outWidth + "  height="
                + options.outHeight);*/
        int maxNumOfPixels = maxResolutionX * maxResolutionY;
        int minSideLength = Math.min(maxResolutionX, maxResolutionY) / 2;
        return computeSampleSize(options, minSideLength, maxNumOfPixels);
    }

    /**
     * 
     * @param options
     * @param minSideLength
     * @param maxNumOfPixels
     * @return
     */
    public static int computeSampleSize(BitmapFactory.Options options,
            int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        Log.d("mapp", "> sample size needed: " + roundedSize);
        return roundedSize;
    }

    /**
     * 
     * @param options
     * @param minSideLength
     * @param maxNumOfPixels
     * @return
     */
    public static int computeInitialSampleSize(BitmapFactory.Options options,
            int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == UNCONSTRAINED) ? 1 : (int) Math
                .ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == UNCONSTRAINED) ? 128 : (int) Math
                .min(Math.floor(w / minSideLength), Math.floor(h
                        / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == UNCONSTRAINED)
                && (minSideLength == UNCONSTRAINED)) {
            return 1;
        } else if (minSideLength == UNCONSTRAINED) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    /**
     * Util for getting rotate info of bitmap.
     * @param context can be null.
     * @param path file path want to get rotation info from.
     * @return
     */
    public static float rotationForImage(Context context, String path) {
        if (!TextUtils.isEmpty(path)) {
            try {
                ExifInterface exif = new ExifInterface(path);
                int rotation = (int) exifOrientationToDegrees(exif
                        .getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_NORMAL));
                return rotation;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return 0.0f;
    }

    /**
     * 
     * @param exifOrientation
     * @return
     */
    private static float exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    /**
     * @param activity
     * @param path
     * @return
     */
	public static Bitmap loadThumbnailImage(Activity activity, String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        if (activity == null) {
        	return null;
        }
        
        Bitmap bmp = null;
        Cursor cursor = null;  
        try {
	        String selection = MediaStore.Images.ImageColumns.DATA
	        		+ "='"
	        		+ path
	        		+ "'";
	        String[] projection = { MediaStore.Images.ImageColumns._ID };	
	        int originalImageId = -1;
	        
	        cursor = activity.getContentResolver().query(
	        		MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
	        		projection, 
	        		selection, 
	        		null, 
	        		null);
	        if (cursor != null) {
	            if (cursor.moveToFirst()) {
		            originalImageId = cursor.getInt(cursor
		                    .getColumnIndex(MediaStore.Images.Media._ID));
	            }
	        }
	
	        // Get (or create upon demand) the micro thumbnail for the original
	        // image.
	        if (originalImageId != -1) {
	        	bmp =  MediaStore.Images.Thumbnails.getThumbnail(activity
		                .getContentResolver(), originalImageId,
		                MediaStore.Images.Thumbnails.MICRO_KIND, null);
	        }
        } catch (Exception e) {
        	e.printStackTrace();        	
        } finally {
        	try {
        		cursor.close();
        		cursor = null;
        	} catch (Exception e) {}
        }       

        if (bmp == null) {
    		bmp = BitmapFactory.decodeFile(path, null);
    	}
        return bmp;
    }
}
