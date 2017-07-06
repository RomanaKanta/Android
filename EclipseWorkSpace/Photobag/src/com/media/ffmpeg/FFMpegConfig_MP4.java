package com.media.ffmpeg;

public class FFMpegConfig_MP4 {
    
    public static final String  CODEC_MPEG4 = "mpeg4";
    
    public static final String  BITRATE_HIGH = "3339000";
    public static final String  BITRATE_MEDIUM = "797000";
    public static final String  BITRATE_LOW = "128000";

    public int[]                resolution = new int[]{568, 320};
    public String               codec = CODEC_MPEG4;
    public String               bitrate = BITRATE_MEDIUM;
    public int                  frameRate = 30;
    
    public static final int[]  RATIO_3_2 = new int[]{3,2};
    public static final int[]  RATIO_4_3 = new int[]{4,3};
    
    public int[]    ratio = RATIO_4_3;
    public int      audioRate = 22050 ;
    public int      audioChannels = 2;
    
}
