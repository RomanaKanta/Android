package com.kanta.studio.watchout.levels;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

import com.kanta.studio.watchout.GameActivity;
import com.kanta.studio.watchout.R;
import com.kanta.studio.watchout.utils.AlartMessage;


public class Level5 extends View{

	Activity mActivity;
	public int xMin = 0; // This view's bounds
	public int xMax;
	public int yMin = 0;
	public int yMax;
	public float ballRadius = 60; // Ball's radius

	public RectF playerBounds; // Needed for Canvas.drawOval
	public float playerX; // Ball's center (x,y)
	public float playerY;

    private RectF ballBounds1;
    private float ballX1; // Ball's center (x,y)
    private float ballY1;
    private float ballSpeedX1 = 15; // Ball's speed (x,y)
    private float ballSpeedY1 = 12;

	private RectF ballBounds2;
	private float ballX2; // Ball's center (x,y)
	private float ballY2;
	private float ballSpeedX2 = 15; // Ball's speed (x,y)
	private float ballSpeedY2 = 10;

    private RectF ballBounds3;
    private float ballX3; // Ball's center (x,y)
    private float ballY3;
    private float ballSpeedX3 = 12; // Ball's speed (x,y)
    private float ballSpeedY3 = 10;

	private Paint paint; // The paint (e.g. style, color) used for drawing

	// Constructor
	public Level5(Activity a) {
		super(a);
 
		mActivity=a;

        playerX = ballRadius + 10;
        playerY = ballRadius + 30;

        ballX1 = ballRadius + mActivity.getResources().getInteger(R.integer.x1);
        ballY1 = ballRadius + mActivity.getResources().getInteger(R.integer.y1);

        ballX2 = ballRadius + mActivity.getResources().getInteger(R.integer.x2);
        ballY2 = ballRadius + mActivity.getResources().getInteger(R.integer.y2);

        ballX3 = ballRadius + mActivity.getResources().getInteger(R.integer.x3);
        ballY3 = ballRadius + mActivity.getResources().getInteger(R.integer.y3);


		playerBounds = new RectF();//set ontouch method for this ball
        ballBounds1 = new RectF();
		ballBounds2 = new RectF();
        ballBounds3 = new RectF();
		paint = new Paint();
	}

	// Called back to draw the view. Also called by invalidate().
	@Override
	protected void onDraw(Canvas canvas) {
		// Draw the ball
		// left = ballX - ballRadius
		// top = ballY - ballRadius
		// right = ballX + ballRadius
		// bottom = ballY + ballRadius
		playerBounds.set(playerX - ballRadius, playerY - ballRadius, playerX
                + ballRadius, playerY + ballRadius);
		paint.setColor(Color.YELLOW);
		canvas.drawOval(playerBounds, paint);

        // 1st ball
        ballBounds1.set(ballX1 - ballRadius, ballY1 - ballRadius, ballX1
                + ballRadius, ballY1 + ballRadius);
        paint.setColor(Color.BLUE);
        canvas.drawOval(ballBounds1, paint);

		// 2nd ball
		ballBounds2.set(ballX2 - ballRadius, ballY2 - ballRadius, ballX2
				+ ballRadius, ballY2 + ballRadius);
		paint.setColor(Color.BLUE);
		canvas.drawOval(ballBounds2, paint);

        // 3rd ball
        ballBounds3.set(ballX3 - ballRadius, ballY3 - ballRadius, ballX3
                + ballRadius, ballY3 + ballRadius);
        paint.setColor(Color.BLUE);
        canvas.drawOval(ballBounds3, paint);

		// Update the position of the ball, including collision detection and
		// reaction.
		update();

		// Delay
		try {
			Thread.sleep(30);
		} catch (InterruptedException e) {
		}

		invalidate(); // Force a re-draw
	}

	// Detect collision and update the position of the ball.
	private void update() {
		// Get new (x,y) position

        ballX1 += ballSpeedX1;
        ballY1 += ballSpeedY1;

		ballX2 += ballSpeedX2;
		ballY2 += ballSpeedY2;

        ballX3 += ballSpeedX3;
        ballY3 += ballSpeedY3;

	
		// left = ballX - ballRadius
		// top = ballY - ballRadius
		// right = ballX + ballRadius
		// bottom = ballY + ballRadius
		// Detect collision and react
//		if (playerX + ballRadius > xMax) {
//			ballSpeedX1 = -ballSpeedX1;
//			playerX = xMax - ballRadius;
//		} else if (playerX - ballRadius < xMin) {
//			ballSpeedX1 = -ballSpeedX1;
//			playerX = xMin + ballRadius;
//		}
//		if (playerY + ballRadius > yMax) {
//			ballSpeedY1 = -ballSpeedY1;
//			playerY = yMax - ballRadius;
//		} else if (playerY - ballRadius < yMin) {
//			ballSpeedY1 = -ballSpeedY1;
//			playerY = yMin + ballRadius;
//		}

        // 1st ball
        // Detect collision and react
        if (ballX1 + ballRadius > xMax) {
            ballSpeedX1 = -ballSpeedX1;
            ballX1 = xMax - ballRadius;
        } else if (ballX1 - ballRadius < xMin) {
            ballSpeedX1 = -ballSpeedX1;
            ballX1 = xMin + ballRadius;
        }
        if (ballY1 + ballRadius > yMax) {
            ballSpeedY1 = -ballSpeedY1;
            ballY1 = yMax - ballRadius;
        } else if (ballY1 - ballRadius < yMin) {
            ballSpeedY1 = -ballSpeedY1;
            ballY1 = yMin + ballRadius;
        }

		// 2nd ball
		// Detect collision and react
		if (ballX2 + ballRadius > xMax) {
			ballSpeedX2 = -ballSpeedX2;
			ballX2 = xMax - ballRadius;
		} else if (ballX2 - ballRadius < xMin) {
			ballSpeedX2 = -ballSpeedX2;
			ballX2 = xMin + ballRadius;
		}
		if (ballY2 + ballRadius > yMax) {
			ballSpeedY2 = -ballSpeedY2;
			ballY2 = yMax - ballRadius;
		} else if (ballY2 - ballRadius < yMin) {
			ballSpeedY2 = -ballSpeedY2;
			ballY2 = yMin + ballRadius;
		}

        // 3rd ball
        // Detect collision and react
        if (ballX3 + ballRadius > xMax) {
            ballSpeedX3 = -ballSpeedX3;
            ballX3 = xMax - ballRadius;
        } else if (ballX3 - ballRadius < xMin) {
            ballSpeedX3 = -ballSpeedX3;
            ballX3 = xMin + ballRadius;
        }
        if (ballY3 + ballRadius > yMax) {
            ballSpeedY3 = -ballSpeedY3;
            ballY3 = yMax - ballRadius;
        } else if (ballY3 - ballRadius < yMin) {
            ballSpeedY3 = -ballSpeedY3;
            ballY3 = yMin + ballRadius;
        }

        float dy12 = ballY1 - ballY2;
        float dx12 = ballX1 - ballX2;
        float distance12 = dy12 * dy12 + dx12 * dx12;

        if (distance12 < ((2 * ballRadius) * (2 * ballRadius))) {

            ballSpeedX1 = -ballSpeedX1;
            ballSpeedX2 = -ballSpeedX2;
        }

        float dy23 = ballY2 - ballY3;
        float dx23 = ballX2 - ballX3;
        float distance23 = dy23 * dy23 + dx23 * dx23;

        if (distance23 < ((2 * ballRadius) * (2 * ballRadius))) {

            ballSpeedX2 = -ballSpeedX2;
            ballSpeedX3 = -ballSpeedX3;
        }

        float dy31 = ballY1 - ballY3;
        float dx31 = ballX1 - ballX3;
        float distance31 = dy31 * dy31 + dx31 * dx31;

        if (distance31 < ((2 * ballRadius) * (2 * ballRadius))) {

            ballSpeedX1 = -ballSpeedX1;
            ballSpeedX3 = -ballSpeedX3;
        }

        collision(playerX, playerY,ballX1,ballY1);
        collision(playerX, playerY,ballX2,ballY2);
        collision(playerX, playerY,ballX3,ballY3);

	}

	// Called back when the view is first created or its size changes.
	@Override
	public void onSizeChanged(int w, int h, int oldW, int oldH) {
		// Set the movement bounds for the ball
		xMax = w - 1;
		yMax = h - 1;
	}
	
	   @SuppressLint("ClickableViewAccessibility")
	   public boolean onTouchEvent(MotionEvent event) {
		   
			float x =  event.getRawX();
			float y =  event.getRawY();

			switch(event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:

//                    playerX = x;
//                    playerY = y - ballRadius;
                    //invalidate();
                    break;

                case MotionEvent.ACTION_UP:

                    break;

                case MotionEvent.ACTION_MOVE:

                    playerX = x;
                    playerY = y - ballRadius;

                    if (playerX + ballRadius > xMax) {
                        playerX = xMax - ballRadius;
                    }
                    if (playerX - ballRadius < xMin) {
                        playerX = xMin + ballRadius;
                    }
                    if (playerY + ballRadius > yMax) {
                        playerY = yMax - ballRadius;
                    }
                    if (playerY - ballRadius < yMin) {
                        playerY = yMin + ballRadius;
                    }

				invalidate();
				break;
			}
	   
	        return true;
	    }

    void reverse(float x_a,float y_a,float x_b,float y_b,float speed_a,float speed_b){

        //measure distance for collision
        float dy = y_a - y_b;
        float dx = x_a - x_b;
        float distance = dy * dy + dx * dx;

        if (distance < ((2 * ballRadius) * (2 * ballRadius))) {

            speed_a = -speed_a;
            speed_b = -speed_b;

        }
    }


    void collision(float x_a,float y_a,float x_b,float y_b){

        //measure distance for collision
        float dy = y_a - y_b;
        float dx = x_a - x_b;
        float distance12 = dy * dy + dx * dx;

        if (distance12 < ((2 * ballRadius) * (2 * ballRadius))) {

            ((GameActivity)mActivity).mTimer.cancel();
            ((GameActivity)mActivity).layout.removeAllViews();

            new AlartMessage(mActivity);
        }

    }

}
