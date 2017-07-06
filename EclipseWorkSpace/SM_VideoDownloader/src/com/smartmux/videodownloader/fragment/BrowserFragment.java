package com.smartmux.videodownloader.fragment;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Patterns;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.URLUtil;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import at.huber.youtubeExtractor.YouTubeUriExtractor;
import at.huber.youtubeExtractor.YtFile;

import com.smartmux.videodownloader.BrowserListActivity;
import com.smartmux.videodownloader.MainActivity;
import com.smartmux.videodownloader.R;
import com.smartmux.videodownloader.database.VisitedSiteDataSource;
import com.smartmux.videodownloader.modelclass.VisitedSiteListModelClass;
import com.smartmux.videodownloader.utils.NetworkChecking;
import com.smartmux.videodownloader.utils.SMConstant;
import com.smartmux.videodownloader.utils.SMSharePref;
import com.smartmux.videodownloader.utils.SMToast;
import com.smartmux.videodownloader.widget.AdvancedWebView;

public class BrowserFragment extends Fragment implements OnClickListener,
		AdvancedWebView.Listener, OnTouchListener, OnLongClickListener {

	NetworkChecking mNetworkChecking;
	private static final String TAG = "BrowserFragment";
	RelativeLayout layout_browser, layout_multiple_browser = null;
	// WebView webView = null;
	AdvancedWebView webView = null;

	Button btn_down, btn_download, btn_go, btn_browser_menu, btn_bookmark_list,
			btn_multiple_browser = null;
	Button a_addbookmark, a_set_home, a_cancel = null;
	ImageView next, back = null;
	EditText etURL = null;
	String uri;
	View browser_falseView, a_falseView = null;
	ProgressBar loading = null;
	private LinearLayout layout_button;

	VisitedSiteDataSource mVisitedSiteDataSource;
	VisitedSiteListModelClass mSiteListModelClass;
	Animation viewOpen, viewClose;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		Log.v("BrowserFragment", "onCreate()");
	}

	public static BrowserFragment createInstance() {
		return new BrowserFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		RelativeLayout rootView = (RelativeLayout) inflater.inflate(
				R.layout.fragment_browser, container, false);

		mNetworkChecking = new NetworkChecking(getActivity());
		
		viewOpen = AnimationUtils
				.loadAnimation(getActivity(), R.anim.bottom_up);

		viewClose = AnimationUtils.loadAnimation(getActivity(),
				R.anim.bottom_down);

		mVisitedSiteDataSource = new VisitedSiteDataSource(getActivity());

		initFragmentContent(rootView);
		initActivityContent();
		initWebViewContent(rootView);

		return rootView;
	}

	void initFragmentContent(View rootView) {

		// // First layout ( browser page)
		layout_browser = (RelativeLayout) rootView
				.findViewById(R.id.browser_layout_first_page);

		layout_button = (LinearLayout) rootView
				.findViewById(R.id.button_layout);
		browser_falseView = (View) rootView.findViewById(R.id.falseView);
		loading = (ProgressBar) rootView
				.findViewById(R.id.falseView_progressBar);
		next = (ImageView) rootView.findViewById(R.id.button_next);
		next.setImageResource(R.drawable.button_next);
		next.setColorFilter(Color.parseColor("#FFFFFF"),
				PorterDuff.Mode.SRC_ATOP);

		back = (ImageView) rootView.findViewById(R.id.button_back);
		back.setImageResource(R.drawable.button_back);
		back.setColorFilter(Color.parseColor("#FFFFFF"),
				PorterDuff.Mode.SRC_ATOP);

		btn_down = (Button) rootView.findViewById(R.id.button_down);
		btn_download = (Button) rootView.findViewById(R.id.button_download);
		btn_browser_menu = (Button) rootView
				.findViewById(R.id.button_browser_menu);
		btn_bookmark_list = (Button) rootView
				.findViewById(R.id.button_bookmark_list);
		btn_multiple_browser = (Button) rootView
				.findViewById(R.id.button_multiple_browser);
		btn_go = (Button) rootView.findViewById(R.id.button_go);
		etURL = (EditText) rootView.findViewById(R.id.edittext_weburl);

		next.setOnClickListener(this);
		back.setOnClickListener(this);
		btn_down.setOnClickListener(this);
		btn_browser_menu.setOnClickListener(this);
		btn_bookmark_list.setOnClickListener(this);
		btn_multiple_browser.setOnClickListener(this);
		btn_download.setOnClickListener(this);
		browser_falseView.setOnClickListener(this);
		btn_go.setOnClickListener(this);

	}

	void initActivityContent() {

		getActivity().findViewById(R.id.button_one).setVisibility(View.GONE);

		getActivity().findViewById(R.id.button_two).setVisibility(View.GONE);

		getActivity().findViewById(R.id.button_three).setVisibility(View.GONE);

		a_addbookmark = (Button) getActivity().findViewById(
				R.id.button_add_bookmark);
		a_addbookmark.setText("Add to bookmark");
		a_addbookmark.setTextColor(Color.parseColor("#2C5BFF"));

		a_set_home = (Button) getActivity().findViewById(R.id.button_home_page);
		a_set_home.setText("Set to home page");
		a_set_home.setTextColor(Color.parseColor("#2C5BFF"));

		a_cancel = (Button) getActivity().findViewById(R.id.button_cancel);
		a_falseView = (View) getActivity().findViewById(R.id.tab_falseView);

		a_addbookmark.setOnClickListener(this);
		a_set_home.setOnClickListener(this);
		a_cancel.setOnClickListener(this);
		a_falseView.setOnClickListener(this);

	}

	void initWebViewContent(RelativeLayout rootView) {

		webView = (AdvancedWebView) rootView.findViewById(R.id.webview);
		if (SMSharePref.getBookmarkUrl(getActivity()).equals("")) {
			
			webView.loadUrl(SMSharePref.getUrl(getActivity()));
			
//			webView.loadUrl("https://www.youtube.com/watch?v=_jHpnb-QmTA");

		} else {

			webView.loadUrl(SMSharePref.getBookmarkUrl(getActivity()));
			SMSharePref.saveBookmarkUrl(getActivity(), "");

		}
		webView.setListener(getActivity(), this);
		webView.setOnTouchListener(this);
		webView.setOnLongClickListener(this);
		
		 webView.getSettings().setAllowFileAccess(true);
		 webView.getSettings().setPluginState(PluginState.ON);
		 webView.getSettings().setPluginState(PluginState.ON_DEMAND);
		 webView.getSettings().setLoadWithOverviewMode(true);
		 webView.getSettings().setUseWideViewPort(true);
		 webView.getSettings().setAllowContentAccess(true);
		webView.requestFocus(View.FOCUS_DOWN);


	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		 switch (event.getAction()) {
				 case MotionEvent.ACTION_DOWN:
				 case MotionEvent.ACTION_UP:
				 if (!v.hasFocus()) {
				 v.requestFocusFromTouch();
				 }
				 break;
				 }
				 return false;
	}
	
	@Override
	public boolean onLongClick(View v) {
		// TODO Auto-generated method stub
		 WebView.HitTestResult hr = ((WebView)v).getHitTestResult();

		 if (hr.getType() == WebView.HitTestResult.SRC_ANCHOR_TYPE) {
			 
			 Log.i(TAG, "getExtra = "+ hr.getExtra() + "\t\t Type= SRC_ANCHOR_TYPE" );
			 return true;
			 
         }else if (hr.getType() == WebView.HitTestResult.EDIT_TEXT_TYPE) {
        	 
			 Log.i(TAG, "getExtra = "+ hr.getExtra() + "\t\t Type= EDIT_TEXT_TYPE" );
			 return true;
			 
         }else  if (hr.getType() == WebView.HitTestResult.EMAIL_TYPE) {
        	 
			 Log.i(TAG, "getExtra = "+ hr.getExtra() + "\t\t Type= EMAIL_TYPE" );
			 return true;
			 
         }else  if (hr.getType() == WebView.HitTestResult.GEO_TYPE) {
        	 
			 Log.i(TAG, "getExtra = "+ hr.getExtra() + "\t\t Type= GEO_TYPE" );
			 return true;
			 
         }else  if (hr.getType() == WebView.HitTestResult.	IMAGE_TYPE) {
        	 
			 Log.i(TAG, "getExtra = "+ hr.getExtra() + "\t\t Type= 	IMAGE_TYPE" );
			 return true;
			 
         }else  if (hr.getType() == WebView.HitTestResult.PHONE_TYPE) {
        	 
			 Log.i(TAG, "getExtra = "+ hr.getExtra() + "\t\t Type= PHONE_TYPE" );
			 return true;
			 
         }else  if (hr.getType() == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE	) {
        	 
			 Log.i(TAG, "getExtra = "+ hr.getExtra() + "\t\t Type= SRC_IMAGE_ANCHOR_TYPE	" );
			 return true;
			 
         }else 
//        	 if (hr.getType() == WebView.HitTestResult.	UNKNOWN_TYPE)
        	 {
			 Log.i(TAG, "getExtra = "+ hr.getExtra() + "\t\t Type= 	UNKNOWN_TYPE" );
			 return true;
         }
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_next:

			next.setColorFilter(Color.parseColor("#6D6D6E"),
					PorterDuff.Mode.SRC_ATOP);
			back.setColorFilter(Color.parseColor("#FFFFFF"),
					PorterDuff.Mode.SRC_ATOP);

			if (webView.canGoForward()) {
				webView.goForward();
			} else {

				Toast.makeText(getActivity(), "Next_Null", 1000).show();
			}

			break;

		case R.id.button_back:

			back.setColorFilter(Color.parseColor("#6D6D6E"),
					PorterDuff.Mode.SRC_ATOP);
			next.setColorFilter(Color.parseColor("#FFFFFF"),
					PorterDuff.Mode.SRC_ATOP);

			if (webView.canGoBack()) {
				webView.goBack();
			} else {

				Toast.makeText(getActivity(), "Back_Null", 1000).show();
			}

			break;

		case R.id.button_download:

			String downloadURL = webView.getUrl().toString();

			if (downloadURL.toLowerCase().contains(
					SMConstant.youtube.toLowerCase())) {
				getYoutubeDownloadUrl(downloadURL);

			} else if (downloadURL.toLowerCase().contains(
					SMConstant.rdbell.toLowerCase())) {
				Toast.makeText(getActivity(), "3rd Bell", 1000).show();

			} else {
				Toast.makeText(getActivity(), "other site", 1000).show();

				// try {
				// String ur = URLGetter.getVideoUrl(downloadURL);
				//
				// Toast.makeText(getActivity(), "other site" + ur,
				// 1000).show();
				// } catch (IOException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }

			}

			break;

		case R.id.button_down:

			if (getActivity().findViewById(R.id.tabhost).getVisibility() == View.VISIBLE) {

				// getActivity().findViewById(R.id.tabhost).startAnimation(viewClose);

				getActivity().findViewById(R.id.tabhost).setVisibility(
						View.GONE);
			} else {

				getActivity().findViewById(R.id.tabhost).setVisibility(
						View.VISIBLE);
				// getActivity().findViewById(R.id.tabhost).startAnimation(viewOpen);
			}
			break;

		case R.id.button_browser_menu:

			getActivity().findViewById(R.id.layout_add_bookmark)
					.startAnimation(viewOpen);
			getActivity().findViewById(R.id.tab_falseView).setVisibility(
					View.VISIBLE);
			getActivity().findViewById(R.id.layout_add_bookmark).setVisibility(
					View.VISIBLE);

			break;

		case R.id.button_bookmark_list:

			Intent bookmark = new Intent(getActivity(),
					BrowserListActivity.class);
			bookmark.putExtra("bookmark", "bookmark");
			startActivity(bookmark);
			SMSharePref.setBackCode(getActivity());
			getActivity().overridePendingTransition(R.anim.bottom_up, 0);
			break;

		case R.id.button_multiple_browser:

			Intent intent = new Intent(getActivity(), BrowserListActivity.class);
			startActivity(intent);
			SMSharePref.setBackCode(getActivity());
			getActivity().overridePendingTransition(R.anim.bottom_up, 0);

			break;

		case R.id.button_go:
			uri = etURL.getText().toString();

			if (Patterns.WEB_URL.matcher(uri).matches()) {

				openBrowser(uri);

			} else {

				SMToast.showToast(getActivity(),
						getString(R.string.invalid_url));

			}

			break;

		case R.id.falseView:

			layout_button.startAnimation(viewClose);
			layout_button.setVisibility(View.GONE);
			browser_falseView.setVisibility(View.GONE);

			break;

		case R.id.button_add_bookmark:

			String title = webView.getTitle();

			mSiteListModelClass = new VisitedSiteListModelClass(title,
					webView.getUrl(), "1");

			long inserted = mVisitedSiteDataSource
					.addNewSite(mSiteListModelClass);
			if (inserted >= 0) {

				Log.d("FOLDER: ", "inserted");

			} else {

				Log.e("FOLDER: ", "insertion fail");

			}

			// Toast.makeText(getActivity(), "add bookmark", 1000).show();
			getActivity().findViewById(R.id.layout_add_bookmark)
					.startAnimation(viewClose);
			getActivity().findViewById(R.id.tab_falseView).setVisibility(
					View.GONE);
			getActivity().findViewById(R.id.layout_add_bookmark).setVisibility(
					View.GONE);

			break;

		case R.id.button_home_page:

			SMSharePref.saveUrl(getActivity(), webView.getUrl());
			// Toast.makeText(getActivity(), "homepage", 1000).show();
			getActivity().findViewById(R.id.layout_add_bookmark)
					.startAnimation(viewClose);
			getActivity().findViewById(R.id.tab_falseView).setVisibility(
					View.GONE);
			getActivity().findViewById(R.id.layout_add_bookmark).setVisibility(
					View.GONE);

			break;

		case R.id.button_cancel:

			getActivity().findViewById(R.id.layout_add_bookmark)
					.startAnimation(viewClose);
			getActivity().findViewById(R.id.tab_falseView).setVisibility(
					View.GONE);
			getActivity().findViewById(R.id.layout_add_bookmark).setVisibility(
					View.GONE);

			break;

		case R.id.tab_falseView:

			getActivity().findViewById(R.id.layout_add_bookmark)
					.startAnimation(viewClose);
			getActivity().findViewById(R.id.tab_falseView).setVisibility(
					View.GONE);
			getActivity().findViewById(R.id.layout_add_bookmark).setVisibility(
					View.GONE);

			break;

		default:
			break;

		}

	}

	// private String getVemoiID(String vurl){
	// String VideoID = "";
	// Pattern regex =
	// Pattern.compile("http://(?:www\\.)?youtu(?:\\.be/|be\\.com/(?:watch\\?v=|v/|embed/|user/(?:[\\w#]+/)+))([^&#?\n]+)");
	//
	// //
	// /(?:https?:\/\/)?(?:www\.)?vimeo.com\/(?:channels\/(?:\w+\/)?|groups\/([^\/]*)\/videos\/|album\/(\d+)\/video\/|)(\d+)(?:$|\/|\?)/
	// Matcher regexMatcher = regex.matcher(vurl);
	// if (regexMatcher.find()) {
	// VideoID = regexMatcher.group(1);
	// }
	//
	//
	//
	// return VideoID;
	//
	//
	// }

	private void getYoutubeDownloadUrl(String youtubeLink) {

		browser_falseView.setVisibility(View.VISIBLE);
		loading.setVisibility(View.VISIBLE);

		YouTubeUriExtractor ytEx = new YouTubeUriExtractor(getActivity()) {

			@Override
			public void onUrisAvailable(String videoId, String videoTitle,
					SparseArray<YtFile> ytFiles) {
				// mainProgressBar.setVisibility(View.GONE);

				if (ytFiles == null) {
					// Something went wrong we got no urls. Always check this.
					getActivity().finish();
					return;
				}
				// Iterate over itags
				for (int i = 0, itag = 0; i < ytFiles.size(); i++) {
					itag = ytFiles.keyAt(i);
					// ytFile represents one file with its url and meta data
					YtFile ytFile = ytFiles.get(itag);

					// Just add videos in a decent format => height -1 = audio
					if (ytFile.getMeta().getHeight() == -1
							|| ytFile.getMeta().getHeight() >= 360) {
						addButtonToMainLayout(videoTitle, ytFile);

					}
				}
			}
		};
		// Ignore the google proprietary webm format
		ytEx.setIncludeWebM(false);
		ytEx.setParseDashManifest(true);
		// Lets execute the request
		ytEx.execute(youtubeLink);

	}

	private void addButtonToMainLayout(final String videoTitle,
			final YtFile ytfile) {
		// Display some buttons and let the user choose the format
		String btnText = (ytfile.getMeta().getHeight() == -1) ? "Audio "
				+ ytfile.getMeta().getAudioBitrate() + " kbit/s" : ytfile
				.getMeta().getHeight() + "p";
		btnText += (ytfile.getMeta().isDashContainer()) ? " dash" : "";
		Button btn = new Button(getActivity());
		btn.setText(btnText);
		layout_button.setVisibility(View.VISIBLE);
		loading.setVisibility(View.GONE);
		layout_button.addView(btn);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String filename;
				if (videoTitle.length() > 55) {
					filename = videoTitle.substring(0, 55) + "."
							+ ytfile.getMeta().getExt();
				} else {
					filename = videoTitle + "." + ytfile.getMeta().getExt();
				}
				filename = filename.replaceAll(
						"\\\\|>|<|\"|\\||\\*|\\?|%|:|#|/", "");

				layout_button.setVisibility(View.GONE);
				browser_falseView.setVisibility(View.GONE);

				SMSharePref.saveDownloadUrl(getActivity(), ytfile.getUrl(),
						videoTitle);

				MainActivity.mTabHost.setCurrentTab(1);

			}
		});

	}

	private void openBrowser(String url) {

		if (!url.trim().startsWith("http://")) {
			url = "http://" + url.trim();
		}
		webView.loadUrl(url.trim());
	}


	private void getAllLink(String url) {
		// TODO Auto-generated method stub
		Document doc;
		try {

			// need http protocol
			doc = Jsoup.connect(url).get();

			// get page title
			String title = doc.title();
			System.out.println("title : " + title);

			// get all links
			Elements links = doc.select("a[href]");
			for (Element link : links) {

				// get the value from href attribute
				System.out.println("\nlink : " + link.attr("href"));
				System.out.println("text : " + link.text());

			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		
		if(!mNetworkChecking.isNetworkAvailable(getActivity())){
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(
					getActivity());

			alertDialog.setTitle(R.string.no_network);
			alertDialog.setMessage(R.string.alart_msg);
			//alertDialog.setCancelable(false);
			alertDialog.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int which) {
							dialog.cancel();
						}
					});
			alertDialog.show();
		}
		
		webView.onResume();
	}

	@Override
	public void onPause() {
		webView.onPause();
		super.onPause();
	}

	@Override
	public void onDestroy() {
		webView.onDestroy();
		super.onDestroy();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		webView.onActivityResult(requestCode, resultCode, intent);
	}

	@Override
	public void onPageStarted(String url, Bitmap favicon) {
		Log.d("Page Loading Started", url);
	}

	@Override
	public void onPageFinished(String url) {

		String title = webView.getTitle();

		mSiteListModelClass = new VisitedSiteListModelClass(title, url, "0");

		long inserted = mVisitedSiteDataSource.addNewSite(mSiteListModelClass);

		if (inserted >= 0) {
			Log.d("FOLDER: ", "inserted");
		} else {
			Log.e("FOLDER: ", "insertion fail");
		}

	}

	@Override
	public void onPageError(int errorCode, String description, String failingUrl) {
		Log.e("Page Loading Error", description);
	}

	@Override
	public void onDownloadRequested(String url, String userAgent,
			String contentDisposition, String mimetype, long contentLength) {

		final String filename = URLUtil.guessFileName(url, contentDisposition,
				mimetype);

		layout_button.setVisibility(View.GONE);
		browser_falseView.setVisibility(View.GONE);

		Log.e("UUUUU: ", "" + url);
		Log.e("DDDDD: ", "" + contentDisposition);
		Log.e("MMMM: ", "" + mimetype);

		if (mimetype.endsWith("/mp4")) {
			
			SMSharePref.saveDownloadUrl(getActivity(), url, filename);

			MainActivity.mTabHost.setCurrentTab(1);
		}else if(mimetype.endsWith("/zip")){
			
			Toast.makeText(getActivity(), "file in zip format", 1000).show();
			
		}

	}

	@Override
	public void onExternalPageRequest(String url) {

	}
	
}
