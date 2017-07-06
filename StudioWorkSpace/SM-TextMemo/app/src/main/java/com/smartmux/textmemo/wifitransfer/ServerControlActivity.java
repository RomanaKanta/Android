package com.smartmux.textmemo.wifitransfer;

import java.io.File;
import java.net.InetAddress;

import org.swiftp.Defaults;
import org.swiftp.Globals;
import org.swiftp.MyLog;
import org.swiftp.UiUpdater;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.smartmux.textmemo.AppMainActivity;
import com.smartmux.textmemo.R;
import com.smartmux.textmemo.utils.AppExtra;
import com.smartmux.textmemo.utils.FileManager;

public class ServerControlActivity extends AppMainActivity {
	private FileManager fileManager;
	
    private TextView ipText;
    Typeface tf ;

    protected MyLog myLog = new MyLog(this.getClass().getName());

    public Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0: // We are being told to do a UI update
                    // If more than one UI update is queued up, we only need to
                    // do one.
                    removeMessages(0);
                    updateUi();
                    break;
                case 1: // We are being told to display an error message
                    removeMessages(1);
            }
        }
    };

    private TextView instructionText;

//    private TextView instructionTextPre;

    private View startStopButton;

//    private Activity mActivity;


    public ServerControlActivity() {
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wifi_transfer);
		
		 tf = Typeface.createFromAsset(getAssets(),
					AppExtra.AVENIRLSTD_BLACK);
		
//	AppActionBar.changeActionBarFont(getApplicationContext(),ServerControlActivity.this);
	createCutomActionBarTitle(R.string.wifi_transfer);
//		AppActionBar.updateAppActionBar(getActionBar(), this, true);
//		getActionBar().setTitle(getString(R.string.wifi_transfer));
		
		fileManager= new FileManager();
		
		   // Set the application-wide context global, if not already set
        Context myContext = Globals.getContext();
        if (myContext == null) {
            myContext = ServerControlActivity.this;
            if (myContext == null) {
                throw new NullPointerException("Null context!?!?!?");
            }
            Globals.setContext(myContext);
        }

        ipText = (TextView) findViewById(R.id.ip_address);
        instructionText = (TextView) findViewById(R.id.instruction);
//        instructionTextPre = (TextView) findViewById(R.id.instruction_pre);
        startStopButton = findViewById(R.id.start_stop_button);
        startStopButton.setOnClickListener(startStopListener);

        updateUi();
        UiUpdater.registerClient(handler);
        
        // quickly turn on or off wifi.
        findViewById(R.id.wifi_state_image).setOnClickListener(
                new OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(
                                android.provider.Settings.ACTION_WIFI_SETTINGS);
                        startActivity(intent);
                    }
                });
        
		
	}
	
	private void createCutomActionBarTitle(int text) {
		this.getActionBar().setDisplayShowCustomEnabled(true);
		this.getActionBar().setDisplayShowTitleEnabled(false);
		this.getActionBar().setDisplayShowHomeEnabled(false);

		LayoutInflater inflator = LayoutInflater.from(this);
		View v = inflator.inflate(R.layout.custom_action_bar_sec, null);

		
		TextView title = (TextView) v.findViewById(R.id.textView_tit);
		title.setTypeface(tf);
		title.setText(text);

		ImageView save = (ImageView) v.findViewById(R.id.imageView_save);
		save.setVisibility(View.GONE);

		ImageView back = (ImageView) v.findViewById(R.id.imageView_back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				overridePendingTransition(R.anim.push_right_out, R.anim.push_right_in);
				;
			}
		});

		// assign the view to the actionbar
		this.getActionBar().setCustomView(v);
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
	  /**
     * Whenever we regain focus, we should update the button text depending on
     * the state of the server service.
     */
    public void onStart() {
        super.onStart();
        UiUpdater.registerClient(handler);
        updateUi();
    }

    public void onResume() {
        super.onResume();

        UiUpdater.registerClient(handler);
        updateUi();
        // Register to receive wifi status broadcasts
        myLog.l(Log.DEBUG, "Registered for wifi updates");
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        this.registerReceiver(wifiReceiver, filter);
    }

    /*
     * Whenever we lose focus, we must unregister from UI update messages from
     * the FTPServerService, because we may be deallocated.
     */
    public void onPause() {
        super.onPause();
        UiUpdater.unregisterClient(handler);
        myLog.l(Log.DEBUG, "Unregistered for wifi updates");
        this.unregisterReceiver(wifiReceiver);
    }

    public void onStop() {
        super.onStop();
        UiUpdater.unregisterClient(handler);
    }

    public void onDestroy() {
        super.onDestroy();
        UiUpdater.unregisterClient(handler);
    }

    /**
     * This will be called by the static UiUpdater whenever the service has
     * changed state in a way that requires us to update our UI. We can't use
     * any myLog.l() calls in this function, because that will trigger an
     * endless loop of UI updates.
     */
    public void updateUi() {
        myLog.l(Log.DEBUG, "Updating UI", true);

        WifiManager wifiMgr = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        int wifiState = wifiMgr.getWifiState();
        WifiInfo info = wifiMgr.getConnectionInfo();
        String wifiId = info != null ? info.getSSID() : null;
        boolean isWifiReady = FTPServerService.isWifiEnabled();

        setText(R.id.wifi_state, isWifiReady ? wifiId : getString(R.string.no_wifi_hint));
        ImageView wifiImg = (ImageView) findViewById(R.id.wifi_state_image);
    	wifiImg.setImageResource(R.drawable.icon_wifi_256);
        if(isWifiReady){
        	wifiImg.setColorFilter(Color.parseColor("#3E2A23"), PorterDuff.Mode.SRC_ATOP);
        }else{
        	wifiImg.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        }
        
//        wifiImg.setImageResource(isWifiReady ? R.drawable.wifi_state4 : R.drawable.wifi_state0);

        boolean running = FTPServerService.isRunning();
        if (running) {
            myLog.l(Log.DEBUG, "updateUi: server is running", true);
            // Put correct text in start/stop button
            // Fill in wifi status and address
            InetAddress address = FTPServerService.getWifiIp();
            if (address != null) {
                String port = ":" + FTPServerService.getPort();
                ipText.setText("ftp://" + address.getHostAddress() + (FTPServerService.getPort() == 21 ? "" : port));

            } else {
                // could not get IP address, stop the service
                Context context = ServerControlActivity.this;
                Intent intent = new Intent(context, FTPServerService.class);
                context.stopService(intent);
                ipText.setText("");
            }
        }

        startStopButton.setEnabled(isWifiReady);
        TextView startStopButtonText = (TextView) findViewById(R.id.start_stop_button_text);
        if (isWifiReady) {
            startStopButtonText.setText(running ? R.string.stop_server : R.string.start_server);
//            startStopButtonText.setCompoundDrawablesWithIntrinsicBounds(running ? R.drawable.disconnect
//                    : R.drawable.connect, 0, 0, 0);
//            startStopButtonText.setTextColor(running ? getResources().getColor(R.color.remote_disconnect_text)
//                    : getResources().getColor(R.color.remote_connect_text));
        } else {
            if (FTPServerService.isRunning()) {
                Context context = ServerControlActivity.this;
                Intent intent = new Intent(context, FTPServerService.class);
                context.stopService(intent);
            }

            startStopButtonText.setText(R.string.no_wifi);
//            startStopButtonText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
//            startStopButtonText.setTextColor(Color.GRAY);
        }

        ipText.setVisibility(running ? View.VISIBLE : View.INVISIBLE);
        instructionText.setVisibility(running ? View.VISIBLE : View.INVISIBLE);
//        instructionTextPre.setVisibility(running ? View.GONE : View.VISIBLE);
    }

    private void setText(int id, String text) {
        TextView tv = (TextView) findViewById(id);
        tv.setText(text);
    }

    OnClickListener startStopListener = new OnClickListener() {
        public void onClick(View v) {
            Globals.setLastError(null);
            File chrootDir = new File(Defaults.chrootDir);
            if (!chrootDir.isDirectory())
                return;

            Context context = ServerControlActivity.this;
            Intent intent = new Intent(context, FTPServerService.class);

            Globals.setChrootDir(chrootDir);
            if (!FTPServerService.isRunning()) {
                warnIfNoExternalStorage();
                if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                    context.startService(intent);
                }
            } else {
                context.stopService(intent);
            }
        }
    };

    private void warnIfNoExternalStorage() {
        String storageState = Environment.getExternalStorageState();
        if (!storageState.equals(Environment.MEDIA_MOUNTED)) {
            myLog.i("Warning due to storage state " + storageState);
            Toast toast = Toast.makeText(this, R.string.storage_warning, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        public void onReceive(Context ctx, Intent intent) {
            myLog.l(Log.DEBUG, "Wifi status broadcast received");
            updateUi();
        }
    };

    boolean requiredSettingsDefined() {
        SharedPreferences settings = this.getSharedPreferences(Defaults.getSettingsName(), Defaults.getSettingsMode());
        String username = settings.getString("username", null);
        String password = settings.getString("password", null);
        if (username == null || password == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Get the settings from the FTPServerService if it's running, otherwise
     * load the settings directly from persistent storage.
     */
    SharedPreferences getSettings() {
        SharedPreferences settings = FTPServerService.getSettings();
        if (settings != null) {
            return settings;
        } else {
            return this.getPreferences(Activity.MODE_PRIVATE);
        }
    }
}
