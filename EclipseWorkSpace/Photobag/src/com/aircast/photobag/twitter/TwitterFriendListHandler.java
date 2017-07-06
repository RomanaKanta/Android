package com.aircast.photobag.twitter;

import android.os.Bundle;

import com.aircast.photobag.R;
import com.aircast.photobag.activity.PBAbsActionBarActivity;
import com.aircast.photobag.widget.actionbar.ActionBar;

public class TwitterFriendListHandler extends PBAbsActionBarActivity {
    
    private ActionBar mHeaderBar;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pb_layout_twitter_friendlist);
     
        mHeaderBar = (ActionBar) findViewById(R.id.headerbar);
        setHeader(mHeaderBar, getString(R.string.tweet_dialog_title));

       /* mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View footerView = mInflater.inflate(
                R.layout.pb_layout_twitter_friendlist_footer_waitting, null);
        
        mDatabaseManager = PBDatabaseManager.getInstance(getApplicationContext());
        Cursor mFriendListCursor = mDatabaseManager.getTwitterFriendListCursor();
        mListCursorAdapter = new FriendListCursorAdapter(
                getApplicationContext(), 
                mFriendListCursor, 
                footerView);
        
        mFriendListTwitter = (ListView) findViewById(R.id.lv_twitter_friend_list);
        mFriendListTwitter.setAdapter(mListCursorAdapter);

        startManagingCursor(mFriendListCursor);

        mFriendListTwitter.addFooterView(footerView);
        mFriendListTwitter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
            }
        });*/
        
        /*if (mGettingFriendListTask == null
                || (mGettingFriendListTask != null && mGettingFriendListTask
                        .getStatus() != Status.RUNNING)) {
            mGettingFriendListTask = new GettingFriendListTask();
            mGettingFriendListTask.execute(0);
        }*/
    }
    
    /**
     * Adapter for holding and bind view to listView.
     * @author NhatVT
     */
    /*private class FriendListCursorAdapter extends android.widget.CursorAdapter {

        private LayoutInflater inflater;
        private Context mContext;
        private Cursor mCursor;
        private GettingFriendListTask mGettingFriendListTask;
        private boolean mIsFinishLoading = false;
        private View mLoadingLayout;
        
        public FriendListCursorAdapter(Context context, Cursor c,
                View loadingLayout) {
            super(context, c);
            if (context == null) {
                return;
            }
            this.mContext = context;
            this.mCursor = c;
            this.mLoadingLayout = loadingLayout;
            
            // FIXME need to create new instance of Twitter Object in here
            try {
                mTwitterObject = TwitterHelper.getTwitterObj(this.mContext);
            } catch (TwitterException e) {
                e.printStackTrace();
            }
            
            isRunOnFirstTime = true;
            
            resetResponseList();
            
            inflater = LayoutInflater.from(context);
            if (c == null || c.isClosed()) {
                return;
            }
            
            ID_PROFILE_ICON_URL = c
                    .getColumnIndex(PBDatabaseDefinition.TwitterFriends.C_TWITTER_URL);
            ID_TWITTER_NAME = c
                    .getColumnIndex(PBDatabaseDefinition.TwitterFriends.C_TWITTER_NAME);
            ID_TWITTER_SCREEN_NAME = c
                    .getColumnIndex(PBDatabaseDefinition.TwitterFriends.C_TWITTER_SCREEN_NAME);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            if (context == null || inflater == null) {
                return null;
            }
            View v = inflater.inflate(
                    R.layout.pb_layout_twitter_friendlist_item, 
                    parent, 
                    false);
            bindView(v, context, cursor);
            return v;
        }

        @Override
        public void bindView(View convertView, Context context, Cursor cursor) {
            if (context == null || cursor == null || cursor.isClosed()) {
                return;
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            if (holder == null) {
                holder = new ViewHolder();
                holder.mProfileIcon = (ImageView) convertView
                        .findViewById(R.id.pb_twitter_item_profile_icon_id);
                holder.mTwitterName = (TextView) convertView
                        .findViewById(R.id.pb_twitter_item_name_id);
                holder.mTwitterScreenName = (TextView) convertView
                        .findViewById(R.id.pb_twitter_item_screenname_id);
                convertView.setTag(holder);
            }
            
            holder.mTwitterScreenName.setText(cursor.getString(ID_TWITTER_SCREEN_NAME));
            holder.mTwitterName.setText(cursor.getString(ID_TWITTER_NAME));
            Bitmap profileIcon = null; // ID_PROFILE_ICON
            holder.mProfileIcon.setImageResource(R.drawable.icon);
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (mCursor != null && !mCursor.isClosed()) {
                if (!mCursor.moveToPosition(position)) {
                    throw new IllegalStateException(
                            "Couldn't move cursor to position " + position);
                }
            }
            // TODO desire continue load next data or not 
            if (!mIsFinishLoading && (getCount() - position) <= 1) {
                if (isTaskLoadMoreFriendFinished()) {
                    // start new task for getting more Twitter friend from server
                    mGettingFriendListTask = new GettingFriendListTask(this.mLoadingLayout);
                    mGettingFriendListTask.execute(0);
                }
            }
            
            // inflate view 
            View v;
            if (convertView == null) {
                v = newView(mContext, mCursor, parent);
            } else {
                v = convertView;
            }
            bindView(v, mContext, mCursor);
            return v;
        }
        
        final private boolean isTaskLoadMoreFriendFinished() {
            if (mGettingFriendListTask == null)
                return true;
            return mGettingFriendListTask.getStatus() == AsyncTask.Status.FINISHED;
        }
        
        *//**
         * Finish loading Twitter friend list from server. 
         * @param done set <b>true</b> if not load data anymore. 
         *//*
        public void setStopLoadingData(boolean done) {
            mIsFinishLoading = done;
            if (done && mLoadingLayout != null) {
                mLoadingLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        mLoadingLayout.setVisibility(View.GONE);
                    }
                });
            }
        }
        
        @Override
        public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
            if (getFilterQueryProvider() != null) {
                return getFilterQueryProvider().runQuery(constraint);
            }
            return super.runQueryOnBackgroundThread(constraint);
        }
        
        private class ViewHolder {
            ImageView mProfileIcon;
            TextView mTwitterName;
            TextView mTwitterScreenName;
        }
        
        private PagableResponseList<User> mResponseList;
        private Twitter mTwitterObject;
        private boolean isRunOnFirstTime = false;
        private final int MAX_ITEM_ON_PAGE = 100;
        
        private void fetchTwitterFriendListFromServer() {
            try {
                String mOwnerScreenName = "owner_screen_name";
                long cursor = -1;
                if (mResponseList != null && isRunOnFirstTime == false) {
                    cursor = mResponseList.getNextCursor();
                }
                // FOLLOWER case
                mResponseList = mTwitterObject.getFollowersStatuses(
                        mOwnerScreenName, cursor);
                // FOLLOWING case
                mResponseList = mTwitterObject.getFriendsStatuses(
                        mOwnerScreenName, cursor);

                if (mResponseList == null) {
                    setStopLoadingData(true);
                    return;
                }
    
                if (mResponseList.size() == 0
                        || mResponseList.size() < MAX_ITEM_ON_PAGE) {
                    setStopLoadingData(true);
                } else {
                    if (isRunOnFirstTime == false) {
                        mResponseList.remove(0);
                    }
                }
                if (isRunOnFirstTime) {
                    isRunOnFirstTime = false;
                }
                
                int nResponseListSize = mResponseList.size();
                for (int i = 0; i < nResponseListSize; i++) {
                    User user = mResponseList.get(i);
                    Log.d("mapp", ">>> Twitter screen name=" + user.getProfileImageURL().toString());
                    // FIXME add code for saving data to database
                    if (mDatabaseManager != null) {
                        PBTwitterFriendEntryModel mTwitterEntryModel = new PBTwitterFriendEntryModel(
                                user.getProfileImageURL().toString(),
                                user.getScreenName(),
                                user.getName());
                        mDatabaseManager.insertTwitter(mTwitterEntryModel);
                    } else {
                        Log.e("mapp", ">>> database manager was null!");
                    }
                }
            } catch (TwitterException e) {
                Log.e("mapp", ">>> fetch Twitter friend list ERROR >>> " + e.toString());
                setStopLoadingData(true);
                return;
            }
        }
        
        *//**
         * Reset all data in ResponseList.
         *//*
        public void resetResponseList() {
            if (mResponseList != null) {
                mResponseList.clear();
                mResponseList = null;
            }
        }

        *//**
         * AsyncTask for getting Twitter friend list. 
         * @author NhatVT
         *//*
        private class GettingFriendListTask extends AsyncTask<Integer, Integer, Void> {
            
            private View mLoadingLayour;
            
            public GettingFriendListTask(View loadingLayout) {
                this.mLoadingLayour = loadingLayout;
            }
            
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (mLoadingLayour != null) {
                    mLoadingLayour.setVisibility(View.VISIBLE);
                }
            }
            
            @Override
            protected Void doInBackground(Integer... params) {
                fetchTwitterFriendListFromServer();
                return null;
            }
            
            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                if (mLoadingLayour != null) {
                    mLoadingLayour.setVisibility(View.GONE);
                }
            }
        }
    }*/
    
    @Override
    protected void handleHomeActionListener() {
        finish();
    }

    @Override
    protected void setLeftBtnsDetailHeader(ActionBar headerBar) {
    }

}
