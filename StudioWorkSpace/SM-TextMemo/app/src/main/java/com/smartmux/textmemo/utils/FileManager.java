package com.smartmux.textmemo.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.smartmux.textmemo.R;
import com.smartmux.textmemo.modelclass.NoteListItem;
import com.smartmux.textmemo.modelclass.SelectedNoteArray;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class FileManager {

	SharedPreferences sharedPreferences = null;
	public static String shareMessage;
	String extension = "";

	
	public FileManager() {
		File file = new File(Environment.getExternalStorageDirectory(),
				AppExtra.APP_ROOT_FOLDER);
		if (!file.exists()) {
			file.mkdirs();
		} else {
		}
	}

	  public static String getSdDirectory() {
	     	String path = Environment.getExternalStorageDirectory().getPath() + "/.Text-Memo-Data";
	        return path;
	    }
	
	public ArrayList<NoteListItem> getAllNotes(Context context) {
		File dirPhoto = new File(AppExtra.APP_ROOT_FOLDER);

		//Log.d("getAllNotes",dirPhoto.getAbsolutePath());
		final ArrayList<NoteListItem> values = new ArrayList<NoteListItem>();

		File[] files = dirPhoto.listFiles();

		if (files == null)
			return null;

		for (File inFile : files) {
			String size = FileUtil.getSizeInFormat(inFile.length());
			String dateTime = FileUtil.getDateTime(inFile);
			long fileSizeInLong = inFile.length();
			values.add(new NoteListItem(R.drawable.thumb_note,
					inFile.getName(), size, dateTime, "",false, fileSizeInLong));
		}
		return values;
	}

	public boolean saveNote(Context context, String noteTitle, String noteBody,
			boolean isNoteModeEdit) {
		String noteDir = AppExtra.APP_ROOT_FOLDER;

		String fileFullPath = noteDir + "/" + noteTitle + ".txt";
		File fileNew = new File(fileFullPath);
		if (fileNew.exists() && !isNoteModeEdit) {
			AppToast.show(context, context.getString(R.string.file_exists));
			return false;
		} else {

			// AppToast.show(context,
			// "File saved .");
			try {
				FileWriter out = new FileWriter(fileNew);
				out.write(noteBody);
				out.close();
			} catch (IOException e) {
				AppToast.show(context, context.getString(R.string.file_write_error));
			}
			return true;
		}

	}

	public void deleteAnyFile(Context context, String fileName) {
		String fileFullPath = AppExtra.APP_ROOT_FOLDER + "/" + fileName;
		File file = new File(fileFullPath);
		if (file.exists()) {
			file.delete();
		}
	}

//	public void renameAnyFile(final NoteListActivity activity, String fileName) {
//		String fileFullPath = AppExtra.APP_ROOT_FOLDER + "/" + fileName;
//		final File file = new File(fileFullPath);
//		if (file.exists()) {
//			AlertDialog.Builder fileDialog = new AlertDialog.Builder(activity);
//			fileDialog.setTitle(activity.getString(R.string.rename_note));
//			final EditText input = new EditText(activity);
//			fileDialog.setView(input);
//			fileDialog.setPositiveButton("Ok",
//					new DialogInterface.OnClickListener() {
//						public void onClick(DialogInterface dialog,
//								int whichButton) {
//							String newName = input.getText().toString();
//							if (!newName.endsWith(".txt")) {
//								newName = newName + ".txt";
//							}
//							file.renameTo(new File(AppExtra.APP_ROOT_FOLDER
//									+ "/", newName));
//							AppToast.show(activity, activity.getString(R.string.rename_sucs));
//						
//							if (activity.getFragmentRefreshListener() != null) {
//								activity.getFragmentRefreshListener()
//										.onRefresh();
//							}
//
//						}
//					});
//			fileDialog.setNegativeButton("Cancel",
//					new DialogInterface.OnClickListener() {
//						public void onClick(DialogInterface dialog,
//								int whichButton) {
//							dialog.dismiss();
//						}
//					});
//			fileDialog.create();
//			fileDialog.show();
//		}
//	}

	public void renameAnyFile(final Context context,

	String fileName) {
		
		final Typeface tf = Typeface.createFromAsset(context.getAssets(),
				AppExtra.AVENIRLSTD_BLACK);	
		final EditText newNameEditText;
		Button yes,no;
		String fileFullPath = AppExtra.APP_ROOT_FOLDER + "/" + fileName;
		if (fileName.endsWith(".txt")) {
			extension = ".txt";
		}
		final File file = new File(fileFullPath);

		if (file.exists()) {
			


			// get prompts.xml view
			LayoutInflater layoutInflater = LayoutInflater.from(context);
			View promptView = layoutInflater.inflate(R.layout.dialog_rename, null);
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
			alertDialogBuilder.setView(promptView);
			
			newNameEditText = (EditText) promptView.findViewById(R.id.newName);
//			newNameEditText.setTypeface(tf);
			newNameEditText.setHint(context.getString(R.string.note_name_enter));
			
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

							String newName = newNameEditText
									.getText().toString();
							if (newName.length() > 0) {
								if (!newName.endsWith(extension)) {
									newName = newName + extension;
								}
								File fileNew = new File(
										AppExtra.APP_ROOT_FOLDER
												+ "/", newName);
								if (fileNew.exists()) {
									AppToast.show(context,
											context.getString(R.string.file_exists));

								} else {
									file.renameTo(fileNew);
									Log.d("file", fileNew
											.getAbsolutePath());

									AppToast.show(context,
											context.getString(R.string.rename_sucs));

//									if (((NoteListActivity) context)
//											.getFragmentAdapterRefreshListener() != null) {
//										((NoteListActivity) context)
//												.getFragmentAdapterRefreshListener()
//												.onAdapterRefresh();
//									}
//									if (((NoteListActivity) context)
//											.getFragmentRefreshListener() != null) {
//										((NoteListActivity) context)
//												.getFragmentRefreshListener()
//												.onRefresh();
//									}
									
									if (newNameEditText != null) {
										InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
										imm.hideSoftInputFromWindow(newNameEditText.getWindowToken(), 0);
									}
									if (SelectedNoteArray.mMode != null) {
										SelectedNoteArray.finishMode();
								
									}
									dialog.dismiss();
								}

								
							} else {

								AppToast.show(context,
										context.getString(R.string.title_name));

							}
							
							if (SelectedNoteArray.mMode != null) {
								SelectedNoteArray.finishMode();
							
							}
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

	}

	public void shareAnyFile(Context context, String fileName) {
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		shareIntent.putExtra(Intent.EXTRA_SUBJECT, fileName);
		try {
			shareMessage = getNoteContent(fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
		context.startActivity(Intent.createChooser(shareIntent, "Share "
				+ fileName + " via"));
	}

	public String getNoteDateTime(String fileName) {
		String fileFullPath = AppExtra.APP_ROOT_FOLDER + "/" + fileName;
		File file = new File(fileFullPath);
		return FileUtil.getDateTime(file);
	}

	public String getNoteContent(String fileName) throws FileNotFoundException {

		String fileFullPath = AppExtra.APP_ROOT_FOLDER + "/" + fileName;
		File file = new File(fileFullPath);

		if (!file.exists())
			return "";

		StringBuilder buf = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));

			String str = reader.readLine();

			while (null != str) {
				buf.append(str);
				buf.append("\n");
				str = reader.readLine();
			}

			reader.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return buf.toString();

	}

	public void setHomeCode(Context context) {
		sharedPreferences = context
				.getSharedPreferences(AppExtra.PREFS_NAME, 0);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt(AppExtra.RETURN_TAG, AppExtra.HOME_CODE);
		editor.commit();
	}

	public void setSettingCode(Context context) {
		sharedPreferences = context
				.getSharedPreferences(AppExtra.PREFS_NAME, 0);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt(AppExtra.RETURN_TAG, AppExtra.SETTING_CODE);
		editor.commit();
	}

	public void setBackCode(Context context) {
		sharedPreferences = context
				.getSharedPreferences(AppExtra.PREFS_NAME, 0);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt(AppExtra.RETURN_TAG, AppExtra.BACK_CODE);
		editor.commit();
	}

	public void setMainActivityCode(Context context) {
		sharedPreferences = context
				.getSharedPreferences(AppExtra.PREFS_NAME, 0);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt(AppExtra.RETURN_TAG, AppExtra.MAIN_ACTIVITY_CODE);
		editor.commit();
	}
	
	public void setDefaultCode(Context context) {
		sharedPreferences = context
				.getSharedPreferences(AppExtra.PREFS_NAME, 0);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt(AppExtra.RETURN_TAG, AppExtra.DEFAULT_CODE);
		editor.commit();
	}
	
	public void setLockCode(Context context) {
		sharedPreferences = context
				.getSharedPreferences(AppExtra.PREFS_NAME, 0);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt(AppExtra.RETURN_TAG, AppExtra.LOCK_CODE);
		editor.commit();
	}

	public void setPaidStatus(Context context) {
		sharedPreferences = context
				.getSharedPreferences(AppExtra.PREFS_NAME, 0);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putBoolean(AppExtra.PURCHASE_TAG, true);
		editor.commit();
	}

	public int getReturnCode(Context context) {
		sharedPreferences = context
				.getSharedPreferences(AppExtra.PREFS_NAME, 0);
		int return_code = sharedPreferences.getInt(AppExtra.RETURN_TAG, 0);
		return return_code;
	}

	public boolean getPaidStatus(Context context) {
		sharedPreferences = context
				.getSharedPreferences(AppExtra.PREFS_NAME, 0);
		boolean paid_status = sharedPreferences.getBoolean(
				AppExtra.PURCHASE_TAG, false);
		return paid_status;
	}
}
