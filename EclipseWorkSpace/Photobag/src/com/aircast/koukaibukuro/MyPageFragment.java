/*package com.aircast.koukaibukuro;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.aircast.koukaibukuro.SearchPageFragment.SearchCommonAdapter;
import com.aircast.koukaibukuro.adapter.CommonAdapter;
import com.aircast.koukaibukuro.adapter.HistoryAdapter;
import com.aircast.koukaibukuro.database.DatabaseHandler;
import com.aircast.koukaibukuro.helper.PBTaskGetGetFavouriteList;
import com.aircast.koukaibukuro.model.Password;
import com.aircast.koukaibukuro.util.ApiHelper;
import com.aircast.koukaibukuro.util.Constant;
import com.aircast.koukaibukuro.util.ProgressHUD;
import com.aircast.koukaibukuro.util.SPreferenceUtils;
import com.aircast.koukaibukuro.util.Util;
import com.aircast.koukaibukuro.util.ApiHelper.Response;
import com.kayac.photobag.R;
import com.kayac.photobag.activity.PBMyPageAcornExchangeListActivity;
import com.kayac.photobag.activity.PBTabBarActivity;
import com.kayac.photobag.application.PBConstant;
import com.kayac.photobag.database.PBDatabaseDefinition;
import com.kayac.photobag.database.PBDatabaseManager;
import com.kayac.photobag.model.PBHistoryEntryModel;
import com.kayac.photobag.utils.PBPreferenceUtils;

public class MyPageFragment extends Fragment implements OnClickListener {

	private View view;
	boolean isOpen = false;
	private EditText nickName;
	private ArrayList<HashMap<String, String>> historyItemList = new ArrayList<HashMap<String, String>>();
	private ArrayList<PBHistoryEntryModel> inboxDataList;
	private ArrayList<PBHistoryEntryModel> sentDataList;
	private List<Password> list = new ArrayList<Password>();
	private ListView listview;
	private ListView pullListview;
	private HistoryAdapter historyAdapter;
	private CommonAdapter adapter;
	private DatabaseHandler db;
	private PBDatabaseManager mDatabaseManager;
	Boolean myReceiverIsRegistered = false;
	LinearLayout pullToRefreshContent;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.kb_mypage_fragment, container, false);

		db = new DatabaseHandler(getActivity());
		pullListview = (ListView) view.findViewById(R.id.pull_listView_common);
		listview = (ListView) view.findViewById(R.id.listView_common);
		
		
		pullToRefreshContent = (LinearLayout) view.findViewById(R.id.pull_to_refresh_content);
		
		
		nickName = (EditText) view.findViewById(R.id.editText_nickname);

		nickName.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				// serchString.setGravity(Gravity.LEFT);
				System.out.println("Atik called when touch");
				nickName.setHint("");
				// System.out.println("Atik called when typing");
				nickName.setFocusable(true);
				nickName.setCursorVisible(true);
				// serchString.setFocusableInTouchMode(true);
				((InputMethodManager) getActivity().getSystemService(
						Context.INPUT_METHOD_SERVICE)).toggleSoftInput(
						InputMethodManager.SHOW_FORCED,
						InputMethodManager.HIDE_IMPLICIT_ONLY);
				return false;
			}
		});

		view.findViewById(R.id.button_nickname_registration)
				.setOnClickListener(this);

		// get value from SimpleApp
		Bundle bundel = getActivity().getIntent().getExtras();

		int mapleCounter = bundel.getInt("mapleCounter");
		int acronsCounter = bundel.getInt("acronsCounter");
		int goldCounter = bundel.getInt("goldCounter");

		TextView txtMapleCounterContent = (TextView) view
				.findViewById(R.id.textView113);
		TextView txtAcronsCounterContent = (TextView) view
				.findViewById(R.id.textView123);
		TextView txtGoldCounterContent = (TextView) view
				.findViewById(R.id.textVie133);

		txtMapleCounterContent.setText(String.valueOf(acronsCounter));
		txtAcronsCounterContent.setText(String.valueOf(mapleCounter));
		txtGoldCounterContent.setText(String.valueOf(goldCounter));

		view.findViewById(R.id.button_donguri).setOnClickListener(this);

		RadioGroup radGrp = (RadioGroup) view
				.findViewById(R.id.radioGroup_mypage);
		radGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup arg0, int id) {

				if (id == R.id.mypage_segmented_btn1) {
					radioButtonTask();
				} else if (id == R.id.mypage_segmented_btn2) {
					radioButtonTask();
				} else if (id == R.id.mypage_segmented_btn3) {
					radioButtonTask();

				}

			}

		});
		

		if (Util.isOnline(getActivity())) {
			PBTaskGetNickName task = new PBTaskGetNickName();
			task.execute();
		}
		
		
		try {
			mDatabaseManager = PBDatabaseManager
					.getInstance(PBTabBarActivity.sMainContext);
			historyItemList = mDatabaseManager.getAllHistoriesCursor();
			inboxDataList = mDatabaseManager.getAcceptedHistories(PBDatabaseDefinition.HISTORY_INBOX);
			sentDataList = mDatabaseManager.getAcceptedHistories(PBDatabaseDefinition.HISTORY_SENT);

			for (PBHistoryEntryModel item : inboxDataList) {

					if (db.isFavouriteItemExists(item.getEntryPassword())) {

						db.deleteFavouriteItem(item.getEntryPassword());
					}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		historyAdapter = new HistoryAdapter(getActivity(),
				inboxDataList, false);
		listview.setAdapter(historyAdapter);
		pullToRefreshContent.setVisibility(View.GONE);
		refreashadapter();
		radioButtonTask();
		

		return view;
	}
	
	private void radioButtonTask() {

		RadioGroup radGrp = (RadioGroup) view
				.findViewById(R.id.radioGroup_mypage);
		       int id = radGrp.getCheckedRadioButtonId();
		       
		       if (id == R.id.mypage_segmented_btn1) {
					listview.setAdapter(null);
					listview.setVisibility(View.VISIBLE);
					pullToRefreshContent.setVisibility(View.GONE);
					historyAdapter = new HistoryAdapter(getActivity(),
							inboxDataList, false);
					listview.setAdapter(historyAdapter);
					
					historyAdapter.notifyDataSetChanged();
					refreashadapter();
				} else if (id == R.id.mypage_segmented_btn2) {
					
					listview.setVisibility(View.VISIBLE);
					pullToRefreshContent.setVisibility(View.GONE);
					listview.setAdapter(null);
					historyAdapter = new HistoryAdapter(getActivity(),
							sentDataList, true);
					listview.setAdapter(historyAdapter);
					historyAdapter.notifyDataSetChanged();
					refreashadapter();
				} else if (id == R.id.mypage_segmented_btn3) {

					boolean isPasswordFavouriteDataSaved = SPreferenceUtils
							.getBoolPref(getActivity(), Constant.PREF_NAME,
									Constant.KB_FAVOURITE_JSON_ISSAVED, false);

					if (isPasswordFavouriteDataSaved) {

						loadDataFromLocal();

					} else {
						if (Util.isOnline(getActivity())) {

							PBTaskGetGetFavouriteList task = new PBTaskGetGetFavouriteList(
									getActivity(), true, true);
							task.execute();
						}

					}

				}

	}

	private FavroiteReceiver mReceiver = new FavroiteReceiver();

	public class FavroiteReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent != null) {

				if (intent.getAction().equals("koukaibukuro.favouritelist")) {
					boolean isPasswordFavouriteDataSaved = SPreferenceUtils
							.getBoolPref(getActivity(), Constant.PREF_NAME,
									Constant.KB_FAVOURITE_JSON_ISSAVED, false);

					if (isPasswordFavouriteDataSaved) {
						RadioGroup radGrp = (RadioGroup) view
								.findViewById(R.id.radioGroup_mypage);
						int selectedId = radGrp.getCheckedRadioButtonId();
						if (selectedId == R.id.mypage_segmented_btn3) {

							loadDataFromLocal();
						}

					}

				}

			}
		}

	};

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);

		if (!hidden) {
			
			try {
				historyItemList = mDatabaseManager.getAllHistoriesCursor();
				inboxDataList = mDatabaseManager.getAcceptedHistories(PBDatabaseDefinition.HISTORY_INBOX);
				sentDataList = mDatabaseManager.getAcceptedHistories(PBDatabaseDefinition.HISTORY_SENT);

				for (PBHistoryEntryModel item : inboxDataList) {

					if (item.getAccepted() == 1) {
						if (db.isFavouriteItemExists(item.getEntryPassword())) {

							db.deleteFavouriteItem(item.getEntryPassword());
						}
					}

				}


			} catch (Exception e) {
				e.printStackTrace();
			}
			
			radioButtonTask();
		}else{
			
			hideSoftKeyboard();
		}

	}

	private void loadDataFromLocal() {
		list.clear();
		list = db.getAllFavouriteItem();
		listview.setVisibility(View.GONE);
		pullToRefreshContent.setVisibility(View.VISIBLE);
		listview.setAdapter(null);
		adapter = new CommonAdapter(getActivity(), list, true, false,
				historyItemList, false);
		pullListview.setAdapter(adapter);
		adapter.notifyDataSetChanged();

	}

	@Override
	public void onResume() {
		Log.e("DEBUG", "onResume of MyPageFragment");
		super.onResume();
		hideSoftKeyboard();
		IntentFilter intent = new IntentFilter();
		intent.addAction("koukaibukuro.favouritelist");
		getActivity().registerReceiver(mReceiver, intent);
		if(!myReceiverIsRegistered){
			   myReceiverIsRegistered = true;
			   getActivity().registerReceiver(mReceiver, intent);
			   
	     }
		
		int tabPosition = PBPreferenceUtils.getIntPref(getActivity().getApplicationContext(), PBConstant.PREF_NAME, 
    			PBConstant.TAB_POSITION, 0);
		
		
		if(tabPosition == 2true){
			boolean isFromDownload = PBPreferenceUtils.getBoolPref(getActivity().getApplicationContext(), PBConstant.PREF_NAME, 
			        PBConstant.ISDOWNLOAD, false);
			System.out.println("Atik is from dowload:"+isFromDownload);
			   if(isFromDownloadtrue) {
			    
			    PBPreferenceUtils.saveBoolPref(getActivity().getApplicationContext(), PBConstant.PREF_NAME, 
			        PBConstant.ISDOWNLOAD, false);
			
				try {
					historyItemList = mDatabaseManager.getAllHistoriesCursor();
					inboxDataList = mDatabaseManager.getAcceptedHistories(PBDatabaseDefinition.HISTORY_INBOX);
					sentDataList = mDatabaseManager.getAcceptedHistories(PBDatabaseDefinition.HISTORY_SENT);
	
					for (PBHistoryEntryModel item : inboxDataList) {
							if (db.isFavouriteItemExists(item.getEntryPassword())) {
								db.deleteFavouriteItem(item.getEntryPassword());
							}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				historyAdapter = new HistoryAdapter(getActivity(),
						inboxDataList, false);
				listview.setAdapter(historyAdapter);
				listview.setVisibility(View.VISIBLE);
				pullToRefreshContent.setVisibility(View.GONE);
				refreashadapter();
				radioButtonTask();
			}
		}
		

	}

	
	@Override
	 public void onPause() {
	  super.onPause();
	   try {
	    if(myReceiverIsRegistered){
	     myReceiverIsRegistered = false;
	     getActivity().unregisterReceiver(mReceiver);
	    }
	    
	   } catch (Exception e) {
	    //do nothing
	   }
	 }

	private void hideSoftKeyboard() {
		InputMethodManager imm = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(nickName.getWindowToken(), 0);
		getActivity().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

	}

	private class PBTaskGetNickName extends AsyncTask<Void, Void, Response> {

		@Override
		protected Response doInBackground(Void... params) {
			String uid = SPreferenceUtils.getStringPref(getActivity(),
					Constant.PREF_NAME, Constant.UID, "");
			Response response = ApiHelper.getNickName(uid);
			return response;
		}

		@Override
		protected void onPostExecute(Response response) {
			super.onPostExecute(response);

			if (response != null) {
				int response_code = response.errorCode;
				String response_body = response.decription;

				try {
					JSONObject jObject;
					jObject = new JSONObject(response_body);

					if (response_code == 200) {
						jObject = new JSONObject(response_body);

						if (jObject != null) {
							if (jObject.has("message")) {

								nickName.setText(jObject.getString("message"));
								
								 * defaultNickName =
								 * jObject.getString("message");
								 
								SPreferenceUtils.saveStringPref(getActivity(),
										Constant.PREF_NAME,
										Constant.KB_NICKNAME,
										jObject.getString("message"));
							}

						}
					} else {
						// Toast when error occured
						jObject = new JSONObject(response_body);

						if (jObject != null) {
							if (jObject.has("message")) {

								// show toast
								Toast.makeText(getActivity(),
										jObject.getString("message"),
										Toast.LENGTH_SHORT).show();

							}

						}
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	private void refreashadapter() {

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {

				historyAdapter.notifyDataSetChanged();
			}
		}, 100);
	}

	private class PBTaskNickNameRegistration extends
			AsyncTask<Void, String, Response> {

		String name;
		Context ctx;
		ProgressHUD mProgressHUD;

		public PBTaskNickNameRegistration(Context context, String name) {
			super();
			this.name = name;
			this.ctx = context;
			mProgressHUD = new ProgressHUD(ctx);
		}

		@Override
		protected void onPreExecute() {
			mProgressHUD.show(true);
			super.onPreExecute();
		}

		@Override
		protected Response doInBackground(Void... params) {

			String uid = SPreferenceUtils.getStringPref(ctx,
					Constant.PREF_NAME, Constant.UID, "");

			Response response = ApiHelper.nickNameRegistration(uid, name);
			return response;
		}

		// @Override
		// protected void onProgressUpdate(String... values) {
		// mProgressHUD.setMessage(values[0]);
		// super.onProgressUpdate(values);
		// }

		@Override
		protected void onPostExecute(Response response) {
			super.onPostExecute(response);
			if (mProgressHUD.isShowing()) {

				mProgressHUD.dismiss();
			}

			if (response != null) {
				int response_code = response.errorCode;
				String response_body = response.decription;

				try {
					JSONObject jObject;
					jObject = new JSONObject(response_body);

					if (response_code == 200) {
						jObject = new JSONObject(response_body);

						if (jObject != null) {
							if (jObject.has("message")) {
								Toast.makeText(ctx,
										jObject.getString("message"),
										Toast.LENGTH_SHORT).show();

								SPreferenceUtils.saveStringPref(getActivity(),
										Constant.PREF_NAME,
										Constant.KB_NICKNAME, nickName
												.getText().toString().trim());

							}

						}
					} else {
						// Toast when error occured
						jObject = new JSONObject(response_body);

						if (jObject != null) {
							if (jObject.has("message")) {

								// show toast
								Toast.makeText(ctx,
										jObject.getString("message"),
										Toast.LENGTH_SHORT).show();

								String default_nickName = SPreferenceUtils
										.getStringPref(getActivity(),
												Constant.PREF_NAME,
												Constant.KB_NICKNAME, "");

								nickName.setText(default_nickName);

							}

						}
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.button_nickname_registration) {

			if (nickName.getText().toString().length() > 0) {

				hideSoftKeyboard();

				boolean isWhitespace;

				isWhitespace = false;
				isWhitespace = nickName.getText().toString().startsWith(" ");

				isWhitespace = nickName.getText().toString().endsWith(" ");
				if (isWhitespace) {

					Toast.makeText(getActivity(),
							R.string.kb_search_whitespace, Toast.LENGTH_SHORT)
							.show();
				} else {
					String default_nickName = SPreferenceUtils.getStringPref(
							getActivity(), Constant.PREF_NAME,
							Constant.KB_NICKNAME, "");

					if (!nickName.getText().toString()
							.equalsIgnoreCase(default_nickName)) {

						if (Util.isOnline(getActivity())) {
							PBTaskNickNameRegistration task = new PBTaskNickNameRegistration(
									getActivity(), nickName.getText()
											.toString());
							task.execute();
						} else {

							Toast.makeText(
									getActivity(),
									getString(R.string.pb_network_not_available_general_message),
									Toast.LENGTH_SHORT).show();
							Toast toast = Toast.makeText(getActivity(), getString(R.string.pb_network_not_available_general_message), 
									1000);
							TextView v1 = (TextView) toast.getView().findViewById(android.R.id.message);
							if( v1 != null) v1.setGravity(Gravity.CENTER);
							toast.show();
						}
					} else {

						Toast.makeText(getActivity(),
								getString(R.string.kb_nickname_exist),
								Toast.LENGTH_SHORT).show();
					}

				}

			} else {

				Toast.makeText(getActivity(), getActivity().getString(R.string.kb_empty_nickname_warning),
						Toast.LENGTH_SHORT).show();
			}

		} else if (v.getId() == R.id.button_donguri) {

			
			Intent intent = new Intent(
					getActivity(),
					PBMyPageAcornExchangeListActivity.class);

			getActivity().startActivity(intent);
			

		}

	}
	
	private class GetNewDataTask extends AsyncTask<Void, String, Response> {

		@Override
		protected Response doInBackground(Void... params) {
			String uid = SPreferenceUtils.getStringPref(getActivity(), Constant.PREF_NAME,
					Constant.UID, "");
			Response response = ApiHelper.getUpdateFavouriteList(uid,list.get(0).getPassword());
			return response;
		}

		@Override
		protected void onPostExecute(Response response) {
			super.onPostExecute(response);

			if (response != null) {
				int response_code = response.errorCode;
				String response_body = response.decription;
				System.out.println("Atik get favourite list json response"
						+ response_body);

				try {
					JSONObject jObject;
					jObject = new JSONObject(response_body);

					if (response_code == 200) {
						
						PBDatabaseManager mDatabaseManager = PBDatabaseManager
								.getInstance(PBTabBarActivity.sMainContext);

						if (jObject.has("passwords")) {
							JSONArray jsonArray = jObject.getJSONArray("passwords");

							SPreferenceUtils.saveBoolPref(getActivity(), Constant.PREF_NAME,
									Constant.KB_FAVOURITE_JSON_ISSAVED, true);

							for (int i = 0; i < jsonArray.length(); i++) {

								JSONObject obj = jsonArray.getJSONObject(i);
								String strDownload = "0";
								
								if (mDatabaseManager.isPasswordExistsInSentItems(obj
										.getString("password"))) {

									strDownload = "1";
								}

								db.addFavouriteItem(obj.getString("thumb_url"),
										obj.getString("nickname"),
										obj.getString("password"),
										obj.getString("created"),
										obj.getString("expires_at"),
										obj.getString("expires_time"),
										obj.getString("photos_count"),
										obj.getString("downloaded_users_count"),
										"1", obj.getString("honey"),
										obj.getString("new"), strDownload,obj.getString("recommended"));

							}


								Intent intent = new Intent(
										"koukaibukuro.favouritelist");
								getActivity().sendBroadcast(intent);

							}

						}

					else {

					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	}
		
	}

}*/


package com.aircast.koukaibukuro;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RadioGroup;
import com.aircast.koukaibukuro.adapter.HistoryAdapter;
import com.aircast.koukaibukuro.util.Constant;
import com.aircast.photobag.R;
import com.aircast.photobag.activity.PBMainTabBarActivity;
import com.aircast.photobag.activity.PBMyPageAcornExchangeListActivity;
import com.aircast.photobag.api.PBAPIContant;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.database.PBDatabaseDefinition;
import com.aircast.photobag.database.PBDatabaseManager;
import com.aircast.photobag.model.PBHistoryEntryModel;
import com.aircast.photobag.utils.PBPreferenceUtils;

public class MyPageFragment extends Fragment implements OnClickListener {

	private View view;
	private ArrayList<PBHistoryEntryModel> inboxDataList;
	private ArrayList<PBHistoryEntryModel> sentDataList;
	private ListView listview;
	private HistoryAdapter historyAdapter;
	private PBDatabaseManager mDatabaseManager;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.kb_mypage_fragment, container, false);

		listview = (ListView) view.findViewById(R.id.listView_common);


		RadioGroup radGrp = (RadioGroup) view
				.findViewById(R.id.radioGroup_mypage);
		radGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup arg0, int id) {

				if (id == R.id.mypage_segmented_btn1) {
					radioButtonTask();
				} else if (id == R.id.mypage_segmented_btn2) {
					radioButtonTask();
				}

			}

		});

		
		try {
			mDatabaseManager = PBDatabaseManager
					.getInstance(PBMainTabBarActivity.sMainContext);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return view;
	}

	private void radioButtonTask() {

		RadioGroup radGrp = (RadioGroup) view
				.findViewById(R.id.radioGroup_mypage);
		int id = radGrp.getCheckedRadioButtonId();
		listview.setAdapter(null);
		listview.setVisibility(View.VISIBLE);
		if (id == R.id.mypage_segmented_btn1) {

			historyAdapter = new HistoryAdapter(getActivity(), inboxDataList,
					false);

			 refreashadapter();
		} else if (id == R.id.mypage_segmented_btn2) {

			historyAdapter = new HistoryAdapter(getActivity(), sentDataList,
					true);
			 refreashadapter();
		}

		listview.setAdapter(historyAdapter);

		historyAdapter.notifyDataSetChanged();

		int position = PBPreferenceUtils.getIntPref(getActivity()
				.getApplicationContext(), PBConstant.PREF_NAME,
				Constant.KB_POSITION, 0);

		if (position != 0) {
			//Toast.makeText(getActivity(), "" + position, 500).show();
			listview.requestFocusFromTouch();
			listview.setSelection(position);
			PBPreferenceUtils.saveIntPref(
					getActivity().getApplicationContext(),
					PBConstant.PREF_NAME, Constant.KB_POSITION, 0);

		}
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);

		if (!hidden) {

			try {
				inboxDataList = mDatabaseManager
						.getAcceptedHistories(PBDatabaseDefinition.HISTORY_INBOX);
				sentDataList = mDatabaseManager
						.getAcceptedHistories(PBDatabaseDefinition.HISTORY_SENT);
				
				if(PBAPIContant.DEBUG){
					
					Log.d("inboxDataList", ""+inboxDataList.size());
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			radioButtonTask();
		} else {

		}

	}

	@Override
	public void onResume() {
		Log.e("DEBUG", "onResume of MyPageFragment");
		super.onResume();

		PBPreferenceUtils.saveBoolPref(getActivity().getApplicationContext(),
				PBConstant.PREF_NAME, PBConstant.ISDOWNLOAD, false);

		try {
			inboxDataList = mDatabaseManager
					.getAcceptedHistories(PBDatabaseDefinition.HISTORY_INBOX);
			sentDataList = mDatabaseManager
					.getAcceptedHistories(PBDatabaseDefinition.HISTORY_SENT);

		} catch (Exception e) {
			e.printStackTrace();
		}

		radioButtonTask();

	}

	@Override
	public void onPause() {
		super.onPause();
	}

	

	

	 private void refreashadapter() {
	
	 new Handler().postDelayed(new Runnable() {
	
	 @Override
	 public void run() {
	
	 historyAdapter.notifyDataSetChanged();
	 int position =
	 PBPreferenceUtils.getIntPref(getActivity().getApplicationContext(),
	 PBConstant.PREF_NAME,
	 Constant.KB_POSITION, 0);
	
	 if(position != 0){
	
	 //Toast.makeText(getActivity(), ""+position, 1000).show();
	 listview.setSelection(position);
	 }
	 }
	 }, 100);
	 }

	

	@Override
	public void onClick(View v) {

		 if (v.getId() == R.id.button_donguri) {

			Intent intent = new Intent(getActivity(),
					PBMyPageAcornExchangeListActivity.class);

			getActivity().startActivity(intent);

		}

	}

}