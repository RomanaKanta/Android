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


public class Level1 extends View{

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
    private float ballSpeedX1 = 10; // Ball's speed (x,y)
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

    private RectF ballBounds4;
    private float ballX4; // Ball's center (x,y)
    private float ballY4;
    private float ballSpeedX4 = 12; // Ball's speed (x,y)
    private float ballSpeedY4 = 8;

    private RectF ballBounds5;
    private float ballX5; // Ball's center (x,y)
    private float ballY5;
    private float ballSpeedX5 = 12; // Ball's speed (x,y)
    private float ballSpeedY5 = 15;

    private RectF ballBounds6;
    private float ballX6; // Ball's center (x,y)
    private float ballY6;
    private float ballSpeedX6 = 8; // Ball's speed (x,y)
    private float ballSpeedY6 = 10;

	private Paint paint; // The paint (e.g. style, color) used for drawing

	// Constructor
	public Level1(Activity a) {
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

        ballX4 = ballRadius + mActivity.getResources().getInteger(R.integer.x4);
        ballY4 = ballRadius + mActivity.getResources().getInteger(R.integer.y4);

        ballX5 = ballRadius + mActivity.getResources().getInteger(R.integer.x5);
        ballY5 = ballRadius + mActivity.getResources().getInteger(R.integer.y5);

        ballX6 = ballRadius + mActivity.getResources().getInteger(R.integer.x6);
        ballY6 = ballRadius + mActivity.getResources().getInteger(R.integer.y6);


		playerBounds = new RectF();//set ontouch method for this ball
        ballBounds1 = new RectF();
		ballBounds2 = new RectF();
        ballBounds3 = new RectF();
        ballBounds4 = new RectF();
        ballBounds5 = new RectF();
        ballBounds6 = new RectF();
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
		paint.setColor(Color.GREEN);
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

        // 4th ball
        ballBounds4.set(ballX4 - ballRadius, ballY4 - ballRadius, ballX4
                + ballRadius, ballY4 + ballRadius);
        paint.setColor(Color.BLUE);
        canvas.drawOval(ballBounds4, paint);

        // 5th ball
        ballBounds5.set(ballX5 - ballRadius, ballY5 - ballRadius, ballX5
                + ballRadius, ballY5 + ballRadius);
        paint.setColor(Color.BLUE);
        canvas.drawOval(ballBounds5, paint);

        // 6th ball
        ballBounds6.set(ballX6 - ballRadius, ballY6 - ballRadius, ballX6
                + ballRadius, ballY6 + ballRadius);
        paint.setColor(Color.BLUE);
        canvas.drawOval(ballBounds6, paint);

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

        ballX4 += ballSpeedX4;
        ballY4 += ballSpeedY4;

        ballX5 += ballSpeedX5;
        ballY5 += ballSpeedY5;

        ballX6 += ballSpeedX6;
        ballY6 += ballSpeedY6;

	
		// left = ballX - ballRadius
		// top = ballY - ballRadius
		// right = ballX + ballRadius
		// bottom = ballY + ballRadius

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

        //4th ball
        if (ballX4 + ballRadius > xMax) {
            ballSpeedX4 = -ballSpeedX4;
            ballX4 = xMax - ballRadius;
        } else if (ballX4 - ballRadius < xMin) {
            ballSpeedX4 = -ballSpeedX4;
            ballX4 = xMin + ballRadius;
        }
        if (ballY4 + ballRadius > yMax) {
            ballSpeedY4 = -ballSpeedY4;
            ballY4 = yMax - ballRadius;
        } else if (ballY4 - ballRadius < yMin) {
            ballSpeedY4 = -ballSpeedY4;
            ballY4 = yMin + ballRadius;
        }

        //5th ball
        if (ballX5 + ballRadius > xMax) {
            ballSpeedX5 = -ballSpeedX5;
            ballX5 = xMax - ballRadius;
        } else if (ballX5 - ballRadius < xMin) {
            ballSpeedX5 = -ballSpeedX5;
            ballX5 = xMin + ballRadius;
        }
        if (ballY5 + ballRadius > yMax) {
            ballSpeedY5 = -ballSpeedY5;
            ballY5 = yMax - ballRadius;
        } else if (ballY5 - ballRadius < yMin) {
            ballSpeedY5 = -ballSpeedY5;
            ballY5 = yMin + ballRadius;
        }

        //6th ball
        if (ballX6 + ballRadius > xMax) {
            ballSpeedX6 = -ballSpeedX6;
            ballX6 = xMax - ballRadius;
        } else if (ballX6 - ballRadius < xMin) {
            ballSpeedX6 = -ballSpeedX6;
            ballX6 = xMin + ballRadius;
        }
        if (ballY6 + ballRadius > yMax) {
            ballSpeedY6 = -ballSpeedY6;
            ballY6 = yMax - ballRadius;
        } else if (ballY6 - ballRadius < yMin) {
            ballSpeedY6 = -ballSpeedY6;
            ballY6 = yMin + ballRadius;
        }

        float dy12 = ballY1 - ballY2;
        float dx12 = ballX1 - ballX2;
        float distance12 = dy12 * dy12 + dx12 * dx12;

        if (distance12 < ((2 * ballRadius) * (2 * ballRadius))) {

            ballSpeedX1 = -ballSpeedX1;
            ballSpeedX2 = -ballSpeedX2;
        }
        float dy13 = ballY1 - ballY3;
        float dx13 = ballX1 - ballX3;
        float distance13 = dy13 * dy13 + dx13 * dx13;

        if (distance13 < ((2 * ballRadius) * (2 * ballRadius))) {

            ballSpeedX1 = -ballSpeedX1;
            ballSpeedX3 = -ballSpeedX3;
        }

        float dy14 = ballY1 - ballY4;
        float dx14 = ballX1 - ballX4;
        float distance14 = dy14 * dy14 + dx14 * dx14;

        if (distance14 < ((2 * ballRadius) * (2 * ballRadius))) {

            ballSpeedX1 = -ballSpeedX1;
            ballSpeedX4 = -ballSpeedX4;
        }

        float dy15 = ballY1 - ballY5;
        float dx15 = ballX1 - ballX5;
        float distance15 = dy15 * dy15 + dx15 * dx15;

        if (distance15 < ((2 * ballRadius) * (2 * ballRadius))) {

            ballSpeedX1 = -ballSpeedX1;
            ballSpeedX5 = -ballSpeedX5;
        }

        float dy16 = ballY1 - ballY6;
        float dx16 = ballX1 - ballX6;
        float distance16 = dy16 * dy16 + dx16 * dx16;

        if (distance16 < ((2 * ballRadius) * (2 * ballRadius))) {

            ballSpeedX1 = -ballSpeedX1;
            ballSpeedX6 = -ballSpeedX6;
        }

        float dy23 = ballY2 - ballY3;
        float dx23 = ballX2 - ballX3;
        float distance23 = dy23 * dy23 + dx23 * dx23;

        if (distance23 < ((2 * ballRadius) * (2 * ballRadius))) {

            ballSpeedX2 = -ballSpeedX2;
            ballSpeedX3 = -ballSpeedX3;
        }

        float dy24 = ballY2 - ballY4;
        float dx24 = ballX2 - ballX4;
        float distance24 = dy24 * dy24 + dx24 * dx24;

        if (distance24 < ((2 * ballRadius) * (2 * ballRadius))) {

            ballSpeedX2 = -ballSpeedX2;
            ballSpeedX4 = -ballSpeedX4;
        }

        float dy25 = ballY2 - ballY5;
        float dx25 = ballX2 - ballX5;
        float distance25 = dy25 * dy25 + dx25 * dx25;

        if (distance25 < ((2 * ballRadius) * (2 * ballRadius))) {

            ballSpeedX2 = -ballSpeedX2;
            ballSpeedX5 = -ballSpeedX5;
        }

        float dy26 = ballY2 - ballY6;
        float dx26 = ballX2 - ballX6;
        float distance26 = dy26 * dy26 + dx26 * dx26;

        if (distance26 < ((2 * ballRadius) * (2 * ballRadius))) {

            ballSpeedX2 = -ballSpeedX2;
            ballSpeedX6 = -ballSpeedX6;
        }

        float dy34 = ballY3 - ballY4;
        float dx34 = ballX3 - ballX4;
        float distance34 = dy34 * dy34 + dx34 * dx34;

        if (distance34 < ((2 * ballRadius) * (2 * ballRadius))) {

            ballSpeedX3 = -ballSpeedX3;
            ballSpeedX4 = -ballSpeedX4;
        }

        float dy35 = ballY3 - ballY5;
        float dx35 = ballX3 - ballX5;
        float distance35 = dy35 * dy35 + dx35 * dx35;

        if (distance35 < ((2 * ballRadius) * (2 * ballRadius))) {

            ballSpeedX3 = -ballSpeedX3;
            ballSpeedX5 = -ballSpeedX5;
        }

        float dy36 = ballY3 - ballY6;
        float dx36 = ballX3 - ballX6;
        float distance36 = dy36 * dy36 + dx36 * dx36;

        if (distance36 < ((2 * ballRadius) * (2 * ballRadius))) {

            ballSpeedX3 = -ballSpeedX3;
            ballSpeedX6 = -ballSpeedX6;
        }


        float dy45 = ballY4 - ballY5;
        float dx45 = ballX4 - ballX5;
        float distance45 = dy45 * dy45 + dx45 * dx45;

        if (distance45 < ((2 * ballRadius) * (2 * ballRadius))) {

            ballSpeedX4 = -ballSpeedX4;
            ballSpeedX5 = -ballSpeedX5;
        }

        float dy46 = ballY4 - ballY6;
        float dx46 = ballX4 - ballX6;
        float distance46 = dy46 * dy46 + dx46 * dx46;

        if (distance46 < ((2 * ballRadius) * (2 * ballRadius))) {

            ballSpeedX4 = -ballSpeedX4;
            ballSpeedX6 = -ballSpeedX6;
        }

        float dy56 = ballY5 - ballY6;
        float dx56 = ballX5 - ballX6;
        float distance56 = dy56 * dy56 + dx56 * dx56;

        if (distance56 < ((2 * ballRadius) * (2 * ballRadius))) {

            ballSpeedX5 = -ballSpeedX5;
            ballSpeedX6 = -ballSpeedX6;
        }



        collision(playerX, playerY,ballX1,ballY1);
        collision(playerX, playerY,ballX2,ballY2);
        collision(playerX, playerY,ballX3,ballY3);
        collision(playerX, playerY,ballX4,ballY4);
        collision(playerX, playerY,ballX5,ballY5);
        collision(playerX, playerY,ballX6,ballY6);

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
