package com.smartmux.videoeditor;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.VideoView;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.smartmux.videoeditor.utils.Utils;
import com.smartmux.wiget.CircularProgressBar;
import com.smartmux.wiget.CircularProgressDrawable;

public class AddCoverActivity extends Activity {
	
		// ffmpeg -i inputfile.avi -r 1 -s 4cif -f image2 image-%3d.jpeg
		private static final String TAG = AddCoverActivity.class
				.getSimpleName();
	
		private String mPath;
		private String outFilePath;
		FFmpeg ffmpeg;
		private VideoView mVideoView;
		private SeekBar seekbar;
		CircularProgressBar   progressBar;
	
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			this.requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.activity_frame);
			mPath = getIntent().getExtras().getString("path", "");
			if (mPath.equals("")) {
	
				finish();
				
				}
			File file = new File(mPath);
			outFilePath = Utils.getVideoFrameDirectory(file.getName());
			progressBar = (CircularProgressBar) findViewById(R.id.progressbar_circular);
			System.out.println(outFilePath);
			mVideoView = (VideoView) findViewById(R.id.videoView_trim_src);
			seekbar = (SeekBar) findViewById(R.id.seekBar_video_thumb);
			
				
				Uri mVideoUri = Uri.parse(mPath);
				mVideoView.setVideoURI(mVideoUri);
				mVideoView.seekTo(0);
				mVideoView.setOnPreparedListener(new OnPreparedListener() {

					@Override
					public void onPrepared(MediaPlayer mp) {

						seekbar.setMax(mp.getDuration());
						seekbar.setProgress(0);

					}
				});
				
				
				seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
					
					@Override
					public void onStopTrackingTouch(SeekBar arg0) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onStartTrackingTouch(SeekBar arg0) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onProgressChanged(SeekBar arg0, int process, boolean arg2) {
						// TODO Auto-generated method stub
						System.out.println(process);
						mVideoView.seekTo(process);
					}
				});
				
				
				this.findViewById(R.id.textView_save).setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// -i input.flv -ss 00:00:14.435 -vframes 1 out.png
						String time = "00:" + Utils
								.getTimeForTrackFormat(mVideoView.getCurrentPosition(), true) + ".0";
						
						String command = "-i " + mPath
						+ " -ss "+time+" -vframes 1 "
						+ outFilePath;
				execFFmpegBinary(command);
					}
				});
	
	
		}
		
		@Override
		protected void onResume(){
			super.onResume();
			ffmpeg = FFmpeg.getInstance(this);
			loadFFMpegBinary();


		}
	
		private void loadFFMpegBinary() {
			try {
				ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
					@Override
					public void onFailure() {
						// showUnsupportedExceptionDialog();
					}
				});
			} catch (FFmpegNotSupportedException e) {
				// showUnsupportedExceptionDialog();
			}
		}
	
		private void execFFmpegBinary(final String command) {
			try {
				ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {
	
					@Override
					public void onFailure(String s) {
						// addTextViewToLayout("FAILED with output : "+s);
						Log.d(TAG, "onFailure  :  " + s);
					}
	
					@Override
					public void onSuccess(String s) {
						// addTextViewToLayout("SUCCESS with output : "+s);
						Log.d(TAG, " onSuccess : ffmpeg " + s);
					}
	
					@Override
					public void onProgress(String s) {
						
						Log.d(TAG, " command :  " + command);
					}
	
					@Override
					public void onStart() {
						
						progressBar.setVisibility(View.VISIBLE);
						((CircularProgressDrawable)progressBar.getIndeterminateDrawable()).start();
						
					}
	
					@Override
					public void onFinish() {
						
						((CircularProgressDrawable)progressBar.getIndeterminateDrawable()).stop();
						progressBar.setVisibility(View.INVISIBLE);
						
						Intent intent = new Intent(AddCoverActivity.this,VideoListActivity.class);
						startActivity(intent);
						finish();
					}
				});
			} catch (FFmpegCommandAlreadyRunningException e) {
				// do nothing for now
			}
		}
	
	
//		@Override
//		public void onDestroy() {
//			super.onDestroy();
//			File tmpDirector = new File(Utils.getVideoFrameDirectory());
//	
//			if (tmpDirector.exists()) {
//				tmpDirector.delete();
//				try {
//					FileUtils.deleteDirectory(tmpDirector);
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//	
//		}
	
	}


