package com.smartmux.banglamusic;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

public class MusicActivity extends Activity {

   public TextView songName,startTimeField,endTimeField;
   private MediaPlayer mediaPlayer;
   private double startTime = 0;
   private double finalTime = 0;
   private Handler myHandler = new Handler();;
   private int forwardTime = 5000; 
   private int backwardTime = 5000;
   private SeekBar seekbar;
   private ImageButton playButton;
   public static int oneTimeOnly = 0;
   private String songTitle;
   private String path;
   private Boolean isPlaying = false;
   
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
      requestWindowFeature(Window.FEATURE_NO_TITLE);
      setContentView(R.layout.player_layout);
      
      Intent intent = getIntent();
  	  songTitle = intent.getStringExtra(MainContainer.SONG_TITLE);
      path = intent.getStringExtra(MainContainer.SONG_PATH);
      
      songName = (TextView)findViewById(R.id.songTitle);
      startTimeField =(TextView)findViewById(R.id.currentDuration);
      endTimeField =(TextView)findViewById(R.id.totalDuration);
      seekbar = (SeekBar)findViewById(R.id.seekBar1);
      playButton = (ImageButton)findViewById(R.id.btnPlay);
      //pauseButton = (ImageButton)findViewById(R.id.imageButton2);
      songName.setText(songTitle);
      mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(path));
      seekbar.setClickable(false);
      //pauseButton.setEnabled(false);

   }

   public void play(View view){
      if(isPlaying){
    	  mediaPlayer.pause();
    	  playButton.setImageResource(R.drawable.btn_play);
    	  isPlaying = false;
      }else{
	      mediaPlayer.start();
	      finalTime = mediaPlayer.getDuration();
	      startTime = mediaPlayer.getCurrentPosition();
	      if(oneTimeOnly == 0){
	         seekbar.setMax((int) finalTime);
	         oneTimeOnly = 1;
	      } 
	
	      endTimeField.setText(String.format("%d:%d", 
	         TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
	         TimeUnit.MILLISECONDS.toSeconds((long) finalTime) - 
	         TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
	         toMinutes((long) finalTime)))
	      );
	      startTimeField.setText(String.format("%d:%d", 
	         TimeUnit.MILLISECONDS.toMinutes((long) startTime),
	         TimeUnit.MILLISECONDS.toSeconds((long) startTime) - 
	         TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
	         toMinutes((long) startTime)))
	      );
	      seekbar.setProgress((int)startTime);
	      myHandler.postDelayed(UpdateSongTime,100);
	      playButton.setImageResource(R.drawable.btn_pause);
	      isPlaying = true;
      }
   }

   private Runnable UpdateSongTime = new Runnable() {
      public void run() {
         startTime = mediaPlayer.getCurrentPosition();
         startTimeField.setText(String.format("%d:%d", 
            TimeUnit.MILLISECONDS.toMinutes((long) startTime),
            TimeUnit.MILLISECONDS.toSeconds((long) startTime) - 
            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
            toMinutes((long) startTime)))
         );
         seekbar.setProgress((int)startTime);
         myHandler.postDelayed(this, 100);
      }
   };
  	
   public void forward(View view){
      int temp = (int)startTime;
      if((temp+forwardTime)<=finalTime){
         startTime = startTime + forwardTime;
         mediaPlayer.seekTo((int) startTime);
      }
      else{
         Toast.makeText(getApplicationContext(), 
         "Cannot jump forward from here", 
         Toast.LENGTH_SHORT).show();
      }

   }
   public void rewind(View view){
      int temp = (int)startTime;
      if((temp-backwardTime)>0){
         startTime = startTime - backwardTime;
         mediaPlayer.seekTo((int) startTime);
      }
      else{
         Toast.makeText(getApplicationContext(), 
         "Cannot jump backward from here",
         Toast.LENGTH_SHORT).show();
      }

   }
  
    @Override
   public void onDestroy(){
       super.onDestroy();
       mediaPlayer.release();
       myHandler.removeCallbacks(UpdateSongTime);
       myHandler = null;
       oneTimeOnly = 0;
       isPlaying=false;
  }

 }
