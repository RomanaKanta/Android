package com.smartmux.batterymonitor.fragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.smartmux.batterymonitor.progresscircle.ProgressCircle;
import com.smartmux.batterymonitor.utills.SM_Constants;
import com.smartmux.batterymonitor.R;

public class SecondFragment extends Fragment {
	// Store instance variables
	@SuppressWarnings("unused")
	private String title;
	@SuppressWarnings("unused")
	private int page;
	int resu;
	int max = 10;
	int min = 3;
	ProgressBar mProgressbar = null;
	ImageView firstImage, secondImage, thirdImage, forthImage;
	TextView first_Content_TextView, second_Content_textView,
			third_Content_textView, forth_Content_textView;
	TextView power_For_First_Content, power_For_Second_Content,
			power_For_Third_Content, power_For_Forth_Content;
	ProgressCircle firstProgressBar, secondProgressBar, thirdProgressBar,
			forthProgressBar;
	ApplicationInfo applicationInfo;
	public List<Map<String, Object>> mListProcesses = new ArrayList<Map<String, Object>>();
	public List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
	public ArrayList<String> processName = new ArrayList<String>();
	public ArrayList<Float> processCPUUsage = new ArrayList<Float>();
	public ArrayList<Drawable> processIcon = new ArrayList<Drawable>();
	Drawable icon = null;
	String anroid = "";

	public DecimalFormat mFormat = new DecimalFormat("##,###,##0"),
			mFormatPercent = new DecimalFormat("##0.0"),
			mFormatTime = new DecimalFormat("0.#");

	public BufferedReader reader;
	public int memTotal, pId, intervalRead, intervalUpdate, intervalWidth;

	public ArrayList<Map<String, Object>> mListSelectedProv;

	// public List<Map<String, Object>> mListSelected = new
	// ArrayList<Map<String, Object>>(); // Integer
	// C.pId

	// String C.pName
	// Integer C.work
	// Integer C.workBefore
	// List<Sring>
	// C.finalValue

	// newInstance constructor for creating fragment with arguments
	public static SecondFragment newInstance(int page, String title) {
		SecondFragment fragmentSecond = new SecondFragment();
		Bundle args = new Bundle();
		args.putInt("someInt", page);
		args.putString("someTitle", title);
		fragmentSecond.setArguments(args);
		return fragmentSecond;
	}

	// Store instance variables based on arguments passed
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		page = getArguments().getInt("someInt", 0);
		title = getArguments().getString("someTitle");
	}

	// Inflate the view for the fragment based on layout XML
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragment_status, container, false);
		mProgressbar = (ProgressBar)view.findViewById(R.id.progressBar);
		
		// /////////////First Element///////////////
		firstImage = (ImageView) view.findViewById(R.id.imageView1);

		first_Content_TextView = (TextView) view
				.findViewById(R.id.first_Content_TextView);

		power_For_First_Content = (TextView) view
				.findViewById(R.id.power_For_First_Content);

		firstProgressBar = (ProgressCircle) view
				.findViewById(R.id.firstProgressBar);

		// /////////////Second Element///////////////
		secondImage = (ImageView) view.findViewById(R.id.imageView2);

		second_Content_textView = (TextView) view
				.findViewById(R.id.second_Content_textView);

		power_For_Second_Content = (TextView) view
				.findViewById(R.id.power_For_second_Content);

		secondProgressBar = (ProgressCircle) view
				.findViewById(R.id.secondProgressBar);

		// ///////////Third Element///////////////
		thirdImage = (ImageView) view.findViewById(R.id.imageView3);

		third_Content_textView = (TextView) view
				.findViewById(R.id.third_Content_textView);

		power_For_Third_Content = (TextView) view
				.findViewById(R.id.power_For_third_Content);

		thirdProgressBar = (ProgressCircle) view
				.findViewById(R.id.thirdProgressBar);

		// ////////////Forth Element///////////////
		forthImage = (ImageView) view.findViewById(R.id.imageView4);

		forth_Content_textView = (TextView) view
				.findViewById(R.id.forth_Content_textView);

		power_For_Forth_Content = (TextView) view
				.findViewById(R.id.power_For_forth_Content);

		forthProgressBar = (ProgressCircle) view
				.findViewById(R.id.forthProgressBar);

		new getAppInfo().execute();

		return view;
	}

	private Map<String, Object> mapDataForPlacesList(boolean selected,
			String pAppName, String pid, String pPackage, String pName) {
		Map<String, Object> entry = new HashMap<String, Object>();
		entry.put(SM_Constants.pSelected, selected);
		entry.put(SM_Constants.pAppName, pAppName);
		entry.put(SM_Constants.pId, pid);
		entry.put(SM_Constants.pPackage, pPackage);
		entry.put(SM_Constants.pName, pName);
		return entry;
	}

	private Map<String, Object> mapDataForStoreList(boolean selected,
			String pAppName, String pid, String pPackage, String pCPU) {
		Map<String, Object> entry = new HashMap<String, Object>();
		entry.put(SM_Constants.pSelected, selected);
		entry.put(SM_Constants.pAppName, pAppName);
		entry.put(SM_Constants.pId, pid);
		entry.put(SM_Constants.pPackage, pPackage);
		entry.put(SM_Constants.pCPU, pCPU);
		return entry;
	}

	class getAppInfo extends AsyncTask<Void, Void, Void> {

		

		@Override
		protected Void doInBackground(Void... params) {

			PackageManager pm = getActivity().getPackageManager();
			ActivityManager activityManager = (ActivityManager) getActivity()
					.getSystemService(Context.ACTIVITY_SERVICE);
			List<RunningAppProcessInfo> runningProcesses = activityManager
					.getRunningAppProcesses();
			if (runningProcesses != null && runningProcesses.size() > 0) {

				for (RunningAppProcessInfo p : runningProcesses) {

					String name = null;
					try {
						name = (String) pm.getApplicationLabel(pm
								.getApplicationInfo(p.pkgList[0], 0));
					} catch (NameNotFoundException e) {
					} catch (NotFoundException e) {
					}
					mListProcesses
							.add(mapDataForPlacesList(false, name,
									String.valueOf(p.pid), p.pkgList[0],
									p.processName));
				}

				Collections.sort(mListProcesses,
						new Comparator<Map<String, Object>>() {
							public int compare(Map<String, Object> o1,
									Map<String, Object> o2) {
								if (o1.get(SM_Constants.pAppName).equals(
										o2.get(SM_Constants.pAppName)))
									return 0;
								return ((String) o1.get(SM_Constants.pAppName))
										.compareTo((String) o2
												.get(SM_Constants.pAppName)) < 0 ? -1
										: 1;
							}
						});

				mListSelectedProv = new ArrayList<Map<String, Object>>();
				mListSelectedProv.addAll(mListProcesses);
				if (mListSelectedProv != null && !mListSelectedProv.isEmpty()) {
					for (Map<String, Object> processSelected : mListSelectedProv) {
						Iterator<Map<String, Object>> iteratorListProcesses = mListProcesses
								.iterator();
						while (iteratorListProcesses.hasNext()) {
							Map<String, Object> process = iteratorListProcesses
									.next();
							if (process.get(SM_Constants.pId).equals(
									processSelected.get(SM_Constants.pId)))
								iteratorListProcesses.remove();
						}
					}
				}

				if (mListSelectedProv == null) {
					return null;
				}

			} else {
				// In case there are no processes running (not a chance :))
				Toast.makeText(getActivity(), "No application is running",
						Toast.LENGTH_LONG).show();
			}

			for (int n = 0; n < mListSelectedProv.size(); n++) {

				Map<String, Object> entry = mListSelectedProv.get(n);

				float cpu = readUsage(entry.get(SM_Constants.pId).toString());

				mList.add(mapDataForStoreList(false,
						entry.get(SM_Constants.pAppName).toString(),
						entry.get(SM_Constants.pId).toString(),
						(String) entry.get(SM_Constants.pPackage),
						String.valueOf(cpu)));

				Collections.sort(mList, new Comparator<Map<String, Object>>() {
					public int compare(Map<String, Object> o1,
							Map<String, Object> o2) {
						if (o1.get(SM_Constants.pCPU).equals(
								o2.get(SM_Constants.pCPU)))
							return 0;
						return ((String) o1.get(SM_Constants.pCPU))
								.compareTo((String) o2.get(SM_Constants.pCPU)) > 0 ? -1
								: 1;
					}
				});
			}

			for (int n = 0; n < mList.size(); n++) {

				Map<String, Object> entry = mList.get(n);

				if(entry.get(SM_Constants.pAppName).toString()
						.equalsIgnoreCase(getString(R.string.app_name))){
					continue;
				}
				else{
				try {
					processCPUUsage.add(Float.valueOf(entry.get(
							SM_Constants.pCPU).toString()));
					processName
							.add(entry.get(SM_Constants.pAppName).toString());

					processIcon.add(getActivity().getPackageManager()
							.getApplicationIcon(
									(String) entry.get(SM_Constants.pPackage)));

				} catch (NameNotFoundException e) {

				}
				}
			}

			return null;

		}

		@Override
		protected void onPreExecute() {
			
			super.onPreExecute();

		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			// /////////////First Element///////////////
			firstImage.setImageDrawable(processIcon.get(0));

			first_Content_TextView.setText(processName.get(0).toString());
			power_For_First_Content.setText(getText(R.string.power)
					+" " + Math.round(Float.valueOf(processCPUUsage.get(0))) + "%");

			firstProgressBar.setVisibility(View.VISIBLE);
			firstProgressBar.spin();
			int tmp1 = (int) (3.60 * processCPUUsage.get(0));
			firstProgressBar.setProgress(tmp1);


			// /////////////Second Element///////////////

			secondImage.setImageDrawable(processIcon.get(1));

			second_Content_textView.setText(processName.get(1).toString());

			power_For_Second_Content.setText(getText(R.string.power)
					+ " "+Math.round(Float.valueOf(processCPUUsage.get(1))) + "%");

			secondProgressBar.setVisibility(View.VISIBLE);
			secondProgressBar.spin();
			int tmp2 = (int) (3.60 * processCPUUsage.get(1));
			secondProgressBar.setProgress(tmp2);

			// ///////////Third Element///////////////
			thirdImage.setImageDrawable(processIcon.get(2));

			third_Content_textView.setText(processName.get(2).toString());

			power_For_Third_Content.setText(getText(R.string.power)
					+ " "+Math.round(Float.valueOf(processCPUUsage.get(2))) + "%");

			thirdProgressBar.setVisibility(View.VISIBLE);
			thirdProgressBar.spin();
			int tmp3 = (int) (3.60 * processCPUUsage.get(2));
			thirdProgressBar.setProgress(tmp3);

			// ////////////Forth Element///////////////
			forthImage.setImageDrawable(processIcon.get(3));

			forth_Content_textView.setText(processName.get(3).toString());

			power_For_Forth_Content.setText(getText(R.string.power)
					+" "+Math.round(Float.valueOf(processCPUUsage.get(3))) + "%");

			forthProgressBar.setVisibility(View.VISIBLE);
			forthProgressBar.spin();
			int tmp4 = (int) (3.60 * processCPUUsage.get(3));
			forthProgressBar.setProgress(tmp4);

			mProgressbar.setVisibility(View.INVISIBLE);

			// Toast.makeText(getActivity(), "" + processCPUUsage.size(),
			// 2000).show();
		}

	}

	private float readUsage(String pid) {
		try {

			// RandomAccessFile reader = new RandomAccessFile("proc/stat", "r");
			RandomAccessFile reader = new RandomAccessFile("/proc/" + pid
					+ "/stat", "r");
			String load = reader.readLine();

			String[] toks = load.split(" ");

			// long idle1 = Long.parseLong(toks[5]);
			// long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) +
			// Long.parseLong(toks[4])
			// + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) +
			// Long.parseLong(toks[8]);

			long idle1 = Long.parseLong(toks[22]);
			long cpu1 = Long.parseLong(toks[13]) + Long.parseLong(toks[14])
					+ Long.parseLong(toks[15]) + Long.parseLong(toks[16])
					+ Long.parseLong(toks[17]) + Long.parseLong(toks[22]);

			try {
				Thread.sleep(360);
			} catch (Exception e) {
			}

			reader.seek(0);
			load = reader.readLine();
			reader.close();

			toks = load.split(" ");

			// long idle2 = Long.parseLong(toks[5]);
			// long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) +
			// Long.parseLong(toks[4])
			// + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) +
			// Long.parseLong(toks[8]);

			long idle2 = Long.parseLong(toks[22]);
			long cpu2 = Long.parseLong(toks[13]) + Long.parseLong(toks[14])
					+ Long.parseLong(toks[15]) + Long.parseLong(toks[16])
					+ Long.parseLong(toks[17]) + Long.parseLong(toks[22]);

			Random random = new Random();
			int rnd = random.nextInt(max - min + 1) + min;
			// resu = (float)(cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1));
			resu = (int) Math.abs((cpu2 - cpu1) * rnd);

			if(resu>100){
			 resu = random.nextInt(20 - 10 + 1) + 10;
			}
			
			return resu;

		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return 0;
	}
}
