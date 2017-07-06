package com.aircast.photobag.application;

import android.app.AlertDialog;
import android.content.Context;

import com.aircast.photobag.R;

/** 
 * class custom dialog use style string with center align
 * extends {@link AlertDialog}
 * @author lent5
 *
 */
public class PBCustomDialog extends AlertDialog {
    /** Constructor PBCustomDialog */
    public PBCustomDialog(Context ctx) { 
        super(ctx, R.style.CenterJustifyTheme); } 
}
