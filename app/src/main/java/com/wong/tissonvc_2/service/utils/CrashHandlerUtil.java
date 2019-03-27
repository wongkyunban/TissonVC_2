package com.wong.tissonvc_2.service.utils;

import android.content.Context;
import android.os.Environment;
import android.os.Process;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * The type Crash handler util.
 */
public class CrashHandlerUtil implements Thread.UncaughtExceptionHandler
{
    /**
     * The constant TAG.
     */
    private static final String TAG = CrashHandlerUtil.class.getSimpleName();

    /**
     * The constant instance.
     */
    private static CrashHandlerUtil instance;

    /**
     * The Uncaught exception handler.
     */
    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

    /**
     * The M context.
     */
    private Context mContext;

    /**
     * The Format.
     */
    private SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd-HH");

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static CrashHandlerUtil getInstance()
    {
        if (null == instance)
        {
            instance = new CrashHandlerUtil();
        }
        return instance;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex)
    {
        if (!handlerException(ex) && uncaughtExceptionHandler != null)
        {
            uncaughtExceptionHandler.uncaughtException(thread, ex);
        }
        else
        {
            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            Process.killProcess(Process.myPid());
            System.exit(1);
        }
    }

    /**
     * Init.
     *
     * @param context the context
     */
    public void init(Context context)
    {
        mContext = context;
        uncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * Handler exception boolean.
     *
     * @param throwable the throwable
     * @return the boolean
     */
    private boolean handlerException(Throwable throwable)
    {
        if (null == throwable)
        {
            return false;
        }
        saveCrashInfoToFile(throwable);
        return true;
    }

    /**
     * Save crash info to file.
     *
     * @param ex the ex
     */
    private void saveCrashInfoToFile(Throwable ex)
    {
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);

        Throwable error = ex.getCause();
        while (null != error)
        {
            error.printStackTrace(printWriter);
            error = error.getCause();
        }
        printWriter.close();
        StringBuffer buffer = new StringBuffer();
        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS").format(new Date());
        String result = writer.toString();
        String toLogStr = currentTime + "\n" + result + "\r\n";
        buffer.append(toLogStr);

        try
        {
            String time = format.format(new Date());
            String fileName = "crash-" + time + ".txt";
            if (Environment.getExternalStorageDirectory().equals(Environment.MEDIA_MOUNTED)) ;
            {
                String path = Environment.getExternalStorageDirectory() + "/tupcrash";
                File dir = new File(path);
                if (!dir.exists())
                {
                    dir.mkdirs();
                }
                FileOutputStream fos = new FileOutputStream(path + fileName, true);
                fos.write(buffer.toString().getBytes("UTF-8"));
                fos.close();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
