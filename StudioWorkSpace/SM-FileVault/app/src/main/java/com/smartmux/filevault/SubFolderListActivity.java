package com.smartmux.filevault;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.smartmux.filevault.adapter.RecycleView_SubFolderListAdapter;
import com.smartmux.filevault.audio.AudioListActivity;
import com.smartmux.filevault.modelclass.SubFolderItemRow;
import com.smartmux.filevault.note.NoteListActivity;
import com.smartmux.filevault.photo.PhotoListActivity;
import com.smartmux.filevault.utils.AppActionBar;
import com.smartmux.filevault.utils.AppExtra;
import com.smartmux.filevault.utils.FileManager;
import com.smartmux.filevault.utils.GeneralUtils;
import com.smartmux.filevault.video.VideoListActivity;
import com.smartmux.filevault.adapter.RecycleView_SubFolderListAdapter.OnItemClickListener;

import java.util.ArrayList;

public class SubFolderListActivity extends AppMainActivity{

	private String 					folderName;
	private FileManager		  		fileManager;
//	private SubFolderListAdapter 	listAdapter;
//	private ListView 			 	listView;

    private RecycleView_SubFolderListAdapter listAdapter;
    private RecyclerView listView;

	private ArrayList<SubFolderItemRow> listItems;
	private ArrayList<String> 			listItemsProperNames;
	private EasyTracker easyTracker = null;
	
	protected static final String TAG = null;
	
	@SuppressLint({ "NewApi", "DefaultLocale" })
	
	@Override
	public void onStart() {
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		boolean isBackground=GeneralUtils.isApplicationBroughtToBackground(getApplicationContext());
		
		int event_code = fileManager.getReturnCode(getApplicationContext());
		if(event_code == AppExtra.HOME_CODE && !isBackground){
			Intent i = new Intent(SubFolderListActivity.this, LoginWindowActivity.class);
	        startActivity(i);
			
		}

		listItems.clear();
		listItems.add(new SubFolderItemRow(getApplicationContext().getString(R.string.photos),
											fileManager.getFileCount(folderName, AppExtra.FOLDER_PHOTOS) + " " + getApplicationContext().getString(R.string.photos),
											R.drawable.subfolder_photo));
		listItems.add(new SubFolderItemRow(getApplicationContext().getString(R.string.videos),
											fileManager.getFileCount(folderName, AppExtra.FOLDER_VIDEOS) + " " + getApplicationContext().getString(R.string.videos),
											R.drawable.subfolder_video));
		listItems.add(new SubFolderItemRow(getApplicationContext().getString(R.string.notes),
											fileManager.getFileCount(folderName, AppExtra.FOLDER_NOTES)  + " " + getApplicationContext().getString(R.string.notes),
											R.drawable.subfolder_note));
		listItems.add(new SubFolderItemRow(getApplicationContext().getString(R.string.audios),
											fileManager.getFileCount(folderName, AppExtra.FOLDER_AUDIOS) + " " + getApplicationContext().getString(R.string.audios),
											R.drawable.subfolder_audio));
		listAdapter.notifyDataSetChanged();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if(keyCode == KeyEvent.KEYCODE_HOME)
	    {
			fileManager.setHomeCode(getApplicationContext());
			AppActivityManager.setLastActivity(this);
	    }else  if (keyCode == KeyEvent.KEYCODE_BACK){   
        	fileManager.setBackCode(getApplicationContext());
		    finish();
		   
		    overridePendingTransition (R.anim.push_right_out, R.anim.push_right_in);  
            return true;
        }
	    return super.onKeyDown(keyCode, event);
	}
	
	 @Override
		protected void onUserLeaveHint() {
	    	fileManager.setHomeCode(getApplicationContext());
			super.onUserLeaveHint();
	}


	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sub_folder_list_activity);

		fileManager		= new FileManager();
		AppActionBar.changeActionBarFont(getApplicationContext(),SubFolderListActivity.this);
		AppActionBar.updateAppActionBar(getActionBar(), this,true);
		Bundle bundle = getIntent().getExtras();
		folderName = bundle.getString(AppExtra.FOLDER_NAME);
        if(folderName != null){
        	getActionBar().setTitle(folderName);
        }
        
		listItems = new ArrayList<SubFolderItemRow>();
		listItems.add(new SubFolderItemRow("Photos",fileManager.getFileCount(folderName, AppExtra.FOLDER_PHOTOS) + " Photos",R.drawable.subfolder_photo));
		listItems.add(new SubFolderItemRow("Videos",fileManager.getFileCount(folderName, AppExtra.FOLDER_VIDEOS) + " Videos",R.drawable.subfolder_video));
		listItems.add(new SubFolderItemRow("Notes",fileManager.getFileCount(folderName, AppExtra.FOLDER_NOTES)   + " Notes",R.drawable.subfolder_note));
		listItems.add(new SubFolderItemRow("Audios",fileManager.getFileCount(folderName, AppExtra.FOLDER_AUDIOS) + " Audios",R.drawable.subfolder_audio));
		
		listItemsProperNames = new ArrayList<String>();
		listItemsProperNames.add(AppExtra.FOLDER_PHOTOS);
		listItemsProperNames.add(AppExtra.FOLDER_VIDEOS);
		listItemsProperNames.add(AppExtra.FOLDER_NOTES);
		listItemsProperNames.add(AppExtra.FOLDER_AUDIOS);
		
		listView = (RecyclerView) findViewById(R.id.listviewSubFolderList);
        listView.setHasFixedSize(true);
        listView.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.VERTICAL, false));
		
		listAdapter = new RecycleView_SubFolderListAdapter(this,listItems);
	    listView.setAdapter(listAdapter);



	    easyTracker = EasyTracker.getInstance(SubFolderListActivity.this);


        listAdapter.SetOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                String subFolderName = listItemsProperNames.get(position);

                switch(position){
                    case 0:// Photos
                    {
                        easyTracker.send(MapBuilder.createEvent("Photos",
                                "onItemClick", "listView/listviewSubFolderList", null).build());
                        fileManager.setBackCode(getApplicationContext());
                        Intent i = new Intent(SubFolderListActivity.this, PhotoListActivity.class);
                        i.putExtra(AppExtra.FOLDER_NAME, folderName);
                        i.putExtra(AppExtra.SUB_FOLDER_NAME, subFolderName);
                        startActivity(i);
                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    }
                    break;
                    case 1:// Videos
                    {
                        easyTracker.send(MapBuilder.createEvent("Videos",
                                "onItemClick", "listView/listviewSubFolderList", null).build());

                        fileManager.setBackCode(getApplicationContext());
                        Intent i = new Intent(SubFolderListActivity.this, VideoListActivity.class);
                        i.putExtra(AppExtra.FOLDER_NAME, folderName);
                        i.putExtra(AppExtra.SUB_FOLDER_NAME, subFolderName);
                        startActivity(i);
                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    }
                    break;
                    case 2:// Notes
                    {
                        easyTracker.send(MapBuilder.createEvent("Notes",
                                "onItemClick", "listView/listviewSubFolderList", null).build());
                        fileManager.setBackCode(getApplicationContext());
                        Intent i = new Intent(SubFolderListActivity.this, NoteListActivity.class);
                        i.putExtra(AppExtra.FOLDER_NAME, folderName);
                        i.putExtra(AppExtra.SUB_FOLDER_NAME, subFolderName);
                        startActivity(i);
                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    }
                    break;
                    case 3:// Audio
                    {
                        easyTracker.send(MapBuilder.createEvent("Audio",
                                "onItemClick", "listView/listviewSubFolderList", null).build());

                        fileManager.setBackCode(getApplicationContext());
                        Intent i = new Intent(SubFolderListActivity.this, AudioListActivity.class);
                        i.putExtra(AppExtra.FOLDER_NAME, folderName);
                        i.putExtra(AppExtra.SUB_FOLDER_NAME, subFolderName);
                        startActivity(i);
                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    }
                    break;
                    default:
                        break;
                }
            }
        });


//
//        listView.setOnItemClickListener(new OnItemClickListener() {
//            @SuppressLint("DefaultLocale")
//			@Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//            	String subFolderName = listItemsProperNames.get(position);
//
//            	switch(position){
//            		case 0:// Photos
//            		{
//            			easyTracker.send(MapBuilder.createEvent("Photos",
//            					"onItemClick", "listView/listviewSubFolderList", null).build());
//	            		fileManager.setBackCode(getApplicationContext());
//	        			Intent i = new Intent(SubFolderListActivity.this, PhotoListActivity.class);
//	        			i.putExtra(AppExtra.FOLDER_NAME, folderName);
//	        			i.putExtra(AppExtra.SUB_FOLDER_NAME, subFolderName);
//	        	        startActivity(i);
//	        	        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
//            		}
//            		break;
//            		case 1:// Videos
//            		{
//            			easyTracker.send(MapBuilder.createEvent("Videos",
//            					"onItemClick", "listView/listviewSubFolderList", null).build());
//
//            				fileManager.setBackCode(getApplicationContext());
//                			Intent i = new Intent(SubFolderListActivity.this, VideoListActivity.class);
//                			i.putExtra(AppExtra.FOLDER_NAME, folderName);
//                			i.putExtra(AppExtra.SUB_FOLDER_NAME, subFolderName);
//                	        startActivity(i);
//                	        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
//            		}
//            		break;
//            		case 2:// Notes
//            		{
//            			easyTracker.send(MapBuilder.createEvent("Notes",
//            					"onItemClick", "listView/listviewSubFolderList", null).build());
//                		fileManager.setBackCode(getApplicationContext());
//            			Intent i = new Intent(SubFolderListActivity.this, NoteListActivity.class);
//            			i.putExtra(AppExtra.FOLDER_NAME, folderName);
//            			i.putExtra(AppExtra.SUB_FOLDER_NAME, subFolderName);
//            	        startActivity(i);
//            	        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
//            		}
//                	break;
//                	case 3:// Audio
//                	{
//                		easyTracker.send(MapBuilder.createEvent("Audio",
//            					"onItemClick", "listView/listviewSubFolderList", null).build());
//
//                			fileManager.setBackCode(getApplicationContext());
//                			Intent i = new Intent(SubFolderListActivity.this, AudioListActivity.class);
//                			i.putExtra(AppExtra.FOLDER_NAME, folderName);
//                			i.putExtra(AppExtra.SUB_FOLDER_NAME, subFolderName);
//                			startActivity(i);
//                			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
//                	}
//                    break;
//                    default:
//                    break;
//            	}
//            }
//        });
	}
	
	
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	 
	}
}
