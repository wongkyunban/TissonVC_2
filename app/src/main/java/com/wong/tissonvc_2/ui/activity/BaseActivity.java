package com.wong.tissonvc_2.ui.activity;

import android.app.Activity;
import android.os.Bundle;

import com.wong.tissonvc_2.ui.viewmanager.ActivityStackManager;


/**
 * The type Base activity.
 * <p/>
 * BaseActivity
 * Basics Activity
 */
public abstract class BaseActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        /**
         * Add a log to load the layout
         */
        ActivityStackManager.INSTANCE.push(this);
    }

    @Override
    public void finish()
    {
        if (ActivityStackManager.INSTANCE.getLastShowActivity() == this)
        {
            ActivityStackManager.INSTANCE.setLastShowActivity(null);
        }
        ActivityStackManager.INSTANCE.popup(this);
        super.finish();

    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy()
    {
        ActivityStackManager.INSTANCE.popup(this);
        super.onDestroy();
    }

    @Override
    public boolean moveTaskToBack(boolean nonRoot)
    {
        return super.moveTaskToBack(nonRoot);
    }

    /**
     * Empty Activity init data
     */
    public abstract void clearData();

}
