package com.aircast.photobag.model;
/**
 * class infor History entry
 * @author lent5
 *
 */
public class PBHistoryEntryModel {
    private long mEntryId;
    private String mCollectionId;
    private String mEntryPassword;
    private long mEntryCreateDate;
    private long mEntryChargeDate;
    private int mEntryNumofDownload;
    private int mEntryNumOfPhoto;
    private String nEntryThumpFile;
    private int mAddibility;
    private String mExtra;
    private int mIsUpdatable;
    private long mUpdatedAt;  
    private long mExpiresAt;
	private int mMapleUsed;
    private int mHoneyUsed;
    
    private String mAdLink = "";
    
    /**
     * <p>Decide whether photo can be save to local.</p>
     * <p>Server return 0 if can not save, 1 if can.<br>
     * If can not save, local saved as -1 instead of 0.<br>
     * Because accident._(:3 」∠)_</p>
     * */
    private int mSaveMark = 1;
    private int mSaveDays = 0;
    private int mIsPublic = 0;
    private int mAccepted = 0;
    private int mMessageCount = 0;
    private String fourDigit  = "-1";

    public PBHistoryEntryModel() {

    }

    public PBHistoryEntryModel(String entryId, String entryPass,
            long entryDate, String thumpFile, int numofDownload) {
        mCollectionId = entryId;
        mEntryPassword = entryPass;
        mEntryCreateDate = entryDate;
        nEntryThumpFile = thumpFile;
        mEntryNumofDownload = numofDownload;
    }

    public PBHistoryEntryModel(long id, String entryCollId, String entryPass,
            long entryDate, long chargeDate, int numOfPhoto, int numofDownload,
            String thumbUrl, int addibility, long updatedAt) {
    	this(entryCollId, entryPass, entryDate, thumbUrl, numofDownload);
        mEntryId = id;
        mEntryChargeDate = chargeDate;
        mEntryNumOfPhoto = numOfPhoto;
        mAddibility = addibility;
        mUpdatedAt = updatedAt;
    }
    
    public PBHistoryEntryModel(long id, String entryCollId, String entryPass,
            long entryDate, long chargeDate, int numOfPhoto, int numofDownload,
            String thumbUrl, int addibility, long updatedAt, int saveMark, int saveDays) {
    	this(id, entryCollId, entryPass, entryDate, chargeDate, numOfPhoto,
    			numofDownload, thumbUrl, addibility, updatedAt);
    	mSaveMark = saveMark == 0 ? -1 : saveMark;
    	mSaveDays = saveDays;
    }
    
    public PBHistoryEntryModel(long id, String entryCollId, String entryPass,
            long entryDate, long chargeDate, int numOfPhoto, int numofDownload,
            String thumbUrl, int addibility, long updatedAt, int saveMark, int saveDays, int isPublic, int accepted) {
    	this(id, entryCollId, entryPass, entryDate, chargeDate, numOfPhoto,
    			numofDownload, thumbUrl, addibility, updatedAt);
    	mSaveMark = saveMark == 0 ? -1 : saveMark;
    	mSaveDays = saveDays;
    	mIsPublic = isPublic;
    	mAccepted = accepted;
    	
    }
    
    public PBHistoryEntryModel(long id, String entryCollId, String entryPass,
            long entryDate, long chargeDate, int numOfPhoto, int numofDownload,
            String thumbUrl, int addibility, String extra, int isUpdatable, 
            long updatedAt, int honeyUsed, int mapleUsed){
    	this(id, entryCollId, entryPass, entryDate, chargeDate, numOfPhoto, numofDownload, thumbUrl, addibility, updatedAt);
    	mExtra = extra;
    	mIsUpdatable = isUpdatable;
    	this.mHoneyUsed = honeyUsed;
    	this.mMapleUsed = mapleUsed;
    }
    
    public PBHistoryEntryModel(long id, String entryCollId, String entryPass,
            long entryDate, long chargeDate, int numOfPhoto, int numofDownload,
            String thumbUrl, int addibility, String extra, int isUpdatable, 
            long updatedAt){
    	this(id, entryCollId, entryPass, entryDate, chargeDate, numOfPhoto, numofDownload, thumbUrl, addibility, updatedAt);
    	mExtra = extra;
    	mIsUpdatable = isUpdatable;
    }
    
    public PBHistoryEntryModel(long id, String entryCollId, String entryPass,
            long entryDate, long chargeDate, int numOfPhoto, int numofDownload,
            String thumbUrl, int addibility, String extra, int isUpdatable, 
            long updatedAt, int honeyUsed, int mapleUsed, int saveMark, int saveDays){
    	this(id, entryCollId, entryPass, entryDate, chargeDate, numOfPhoto, numofDownload, 
    			thumbUrl, addibility, extra, isUpdatable, updatedAt, honeyUsed, mapleUsed);
    	mSaveMark = saveMark == 0 ? -1 : saveMark;
    	mSaveDays = saveDays;
    }
    
    public void setEntryId(long entryId) {
        mEntryId = entryId;
    }

    public void setEntryCollectionId(String entryCollId) {
        mCollectionId = entryCollId;
    }

    public void setEntryPassword(String entryPassword) {
        mEntryPassword = entryPassword;
    }

    public void setEntryCreateDate(long entryCreateDate) {
        mEntryCreateDate = entryCreateDate;
    }

    public void setEntryChargeDate(long entryChargeDate) {
        mEntryChargeDate = entryChargeDate;
    }

    public void setEntryNumofDownload(int entryNumofDownload) {
        mEntryNumofDownload = entryNumofDownload;
    }

    public void setEntryNumOfPhoto(int numOfPhoto) {
        mEntryNumOfPhoto = numOfPhoto;
    }

    public void setEntryThump(String entryThump) {
        nEntryThumpFile = entryThump;
    }
    
    public void setEntryAddibility(int addibility) {
        mAddibility = addibility;
    }
    
    public void setEntryExtra(String extra) {
        mExtra = extra;
    }
    
    public void setEntryIsUpdatable(int isUpdatable) {
        mIsUpdatable = isUpdatable;
    }
    
    public void setEntryUpdatedAt(long updatedAt) {
        mUpdatedAt = updatedAt;
    }

    public long getEntryId() {
        return this.mEntryId;
    }

    public String getCollectionId() {
        return this.mCollectionId;
    }

    public String getEntryPassword() {
        return this.mEntryPassword;
    }

    public long getEntryCreateDate() {
        return this.mEntryCreateDate;
    }

    public String getEntryThump() {
        return nEntryThumpFile;
    }

    public int getEntryNumofDownload() {
        return mEntryNumofDownload;
    }

    public long getEntryChargeDate() {
        return mEntryChargeDate;
    }

    public int getEntryNumOfPhoto() {
        return mEntryNumOfPhoto;
    }
    
    public int getEntryAddibility() {
        return mAddibility;
    }
    
    public String getEntryExtra() {
        return mExtra;
    }
    
    public long getEntryIsUpdatable() {
        return mIsUpdatable;
    }
    
    public long getEntryUpdatedAt() {
        return mUpdatedAt;
    }
    
    
    public int getEntryMapleUsed() {
		return mMapleUsed;
	}

	public void setEntryMapleUsed(int mapleUsed) {
		this.mMapleUsed = mapleUsed;
	}

	public int getEntryHoneyUsed() {
		return mHoneyUsed;
	}

	public void setEntryHoneyUsed(int honeyUsed) {
		this.mHoneyUsed = honeyUsed;
	}
	
	public void setEntrySaveMark(int mark) {
		mSaveMark = mark == 0 ? -1 : mark;
	}
	
	public int getEntrySaveMark() {
		return mSaveMark;
	}
	
	public boolean canSave() {
		return mSaveMark!=-1;
	}
	
	public void setEntrySaveDays(int day) {
		mSaveDays = day;
	}
	
	public int getEntrySaveDays() {
		return mSaveDays;
	}
	
	public void setEntryAdLink(String link) {
		mAdLink = link;
	}

	public String getEntryAdLink() {
		return mAdLink;
	}
	public void setIsPublic (int isPublic) {
		mIsPublic = isPublic;
	}
	
	public int getIsPublic() {
		return mIsPublic;
	}
	
	public void setAccepted (int accepted) {
		mAccepted = accepted;
	}
	
	public int getAccepted() {
		return mAccepted;
	}

	public int getmMessageCount() {
		return mMessageCount;
	}

	public void setmMessageCount(int mMessageCount) {
		this.mMessageCount = mMessageCount;
	}

	public long getmExpiresAt() {
		return mExpiresAt;
	}

	public void setmExpiresAt(long mExpiresAt) {
		this.mExpiresAt = mExpiresAt;
	}

	public String getFourDigit() {
		return fourDigit;
	}

	public void setFourDigit(String fourDigit) {
		this.fourDigit = fourDigit;
	}
	
	
}
