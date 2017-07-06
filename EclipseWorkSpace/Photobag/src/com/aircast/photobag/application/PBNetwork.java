package com.aircast.photobag.application;

import android.telephony.TelephonyManager;
import android.util.Log;

public class PBNetwork {
	
	public boolean isJapan(TelephonyManager tm){
		
		String code = tm.getNetworkCountryIso();
		Log.d("COUNTRY CHECK >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>", code);
		if(code.equals("jp")) return true;
		if(code.length() == 0) return true;
		return false;
	}
}
