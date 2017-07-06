package com.aircast.photobag.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;

/**
 * class Group activity implement common methods for GoupActivity 
 * <p><b>TODO seems never be used, remove this activity later?</b></p>
 * @author lent5
 */
public abstract class PBAbsGroupActivity extends ActivityGroup{
    
    private static final String TAG = "PBAbsGroupActivity";
    /** store the activity id's is in tab's stack */ 
    private ArrayList<String> mActivityIDList;
    /** store the history view we open*/
    protected ArrayList<View> history;
    /** remove activity in group activity */
    public abstract void removeActivity();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Log.d(TAG, "Nemo "+1);
        if (mActivityIDList == null) {
            mActivityIDList = new ArrayList<String>();
        }
    }

    @Override
    public void finishFromChild(Activity child) {
        // super.finishFromChild(child);
        finishActFromChild(child);
    }

    @Override
    public void finishActivityFromChild(Activity child, int requestCode) {
        // super.finishActivityFromChild(child, requestCode);
        finishActFromChild(child);
    }

    /**
     *  TODO support remove active activity from stack activities
     *  use in @finishFromChild and @finishActivityFromChild
     *  @param child
     */
    private void finishActFromChild(Activity child){
        LocalActivityManager manager = getLocalActivityManager();
        int index = mActivityIDList.size()-1;

        if (index < 1) {
            finish();
            return;
        }

        manager.destroyActivity(mActivityIDList.get(index), true);
        mActivityIDList.remove(index);
        index--;
        String lastId = mActivityIDList.get(index);
        Intent lastIntent = manager.getActivity(lastId).getIntent();
        Window newWindow = manager.startActivity(lastId, lastIntent);
        setContentView(newWindow.getDecorView());
    }

    /**
     * If a Child Activity handles KeyEvent.KEYCODE_BACK.
     * Simply override and add this method.
     */
    @Override
    public void onBackPressed () {
        if(mActivityIDList == null) return;
        int childLen = mActivityIDList.size();
        if ( childLen > 1) {
            Activity current = getLocalActivityManager()
            .getActivity(mActivityIDList.get(childLen-1));
            current.finish();
        }
        super.onBackPressed();
    }


    /**
     *  TODO start activity child on tab
     *  @param cls
     *  @param bundle data tranfer to next activity, could be null
     */
    public void startChildActivity(Class<?> cls, Bundle bundle) {
        String id = cls.getName();

        Intent intent = new Intent(PBAbsGroupActivity.this, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if(bundle != null){
            intent.putExtra("data", bundle);
        }

        Window window = getLocalActivityManager().startActivity(cls.getName(),intent);

        if (window != null) {
            if(!mActivityIDList.contains(id)){
                mActivityIDList.add(id);
            }
            setContentView(window.getDecorView());
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        System.gc();        
    }
}
