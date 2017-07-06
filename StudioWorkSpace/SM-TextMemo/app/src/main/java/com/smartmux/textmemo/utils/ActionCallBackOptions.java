package com.smartmux.textmemo.utils;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartmux.textmemo.NoteListActivity;
import com.smartmux.textmemo.R;
import com.smartmux.textmemo.modelclass.NoteListItem;
import com.smartmux.textmemo.modelclass.SelectedNoteArray;
import com.smartmux.textmemo.util.ProgressHUD;

public class ActionCallBackOptions {
	
	Context mContext;
	ActionMode mMode;
	FileManager fileManager;
	
	public ActionCallBackOptions(Context context, ActionMode mode, int count){
		
		this.mContext = context;
		this.mMode = mode;
		fileManager = new FileManager();
		createCutomActionBarTitle( context,  mode,  count);
		
	}

	
	private void createCutomActionBarTitle(final Context context, final ActionMode mode, int count) {

		LayoutInflater inflator = LayoutInflater.from(mContext);
		View v = inflator.inflate(R.layout.custom_action_callback_layer, null);

		Typeface tf = Typeface.createFromAsset(mContext.getAssets(),
				AppExtra.AVENIRLSTD_BLACK);
		TextView title = (TextView) v.findViewById(R.id.textView_count);
		title.setTypeface(tf);
		title.setText(String.valueOf(count));

		ImageView delete = (ImageView) v.findViewById(R.id.imageView_delete);
		ImageView rename = (ImageView) v.findViewById(R.id.imageView_rename);
	    ImageView share = (ImageView) v.findViewById(R.id.imageView_share);

	    delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				menuOperation(context,mode,1);
				
			}
		});
	    
	    rename.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				menuOperation(context,mode,2);
				
			}
		});

		share.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				menuOperation(context,mode,3);
				
			}
		});

		// assign the view to the actionbar
		mode.setCustomView(v);
	}
	
	
	
	private void menuOperation( final Context context, final ActionMode mode,int option ){
		ArrayList<NoteListItem> selectedListItems = new ArrayList<NoteListItem>();

		StringBuilder selectedItems = new StringBuilder();

		// get items selected
		List<Integer> selectedIndex = new ArrayList<Integer>();
		final ArrayList<String> selectedFileName = new ArrayList<String>();
		String SelectedForRename = null;
		int indexnum = 0;

		// for (CommonItemRow i : ((NoteListActivity) ctx).listAdapter.data) {
		for (NoteListItem i : SelectedNoteArray.getSelectedData()) {
			if (i.isChecked()) {
				selectedListItems.add(i);
				selectedItems.append(i.getTitle()).append(", ");
				SelectedForRename = i.getTitle();
				selectedIndex.add(indexnum);
				selectedFileName.add(i.getTitle());

			}
			indexnum++;
		}
		
		switch (option) {

	case 1:{

			final Typeface tf = Typeface.createFromAsset(context.getAssets(),
						AppExtra.AVENIRLSTD_BLACK);
			


			// get prompts.xml view
			LayoutInflater layoutInflater = LayoutInflater.from(context);
			View promptView = layoutInflater.inflate(R.layout.dialog_delete, null);
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
			alertDialogBuilder.setView(promptView);

			// setup a dialog window
			alertDialogBuilder.setCancelable(true);
			// Add the buttons
			alertDialogBuilder.setNegativeButton("NO",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.dismiss();
						}
					});

			alertDialogBuilder.setPositiveButton("YES",
					new DialogInterface.OnClickListener() {
						public void onClick(final DialogInterface dialog, int id) {
							
//							runDeleteTask(selectedFileName);
							DeleteTask del = new DeleteTask(selectedFileName);
							del.execute();
							
							dialog.dismiss();
						
						}
					});

			// create an alert dialog
			final AlertDialog alert = alertDialogBuilder.create();

			alert.setOnShowListener(new OnShowListener() {
				@Override
				public void onShow(DialogInterface arg0) {

					alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTypeface(tf);
					alert.getButton(AlertDialog.BUTTON_POSITIVE).setTypeface(tf);
//					alert.getButton(AlertDialog.BUTTON_NEGATIVE)
//							.setBackgroundColor(Color.parseColor("#FFFFFF"));
//					alert.getButton(AlertDialog.BUTTON_POSITIVE)
//							.setBackgroundColor(Color.parseColor("#FFFFFF"));

				}
			});
			alert.show();

		}
	
	break;
	
	case 2:{

		// Archive
		if (selectedListItems.size() > 1) {

			AppToast.show(mContext, mContext.getString(R.string.mul_rename));
		} else {
			
			fileManager.renameAnyFile(mContext, SelectedForRename);
			
//			showRenameFileDialog(SelectedForRename);
//			mode.finish();
		}

	
	}
	
	break;
	
	case 3:{


		if (selectedListItems.size() > 1) {
			AppToast.show(mContext, mContext.getString(R.string.mul_share));

		} else {
			
			NoteListActivity.isPrefOpen = true;
			fileManager.shareAnyFile(mContext, SelectedForRename);
//			shareFile(SelectedForRename);
//			mode.finish();

		}
	
	}
	
	break;
	
	default:
		break;
	
		}
		
	}
	
	public void removeFile(String fileName) {

		fileManager.deleteAnyFile(mContext, fileName);

	}

	public void showRenameFileDialog(String selectedFileName) {

		fileManager.renameAnyFile(mContext, selectedFileName);
		
//		if (((NoteListActivity) mContext).getFragmentRefreshListener() != null) {
//			((NoteListActivity) mContext).getFragmentRefreshListener().onRefresh();
//		}

	}

	public void shareFile(String fileName) {

		NoteListActivity.isPrefOpen = true;
		fileManager.shareAnyFile(mContext, fileName);
	}

	public void runDeleteTask(ArrayList<String> selectedFileNames) {
		DeleteTask del = new DeleteTask(selectedFileNames);
		del.execute();

	}

	public class DeleteTask extends AsyncTask<Void, Void, Void> {
		// ProgressDialog dialog;
		ArrayList<String> selectedFileNames;
		ProgressHUD mProgressHUD = new ProgressHUD(mContext);

		public DeleteTask(ArrayList<String> selectedFileNames) {

			this.selectedFileNames = selectedFileNames;
		}

		@Override
		protected void onPreExecute() {
			mProgressHUD.show(true);
		}

		@Override
		protected Void doInBackground(Void... params) {

			for (int i = 0; i < selectedFileNames.size(); i++) {

				fileManager.deleteAnyFile(mContext, selectedFileNames.get(i));
//				removeFile(selectedFileNames.get(i));

			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			super.onPostExecute(result);

			if (mProgressHUD.isShowing()) {

				mProgressHUD.dismiss();
			}
			
			if (SelectedNoteArray.mMode != null) {
				SelectedNoteArray.finishMode();
		
			}
//			if (((NoteListActivity) mContext).getFragmentRefreshListener() != null) {
//				((NoteListActivity) mContext).getFragmentRefreshListener().onRefresh();
//			}

		}

	}
}
