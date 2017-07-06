///*package com.kobayashi.photobox.utils;
//
//import java.io.File;
//
//import android.media.MediaMetadataRetriever;
//import android.text.TextUtils;
//import android.util.Log;
//
//import com.kobayashi.photobox.application.PBConstant;
//
//public class PBVideoUtils {
//   
//    
//	private String cmd;
//	
//    public PBVideoUtils() {
//    	
//    }
//    
//   
//    public String compressVideo(String fileSourcePath) {
//
//        if (TextUtils.isEmpty(fileSourcePath)) {
//            return fileSourcePath;
//        }       
//        
//        String tempFileName;
//        try {
//            
//            String tempFullFileName = fileSourcePath.substring(0,
//                    fileSourcePath.length() - 4);
//            Log.d("tmp file name ", tempFullFileName);
//            // check cache directory on sdcard first;
//            File dir = new File(PBGeneralUtils.getCacheFolderPath());
//            if (!dir.exists()) {
//                dir.mkdirs();
//            }
//            
//            int hashName = tempFullFileName.hashCode();    
//            tempFileName = dir.getAbsolutePath() + "/" + hashName + PBConstant.MEDIA_VIDEO_EXT_MP4;
//            
//            int[] res = getVideoResulation(fileSourcePath);
//            Log.d("res[0] ",""+ res[0]);
//            Log.d("res[1] ", ""+res[1]);
//            int width;
//            int heigth; 
//            if (res[0] >  res[1]) {
//                width = 568;
//                heigth = 320;
//            } else {
//            	  width = 320;
//                  heigth = 568;
//            }
//            
////            cmd = "-i "+fileSourcePath+" -vcodec "
////              		+ "libx264 -b 150k -c:a copy "+tempFileName;
//            
//            cmd = "-i "+fileSourcePath+" -s "+width+"x"+heigth+" -r 25 -vcodec "
//            	           		+ "libx264 -b 150k -c:a copy "+tempFileName;
//            
////            cmd = "-y -i "+fileSourcePath+" -strict 2 -vcodec libx264"
////           		+ " -acodec aac -ar 44100 -ac 2 -s "+width+"x"+heigth+" "+tempFileName;
////            		 Log.e(PBConstant.TAG, "cmd: " +cmd);
//        } catch (RuntimeException e) {
//            return "";
//        } catch (Exception e) {
//            return "";
//        }
//
//        return tempFileName;
//
//    }
//    
//    public String getCommand(){
//    	
//    	return cmd;
//    }
//    
//    public long getDuration(String filesource) {
//    	
//    	MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
//    	metaRetriever.setDataSource(filesource);
//    	int duration = Integer.parseInt(metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
//        return duration;
//    }
//    
//    private int[] getVideoResulation(String filesource){
//    	
//    	MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
//    	metaRetriever.setDataSource(filesource);
//    	int[] res = new int[2];  
//    	res[0] = Integer.parseInt(metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
//    	res[1] = Integer.parseInt(metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
//    
//    	Log.d("res ", res[0]+" "+res[1]);
//    	return res;
//    }
//    
//}
//*/
//
//package com.smartmux.videoeditor.utils;
//
//import java.io.File;
//
//import android.media.MediaMetadataRetriever;
//import android.text.TextUtils;
//import android.util.Log;
//
//
//public class PBVideoUtils {
//   
//    
//	private String cmd;
//	
//    public PBVideoUtils() {
//    	
//    }
//    
//   
//    public String compressVideo(String fileSourcePath) {
//
//        if (TextUtils.isEmpty(fileSourcePath)) {
//            return fileSourcePath;
//        }       
//        
//        String tempFileName;
//        try {
//            
//            String tempFullFileName = fileSourcePath.substring(0,
//                    fileSourcePath.length() - 4);
//            Log.d("tmp file name ", tempFullFileName);
//            // check cache directory on sdcard first;
//            File dir = new File(PBGeneralUtils.getCacheFolderPath());
//            if (!dir.exists()) {
//                dir.mkdirs();
//            }
//            
//            int hashName = tempFullFileName.hashCode();    
//            tempFileName = dir.getAbsolutePath() + "/" + hashName + PBConstant.MEDIA_VIDEO_EXT_MP4;
//            
//            int[] res = getVideoResulation(fileSourcePath);
//            Log.d("res[0] ",""+ res[0]);
//            Log.d("res[1] ", ""+res[1]);
//            int width;
//            int heigth; 
//            if (res[0] >  res[1]) {
//                width = 568;
//                heigth = 320;
//            } else {
//            	  width = 320;
//                  heigth = 568;
//            }
//            
////            cmd = "-i "+fileSourcePath+" -vcodec "
////              		+ "libx264 -b 150k -c:a copy "+tempFileName;
//            
//            cmd = "-i "+fileSourcePath+" -s "+width+"x"+heigth+" -r 30 -vcodec "
//            	           		+ "libx264 -b 300k -c:a copy "+tempFileName;
//            
////            cmd = "-y -i "+fileSourcePath+" -strict 2 -vcodec libx264"
////           		+ " -acodec aac -ar 44100 -ac 2 -s "+width+"x"+heigth+" "+tempFileName;
////            		 Log.e(PBConstant.TAG, "cmd: " +cmd);
//        } catch (RuntimeException e) {
//            return "";
//        } catch (Exception e) {
//            return "";
//        }
//
//        return tempFileName;
//
//    }
//    
//    public String getCommand(){
//    	
//    	return cmd;
//    }
//    
//    public long getDuration(String filesource) {
//    	
//    	MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
//    	metaRetriever.setDataSource(filesource);
//    	int duration = Integer.parseInt(metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
//        return duration;
//    }
//    
//    private int[] getVideoResulation(String filesource){
//    	
//    	MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
//    	metaRetriever.setDataSource(filesource);
//    	int[] res = new int[2];  
//    	res[0] = Integer.parseInt(metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
//    	res[1] = Integer.parseInt(metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
//    
//    	Log.d("res ", res[0]+" "+res[1]);
//    	return res;
//    }
//    
//}
