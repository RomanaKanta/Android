package com.smartmux.videoeditor;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Toast;
import android.widget.VideoView;

public class SplashActivity extends Activity implements OnClickListener {

	private static final int ACTION_TAKE_VIDEO = 3;
	private static final int ACTION_PICK_VIDEO = 4;
	private static final String TAG = SplashActivity.class.getSimpleName();
	private VideoView videoView;
	private Uri videoUri;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);
		videoView = (VideoView) findViewById(R.id.videoView_src);
		videoUri = null;
		this.findViewById(R.id.button_capture).setOnClickListener(this);
		this.findViewById(R.id.button_import).setOnClickListener(this);
		this.findViewById(R.id.textView_src).setOnClickListener(this);
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		// this.findViewById(R.id.content_video_fake).setVisibility(View.VISIBLE);

	}



	
	private void dispatchTakeVideoIntent() {
		Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		startActivityForResult(takeVideoIntent, ACTION_TAKE_VIDEO);
	}

	private void dispatchImportVideoIntent() {
		Intent intent = new Intent(Intent.ACTION_PICK, null);
		intent.setType("video/*");
		startActivityForResult(intent, ACTION_PICK_VIDEO);
	}

	@SuppressWarnings("deprecation")
	public String getRealPathFromURI(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };

		Cursor cursor = managedQuery(uri, projection, null, null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	private void handleCameraVideo(Intent intent) {
		
		videoUri = intent.getData();
		// videoView.setVideoURI(videoUri);
		String path = getRealPathFromURI(videoUri);
		//getFrames(path);
		
		Toast.makeText(getApplicationContext(), "Video Selected", 3000).show();	
		
		
		this.findViewById(R.id.content_video_fake).setVisibility(View.GONE);
		
//	
//			rangeSeekBar
//			.setSeekBarChangeListener(new RangeSeekBar.SeekBarChangeListener() {
//				@Override
//				public void SeekBarValueChanged(int leftThumb,
//						int rightThumb) {
//					
//					textViewTrimingStart.setText(Utils
//							.getTimeForTrackFormat(leftThumb, true));
//					textViewTrimingEnd.setText(Utils
//							.getTimeForTrackFormat(rightThumb, true));
//					textViewTrimingTime.setText(Utils
//							.getTimeForTrackFormat(rightThumb - leftThumb, true));
//					if(mTrimEndTime!=rightThumb){
//						
//						mVideoView.seekTo(rightThumb);
//						
//					}else if(mTrimStartTime!=leftThumb){
//						
//						mVideoView.seekTo(leftThumb);
//						
//					}
//						
//					    mTrimStartTime = leftThumb;
//				        mTrimEndTime = rightThumb;
//				     //   setProgress();
//				}
//			});
//		
		
		
		// videoView.start();
		// videoView.setOnPreparedListener(new OnPreparedListener() {
		// @Override
		// public void onPrepared(MediaPlayer mp) {
		//
		// mp.setOnVideoSizeChangedListener(new OnVideoSizeChangedListener() {
		// @Override
		// public void onVideoSizeChanged(MediaPlayer mp, int width, int height)
		// {
		// /*
		// * add media controller
		// */
		// MediaController mediaController = new
		// MediaController(SplashActivity.this);
		// videoView.setMediaController(mediaController);
		// /*
		// * and set its position on screen
		// */
		// mediaController.setAnchorView(videoView);
		// }
		// });
		// }
		// });
	}
	
	
//	
//	long start,end;
//	private class LoadFrames extends AsyncTask<String, Void, List<Bitmap>> {
//		MediaPlayer mp;
//
//		@SuppressLint("NewApi") @Override
//		protected List<Bitmap> doInBackground(String... arg0) {
//			
//			
//			List<Bitmap>  listOfBitmap = new ArrayList<Bitmap>();
//				 mp = MediaPlayer.create(getBaseContext(), videoUri);
//				int millis = mp.getDuration();
//				mp.release();
//				
//				String video = arg0[0];	
//				
//				 MediaMetadataRetriever mRetriever = new MediaMetadataRetriever();
//				 mRetriever.setDataSource(video);
//				start = System.currentTimeMillis();
//				
//				int FRAME_BYTES=(millis*1000)/10;  
//				int FRAMESMAX=11;  
//				
//				for(int currentFrame=1;currentFrame<FRAMESMAX; currentFrame++){          
//						//listOfBitmap.add(mFrameGrabber.getFrameAtTime(FRAME_BYTES*currentFrame));
//					Bitmap bmp = mRetriever.getFrameAtTime(FRAME_BYTES*currentFrame, MediaMetadataRetriever.OPTION_CLOSEST);
//					if(bmp != null){
//						listOfBitmap.add(Bitmap.createScaledBitmap(bmp, 110, 110, false));
//					}
//					
//				}
//			end = System.currentTimeMillis();
//			System.out.println("Total duration ms" + millis);
//			System.out.println("duration: "+( end - start));
//			
//			System.out.println(video);
//			return listOfBitmap;
//		}
//
//		@Override
//		protected void onPostExecute(List<Bitmap> result) {
//			// TODO Auto-generated method stub
//			
//			super.onPostExecute(result);
//		
////			HorizoantalListView videoListView = (HorizoantalListView) findViewById(R.id.hlvSimpleList);
////
////			VideoFrameAdapter imageAdapter = new VideoFrameAdapter(SplashActivity.this,result);
////			videoListView.setAdapter(imageAdapter);
//			
//
//
//			rangeSeekBar.setVisibility(View.VISIBLE);
//	        // Set the range
//	        rangeSeekBar.setRangeValues(0, 100);
//	        rangeSeekBar.setSelectedMinValue(0);
//	        rangeSeekBar.setSelectedMaxValue(100);
////	        rangeSeekBar.setMaxValue(mp.getDuration());
//	        rangeSeekBar.getBitmapList( getCombinedBitmap( result),width);
//	        
//	    	mProgressbar.setVisibility(View.INVISIBLE);
//	        
//		}
//
//		   public Bitmap getCombinedBitmap(List<Bitmap>  list) {
//			   Bitmap drawnBitmap = null;
//		         width = 110*list.size();
//		         float left = 0;
//		         float top=0;
//
//		        try {
//		            drawnBitmap = Bitmap.createBitmap(width, 120, Config.ARGB_8888);
//
//		            Canvas canvas = new Canvas(drawnBitmap);
//		            // JUST CHANGE TO DIFFERENT Bitmaps and coordinates .
//		            for (int i = 0; i < list.size(); i++) {
//		          	 		            	
//		              canvas.drawBitmap(list.get(i), left, top, null);
//		                
//		                 left=left+110;
//		                 top=0;
//		            }
//
//		        }
//		        catch (Exception e) {
//		            e.printStackTrace();
//		        }
//		        
//		        BitmapClass.setBp(drawnBitmap);
//		        return drawnBitmap;
//		    }
//		
//	}
//

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {

		case ACTION_TAKE_VIDEO: {
			if (resultCode == RESULT_OK) {
				handleCameraVideo(data);

			}
			break;
		} // ACTION_TAKE_VIDEO
		case ACTION_PICK_VIDEO: {
			if (resultCode == RESULT_OK) {
				handleCameraVideo(data);

			}
			break;

		} // ACT

		} // switch
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splash, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_next) {

			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {

		int id = v.getId();
		if (id == R.id.button_capture) {

			dispatchTakeVideoIntent();
		} else if (id == R.id.button_import) {

			dispatchImportVideoIntent();
		} else if (id == R.id.textView_src) {

			if (videoUri != null) {
				Intent intent = new Intent(SplashActivity.this,
						VideoTrimActivity.class);
				intent.putExtra("path", getRealPathFromURI(videoUri));

				startActivity(intent);

			} else {

				Toast.makeText(getApplicationContext(),
						"Please pick or import any video file!!",
						Toast.LENGTH_SHORT).show();
			}

		}

	}

}
