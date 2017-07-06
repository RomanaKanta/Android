package com.roundflat.musclecard.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ClickHandlerReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
		Log.d("new mgs", "new convertion");
		Log.d("intent.getAction()", intent.getAction());

		if (intent != null) {

			if (intent.getAction().equals("ListItemClick")) {
				
				

			}

			if (intent.getAction().equals("ButtonClick")) {

		

			}
	     }
		
	}

}
