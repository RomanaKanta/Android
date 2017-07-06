package com.smartmux.textmemo;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartmux.textmemo.utils.AppExtra;
import com.smartmux.textmemo.utils.AppToast;
import com.smartmux.textmemo.utils.AppUserInfo;
import com.smartmux.textmemo.utils.FileManager;

public class ChangePasswordAvtivity extends AppMainActivity implements
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
		setContentView(R.layout.activity_change_password);

//		AppActionBar.changeActionBarFont(getApplicationContext(),
//				ChangePasswordAvtivity.this);
//		AppActionBar.updateAppActionBar(getActionBar(), this, false, true);
		
		createCutomActionBarTitle(R.string.change_password);

		btnUserInfo = (Button) this.findViewById(R.id.user_info);
		btnUserInfo.setOnClickListener(this);

		fileManager = new FileManager();
		Typeface tf = Typeface.createFromAsset(getAssets(),
				AppExtra.AVENIRLSTD_BLACK);
	       

		answer = (EditText) findViewById(R.id.user_secret_question_answer);
		answer.setTypeface(tf);
		password = (EditText) findViewById(R.id.user_password_conf);
		password.setTypeface(tf);
		question = (TextView) findViewById(R.id.user_secret_question);
		passwordConfirm = (EditText) findViewById(R.id.user_new_password_conf);
		passwordConfirm.setTypeface(tf);

		appUserinfo = new AppUserInfo(this);
		String _oldQeustion = appUserinfo.getQuestion();
		_oldAnswer = appUserinfo.getAnswer();

		getActionBar().setTitle(getString(R.string.change_password));
		password.setVisibility(View.VISIBLE);
		passwordConfirm.setVisibility(View.VISIBLE);
		question.setVisibility(View.GONE);
		answer.setHint(getString(R.string.old_password));
		password.setHint(getString(R.string.new_password));
		btnUserInfo.setText(getString(R.string.confirm));

		btnUserInfo.setTypeface(tf);

		question.setText(String.format(getString(R.string.Secret_questiony),
				_oldQeustion));
		setTextWatcher() ;

	}

	 private void createCutomActionBarTitle(int text){
         this.getActionBar().setDisplayShowCustomEnabled(true);
         this.getActionBar().setDisplayShowTitleEnabled(false);
         this.getActionBar().setDisplayShowHomeEnabled(false);
  
         LayoutInflater inflator = LayoutInflater.from(this);
         View v = inflator.inflate(R.layout.custom_action_bar_sec, null);
  
         Typeface tf = Typeface.createFromAsset(getAssets(),AppExtra.AVENIRLSTD_BLACK);
         TextView title = (TextView)v.findViewById(R.id.textView_tit);
         title.setTypeface(tf);
         title.setText(text);
         
         ImageView save = (ImageView) v.findViewById(R.id.imageView_save);
 		save.setVisibility(View.GONE);
         
         ImageView back = (ImageView)v.findViewById(R.id.imageView_back);
         back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				overridePendingTransition(R.anim.push_right_out,
						R.anim.push_right_in);
			}
		});
         
         //assign the view to the actionbar
         this.getActionBar().setCustomView(v);
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

	
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		switch (item.getItemId()) {
//
//		case android.R.id.home:
//			finish();
//			overridePendingTransition(R.anim.push_right_out,
//					R.anim.push_right_in);
//			return true;
//		default:
//			return super.onOptionsItemSelected(item);
//		}
//	}

	@Override
	public void onClick(View arg0) {

//		final String _oldPassword = appUserinfo.getPassword();
//		if (answer.getText().toString().length() != AppExtra.TOTAL_PASSWORD_DIGIT) {
//			AppToast.show(getApplicationContext(),
//					getString(R.string.Enter_4_Digits_Passowrd));
//			return;
//		}
//
//		if (!answer.getText().toString().equals(_oldPassword)) {
//			AppToast.show(getApplicationContext(),
//					getString(R.string.Password_Match));
//			return;
//		}
//
//		if (password.getText().toString().length() != AppExtra.TOTAL_PASSWORD_DIGIT) {
//			AppToast.show(getApplicationContext(),
//					getString(R.string.Enter_4_Digits_Passowrd));
//			return;
//		}
//
//		if (password.getText().toString().length() != AppExtra.TOTAL_PASSWORD_DIGIT) {
//			AppToast.show(getApplicationContext(),
//					getString(R.string.Enter_4_Digits_Confirm_Password));
//			return;
//		}
//
//		if (passwordConfirm.getText().toString().length() != AppExtra.TOTAL_PASSWORD_DIGIT) {
//			AppToast.show(getApplicationContext(),
//					getString(R.string.Enter_4_Digits_Confirm_Password));
//			return;
//		}
//
//		if (!password.getText().toString()
//				.equals(passwordConfirm.getText().toString())) {
//			AppToast.show(getApplicationContext(),
//					getString(R.string.Password_Match));
//			return;
//		}
		

		final String _oldPassword = appUserinfo.getPassword();

		if (answer.getText().toString().length() == 0) {
			answer.requestFocus();
			answer.setError(getString(R.string.empty_field));
			return;
		}

		if ((answer.getText().toString().length() != 0)
				&& (!answer.getText().toString().equals(_oldPassword))) {
			answer.requestFocus();
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
		Intent intent = new Intent(ChangePasswordAvtivity.this,
				LoginWindowActivity.class);
		startActivity(intent);
		// overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

	}

	@Override
	protected void onUserLeaveHint() {
		fileManager.setSettingCode(getApplicationContext());
		super.onUserLeaveHint();
	}

	@Override
	protected void onResume() {
		super.onResume();

		int event_code = fileManager.getReturnCode(getApplicationContext());
		if (event_code == AppExtra.SETTING_CODE) {
			Intent i = new Intent(ChangePasswordAvtivity.this,
					LoginWindowActivity.class);
			startActivity(i);
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
		overridePendingTransition(R.anim.push_right_out, R.anim.push_right_in);
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
