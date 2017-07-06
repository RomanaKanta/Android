package com.smartmux.filevaultfree.audio;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Chronometer;
import android.widget.TextView;

import com.github.lassana.recorder.AudioRecorder;
import com.smartmux.filevaultfree.AppMainActivity;
import com.smartmux.filevaultfree.LoginWindowActivity;
import com.smartmux.filevaultfree.R;
import com.smartmux.filevaultfree.utils.AppActionBar;
import com.smartmux.filevaultfree.utils.AppExtra;
import com.smartmux.filevaultfree.utils.FileManager;
import com.smartmux.filevaultfree.utils.FileUtil;

@SuppressLint("NewApi")
public class RecordAudio extends AppMainActivity {

	private MediaRecorder myAudioRecorder;
	private String outputFile = null;

	
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
	private boolean isRecording=false;
	 private AudioRecorder mAudioRecorder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.voice_recorder);

		fileManager = new FileManager();
		fileManager.setBackCode(getApplicationContext());
		AppActionBar.changeActionBarFont(getApplicationContext(),RecordAudio.this);
		AppActionBar.updateAppActionBar(getActionBar(), this, true);

		Bundle bundle = getIntent().getExtras();

		folderName = bundle.getString(AppExtra.FOLDER_NAME);
		subFolderName = bundle.getString(AppExtra.SUB_FOLDER_NAME);
		audioMode = bundle.getString(AppExtra.AUDIO_MODE);
		audioFileName = bundle.getString(AppExtra.AUDIO_FILENAME);

		if ((folderName != null) && (subFolderName != null)) {
			getActionBar().setTitle(folderName + "/" + subFolderName);
		}

		
//		 mAudioRecorder = savedInstanceState == null
//	                ? RecorderApplication.getApplication(this.createRecorder(getNextFileName())
//	                : RecorderApplication.getApplication(this).getRecorder();
	              
	               
//	        mStartButton = (Button) view.findViewById(R.id.buttonStartRecording);
//	        mStartButton.setOnClickListener(mOnClickListener);
//	        mPauseButton = (Button) view.findViewById(R.id.buttonPauseRecording);
//	        mPauseButton.setOnClickListener(mOnClickListener);	
		

		textViewStatus = (TextView) findViewById(R.id.textViewStatus); 
//		seekBarAudio=(CircularSeekBar) findViewById(R.id.seekBar_audio);
//		seekBarAudio.setVisibility(View.VISIBLE);
		textViewStatus.setText("Press Record Button");

	
		
		
		
	

		if (audioMode.equals(AppExtra.AUDIO_MODE_PLAY)) {
		
			
			//chronometer.setVisibility(View.INVISIBLE);
			textViewStatus.setText("Press Play Button");
			outputFile = AppExtra.APP_ROOT_FOLDER + "/" + folderName + "/"
					+ subFolderName + "/" + audioFileName;
			myAudioRecorder.setOutputFile(outputFile);
		} else {
			outputFile = AppExtra.APP_ROOT_FOLDER + "/" + folderName + "/"
					+ subFolderName + "/" + FileUtil.getDynamicFileName()
					+ ".3gp";
			myAudioRecorder.setOutputFile(outputFile);
			//seekBarAudio.setVisibility(View.INVISIBLE);
		}
		
		
		
		
//		 handler = new Handler();
//		audioPlayer = new MediaPlayer();
//		try {
//			audioPlayer.setDataSource(outputFile);
//			audioPlayer.prepare();
//			
//			 mediaPos = audioPlayer.getCurrentPosition();
//
//		        mediaMax = audioPlayer.getDuration();
//
////		        seekBarAudio.setMax(mediaMax); // Set the Maximum range of the
////		                                    // seekBar.setProgress(mediaPos);// set
////		                                    // current progress to song's
////		        seekBarAudio.setProgress(mediaPos);// set current progress to song's
//		        
//		} catch (IllegalArgumentException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (SecurityException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IllegalStateException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		audioPlayer.setOnCompletionListener(new OnCompletionListener() {
//			
//			@Override
//			public void onCompletion(MediaPlayer mp) {
//				// TODO Auto-generated method stub
//				Toast.makeText(getApplicationContext(), " Complete", 1000).show();
//				isPlay=false;
//			}
//		});
//		
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
//						
//						long time = progress;
//						int h = (int) (time / 3600000);
//						int m = (int) (time - h * 3600000) / 60000;
//						int s = (int) (time - h  *3600000 - m * 60000) / 1000;
//						String hh = h < 10 ? "0" + h : h + "";
//						String mm = m < 10 ? "0" + m : m + "";
//						String ss = s < 10 ? "0" + s : s + "";
//						chronometer.setText(mm + ":"+ ss);
//						
//						
//					}
//				} );
//				
//				
//				
//				
//			}
//		});
		
		 // handler.removeCallbacks(moveSeekBarThread);
		

		
	
	}
	  private String getNextFileName(){
      	return null;
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
//			if(!isRecording){
//			myAudioRecorder.prepare();
//			myAudioRecorder.start();
//			chronometer.start();
//			textViewStatus.setText("Recording ...");
//			view.setBackgroundResource(R.drawable.start_record_button);
//			//buttonStart.setEnabled(false);
//			//buttonStop.setEnabled(true);
//			isRecording=true;
//			
//			}else{
//				
//				myAudioRecorder.stop();
//				myAudioRecorder.release();
////				myAudioRecorder = null;
////				buttonStop.setEnabled(false);
////				buttonPlay.setEnabled(true);
//				chronometer.stop();
//				
//				textViewStatus.setText("Recording Finished");
//				view.setBackgroundResource(R.drawable.record_button);
//				finish();
//				startActivity(getIntent());
//				
//			}
//			
//		} catch (IllegalStateException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//	
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
//
//	public void play(View view) throws IllegalArgumentException,
//			SecurityException, IllegalStateException, IOException {
//		
//	
//		if(!isPlay){
//		audioPlayer.start();
//		 // handler.postDelayed(moveSeekBarThread, 100);
//			chronometer.start();
//		isPlay=true;
//
//	     
//	        System.out.println("ishti    "+audioPlayer);
//		Toast.makeText(getApplicationContext(), "play", 1000).show();
//		
//		}else{
//			
//			audioPlayer.stop();
//			
//			audioPlayer.prepare();
//			isPlay=false;
//			 System.out.println("ishti    "+audioPlayer);
//			Toast.makeText(getApplicationContext(), "stop", 1000).show();
//		}
//		textViewStatus.setText("Recording Playing ...");
//	}
//
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			if (!audioMode.equals(AppExtra.AUDIO_MODE_PLAY)) {
//				 //handler.removeCallbacks(moveSeekBarThread);
//				try {
//					if (myAudioRecorder != null) {
//						
//						myAudioRecorder.release();
//						myAudioRecorder = null;
//					}
//					if(audioPlayer!=null){
//						
//						audioPlayer.release();
//						audioPlayer=null;
//					}
//					
//				} catch (IllegalStateException e) {
//					e.printStackTrace();
//				}
//				
//			}
//			fileManager.setBackCode(getApplicationContext());
//			finish();
//			overridePendingTransition(R.anim.push_right_out,
//					R.anim.push_right_in);
//			return true;
//		}
//		return super.onKeyDown(keyCode, event);
//	}
//
//	@Override
//	protected void onUserLeaveHint() {
//		fileManager.setHomeCode(getApplicationContext());
//		super.onUserLeaveHint();
//	}
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
			Intent i = new Intent(RecordAudio.this, LoginWindowActivity.class);
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
		//handler.removeCallbacks(moveSeekBarThread);
		if (myAudioRecorder != null) {

			myAudioRecorder.release();
			myAudioRecorder = null;
		}

	}
}