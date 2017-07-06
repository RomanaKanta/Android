package com.aircast.photobag.model;

/** class information of photo */
public class PBHistoryPhotoModel {
    /** Photo's url */
    private String mPhoto;
    /** Photo thumb's url */
    private String mThumb;
    /** history collection's id */
    private String mHistoryId;
    /** media type*/
    private int mType;
    /**
     * video duration.
     */
    private long mVideoDuration;
    /** Contructor PBHistoryPhotoModel */
    public PBHistoryPhotoModel(String url, String thumb, String historyId, int mediaType) {
        mPhoto = url;
        mThumb = thumb;
        mHistoryId = historyId;
        mType = mediaType;
    }
    
    /** Contructor PBHistoryPhotoModel */
    public PBHistoryPhotoModel(String url, String thumb, String historyId, int mediaType, long duration) {
        mPhoto = url;
        mThumb = thumb;
        mHistoryId = historyId;
        mType = mediaType;
        setVideoDuration(duration);
    }

    /** get media type */
    public int getMediaType(){
        return mType;
    }

    /** get photo's url */
    public String getPhoto(){
        return mPhoto;
    }

    /** get photo's url */
    public String getThumb(){
        return mThumb;
    }

    /** get photo's url */
    public String getHistoryId(){
        return mHistoryId;
    }

    public long getVideoDuration() {
        return mVideoDuration;
    }

    public void setVideoDuration(long mVideoDuration) {
        this.mVideoDuration = mVideoDuration;
    }
}
