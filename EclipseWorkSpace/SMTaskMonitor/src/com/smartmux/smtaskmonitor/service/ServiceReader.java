/* 
 * 2010-2015 (C) Antonio Redondo
 * http://antonioredondo.com
 * https://github.com/AntonioRedondo/AnotherMonitor
 *
 * Code under the terms of the GNU General Public License v3.
 *
 */

package com.smartmux.smtaskmonitor.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.smartmux.smtaskmonitor.R;
import com.smartmux.smtaskmonitor.utils.SMConstant;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Debug;
import android.os.Debug.MemoryInfo;
import android.os.IBinder;
import android.os.Process;
import android.util.Log;

public class ServiceReader extends Service {

	private boolean /* threadSuspended, */recording, firstRead = true;
	private int memTotal, pId, intervalRead, intervalUpdate, intervalWidth,
			maxSamples = 2000;
	private long workT, totalT, workAMT, total, totalBefore, work, workBefore,
			workAM, workAMBefore;
	private String s;
	private String[] sa;
	private List<Float> cpuTotal, cpuAM;
	private List<Integer> memoryAM;
	private List<Map<String, Object>> mListSelected; // Integer C.pId
														// String C.pName
														// Integer C.work
														// Integer C.workBefore
														// List<Sring>
														// C.finalValue
	private List<String> memUsed, memAvailable, memFree, cached, threshold;
	private ActivityManager am;
	private Debug.MemoryInfo[] amMI;
	private ActivityManager.MemoryInfo mi;
	private NotificationManager mNM;
	private BufferedReader reader;
	private SharedPreferences mPrefs;
	private Runnable readRunnable = new Runnable() { // http://docs.oracle.com/javase/8/docs/technotes/guides/concurrency/threadPrimitiveDeprecation.html
		@Override
		public void run() {
			Thread thisThread = Thread.currentThread();
			while (readThread == thisThread) {
				read();
				try {
					Thread.sleep(intervalUpdate);
				} catch (InterruptedException e) {
					break;
				}

			}
		}


	};
	private volatile Thread readThread = new Thread(readRunnable,
			SMConstant.readThread);

	public class ServiceReaderDataBinder extends Binder {
		public ServiceReader getService() {
			return ServiceReader.this;
		}
	}

	@Override
	public void onCreate() {
		cpuTotal = new ArrayList<Float>(maxSamples);
		cpuAM = new ArrayList<Float>(maxSamples);
		memoryAM = new ArrayList<Integer>(maxSamples);
		memUsed = new ArrayList<String>(maxSamples);
		memAvailable = new ArrayList<String>(maxSamples);
		memFree = new ArrayList<String>(maxSamples);
		cached = new ArrayList<String>(maxSamples);
		threshold = new ArrayList<String>(maxSamples);

		pId = Process.myPid();

		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		amMI = am.getProcessMemoryInfo(new int[] { pId });
		mi = new ActivityManager.MemoryInfo();

		mPrefs = getSharedPreferences(getString(R.string.app_name)
				+ SMConstant.prefs, MODE_PRIVATE);
		intervalRead = mPrefs.getInt(SMConstant.intervalRead, 1000);
		intervalUpdate = mPrefs.getInt(SMConstant.intervalUpdate, 1000);
		intervalWidth = mPrefs.getInt(SMConstant.intervalWidth, 1);

		readThread.start();
	}

	@Override
	public void onDestroy() {
		if (recording)
			// stopRecord();
			mNM.cancelAll();


		try {
			readThread.interrupt();
		} catch (Exception e) {
			e.printStackTrace();
		}
		synchronized (this) {
			readThread = null;
			notify();
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return new ServiceReaderDataBinder();
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("unchecked")
	private void read() {
		try {
			reader = new BufferedReader(new FileReader("/proc/meminfo"));
			s = reader.readLine();
			while (s != null) {
				// Memory is limited as far as we know
				while (memFree.size() >= maxSamples) {
					cpuTotal.remove(cpuTotal.size() - 1);
					cpuAM.remove(cpuAM.size() - 1);
					memoryAM.remove(memoryAM.size() - 1);

					memUsed.remove(memUsed.size() - 1);
					memAvailable.remove(memAvailable.size() - 1);
					memFree.remove(memFree.size() - 1);
					cached.remove(cached.size() - 1);
					threshold.remove(threshold.size() - 1);
				}
				if (mListSelected != null && !mListSelected.isEmpty()) {
					List<Integer> l = (List<Integer>) ((Map<String, Object>) mListSelected
							.get(0)).get(SMConstant.pFinalValue);
					if (l != null && l.size() >= maxSamples)
						for (Map<String, Object> m : mListSelected) {
							((List<Integer>) m.get(SMConstant.pFinalValue))
									.remove(l.size() - 1);
							((List<Integer>) m.get(SMConstant.pTPD))
									.remove(((List<Integer>) m
											.get(SMConstant.pTPD)).size() - 1);
						}
				}
				if (mListSelected != null && !mListSelected.isEmpty()) {
					for (Map<String, Object> m : mListSelected) {
						List<Integer> l = (List<Integer>) m
								.get(SMConstant.pFinalValue);
						if (l == null)
							break;
						while (l.size() >= maxSamples)
							l.remove(l.size() - 1);
						l = (List<Integer>) m.get(SMConstant.pTPD);
						while (l.size() >= maxSamples)
							l.remove(l.size() - 1);
					}
				}

				// Memory values. Percentages are calculated in the ActivityMain
				// class.
				if (firstRead && s.startsWith("MemTotal:")) {
					memTotal = Integer.parseInt(s.split("[ ]+", 3)[1]);
					firstRead = false;
				} else if (s.startsWith("MemFree:"))
					memFree.add(0, s.split("[ ]+", 3)[1]);
				else if (s.startsWith("Cached:"))
					cached.add(0, s.split("[ ]+", 3)[1]);

				s = reader.readLine();
			}
			reader.close();

			// http://stackoverflow.com/questions/3170691/how-to-get-current-memory-usage-in-android
			am.getMemoryInfo(mi);
			if (mi == null) { // Sometimes mi is null
				memUsed.add(0, String.valueOf(0));
				memAvailable.add(0, String.valueOf(0));
				threshold.add(0, String.valueOf(0));
			} else {
				memUsed.add(0, String.valueOf(memTotal - mi.availMem / 1024));
				memAvailable.add(0, String.valueOf(mi.availMem / 1024));
				threshold.add(0, String.valueOf(mi.threshold / 1024));
			}

			memoryAM.add(amMI[0].getTotalPrivateDirty());
			reader = new BufferedReader(new FileReader("/proc/stat"));
			sa = reader.readLine().split("[ ]+", 9);
			work = Long.parseLong(sa[1]) + Long.parseLong(sa[2])
					+ Long.parseLong(sa[3]);
			total = work + Long.parseLong(sa[4]) + Long.parseLong(sa[5])
					+ Long.parseLong(sa[6]) + Long.parseLong(sa[7]);
			reader.close();

			reader = new BufferedReader(
					new FileReader("/proc/" + pId + "/stat"));
			sa = reader.readLine().split("[ ]+", 18);
			workAM = Long.parseLong(sa[13]) + Long.parseLong(sa[14])
					+ Long.parseLong(sa[15]) + Long.parseLong(sa[16]);
			reader.close();

			if (mListSelected != null && !mListSelected.isEmpty()) {
				int[] arrayPIds = new int[mListSelected.size()];
				synchronized (mListSelected) {
					int n = 0;
					for (Map<String, Object> p : mListSelected) {
						try {
							if (p.get(SMConstant.pDead) == null) {
								reader = new BufferedReader(new FileReader(
										"/proc/" + p.get(SMConstant.pId)
												+ "/stat"));
								arrayPIds[n] = Integer.valueOf((String) p
										.get(SMConstant.pId));
								++n;
								sa = reader.readLine().split("[ ]+", 18);
								p.put(SMConstant.work,
										(float) Long.parseLong(sa[13])
												+ Long.parseLong(sa[14])
												+ Long.parseLong(sa[15])
												+ Long.parseLong(sa[16]));
								reader.close();
							}
						} catch (FileNotFoundException e) {
							p.put(SMConstant.pDead, Boolean.TRUE);
							Intent intent = new Intent(
									SMConstant.actionDeadProcess);
							intent.putExtra(SMConstant.process,
									(Serializable) p);
							sendBroadcast(intent);
						}
					}
				}

				MemoryInfo[] mip = am.getProcessMemoryInfo(arrayPIds);
				int n = 0;
				for (Map<String, Object> entry : mListSelected) {
					List<Integer> l = (List<Integer>) entry
							.get(SMConstant.pTPD);
					if (l == null) {
						l = new ArrayList<Integer>();
						entry.put(SMConstant.pTPD, l);
					}
					if (entry.get(SMConstant.pDead) == null) {
						l.add(0, mip[n].getTotalPrivateDirty());
						++n;
					} else
						l.add(0, 0);
				}
			}

			if (totalBefore != 0) {
				totalT = total - totalBefore;
				workT = work - workBefore;
				workAMT = workAM - workAMBefore;

				cpuTotal.add(0,
						restrictPercentage(workT * 100 / (float) totalT));
				cpuAM.add(0, restrictPercentage(workAMT * 100 / (float) totalT));

				if (mListSelected != null && !mListSelected.isEmpty()) {
					int workPT = 0;
					List<Float> l;

					synchronized (mListSelected) {
						for (Map<String, Object> p : mListSelected) {
							if (p.get(SMConstant.workBefore) == null)
								break;
							l = (List<Float>) p.get(SMConstant.pFinalValue);
							if (l == null) {
								l = new ArrayList<Float>();
								p.put(SMConstant.pFinalValue, l);
							}
							while (l.size() >= maxSamples)
								l.remove(l.size() - 1);

							workPT = (int) ((Float) p.get(SMConstant.work) - (Float) p
									.get(SMConstant.workBefore));
							l.add(0, restrictPercentage(workPT * 100
									/ (float) totalT));
						}
					}
				}
			}

			totalBefore = total;
			workBefore = work;
			workAMBefore = workAM;

			if (mListSelected != null && !mListSelected.isEmpty())
				for (Map<String, Object> p : mListSelected)
					p.put(SMConstant.workBefore, p.get(SMConstant.work));

			reader.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private float restrictPercentage(float percentage) {
		if (percentage > 100)
			return 100;
		else if (percentage < 0)
			return 0;
		else
			return percentage;
	}



	void setIntervals(int intervalRead, int intervalUpdate, int intervalWidth) {
		this.intervalRead = intervalRead;
		this.intervalUpdate = intervalUpdate;
		this.intervalWidth = intervalWidth;
	}

	public List<Map<String, Object>> getProcesses() {
		return mListSelected != null && !mListSelected.isEmpty() ? mListSelected
				: null;
	}

	public void addProcess(Map<String, Object> process) {
		if (mListSelected == null)
			mListSelected = Collections
					.synchronizedList(new ArrayList<Map<String, Object>>());
		mListSelected.add(process);
	}

	void removeProcess(Map<String, Object> process) {
		synchronized (mListSelected) {
			Iterator<Map<String, Object>> i = mListSelected.iterator();
			while (i.hasNext())
				if (i.next().get(SMConstant.pId)
						.equals(process.get(SMConstant.pId))) {
					i.remove();
					Log.i(getString(R.string.w_processes_dead_notification),
							(String) process.get(SMConstant.pName));
				}
		}
	}


	boolean isRecording() {
		return recording;
	}

	public int getIntervalRead() {
		return intervalRead;
	}

	public int getIntervalUpdate() {
		return intervalUpdate;
	}

	public int getIntervalWidth() {
		return intervalWidth;
	}

	public List<Float> getCPUTotalP() {
		return cpuTotal;
	}

	List<Float> getCPUAMP() {
		return cpuAM;
	}

	List<Integer> getMemoryAM() {
		return memoryAM;
	}

	public int getMemTotal() {
		return memTotal;
	}

	public List<String> getMemUsed() {
		return memUsed;
	}

	public List<String> getMemAvailable() {
		return memAvailable;
	}

	public List<String> getMemFree() {
		return memFree;
	}

	public List<String> getCached() {
		return cached;
	}

	public List<String> getThreshold() {
		return threshold;
	}
}
