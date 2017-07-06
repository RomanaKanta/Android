package com.smartmux.textmemo.modelclass;

import java.util.ArrayList;

import android.content.Context;
import android.view.ActionMode;
import android.widget.Toast;

import com.smartmux.textmemo.NoteListActivity;
import com.smartmux.textmemo.utils.ActionCallBackOptions;
import com.smartmux.textmemo.utils.AnActionModeOfEpicProportions;

public class SelectedNoteArray {
	public static ArrayList<NoteListItem> selectedData = new ArrayList<NoteListItem>();

	public static ActionMode mMode;
	public static int count = 0;
	public static boolean actionMode = false;
	public static int getCount() {
		return count;
	}

	public static void setCount(int c) {
		count = c;
	}

	public static void createActionMode(Context context) {
		mMode = ((NoteListActivity) context)
				.startActionMode(new AnActionModeOfEpicProportions(context));
	}

	public static ActionMode getmMode() {
		return mMode;
	}

	public static void setModeCount(Context context, int count) {
		if (mMode != null) {
			new ActionCallBackOptions(context, mMode, count);
		}
	}

	public static void setmMode(ActionMode mode) {
		mMode = mode;
	}

	public static void finishMode() {
		
		if (mMode != null) {
			mMode.finish();
		}
		
	}

	public static ArrayList<NoteListItem> getSelectedData() {
		return selectedData;
	}

	public static void setSelectedData(ArrayList<NoteListItem> selected) {

		if (selectedData != null)
			selectedData.clear();

		selectedData.addAll(selected);
	}

	public static void setData(NoteListItem data) {
		selectedData.add(data);
	}

	public static void removeData(int index) {
		selectedData.remove(index);
	}

	public static void clearList() {
		selectedData.clear();
	}

}
