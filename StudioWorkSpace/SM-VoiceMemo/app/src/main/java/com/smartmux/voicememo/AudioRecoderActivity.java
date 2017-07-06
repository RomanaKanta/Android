package com.smartmux.voicememo;

import java.io.IOException;

import org.apache.commons.io.FilenameUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.MediaPlayer;
import android.media.MediaRecorder.AudioSource;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.smartmux.voicememo.utils.AppActionBar;
import com.smartmux.voicememo.utils.AppExtra;
import com.smartmux.voicememo.utils.FileManager;
import com.smartmux.voicememo.utils.FileUtil;

public class AudioRecoderActivity extends AppMainActivity {

	private String outputFile = null;
	private ToggleButton buttonStart;
	private ToggleButton buttonPlay;
	private TextView textViewStatus;
	private String audioMode;
	private String audioFileName;
	private FileManager fileManager;
	AudioRecorder ar;
	boolean sdIsMounted;
	MediaPlayer mediaPlayer;
	TelephonyManager telephonyManager;
	boolean isPhone;
	PhoneStateListener callEventListener;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.audio_record);

		fileManager = new FileManager();
		fileManager.setBackCode(getApplicationContext());
		AppActionBar.updateAppActionBar(getActionBar(), this, true);

		PackageManager pm = getPackageManager();
		isPhone = pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY);

		Bundle bundle = getIntent().getExtras();

		audioMode = bundle.getString(AppExtra.AUDIO_MODE);
		audioFileName = bundle.getString(AppExtra.AUDIO_FILENAME);

		buttonStart = (ToggleButton) findViewById(R.id.buttonRecord);
		buttonPlay = (ToggleButton) findViewById(R.id.buttonPlay);
		textViewStatus = (TextView) findViewById(R.id.textViewStatus);

		textViewStatus.setText("Press Record Button");
		buttonPlay.setEnabled(false);
		
		if (audioMode.equals(AppExtra.AUDIO_MODE_PLAY)) {
			

			buttonPlay.setVisibility(View.VISIBLE);
			buttonStart.setVisibility(View.INVISIBLE);
			textViewStatus.setText("Press Play Button");
			buttonPlay.setEnabled(true);
			getActionBar().setTitle(
					FilenameUtils.removeExtension(audioFileName));

			outputFile = AppExtra.APP_ROOT_FOLDER + "/" + audioFileName;


		} else {
			outputFile = AppExtra.APP_ROOT_FOLDER + "/"
					+ FileUtil.getDynamicFileName() + ".3gp";
		}

		if (isPhone) {
			callEventListener = new PhoneStateListener() {

				@Override
				public void onCallStateChanged(int state, String incomingNumber) {
					super.onCallStateChanged(state, incomingNumber);
					// if (state == TelephonyManager.CALL_STATE_IDLE){
					// //....
					// }
					if (state == TelephonyManager.CALL_STATE_RINGING) {
						if (ar != null) {
							if (ar.getState() == AudioRecorder.State.RECORDING) {
								stopRecording();
								buttonStart.setChecked(false);
							}
							if (ar != null) {

								if (mediaPlayer.isPlaying()) {
									pauseAudio();
									buttonPlay.setChecked(false);
								}
							}

						}
					}
				}
			};
			telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			telephonyManager.listen(callEventListener,
					PhoneStateListener.LISTEN_CALL_STATE);
		}

	}
	
	public void start(View view) {
	
	
	//buttonStop.setEnabled(true);
	buttonPlay.setVisibility(View.VISIBLE);
	
	try {
	if(buttonStart.isChecked()){
		
		buttonPlay.setEnabled(true);
		textViewStatus.setText("Recording Finished");
		buttonStart.setEnabled(false);
		stopRecording();
			
		
	}else{
		
		textViewStatus.setText("Recording ...");
		startRecording() ;
	}
	
	} catch (IllegalStateException e) {
		e.printStackTrace();
	}
}

	public void startRecording() {

		ar = new AudioRecorder(false, AudioSource.MIC, 8000,
				AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);

		try {
			ar.setOutputFile(outputFile);
			ar.prepare();
			ar.start();
		} catch (Exception e) {
			Log.e("record: ", e.getMessage());
		}
	}

	public void stopRecording() {
		ar.stop();
		ar.release();

	}

	public void play(View view) throws IllegalArgumentException,
			SecurityException, IllegalStateException, IOException {

		if (buttonPlay.isChecked()) {

			playAudio();
			textViewStatus.setText("Recording Playing ...");

		} else {

			pauseAudio();
			textViewStatus.setText("Recording Pause ...");
		}

	}

	public void playAudio() {

		try {
			mediaPlayer = new MediaPlayer();
			mediaPlayer.reset();
			mediaPlayer.setDataSource(outputFile);
			mediaPlayer.prepare();
			mediaPlayer.start();
			buttonPlay.setChecked(true);

		} catch (IOException e) {
			Log.e(getString(R.string.app_name), e.getMessage());
		}
	}

	public void pauseAudio() {

		mediaPlayer.pause();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			fileManager.setBackCode(getApplicationContext());
			
			if (ar != null) {
				if (ar.getState() == AudioRecorder.State.RECORDING) {
					stopRecording();
				}
			}
			if (mediaPlayer != null) {

				if(mediaPlayer.isPlaying()){
					
					mediaPlayer.stop();
					
				}
				mediaPlayer.release();
				mediaPlayer = null;
			}

			if (isPhone) {
				telephonyManager.listen(callEventListener,
						PhoneStateListener.LISTEN_NONE);
			}
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
	protected void onResume() {
		super.onResume();

		int event_code = fileManager.getReturnCode(getApplicationContext());
		if (event_code == AppExtra.HOME_CODE) {
			Intent i = new Intent(AudioRecoderActivity.this,
					LoginWindowActivity.class);
			startActivity(i);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (ar != null) {
			if (ar.getState() == AudioRecorder.State.RECORDING) {
				stopRecording();
			}
		}
		if (mediaPlayer != null) {

			if(mediaPlayer.isPlaying()){
				
				mediaPlayer.stop();
				
			}
			mediaPlayer.release();
			mediaPlayer = null;
		}

		if (isPhone) {
			telephonyManager.listen(callEventListener,
					PhoneStateListener.LISTEN_NONE);
		}

	}

	public void updateFreeSpace() {
		if (sdIsMounted) {
			StatFs stat = new StatFs(Environment.getExternalStorageDirectory()
					.getPath());
			double availableSize = (double) stat.getAvailableBlocks()
					* (double) stat.getBlockSize();
			// One binary gigabyte equals 1,073,741,824 bytes.
			// double gigaAvailable = sdAvailSize / 1073741824;
			double mbAvailable = availableSize / 1048576;

		}

	}

}
