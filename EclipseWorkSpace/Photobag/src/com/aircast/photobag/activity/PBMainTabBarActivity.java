package com.aircast.photobag.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.RejectedExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.aircast.photobag.R;
import com.aircast.photobag.api.PBAPIContant;
import com.aircast.photobag.api.PBAPIHelper;
import com.aircast.photobag.api.PBTaskDealADAchievements;
import com.aircast.photobag.api.PBTaskGetFreePeriod;
import com.aircast.photobag.api.ResponseHandle;
import com.aircast.photobag.api.ResponseHandle.Response;
import com.aircast.photobag.application.PBApplication;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.database.PBDatabaseDefinition;
import com.aircast.photobag.database.PBDatabaseManager;
import com.aircast.photobag.fragment.PBDownloadFragment;
import com.aircast.photobag.fragment.PBHistoryMainFragment;
import com.aircast.photobag.fragment.PBMyPageMainFragment;
import com.aircast.photobag.fragment.PBUploadMainFragment;
import com.aircast.photobag.model.PBTimelineHistoryModel;
import com.aircast.photobag.model.PBTimelineHistoryModel.Type;
import com.aircast.photobag.services.UpdateHistoryService;
import com.aircast.photobag.utils.PBGeneralUtils;
import com.aircast.photobag.utils.PBPreferenceUtils;
import com.aircast.photobag.widget.BadgeView;
import com.aircast.photobag.widget.actionbar.ActionBar;
import com.aircast.photobag.widget.actionbar.ActionBar.Action;
import com.aircast.photobag.widget.actionbar.ActionBar.IntentAction;

public class PBMainTabBarActivity extends FragmentActivity implements
		TabHost.OnTabChangeListener {
	public TabInfo mLastTab = null;
	public TabHost mTabHost;
	public HashMap mapTabInfo = new HashMap();
	public TextView notifyTxT;
	// private TextView title;
	private Button topleftSideButton;
	int kumaMemoUpdateNumber;
	boolean isShowKumaMemo = false;
	private ActionBar mHeaderBar;
	public static PBMainTabBarActivity sMainContext;

	private Action mGoToSettingScreenAction;
	private Action mGoToMailScreenAction;
	private Action mGoToDeleteHistoryAction;
	private PBDatabaseManager mDatabaseManager;

	public static AlertDialog mConfirmationForExitTabBar;

	// Atik device migration lock check
	public static boolean isDeviceLock = false;
	// public static boolean isExecuteLockAPI = false;

	public static String message_response_check_lock_status = "";
	public static String titte_after_start_migration = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main_tab_bar_new);
		runSetupNotificationIcon();
		mDatabaseManager = PBDatabaseManager.getInstance(this);
		sMainContext = this;
		PBApplication.LANG = Locale.getDefault().toString();
		topleftSideButton = (Button) findViewById(R.id.Button_actionbar_back);
		notifyTxT = (TextView) findViewById(R.id.textView_kumaMemoUpdateNumber);
		// title.setText("公開袋");
		// topleftSideButton.setText(getString(R.string.kb_kumamemo_button_text));

		// 2012.02.14 @lent add header bar layout #S
		mHeaderBar = (ActionBar) findViewById(R.id.headerbar);
		mHeaderBar.setTitle(R.string.tab_mnu_dowload_main);
		// mHeaderBar.setHomeLogo(android.R.drawable.ic_menu_more);

		// set Home action button
		final Action homeAction = new IntentAction(this, null, R.drawable.icon);
		homeAction.setBackground(Color.TRANSPARENT);
		mHeaderBar.setHomeLogo(R.drawable.icon);

		// check newest version update
		PBApplication.startGetParameters();

		PBApplication.setHandlerToTokenUpdateTask(mHandler);

		// get free period from server
		String token = PBPreferenceUtils.getStringPref(this,
				PBConstant.PREF_NAME, PBConstant.PREF_NAME_TOKEN, "");

		if (!TextUtils.isEmpty(token)) {
			mTaskGetFreePeriod = new PBTaskGetFreePeriod();
			if (mTaskGetFreePeriod.getStatus() != AsyncTask.Status.RUNNING) {
				try {
					mTaskGetFreePeriod.execute();
				} catch (RejectedExecutionException e) {
					e.printStackTrace();
				}
			}

			try {
				new PBTaskDealADAchievements().execute();
			} catch (Exception e) {
			}
		}
		// 20120626 add to support download file from nakamap <S>
		Intent intent = getIntent();
		if (Intent.ACTION_VIEW.equals(intent.getAction())) {
			Uri uri = intent.getData();
			if (uri.getAuthority().contains("download")) {
				String pwd = uri.getQueryParameter("password");
				PBDownloadFragment.passwordFromUrl = pwd;
			}
		}

		mHeaderBar.removeAllActions();

		this.listNotificationListener = new ArrayList<PBMainTabBarActivity.TimelineNotificationListener>();
		pollingThread.start();

		// initialize TabHost

		initialiseTabHost(savedInstanceState);

		// Open App From Outside,when user select Image from Other App,Like
		// Gallery etc
		if (Intent.ACTION_SEND_MULTIPLE.equals(intent.getAction())
				|| Intent.ACTION_SEND.equals(intent.getAction())) {
			PBMainTabBarActivity.sMainContext.mTabHost
					.setCurrentTabByTag(PBConstant.UPLOAD_TAG);
		}

		// Atik execute device migration check
		if (PBApplication.hasNetworkConnection()) {

			new PBTaskCheckDeviceLockStatus().execute();

		}
		
		if (PBApplication.hasNetworkConnection()) {
			IntentFilter i = new IntentFilter();
			i.addAction("photobag.history.update");
			registerReceiver(historyDataReceiver, i);
		}

		
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		// Toast.makeText(sMainContext, "On Destroy", 1000).show();
		if (historyDataReceiver != null) {
			 unregisterReceiver(historyDataReceiver);
		}
		UpdateHistoryService.UPDATE_TIME_PERIOD = 1000 * 3 * 60;
		
		super.onDestroy();
	}

	private boolean isPollingTimelineNotification = true;
	private long pollingTimeUpdate = 30 * 60 * 1000;
	private Thread pollingThread = new Thread(new Runnable() {

		@Override
		public void run() {
			while (isPollingTimelineNotification) {
				checkNewTimelineNotification();
				try {
					Thread.sleep(pollingTimeUpdate);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	});

	private Handler checknotificationHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == PBTaskCheckNotification.NEW_NOTIF) {
				int newHoney = PBPreferenceUtils.getIntPref(sMainContext,
						PBConstant.PREF_NAME,
						PBConstant.PREF_NAME_NOTIF_HONEY_NEW, 0);
				int newMaple = PBPreferenceUtils.getIntPref(sMainContext,
						PBConstant.PREF_NAME,
						PBConstant.PREF_NAME_NOTIF_MAPLE_NEW, 0);
				int newDonguri = PBPreferenceUtils.getIntPref(sMainContext,
						PBConstant.PREF_NAME,
						PBConstant.PREF_NAME_NOTIF_DONGURI_NEW, 0);
				int newGold = PBPreferenceUtils.getIntPref(sMainContext,
						PBConstant.PREF_NAME,
						PBConstant.PREF_NAME_NOTIF_GOLD_NEW, 0);
				if (newHoney + newMaple + newDonguri + newGold > 0) {

					// Atik comment out the code for tab view notification
					// change
					/*
					 * showNotificationTab(3, "" + (newMaple + newHoney +
					 * newDonguri + newGold));
					 */

					showNotificationTab(4, ""
							+ (newMaple + newHoney + newDonguri + newGold));

					String notif = msg.obj == null ? "" : msg.obj.toString();

					if (PBAPIContant.DEBUG) {

						System.out.println("Atik notif: " + notif);
						System.out.println("Atik honey: " + newHoney);
						System.out.println("Atik maple: " + newMaple);
						System.out.println("Atik donguri: " + newDonguri);
						System.out.println("Atik gold: " + newGold);
					}

					for (TimelineNotificationListener listener : listNotificationListener) {

						listener.onReceiveNotification(newHoney, newMaple,
								newDonguri, newGold, notif);
					}
				}
			}

		}
	};

	/**
	 * Register the listener. The new notification (point history) will be
	 * broadcast.
	 * 
	 * @param listener
	 */
	public void registerNotificationListener(
			TimelineNotificationListener listener) {
		if (listener == null)
			return;
		for (TimelineNotificationListener item : listNotificationListener) {
			if (item.equals(listener))
				return;
		}
		this.listNotificationListener.add(listener);
	}

	/**
	 * Unregister the point history notification listener(if exists). If not,
	 * nothing happened
	 * 
	 * @param listener
	 */
	public void unregisterNotificationListener(
			TimelineNotificationListener listener) {
		if (listener == null)
			return;
		this.listNotificationListener.remove(listener);
	}

	/**
	 * Will set all point history to be "Read". This will set directly to 0
	 * notification. Will hide the notification label at Tab bar "my page"
	 */
	public void setMarkAllNotificationRead() {

		new Thread(new Runnable() {
			@Override
			public void run() {

				PBDatabaseManager.getInstance(getApplicationContext())
						.setMarkAllReadTimelineHistory();
				PBPreferenceUtils.saveIntPref(sMainContext,
						PBConstant.PREF_NAME,
						PBConstant.PREF_NAME_NOTIF_HONEY_NEW, 0);
				PBPreferenceUtils.saveIntPref(sMainContext,
						PBConstant.PREF_NAME,
						PBConstant.PREF_NAME_NOTIF_MAPLE_NEW, 0);
				PBPreferenceUtils.saveIntPref(sMainContext,
						PBConstant.PREF_NAME,
						PBConstant.PREF_NAME_NOTIF_DONGURI_NEW, 0);
				PBPreferenceUtils.saveIntPref(sMainContext,
						PBConstant.PREF_NAME,
						PBConstant.PREF_NAME_NOTIF_GOLD_NEW, 0);
				PBMainTabBarActivity.this.checknotificationHandler
						.post(new Runnable() {

							@Override
							public void run() {

								hideNotificationTab(4);
							}
						});

			}
		}).start();
	}

	/**
	 * Interface of TimelineNotificationListener, which is serve as listener
	 * class to receive new notification of point history timeline
	 * 
	 * @author agung
	 * 
	 */
	public interface TimelineNotificationListener {
		public void onReceiveNotification(int newHoney, int newMaple,
				int newDonguri, int newGold, String notif);

	}

	/**
	 * list to hold all reference of notification listener
	 */
	private ArrayList<TimelineNotificationListener> listNotificationListener;

	private PBTaskCheckNotification checkNotifTask;

	/**
	 * Task to check new notification from server. Until now, no push
	 * notification from server, then current implementation just doing polling
	 * from server.
	 */
	public class PBTaskCheckNotification extends AsyncTask<Void, Void, Boolean> {
		private PBTimelineHistoryModel newestEntry;
		private int rows = 20;
		private Handler handler;
		public static final int NEW_NOTIF = 1;
		public static final int NO_NEW_NOTIF = 2;
		private int newNotif;
		String notification;

		public PBTaskCheckNotification(Handler handler) {
			this.handler = handler;
			newNotif = 0;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			String token = PBPreferenceUtils.getStringPref(
					PBApplication.getBaseApplicationContext(),
					PBConstant.PREF_NAME, PBConstant.PREF_NAME_TOKEN, "");
			if (TextUtils.isEmpty(token)) {

				Log.e("AGUNG",
						"TOKEN EMPTY at CHECK NOTIFICATION TIMELINE TASK");
				return false;
			}
			newestEntry = null;// PBDatabaseManager
			// .getInstance(getApplicationContext())
			// .getNewestTimelineHistory();
			int older_than = -1;
			if (newestEntry != null) {
				older_than = newestEntry.getId();
			}
			Response response = PBAPIHelper.getTimelineHistory(rows,
					older_than, "ja", token);

			if (PBAPIContant.DEBUG) {
				Log.d("AGUNG", "CHECK TIME LINE NOTIF TASK :"
						+ response.decription);
			}

			if (response.errorCode == ResponseHandle.CODE_200_OK) {
				try {
					ResponseHandle.parseTimelineHistoryAndSaveToDatabase(
							getApplicationContext(), response.decription);

				} catch (Exception e) {
					Log.e("AGUNG",
							"error at fetch check notification"
									+ e.getMessage());
				}

				PBDatabaseManager dbMan = PBDatabaseManager
						.getInstance(sMainContext);
				int newHoney = dbMan.getTotalNewHoney();
				int newMaple = dbMan.getTotalNewMaple();
				int newDonguri = dbMan.getTotalNewDonguri();
				int newGold = dbMan.getTotalNewGold();
				newNotif = newHoney + newMaple + newDonguri + newGold;
				Log.d("AGUNG", "newnotif : " + newNotif);

				PBPreferenceUtils.saveIntPref(sMainContext,
						PBConstant.PREF_NAME,
						PBConstant.PREF_NAME_NOTIF_HONEY_NEW, newHoney);
				PBPreferenceUtils.saveIntPref(sMainContext,
						PBConstant.PREF_NAME,
						PBConstant.PREF_NAME_NOTIF_MAPLE_NEW, newMaple);
				PBPreferenceUtils.saveIntPref(sMainContext,
						PBConstant.PREF_NAME,
						PBConstant.PREF_NAME_NOTIF_DONGURI_NEW, newDonguri);
				PBPreferenceUtils.saveIntPref(sMainContext,
						PBConstant.PREF_NAME,
						PBConstant.PREF_NAME_NOTIF_GOLD_NEW, newGold);

				newestEntry = PBDatabaseManager.getInstance(
						getApplicationContext()).getNewestTimelineHistory();
				notification = "";
				if (newestEntry != null) {
					if (newestEntry.getType() == Type.ACORN) {
						notification = sMainContext
								.getString(R.string.pb_my_page_acorn_get);
					} else if (newestEntry.getType() == Type.HONEY) {
						notification = sMainContext
								.getString(R.string.pb_my_page_sp_honey_get);
					} else if (newestEntry.getType() == Type.MAPLE) {
						notification = sMainContext
								.getString(R.string.pb_my_page_honey_get);
					} else if (newestEntry.getType() == Type.GOLDACORN) {
						notification = sMainContext
								.getString(R.string.pb_my_page_gold_get);
					}
				}
				return true;

			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			Message message = new Message();
			if (result) {
				message.what = NEW_NOTIF;
				message.arg1 = newNotif;
				message.obj = notification;
				this.handler.sendMessage(message);
			} else {
				message.what = NO_NEW_NOTIF;
				this.handler.sendMessage(message);
			}
		}
	}

	/**
	 * Will trigger checking new notification
	 */
	private void checkNewTimelineNotification() {
		Log.d("AGUNG", "CHECK TIME LINE NOTIF");
		if (checkNotifTask == null
				|| (checkNotifTask != null && checkNotifTask.getStatus() != AsyncTask.Status.RUNNING)) {
			checkNotifTask = new PBTaskCheckNotification(
					checknotificationHandler);
			checkNotifTask.execute();
		} else {

			Log.d("AGUNG", "CANT CHECK TIME LINE NOTIF AT THIS TIME");
		}
	}

	/**
	 * another class can request the task to check new notification from server
	 */
	public void requestCheckTimelineNotification() {

		Log.d("AGUNG", "REQUEST TIME LINE NOTIF");
		if (checkNotifTask == null
				|| (checkNotifTask != null && checkNotifTask.getStatus() != AsyncTask.Status.RUNNING)) {
			checkNotifTask = new PBTaskCheckNotification(
					checknotificationHandler);
			checkNotifTask.execute();
		} else {

			Log.d("AGUNG", "CANT REQUEST TIME LINE NOTIF AT THIS TIME");
		}
	}

	private HashMap<Integer, View> notificationViewMap;
	private DisplayMetrics dm;
	private ViewGroup mainScreen;
	private int[] offsetError;
	private TabWidget tab;
	private int[] locationTab;

	private void animateTranslate(View view, float xFrom, float xTo,
			float yFrom, float yTo, Interpolator interpolator, long duration,
			final Runnable onFinished, boolean fillAfter) {
		TranslateAnimation animation = new TranslateAnimation(
				TranslateAnimation.ABSOLUTE, xFrom,
				TranslateAnimation.ABSOLUTE, xTo, TranslateAnimation.ABSOLUTE,
				yFrom, TranslateAnimation.ABSOLUTE, yTo);
		if (interpolator != null)
			animation.setInterpolator(interpolator);
		animation.setDuration(duration);
		animation.setFillBefore(true);
		animation.setFillEnabled(true);
		animation.setFillAfter(fillAfter);

		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				animation.setAnimationListener(null);
				if (onFinished != null)
					onFinished.run();
			}
		});
		view.startAnimation(animation);
	}

	private void runSetupNotificationIcon() {
		this.notificationViewMap = new HashMap<Integer, View>();
		this.mainScreen = (ViewGroup) findViewById(R.id.main_content_layout);
		this.tab = (TabWidget) findViewById(android.R.id.tabs);
		this.dm = getResources().getDisplayMetrics();
		this.offsetError = new int[2];
		this.locationTab = new int[2];

		this.mainScreen.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						// make sure it is not called anymore
						mainScreen.getViewTreeObserver()
								.removeGlobalOnLayoutListener(this);
						updatePositionWhenLayoutingDoneForNotificationPurpose();
						// showNotificationTab(3, "123");
					}
				});
	}

	static int count = 0;

	private void updatePositionWhenLayoutingDoneForNotificationPurpose() {
		Log.e("AGUNG", "Called : " + count);
		offsetError[0] = dm.widthPixels - mainScreen.getWidth();
		offsetError[1] = dm.heightPixels - mainScreen.getHeight();
		this.tab.getLocationOnScreen(this.locationTab);
	}

	private class NotificationHolder {
		public TextView notifTextView;
	}

	public void showNotificationTab(int tabNumber, String text) {
		View temp = this.notificationViewMap.get(tabNumber);
		NotificationHolder holder = null;
		if (temp == null) {
			LayoutInflater inflater = LayoutInflater.from(this);
			temp = inflater.inflate(R.layout.notification_icon_item, null);
			holder = new NotificationHolder();
			holder.notifTextView = (TextView) temp
					.findViewById(R.id.notification_icon_text);
			temp.setTag(holder);
			this.notificationViewMap.put(tabNumber, temp);
			temp.setVisibility(View.INVISIBLE);

			this.mainScreen.addView(temp, 0);
			temp.setVisibility(View.INVISIBLE);
			temp.measure(0, 0);

			int width = temp.getMeasuredWidth();
			int height = temp.getMeasuredHeight();

			holder.notifTextView.setText(text);
			int totalTab = tab.getTabCount();

			// int mostLeft = this.locationTab[1] - offsetError[0];
			int mostLeft = this.locationTab[0] - offsetError[0];
			int mostTop = this.locationTab[1] - offsetError[1];
			System.out.println("Atik mostLeft:" + mostLeft);
			System.out.println("Atik mostTop:" + mostTop);

			// int targetY = (int) (mostTop - 0.3 * height);
			int targetY = (int) (mostTop - 0.3 * height);
			System.out.println("Atik target Y for badge display:" + targetY);

			int targetX = (int) (mostLeft + (tab.getWidth() / totalTab)
					* tabNumber - 0.2 * width);
			System.out.println("Atik target X for badge display:" + targetX);

			DisplayMetrics displayMetrics = new DisplayMetrics();
			WindowManager wm = (WindowManager) getApplicationContext()
					.getSystemService(Context.WINDOW_SERVICE); // the results
																// will be
																// higher than
																// using the
																// activity
																// context
																// object or the
																// getWindowManager()
																// shortcut
			wm.getDefaultDisplay().getMetrics(displayMetrics);
			int screenWidth = displayMetrics.widthPixels;
			int screenHeight = displayMetrics.heightPixels;

			System.out.println("Atik target Screen width is:" + screenWidth);
			System.out.println("Atik target Screen hight is:" + screenHeight);

			if (screenWidth <= 480) { // below XHDPI device
				targetY = 70;
				targetX = 430;
			} else { // from and above XHDPI device
				targetY = 140;
				targetX = 970;
			}
			/*
			 * targetY = 140; targetX = 970;
			 */

			int[] tempArr = new int[2];
			temp.getLocationOnScreen(tempArr);
			tempArr[0] -= offsetError[0];
			tempArr[1] -= offsetError[1];
			int translateX = targetX - tempArr[0];
			int translateY = targetY - tempArr[1];
			final View notifView = temp;
			animateTranslate(temp, 0, translateX, 0, translateY,
					new LinearInterpolator(), 0, new Runnable() {

						@Override
						public void run() {
							if (notifView == null)
								return;
							notifView.setVisibility(View.VISIBLE);

							final ViewGroup parent = (ViewGroup) notifView
									.getParent();
							if (parent != null)
								parent.bringChildToFront(notifView);

						}
					}, true);

			/*
			 * badgeForTab = new BadgeView(this,temp); badgeForTab.setText("1");
			 */
		} else {

			temp.setVisibility(View.INVISIBLE);
			holder = (NotificationHolder) temp.getTag();
			holder.notifTextView.setText(text);
			temp.setVisibility(View.VISIBLE);
		}

		/*
		 * View temp = this.notificationViewMap.get(tabNumber); badgeForTab =
		 * new BadgeView(this,temp); badgeForTab.setText("1");
		 */
	}

	public void hideNotificationTab(final int tabNumber) {
		Log.d("AGUNG", "hide notification at tab:" + tabNumber);
		final View notifView = this.notificationViewMap.get(tabNumber);
		if (notifView == null) {

			Log.e("AGUNG", "Notification view is null");
			return;
		}

		notifView.setVisibility(View.INVISIBLE);
		mainScreen.removeView(notifView);
		notificationViewMap.put(tabNumber, null);
	}

	private final int MSG_UPDATE_UI = 103;
	/** handle use to update UI */
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_UPDATE_UI: // new newest message
				showNotifyNewestVersion((String) msg.obj);
				break;
			default:
				break;
			}
		};
	};

	private PBTaskGetFreePeriod mTaskGetFreePeriod;

	/*
	 * This method will be called when launch mode is singleTask
	 */
	public void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);

		// Intent intent = getIntent();
		if (Intent.ACTION_VIEW.equals(intent.getAction())) {
			Uri uri = intent.getData();
			if (uri.getAuthority().contains("download")) {
				String pwd = uri.getQueryParameter("password");
				PBDownloadFragment.passwordFromUrl = pwd;
			}
		}

		// System.out.println("Atik Called in OnNewIntent");
	}

	@Override
	protected void onResume() {

		// 20120217 @lent5 #E
		super.onResume();
		// 20120217 @lent5 #S
		// clear text and change tab if return when download complete
		/*
		 * if (getIntent().getStringExtra(PBConstant.TAB_SET_BY_TAG) != null &&
		 * PBConstant.HISTORY_TAG.equals(getIntent().getStringExtra(
		 * PBConstant.TAB_SET_BY_TAG))) { PBTabBarActivity.sMainContext.mTabHost
		 * .setCurrentTabByTag(PBConstant.HISTORY_TAG); }
		 */

		if (PBConstant.IS_HISTORY_TAG) {
			if (getIntent().getStringExtra(PBConstant.TAB_SET_BY_TAG) != null
					&& PBConstant.HISTORY_TAG.equals(getIntent()
							.getStringExtra(PBConstant.TAB_SET_BY_TAG))) {
				PBMainTabBarActivity.sMainContext.mTabHost
						.setCurrentTabByTag(PBConstant.HISTORY_TAG);
				PBConstant.IS_HISTORY_TAG = false;
			}

		}

		if ((PBDownloadFragment.sDownloadComplete == 2
				&& PBMainTabBarActivity.sMainContext != null && PBMainTabBarActivity.sMainContext.mTabHost != null)) {
			PBMainTabBarActivity.sMainContext.mTabHost
					.setCurrentTabByTag(PBConstant.HISTORY_TAG);
		}

		if (PBDownloadFragment.sDownloadComplete != 0) {
			PBDownloadFragment.sDownloadComplete = 0;
		}

		int newHoney = PBPreferenceUtils.getIntPref(sMainContext,
				PBConstant.PREF_NAME, PBConstant.PREF_NAME_NOTIF_HONEY_NEW, 0);
		int newMaple = PBPreferenceUtils.getIntPref(sMainContext,
				PBConstant.PREF_NAME, PBConstant.PREF_NAME_NOTIF_MAPLE_NEW, 0);
		int newDonguri = PBPreferenceUtils
				.getIntPref(sMainContext, PBConstant.PREF_NAME,
						PBConstant.PREF_NAME_NOTIF_DONGURI_NEW, 0);
		int newGold = PBPreferenceUtils.getIntPref(sMainContext,
				PBConstant.PREF_NAME, PBConstant.PREF_NAME_NOTIF_GOLD_NEW, 0);
		Log.e("AGUNG", "ON RESUME TAB *" + (newHoney + newMaple + newDonguri));
		if (newHoney + newMaple + newDonguri + newGold <= 0)
			hideNotificationTab(4);

		
		UpdateHistoryService.UPDATE_TIME_PERIOD = 60000;
		if(PBConstant.doUpdate){
			PBConstant.doUpdate = false;
			UpdateHistoryService.UPDATE_TIME_PERIOD = 100;
		}
		this.startService(new Intent(getApplicationContext(),
				UpdateHistoryService.class));
	}

	private HistoryDataReceiver historyDataReceiver = new HistoryDataReceiver();

	@Override
	protected void onPause() {
		super.onPause();
		
		UpdateHistoryService.UPDATE_TIME_PERIOD = 1000 * 3 * 60;
		this.stopService(new Intent(sMainContext, UpdateHistoryService.class));
	}

	public class HistoryDataReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {



			if (intent.getExtras() != null) {

				int groupStatus = intent.getExtras().getInt("status");
				if (groupStatus == 1) {

					Cursor mCursor = mDatabaseManager
							.getHistoriesCursor(PBDatabaseDefinition.HISTORY_INBOX);

					updateNumber = 0;
					if (mCursor.moveToFirst()) {
						do {

							if (mCursor
									.getInt(mCursor
											.getColumnIndex(PBDatabaseDefinition.HistoryData.C_IS_UPDATABLE)) == 1) {

								updateNumber++;
							}
						} while (mCursor.moveToNext());
					}

					showUpdateNotification(updateNumber);
				} else {

				}

			}
			;

		}
	}

	private int updateNumber = 0;
	private BadgeView txtNotificationUpdate;

	public void showUpdateNotification(int updateNumber) {
		if(PBHistoryMainFragment.isAttach){
			TabInfo newTab = (TabInfo) this.mapTabInfo.get(PBConstant.HISTORY_TAG);
			PBHistoryMainFragment fragment = (PBHistoryMainFragment) newTab.fragment;
			fragment.initialHistoryData(true);
		}
	
		txtNotificationUpdate.setTextSize(12);
		txtNotificationUpdate.setBadgePosition(BadgeView.POSITION_CENTER);
		txtNotificationUpdate.setText("" + updateNumber);
		
		
		if (updateNumber > 0) {

			if (!txtNotificationUpdate.isShown()) {

				txtNotificationUpdate.show();
			}

		} else {
			
			txtNotificationUpdate.hide();

		}

	}

	@Override
	public void onBackPressed() {
		// super.onBackPressed();


		TabInfo newTab = (TabInfo) this.mapTabInfo.get(PBConstant.HISTORY_TAG);
		PBHistoryMainFragment fragment = (PBHistoryMainFragment) newTab.fragment;
		if (fragment != null) {

			if (PBHistoryMainFragment.isDeleteModeIsSelected) {

				fragment.setupForDeleteModeWhenBackKeyPressed();
				return;
			}
		}

		AlertDialog.Builder exitDialog = new AlertDialog.Builder(
				new ContextThemeWrapper(this, R.style.popup_theme));
		exitDialog.setTitle("");
		exitDialog.setMessage(getString(R.string.app_exit_message));
		exitDialog.setCancelable(false);
		exitDialog.setPositiveButton(getString(R.string.dialog_ok_btn),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						finish();
					}
				});
		exitDialog.setNegativeButton(getString(R.string.dialog_cancel_btn),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

					}
				});

		exitDialog.show();

	}

	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		super.startActivityForResult(intent, requestCode);
	}

	//
	private class TabInfo {
		private String tag;
		private Class clss;
		private Bundle args;
		private Fragment fragment;

		TabInfo(String tag, Class clazz, Bundle args) {
			this.tag = tag;
			this.clss = clazz;
			this.args = args;
		}

	}

	class TabFactory implements TabHost.TabContentFactory {

		private final Context mContext;

		public TabFactory(Context context) {
			mContext = context;
		}

		public View createTabContent(String tag) {
			View v = new View(mContext);
			v.setMinimumWidth(0);
			v.setMinimumHeight(0);
			return v;
		}

	}

	@SuppressWarnings("unchecked")
	private void initialiseTabHost(Bundle args) {

		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();
		TabInfo tabInfo = null;

		View tabViewDownload = createTabView(mTabHost.getContext(),
				getString(R.string.tab_mnu_dowload_main),
				R.drawable.pb_bg_tab_dl);
		PBMainTabBarActivity.addTab(
				this,
				this.mTabHost,
				this.mTabHost.newTabSpec(PBConstant.DOWNLOAD_TAG).setIndicator(
						tabViewDownload),
				(tabInfo = new TabInfo(PBConstant.DOWNLOAD_TAG,
						PBDownloadFragment.class, args)));
		this.mapTabInfo.put(tabInfo.tag, tabInfo);

		View tabViewUpload = createTabView(mTabHost.getContext(),
				getString(R.string.tab_mnu_upload_main),
				R.drawable.pb_bg_tab_ul);
		PBMainTabBarActivity.addTab(this, this.mTabHost, this.mTabHost
				.newTabSpec(PBConstant.UPLOAD_TAG).setIndicator(tabViewUpload),
				(tabInfo = new TabInfo(PBConstant.UPLOAD_TAG,
						PBUploadMainFragment.class, args)));
		this.mapTabInfo.put(tabInfo.tag, tabInfo);

		View tabViewHistory = createTabView(mTabHost.getContext(),
				getString(R.string.tab_mnu_history_main),
				R.drawable.pb_bg_tab_history);
		PBMainTabBarActivity.addTab(this, this.mTabHost, this.mTabHost
				.newTabSpec(PBConstant.HISTORY_TAG)
				.setIndicator(tabViewHistory), (tabInfo = new TabInfo(
				PBConstant.HISTORY_TAG, PBHistoryMainFragment.class, args)));
		this.mapTabInfo.put(tabInfo.tag, tabInfo);

		View tabViewMy = createTabView(mTabHost.getContext(),
				getString(R.string.tab_mnu_my_page_main),
				R.drawable.pb_bg_tab_my_page);
		PBMainTabBarActivity.addTab(this, this.mTabHost, this.mTabHost
				.newTabSpec(PBConstant.MY_PAGE_TAG).setIndicator(tabViewMy),
				(tabInfo = new TabInfo(PBConstant.MY_PAGE_TAG,
						PBMyPageMainFragment.class, args)));
		this.mapTabInfo.put(tabInfo.tag, tabInfo);

		this.onTabChanged(PBConstant.DOWNLOAD_TAG);
		mTabHost.setOnTabChangedListener(this);
		
		
		
		 txtNotificationUpdate = new BadgeView(this,
				mTabHost.getTabWidget(), 2);

	}

	private View createTabView(Context context, String text, int resID) {

		View view = LayoutInflater.from(context).inflate(
				R.layout.pb_tabbar_item, null);
		TextView tv = (TextView) view.findViewById(R.id.tabsText);
		tv.setTextColor(Color.WHITE);
		if (!TextUtils.isEmpty(text)) {
			tv.setText(text);
		}
		ImageView tabIcon = (ImageView) view.findViewById(R.id.tabsIcon);
		tabIcon.setBackgroundResource(resID);
		return view;
	}

	private static void addTab(PBMainTabBarActivity activity, TabHost tabHost,
			TabHost.TabSpec tabSpec, TabInfo tabInfo) {

		tabSpec.setContent(activity.new TabFactory(activity));
		String tag = tabSpec.getTag();
		tabInfo.fragment = activity.getSupportFragmentManager()
				.findFragmentByTag(tag);
		if (tabInfo.fragment != null && !tabInfo.fragment.isDetached()) {
			android.support.v4.app.FragmentTransaction ft = activity
					.getSupportFragmentManager().beginTransaction();
			ft.detach(tabInfo.fragment);
			ft.commit();
			activity.getSupportFragmentManager().executePendingTransactions();
		}

		tabHost.addTab(tabSpec);
	}

	@Override
	public void onTabChanged(String tabId) {

		final TabInfo newTab = (TabInfo) this.mapTabInfo.get(tabId);

		if (mLastTab != newTab) {
			FragmentTransaction ft = this.getSupportFragmentManager()
					.beginTransaction();

			if (mLastTab != null) {
				if (mLastTab.fragment != null) {

					ft.hide(mLastTab.fragment);
				}
			}
			if (newTab != null) {

				if (newTab.fragment == null) {
					newTab.fragment = Fragment.instantiate(this,
							newTab.clss.getName(), newTab.args);
					ft.add(R.id.bodycontent, newTab.fragment, newTab.tag);
				} else {

					ft.show(newTab.fragment);
				}
			}
			mLastTab = newTab;
			ft.commit();

			if (mHeaderBar != null) {

				mHeaderBar.setTitle(getTitle(tabId));
				if (TextUtils.equals(PBConstant.MY_PAGE_TAG, tabId)) {
					mHeaderBar.removeAllActions();
					if (mGoToSettingScreenAction == null) {
						mGoToSettingScreenAction = new ActionBar.ViewAction(
								PBMainTabBarActivity.this,
								new ActionBar.PerformActionListener() {

									@Override
									public void performAction(View view) {
										// TODO Auto-generated method stub
										if (!isDeviceLock) {
											Intent settingIntent = new Intent(
													PBMainTabBarActivity.this,
													PBSettingMainActivity.class);
											startActivity(settingIntent);
										} else {
											updateUISuccessfull(
													message_response_check_lock_status,
													titte_after_start_migration);
										}

									}
								}, R.drawable.setting_button);
					}

					mGoToSettingScreenAction.setBackground(getResources()
							.getDrawable(R.drawable.actionbar_home_btn));
					mHeaderBar.addAction(mGoToSettingScreenAction);

				} else if ((TextUtils.equals(PBConstant.DOWNLOAD_TAG, tabId))
						|| (TextUtils.equals(PBConstant.UPLOAD_TAG, tabId))) {

					mHeaderBar.removeAllActions();
					if (mGoToMailScreenAction == null) {
						mGoToMailScreenAction = new ActionBar.ViewAction(
								PBMainTabBarActivity.this,
								new ActionBar.PerformActionListener() {
									@Override
									public void performAction(View view) {

										if (!PBApplication
												.hasNetworkConnection()) {
											/*
											 * PBApplication
											 * .makeToastMsg(getString(R.string.
											 * pb_network_not_available_general_message
											 * ));
											 */
											Toast toast = Toast
													.makeText(
															PBMainTabBarActivity.this,
															getString(R.string.pb_network_not_available_general_message),
															1000);
											TextView v1 = (TextView) toast
													.getView()
													.findViewById(
															android.R.id.message);
											if (v1 != null)
												v1.setGravity(Gravity.CENTER);
											toast.show();
											return;
										}
										Intent settingIntent = new Intent(
												PBMainTabBarActivity.this,
												PBMailReportActivity.class);
										startActivity(settingIntent);
									}
								}, R.drawable.mail_button);
					}
					mGoToMailScreenAction.setBackground(getResources()
							.getDrawable(R.drawable.actionbar_home_btn));
					mHeaderBar.addAction(mGoToMailScreenAction);

				} else if ((TextUtils.equals(PBConstant.HISTORY_TAG, tabId))) {

					mHeaderBar.removeAllActions();
					if (mGoToDeleteHistoryAction == null) {
						mGoToDeleteHistoryAction = new ActionBar.ViewAction(
								PBMainTabBarActivity.this,
								new ActionBar.PerformActionListener() {

									@Override
									public void performAction(View view) {

										PBHistoryMainFragment fragment = (PBHistoryMainFragment) newTab.fragment;
										fragment.refreshContent();
									}
								}, R.drawable.icon_delete);
					}

					mGoToDeleteHistoryAction.setBackground(getResources()
							.getDrawable(R.drawable.actionbar_home_btn));
					mHeaderBar.addAction(mGoToDeleteHistoryAction);
				}

				else {
					mHeaderBar.removeAllActions();
				}
			}

		}
	}

	/**
	 * check version package, there's update package or not and notify to user
	 * that the place to get newest package
	 */
	private void showNotifyNewestVersion(String reponseJson) {
		if (TextUtils.isEmpty(reponseJson))
			return;
		JSONObject result;
		String message = "";
		String urlMarket = "";
		String version = "1.0";
		try {
			result = new JSONObject(reponseJson);

			if (result != null) {
				if (result.has("message")) {
					message = result.getString("message");
				}
				if (result.has("url")) {
					urlMarket = result.getString("url");
				}
				if (result.has("newest")) {
					version = result.getString("newest");
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		// show popup dialog
		if (PBMainTabBarActivity.sMainContext != null) {
			final String urlUpdate = urlMarket;
			PBGeneralUtils.showAlertDialogAction(
					PBMainTabBarActivity.sMainContext, String.format(
							getString(R.string.pb_dialog_version_title),
							version),
					getString(R.string.pb_dialog_version_message),
					getString(R.string.dialog_ok_btn),
					getString(R.string.dialog_cancel_btn),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (!TextUtils.isEmpty(urlUpdate)) {
								Intent i = new Intent(Intent.ACTION_VIEW);
								i.setData(Uri.parse(urlUpdate));
								if (PBMainTabBarActivity.sMainContext != null) {
									PBMainTabBarActivity.sMainContext
											.startActivity(i);
								}
							}

							dialog.dismiss();
						}
					});
		}
	}

	/*
	 * Atik Device unlock status check method
	 */

	private class PBTaskCheckDeviceLockStatus extends
			AsyncTask<Void, Void, Void> {

		boolean result200Ok = false;
		boolean result400 = false;

		@Override
		protected Void doInBackground(Void... params) {

			System.out
					.println("Atik called for device lock status check doInBackground in MainTabbarActivity");
			String migrationCode = PBPreferenceUtils.getStringPref(
					PBApplication.getBaseApplicationContext(),
					PBConstant.PREF_NAME, PBConstant.PREF_MIGRATON_CODE, "");

			if (TextUtils.isEmpty(migrationCode)) {
				System.out
						.println("Atik no migration code is set MainTabbarActivity");
				return null;
			}

			System.out.println("Atik  migration code is :" + migrationCode);
			Response res = PBAPIHelper
					.checkDeviceLockForDeviceChange(migrationCode);
			System.out.println("atik response:" + res.errorCode);
			Log.d("MIGRATION_CODE", "res: " + res.errorCode + " "
					+ res.decription);
			if (res != null) {

				if (res.errorCode == ResponseHandle.CODE_200_OK) {
					result200Ok = true;
					System.out.println("atik MIGRATION_CHECK_LOCK_STATUS"
							+ "200 OK:" + res.decription
							+ ":MainTabbarActivity");
					isDeviceLock = true;

					// Atik parsing messasge from server

					try {
						JSONObject result = new JSONObject(res.decription);
						if (result.has("message")) {
							System.out.println("200 OK message is:"
									+ result.getString("message"));
							message_response_check_lock_status = result
									.getString("message");

						}
						if (result.has("result")) {
							result.getString("result");
						}

						if (result.has("title")) {
							titte_after_start_migration = result
									.getString("title");
						}

						System.out.println("Atik device lock status is 200 OK");
						isDeviceLock = true;

					} catch (JSONException e) {
						System.out.println("MIGRATION_CODE"
								+ " Json parse exception occured");
					}

				} else if (res.errorCode == ResponseHandle.CODE_400) {
					result400 = true;
					System.out
							.println("Atik device lock status is 400 in MainTabbarActivity");

					isDeviceLock = false;
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (result200Ok) {
				isDeviceLock = true;

			} else if (result400) {
				isDeviceLock = false;
			}
		}
	}

	/**
	 * get string title of tab screen
	 * 
	 * @param tag
	 * @return
	 */
	private String getTitle(String tag) {

		if (TextUtils.equals(PBConstant.DOWNLOAD_TAG, tag)) {
			return getString(R.string.screen_title_dowload_input_pwd);
		} else if (TextUtils.equals(PBConstant.UPLOAD_TAG, tag)) {
			return getString(R.string.screen_title_upload_main);
		} else if (TextUtils.equals(PBConstant.HISTORY_TAG, tag)) {
			return getString(R.string.screen_title_history_main);
		} else if (TextUtils.equals(PBConstant.MY_PAGE_TAG, tag)) {
			return getString(R.string.screen_title_my_page_main);
		}

		return getString(R.string.pb_app_name);
	}

	private DialogInterface.OnClickListener mOnClickOkDialogMigrationVerified = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
		}
	};

	// Update UI when received merge code successfully
	private void updateUISuccessfull(final String message, final String title) {

		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				// Delay dialog display after 1s = 1000ms
				PBGeneralUtils.showAlertDialogActionWithOnClick(
						PBMainTabBarActivity.this, title, message,
						getString(R.string.dialog_ok_btn),
						mOnClickOkDialogMigrationVerified);
				// Toast.makeText(PBTabBarActivity.this, "別の端末へ移行済みです。",
				// Toast.LENGTH_LONG).show();

			}
		}, 1000);

	}

}