package com.smartmux.filevaultfree;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.smartmux.filevaultfree.utils.AppActionBar;
import com.smartmux.filevaultfree.utils.AppExtra;
import com.smartmux.filevaultfree.utils.AppToast;
import com.smartmux.filevaultfree.utils.AppUserInfo;
import com.smartmux.filevaultfree.utils.FileManager;

public class PasswordChangeActivity extends AppMainActivity implements
		OnClickListener {

	private EditText answer;
	private EditText password;
	private EditText passwordConfirm;
	private TextView question;
	private String _oldAnswer;
	private String type;
	private AppUserInfo appUserinfo;
	private FileManager fileManager;
	private Button btnUserInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_password_change);
		AppActionBar.changeActionBarFont(getApplicationContext(),
				PasswordChangeActivity.this);

		AppActionBar.updateAppActionBar(getActionBar(), this, true);

		if (!getIntent().hasExtra("type")) {

			finish();
		}

		btnUserInfo = (Button) this.findViewById(R.id.user_info);
		btnUserInfo.setOnClickListener(this);

		fileManager = new FileManager();

		answer = (EditText) findViewById(R.id.user_secret_question_answer);
		password = (EditText) findViewById(R.id.user_password_conf);
		question = (TextView) findViewById(R.id.user_secret_question);
		passwordConfirm = (EditText) findViewById(R.id.user_new_password_conf);
		password.setEnabled(false);
		passwordConfirm.setEnabled(false);

		appUserinfo = new AppUserInfo(this);
		String _oldQeustion = appUserinfo.getQuestion();
		_oldAnswer = appUserinfo.getAnswer();

		type = getIntent().getExtras().getString("type");
		if (type.equals("change")) {
			getActionBar().setTitle(getString(R.string.change_password));
			password.setVisibility(View.VISIBLE);
			passwordConfirm.setVisibility(View.VISIBLE);
			question.setVisibility(View.GONE);
			answer.setHint("Old Password");
			password.setHint("New Password");
			btnUserInfo.setText("Confirm New Password");
		} else {

			getActionBar().setTitle(getString(R.string.forgot_password_title));
			password.setVisibility(View.GONE);
			question.setVisibility(View.VISIBLE);

			btnUserInfo.setText("Get Password");
		}

		question.setText(String.format(getString(R.string.Secret_questiony),
				_oldQeustion));

		setTextWatcher();

	}

	public void setTextWatcher() {

		answer.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				if (s.length() != 0) {
					password.setEnabled(true);
					passwordConfirm.setEnabled(true);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		password.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (s.length() > AppExtra.TOTAL_PASSWORD_DIGIT) {
					password.setError(getString(R.string.Enter_4_Digits_Passowrd));
				} else {
					password.setError(null);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

	}

	@Override
	public void onClick(View arg0) {
		if (type.equals("change")) {
			final String _oldPassword = appUserinfo.getPassword();

			if (answer.getText().toString().length() == 0) {
				answer.setError(getString(R.string.empty_field));
				return;
			}

			if ((answer.getText().toString().length() != 0)
					&& (!answer.getText().toString().equals(_oldPassword))) {
				answer.setError(getString(R.string.old_Password_Match));
				return;
			}

			if (password.getText().toString().length() == 0) {
				password.requestFocus();
				password.setError(getString(R.string.empty_field));
				return;
			}
			if ((password.getText().toString().length() != 0)
					&& (password.getText().toString().length() <AppExtra.TOTAL_PASSWORD_DIGIT)) {
				password.requestFocus();
				password.setError(getString(R.string.Enter_4_Digits_Passowrd));
				return;
			}

			if (passwordConfirm.getText().toString().length() == 0) {
				passwordConfirm.requestFocus();
				passwordConfirm.setError(getString(R.string.empty_field));
				return;
			}

			if ((passwordConfirm.getText().toString().length() != 0)
					&& (!password.getText().toString()
							.equals(passwordConfirm.getText().toString()))) {
				passwordConfirm.requestFocus();
				passwordConfirm.setError(getString(R.string.Password_Match));
				return;
			}

			AppToast.show(getApplicationContext(),
					getString(R.string.Password_Change_successfully));
			appUserinfo.setPassword(password.getText().toString());
			finish();
			Intent intent = new Intent(PasswordChangeActivity.this,
					SettingActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

		} else {

			if (!answer.getText().toString().trim().equals(_oldAnswer)) {
				AppToast.show(getApplicationContext(), getApplicationContext()
						.getString(R.string.wrong_password));
				return;
			}

			final String _oldPassword = appUserinfo.getPassword();
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(
					new ContextThemeWrapper(this, android.R.style.Theme_Dialog));
			alertDialog.setMessage(String.format(
					getString(R.string.Your_password_is), _oldPassword));
			alertDialog.setPositiveButton(getString(R.string.Thanks),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});
			alertDialog.show();
		}

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
		overridePendingTransition(R.anim.push_right_out, R.anim.push_right_in);
		;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			fileManager.setBackCode(getApplicationContext());
			finish();
			overridePendingTransition(R.anim.push_right_out,
					R.anim.push_right_in);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
