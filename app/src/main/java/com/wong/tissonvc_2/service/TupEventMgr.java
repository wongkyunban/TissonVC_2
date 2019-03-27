package com.wong.tissonvc_2.service;

import android.content.Context;

import com.wong.tissonvc_2.service.utils.TUPLogUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The type Tup event mgr.
 * <p/>
 * TupEventMgr
 * Tup Event Management Register
 * the appropriate callback event
 */
public final class TupEventMgr
{
    /**
     * The constant TAG.
     */
    private static final String TAG = TupEventMgr.class.getSimpleName();

    /**
     * The constant tupEventMgr.
     */
    private static TupEventMgr tupEventMgr = new TupEventMgr();

    /**
     * The constant tupNotifyList.
     */
    private static List<TupNotify> tupNotifyList = new ArrayList<TupNotify>();

    /**
     * The constant context.
     */
    private static Context context;

    /**
     * Instantiates a new Tup event mgr.
     */
    private TupEventMgr()
    {
    }

    /**
     * Gets tup service mgr.
     *
     * @return the tup service mgr
     */
    public static TupEventMgr getTupEventMgr()
    {
        return tupEventMgr;
    }

    /**
     * registerTupNotify
     * register callBack events
     *
     * @param tupNotify the tup notification
     */
    public static void registerTupNotify(TupNotify tupNotify)
    {
        if (tupNotify != null && !tupNotifyList.contains(tupNotify))
        {
            tupNotifyList.add(tupNotify);
        }
    }

    /**
     * Unregister tup notify.
     *
     * @param tupNotify the tup notification
     */
    public static void unregisterTupNotify(TupNotify tupNotify)
    {
        if (tupNotify != null)
        {
            tupNotifyList.remove(tupNotify);
        }
    }

    /**
     * clearTupNotify
     * clear callBack events
     */
    public static void clearTupNotify()
    {
        if (tupNotifyList != null)
        {
            tupNotifyList.clear();
        }
    }

    /**
     * Gets tup notification list.
     *
     * @return the tup notification list
     */
    public static List<TupNotify> getTupNotifyList()
    {
        return tupNotifyList;
    }

    /**
     * setTupContext
     *
     * @param tupContext the tup c ontext
     */
    public static void setTupContext(Context tupContext)
    {
        if (tupContext != null)
        {
            context = tupContext;
        }
    }

    /**
     * Gets tup context.
     *
     * @return the tup context
     */
    public static Context getTupContext()
    {
        return context;
    }

    /**
     * sendRegisterResult
     *
     * @param isSuccess the is success
     * @param errorCode the error code
     */
    public static void onRegisterEventNotify(int isSuccess, int errorCode)
    {
        if (tupNotifyList != null)
        {
            for (Iterator iterator = tupNotifyList.iterator(); iterator.hasNext(); )
            {
                TupNotify listener = (TupNotify) iterator.next();
                try
                {
                    listener.onRegisterNotify(isSuccess, errorCode);
                }
                catch (Exception exception)
                {
                    TUPLogUtil.e(TAG, "onRegisterNotify catch exception:" + exception.toString());
                }
            }
        }
    }

    /**
     * smc login notify.
     *
     * @param smcLoginResult the smc login result
     * @param errorReason    the error reason
     */
    public static void onSMCLoginNotify(int smcLoginResult, String errorReason)
    {
        if (tupNotifyList != null)
        {
            for (Iterator iterator = tupNotifyList.iterator(); iterator.hasNext(); )
            {
                TupNotify listener = (TupNotify) iterator.next();
                try
                {
                    listener.onSMCLogin(smcLoginResult, errorReason);
                }
                catch (Exception exception)
                {
                    TUPLogUtil.e(TAG, "onSMCLoginNotify catch exception:" + exception.toString());
                }
            }
        }
    }


    /**
     * onCallEventNotify
     *
     * @param callType the call type
     * @param object   the object
     */
    public static void onCallEventNotify(int callType, Object object)
    {
        if (tupNotifyList != null)
        {
            for (Iterator iterator = tupNotifyList.iterator(); iterator.hasNext(); )
            {
                TupNotify listener = (TupNotify) iterator.next();
                try
                {
                    listener.onCallNotify(callType, object);
                }
                catch (Exception exception)
                {
                    TUPLogUtil.i(TAG, "onCallEvent catch exception:" + exception.toString());
                }
            }
        }
    }

}
