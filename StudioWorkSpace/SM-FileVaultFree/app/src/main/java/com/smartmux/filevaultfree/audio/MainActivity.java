package com.smartmux.filevaultfree.audio;

//
//import android.app.FragmentTransaction;
//import android.os.Bundle;
//import android.support.v4.app.FragmentActivity;
//import android.util.Log;
//import android.view.KeyEvent;
//
//import com.smartmux.filevaultfree.R;
//import com.smartmux.filevaultfree.utils.AppActionBar;
//import com.smartmux.filevaultfree.utils.AppExtra;
//import com.smartmux.filevaultfree.utils.FileManager;
//
//
///**
// * @author Nikolai Doronin {@literal <lassana.nd@gmail.com>}
// * @since 8/18/13
// */
//public class MainActivity extends FragmentActivity {
//
//	FileManager fileManager;
//	private String folderName;
//	private String subFolderName;
//	private String audioMode;
//	private String audioFileName;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);

//		
//    }
//    
//    
//    @Override
//    protected void onResume() {
//    	// TODO Auto-generated method stub
//    	super.onResume();
//		
//    }
//    @Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			fileManager.setBackCode(getApplicationContext());
//			finish();
//			overridePendingTransition(R.anim.push_right_out,
//					R.anim.push_right_in);
//			return true;
//		}
//		return super.onKeyDown(keyCode, event);
//	}
//    @Override
//	protected void onUserLeaveHint() {
//		fileManager.setHomeCode(getApplicationContext());
//		super.onUserLeaveHint();
//	}
//    

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Chronometer.OnChronometerTickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lassana.recorder.AudioRecorder;
import com.github.lassana.recorder.AudioRecorderBuilder;
import com.smartmux.filevaultfree.AppMainActivity;
import com.smartmux.filevaultfree.R;
import com.smartmux.filevaultfree.utils.AppActionBar;
import com.smartmux.filevaultfree.utils.AppExtra;
import com.smartmux.filevaultfree.utils.FileManager;
import com.smartmux.filevaultfree.utils.FileUtil;

import java.io.File;

/**
 * @author Nikolai Doronin {@literal <lassana.nd@gmail.com>}
 * @since 8/18/13
 */
public class MainActivity extends AppMainActivity {

	private ImageButton mStartButton;
	private ImageButton mPauseButton;
	private Button mPlayButton;
	private String folderName;
	private String subFolderName;
	private String audioMode;
	private String audioFileName;
	private long time = 0;
	private long base;
	private String outputFile;
	private Chronometer chronometer;
	private FileManager fileManager;
	private String mActiveRecordFileName;
	private boolean isAlreadyPaused = false;
	private AudioRecorder mAudioRecorder;

	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.buttonStartRecording:
				start();
				break;
			case R.id.buttonPauseRecording:
				pause();
				break;
			case R.id.buttonPlayRecording:
				play();
				break;
			default:
				break;
			}
		}
	};
	private TextView mRecordStatus;
	private TextView headerTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_main);
		fileManager = new FileManager();
		AppActionBar.changeActionBarFont(getApplicationContext(),
				MainActivity.this);
		AppActionBar.updateAppActionBar(getActionBar(), this, true);
		getActionBar().setTitle("Record");
		Bundle bundle = getIntent().getExtras();

		folderName = bundle.getString(AppExtra.FOLDER_NAME);
		subFolderName = bundle.getString(AppExtra.SUB_FOLDER_NAME);
		audioMode = bundle.getString(AppExtra.AUDIO_MODE);

//		mAudioRecorder = savedInstanceState == null ? AppClass.getApplication(
//				getApplicationContext()).createRecorder(getNextFileName())
//				: AppClass.getApplication(getApplicationContext())
//						.getRecorder();

        mAudioRecorder = AudioRecorderBuilder.with(getApplicationContext())
                .fileName(getNextFileName())
                .config(AudioRecorder.MediaRecorderConfig.DEFAULT)
                .loggable()
                .build();

		mStartButton = (ImageButton) findViewById(R.id.buttonStartRecording);
		mStartButton.setOnClickListener(mOnClickListener);
		mPauseButton = (ImageButton) findViewById(R.id.buttonPauseRecording);
		mPauseButton.setOnClickListener(mOnClickListener);
		mPlayButton = (Button) findViewById(R.id.buttonPlayRecording);
		mPlayButton.setOnClickListener(mOnClickListener);
		mRecordStatus = (TextView) findViewById(R.id.recording_status);
		chronometer = (Chronometer) findViewById(R.id.chronometer);

		chronometer
				.setOnChronometerTickListener(new OnChronometerTickListener() {

					@Override
					public void onChronometerTick(Chronometer cArg) {
						base = cArg.getBase();
						time = SystemClock.elapsedRealtime() - cArg.getBase();
						int h = (int) (time / 3600000);
						int m = (int) (time - h * 3600000) / 60000;
						int s = (int) (time - h * 3600000 - m * 60000) / 1000;
						String hh = h < 10 ? "0" + h : h + "";
						String mm = m < 10 ? "0" + m : m + "";
						String ss = s < 10 ? "0" + s : s + "";
						cArg.setText(mm + ":" + ss);
					}
				});

		invalidateButtons();
	}

	private String getNextFileName() {

		outputFile = AppExtra.APP_ROOT_FOLDER + "/" + folderName + "/"
				+ subFolderName + "/" + FileUtil.getDynamicFileName() + ".3gp";
		// return Environment.getExternalStorageDirectory()
		// + File.separator
		// + "Record_"
		// + System.currentTimeMillis()
		// + ".mp4";
		return outputFile;
	}

	private void invalidateButtons() {
		switch (mAudioRecorder.getStatus()) {
		// case STATUS_UNKNOWN:
		// // mStartButton.setEnabled(false);
		// // mPauseButton.setEnabled(false);
		// mPlayButton.setEnabled(false);
		// mStartButton.setEnabled(false);
		// mPauseButton.setEnabled(false);
		// break;
		case STATUS_READY_TO_RECORD:
			// mStartButton.setEnabled(true);
			// mPauseButton.setEnabled(false);
			mPlayButton.setEnabled(false);

			mStartButton.setVisibility(View.VISIBLE);
			mPauseButton.setVisibility(View.INVISIBLE);
			break;
		case STATUS_RECORDING:
			mStartButton.setVisibility(View.INVISIBLE);
			mPauseButton.setVisibility(View.VISIBLE);
			mPlayButton.setEnabled(false);
			break;
		case STATUS_RECORD_PAUSED:
			mStartButton.setVisibility(View.VISIBLE);
			mPauseButton.setVisibility(View.INVISIBLE);
			mPlayButton.setEnabled(true);
			break;
		default:
			break;
		}
	}

	private void start() {
		mAudioRecorder.start(new AudioRecorder.OnStartListener() {
			@SuppressLint("ResourceAsColor")
			@Override
			public void onStarted() {
				mRecordStatus.setVisibility(View.VISIBLE);
				mRecordStatus.setText("Recording......");
				mRecordStatus.setTextColor(Color.parseColor("#EE000E"));
				invalidateButtons();
				chronometer.setBase(SystemClock.elapsedRealtime() + time);
				chronometer.start();
			}

			@Override
			public void onException(Exception e) {
				invalidateButtons();
				Toast.makeText(getApplicationContext(),
						getString(R.string.toast_error_audio_recorder, e),
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	@SuppressLint("ResourceAsColor")
	private void pause() {
		mAudioRecorder.pause(new AudioRecorder.OnPauseListener() {
			@Override
			public void onPaused(String activeRecordFileName) {
				mActiveRecordFileName = activeRecordFileName;
				Log.d("File", mActiveRecordFileName);
				invalidateButtons();
				time = chronometer.getBase() - SystemClock.elapsedRealtime();
				chronometer.stop();
				isAlreadyPaused = true;

				mRecordStatus.setText(R.string.tap_status);
				mRecordStatus.setTextColor(R.color.gray);
			}

			@Override
			public void onException(Exception e) {
				invalidateButtons();
				Toast.makeText(getApplicationContext(),
						getString(R.string.toast_error_audio_recorder, e),
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		// pause();
		super.onDestroy();

		// AppToast.show(getApplicationContext(), "Fragment Finished");
	}

	private void play() {
		File file = new File(mActiveRecordFileName);
		if (file.exists()) {
			if (!audioMode.equals(AppExtra.AUDIO_MODE_PLAY)) {
				try {
					if (mAudioRecorder != null) {

						mAudioRecorder = null;
					}

				} catch (IllegalStateException e) {
					e.printStackTrace();
				}

			}
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(file), "audio/*");
			startActivity(intent);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			fileManager.setBackCode(getApplicationContext());

			if (mAudioRecorder.isRecording()) {

				pause();
//				LayoutInflater layoutInflater = null;
//				View contentView = getLayoutInflater().inflate(
//						R.layout.alert_content, null);
//				Holder holder = new ViewHolder(contentView);
//				TextView alertText = (TextView) contentView
//						.findViewById(R.id.alert_text);
//				alertText.setText(R.string.record_dialog);
//				DialogPlus dialogPlus = DialogPlus
//						.newDialog(this)
//						.setContentHolder(holder)
//						.setGravity(Gravity.CENTER)
//						.setHeader(R.layout.header_alert)
//
//						// .setAdapter(null)
//						// .setOnItemClickListener(itemClickListener)
//						.setOnClickListener(
//								new com.orhanobut.dialogplus.OnClickListener() {
//
//									private String newName;
//
//									@Override
//									public void onClick(DialogPlus dialog,
//											View view) {
//
//										// TODO Auto-generated method stub
//										switch (view.getId()) {
//
//										case R.id.yes_button:
//											finish();
//											overridePendingTransition(R.anim.push_right_out,
//													R.anim.push_right_in);
//											// pause();
//											dialog.dismiss();
//
//										case R.id.no_button:
//
//											dialog.dismiss();
//
//										}
//
//									}
//								})
//						.setOnDismissListener(new OnDismissListener() {
//							@Override
//							public void onDismiss(DialogPlus dialog) {
//							}
//						}).setOnBackPressListener(new OnBackPressListener() {
//							@Override
//							public void onBackPressed(DialogPlus dialog) {
//
//							}
//						}).setCancelable(true).create();
//				View headerView = dialogPlus.getHeaderView();
//				headerTitle = (TextView) headerView
//						.findViewById(R.id.header_title);
//				headerTitle.setText("Alert !!");
//				dialogPlus.show();

			} 
				finish();
				overridePendingTransition(R.anim.push_right_out,
						R.anim.push_right_in);
			
		}
		return true;

	}

	@Override
	protected void onUserLeaveHint() {
		fileManager.setHomeCode(getApplicationContext());
		super.onUserLeaveHint();
	}

}
