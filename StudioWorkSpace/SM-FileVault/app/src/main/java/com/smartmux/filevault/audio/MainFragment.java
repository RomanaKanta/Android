package com.smartmux.filevault.audio;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Chronometer.OnChronometerTickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.github.lassana.recorder.AudioRecorder;
import com.github.lassana.recorder.AudioRecorderBuilder;
import com.smartmux.filevault.R;
import com.smartmux.filevault.utils.AppExtra;
import com.smartmux.filevault.utils.AppToast;
import com.smartmux.filevault.utils.FileUtil;

import java.io.File;

/**
* @author Nikolai Doronin {@literal <lassana.nd@gmail.com>}
* @since 8/18/13
*/
public class MainFragment extends Fragment {

    private ImageButton mStartButton;
    private ImageButton mPauseButton;
    private Button mPlayButton;
    private String folderName;
	private String subFolderName;
	private String audioMode;
	private String audioFileName;
	private long time=0;
	private long base;

    private String mActiveRecordFileName;

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
	private String outputFile;
	private Chronometer chronometer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	folderName = getArguments().getString(AppExtra.FOLDER_NAME);
		subFolderName = getArguments().getString(AppExtra.SUB_FOLDER_NAME);
		audioMode = getArguments().getString(AppExtra.AUDIO_MODE);
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


//        mAudioRecorder = savedInstanceState == null
//                ? ApplicationClass.getApplication(getActivity()).createRecorder(getNextFileName())
//                : ApplicationClass.getApplication(getActivity()).getRecorder();

        mAudioRecorder = AudioRecorderBuilder.with(getActivity())
                .fileName(getNextFileName())
                .config(AudioRecorder.MediaRecorderConfig.DEFAULT)
                .loggable()
                .build();

        mStartButton = (ImageButton) view.findViewById(R.id.buttonStartRecording);
        mStartButton.setOnClickListener(mOnClickListener);
        mPauseButton = (ImageButton) view.findViewById(R.id.buttonPauseRecording);
        mPauseButton.setOnClickListener(mOnClickListener);
        mPlayButton = (Button) view.findViewById(R.id.buttonPlayRecording);
        mPlayButton.setOnClickListener(mOnClickListener);


        chronometer = (Chronometer) view.findViewById(R.id.chronometer);
		//chronometer.setVisibility(View.VISIBLE);

		chronometer
				.setOnChronometerTickListener(new OnChronometerTickListener() {



					@Override
					public void onChronometerTick(Chronometer cArg) {
						base=cArg.getBase();
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

        //Bundle bundle = this.getArguments();


		//audioFileName = bundle.getString(AppExtra.AUDIO_FILENAME);

        invalidateButtons();
    }

    private String getNextFileName() {

    	outputFile = AppExtra.APP_ROOT_FOLDER + "/" + folderName + "/"
				+ subFolderName + "/" + FileUtil.getDynamicFileName()
				+ ".3gp";
//        return Environment.getExternalStorageDirectory()
//                + File.separator
//                + "Record_"
//                + System.currentTimeMillis()
//                + ".mp4";
    	return outputFile;
    }

    private void invalidateButtons() {
        switch (mAudioRecorder.getStatus()) {
//            case STATUS_UNKNOWN:
////                mStartButton.setEnabled(false);
////                mPauseButton.setEnabled(false);
//                mPlayButton.setEnabled(false);
//            	 mStartButton.setEnabled(false);
//                 mPauseButton.setEnabled(false);
//                break;
            case STATUS_READY_TO_RECORD:
//                mStartButton.setEnabled(true);
//                mPauseButton.setEnabled(false);
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
            @Override
            public void onStarted() {
                invalidateButtons();
                chronometer.setBase(SystemClock.elapsedRealtime() + time);
                chronometer.start();
            }

            @Override
            public void onException(Exception e) {
                invalidateButtons();
                Toast.makeText(getActivity(), getString(R.string.toast_error_audio_recorder, e),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void pause() {
        mAudioRecorder.pause(new AudioRecorder.OnPauseListener() {
            @Override
            public void onPaused(String activeRecordFileName) {
                mActiveRecordFileName = activeRecordFileName;
                Log.d("File",mActiveRecordFileName);
                invalidateButtons();
                time = chronometer.getBase() - SystemClock.elapsedRealtime();
                chronometer.stop();
            }

            @Override
            public void onException(Exception e) {
                invalidateButtons();
//                Toast.makeText(getActivity(), getString(R.string.toast_error_audio_recorder, e),
//                        Toast.LENGTH_SHORT).show();
            }
        });
    }
@Override
public void onDestroy() {
	// TODO Auto-generated method stub
//	 pause();
	super.onDestroy();

	AppToast.show(getActivity(), "Fragment Finished");
}
    private void play() {
        File file = new File(mActiveRecordFileName);
        if ( file.exists() ) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "audio/*");
            startActivity(intent);
        }
    }
}
