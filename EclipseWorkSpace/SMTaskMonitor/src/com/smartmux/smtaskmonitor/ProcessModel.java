package com.smartmux.smtaskmonitor;

public class ProcessModel {

	private String processName;
	private String processId;
	private String cpuUsage;
	private String memoryUsage;
	private String ImagePath;

	public ProcessModel(String processName, String processId, String cpuUsage, String memoryUsage,String ImagePath) {
		this.processName = processName;
		this.processId = processId;
		this.cpuUsage = cpuUsage;
		this.memoryUsage = memoryUsage;
		this.ImagePath = ImagePath;

	}

	public String getImagePath() {
		return ImagePath;
	}

	public void setImagePath(String imagePath) {
		ImagePath = imagePath;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public String getCpuUsage() {
		return cpuUsage;
	}

	public void setCpuUsage(String cpuUsage) {
		this.cpuUsage = cpuUsage;
	}

	public String getMemoryUsage() {
		return memoryUsage;
	}

	public void setMemoryUsage(String memoryUsage) {
		this.memoryUsage = memoryUsage;
	}

	
}