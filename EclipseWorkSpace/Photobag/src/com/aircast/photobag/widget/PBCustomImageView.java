package com.aircast.photobag.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.aircast.photobag.R;
/**
 * PbCustom Image View support avoid use bitmap recycled
 * @author lent5
 *
 */
public class PBCustomImageView extends ImageView {
    /** bitmap */
    private Bitmap mBitmap;

    /**
     * Constructor PBCustomImageView
     * @param context
     * @param attrs
     */
    public PBCustomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        playableIcon = BitmapFactory.decodeResource(getResources(), R.drawable.icon_video_play);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        // super.setImageDrawable(new FastBitmapDrawable(this, bm));
        this.mBitmap = bm;
    }
    
    Drawable mDrawable;
    private boolean mIsItemCanPlay = false;
    private Bitmap playableIcon;
    
    public void setItemCanPlay(boolean canPlay) {
        mIsItemCanPlay = canPlay;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (this.mBitmap != null) {
            if (this.mBitmap.isRecycled()) {
                return;
            }
        }
        /*mDrawable = getDrawable();
        if (mDrawable instanceof FastBitmapDrawable) {
            
        }*/
        super.onDraw(canvas);
        if (mIsItemCanPlay && playableIcon != null) {
            int x = (getWidth() - playableIcon.getWidth()) / 2;
            int y = (getHeight() - playableIcon.getHeight()) / 2;                    
            canvas.drawBitmap(playableIcon, x, y, null);
        }
    }
    
    class FastBitmapDrawable extends Drawable {
        private Bitmap mBitmap;
        private final PBCustomImageView parent;
        
        FastBitmapDrawable(PBCustomImageView parent, Bitmap b) {
            this.parent = parent;
            this.mBitmap = b;
        }
        
        FastBitmapDrawable(PBCustomImageView parent, Bitmap b, boolean isDelete) {
            this.parent = parent;
            mBitmap = b;
        }

        @Override
        public void draw(Canvas canvas) {
            canvas.drawBitmap(mBitmap, 
                    null, 
                    new Rect(0, 0, parent.getWidth(), parent.getHeight()), 
                    null);
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }

        @Override
        public void setAlpha(int alpha) {
        }

        @Override
        public void setColorFilter(ColorFilter cf) {
        }

        @Override
        public int getIntrinsicWidth() {
            return mBitmap.getWidth();
        }

        @Override
        public int getIntrinsicHeight() {
            return mBitmap.getHeight();
        }

        @Override
        public int getMinimumWidth() {
            return mBitmap.getWidth();
        }

        @Override
        public int getMinimumHeight() {
            return mBitmap.getHeight();
        }

        public Bitmap getBitmap() {
            return mBitmap;
        }
    }
}
