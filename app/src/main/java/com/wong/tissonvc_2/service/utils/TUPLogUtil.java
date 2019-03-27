package com.wong.tissonvc_2.service.utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The type Tup log util.
 * <p/>
 * TUPLogUtil
 * Record tup log,
 * Written in the SD card file
 */
public final class TUPLogUtil
{

    /**
     * The constant CHARSET_UTF_8.
     */
    public static final String CHARSET_UTF_8 = "UTF-8";
    /**
     * The constant TUP_LOG.
     */
    public static final String TUP_LOG = "VCLOG";
    /**
     * The constant TUP_LOG_FILE_NAME.
     */
    public static final String TUP_LOG_FILE_NAME = "TUPLocal.log";
    /**
     * The constant TAG.
     */
    private static final String TAG = "TUPLogUtil";
    /**
     * The constant isLog.
     */
    private static boolean isLog = true;
    /**
     * The constant format.
     */
    private static String format = "yyyy-MM-dd HH:mm:ss.SSS";
    /**
     * The constant logFileSize.
     */
    private static double logFileSize = 1024.00 * 5;

    /**
     * Instantiates a new Tup log util.
     */
    private TUPLogUtil()
    {
    }

    /**
     * log debug.
     *
     * @param msg the msg
     */
    public static void d(String msg)
    {
        if (isLog)
        {
            writeLog("debug" + "-" + msg);
            Log.d(TAG, " " + msg);
        }
    }

    /**
     * log debug.
     *
     * @param tag the tag
     * @param msg the msg
     */
    public static void d(String tag, String msg)
    {
        if (isLog)
        {
            Log.d(TAG, tag + " " + msg);
            writeLog("debug" + "-" + getTagName(tag) + " : " + msg);
        }
    }

    /**
     * log info.
     *
     * @param tag the tag
     * @param msg the msg
     */
    public static void i(String tag, String msg)
    {
        if (isLog)
        {
            Log.i(TAG, tag + " " + msg);
            writeLog("info" + "-" + getTagName(tag) + " : " + msg);

        }

    }

    /**
     * log error.
     *
     * @param tag the tag
     * @param msg the msg
     */
    public static void e(String tag, String msg)
    {
        if (isLog)
        {
            Log.e(TAG, tag + " " + msg);
            writeLog("error" + "-" + getTagName(tag) + " : " + msg);
        }

    }

    /**
     * Gets tag name.
     *
     * @param tag the tag
     * @return the tag name
     */
    private static String getTagName(String tag)
    {
        return tag == null ? TUP_LOG : tag;
    }

    /**
     * Write log.
     *
     * @param logText the log text
     */
    private static void writeLog(String logText)
    {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            return;
        }

        String nowTimeStr = String.format("[%s]", new SimpleDateFormat(format).format(new Date()));
        String toLogStr = nowTimeStr + " " + logText;
        toLogStr += "\r\n";

        FileOutputStream fileOutputStream = null;
        String logFile = Environment.getExternalStorageDirectory().toString() + "/" + TUP_LOG;
        String filename = TUP_LOG_FILE_NAME;
        try
        {

            File fileOld = new File(logFile + "/" + filename);
            if ((float) ((fileOld.length() + logText.length()) / 1024.00) > logFileSize)
            {
                File bakFile = new File(fileOld.getPath() + ".bak");
                if (bakFile.exists())
                {
                    if (bakFile.delete())
                    {
                        Log.d("Write Log", "delete " + bakFile.getName());
                    }
                }
                if (fileOld.renameTo(bakFile))
                {
                    Log.d("Write Log", fileOld.getName() + " rename to " + bakFile.getName());
                }
            }

            File file = new File(logFile);
            if (!file.exists())
            {
                if (file.mkdir())
                {
                    Log.d("Write Log", "create " + file.getName());
                }
            }

            File filepath = new File(logFile + "/" + filename);
            if (!filepath.exists())
            {
                if (filepath.createNewFile())
                {
                    Log.d("Write Log", "create " + filepath.getName());
                }
            }
            fileOutputStream = new FileOutputStream(filepath, true);

            byte[] buffer = toLogStr.getBytes(CHARSET_UTF_8);

            fileOutputStream.write(buffer);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (fileOutputStream != null)
            {
                try
                {
                    fileOutputStream.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

}
