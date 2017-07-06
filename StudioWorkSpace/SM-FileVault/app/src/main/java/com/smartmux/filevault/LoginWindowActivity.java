package com.smartmux.filevault;

import android.app.FragmentManager;
import android.content.Context;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.smartmux.filevault.fragmentdialog.ForgotDialog;
import com.smartmux.filevault.utils.AppUserInfo;

public class LoginWindowActivity extends AppLockActivity {
	EditText hints;
	private Object dialogPlus;
	private AppUserInfo appUserinfo;
	private String _oldAnswer;
	private EditText answer;
	private TextView question;
	private TextView showPassword;
	private Button mSubmitButton;

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
	public int getPinLength() {
		return super.getPinLength();// you can override this method to change
									// the pin length from the default 4
	}
	   private class MyFocusChangeListener implements OnFocusChangeListener {

		    public void onFocusChange(View v, boolean hasFocus){

		        if(v.getId() == R.id.user_secret_question_answer && !hasFocus) {

		            InputMethodManager imm =  (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
	            
		        }
		    }
		}

}
