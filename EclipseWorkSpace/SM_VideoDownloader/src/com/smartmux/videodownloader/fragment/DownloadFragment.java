package com.smartmux.videodownloader.fragment;

import java.io.File;
import java.util.ArrayList;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.Directory;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.smartmux.videodownloader.R;
import com.smartmux.videodownloader.adapter.DownloadListAdapter;
import com.smartmux.videodownloader.database.DownloadDataSource;
import com.smartmux.videodownloader.database.VideoDataSource;
import com.smartmux.videodownloader.modelclass.DownloadListModelClass;
import com.smartmux.videodownloader.modelclass.FileListModelClass;
import com.smartmux.videodownloader.utils.SMConstant;
import com.smartmux.videodownloader.utils.SMSharePref;
import com.smartmux.videodownloader.utils.VideoInfoUtils;
import com.thin.downloadmanager.DefaultRetryPolicy;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListener;
import com.thin.downloadmanager.RetryPolicy;
import com.thin.downloadmanager.ThinDownloadManager;

public class DownloadFragment extends Fragment {

	private SharedPreferences prefs;
	VideoDataSource mVideoDataSource;
	DownloadDataSource mDownloadDataSource;
	private ThinDownloadManager downloadManager;
	MyDownloadStatusListener myDownloadStatusListener;
	private static final int DOWNLOAD_THREAD_POOL_SIZE = 4;
	private Uri destinationUri;
	String downloadUrl;
	ArrayList<String> urllist = new ArrayList<String>();
	int itemID = 0;
	int down = 0;
	// int downloadID = 0;
	ArrayList<DownloadRequest> downloadRequests = new ArrayList<DownloadRequest>();

	static File mediaFile = null;
	ListView mDownloadList = null;
	// DownloadRequest downloadRequest1;
	DownloadListAdapter mDownloadListAdapter;

	int mProgress = 0;
	DownloadListModelClass mDownloadListModelClass = new DownloadListModelClass();
	ArrayList<DownloadListModelClass> mModelArrayList = new ArrayList<DownloadListModelClass>();
	File folder;
	int listSize = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_download, container,
				false);
		
		mDownloadDataSource = new DownloadDataSource(getActivity());
		mVideoDataSource = new VideoDataSource(getActivity());
		createDirectory();

		folder = new File(SMConstant.mediaStorageDir.getPath());
		mDownloadList = (ListView) rootView
				.findViewById(R.id.listview_download);

		mDownloadListAdapter = new DownloadListAdapter(getActivity(),
				mModelArrayList);
		mDownloadList.setAdapter(mDownloadListAdapter);

		downloadUrl = SMSharePref.getDownloadUrl(getActivity());

		listSize = mDownloadListAdapter.getCount();

		downloadRequests.add(new DownloadRequest());

		if (!(downloadUrl.equals(""))) {

			String title = SMSharePref.getUrlTitle(getActivity());

			destinationUri = getOutputMediaFileUri(title);

			DownloadRequest downloadRequest1;

			downloadRequest1 = downloadRequests.get(listSize);

			downloadProcess(downloadUrl, title, downloadRequest1, down);

			// Toast.makeText(
			// getActivity(),
			// "List " + listSize + "req" + downloadRequests.size() + "id"
			// + down, 2000).show();
			String size = "0";
			prepend(mModelArrayList, new DownloadListModelClass(down, title,
					size, String.valueOf(mProgress)));
			mDownloadListAdapter.notifyDataSetChanged();

			down++;

			System.out.println("URL==> " + downloadUrl);
			System.out.println("title==> " + title);

			SMSharePref.saveDownloadUrl(getActivity(), "", "");

		}

		return rootView;
	}

	void prepend(ArrayList<DownloadListModelClass> list,
			final DownloadListModelClass first) {

		list.add(0, first);
	}

	public void downloadProcess(String url, String title,
			DownloadRequest downloadRequest1, int dID) {

		myDownloadStatusListener = new MyDownloadStatusListener();
		downloadManager = new ThinDownloadManager(DOWNLOAD_THREAD_POOL_SIZE);

		RetryPolicy retryPolicy = new DefaultRetryPolicy();
		Uri downloadUri = Uri.parse(url);

		downloadRequest1 = new DownloadRequest(downloadUri)
				.setDestinationURI(destinationUri)

				.setPriority(DownloadRequest.Priority.HIGH)
				.setRetryPolicy(retryPolicy)
				.setDownloadListener(myDownloadStatusListener);

		// downloadManager.add(downloadRequest1);

		if (downloadManager.query(dID) == downloadManager.STATUS_NOT_FOUND) {
			downloadManager.add(downloadRequest1, dID);
		}

	}

	class MyDownloadStatusListener implements DownloadStatusListener {

		@Override
		public void onDownloadComplete(int id) {
			for (int i = 0; i < listSize; i++) {

				View conview = mDownloadList.getChildAt(i);

				TextView dwnID = (TextView) conview
						.findViewById(R.id.textView_id);

				String s = dwnID.getText().toString();

				if (id == Integer.parseInt(s)) {

					Log.d("download complete  ", " id" + id);
			
					DownloadListModelClass model = mDownloadListAdapter
							.getItem(i);
					model.setmProgress("100");
					
//					TextView dwntitle = (TextView) conview
//							.findViewById(R.id.textView_download_title);
//					String name = dwntitle.getText().toString();
//					insertVideo(name);
				}
			}
		}

		@Override
		public void onDownloadFailed(int id, int errorCode, String errorMessage) {
			for (int i = 0; i < listSize; i++) {

				View conview = mDownloadList.getChildAt(i);

				TextView dwnID = (TextView) conview
						.findViewById(R.id.textView_id);

				String s = dwnID.getText().toString();

				if (id == Integer.parseInt(s)) {

					Log.e("download failed  ", " id" + id);
					Log.e("ErrorCode ", "" + errorCode);
					Log.e("ErrorMessage ", "" + errorMessage);

					DownloadListModelClass model = mDownloadListAdapter
							.getItem(i);
					model.setmProgress("0");
				}
			}

		}

		@Override
		public void onProgress(int id, long totalBytes, long downloadedBytes,
				int progress) {
			// TODO Auto-generated method stub

			System.out.println("total" + totalBytes);

			for (int i = 0; i < mDownloadList.getChildCount(); i++) {
				View conview = mDownloadList.getChildAt(i);

				TextView dwnID = (TextView) conview
						.findViewById(R.id.textView_id);

				String s = dwnID.getText().toString();

				TextView dwntitle = (TextView) conview
						.findViewById(R.id.textView_download_title);

				if (id == Integer.parseInt(s)) {

					ProgressBar bar = (ProgressBar) conview
							.findViewById(R.id.download_progressBar);
					bar.setProgress(progress);

					TextView dwnprog = (TextView) conview
							.findViewById(R.id.textView_progress);
					dwnprog.setText("" + progress + " %");
					String totalsize = String.valueOf(VideoInfoUtils.getSizeInFormat(totalBytes));
					String downloadsize = String.valueOf(VideoInfoUtils.getSizeInFormat(downloadedBytes));
					
					TextView tvsize = (TextView) conview
							.findViewById(R.id.textView_total_size);
					tvsize.setText(totalsize +" / " + downloadsize);
//					System.out.println(progress);
					if (progress == 100) {
						String name = dwntitle.getText().toString();
						insertVideo(name);
					}
				}
			}

		}
	}

	public void insertVideo(String nam) {

		String name = nam;
		String url = folder + "/" + name;
		String duration = VideoInfoUtils.getDuration(url);
		String size = String.valueOf(VideoInfoUtils.getSizeInFormat(new File(
				url).length()));
		String date = VideoInfoUtils.getCurrentDate();
		String time = VideoInfoUtils.getCurrentTime();

		FileListModelClass videomodel = new FileListModelClass(name, duration,
				size, date, time, url);

		long inserted = mVideoDataSource.addNewVideo(videomodel);
		if (inserted >= 0) {

			Log.d("VIDEO: ", "inserted");

		} else {

			Log.e("VIDEO: ", "insertion fail");

		}

	}

	public Uri getOutputMediaFileUri(String title) {
		Uri mUri = Uri.fromFile(getOutputMediaFile(title));
		return mUri;
	}

	static void createDirectory() {
		if (!SMConstant.mediaStorageDir.exists()) {
			if (!SMConstant.mediaStorageDir.mkdirs()) {
				Log.d(SMConstant.FOLDER_NAME, "Oops! Failed create "
						+ SMConstant.FOLDER_NAME + " directory");

			}
		}
		
	
	}

	private static File getOutputMediaFile(String title) {

		createDirectory();

		if (!(title.equals(""))) {
			mediaFile = new File(SMConstant.mediaStorageDir.getPath()
					+ File.separator + "" + title + "");
		} else {
			return null;
		}
		return mediaFile;
	}

}
