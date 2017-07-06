package com.smartux.photocollage.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class CustomLoader extends AsyncTask<Void, Void, Void> {

	private ProgressDialog progress = null;
private Context context;
	
	public CustomLoader(Context ctx) {
		context = ctx;
	}

	@Override
	protected void onPreExecute() {
		progress = ProgressDialog.show(context, null,
				"Loading");

		super.onPreExecute();
	}

	@Override
	protected Void doInBackground(Void... params) {
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		progress.dismiss();
		super.onPostExecute(result);


		}

	@Override
	protected void onCancelled() {
		super.onCancelled();
	}

	@Override
	protected void onCancelled(Void result) {
		super.onCancelled(result);
	}

	@Override
	protected void onProgressUpdate(Void... values) {
		super.onProgressUpdate(values);
	}
	
	
}
