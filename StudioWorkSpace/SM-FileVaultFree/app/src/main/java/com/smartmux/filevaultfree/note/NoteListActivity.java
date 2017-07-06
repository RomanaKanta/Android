package com.smartmux.filevaultfree.note;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
import com.smartmux.filevaultfree.adapter.RecycleView_NoteAdapter;
import com.smartmux.filevaultfree.adapter.RecycleView_NoteAdapter.OnItemClickListener;
import com.smartmux.filevaultfree.modelclass.CommonItemRow;
import com.smartmux.filevaultfree.utils.AppActionBar;
import com.smartmux.filevaultfree.utils.AppExtra;
import com.smartmux.filevaultfree.utils.AppToast;
import com.smartmux.filevaultfree.utils.FileManager;
import com.smartmux.filevaultfree.utils.FileManagerListener;
import com.smartmux.filevaultfree.utils.GeneralUtils;
import com.smartmux.filevaultfree.utils.ProgressHUD;
import com.smartmux.filevaultfree.widget.FloatingActionButton;

@SuppressLint("NewApi")
public class NoteListActivity extends AppMainActivity implements
		OnClickListener {

	private String folderName;
	private String subFolderName;
	String extension = "";
	private FileManager fileManager;

	private RecyclerView listView;
	private ArrayList<CommonItemRow> listItems = new ArrayList<CommonItemRow>();
	private RecycleView_NoteAdapter listAdapter;

	private UpdateReceiver mReceiver = new UpdateReceiver();
	private FloatingActionButton addButton;

	public class UpdateReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			AppToast.show(getApplicationContext(), "Rename successful");
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
	public void onDestroy() {
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

		boolean isBackground = GeneralUtils
				.isApplicationBroughtToBackground(getApplicationContext());
		// AppToast.show(getApplicationContext(), ""+isBackground);
		int event_code = fileManager.getReturnCode(getApplicationContext());
		if (event_code == AppExtra.HOME_CODE && !isBackground) {
			Intent i = new Intent(NoteListActivity.this,
					LoginWindowActivity.class);
			startActivity(i);

		}

		setAdapter();
		
//		if (listAdapter != null) {
//			listAdapter = null;
//		}
//
//		listItems = fileManager
//				.getAllNotes(getApplicationContext(), folderName);
//		listAdapter = new RecycleView_NoteAdapter(this, 
//				listItems);
//		listView.setAdapter(listAdapter);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_listview);

		addButton = (FloatingActionButton) findViewById(R.id.fab_add_button);
		addButton.setOnClickListener(this);
		addButton.setVisibility(View.VISIBLE);
		
		fileManager = new FileManager();
		fileManager.setBackCode(getApplicationContext());
		fileManager.setListener(new FileManagerListener() {
			public void noteSaved(CommonItemRow row) {
				listItems.add(row);
				listAdapter.notifyDataSetChanged();
			}

			@Override
			public void photoSaved(CommonItemRow row) {
			}

			@Override
			public void videoSaved(CommonItemRow row) {
			}

			@Override
			public void audioSaved(CommonItemRow row) {
			}

		});
		AppActionBar.changeActionBarFont(getApplicationContext(),
				NoteListActivity.this);
		AppActionBar.updateAppActionBar(getActionBar(), this, true);

		Bundle bundle = getIntent().getExtras();

		folderName = bundle.getString(AppExtra.FOLDER_NAME);
		subFolderName = bundle.getString(AppExtra.SUB_FOLDER_NAME);

		if ((folderName != null) && (subFolderName != null)) {
			getActionBar().setTitle(folderName + "/" + subFolderName);
		}

		listView = (RecyclerView) findViewById(R.id.common_listview);
		listView.setHasFixedSize(true);
		listView.setLayoutManager(new LinearLayoutManager(
				this, LinearLayoutManager.VERTICAL, false));
		
//		listItems = fileManager
//				.getAllNotes(getApplicationContext(), folderName);
		
//		setAdapter();

//		listAdapter.SetOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(View view, int position) {
//				String fileName = listItems.get(position).getTitle();
//				fileManager.setBackCode(getApplicationContext());
//				Intent i = new Intent(NoteListActivity.this, NoteEditor.class);
//
//				Log.d("folderName", folderName);
//				Log.d("subFolderName", subFolderName);
//				Log.d("AppExtra.NOTE_MODE_EDIT", AppExtra.NOTE_MODE_EDIT);
//				i.putExtra(AppExtra.FOLDER_NAME, folderName);
//				i.putExtra(AppExtra.SUB_FOLDER_NAME, subFolderName);
//				i.putExtra(AppExtra.NOTE_MODE, AppExtra.NOTE_MODE_EDIT);
//				i.putExtra(AppExtra.NOTE_FILENAME, fileName);
//				startActivity(i);
//				overridePendingTransition(R.anim.push_left_in,
//						R.anim.push_left_out);
//			}
//		});
		// listView.setOnItemLongClickListener(new OnItemLongClickListener() {
		// public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
		// int position, long id) {
		// String fileName = listItems.get(position).getTitle();
		// showFileOptionDialog(fileName, position);
		// return true;
		// }
		// });

	

		IntentFilter intent = new IntentFilter();
		intent.addAction("file modify");
		registerReceiver(mReceiver, intent);
	}

	public void setAdapter() {

		if (listItems != null && listItems.size() > 0) {

			listItems.clear();
		}
		listItems = fileManager
				.getAllNotes(getApplicationContext(), folderName);

		if (listItems != null) {
			listAdapter = new RecycleView_NoteAdapter(this,
					listItems);
			listView.setAdapter(listAdapter);
		}
		listAdapter.notifyDataSetChanged();
		
		listAdapter.SetOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(View view, int position) {
				String fileName = listItems.get(position).getTitle();
				fileManager.setBackCode(getApplicationContext());
				Intent i = new Intent(NoteListActivity.this, NoteEditor.class);

				Log.d("folderName", folderName);
				Log.d("subFolderName", subFolderName);
				Log.d("AppExtra.NOTE_MODE_EDIT", AppExtra.NOTE_MODE_EDIT);
				i.putExtra(AppExtra.FOLDER_NAME, folderName);
				i.putExtra(AppExtra.SUB_FOLDER_NAME, subFolderName);
				i.putExtra(AppExtra.NOTE_MODE, AppExtra.NOTE_MODE_EDIT);
				i.putExtra(AppExtra.NOTE_FILENAME, fileName);
				startActivity(i);
				overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);
			}
		});
		
		
		if (listItems.size() == 0) {
			AppToast.show(getApplicationContext(), "Please add notes");
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
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.fab_add_button: {
			Intent i = new Intent(NoteListActivity.this, NoteEditor.class);
			i.putExtra(AppExtra.FOLDER_NAME, folderName);
			i.putExtra(AppExtra.SUB_FOLDER_NAME, subFolderName);
			i.putExtra(AppExtra.NOTE_MODE, AppExtra.NOTE_MODE_CREATE);
			startActivity(i);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

			break;
		}
		default:

			break;
		}

	}

	public static final class AnActionModeOfEpicProportions implements
			ActionMode.Callback {

		Context ctx;
		private TextView headerTitle;

		public AnActionModeOfEpicProportions(Context ctx) {
			this.ctx = ctx;
		}

		@SuppressLint("NewApi")
		@Override
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
			List<Integer> selectedIndex = new ArrayList<Integer>();
			final ArrayList<String> selectedFileName = new ArrayList<String>();
			String SelectedForRename = null;
			int indexnum = 0;
			for (CommonItemRow i : ((NoteListActivity) ctx).listAdapter.data) {
				if (i.isChecked()) {
					selectedListItems.add(i);
					selectedItems.append(i.getTitle()).append(", ");
					SelectedForRename = i.getTitle();
					selectedIndex.add(indexnum);
					selectedFileName.add(i.getTitle());

				}
				indexnum++;
			}

			if (item.getTitle().equals("Delete")) {

				// Delete
				// get prompts.xml view
				LayoutInflater layoutInflater = LayoutInflater.from((NoteListActivity) ctx);
				View promptView = layoutInflater.inflate(R.layout.dialog_screen_delete, null);
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder((NoteListActivity) ctx);
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
								for (int i = 0; i < selectedFileName
										.size(); i++) {
									((NoteListActivity) ctx)
											.removeFile(selectedFileName
													.get(i));
									// System.out.println(selectedFolderName.get(i)+""+selectedIndex.get(i));

								}

								((NoteListActivity) ctx)
										.runDeleteTask();
								dialog.dismiss();
							}
						});

				// create an alert dialog
			alertDialogBuilder.create().show();

			

				// for(int i=0;i<selectedFileName.size();i++){
				// ((NoteListActivity)ctx).removeFile(selectedFileName.get(i));
				// //
				// System.out.println(selectedFolderName.get(i)+""+selectedIndex.get(i));
				//
				// }
				//
				// ((NoteListActivity)ctx).runDeleteTask();

//				LayoutInflater layoutInflater = null;
//				View contentView = ((NoteListActivity) ctx).getLayoutInflater()
//						.inflate(R.layout.alert_content, null);
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
//											for (int i = 0; i < selectedFileName
//													.size(); i++) {
//												((NoteListActivity) ctx)
//														.removeFile(selectedFileName
//																.get(i));
//												// System.out.println(selectedFolderName.get(i)+""+selectedIndex.get(i));
//
//											}
//
//											((NoteListActivity) ctx)
//													.runDeleteTask();
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

			} else if (item.getTitle().equals("Rename")) {
				// Archive
				if (selectedListItems.size() > 1) {
					toast = Toast.makeText(ctx,
							"Can not rename multiple file at a time",
							Toast.LENGTH_LONG);
				} else {
					((NoteListActivity) ctx)
							.showRenameFileDialog(SelectedForRename);
					// toast = Toast.makeText(ctx, "Archive: " +
					// selectedItems.toString(), Toast.LENGTH_SHORT);
				}

			} else {

				if (selectedListItems.size() > 1) {
					toast = Toast.makeText(ctx,
							"Can not share multiple note at a time",
							Toast.LENGTH_LONG);
				} else {
					((NoteListActivity) ctx).shareFile(SelectedForRename);
					// toast = Toast.makeText(ctx, "share", Toast.LENGTH_SHORT);
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
			((NoteListActivity) ctx).listAdapter.checkedCount = 0;
			((NoteListActivity) ctx).listAdapter.isActionModeShowing = false;
			// set list items states to false
			for (CommonItemRow item : ((NoteListActivity) ctx).listAdapter.data) {
				item.setIsChecked(false);
			}
			((NoteListActivity) ctx).listAdapter.notifyDataSetChanged();
			// Toast.makeText(ctx, "Action mode closed",
			// Toast.LENGTH_SHORT).show();
		}
	}

	public void removeFile(String fileName) {

		fileManager.deleteAnyFile(getApplicationContext(), folderName,
				subFolderName, fileName);
	}

	public void showRenameFileDialog(String selectedFileName) {
		View contentView = getLayoutInflater().inflate(R.layout.dialog_screen_rename, null);
		fileManager.renameAnyFile(NoteListActivity.this, folderName,
				subFolderName, selectedFileName, contentView);
		listAdapter.notifyDataSetChanged();
	}

	public void shareFile(String fileName) {
		String fileFullPath = AppExtra.APP_ROOT_FOLDER + "/" + folderName + "/"
				+ subFolderName + "/" + fileName;
		Intent emailIntent = new Intent(Intent.ACTION_SEND);
		emailIntent.setType("*/*");
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Text File");
		emailIntent.putExtra(Intent.EXTRA_TEXT, "Read This Text");
		emailIntent.putExtra(Intent.EXTRA_STREAM,
				Uri.fromFile(new File(fileFullPath)));// path of video
		startActivity(Intent.createChooser(emailIntent, "Send mail..."));
	}

	public void runDeleteTask() {
		DeleteTask del = new DeleteTask();
		del.execute();

	}

	public class DeleteTask extends AsyncTask<Void, Void, Void> {
		// ProgressDialog dialog;
		ProgressHUD mProgressHUD = new ProgressHUD(NoteListActivity.this);

		@Override
		protected void onPreExecute() {
			mProgressHUD.show(true);
			// dialog = new ProgressDialog(NoteListActivity.this);
			// dialog.setTitle("Deleting Folder...");
			// dialog.setMessage("Please wait...");
			// dialog.setIndeterminate(true);
			// dialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			// TODO Auto-generated method stub
			listItems.clear();
			listItems = fileManager.getAllNotes(getApplicationContext(),
					folderName);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			setAdapter();
			if (mProgressHUD.isShowing()) {

				mProgressHUD.dismiss();
			}
		}

	}

}
