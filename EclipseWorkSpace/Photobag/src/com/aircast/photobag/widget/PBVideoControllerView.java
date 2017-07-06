package com.aircast.photobag.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.aircast.photobag.R;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.utils.PBGeneralUtils;
import com.aircast.photobag.utils.PBPreferenceUtils;

public class PBVideoControllerView extends LinearLayout {
    
    /** The Constant FADE OUT */
    protected static final int FADE_OUT         = 1;
    /** The Constant updated button status */
    protected static final int UPDATE_BUTTON    = 3;
    protected static final int MSG_UPDATE_SEEKBAR = 4;
    /** Time for showing controller on the screen. */
    public static final int sDefaultTimeout     = 3000;
    
    /** The variable for controlling show or hide controller */
    private boolean mIsShowing = false;

    private ImageView mPlayPauseButton;
    private TextView mElapsedTime;
    private TextView mRemainingTime;
    SeekBar mVideoSeekBar;
    private PBCustomVideoView mVideoView;
    
    private boolean mSeekBarChange = false;
    private Context mContext;
    
    public PBVideoControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public PBVideoControllerView(Context context) {
        super(context);
        mContext = context;
    }
    
    /** 
     * The m Handler to control
     *          FADE_OUT: hide control_bar, 
     *          UPDATE_BUTTON: play/pause video
     */
    protected Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FADE_OUT:
                    hide();                             
                    break;
                    
                case UPDATE_BUTTON:
                    updatePausePlay();
                    break;
                    
                case MSG_UPDATE_SEEKBAR:   
                    if(msg.arg1 > 0){
                        mVideoView.seekToPos(msg.arg1, true);
                    }
                    // update seekbar
                    if(!mSeekBarChange && (mVideoSeekBar != null) && (mVideoView != null)){   
                        int lst = mVideoView.getCurrentPosition();
                        // Log.d("mapp", "[MSG_UPDATE_SEEKBAR][mVideoView.getCurrentPosition()] = " + lst);
                        mVideoSeekBar.setProgress(lst);
                    }
                    // update duration timer
                    if (mElapsedTime != null && mRemainingTime != null && mVideoView != null) {
                        updateDurationTime(mVideoView.getCurrentPosition());
                    }
                    // update play/pause button
                    updatePausePlay();
                    if (isVideoPlaying()) {
                        mHandler.sendEmptyMessageDelayed(MSG_UPDATE_SEEKBAR, 1000);
                    }
                    break;
                    
            }
        }
    };

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mPlayPauseButton = (ImageView) findViewById(R.id.btn_video_play_pause);
        mElapsedTime = (TextView) findViewById(R.id.tv_video_elapsed_time);
        mRemainingTime = (TextView) findViewById(R.id.tv_video_remaining_time);
        mVideoSeekBar = (SeekBar) findViewById(R.id.video_seekbar);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // return super.onTouchEvent(event);
        return true;
    }
    
    public void setVideoView(PBCustomVideoView player) throws NullPointerException{
        if (player == null) {
            throw new NullPointerException("Media player cannot be null!");
        }
        mVideoView = player;
        updateRangeForVideoSeekBar();
        setupPlayPauseButton();
        setupSeekBar();
        /*// should update progress status of seekbar and time duration
        mHandler.sendEmptyMessageDelayed(MSG_UPDATE_SEEKBAR, 2000);*/
    }
    
    private void updateDurationTime(int progress) {
        if (mRemainingTime != null && mElapsedTime != null) {
            mElapsedTime.setText("-"
                    + PBGeneralUtils.makeTimeString(mContext, progress / 1000));
            mRemainingTime.setText(PBGeneralUtils.makeTimeString(mContext,
                    (mVideoView.getDuration() - progress) / 1000));
        }
    }
    
    private void setupSeekBar() {
        mVideoSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            // int lastPosition = 0;
            public void onProgressChanged(SeekBar arg0, int progress, boolean fromUser) {
                if ((fromUser) && (mVideoView != null)) {
                    // mVideoView.seekToPos(position, true);
                    // show();
                    // lastPosition = position;
                }
                // update duration time
                updateDurationTime(progress);
            }

            public void onStartTrackingTouch(SeekBar arg0) {
                mSeekBarChange = true; 
                // 2011031 @le.nguyen fix #5728 start
                if ((mVideoView != null)) {  
                    mHandler.removeMessages(MSG_UPDATE_SEEKBAR);
                    // show();
                }             
            }

            public void onStopTrackingTouch(SeekBar arg0) {
                if ((mVideoView != null)) {
                    mVideoView.seekToPos(arg0.getProgress());
                    // show();                   
                    mHandler.sendEmptyMessage(MSG_UPDATE_SEEKBAR);                    
                }          
                mSeekBarChange = false;
                Log.d("mapp", "[ mVideoView.seekToPos(Seekbar.getProgress())] = " + arg0.getProgress() 
                        + " [ mVideoView.getCurrentPosition()] = " + mVideoView.getCurrentPosition()); 
            }
        });
        // plan for starting auto update seekbar!
        mHandler.sendEmptyMessageDelayed(MSG_UPDATE_SEEKBAR, 1000);
    }
    
    public void updateSeekBarInfo() {
        // remove old msg first
        mHandler.removeMessages(MSG_UPDATE_SEEKBAR);
        // update msg queue now
        mHandler.sendEmptyMessage(MSG_UPDATE_SEEKBAR);
    }
    
    private void setupPlayPauseButton() {
        if (mVideoView != null && mPlayPauseButton != null) {
            mPlayPauseButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isVideoPlaying()) {
                        mVideoView.pausePlayback();
                        // saving playback state
                        PBPreferenceUtils.saveBoolPref(
                                mContext, 
                                PBConstant.PREF_NAME, 
                                PBConstant.PREF_IS_VIDEO_PLAYING, 
                                false);
                    } else {
                        mVideoView.resumePlayback();
                        // saving playback state
                        PBPreferenceUtils.saveBoolPref(
                                mContext, 
                                PBConstant.PREF_NAME, 
                                PBConstant.PREF_IS_VIDEO_PLAYING, 
                                true);
                    }
                    updatePausePlay();
                    // should update progress status of seekbar and time duration
                    mHandler.sendEmptyMessageDelayed(MSG_UPDATE_SEEKBAR, 1000);
                }
            });
        }
    }
    
    public void setVisibilityOfController(int visibility) {
        /*if (this.getVisibility() == View.VISIBLE) {
            this.setVisibility(View.GONE);
        } else {
            if (this.getVisibility() == View.GONE) {
                this.setVisibility(View.VISIBLE);
            }
        }*/
        this.setVisibility(visibility);
        updatePausePlay();
    }
    
    /** Set the range of the video progress bar to 0...max. */
    public void updateRangeForVideoSeekBar(){
        if (mVideoView != null) {
            if (mVideoSeekBar != null) {
                mVideoSeekBar.setMax(mVideoView.getDuration());
            }
        }
    }
    
    /** Hide the controller */
    public void hide() {
        mIsShowing = false;
        setVisibilityOfController(View.GONE);
    }
    
    /** Show the controller on screen. It will go away automatically after 3 seconds of inactivity. */
    public void show() {
        show(sDefaultTimeout);
    }
    
    /**
     * Show the controller on screen. It will go away
     * automatically after 'timeout' milliseconds of inactivity.
     * @param timeout The timeout in milliseconds. Use 0 to show
     * the controller until hide() is called.
     */
    public void show(int timeout) {
        if (!mIsShowing) {
            setVisibilityOfController(View.VISIBLE);
            mIsShowing = true;
        }
        
        if (mIsShowing) {
            updatePausePlay();
            // remove FADE_OUT message and re-send again if timeout isn't empty.
            Message msg = mHandler.obtainMessage(FADE_OUT);
            mHandler.removeMessages(FADE_OUT);
            if (timeout != 0) {
                mHandler.sendMessageDelayed(msg, timeout);
            }
        }
    }
    
    /** Update the status of Play/Pause button */
    public void updatePausePlay() {
        /*if(!mIsShowing) {
            mHandler.removeMessages(UPDATE_BUTTON);
            return;
        }*/

        if (mPlayPauseButton == null)
            return;

        if (isVideoPlaying()) {
            mPlayPauseButton.setImageResource(R.drawable.ic_pose);
        } else { 
            mPlayPauseButton.setImageResource(R.drawable.ic_play);
        }
        /*Message msg = mHandler.obtainMessage(UPDATE_BUTTON);
        mHandler.sendMessageDelayed(msg, 200);*/
    }
    
    public boolean isVideoPlaying() {
        if (mVideoView != null) {
            return mVideoView.isPlaying();
        }
        return false;
    }

}
