package com.kanta.studio.watchout;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kanta.studio.watchout.levels.Level1;
import com.kanta.studio.watchout.levels.Level2;
import com.kanta.studio.watchout.levels.Level3;
import com.kanta.studio.watchout.levels.Level4;
import com.kanta.studio.watchout.levels.Level5;
import com.kanta.studio.watchout.utils.AlartMessage;
import com.kanta.studio.watchout.utils.ConstantString;

import java.util.Timer;
import java.util.TimerTask;


public class GameActivity extends Activity {
	TextView mTextField;
//	MyReceiver myReceiver;
//	IntentFilter intentFilter;
	AlartMessage mAlartMessage;
	public Timer mTimer;
	int mRepeat = 0;
    int mTime = 0;
	public RelativeLayout layout;
//	View mDynamicMove;

	// View DefaultPage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_game);

		mRepeat= getIntent().getExtras().getInt(ConstantString.active_level);



		mTextField = (TextView) findViewById(R.id.timer);
//		mDynamicMove = new DynamicMove(this);
		// DefaultPage = new DefaultPage(this);
		// DefaultPage.setBackgroundResource(R.drawable.redball);
		layout = (RelativeLayout) findViewById(R.id.rootLayout);
		

		mTimer = new Timer();

		if (mRepeat == 1) {
			
			layout.addView(new Level1(GameActivity.this));
			mTime = 20;
			TimerTask task = new TimerTask() {

				@Override
				public void run() {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {

							mTextField.setText(ConstantString.remaining + mTime
									+ "");
							if (mTime > 0)
								mTime -= 1;
							else {

								mTimer.cancel();
								layout.removeAllViews();

								// layout.addView(DefaultPage);

								mAlartMessage = new AlartMessage(
										GameActivity.this, 1);

							}
						}
					});
				}
			};
			mTimer.scheduleAtFixedRate(task, 0, 1000);
		} else if (mRepeat == 2) {
			
			layout.addView(new Level2(GameActivity.this));
			TimerTask mtask = new TimerTask() {
				int mTime = 30;

				@Override
				public void run() {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {

							mTextField.setText(ConstantString.remaining + mTime
									+ "");
							if (mTime > 0)
								mTime -= 1;
							else {
								mAlartMessage = new AlartMessage(
										GameActivity.this, 2);
								mTimer.cancel();
								layout.removeAllViews();
							}
						}
					});
				}
			};
			mTimer.scheduleAtFixedRate(mtask, 0, 1000);
		} else if (mRepeat == 3) {
			
			layout.addView(new Level3(GameActivity.this));
			TimerTask mtask = new TimerTask() {
				int mTime = 40;

				@Override
				public void run() {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {

							mTextField.setText(ConstantString.remaining + mTime
									+ "");
							if (mTime > 0)
								mTime -= 1;
							else {
								mAlartMessage = new AlartMessage(
										GameActivity.this, 3);
								mTimer.cancel();
								layout.removeAllViews();
							}
						}
					});
				}
			};
			mTimer.scheduleAtFixedRate(mtask, 0, 1000);
		} else if (mRepeat == 4) {
			
			layout.addView(new Level4(GameActivity.this));
			TimerTask mtask = new TimerTask() {
				int mTime = 50;

				@Override
				public void run() {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {

							mTextField.setText(ConstantString.remaining + mTime
									+ "");
							if (mTime > 0)
								mTime -= 1;
							else {
								mAlartMessage = new AlartMessage(
										GameActivity.this, 4);
								mTimer.cancel();
								layout.removeAllViews();
							}
						}
					});
				}
			};
			mTimer.scheduleAtFixedRate(mtask, 0, 1000);
		} else {
			
			layout.addView(new Level5(GameActivity.this));
			TimerTask mtask = new TimerTask() {
				int mTime = 60;

				@Override
				public void run() {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {

							mTextField.setText(ConstantString.remaining + mTime
									+ "");
							if (mTime > 0)
								mTime -= 1;
							else {
								mAlartMessage = new AlartMessage(
										GameActivity.this, 5);
								mTimer.cancel();
								layout.removeAllViews();
							}
						}
					});
				}
			};
			mTimer.scheduleAtFixedRate(mtask, 0, 1000);
		}
	}

}
