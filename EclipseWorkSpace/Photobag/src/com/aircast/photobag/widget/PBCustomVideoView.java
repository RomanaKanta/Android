package com.aircast.photobag.widget;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import android.content.Context;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class PBCustomVideoView extends SurfaceView{

    protected static final String TAG = "CVideoView";
    private static final boolean ENABLE_LOG = false;
    
    public int mSurfaceWidth;
    public int mSurfaceHeight;
    public int mVideoHeight;
    public int mVideoWidth;
    
    // All the stuff we need for playing and showing a video
    private SurfaceHolder mSurfaceHolder = null;
    private MediaPlayer mMediaPlayer = null;
    private PBVideoControllerView mVideoController;

    // all possible internal states
    private static final int STATE_ERROR              = -1;
    private static final int STATE_IDLE               = 0;
    private static final int STATE_PREPARING          = 1;
    private static final int STATE_PREPARED           = 2;
    private static final int STATE_PLAYING            = 3;
    private static final int STATE_PAUSED             = 4;
    private static final int STATE_PLAYBACK_COMPLETED = 5;

    private int mCurrentState = STATE_IDLE;
    int mTargetState  = STATE_IDLE;
    int mSeekWhenPrepared; // recording the seek position while preparing
    private int pos = -1;
    boolean mIsRepeat = false;
    private boolean mIsInit = false;
    
    private boolean mIsVideoDataReady = false;
    public boolean mIsPlayInFullScreenMode = true;
    
    /** Specify path to video file*/
    private String filePath = "";
    private int mDuration = -1;
    private long lastTime = -1;
    private static final int DELAY_SEEK = 300;
    Context mContext;
    
    public PBCustomVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        initVideoView();
    }

    public PBCustomVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initVideoView();
    }

    public PBCustomVideoView(Context context) {
        super(context);
        this.mContext = context;
        initVideoView();
    }

    SurfaceHolder.Callback mSurfaceHolderCallback = new SurfaceHolder.Callback() {
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            Log.d(TAG,"surfaceChanged():width=" + width + ".Height="+height);
            mSurfaceWidth = width;
            mSurfaceHeight = height;
            
            Log.d(TAG, "surfaceChanged");
//            if((width > 0) && (height > 0))               
//                if (mMediaPlayer != null && mIsVideoDataReady && mVideoWidth > 0 && mVideoHeight > 0) {
//                    if (pos > 0) 
//                        startMediaPlayer();
//            }
            
            mSurfaceWidth = width;
            mSurfaceHeight = height;
            if (mMediaPlayer != null && (mCurrentState == STATE_PREPARED)
                    && mVideoWidth == width && mVideoHeight == height) {
                if (mSeekWhenPrepared != 0) {
                    mMediaPlayer.seekTo(mSeekWhenPrepared);
                    mSeekWhenPrepared = 0;
                }
                // startMediaPlayer(); // 20120509 disable autoplay function
            }
            
            
        }

        public void surfaceCreated(SurfaceHolder holder) {
//            //Toast.makeText(mContext, ">>>>surfaceCreated<<<<", 0).show();
//            mSurfaceHolder = holder;
//            // setFullScreen(true);
//            if (mMediaPlayer != null && mTargetState == STATE_PAUSED) {
//                mMediaPlayer.setDisplay(mSurfaceHolder);
//            } else {
//                openVideo();
//                // The video could not be played back if launch the player quickly
//                // play(filePath);
//                updateSeekBar();
//                if (mVideoController != null) {
//                    mVideoController.updateSeekBarInfo();
//                }
//            }
            Log.d(TAG, "surfaceCreated");
            mSurfaceHolder = holder;
            openVideo();
            updateSeekBar();
            if (mVideoController != null) {
                mVideoController.updateSeekBarInfo();
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            // Toast.makeText(mContext, ">>>>surfaceDestroyed<<<<", 0).show();
            Log.d(TAG, "surfaceDestroyed");
            
            pos = getCurrentPosition();
            if((mDuration - pos) <= 1000) 
                pos = -1;

            // after we return from this we can't use the surface any more
            if (mSurfaceHolder != null)
                mSurfaceHolder = null;
            release(true);
        }
    };
    
    private void initVideoView() {
        mVideoWidth = 0;
        mVideoHeight = 0;
        mIsPlayInFullScreenMode = true;
        getHolder().addCallback(mSurfaceHolderCallback);
        getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
        mCurrentState = STATE_IDLE;
        mTargetState  = STATE_IDLE;
    }
    
    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        requestLayout();
        invalidate();
    }
    
    public void setVideoPath(String path) {
        if (!TextUtils.isEmpty(path)) {    
            filePath = path;
            openVideo();
            requestLayout();
            invalidate();
        }
    }
    
    /** seek video playback to position */
    public void seekToPos(int msec) {
        if (System.currentTimeMillis() - lastTime < DELAY_SEEK)
            return;

        if ((mMediaPlayer != null) && (mIsInit))
            mMediaPlayer.seekTo(msec);
        lastTime = System.currentTimeMillis();
    }

    public void seekToPos(int msec, boolean scroll) {
        if ((mMediaPlayer != null) && (mIsInit))
            mMediaPlayer.seekTo(msec);
    }

    public void stopPlaybackAndRelease() {
        if (mMediaPlayer != null) {
            // mMediaPlayer.stop();
            stopMediaPlayer();
            
            mMediaPlayer.release();
            mMediaPlayer = null;
            mCurrentState = STATE_IDLE;
            mTargetState  = STATE_IDLE;
        }
    }
    
    /**
     * Starts or resumes playback. If playback had previously been paused,
     * playback will continue from where it was paused. If playback had been
     * stopped, or never started before, playback will start at the beginning.
     */
    public void resumePlayback() {
        if ((mMediaPlayer != null) && (mIsInit)
                && (mMediaPlayer.isPlaying() == false)) {
            startMediaPlayer();
            mCurrentState = STATE_PLAYING;
            mTargetState = STATE_PLAYING;
        }
    }
    
    public void keyBack() {
        if (mMediaPlayer != null){
            mMediaPlayer.seekTo(mMediaPlayer.getDuration());            
        }
    }

    public void pausePlayback() {
        if (isInPlaybackState()) {
            if (mMediaPlayer.isPlaying()) {
                // mMediaPlayer.pause();
                pauseMediaPlayer();
                mCurrentState = STATE_PAUSED;
            }
        }
        mTargetState = STATE_PAUSED;
    }
    
    public boolean isPlaying() {
        return isInPlaybackState() && mMediaPlayer.isPlaying();
    }
    
    public void setVideoSize(int width, int height){
        mVideoWidth = width;
        mVideoHeight = height;
    }

    public void setFullScreen(boolean full) {
        mIsPlayInFullScreenMode = full;
        requestLayout();
        invalidate();
    }
    
    public void start() {
        if (isInPlaybackState()) {
            startMediaPlayer();
            mCurrentState = STATE_PLAYING;
            Log.i(TAG,"[start] MediaPlayer::start() is called");            
        }
        mTargetState = STATE_PLAYING;
    }
    
    public int getCurrentPosition() {
        if (isInPlaybackState()) 
            return mMediaPlayer.getCurrentPosition();
        return 0;
    }
    
    private boolean isInPlaybackState() {
        return (mMediaPlayer != null && 
                mCurrentState != STATE_ERROR &&
                mCurrentState != STATE_IDLE &&
                mCurrentState != STATE_PREPARING);
    }
    
    /** Release the media player in any state */
    private void release(boolean cleartargetstate) {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mCurrentState = STATE_IDLE;
            if (cleartargetstate) {
                mTargetState  = STATE_IDLE;
            }
        }
    }
    
    /** set repeat mode a file */
    public void setLoop(boolean mode) {
        mIsRepeat = mode;
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        /*if (isInPlaybackState() && mVideoController != null) {
            toggleMediaControlsVisiblity();
        }*/
        return false;
    }
    
    /*private void toggleMediaControlsVisiblity() {
        if (mVideoController.isShowing()) { 
            mVideoController.hide();
        } else {
            mVideoController.show();
        }
    }*/
    
//    private void createMediaPlayer() {
//        release(false);
//
//        // The video could not be played back if launch the player quickly        
//        if (mMediaPlayer == null) {
//            mIsInit = false;
//            mMediaPlayer = new MediaPlayer();
//
//            mMediaPlayer.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {
//                public void onBufferingUpdate(MediaPlayer mp, int percent) { }
//            });
//
//            mMediaPlayer.setOnCompletionListener(mMediaOnCompletionListener);
//            mMediaPlayer.setOnErrorListener(mMediaPlayerOnErrorListener);
//            mMediaPlayer.setOnInfoListener(new OnInfoListener() {
//                public boolean onInfo(MediaPlayer mp, int what, int extra) {
//                    return false;
//                }
//            });
//            mMediaPlayer.setOnSeekCompleteListener(new OnSeekCompleteListener() {
//                public void onSeekComplete(MediaPlayer mp) { }
//            });
//            mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
//            mMediaPlayer.setOnVideoSizeChangedListener(mOnVideoSizeChangedListener);
//        }
//    }
    
    private void openVideo() {
        if (filePath == null || mSurfaceHolder == null) {
            // not ready for playback just yet, will try again later
            return;
        }

        // we shouldn't clear the target state, because somebody might have
        // called start() previously
        release(false);
        FileInputStream fis = null;
        
        try {
            if (mMediaPlayer == null) {
                mMediaPlayer = new MediaPlayer();
            }
            mDuration = -1;
            
            mIsVideoDataReady = false;
            
            mMediaPlayer.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {
                public void onBufferingUpdate(MediaPlayer mp, int percent) { }
            });

            mMediaPlayer.setOnCompletionListener(mMediaOnCompletionListener);
            mMediaPlayer.setOnErrorListener(mMediaPlayerOnErrorListener);
            mMediaPlayer.setOnInfoListener(new OnInfoListener() {
                public boolean onInfo(MediaPlayer mp, int what, int extra) {
                    return false;
                }
            });
            mMediaPlayer.setOnSeekCompleteListener(new OnSeekCompleteListener() {
                public void onSeekComplete(MediaPlayer mp) { }
            });
            mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
            mMediaPlayer.setOnVideoSizeChangedListener(mOnVideoSizeChangedListener);
            
            File media = new File(filePath);
            fis = new FileInputStream(media);
            mMediaPlayer.setDataSource(fis.getFD());
            mMediaPlayer.setDisplay(mSurfaceHolder);
            mMediaPlayer.prepare();
            mMediaPlayer.setScreenOnWhilePlaying(true);
            
            mCurrentState = STATE_PREPARING;
        } catch (IllegalStateException e) {
            if (ENABLE_LOG)
                Log.d(TAG, "play error=" + e.getMessage());
            e.printStackTrace();
            if (mOnErrorListener != null)
                mOnErrorListener.onError(mMediaPlayer, 0, 0);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            return;
        } catch (IOException e) {
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            if (ENABLE_LOG)
                Log.d(TAG, "play error=" + e.getMessage());
            e.printStackTrace();
            if (mOnErrorListener != null)
                mOnErrorListener.onError(mMediaPlayer, 0, 0);
            return;
        } finally {
        	try {
        		fis.close();
        		fis = null;
        	} catch (Exception e) {}
        }
    }
    
//    /** play video file */
//    public void play(String in) {
//        if(TextUtils.isEmpty(in))
//            return;
//        if(filePath.compareTo(in) == 0) {
//            if((mMediaPlayer != null) && (mMediaPlayer.isPlaying()))
//                return;
//        } else {
//            filePath = in;
//            pos = -1;
//        }
//        
//        if((mMediaPlayer != null) && mMediaPlayer.isPlaying()){
//            // mMediaPlayer.stop();
//            stopMediaPlayer();
//        }
//        
//        mVideoWidth = 0;
//        mVideoHeight = 0;
//        mIsVideoDataReady = false;
//
//        createMediaPlayer();
//        
//        try {
//            mMediaPlayer.setDataSource(filePath);
//            mMediaPlayer.setDisplay(mSurfaceHolder);
//            mMediaPlayer.prepare();
//        } catch (IllegalStateException e) {
//            if (ENABLE_LOG)
//                Log.d(TAG, "play error=" + e.getMessage());
//            e.printStackTrace();
//            if (mOnErrorListener != null)
//                mOnErrorListener.onError(mMediaPlayer, 0, 0);
//            mCurrentState = STATE_ERROR;
//            mTargetState = STATE_ERROR;
//            return;
//        } catch (IOException e) {
//            mCurrentState = STATE_ERROR;
//            mTargetState = STATE_ERROR;
//            if (ENABLE_LOG)
//                Log.d(TAG, "play error=" + e.getMessage());
//            e.printStackTrace();
//            if (mOnErrorListener != null)
//                mOnErrorListener.onError(mMediaPlayer, 0, 0);
//            return;
//        }
//        // TODO after start playback, we need to start refresh seekbar progress and displayed time 
//        if (mVideoController != null) {
//            mVideoController.updateSeekBarInfo();
//        }
//    }
    
    /** cache duration as mDuration for faster access */ 
    public int getDuration() {
        if (mCurrentState == STATE_ERROR) {
            return 0;
        }
        if ((mMediaPlayer != null)/* && (mIsInit)*/) {
            if (mDuration > 0)
                return mDuration;
            mDuration = mMediaPlayer.getDuration();
            return mDuration;
        } 
        mDuration = -1;
        return mDuration;
    }
    
    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }
    
    // 20120509 disable autoplay function <S>
    private boolean mCanAutoPlay = false;
    public void setCanAutoPlayWhenResumeVideoView(boolean autoplay) {
        mCanAutoPlay = autoplay;
    }
    // 20120509 disable autoplay function <E>
    
    private OnPreparedListener mOnPreparedListener = new OnPreparedListener(){
        public void onPrepared(MediaPlayer mp) {
            mCurrentState = STATE_PREPARED;
            
            mIsInit = true;
            mDuration = getDuration();
            if (ENABLE_LOG)
                Log.d(TAG, "OnPreparedListener");
            mIsVideoDataReady = true;           
            // mVideoWidth = mp.getVideoWidth();
            // mVideoHeight = mp.getVideoHeight();
            
            // only set mVideoWidth mVideoHeight if return values >0
            int nWidth = mp.getVideoWidth();
            int nHeight = mp.getVideoHeight();
            if (nWidth > 0 && nHeight > 0) {
                mVideoWidth = nWidth;
                mVideoHeight = nHeight;
            } else {
                mVideoWidth = mMediaPlayer.getVideoWidth();
                mVideoHeight = mMediaPlayer.getVideoHeight();
            }
            if (ENABLE_LOG) Log.d(TAG,"onPrepared():width=" + mVideoWidth + ".Height="+ mVideoHeight);
            if (mVideoWidth >= 0 && mVideoHeight >= 0) {
                /*if (mSurfaceHolder != null)
                    mSurfaceHolder.setFixedSize(mVideoWidth, mVideoHeight);*/
                getHolder().setFixedSize(mVideoWidth, mVideoHeight);
                if (mIsVideoDataReady){
                    if (pos > 0) {
                        mMediaPlayer.seekTo(pos);
                        pos = -1;
                    }
                    // startMediaPlayer(); // 20120424 disable auto playback!
                    if (mCanAutoPlay) {
                        startMediaPlayer();
                    }
                    /*requestLayout();
                    invalidate();*/
                    if (ENABLE_LOG) Log.d(TAG, "onPrepared");
                }               
            } else {
                // We don't know the video size yet, but should start anyway.
                // The video size might be reported to us later.
                if (mTargetState == STATE_PLAYING) {
                    if (mCanAutoPlay) {
                        start();                        
                    }
                }
            }
        }
        
    };
    
    private OnVideoSizeChangedListener mOnVideoSizeChangedListener = new OnVideoSizeChangedListener() {
        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) 
        {
            mVideoWidth = mp.getVideoWidth();
            mVideoHeight = mp.getVideoHeight();
            if (mVideoWidth != 0 && mVideoHeight != 0) {
                getHolder().setFixedSize(mVideoWidth, mVideoHeight);
            }
            /*
            // only set mVideoWidth mVideoHeight if return values >0
            int nWidth = mp.getVideoWidth();
            int nHeight = mp.getVideoHeight();
            if (nWidth > 0 && nHeight > 0) {
                mVideoWidth = nWidth;
                mVideoHeight = nHeight;
            } else {
                mVideoWidth = mMediaPlayer.getVideoWidth();
                mVideoHeight = mMediaPlayer.getVideoHeight();
            }
            
            if (ENABLE_LOG) Log.d(TAG,"onVideoSizeChanged():width=" + mVideoWidth + ".Height="+ mVideoHeight);
            if(mVideoWidth >= 0 && mVideoHeight >= 0){
                if (mSurfaceHolder != null)
                    mSurfaceHolder.setFixedSize(mVideoWidth, mVideoHeight);
                if (mIsVideoDataReady){
                    if (pos > 0) {
                        mMediaPlayer.seekTo(pos);
                        pos = -1;
                    }      
                    // mMediaPlayer.start();
                    startMediaPlayer();
                    requestLayout();
                    invalidate();
                    if (ENABLE_LOG) Log.d(TAG,"onVideoSizeChanged"); 
                }
            }*/
        }
    };

    // The video could not be played back if launch the player quickly
    public void setPath(String path) {
        if(!TextUtils.isEmpty(path)) {
            filePath = path;
        }
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
        int height = getDefaultSize(mVideoHeight, heightMeasureSpec);
        
        if (mVideoWidth > 0 && mVideoHeight > 0) {
            if ( mVideoWidth * height  > width * mVideoHeight ) {
                // video height exceeds screen, shrink it
                height = width * mVideoHeight / mVideoWidth;
            } else if ( mVideoWidth * height  < width * mVideoHeight ) {
                // video width exceeds screen, shrink it
                width = height * mVideoWidth / mVideoHeight;
            } else {
                // aspect ratio is correct
            }
        }
        
        setMeasuredDimension(width, height);
    }
    
    private OnCompletionListener mOnCompletionListener;
    private OnErrorListener mOnErrorListener;
    
    private MediaPlayer.OnCompletionListener mMediaOnCompletionListener 
                            = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mp) {
            mCurrentState = STATE_PLAYBACK_COMPLETED;
            mTargetState = STATE_PLAYBACK_COMPLETED;
            /*if (mVideoController != null) {
                mVideoController.hide();
            }*/
            if (mOnCompletionListener != null) {
                mOnCompletionListener.onCompletion(mMediaPlayer);
            }
            // should update seekbar info when done!
            resetSeekbarAndVideoPlayerToZero();
        }
    };
    
    public void resetSeekbarAndVideoPlayerToZero() {
        if (mVideoController != null && mMediaPlayer != null) {
            mMediaPlayer.seekTo(0);
            pos = -1;
            mVideoController.updateSeekBarInfo();
        }
    }
    
    private MediaPlayer.OnErrorListener mMediaPlayerOnErrorListener 
                        = new MediaPlayer.OnErrorListener() {
        public boolean onError(MediaPlayer mp, int what, int extra) {
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            mIsInit = false;
            if (mOnErrorListener != null)
                return mOnErrorListener.onError(mMediaPlayer, what, extra);
            return false;
        }
    };
    
    public void setOnCompletionListener(OnCompletionListener l) {
        mOnCompletionListener = l;
    }
    
    public void setOnErrorListener(OnErrorListener l) {
        mOnErrorListener = l;
    }
    
    private void startMediaPlayer(){
        if (ENABLE_LOG) {
            Log.d(TAG, ">>> start media player!!!");
        }
        mMediaPlayer.start();
    }

    private void pauseMediaPlayer(){        
        mMediaPlayer.pause();
    }
    
    private void stopMediaPlayer(){
        mMediaPlayer.stop();
    }
    
    // The video could not be played back if launch the player quickly    
    public void updateSeekBar() {
        if (mVideoController != null) {
            if (mVideoController.mVideoSeekBar != null && mMediaPlayer != null) {
                mVideoController.mVideoSeekBar.setMax(mMediaPlayer.getDuration());
            }
        }
    }

    public void addVideoController(PBVideoControllerView videoControllerView) {
        mVideoController = videoControllerView;
    }
    
}
