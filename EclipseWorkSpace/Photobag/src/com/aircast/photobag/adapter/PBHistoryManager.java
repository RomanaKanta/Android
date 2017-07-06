package com.aircast.photobag.adapter;

import java.util.ArrayList;

import com.aircast.photobag.model.PBHistoryEntryModel;

public class PBHistoryManager {

    private static PBHistoryManager mInstance;
    private ArrayList<PBHistoryEntryModel> mDownloadList, mUploadList;

    public PBHistoryManager() {
        mDownloadList = new ArrayList<PBHistoryEntryModel>();
        mUploadList = new ArrayList<PBHistoryEntryModel>();
    }

    /**
     * Return singleton instance of PBHistoryManager
     * 
     * @return
     */
    public static PBHistoryManager getInstance() {
        if (mInstance == null) {
            mInstance = new PBHistoryManager();
        }
        return mInstance;
    }

    /**
     * Set data for download list
     * 
     * @param lstDownload
     */
    public void setDownloadList(ArrayList<PBHistoryEntryModel> lstDownload) {
        mDownloadList = lstDownload;
    }

    /**
     * Get download list
     * 
     * @return
     */
    public ArrayList<PBHistoryEntryModel> getDownloadList() {
        return mDownloadList;
    }

    /**
     * Set upload list
     * 
     * @param lstUpload
     */
    public void setUploadList(ArrayList<PBHistoryEntryModel> lstUpload) {
        mUploadList = lstUpload;
    }

    /**
     * Get upload list
     * 
     * @return
     */
    public ArrayList<PBHistoryEntryModel> getUploadList() {
        return mUploadList;
    }

}
