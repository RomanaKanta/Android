package com.smartmux.filevault.lib;
//package com.smartmux.filevaultfree.lib;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.util.ArrayList;
//import com.smartmux.filevaultfree.R;
//import com.smartmux.filevaultfree.lib.items.CommonItemRow;
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.graphics.Bitmap;
//import android.net.Uri;
//import android.os.Environment;
//import android.webkit.MimeTypeMap;
//import android.widget.EditText;
//
//public class FileManager{
//
//	FileManagerListener fetchListener = null;
//	SharedPreferences sharedPreferences = null;
//	String extension = null;
//	
//	public void setListener(FileManagerListener listener) {
//        this.fetchListener = listener;
//    }
//	
//	public FileManager()
//	{
//		File file = new File(Environment.getExternalStorageDirectory(), AppExtra.APP_ROOT_FOLDER);
//	    if (!file.exists()) {
//	    	file.mkdirs();
//	    }else{
//	    }
//	}
//
//	public int getFileCount(String folderName,String subFolderName)
//	{
//		File file = new File(AppExtra.APP_ROOT_FOLDER + "/"+ folderName + "/" + subFolderName);
//		File[] files = file.listFiles();
//		return (files == null) ? 0 : files.length;
//	}
//	
//
//	public ArrayList<CommonItemRow> getAllVideos(Context context,String folderName)
//	{
//	    File dirVideo = new File(AppExtra.APP_ROOT_FOLDER + "/"+ folderName + "/"+ AppExtra.FOLDER_VIDEOS);
//		
//		final ArrayList<CommonItemRow> values = new ArrayList<CommonItemRow>();
//		
//		File[] files = dirVideo.listFiles();
//		
//		if(files == null) return null;
//		
//		for (File inFile : files) {
//			String name = inFile.getName();
//			String size = FileUtil.getSizeInFormat(inFile.length());
//			String dateTime = FileUtil.getDateTime(inFile);
//			String duration = FileUtil.getMediaDuration(context,inFile);
//			values.add(new CommonItemRow(R.drawable.thumb_video,name,size,dateTime,duration));
//		}
//		return values;
//	}
//	
//	public ArrayList<CommonItemRow> getAllAudios(Context context,String folderName)
//	{
//	    File dirPhoto = new File(AppExtra.APP_ROOT_FOLDER + "/"+ folderName + "/"+ AppExtra.FOLDER_AUDIOS);
//		
//		final ArrayList<CommonItemRow> values = new ArrayList<CommonItemRow>();
//		
//		File[] files = dirPhoto.listFiles();
//		
//		if(files == null) return null;
//		
//		for (File inFile : files) {
//			String name = inFile.getName();
//			String size = FileUtil.getSizeInFormat(inFile.length());
//			String dateTime = FileUtil.getDateTime(inFile);
//			String duration = FileUtil.getMediaDuration(context,inFile);
//			values.add(new CommonItemRow(R.drawable.thumb_audio,name,size,dateTime,duration));
//		}
//		return values;
//	}
//
//	public ArrayList<CommonItemRow> getAllNotes(Context context,String folderName)
//	{
//	    File dirPhoto = new File(AppExtra.APP_ROOT_FOLDER + "/"+ folderName + "/"+ AppExtra.FOLDER_NOTES);
//		
//		final ArrayList<CommonItemRow> values = new ArrayList<CommonItemRow>();
//		
//		File[] files = dirPhoto.listFiles();
//		
//		if(files == null) return null;
//		
//		for (File inFile : files) {
//	    	String size 	= FileUtil.getSizeInFormat(inFile.length());
//	    	String dateTime = FileUtil.getDateTime(inFile);
//			values.add(new CommonItemRow(R.drawable.thumb_note,inFile.getName(),size,dateTime,""));
//		}
//		return values;
//	}
//
//	public ArrayList<CommonItemRow> getAllPhotos(Context context,String folderName)
//	{
//	    File dirPhoto = new File(AppExtra.APP_ROOT_FOLDER + "/"+ folderName + "/"+ AppExtra.FOLDER_PHOTOS);
//		
//		final ArrayList<CommonItemRow> values = new ArrayList<CommonItemRow>();
//		
//		File[] files = dirPhoto.listFiles();
//		
//		if(files == null) return null;
//		
//		for (File inFile : files) {
//	    	String size 	= FileUtil.getSizeInFormat(inFile.length());
//	    	String dateTime = FileUtil.getDateTime(inFile);
//	    	String dimensions = FileUtil.getPhotoDimensions(inFile);
//			values.add(new CommonItemRow(R.drawable.thumb_photo,inFile.getName(),size,dateTime,dimensions));
//		}
//		return values;
//	}
//
//	public String getFolderSubFileCount(String folderName,Context context)
//	{
//		int	   photos = getFileCount(folderName,AppExtra.FOLDER_PHOTOS);
//		int	   videos = getFileCount(folderName,AppExtra.FOLDER_VIDEOS);
//		int	   notes = getFileCount(folderName,AppExtra.FOLDER_NOTES);
//		int	   audios = getFileCount(folderName,AppExtra.FOLDER_AUDIOS);
//		return context.getString(R.string.photos) + "("+photos+")," + context.getString(R.string.videos) + "("+videos+")," + context.getString(R.string.notes) + "("+notes+")," + context.getString(R.string.audios) + "("+audios+")";
//	}
//	
//	public ArrayList<CommonItemRow> getFolderNames(Context context)
//	{
//		final ArrayList<CommonItemRow> values = new ArrayList<CommonItemRow>();
//		
//		File f = new File(AppExtra.APP_ROOT_FOLDER);
//		File[] files = f.listFiles();
//		
//		if(files == null) return null;
//		
//		for (File inFile : files) {
//		    if (inFile.isDirectory()) {
//		    	String size 	= FileUtil.getSizeInFormat(FileUtil.getDirectorySize(inFile));
//		    	String dateTime = FileUtil.getDateTime(inFile);
//		    	String fileCount = getFolderSubFileCount(inFile.getName(),context);
//		        values.add(new CommonItemRow(R.drawable.thumb_folder,inFile.getName(),size,dateTime,fileCount));
//		    }
//		}
//		return values;
//	}
//	
//
//	public boolean createNewFolder(Context context,String folderName)
//	{
//		if(folderName.length() == 0){
//	    	AppToast.show(context, context.getString(R.string.folder_cannot_blank) + "[" + folderName + "]");
//	    	return false;
//		}
//		
//		File file = new File(AppExtra.APP_ROOT_FOLDER + "/"+ folderName);
//	    if (file.exists()) {
//	    	AppToast.show(context, context.getString(R.string.folder_already_exist) + "[" + folderName + "]");
//	    	return false;
//	    }
//	    
//	    if (!file.mkdir()) {
//	    	AppToast.show(context, context.getString(R.string.folder_creation_error) + "[" + folderName + "]");
//	    	return false;
//	    }
//	    
//	    File dirPhoto = new File(file + "/"+ AppExtra.FOLDER_PHOTOS);
//	    File dirVideo = new File(file + "/"+ AppExtra.FOLDER_VIDEOS);
//	    File dirNotes = new File(file + "/"+ AppExtra.FOLDER_NOTES);
//	    File dirAudios = new File(file + "/"+ AppExtra.FOLDER_AUDIOS);
//	    
//	    dirPhoto.mkdir();
//	    dirVideo.mkdir();
//	    dirNotes.mkdir();
//	    dirAudios.mkdir();
//	    return true;
//	}
//	
//
//	public boolean saveNote(Context context,String folderName,String noteTitle,String noteBody)
//	{
//		String noteDir = AppExtra.APP_ROOT_FOLDER + "/"+ folderName + "/"+ AppExtra.FOLDER_NOTES;
//		
//		String fileFullPath = noteDir + "/" + noteTitle + ".txt";
//		
//		try {
//            FileWriter out = new FileWriter(new File(fileFullPath));
//            out.write(noteBody);
//            out.close();
//        } catch (IOException e) {
//        	AppToast.show(context, "Note Write Error :");
//        }
//        
//        File file = new File(fileFullPath);
//        
//        if (file.exists())
//        {
//        	if (this.fetchListener != null){
//    	    	String size 	= FileUtil.getSizeInFormat(FileUtil.getDirectorySize(file));
//    	    	String dateTime = FileUtil.getDateTime(file);
//        		CommonItemRow row = new CommonItemRow(R.drawable.thumb_note,file.getName(),size,dateTime,"");
//        		this.fetchListener.noteSaved(row);
//        	}
//        }
//		return true;
//	}
//	
//
//	public boolean savePhoto(Context context,String folderName,Bitmap bitmap)
//	{
//		String photoDir = AppExtra.APP_ROOT_FOLDER + "/"+ folderName + "/"+ AppExtra.FOLDER_PHOTOS;
//		
//		File dirPhoto = new File(photoDir);
//		
//		if (!dirPhoto.exists()) {
//	    	AppToast.show(context, "Not Exist :" + folderName);
//	    	return false;
//	    }
//		
//		String fileFullPath = photoDir + "/" + FileUtil.getDynamicFileName() + ".png";
//	
//		
//		File file = new File(fileFullPath);
//        
//        if (file.exists())
//        	file.delete();
//        try {
//            FileOutputStream out = new FileOutputStream(file);
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
//            out.flush();
//            out.close();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        
//        if (file.exists())
//        {
//
//        	if (this.fetchListener != null){
//    	    	String size 	= FileUtil.getSizeInFormat(file.length());
//    	    	String dateTime = FileUtil.getDateTime(file);
//    	    	String dimensions = FileUtil.getPhotoDimensions(file);
//    	    	CommonItemRow row = new CommonItemRow(R.drawable.thumb_photo,file.getName(),size,dateTime,dimensions);
//        		this.fetchListener.photoSaved(row);
//        	}
//        }
//		
//		return true;
//	}
//
//	public boolean saveVideo(Context context,String folderName,FileInputStream fis)
//	{
//        String videoDir = AppExtra.APP_ROOT_FOLDER + "/"+ folderName + "/"+ AppExtra.FOLDER_VIDEOS;
//		
//		File dirVideo = new File(videoDir);
//		
//		if (!dirVideo.exists()) {
//	    	AppToast.show(context, "Not Exist :" + folderName);
//	    	return false;
//	    }
//		
//		String fileFullPath = videoDir + "/" + FileUtil.getDynamicFileName() + ".mp4";
//		
//		File file = new File(fileFullPath);
//        
//        if (file.exists())
//        	file.delete();
//        try {
//            FileOutputStream out = new FileOutputStream(file);
//            byte[] buf = new byte[1024];
//            int len;
//            while ((len = fis.read(buf)) > 0) {
//            	out.write(buf, 0, len);
//            }       
//            fis.close();
//            out.close();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        
//        if (file.exists())
//        {
//        	if (this.fetchListener != null){
//    			String name = file.getName();
//    			String size = FileUtil.getSizeInFormat(file.length());
//    			String dateTime = FileUtil.getDateTime(file);
//    			String duration = FileUtil.getMediaDuration(context,file);
//    			CommonItemRow row = new CommonItemRow(R.drawable.thumb_video,name,size,dateTime,duration);
//        		this.fetchListener.videoSaved(row);
//        	}
//        }
//		
//		return true;	
//	}
//
//
//	public void deleteFolder(Context context,String folderName)
//	{
//		String path = AppExtra.APP_ROOT_FOLDER + "/"+ folderName;
//		
//		File dir = new File(path);
//
//	    if (dir.exists()) {
//	        String deleteCmd = "rm -r " + path;
//	        Runtime runtime = Runtime.getRuntime();
//			AppToast.show(context, "Folder Deleted" + folderName);
//	        try {
//	            runtime.exec(deleteCmd);
//	        } catch (IOException e) {
//	        	
//	        }
//	    }
//	}
//
//	
//	public void deleteAnyFile(Context context,String folderName,String subFolderName,String fileName)
//	{
//		String fileFullPath = AppExtra.APP_ROOT_FOLDER + "/"+ folderName + "/"+ subFolderName + "/"+ fileName;
//		File file = new File(fileFullPath);
//		if(file.exists()){
//			file.delete();
//		}
//	}
//	public void renameAnyFile(final Context context,final String folderName,final String subFolderName,String fileName)
//	{
//		String fileFullPath = AppExtra.APP_ROOT_FOLDER + "/"+ folderName + "/"+ subFolderName + "/"+ fileName;
//		if (fileName.endsWith(".txt")){
//			extension = ".txt";
//		}else if (fileName.endsWith(".png")){
//			extension = ".png";
//		}else if (fileName.endsWith(".mp4")){
//			extension = ".mp4";
//		}else if (fileName.endsWith(".3gp")){
//			extension = ".3gp";
//		}
//		final File file = new File(fileFullPath);
//	
//		if(file.exists()){
//			 AlertDialog.Builder fileDialog = new AlertDialog.Builder(context);
//		        fileDialog.setTitle("Rename Your File");
//		        final EditText input = new EditText(context);
//		        fileDialog.setView(input);
//		        fileDialog.setPositiveButton("Ok",
//		                new DialogInterface.OnClickListener() {
//		                    public void onClick(DialogInterface dialog, int whichButton) {
//		                        String newName = input.getText().toString();
//		                        if (!newName.endsWith(extension)){
//					    			newName = newName+extension;
//					    		}
//		                        	file.renameTo(new File(AppExtra.APP_ROOT_FOLDER + "/"+ folderName + "/"+ subFolderName + "/",newName));
//		                            AppToast.show(context, "File renamed successfully");
//		                        }
//		                    });
//		        fileDialog.setNegativeButton(context.getString(R.string.cancel),
//		                new DialogInterface.OnClickListener() {
//		                    public void onClick(DialogInterface dialog, int whichButton) {
//		                        dialog.dismiss();
//		                    }
//		                });
//		        fileDialog.create();
//		        fileDialog.show();
//		}
//	}
//	public void shareAnyFile(Context context,String folderName,String subFolderName,String fileName)
//	{
//		String fileFullPath = AppExtra.APP_ROOT_FOLDER + "/"+ folderName + "/"+ subFolderName + "/"+ fileName;
//		File file = new File(fileFullPath);
//		if(file.exists()){
//			MimeTypeMap map = MimeTypeMap.getSingleton();
//		    String ext = MimeTypeMap.getFileExtensionFromUrl(file.getName());
//		    String type = map.getMimeTypeFromExtension(ext);
//	
//		    if (type == null)
//		        type = "*/*";
//			Intent shareIntent =  new Intent(android.content.Intent.ACTION_SEND); 
//			Uri data = Uri.fromFile(file);
//	
//			shareIntent.setDataAndType(data, type);
//			context.startActivity(Intent.createChooser(shareIntent, "Share "+fileName+" via"));
//		}
//	}
//
//	public String getNoteDateTime(String folderName,String subFolderName,String fileName){
//		String fileFullPath = AppExtra.APP_ROOT_FOLDER + "/"+ folderName + "/"+ subFolderName + "/"+ fileName;
//		File file = new File(fileFullPath);
//		return FileUtil.getDateTime(file);
//	}
//	
//
//	public String getNoteContent(String folderName,String subFolderName,String fileName) throws FileNotFoundException{
//		
//		String fileFullPath = AppExtra.APP_ROOT_FOLDER + "/"+ folderName + "/"+ subFolderName + "/"+ fileName;
//		File file = new File(fileFullPath);
//		
//		if(!file.exists()) return "";
//		
//		FileInputStream input = new FileInputStream(file);
//		BufferedReader myReader = new BufferedReader(new InputStreamReader(input));
//		
//		StringBuilder allLines = new StringBuilder();
//		String line;
//		try {
//			while ((line = myReader.readLine()) != null) {
//				allLines.append(line+"\n");
//			}
//		}catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//		try {
//			myReader.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return allLines.toString();
//	}
//	
//
//	public void renameFolder(Context context,String oldFolderName,String newFolderName)
//	{
//		String oldFolderPath = AppExtra.APP_ROOT_FOLDER + "/" + oldFolderName;
//		String newFolderPath = AppExtra.APP_ROOT_FOLDER + "/" + newFolderName;
//		
//		File oldFile = new File(oldFolderPath);
//		File newFile = new File(newFolderPath);
//		boolean flag = oldFile.renameTo(newFile);
//		if(flag){
//			AppToast.show(context, "Folder Renamed :" + oldFolderName + " to " + newFolderName);
//		}else{
//			AppToast.show(context, "Folder Rename Error :" + oldFolderName + " to " + newFolderName);
//		}
//	}
//	
//	public void setHomeCode(Context context) {
//		sharedPreferences = context.getSharedPreferences(AppExtra.PREFS_NAME, 0);
//    	SharedPreferences.Editor editor = sharedPreferences.edit();  
//	    editor.putInt(AppExtra.RETURN_TAG, AppExtra.HOME_CODE);
//	    editor.commit();
//	}
//	
//	public void setBackCode(Context context) {
//		sharedPreferences = context.getSharedPreferences(AppExtra.PREFS_NAME, 0);
//    	SharedPreferences.Editor editor = sharedPreferences.edit();  
//	    editor.putInt(AppExtra.RETURN_TAG, AppExtra.BACK_CODE);
//	    editor.commit();
//	}
//	public void setDefaultCode(Context context) {
//		sharedPreferences = context.getSharedPreferences(AppExtra.PREFS_NAME, 0);
//    	SharedPreferences.Editor editor = sharedPreferences.edit();  
//	    editor.putInt(AppExtra.RETURN_TAG, AppExtra.DEFAULT_CODE);
//	    editor.commit();
//	}
//	public void setGalleryCode(Context context) {
//		sharedPreferences = context.getSharedPreferences(AppExtra.PREFS_NAME, 0);
//    	SharedPreferences.Editor editor = sharedPreferences.edit();  
//	    editor.putInt(AppExtra.GALLERY_TAG, AppExtra.BACK_CODE);
//	    editor.commit();
//	}
//	public void removeGalleryCode(Context context) {
//		sharedPreferences = context.getSharedPreferences(AppExtra.PREFS_NAME, 0);
//    	SharedPreferences.Editor editor = sharedPreferences.edit();  
//	    editor.putInt(AppExtra.GALLERY_TAG, AppExtra.DEFAULT_CODE);
//	    editor.commit();
//	}
//	
//	public void setPaidStatus(Context context) {
//		sharedPreferences = context.getSharedPreferences(AppExtra.PREFS_NAME, 0);
//    	SharedPreferences.Editor editor = sharedPreferences.edit();  
//	    editor.putBoolean(AppExtra.PURCHASE_TAG, true);
//	    editor.commit();
//	}
//	
//	public int getReturnCode(Context context) {
//		sharedPreferences = context.getSharedPreferences(AppExtra.PREFS_NAME, 0);
//		int return_code = sharedPreferences.getInt(AppExtra.RETURN_TAG, -1);
//    	return return_code;
//	}
//	public int getGalleryCode(Context context) {
//		sharedPreferences = context.getSharedPreferences(AppExtra.PREFS_NAME, 0);
//		int return_code = sharedPreferences.getInt(AppExtra.GALLERY_TAG, -1);
//    	return return_code;
//	}
//	public boolean getPaidStatus(Context context) {
//		sharedPreferences = context.getSharedPreferences(AppExtra.PREFS_NAME, 0);
//		boolean paid_status = sharedPreferences.getBoolean(AppExtra.PURCHASE_TAG, false);
//    	return paid_status;
//	}
//
//}
