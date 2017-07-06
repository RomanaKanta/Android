package com.smartmux.filevault.utils;

import com.smartmux.filevault.modelclass.CommonItemRow;

import java.util.EventListener;


public interface FileManagerListener extends EventListener {
	public void photoSaved(CommonItemRow row);
	public void noteSaved(CommonItemRow row);
	public void videoSaved(CommonItemRow row);
	public void audioSaved(CommonItemRow row);
}