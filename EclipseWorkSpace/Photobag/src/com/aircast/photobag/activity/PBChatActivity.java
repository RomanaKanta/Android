package com.aircast.photobag.activity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aircast.photobag.R;
import com.aircast.photobag.adapter.PBChatMessageContentAdapter;
import com.aircast.photobag.api.PBAPIHelper;
import com.aircast.photobag.api.ResponseHandle;
import com.aircast.photobag.api.ResponseHandle.Response;
import com.aircast.photobag.application.PBApplication;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.database.ChatDatabaseHandler;
import com.aircast.photobag.services.ChatMgsUpadteService;
import com.aircast.photobag.services.OnTaskCompleted;
import com.aircast.photobag.services.PBMessageListTask;
import com.aircast.photobag.utils.PBGeneralUtils;
import com.aircast.photobag.utils.PBPreferenceUtils;
import com.aircast.photobag.widget.PBCustomWaitingProgress;
import com.aircast.photobag.widget.actionbar.ActionBar;
import com.aircast.photobag.widget.actionbar.ActionBar.Action;

public class PBChatActivity extends PBAbsActionBarActivity implements
		OnTaskCompleted {
	private ActionBar mHeaderBar;
	private String mPassword;
	private Context mContext;
	private TextView mTvCountDown;
	private long mCountDownTime = 0;
	private PBCounter mCounter;
	private final long COUNT_DOWN_INTERVAL = 1000;
	private ListView chatMessageList;
	private PBChatMessageContentAdapter adapter;
	private Button mButtonChatState;
	private MessageReceiver messageReceiver = new MessageReceiver();
	private ArrayList<HashMap<String, String>> listOfMessagesInHashMap = new ArrayList<HashMap<String, String>>();
	private EditText mgsTxT;
	private Response response;
	private String deviceUUID;
	private PBCustomWaitingProgress waitingProgress;
	private Action chatAction;
	Animation rotation;
	private boolean isAnimition = false;
	private View mActionView;
	private long timerValue;
	private long mCollectionChargeAt;
	private String mCollectionId;
	private ChatDatabaseHandler db;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.pb_layout_chat_history);
		mContext = this;
		if (getIntent() == null) {
			finish();
			return;
		}
		Bundle extras = getIntent().getBundleExtra("data");
		if (extras == null) {
			finish();
			return;
		}
		
        final boolean hasInternet = PBApplication.hasNetworkConnection();

		db = new ChatDatabaseHandler(
				getApplicationContext());

		final View mRootView = findViewById(R.id.RelativeLayout_rootview);
		mRootView.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						int heightDiff = mRootView.getRootView().getHeight()
								- mRootView.getHeight();
						RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
								RelativeLayout.LayoutParams.FILL_PARENT,
								RelativeLayout.LayoutParams.WRAP_CONTENT);
						LinearLayout lp_mgs_content = (LinearLayout) findViewById(R.id.chat_history_content);
						if (heightDiff > 100) {
							findViewById(R.id.adstir_histroy_detail)
									.setVisibility(View.GONE);
							params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
									RelativeLayout.TRUE);
							
							
							PBPreferenceUtils.saveBoolPref(getApplicationContext(),
									PBConstant.PREF_NAME, PBConstant.PREF_LISTVIEW_SELECTION,
									false);
							int numberOfMesageCount = db.getChatRowCount(mCollectionId);

							if(!hasInternet && numberOfMesageCount==0) {
								// no need to do anything as no data exists
							} else {
								chatMessageList.postDelayed(new Runnable() {
							        @Override
							        public void run() {
							        	//System.out.println("Atik called runnable for update listview");
							        	chatMessageList.setSelection(chatMessageList.getCount());
							        	chatMessageList.smoothScrollToPosition(chatMessageList.getCount());
							        }
							    }, 100);
								
								chatMessageList.setOnTouchListener(new OnTouchListener() {

									@Override
									public boolean onTouch(View arg0, MotionEvent arg1) {
										hideSoftKeyboard(); // hide softkeyboard
										return false;
									}
								});
								// Up the listview 
								chatMessageList.setSelection(chatMessageList.getCount() - 1);
							}


						} else {
							PBPreferenceUtils.saveBoolPref(getApplicationContext(),
									PBConstant.PREF_NAME, PBConstant.PREF_LISTVIEW_SELECTION,
									true);
							params.addRule(RelativeLayout.ABOVE,
									R.id.adstir_histroy_detail);
							findViewById(R.id.adstir_histroy_detail)
									.setVisibility(View.VISIBLE);
							 // Up the listview
					         //mgsList.setSelection(mgsList.getCount() - 1);
							
						}
						
						params.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
								RelativeLayout.TRUE);
						lp_mgs_content.setLayoutParams(params);
					}
				});

		// getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		// // Atik for not showing add below softkeyboard
		mgsTxT = (EditText) findViewById(R.id.message);
		mgsTxT.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		mgsTxT.setFocusableInTouchMode(true);
		final Button btnChatMessageSend = (Button) findViewById(R.id.send_chat_message);
		btnChatMessageSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				if(PBApplication.hasNetworkConnection()) { // If internet available then able to send message otherwise not
					String checkBlankSpace = mgsTxT.getText().toString()
							.replaceAll("\\p{Z}", "");
					Log.d("checkBlankSpace", checkBlankSpace);

					if (checkBlankSpace.length() > 0) {
						if (mgsTxT.getText().toString().length() < PBConstant.CHAT_MAXIMUM_INPUT_CHARACTER) {
							btnChatMessageSend.setEnabled(false);
							String token = PBPreferenceUtils.getStringPref(
									PBApplication.getBaseApplicationContext(),
									PBConstant.PREF_NAME,
									PBConstant.PREF_NAME_TOKEN, "");

							if (!TextUtils.isEmpty(token)) {
								
								Response response = PBAPIHelper.groupStatus(token,
										deviceUUID, mPassword);
								// return response;
								if (response != null) {

									int response_code = response.errorCode;
									String response_body = response.decription;

									System.out
											.println("Atik group status response code:"
													+ response_code);
									System.out
											.println("Atik group status response body:"
													+ response_body);
									try {
										JSONObject jObject;
										jObject = new JSONObject(response_body);

										if (response_code == ResponseHandle.CODE_200_OK) {
											mgsTxT.setFocusableInTouchMode(true);

											// mButtonChatState.setText("ON");
											if (jObject.has("message")) {

												timerValue = jObject
														.getLong("message");
												PBPreferenceUtils.saveLongPref(
														PBApplication
																.getBaseApplicationContext(),
														PBConstant.PREF_NAME,
														PBConstant.PREF_GROUP_EXPIRED_TIME,
														timerValue);
												System.out
														.println("Atik timer value:"
																+ timerValue);
											}
											updateUI(timerValue);
											PBTaskMessasgeSend task = new PBTaskMessasgeSend();
											task.execute();

										} else {
											if (mCounter != null) {
												mCounter.cancel();
											}
											mgsTxT.setFocusable(false);
											mButtonChatState.setBackgroundResource(R.drawable.i_end);
											mButtonChatState.setText(getString(R.string.chat_active_status_end));
											mTvCountDown.setVisibility(View.VISIBLE);
											mTvCountDown.setText("");
											findViewById(R.id.send_chat_message).setEnabled(false); // disable send
											mHeaderBar.removeAllActions(); // remove update refresh button
											Toast.makeText(getApplicationContext(),
													jObject.getString("message"),
													Toast.LENGTH_LONG).show();

										}

									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							}

									
								//}
							
						} else {
							Toast.makeText(getApplicationContext(),
									getString(R.string.chat_input_character_limit),
									Toast.LENGTH_SHORT).show();
						}
				}
				

				}else{

					AlertDialog.Builder	exitDialog =  new AlertDialog.Builder(new ContextThemeWrapper(PBChatActivity.this,
						     R.style.popup_theme));
						exitDialog .setTitle(getString(R.string.pb_chat_message_internet_offline_dialog_title));
						exitDialog .setMessage(getString(R.string.pb_chat_message_internet_offline_dialog_message));
						exitDialog .setCancelable(false);
						exitDialog .setPositiveButton(getString(R.string.dialog_ok_btn),
						       new DialogInterface.OnClickListener() {
						        @Override
						        public void onClick(DialogInterface dialog,
						          int which) {
						        	
						        	
						        	dialog.dismiss();
						        }
								});
						         
						     exitDialog.show();
				}
			}
			
		});

		mgsTxT.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence str, int start,
					int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence str, int start, int before,
					int count) {

			}

			@Override
			public void afterTextChanged(Editable str) {
				String checkBlankSpace = mgsTxT.getText().toString()
						.replaceAll("\\p{Z}", "");
				Log.d("checkBlankSpace", checkBlankSpace);
				if (checkBlankSpace.length() > 0) {

					findViewById(R.id.send_chat_message).setEnabled(true);
				} else {
					findViewById(R.id.send_chat_message).setEnabled(false);
				}
			}
		});

		mPassword = extras.getString(PBConstant.HISTORY_PASSWORD);
		mCollectionChargeAt = extras.getLong(PBConstant.HISTORY_CHARGE_DATE, 0);
		
		mCollectionId = extras.getString(PBConstant.HISTORY_COLLECTION_ID);
		
		PBPreferenceUtils.saveStringPref(
				PBApplication.getBaseApplicationContext(),
				PBConstant.PREF_NAME, PBConstant.PREF_CURRENT_HISTORY_COLLECTION_ID, mCollectionId);

		PBPreferenceUtils.saveStringPref(
				PBApplication.getBaseApplicationContext(),
				PBConstant.PREF_NAME, PBConstant.PREF_GROUP_NAME, mPassword);

		mHeaderBar = (ActionBar) findViewById(R.id.headerbar);
		setHeader(mHeaderBar, mPassword);

		chatAction = new ActionBar.ViewAction(this,
				new ActionBar.PerformActionListener() {
					@Override
					public void performAction(View view) {
						if(PBApplication.hasNetworkConnection()) { // If internet available then can able to press update button

							isAnimition = true;
							mActionView = view;
							rotation = AnimationUtils.loadAnimation(
									PBChatActivity.this,
									R.anim.manual_refresh_btn_roate);
							mActionView.startAnimation(rotation);

							// Atik need to set check for group status
							String token = PBPreferenceUtils.getStringPref(
									PBApplication.getBaseApplicationContext(),
									PBConstant.PREF_NAME,
									PBConstant.PREF_NAME_TOKEN, "");

							if (!TextUtils.isEmpty(token)) {
								Response responseForGroup = PBAPIHelper.groupStatus(token,
										deviceUUID, mPassword);
								// return response;
								if (responseForGroup != null) {

									int response_code = responseForGroup.errorCode;
									String response_body = responseForGroup.decription;

									System.out
											.println("Atik group status response code:"
													+ response_code);
									System.out
											.println("Atik group status response body:"
													+ response_body);
									try {
										JSONObject jObject;
										jObject = new JSONObject(response_body);

										if (response_code == ResponseHandle.CODE_200_OK) {
											mgsTxT.setFocusableInTouchMode(true);

											// mButtonChatState.setText("ON");
											if (jObject.has("message")) {

												timerValue = jObject
														.getLong("message");
												PBPreferenceUtils.saveLongPref(
														PBApplication
																.getBaseApplicationContext(),
														PBConstant.PREF_NAME,
														PBConstant.PREF_GROUP_EXPIRED_TIME,
														timerValue);
												System.out
														.println("Atik timer value:"
																+ timerValue);
											}
											/*mCountDownTime = (timerValue * 1000)
													- System.currentTimeMillis();*/
											updateUI(timerValue);
											
											
																			
										String groupName = PBPreferenceUtils.getStringPref(
														PBApplication.getBaseApplicationContext(),
														PBConstant.PREF_NAME,
														PBConstant.PREF_GROUP_NAME, "");
										
										PBMessageListTask task = new PBMessageListTask(
													PBChatActivity.this, groupName,listOfMessagesInHashMap.size());
		
										try {
											response = task.execute().get();
										} catch (InterruptedException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										} catch (ExecutionException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}


										} else {
											if (mCounter != null) {
												mCounter.cancel();
											}
											mgsTxT.setFocusable(false);
											mButtonChatState.setBackgroundResource(R.drawable.i_end);
											mButtonChatState.setText(getString(R.string.chat_active_status_end));
											mTvCountDown.setVisibility(View.VISIBLE);
											mTvCountDown.setText("");
											findViewById(R.id.send_chat_message).setEnabled(false); // disable send
											mHeaderBar.removeAllActions(); // remove update refresh button
											Toast.makeText(getApplicationContext(),
													jObject.getString("message"),
													Toast.LENGTH_LONG).show();

										}

									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}

								}
							}

						
						} else { // Network connection is not available
							
							
							Toast toast = Toast.makeText(PBChatActivity.this, getString(R.string.pb_network_not_available_general_message), 
									1000);
							TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
							if( v != null) v.setGravity(Gravity.CENTER);
							toast.show();
							
				       	 	/*Toast.makeText(PBChatActivity.this, 
				       			 getString(R.string.pb_network_not_available_general_message), 1000).show();*/							
						}
					}

				}, R.drawable.btn_chat_list_refresh);
		chatAction.setBackground(getResources().getDrawable(
				R.drawable.actionbar_home_btn));
		mHeaderBar.addAction(chatAction);


		mButtonChatState = (Button) findViewById(R.id.button_chat_state);
		mTvCountDown = (TextView) findViewById(R.id.textView_chat_remaingtime);
		
		int numberOfMesageCount = db.getChatRowCount(mCollectionId);
		System.out.println("Atik number of message is:" +numberOfMesageCount );
		
        
        // If there is no meesage and no internet connection, then no need to run the service
        if(!hasInternet && numberOfMesageCount == 0) {
    		/*PBPreferenceUtils.saveIntPref(
			PBApplication.getBaseApplicationContext(),
			PBConstant.PREF_NAME, PBConstant.PREF_MGS_COUNT, 0);*/
			PBPreferenceUtils.saveIntPref(
					PBApplication.getBaseApplicationContext(),
					PBConstant.PREF_NAME, PBConstant.PREF_MGS_COUNT, numberOfMesageCount);
        } else {
    		/*PBPreferenceUtils.saveIntPref(
			PBApplication.getBaseApplicationContext(),
			PBConstant.PREF_NAME, PBConstant.PREF_MGS_COUNT, 0);*/
			PBPreferenceUtils.saveIntPref(
					PBApplication.getBaseApplicationContext(),
					PBConstant.PREF_NAME, PBConstant.PREF_MGS_COUNT, numberOfMesageCount);
			
			listOfMessagesInHashMap = db.getAllMessage(mCollectionId);
		
		
			chatMessageList = (ListView) findViewById(R.id.list_messageHistory);
			adapter = new PBChatMessageContentAdapter(PBChatActivity.this, listOfMessagesInHashMap);
			chatMessageList.setAdapter(adapter);
			
			chatMessageList.postDelayed(new Runnable() {
		        @Override
		        public void run() {
		        	//System.out.println("Atik called runnable for update listview");
		        	chatMessageList.setSelection(chatMessageList.getCount() -1 );
		        }
		    }, 100);
		
			deviceUUID = PBPreferenceUtils.getStringPref(
					PBApplication.getBaseApplicationContext(),
					PBConstant.PREF_NAME, PBConstant.PREF_NAME_UID, "0");
		
			waitingProgress = new PBCustomWaitingProgress(PBChatActivity.this);
			
			
			if(listOfMessagesInHashMap.size() < 1){
				
				if (waitingProgress != null)
					waitingProgress.showWaitingLayout();
			}
			
			
			
			if(PBApplication.hasNetworkConnection()) { 
				PBTaskGroupStatus task = new PBTaskGroupStatus();
				task.execute();
				
			}
			
        }
        
        IntentFilter i = new IntentFilter();
		i.addAction("photobag.chat.mgslist");
		i.addAction("photobag.chat.groupstatus");
		registerReceiver(messageReceiver, i);
		
		this.startService(new Intent(mContext, ChatMgsUpadteService.class));

	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
//		if(PBApplication.hasNetworkConnection()) { 
//			if(messageReceiver != null) {
//				try {
//					unregisterReceiver(messageReceiver); 
//					messageReceiver = null;
//				} catch (Exception e ) {
//					// do nothing
//				}
//				this.stopService(new Intent(mContext, ChatMgsUpadteService.class));
//			}
//
//		} else {
//			if(messageReceiver != null) {
//				try {
//					unregisterReceiver(messageReceiver); 
//					messageReceiver = null;
//				} catch (Exception e ) {
//					// do nothing
//				}
//				this.stopService(new Intent(mContext, ChatMgsUpadteService.class));
//			}
//		}
		
		this.stopService(new Intent(mContext, ChatMgsUpadteService.class));

	}

	@Override
	protected void onResume() {
		super.onResume();
			hideSoftKeyboard(); // hide softkeyboard

	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if(mCounter != null) {
			
			mCounter.cancel();
		}
		
		if(messageReceiver != null) {
			try {
				unregisterReceiver(messageReceiver); 
				messageReceiver = null;
			} catch (Exception e ) {
				// do nothing
			}
		}
	}
	
	@Override
	public void onBackPressed(){
		
		if(mCounter != null) {
			
			mCounter.cancel();
		}
		
		finish();
		
	}


	public class MessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			Log.d("new mgs", "new convertion");
			Log.d("intent.getAction()", intent.getAction());

			if (intent != null) {

				if (intent.getAction().equals("photobag.chat.groupstatus")) {

					if (intent.getExtras() != null) {

						int groupStatus = intent.getExtras().getInt("status");
						if (groupStatus == 1) {
							mgsTxT.setFocusableInTouchMode(true);
							timerValue = PBPreferenceUtils.getLongPref(
									PBApplication.getBaseApplicationContext(),
									PBConstant.PREF_NAME,
									PBConstant.PREF_GROUP_EXPIRED_TIME, 0);
							
							Log.d("timerValue", ""+timerValue);
							System.out.println("Atik timer value from server in onReceive"+timerValue);
							updateUI(timerValue);

						} else {
							if (mCounter != null) {
								mCounter.cancel();
							}
							String mgs = intent.getExtras().getString("mgs");
							mgsTxT.setFocusable(false);
							mButtonChatState.setBackgroundResource(R.drawable.i_end);
							mButtonChatState.setText(getString(R.string.chat_active_status_end));
							mTvCountDown.setVisibility(View.VISIBLE);
							mTvCountDown.setText("");
							findViewById(R.id.send_chat_message).setEnabled(false); // disable send
							mHeaderBar.removeAllActions(); // remove update refresh button
							Toast.makeText(getApplicationContext(),
									mgs, Toast.LENGTH_LONG)
									.show();
						}
					}
				}

				if (intent.getAction().equals("photobag.chat.mgslist")) {

					// listOfMgs.clear();

					if (intent.getExtras() != null) {
						if (intent.getExtras().getInt("noDataExist", 0) == 1) {
							System.out
									.println("Atik no data exist in send broadcast");
							if (waitingProgress != null)
								waitingProgress.hideWaitingLayout();
						} else {
							if (waitingProgress != null)
								waitingProgress.hideWaitingLayout();

							
							listOfMessagesInHashMap.clear();
							listOfMessagesInHashMap = db.getAllMessage(mCollectionId);

							PBPreferenceUtils
									.saveIntPref(PBApplication
											.getBaseApplicationContext(),
											PBConstant.PREF_NAME,
											PBConstant.PREF_MGS_COUNT,
											listOfMessagesInHashMap.size());
							adapter = new PBChatMessageContentAdapter(PBChatActivity.this,
							         listOfMessagesInHashMap);
							chatMessageList.setAdapter(adapter);
							adapter.notifyDataSetChanged();
							chatMessageList.post(new Runnable() {
								public void run() {
									chatMessageList.setSelection(chatMessageList.getCount() - 1);
								}
							});
						}
					}

				}
			}

		}

	};

	private void updateUI(long timeUntil) {
		if (mCounter != null) {
			mCounter.cancel();
		}
		
		System.out.println("Atik timer value in updateUI method:"+timeUntil);
		
		timeUntil = (timeUntil * 1000);
		Log.d("mCountDownTime", ""+mCountDownTime);
		Log.d("get value", ""+timeUntil);
		Log.d("mCollectionChargeAt", ""+mCollectionChargeAt);
		
		mTvCountDown.setVisibility(View.VISIBLE);
			mTvCountDown.setText(getString(R.string.chat_password_free_time)
					+ parseCountDownToTimeSring(timeUntil));
			mCounter = new PBCounter(timeUntil, COUNT_DOWN_INTERVAL);
			mCounter.start();


	}

	public final static int CHANGE_TIME_REQ_CODE = 10021;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == CHANGE_TIME_REQ_CODE && resultCode == RESULT_OK) {
			if (data.getExtras() != null) {
				long timeUntil = data.getLongExtra("time_until_in_seconds", -1);

				// updateUI(timeUntil);
				mCollectionChargeAt = timeUntil;
			}
		}
	}
	
	

	public class PBCounter extends CountDownTimer {

		public PBCounter(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			//System.out.println("Atik called on finish");
			/*mTvCountDown.setText(getString(R.string.chat_password_free_time)
					+ R.string.screen_confirm_password_time_default);*/
			if (mCounter != null) {
				mCounter.cancel();
			}
			mgsTxT.setFocusable(false);
			mButtonChatState.setBackgroundResource(R.drawable.i_end);
			mButtonChatState.setText(getString(R.string.chat_active_status_end));
			mTvCountDown.setVisibility(View.VISIBLE);
			mTvCountDown.setText("");
			findViewById(R.id.send_chat_message).setEnabled(false); // disable send
			mHeaderBar.removeAllActions(); // remove update refresh button
			return;
		}

		@Override
		public void onTick(long millisUntilFinished) {
			
			
			//System.out.println("Atik called on every tick");
			if (millisUntilFinished <= 0) {
				//System.out.println("Atik called tick when value is 0");
				if (mCounter != null) {
					mCounter.cancel();
				}
				mgsTxT.setFocusable(false);
				mButtonChatState.setBackgroundResource(R.drawable.i_end);
				mButtonChatState.setText(getString(R.string.chat_active_status_end));
				mTvCountDown.setVisibility(View.VISIBLE);
				mTvCountDown.setText("");
				findViewById(R.id.send_chat_message).setEnabled(false); // disable send
				mHeaderBar.removeAllActions(); // remove update refresh button
				return;

			}
			mgsTxT.setFocusableInTouchMode(true);
			mTvCountDown.setText(getString(R.string.chat_password_free_time)
					+ parseCountDownToTimeSring(millisUntilFinished));
		}

	}

	private String parseCountDownToTimeSring(long countDownTime) {
		long time = countDownTime / 1000;
		int seconds = (int) (time % 60);
		time = time / 60; // calculate to minutes
		int minutes = (int) (time % 60);
		time = time / 60; // calculate to hours
		int hours = (int) time;

		DecimalFormat formatter = new DecimalFormat("#00");
		DecimalFormat hourFormater;
		if (hours >= 999) {
			hourFormater = new DecimalFormat("#0000");
		} else if (hours > 99) {
			hourFormater = new DecimalFormat("#000");
		} else {
			hourFormater = new DecimalFormat("#00");
		}

//		Log.d("parseCountDownToTimeSring", String.format("%s:%s:%s", hourFormater.format(hours),
//				formatter.format(minutes), formatter.format(seconds)));
		return String.format("%s:%s:%s", hourFormater.format(hours),
				formatter.format(minutes), formatter.format(seconds));
	}

	@Override
	protected void handleHomeActionListener() {
		// TODO Auto-generated method stub
		if(mCounter != null) {
			
			mCounter.cancel();
		}
		finish();
	}

	@Override
	protected void setLeftBtnsDetailHeader(ActionBar headerBar) {
		// TODO Auto-generated method stub

	}

	private class PBTaskGroupStatus extends AsyncTask<Void, Void, Response> {

		@Override
		protected Response doInBackground(Void... params) {
			String token = PBPreferenceUtils.getStringPref(
					PBApplication.getBaseApplicationContext(),
					PBConstant.PREF_NAME, PBConstant.PREF_NAME_TOKEN, "");

			if (!TextUtils.isEmpty(token)) {
				Response response = PBAPIHelper.groupStatus(token, deviceUUID,
						mPassword);
//				if (response != null) {
//					int response_code = response.errorCode;
//
//						if (response_code == ResponseHandle.CODE_200_OK) {
//							
//							new PBMessageListTask(PBChatActivity.this, mPassword,0).execute();
//						}
//					
//				}
				
				return response;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Response result) {
			super.onPostExecute(result);

			if (result != null) {

				int response_code = result.errorCode;
				String response_body = result.decription;
				try {
					JSONObject jObject;
					jObject = new JSONObject(response_body);

					if (response_code == ResponseHandle.CODE_200_OK) {
						mgsTxT.setFocusableInTouchMode(true);
						// mButtonChatState.setText("ON");
						if (jObject.has("message")) {

							timerValue = jObject.getLong("message");
							PBPreferenceUtils.saveLongPref(
									PBApplication.getBaseApplicationContext(),
									PBConstant.PREF_NAME,
									PBConstant.PREF_GROUP_EXPIRED_TIME,
									timerValue);
							System.out
									.println("Atik timer value in group status check:" + timerValue);
							
						}
						/*mCountDownTime = (timerValue * 1000)
								- System.currentTimeMillis();*/
						updateUI(timerValue);

					} else {

						//updateUI(0);
						if (mCounter != null) {
							mCounter.cancel();
						}
						mgsTxT.setFocusable(false);
						mButtonChatState.setBackgroundResource(R.drawable.i_end);
						mButtonChatState.setText(getString(R.string.chat_active_status_end));
						mTvCountDown.setVisibility(View.VISIBLE);
						mTvCountDown.setText("");
						findViewById(R.id.send_chat_message).setEnabled(false); // disable send
						mHeaderBar.removeAllActions(); // remove update refresh button
						Toast.makeText(getApplicationContext(),
								jObject.getString("message"), Toast.LENGTH_LONG)
								.show();

					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				try {
					Log.e("AGUNG", "Error :" + result.errorCode + " -> "
							+ result.decription);
				} catch (Exception e) {// TODO
					e.printStackTrace();
					Log.e("AGUNG", "Error :" + result.errorCode + " -> "
							+ result.decription);
				}
			}
		}

	}

	private class PBTaskMessasgeSend extends AsyncTask<Void, Void, Response> {
		ProgressDialog progressDelete;

		@Override
		protected Response doInBackground(Void... params) {
			String token = PBPreferenceUtils.getStringPref(
					PBApplication.getBaseApplicationContext(),
					PBConstant.PREF_NAME, PBConstant.PREF_NAME_TOKEN, "");

			if (!TextUtils.isEmpty(token)) {
				Response response = PBAPIHelper.sendMessage(token, deviceUUID,
						mPassword, mgsTxT.getText().toString());

				System.out.println("Atik send message response"
						+ response.toString());

				return response;
			}
			return null;
		}

		@Override
		protected void onPreExecute() {
			progressDelete = new ProgressDialog(mContext,
					R.style.MyProgressDialogTheme);

			if (mContext != null) {
				progressDelete.setCanceledOnTouchOutside(false);
				progressDelete
						.setProgressStyle(android.R.style.Widget_ProgressBar_Small);

				progressDelete.show();
			}
		}

		@Override
		protected void onPostExecute(Response result) {
			super.onPostExecute(result);

			if (result != null) {

				int response_code = result.errorCode;
				JSONObject jObjectError = null;

				String respone_description = result.decription;
				try {
					jObjectError = new JSONObject(respone_description);
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				System.out.println("Atik response send message"
						+ respone_description);
				if (response_code == ResponseHandle.CODE_200_OK) {

					JSONObject jObject;
					try {
						jObject = new JSONObject(respone_description);

						if (jObject != null) {
							if (jObject.has("status")) {

								if (jObject.getString("status").equals("OK")) {
									mgsTxT.setFocusableInTouchMode(true);
									String groupName = PBPreferenceUtils
											.getStringPref(
													PBApplication
															.getBaseApplicationContext(),
													PBConstant.PREF_NAME,
													PBConstant.PREF_GROUP_NAME,
													"");
									
									PBMessageListTask task = new PBMessageListTask(
											PBChatActivity.this, groupName,
											listOfMessagesInHashMap.size());

									try {

										response = null;
										response = task.execute().get();
										chatMessageList.setSelection(chatMessageList.getCount() - 1);
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (ExecutionException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									// task.execute();
								}



							}

						}
					} catch (JSONException e) {
						e.printStackTrace();
					}

				} else if (response_code == ResponseHandle.CODE_403) {

				} else
					try {
						if(jObjectError.getString("status").equals("NG")) {

							PBGeneralUtils.showAlertDialogNoTitleWithOnClick(
									mContext,
									""+jObjectError.getString("message"));
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

			} else {
				try {
					Log.e("AGUNG", "Error :" + result.errorCode + " -> "
							+ result.decription);
				} catch (Exception e) {// TODO
					e.printStackTrace();
					Log.e("AGUNG", "Error :" + result.errorCode + " -> "
							+ result.decription);
				}
			}

			mgsTxT.setText("");
			findViewById(R.id.send_chat_message).setEnabled(true);

			if (mContext != null && progressDelete != null) {
				progressDelete.cancel();
			}
		}

	}

	@Override
	public void onTaskCompleted() {

		if (waitingProgress != null)
			waitingProgress.hideWaitingLayout();
		// change here for nullcharacterException
		if (isAnimition) {

			isAnimition = false;
			if(mActionView != null){
				mActionView.clearAnimation();
			}
		}

		if (response != null) {
			int response_code = response.errorCode;
			String respone_description = response.decription;
			System.out.println("Atik response for refresh data"
					+ respone_description);
			Log.d(" ac respone_description", respone_description);
			if (response_code == ResponseHandle.CODE_200_OK) {
				mgsTxT.setFocusableInTouchMode(true);
				JSONObject jObject;
				try {
					jObject = new JSONObject(respone_description);

					if (jObject != null) {
						if (jObject.has("messages")) {

							JSONArray resultArray = jObject
									.getJSONArray("messages");
							for (int i = 0; i < resultArray.length(); i++) {
								JSONObject obj = resultArray.getJSONObject(i);
								/*HashMap<String, String> map = new HashMap<String, String>();


								map.put("message", obj.getString("message"));
								map.put("uid", obj.getString("uid"));
								map.put("created", obj.getString("created"))*/;
//								db.addMessage(obj.getString("uid"), obj.getString("message"), obj.getString("created")
//										, mCollectionId);
								db.addMessage(obj.getString("uid"), obj.getString("message"), obj.getString("created")
										, mCollectionId,obj.getString("nickname"),obj.getString("color"));

								//listOfMessagesInHashMap.add(map);
							}
							listOfMessagesInHashMap.clear();
							listOfMessagesInHashMap = db.getAllMessage(mCollectionId);
							PBPreferenceUtils.saveIntPref(
									PBApplication.getBaseApplicationContext(),
									PBConstant.PREF_NAME,
									PBConstant.PREF_MGS_COUNT,
									listOfMessagesInHashMap.size());
							
							/*PBPreferenceUtils
									.saveIntPref(PBApplication
											.getBaseApplicationContext(),
											PBConstant.PREF_NAME,
											PBConstant.PREF_MGS_COUNT,
											listOfMessagesInHashMap.size());*/
							System.out.println("Atik called notify listview");
							adapter = new PBChatMessageContentAdapter(PBChatActivity.this,
							         listOfMessagesInHashMap);
							chatMessageList.setAdapter(adapter);
							adapter.notifyDataSetChanged();
							chatMessageList.setSelection(chatMessageList.getCount() - 1);

						}

					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

		}

	}


	/**
	 * method support hide soft keyboard on phone screen.
	 */
	private void hideSoftKeyboard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mgsTxT.getWindowToken(), 0);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		boolean fromSoftKeyboard = PBPreferenceUtils.getBoolPref(
                getApplicationContext(), 
                PBConstant.PREF_NAME, 
                PBConstant.PREF_LISTVIEW_SELECTION, 
                false);

		if(!fromSoftKeyboard) {
			chatMessageList.postDelayed(new Runnable() {
		        @Override
		        public void run() {
		        	//System.out.println("Atik called runnable for update listview");
		        	chatMessageList.setSelection(chatMessageList.getCount());
		        	chatMessageList.smoothScrollToPosition(chatMessageList.getCount());
		        }
		    }, 100);
		}
	}

}
