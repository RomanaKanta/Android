package com.aircast.photobag.utils;

import java.io.File;

import android.media.MediaMetadataRetriever;
import android.text.TextUtils;
import android.util.Log;

import com.aircast.photobag.application.PBConstant;
import com.media.ffmpeg.FFMpeg;
import com.media.ffmpeg.FFMpegConfig_MP4;

public class PBVideoUtils {
	private String cmd;

    private /*static*/ FFMpeg ffmpeg;
    public PBVideoUtils() {

    }

    public String compressVideo(String fileSourcePath) { /// photobox



        if (TextUtils.isEmpty(fileSourcePath)) {
            return fileSourcePath;
        }       
        
        String tempFileName;
        try {
            
            String tempFullFileName = fileSourcePath.substring(0,
                    fileSourcePath.length() - 4);
            // check cache directory on sdcard first;
            File dir = new File(PBGeneralUtils.getCacheFolderPath());
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            int hashName = tempFullFileName.hashCode();    
            tempFileName = dir.getAbsolutePath() + "/" + hashName + PBConstant.MEDIA_VIDEO_EXT_MP4;

            int[] res = getVideoResulation(fileSourcePath);
            Log.d("TinhNH1", "Video Resolution: " + res[0] + res[1]);
            int width;
            int heigth; 
            
            if(res[0] == res[1]){
            	width = res[0];
                heigth = res[0];
            	
            }else if (res[0] > res[1]) {
            	
            	width = 568;
                heigth = 320;
            } else {
            	 width = 320;
                 heigth = 568;
            }
            
           
            
            cmd = "-i "+fileSourcePath+" -s "+width+"x"+heigth+" -r 30 -vcodec "
	           		+ "libx264 -b 300k -c:a copy "+tempFileName;
            
        } catch (RuntimeException e) {
            return "";
        } catch (Exception e) {
            return "";
        }

        return tempFileName;

    }
 public String getCommand(){
    	
    	return cmd;
    }
 
 public long getDuration(String filePath) {
 	
 	MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
 	metaRetriever.setDataSource(filePath);
 	int duration = Integer.parseInt(metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
     return duration;
 }
 
 private int[] getVideoResulation(String filePath){
 	
 	MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
 	metaRetriever.setDataSource(filePath);
 	int[] res = new int[2];  
 	res[0] = Integer.parseInt(metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
 	res[1] = Integer.parseInt(metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
 
 	Log.d("res ", res[0]+" "+res[1]);
 	return res;
 }
 
    public void cancelCompression()
    {
       if(ffmpeg != null) {
           ffmpeg.release();
           ffmpeg = null;
       }
    }

    
    public void clearData() {
        if (ffmpeg != null) {
            ffmpeg.clear();
            ffmpeg = null;
        }

    }

    
}
