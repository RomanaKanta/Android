package com.smartmux.banglaebook.plus;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.artifex.mupdfdemo.MuPDFActivity;
import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.smartmux.banglaebook.plus.adapter.BookListAdapter;
import com.smartmux.banglaebook.plus.adapter.helper.BookDownloadTask;
import com.smartmux.banglaebook.plus.adapter.helper.OnTaskCompleted;
import com.smartmux.banglaebook.plus.model.BannerItem;
import com.smartmux.banglaebook.plus.model.ItemRow;
import com.smartmux.banglaebook.plus.model.ItemRow.DownloadState;
import com.smartmux.banglaebook.plus.util.Constant;
import com.smartmux.banglaebook.plus.util.EBookPreferenceUtils;
import com.smartmux.banglaebook.plus.util.ImageLoader;
import com.smartmux.banglaebook.plus.util.JSONParser;
import com.smartmux.banglaebook.plus.util.ProgressHUD;
import com.smartmux.banglaebook.plus.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
//import com.google.analytics.tracking.android.EasyTracker;
//import com.google.analytics.tracking.android.MapBuilder;

public class MainActivity extends Activity implements OnClickListener,
		OnItemClickListener, OnTaskCompleted {

	private final static String TAG = MainActivity.class.getSimpleName();
	JSONArray books = null;
	ArrayList<ItemRow> bookList = new ArrayList<ItemRow>();

	SlidingMenu menu;
	Button btnSlide;
	int btnWidth;
	LinearLayout shadowContent;
	// private EasyTracker easyTracker = null;
	BookListAdapter bookGridAdapter;

	private PullToRefreshListView pullRefreshListview;
	ListView bookListView;
	private int position;

    SegmentTabLayout tabLayout;
    private String[] tabTitles = {"All Books", "My Books"};
    TextView no_book;
    EditText search;
    CharSequence searchChar = "";

    FragmentTransaction fragmentTransaction;

	@Override
	public void onStart() {
		super.onStart();
		// EasyTracker.getInstance(this).activityStart(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		// EasyTracker.getInstance(this).activityStop(this);
	}

    public void setPermission(){

        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
               // Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
               // Toast.makeText(MainActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }


        };

        new TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);

        setPermission();

        setSideView();

        no_book = (TextView)findViewById(R.id.on_mybook);
        search = (EditText)findViewById(R.id.editText_serach_book);
        tabLayout = (SegmentTabLayout) findViewById(R.id.segment_tablayout);

		pullRefreshListview = (PullToRefreshListView) findViewById(R.id.book_list);
		pullRefreshListview.setMode(Mode.PULL_FROM_START);
        bookListView = pullRefreshListview.getRefreshableView();

        loadLocalJson();
        tabLayoutInit();
        fragmentTransaction = getFragmentManager().beginTransaction();

        tabLayout.setSelected(true);

		pullRefreshListview
				.setOnRefreshListener(new OnRefreshListener<ListView>() {

					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {

						String label = "Checking Book";
						refreshView.getLoadingLayoutProxy()
								.setLastUpdatedLabel(label);

						// Do work to refresh the list here.
						new GetNewDataTask().execute();
					}
				});



        bookListView.setOnItemClickListener(this);

        loadAllBooks();

        setBookSearch();

        boolean isAgreementShow = EBookPreferenceUtils.getBoolPref(
                getApplicationContext(), Constant.PREF_NAME,
                Constant.AGREEMENT_SHOW, false);
        if (!isAgreementShow) {
            agreementDialog();
        }
	}

    private void setBookSearch(){


            search.setCursorVisible(true);

            search.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence cs, int arg1, int arg2,
                                          int arg3) {
                    searchChar = cs;
                    if(bookGridAdapter!=null && bookGridAdapter.getList() != null) {




                        bookGridAdapter.getFilter().filter(cs.toString());

                }
                }


                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1,
                                              int arg2, int arg3) {
                }

                @Override
                public void afterTextChanged(Editable arg0) {

                }
            });



    }

    private void setSideView(){

        btnSlide = (Button) findViewById(R.id.btnSlide);
        btnSlide.setOnClickListener(this);
        findViewById(R.id.button_feedback).setOnClickListener(this);
        // configure the SlidingMenu
        menu = new SlidingMenu(this);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setShadowDrawable(R.drawable.shadow);
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        // menu.setBehindCanvasTransformer(t)
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(R.layout.agreement_view);

        shadowContent = (LinearLayout) findViewById(R.id.shadow_content);

        menu.setOnOpenListener(new SlidingMenu.OnOpenListener() {

            @Override
            public void onOpen() {

                shadowContent.setVisibility(View.VISIBLE);
                shadowContent.setBackgroundColor(Color.parseColor("#90000000"));

            }

        });
        menu.setOnCloseListener(new SlidingMenu.OnCloseListener() {

            @Override
            public void onClose() {

                shadowContent.setVisibility(View.GONE);

            }

        });



    }

    private void tabLayoutInit(){

        tabLayout.setTabData(tabTitles);

        tabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {

                 ArrayList<ItemRow> listItems = new ArrayList<ItemRow>();
                if (position == 0) {

//                    FragmentAllBookList fragment = new FragmentAllBookList();
//
//                    fragmentTransaction.replace(R.id.body_content, fragment);
//                    fragmentTransaction.commit();

                    no_book.setVisibility(View.GONE);
                    if(listItems!=null){
                        listItems = bookList;
                    }


                    Log.e("onTabSelect", "0 " + listItems.size());

                    pullRefreshListview.setMode(Mode.PULL_FROM_START);

                    //  Collections.sort(bookList, new Utils.DateComparator());

                } else if (position == 1) {

                    if(listItems!=null) {
                        listItems = loadMyBooks();
                    }

                    if(listItems.size()==0){
                        no_book.setVisibility(View.VISIBLE);
                    }else{
                        no_book.setVisibility(View.GONE);
                    }
                    Log.e("onTabSelect", "1 " + listItems.size());

                    pullRefreshListview.setMode(Mode.DISABLED);
                    // Collections.sort(bookList, new Utils.TitleComparator());

                }
                bookGridAdapter = new BookListAdapter(MainActivity.this,
                        R.layout.item_row, listItems);
                bookListView.setAdapter(bookGridAdapter);
                bookGridAdapter.notifyDataSetChanged();


                Log.e("onTabSelect", "   " + listItems.size());

                //System.out.println("onTabSelect " +position);
//                bookList = db.getAllBooks(query);
//                bookAdapter.setTempList(bookList);
//                bookAdapter.notifyDataSetChanged();
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
    }

	public void loadAllBooks() {

		boolean isLocalAvailable = EBookPreferenceUtils.getBoolPref(
				getApplicationContext(), Constant.PREF_NAME,
				Constant.ISLOCALAVAILABLE, false);

		if (bookListView.getChildCount() == 0) {

			if (isLocalAvailable) {

				String eBookJsondata = EBookPreferenceUtils.getStringPref(
						getApplicationContext(), Constant.PREF_NAME,
						Constant.EBOOK_JSON, "");
				JSONObject json;
				try {
					json = new JSONObject(eBookJsondata);
					setDataFromJson(json);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {

				new SM_AsyncTaskForGetBook().execute(Constant.EBOOK_URL);
			}

		} else {

			int position = EBookPreferenceUtils.getIntPref(
					getApplicationContext(), Constant.PREF_NAME,
					Constant.EBOOK_POSITION, 0);
            bookListView.setSelection(position - 1);
		}

	}

    public ArrayList<ItemRow> loadMyBooks() {

        ArrayList<ItemRow> values  = new ArrayList<ItemRow>();

        if(values.size()>0){
            values.clear();
        }

        if(bookList!=null){
           for(int i=0; i<bookList.size(); i++){
               File file = new File(Environment.getExternalStorageDirectory()
                       .toString(), "Books/" + bookList.get(i).getTitle() + ".pdf");
               if (file.exists()) {
                   String bengaliTitle = bookList.get(i).getBengaliTitle();
                   String title = bookList.get(i).getTitle();
                   String image = bookList.get(i).getImage();
                   String pdfUrl = bookList.get(i).getPdfUrl();
                   String pdfPage = bookList.get(i).getPage();
                   String pdfAuthor = bookList.get(i).getAurthor();
                   String pdfSize = bookList.get(i).getsize();

                   ItemRow item = new ItemRow(bengaliTitle, title, pdfPage, image,
                           pdfAuthor, pdfSize, pdfUrl, true);
                   values.add(item);
               }
           }
        }

        return values;

    }

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position,
			long arg3) {



		ItemRow rowItem = bookGridAdapter.getItem(position - 1);
		File file = new File(Environment.getExternalStorageDirectory()
				.toString(), "Books/" + rowItem.getTitle() + ".pdf");

		// filePath = file.getAbsolutePath();
		EBookPreferenceUtils.saveIntPref(getApplicationContext(),
				Constant.PREF_NAME, Constant.EBOOK_POSITION, position);
		if (!rowItem.getDownloadState().equals(DownloadState.DOWNLOADING)) {

			if (file.exists()) {

				// menu.toggle();

				Uri uri = Uri.parse(file.getAbsolutePath());
//				Intent intent = new Intent(MainActivity.this,
//						MuPDFActivity.class);
//				intent.putExtra(Constant.TAG_BTITLE, rowItem.getBengaliTitle());
//				intent.setAction(Intent.ACTION_VIEW);
//				intent.setData(uri);
//				startActivity(intent);
				// overridePendingTransition(R.anim.push_left_in,
				// R.anim.push_left_out);


				Intent intent = new Intent(MainActivity.this,
						MuPDFActivity.class);
				intent.putExtra(Constant.TAG_BTITLE, rowItem.getBengaliTitle());
				intent.setAction("mupdf");
				intent.setData(uri);
				startActivity(intent);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);


			} else {

				this.position = position - 1;
				rowItem.setDownloadState(DownloadState.QUEUED);
				rowItem.setDownload(true);
				CustomizeDialogLogout customizeDialog = new CustomizeDialogLogout(
						MainActivity.this, rowItem);
				customizeDialog.getWindow().getAttributes().windowAnimations = R.style.Animations_SmileWindow;
				customizeDialog.show();

			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();



		if (BannerItem.notifyShow) {
			notifyDialog();

			BannerItem.notifyShow = false;
		}

        if (bookList != null && bookList.size() != 0) {
            if (search.getText().toString() != "") {
                bookGridAdapter.getFilter().filter(searchChar.toString());
            }
        }
        tabLayout.getTitleView(0).setText("All Books("+ bookList.size() + ")");
        tabLayout.getTitleView(1).setText("My Books("+ loadMyBooks().size() + ")");

	}

	private void setDataFromJson(JSONObject json) {

        if(bookList!=null && bookList.size()>0){
            bookList.clear();
        }

		try {
			// Getting JSON Array
			books = json.getJSONArray(Constant.TAG_RADIOS);

			for (int i = 0; i < books.length(); i++) {
				JSONObject c = books.getJSONObject(i);
				String bengaliTitle = c.getString(Constant.TAG_BTITLE);
				String title = c.getString(Constant.TAG_ETITLE);
				String image = c.getString(Constant.TAG_THUMBURL);
				String pdfUrl = c.getString(Constant.TAG_PDFURL);
				String pdfPage = c.getString(Constant.TAG_PAGES);
				String pdfAuthor = c.getString(Constant.TAG_AUTHOR);
				String pdfSize = c.getString(Constant.TAG_SIZE);

				ItemRow item = new ItemRow(bengaliTitle, title, pdfPage, image,
						pdfAuthor, pdfSize, pdfUrl, false);
				bookList.add(item);

			}

           Log.e("setDataFromJson",""+bookList.size());

//			bookGridAdapter = new BookListAdapter(MainActivity.this,
//					R.layout.item_row, bookList);
//			bookListView.setAdapter(bookGridAdapter);
//			int position = EBookPreferenceUtils.getIntPref(
//					getApplicationContext(), Constant.PREF_NAME,
//					Constant.EBOOK_POSITION, 0);
//            bookListView.setSelection(position - 1);

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	class SM_AsyncTaskForGetBook extends AsyncTask<String, String, JSONObject> {

		ProgressHUD mProgressHUD;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressHUD = ProgressHUD.show(MainActivity.this,
					"Loading Books...", true);

		}

		@Override
		protected JSONObject doInBackground(String... params) {
			// Creating new JSON Parser
			JSONParser jParser = new JSONParser();

			// Getting JSON from URL
			JSONObject json = jParser.getJSONFromUrl(Constant.EBOOK_URL);


			return json;
		}

		@Override
		protected void onProgressUpdate(String... values) {
			mProgressHUD.setMessage(values[0]);
			super.onProgressUpdate(values);
		}

		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);
			if (mProgressHUD.isShowing()) {

				mProgressHUD.dismiss();
			}
            if(result != null){
                EBookPreferenceUtils.saveBoolPref(getApplicationContext(),
                        Constant.PREF_NAME, Constant.ISLOCALAVAILABLE, true);
                EBookPreferenceUtils.saveStringPref(getApplicationContext(),
                        Constant.PREF_NAME, Constant.EBOOK_JSON, result.toString());

                    setDataFromJson(result);


            }
		}
	}

	@Override
	public void onClick(View view) {

		if (view.getId() == R.id.btnSlide) {

			menu.toggle();

			// easyTracker.send(MapBuilder.createEvent("ButtonSlide", "onClick",
			// "homeScreen/btnSlide", null).build());

		} else if (view.getId() == R.id.button_feedback) {

            // easyTracker.send(MapBuilder.createEvent("ButtonSlide", "onClick",
            // "homeScreen/button_feedback", null).build());

			Intent i = new Intent(MainActivity.this, SMFeedBackActivity.class);
			startActivity(i);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

		}

	}

	@Override
	public void onTaskCompleted(ItemRow row) {

		File file = new File(Environment.getExternalStorageDirectory()
				.toString(), "Books/" + row.getTitle() + ".pdf");
		// Toast.makeText(getApplicationContext(), file.getName(),
		// Toast.LENGTH_SHORT).show();
		if (file.exists()) {

			View view = bookListView.getChildAt((position - 1)
					- bookListView.getFirstVisiblePosition());

			if (view == null)
				return;

			TextView readNumberOfPages = (TextView) view
					.findViewById(R.id.textview_read_numberofpages);

			readNumberOfPages.setVisibility(View.VISIBLE);
			bookGridAdapter.notifyDataSetChanged();
            bookListView.setSelection(position - 1);
		}

	}

	private class CustomizeDialogLogout extends Dialog implements
			OnClickListener {

		Button okButton;
		Button cancelButton;
		View v = null;
		ItemRow row;

		public CustomizeDialogLogout(Context context, ItemRow row) {
			super(context);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.download_confirm_dialog);
			v = getWindow().getDecorView();
			v.setBackgroundResource(android.R.color.transparent);
			okButton = (Button) findViewById(R.id.OkButton);
			okButton.setOnClickListener(this);
			cancelButton = (Button) findViewById(R.id.CancelButton);
			cancelButton.setOnClickListener(this);
			this.row = row;
		}

		@SuppressLint("NewApi")
		@Override
		public void onClick(View v) {
			/** When OK Button is clicked, dismiss the dialog */
			if (v == okButton) {

				dismiss();
				BookDownloadTask task = new BookDownloadTask(MainActivity.this,
						row, MainActivity.this);
				task.execute();
			} else if (v == cancelButton) {

				dismiss();
			}

		}

	}

	private class GetNewDataTask extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			JSONParser jParser = new JSONParser();

			// Getting JSON from URL
			JSONObject json = jParser.getJSONFromUrl(Constant.EBOOK_URL);
			EBookPreferenceUtils.saveBoolPref(getApplicationContext(),
					Constant.PREF_NAME, Constant.ISLOCALAVAILABLE, true);
			EBookPreferenceUtils.saveStringPref(getApplicationContext(),
					Constant.PREF_NAME, Constant.EBOOK_JSON, json.toString());

			return null;
		}

		@Override
		protected void onPostExecute(String response) {
			super.onPostExecute(response);
			// Call onRefreshComplete when the list has been refreshed.
			pullRefreshListview.onRefreshComplete();
			// pullRefreshGridView.onRefreshComplete();
			String eBookJsondata = EBookPreferenceUtils.getStringPref(
					getApplicationContext(), Constant.PREF_NAME,
					Constant.EBOOK_JSON, "");
			JSONObject json;
			try {
				json = new JSONObject(eBookJsondata);
				// Getting JSON Array
				books = json.getJSONArray(Constant.TAG_RADIOS);

                if(bookList!=null && bookList.size()>0){
                    bookList.clear();
                }

				for (int i = 0; i < books.length(); i++) {
					JSONObject c = books.getJSONObject(i);
					String bengaliTitle = c.getString(Constant.TAG_BTITLE);
					String title = c.getString(Constant.TAG_ETITLE);
					String image = c.getString(Constant.TAG_THUMBURL);
					String pdfUrl = c.getString(Constant.TAG_PDFURL);
					String pdfPage = c.getString(Constant.TAG_PAGES);
					String pdfAuthor = c.getString(Constant.TAG_AUTHOR);
					String pdfSize = c.getString(Constant.TAG_SIZE);

					ItemRow item = new ItemRow(bengaliTitle, title, pdfPage,
							image, pdfAuthor, pdfSize, pdfUrl, false);
					bookList.add(item);

				}


//				bookGridAdapter = new BookListAdapter(MainActivity.this,
//						R.layout.item_row, bookList);
//				// listView.setAdapter(menuAdapter);
//                bookListView.setAdapter(bookGridAdapter);
//				bookGridAdapter.notifyDataSetChanged();

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	protected ImageLoader mImageLoader;

	@SuppressLint("NewApi")
	private void notifyDialog() {

		mImageLoader = new ImageLoader(this);

		// get prompts.xml view
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		View promptView = layoutInflater.inflate(R.layout.notification_dialog,
				null);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setView(promptView);

		ImageView thumb = (ImageView) promptView
				.findViewById(R.id.notify_thumb);

		if (!TextUtils.isEmpty(BannerItem.thumbUrl)) {

			mImageLoader.DisplayImage(BannerItem.thumbUrl, thumb);

			// URL url = new URL(item.getThumbUrl());
			// Bitmap bmp =
			// BitmapFactory.decodeStream(url.openConnection().getInputStream());
			// imageView.setImageBitmap(bmp);

		}

		// thumb.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// Intent i = new Intent(Intent.ACTION_VIEW);
		// i.setData(Uri.parse(item.getActionUrl()));
		// activity.startActivity(i);
		// }
		// });

		// setup a dialog window
		alertDialogBuilder.setCancelable(true);

		alertDialogBuilder.setPositiveButton(BannerItem.text,
				new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog, int id) {

						if (!(BannerItem.actionUrl.equals(""))) {
							Intent i = new Intent(Intent.ACTION_VIEW, Uri
									.parse(BannerItem.actionUrl));
							startActivity(i);
						} else {
							Toast.makeText(getApplicationContext(),
									"URL empty", Toast.LENGTH_SHORT).show();
						}

						dialog.dismiss();
						// BannerItem.thumbUrl = "";
						// BannerItem.text = "";
						// BannerItem.actionUrl= "";
					}
				});

		// alertDialogBuilder.setOnDismissListener(new OnDismissListener() {
		//
		// @Override
		// public void onDismiss(DialogInterface dialog) {
		// BannerItem.thumbUrl = "";
		// BannerItem.text = "";
		// BannerItem.actionUrl= "";
		//
		// }
		// });
		// create an alert dialog
		alertDialogBuilder.create().show();

	}

	private void agreementDialog() {

		// get prompts.xml view
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		View promptView = layoutInflater.inflate(R.layout.agreement_view, null);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setView(promptView);


		// setup a dialog window
		alertDialogBuilder.setCancelable(false);

		alertDialogBuilder.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog, int id) {

						EBookPreferenceUtils.saveBoolPref(
								getApplicationContext(), Constant.PREF_NAME,
								Constant.AGREEMENT_SHOW, true);
						dialog.dismiss();
					}
				});

		// create an alert dialog
		alertDialogBuilder.create().show();

	}

    private void loadLocalJson(){

        if(bookList!=null && bookList.size()>0){
            bookList.clear();
        }


        try {
            JSONArray jTutorialArray = new JSONArray(
                    Util.loadJSONFromAsset(this, "ebook_data.json"));

            for (int i = 0; i < jTutorialArray.length(); i++) {
                JSONObject c = jTutorialArray.getJSONObject(i);
                String bengaliTitle = c.getString(Constant.TAG_BTITLE);
                String title = c.getString(Constant.TAG_ETITLE);
                String image = c.getString(Constant.TAG_THUMBURL);
                String pdfUrl = c.getString(Constant.TAG_PDFURL);
                String pdfPage = c.getString(Constant.TAG_PAGES);
                String pdfAuthor = c.getString(Constant.TAG_AUTHOR);
                String pdfSize = c.getString(Constant.TAG_SIZE);

                ItemRow item = new ItemRow(bengaliTitle, title, pdfPage, image,
                        pdfAuthor, pdfSize, pdfUrl, false);
                bookList.add(item);

            }
            Log.e("loadLocalJson" ,"" +bookList.size());
        } catch (JSONException e) {

            e.printStackTrace();
        }

        bookGridAdapter = new BookListAdapter(MainActivity.this,
                R.layout.item_row, bookList);
        bookListView.setAdapter(bookGridAdapter);
    }

}
