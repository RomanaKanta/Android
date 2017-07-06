package com.aircast.photobag.widget;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import com.aircast.photobag.R;

public class PBCustomWaitingProgress {

    private Activity mContext;
    private Dialog mDlgRoot;
    private LayoutInflater mInflater;

    /**
     * Constructer for custom menu like iPhone style.
     * 
     * @param context
     *            must using <b>this</b> when using custom Dialog in this case.
     */
    public PBCustomWaitingProgress(Activity context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(this.mContext);

        mDlgRoot = new Dialog(context, R.style.ThemeProgressBarCustom);
        mDlgRoot.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDlgRoot.setCancelable(false);
        View mMenuLayout = this.mInflater.inflate(
                R.layout.pb_custom_progress_bar, null);
        mDlgRoot.setContentView(mMenuLayout);
    }

    public void showWaitingLayout() {
        if (mContext == null || mContext.isFinishing()) {
            return;
        }
        if (mDlgRoot != null && !mDlgRoot.isShowing()) {
            mDlgRoot.show();
        }
    }

    public void hideWaitingLayout() {
        if (mDlgRoot != null)
            if (mDlgRoot.isShowing())
                mDlgRoot.dismiss();
    }

}
