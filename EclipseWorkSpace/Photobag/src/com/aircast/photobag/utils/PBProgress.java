package com.aircast.photobag.utils;

public class PBProgress {
    
    public PBProgress(int totalFile) {
        this.medialistSize = totalFile;
        this.currentLevelOfProgressBar = 0;
    }

    private int currentLevelOfProgressBar = 0;
    private int mCurrentProgress = 0;
    private int percent = 0;
    private int medialistSize = 0;
    private boolean isUpdatedIndex = false;
    public int getPercentOfProcessWithOneFile(int progress) {
        if (medialistSize == 0) {
            return 0;
        }
        if (progress > 0) {
            percent = 0;
            if (medialistSize > 25) {
                if (progress >= 96) {
                    if (!isUpdatedIndex) {
                        currentLevelOfProgressBar = currentLevelOfProgressBar + 1;
                    }
                    isUpdatedIndex = true;
                } else {
                    isUpdatedIndex = false;
                }
            } else {
                percent = progress / medialistSize;
                if (percent >= mCurrentProgress) {
                    currentLevelOfProgressBar = currentLevelOfProgressBar + (percent - mCurrentProgress);
                    mCurrentProgress = percent;
                } else {
                    currentLevelOfProgressBar = currentLevelOfProgressBar + percent;
                    mCurrentProgress = percent;
                }
            }
        }

        return currentLevelOfProgressBar;
    }
    
    public int getCurrentLevelOfProgress() {
        return this.currentLevelOfProgressBar;
    }
    
    /**
     * Must invoke this method before execute getPercentOfProgress().
     */
    public void resetBeforeStartNewProcess() {
        this.mCurrentProgress = 0;
    }
    
}
