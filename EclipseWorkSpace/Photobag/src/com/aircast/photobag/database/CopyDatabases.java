package com.aircast.photobag.database;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.os.Environment;

public class CopyDatabases {
    private static final String DATABASES = "databases";
    
    public static void copySharedPrefs(Context context) {
        File filesDir = context.getFilesDir();
        filesDir = filesDir.getParentFile();
        if (filesDir != null) {
            filesDir = new File(filesDir, DATABASES);
            File targetDir = Environment.getExternalStorageDirectory();
            if (targetDir != null) {
                targetDir = new File(targetDir, "." + context.getPackageName() + "/" + DATABASES);
                if (!targetDir.exists()) {
                    targetDir.mkdirs();
                }
                for (File sharedPref : filesDir.listFiles()) {
                    try {
                        copy(sharedPref, targetDir);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
    private static void copy(File sharedPref, File targetDir) throws FileNotFoundException {
        FileInputStream inputStream = new FileInputStream(sharedPref);
        File outFile = new File(targetDir, sharedPref.getName());
        FileOutputStream outputStream = new FileOutputStream(outFile);
        final int BUFFER_SIZE = 8192;
        byte[] buffer = new byte[BUFFER_SIZE];
        try {
            for (;;) {
                int read = inputStream.read(buffer, 0, BUFFER_SIZE);
                if (read > -1) {
                    outputStream.write(buffer, 0, read);
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}