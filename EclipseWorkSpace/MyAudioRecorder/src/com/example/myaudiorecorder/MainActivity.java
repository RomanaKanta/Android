package com.example.myaudiorecorder;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	TextView text;
	MediaRecorder recorder;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
	

		text = (TextView) findViewById(R.id.textView1);
		

		findViewById(R.id.start).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				startRecording();
				
				text.setText("recording");
			}
		});
		
		
		findViewById(R.id.pause).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
					
				  stopRecording();
				text.setText("pause");
			}
		});
		
		
		findViewById(R.id.save).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				text.setText("saving");
			}
		});

	}

	private void startRecording() {
	    recorder = new MediaRecorder();
	    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
	    recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
	    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
	    recorder.setOutputFile(getTemporaryFileName());
	    recorder.setOnErrorListener(errorListener);
	    recorder.setOnInfoListener(infoListener);
	    try {
	        recorder.prepare();
	        recorder.start();
	    } catch (IllegalStateException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

	private void stopRecording() {
	    if (null != recorder) {
	        recorder.stop();
	        recorder.reset();
	        recorder.release();
	        recorder = null;
	    }
	}
	
	
  private String getTemporaryFileName() {
		  
		  return  Environment.getExternalStorageDirectory()  + File.separator +  "MyRecoder" + File.separator + "TempFile" ;
		  
//	        return getApplicationContext().getCacheDir().getAbsolutePath() + File.separator + "tmprecord";
	    }
  private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
	    @Override
	    public void onError(MediaRecorder mr, int what, int extra) {
	        Toast.makeText(MainActivity.this, "Error: " + what + ", " + extra, Toast.LENGTH_SHORT).show();
	    }
	};

	private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
	    @Override
	    public void onInfo(MediaRecorder mr, int what, int extra) {
	        Toast.makeText(MainActivity.this, "Warning: " + what + ", " + extra, Toast.LENGTH_SHORT).show();
	    }
	};
}
