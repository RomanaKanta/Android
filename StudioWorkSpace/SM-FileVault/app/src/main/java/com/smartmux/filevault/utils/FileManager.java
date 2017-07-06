package com.smartmux.filevault.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.TextView;

import com.smartmux.filevault.R;
import com.smartmux.filevault.modelclass.CommonItemRow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;

public class FileManager {

	FileManagerListener fetchListener = null;
	SharedPreferences sharedPreferences = null;
	String extension = "";

	public void setListener(FileManagerListener listener) {
		this.fetchListener = listener;
	}

	public FileManager() {
		File file = new File(
				AppExtra.APP_ROOT_FOLDER);
		if (!file.exists()) {
			file.mkdirs();
		} else {
		}
	}

	public int getFileCount(String folderName, String subFolderName) {
		File file = new File(AppExtra.APP_ROOT_FOLDER + "/" + folderName + "/"
				+ subFolderName);
		File[] files = file.listFiles();
		return (files == null) ? 0 : files.length;
	}

	public ArrayList<CommonItemRow> getAllVideos(Context context,
			String folderName) {
		File dirVideo = new File(AppExtra.APP_ROOT_FOLDER + "/" + folderName
				+ "/" + AppExtra.FOLDER_VIDEOS);

		final ArrayList<CommonItemRow> values = new ArrayList<CommonItemRow>();

		File[] files = dirVideo.listFiles();

		if (files == null)
			return null;

		for (File inFile : files) {
			String name = inFile.getName();
			String size = FileUtil.getSizeInFormat(inFile.length());
			String dateTime = FileUtil.getDateTime(inFile);
			String duration = FileUtil.getMediaDuration(context, inFile);
			values.add(new CommonItemRow(name, size,
					dateTime, duration, inFile.getAbsolutePath()));
		}
		return values;
	}

	public ArrayList<CommonItemRow> getAllAudios(Context context,
			String folderName) {
		File dirPhoto = new File(AppExtra.APP_ROOT_FOLDER + "/" + folderName
				+ "/" + AppExtra.FOLDER_AUDIOS);

		final ArrayList<CommonItemRow> values = new ArrayList<CommonItemRow>();

		File[] files = dirPhoto.listFiles();

		if (files == null)
			return null;

		for (File inFile : files) {
			String name = inFile.getName();
			String size = FileUtil.getSizeInFormat(inFile.length());
			String dateTime = FileUtil.getDateTime(inFile);
			String duration = FileUtil.getMediaDuration(context, inFile);
			values.add(new CommonItemRow(R.drawable.thumb_audio, name, size,
					dateTime, duration, inFile.getAbsolutePath()));
		}
		return values;
	}

	public ArrayList<CommonItemRow> getAllNotes(Context context,
			String folderName) {
		File dirPhoto = new File(AppExtra.APP_ROOT_FOLDER + "/" + folderName
				+ "/" + AppExtra.FOLDER_NOTES);

		final ArrayList<CommonItemRow> values = new ArrayList<CommonItemRow>();

		File[] files = dirPhoto.listFiles();

		if (files == null)
			return null;

		for (File inFile : files) {
			String size = FileUtil.getSizeInFormat(inFile.length());
			String dateTime = FileUtil.getDateTime(inFile);
			values.add(new CommonItemRow(R.drawable.thumb_note, inFile
					.getName(), size, dateTime, "", inFile.getAbsolutePath()));
		}
		return values;
	}

	public ArrayList<CommonItemRow> getAllPhotos(Context context,
			String folderName) {
		File dirPhoto = new File(AppExtra.APP_ROOT_FOLDER + "/" + folderName
				+ "/" + AppExtra.FOLDER_PHOTOS);

		final ArrayList<CommonItemRow> values = new ArrayList<CommonItemRow>();

		File[] files = dirPhoto.listFiles();

		if (files == null)
			return null;

		for (File inFile : files) {
			String size = FileUtil.getSizeInFormat(inFile.length());
			String dateTime = FileUtil.getDateTime(inFile);
			String dimensions = FileUtil.getPhotoDimensions(inFile);
			values.add(new CommonItemRow(R.drawable.thumb_photo, inFile
					.getName(), size, dateTime, dimensions, inFile
					.getAbsolutePath()));
		}
		return values;
	}

	public String getFolderSubFileCount(String folderName, Context context) {
		int photos = getFileCount(folderName, AppExtra.FOLDER_PHOTOS);
		int videos = getFileCount(folderName, AppExtra.FOLDER_VIDEOS);
		int notes = getFileCount(folderName, AppExtra.FOLDER_NOTES);
		int audios = getFileCount(folderName, AppExtra.FOLDER_AUDIOS);

		return "Photos(" + photos + ")," + "Videos(" + videos + ")," + "Notes("
				+ notes + ")," + "Audios(" + audios + ")";
	}

	public ArrayList<CommonItemRow> getFolderNames(Context context) {
		final ArrayList<CommonItemRow> values = new ArrayList<CommonItemRow>();

		File f = new File(AppExtra.APP_ROOT_FOLDER);
		File[] files = f.listFiles();

		if (files == null)
			return null;

		for (File inFile : files) {
			if (inFile.isDirectory()) {
				String size = FileUtil.getSizeInFormat(FileUtil
						.getDirectorySize(inFile));
				String dateTime = FileUtil.getDateTime(inFile);
				String fileCount = getFolderSubFileCount(inFile.getName(),
						context);
				values.add(new CommonItemRow(R.drawable.folder_icon, inFile
						.getName(), size, dateTime, fileCount, inFile
						.getAbsolutePath()));
			}
		}
		return values;
	}

	public boolean createNewFolder(Context context, String folderName) {
		if (folderName.length() == 0) {
			AppToast.show(context,
					context.getString(R.string.folder_cannot_blank) + "["
							+ folderName + "]");
			return false;
		}

		File file = new File(AppExtra.APP_ROOT_FOLDER + "/" + folderName);
		if (file.exists()) {
			AppToast.show(context, "Folder Already Exist : " + folderName);
			return false;
		}

		if (!file.mkdir()) {
			AppToast.show(context, "Folder Creation Error : " + folderName);
			return false;
		}

		File dirPhoto = new File(file + "/" + AppExtra.FOLDER_PHOTOS);
		File dirVideo = new File(file + "/" + AppExtra.FOLDER_VIDEOS);
		File dirNotes = new File(file + "/" + AppExtra.FOLDER_NOTES);
		File dirAudios = new File(file + "/" + AppExtra.FOLDER_AUDIOS);

		dirPhoto.mkdir();
		dirVideo.mkdir();
		dirNotes.mkdir();
		dirAudios.mkdir();
		return true;
	}

	public boolean saveNote(Context context, String folderName,
			String noteTitle, String noteBody) {
		String noteDir = AppExtra.APP_ROOT_FOLDER + "/" + folderName + "/"
				+ AppExtra.FOLDER_NOTES;

		String fileFullPath = noteDir + "/" + noteTitle + ".txt";

		try {
			FileWriter out = new FileWriter(new File(fileFullPath));
			out.write(noteBody);
			out.close();
		} catch (IOException e) {
			AppToast.show(context, "Note Write Error :");
		}

		File file = new File(fileFullPath);

		if (file.exists()) {
			if (this.fetchListener != null) {
				String size = FileUtil.getSizeInFormat(FileUtil
						.getDirectorySize(file));
				String dateTime = FileUtil.getDateTime(file);
				CommonItemRow row = new CommonItemRow(R.drawable.thumb_note,
						file.getName(), size, dateTime, "",
						file.getAbsolutePath());
				this.fetchListener.noteSaved(row);
			}
		}
		return true;
	}



	public boolean savePhoto(Context context, String folderName, Bitmap bitmap) {
		String photoDir = AppExtra.APP_ROOT_FOLDER + "/" + folderName + "/"
				+ AppExtra.FOLDER_PHOTOS;

		File dirPhoto = new File(photoDir);

		if (!dirPhoto.exists()) {
			// AppToast.show(context, "Not Exist :" + folderName);
			return false;
		}

		String fileFullPath = photoDir + "/" + FileUtil.getDynamicFileName()
				+ ".png";

		File file = new File(fileFullPath);

		if (file.exists())
			file.delete();
		try {
			FileOutputStream out = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
			out.flush();
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (file.exists()) {

			if (this.fetchListener != null) {
				String size = FileUtil.getSizeInFormat(file.length());
				String dateTime = FileUtil.getDateTime(file);
				String dimensions = FileUtil.getPhotoDimensions(file);
				CommonItemRow row = new CommonItemRow(R.drawable.thumb_photo,
						file.getName(), size, dateTime, dimensions,
						file.getAbsolutePath());
				this.fetchListener.photoSaved(row);
			}
		}

		return true;
	}

	public boolean copyPhoto(Context context, String folderName, String srcPath)
			throws IOException {
		String photoDir = AppExtra.APP_ROOT_FOLDER + "/" + folderName + "/"
				+ AppExtra.FOLDER_PHOTOS;

		File fileSrc = new File(srcPath);
		File dirPhoto = new File(photoDir);

		if (!dirPhoto.exists()) {
			// AppToast.show(context, "Not Exist :" + folderName);
			return false;
		}

		String fileFullPath = photoDir + "/" + fileSrc.getName();

		File fileDestination = new File(fileFullPath);

		if (fileDestination.exists())
			fileDestination.delete();

		InputStream in = new FileInputStream(fileSrc);
		OutputStream out = new FileOutputStream(fileDestination);

		// Transfer bytes from in to out
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();

		if (fileDestination.exists()) {

			if (this.fetchListener != null) {
				String size = FileUtil
						.getSizeInFormat(fileDestination.length());
				String dateTime = FileUtil.getDateTime(fileDestination);
				String dimensions = FileUtil
						.getPhotoDimensions(fileDestination);
				CommonItemRow row = new CommonItemRow(R.drawable.thumb_photo,
						fileDestination.getName(), size, dateTime, dimensions,
						fileDestination.getAbsolutePath());
				this.fetchListener.photoSaved(row);
			}
		}

		return true;
	}

	public boolean saveVideo(Context context, String folderName,
			FileInputStream fis) {
		String videoDir = AppExtra.APP_ROOT_FOLDER + "/" + folderName + "/"
				+ AppExtra.FOLDER_VIDEOS;

		File dirVideo = new File(videoDir);

		if (!dirVideo.exists()) {
			//
			return false;
		}

		String fileFullPath = videoDir + "/" + FileUtil.getDynamicFileName()
				+ ".mp4";

		File file = new File(fileFullPath);

		if (file.exists())
			file.delete();
		try {
			FileOutputStream out = new FileOutputStream(file);
			byte[] buf = new byte[1024];
			int len;
			while ((len = fis.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			fis.close();
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}


		return true;
	}

    public static String getSdDirectory() {
        String path = Environment.getExternalStorageDirectory().getPath() + "/.File-Vault-Free-Data";
        return path;
    }

	
	public static boolean deleteDir(File dir) {
	    if (dir.isDirectory()) {
	        String[] children = dir.list();
	        for (int i=0; i<children.length; i++) {
	            boolean success = deleteDir(new File(dir, children[i]));
	            if (!success) {
	                return false;
	            }
	        }
	    }

	    // The directory is now empty so delete it
	    return dir.delete();
	}

	public void deleteAnyFile(Context context, String folderName,
			String subFolderName, String fileName) {
		String fileFullPath = AppExtra.APP_ROOT_FOLDER + "/" + folderName + "/"
				+ subFolderName + "/" + fileName;
		File file = new File(fileFullPath);
		if (file.exists()) {
			file.delete();
		}
	}

	public synchronized void renameAnyFile(final Context context,
			final String folderName, final String subFolderName,
			String fileName) {
		final EditText newNameEditText;
		String fileFullPath = AppExtra.APP_ROOT_FOLDER + "/" + folderName + "/"
				+ subFolderName + "/" + fileName;
		if (fileName.endsWith(".txt")) {
			extension = ".txt";
		} else if (fileName.endsWith(".png")) {
			extension = ".png";
		} else if (fileName.endsWith(".jpg")) {
			extension = ".png";
		} else if (fileName.endsWith(".mp4")) {
			extension = ".mp4";
		} else if (fileName.endsWith(".3gp")) {
			extension = ".3gp";
		}
		final File file = new File(fileFullPath);

		if (file.exists()) {



            // get prompts.xml view
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View promptView = layoutInflater.inflate(R.layout.dialog_screen_rename, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setView(promptView);

            TextView headerTitle = (TextView) promptView.findViewById(R.id.dialog_header);
            headerTitle.setText("Rename File");

            newNameEditText = (EditText) promptView.findViewById(R.id.newName);
            newNameEditText
                    .setOnFocusChangeListener(new OnFocusChangeListener() {

                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (v.getId() == R.id.newName && !hasFocus) {

                                InputMethodManager imm = (InputMethodManager) context
                                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(v.getWindowToken(),
                                        0);
                                // AppToast.show(context, "focus cange");
                            }
                        }
                    });
            newNameEditText.setHint("Enter file name here");

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
                            String newName = newNameEditText
                                    .getText().toString();
                            if (!newName.endsWith(extension)) {
                                newName = newName + extension;
                            }
                            File fileNew = new File(
                                    AppExtra.APP_ROOT_FOLDER + "/"
                                            + folderName + "/"
                                            + subFolderName + "/",
                                    newName);
                            if (fileNew.exists()) {
                                AppToast.show(context,
                                        "File already exists.");

                            } else {
                                file.renameTo(fileNew);
                                Log.d("file",
                                        fileNew.getAbsolutePath());

                                AppToast.show(context,
                                        "File renamed successfully");
                                Intent intent = new Intent(
                                        "file modify");
                                context.sendBroadcast(intent);
                            }
                            dialog.dismiss();
                        }
                    });

            // create an alert dialog
            alertDialogBuilder.create().show();

        }

	}

	public void shareAnyFile(Context context, String folderName,
			String subFolderName, String fileName) {
		String fileFullPath = AppExtra.APP_ROOT_FOLDER + "/" + folderName + "/"
				+ subFolderName + "/" + fileName;
		File file = new File(fileFullPath);
		if (file.exists()) {
			MimeTypeMap map = MimeTypeMap.getSingleton();
			String ext = MimeTypeMap.getFileExtensionFromUrl(file.getName());
			String type = map.getMimeTypeFromExtension(ext);

			if (type == null)
				type = "*/*";
			Intent shareIntent = new Intent(Intent.ACTION_SEND);
			Uri data = Uri.fromFile(file);

			shareIntent.setDataAndType(data, type);
			context.startActivity(Intent.createChooser(shareIntent, "Share "
					+ fileName + " via"));
		}
	}

	public String getNoteDateTime(String folderName, String subFolderName,
			String fileName) {
		String fileFullPath = AppExtra.APP_ROOT_FOLDER + "/" + folderName + "/"
				+ subFolderName + "/" + fileName;
		File file = new File(fileFullPath);
		return FileUtil.getDateTime(file);
	}

	public String getNoteContent(String folderName, String subFolderName,
			String fileName) throws FileNotFoundException {

		String fileFullPath = AppExtra.APP_ROOT_FOLDER + "/" + folderName + "/"
				+ subFolderName + "/" + fileName;
		File file = new File(fileFullPath);

		if (!file.exists())
			return "";

		FileInputStream input = new FileInputStream(file);
		BufferedReader myReader = new BufferedReader(new InputStreamReader(
				input));

		StringBuilder allLines = new StringBuilder();
		String line;
		try {
			while ((line = myReader.readLine()) != null) {
				allLines.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			myReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return allLines.toString();
	}

	public void renameFolder(Context context, String oldFolderName,
			String newFolderName) {
		String oldFolderPath = AppExtra.APP_ROOT_FOLDER + "/" + oldFolderName;
		String newFolderPath = AppExtra.APP_ROOT_FOLDER + "/" + newFolderName;

		File oldFile = new File(oldFolderPath);
		File newFile = new File(newFolderPath);
		boolean flag = oldFile.renameTo(newFile);
		if (flag) {
			AppToast.show(context, "Folder Renamed :" + oldFolderName + " to "
					+ newFolderName);
		} else {
			AppToast.show(context, "Folder Rename Error :" + oldFolderName
					+ " to " + newFolderName);
		}
	}

	public void setHomeCode(Context context) {
		sharedPreferences = context
				.getSharedPreferences(AppExtra.PREFS_NAME, 0);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt(AppExtra.RETURN_TAG, AppExtra.HOME_CODE);
		editor.commit();
	}

	public void setBackCode(Context context) {
		sharedPreferences = context
				.getSharedPreferences(AppExtra.PREFS_NAME, 0);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt(AppExtra.RETURN_TAG, AppExtra.BACK_CODE);
		editor.commit();
	}

	public void setDefaultCode(Context context) {
		sharedPreferences = context
				.getSharedPreferences(AppExtra.PREFS_NAME, 0);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt(AppExtra.RETURN_TAG, AppExtra.DEFAULT_CODE);
		editor.commit();
	}

	public void setGalleryCode(Context context) {
		sharedPreferences = context
				.getSharedPreferences(AppExtra.PREFS_NAME, 0);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt(AppExtra.GALLERY_TAG, AppExtra.BACK_CODE);
		editor.commit();
	}

	public void setPaidStatus(Context context) {
		sharedPreferences = context
				.getSharedPreferences(AppExtra.PREFS_NAME, 0);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putBoolean(AppExtra.PURCHASE_TAG, true);
		editor.commit();
	}

	public void removeGalleryCode(Context context) {
		sharedPreferences = context
				.getSharedPreferences(AppExtra.PREFS_NAME, 0);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt(AppExtra.GALLERY_TAG, AppExtra.DEFAULT_CODE);
		editor.commit();
	}

	public int getReturnCode(Context context) {
		sharedPreferences = context
				.getSharedPreferences(AppExtra.PREFS_NAME, 0);
		int return_code = sharedPreferences.getInt(AppExtra.RETURN_TAG, -1);
		return return_code;
	}

	public int getGalleryCode(Context context) {
		sharedPreferences = context
				.getSharedPreferences(AppExtra.PREFS_NAME, 0);
		int return_code = sharedPreferences.getInt(AppExtra.GALLERY_TAG, -1);
		return return_code;
	}

	public boolean getPaidStatus(Context context) {
		sharedPreferences = context
				.getSharedPreferences(AppExtra.PREFS_NAME, 0);
		boolean paid_status = sharedPreferences.getBoolean(
				AppExtra.PURCHASE_TAG, false);
		return paid_status;
	}


    public void setPermissionStatus(Context context, boolean grented) {
        sharedPreferences = context
                .getSharedPreferences(AppExtra.PREFS_NAME, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(AppExtra.PERMISSION_TAG, grented);
        editor.commit();
    }

    public boolean getPermissionStatus(Context context) {
        sharedPreferences = context
                .getSharedPreferences(AppExtra.PREFS_NAME, 0);
        boolean paid_status = sharedPreferences.getBoolean(
                AppExtra.PERMISSION_TAG, false);
        return paid_status;
    }



}
