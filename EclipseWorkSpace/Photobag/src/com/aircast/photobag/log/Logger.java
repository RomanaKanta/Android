package com.aircast.photobag.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.os.Environment;
import android.util.Log;

public class Logger {
    private final static String LOG_FILE = "photobag.log";
    public boolean isEnabled = true;
    private static final String SPACE = " ";
    private static final String CLASS_METHOD_SEPARATOR = "::";
    private static final String LINE_INDICATOR = " @L";
    private static final String D = "D ";
    private static final String E = "E ";
    private static final String I = "I ";
    private static final String W = "W ";
    private static final String V = "V ";
    private static final boolean FILE_LOG_ENABLED = false;//PackageManagerUtil.isDebugMode();
    final String TAG;
    private static FileLogger mFileLogger = null;
    private synchronized static FileLogger getLogger() {
        if (mFileLogger == null) {
            mFileLogger = new FileLogger();
        }
        
        return mFileLogger;
    }
    
    public Logger(Class<?> classRef, boolean isEnabled) {
        TAG = classRef.getSimpleName();
        this.isEnabled = isEnabled;
    }
    
    public Logger(Class<?> classRef) {
        TAG = classRef.getSimpleName();
    }
    
    public Logger(String tag) {
        TAG = tag;
    }
    
    public void debug(Object... messages) {
        if (isEnabled) {
            String msg = buildMessage(messages);
            _log(D, TAG, msg);
            Log.d(TAG, msg);
        }
    }
    
    public void debug(String message, Throwable t) {
        if (isEnabled) {
            log(D, TAG, message, t);
            Log.d(TAG, message, t);
        }
    }
    
    public void verbose(Object... messages) {
        if (isEnabled) {
            String msg = buildMessage(messages);
            _log(V, TAG, msg);
            Log.v(TAG, msg);
        }
    }
    public void verbose(String message, Throwable t) {
        if (isEnabled) {
            log(V, TAG, message, t);
            Log.v(TAG, message, t);
        }
    }
    
    public void info(Object... messages) {
        if (isEnabled) {
            String msg = buildMessage(messages);
            _log(I, TAG, msg);
            Log.i(TAG, msg);
        }
    }
    
    public void info(String message, Throwable t) {
        if (isEnabled) {
            log(I, TAG, message, t);
            Log.i(TAG, message, t);
        }
    }
    
    public void warn(Object... messages) {
        if (isEnabled) {
            String msg = buildMessage(messages);
            _log(W, TAG, msg);
            Log.w(TAG, msg);
        }
    }
    
    public void warn(String message, Throwable t) {
        if (isEnabled) {
            log(W, TAG, message, t);
            Log.w(TAG, message, t);
        }
    }
    
    public void error(Object... messages) {
        if (isEnabled) {
            String msg = buildMessage(messages);
            _log(E, TAG, msg);
            Log.e(TAG, msg);
        }
    }
    
    public void error(String message, Throwable t) {
        if (isEnabled) {
            log(E, TAG, message, t);
            Log.e(TAG, message, t);
        }
    }
    
    public synchronized static void d(String tag, String message) {
        _log(D, tag, message);
        Log.d(tag, message);
    }
    
    public synchronized static void e(String tag, String message) {
        _log(E, tag, message);
        Log.d(tag, message);
    }
    
    public synchronized static void i(String tag, String message) {
        _log(I, tag, message);
        Log.d(tag, message);
    }
    
    public synchronized static void w(String tag, String message) {
        _log(W, tag, message);
        Log.d(tag, message);
    }
    
    public synchronized static void v(String tag, String message) {
        _log(V, tag, message);
        Log.d(tag, message);
    }
    /*public synchronized static void log(String mode, String tag, String message) {
        _log(mode, tag, message);
        Log.d(tag, message);
    }*/
    
    private synchronized static void _log(String mode, String tag, String message) {
        if (FILE_LOG_ENABLED) {
            if (!E.equals(mode) && message.length() > 80) {
                message = message.substring(0, 77) + "...";
            }
            getLogger()
                .appendTimeStamp()
                .append(mode)
                .append(tag)
                .append(SPACE)
                .append(message)
                .newline()
                .flush();
        }
    }
    
    private synchronized static void log(String mode, String tag, String message, Throwable t) {
        _log(mode, tag, message);

        
        FileLogger logger = getLogger();
        for (StackTraceElement element : t.getStackTrace()) {
            logger
                .appendTimeStamp()
                .append(E)
                .append(tag)
                .append(SPACE)
                .append(element.getClassName())
                .append(CLASS_METHOD_SEPARATOR)
                .append(element.getMethodName())
                .append(LINE_INDICATOR)
                .append(element.getLineNumber() + "")
                .newline()
                .flush();
        }
    }
    
    String buildMessage(Object[] messages) {
        StringBuilder builder = new StringBuilder();
        
        if (messages != null) {
            int len = messages.length;
            for (int i = 0; i < len; ++i) {
                builder.append(messages[i]);
                if (i < len - 1)
                    builder.append(SPACE);
            }
        }
        
        return builder.toString();
    }
    
    private static class FileLogger implements Runnable {
        private final ExecutorService mExecutorService = Executors.newSingleThreadExecutor();
        private final StringBuffer mLogBuffer = new StringBuffer(8192);
        private static final Date DATE = new Date();
        private static final String NL = "\n";
        private File mLogFile = null;
        
        FileLogger appendTimeStamp() {
            DATE.setTime(System.currentTimeMillis());
            mLogBuffer.append(String.format("%02d:%02d:%02d ", DATE.getHours(), DATE.getMinutes(), DATE.getSeconds()));
            return this;
        }
        
        public FileLogger append (CharSequence csq) {
            mLogBuffer.append(csq);
            return this;
        }
        
        public FileLogger newline() {
            mLogBuffer.append(NL);
            return this;
        }
        
        @Override
        public void run() {
            File file = getLogFile();
            if (file == null) {
                return;
            }
            FileOutputStream fos = null;
            OutputStreamWriter writer = null;
            try {
                fos = new FileOutputStream(file, true);
                Charset charset = Charset.forName("UTF-8");
                CharsetEncoder enc = charset.newEncoder();
                writer = new OutputStreamWriter(fos, enc);
            } catch (FileNotFoundException e) {
                return;
            }
            
            int len = mLogBuffer.length();
            char[] buff = new char[len];
            mLogBuffer.getChars(0, len, buff, 0);
            try {
                writer.write(buff);
                mLogBuffer.delete(0, len);
            } catch (IOException e) {}
            
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) { }
            }
            
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) { }
            }
        }
        
        private synchronized File getLogFile() {
            if (mLogFile == null) {
                File file = getLogInFile(Environment.getExternalStorageDirectory());
                if (file == null) {
                   file =  getLogInFile(Environment.getDownloadCacheDirectory());
                }
                if (file == null) {
                    file =  getLogInFile(Environment.getDataDirectory());
                }
                mLogFile = file;
            }
            
            return mLogFile;
        }
        
        private static File getLogInFile(File dir) {
            File file = new File(dir, LOG_FILE);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    return null;
                }
            }
            return file;
        }
        
        private synchronized void flush() {
            mExecutorService.submit(this);
        }
    }
}
