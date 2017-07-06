package com.smartmux.videodownloader.BrowserUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;

import com.smartmux.videodownloader.MainActivity;
import com.smartmux.videodownloader.R;
import com.smartmux.videodownloader.utils.SMSharePref;

public class SMDownloadListener implements DownloadListener {
    private Context context;

    public SMDownloadListener(Context context) {
        super();
        this.context = context;
    }

    
    
    @Override
    public void onDownloadStart(final String url, String userAgent, final String contentDisposition, final String mimeType, long contentLength) {
        final Context holder = IntentUnit.getContext();
        
        final String filename = URLUtil.guessFileName(url, contentDisposition, mimeType);
        
        if (holder == null || !(holder instanceof Activity)) {
//            BrowserUnit.download(context, url, contentDisposition, mimeType);
            
//        	SMSharePref.saveDownloadUrl(context,url,filename);
//			Intent intent = new Intent(context,
//					MainActivity.class);
//			intent.putExtra("settab", "Downloads");
//			context.startActivity(intent);
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(holder);
        builder.setCancelable(false);

        builder.setTitle(R.string.dialog_title_download);
        builder.setMessage(URLUtil.guessFileName(url, contentDisposition, mimeType));

        builder.setPositiveButton(R.string.dialog_button_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                BrowserUnit.download(holder, url, contentDisposition, mimeType);
            	SMSharePref.saveDownloadUrl(context,url,filename);
    			Intent intent = new Intent(context,
    					MainActivity.class);
    			intent.putExtra("settab", "Downloads");
    			context.startActivity(intent);
            }
        });
  
        builder.setNegativeButton(R.string.dialog_button_negative, null);
        builder.create().show();
    }
}
