package com.smartmux.filevaultfree.audio;

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
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.smartmux.filevaultfree.AppMainActivity;
import com.smartmux.filevaultfree.LoginWindowActivity;
import com.smartmux.filevaultfree.R;
import com.smartmux.filevaultfree.adapter.RecycleView_FolderListAdapter;
import com.smartmux.filevaultfree.adapter.RecycleView_FolderListAdapter.OnItemClickListener;
import com.smartmux.filevaultfree.modelclass.CommonItemRow;
import com.smartmux.filevaultfree.utils.AppActionBar;
import com.smartmux.filevaultfree.utils.AppExtra;
import com.smartmux.filevaultfree.utils.AppToast;
import com.smartmux.filevaultfree.utils.FileManager;
import com.smartmux.filevaultfree.utils.GeneralUtils;
import com.smartmux.filevaultfree.utils.ProgressHUD;
import com.smartmux.filevaultfree.widget.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class AudioListActivity extends AppMainActivity implements OnClickListener{

	private String folderName;
	private String subFolderName;

	FileManager fileManager;

	private ArrayList<CommonItemRow> listItems = new ArrayList<CommonItemRow>();
	private RecycleView_FolderListAdapter audioListAdapter;
	private RecyclerView audioListView;
	private UpdateReceiver mReceiver = new UpdateReceiver();
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
		setAdapter();

//		if (audioListAdapter != null) {
//			audioListAdapter = null;
//		}
//
//		listItems = fileManager.getAllAudios(getApplicationContext(),
//				folderName);
//		//Toast.makeText(getApplicationContext(), "size "+listItems.size(), 500).show();
//		audioListAdapter = new RecycleView_FolderListAdapter(this, 
//				listItems,true);
//		audioListView.setAdapter(audioListAdapter);
//		audioListAdapter.notifyDataSetChanged();
	}

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_listview);

		 addButton=(FloatingActionButton) findViewById(R.id.fab_add_button);
	        addButton.setVisibility(View.VISIBLE);
	        addButton.setOnClickListener(this);
		
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

		audioListView = (RecyclerView) findViewById(R.id.common_listview);
		audioListView.setHasFixedSize(false);
		audioListView.setLayoutManager(new LinearLayoutManager(
				this, LinearLayoutManager.VERTICAL, false));
		
		
//		listItems = fileManager.getAllAudios(getApplicationContext(),
//				folderName);
		
//		setAdapter();

//		audioListAdapter.SetOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(View view, int position) {
//				String fileName = listItems.get(position).getTitle();
//				fileManager.setBackCode(getApplicationContext());
//				Intent i = new Intent(AudioListActivity.this,
//						PlayAudioActivity.class);
//				i.putExtra(AppExtra.FOLDER_NAME, folderName);
//				i.putExtra(AppExtra.SUB_FOLDER_NAME, subFolderName);
//				i.putExtra(AppExtra.AUDIO_MODE, AppExtra.AUDIO_MODE_PLAY);
//				i.putExtra(AppExtra.AUDIO_FILENAME, fileName);
//				startActivity(i);
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
			audioListAdapter = new RecycleView_FolderListAdapter(this, 
					listItems,true);
			audioListView.setAdapter(audioListAdapter);
		}
		audioListAdapter.notifyDataSetChanged();
		
		
		audioListAdapter.SetOnItemClickListener(new OnItemClickListener() {

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
				overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);
			}
		});
		
		if(listItems.size() == 0){
			AppToast.show(getApplicationContext(), "Please add audios");
			return;
		}
		
	}

	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			fileManager.setBackCode(getApplicationContext());
			finish();
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
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fab_add_button:
			
			fileManager.setBackCode(getApplicationContext());
			Intent i = new Intent(AudioListActivity.this, MainActivity.class);
			i.putExtra(AppExtra.FOLDER_NAME, folderName);
			i.putExtra(AppExtra.SUB_FOLDER_NAME, subFolderName);
			i.putExtra(AppExtra.AUDIO_MODE, AppExtra.AUDIO_MODE_RECORD);
			
//			Bundle bundle = new Bundle();
//			bundle.putString(AppExtra.FOLDER_NAME, folderName);
//			bundle.putString(AppExtra.SUB_FOLDER_NAME, subFolderName);
//			bundle.putString(AppExtra.AUDIO_MODE, AppExtra.AUDIO_MODE_RECORD);
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

//			menu.add("Delete").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
//			menu.add("Rename").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
//			menu.add("Share").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
//			menu.add("Move").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
//			menu.add("Remove star").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

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
			for (CommonItemRow i : ((AudioListActivity) ctx).audioListAdapter.data) {
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

			
				
//			
//
//				for(int i=0;i<selectedFileName.size();i++){
//				((AudioListActivity)ctx).removeFile(selectedFileName.get(i));
////					System.out.println(selectedFolderName.get(i)+""+selectedIndex.get(i));
//					
//				}
//				
//				((AudioListActivity)ctx).runDeleteTask();
				
				
//				LayoutInflater layoutInflater = null;
//				View contentView = ((AudioListActivity) ctx)
//						.getLayoutInflater().inflate(R.layout.alert_content,
//								null);
//				Holder holder = new ViewHolder(contentView);
//				TextView alertText = (TextView) contentView
//						.findViewById(R.id.alert_text);
//				alertText.setText(R.string.delete_text);
//				DialogPlus dialogPlus = DialogPlus
//						.newDialog(ctx)
//						.setContentHolder(holder)
//						.setGravity(Gravity.CENTER)
//						.setHeader(R.layout.header_alert)
//
//						.setOnClickListener(
//								new com.orhanobut.dialogplus.OnClickListener() {
//
//									@Override
//									public void onClick(DialogPlus dialog,
//											View view) {
//
//										// TODO Auto-generated method stub
//										switch (view.getId()) {
//
//										case R.id.yes_button:
//
//											for(int i=0;i<selectedFileName.size();i++){
//											((AudioListActivity)ctx).removeFile(selectedFileName.get(i));
////												System.out.println(selectedFolderName.get(i)+""+selectedIndex.get(i));
//												
//											}
//											
//											((AudioListActivity)ctx).runDeleteTask();
//											dialog.dismiss();
//
//										case R.id.no_button:
//
//											dialog.dismiss();
//
//										}
//
//									}
//								})
//						.setOnDismissListener(new OnDismissListener() {
//							@Override
//							public void onDismiss(DialogPlus dialog) {
//							}
//						}).setOnBackPressListener(new OnBackPressListener() {
//							@Override
//							public void onBackPressed(DialogPlus dialog) {
//
//							}
//						}).setCancelable(true).create();
//				View headerView = dialogPlus.getHeaderView();
//				headerTitle = (TextView) headerView
//						.findViewById(R.id.header_title);
//				headerTitle.setText("Delete");
//				dialogPlus.show();	
//				
				
				
				


			} else if (item.getTitle().equals("Rename")) {
				// Archive
				if(selectedListItems.size()>1){
					toast = Toast.makeText(ctx, "Can not rename multiple folder at a time", Toast.LENGTH_LONG);
				}else{
					((AudioListActivity)ctx).showRenameFileDialog(SelectedForRename);
				//	toast = Toast.makeText(ctx, "Archive: " + selectedItems.toString(), Toast.LENGTH_SHORT);
				}
			

			}else{
				if(selectedListItems.size()>1){
					toast = Toast.makeText(ctx, "Can not share multiple audio at a time", Toast.LENGTH_LONG);
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
			((AudioListActivity) ctx).audioListAdapter.checkedCount = 0;
			((AudioListActivity) ctx).audioListAdapter.isActionModeShowing = false;
			// set list items states to false
			for (CommonItemRow item : ((AudioListActivity) ctx).audioListAdapter.data) {
				item.setIsChecked(false);
			}
			((AudioListActivity) ctx).audioListAdapter.notifyDataSetChanged();
			//Toast.makeText(ctx, "Action mode closed", Toast.LENGTH_SHORT).show();
		}
	}

	public void showRenameFileDialog(String selectedFileName){
		  View contentView = getLayoutInflater().inflate(R.layout.dialog_screen_rename, null);
		fileManager.renameAnyFile(AudioListActivity.this,
				folderName, subFolderName, selectedFileName,contentView);
		audioListAdapter.notifyDataSetChanged();
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
