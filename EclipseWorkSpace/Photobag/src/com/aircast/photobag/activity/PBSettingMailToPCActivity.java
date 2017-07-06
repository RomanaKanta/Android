package com.aircast.photobag.activity;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.aircast.photobag.R;
import com.aircast.photobag.api.PBAPIHelper;
import com.aircast.photobag.api.ResponseHandle;
import com.aircast.photobag.api.ResponseHandle.Response;
import com.aircast.photobag.application.PBApplication;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.utils.PBPreferenceUtils;
import com.aircast.photobag.widget.FButton;
import com.aircast.photobag.widget.actionbar.ActionBar;

/**
 * Send mail to PC so user can upload from PC.
 * */
public class PBSettingMailToPCActivity extends PBAbsActionBarActivity
implements OnClickListener {
	
    private FButton mDoneButton;    
    private static final int MSG_URL_SUC = 1;
    private static String urlForPC;

    private Context cxt;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pb_layout_setting_mail_to_pc);
        
        ActionBar headerBar = (ActionBar) findViewById(R.id.headerbar);
        setHeader(headerBar, getString(R.string.pb_setting_mail_to_pc));
        
        cxt = PBApplication.getBaseApplicationContext();
        
        mDoneButton = (FButton) findViewById(R.id.btn_setting_sendmail);
        mDoneButton.setOnClickListener(this);
    }

	@Override
	public void onClick(View view) {
		
        if (!PBApplication.hasNetworkConnection()) {
            PBApplication.makeToastMsg(getString(R.string.pb_network_error_content));
            return;
        }
		
        mDoneButton.setClickable(false);
		new Thread() {
			@Override
        	public void run(){
				urlForPC = CreateUrlForPC();
				
				handlerEmailIntent.sendEmptyMessage(MSG_URL_SUC);
        	}
		}.start();
	}
	
    private Handler handlerEmailIntent = new Handler() {
    	@Override
    	public void handleMessage(Message msg){
    		switch (msg.what) {
    		case MSG_URL_SUC:
    			mDoneButton.setClickable(true);
    			
    			Intent sendIntent = new Intent(Intent.ACTION_SEND);
    	    	sendIntent.putExtra(Intent.EXTRA_EMAIL, "");
    	    	
                String mailTextPre = getString(R.string.pb_setting_mailtext_pre);
                String mailTextPos = getString(R.string.pb_setting_mailtext_pos);

                String manufacturer = Build.MANUFACTURER;
                if(manufacturer.equals(PBConstant.MANUFACTURER_AMAZON)){
                	mailTextPre = mailTextPre.replace("\n", "<br/>");
                	mailTextPos = mailTextPos.replace("\n", "<br/>");
                }
                
    	    	sendIntent.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.pb_setting_mail_title));
    	    	
    			if (!TextUtils.isEmpty(urlForPC)) {
    				sendIntent.putExtra(Intent.EXTRA_TEXT, mailTextPre
    						+ urlForPC
    						+ mailTextPos);
    			}
    			else {
    				sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.pb_setting_mailtext_error));
    			}
    	    	
    	    	// for real phone
    	    	sendIntent.setType(PBConstant.INTENT_TYPE_SENDMAIL);
    	    	// for test in emulator
    	    	//sendIntent.setType(PBConstant.INTENT_TYPE_SENDMAIL_TEST);
    	    	
    	        PackageManager packageManager = cxt.getPackageManager();
    	        List<ResolveInfo> list = packageManager.queryIntentActivities(sendIntent,
    	                PackageManager.GET_ACTIVITIES);
    	        if (list.size() != 0){    	
    	    	    startActivity(sendIntent);
    	        }
    			
    			break;
    		default:
    			break;
    		}
    	};
    };
	
	private String CreateUrlForPC(){		
		
        String token =  PBPreferenceUtils.getStringPref(cxt, PBConstant.PREF_NAME, 
                PBConstant.PREF_NAME_TOKEN,"");
        Response response = PBAPIHelper.getPCSigninURL(token);
        
        if (response.errorCode != ResponseHandle.CODE_200_OK) {
            return null;
        }
        try {
            JSONObject result = new JSONObject(response.decription);

            if (result != null) {
                if (cxt == null) {
                    return null;
                }
                if (result.has("url")) {
                	return result.getString("url");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
		return null;
	}

	@Override
	protected void handleHomeActionListener() {
		finish();
	}

	@Override
	protected void setLeftBtnsDetailHeader(ActionBar headerBar) {		
	}
}
