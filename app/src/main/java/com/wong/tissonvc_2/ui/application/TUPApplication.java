package com.wong.tissonvc_2.ui.application;

import android.app.Application;
import android.os.Environment;

import com.huawei.application.BaseApp;
import com.wong.tissonvc_2.service.TupEventMgr;
import com.wong.tissonvc_2.service.call.CallService;
import com.wong.tissonvc_2.service.conf.ConferenceService;
import com.wong.tissonvc_2.service.contacts.ContactService;
import com.wong.tissonvc_2.service.login.LoginService;
import com.wong.tissonvc_2.service.utils.CrashHandlerUtil;
import com.wong.tissonvc_2.service.utils.TUPLogUtil;
import com.huawei.tup.TUPInterfaceService;

/**
 * The type Tup application.
 * <p/>
 * TUPApplication
 * TUP Application
 */
public class TUPApplication extends Application
{
    private static Application app;
    /**
     * Gets app.
     *
     * @return the app
     */
    public static Application getApplication()
    {
        return app;
    }

    private void setApplication(Application application)
    {
        app = application;
    }


    private TUPInterfaceService tupInterfaceService;

    /**
     * onCreate
     */
    @Override
    public void onCreate()
    {
        setApplication(this);
        TupEventMgr.setTupContext(app);
        CrashHandlerUtil.getInstance().init(app);
        BaseApp.setApp(this);

//        CallService.getInstance().tupCallInit();
//        tupInterfaceService = new TUPInterfaceService();
//        tupInterfaceService.StartUpService();
//        tupInterfaceService.SetAppPath(getApplicationInfo().dataDir + "/lib");
//        LoginService.getInstance().init(tupInterfaceService);
//        ConferenceService.getInstance().confInit(tupInterfaceService);
//
//        BaseApp.setApp(this);
//        ContactService.getInstance().setLogParam(3, 2 * 1024, 2
//                , Environment.getExternalStorageDirectory().toString() + "/VCLOG/contacts");

//        ContactService.getInstance().startServer();


        super.onCreate();
        TUPLogUtil.i("TUPApp", "onCreate.");
    }

    @Override
    public void onTerminate()
    {
        super.onTerminate();
        if (tupInterfaceService != null)
        {
            tupInterfaceService.ShutDownService();
        }
    }

    public void setTupInterfaceService(TUPInterfaceService tupInterfaceService) {
        this.tupInterfaceService = tupInterfaceService;
    }
}
