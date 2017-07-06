package com.smartmux.banglaebook.plus.adapter.helper;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.smartmux.banglaebook.plus.model.ItemRow;
import com.smartmux.banglaebook.plus.model.ItemRow.DownloadState;
import com.smartmux.banglaebook.plus.util.ProgressHUD;
import com.smartmux.banglaebook.plus.util.ProgressWheel;

public class BookDownloadTask extends AsyncTask<String, String, String> {
	
	 private ItemRow row;
	 private OnTaskCompleted listener;
	 private ProgressWheel progressDownload;
	 ProgressHUD mProgressHUD;
	 Context context;


	public BookDownloadTask(Context context, ItemRow row,OnTaskCompleted listener) {
		super();
		this.row = row;
		this.listener =  listener;
		this.context = context;
		
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mProgressHUD = ProgressHUD.show(context,
				"Downloading Book Plaese Wait", true);
	
		 progressDownload = row.getProgressBar();
		 if(progressDownload != null) {
			 
				progressDownload.setVisibility(View.VISIBLE);
				progressDownload.spin();
		 }
		
	}

	@Override
	protected String doInBackground(String... aurl) {
		int count;
		row.setDownloadState(DownloadState.DOWNLOADING);
		try {
			String filename = row.getTitle() + ".pdf";
			URL url = new URL(row.getPdfUrl());
			URLConnection conexion = url.openConnection();
			conexion.connect();

			int lenghtOfFile = conexion.getContentLength();
			Log.d("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);

			InputStream input = new BufferedInputStream(url.openStream());
			OutputStream output = new FileOutputStream(
					getSaveFilePath(filename));

			byte data[] = new byte[1024];

			long total = 0;

			while ((count = input.read(data)) != -1) {
				total += count;
				publishProgress(""+ (int) ((total * 100) / lenghtOfFile));
				output.write(data, 0, count);
			}

			output.flush();
			output.close();
			input.close();
		} catch (Exception e) {
		}
		return null;

	}

	protected void onProgressUpdate(String... values) {
		
//		mProgressHUD.setMessage("progress" + values[0]);
		
		 row.setProgress((Float.parseFloat(values[0])));
		
		    if(progressDownload != null) {
		    	progressDownload.setProgress(row.getProgress());
		    	progressDownload.setText(values[0] + "%");
		    }
	}

	@Override
	protected void onPostExecute(String unused) {
		super.onPostExecute(unused);
		
		row.setDownloadState(DownloadState.COMPLETE);
		 if(progressDownload != null) {
			 
				progressDownload.stopSpinning();
				progressDownload.setVisibility(View.INVISIBLE);
		 }
		 if (mProgressHUD.isShowing()) {

				mProgressHUD.dismiss();
			}
		listener.onTaskCompleted(row);
		

	}
	
	public static File getSaveFilePath(String fileName) {
		File dir = new File(Environment.getExternalStorageDirectory(), "Books");
		dir.mkdirs();
		File file = new File(dir, fileName);
		return file;
	}


}
