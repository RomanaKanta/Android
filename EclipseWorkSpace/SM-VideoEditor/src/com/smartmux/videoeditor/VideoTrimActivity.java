package com.smartmux.videoeditor;

import java.util.ArrayList;
import java.util.List;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.smartmux.seekbar.VideoSliceSeekBar;
import com.smartmux.videoeditor.utils.BitmapClass;
import com.smartmux.videoeditor.utils.Utils;
import com.smartmux.wiget.CircularProgressBar;
import com.smartmux.wiget.CircularProgressDrawable;
import com.smartmux.wiget.CustomVideoView;

public class VideoTrimActivity extends Activity {

	private static final String TAG = VideoTrimActivity.class.getSimpleName();

	private CustomVideoView mVideoView;
	private String mPath;
	private String outFilePath;
	FFmpeg ffmpeg;
	LinearLayout mTimeLayer = null;
	TextView textViewTrimingStart, textViewTrimingEnd, textViewTrimingTime;
	VideoSliceSeekBar videoSliceSeekBar;
	CircularProgressBar progressBar;
	ProgressBar mProgressBar = null;
	ImageView videoControlBtn;
	private int mTrimStartTime = 0;
	private int mTrimEndTime = 0;
	private int mVideoPosition = 0;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tmp);
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
		mTimeLayer = (LinearLayout) findViewById(R.id.layout_time);

		mVideoView = (CustomVideoView) findViewById(R.id.videoView_trim);
		mPath = getIntent().getExtras().getString("path", "");

		textViewTrimingStart = (TextView) findViewById(R.id.textView_start);
		textViewTrimingEnd = (TextView) findViewById(R.id.textView_end);
		textViewTrimingTime = (TextView) findViewById(R.id.textView_triming_time);
		progressBar = (CircularProgressBar) findViewById(R.id.progressbar_circular);
		// progressBarIndeterminate.setVisibility(View.INVISIBLE);
		videoSliceSeekBar = (VideoSliceSeekBar) findViewById(R.id.seek_bar);

		videoControlBtn = (ImageView) findViewById(R.id.video_control_btn);
		if (!mPath.equals("")) {

			if (Utils.isDirectoryExists()) {

				outFilePath = Utils.setVideoFileName();
			}

			System.out.println(mPath);

			// #########################//
			handleCameraVideo(mPath);
			// ##########################//

			ffmpeg = FFmpeg.getInstance(this);
			loadFFMpegBinary();

		}
		mVideoView
				.setPlayPauseListener(new CustomVideoView.PlayPauseListener() {

					@Override
					public void onPlay() {
						System.out.println("Play!");
						// videoControlBtn.setVisibility(View.INVISIBLE);
					}

					@Override
					public void onPause() {
						System.out.println("Pause!");
						videoControlBtn
								.setImageResource(R.drawable.ic_vidcontrol_play);
						ObjectAnimator anim = ObjectAnimator.ofFloat(
								videoControlBtn, "alpha", 1f, 0f);
						anim.setDuration(450);
						anim.start();
						anim.addListener(new AnimatorListener() {
							@Override
							public void onAnimationStart(Animator animation) {
							}

							@Override
							public void onAnimationEnd(Animator animation) {
								videoControlBtn.setVisibility(View.VISIBLE);
							}

							@Override
							public void onAnimationCancel(Animator animation) {
								videoControlBtn.setVisibility(View.VISIBLE);
							}

							@Override
							public void onAnimationRepeat(Animator animation) {
							}
						});

					}
				});
		// performVideoViewClick();

		findViewById(R.id.content_video).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {

						performVideoViewClick();
					}
				});

		this.findViewById(R.id.textView_trim).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						String timeStart = "00:"
								+ textViewTrimingStart.getText().toString()
								+ ".0";
						String timeEnd = "00:"
								+ textViewTrimingEnd.getText().toString()
								+ ".0";
						String command = "-i " + mPath + " -ss " + timeStart
								+ " -c copy -t " + timeEnd + " " + outFilePath;
						execFFmpegBinary(command);
					}
				});

	}

	@Override
	protected void onResume() {
		super.onResume();
		mVideoView.seekTo(0);
		// mVideoView.pause();
		videoControlBtn.setImageResource(R.drawable.ic_vidcontrol_play);
	}

	Uri mVideoUri = null;

	private void handleCameraVideo(String path) {

		mProgressBar.setVisibility(View.VISIBLE);
		mVideoUri = Uri.parse(path);

		// ###########################//
		new LoadFrames().execute(mPath);
		// ###########################//
		//
		// mVideoView.setVideoURI(mVideoUri);
		//
		// mVideoView.setOnPreparedListener(new OnPreparedListener() {
		//
		// @Override
		// public void onPrepared(MediaPlayer mp) {
		//
		// videoSliceSeekBar
		// .setSeekBarChangeListener(new
		// VideoSliceSeekBar.SeekBarChangeListener() {
		// @Override
		// public void SeekBarValueChanged(int leftThumb,
		// int rightThumb) {
		// if (mVideoView.isPlaying()) mVideoView.pause();
		// textViewTrimingStart.setText(Utils
		// .getTimeForTrackFormat(leftThumb, true));
		// textViewTrimingEnd.setText(Utils
		// .getTimeForTrackFormat(rightThumb, true));
		// textViewTrimingTime.setText(Utils
		// .getTimeForTrackFormat(rightThumb - leftThumb, true));
		// if(mTrimEndTime!=rightThumb){
		//
		// mVideoView.seekTo(rightThumb);
		//
		// }else if(mTrimStartTime!=leftThumb){
		//
		// mVideoView.seekTo(leftThumb);
		//
		// }
		//
		// mTrimStartTime = leftThumb;
		// mTrimEndTime = rightThumb;
		// // setProgress();
		// }
		// });
		// videoSliceSeekBar.setVisibility(View.VISIBLE);
		// videoSliceSeekBar.getBitmapList(bmp);
		// videoSliceSeekBar.setMaxValue(mp.getDuration());
		// videoSliceSeekBar.setLeftProgress(0);
		// videoSliceSeekBar.setRightProgress(mp.getDuration());
		// videoSliceSeekBar.setProgressMinDiff(mp.getDuration() / 10);
		//
		//
		// videoControlBtn.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// performVideoViewClick();
		// }
		// });
		//
		// mp.setOnVideoSizeChangedListener(new OnVideoSizeChangedListener() {
		// @Override
		// public void onVideoSizeChanged(MediaPlayer mp, int width,
		// int height) {
		// }
		// });
		// }
		// });
	}

	private int setProgress() {
		mVideoPosition = mVideoView.getCurrentPosition();
		// If the video position is smaller than the starting point of trimming,
		// correct it.
		if (mVideoPosition < mTrimStartTime) {
			mVideoView.seekTo(mTrimStartTime);
			mVideoPosition = mTrimStartTime;
		}
		// If the position is bigger than the end point of trimming, show the
		// replay button and pause.
		if (mVideoPosition >= mTrimEndTime && mTrimEndTime > 0) {
			if (mVideoPosition > mTrimEndTime) {
				mVideoView.seekTo(mTrimEndTime);
				mVideoPosition = mTrimEndTime;
			}
			// mController.showEnded();
			mVideoView.pause();
		}

		int duration = mVideoView.getDuration();
		if (duration > 0 && mTrimEndTime == 0) {
			mTrimEndTime = duration;
		}
		Log.d("setTimes", mVideoPosition + " " + duration + " "
				+ mTrimStartTime + " " + mTrimEndTime);
		// mController.setTimes(mVideoPosition, duration, mTrimStartTime,
		// mTrimEndTime);
		// // Enable save if there's modifications
		// mSaveVideoTextView.setEnabled(isModified());
		return mVideoPosition;
	}

	private void performVideoViewClick() {
		videoControlBtn.setVisibility(View.VISIBLE);
		if (mVideoView.isPlaying()) {
			videoControlBtn.setImageResource(R.drawable.ic_vidcontrol_play);
			mVideoView.pause();
			videoSliceSeekBar.setSliceBlocked(false);
			// videoSliceSeekBar.removeVideoStatusThumb();
		} else {
			videoControlBtn.setImageResource(R.drawable.ic_vidcontrol_pause);
			mVideoView.seekTo(videoSliceSeekBar.getLeftProgress());
			mVideoView.start();
			videoSliceSeekBar.setSliceBlocked(false);
			videoSliceSeekBar.videoPlayingProgress(videoSliceSeekBar
					.getLeftProgress());
			videoStateObserver.startVideoProgressObserving();
		}

		ObjectAnimator anim = ObjectAnimator.ofFloat(videoControlBtn, "alpha",
				1f, 0f);
		anim.setDuration(200);
		anim.start();
		anim.addListener(new AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				videoControlBtn.setVisibility(View.INVISIBLE);
			}

			@Override
			public void onAnimationCancel(Animator animation) {
				videoControlBtn.setVisibility(View.INVISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
			}
		});
	}

	//

	private StateObserver videoStateObserver = new StateObserver();

	private class StateObserver extends Handler {

		private boolean alreadyStarted = false;

		private void startVideoProgressObserving() {
			if (!alreadyStarted) {
				alreadyStarted = true;
				sendEmptyMessage(0);
			}
		}

		private Runnable observerWork = new Runnable() {
			@Override
			public void run() {
				startVideoProgressObserving();
			}
		};

		@Override
		public void handleMessage(Message msg) {
			alreadyStarted = false;
			videoSliceSeekBar.videoPlayingProgress(mVideoView
					.getCurrentPosition());
			if (mVideoView.isPlaying()
					&& mVideoView.getCurrentPosition() < videoSliceSeekBar
							.getRightProgress()) {
				postDelayed(observerWork, 50);
				// videoControlBtn.setBackgroundResource(R.drawable.ic_vidcontrol_pause);
			} else {

				if (mVideoView.isPlaying())
					mVideoView.pause();
				// videoControlBtn.setBackgroundResource(R.drawable.ic_vidcontrol_reload);
				// videoControlBtn.setBackgroundResource(R.drawable.ic_vidcontrol_play);
				videoSliceSeekBar.setSliceBlocked(false);
				// videoSliceSeekBar.removeVideoStatusThumb();
			}
		}
	}

	private void loadFFMpegBinary() {
		try {
			ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
				@Override
				public void onFailure() {
					// showUnsupportedExceptionDialog();
				}
			});
		} catch (FFmpegNotSupportedException e) {
			// showUnsupportedExceptionDialog();
		}
	}

	private void execFFmpegBinary(final String command) {
		try {
			ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {
				@Override
				public void onFailure(String s) {
					// addTextViewToLayout("FAILED with output : "+s);
					Log.d(TAG, "onFailure  :  " + s);
				}

				@Override
				public void onSuccess(String s) {
					// addTextViewToLayout("SUCCESS with output : "+s);
					Log.d(TAG, " onSuccess : ffmpeg " + s);
				}

				@Override
				public void onProgress(String s) {
					Log.d(TAG, "onProgress  :  " + s);
					// addTextViewToLayout("progress : "+s);
					// progressDialog.setMessage("Processing\n"+s);
				}

				@Override
				public void onStart() {
					// outputLayout.removeAllViews();
					// progressBarIndeterminate.setVisibility(View.VISIBLE);
					Log.d(TAG, "Started command : ffmpeg " + command);
					// progressDialog.setMessage("Processing...");
					// progressDialog.show();
					progressBar.setVisibility(View.VISIBLE);
					((CircularProgressDrawable) progressBar
							.getIndeterminateDrawable()).start();
				}

				@Override
				public void onFinish() {
					Log.d(TAG, "Finished command : ffmpeg " + command);
					// progressDialog.dismiss();
					// progressBarIndeterminate.setVisibility(View.INVISIBLE);

					((CircularProgressDrawable) progressBar
							.getIndeterminateDrawable()).stop();
					progressBar.setVisibility(View.INVISIBLE);
					Toast.makeText(getApplicationContext(), "Trimimg Complete",
							Toast.LENGTH_LONG).show();

					Intent intent = new Intent(VideoTrimActivity.this,
							AddCoverActivity.class);
					intent.putExtra("path", outFilePath);
					startActivity(intent);
					finish();
				}
			});
		} catch (FFmpegCommandAlreadyRunningException e) {
			// do nothing for now
		}
	}

	int width;
	long start, end;

	private class LoadFrames extends AsyncTask<String, Void, List<Bitmap>> {
		MediaPlayer mp;

		@SuppressLint("NewApi")
		@Override
		protected List<Bitmap> doInBackground(String... arg0) {

			List<Bitmap> listOfBitmap = new ArrayList<Bitmap>();
			mp = MediaPlayer.create(getBaseContext(), mVideoUri);
			int millis = mp.getDuration();
			mp.release();

			String video = arg0[0];

			MediaMetadataRetriever mRetriever = new MediaMetadataRetriever();
			mRetriever.setDataSource(video);
			start = System.currentTimeMillis();

			int FRAME_BYTES = (millis * 1000) / 10;
			int FRAMESMAX = 11;

			for (int currentFrame = 1; currentFrame < FRAMESMAX; currentFrame++) {
				// listOfBitmap.add(mFrameGrabber.getFrameAtTime(FRAME_BYTES*currentFrame));
				Bitmap bmp = mRetriever.getFrameAtTime(FRAME_BYTES
						* currentFrame, MediaMetadataRetriever.OPTION_CLOSEST);
				if (bmp != null) {
					listOfBitmap.add(Bitmap.createScaledBitmap(bmp, 110, 110,
							false));
				}

			}
			end = System.currentTimeMillis();
			System.out.println("Total duration ms" + millis);
			System.out.println("duration: " + (end - start));

			System.out.println(video);
			return listOfBitmap;
		}

		@Override
		protected void onPostExecute(final List<Bitmap> result) {
			// TODO Auto-generated method stub

			super.onPostExecute(result);

			mTimeLayer.setVisibility(View.VISIBLE);

			mVideoView.setVideoURI(mVideoUri);

			mVideoView.setOnPreparedListener(new OnPreparedListener() {

				@Override
				public void onPrepared(MediaPlayer mp) {

					videoSliceSeekBar
							.setSeekBarChangeListener(new VideoSliceSeekBar.SeekBarChangeListener() {
								@Override
								public void SeekBarValueChanged(int leftThumb,
										int rightThumb) {
									if (mVideoView.isPlaying())
										mVideoView.pause();
									textViewTrimingStart.setText(Utils
											.getTimeForTrackFormat(leftThumb,
													true));
									textViewTrimingEnd.setText(Utils
											.getTimeForTrackFormat(rightThumb,
													true));
									textViewTrimingTime.setText(Utils
											.getTimeForTrackFormat(rightThumb
													- leftThumb, true));
									if (mTrimEndTime != rightThumb) {

										mVideoView.seekTo(rightThumb);

									} else if (mTrimStartTime != leftThumb) {

										mVideoView.seekTo(leftThumb);

									}

									mTrimStartTime = leftThumb;
									mTrimEndTime = rightThumb;
									// setProgress();
								}
							});
					videoSliceSeekBar.setVisibility(View.VISIBLE);
					videoSliceSeekBar.getBitmapList(getCombinedBitmap(result));
					videoSliceSeekBar.setMaxValue(mp.getDuration());
					videoSliceSeekBar.setLeftProgress(0);
					videoSliceSeekBar.setRightProgress(mp.getDuration());
					videoSliceSeekBar.setProgressMinDiff(mp.getDuration() / 10);

					mProgressBar.setVisibility(View.GONE);

					videoControlBtn
							.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									performVideoViewClick();
								}
							});

					mp.setOnVideoSizeChangedListener(new OnVideoSizeChangedListener() {
						@Override
						public void onVideoSizeChanged(MediaPlayer mp,
								int width, int height) {
						}
					});
				}
			});

			// rangeSeekBar.setVisibility(View.VISIBLE);
			// // Set the range
			// rangeSeekBar.setRangeValues(0, 100);
			// rangeSeekBar.setSelectedMinValue(0);
			// rangeSeekBar.setSelectedMaxValue(100);
			// // rangeSeekBar.setMaxValue(mp.getDuration());
			// rangeSeekBar.getBitmapList( getCombinedBitmap( result),width);
			//
			// mProgressbar.setVisibility(View.INVISIBLE);

		}

		public Bitmap getCombinedBitmap(List<Bitmap> list) {
			Bitmap drawnBitmap = null;
			width = 110 * list.size();
			float left = 0;
			float top = 0;

			try {
				drawnBitmap = Bitmap.createBitmap(width, 120, Config.ARGB_8888);

				Canvas canvas = new Canvas(drawnBitmap);
				// JUST CHANGE TO DIFFERENT Bitmaps and coordinates .
				for (int i = 0; i < list.size(); i++) {

					canvas.drawBitmap(list.get(i), left, top, null);

					left = left + 110;
					top = 0;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			BitmapClass.setBp(drawnBitmap);
			return drawnBitmap;
		}

	}

}
