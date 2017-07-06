package com.aircast.photobag.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
 
public class PushDialogActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Bundle bundle = getIntent().getExtras();
        String message = bundle.getString("MESSAGE");
        String title = bundle.getString("TITLE");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        
        if(title == null) return;
        if(message == null) return;
        	
        builder.setTitle(title);
        builder.setMessage(message).setNegativeButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}