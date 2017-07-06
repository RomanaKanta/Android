package com.aircast.koukaibukuro.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;



/**
 * general utilities with method create dialog, checkign sdcard, covert string, etc
 * @author lent5
 */
public class GeneralUtils {
    // 20120418 add to support video function <S>
    private static StringBuilder sFormatBuilder = new StringBuilder();
    private static Formatter sFormatter = new Formatter(sFormatBuilder, Locale.getDefault());
    private static final Object[] sTimeArgs = new Object[5];
    private static volatile float density = -1;
    
    public static boolean checkFileSizeCanUpload(String filepath, long limitsize) {
        if (TextUtils.isEmpty(filepath)) {
            return false;
        }
        Log.i("mapp", "-- checking file: " + filepath);
        File f = new File(filepath);
        if (!f.exists() || f.length() == 0) {
            return false;
        }
        // int fileSize = (int) (f.length() / 1048576); // 1024*1024
        if (/*fileSize*/f.length() > limitsize)
            return false;
        
        return true;
    }
    
//    public static String makeTimeString(Context context, long secs) {
//        String durationformat = context
//                .getString(secs < 3600 ? R.string.durationformatshort
//                        : R.string.durationformatlong);
//
//        sFormatBuilder.setLength(0);
//
//        final Object[] timeArgs = sTimeArgs;
//        timeArgs[0] = secs / 3600;
//        timeArgs[1] = secs / 60;
//        timeArgs[2] = (secs / 60) % 60;
//        timeArgs[3] = secs;
//        timeArgs[4] = secs % 60;
//
//        return sFormatter.format(durationformat, timeArgs).toString();
//    }
//    // 20120418 add to support video function <E>
//    
//    /**
//     * Set photo as wallpaper
//     * @param filepath
//     */
//    public static boolean setPhotoAsWallpaper(Context context, Bitmap bitmap){  
//        if(bitmap == null) return false;
//
//        try {
//            WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
//            wallpaperManager.setBitmap(bitmap);
//        } catch (IOException e) {               
//            e.printStackTrace();
//            return false;
//        }
//
//        return true;
//    }
//
//    /**
//     * start intent share photo
//     * @param context the conetxt
//     * @param url photo's url
//     */
//    public static void photoViewerSharing(Context context, String url){
//    	
//    	List<Intent> targetShareIntents=new ArrayList<Intent>();
//        
//    	Intent shareIntent=new Intent();
//        shareIntent.setAction(Intent.ACTION_SEND);
//        shareIntent.setType("image/png");
//        
//        List<ResolveInfo> resInfos= context.getPackageManager().queryIntentActivities(shareIntent, 0);
//        if(!resInfos.isEmpty()){
//            for(ResolveInfo resInfo : resInfos){
//                String packageName=resInfo.activityInfo.packageName;
//                Log.i("Package Name", packageName);
//                if(!packageName.contains(PBConstant.PREF_NAME)){
//                    Intent intent=new Intent();
//                    intent.setComponent(new ComponentName(packageName, resInfo.activityInfo.name));
//                    intent.setAction(Intent.ACTION_SEND);
//                    File photo = new File(url);
//                    intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(photo));
//                    intent.setType("image/png");
//                    intent.setPackage(packageName);
//                    targetShareIntents.add(intent);
//                }
//            }
//            if(!targetShareIntents.isEmpty()){
//                Intent chooserIntent=Intent.createChooser(targetShareIntents.remove(0), context.getResources().getText(R.string.pb_app_name));
//                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetShareIntents.toArray(new Parcelable[]{}));
//                context.startActivity(chooserIntent);
//            }
//        }
//        
//        /*
//        final Intent shareIntent = new Intent(Intent.ACTION_SEND);
//        File photo = new File(url);
//        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(photo));
//        shareIntent.setType("image/png");
//
//        if(context == null) return;
//        context.startActivity(Intent.createChooser(shareIntent, 
//                context.getResources().getText(R.string.pb_app_name)));
//        */
//    }
//    
//    
//    /**
//     * start intent report photo
//     * @param context the conetxt
//     * @param url photo's url
//     */
//    public static void photoViewerReporting(Context context, String url,String password){
//    	
//    	List<Intent> targetReportIntents=new ArrayList<Intent>();
//        
//    	  final Intent reportIntent = new Intent(android.content.Intent.ACTION_SEND);
//         	
//             reportIntent.setType("image/*");
//        
//        List<ResolveInfo> resInfos= context.getPackageManager().queryIntentActivities(reportIntent, 0);
//        if(!resInfos.isEmpty()){
//            for(ResolveInfo resInfo : resInfos){
//                String packageName=resInfo.activityInfo.packageName;
//                Log.i("Package Name", packageName);
//                if(!packageName.contains(PBConstant.PREF_NAME)){
//                    Intent intent=new Intent();
//                    intent.setComponent(new ComponentName(packageName, resInfo.activityInfo.name));
//                    intent.setAction(Intent.ACTION_SEND);
//                    String[] recipients = new String[] {};
//                    intent.putExtra(android.content.Intent.EXTRA_EMAIL, recipients);
//                    intent.putExtra(android.content.Intent.EXTRA_SUBJECT,
//                 			context.getString(R.string.pb_app_name));
//                 	String sendMessage = "Password : " + password;
//                 	
//                 	sendMessage += "\n" + context.getString(R.string.pb_report_it_msg);
//                 	sendMessage += String.format(context.getString(
//                             R.string.pb_setting_contactweb_send_message,
//                             PBPreferenceUtils.getStringPref(PBApplication
//                                     .getBaseApplicationContext(), PBConstant.PREF_NAME,
//                                     PBConstant.PREF_NAME_UID, context.getString(R.string.pb_setting_contact_code))));
//                 	sendMessage +="";
//                     sendMessage += "\nMobile Type: " + Build.MODEL; 
//                     sendMessage += "\nOs: Android v" + Build.VERSION.RELEASE;
//                     sendMessage += "\nAPI lv:" + Build.VERSION.SDK_INT;
//                     try {
//         				sendMessage += "\nApp version:"
//         							+ context.getPackageManager().getPackageInfo(context.getPackageName(),0).versionName;
//         			} catch (NameNotFoundException e) {
//         				e.printStackTrace();
//         			}
//                     sendMessage += "\nLanguage :" + Locale.getDefault().getDisplayLanguage();
//                     
//                     String[] emailRecipients = new String[] { context.getString(R.string.pb_setting_contact_mail)};
//                     intent.putExtra(android.content.Intent.EXTRA_EMAIL,
//                             emailRecipients);
//                     intent.putExtra(android.content.Intent.EXTRA_TEXT, sendMessage);
//                    File photo = new File(url);
//                    intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(photo));
//                    intent.setType("image/png");
//                    intent.setPackage(packageName);
//                    targetReportIntents.add(intent);
//                }
//            }
//            if(!targetReportIntents.isEmpty()){
//                Intent chooserIntent=Intent.createChooser(targetReportIntents.remove(0), context.getResources().getText(R.string.pb_app_name));
//                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetReportIntents.toArray(new Parcelable[]{}));
//                context.startActivity(chooserIntent);
//            }
//        }
//        
//     
//    }


    /**
     * check sdcard is mount or not
     * @param context
     * @param showNotify if true show dialog notify if sdcard not mounted
     * @param buttonClick1
     * @return
     */
//    public static boolean checkSdcard(Context context, 
//            boolean showNotify, DialogInterface.OnClickListener buttonClick1) {
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
//                dialog.show();
//            }catch (Exception e) {
//                Log.w(PBConstant.TAG, e.getMessage());
//            }
//        }
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
//    public static boolean checkSdcard(Context context, 
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

    /**
     * get real path of file store local cache on phone
     * <p>If the file url contains "can_save=0", it will return an 
     * internal path that under the application's folder, else it 
     * will return the external path in sdcard.</p>
     *  @param photoUrl
     *  @return file's path.
     */
    public static String getPathFromCacheFolder(String photoUrl){
        if (TextUtils.isEmpty(photoUrl)) {
            return null;
        }
        
        File dir = new File ( Environment.getExternalStorageDirectory().getAbsolutePath() 
              + "/" + Constant.CACHE_FOLDER_NAME);
        if(!dir.exists()){
            dir.mkdirs();
        }
        return dir.getAbsolutePath() + "/" + photoUrl.hashCode();        
    }
    
    
    /**
     * get real path of file store local cache on phone
     * <p>If the file url contains "can_save=0", it will return an 
     * internal path that under the application's folder, else it 
     * will return the external path in sdcard.</p>
     *  @return file's path.
     */
    public static String getPathFromOpenBagCacheFolder(){

        
        File dir = new File ( Environment.getExternalStorageDirectory().getAbsolutePath() 
              + "/" + Constant.CACHE_FOLDER_FOR_SEARCH_DATA_NAME);
        
        
        if(!dir.exists()){
            dir.mkdirs();
        }
        
        System.out.println("Atik cache path is:"+dir.getAbsolutePath() + "/");
        return dir.getAbsolutePath() + "/";        
    }
    
    /**
     * Convert milisecond to Standard time (DateTime)
     * 
     * @param milisecond
     * @return
     */
    public static String convertMilisecondToDate(long milisecond) {
        SimpleDateFormat simpleDate = new SimpleDateFormat(
                "yyyy/MM/dd HH:mm:ss");
        Date date = new Date(milisecond);
        return simpleDate.format(date);
    }
}
