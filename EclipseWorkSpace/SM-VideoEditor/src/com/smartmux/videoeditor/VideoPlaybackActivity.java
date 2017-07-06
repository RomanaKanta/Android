//package com.smartmux.videoeditor;
//
//import java.util.ArrayList;
//
//import android.annotation.SuppressLint;
//import android.app.Activity;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.Point;
//import android.os.Bundle;
//import android.view.Display;
//import android.view.MotionEvent;
//import android.widget.Toast;
//
//import com.smartmux.videorecorder.video.AdaptiveSurfaceView;
//import com.smartmux.videorecorder.video.PlaybackHandler;
//import com.smartmux.videorecorder.video.VideoPlaybackManager;
//
//
//
//public class VideoPlaybackActivity extends Activity {
//	public static String FileNameArg = "arg_filename";
//	private ArrayList<Bitmap> frames;
////	private HorizoantalListView frameList;
////	private   FrameCustomArrayAdapter adapter;
//	private static String fileName = null;
//	
//	private int width;
//
//
//	
//	@SuppressLint("NewApi") @Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.update_thumb);
//	
//		Intent i = getIntent();
//		if ((i != null) && (i.getExtras() != null)) {
//			fileName = i.getExtras().getString(FileNameArg);
//		}
//		Display display = getWindowManager().getDefaultDisplay();
//		Point size = new Point();
//		display.getSize(size);
//		width = size.x-12;
//		
//		int height = size.y;
//		videoView = (AdaptiveSurfaceView) findViewById(R.id.videoView);
////		 frames=getFrames(fileName);
//			
//		//  frameList=(HorizoantalListView) findViewById(R.id.hlvSimpleList);
//
//		//new LoadFrames().execute();
//		playbackManager = new VideoPlaybackManager(this, videoView, playbackHandler);
//		Toast.makeText(this,fileName, 1000).show();
//		 //String root = Environment.getExternalStorageDirectory().toString();
//	  //String fname = Environment.getExternalStorageDirectory().toString()+"/saved_videos/Video-150_50000.mp4";
//		
//		playbackManager.setupPlayback(fileName);
//		
//		
//	}
////	 @SuppressLint("NewApi") public ArrayList<Bitmap> getFrames(String path){
////		    try {
////		        ArrayList<Bitmap> bArray = new ArrayList<Bitmap>();
////		        bArray.clear();
////		        MediaMetadataRetriever mRetriever = new MediaMetadataRetriever();
////		        mRetriever.setDataSource(path);
////		       
////		        String time = mRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
////		        long timeInmillisec = Long.parseLong( time );
////		        long duration = timeInmillisec / 1000;
////		        long hours = duration / 3600;
////		        long minutes = (duration - hours * 3600) / 60;
////		        long seconds = duration - (hours * 3600 + minutes * 60);
//////		       System.out.println("date"+duration);
////		        // Seek bar
////		       // RangeSeekBar seek=(RangeSeekBar) findViewById(R.id.seek);
////		       // seek.setRangeValues(0, seconds);
////		        for (int i = 1000; i <21000; i=i+1000) {
////		        	  bArray.add(Bitmap.createScaledBitmap(mRetriever.getFrameAtTime(i, MediaMetadataRetriever.OPTION_CLOSEST), width/20, 160, false));
////
////		        }
////		        System.out.println("size "+width);
////		        return bArray;
////		    } catch (Exception e) {
////		        // TODO: handle exception
////
////		        return null;
////
////		    }
////		}
//	 
////		private class LoadFrames extends AsyncTask<Void, Void, Void> {
////
////			private Drawable myIcon;
////			private ProgressDialog progress=null;
////
////			@SuppressLint("NewApi") @Override
////			protected Void doInBackground(Void... arg0) {
////				
////				// TODO Auto-generated method stub
////				
////				 frames=getFrames(fileName);
////				  adapter=new FrameCustomArrayAdapter(getApplicationContext(), frames);
////
////				 
////				return null;
////			}
////
////			@Override
////			protected void onPostExecute(Void result) {
////				// TODO Auto-generated method stub
////				
////				super.onPostExecute(result);
////				 frameList.setAdapter(adapter);
////
////			
////				 progress.dismiss();
////				
////			}
////
////			@Override
////			protected void onPreExecute() {
////				// TODO Auto-generated method stub
////				
////				progress = ProgressDialog.show(VideoPlaybackActivity.this, null,
////						"");
////				super.onPreExecute();
////			}
////
////
////			@Override
////			protected void onProgressUpdate(Void... values) {
////				// TODO Auto-generated method stub
////				super.onProgressUpdate(values);
////			}
////
////			
////			
////			
////		}
//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		playbackManager.showMediaController();
//	    return false;
//	}
//	
//	@Override
//	protected void onPause() {
//		super.onPause();
//		
//		playbackManager.pause();
//		playbackManager.hideMediaController();
//	}
//	
//	@Override
//	protected void onDestroy() {
//		playbackManager.dispose();
//		playbackHandler = null;
//		
//		super.onDestroy();
//	}
//}
