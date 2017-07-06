package com.smartmux.filevault.video;

import java.io.File;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

import com.smartmux.filevault.AppMainActivity;
import com.smartmux.filevault.LoginWindowActivity;
import com.smartmux.filevault.R;
import com.smartmux.filevault.utils.AppExtra;
import com.smartmux.filevault.utils.FileManager;
public class VideoViewActivity extends AppMainActivity {
	private VideoView myVideoView;
	private int position = 0;
	private MediaController mediaControls;
	private String filePath;
	private FileManager fileManager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.video_view);
		
		fileManager = new FileManager();
		fileManager.setBackCode(getApplicationContext());
		//set the media controller buttons
				if (mediaControls == null) {
					mediaControls = new MediaController(VideoViewActivity.this);
				}

				//initialize the VideoView
				myVideoView = (VideoView) findViewById(R.id.video_view);


				try {
					//set the media controller in the VideoView
					myVideoView.setMediaController(mediaControls);

					Intent receiveIntent = getIntent();
					if (receiveIntent != null) {
						filePath = receiveIntent.getStringExtra("filePath");
					}
					//set the uri of the video to be played
					myVideoView.setVideoURI(Uri.fromFile(new File(filePath)));

				} catch (Exception e) {
					Log.e("Error", e.getMessage());
					e.printStackTrace();
				}

				myVideoView.requestFocus();
				//we also set an setOnPreparedListener in order to know when the video file is ready for playback
				myVideoView.setOnPreparedListener(new OnPreparedListener() {
				
					public void onPrepared(MediaPlayer mediaPlayer) {
						// close the progress bar and play the video
						//if we have a position on savedInstanceState, the video playback should start from here
					findViewById(R.id.progressBar1).setVisibility(View.GONE);
						myVideoView.seekTo(position);
						if (position == 0) {
							myVideoView.start();
						} else {
							//if we come from a resumed activity, video playback will be paused
							myVideoView.pause();
						}
					}
				});

			}

			@Override
			public void onSaveInstanceState(Bundle savedInstanceState) {
				super.onSaveInstanceState(savedInstanceState);
				//we use onSaveInstanceState in order to store the video playback position for orientation change
				savedInstanceState.putInt("Position", myVideoView.getCurrentPosition());
				myVideoView.pause();
			}

			@Override
			public void onRestoreInstanceState(Bundle savedInstanceState) {
				super.onRestoreInstanceState(savedInstanceState);
				//we use onRestoreInstanceState in order to play the video playback from the stored position 
				position = savedInstanceState.getInt("Position");
				myVideoView.seekTo(position);
			}
			
			@Override
			 protected void onResume() {
				super.onResume();
				
				int event_code = fileManager.getReturnCode(getApplicationContext());
				if(event_code == AppExtra.HOME_CODE ){
					Intent i = new Intent(VideoViewActivity.this, LoginWindowActivity.class);
			        startActivity(i);
				}
			}
			
			@Override
			 public boolean onKeyDown(int keyCode, KeyEvent event) {
			        if (keyCode == KeyEvent.KEYCODE_BACK){   
			        	fileManager.setBackCode(getApplicationContext());
					    finish();
					    overridePendingTransition(R.anim.push_right_out,
								R.anim.push_right_in); 
			            return true;
			        }
			        return super.onKeyDown(keyCode, event);
			 }
			
			 @Override
				protected void onUserLeaveHint() {
			    	fileManager.setHomeCode(getApplicationContext());
					super.onUserLeaveHint();
					}

}
