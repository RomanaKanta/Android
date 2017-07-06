package com.smartmux.textmemo;

import android.app.FragmentManager;
import android.view.KeyEvent;

import com.smartmux.textmemo.dialogfragment.ForgotDialog;

public class LoginWindowActivity extends AppLockActivity {

	@Override
	public void showForgotDialog() {
	
		FragmentManager fragmentManager = getFragmentManager();
		
		ForgotDialog forgotDialog = new ForgotDialog();
		forgotDialog.show(fragmentManager, "Forgot Dialog Fragment");

	}

	@Override
	public void onPinFailure(int attempts) {
	}

	@Override
	public void onPinSuccess(int attempts) {
	}
	@Override
	public void onBackPressed() {
		moveTaskToBack(true);
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			moveTaskToBack(true);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	@Override
	public int getPinLength() {
		return super.getPinLength();// you can override this method to change
									// the pin length from the default 4
	}

}
