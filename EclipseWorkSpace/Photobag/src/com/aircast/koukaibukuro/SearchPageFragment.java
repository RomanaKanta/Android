package com.aircast.koukaibukuro;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aircast.koukaibukuro.database.DatabaseHandler;
import com.aircast.koukaibukuro.model.Password;
import com.aircast.koukaibukuro.util.ApiHelper;
import com.aircast.koukaibukuro.util.ApiHelper.Response;
import com.aircast.koukaibukuro.util.Constant;
import com.aircast.koukaibukuro.util.ImageLoader;
import com.aircast.koukaibukuro.util.KBDownLoadManager;
import com.aircast.koukaibukuro.util.ProgressHUD;
import com.aircast.koukaibukuro.util.SPreferenceUtils;
import com.aircast.koukaibukuro.util.Util;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.aircast.photobag.R;
import com.aircast.photobag.activity.PBHistoryInboxDetailActivity;
import com.aircast.photobag.activity.PBMainTabBarActivity;
import com.aircast.photobag.api.PBAPIContant;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.database.PBDatabaseManager;
import com.aircast.photobag.utils.PBGeneralUtils;
import com.aircast.photobag.utils.PBPreferenceUtils;

@SuppressLint("NewApi")
public class SearchPageFragment extends Fragment implements OnClickListener {

	private View view;
	boolean isOpen = false;
	private SearchCommonAdapter adapter;
	private EditText searchString;
	List<Password> passwordlist = new ArrayList<Password>();
	private ArrayList<HashMap<String, String>> historyList;
	private PullToRefreshListView pullRefreshListview;
	private ListView mainPageListview;
	private TextView hearderView;
	private PBDatabaseManager mDatabaseManager;
	private  JSONArray  jSearchArray = null;
	private static ProgressHUD mProgressHUD;

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.kb_searchpage_fragment, container, false);
		pullRefreshListview = (PullToRefreshListView) view
				.findViewById(R.id.listView_common);
		
		pullRefreshListview.setMode(Mode.DISABLED);
		mainPageListview = pullRefreshListview.getRefreshableView();
		pullRefreshListview.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

				// Update the LastUpdatedLabel
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

				//listview.setProgressBarUI(true);
				new GetNewDataTask().execute();
			}
		});
		
		// Call AsyncTask for getting all the serach data
		
		
		hearderView = (TextView) view.findViewById(R.id.kb_search_hearderView);
		hearderView.setVisibility(View.INVISIBLE);
		searchString = (EditText) view
				.findViewById(R.id.editText_search_password);

		final View mRootView = getActivity().findViewById(R.id.root_content);
		mRootView.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						int heightDiff = mRootView.getRootView().getHeight()
								- mRootView.getHeight();
						if (heightDiff > 100) {
							getActivity().findViewById(R.id.ad_content)
									.setVisibility(View.GONE);
							//listview.setLockScrollWhileRefreshing(false); // Atik code problem here
							pullRefreshListview.setMode(Mode.DISABLED);

						} else {

							pullRefreshListview.setMode(Mode.PULL_FROM_START);
							//listview.setLockScrollWhileRefreshing(true); // Atik code problem here
							getActivity().findViewById(R.id.ad_content)
									.setVisibility(View.VISIBLE);

						}
					}
				});

		
		searchString
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if (actionId == EditorInfo.IME_ACTION_SEARCH) {
							performSearch();
							return true;
						}
						return false;
					}

				});

		searchString.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2,
					int arg3) {
				// When user changed the Text
				//listview.setLockScrollWhileRefreshing(false); // Atik code problem here
				pullRefreshListview.setMode(Mode.DISABLED);
				adapter.getFilter().filter(cs.toString());

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {

			}
		});
		view.findViewById(R.id.button_search_password).setOnClickListener(this);
		view.findViewById(R.id.textfiled_content).setOnClickListener(this);
		
		// Create progress bar for showing 
		mProgressHUD = new ProgressHUD(getActivity());

		
		 mDatabaseManager  = PBDatabaseManager
			        .getInstance(PBMainTabBarActivity.sMainContext);
		 
		 try {
				historyList = mDatabaseManager.getAllHistoriesCursor();
				loadDataFromLocal();
				//loadAllDataFromLocal();
				
				/*if (Util.isOnline(getActivity())) {

					mProgressHUD.show(true);

					OpenPageGetAllPasswordListTask task = new OpenPageGetAllPasswordListTask();
					task.execute();

				} else {
					mProgressHUD.show(true);
					String fileForJson = PBGeneralUtils.getPathFromSearchOpenbagDataFolderPath();
					File yourFile = new File(fileForJson);
					FileInputStream stream = new FileInputStream(yourFile);
				    String jString = null;
				    try {
			            FileChannel fc = stream.getChannel();
			            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
			             Instead of using default, pass in a decoder. 
			            jString = Charset.defaultCharset().decode(bb).toString();
				    } catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
				    	try {
							stream.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				    }
				    JSONObject jsonObj = new JSONObject(jString);
				    jSearchArray = jsonObj.getJSONArray("passwords"); 
					
					
					if (isArrivalTabSelectedtrue) {

						SPreferenceUtils.saveStringPref(getActivity(),
								Constant.PREF_NAME,
								Constant.KB_ALL_PASSWORD_JSON,
								jsonArray.toString());
						SPreferenceUtils
								.saveBoolPref(
										getActivity(),
										Constant.PREF_NAME,
										Constant.KB_ALL_PASSWORD_JSON_ISSAVED,
										true);

						//passwordlist = Util.getPasswordList(jsonArray);
						
						
						if(jSearchArray.length() > 0){
							hearderView.setVisibility(View.VISIBLE);
							hearderView.setText(String.format(
									getActivity().getString(R.string.kb_search_find_item),
									jSearchArray.length()));
							
						}
						
						passwordlist = Util.getPasswordList(jSearchArray);

						adapter = new SearchCommonAdapter(getActivity(),
								passwordlist);
						mainPageListview.setAdapter(adapter);
						adapter.notifyDataSetChanged();
						
						if (mProgressHUD.isShowing()) {

							mProgressHUD.dismiss();
						}
						
						if (mProgressHUD.isShowing()) {

							mProgressHUD.dismiss();
						}

					} 
				}*/
				
			} catch (Exception e) {
				e.printStackTrace();
			}
	  
		
		return view;
	}
	
	
	private void loadDataFromLocal() {

		try {

					String arrivalJson = SPreferenceUtils.getStringPref(
							getActivity(), Constant.PREF_NAME,
							Constant.KB_ARRIVAL_PASSWORD_JSON, "");
					/*JSONArray jsonArray = new JSONArray(arrivalJson);*/
					jSearchArray = new JSONArray(arrivalJson);
					for (int i = 0; i < jSearchArray.length(); i++) {

						JSONObject obj = jSearchArray.getJSONObject(i);

						Password mPassword = new Password();
						mPassword.setNickName(obj.getString("nickname"));
						mPassword.setPassword(obj.getString("password"));
						mPassword.setCreatedDate(obj.getString("created"));
						mPassword.setPhotoCount(Integer.parseInt(obj
								.getString("photos_count")));
						mPassword.setNumberOfDownload(Integer.parseInt(obj
								.getString("downloaded_users_count")));
						mPassword.setFavorite(Integer.parseInt(obj
								.getString("favourite")));
						mPassword.setHoney(Integer.parseInt(obj
								.getString("honey")));
						mPassword.setThumbURL(obj.getString("thumb_url"));
						mPassword.setExpiresAT(obj.getString("expires_at"));
						mPassword.setExpiredTime(obj.getString("expires_time"));
						mPassword.setChargesTime(obj.getString("charges_time"));
						mPassword.setNewItem(Integer.parseInt(obj
								.getString("new")));
						mPassword.setDownload(false);
						
						 String collectionId = mDatabaseManager.getCollectionId(obj
									.getString("password"));
			   				if (mDatabaseManager.isPasswordExistsInSentItems(collectionId)) {
//						if (mDatabaseManager.isPasswordExistsInSentItems(obj
//								.getString("password"))) {

							mPassword.setDownload(true);
						}
						mPassword.setRecommend(Integer.parseInt(obj
								.getString("recommended")));

						passwordlist.add(mPassword);

					}

					if(passwordlist.size() > 0){
						hearderView.setVisibility(View.VISIBLE);
						hearderView.setText(String.format(
								getActivity().getString(R.string.kb_search_find_item),
								passwordlist.size()));
						
					}
					adapter = new SearchCommonAdapter(getActivity(),
							passwordlist);
					mainPageListview.setAdapter(adapter);
					adapter.notifyDataSetChanged();
				

			
			if(position != 0){
				
				mainPageListview.setSelection(position); //// Atik code problem here
			}
		} catch (JSONException e) {

			e.printStackTrace();
		}

	}
	
	private void loadAllDataFromLocal() {

		try {

					/*String arrivalJson = SPreferenceUtils.getStringPref(
							getActivity(), Constant.PREF_NAME,
							Constant.KB_ALL_PASSWORD_JSON, "");*/
					
					//  Read JSON Data
					
					String fileForJson = PBGeneralUtils.getPathFromSearchOpenbagDataFolderPath();
					
					File yourFile = new File(fileForJson);
					FileInputStream stream = new FileInputStream(yourFile);
				    String jString = null;
				    try {
			            FileChannel fc = stream.getChannel();
			            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
			            /* Instead of using default, pass in a decoder. */
			            jString = Charset.defaultCharset().decode(bb).toString();
				    } catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
				    	try {
							stream.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				    }
				    JSONObject jsonObj = new JSONObject(jString);
					
					/*JSONArray jsonArray = new JSONArray(arrivalJson);*/
					jSearchArray = jsonObj.getJSONArray("passwords"); 
							
							//new JSONArray(passwords);
					for (int i = 0; i < 20/*jSearchArray.length()*/; i++) {

						JSONObject obj = jSearchArray.getJSONObject(i);

						Password mPassword = new Password();
						mPassword.setNickName(obj.getString("nickname"));
						mPassword.setPassword(obj.getString("password"));
						mPassword.setCreatedDate(obj.getString("created"));
						mPassword.setPhotoCount(Integer.parseInt(obj
								.getString("photos_count")));
						mPassword.setNumberOfDownload(Integer.parseInt(obj
								.getString("downloaded_users_count")));
						mPassword.setFavorite(Integer.parseInt(obj
								.getString("favourite")));
						mPassword.setHoney(Integer.parseInt(obj
								.getString("honey")));
						mPassword.setThumbURL(obj.getString("thumb_url"));
						mPassword.setExpiresAT(obj.getString("expires_at"));
						mPassword.setExpiredTime(obj.getString("expires_time"));
						mPassword.setChargesTime(obj.getString("charges_time"));
						mPassword.setNewItem(Integer.parseInt(obj
								.getString("new")));
						mPassword.setDownload(false);
						
						 String collectionId = mDatabaseManager.getCollectionId(obj
									.getString("password"));
			   				if (mDatabaseManager.isPasswordExistsInSentItems(collectionId)) {
//						if (mDatabaseManager.isPasswordExistsInSentItems(obj
//								.getString("password"))) {

							mPassword.setDownload(true);
						}
						mPassword.setRecommend(Integer.parseInt(obj
								.getString("recommended")));

						passwordlist.add(mPassword);

					}

					if(passwordlist.size() > 0){
						hearderView.setVisibility(View.VISIBLE);
						hearderView.setText(String.format(
								getActivity().getString(R.string.kb_search_find_item),
								passwordlist.size()));
						
					}
					adapter = new SearchCommonAdapter(getActivity(),
							passwordlist);
					mainPageListview.setAdapter(adapter);
					adapter.notifyDataSetChanged();
				

			
			if(position != 0){
				
				mainPageListview.setSelection(position); //// Atik code problem here
			}
		} catch (JSONException e) {

			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	
	
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);

		if (hidden) {
			
			hideSoftKeyboard();
		}

	}
	private int position = 0;
	
	@Override
	public void onResume() {
		super.onResume();
		position = 0;
		int tabPosition = PBPreferenceUtils.getIntPref(getActivity().getApplicationContext(), PBConstant.PREF_NAME, 
    			PBConstant.TAB_POSITION, 0);
		
		
		if(tabPosition == 1){
			boolean isFromDownload = PBPreferenceUtils.getBoolPref(getActivity().getApplicationContext(), PBConstant.PREF_NAME, 
			        PBConstant.ISDOWNLOAD, false);
			   if(isFromDownload) {
			
				try {
					historyList = mDatabaseManager.getAllHistoriesCursor();
					passwordlist = Util.getPasswordList(jSearchArray
							);
					adapter = new SearchCommonAdapter(getActivity(), passwordlist);
					mainPageListview.setAdapter(null);
					mainPageListview.setAdapter(adapter);
					adapter.notifyDataSetChanged();
					position = 	PBPreferenceUtils.getIntPref(getActivity().getApplicationContext(), PBConstant.PREF_NAME, 
			    			Constant.KB_POSITION, 0);
					PBPreferenceUtils.saveIntPref(getActivity().getApplicationContext(), PBConstant.PREF_NAME, 
			    			Constant.KB_POSITION, 0);
					if(position != 0){
						
						//listview.setSelection(position); // Atik code problem here
						mainPageListview.setSelection(position);
					}
					
					performSearch();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
		      }
		 }
	}
	

	@Override
	 public void onClick(View v) {

	  if (v.getId() == R.id.button_search_password) {

	   searchString.setCursorVisible(false);
	   searchString.setText("");
	   searchString.setHint(getString(R.string.kb_search_hint));

	  }else if (v.getId() == R.id.textfiled_content) {
	   searchString.setCursorVisible(true);
	   searchString.requestFocus();
	   
	   ((InputMethodManager) getActivity().getSystemService(
	     Context.INPUT_METHOD_SERVICE)).toggleSoftInput(
	     InputMethodManager.SHOW_FORCED,
	     InputMethodManager.HIDE_IMPLICIT_ONLY);

	  }

	 }
	
	
	private void performSearch() {
		
		hideSoftKeyboard();
		
		new PBTaskPasswordListForSearchWithNewArrival(getActivity()).execute();
	}


	@Override
	public void onPause() {
		Log.e("DEBUG", "OnPause of SearchPageFragment");
		super.onPause();
	}

	private void hideSoftKeyboard() {

		InputMethodManager imm = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(searchString.getWindowToken(), 0);
		getActivity().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

	}

	public class PBTaskPasswordListForSearchWithNewArrival extends
			AsyncTask<Void, String, Response> {

		private Context ctx;
		ProgressHUD mProgressHUD;

		public PBTaskPasswordListForSearchWithNewArrival(Context context) {
			super();
			this.ctx = context;
		}

		@Override
		protected void onPreExecute() {
			mProgressHUD = new ProgressHUD(ctx);
			mProgressHUD.show(true);
			super.onPreExecute();
		}

		@Override
		protected Response doInBackground(Void... params) {

			String uid = SPreferenceUtils.getStringPref(ctx,
					Constant.PREF_NAME, Constant.UID, "");
			Response response = ApiHelper.getsearchListByArrival(uid, searchString.getText().toString(), "top", "all");

			return response;
		}

		@Override
		protected void onPostExecute(Response response) {
			super.onPostExecute(response);
			if (mProgressHUD.isShowing()) {
				mProgressHUD.dismiss();
			}

			if (response != null) {
				int response_code = response.errorCode;
				String response_body = response.decription;
				
				
				if(PBAPIContant.DEBUG){
					
					System.out.println("Atik group status response code:"
							+ response_code);
					System.out.println("Atik group status response body:"
							+ response_body);
					
				}
				
				try {
					JSONObject jObject;
					jObject = new JSONObject(response_body);

					if (response_code == 200) {

						if (jObject.has("passwords")) {

							Log.d("jObject", jObject.toString());
							JSONArray jsonArray = jObject
									.getJSONArray("passwords");
							passwordlist.clear();
								jSearchArray =  new JSONArray(
										jsonArray.toString());
								passwordlist = Util.getPasswordList(jsonArray
										);

							adapter = new SearchCommonAdapter(getActivity(),
									passwordlist);
							mainPageListview.setAdapter(adapter);
							adapter.notifyDataSetChanged();
							
							if(passwordlist.size() > 0){
								hearderView.setVisibility(View.VISIBLE);
								hearderView.setText(String.format(
										ctx.getString(R.string.kb_search_find_item),
										passwordlist.size()));
								
							}
							

						}

					} else if (response_code == 400) {

						
						passwordlist.clear();
						adapter.notifyDataSetChanged();
						hearderView.setVisibility(View.VISIBLE);
						hearderView.setText(String.format(
								ctx.getString(R.string.kb_search_find_item),
								passwordlist.size()));

					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

	}

	public class SearchCommonAdapter extends BaseAdapter implements Filterable {
		protected ImageLoader mImageLoader;
		private Context ctx;
		private DatabaseHandler db;
		private List<Password> tempList = new ArrayList<Password>();
		private List<Password> list = new ArrayList<Password>();

		public SearchCommonAdapter(Context context, List<Password> list) {
			super();
			this.ctx = context;
			mImageLoader = new ImageLoader(context);
			db = new DatabaseHandler(ctx);
			this.list = list;
			this.tempList = list;
		}

		public int getCount() {

			return list.size();
		}

		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		// public void resetData(){
		//
		// list = tempList;
		// }

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			SearchViewHolder holder;
			if (convertView == null) {

				holder = new SearchViewHolder();
				LayoutInflater inflater = LayoutInflater.from(ctx);
				convertView = inflater.inflate(R.layout.kb_common_list_row,
						parent, false);

				holder.iconThub = (ImageView) convertView
						.findViewById(R.id.imageView_photo_thumb);
				
				holder.thumbNew = (ImageView) convertView
						.findViewById(R.id.imageView_new_item);
				holder.thumbRecommend = (ImageView) convertView
						.findViewById(R.id.imageView_recommend_item);

				holder.password = (TextView) convertView
						.findViewById(R.id.textView_password);
				holder.posted_user_name = (TextView) convertView
						.findViewById(R.id.textView_posted_user_name);
				holder.expiresTime = (TextView) convertView
						.findViewById(R.id.textView_expires_time);

				holder.photo_count = (TextView) convertView
						.findViewById(R.id.textView_photos_count);
				holder.downloaded_users_count = (TextView) convertView
						.findViewById(R.id.textView_downloaded_users_count);
				holder.photo_thumb_serial = (TextView) convertView
						.findViewById(R.id.photo_thumb_serial);

				holder.item_category = (TextView) convertView
						.findViewById(R.id.TextView_item_category);
				holder.item_honey = (TextView) convertView
						.findViewById(R.id.TextView_item_honey);
				holder.chargesTime = (TextView) convertView
						.findViewById(R.id.textView_charges_time);

				convertView.setTag(holder);

			} else {

				holder = (SearchViewHolder) convertView.getTag();
			}
				if (list.get(position).getRecommend() == 1) {
					holder.thumbRecommend.setVisibility(View.VISIBLE);
				} else if (list.get(position).getRecommend() == 0) { // bug fix
																		// display
					holder.thumbRecommend.setVisibility(View.GONE);
				}	
			
			if (list.get(position).getNewItem() == 1) {
				holder.thumbNew.setVisibility(View.VISIBLE);
			} else if (list.get(position).getNewItem() == 0) { // bug fix
																	// display
				holder.thumbNew.setVisibility(View.GONE);
			}
			
			holder.chargesTime.setText(""+list.get(position).getChargesTime());
			holder.password.setText(list.get(position).getPassword());
			holder.posted_user_name.setText(list.get(position).getNickName());
			holder.expiresTime.setText(list.get(position).getExpiredTime());

			holder.photo_count.setText("" + list.get(position).getPhotoCount());
			holder.downloaded_users_count.setText(""
					+ list.get(position).getNumberOfDownload());

			if (!TextUtils.isEmpty(list.get(position).getThumbURL())) {

				mImageLoader.DisplayImage(list.get(position).getThumbURL(),
						holder.iconThub);

			}

			if (!list.get(position).isDownload()) {
				holder.item_honey.setVisibility(View.INVISIBLE);
				holder.item_category.setVisibility(View.VISIBLE);
				holder.item_category
						.setBackgroundResource(R.drawable.custom_shape_onlyfree);
				holder.item_category.setText(ctx.getString(R.string.kb_free));
				holder.item_category.setTextColor(ctx.getResources().getColor(R.color.gray_deep_dark));
				holder.chargesTime.setVisibility(View.VISIBLE);

			}

			if (list.get(position).getHoney() > 0) {
				holder.item_honey.setVisibility(View.VISIBLE);
				holder.item_category.setVisibility(View.INVISIBLE);
				holder.chargesTime.setVisibility(View.INVISIBLE);
			}else{
				
				holder.item_honey.setVisibility(View.INVISIBLE);
				holder.item_category.setVisibility(View.VISIBLE);
				holder.chargesTime.setVisibility(View.VISIBLE);
			}

			if (list.get(position).isDownload()) {
				holder.item_honey.setVisibility(View.INVISIBLE);
				holder.item_category.setVisibility(View.VISIBLE);
				holder.item_category
						.setBackgroundResource(R.drawable.custom_shape_freeandopen);
				holder.item_category.setText(ctx.getString(R.string.kb_open));
				holder.item_category.setTextColor(ctx.getResources().getColor(R.color.blue_apple));
				holder.chargesTime.setVisibility(View.INVISIBLE);

			}

			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					
						if (list.get(position).isDownload()) {
							for (int j = 0; j < historyList.size(); j++) {
								

								if (list.get(position).getPassword().equals(
										historyList.get(j).get(
												Constant.C_PASSWORD))) {
									

									Bundle extras = new Bundle();

									extras.putLong(Constant.HISTORY_ITEM_ID_K,
											(Long.parseLong(historyList.get(j)
													.get(Constant.C_ID))));
									extras.putString(
											Constant.HISTORY_COLLECTION_ID_K,
											historyList.get(j).get(
													Constant.C_COLECTION_ID));
									extras.putString(
											Constant.HISTORY_PASSWORD_K,
											historyList.get(j).get(
													Constant.C_PASSWORD));
									extras.putLong(
											Constant.HISTORY_CREATE_DATE_K,
											(Long.parseLong(historyList.get(j)
													.get(Constant.C_CREATED_AT))));
									extras.putLong(
											Constant.HISTORY_CHARGE_DATE_K,
											(Long.parseLong(historyList.get(j)
													.get(Constant.C_CHARGES_AT))));
									extras.putString(
											Constant.COLLECTION_THUMB_K,
											historyList.get(j).get(
													Constant.C_THUMB));
									extras.putInt(
											Constant.HISTORY_ADDIBILITY_K,
											Integer.parseInt(historyList.get(j)
													.get(Constant.C_ADDIBILITY)));
									
									extras.putBoolean(Constant.HISTORY_CATEGORY_INBOX_K, 
									           Integer.parseInt(historyList.get(j)
									             .get(Constant.C_TYPE)) == 1? true : false);
									
									extras.putInt(
											Constant.HISTORY_IS_UPDATABLE_K,
											Integer.parseInt(historyList
													.get(j)
													.get(Constant.C_IS_UPDATABLE)));
									extras.putLong(
											Constant.HISTORY_UPDATED_AT_K,
											(Long.parseLong(historyList.get(j)
													.get(Constant.C_UPDATED_AT))));
									extras.putInt(Constant.HISTORY_SAVE_MARK_K,
											Integer.parseInt(historyList.get(j)
													.get(Constant.C_SAVE_MARK)));
									extras.putInt(Constant.HISTORY_SAVE_DAYS_K,
											Integer.parseInt(historyList.get(j)
													.get(Constant.C_SAVE_DAYS)));
									extras.putString(
											Constant.HISTORY_AD_LINK_K,
											historyList.get(j).get(
													Constant.C_AD_LINK));

									Intent intent = new Intent(
											ctx,
											PBHistoryInboxDetailActivity.class);
									intent.putExtra("data", extras);
									intent.putExtra(PBConstant.PREF_PASSWORD_FROM_LIBRARY, true);

									((Activity) ctx).startActivity(intent);

								}
							}

						} else {
							

							if (Util.isOnline(ctx)) {
								PBPreferenceUtils.saveIntPref(ctx.getApplicationContext(), PBConstant.PREF_NAME, 
						    			Constant.KB_POSITION, position);
							new KBDownLoadManager((FragmentActivity) ctx,list.get(position).getPassword(),1);
						
							} else {
	
								Toast.makeText(
										ctx.getApplicationContext(),
										ctx.getString(R.string.pb_chat_message_internet_offline_dialog_message),
										Toast.LENGTH_SHORT).show();
							}
						}

					
				}

			});

			return convertView;

		}

		@Override
		public Filter getFilter() {
			Filter filter = new Filter() {

				@SuppressWarnings("unchecked")
				@Override
				protected void publishResults(CharSequence constraint,
						FilterResults results) {

					list = (List<Password>) results.values;
					
					if (list.size() > 0) {
						hearderView.setText(String.format(
								ctx.getString(R.string.kb_search_find_item),
								list.size()));

					}

					if (list.size() < 1) {

						hearderView
								.setText(getString(R.string.kb_search_not_found));
					}

					if (searchString.getText().toString().equals("")) {

						hearderView
								.setText(String
										.format(getString(R.string.kb_search_first_time)));
					}
					notifyDataSetChanged();
				}

				@Override
				protected FilterResults performFiltering(CharSequence constraint) {

					FilterResults results = new FilterResults();
					List<Password> FilteredArrayNames = new ArrayList<Password>();
					List<Password> FilteredArrayNamesTemp = new ArrayList<Password>();

					if (constraint == null || constraint.length() == 0) {
						// No filter implemented we return all the list
						results.values = tempList;
						results.count = tempList.size();
					} else {

						// perform your search here using the searchConstraint
						// String.
						constraint = constraint.toString().toLowerCase();

						for (Password password : tempList) {

							if (password.getPassword().toLowerCase()
									.startsWith(constraint.toString())) {
								FilteredArrayNames.add(password);
							} else {

								FilteredArrayNamesTemp.add(password);
							}

						}
						

						for (Password password : FilteredArrayNamesTemp) {

							if (password.getPassword().toLowerCase()
									.contains(constraint.toString())) {
								FilteredArrayNames.add(password);
							} 

						}

					

						results.count = FilteredArrayNames.size();
						results.values = FilteredArrayNames;
						Log.e("VALUES", results.values.toString());

					}

					return results;
				}
			};

			return filter;
		}

		private class SearchViewHolder {
			public ImageView iconThub;
			public ImageView thumbCatagory;
			public ImageView thumbNew;
			public ImageView thumbRecommend;
			public TextView password;
			public TextView posted_user_name;
			public TextView expiresTime;
			public TextView downloaded_users_count;
			public TextView item_category;
			public TextView photo_thumb_serial;
			public TextView photo_count;
			public TextView item_honey;
			 public TextView chargesTime;
		}

	}

	private class GetNewDataTask extends AsyncTask<Void, String, Response> {

		@Override
		protected Response doInBackground(Void... params) {
			String uid = SPreferenceUtils.getStringPref(getActivity(),
					Constant.PREF_NAME, Constant.UID, "");

			/*Response response = ApiHelper.getUpdateSearchPassword(uid,
					passwordlist.get(0).getPassword());*/
			
			Response response = ApiHelper.getUpdatePasswordListForType(uid,
				     passwordlist.get(0).getPassword(),"arrival","all");

			return response;
		}

		@Override
		protected void onPostExecute(Response response) {
			super.onPostExecute(response);
			// Call onRefreshComplete when the list has been refreshed.
			//listview.setProgressBarUI(false);
			view.findViewById(R.id.progressBar_refresh).setVisibility(View.GONE);
			pullRefreshListview.onRefreshComplete();

			try {

				int response_code = response.errorCode;
				String response_body = response.decription;
				Log.d("update response", response_body);
				JSONObject jObject;
				jObject = new JSONObject(response_body);

					if (response_code == 200) {

						List<Password> tmpList = new ArrayList<Password>();
						tmpList = Util.cloneList(passwordlist);
						passwordlist.clear();
						if (jObject.has("passwords")) {

							JSONArray jsonArray = jObject
									.getJSONArray("passwords");
								passwordlist = Util.getPasswordList(jsonArray
										);
								jSearchArray.put(jsonArray);
								
								Log.d("jSearchArray", jSearchArray.toString());
							
							passwordlist.addAll(tmpList);
							
							hearderView.setVisibility(View.VISIBLE);
							hearderView
									.setText(getString(R.string.kb_search_first_time));

							adapter = new SearchCommonAdapter(getActivity(),
									passwordlist);
							mainPageListview.setAdapter(adapter);
							adapter.notifyDataSetChanged();

						}
					}


			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
	
	private class OpenPageGetAllPasswordListTask extends
	AsyncTask<Void, String, Response> {

	@Override
	protected Response doInBackground(Void... params) {
		Response response = null;
		String uid = SPreferenceUtils.getStringPref(getActivity(),
				Constant.PREF_NAME, Constant.UID, "");

		response = ApiHelper.getAllPasswordList(uid);


		return response;
	}

	@Override
	protected void onPostExecute(Response response) {
		super.onPostExecute(response);


		/*if (rotation != null) {

			btn_refresh.clearAnimation();
		}*/

		if (response != null) {
			int response_code = response.errorCode;
			String response_body = response.decription;
			String fileForJson = PBGeneralUtils.getPathFromSearchOpenbagDataFolderPath();

			try {
				JSONObject jObject;
				jObject = new JSONObject(response_body);

				if (response_code == 200) {
					mProgressHUD.show(true);

					if (jObject.has("passwords")) {

						JSONArray jsonArray = jObject
								.getJSONArray("passwords");
						
						
						
						
						// get file name and then write it to file
						
						//PBGeneralUtils.getCacheFolderPath(isExternal)


						
						System.out.println("Atik path for JSON file"+fileForJson);
						
						try {
							 
							FileWriter file = new FileWriter(fileForJson);
							file.write(jObject.toString());
							file.flush();
							file.close();
					 
						} catch (IOException e) {
							e.printStackTrace();
						}
						
						
						
						
						File yourFile = new File(fileForJson);
						FileInputStream stream = new FileInputStream(yourFile);
					    String jString = null;
					    MappedByteBuffer bb = null;
					    try {
				            FileChannel fc = stream.getChannel();
				            bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
				            /* Instead of using default, pass in a decoder. */
				            jString = Charset.defaultCharset().decode(bb).toString();
				            bb.clear();
					    } catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} finally {
					    	try {
								stream.close();
								bb.clear();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
					    }
					    JSONObject jsonObj = new JSONObject(jString);
					    jSearchArray = jsonObj.getJSONArray("passwords"); 
						
						
						if (/*isArrivalTabSelected*/true) {

							/*SPreferenceUtils.saveStringPref(getActivity(),
									Constant.PREF_NAME,
									Constant.KB_ALL_PASSWORD_JSON,
									jsonArray.toString());
							SPreferenceUtils
									.saveBoolPref(
											getActivity(),
											Constant.PREF_NAME,
											Constant.KB_ALL_PASSWORD_JSON_ISSAVED,
											true);*/

							//passwordlist = Util.getPasswordList(jsonArray);
							
							
							if(jSearchArray.length() > 0){
								hearderView.setVisibility(View.VISIBLE);
								hearderView.setText(String.format(
										getActivity().getString(R.string.kb_search_find_item),
										jSearchArray.length()));
								
							}
							
							passwordlist = Util.getPasswordList(jSearchArray);

							adapter = new SearchCommonAdapter(getActivity(),
									passwordlist);
							mainPageListview.setAdapter(adapter);
							adapter.notifyDataSetChanged();
							
							if (mProgressHUD.isShowing()) {

								mProgressHUD.dismiss();
							}
							
							/*if (mProgressHUD.isShowing()) {

								mProgressHUD.dismiss();
							}*/

						} 

					}

				} else {
					
					
					File yourFile = new File(fileForJson);
					FileInputStream stream = new FileInputStream(yourFile);
				    String jString = null;
				    MappedByteBuffer bb = null;
				    try {
			            FileChannel fc = stream.getChannel();
			            bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
			            /* Instead of using default, pass in a decoder. */
			            jString = Charset.defaultCharset().decode(bb).toString();
				    } catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
				    	try {
							stream.close();
							bb.clear();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				    }
				    JSONObject jsonObj = new JSONObject(jString);
				    jSearchArray = jsonObj.getJSONArray("passwords"); 
					
					
					if (/*isArrivalTabSelected*/true) {

						/*SPreferenceUtils.saveStringPref(getActivity(),
								Constant.PREF_NAME,
								Constant.KB_ALL_PASSWORD_JSON,
								jsonArray.toString());
						SPreferenceUtils
								.saveBoolPref(
										getActivity(),
										Constant.PREF_NAME,
										Constant.KB_ALL_PASSWORD_JSON_ISSAVED,
										true);*/

						//passwordlist = Util.getPasswordList(jsonArray);
						
						
						if(jSearchArray.length() > 0){
							hearderView.setVisibility(View.VISIBLE);
							hearderView.setText(String.format(
									getActivity().getString(R.string.kb_search_find_item),
									jSearchArray.length()));
							
						}
						
						passwordlist = Util.getPasswordList(jSearchArray);

						adapter = new SearchCommonAdapter(getActivity(),
								passwordlist);
						mainPageListview.setAdapter(adapter);
						adapter.notifyDataSetChanged();
						
						if (mProgressHUD.isShowing()) {

							mProgressHUD.dismiss();
						}
						
						/*if (mProgressHUD.isShowing()) {

							mProgressHUD.dismiss();
						}*/

					} 
				}

			} catch (JSONException e) {

				e.printStackTrace();
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}

	}

}

}