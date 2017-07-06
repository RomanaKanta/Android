package com.smartmux.filevault.audio;
//package com.smartmux.filevaultfree.audio;
//
//import java.io.IOException;
//
//import android.annotation.SuppressLint;
//import android.content.Intent;
//import android.media.MediaPlayer;
//import android.media.MediaRecorder;
//import android.os.Bundle;
//import android.view.KeyEvent;
//import android.view.View;
//import android.widget.ImageButton;
//import android.widget.TextView;
//
//import com.smartmux.filevaultfree.AppMainActivity;
//import com.smartmux.filevaultfree.LoginWindowActivity;
//import com.smartmux.filevaultfree.R;
//import com.smartmux.filevaultfree.utils.AppActionBar;
//import com.smartmux.filevaultfree.utils.AppExtra;
//import com.smartmux.filevaultfree.utils.FileManager;
//import com.smartmux.filevaultfree.utils.FileUtil;
//
//@SuppressLint("NewApi")
//public class AudioRecorder extends AppMainActivity {
//
//	private MediaRecorder myAudioRecorder;
//	private String outputFile = null;
//
//	private ImageButton buttonStart;
//	private ImageButton buttonStop;
//	private ImageButton buttonPlay;
//	private TextView textViewStatus;
//
//	private String folderName;
//	private String subFolderName;
//	private String audioMode;
//	private String audioFileName;
//	private FileManager fileManager;
//	private MediaPlayer audioPlayer;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.voice_recorder);
//
//		fileManager = new FileManager();
//		fileManager.setBackCode(getApplicationContext());
//		AppActionBar.updateAppActionBar(getActionBar(), this, true);
//
//		Bundle bundle = getIntent().getExtras();
//
//		folderName = bundle.getString(AppExtra.FOLDER_NAME);
//		subFolderName = bundle.getString(AppExtra.SUB_FOLDER_NAME);
//		audioMode = bundle.getString(AppExtra.AUDIO_MODE);
//		audioFileName = bundle.getString(AppExtra.AUDIO_FILENAME);
//
//		if ((folderName != null) && (subFolderName != null)) {
//			getActionBar().setTitle(folderName + "/" + subFolderName);
//		}
//
//		buttonStart = (ImageButton) findViewById(R.id.buttonRecord);
//		buttonStop = (ImageButton) findViewById(R.id.buttonStop);
//		buttonPlay = (ImageButton) findViewById(R.id.buttonPlay);
//		textViewStatus = (TextView) findViewById(R.id.textViewStatus);
//
//		textViewStatus.setText("Press Record Button");
//
//		buttonStop.setEnabled(false);
//		buttonPlay.setEnabled(false);
//
//		myAudioRecorder = new MediaRecorder();
//		myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//		myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//		myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
//
//		if (audioMode.equals(AppExtra.AUDIO_MODE_PLAY)) {
//			buttonStop.setAlpha(0.0f);
//			buttonStart.setAlpha(0.0f);
//			textViewStatus.setText("Press Play Button");
//			buttonPlay.setEnabled(true);
//			outputFile = AppExtra.APP_ROOT_FOLDER + "/" + folderName + "/"
//					+ subFolderName + "/" + audioFileName;
//			myAudioRecorder.setOutputFile(outputFile);
//		} else {
//			outputFile = AppExtra.APP_ROOT_FOLDER + "/" + folderName + "/"
//					+ subFolderName + "/" + FileUtil.getDynamicFileName()
//					+ ".3gp";
//			myAudioRecorder.setOutputFile(outputFile);
//		}
//
//	}
//
//	public void start(View view) {
//		try {
//			myAudioRecorder.prepare();
//			myAudioRecorder.start();
//		} catch (IllegalStateException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		buttonStart.setEnabled(false);
//		buttonStop.setEnabled(true);
//		textViewStatus.setText("Recording ...");
//	}
//
//	public void stop(View view) {
//		myAudioRecorder.stop();
//		myAudioRecorder.release();
//		myAudioRecorder = null;
//		buttonStop.setEnabled(false);
//		buttonPlay.setEnabled(true);
//		textViewStatus.setText("Recording Finished");
//	}
//
//	public void play(View view) throws IllegalArgumentException,
//			SecurityException, IllegalStateException, IOException {
//		audioPlayer = new MediaPlayer();
//		audioPlayer.setDataSource(outputFile);
//		audioPlayer.prepare();
//		audioPlayer.start();
//		textViewStatus.setText("Recording Playing ...");
//	}
//
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			if (!audioMode.equals(AppExtra.AUDIO_MODE_PLAY)) {
//				try {
//					if (myAudioRecorder != null) {
//						
//						myAudioRecorder.release();
//						myAudioRecorder = null;
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
//
//	@Override
//	protected void onResume() {
//		super.onResume();
//
//		int event_code = fileManager.getReturnCode(getApplicationContext());
//		if (event_code == AppExtra.HOME_CODE) {
//			Intent i = new Intent(AudioRecorder.this, LoginWindowActivity.class);
//			startActivity(i);
//		}
//	}
//
//	@Override
//	public void onDestroy() {
//		super.onDestroy();
//
//		if (audioPlayer != null) {
//
//			audioPlayer.release();
//			audioPlayer = null;
//		}
//
//		if (myAudioRecorder != null) {
//
//			myAudioRecorder.release();
//			myAudioRecorder = null;
//		}
//
//	}
//}