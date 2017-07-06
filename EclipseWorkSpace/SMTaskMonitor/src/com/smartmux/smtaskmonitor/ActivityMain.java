/* 
 * 2010-2015 (C) Antonio Redondo
 * http://antonioredondo.com
 * https://github.com/AntonioRedondo/AnotherMonitor
 *
 * Code under the terms of the GNU General Public License v3.
 *
 */

package com.smartmux.smtaskmonitor;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.smartmux.smtaskmonitor.service.ServiceReader;
import com.smartmux.smtaskmonitor.utils.SMConstant;
import com.smartmux.widget.SlidingUpPanelLayout;
import com.smartmux.widget.SlidingUpPanelLayout.PanelSlideListener;
import com.smartmux.widget.SlidingUpPanelLayout.PanelState;

public class ActivityMain extends Activity {

//	private boolean cpuTotal, cpuAM, memUsed, memAvailable, memFree, cached,
//			threshold, settingsShown, canvasLocked;

	private LinearLayout mLCPUTotal, mLMemUsed, mLMemAvailable, mLMemFree,
			mLCached, mLThreshold;
	
	private TextView mTVCPUTotalP, mTVMemTotal, mTVMemUsed, mTVMemAvailable,
			mTVMemFree, mTVCached, mTVThreshold, mTVMemUsedP, mTVMemAvailableP,
			mTVMemFreeP, mTVCachedP, mTVThresholdP;
	
	private DecimalFormat mFormat = new DecimalFormat("##,###,##0"),
			mFormatPercent = new DecimalFormat("##0.0"),
			mFormatTime = new DecimalFormat("0.#");
	
	private Resources res;
	
	private List<Map<String, Object>> mListProcesses = 
			new ArrayList<Map<String, Object>>();
	
	private ProcessAdapter adapter;
	private ListView processListView;
	
	private ArrayList<Map<String, Object>> mListSelectedProv = 
			new ArrayList<Map<String, Object>>();
	
	private ServiceReader mSR;
	
	private List<Map<String, Object>> mListSelected = 
			new ArrayList<Map<String, Object>>();
	
	private Handler mHandler = new Handler();
	
	// private Thread mThread;
	private Runnable drawRunnable = new Runnable() {
		@SuppressWarnings("unchecked")
		@SuppressLint("NewApi")
		@Override
		public void run() {
			mHandler.postDelayed(this, 2000);
			if (mSR != null) {

				setTextLabelCPU(null, mTVCPUTotalP, mSR.getCPUTotalP());

				setTextLabelMemory(mTVMemUsed, mTVMemUsedP, mSR.getMemUsed());
				setTextLabelMemory(mTVMemAvailable, mTVMemAvailableP,
						mSR.getMemAvailable());
				setTextLabelMemory(mTVMemFree, mTVMemFreeP, mSR.getMemFree());
				setTextLabelMemory(mTVCached, mTVCachedP, mSR.getCached());
				setTextLabelMemory(mTVThreshold, mTVThresholdP,
						mSR.getThreshold());

				for (int n = 0; n < processListView.getCount(); n++) {

					setTextLabelCPUProcess(n);
				}
			}
		}
	};

	private ServiceConnection mServiceConnection = new ServiceConnection() {
		@SuppressLint("NewApi")
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {

			Log.d("onServiceConnected", "onServiceConnected");
			mSR = ((ServiceReader.ServiceReaderDataBinder) service)
					.getService();

			mTVMemTotal.setText(mFormat.format(mSR.getMemTotal())
					+ SMConstant.kB);

			mHandler.removeCallbacks(drawRunnable);
			mHandler.post(drawRunnable);

		}

		@Override
		public void onServiceDisconnected(ComponentName className) {
			mSR = null;
		}
	};

	@SuppressLint({ "InlinedApi", "NewApi", "InflateParams" })
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		startService(new Intent(this, ServiceReader.class));
		setContentView(R.layout.task_activity);

		adapter = new ProcessAdapter(this, mListSelectedProv);

		processListView = (ListView) this.findViewById(R.id.listViewprocess);

		processListView.setAdapter(adapter);

		processListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					final int position, long arg3) {
				String processName = (String) mListSelectedProv.get(position)
						.get(SMConstant.pAppName);

				if (!processName.equalsIgnoreCase("Android System")
						&& !processName
								.equalsIgnoreCase(getString(R.string.app_name))) {

					AlertDialog.Builder exitDialog = new AlertDialog.Builder(
							new ContextThemeWrapper(ActivityMain.this,
									R.style.popup_theme));
					exitDialog.setTitle("");
					exitDialog.setMessage("Do You want to kill this process?");
					exitDialog.setCancelable(false);
					exitDialog.setPositiveButton(
							getString(R.string.dialog_yes_btn),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {

									android.os.Process.killProcess(Integer
											.parseInt((String) (mListSelectedProv
													.get(position)
													.get(SMConstant.pId))));
									mListSelectedProv.remove(position);
									adapter = new ProcessAdapter(
											ActivityMain.this,
											mListSelectedProv);
									processListView.setAdapter(adapter);
								}
							});
					exitDialog.setNegativeButton(
							getString(R.string.dialog_no_btn),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {

								}
							});

					exitDialog.show();
				}

			}
		});
		res = getResources();

		////******************************Total CPU info*************************\\\\\\\\\\\\\
		mLCPUTotal = (LinearLayout) findViewById(R.id.LCPUTotal);
		mLCPUTotal.setTag(SMConstant.cpuTotal);

		mLMemUsed = (LinearLayout) findViewById(R.id.LMemUsed);
		mLMemUsed.setTag(SMConstant.memUsed);
		
		mLMemAvailable = (LinearLayout) findViewById(R.id.LMemAvailable);
		mLMemAvailable.setTag(SMConstant.memAvailable);
		
		mLMemFree = (LinearLayout) findViewById(R.id.LMemFree);
		mLMemFree.setTag(SMConstant.memFree);
		
		mLCached = (LinearLayout) findViewById(R.id.LCached);
		mLCached.setTag(SMConstant.cached);
		
		mLThreshold = (LinearLayout) findViewById(R.id.LThreshold);
		mLThreshold.setTag(SMConstant.threshold);
///////////////////////////*****************************\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
		
		mTVCPUTotalP = (TextView) findViewById(R.id.TVCPUTotalP);
		mTVMemTotal = (TextView) findViewById(R.id.TVMemTotal);
		mTVMemUsed = (TextView) findViewById(R.id.TVMemUsed);
		mTVMemUsedP = (TextView) findViewById(R.id.TVMemUsedP);
		mTVMemAvailable = (TextView) findViewById(R.id.TVMemAvailable);
		mTVMemAvailableP = (TextView) findViewById(R.id.TVMemAvailableP);
		mTVMemFree = (TextView) findViewById(R.id.TVMemFree);
		mTVMemFreeP = (TextView) findViewById(R.id.TVMemFreeP);
		mTVCached = (TextView) findViewById(R.id.TVCached);
		mTVCachedP = (TextView) findViewById(R.id.TVCachedP);
		mTVThreshold = (TextView) findViewById(R.id.TVThreshold);
		mTVThresholdP = (TextView) findViewById(R.id.TVThresholdP);

		SlidingUpPanelLayout mLayout = (SlidingUpPanelLayout) this
				.findViewById(R.id.sliding_layout);
		mLayout.setPanelState(PanelState.COLLAPSED);
		mLayout.setPanelSlideListener(new PanelSlideListener() {

			@Override
			public void onPanelSlide(View panel, float slideOffset) {

				Log.d("slide", "onPanelSlide");
				ActivityMain.this.findViewById(R.id.dragView)
						.setBackgroundColor(Color.parseColor("#991c3d0f"));

			}

			@Override
			public void onPanelExpanded(View panel) {

				ActivityMain.this.findViewById(R.id.arrow_indicator)
						.setVisibility(View.GONE);
				ActivityMain.this.findViewById(R.id.dragView)
						.setBackgroundColor(Color.parseColor("#991c3d0f"));
				Log.d("slide", "onPanelExpanded");

			}

			@Override
			public void onPanelCollapsed(View panel) {

				ActivityMain.this.findViewById(R.id.arrow_indicator)
						.setVisibility(View.VISIBLE);
				ActivityMain.this.findViewById(R.id.dragView)
						.setBackgroundColor(Color.parseColor("#1c3d0f"));
				Log.d("slide", "onPanelCollapsed");
			}

			@Override
			public void onPanelAnchored(View panel) {
				Log.d("slide", "onPanelAnchored");

			}

			@Override
			public void onPanelHidden(View panel) {
				Log.d("slide", "onPanelHidden");

			}
		});

	}

	private Map<String, Object> mapDataForPlacesList(boolean selected,
			String pAppName, String pid, String pPackage, String pName) {
		Map<String, Object> entry = new HashMap<String, Object>();
		entry.put(SMConstant.pSelected, selected);
		entry.put(SMConstant.pAppName, pAppName);
		entry.put(SMConstant.pId, pid);
		entry.put(SMConstant.pPackage, pPackage);
		entry.put(SMConstant.pName, pName);
		return entry;
	}

	private void setTextLabelCPU(TextView absolute, TextView percent,
			List<Float> values,
			@SuppressWarnings("unchecked") List<Integer>... valuesInteger) {
		if (valuesInteger.length == 1) {
			percent.setText(mFormatPercent.format(valuesInteger[0].get(0) * 100
					/ (float) mSR.getMemTotal())
					+ SMConstant.percent);
			// mTVMemoryAM.setVisibility(View.VISIBLE);
			// mTVMemoryAM.setText(mFormat.format(valuesInteger[0].get(0)) +
			// C.kB);
		} else if (!values.isEmpty()) {
			percent.setText(mFormatPercent.format(values.get(0))
					+ SMConstant.percent);
			// mTVMemoryAM.setVisibility(View.INVISIBLE);
		}
	}

	private void setTextLabelMemory(TextView absolute, TextView percent,
			List<String> values) {
		if (!values.isEmpty()) {
			absolute.setText(mFormat.format(Integer.parseInt(values.get(0)))
					+ SMConstant.kB);
			percent.setText(mFormatPercent.format(Integer.parseInt(values
					.get(0)) * 100 / (float) mSR.getMemTotal())
					+ SMConstant.percent);
		}
	}

	@SuppressWarnings("unchecked")
	private void setTextLabelCPUProcess(int position) {
		Map<String, Object> entry = mListSelectedProv.get(position);
		if (entry != null
				&& (List<String>) entry.get(SMConstant.pFinalValue) != null
				&& ((List<String>) entry.get(SMConstant.pFinalValue)).size() != 0
				&& (List<String>) entry.get(SMConstant.pTPD) != null
				&& !((List<String>) entry.get(SMConstant.pTPD)).isEmpty()
				&& entry.get(SMConstant.pDead) == null) {

			View view = getViewByPosition(position, processListView);

			TextView cpuUsage = (TextView) view
					.findViewById(R.id.textView_cpuUsage);
			TextView memoryUsage = (TextView) view
					.findViewById(R.id.textView_memoryUsage);
			TextView memorySize = (TextView) view
					.findViewById(R.id.textView_mSize);

			cpuUsage.setText(mFormatPercent.format(((List<String>) entry
					.get(SMConstant.pFinalValue)).get(0)) + SMConstant.percent);
			memoryUsage.setText(mFormatPercent.format(((List<Integer>) entry
					.get(SMConstant.pTPD)).get(0)
					* 100
					/ (float) mSR.getMemTotal()) + SMConstant.percent);

			memorySize.setText(mFormat.format(((List<String>) entry
					.get(SMConstant.pTPD)).get(0)) + SMConstant.kB);

			//
			// memoryUsage.setText(mFormatPercent.format(((List<Integer>) entry
			// .get(C.pTPD)).get(0)
			// * 100
			// / (float) mSR.getMemTotal()) +
			// C.percent+"\n"+mFormat.format(((List<String>) entry.get(C.pTPD))
			// .get(0)) + C.kB);
		}

		// adapter.notifyDataSetChanged();

	}

	public View getViewByPosition(int pos, ListView listView) {
		final int firstListItemPosition = listView.getFirstVisiblePosition();
		final int lastListItemPosition = firstListItemPosition
				+ listView.getChildCount() - 1;

		if (pos < firstListItemPosition || pos > lastListItemPosition) {
			return listView.getAdapter().getView(pos, null, listView);
		} else {
			final int childIndex = pos - firstListItemPosition;
			return listView.getChildAt(childIndex);
		}
	}

	private int getColourForProcess(int n) {
		if (n == 0)
			return res.getColor(R.color.process3);
		else if (n == 1)
			return res.getColor(R.color.process4);
		else if (n == 2)
			return res.getColor(R.color.process5);
		else if (n == 3)
			return res.getColor(R.color.process6);
		else if (n == 4)
			return res.getColor(R.color.process7);
		else if (n == 5)
			return res.getColor(R.color.process8);
		else if (n == 6)
			return res.getColor(R.color.process1);
		else if (n == 7)
			return res.getColor(R.color.process2);
		n -= 8;
		return getColourForProcess(n);
	}

	@Override
	public void onStart() {
		super.onStart();
		bindService(new Intent(this, ServiceReader.class), mServiceConnection,
				0);

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				setProcessResult();
			}
		}, 1000);

	}

	private void setProcessResult() {

		PackageManager pm = getPackageManager();
		List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = ((ActivityManager) getSystemService(ACTIVITY_SERVICE))
				.getRunningAppProcesses();
		if (runningAppProcesses != null) {
			for (ActivityManager.RunningAppProcessInfo p : runningAppProcesses) {

				String name = null;
				try {
					name = (String) pm.getApplicationLabel(pm
							.getApplicationInfo(p.pkgList[0], 0));
				} catch (NameNotFoundException e) {
				} catch (NotFoundException e) {
				}
				mListProcesses.add(mapDataForPlacesList(false, name,
						String.valueOf(p.pid), p.pkgList[0], p.processName));
			}

			Collections.sort(mListProcesses,
					new Comparator<Map<String, Object>>() {
						public int compare(Map<String, Object> o1,
								Map<String, Object> o2) {
							if (o1.get(SMConstant.pAppName).equals(
									o2.get(SMConstant.pAppName)))
								return 0;
							return ((String) o1.get(SMConstant.pAppName))
									.compareTo((String) o2
											.get(SMConstant.pAppName)) < 0 ? -1
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
						if (process.get(SMConstant.pId).equals(
								processSelected.get(SMConstant.pId)))
							iteratorListProcesses.remove();
					}
				}
			}

			if (mListSelectedProv != null) {
				// mListSelectedProv = mListProcesses;

				Log.d("mListSelectedProv", "" + mListSelectedProv.size());
				if (mListSelectedProv == null)
					return;

				if (mSR == null) {

					Log.d("mSR", "null");
				}
				for (Map<String, Object> process : mListSelectedProv) {
					process.put(SMConstant.pColour, getColourForProcess(mSR
							.getProcesses() != null ? mSR.getProcesses().size()
							: 0));
					mSR.addProcess(process);
				}

				mListSelected = mSR.getProcesses();
				Log.d("mListSelected", "" + mListSelected.size());
				// if (data.getBooleanExtra(C.screenRotated, false))
				// mListSelectedProv = mListSelected;

			} else {
				mListSelected = mSR.getProcesses();
				mListSelectedProv = (ArrayList<Map<String, Object>>) mListSelected;
			}

			if (mListSelectedProv == null)
				return;
			// processListView.setVerticalFadingEdgeEnabled(false);
			// processListView.setHorizontalFadingEdgeEnabled(false);
			adapter = new ProcessAdapter(this, mListSelectedProv);
			processListView.setOverScrollMode(View.OVER_SCROLL_NEVER);
			processListView.setAdapter(adapter);
			adapter.notifyDataSetChanged();

		}

	}

	@Override
	public void onResume() {
		super.onResume();
		mHandler.removeCallbacks(drawRunnable);
		mHandler.post(drawRunnable);
	}

	@Override
	public void onPause() {
		super.onPause();

		mHandler.removeCallbacks(drawRunnable);
	}

	@Override
	public void onStop() {
		super.onStop();

		mHandler.removeCallbacks(drawRunnable);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		mHandler.removeCallbacks(drawRunnable);

		unbindService(mServiceConnection);
	}

	@Override
	public void onBackPressed() {

		super.onBackPressed();
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU && event.getRepeatCount() == 0) {

			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

}
