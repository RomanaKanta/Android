package com.smartmux.textmemo.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.smartmux.textmemo.NoteListActivity;
import com.smartmux.textmemo.modelclass.SelectedNoteArray;

public class AnActionModeOfEpicProportions implements ActionMode.Callback {

	Context context;
//	private TextView headerTitle;
//	FileManager fileManager;

	public AnActionModeOfEpicProportions(Context c) {
		this.context = c;
//		fileManager = new FileManager();

	}

	@SuppressLint("NewApi")
	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {

//		menu.add("Delete").setIcon(R.drawable.icon_delete);
//		menu.add("Rename").setIcon(R.drawable.icon_rename);
//		menu.add("Share").setIcon(R.drawable.icon_share);

		return true;
	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		return false;
	}

	@Override
	public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {

////		Toast toast = null;
//
//		ArrayList<NoteListItem> selectedListItems = new ArrayList<NoteListItem>();
//
//		StringBuilder selectedItems = new StringBuilder();
//
//		// get items selected
//		List<Integer> selectedIndex = new ArrayList<Integer>();
//		final ArrayList<String> selectedFileName = new ArrayList<String>();
//		String SelectedForRename = null;
//		int indexnum = 0;
//
//		// for (CommonItemRow i : ((NoteListActivity) ctx).listAdapter.data) {
//		for (NoteListItem i : SelectedNoteArray.getSelectedData()) {
//			if (i.isChecked()) {
//				selectedListItems.add(i);
//				selectedItems.append(i.getTitle()).append(", ");
//				SelectedForRename = i.getTitle();
//				selectedIndex.add(indexnum);
//				selectedFileName.add(i.getTitle());
//
//
//			}
//			indexnum++;
//		}
//
//		if (item.getTitle().equals("Delete")) {
//			Typeface tf = Typeface.createFromAsset(ctx.getAssets(),
//						AppExtra.AVENIRLSTD_BLACK);
//			LayoutInflater layoutInflater = null;
//			View contentView = ((NoteListActivity) ctx).getLayoutInflater()
//					.inflate(R.layout.alert_content, null);
//			Holder holder = new ViewHolder(contentView);
//			TextView alertText = (TextView) contentView
//					.findViewById(R.id.alert_text);
//			alertText.setText(R.string.delete_text);
//			Button yes= (Button) contentView
//					.findViewById(R.id.yes_button);
//			Button no = (Button) contentView
//					.findViewById(R.id.no_button);
//			
//		yes.setTypeface(tf);
//		no.setTypeface(tf);
//		
//			
//			DialogPlus dialogPlus = DialogPlus
//					.newDialog(ctx)
//					.setContentHolder(holder)
//					.setGravity(Gravity.CENTER)
//					.setHeader(R.layout.header_alert)
//
//					.setOnClickListener(
//							new com.orhanobut.dialogplus.OnClickListener() {
//
//								@Override
//								public void onClick(DialogPlus dialog, View view) {
//
//									// TODO Auto-generated method stub
//									switch (view.getId()) {
//
//									case R.id.yes_button:
//
//										runDeleteTask(selectedFileName);
//										mode.finish();
//										if (((NoteListActivity) ctx).getFragmentRefreshListener() != null) {
//											((NoteListActivity) ctx).getFragmentRefreshListener().onRefresh();
//										}
//										dialog.dismiss();
//
//									case R.id.no_button:
//
//										dialog.dismiss();
//
//									}
//
//								}
//							}).setOnDismissListener(new OnDismissListener() {
//						@Override
//						public void onDismiss(DialogPlus dialog) {
//						}
//					}).setOnBackPressListener(new OnBackPressListener() {
//						@Override
//						public void onBackPressed(DialogPlus dialog) {
//
//						}
//					}).setCancelable(true).create();
//			
//			View headerView = dialogPlus.getHeaderView();
//			headerTitle = (TextView) headerView.findViewById(R.id.header_title);
//			headerTitle.setText("Delete");
//			dialogPlus.show();
//			
//			mode.finish();
//
//		} else if (item.getTitle().equals("Rename")) {
//			// Archive
//			if (selectedListItems.size() > 1) {
////				toast = Toast.makeText(ctx,
////						"Cannot rename multiple file at a time",
////						Toast.LENGTH_SHORT);
//				AppToast.show(ctx, ctx.getString(R.string.mul_rename));
//			} else {
//				showRenameFileDialog(SelectedForRename);
//				mode.finish();
//			}
//
//		} else {
//
//			if (selectedListItems.size() > 1) {
//				AppToast.show(ctx, ctx.getString(R.string.mul_share));
////				toast = Toast.makeText(ctx,
////						"Cannot share multiple note at a time",
////						Toast.LENGTH_SHORT);
//			} else {
//				shareFile(SelectedForRename);
//				mode.finish();
//
//			}
//		}
////		if (toast != null) {
////			toast.show();
////		}

		return false;
	}

	@Override
	public void onDestroyActionMode(ActionMode mode) {

		SelectedNoteArray.clearList();
		
		if(	((NoteListActivity) context).pagerAdapter!=null){
		((NoteListActivity) context).pagerAdapter
		.notifyDataSetChanged();
		}
	
	
	}

	
//	public void removeFile(String fileName) {
//
//		fileManager.deleteAnyFile(ctx, fileName);
//
//	}
//
//	public void showRenameFileDialog(String selectedFileName) {
//		View contentView = ((Activity) ctx).getLayoutInflater().inflate(
//				R.layout.content, null);
//		fileManager.renameAnyFile(ctx, selectedFileName, contentView);
//		// listAdapter.notifyDataSetChanged();
//		
//		if (((NoteListActivity) ctx).getFragmentRefreshListener() != null) {
//			((NoteListActivity) ctx).getFragmentRefreshListener().onRefresh();
//		}
//
//	}
//
//	public void shareFile(String fileName) {
//
//		NoteListActivity.isPrefOpen = true;
//		fileManager.shareAnyFile(ctx, fileName);
//	}
//
//	public void runDeleteTask(ArrayList<String> selectedFileNames) {
//		DeleteTask del = new DeleteTask(selectedFileNames);
//		del.execute();
//
//	}
//
//	public class DeleteTask extends AsyncTask<Void, Void, Void> {
//		// ProgressDialog dialog;
//		ArrayList<String> selectedFileNames;
//		ProgressHUD mProgressHUD = new ProgressHUD(ctx);
//
//		public DeleteTask(ArrayList<String> selectedFileNames) {
//
//			this.selectedFileNames = selectedFileNames;
//		}
//
//		@Override
//		protected void onPreExecute() {
//			mProgressHUD.show(true);
//		}
//
//		@Override
//		protected Void doInBackground(Void... params) {
//
//			// TODO Auto-generated method stub
//			for (int i = 0; i < selectedFileNames.size(); i++) {
//
//				removeFile(selectedFileNames.get(i));
//
//			}
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(Void result) {
//			// TODO Auto-generated method stub
//			super.onPostExecute(result);
//
//			if (mProgressHUD.isShowing()) {
//
//				mProgressHUD.dismiss();
//			}
//
//			if (((NoteListActivity) ctx).getFragmentAdapterRefreshListener() != null) {
//				((NoteListActivity) ctx).getFragmentAdapterRefreshListener()
//						.onAdapterRefresh();
//			}
//		}
//
//	}
}
