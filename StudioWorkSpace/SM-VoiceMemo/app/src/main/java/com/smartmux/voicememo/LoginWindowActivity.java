package com.smartmux.voicememo;

import java.util.ArrayList;

import com.smartmux.voicememo.utils.AppActionBar;
import com.smartmux.voicememo.utils.AppExtra;
import com.smartmux.voicememo.utils.AppToast;
import com.smartmux.voicememo.utils.AppUserInfo;
import com.smartmux.voicememo.utils.FileManager;

import android.os.Vibrator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class LoginWindowActivity extends AppMainActivity{

	int numberCount = 0;
	private TextView	textViewPassword;
	private TextView	textViewStatus;
	private AppUserInfo appUserinfo;
	private String		user_password = null;
	
	final ArrayList<ImageButton> buttons = new ArrayList<ImageButton>();
	private FileManager fileManager;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		fileManager		= new FileManager();

		setContentView(R.layout.lockscreen);
		
		AppActionBar.updateAppActionBar(getActionBar(), this,false);
		getActionBar().setTitle("");
		
		textViewPassword	= (TextView)findViewById(R.id.passwordField);
		textViewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		textViewPassword.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
		
		textViewStatus	= (TextView)findViewById(R.id.passwordStatus);
		
		buttons.add((ImageButton)findViewById(R.id.num_1));
		buttons.add((ImageButton)findViewById(R.id.num_2));
		buttons.add((ImageButton)findViewById(R.id.num_3));
		buttons.add((ImageButton)findViewById(R.id.num_4));
		buttons.add((ImageButton)findViewById(R.id.num_5));
		buttons.add((ImageButton)findViewById(R.id.num_6));
		buttons.add((ImageButton)findViewById(R.id.num_7));
		buttons.add((ImageButton)findViewById(R.id.num_8));
		buttons.add((ImageButton)findViewById(R.id.num_9));
		buttons.add((ImageButton)findViewById(R.id.num_0));
		
		appUserinfo = new AppUserInfo(this);
		appUserinfo.createRootFolder();
		user_password = appUserinfo.getPassword();
		
		if(user_password == null || user_password.length() == 0){
			showRegistrationWindow();
		}
	}
	
	private void doactiveAllButtons(boolean flag)
	{
		int childcount = buttons.size();
		for (int i=0; i < childcount; i++){
		      ImageButton button = buttons.get(i);
		      button.setClickable(flag);
		      float alpha = flag ? 1.0f : 0.50f;
		      AlphaAnimation alphaUp = new AlphaAnimation(alpha, alpha);
		      alphaUp.setFillAfter(true);
		      button.startAnimation(alphaUp);
		}
	}
	
	public void onClick(View v){
		if(v.getTag().equals("clear")){
			textViewPassword.setText("");
			numberCount = 0;
			doactiveAllButtons(true);
			textViewStatus.setText("Enter Password");
		}else if(v.getTag().equals("enter")){
			
			if(textViewPassword.getText().toString().length() < 4){
				textViewStatus.setText("Password Must Be in 4 Digits");
				textViewPassword.setText("");
				numberCount = 0;
			}else if(user_password.equals(textViewPassword.getText().toString())){
				fileManager.setBackCode(getApplicationContext());
    			finish();
    	        overridePendingTransition (R.anim.goto_right_next, R.anim.close_main);

    	        textViewPassword.setText("");
    			numberCount = 0;
    			doactiveAllButtons(true);
    			textViewStatus.setText("Enter Password");
    	        
			}else{
				
				Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
			    findViewById(R.id.loginContent).startAnimation(shake);
			    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			    vibrator.vibrate(1000);
				textViewPassword.setText("");
				numberCount = 0;
				textViewStatus.setText("Invalid Password");
				doactiveAllButtons(true);
			}
		}else{
			numberCount ++;
			textViewPassword.setText(textViewPassword.getText() + v.getTag().toString());
			if(numberCount == AppExtra.TOTAL_PASSWORD_DIGIT){
				doactiveAllButtons(false);
			}
		}
	}
	
	private void showRegistrationWindow()
	{
		LayoutInflater factory = LayoutInflater.from(this);

		final View textEntryView = factory.inflate(R.layout.user_registration, null);
		
		final EditText question = (EditText) textEntryView.findViewById(R.id.user_secret_question);
		final EditText answer = (EditText) textEntryView.findViewById(R.id.user_secret_answer);
		final EditText password = (EditText) textEntryView.findViewById(R.id.user_secret_password);
		final EditText confrimPassword = (EditText) textEntryView.findViewById(R.id.user_secret_confirm_password);
		
		 AlertDialog.Builder alert = new AlertDialog.Builder(this);
	        alert.setTitle("Registration"); 
	        alert.setView(textEntryView);
	        alert.setCancelable(false);
	        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int whichButton) {
	        }
	    });
	        final AlertDialog dialog = alert.create();
	        dialog.show();
	        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
	        {            
	            @Override
	            public void onClick(View v)
	            {
		    		String questionText = question.getText().toString().trim();
		    		String answerText = answer.getText().toString().trim();
		    		String passwordText = password.getText().toString().trim();
		    		String confrimPasswordText = confrimPassword.getText().toString().trim();
		        	
		        	if(questionText.length() == 0){
		        		AppToast.show(getApplicationContext(), "Secret Question can't be Empty");
		        		return;
		        	}
		        	
		        	if(answerText.length() == 0){
		        		AppToast.show(getApplicationContext(), "Secret Answer can't be Empty");
		        		return;
		        	}

		        	if(passwordText.length() != AppExtra.TOTAL_PASSWORD_DIGIT){
		        		AppToast.show(getApplicationContext(), "Enter 4 Digits Passowrd");
		        		return;
		        	}
		        	
		        	if(confrimPasswordText.length() != AppExtra.TOTAL_PASSWORD_DIGIT){
		        		AppToast.show(getApplicationContext(), "Enter 4 Digits Confirm Password");
		        		return;
		        	}

		        	if(!passwordText.equals(confrimPasswordText)){
		        		AppToast.show(getApplicationContext(), "Password Doesn't Match");
			        	return;
		        	}
		        	AppToast.show(getApplicationContext(), "Registration completed successfully!");
		        	appUserinfo.setQuestion(questionText);
		        	appUserinfo.setAnswer(answerText);
		        	appUserinfo.setPassword(passwordText);
		        	
		        	user_password = passwordText;
		        	dialog.dismiss();
	            }
	        });
	}
	
	private void showForgetPasswordWindow()
	{
		LayoutInflater factory = LayoutInflater.from(this);

		final View textEntryView = factory.inflate(R.layout.forgot_password, null);
		
		final EditText question = (EditText) textEntryView.findViewById(R.id.user_secret_question_answer);
		
		String _oldQeustion	 	= appUserinfo.getQuestion();
		final String _oldAnswer = appUserinfo.getAnswer();
		
		 AlertDialog.Builder alert = new AlertDialog.Builder(this);
		 
		 	alert.setTitle("Forget Password?");
	        alert.setMessage(_oldQeustion);
	        alert.setView(textEntryView);
	        alert.setCancelable(false);
	        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int whichButton) {
	        	if(!question.getText().toString().trim().equals(_oldAnswer)){
	        		AppToast.show(getApplicationContext(), "Your answer doesn't match");
		        	return;
	        	}
	        	showPasswordDialog();
	        	dialog.dismiss();
	        }
	    });
	    alert.show();
	}
	
	private void showPasswordDialog(){
		final String _oldPassword 	= appUserinfo.getPassword();
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				  new ContextThemeWrapper(this, android.R.style.Theme_Dialog));
		alertDialog.setMessage(" Your password is "+_oldPassword);
		alertDialog.setPositiveButton("Thanks!", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog,int which) {
	            	dialog.cancel();
	            }
	        });
	    alertDialog.show();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_login_window_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_forget_password:{
			showForgetPasswordWindow();
			return true;
		}
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	 public boolean onKeyDown(int keyCode, KeyEvent event) {
	        if (keyCode == KeyEvent.KEYCODE_BACK){
	        	Intent intent = new Intent(getApplicationContext(), AudioListActivity.class);
	        	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        	intent.putExtra("EXIT", true);
	        	startActivity(intent);
	            return true;
	        }
	        return super.onKeyDown(keyCode, event);
	 }
	
}
