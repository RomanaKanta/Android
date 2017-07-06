package com.smartmux.voicememo.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.webkit.MimeTypeMap;
import android.widget.EditText;

import com.smartmux.voicememo.R;
import com.smartmux.voicememo.modelclass.CommonItemRow;

import java.io.File;
import java.util.ArrayList;

public class FileManager{
	
	SharedPreferences sharedPreferences = null;

	public FileManager()
	{
		File file = new File(Environment.getExternalStorageDirectory(), AppExtra.APP_ROOT_FOLDER);
	    if (!file.exists()) {
	    	file.mkdirs();
	    }else{
	    }
	}
	

	public ArrayList<CommonItemRow> getAllAudios(Context context)
	{
	    File dirPhoto = new File(AppExtra.APP_ROOT_FOLDER);
		
		final ArrayList<CommonItemRow> values = new ArrayList<CommonItemRow>();
		
		File[] files = dirPhoto.listFiles();
		
		if(files == null) return null;
		
		for (File inFile : files) {
			if(inFile.exists() && inFile.length() != 0){
				
				String name = inFile.getName();
				String size = FileUtil.getSizeInFormat(inFile.length());
				String dateTime = FileUtil.getDateTime(inFile);
				String duration = FileUtil.getMediaDuration(context,inFile);
				values.add(new CommonItemRow(R.drawable.thumb_audio,name,size,dateTime,duration));
			}
			
		}
		return values;
	}


	public void deleteAnyFile(Context context,String fileName)
	{
		String fileFullPath = AppExtra.APP_ROOT_FOLDER + "/"+ fileName;
		File file = new File(fileFullPath);
		if(file.exists()){
			file.delete();
		}
	}
	public void renameAnyFile(final Context context,String fileName)
	{
		String fileFullPath = AppExtra.APP_ROOT_FOLDER + "/"+ fileName;
		final File file = new File(fileFullPath);
		if(file.exists()){
			 AlertDialog.Builder fileDialog = new AlertDialog.Builder(context);
		        fileDialog.setTitle("Rename Audio");
		        final EditText input = new EditText(context);
		        fileDialog.setView(input);
		        fileDialog.setPositiveButton("Ok",
		                new DialogInterface.OnClickListener() {
		                    public void onClick(DialogInterface dialog, int whichButton) {
		                        String newName = input.getText().toString();
		                        if (!newName.endsWith(".3gp")){
					    			newName = newName+".3gp";
					    		}
		                        	file.renameTo(new File(AppExtra.APP_ROOT_FOLDER + "/",newName));
		                            AppToast.show(context, "Audio renamed successfully");
		                        }
		                    });
		        fileDialog.setNegativeButton("Cancel",
		                new DialogInterface.OnClickListener() {
		                    public void onClick(DialogInterface dialog, int whichButton) {
		                        dialog.dismiss();
		                    }
		                });
		        fileDialog.create();
		        fileDialog.show();
		}
	}
	public void shareAnyFile(Context context,String fileName)
	{
		String fileFullPath = AppExtra.APP_ROOT_FOLDER + "/"+ fileName;
		File file = new File(fileFullPath);
		if(file.exists()){
			MimeTypeMap map = MimeTypeMap.getSingleton();
		    String ext = MimeTypeMap.getFileExtensionFromUrl(file.getName());
		    String type = map.getMimeTypeFromExtension(ext);
	
		    if (type == null)
		        type = "*/*";
			Intent shareIntent =  new Intent(Intent.ACTION_SEND);
			Uri data = Uri.fromFile(file);
	
			shareIntent.setDataAndType(data, type);
			context.startActivity(Intent.createChooser(shareIntent, "Share "+fileName+" via"));
		}
	}


	public String getNoteDateTime(String fileName){
		String fileFullPath = AppExtra.APP_ROOT_FOLDER + "/"+ fileName;
		File file = new File(fileFullPath);
		return FileUtil.getDateTime(file);
	}
	
	public void setHomeCode(Context context) {
		sharedPreferences = context.getSharedPreferences(AppExtra.PREFS_NAME, 0);
    	SharedPreferences.Editor editor = sharedPreferences.edit();  
	    editor.putInt(AppExtra.RETURN_TAG, AppExtra.HOME_CODE);
	    editor.commit();
	}
	
	public void setBackCode(Context context) {
		sharedPreferences = context.getSharedPreferences(AppExtra.PREFS_NAME, 0);
    	SharedPreferences.Editor editor = sharedPreferences.edit();  
	    editor.putInt(AppExtra.RETURN_TAG, AppExtra.BACK_CODE);
	    editor.commit();
	}
	public void setDefaultCode(Context context) {
		sharedPreferences = context.getSharedPreferences(AppExtra.PREFS_NAME, 0);
    	SharedPreferences.Editor editor = sharedPreferences.edit();  
	    editor.putInt(AppExtra.RETURN_TAG, AppExtra.DEFAULT_CODE);
	    editor.commit();
	}
	public void setPaidStatus(Context context) {
		sharedPreferences = context.getSharedPreferences(AppExtra.PREFS_NAME, 0);
    	SharedPreferences.Editor editor = sharedPreferences.edit();  
	    editor.putBoolean(AppExtra.PURCHASE_TAG, true);
	    editor.commit();
	}
	
	public int getReturnCode(Context context) {
		sharedPreferences = context.getSharedPreferences(AppExtra.PREFS_NAME, 0);
		int return_code = sharedPreferences.getInt(AppExtra.RETURN_TAG, -1);
    	return return_code;
	}
	
	public boolean getPaidStatus(Context context) {
		sharedPreferences = context.getSharedPreferences(AppExtra.PREFS_NAME, 0);
		boolean paid_status = sharedPreferences.getBoolean(AppExtra.PURCHASE_TAG, false);
    	return paid_status;
	}
}
