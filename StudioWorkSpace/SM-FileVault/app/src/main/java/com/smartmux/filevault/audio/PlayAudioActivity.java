package com.smartmux.filevault.audio;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Chronometer;
import android.widget.Chronometer.OnChronometerTickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.smartmux.filevault.AppMainActivity;
import com.smartmux.filevault.LoginWindowActivity;
import com.smartmux.filevault.R;
import com.smartmux.filevault.utils.AppActionBar;
import com.smartmux.filevault.utils.AppExtra;
import com.smartmux.filevault.utils.FileManager;
import com.smartmux.filevault.utils.FileUtil;

@SuppressLint("NewApi")
public class PlayAudioActivity extends AppMainActivity {

	private MediaRecorder myAudioRecorder;
	private String outputFile = null;

	//private ImageButton buttonStart;
	//private ImageButton buttonStop;
	private ImageButton buttonPlay;
	private TextView textViewStatus;

	private String folderName;
	private String subFolderName;
	private String audioMode;
	private String audioFileName;
	private FileManager fileManager;
	private MediaPlayer audioPlayer;
	private Chronometer chronometer;
	private boolean isPlay=false;
	//private CircularSeekBar seekBarAudio;
	private int mediaPos;
	private int mediaMax;
	private Handler handler;
	private long time=0;
	private long base;


	@SuppressLint("ResourceAsColor") @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.voice_recorder);

		fileManager = new FileManager();
		fileManager.setBackCode(getApplicationContext());
		AppActionBar.updateAppActionBar(getActionBar(), this, true);

		Bundle bundle = getIntent().getExtras();

		folderName = bundle.getString(AppExtra.FOLDER_NAME);
		subFolderName = bundle.getString(AppExtra.SUB_FOLDER_NAME);
		audioMode = bundle.getString(AppExtra.AUDIO_MODE);
		audioFileName = bundle.getString(AppExtra.AUDIO_FILENAME);

		if ((folderName != null) && (subFolderName != null)) {
			getActionBar().setTitle(folderName + "/" + subFolderName);
		}

		
		 chronometer = (Chronometer) findViewById(R.id.chronometer);
		//chronometer.setVisibility(View.VISIBLE);

		chronometer
				.setOnChronometerTickListener(new OnChronometerTickListener() {
					

					

					@Override
					public void onChronometerTick(Chronometer cArg) {
						
						base = cArg.getBase();
						 time = SystemClock.elapsedRealtime()
								- cArg.getBase();
						int h = (int) (time / 3600000);
						int m = (int) (time - h * 3600000) / 60000;
						int s = (int) (time - h  *3600000 - m * 60000) / 1000;
						String hh = h < 10 ? "0" + h : h + "";
						String mm = m < 10 ? "0" + m : m + "";
						String ss = s < 10 ? "0" + s : s + "";
						cArg.setText(mm + ":"+ ss);
					}
				});

		
		
		
		
	//	buttonStart = (ImageButton) findViewById(R.id.buttonRecord);
	//	buttonStop = (ImageButton) findViewById(R.id.buttonStop);
		buttonPlay = (ImageButton) findViewById(R.id.buttonPlay);
		textViewStatus = (TextView) findViewById(R.id.textViewStatus); 
//		seekBarAudio=(CircularSeekBar) findViewById(R.id.seekBar_audio);
//		seekBarAudio.setVisibility(View.VISIBLE);
		textViewStatus.setText("Press Play Button");

		//buttonStop.setEnabled(false);
		buttonPlay.setEnabled(false);
		
		
		
		
		myAudioRecorder = new MediaRecorder();
		myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);

		if (audioMode.equals(AppExtra.AUDIO_MODE_PLAY)) {
			//buttonStop.setAlpha(0.0f);
			//buttonStart.setAlpha(0.0f);
			
			//chronometer.setVisibility(View.INVISIBLE);
			textViewStatus.setText("Press Play Button");
			buttonPlay.setEnabled(true);
			outputFile = AppExtra.APP_ROOT_FOLDER + "/" + folderName + "/"
					+ subFolderName + "/" + audioFileName;
			myAudioRecorder.setOutputFile(outputFile);
			
		} else {
			buttonPlay.setVisibility(View.INVISIBLE);
			outputFile = AppExtra.APP_ROOT_FOLDER + "/" + folderName + "/"
					+ subFolderName + "/" + FileUtil.getDynamicFileName()
					+ ".3gp";
			myAudioRecorder.setOutputFile(outputFile);
		//	seekBarAudio.setVisibility(View.INVISIBLE);
		}
		
		
		
		
		 handler = new Handler();
		audioPlayer = new MediaPlayer();
		try {
			audioPlayer.setDataSource(outputFile);
			audioPlayer.prepare();
			
//			 mediaPos = audioPlayer.getCurrentPosition();
//
//		        mediaMax = audioPlayer.getDuration();
//
//		        seekBarAudio.setMax(mediaMax); // Set the Maximum range of the
//		                                    // seekBar.setProgress(mediaPos);// set
//		                                    // current progress to song's
//		        seekBarAudio.setProgress(mediaPos);// set current progress to song's
		        
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		audioPlayer.setOnCompletionListener(new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				//Toast.makeText(getApplicationContext(), " Complete", 1000).show();
				isPlay=false;
				chronometer.stop();
				time=0;
				textViewStatus.setText("Press Play Button");
				buttonPlay.setBackgroundResource(R.drawable.play_button);
			}
		});
//		seekBarAudio.setOnSeekBarChangeListener(new OnCircularSeekBarChangeListener() {
//			
//			@Override
//			public void onStopTrackingTouch(CircularSeekBar seekBar) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onStartTrackingTouch(CircularSeekBar seekBar) {
//				// TODO Auto-generated method stub
//			
//				
//			}
//			
//			@Override
//			public void onProgressChanged(final CircularSeekBar circularSeekBar,
//					final int progress, boolean fromUser) {
//				// TODO Auto-generated method stub
//				chronometer
//				.setOnChronometerTickListener(new OnChronometerTickListener() {
//					
//					@Override
//					public void onChronometerTick(Chronometer chronometer) {
//						// TODO Auto-generated method stub
//						  long time = SystemClock.elapsedRealtime()
//								- chronometer.getBase();
//						int h = (int) (time / 3600000);
//						int m = (int) (time - h * 3600000) / 60000;
//						int s = (int) (time - h  *3600000 - m * 60000) / 1000;
//						String hh = h < 10 ? "0" + h : h + "";
//						String mm = m < 10 ? "0" + m : m + "";
//						String ss = s < 10 ? "0" + s : s + "";
//						chronometer.setText(mm + ":"+ ss);
//						
////						if(progress>=circularSeekBar.getMax()){
////							
////							Toast.makeText(getApplicationContext(), time+"time"+progress+"progress", 1000).show();
////						}
//					}
//				} );
//				
//				
//			}
//		});
		
		 // handler.removeCallbacks(moveSeekBarThread);
		

		
	
	}
	
//	 private Runnable moveSeekBarThread = new Runnable() {
//
//	        public void run() {
//	        	if(audioPlayer!=null){
//	            if (audioPlayer.isPlaying()) {
//
//	                int mediaPos_new = audioPlayer.getCurrentPosition();
//	                int mediaMax_new = audioPlayer.getDuration();
//
//	                seekBarAudio.setMax(mediaMax_new);
//	                seekBarAudio.setProgress(mediaPos_new);
//
//	                handler.postDelayed(this, 100); // Looping the thread after 0.1
//	                                               // seconds
//	            } else {
//	                int mediaPos_new = audioPlayer.getCurrentPosition();
//	                int mediaMax_new = audioPlayer.getDuration();
//
//	                seekBarAudio.setMax(mediaMax_new);
//	                seekBarAudio.setProgress(mediaPos_new);
//
//	                handler.postDelayed(this, 100); // Looping the thread after 0.1
//	                                                // seconds
//	            }
//	        }
//	        }
//	    };

//	   	public void start(View view) {
//		try {
//			myAudioRecorder.prepare();
//			myAudioRecorder.start();
//			
//		} catch (IllegalStateException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		//buttonStart.setEnabled(false);
//		buttonStop.setEnabled(true);
//		chronometer.start();
//		textViewStatus.setText("Recording ...");
//	}
//
//	public void stop(View view) {
//		myAudioRecorder.stop();
//		myAudioRecorder.release();
//		myAudioRecorder = null;
//		buttonStop.setEnabled(false);
//		buttonPlay.setEnabled(true);
//		chronometer.stop();
//		
//		textViewStatus.setText("Recording Finished");
//	}

	public void play(View view) throws IllegalArgumentException,
			SecurityException, IllegalStateException, IOException {
		
	
		if(!isPlay){
		audioPlayer.start();
//		  handler.postDelayed(moveSeekBarThread, 100);
		  chronometer.setBase(SystemClock.elapsedRealtime() + time);
			chronometer.start();
		isPlay=true;

	     
	        System.out.println("ishti    "+audioPlayer);
	        textViewStatus.setText("Recording Playing ...");
	        buttonPlay.setBackgroundResource(R.drawable.pause_button);
		//Toast.makeText(getApplicationContext(), "play", 1000).show();
		
		}else{
			
			audioPlayer.stop();
			
			audioPlayer.prepare();
			isPlay=false;
			 System.out.println("ishti    "+audioPlayer);
			 chronometer.stop();
			 time = chronometer.getBase() - SystemClock.elapsedRealtime();
			   textViewStatus.setText("Recording Paused ...");
			   buttonPlay.setBackgroundResource(R.drawable.play_button);
			//Toast.makeText(getApplicationContext(), "stop", 1000).show();
		}
		
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (!audioMode.equals(AppExtra.AUDIO_MODE_PLAY)) {
				// handler.removeCallbacks(moveSeekBarThread);
				try {
					if (myAudioRecorder != null) {
						
						myAudioRecorder.release();
						myAudioRecorder = null;
					}
					if(audioPlayer!=null){
						
						audioPlayer.release();
						audioPlayer=null;
					}
					
				} catch (IllegalStateException e) {
					e.printStackTrace();
				}
				
			}
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
	@Override
	public void onPause(){
	    super.onPause();
	   // handler.removeCallbacks(moveSeekBarThread);
	   
	  //  myAudioRecorder.release();
	    //and so on

	}
	@Override
	protected void onResume() {
		super.onResume();

		int event_code = fileManager.getReturnCode(getApplicationContext());
		if (event_code == AppExtra.HOME_CODE) {
			Intent i = new Intent(PlayAudioActivity.this, LoginWindowActivity.class);
			startActivity(i);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (audioPlayer != null) {
			handler.removeCallbacksAndMessages(null);
			audioPlayer.release();
			audioPlayer = null;
		}
	//	handler.removeCallbacks(moveSeekBarThread);
		if (myAudioRecorder != null) {

			myAudioRecorder.release();
			myAudioRecorder = null;
		}

	}
}