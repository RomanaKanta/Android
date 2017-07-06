//package com.smartmux.videoeditor.utils;
//
//import java.io.BufferedInputStream;
//import java.io.BufferedOutputStream;
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.FileReader;
//import java.io.IOException;
//import java.io.OutputStream;
//import java.io.PrintWriter;
//import java.nio.channels.ClosedChannelException;
//import java.nio.channels.FileChannel;
//import java.nio.channels.NonReadableChannelException;
//import java.nio.channels.NonWritableChannelException;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.Formatter;
//import java.util.List;
//import java.util.Locale;
//
//import javax.crypto.Cipher;
//import javax.crypto.SecretKey;
//import javax.crypto.SecretKeyFactory;
//import javax.crypto.spec.DESKeySpec;
//
//import android.annotation.SuppressLint;
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.app.Dialog;
//import android.app.WallpaperManager;
//import android.content.ComponentName;
//import android.content.ContentResolver;
//import android.content.ContentUris;
//import android.content.ContentValues;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.pm.ResolveInfo;
//import android.database.Cursor;
//import android.database.CursorIndexOutOfBoundsException;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Matrix;
//import android.media.MediaPlayer;
//import android.net.Uri;
//import android.os.Environment;
//import android.os.IBinder;
//import android.os.Parcelable;
//import android.provider.MediaStore;
//import android.provider.MediaStore.Images;
//import android.provider.MediaStore.Images.Media;
//import android.provider.MediaStore.Video;
//import android.text.Editable;
//import android.text.InputFilter;
//import android.text.Spanned;
//import android.text.TextUtils;
//import android.text.TextWatcher;
//import android.util.Base64;
//import android.util.Log;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.WindowManager;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.EditText;
//import android.widget.TextView;
//
//
//
///**
// * general utilities with method create dialog, checkign sdcard, covert string, etc
// * @author lent5
// */
//public class PBGeneralUtils {
//    // 20120418 add to support video function <S>
//    private static StringBuilder sFormatBuilder = new StringBuilder();
//    private static Formatter sFormatter = new Formatter(sFormatBuilder, Locale.getDefault());
//    private static final Object[] sTimeArgs = new Object[5];
//    private static volatile float density = -1;
//    
//   
//    
//   
//    
//
//    /**
//     * check sdcard is mount or not
//     * @param context
//     * @param showNotify if true show dialog notify if sdcard not mounted
//     * @param buttonClick1
//     * @return
//     */
//    @SuppressWarnings("deprecation")
//	public static boolean checkSdcard(Context context, 
//            boolean showNotify, DialogInterface.OnClickListener buttonClick1) {
//        boolean rs = android.os.Environment.getExternalStorageState().equals(
//                android.os.Environment.MEDIA_MOUNTED);
//
//       
//        return rs;
//    }
//    
//    public static boolean checkExternalStorage(Context context, DialogInterface.OnClickListener listener) {
//        if (context == null) {
//            return false;
//        }
//        boolean isExternalStorageEnough = PBMemoryCheck.isExternalStorageEnough();
//        Log.i("mapp", ">>> is external memory enough space for upload video: " + isExternalStorageEnough);
//        if (!isExternalStorageEnough) {
//            PBGeneralUtils
//                .showAlertDialogActionWithOnClick(
//                        context,
//                        android.R.drawable.ic_dialog_alert,
//                        null,
//                        context.getString(R.string.pb_choose_file_external_storage_msg),
//                        context.getString(R.string.pb_btn_OK), 
//                        listener);
//        }
//        return isExternalStorageEnough;
//    }
//    
//    public static boolean checkExternalStorageFull(Context context, DialogInterface.OnClickListener listener) {
//        if (context == null) {
//            return false;
//        }
//        boolean isExternalStorageNotFull = PBMemoryCheck.isExternalStorageNotFull();
//        Log.i("mapp", ">>> is external memory enough space for download: " + isExternalStorageNotFull);
//        if (!isExternalStorageNotFull) {
//            PBGeneralUtils
//                .showAlertDialogActionWithOnClick(
//                        context,
//                        android.R.drawable.ic_dialog_alert,
//                        null,
//                        context.getString(R.string.pb_choose_file_external_storage_msg),
//                        context.getString(R.string.pb_btn_OK), 
//                        listener);
//        }
//        return isExternalStorageNotFull;
//    }
//    
//    /**
//     * check sdcard is mount or not
//     * @param context
//     * @param showNotify if true show dialog notify if sdcard not mounted
//     * @param buttonClick1
//     * @return
//     */
//    @SuppressWarnings("deprecation")
//	public static boolean checkSdcard(Context context, 
//            boolean showNotify, boolean cancelable, DialogInterface.OnClickListener buttonClick1) {
//        boolean rs = android.os.Environment.getExternalStorageState().equals(
//                android.os.Environment.MEDIA_MOUNTED);
//
//        // show notification dialog
//        if(showNotify && !rs && context != null){
//            try{
//                PBCustomDialog dialog = new PBCustomDialog(context);                
//                dialog.setButton(context.getString(R.string.pb_btn_OK), buttonClick1);
//                dialog.setTitle(R.string.pb_dl_sdcard_checking_title);
//                dialog.setIcon(android.R.drawable.ic_dialog_alert);
//                dialog.setMessage(context.getString(R.string.pb_dl_sdcard_checking_message));
//                dialog.setCancelable(cancelable);
//                dialog.show();
//            }catch (Exception e) {
//                Log.w(PBConstant.TAG, e.getMessage());
//            }
//        }
//
//        return rs;
//    }
//
//    /**
//     * get real path of file store local cache on phone
//     * <p>If the file url contains "can_save=0", it will return an 
//     * internal path that under the application's folder, else it 
//     * will return the external path in sdcard.</p>
//     *  @param photoUrl
//     *  @return file's path.
//     */
//    public static String getPathFromCacheFolder(String photoUrl){
//        if (TextUtils.isEmpty(photoUrl)) {
//            return null;
//        }
//        
//        File dir = new File (getCacheFolderPath(!photoUrl.contains("can_save=0")));
//        if(!dir.exists()){
//            dir.mkdirs();
//        }
//        
//        return dir.getAbsolutePath() + "/" + photoUrl.hashCode();        
//    }
//    
//    /**
//     * return <code>"mnt/sdcard/.photobag_cache"</code>.<br>
//     * */
//    public static String getCacheFolderPath() {
//    	return getCacheFolderPath(true);
//    }
//    
//    /**
//     * Get Cache Folder Path, which stored the temp file of media.<br>
//     * @param isExternal is external path.
//     * @return <code>"mnt/sdcard/.photobag_cache"</code> if isExternal is true,<br> 
//     * else <code>"data/data/com.kobayashi.photobox/app_.photobag_cache"</code>
//     * */
//    public static String getCacheFolderPath(boolean isExternal) {
//    	if (isExternal) {
//    		return Environment.getExternalStorageDirectory().getAbsolutePath() 
//                    + "/" + PBConstant.CACHE_FOLDER_NAME;
//    	} else {
//    		return PBApplication.getBaseApplicationContext()
//    				.getDir(PBConstant.CACHE_FOLDER_NAME, Context.MODE_PRIVATE).getAbsolutePath();
//    	}
//    }
//    
//    public static String getTokenFromCacheFolder() {
//    	
//    	BufferedReader reader = null;
//    	try {
//    		String filename = ("http://photobag.in/photo/" + PBApplication.getMobileCode()).hashCode() + "";
//    		File file = new File(getCacheFolderPath(), filename);
//    		if (!file.exists()) {
//    			return null;
//    		}
//    		
//    		String line, results = "";
//    		reader = new BufferedReader(new FileReader(getCacheFolderPath() + "/" + filename));
//    		while (!TextUtils.isEmpty(line = reader.readLine())) {
//    			results += line;
//    		}
//    		
//    		String tmp = PBApplication.getMobileCode().hashCode() + "";
//    		byte b[] = tmp.getBytes("UTF8");
//    		DESKeySpec keySpec = new DESKeySpec(b);
//    		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
//    		SecretKey key = keyFactory.generateSecret(keySpec);
//    		
//    		Cipher cipher = Cipher.getInstance("DES");
//    		cipher.init(Cipher.DECRYPT_MODE, key);
//    		
//    		byte[] encrypt = Base64.decode(results, Base64.DEFAULT);
//    		String decrypedPwd = new String(cipher.doFinal(encrypt), "UTF8");
//    		
//    		String[] data = decrypedPwd.split("____");
//    		if (data == null || data.length != 3) {
//    			return null;
//    		}
//    		
//    		PBPreferenceUtils.saveStringPref(
//    				PBApplication.getBaseApplicationContext(), 
//    				PBConstant.PREF_NAME, PBConstant.PREF_NAME_TOKEN, data[0]);
//    		PBPreferenceUtils.saveStringPref(
//    				PBApplication.getBaseApplicationContext(), 
//    				PBConstant.PREF_NAME, PBConstant.PREF_NAME_UID, data[1]);
//    		PBPreferenceUtils.saveStringPref(
//    				PBApplication.getBaseApplicationContext(), 
//    				PBConstant.PREF_NAME, PBConstant.PREF_NAME_INVITE_CODE, data[2]);
//    		
//    		return data[0];
//    		
//    	} catch (Exception e) {
//    		e.printStackTrace();
//    	} finally {
//			try {
//				if (reader != null)
//					reader.close();
//			} catch (IOException e) {}
//    	}
//    	
//    	return null;
//    }
//    
//    public static void saveTokenToCacheFolder() {
//    	
//    	String dirPath = getCacheFolderPath();
//    	
//    	// Check Cache Folder Exist,If not then Create
//        File fileDir = new File(dirPath);
//        if (!fileDir.exists()) {
//        	if (!fileDir.mkdirs()) {
//        		Log.e("TravellerLog :: ", "Problem creating Image folder");
//        		}
//        }
//
//    	String filename = ("http://photobag.in/photo/" + PBApplication.getMobileCode()).hashCode() + "";
//		File file = new File(getCacheFolderPath(), filename);
//		if (file.exists()) {
//			return;
//		}
//		
//		PrintWriter out = null;
//    	
//    	try {    		
//    		String token = PBPreferenceUtils.getStringPref(
//    				PBApplication.getBaseApplicationContext(),
//    				PBConstant.PREF_NAME, PBConstant.PREF_NAME_TOKEN, "");
//    		String uid = PBPreferenceUtils.getStringPref(
//    				PBApplication.getBaseApplicationContext(),
//    				PBConstant.PREF_NAME, PBConstant.PREF_NAME_UID, "");
//    		String iv_code = PBPreferenceUtils.getStringPref(
//    				PBApplication.getBaseApplicationContext(),
//    				PBConstant.PREF_NAME, PBConstant.PREF_NAME_INVITE_CODE, "");
//    		
//    		if (TextUtils.isEmpty(token) || TextUtils.isEmpty(uid) || TextUtils.isEmpty(iv_code)) {
//    			return;
//    		}
//    		
//    		Log.d("TOKEN INFO >>>>>>>>>>>>>>filename>>>>>>>>>>>>>>>>>>>>>>>>>>>:", filename);
//    		Log.d("TOKEN INFO >>>>>>>>>>>>>>token>>>>>>>>>>>>>>>>>>>>>>>>>>>:", token);
//    		Log.d("TOKEN INFO >>>>>>>>>>>>>>uid>>>>>>>>>>>>>>>>>>>>>>>>>>>:", uid);
//    		Log.d("TOKEN INFO >>>>>>>>>>>>>>iv_code>>>>>>>>>>>>>>>>>>>>>>>>>>>:", iv_code);
//    		
//    		String tmp = PBApplication.getMobileCode().hashCode() + "";
//    		byte b[] = tmp.getBytes("UTF8");
//    		DESKeySpec keySpec = new DESKeySpec(b);
//    		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
//    		SecretKey key = keyFactory.generateSecret(keySpec);
//    		
//    		Cipher cipher = Cipher.getInstance("DES");
//    		cipher.init(Cipher.ENCRYPT_MODE, key);
//    		String encrypedPwd = Base64.encodeToString(
//    				cipher.doFinal((token + "____" + uid + "____" + iv_code).getBytes("UTF8")), 
//    				Base64.DEFAULT);
//    		
//    		File outputFile	= new File(getCacheFolderPath(), filename);
//    		Log.d("TOKEN INFO >>>>>>>>>>>>>>outputFile>>>>>>>>>>>>>>>>>>>>>>>>>>>:", outputFile.getAbsolutePath());
//    		out = new PrintWriter(outputFile);
//    	    out.print(encrypedPwd);
//    		
//    	} catch (Exception e) {
//    		e.printStackTrace();
//    	} finally {
//    	    if (out != null) out.close();
//    	}
//    }
//    
//    public static boolean checkVideoIsValid(String videoFileSrc) {
//        if (TextUtils.isEmpty(videoFileSrc)) {
//            return false;
//        }
//        
//        boolean result = false;
//        long t1 = System.currentTimeMillis();
//        MediaPlayer mediaPlayer = new MediaPlayer();
//        try {
//            mediaPlayer.setDataSource(videoFileSrc);
//            mediaPlayer.prepare();
//            result = true;
//        } catch (IllegalArgumentException e) {
//            // e.printStackTrace();
//            Log.e(PBConstant.TAG, "-- init for checking video ERROR: " + e.toString());
//        } catch (IllegalStateException e) {
//            // e1.printStackTrace();
//            Log.e(PBConstant.TAG, "-- init for checking video ERROR: " + e.toString());
//        } catch (IOException e) {
//            // e1.printStackTrace();
//            Log.e(PBConstant.TAG, "-- init for checking video ERROR: " + e.toString());
//        }
//        
//        Log.i(PBConstant.TAG, "> check video cached file is OK: " + result);
//        if (mediaPlayer != null) {
//            mediaPlayer.release();
//            mediaPlayer = null;
//        }
//        Log.e(PBConstant.TAG, ">>> check fake video item cost: " + (System.currentTimeMillis() - t1));
//        return result;
//    }
//    
//    /**
//     * Util for copying file by using Linux command. 
//     * @return copying result.
//     */
//    public static boolean copyFileByCommand(String fileSrcPath, String fileDestPath) {
//        if (TextUtils.isEmpty(fileSrcPath) || TextUtils.isEmpty(fileDestPath)) {
//            return false;
//        }
//        
//        boolean copyResult = false;
//        
//        File src = new File(fileSrcPath);
//        File dest = new File(fileDestPath);
//        
//        try {
//          
//          copyResult = copyFile(src, dest);	  
//            
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return copyResult;
//    }
//    
//    public static boolean copyFile(String sourcePath, String destPath)
//            throws IOException {
//        if (TextUtils.isEmpty(destPath) || TextUtils.isEmpty(sourcePath)) {
//            return false;
//        }
//        File src = new File(sourcePath);
//        File dest = new File(destPath);
//
//        return copyFile(src, dest);
//    }
//    
//    public static boolean copyFile(File sourceFile, File destFile)
//            throws IOException {
//        boolean result = false;
//        if (!destFile.exists()) {
//            destFile.createNewFile();
//        }
//
//        FileChannel source = null;
//        FileChannel destination = null;
//        try {
//            source = new FileInputStream(sourceFile).getChannel();
//            destination = new FileOutputStream(destFile).getChannel();
//            destination.transferFrom(source, 0, source.size());
//            result = true;
//        } catch (IllegalArgumentException e) {
//            e.printStackTrace();
//        } catch (NonReadableChannelException e) {
//            e.printStackTrace();
//        } catch (NonWritableChannelException e) {
//            e.printStackTrace();
//        } catch (ClosedChannelException e) {
//            e.printStackTrace();
//        } finally {
//            if (source != null) {
//                source.close();
//            }
//            if (destination != null) {
//                destination.close();
//            }
//        }
//        return result;
//    }
//
//    /**
//     * method support hide soft keyboard on phone screen.
//     */
//    public static void hideSoftKeyboard(Activity activity, IBinder windowToken) {
//        if(activity == null || windowToken == null) return;
//        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);        
//        imm.hideSoftInputFromWindow(/*mSettingInvite.getWindowToken()*/windowToken, 0);
//        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//    }
//
//    /**
//     * store resource to local storage and return file
//     * 
//     * @param context
//     * @param reourceBitmap
//     * @return
//     */
//    public static File getResourceBarCode(Context context, int reourceBitmap) {
//        // InputStream inputStream =
//        // context.getResources().openRawResource(reourceBitmap);
//        if (context == null)
//            return null;
//
//        String filename = "photobagCode.jpg";
//        String filepath = "/mnt/scard/" + filename;
//        File sdCard = Environment.getExternalStorageDirectory();
//        File dir = new File (sdCard.getAbsolutePath() + "/" + PBConstant.CACHE_FOLDER_NAME);
//
//        if (dir != null) {
//            filepath = dir.getAbsolutePath()
//                    + "/" + filename;
//        }
//
//        File barcode = new File(filepath /* context.getFilesDir(), filename */);
//        Log.d("done", "barcode.exists() :" + barcode.exists());
//        if (!barcode.exists() || barcode.length() == 0) {
//            // restore barcode
//            try {
//                // OutputStream outStream =
//                // context.getContentResolver().openOutputStream(uri);
//                Bitmap bm = BitmapFactory.decodeResource(
//                        context.getResources(), reourceBitmap);
//                if (bm == null)
//                    return null;
//
//                OutputStream outStream = new FileOutputStream(barcode);
//                // OutputStream outStream = context.openFileOutput(filename,
//                // Context.MODE_PRIVATE);
//                bm.compress(PBConstant.COMPRESS_FORMAT, PBConstant.DECODE_COMPRESS_PRECENT, outStream);
//                outStream.flush();
//                outStream.close();
//                Log.d("done", "done");
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//                return null;
//            } catch (IOException e) {
//                e.printStackTrace();
//                return null;
//            } catch (OutOfMemoryError e) {
//                e.printStackTrace();
//                return null;
//            }
//        }
//        // Log.d("done", barcode.length() + " - " + barcode.getAbsolutePath());
//        return barcode;
//    }
//
//    /**
//     * parse a date tiem string format to milliseconds
//     * 
//     * @param datetime
//     * @return
//     */
//    public static long parseDateStringToMiliseconds(String datetime) {
//        // EEE MMM dd HH:mm:ss zzz yyyy
//        // "E, dd MMM yyyy HH:mm:ss z"
//        DateFormat formatter;
//        formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
//        Date date = null;
//        try {
//            date = (Date) formatter.parse(datetime);
//        } catch (java.text.ParseException e) {
//            Log.e(PBConstant.TAG, "parsing date failed $ " + datetime);
//        }
//
//        return (date != null) ? date.getTime() : 0;
//    }
//
//    /**
//     * parse a date time to string format
//     * 
//     * @param datetime
//     * @return
//     */
//    public static String parseDateTimeToString(long datetime) {
//        // EEE MMM dd HH:mm:ss zzz yyyy
//        DateFormat formatter;
//        formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");
//
//        Date date = new Date(datetime);
//
//        return formatter.format(date);
//    }
//
//    /** get language local of devide */
//    public static String getCurrentLanguageDevice() {
//        return Locale.getDefault().getLanguage();
//    }
//
//    /**
//     * Convert Unix time (milisecond) to Standard time(second)
//     * 
//     * @param unixTime
//     * @return
//     */
//    public static String convertUnixTimetoStandarTime(long unixTime) {
//        long time = new Long(unixTime).longValue();
//
//        long timestamp = time * 1000;
//        Date date = new Date(timestamp);
//        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy/MM/dd HH:mm");
//        return simpleDate.format(date);
//    }
//
//    /**
//     * Convert milisecond to Standard time (DateTime)
//     * 
//     * @param milisecond
//     * @return
//     */
//    public static String convertMilisecondToDate(long milisecond) {
//        SimpleDateFormat simpleDate = new SimpleDateFormat(
//                "yyyy/MM/dd HH:mm:ss");
//        Date date = new Date(milisecond);
//        return simpleDate.format(date);
//    }
//
//    /**
//     * show the dialog confirm exit or not when user press back key
//     */
//    /*
//    public static void showDialogConfirmExit() {
//        DialogInterface.OnClickListener onClickDialog = new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int whichButton) {
//                // FIXME check null before using
//                if (PBTabBarActivity.sMainContext != null) {
//                    PBTabBarActivity.sMainContext.finish();
//                }
//            }
//        };
//        /*
//     * can't load string
//     * showAlertDialogAction(PBTabBarActivity.mMainContext.
//     * getApplicationContext(),
//     * PBTabBarActivity.mMainContext.getString(R.string
//     * .pb_dialog_exit_confirm_title),
//     * PBTabBarActivity.mMainContext.getString
//     * (R.string.pb_dialog_exit_confirm_message),
//     * PBTabBarActivity.mMainContext.getString(R.string.pb_str_ok) ,
//     * PBTabBarActivity.mMainContext.getString(R.string.pb_cancel),
//     * onClickDialog );
//     */
//
//    // 2012.02.13 @lent fix check bull before use and use string with
//    // resources
//    /*
//        Context context = PBTabBarActivity.sMainContext;
//        if (context != null) {
//            showAlertDialogAction(context,
//                    context.getString(R.string.dialog_confirm_exit_title),
//                    context.getString(R.string.dialog_confirm_exit_message),
//                    context.getString(R.string.dialog_ok_btn),
//                    context.getString(R.string.dialog_cancel_btn),
//                    onClickDialog);
//        }
//
//    }
//     */
//    /**
//     * show a general alert dialog which has title, message , positive and
//     * negative buttons
//     * 
//     * @param context
//     * @param title
//     * @param message
//     * @param positiveButtonName
//     * @param negativeButtonName
//     * @param positiveBtnOnClick
//     */
//    public static void showAlertDialogAction(Context context, String title,
//            String message, String positiveButtonName, String negativeButtonName,
//            DialogInterface.OnClickListener positiveBtnOnClick) {
//        if(context == null) return;
//
//        // PBCustomDialog dialog = new PBCustomDialog(context);
//        AlertDialog dialog = new AlertDialog.Builder(context).create();
//        dialog.setIcon(android.R.drawable.ic_dialog_alert);
//        dialog.setButton(Dialog.BUTTON_POSITIVE, positiveButtonName, positiveBtnOnClick);
//        dialog.setButton(Dialog.BUTTON_NEGATIVE, negativeButtonName, new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//        // 20120329 mod by NhatVT fix #1830 <S>
//        if (!TextUtils.isEmpty(title)) {
//            dialog.setTitle(title);
//        }
//        if (!TextUtils.isEmpty(message)) {
//            dialog.setMessage(message);
//        }
//        // 20120329 mod by NhatVT fix #1830 <E>
//        dialog.setCancelable(false);
//        dialog.show();
//    }
//
//    
//    /**
//     * show a general alert dialog which has  message , positive and
//     * negative buttons
//     * 
//     * @param context
//     * @param message
//     * @param positiveButtonName
//     * @param negativeButtonName
//     * @param positiveBtnOnClick
//     */
//    public static void showAlertDialogActionWithoutTitle(Context context, 
//            String message, String positiveButtonName, String negativeButtonName,
//            DialogInterface.OnClickListener positiveBtnOnClick) {
//        if(context == null) return;
//
//        // PBCustomDialog dialog = new PBCustomDialog(context);
//        AlertDialog dialog = new AlertDialog.Builder(context).create();
//        dialog.setButton(Dialog.BUTTON_POSITIVE, positiveButtonName, positiveBtnOnClick);
//        dialog.setButton(Dialog.BUTTON_NEGATIVE, negativeButtonName, new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//
//        if (!TextUtils.isEmpty(message)) {
//            dialog.setMessage(message);
//        }
//        // 20120329 mod by NhatVT fix #1830 <E>
//        dialog.setCancelable(false);
//        dialog.show();
//    }
//    
//    /**
//     * 
//     * @param context
//     * @param edInput
//     * @param title
//     * @param message
//     * @param buttonName1
//     * @param buttonName2
//     * @param buttonClick1
//     * @return
//     */
//    @SuppressLint("InflateParams") @SuppressWarnings("deprecation")
//	public static AlertDialog showInputAlertDialogAction(Context context, final EditText edInput, String title,
//            String message, String buttonName1, String buttonName2,
//            DialogInterface.OnClickListener buttonClick1) {
//        AlertDialog dialog = null;
//        if(context == null) return dialog;
//        try{
//            // PBCustomDialog dialog = new PBCustomDialog(context);
//            dialog = new AlertDialog.Builder(context).create();
//            dialog.setIcon(android.R.drawable.ic_dialog_alert);
//            dialog.setButton(buttonName1, buttonClick1);
//            dialog.setButton2(buttonName2, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                }
//            });
//            // dialog.setTitle(title);
//            final int maximum = 138;
//            LayoutInflater factory = LayoutInflater.from(context);
//            final View loginEntryView = factory.inflate(R.layout.pb_input_dialog_title_view, null);
//            TextView tvTitle = (TextView)loginEntryView.findViewById(R.id.tittle);
//            tvTitle.setText(title);
//            final TextView tvTitleCount = (TextView)loginEntryView.findViewById(R.id.title_counter);
//            // fix # 1809 wrong counter initial 
//            tvTitleCount.setText(Integer.toString(maximum - message.length()));
//            dialog.setCustomTitle(loginEntryView);
//            // add filter maximum
//            edInput.setGravity(Gravity.TOP);
//            edInput.setMinLines(5);
//            edInput.setMaxLines(5);
//            edInput.setText(message);
//            InputFilter[] FilterArray = new InputFilter[2];
//            FilterArray[0] = new InputFilter.LengthFilter(maximum);
//            FilterArray[1] = new InputFilter() {
//                @Override
//                public CharSequence filter(CharSequence source, int start, int end,
//                        Spanned dest, int dstart, int dend) {
//                    /*if(edInput.getLineCount() >= 5){
//                        if(edInput.getLineCount() == 5 && 
//                                (source.length()-1 >= 0) && source.charAt(source.length()-1) != '\n'){
//                            return source;
//                        }
//                        return "";
//                    }
//                    return null;*/ return source;
//                }
//            };
//            edInput.addTextChangedListener(new TextWatcher() {
//                
//                @Override
//                public void onTextChanged(CharSequence s, int start, int before, int count) {
//                }
//                @Override
//                public void beforeTextChanged(CharSequence s, int start, int count,
//                        int after) {
//                }
//
//                @Override
//                public void afterTextChanged(Editable s) {
//                    if(s != null && tvTitleCount != null){
//                        tvTitleCount.setText(Integer.toString(maximum - s.length()));
//                    }
//                }
//            });
//            edInput.setFilters(FilterArray);
//            dialog.setView(edInput);
//            // dialog.setMessage(message);
//            dialog.show();
//        }catch (Exception e) {
//            Log.e(PBConstant.TAG, "could not show dialog ...");
//            // e.printStackTrace();
//        }
//        return dialog;
//    }
//    /**
//     * show a general alert dialog which has title, message , positive and
//     * negative buttons
//     * 
//     * @param context
//     * @param title
//     * @param message
//     * @param buttonName1
//     * @param buttonName2
//     * @param buttonClick1
//     * @param buttonClick2
//     */
//    @SuppressWarnings("deprecation")
//	public static void showAlertDialogAction(Context context, String title,
//            String message, String buttonName1, String buttonName2,
//            DialogInterface.OnClickListener buttonClick1,
//            DialogInterface.OnClickListener buttonClick2) {
//        if(context == null) return;
//        try{
//            // PBCustomDialog dialog = new PBCustomDialog(context);
//            AlertDialog dialog = new AlertDialog.Builder(context).create();
//            dialog.setIcon(android.R.drawable.ic_dialog_alert);
//            dialog.setButton(buttonName1, buttonClick1);
//            dialog.setButton2(buttonName2, buttonClick2);
//            dialog.setTitle(title);
//            dialog.setMessage(message);
//            dialog.setCancelable(false);// 20120507 diable BackHW key when this dialog is showing!!!
//            dialog.show();
//        }catch (Exception e) {
//            Log.e(PBConstant.TAG, "could not show dialog ...");
//        }
//    }
//    
//    /**
//     * show a general alert dialog which has title, message , positive and
//     * negative buttons
//     * 
//     * @param context
//     * @param title
//     * @param message
//     * @param buttonName1
//     * @param buttonClick1
//     */
//    @SuppressWarnings("deprecation")
//	public static void showAlertDialogAction(Context context, String title,
//            String message, String buttonName1,
//            DialogInterface.OnClickListener buttonClick1) {
//        if(context == null) return;
//        try{
//            // PBCustomDialog dialog = new PBCustomDialog(context);
//            AlertDialog dialog = new AlertDialog.Builder(context).create();
//            dialog.setButton(buttonName1, buttonClick1);
//            dialog.setTitle(title);
//            dialog.setMessage(message);
//            dialog.setCancelable(false);// 20120507 diable BackHW key when this dialog is showing!!!
//            dialog.show();
//        }catch (Exception e) {
//            Log.e(PBConstant.TAG, "could not show dialog ...");
//        }
//    }
//    
//    /**
//     * 
//     * @param context
//     * @param title
//     * @param message
//     * @param buttonName1
//     * @param buttonClick1
//     */
//    @SuppressWarnings("deprecation")
//	public static void showAlertDialogActionWithOnClick(Context context,
//            int iconId, String title, String message, String buttonName1,
//            DialogInterface.OnClickListener buttonClick1) {
//        if(context == null) return;
//        try{
//            // PBCustomDialog dialog = new PBCustomDialog(context);
//            AlertDialog dialog = new AlertDialog.Builder(context).create();
//            dialog.setButton(buttonName1, buttonClick1);
//            if (!TextUtils.isEmpty(title)) {
//                dialog.setTitle(title);
//            }
//            dialog.setIcon(iconId);
//            if (!TextUtils.isEmpty(message)) {
//                dialog.setMessage(message);
//            }
//            dialog.show();
//        }catch (Exception e) {
//            Log.e(PBConstant.TAG, "could not show dialog ...");
//        }
//    }
//
//    /**
//     * @param context
//     * @param title
//     * @param message
//     * @param buttonName1
//     * @param buttonClick1
//     */
//    @SuppressWarnings("deprecation")
//	public static void showAlertDialogActionWithOnClick(Context context,
//            int iconId, String title, String message, String buttonName1,
//            DialogInterface.OnClickListener buttonClick1, boolean cancelable) {
//        if(context == null) return;
//        try{
//            // PBCustomDialog dialog = new PBCustomDialog(context);
//            AlertDialog dialog = new AlertDialog.Builder(context).create();
//            dialog.setButton(buttonName1, buttonClick1);
//            if (!TextUtils.isEmpty(title)) {
//                dialog.setTitle(title);
//            }
//            dialog.setIcon(iconId);
//            dialog.setMessage(message);
//            dialog.setCancelable(cancelable);
//            dialog.show();
//        }catch (Exception e) {
//            Log.e(PBConstant.TAG, "could not show dialog ...");
//        }
//    }
//    /**
//     * show a general alert dialog which has title, message , positive and
//     * negative buttons
//     * 
//     * @param context
//     * @param title
//     * @param message
//     * @param buttonName1
//     * @param buttonName2
//     * @param buttonClick1
//     */
//    @SuppressWarnings("deprecation")
//	public static AlertDialog showAlertDialogActionWithOnClick(Context context,
//            String title, String message, String buttonName1,
//            DialogInterface.OnClickListener buttonClick1) {
//        AlertDialog dialog = null;
//        if(context == null) return dialog;
//        try{
//            /*AlertDialog */dialog = new AlertDialog.Builder(context).create();
//            dialog.setIcon(android.R.drawable.ic_dialog_alert);
//            dialog.setButton(buttonName1, buttonClick1);
//            dialog.setTitle(title);
//            dialog.setMessage(message);
//            dialog.setCancelable(false);
//            dialog.show();
//        }catch (Exception e) {
//            Log.e(PBConstant.TAG, "could not show dialog ...");
//        }
//        return dialog;
//    }
//
//    /**
//     * show a general alert dialog which has not title, message , positive and
//     * negative buttons
//     * 
//     * @param context
//     * @param message
//     * @param buttonName1
//     * @param buttonClick1
//     */
//    @SuppressWarnings("deprecation")
//	public static void showAlertDialogNoTitleWithOnClick(Context context,
//            String message, String buttonName1,
//            DialogInterface.OnClickListener buttonClick1) {
//        if(context == null) return;
//        try{
//            AlertDialog dialog = new AlertDialog.Builder(context).create();
//            dialog.setIcon(R.drawable.icon);
//            dialog.setButton(buttonName1, buttonClick1);
//            dialog.setMessage(message);
//            dialog.setCancelable(false);
//            dialog.show();
//        }catch (Exception e) {
//            Log.e(PBConstant.TAG, "could not show dialog ...");
//        }
//    }
//    
//    /**
//     * show a general alert dialog which has not title, message , positive and
//     * negative buttons
//     * 
//     * @param context
//     * @param message
//     */
//    public static void showAlertDialogNoTitleWithOnClick(Context context,
//            String message) {
//        if(context == null) return;
//        try{
//            AlertDialog dialog = new AlertDialog.Builder(context).create();
//            dialog.setIcon(R.drawable.icon);
//            dialog.setMessage(message);
//            dialog.setCancelable(true);
//            dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "OK", new DialogInterface.OnClickListener() {
//				
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					dialog.dismiss();
//				}
//			});
//            dialog.show();
//        }catch (Exception e) {
//            Log.e(PBConstant.TAG, "could not show dialog ...");
//        }
//    }
//
//    
//    /**
//     * show a general alert dialog which has not title, message , positive and
//     * negative buttons
//     * 
//     * @param context
//     * @param message
//     * @param buttonName1
//     * @param buttonClick1
//     */
//    @SuppressWarnings("deprecation")
//	public static void showAlertDialogNoTitleNoIconWithOnClick(Context context,
//            String message, String buttonName1,
//            DialogInterface.OnClickListener buttonClick1) {
//        if(context == null) return;
//        try{
//            AlertDialog dialog = new AlertDialog.Builder(context).create();
//            dialog.setButton(buttonName1, buttonClick1);
//            dialog.setMessage(message);
//            dialog.setCancelable(false);
//            dialog.show();
//        }catch (Exception e) {
//            Log.e(PBConstant.TAG, "could not show dialog ...");
//        }
//    }
//    
//    /**
//     * show a general alert dialog which has title, message , positive and
//     * negative buttons
//     * 
//     * @param context
//     * @param title
//     * @param message
//     * @param buttonName1
//     */
//    @SuppressWarnings("deprecation")
//	public static void showAlertDialogActionWithOkButton(Context context,
//            String title, String message, String buttonName1) {
//        if(context == null) return;
//
//        // PBCustomDialog dialog = new PBCustomDialog(context);
//        try{
//            AlertDialog dialog = new AlertDialog.Builder(context).create();
//            dialog.setButton(buttonName1, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                }
//            });
//            dialog.setIcon(R.drawable.icon);
//            dialog.setTitle(title);
//            dialog.setMessage(message);
//            dialog.setCancelable(false);
//            dialog.show();
//        }catch (Exception e) {
//            Log.e(PBConstant.TAG, "could not show dialog ...");
//        }
//    }
//    
//    /**
//     * Insert an image and create a thumbnail for it.
//     *
//     * @param cr The content resolver to use
//     * @param source The stream to use for the image
//     * @param title The name of the image
//     * @param description The description of the image
//     * @return The URL to the newly created image, or <code>null</code> if the image failed to be stored
//     *              for any reason.
//     */
//    public static final String insertImage(ContentResolver cr, Bitmap source,
//                                           String title, String description) {
//        ContentValues values = new ContentValues();
//        values.put(Images.Media.TITLE, title);
//        values.put(Images.Media.DESCRIPTION, description);
//        values.put(Images.Media.MIME_TYPE, "image/jpeg");
//
//        Uri url = null;
//        String stringUrl = null;    /* value to be returned */
//        boolean isSaveImgSuccess = true;
//
//        try {
//            url = cr.insert(Media.EXTERNAL_CONTENT_URI, values);
//
//            /*
//            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//            File imageFileName = new File(path, "1.jpg"); //imageFileFolder
//            try
//            {
//            	FileOutputStream out = new FileOutputStream(imageFileName);
//            	source.compress(Bitmap.CompressFormat.JPEG, 100, out);
//              out.flush();
//              out.close();
//              //scanPhoto(imageFileName.toString());
//              out = null;
//            } catch (Exception e)
//            {
//              e.printStackTrace();
//            }
//            */
//            
//            
//            if (source != null) {
//                OutputStream imageOut = cr.openOutputStream(url);
//                try {
//                    source.compress(Bitmap.CompressFormat.PNG, 100, imageOut);
//                } finally {
//                    imageOut.close();
//                }
//                /*
//                long id = ContentUris.parseId(url);
//                // Wait until MINI_KIND thumbnail is generated.
//                Bitmap miniThumb = Images.Thumbnails.getThumbnail(cr, id,
//                        Images.Thumbnails.MINI_KIND, null);
//                // This is for backward compatibility.
//                Bitmap microThumb = storeThumbnail(cr, miniThumb, id, 50F, 50F,
//                        Images.Thumbnails.MICRO_KIND);
//                */
//            } else {
//                Log.e(PBConstant.TAG, "Failed to create thumbnail, removing original");
//                isSaveImgSuccess = false;
//            }
//        } catch (Exception e) {
//            Log.e(PBConstant.TAG, "Failed to insert image", e);
//            isSaveImgSuccess = false;
//        } catch (OutOfMemoryError e) {
//            Log.e(PBConstant.TAG, "Failed to insert image", e);
//            isSaveImgSuccess = false;
//        }
//        if (!isSaveImgSuccess) {
//            if (url != null) {
//                cr.delete(url, null, null);
//                url = null;
//            }
//        }
//        if (url != null) {
//            stringUrl = url.toString();
//        }
//        return stringUrl;
//    }
//    
//    /**
//     * Insert a video and create a thumbnail for it.
//     *
//     * @param cr The content resolver to use
//     * @param source The stream to use for the video.
//     * @param title The name of the image
//     * @param description The description of the image
//     * @param videoDuration 
//     * @return The URL to the newly created image, or <code>null</code> if the image failed to be stored
//     *              for any reason.
//     */
//    public static final String insertVideo(ContentResolver cr, String source,
//                                           String title, String description, long videoDuration) {
//        ContentValues values = new ContentValues();
//        values.put(Video.Media.TITLE, title);
//        values.put(Video.Media.DESCRIPTION, description);
//        values.put(Video.Media.MIME_TYPE, "video/3gpp");
//        if (videoDuration > 0) {
//            values.put(Video.Media.DURATION, videoDuration);
//        }
//
//        Uri url = null;
//        String stringUrl = null;    /* value to be returned */
//        boolean isSaveVideoSuccess = true;
//
//        try {
//            url = cr.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
//            if (!TextUtils.isEmpty(source)) {
//                OutputStream imageOut = cr.openOutputStream(url);
//                if (imageOut == null) {
//                    return stringUrl;
//                }
//                FileInputStream inputStream = new FileInputStream(new File(source));
//                try {
//                    byte[] buffer = new byte[4096];
//                    int len;
//                    while ((len = inputStream.read(buffer)) != -1){
//                        imageOut.write(buffer, 0, len);
//                    }
//                } finally {
//                    if (inputStream != null) {
//                        try {
//                            inputStream.close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                            isSaveVideoSuccess = false;
//                        }
//                    }
//                    if (imageOut != null) {
//                        try {
//                            imageOut.close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                            isSaveVideoSuccess = false;
//                        }
//                    }
//                }                
//            } else {
//                Log.e(PBConstant.TAG, "Failed to create thumbnail, removing original");
//                isSaveVideoSuccess = false;
//            }
//        } catch (Exception e) {
//            Log.e(PBConstant.TAG, "Failed to insert image", e);
//            isSaveVideoSuccess = false;
//        } catch (OutOfMemoryError e) {
//            Log.e(PBConstant.TAG, "Failed to insert image", e);
//            isSaveVideoSuccess = false;
//        }
//        if (!isSaveVideoSuccess) {
//            if (url != null) {
//                cr.delete(url, null, null);
//                url = null;
//            }
//        }
//        if (url != null) {
//            stringUrl = url.toString();
//        }
//        return stringUrl;
//    }
//    
//    /**
//     * Insert an image and create a thumbnail for it.
//     *
//     * @param cr The content resolver to use
//     * @param source The stream to use for the image
//     * @param title The name of the image
//     * @param description The description of the image
//     * @return The URL to the newly created image, or <code>null</code> if the image failed to be stored
//     *              for any reason.
//     */
//	public static final String insertImage(ContentResolver cr, String source,
//                                           String title, String description) {
//		
//		ContentValues values = new ContentValues();
//        values.put(Images.Media.TITLE, title);
//        values.put(Images.Media.DESCRIPTION, description);
//        values.put(Images.Media.MIME_TYPE, "image/jpeg");
//        
//        Uri url = null;
//        String stringUrl = null;    /* value to be returned */
//        boolean isSaveImgSuccess = true;
//
//        try {
//            url = cr.insert(Media.EXTERNAL_CONTENT_URI, values);
//            
//            if (!TextUtils.isEmpty(source)) {
//            	//Uri.fromFile(Environment.getExternalStorageDirectory())
//            	
//                OutputStream imageOut = cr.openOutputStream(url);
//                
//                if (imageOut == null) {
//                    return stringUrl;
//                }
//                
//                FileInputStream inputStream = new FileInputStream(source);
//                
//                try {
//                    byte[] buffer = new byte[1024];
//                    while (true) {
//                        int bytesRead = inputStream.read(buffer);
//                        if (bytesRead <= 0) {
//                            break;
//                        }
//                        imageOut.write(buffer, 0, bytesRead);
//                    }
//                    // source.compress(Bitmap.CompressFormat.PNG, 100, imageOut);
//                } finally {
//                    if (inputStream != null) {
//                        try {
//                            inputStream.close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                            isSaveImgSuccess = false;
//                        }
//                    }
//                    if (imageOut != null) {
//                        try {
//                            imageOut.close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                            isSaveImgSuccess = false;
//                        }
//                    }
//                }
//            } else {
//                Log.e(PBConstant.TAG, "Failed to create thumbnail, removing original");
//                isSaveImgSuccess = false;
//            }
//        }  catch (OutOfMemoryError e) {
//            Log.e(PBConstant.TAG, "Failed to Insert Image", e);
//            isSaveImgSuccess = false;
//        }catch (FileNotFoundException e) {
//            Log.e(PBConstant.TAG, "FileNotFoundException", e);
//            isSaveImgSuccess = false;
//        }catch (Exception e) {
//            Log.e(PBConstant.TAG, "Failed to Insert Image", e);
//            isSaveImgSuccess = false;
//        }
//        if (!isSaveImgSuccess) {
//            if (url != null) {
//                cr.delete(url, null, null);
//                url = null;
//            }
//        }
//        if (url != null) {
//            stringUrl = url.toString();
//        }
//        return stringUrl;
//    }
//    
//	
//	   /**
//     * Insert a video and create a thumbnail  for it for Android API Level 19(Kitkat)..
//     *
//     * @param cr The content resolver to use
//     * @param source The stream to use for the video.
//     * @param title The name of the image
//     * @param description The description of the image
//     * @param videoDuration 
//     * @param context application context
//     * @author atikur
//     * @return The URL to the newly created image, or <code>null</code> if the image failed to be stored
//     *              for any reason.
//     */
//    public static final String insertVideoKitkat(ContentResolver cr, String source,
//                                           String title, String description, long videoDuration, Context context) {
//        boolean isSaveVideoSuccess = true;
//
//        String filePath = null;
//        filePath = PBGeneralUtils.getPathFromCacheFolder(source);
//        if (!TextUtils.isEmpty(filePath)) { 
//        	System.out.println("Source video file path not exists");
//        }
//        /*String desFilePath = null;
//        filePath = PBGeneralUtils.getPathFromCacheFolder(source);
//        
//        if (!TextUtils.isEmpty(filePath)) {
//        			desFilePath = PBGeneralUtils.getCacheFolderPath()
//            		+ PBConstant.SPLASH_CHAR + filePath.hashCode();
//        	
//        	 + PBConstant.CACHE_SHARE_FILE_NAME;
//            if (new File(desFilePath).mkdirs()) {
//                Log.i("mapp", "> Create temp folder for sharing OK!");
//            }
//            
//            
//            desFilePath = desFilePath 
//                    + PBConstant.SPLASH_CHAR
//                    + PBConstant.CACHE_SHARE_FILE_NAME + PBConstant.MEDIA_VIDEO_EXT_3GP;
//        }*/
//        
//        // Customized folder structure path in SDCard
//    	File getFile = null;
//		String createFileName ="";
//		createFileName =  filePath.hashCode()
//        						+PBConstant.MEDIA_VIDEO_EXT_3GP;
//		File saveDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
//	    if ( !saveDir.exists() || !saveDir.isDirectory() ) {
//			if(!saveDir.mkdir()){
//				return null;	
//			}
//		}
//		getFile  = new File(saveDir, createFileName);
//
//        
//        /*String destinationFilename = android.os.Environment.getExternalStorageDirectory().getPath()
//	        		+File.separatorChar+filePath.hashCode()
//	        		+PBConstant.MEDIA_VIDEO_EXT_3GP;*/
//        
//        BufferedInputStream bis = null;
//        BufferedOutputStream bos = null;
//        
//        try {
//			bis = new BufferedInputStream(new FileInputStream(source));
//			bos = new BufferedOutputStream(new FileOutputStream(getFile.getAbsolutePath(), false));
//			byte[] buf = new byte[1024];
//			bis.read(buf);
//			do {
//		        bos.write(buf);
//		      } while(bis.read(buf) != -1);
//			
//		} catch (FileNotFoundException e1) {
//			isSaveVideoSuccess = false;
//		} catch (IOException e) {
//			isSaveVideoSuccess = false;
//		} finally {
//		      try {
//
//		          if (bis != null) bis.close();
//		          if (bos != null) bos.close();
//
//		    	  
//		    	  Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//		    	  File f = new File(getFile.getAbsolutePath());
//		    	  Uri contentUri = Uri.fromFile(f);
//		    	  mediaScanIntent.setData(contentUri);
//		    	  context.sendBroadcast(mediaScanIntent);
//		        } catch (IOException e) {
//		        	isSaveVideoSuccess = false;
//		        }
//		      }
//        if (!isSaveVideoSuccess) {
//        	return null;
//        } else {
//        	return getFile.getAbsolutePath();
//        }
//        
//    }
//    
//    /**
//     * Insert an image and create a thumbnail for it for Android API Level 19(Kitkat).
//     *
//     * @param cr The content resolver to use
//     * @param source The stream to use for the image
//     * @param title The name of the image
//     * @param description The description of the image
//     * @param context application context
//     * @return The URL to the newly created image, or <code>null</code> if the image failed to be stored
//     *              for any reason.
//     */
//	public static final String insertImageKitkat(ContentResolver cr, String source,
//                                           String title, String description, Context context) {
//		
//
//        
//        boolean isSaveImgSuccess = true;
//
//        /*String destinationFilename = android.os.Environment.getExternalStorageDirectory().getPath()
//        		+File.separatorChar+source.hashCode()
//        		+PBConstant.MEDIA_PHOTO_EXT_JPG;*/
//        
//        File getFile = null;
//		String createFileName ="";
//		createFileName =  source.hashCode()
//        						+PBConstant.MEDIA_PHOTO_EXT_JPG;
//		File saveDir = Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_PICTURES);
//	    if ( !saveDir.exists() || !saveDir.isDirectory() ) {
//			if(!saveDir.mkdir()){
//				return null;	
//			}
//		}
//        
//		getFile  = new File(saveDir, createFileName);
//        String destinationFile = "";
//        destinationFile = getFile.getAbsolutePath();
//        
//        BufferedInputStream bis = null;
//        BufferedOutputStream bos = null;
//        /*if (!TextUtils.isEmpty(source)) { 
//        	return null;
//        }*/
//        try {
//			bis = new BufferedInputStream(new FileInputStream(source));
//			bos = new BufferedOutputStream(new FileOutputStream(destinationFile, false));
//			byte[] buf = new byte[1024];
//			bis.read(buf);
//			do {
//		        bos.write(buf);
//		      } while(bis.read(buf) != -1);
//			
//		} catch (FileNotFoundException e1) {
//			isSaveImgSuccess = false;
//		} catch (IOException e) {
//			isSaveImgSuccess = false;
//		} finally {
//		      try {
//
//		          if (bis != null) bis.close();
//		          if (bos != null) bos.close();
//
//		    	  Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//		    	  File f = new File(destinationFile);
//		    	  Uri contentUri = Uri.fromFile(f);
//		    	  mediaScanIntent.setData(contentUri);
//		    	  context.sendBroadcast(mediaScanIntent);
//		    	                
//		        } catch (IOException e) {
//		        	isSaveImgSuccess = false;
//		        }
//	   }
//
//        if (!isSaveImgSuccess) {
//        	return null;
//        } else {
//        	return destinationFile;
//        }
//        
//
//    }
//	
//	
//    @SuppressWarnings("unused")
//	private static final Bitmap storeThumbnail(ContentResolver cr,
//            Bitmap source, long id, float width, float height, int kind) {
//        if (source == null) {
//            return null;
//        }
//        if (source.getWidth() <= 0 || source.getHeight() <= 0) {
//            return null;
//        }
//        // create the matrix to scale it
//        Matrix matrix = new Matrix();
//
//        float scaleX = width / source.getWidth();
//        float scaleY = height / source.getHeight();
//
//        matrix.setScale(scaleX, scaleY);
//
//        try {
//            Bitmap thumb = Bitmap.createBitmap(source, 0, 0,
//                                               source.getWidth(),
//                                               source.getHeight(), matrix,
//                                               true);
//    
//            ContentValues values = new ContentValues(4);
//            values.put(Images.Thumbnails.KIND, kind);
//            values.put(Images.Thumbnails.IMAGE_ID, (int) id);
//            values.put(Images.Thumbnails.HEIGHT, thumb.getHeight());
//            values.put(Images.Thumbnails.WIDTH, thumb.getWidth());
//    
//            Uri url = cr.insert(Images.Thumbnails.EXTERNAL_CONTENT_URI, values);
//    
//            try {
//                OutputStream thumbOut = cr.openOutputStream(url);
//    
//                thumb.compress(Bitmap.CompressFormat.JPEG, 100, thumbOut);
//                thumbOut.close();
//                return thumb;
//            }
//            catch (FileNotFoundException ex) {
//                return null;
//            }
//            catch (IOException ex) {
//                return null;
//            }
//        } catch (OutOfMemoryError oom) {
//            Log.e(PBConstant.TAG, ">>> OOM when store thumbnail!");
//            return null;
//        }
//    }
//
//    /**
//     * Save bitmap into camera roll
//     * 
//     * @param context
//     * @param fileSrc image file source path. 
//     * @param videoDuration 
//     */
//    @SuppressWarnings({ "unused", "deprecation" })
//	public static void saveInCameraRoll(Context context, String fileSrc,
//            int mediaType, long videoDuration) {
//    	
//    	//final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
//
//    	
//        // if(bmSrc == null) return;
//        if (TextUtils.isEmpty(fileSrc)) {
//            return;
//        }
//
//        String OUR_TITLE = PBGeneralUtils.class.getName();
//        if(context != null){
//            OUR_TITLE = context.getResources().getString(
//                    R.string.pb_app_name);
//        }
//        if (mediaType == PBDatabaseDefinition.MEDIA_PHOTO) {
//        	if(/*!isKitKat*/false) {
//                // Save the image. This also saves a micro and mini thumbnail
//                String sUri = insertImage(
//                        context.getContentResolver(), fileSrc, OUR_TITLE, "");
//                // 20120229 added by NhatVT check null before using <S>
//                if (TextUtils.isEmpty(sUri)) {
//                    return;
//                }
//                try {
//                // 20120229 added by NhatVT check null before using <E>
//                // Now update the mini Thumbnail record with the image's ID
//                long id = ContentUris.parseId(Uri.parse(sUri));
//                // Strip off the Id num
//                Uri thumbUri = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI; // Thumbnail provider
//        
//                // Query for the mini thumbnail for the image just saved. There should be just 1.
//                // See the comments on showGallery() for an explanation of managedQuery()
//                String[] projection = {
//                        MediaStore.Images.ImageColumns._ID, // The columns we want
//                        MediaStore.Images.Thumbnails.IMAGE_ID,
//                        MediaStore.Images.Thumbnails.KIND };
//                String selection = MediaStore.Images.Thumbnails.KIND + " = "
//                        + MediaStore.Images.Thumbnails.MINI_KIND + " AND "
//                        + MediaStore.Images.Thumbnails.IMAGE_ID + " = " + id;
//        
//                Cursor c = ((Activity) context).managedQuery(thumbUri, projection,
//                        selection, null, null); // Should return just 1
//        
//                if(c != null && c.moveToFirst()){
//                    // Log.i(TAG, "Save Cursor = " + c.getCount());
//                    try{
//                        int thumbId = c.getInt(c
//                                .getColumnIndexOrThrow(MediaStore.Images.ImageColumns._ID)); // Thumbnail ID
//            
//                        // Update the image's entry with the Thumbnail's ID
//                        ContentResolver cr = context.getContentResolver();
//                        ContentValues values = new ContentValues();
//                        values.put(MediaStore.Images.ImageColumns.PICASA_ID, thumbId);
//                        Log.d("[Check for Kindle Version]Photo Save Date :", ""+String.valueOf(System.currentTimeMillis()));
//                        // For Kindle
//                        values.put(MediaStore.Images.ImageColumns.DATE_TAKEN, String.valueOf(System.currentTimeMillis()));
//                        // ORIGINAL
//                        //values.put(MediaStore.Images.ImageColumns.DATE_ADDED, String.valueOf(System.currentTimeMillis()));
//                        //values.put(MediaStore.Images.ImageColumns.DATE_MODIFIED, String.valueOf(System.currentTimeMillis()));
//                        cr.update(Uri.parse(sUri), values, "", null);
//                        
//                        // TODO sendBroadcast for content resolver atik
//      		    	  Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//    		    	  //mediaScanIntent.setData(Uri.parse(sUri));
//    		    	  context.sendBroadcast(mediaScanIntent);
//                        
//                    }catch (CursorIndexOutOfBoundsException e) {
//                        Log.e(PBConstant.TAG, "faile to create thumb when save sdcard" + e.toString());
//                    }catch (Exception e) {
//                        Log.e(PBConstant.TAG, "faile to create thumb when save sdcard" + e.toString());
//                    }
//                }
//                } catch (UnsupportedOperationException e) {
//                    Log.e(PBConstant.TAG, "faile to create thumb when save sdcard" + e.toString());
//                }
//
//        	} else {
//        		String sUri = insertImageKitkat(
//                        context.getContentResolver(), fileSrc, OUR_TITLE, "",context);
//                if (TextUtils.isEmpty(sUri)) {
//                    return;
//                }
//        	}
//        } else { // TODO process saving video info to media store
//        	
//          	if(/*!isKitKat*/false) { 
//                // Save the name and description of a video in a ContentValues map.
//                String sUri = insertVideo(
//                        context.getContentResolver(), fileSrc, OUR_TITLE, "", videoDuration);
//                if (TextUtils.isEmpty(sUri)) {
//                    return;
//                }
//                try{
//                    // update video date time 
//                    ContentResolver cr = context.getContentResolver();
//                    ContentValues values = new ContentValues();
//                    values.put(MediaStore.Images.ImageColumns.DATE_ADDED, String.valueOf(System.currentTimeMillis()));
//                    values.put(MediaStore.Images.ImageColumns.DATE_MODIFIED, String.valueOf(System.currentTimeMillis()));
//                    cr.update(Uri.parse(sUri), values, "", null);
//                } catch (NullPointerException e) {
//                    // check null when execute update command
//                    Log.e(PBConstant.TAG, "faile when update video info [update command] with error: " + e.toString());
//                } catch (Exception e) {
//                    Log.e(PBConstant.TAG, "faile when update video info" + e.toString());
//                }
//          	}  else {
//            	String sUri = insertVideoKitkat(
//                        context.getContentResolver(), fileSrc, OUR_TITLE, "", videoDuration,context);
//                if (TextUtils.isEmpty(sUri)) {
//                    return;
//                }
//          	}      	
//        }
//    }
//
//    /**
//     * Save bitmap into camera roll
//     * 
//     * @param context
//     * @param source Bitmap. 
//     */
//    @SuppressWarnings("deprecation")
//	public static void saveInCameraRoll(Context context, Bitmap bmp) {
//        // if(bmSrc == null) return;
//        if (bmp == null) {
//            return;
//        }
//
//        String OUR_TITLE = PBGeneralUtils.class.getName();
//        if(context != null){
//            OUR_TITLE = context.getResources().getString(
//                    R.string.pb_app_name);
//        }
//
//        // Save the image. This also saves a micro and mini thumbnail
//        String sUri = insertImage(
//                context.getContentResolver(), bmp, OUR_TITLE, "");
//        // 20120229 added by NhatVT check null before using <S>
//        if (TextUtils.isEmpty(sUri)) {
//            return;
//        }
//        try {
//        // 20120229 added by NhatVT check null before using <E>
//        // Now update the mini Thumbnail record with the image's ID
//        long id = ContentUris.parseId(Uri.parse(sUri));
//        // Strip off the Id num
//        Uri thumbUri = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI; // Thumbnail provider
//
//        // Query for the mini thumbnail for the image just saved. There should be just 1.
//        // See the comments on showGallery() for an explanation of managedQuery()
//        String[] projection = {
//                MediaStore.Images.ImageColumns._ID, // The columns we want
//                MediaStore.Images.Thumbnails.IMAGE_ID,
//                MediaStore.Images.Thumbnails.KIND };
//        String selection = MediaStore.Images.Thumbnails.KIND + " = "
//                + MediaStore.Images.Thumbnails.MINI_KIND + " AND "
//                + MediaStore.Images.Thumbnails.IMAGE_ID + " = " + id;
//
//        Cursor c = ((Activity) context).managedQuery(thumbUri, projection,
//                selection, null, null); // Should return just 1
//
//        if(c != null && c.moveToFirst()){
//            // Log.i(TAG, "Save Cursor = " + c.getCount());
//            try{
//                int thumbId = c.getInt(c
//                        .getColumnIndexOrThrow(MediaStore.Images.ImageColumns._ID)); // Thumbnail ID
//    
//                // Update the image's entry with the Thumbnail's ID
//                ContentResolver cr = context.getContentResolver();
//                ContentValues values = new ContentValues();
//                values.put(MediaStore.Images.ImageColumns.PICASA_ID, thumbId);
//                values.put(MediaStore.Images.ImageColumns.DATE_ADDED, String.valueOf(System.currentTimeMillis()));
//                values.put(MediaStore.Images.ImageColumns.DATE_MODIFIED, String.valueOf(System.currentTimeMillis()));
//                cr.update(Uri.parse(sUri), values, "", null);
//            }catch (CursorIndexOutOfBoundsException e) {
//                Log.e(PBConstant.TAG, "faile to create thumb when save sdcard" + e.toString());
//            }catch (Exception e) {
//                Log.e(PBConstant.TAG, "faile to create thumb when save sdcard" + e.toString());
//            }
//        }
//        } catch (UnsupportedOperationException e) {
//            Log.e(PBConstant.TAG, "faile to create thumb when save sdcard" + e.toString());
//        }
//       
//    }
//    
//    /**
//     * Delete cache file
//     * 
//     * @param path Cache file's url.
//     */
//    public static void deleteCacheFile(String path) {
//        try {
//            String filePath = getPathFromCacheFolder(path);
//            
//            if (path.contains("/video") && !path.contains("?width")) {
//                // delete video thumb
//                File file = new File(filePath + PBConstant.VIDEO_THUMB_STR);
//                if (file.exists()) {
//                    file.delete();
//                }
//            }
//            File file = new File(filePath);
//            if (file.exists()) {
//                file.delete();
//            }
//            // 20120421 mod by NhatVT, remove video file <E>
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
//    
//
//    /**
//     * Clean unnecessary files from 
//     * photobag application
//     * @author atikur
//     */
//    public static void cleanUnnecessaryDataFromPhotobag() {
//        try {
//        	String[] allFilesinPhotobag;
//        	String filePath = getCacheFolderPath(true);
//        	if(!filePath.isEmpty()) {
//        		File file = new File(filePath);
//        		allFilesinPhotobag = file.list(); 
//        		for (int i=0; i<allFilesinPhotobag.length; i++) {  
//        	         File myFile = new File(file, allFilesinPhotobag[i]);   
//        	         myFile.delete();  
//        	     } 
//        	} else {
//        		System.out.println("Could not get path");
//        	}
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
//
//    public static boolean checkExistFile(String path) {
//        try {
//            File file = new File(path);
//            return file.exists();
//        }
//        catch(Exception ex){ex.printStackTrace();}
//        return false;
//    }
//    
//    //20120613 Bac formatHoneyCountValue
//    public static String formatHoneyCountValue(int honeyCount) {
//        if(honeyCount < 10){
//        	return "0" + honeyCount;
//        }
//    	return "" + honeyCount;
//    }
//    
//    public static void openAcornWebview(Context context) {
//    	try {  
//    		Intent intent = new Intent(context,
//    				PBWebViewActivity.class);
//    		intent.putExtra(PBAPIContant.PB_SETTING_EXTRA_URL, 
//    				context.getString(R.string.pb_setting_url_faq_donguri_info));
//    		intent.putExtra(PBAPIContant.PB_SETTING_EXTRA_TITLE, 
//    				context.getString(R.string.pb_faq_donguri_hoshii));
//    		((Activity) context).startActivityForResult(intent, 
//    				PBConstant.REQUEST_VIEW_WEB_PAGE);
//    	} catch (Exception e) {}
//    }
//    
//    private static float getDensity(Context context) {
//        if (density > 0.0f) return density;
//        density = context.getResources().getDisplayMetrics().density;
//        return density;
//    }
//    
//    public static int getActualSize(Context context, int dp) {
//        return (int) (0.5f + dp * getDensity(context));
//    }
//    
//    /**
//     * Collection of all files stored in cache folder
//     * 
//     */
//    public static ArrayList<String> getAllCacheFileFromStorage() {
//    	
//    	
//    	ArrayList<String> listOfFile = new ArrayList<String>();
//    	
//       	String externalPath = Environment.getExternalStorageDirectory().getAbsolutePath() 
//                + "/" + PBConstant.CACHE_FOLDER_NAME;
//       	String internalPath = PBApplication.getBaseApplicationContext()
//				.getDir(PBConstant.CACHE_FOLDER_NAME, Context.MODE_PRIVATE).getAbsolutePath();
//       	
//        if(!(externalPath.isEmpty()) || externalPath != null) {
//            File fileExternal = new File(externalPath); 
//            File externalFileList[] = fileExternal.listFiles();
//            Log.d("Files", "Size: "+ externalFileList.length);
//            for (int i=0; i < externalFileList.length; i++)
//            {
//            	listOfFile.add(externalFileList[i].toString());
//                Log.d("Files", "FileName:" + externalFileList[i].getName());
//            }
//            
//        }
//
//        if(!(internalPath.isEmpty()) || internalPath != null) {
//            File fileInternal = new File(internalPath); 
//            File internalFileList[] = fileInternal.listFiles();
//            Log.d("Files", "Size: "+ internalFileList.length);
//            for (int i=0; i < internalFileList.length; i++)
//            {
//            	listOfFile.add(internalFileList[i].toString());
//                Log.d("Files", "FileName:" + internalFileList[i].getName());
//            }
//        }
//		return listOfFile;
//
//    }
//    
//    /**
//     * Check collection ID or password exists in Inbox History
//     * @param collection id
//     * @author atikur
//     *//*
//    public static boolean isPasswordExistsInHistoryInbox(String collection_ID) {
//    	
//	    boolean existAikotobaInInbox = false;
//	    String collectionID = null;
//		PBDatabaseManager dbMgr = PBDatabaseManager
//				.getInstance(PBApplication.getBaseApplicationContext());
//		Cursor cursor = dbMgr.getHistoriesCursor(PBDatabaseDefinition.HISTORY_INBOX);
//	    if (cursor.moveToFirst()){
//			   while(!cursor.isAfterLast()){
//				  collectionID = cursor.getString(cursor.getColumnIndex(
//						  PBDatabaseDefinition.HistoryData.C_COLECTION_ID));
//			      if(collection_ID.equalsIgnoreCase(collectionID)) {
//			    	  System.out.println("Atik call password exists in Inbox");
//			    	  existAikotobaInInbox  = true; 
//					  if (cursor != null && !cursor.isClosed())
//						  cursor.close();
//
//			    	break;
//			      } 
//			      cursor.moveToNext();
//			   }
//			}
//		 if (cursor != null && !cursor.isClosed())
//			  cursor.close();
//		 
//    	return existAikotobaInInbox;
//    }
//    
//    *//**
//     * Check collection ID or password exists in Sent History
//     * @param collection id
//     * @author atikur
//     *//*
//    public static boolean isPasswordExistsInHistorySent(String collection_ID) {
//    	
//	    boolean existAikotobaInInbox = false;
//	    String collectionID = null;
//		PBDatabaseManager dbMgr = PBDatabaseManager
//				.getInstance(PBApplication.getBaseApplicationContext());
//		//dbMgr.i
//		Cursor cursor = dbMgr.getHistoriesCursor(PBDatabaseDefinition.HISTORY_SENT);
//	    if (cursor.moveToFirst()){
//			   while(!cursor.isAfterLast()){
//				  collectionID = cursor.getString(cursor.getColumnIndex(
//						  PBDatabaseDefinition.HistoryData.C_COLECTION_ID));
//			      if(collection_ID.equalsIgnoreCase(collectionID)) {
//			    	  System.out.println("Atik call password exists in Inbox");
//			    	  existAikotobaInInbox  = true;
//					  if (cursor != null && !cursor.isClosed())
//						  cursor.close();
//			    	  break;
//			      } 
//			      cursor.moveToNext();
//			   }
//			}
//		 if (cursor != null && !cursor.isClosed())
//			  cursor.close();
//		    
//    	 return existAikotobaInInbox;
//    }*/
//    
//    
//    /**
//	 * get real path of file store local cache on phone
//	 * <p>If the file url contains "can_save=0", it will return an 
//	 * internal path that under the application's folder, else it 
//	 * will return the external path in sdcard.</p>
//	 *  @param photoUrl
//	 *  @return file's path.
//	 */
//	public static String getPathGiftExchangeHistoryDataFolderPath(){
//	
//	    File dir = new File (getLocalGiftExchangeHistoryDataFolderPath(true));
//	    if(!dir.exists()){
//	        dir.mkdirs();
//	    }
//	    
//	    return dir.getAbsolutePath() + "/" + "gift_history_data.json";        
//	}
//	
//	/**
//	 * get real path of file store local cache on phone
//	 * <p>If the file url contains "can_save=0", it will return an 
//	 * internal path that under the application's folder, else it 
//	 * will return the external path in sdcard.</p>
//	 *  @param photoUrl
//	 *  @return file's path.
//	 */
//	public static String getPathGiftExchangeHistoryDataFolderPath1(){
//	
//	    File dir = new File (getLocalGiftExchangeHistoryDataFolderPath(true));
//	    if(!dir.exists()){
//	        dir.mkdirs();
//	    }
//	    
//	    return dir.getAbsolutePath() + "/" + "gift_exchange_history_data.html";        
//	}
//	
//	
//	
//	/**
//	 * Get Cache Folder Path, which stored the temp file of media.<br>
//	 * @param isExternal is external path.
//	 * @return <code>"mnt/sdcard/.photobag_cache"</code> if isExternal is true,<br> 
//	 * else <code>"data/data/com.kayac.photobag/app_.photobag_cache"</code>
//	 * */
//	public static String getLocalGiftExchangeHistoryDataFolderPath(boolean isExternal) {
//		if (isExternal) {
//			return Environment.getExternalStorageDirectory().getAbsolutePath() 
//	                + "/" + PBConstant.LOCAL_GIFT_EXCHANGE_HISTORY_DATA_FOLDER_NAME;
//		} else {
//			return PBApplication.getBaseApplicationContext()
//					.getDir(PBConstant.LOCAL_GIFT_EXCHANGE_HISTORY_DATA_FOLDER_NAME, Context.MODE_PRIVATE).getAbsolutePath();
//		}
//	}
//}
