package com.aircast.photobag.model;
/**
 * Model for Twitter Friend.
 * @author NhatVT
 *
 */
public class PBTwitterFriendEntryModel {
    private String mProfileIconUrl;
    private String mTwitterScreenName;
    private String mTwitterName;

    public PBTwitterFriendEntryModel() {

    }

    public PBTwitterFriendEntryModel(String iconUrl, String screenName, String name) {
        setProfileIconUrl(iconUrl);
        setTwitterScreenName(screenName);
        setTwitterName(name);
    }

    public String getProfileIconUrl() {
        return mProfileIconUrl;
    }

    public void setProfileIconUrl(String mProfileIconUrl) {
        this.mProfileIconUrl = mProfileIconUrl;
    }

    public String getTwitterScreenName() {
        return mTwitterScreenName;
    }

    public void setTwitterScreenName(String mTwitterScreenName) {
        this.mTwitterScreenName = mTwitterScreenName;
    }

    public String getTwitterName() {
        return mTwitterName;
    }

    public void setTwitterName(String mTwitterName) {
        this.mTwitterName = mTwitterName;
    }

}
