package com.aircast.photobag.twitter;

public class TwitterHelper {
	
	
}


/* // [9:37:27 AM 2012-02-27] Nguyen Tuan Anh: CONSUMER_KEY jKkqSmfHcBjuazHP0f2w
// CONSUMER_SECRET 5QyHAG0UUViuOEnjmgaqJkdRLo7xpBgLcClrZmmyjo
private static final String CONSUMER_KEY = "lMZOu2kyJCfBVTicQY4GQ";
private static final String CONSUMER_SECRET = "JpIb6atx5mQK2oGGS9ikl0UcNSeBcaO0AroaN1VSE";

*//**
 * Get Twitter object from token key that was saved on pref.
 * 
 * @param context
 *            activity context.
 * @return Twitter object.
 * @throws TwitterException
 *//*
public static Twitter getTwitterObj(Context context)
        throws TwitterException {
    if (context == null) {
        throw new TwitterException("Conext is null!");
    }
    // PBPreferenceUtils.saveStringPref(context,PBConstant.PREF_NAME,
    // PREF_TWITTER_USERNAME,username);
    String tokenKey = PBPreferenceUtils.getStringPref(context,
            PBConstant.PREF_NAME, TwitterHandler.PREF_TWITTER_TOKEN, "");

    String tokenSecretKey = PBPreferenceUtils.getStringPref(context,
            PBConstant.PREF_NAME, TwitterHandler.PREF_TWITTER_SCRECRET, "");
    // get token key from PREF
    if (TextUtils.isEmpty(tokenKey) || TextUtils.isEmpty(tokenSecretKey)) {
        throw new TwitterException("token key or token secret key is null!");
    }

    Twitter twitter = null;
    try {
        // mTwitter.setOAuthAccessToken(token);
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true).setOAuthConsumerKey(CONSUMER_KEY)
                .setOAuthConsumerSecret(CONSUMER_SECRET)
                .setOAuthAccessToken(tokenKey).setOAuthAccessTokenSecret(
                        tokenSecretKey);

        TwitterFactory tf = new TwitterFactory(cb.build());
        twitter = tf.getInstance();
    } catch (IllegalStateException e) {
        Log
                .e("mapp", ">>> cannot create Twitter instance: "
                        + e.toString());
        throw new TwitterException(e);
    }
    return twitter;
}

*//**
 * Method for support login and get token key from Twitter server.
 * 
 * @param context
 *            Activity Context.
 * @param username
 *            Twitter user name.
 * @param password
 *            password.
 * @param storeToken
 *            set <b>true</b> if want to save access token key for fast
 *            access in future use, otherwise.
 * @return
 * @throws TwitterException
 *//*
public static Twitter getLogin(Context context, String username,
        String password, boolean storeToken) throws TwitterException {
    if (context == null) {
        return null;
    }
    if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
        return null;
    }

    Configuration configuration = new ConfigurationBuilder()
            .setOAuthConsumerKey(CONSUMER_KEY).setOAuthConsumerSecret(
                    CONSUMER_SECRET).build();

    Twitter twitter = new TwitterFactory(configuration)
            .getInstance(new BasicAuthorization(username, password));

    AccessToken token;

    try {
        token = twitter.getOAuthAccessToken();
        // mTwitter.setOAuthAccessToken(token);
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true).setOAuthConsumerKey(CONSUMER_KEY)
                .setOAuthConsumerSecret(CONSUMER_SECRET)
                .setOAuthAccessToken(token.getToken())
                .setOAuthAccessTokenSecret(token.getTokenSecret());

        // TODO need to save Token and SecretToken for fast access?
        if (storeToken) {
            storeAccessToken(context, token);
        }
        TwitterFactory tf = new TwitterFactory(cb.build());
        twitter = tf.getInstance();
    } catch (TwitterException e) {
        Log.e("mapp", ">>> [Twitter] ERROR while execute login: "
                + e.toString());
        throw new TwitterException(e);
    } catch (IllegalStateException e) {
        Log.e("mapp", ">>> [Twitter] ERROR while execute login: "
                + e.toString());
        throw new TwitterException(e);
    }
    return twitter;
}

*//**
 * Save access token key for future use.
 * 
 * @param context
 *            activity Context.
 * @param tocken
 *            token key want to save to Pref.
 * @return return true if storing token key successful.
 *//*
private static boolean storeAccessToken(Context context, AccessToken token) {
    if (context == null) {
        return false;
    }
    if (token == null) {
        return false;
    }

    PBPreferenceUtils.saveStringPref(context, PBConstant.PREF_NAME,
            PBConstant.PREF_TOKEN, token.getToken());
    PBPreferenceUtils.saveStringPref(context, PBConstant.PREF_NAME,
            PBConstant.PREF_TOKEN_SECRET, token.getTokenSecret());
    return true;
}

*//**
 * UPdate Twitter status.
 * @param twitterObj Twitter object.
 * @param statusWantToSet status want to update.
 * @return
 * @throws TwitterException
 *//*
public static boolean setUpdateStatus(Twitter twitterObj,
        String statusWantToSet) throws TwitterException {
    if (twitterObj == null) {
        return false;
    }
    if (TextUtils.isEmpty(statusWantToSet)) {
        return false;
    }
    try {
        twitterObj.updateStatus(statusWantToSet);
        return true;
    } catch (TwitterException e) {
        // e.printStackTrace();
        Log.e("mapp", ">> update twitter status fail!");
        throw new TwitterException(e);
    }
}

private PagableResponseList<User> mResponseList;

*//**
 * getting following friend list from Twitter server.
 * @param twitterObj Twitter object.
 * @param responseList following friend list.
 * @throws TwitterException
 *//*
public void fetchFollowingData(Twitter twitterObj,
        PagableResponseList<User> responseList) throws TwitterException {
    if (twitterObj == null) {
        return;
    }
    long cursor = -1;
    if (mResponseList != null) {
        cursor = responseList.getNextCursor();
    }
    responseList = twitterObj
            .getFriendsStatuses("mOwnerScreenName", cursor);

    for (int i = 0; i < mResponseList.size(); i++) {
        User user = mResponseList.get(i);
        user.getName();
        // TODO insert database or something like that
    }
}

// /**
// * Updates the authenticating user's profile image.
// * @param context the application context.
// * @param mProfileImage image file that we want to upload.
// * @return <b>true</b> if the operation is successes, otherwise.
// */
// public boolean updateTwitterProfileIcon(Context context, File
// mProfileImage) {
// // File mFile = new File(filePath);
// // final File path = new File(Environment.getExternalStorageDirectory(),
// // context.getPackageName());
// // if (!path.exists()) {
// // path.mkdir();
// // }
// // mFile = new File(path, fileTempName);
// boolean result = false;
// Twitter mTwitter;
// try {
// mTwitter = getInstance(context);
// mTwitter.updateProfileImage(mProfileImage);
// result = true;
// } catch (IllegalArgumentException e) {
// e.printStackTrace();
// } catch (NullPointerException e) {
// e.printStackTrace();
// } catch (TwitterException e) {
// e.printStackTrace();
// }
// return result;
// }
//    
// public static void saveAccessToken(Context context, String accessToken,
// String accessTokenSecret) {
// PreferenceUtil.saveDefaultPreferences(context, ACCESS_TOKEN,
// accessToken);
// PreferenceUtil.saveDefaultPreferences(context, ACCESS_TOKEN_SECRET,
// accessTokenSecret);
// }
/**/


 