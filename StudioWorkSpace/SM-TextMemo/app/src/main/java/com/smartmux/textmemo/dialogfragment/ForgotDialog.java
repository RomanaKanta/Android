package com.smartmux.textmemo.dialogfragment;

import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.smartmux.textmemo.R;
import com.smartmux.textmemo.utils.AppExtra;
import com.smartmux.textmemo.utils.AppUserInfo;

public class ForgotDialog extends DialogFragment {

	EditText hints;
	private AppUserInfo appUserinfo;
	private String _oldAnswer;
	private EditText answer;
	private TextView question;
	private TextView showPassword;
	private Button mSubmitButton;

	public void onResume() {
		super.onResume();

		DisplayMetrics displaymetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay()
				.getMetrics(displaymetrics);
		int width = displaymetrics.widthPixels;

		getDialog().getWindow().getAttributes().gravity = Gravity.CENTER;
		WindowManager.LayoutParams lp = getDialog().getWindow().getAttributes();
		int mrgnwt = (int) getActivity().getResources().getDimension(
				R.dimen.mrgn_left)
				+ (int) getActivity().getResources().getDimension(R.dimen.mrgn_left);
		lp.width = (int) (width - mrgnwt);
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		getDialog().getWindow().setAttributes(lp);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.dialog_forgot_password, container,
				false);

		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		getDialog().getWindow().setBackgroundDrawableResource(
				android.R.color.transparent);
		getDialog().getWindow().setGravity(Gravity.CENTER);
//		getDialog().getWindow().getAttributes().windowAnimations = R.style.AnimationBottomUpDown;

		appUserinfo = new AppUserInfo(getActivity());
		String _oldQeustion = appUserinfo.getQuestion();
		_oldAnswer = appUserinfo.getAnswer();

		Typeface font = Typeface.createFromAsset(getActivity().getAssets(),
				AppExtra.AVENIRLSTD_LIGHT);
		Typeface font_black = Typeface.createFromAsset(getActivity().getAssets(),
				AppExtra.AVENIRLSTD_BLACK);
		showPassword = (TextView) rootView.findViewById(R.id.showPassword);
		question = (TextView) rootView
				.findViewById(R.id.user_secret_question);
		showPassword.setTypeface(font_black);
		question.setTypeface(font);
		question.setText(getString(R.string.secrect_que)+ _oldQeustion);
		answer = (EditText) rootView
				.findViewById(R.id.user_secret_question_answer);
		
		OnFocusChangeListener ofcListener = new MyFocusChangeListener();
		answer.setOnFocusChangeListener(ofcListener);
		answer.setOnEditorActionListener(new OnEditorActionListener() {
			
			

			@Override
			public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {

				getDialog().getWindow().setSoftInputMode(
			    		    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
			    		);
				return false;
			}
		});
		       
		
		answer.setHint(getString(R.string.ans));
		answer.setTypeface(font);
	      mSubmitButton = (Button) rootView.findViewById(R.id.user_info);
	      mSubmitButton.setTypeface(font_black);

		mSubmitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				if (!answer.getText().toString().trim().equals(_oldAnswer)) {

					showPassword.setText(R.string.wrong_password);
					showPassword.setTextColor(Color.RED);
					showPassword.setVisibility(View.VISIBLE);
					return;
				} else {

					final String _oldPassword = appUserinfo.getPassword();

					showPassword.setText(getString(R.string.password) + _oldPassword);
					showPassword.setVisibility(View.VISIBLE);
					showPassword.setTextColor(Color.parseColor("#6A21C5"));
					mSubmitButton.setVisibility(View.GONE);
					
				}

			}
		});
		
		
		return rootView;
	}
	
	 private class MyFocusChangeListener implements OnFocusChangeListener {

		    public void onFocusChange(View v, boolean hasFocus){

		        if(v.getId() == R.id.user_secret_question_answer && !hasFocus) {

		            InputMethodManager imm =  (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
	            
		        }
		    }
		}
	
}
