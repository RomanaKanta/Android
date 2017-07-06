package com.smartmux.textmemo;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.smartmux.textmemo.utils.AppActionBar;
import com.smartmux.textmemo.utils.AppExtra;
import com.smartmux.textmemo.utils.AppToast;
import com.smartmux.textmemo.utils.AppUserInfo;

public class RegistrationActivity extends AppMainActivity implements
		OnClickListener {

	private EditText question;
	private EditText answer;
	private EditText password;
	private EditText confrimPassword;
	private Button btnResgistration;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_registration);
		
		AppActionBar.changeActionBarFont(getApplicationContext(),RegistrationActivity.this);
		AppActionBar.updateAppActionBar(getActionBar(), this, true, false);
		getActionBar().setTitle(getString(R.string.registration));
		
		Typeface tf = Typeface.createFromAsset(getAssets(),
				AppExtra.AVENIRLSTD_BLACK);
		
		question = (EditText) findViewById(R.id.user_secret_question);
		question.setTypeface(tf);
		answer = (EditText) findViewById(R.id.user_secret_answer);
		answer.setTypeface(tf);
		password = (EditText) findViewById(R.id.user_secret_password);
		password.setTypeface(tf);
		confrimPassword = (EditText) findViewById(R.id.user_secret_confirm_password);
		confrimPassword.setTypeface(tf);
		btnResgistration = (Button) findViewById(R.id.user_registration_confirm);
		btnResgistration.setText(getString(R.string.register));

		btnResgistration.setTypeface(tf);
		btnResgistration.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		String questionText = question.getText().toString().trim();
		String answerText = answer.getText().toString().trim();
		String passwordText = password.getText().toString().trim();
		String confrimPasswordText = confrimPassword.getText().toString()
				.trim();

		if (questionText.length() == 0) {
			AppToast.show(getApplicationContext(),
					getString(R.string.Secret_Question_Empty));
			return;
		}

		if (answerText.length() == 0) {
			AppToast.show(getApplicationContext(),
					getString(R.string.Secret_Answer_Empty));
			return;
		}

		if (passwordText.length() != AppExtra.TOTAL_PASSWORD_DIGIT) {
			AppToast.show(getApplicationContext(), getString(R.string.Enter_4_Digits_Passowrd));
			return;
		}

		if (confrimPasswordText.length() != AppExtra.TOTAL_PASSWORD_DIGIT) {
			AppToast.show(getApplicationContext(),
					getString(R.string.Enter_4_Digits_Confirm_Password));
			return;
		}

		if (!passwordText.equals(confrimPasswordText)) {
			AppToast.show(getApplicationContext(),getString(R.string.Password_Match));
			return;
		}
		AppToast.show(getApplicationContext(),
				getString(R.string.Registration_Completed_successfully));
		AppUserInfo appUserinfo = new AppUserInfo(this);
		appUserinfo.setQuestion(questionText);
		appUserinfo.setAnswer(answerText);
		appUserinfo.setPassword(passwordText);
		Intent i = new Intent(RegistrationActivity.this,
				NoteListActivity.class);
		startActivity(i);
		finish();
	}

}
