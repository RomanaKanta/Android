package com.smartmux.filevaultfree;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.smartmux.filevaultfree.utils.AppActionBar;
import com.smartmux.filevaultfree.utils.AppExtra;
import com.smartmux.filevaultfree.utils.AppToast;
import com.smartmux.filevaultfree.utils.AppUserInfo;

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
		setContentView(R.layout.user_registration);
		AppActionBar.changeActionBarFont(getApplicationContext(),RegistrationActivity.this);
		AppActionBar.updateAppActionBar(getActionBar(), this, false);
		getActionBar().setTitle(getString(R.string.registration));
		question = (EditText) findViewById(R.id.user_secret_question);
		answer = (EditText) findViewById(R.id.user_secret_answer);
		password = (EditText) findViewById(R.id.user_secret_password);
		confrimPassword = (EditText) findViewById(R.id.user_secret_confirm_password);
		btnResgistration = (Button) this.findViewById(R.id.user_registration_confirm);
		btnResgistration.setText("Get Registered");
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
				FolderListActivity.class);
		startActivity(i);
		finish();
	}

}
