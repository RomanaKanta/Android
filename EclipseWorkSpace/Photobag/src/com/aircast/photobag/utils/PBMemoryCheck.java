package com.aircast.photobag.utils;

import java.io.File;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

public class PBMemoryCheck {
    
    /**
     * Gets the Android data directory.
     */
    private static final String ANDROID_DATA_PATH = Environment.getDataDirectory().getPath();
    
    /**
     * Threshold free memory available for external memory.
     */
    private static final long EXTERNAL_MEMORY_THRESHOLD = 1024 * 1024 * 100;
    /**
     * Threshold free memory available for external memory.
     */
    private static final long EXTERNAL_MEMORY_FULL = 1024 * 1024 * 1;
    /**
     * Threshold free memory available for internal memory.
     */
    private static final long INTERNAL_MEMORY_THRESOLD = 1024 * 1024 * 5;

    /**
     * Returns the length of this file in bytes. Returns 0 if the file does not
     * exist. The result for a directory is not defined.
     * 
     * @param file
     *            files want to check.
     * @return the number of bytes in this file.
     */
    public static long getFileSize(File file) {
        long size = 0;

        if (file == null)
            return 0;
        if (file.isFile())
            return file.length();
        else {
            File files[] = file.listFiles();
            if(files != null){
                for (File currentFile : files)
                    size += getFileSize(currentFile);
            }
            return size;
        }
    }

    /**
     * Check internal memory size.
     * @return The number of blocks that are free on the file system and
     *         available to applications.
     */
    static private synchronized long checkIternalMemory() {
        StatFs fileStats = new StatFs(ANDROID_DATA_PATH);
        long availableBlocks = fileStats.getAvailableBlocks();
        long blockSize = fileStats.getBlockSize();
        long size = availableBlocks * blockSize;
        Log.i("memcheck", "internal memory: " + size);
        return size;
    }
    
    static public synchronized boolean isInternalStorageEnough(Context ctx) {
        long size = checkIternalMemory();

        if (size > INTERNAL_MEMORY_THRESOLD) {
            return true;
        }

        File file = ctx.getCacheDir();
        if ((size + getFileSize(file)) > INTERNAL_MEMORY_THRESOLD) {
            return true;
        }

        return false;
    }
    
    /**
     * Util for checking external storage, return false if less than 100Mb.
     * @return return <b>true</b> if external storage has enough memory for operation, otherwise.
     */
    static public synchronized boolean isExternalStorageEnough() {
        if (Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            String storageDirectory = Environment.getExternalStorageDirectory()
                    .toString();
            StatFs stat = new StatFs(storageDirectory);
            long remaining = (long) stat.getAvailableBlocks()
                    * (long) stat.getBlockSize();
            if (remaining < EXTERNAL_MEMORY_THRESHOLD) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }
    
    /**
     * Util for checking external storage, return true if less than 5Mb.
     * @return return <b>true</b> if external storage has enough memory for operation, otherwise.
     */
    static public synchronized boolean isExternalStorageNotFull() {
        if (Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            String storageDirectory = Environment.getExternalStorageDirectory()
                    .toString();
            StatFs stat = new StatFs(storageDirectory);
            long remaining = (long) stat.getAvailableBlocks()
                    * (long) stat.getBlockSize();
            if (remaining < EXTERNAL_MEMORY_FULL) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }
    
}
