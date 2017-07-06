package com.smartmux.batterymonitor.utills;

import com.smartmux.batterymonitor.R;

public class SM_Constants {
	public static String TAG = "MainActivity";
	public static String sharedPreferanceName = "BATTERY_MONITOR";
	public static String batteryCharging = "BatteryCharging";
	public static String batteryDown = "BatteryDown";
	public static String status = "Status";
	public static String plug = "Plug";
	public static String current_level = "Level";
	public static String level = "Current_Level";
	public static String next_level = "Next_Level";
	public static String estimatetime = "Estimate_Time";
	public static String remainingtime = "Remaining_Time";
	public static String cpuUsage = "CPU_Usage";
	public static String processIcon = "Process_Icon";
	public static String processName = "Process_Name";
	public static String wifiusage = "Wifi_Usage";
	public static String brightnessusage = "Brightness_Usage";
	public static String gpsusage = "GPS_Usage";
	public static String bluetoothusage = "Bluetooth_Usage";
	public static String flag = "Flag";
	

	public static final String prefs = "Prefs";
	public static final String sbh = "status_bar_height";
	public static final String nbh = "navigation_bar_height";
	public static final String dimen = "dimen";
	public static final String android = "android";
	public static final String europeLondon = "Europe/London";
	public static final String marketDetails = "market://details?id=";

	// ServiceReader
	public static final String readThread = "readThread";

	public static final String actionStartRecord = "actionRecord";
	public static final String actionStopRecord = "actionStop";
	public static final String actionClose = "actionClose";
	public static final String actionSetIconRecord = "actionSetIconRecord";
	public static final String actionDeadProcess = "actionRemoveProcess";
	public static final String actionFinishActivity = "actionCloseActivity";

	public static final String pId = "pId";
	public static final String pName = "pName";
	public static final String pCPU = "pCPU";
	public static final String pPackage = "pPackage";
	public static final String pAppName = "pAppName";
	public static final String pTPD = "pPTD";
	public static final String pSelected = "pSelected";
	public static final String pDead = "pDead";
	public static final String pColour = "pColour";
	public static final String work = "work";
	public static final String workBefore = "workBefore";
	public static final String pFinalValue = "finalValue";
	public static final String process = "process";
	public static final String screenRotated = "screenRotated";
	public static final String listSelected = "listSelected";
	public static final String listProcesses = "listProcesses";

	// ActivityMain
	public static final String kB = "kB";
	public static final String percent = "%";
	public static final String drawThread = "drawThread";
	public static final String menuShown = "menuShown";
	public static final String settingsShown = "settingsShown";
	public static final String orientation = "orientation";
	public static final String processesMode = "processesMode";
	public static final String graphicMode = "graphicMode";
	public static final String canvasLocked = "canvasLocked";

	public static final String welcome = "firstTime";
	public static final String welcomeDate = "firstTimeDate";
	public static final String firstTimeProcesses = "firstTimeProcesses";
	public static final String feedbackFirstTime = "feedbackFirstTime";
	public static final String feedbackDone = "feedbackDone";

	public static final String intervalRead = "intervalRead";
	public static final String intervalUpdate = "intervalUpdate";
	public static final String intervalWidth = "intervalWidth";

	public static final String cpuTotal = "cpuTotalD";
	public static final String cpuAM = "cpuAMD";
	public static final String memUsed = "memUsedD";
	public static final String memAvailable = "memAvailableD";
	public static final String memFree = "memFreeD";
	public static final String cached = "cachedD";
	public static final String threshold = "thresholdD";

	// GraphicView
	public static final int processModeCPU = 0;
	public static final int processModeMemory = 1;
	public static final int graphicModeShow = 0;
	public static final int graphicModeHide = 1;

	// ActivityPreferences
	public static final String currentItem = "ci";

	public static final String mSRead = "mSRead";
	public static final String mSUpdate = "mSUpdate";
	public static final String mSWidth = "mSWidth";

	public static final String mCBMemFreeD = "memFreeD";
	public static final String mCBBuffersD = "buffersD";
	public static final String mCBCachedD = "cachedD";
	public static final String mCBActiveD = "activeD";
	public static final String mCBInactiveD = "inactiveD";
	public static final String mCBSwapTotalD = "swapTotalD";
	public static final String mCBDirtyD = "dirtyD";
	public static final String mCBCpuTotalD = "cpuTotalD";
	public static final String mCBCpuAMD = "cpuAMD";
	public static final String mCBCpuRestD = "cpuRestD";

	public static int[] myImageList = new int[] {
			R.drawable.ic_stat_00_pct_charged,
			R.drawable.ic_stat_01_pct_charged,
			R.drawable.ic_stat_02_pct_charged,
			R.drawable.ic_stat_03_pct_charged,
			R.drawable.ic_stat_04_pct_charged,
			R.drawable.ic_stat_05_pct_charged,
			R.drawable.ic_stat_06_pct_charged,
			R.drawable.ic_stat_07_pct_charged,
			R.drawable.ic_stat_08_pct_charged,
			R.drawable.ic_stat_09_pct_charged,
			R.drawable.ic_stat_10_pct_charged,
			R.drawable.ic_stat_11_pct_charged,
			R.drawable.ic_stat_12_pct_charged,
			R.drawable.ic_stat_13_pct_charged,
			R.drawable.ic_stat_14_pct_charged,
			R.drawable.ic_stat_15_pct_charged,
			R.drawable.ic_stat_16_pct_charged,
			R.drawable.ic_stat_17_pct_charged,
			R.drawable.ic_stat_18_pct_charged,
			R.drawable.ic_stat_19_pct_charged,
			R.drawable.ic_stat_20_pct_charged,
			R.drawable.ic_stat_21_pct_charged,
			R.drawable.ic_stat_22_pct_charged,
			R.drawable.ic_stat_23_pct_charged,
			R.drawable.ic_stat_24_pct_charged,
			R.drawable.ic_stat_25_pct_charged,
			R.drawable.ic_stat_26_pct_charged,
			R.drawable.ic_stat_27_pct_charged,
			R.drawable.ic_stat_28_pct_charged,
			R.drawable.ic_stat_29_pct_charged,
			R.drawable.ic_stat_30_pct_charged,
			R.drawable.ic_stat_31_pct_charged,
			R.drawable.ic_stat_32_pct_charged,
			R.drawable.ic_stat_33_pct_charged,
			R.drawable.ic_stat_34_pct_charged,
			R.drawable.ic_stat_35_pct_charged,
			R.drawable.ic_stat_36_pct_charged,
			R.drawable.ic_stat_37_pct_charged,
			R.drawable.ic_stat_38_pct_charged,
			R.drawable.ic_stat_39_pct_charged,
			R.drawable.ic_stat_40_pct_charged,
			R.drawable.ic_stat_41_pct_charged,
			R.drawable.ic_stat_42_pct_charged,
			R.drawable.ic_stat_43_pct_charged,
			R.drawable.ic_stat_44_pct_charged,
			R.drawable.ic_stat_45_pct_charged,
			R.drawable.ic_stat_46_pct_charged,
			R.drawable.ic_stat_47_pct_charged,
			R.drawable.ic_stat_48_pct_charged,
			R.drawable.ic_stat_49_pct_charged,
			R.drawable.ic_stat_50_pct_charged,
			R.drawable.ic_stat_51_pct_charged,
			R.drawable.ic_stat_52_pct_charged,
			R.drawable.ic_stat_53_pct_charged,
			R.drawable.ic_stat_54_pct_charged,
			R.drawable.ic_stat_55_pct_charged,
			R.drawable.ic_stat_56_pct_charged,
			R.drawable.ic_stat_57_pct_charged,
			R.drawable.ic_stat_58_pct_charged,
			R.drawable.ic_stat_59_pct_charged,
			R.drawable.ic_stat_60_pct_charged,
			R.drawable.ic_stat_61_pct_charged,
			R.drawable.ic_stat_62_pct_charged,
			R.drawable.ic_stat_63_pct_charged,
			R.drawable.ic_stat_64_pct_charged,
			R.drawable.ic_stat_65_pct_charged,
			R.drawable.ic_stat_66_pct_charged,
			R.drawable.ic_stat_67_pct_charged,
			R.drawable.ic_stat_68_pct_charged,
			R.drawable.ic_stat_69_pct_charged,
			R.drawable.ic_stat_70_pct_charged,
			R.drawable.ic_stat_71_pct_charged,
			R.drawable.ic_stat_72_pct_charged,
			R.drawable.ic_stat_73_pct_charged,
			R.drawable.ic_stat_74_pct_charged,
			R.drawable.ic_stat_75_pct_charged,
			R.drawable.ic_stat_76_pct_charged,
			R.drawable.ic_stat_77_pct_charged,
			R.drawable.ic_stat_78_pct_charged,
			R.drawable.ic_stat_79_pct_charged,
			R.drawable.ic_stat_80_pct_charged,
			R.drawable.ic_stat_81_pct_charged,
			R.drawable.ic_stat_82_pct_charged,
			R.drawable.ic_stat_83_pct_charged,
			R.drawable.ic_stat_84_pct_charged,
			R.drawable.ic_stat_85_pct_charged,
			R.drawable.ic_stat_86_pct_charged,
			R.drawable.ic_stat_87_pct_charged,
			R.drawable.ic_stat_88_pct_charged,
			R.drawable.ic_stat_89_pct_charged,
			R.drawable.ic_stat_90_pct_charged,
			R.drawable.ic_stat_91_pct_charged,
			R.drawable.ic_stat_92_pct_charged,
			R.drawable.ic_stat_93_pct_charged,
			R.drawable.ic_stat_94_pct_charged,
			R.drawable.ic_stat_95_pct_charged,
			R.drawable.ic_stat_96_pct_charged,
			R.drawable.ic_stat_97_pct_charged,
			R.drawable.ic_stat_98_pct_charged,
			R.drawable.ic_stat_99_pct_charged,
			R.drawable.ic_stat_z100_pct_charged };

}
