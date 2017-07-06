package com.media.ffmpeg;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.text.TextUtils;
import android.util.Log;

public class FFMpeg {

    public static final String[] EXTENSIONS = new String[] { ".mp4", ".3gp" };

    private static boolean sLoaded = false;
    private Thread mThread;
    private IFFMpegListener mListener;

    private FFMpegFile mInputFile;
    private FFMpegFile mOutputFile;
    private boolean mConverting;
    private long duration = 0;

    public FFMpeg() throws FFMpegException {
        if (!loadLibs()) {
            throw new FFMpegException(FFMpegException.LEVEL_FATAL,
                    "Couldn't load native libs");
        }
        native_avcodec_register_all();
        native_av_register_all();
        mConverting = false;
    }

    /**
     * loads all native libraries
     * 
     * @return true if all libraries was loaded, otherwise return false
     */
    private static boolean loadLibs() {
        if (sLoaded) {
            return true;
        }
        boolean err = false;
        try {
            System.loadLibrary("ffmpeg");
            System.loadLibrary("ffmpeg-util-jni");
            System.loadLibrary("ffmpeg_jni");
        } catch (UnsatisfiedLinkError e) {
            // fatal error, we can't load some our libs
            Log.d("FFMpeg", "Couldn't load lib: - " + e.getMessage());
            err = true;
        }
        if (!err) {
            sLoaded = true;
        }
        return sLoaded;
    }

    public FFMpegUtils getUtils() {
        return new FFMpegUtils();
    }

    public boolean isConverting() {
        return mConverting;
    }

    public void setListener(IFFMpegListener listener) {
        mListener = listener;
    }

    public FFMpegFile getOutputFile() {
        return mOutputFile;
    }

    public FFMpegFile getInputFile() {
        return mInputFile;
    }

    public void init(String inputFile, String outputFile)
            throws RuntimeException, IOException {
        native_av_init();

        mInputFile = setInputFile(inputFile);
        mOutputFile = setOutputFile(outputFile);
    }

    // @TinhNH 7-5-2012 START
    // add check Video H/W

    public void setConfig_MP4_hor(FFMpegConfig_MP4 config, String videoBitrate) {
//        setFrameSize(config.resolution[1], config.resolution[0]);
//        setFrameRate(config.frameRate);
//        setVideoCodec(config.codec);
//        setBitrate(config.bitrate);
        setFrameSize(config.resolution[1], config.resolution[0]);
        setAudioChannels(config.audioChannels);
        setAudioRate(config.audioRate);
        setFrameRate(config.frameRate);
        setVideoCodec(config.codec);
        setFrameAspectRatio(config.ratio[0], config.ratio[1]);
        //setBitrate(config.bitrate); // comment out by atik
        setBitrate(videoBitrate);
        
        native_av_parse_options(new String[] { "ffmpeg",
                mOutputFile.getFile().getAbsolutePath() });
    }

    public void setConfig_MP4_ver(FFMpegConfig_MP4 config, String videoBitrate) {
//        setFrameSize(config.resolution[0], config.resolution[1]);
//        setFrameRate(config.frameRate);
//        setVideoCodec(config.codec);
//        setBitrate(config.bitrate);
        setFrameSize(config.resolution[0], config.resolution[1]);
        setAudioChannels(config.audioChannels);
        setAudioRate(config.audioRate);
        setFrameRate(config.frameRate);
        setVideoCodec(config.codec);
        setFrameAspectRatio(config.ratio[0], config.ratio[1]);
        //setBitrate(config.bitrate); // comment out by atik
        setBitrate(videoBitrate);
        
        native_av_parse_options(new String[] { "ffmpeg",
                mOutputFile.getFile().getAbsolutePath() });

    }

    // @TinhNH 7-5-2012 END

    public void setBitrate(String bitrate) {
        native_av_setBitrate("b", bitrate);
    }

    public void setFrameAspectRatio(int x, int y) {
        native_av_setFrameAspectRatio(x, y);
    }

    public void setVideoCodec(String codec) {
        native_av_setVideoCodec(codec);
    }

    public void setAudioRate(int rate) {
        native_av_setAudioRate(rate);
    }

    public void setAudioChannels(int channels) {
        native_av_setAudioChannels(channels);
    }

    public void setFrameRate(int rate) {
        native_av_setFrameRate(rate);
    }

    public void setFrameSize(int width, int height) {
        native_av_setFrameSize(width, height);
    }

    public FFMpegFile setInputFile(String filePath) throws IOException {
        File f = new File(filePath);
        if (!f.exists()) {
            Log.d("FFMpegPlayerActivity", "File error");
            throw new FileNotFoundException("File: " + filePath
                    + " doesn't exist");
        }
        FFMpegAVFormatContext c = null;
        try {
            c = native_av_setInputFile(filePath);

        } catch (Exception e) {
            Log.d("FFMpegPlayerActivity", "error");
        }
        //20120507 START : add getduration of Video
        if(c != null)
        {
            duration = c.getDurationInMiliseconds();
        }
        // Log.d("FFMpegPlayerActivity", duration +"  duration");
        //20120507 END.
        return new FFMpegFile(f, c);
    }

    public FFMpegFile setOutputFile(String filePath)
            throws FileNotFoundException {

        File f = new File(filePath);
        if (f.exists()) {
            f.delete();
        }
        Log.d("FFMpegPlayerActivity ", f.getAbsolutePath());
        // FFMpegAVFormatContext c = native_av_setOutputFile(filePath);
        return new FFMpegFile(f, null);
    }

    public void convert() throws RuntimeException {
        mConverting = true;
        if (mListener != null) {
            mListener.onConversionStarted();
        }

        native_av_convert();
        mConverting = false;
        if (mListener != null) {
            mListener.onConversionCompleted();
        }
    }

    public void asd() throws RuntimeException {
        mThread = new Thread() {
            @Override
            public void run() {
                try {
                    convert();
                } catch (RuntimeException e) {
                    if (mListener != null) {
                        mListener.onError(e);
                    }
                }
            }
        };
        mThread.start();
    }

    public void waitOnEnd() throws InterruptedException {
        if (mThread == null) {
            return;
        }
        mThread.join();
    }
    
    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
    public void release() {
        native_av_release(1);
    }
    // clear enviroment
    public void clear() {
        native_av_clear(1);
    }

    @Override
    protected void finalize() throws Throwable {
        Log.d("FFMpeg", "finalizing ffmpeg main class");
        sLoaded = false;
    }

    public interface IFFMpegListener {
        public void onConversionProcessing(FFMpegReport report);

        public void onConversionStarted();

        public void onConversionCompleted();

        public void onError(Exception e);
    }

    private native void native_avcodec_register_all();

    private native void native_av_register_all();

    private native void native_av_init() throws RuntimeException;

    private native FFMpegAVFormatContext native_av_setInputFile(String filePath)
            throws IOException;

    private native FFMpegAVFormatContext native_av_setOutputFile(String filePath)
            throws IOException;

    private native int native_av_setBitrate(String opt, String arg);

    private native void native_av_newVideoStream(int pointer);

    /**
     * ar
     * 
     * @param rate
     */
    private native void native_av_setAudioRate(int rate);

    private native void native_av_setAudioChannels(int channels);

    private native void native_av_setVideoChannel(int channel);

    /**
     * r
     * 
     * @param rate
     * @throws RuntimeException
     */
    private native FFMpegAVRational native_av_setFrameRate(int rate)
            throws RuntimeException;

    /**
     * ration
     * 
     * @param x
     * @param y
     */
    private native void native_av_setFrameAspectRatio(int x, int y);

    /**
     * codec
     * 
     * @param codec
     */
    private native void native_av_setVideoCodec(String codec);

    /**
     * resolution
     * 
     * @param width
     * @param height
     */
    private native void native_av_setFrameSize(int width, int height);

    private native void native_av_parse_options(String[] args)
            throws RuntimeException;

    private native void native_av_convert() throws RuntimeException;

    private native int native_av_release(int code);
    private native int native_av_clear(int code);

    /**
     * callback called by native code to inform java about conversion
     * 
     * @param total_size
     * @param time
     * @param bitrate
     */
    private void onReport(double total_size, double time, double bitrate) {

        if (mListener != null) {
            FFMpegReport report = new FFMpegReport();
            report.total_size = total_size;
            report.time = time;
            report.bitrate = bitrate;
            mListener.onConversionProcessing(report);
        }
    }

    
    public static native void naInit(String _videoFileName);
    public static native int[] naGetVideoResolution();
    public static native void naClose();
    
    
    public int[] GetVideoResolution(String videoName){
        naInit(videoName);
        int[] re = naGetVideoResolution();
        naClose();
        return re;
    }
    
    /**
     * get duration from 1 file
     * */
    public long getDurationFromFile(String filePath) {
        long ret = 0;
        if (!TextUtils.isEmpty(filePath)) {
                native_av_init();
//                FFMpegFile mInputFile = setInputFile(filePath);
//                if (mInputFile != null) {
//                    ret = mInputFile.mContext.getDurationInMiliseconds();
//                    clear();
//                    mInputFile = null;
//                }
            FFMpegAVFormatContext c = null;
            try {
                c = native_av_setInputFile(filePath);

            } catch (Exception e) {
                Log.d("FFMpegPlayerActivity", "error");
            }
            if(c != null)
            {
                duration = c.getDurationInMiliseconds();
            }
                return duration;

        }
        return ret;
    }
    
}
