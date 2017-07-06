package com.smartmux.filevaultfree.utils;

import java.util.EventListener;

import com.smartmux.filevaultfree.modelclass.CommonItemRow;


public interface FileManagerListener extends EventListener {
	public void photoSaved(CommonItemRow row);
	public void noteSaved(CommonItemRow row);
	public void videoSaved(CommonItemRow row);
	public void audioSaved(CommonItemRow row);
}