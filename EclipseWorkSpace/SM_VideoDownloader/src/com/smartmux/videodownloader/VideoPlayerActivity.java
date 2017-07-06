package com.smartmux.videodownloader;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.smartmux.videodownloader.lockscreen.utils.AppExtra;
import com.smartmux.videodownloader.utils.SMConstant;
import com.smartmux.videodownloader.utils.SMSharePref;

public class VideoPlayerActivity extends FragmentActivity implements
		OnClickListener, SurfaceHolder.Callback, OnPreparedListener {

	private MediaPlayer mediaPlayer;
	private SurfaceHolder vidHolder;
	private SurfaceView vidSurface;
	boolean pausing = false;
	VideoView mVideoView;
	TextView videoTitle, back;
	ImageView playVideo;
	String uriPath, title;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		getWindow().setFormat(PixelFormat.UNKNOWN);

		setContentView(R.layout.activity_video_player);

		MediaController vidControl = new MediaController(this);
		
		
		videoTitle = (TextView) findViewById(R.id.textView_video_title);
		back = (TextView) findViewById(R.id.textView_back);
		back.setText(R.string.back);

		playVideo = (ImageView) findViewById(R.id.imageview_play);

		// Displays a video file.
		mVideoView = (VideoView) findViewById(R.id.videoview);

		vidControl.setAnchorView(mVideoView);
		mVideoView.setMediaController(vidControl);
//		vidSurface = (SurfaceView) findViewById(R.id.surfView);
//		vidHolder = vidSurface.getHolder();
//		vidHolder.addCallback(this);
		
		if (getIntent().hasExtra("video_path")
				&& !getIntent().getExtras().getString("video_path").equals("")) {

			uriPath = getIntent().getExtras().getString("video_path");
			title = getIntent().getExtras().getString("video_title");
			videoTitle.setText(title);

			Uri uri = Uri.parse(uriPath);
			mVideoView.setVideoURI(uri);
			mVideoView.requestFocus();
			mVideoView.start();

			playVideo.setOnClickListener(this);
			back.setOnClickListener(this);

		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.textView_back:
			if (getIntent().hasExtra("Tag")) {
				Intent eventsIntent = new Intent(VideoPlayerActivity.this,
						MainActivity.class);

				eventsIntent.putExtra("settab", "Videos");
				startActivity(eventsIntent);
				finish();
				overridePendingTransition(R.anim.push_right_in,
						R.anim.push_right_out);
			} else {
				finish();
				overridePendingTransition(R.anim.push_right_in,
						R.anim.push_right_out);
			}
			SMSharePref.setBackCode(getApplicationContext());

			break;
		case R.id.imageview_play:
			Uri uri = Uri.parse(uriPath);
			mVideoView.setVideoURI(uri);
			mVideoView.requestFocus();
			mVideoView.start();
			break;
		default:
			break;
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		try {
		    mediaPlayer = new MediaPlayer();
		    mediaPlayer.setDisplay(vidHolder);
		    mediaPlayer.setDataSource(uriPath);
		    mediaPlayer.prepare();
		    mediaPlayer.setOnPreparedListener(this);
		    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		} 
		catch(Exception e){
		    e.printStackTrace();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onPrepared(MediaPlayer mp) {
	//start playback
		mediaPlayer.start();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		String security = SMSharePref.getSecurity(VideoPlayerActivity.this);
		int event_code = SMSharePref.getReturnCode(getApplicationContext());
		if (security.equals(SMConstant.on) && event_code == AppExtra.HOME_CODE) {
//			Toast.makeText(getApplicationContext(),
//					"event_code" + event_code, 1000).show();
			
		Intent i = new Intent(VideoPlayerActivity.this, AppLockActivity.class);
		i.putExtra("passcode", "password_match");
        startActivity(i);
        overridePendingTransition(R.anim.bottom_up, 0);
	}
		
	}

	 @Override
		protected void onUserLeaveHint() {
		 SMSharePref.setHomeCode(getApplicationContext());
			super.onUserLeaveHint();
	}

	

}
