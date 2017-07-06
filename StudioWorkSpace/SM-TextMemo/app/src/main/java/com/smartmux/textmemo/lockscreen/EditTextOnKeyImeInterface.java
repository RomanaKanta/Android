package com.smartmux.textmemo.lockscreen;

import android.view.KeyEvent;

/**
 * Created by stoyan on 5/11/15.
 */
public interface EditTextOnKeyImeInterface {

    /**
     * Set to be called in {@link TypefaceEditText#onKeyPreIme(int, android.view.KeyEvent)}
     * @param keyCode
     * @param event
     * @return
     */
    boolean onKeyPreIme(int keyCode, KeyEvent event);
}
