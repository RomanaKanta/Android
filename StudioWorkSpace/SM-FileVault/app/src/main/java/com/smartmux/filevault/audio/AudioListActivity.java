package com.smartmux.filevault.audio;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.smartmux.filevault.AppMainActivity;
import com.smartmux.filevault.LoginWindowActivity;
import com.smartmux.filevault.R;
import com.smartmux.filevault.adapter.RecycleView_FolderListAdapter;
import com.smartmux.filevault.adapter.RecycleView_FolderListAdapter.OnItemClickListener;
import com.smartmux.filevault.modelclass.CommonItemRow;
import com.smartmux.filevault.utils.AppActionBar;
import com.smartmux.filevault.utils.AppExtra;
import com.smartmux.filevault.utils.AppToast;
import com.smartmux.filevault.utils.FileManager;
import com.smartmux.filevault.utils.GeneralUtils;
import com.smartmux.filevault.utils.ProgressHUD;
import com.smartmux.filevault.widget.FloatingActionButton;
import com.smartmux.filevault.widget.FloatingActionsMenu;

import java.util.ArrayList;
import java.util.List;
public class AudioListActivity extends AppMainActivity {

	private String folderName;
	private String subFolderName;

	FileManager fileManager;

	private ArrayList<CommonItemRow> listItems = null;
	private RecycleView_FolderListAdapter listAdapter;
	private RecyclerView listView;
	private UpdateReceiver mReceiver = new UpdateReceiver();
	private FloatingActionsMenu actionMenu;
	private FloatingActionButton addButton;

	public class UpdateReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			Log.d("call", "file modify");
			if (intent != null) {

				if (intent.getAction().equals("file modify")) {
					Log.d("call", "setAdapter");
					setAdapter();
				}

			}
		}

	};

	@Override
	public void onPause() {
		super.onPause();

	
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		if (mReceiver != null) {
			try {
				unregisterReceiver(mReceiver);
				mReceiver = null;
			} catch (Exception e) {
				// do nothing
			}
		}

	}

	protected void onResume() {
		super.onResume();
		
		boolean isBackground=GeneralUtils.isApplicationBroughtToBackground(getApplicationContext());
	//	AppToast.show(getApplicationContext(), ""+isBackground);
		int event_code = fileManager.getReturnCode(getApplicationContext());
		
		if (event_code == AppExtra.HOME_CODE &&!isBackground) {
			Intent i = new Intent(AudioListActivity.this,
					LoginWindowActivity.class);
			startActivity(i);
			

		}

		if (listAdapter != null) {
			listAdapter = null;
		}

		listItems = fileManager.getAllAudios(getApplicationContext(),
				folderName);
		//Toast.makeText(getApplicationContext(), "size "+listItems.size(), 500).show();

        if(listItems!=null) {
            listAdapter = new RecycleView_FolderListAdapter(this,
                    listItems, true);
            listView.setAdapter(listAdapter);
            listAdapter.notifyDataSetChanged();

            listAdapter.SetOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    String fileName = listItems.get(position).getTitle();
                    fileManager.setBackCode(getApplicationContext());
                    Intent i = new Intent(AudioListActivity.this,
                            PlayAudioActivity.class);
                    i.putExtra(AppExtra.FOLDER_NAME, folderName);
                    i.putExtra(AppExtra.SUB_FOLDER_NAME, subFolderName);
                    i.putExtra(AppExtra.AUDIO_MODE, AppExtra.AUDIO_MODE_PLAY);
                    i.putExtra(AppExtra.AUDIO_FILENAME, fileName);
                    startActivity(i);
                    if(listAdapter.isActionModeShowing){
                        listAdapter.mMode.finish();
                    }
                    overridePendingTransition(R.anim.push_left_in,
                            R.anim.push_left_out);
                }
            });
        }
	}

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_listview);
		
//		actionMenu=(FloatingActionsMenu) findViewById(R.id.multiple_actions);
//		actionMenu.setVisibility(View.VISIBLE);
//
//		final FloatingActionButton actionB = (FloatingActionButton) findViewById(R.id.action_first);
//	    actionB.setSize(FloatingActionButton.SIZE_MINI);
//	    actionB.setIcon(R.drawable.videocapture);
//	    
//	    final FloatingActionButton actionA = (FloatingActionButton) findViewById(R.id.action_second);
//	    actionA.setSize(FloatingActionButton.SIZE_MINI);
//	    actionA.setIcon(R.drawable.videogallery);
		 addButton=(FloatingActionButton) findViewById(R.id.fab_add_button);
	        addButton.setVisibility(View.VISIBLE);
		
		fileManager = new FileManager();
		fileManager.setBackCode(getApplicationContext());
		AppActionBar.changeActionBarFont(getApplicationContext(),AudioListActivity.this);
		AppActionBar.updateAppActionBar(getActionBar(), this, true);

		Bundle bundle = getIntent().getExtras();
		folderName = bundle.getString(AppExtra.FOLDER_NAME);
		subFolderName = bundle.getString(AppExtra.SUB_FOLDER_NAME);
		if ((folderName != null) && (subFolderName != null)) {
			getActionBar().setTitle(folderName + "/" + subFolderName);
		}

		listView = (RecyclerView) findViewById(R.id.common_listview);
        listView.setHasFixedSize(false);
        listView.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.VERTICAL, false));

		listItems = fileManager.getAllAudios(getApplicationContext(),
				folderName);

//		listView.setOnItemClickListener(new OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				String fileName = listItems.get(position).getTitle();
//				fileManager.setBackCode(getApplicationContext());
//				Intent i = new Intent(AudioListActivity.this,
//						PlayAudioActivity.class);
//				i.putExtra(AppExtra.FOLDER_NAME, folderName);
//				i.putExtra(AppExtra.SUB_FOLDER_NAME, subFolderName);
//				i.putExtra(AppExtra.AUDIO_MODE, AppExtra.AUDIO_MODE_PLAY);
//				i.putExtra(AppExtra.AUDIO_FILENAME, fileName);
//				startActivity(i);
//				 if(listAdapter.isActionModeShowing){
//		    	        listAdapter.mMode.finish();
//		    	        }
//				overridePendingTransition(R.anim.push_left_in,
//						R.anim.push_left_out);
//			}
//		});
//		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
//			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
//					int position, long id) {
//				String fileName = listItems.get(position).getTitle();
//				showFileOptionDialog(fileName, position);
//				return true;
//			}
//		});
		if(listItems.size() == 0){
			AppToast.show(getApplicationContext(), "Please add audios");
			return;
		}
		
		IntentFilter intent = new IntentFilter();
		intent.addAction("file modify");
		registerReceiver(mReceiver, intent);
	}


	public void setAdapter() {
		if (listItems.size() > 0) {

			listItems.clear();
		}
		listItems = fileManager.getAllAudios(getApplicationContext(),
				folderName);

		if (listItems != null) {
			listAdapter = new RecycleView_FolderListAdapter(this,
					listItems,true);
			listView.setAdapter(listAdapter);
		}
		listAdapter.notifyDataSetChanged();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			fileManager.setBackCode(getApplicationContext());
			finish();
			//AppToast.show(getApplicationContext(), "Back");
			overridePendingTransition(R.anim.push_right_out,
					R.anim.push_right_in);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onUserLeaveHint() {
		fileManager.setHomeCode(getApplicationContext());
		super.onUserLeaveHint();
	}

	public void ClickEvent(View v) {
		// TODO Auto-generated method stub
		
		switch (v.getId()) {
		case R.id.fab_add_button:
			
			 if(listAdapter.isActionModeShowing){
	    	        listAdapter.mMode.finish();
	    	        }
			
			
			fileManager.setBackCode(getApplicationContext());
			Intent i = new Intent(AudioListActivity.this, MainActivity.class);
			i.putExtra(AppExtra.FOLDER_NAME, folderName);
			i.putExtra(AppExtra.SUB_FOLDER_NAME, subFolderName);
			i.putExtra(AppExtra.AUDIO_MODE, AppExtra.AUDIO_MODE_RECORD);

			startActivity(i);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			
			break;

		default:
			break;
		}
		
	}
	
	
	
	
	
	public static final class AnActionModeOfEpicProportions implements ActionMode.Callback {

		Context ctx;
		private TextView headerTitle;

		public AnActionModeOfEpicProportions(Context ctx) {
			this.ctx = ctx;
		}

		@SuppressLint("NewApi") @Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			
			menu.add("Delete").setIcon(R.drawable.delete);
			menu.add("Rename").setIcon(R.drawable.rename);
			menu.add("Share").setIcon(R.drawable.share_icon);

			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

			Toast toast = null;

			ArrayList<CommonItemRow> selectedListItems = new ArrayList<CommonItemRow>();
			

			StringBuilder selectedItems = new StringBuilder();

			// get items selected
			List<Integer> selectedIndex=new ArrayList<Integer>();
			final ArrayList<String> selectedFileName=new ArrayList<String>();
			String SelectedForRename=null;
			int indexnum=0;
			for (CommonItemRow i : ((AudioListActivity) ctx).listAdapter.data) {
				if (i.isChecked()) {
					selectedListItems.add(i);
					selectedItems.append(i.getTitle()).append(", ");
					SelectedForRename=i.getTitle();
					selectedIndex.add(indexnum);
					selectedFileName.add(i.getTitle());
					
					
				}
				indexnum++;
			}

			if (item.getTitle().equals("Delete")) {
				// Delete

// Delete
                // get prompts.xml view
                LayoutInflater layoutInflater = LayoutInflater.from((AudioListActivity) ctx);
                View promptView = layoutInflater.inflate(R.layout.dialog_screen_delete, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder((AudioListActivity) ctx);
                alertDialogBuilder.setView(promptView);

//				headerTitle = (TextView) promptView.findViewById(R.id.dialog_delete_header);
//				headerTitle.setText("Delete");

                TextView alertText = (TextView) promptView
                        .findViewById(R.id.alert_text);
                alertText.setText(R.string.delete_files);

                // setup a dialog window
                alertDialogBuilder.setCancelable(true);


                // Add the buttons
                alertDialogBuilder.setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });

                alertDialogBuilder.setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, int id) {
                                for(int i=0;i<selectedFileName.size();i++){
                                    ((AudioListActivity)ctx).removeFile(selectedFileName.get(i));
//										System.out.println(selectedFolderName.get(i)+""+selectedIndex.get(i));

                                }

                                ((AudioListActivity)ctx).runDeleteTask();
                                dialog.dismiss();
                            }
                        });

                // create an alert dialog
                alertDialogBuilder.create().show();

			} else if (item.getTitle().equals("Rename")) {
				// Archive
				if(selectedListItems.size()>1){
                    toast = Toast.makeText(ctx, "Can not rename multiple files at a time", Toast.LENGTH_SHORT);
                }else{
					((AudioListActivity)ctx).showRenameFileDialog(SelectedForRename);
				//	toast = Toast.makeText(ctx, "Archive: " + selectedItems.toString(), Toast.LENGTH_SHORT);
				}
			

			}else{
				if(selectedListItems.size()>1){
                    toast = Toast.makeText(ctx, "Can not share multiple audios at a time", Toast.LENGTH_SHORT);
                }else{
				((AudioListActivity)ctx).shareFile(SelectedForRename);
				}
			}			
			if (toast != null) {
				toast.show();
			}
			mode.finish();
			return true;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			// Action mode is finished reset the list and 'checked count' also
			// set all the list items checked states to false
			((AudioListActivity) ctx).listAdapter.checkedCount = 0;
			((AudioListActivity) ctx).listAdapter.isActionModeShowing = false;
			// set list items states to false
			for (CommonItemRow item : ((AudioListActivity) ctx).listAdapter.data) {
				item.setIsChecked(false);
			}
			((AudioListActivity) ctx).listAdapter.notifyDataSetChanged();
			//Toast.makeText(ctx, "Action mode closed", Toast.LENGTH_SHORT).show();
		}
	}

	public void showRenameFileDialog(String selectedFileName){
		fileManager.renameAnyFile(AudioListActivity.this,
				folderName, subFolderName, selectedFileName);
		listAdapter.notifyDataSetChanged();
		}
	   
	 public void removeFile(String fileName){
		 
		 fileManager.deleteAnyFile(getApplicationContext(),
					folderName, subFolderName, fileName);
	 }
	 

	   public void shareFile(String fileName){
		   
		   String fileFullPath = AppExtra.APP_ROOT_FOLDER + "/" + folderName + "/"
					+ subFolderName + "/" + fileName;
		   
		   Intent sendIntent = new Intent(Intent.ACTION_SEND);
	        sendIntent.setType("audio/mp4");
	        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Audio");
	       sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+fileFullPath));
	        sendIntent.putExtra(Intent.EXTRA_TEXT, "Enjoy the Audio");
	        startActivity(Intent.createChooser(sendIntent, "Email:"));  
	   }
 
  

   public void runDeleteTask(){
	     DeleteTask del= new DeleteTask();
	     del.execute();
	     
 }
   
   public class DeleteTask extends AsyncTask<Void, Void,Void > {
		 //ProgressDialog dialog;
		 ProgressHUD mProgressHUD=new ProgressHUD(AudioListActivity.this);
		     @Override
		     protected void onPreExecute() {
		    		mProgressHUD.show(true);
//		         dialog = new ProgressDialog(AudioListActivity.this);
//		         dialog.setTitle("Deleting Folder...");
//		         dialog.setMessage("Please wait...");
//		         dialog.setIndeterminate(true);
//		         dialog.show();
		     }

			@Override
			protected Void doInBackground(Void... params) {
				
				// TODO Auto-generated method stub
				  listItems.clear();
				  listItems = fileManager.getAllAudios(getApplicationContext(),
							folderName);
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				
				setAdapter();
				//listAdapter.notifyDataSetChanged();
				if (mProgressHUD.isShowing()) {

					mProgressHUD.dismiss();
				}
			}
			

		     
	}
  
	
	
}
