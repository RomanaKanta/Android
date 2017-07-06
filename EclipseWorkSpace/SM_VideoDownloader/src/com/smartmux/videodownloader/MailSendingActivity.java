package com.smartmux.videodownloader;

import com.smartmux.videodownloader.lockscreen.utils.AppExtra;
import com.smartmux.videodownloader.utils.SMConstant;
import com.smartmux.videodownloader.utils.SMSharePref;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MailSendingActivity extends Activity implements OnClickListener{
	
	TextView tvCancel,tvSend = null;
	EditText etTo, etCc, etSub, etBody = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		setContentView(R.layout.mail_layout);
		
		etTo = (EditText)findViewById(R.id.edittext_mail_to);
		etCc = (EditText)findViewById(R.id.edittext_mail_cc);
		etSub = (EditText)findViewById(R.id.edittext_mail_sub);
		etBody = (EditText)findViewById(R.id.edittext_mail_body);
		
		 tvCancel = (TextView)findViewById(R.id.textView_mail_cancel);
		 tvSend = (TextView)findViewById(R.id.textView_mail_send);
		 
		 tvCancel.setOnClickListener(this);
		 tvSend.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.textView_mail_cancel:
			SMSharePref.setBackCode(getApplicationContext());
			finish();
			break;

		case R.id.textView_mail_send:
			
			sendEmail();
			Toast.makeText(getApplicationContext(), "send", 1000).show();
			break;

		default:
			break;

		}
	}

	public void sendEmail() {

		Intent emailIntent = new Intent(Intent.ACTION_SEND,
				Uri.parse("mailto:"));
		emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { etTo.getText().toString()});
		emailIntent.putExtra(Intent.EXTRA_CC, new String[] {etCc.getText().toString()});
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, etSub.getText().toString());
		emailIntent.putExtra(Intent.EXTRA_TEXT, etBody.getText().toString());
		emailIntent.setType("message/rfc822");
		startActivity(Intent.createChooser(emailIntent, "Send Mail"));
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		String security = SMSharePref.getSecurity(MailSendingActivity.this);
		int event_code = SMSharePref.getReturnCode(getApplicationContext());
		if (security.equals(SMConstant.on) && event_code == AppExtra.HOME_CODE) {
//			Toast.makeText(getApplicationContext(),
//					"event_code" + event_code, 1000).show();
			
		Intent i = new Intent(MailSendingActivity.this, AppLockActivity.class);
		i.putExtra("passcode", "password_match");
        startActivity(i);
        overridePendingTransition(R.anim.bottom_up, 0);
	}
		
	}

	 @Override
		protected void onUserLeaveHint() {
		 SMSharePref.setHomeCode(getApplicationContext());
			super.onUserLeaveHint();
	}

	
	
}
