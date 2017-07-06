package com.smartmux.videoeditor;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoActivity extends Activity {
	
	private String fileName;
	private VideoView videoView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.player);
		
		fileName = getIntent().getExtras().getString("filename", "");
		if(fileName.equals("")){
			finish();
		}
		

			String root = Environment.getExternalStorageDirectory()
					.getAbsolutePath();
			File dir = new File(root + "/.VideoTrimmer");
			
		
		videoView = (VideoView) findViewById(R.id.videoView_player);
		
		Uri uri = Uri.parse(dir.getAbsolutePath()+"/"+fileName.replace(".jpeg", ".mp4"));
		videoView.setVideoURI(uri);
		
		videoView.start();
		videoView.setOnPreparedListener(new OnPreparedListener() {
	        @Override
	        public void onPrepared(MediaPlayer mp) {
	            mp.setOnVideoSizeChangedListener(new OnVideoSizeChangedListener() { 
	            @Override
	            public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
	                /*
	                 * add media controller
	                 */
	            	MediaController mediaController = new MediaController(VideoActivity.this);
	            	videoView.setMediaController(mediaController);
	                /*
	                 * and set its position on screen
	                 */
	            	mediaController.setAnchorView(videoView);
	            }
	        });
	            
	        }
	
		});
	}

}
