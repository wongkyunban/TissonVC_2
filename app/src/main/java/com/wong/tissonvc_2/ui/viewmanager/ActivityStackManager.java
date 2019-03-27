package com.wong.tissonvc_2.ui.viewmanager;

import android.app.Activity;

import com.wong.tissonvc_2.service.utils.TUPLogUtil;
import com.wong.tissonvc_2.ui.activity.BaseActivity;

import java.util.Stack;

/**
 * The type Activity stack manager.
 * <p/>
 * ActivityStackManager
 * activity manager class
 */
public final class ActivityStackManager
{
    /**
     * The constant INSTANCE.
     */
    public static final ActivityStackManager INSTANCE = new ActivityStackManager();

    private static final String TAG = ActivityStackManager.class.getSimpleName();

    private Stack<BaseActivity> activityStack;

    private BaseActivity lastShowActivity;

    private ActivityStackManager()
    {
        activityStack = new Stack<BaseActivity>();
    }

    /**
     * Push.
     *
     * @param activity the activity
     */
    public void push(BaseActivity activity)
    {
        if (activity != null)
        {
            TUPLogUtil.i(TAG, "ActivityTask  push :" + activity.toString());
            activityStack.push(activity);
        }
    }

    /**
     * finishAllViewInTask
     */
    public void finishAllViewInTask()
    {
        final int size = activityStack.size();
        BaseActivity temp = null;
        for (int i = 0; i < size; i++)
        {
            temp = activityStack.pop();
            if (temp != null)
            {
                TUPLogUtil.i(TAG, "finishAllViewInTask() activity : " + temp.toString());
                temp.clearData();
                temp.finish();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        }
    }

    /**
     * Popup boolean.
     *
     * @param curAc the cur ac
     * @return the boolean
     */
    public boolean popup(Activity curAc)
    {
        if (curAc != null)
        {
            boolean reg = activityStack.removeElement(curAc);
            TUPLogUtil.i(TAG, "ActivityTask  remove :" + curAc.toString() + " , result = " + reg);
            return reg;
        }
        return false;
    }

    /**
     * Gets last show activity.
     *
     * @return the last show activity
     */
    public BaseActivity getLastShowActivity()
    {
        return lastShowActivity;
    }

    /**
     * Sets last show activity.
     *
     * @param lastShowActivity the last show activity
     */
    public void setLastShowActivity(BaseActivity lastShowActivity)
    {
        this.lastShowActivity = lastShowActivity;
    }

}
