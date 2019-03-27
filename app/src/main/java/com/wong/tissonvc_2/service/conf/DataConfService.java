package com.wong.tissonvc_2.service.conf;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.wong.tissonvc_2.service.notify.VCDataConfNotify;
import com.wong.tissonvc_2.service.utils.TUPLogUtil;
import com.huawei.meeting.ConfDefines;
import com.huawei.meeting.ConfExtendMsg;
import com.huawei.meeting.ConfExtendUserInfoMsg;
import com.huawei.meeting.ConfGLView;
import com.huawei.meeting.ConfInfo;
import com.huawei.meeting.ConfInstance;
import com.huawei.meeting.ConfMsg;
import com.huawei.meeting.ConfOper;
import com.huawei.meeting.ConfResult;
import com.huawei.meeting.Conference;
import com.huawei.meeting.IConferenceUI;
import com.huawei.utils.StringUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

/**
 * The type Data conf service.
 */
public class DataConfService implements IConferenceUI
{
    /**
     * The constant TAG.
     */
    private static final String TAG = "DataConfService";

    /**
     * The constant CONF_NEW_VAL.
     */
    private static final int CONF_NEW_VAL = 99999;
    /**
     * The constant CONF_RELEASE_VAL.
     */
    private static final int CONF_RELEASE_VAL = 99998;
    /**
     * The constant CONF_HEARTBEAT.
     */
    private static final int CONF_HEARTBEAT = 99997;
    /**
     * The constant LEAVE_CONF.
     */
    private static final int LEAVE_CONF = 99989;
    /**
     * The constant UPDATE_DESKVIEW.
     */
    private static final int UPDATE_DESKVIEW = 99988;
    /**
     * The constant UPDATE_DOCUVIEW.
     */
    private static final int UPDATE_DOCUVIEW = 99987;
    /**
     * The constant RELEASE_DESKVIEW.
     */
    private static final int RELEASE_DESKVIEW = 99986;
    /**
     * The constant RELEASE_DOCUVIEW.
     */
    private static final int RELEASE_DOCUVIEW = 99985;
    /**
     * The constant UPDATE_WBVIEW.
     */
    private static final int UPDATE_WBVIEW = 99984;
    /**
     * The constant RELEASE_WBVIEW.
     */
    private static final int RELEASE_WBVIEW = 99983;

    /**
     * The constant instance.
     */
    private static DataConfService instance;

    /**
     * The Users.
     */
    private List<String> users = new ArrayList<>();
    /**
     * The User info map.
     */
    private Map<String, UserInfo> userInfoMap = new HashMap<>();

    /**
     * The User map.
     */
    private Map<String, String> userMap = new HashMap<>();


    /**
     * The Conf.
     */
    private ConfInstance conf;

    /**
     * The Component val.
     */
    private int componentVal = ConfDefines.IID_COMPONENT_BASE
            | ConfDefines.IID_COMPONENT_DS | ConfDefines.IID_COMPONENT_AS
            | ConfDefines.IID_COMPONENT_WB;

    /**
     * The Vc data conf notify.
     */
    private VCDataConfNotify vcDataConfNotify;

    /**
     * The Is data conf.
     */
    private boolean isDataConf = false;

    /**
     * Is data conf boolean.
     *
     * @return the boolean
     */
    public boolean isDataConf()
    {
        return isDataConf;
    }

    /**
     * Sets data conf.
     *
     * @param dataConf the data conf
     */
    public void setDataConf(boolean dataConf)
    {
        isDataConf = dataConf;
    }

    /**
     * remote shared desktop SurfaceView
     */
    private ConfGLView desktopSurfaceView;

    /**
     * remote shared doc SurfaceView
     */
    private ConfGLView docSurfaceView;

    /**
     * remote shared wb SurfaceView
     */
    private ConfGLView wbSurfaceView;

    /**
     * remote shared desktop ViewGroup
     */
    private ViewGroup mDesktopViewContainer;

    /**
     * remote shared doc ViewGroup
     */
    private ViewGroup mDocViewContainer;

    /**
     * remote shared wb ViewGroup
     */
    private ViewGroup mWbViewContainer;


    /**
     * The Wb current doc id.
     */
    private int wbCurrentDocID = 0;
    /**
     * The Wb current page id.
     */
    private int wbCurrentPageID = 0;
    /**
     * The Is wb load.
     */
    private boolean isWbLoad = false;

    /**
     * current shared doc counts
     */
    private int dscurrentDocCount = 0;

    /**
     * current shared doc ID
     */
    private int dscurrentDocID = 0;

    /**
     * current shared doc page
     */
    private int dscurrentPageID = 0;

    /**
     * The Update documsg.
     */
    private Message updateDocumsg;
    /**
     * The Update deskmsg.
     */
    private Message updateDeskmsg;
    /**
     * The Update wbmsg.
     */
    private Message updateWbmsg;


    /**
     * Instantiates a new Data conf service.
     */
    private DataConfService()
    {
        System.loadLibrary("TupConf");
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static synchronized DataConfService getInstance()
    {
        if (instance == null)
        {
            instance = new DataConfService();
        }
        return instance;
    }

    /**
     * Gets conf.
     *
     * @return the conf
     */
    public ConfInstance getConf()
    {
        return conf;
    }

    /**
     * Register vc data conf notify.
     *
     * @param notify the notify
     */
    public void registerVCDataConfNotify(VCDataConfNotify notify)
    {
        vcDataConfNotify = notify;
    }


    /**
     * heartbeat Timer
     */
    private Timer mytimer;

    /**
     * heartbeat Handler
     */
    private Handler mheartBeatHandler;

    /**
     * The Conf thread.
     */
    private WorkThread confThread;

    /**
     * The Conf thread start semaphore.
     */
    private Semaphore confThreadStartSemaphore;

    /**
     * The Mconf handler.
     */
    private Handler mconfHandler;

    /**
     * mainThread ID
     */
    private long mMainThreadID;

    /**
     * conf handle
     */
    private int confHandle = 0;

    /**
     * Init conf.
     */
    public void initConf()
    {
        mytimer = new Timer();
        mytimer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                Message m = new Message();
                m.what = 0;
                mheartBeatHandler.sendMessage(m);
            }
        }, 200, 100);

        mheartBeatHandler = new Handler()
        {
            public void handleMessage(Message msg)
            {
                heartBeat();
            }
        };

        mMainThreadID = Looper.getMainLooper().getThread().getId();
        confThreadStartSemaphore = new Semaphore(0);
        confThread = new WorkThread();
        confThread.start();
        confThreadStartSemaphore.acquireUninterruptibly();
        mconfHandler = confThread.getHandler();
        isDataConf = true;
    }

    /**
     * the method control operation  in mainThread
     */
    private class WorkThread extends Thread
    {
        /**
         * The M handler.
         */
        private Handler mHandler;

        public void run()
        {
            Looper.prepare();
            mHandler = new Handler()
            {
                public void handleMessage(Message msg)
                {
                    handleMsg(msg);
                }
            };
            confThreadStartSemaphore.release();
            Looper.loop();
        }

        /**
         * Gets handler.
         *
         * @return the handler
         */
        public Handler getHandler()
        {
            return mHandler;
        }
    }

    /**
     * {@inheritDoc}
     * set shared view Container
     *
     * @param context    the context
     * @param sharedView the shared view
     * @param sharedType the shared type
     */
    public void setSharedViewContainer(Context context, ViewGroup sharedView,
                                       int sharedType)
    {
        if (ConfDefines.IID_COMPONENT_AS == sharedType)
        {
            mDesktopViewContainer = sharedView;
            mDesktopViewContainer.removeAllViews();
            desktopSurfaceView = new ConfGLView(context);
            desktopSurfaceView.setConf(conf);
            desktopSurfaceView.setViewType(sharedType);
            mDesktopViewContainer.addView(desktopSurfaceView);
            desktopSurfaceView.onResume();
            desktopSurfaceView.setVisibility(View.VISIBLE);
        }
        else if (ConfDefines.IID_COMPONENT_DS == sharedType)
        {
            mDocViewContainer = sharedView;
            mDocViewContainer.removeAllViews();
            docSurfaceView = new ConfGLView(context);
            docSurfaceView.setConf(conf);
            docSurfaceView.setViewType(sharedType);
            mDocViewContainer.addView(docSurfaceView);
            docSurfaceView.onResume();
            docSurfaceView.setVisibility(View.VISIBLE);
        }
        else if (ConfDefines.IID_COMPONENT_WB == sharedType)
        {
            mWbViewContainer = sharedView;
            mWbViewContainer.removeAllViews();
            wbSurfaceView = new ConfGLView(context);
            wbSurfaceView.setConf(conf);
            wbSurfaceView.setViewType(sharedType);
            mWbViewContainer.addView(wbSurfaceView);
            wbSurfaceView.onResume();
            wbSurfaceView.setVisibility(View.VISIBLE);
        }
        else
        {
            Log.e(TAG, "setSharedViewContainer | sharedType = "
                    + sharedType + " not support type");
        }
    }


    /**
     * heartBeat
     */
    public void heartBeat()
    {
        if (isMainThread())
        {
            Message msg = new Message();
            msg.what = CONF_HEARTBEAT;
            mconfHandler.sendMessage(msg);
            return;
        }
        conf.confHeartBeat();
    }

    /**
     * {@inheritDoc}
     * kick out
     *
     * @param nUserID the n user id
     * @return boolean boolean
     */
    public boolean kickout(String nUserID)
    {
        if (isMainThread())
        {
            Message msg = new Message();
            msg.what = ConfOper.CONF_OPER_KICKOUT;
            msg.obj = nUserID;
            mconfHandler.sendMessage(msg);
            return true;
        }
        String uId = userMap.get(nUserID);
        int nRet = conf.confUserKickout(Long.parseLong(uId));
        return (nRet == 0);
    }

    /**
     * {@inheritDoc}
     * set role
     *
     * @param nUserID the n user id
     * @param nRole   the n role
     * @return role role
     */
    public boolean setRole(String nUserID, int nRole)
    {
        if (isMainThread())
        {
            Message msg = new Message();
            msg.what = ConfOper.CONF_OPER_SET_ROLE;
            msg.obj = nUserID;
            msg.arg2 = nRole;
            mconfHandler.sendMessage(msg);
            return true;
        }
        TUPLogUtil.i(TAG, "-----setRole:nUserID====" + nUserID);
        String uId = userMap.get(nUserID);
        TUPLogUtil.i(TAG, "-----setRole:uId====" + uId);
        int nRet = conf.confUserSetRole(Long.parseLong(uId), nRole);
        return (nRet == 0);
    }

    /**
     * Is main thread boolean.
     *
     * @return the boolean
     */
    private boolean isMainThread()
    {
        return Thread.currentThread().getId() == mMainThreadID;
    }


    /**
     * Handle msg.
     *
     * @param msg the msg
     */
    private void handleMsg(Message msg)
    {
        switch (msg.what)
        {
            case CONF_NEW_VAL:
                confNew((ConfInfo) msg.obj);
                break;
            case CONF_RELEASE_VAL:
                confRelease();
                break;
            case CONF_HEARTBEAT:
                heartBeat();
                break;
            case ConfOper.CONF_OPER_JOIN:
                joinConf();
                break;
            case ConfOper.CONF_OPER_LEAVE:
                leaveConf();
                break;
            case ConfOper.CONF_OPER_KICKOUT:
            {
                String nUserID = (String) msg.obj;
                kickout(nUserID);
            }
            break;
            case ConfOper.CONF_OPER_SET_ROLE:
            {
                String nUserID = (String) msg.obj;
                int nRole = msg.arg2;
                setRole(nUserID, nRole);
            }
            break;
            case ConfOper.CONF_OPER_LOAD_COMPONENT:
                loadComponent();
                break;
            case ConfOper.CONF_OPER_TERMINATE:
                terminateConf();
                break;
            default:
                break;
        }
    }


    /**
     * Init conf sdk.
     */
    private void initConfSDK()
    {
        String logFile = Environment.getExternalStorageDirectory().toString()
                + File.separator + "VCLOG/dataconf";
        File dirFile = new File(logFile);
        if (!(dirFile.exists()) && !(dirFile.isDirectory()))
        {
            if (dirFile.mkdir())
            {
                TUPLogUtil.i(TAG, "mkdir " + dirFile.getPath());
            }
        }
        Conference.getInstance().setLogLevel(3, 3);
        Conference.getInstance().setPath(logFile, logFile);
        Conference.getInstance().initSDK(false, 4);
    }


    /**
     * New conf.
     *
     * @param confInfo the conf info
     */
    private void newConf(ConfInfo confInfo)
    {
        initConfSDK();
        conf = new ConfInstance();
        conf.setConfUI(this);
        boolean ret = confNew(confInfo);
        TUPLogUtil.i(TAG, "newConf:" + ret);
    }


    /**
     * Join conf boolean.
     *
     * @param confInfo the conf info
     * @return the boolean
     */
    public boolean joinConf(ConfInfo confInfo)
    {
        newConf(confInfo);
        return joinConf();
    }


    /**
     * Conf new boolean.
     *
     * @param cinfo the cinfo
     * @return the boolean
     */
    private boolean confNew(ConfInfo cinfo)
    {
        if (isMainThread())
        {
            Message msg = new Message();
            msg.what = CONF_NEW_VAL;
            msg.obj = cinfo;
            mconfHandler.sendMessage(msg);
            return true;
        }

        boolean flag = conf.confNew(cinfo);
        if (flag)
        {
            confHandle = conf.getConfHandle();
        }
        return flag;
    }

    /**
     * Join conf boolean.
     *
     * @return the boolean
     */
    private boolean joinConf()
    {
        if (isMainThread())
        {
            Message msg = new Message();
            msg.what = ConfOper.CONF_OPER_JOIN;
            mconfHandler.sendMessage(msg);
            return true;
        }
        int nRet = conf.confJoin();
        TUPLogUtil.i(TAG, "joinConf:" + nRet);
        return (nRet == 0);
    }

    /**
     * Load component boolean.
     *
     * @return the boolean
     */
    private boolean loadComponent()
    {
        if (isMainThread())
        {
            Message msg = new Message();
            msg.what = ConfOper.CONF_OPER_LOAD_COMPONENT;
            mconfHandler.sendMessage(msg);
            return true;
        }

        int nRet = conf.confLoadComponent(componentVal);
        TUPLogUtil.i(TAG, "LoadComponent |  nRet = " + nRet);
        return (nRet == 0);
    }


    /**
     * Annot reg customer type int.
     *
     * @param compid the compid
     * @return the int
     */
    private int annotRegCustomerType(int compid)
    {
        return conf.annotRegCustomerType(compid);

    }

    /**
     * Annot init resource int.
     *
     * @param path the path
     * @param ciid the ciid
     * @return the int
     */
    public int annotInitResource(String path, int ciid)
    {
        return conf.annotInitResource(path, ciid);
    }


    /**
     * {@inheritDoc}
     * leave data conf
     *
     * @return boolean boolean
     */
    public boolean leaveConf()
    {
        if (isMainThread())
        {
            Message msg = new Message();
            msg.what = ConfOper.CONF_OPER_LEAVE;
            mconfHandler.sendMessage(msg);
            return true;
        }

        int nRet = conf.confLeave();
        TUPLogUtil.i(TAG, "leaveConf |  nRet = " + nRet);
        return (nRet == 0);
    }

    /**
     * terminate data conf
     *
     * @return the boolean
     */
    public boolean terminateConf()
    {
        if (isMainThread())
        {
            Message msg = new Message();
            msg.what = ConfOper.CONF_OPER_TERMINATE;
            mconfHandler.sendMessage(msg);
            return true;
        }
        int nRet = conf.confTerminate();
        TUPLogUtil.i(TAG, "TerminateConf |  nRet = " + nRet);
        return (nRet == 0);
    }

    /**
     * release data conf
     */
    private void confRelease()
    {
        if (isMainThread())
        {
            Message msg = new Message();
            msg.what = CONF_RELEASE_VAL;
            mconfHandler.sendMessage(msg);
            return;
        }
        conf.confRelease();
        confHandle = 0;
        users.clear();
        userInfoMap.clear();
        userMap.clear();
        isDataConf = false;
        isWbLoad = false;
        TUPLogUtil.i(TAG, "confRelease");
        mMainThreadID = 0;
        if (confThread != null)
        {
            confThread.getHandler().getLooper().quit();
            confThread.interrupt();
            confThread = null;
        }
    }


    /**
     * doc share notify
     *
     * @param msg       the msg
     * @param extendMsg the extend msg
     */
    private void confMsgNotifyDs(ConfMsg msg, ConfExtendMsg extendMsg)
    {
        int msgType = msg.getMsgType();
        int nValue1 = msg.getnValue1();
        int nValue2 = (int) msg.getnValue2();

        switch (msgType)
        {
            case ConfMsg.COMPT_MSG_DS_ON_DOC_NEW:
                TUPLogUtil.i(TAG, "COMPT_MSG_DS_ON_DOC_NEW");
                if (dscurrentDocCount == 0)
                {
                    TUPLogUtil.i(TAG, "DATA_SHARE_START_EVENT doc");
                    vcDataConfNotify.onDataShareResult(ConfDefines.IID_COMPONENT_DS, 1);
                }
                updateDocumsg = new Message();
                updateDocumsg.what = UPDATE_DOCUVIEW;
                videoHandler.sendMessage(updateDocumsg);
                break;
            case ConfMsg.COMPT_MSG_DS_PAGE_DATA_DOWNLOAD:
                TUPLogUtil.i(TAG, "COMPT_MSG_DS_PAGE_DATA_DOWNLOAD");
                if ((nValue1 == dscurrentDocID)
                        && ((int) nValue2 == dscurrentPageID))
                {
                    updateDocumsg = new Message();
                    updateDocumsg.what = UPDATE_DOCUVIEW;
                    videoHandler.sendMessage(updateDocumsg);
                }
                break;
            case ConfMsg.COMPT_MSG_DS_ON_CURRENT_PAGE_IND:
                TUPLogUtil.i(TAG, "COMPT_MSG_DS_ON_CURRENT_PAGE_IND");
                if (nValue1 != 0) // nValue1：docID
                {
                    dscurrentDocID = nValue1;
                    dscurrentPageID = (int) nValue2;
                    dsSetcurrentpage(nValue1, (int) nValue2);
                }
                updateDocumsg = new Message();
                updateDocumsg.what = UPDATE_DOCUVIEW;
                videoHandler.sendMessage(updateDocumsg);
                break;
            case ConfMsg.COMPT_MSG_DS_ANDROID_DOC_COUNT:
                TUPLogUtil.i(TAG, "COMPT_MSG_DS_ANDROID_DOC_COUNT");
                dscurrentDocCount = nValue1;
                if (nValue1 == 0) // doc share end
                {
                    Message releaseDocumsg = new Message();
                    releaseDocumsg.what = RELEASE_DOCUVIEW;
                    videoHandler.sendMessage(releaseDocumsg);
                    vcDataConfNotify.onDataShareResult(ConfDefines.IID_COMPONENT_DS, 0);
                    TUPLogUtil.i(TAG, "RELEASE_DOCUVIEW doc");
                }
                break;
            default:
                if (msgType == ConfMsg.COMPT_MSG_DS_ON_DOC_DEL
                        || msgType == ConfMsg.COMPT_MSG_DS_ON_PAGE_NEW
                        || msgType == ConfMsg.COMPT_MSG_DS_ON_CURRENT_PAGE
                        || msgType == ConfMsg.COMPT_MSG_DS_ON_DRAW_DATA_NOTIFY)
                {
                    updateDocumsg = new Message();
                    updateDocumsg.what = UPDATE_DOCUVIEW;
                    videoHandler.sendMessage(updateDocumsg);
                    if (msgType == ConfMsg.COMPT_MSG_DS_ON_DRAW_DATA_NOTIFY)
                    {
                        updateDeskmsg = new Message();
                        updateDeskmsg.what = UPDATE_DESKVIEW;
                        videoHandler.sendMessage(updateDeskmsg);
                        TUPLogUtil.i(TAG, "Ds Share: COMPT_MSG_DS_ON_DRAW_DATA_NOTIFY");
                    }
                }
                break;
        }

    }

    /**
     * {@inheritDoc}
     * doc share —— set current page
     *
     * @param nDocID  the n doc id
     * @param nPageID the n page id
     * @return boolean boolean
     */
    private boolean dsSetcurrentpage(int nDocID, int nPageID)
    {
        if (isMainThread())
        {
            Message msg = new Message();
            msg.what = ConfOper.DS_OPER_SET_CURRENTPAGE;
            msg.arg1 = nDocID;
            msg.arg2 = nPageID;
            mconfHandler.sendMessage(msg);
            return true;
        }
        int nRet = conf.dsSetCurrentPage(nDocID, nPageID);
        TUPLogUtil.i(TAG, "dsSetcurrentpage |  nRet = " + nRet);
        return (nRet == 0);
    }


    @Override
    public void confMsgNotify(ConfMsg msg, ConfExtendMsg extendMsg)
    {
        long nValue1 = msg.getnValue1();
        long nValue2 = msg.getnValue2();
        int msgType = msg.getMsgType();

        TUPLogUtil.i(TAG, "msgType = " + msgType + " , nValue1 = " + nValue1
                + " , nValue2 = " + nValue2);
        switch (msgType)
        {
            case ConfMsg.CONF_MSG_ON_CONFERENCE_JOIN:
                TUPLogUtil.i(TAG, "CONF_MSG_ON_CONFERENCE_JOIN |  nRet = " + nValue1);
                if (nValue1 == ConfResult.TC_OK)
                {
                    loadComponent();
                }
                break;
            case ConfMsg.CONF_MSG_USER_ON_ENTER_IND:
            {
                ConfExtendUserInfoMsg infoMsg = (ConfExtendUserInfoMsg) extendMsg;
                long userId = infoMsg.getUserid();
                Log.e(TAG, "--------CONF_MSG_USER_ON_ENTER_IND userId= " + userId);
                onUserEnter((ConfExtendUserInfoMsg) extendMsg);
            }

            case ConfMsg.CONF_MSG_ON_COMPONENT_LOAD:
                String bmpPath = Environment.getExternalStorageDirectory() + File.separator + "annotImages";
                switch ((int) nValue2)
                {
                    case ConfDefines.IID_COMPONENT_DS:
                        annotRegCustomerType(ConfDefines.IID_COMPONENT_DS);
                        annotInitResource(bmpPath,
                                ConfDefines.IID_COMPONENT_DS);
                        TUPLogUtil.i(TAG, "loadComponent DS ");
                        break;
                    case ConfDefines.IID_COMPONENT_AS:
                        TUPLogUtil.i(TAG, "loadComponent AS ");
                        annotRegCustomerType(ConfDefines.IID_COMPONENT_AS);
                        annotInitResource(bmpPath,
                                ConfDefines.IID_COMPONENT_AS);
                        break;
                    case ConfDefines.IID_COMPONENT_WB:
                        TUPLogUtil.i(TAG, "loadComponent WB ");
                        annotRegCustomerType(ConfDefines.IID_COMPONENT_WB);
                        annotInitResource(bmpPath,
                                ConfDefines.IID_COMPONENT_WB);
                        break;
                    default:
                        break;
                }
                break;

            case ConfMsg.CONF_MSG_USER_ON_LEAVE_IND:
            {
                ConfExtendUserInfoMsg infoMsg = (ConfExtendUserInfoMsg) extendMsg;
                long userId = infoMsg.getUserid();
                TUPLogUtil.i(TAG, userId + "  leave dataconf");
                onUserLeave((ConfExtendUserInfoMsg) extendMsg);
            }
            break;
            case ConfMsg.CONF_MSG_ON_CONFERENCE_LEAVE:
                leaveConf();
                break;

            case ConfMsg.COMPT_MSG_AS_ON_SCREEN_DATA:
            case ConfMsg.COMPT_MSG_AS_ON_SHARING_SESSION:
            case ConfMsg.COMPT_MSG_AS_ON_SCREEN_SIZE:
            case ConfMsg.COMPT_MSG_AS_ON_SHARING_STATE:
                confMsgNotifyAs(msg, extendMsg);
                break;
            case ConfMsg.COMPT_MSG_DS_ANDROID_DOC_COUNT:
            case ConfMsg.COMPT_MSG_DS_ON_DOC_NEW:
            case ConfMsg.COMPT_MSG_DS_ON_DOC_DEL:
            case ConfMsg.COMPT_MSG_DS_ON_PAGE_NEW:
            case ConfMsg.COMPT_MSG_DS_ON_PAGE_DEL:
            case ConfMsg.COMPT_MSG_DS_ON_CURRENT_PAGE:
            case ConfMsg.COMPT_MSG_DS_ON_DRAW_DATA_NOTIFY:
            case ConfMsg.COMPT_MSG_DS_PAGE_DATA_DOWNLOAD:
            case ConfMsg.COMPT_MSG_DS_ON_CURRENT_PAGE_IND:
                confMsgNotifyDs(msg, extendMsg);
                break;
            case ConfMsg.COMPT_MSG_WB_ON_DOC_NEW:
            case ConfMsg.COMPT_MSG_WB_ON_DOC_DEL:
            case ConfMsg.COMPT_MSG_WB_XML_ON_NEW_DOC:
            case ConfMsg.COMPT_MSG_WB_ON_PAGE_NEW:
            case ConfMsg.COMPT_MSG_WB_ON_PAGE_DEL:
            case ConfMsg.COMPT_MSG_WB_ON_CURRENT_PAGE:
            case ConfMsg.COMPT_MSG_WB_ON_CURRENT_PAGE_IND:
            case ConfMsg.COMPT_MSG_WB_ON_DRAW_DATA_NOTIFY:
                confMsgNotifyWb(msg, extendMsg);
                break;
            case ConfMsg.CONF_MSG_USER_ON_HOST_CHANGE_IND:
                Log.e(TAG, "--------CONF_MSG_USER_ON_HOST_CHANGE_IND");
                updateHostInformation(nValue1, nValue2);
                break;
            case ConfMsg.CONF_MSG_USER_ON_PRESENTER_CHANGE_IND:
                Log.e(TAG, "--------CONF_MSG_USER_ON_PRESENTER_CHANGE_IND");
                updateSpeakerInformation(nValue1, nValue2);
                break;

            case ConfMsg.CONF_MSG_USER_ON_HOST_GIVE_CFM:
                break;
            case ConfMsg.CONF_MSG_USER_ON_HOST_CHANGE_CFM:
            case ConfMsg.CONF_MSG_USER_ON_HOST_GIVE_IND:
            case ConfMsg.CONF_MSG_USER_ON_PRESENTER_CHANGE_CFM:
            case ConfMsg.CONF_MSG_USER_ON_PRESENTER_GIVE_IND:
            case ConfMsg.CONF_MSG_USER_ON_PRESENTER_GIVE_CFM:
                TUPLogUtil.e(TAG, "setRole->" + msgType);
                break;

            default:
                break;

        }
    }


    /**
     * On user leave.
     *
     * @param extendMsg the extend msg
     */
    private void onUserLeave(ConfExtendUserInfoMsg extendMsg)
    {
        ConfExtendUserInfoMsg leaveInfoMsg = extendMsg;
        String leaveUserId = leaveInfoMsg.getUserid() + "";
        String leaveUserName = leaveInfoMsg.getUserName();
        TUPLogUtil.i(TAG, "----leaveUserId=" + leaveUserId + ",leaveUserName=" + leaveUserName);
        removeUser(leaveUserId);
        removeUserInfo(leaveUserId);

        if (leaveUserName != null && userMap.containsKey(leaveUserName))
        {
            userMap.remove(leaveUserName);
        }
    }

    /**
     * On user enter.
     *
     * @param extendMsg the extend msg
     */
    private void onUserEnter(ConfExtendUserInfoMsg extendMsg)
    {
        ConfExtendUserInfoMsg enterInfoMsg = extendMsg;
        String enterUserId = enterInfoMsg.getUserid() + "";
        String enterUserName = enterInfoMsg.getUserName();
        int userRole = enterInfoMsg.getUserRole();
        TUPLogUtil.i(TAG, "-----enterUserId=" + enterUserId + ",enterUserName=" + enterUserName
                + ",userRole=" + userRole);

        if (!userMap.containsKey(enterUserName))
        {
            userMap.put(enterUserName, enterUserId);
        }
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(enterUserId);
        userInfo.setUserName(enterUserName);
        switch (userRole)
        {
            case 1:
                userInfo.setHost(true);
                userInfo.setSpeaker(false);
                break;
            case 2:
                userInfo.setSpeaker(true);
                userInfo.setHost(false);
                break;
            case 3:
                userInfo.setSpeaker(true);
                userInfo.setHost(true);
                break;
            case 8:
                userInfo.setSpeaker(false);
                userInfo.setHost(false);
                break;
            default:
                break;
        }
        addUserInfo(userInfo);
        addUser(enterUserId);
    }

    /**
     * Add user.
     *
     * @param userId the user id
     */
    public void addUser(String userId)
    {
        if (!users.contains(userId) && StringUtil.isNotEmpty(userId))
        {
            users.add(userId);
        }
    }

    /**
     * Remove user.
     *
     * @param userId the user id
     */
    public void removeUser(String userId)
    {
        if (users.contains(userId) && StringUtil.isNotEmpty(userId))
        {
            users.remove(userId);
        }
    }

    /**
     * Update host information.
     *
     * @param oldHost the old host
     * @param newHost the new host
     */
    private void updateHostInformation(long oldHost, long newHost)
    {
        String oldHostNumber = String.valueOf(oldHost);
        String newHostNumber = String.valueOf(newHost);
        TUPLogUtil.i(TAG, "oldHostNumber->" + oldHostNumber);
        TUPLogUtil.i(TAG, "newHostNumber->" + newHostNumber);

        if (users.contains(oldHostNumber))
        {
            UserInfo oldHostUserInfo = getUserInfo(oldHostNumber);
            oldHostUserInfo.setHost(false);
            addUserInfo(oldHostUserInfo);
        }

        if (users.contains(newHostNumber))
        {
            UserInfo newHostUserInfo = getUserInfo(newHostNumber);
            newHostUserInfo.setHost(true);
            addUserInfo(newHostUserInfo);
        }
    }


    /**
     * Update speaker information.
     *
     * @param oldSpeaker the old speaker
     * @param newSpeaker the new speaker
     */
    private void updateSpeakerInformation(long oldSpeaker, long newSpeaker)
    {
        String oldSpeakerNumber = String.valueOf(oldSpeaker);
        String newSpeakerNumber = String.valueOf(newSpeaker);
        TUPLogUtil.i(TAG, "----oldSpeakerNumber->" + oldSpeakerNumber);
        TUPLogUtil.i(TAG, "----newSpeakerNumber->" + newSpeakerNumber);

        if (users.contains(oldSpeakerNumber))
        {
            UserInfo oldSpeakerUserInfo = getUserInfo(oldSpeakerNumber);
            oldSpeakerUserInfo.setSpeaker(false);
            addUserInfo(oldSpeakerUserInfo);
        }

        if (users.contains(newSpeakerNumber))
        {
            UserInfo newSpeakerUserInfo = getUserInfo(newSpeakerNumber);
            newSpeakerUserInfo.setSpeaker(true);
            addUserInfo(newSpeakerUserInfo);
        }
    }


    /**
     * Gets user info.
     *
     * @param userId the user id
     * @return the user info
     */
    public UserInfo getUserInfo(String userId)
    {
        return userInfoMap.get(userId);
    }


    /**
     * Add user map.
     *
     * @param userInfo the user info
     * @return the map
     */
    public Map<String, UserInfo> addUserInfo(UserInfo userInfo)
    {
        String userId = userInfo.getUserId();
        if (!userInfoMap.containsKey(userId))
        {
            users.add(userId);
        }
        userInfoMap.put(userInfo.getUserId(), userInfo);
        return userInfoMap;
    }


    /**
     * Remove userInfo.
     *
     * @param userId the user id
     */
    public void removeUserInfo(String userId)
    {
        if (userInfoMap.containsKey(userId) && StringUtil.isNotEmpty(userId))
        {
            userInfoMap.remove(userId);
        }
    }


    /**
     * Conf msg notify wb.
     *
     * @param msg       the msg
     * @param extendMsg the extend msg
     */
    private void confMsgNotifyWb(ConfMsg msg, ConfExtendMsg extendMsg)
    {
        int msgType = msg.getMsgType();
        switch (msgType)
        {
            case ConfMsg.COMPT_MSG_WB_XML_ON_NEW_DOC:
            case ConfMsg.COMPT_MSG_WB_ON_PAGE_DEL:
            case ConfMsg.COMPT_MSG_WB_ON_CURRENT_PAGE:
            case ConfMsg.COMPT_MSG_WB_ON_DRAW_DATA_NOTIFY:
                updateWbmsg = new Message();
                updateWbmsg.what = UPDATE_WBVIEW;
                videoHandler.sendMessage(updateWbmsg);
                TUPLogUtil.i(TAG, "WB UPDATE_DESKVIEW");
                break;

            case ConfMsg.COMPT_MSG_WB_ON_PAGE_NEW:
                wbCurrentDocID = msg.getnValue1();
                wbCurrentPageID = (int) msg.getnValue2();
                conf.wbSetCurrentPage(wbCurrentDocID, wbCurrentPageID);
                updateWbmsg = new Message();
                updateWbmsg.what = UPDATE_WBVIEW;
                videoHandler.sendMessage(updateWbmsg);
                TUPLogUtil.i(TAG, "COMPT_MSG_WB_ON_PAGE_NEW");
                break;

            case ConfMsg.COMPT_MSG_WB_ON_DOC_NEW:
                vcDataConfNotify.onDataShareResult(ConfDefines.IID_COMPONENT_WB, 512);
                TUPLogUtil.i(TAG, "COMPT_MSG_WB_ON_DOC_NEW");
                isWbLoad = true;
                break;

            case ConfMsg.COMPT_MSG_WB_ON_CURRENT_PAGE_IND:
                TUPLogUtil.i(TAG, "COMPT_MSG_WB_ON_CURRENT_PAGE_IND");
                if (isWbLoad)
                {
                    vcDataConfNotify.onDataShareResult(ConfDefines.IID_COMPONENT_WB, 512);
                }
                if (msg.getnValue1() != 0)
                {
                    wbCurrentDocID = msg.getnValue1();
                    wbCurrentPageID = (int) msg.getnValue2();
                    conf.wbSetCurrentPage(wbCurrentDocID, wbCurrentPageID);
                    updateWbmsg = new Message();
                    updateWbmsg.what = UPDATE_WBVIEW;
                    videoHandler.sendMessage(updateWbmsg);
                }
                else
                {
                    updateWbmsg = new Message();
                    updateWbmsg.what = UPDATE_WBVIEW;
                    videoHandler.sendMessage(updateWbmsg);
                }
                break;

            case ConfMsg.COMPT_MSG_WB_ON_DOC_DEL:
                TUPLogUtil.i(TAG, "COMPT_MSG_WB_ON_DOC_DEL");
                Message releaseWbmsg = new Message();
                releaseWbmsg.what = RELEASE_WBVIEW;
                videoHandler.sendMessage(releaseWbmsg);

                isWbLoad = false;
                break;

            default:
                break;
        }
    }


    /**
     * The Video handler.
     */
    final Handler videoHandler = new Handler(Looper.getMainLooper())
    {
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case LEAVE_CONF:
                    leaveConf();
                    confRelease();
                    releaseConf();
                    break;
                case UPDATE_DOCUVIEW:
                    updateDocSharedView();
                    break;
                case UPDATE_DESKVIEW:
                    updateDesktopSharedView();
                    break;
                case RELEASE_DOCUVIEW:
                    releaseDocShareView();
                    break;
                case RELEASE_DESKVIEW:
                    releaseDesktopShareView();
                    break;
                case UPDATE_WBVIEW:
                    updateWbSharedView();
                    break;
                case RELEASE_WBVIEW:
                    releaseWbShareView();
                    break;
                default:
                    break;
            }

        }
    };


    /**
     * Release conf.
     */
    public void releaseConf()
    {
        if (mytimer != null)
        {
            mytimer.cancel();
            mytimer = null;
        }
        if (confThreadStartSemaphore != null)
        {
            confThreadStartSemaphore.release();
            confThreadStartSemaphore = null;
        }
        releaseShareView();
    }

    /**
     * release shared view
     */
    private void releaseShareView()
    {
        releaseDesktopShareView();
        releaseDocShareView();
        releaseWbShareView();
    }

    /**
     * release desktop shared view
     */
    private void releaseDesktopShareView()
    {
        if (desktopSurfaceView != null && mDesktopViewContainer != null)
        {
            desktopSurfaceView.onPause();
            mDesktopViewContainer.removeView(desktopSurfaceView);
            mDesktopViewContainer.removeAllViews();
            mDesktopViewContainer.invalidate();
            desktopSurfaceView = null;
        }
    }

    /**
     * release desktop shared view
     */
    private void releaseWbShareView()
    {
        if (wbSurfaceView != null && mWbViewContainer != null)
        {
            wbSurfaceView.onPause();
            mWbViewContainer.removeView(wbSurfaceView);
            mWbViewContainer.removeAllViews();
            mWbViewContainer.invalidate();
            wbSurfaceView = null;
        }
        wbCurrentDocID = 0;
        wbCurrentPageID = 0;
    }


    /**
     * release dcc shared view
     */
    private void releaseDocShareView()
    {
        if (docSurfaceView != null && mDocViewContainer != null)
        {
            docSurfaceView.onPause();
            mDocViewContainer.removeView(docSurfaceView);
            mDocViewContainer.removeAllViews();
            mDocViewContainer.invalidate();
            docSurfaceView = null;
        }

        dscurrentDocCount = 0;
        dscurrentDocID = 0;
        dscurrentPageID = 0;
    }

    /**
     * update doc shared view
     */
    private void updateDocSharedView()
    {
        if (docSurfaceView != null)
        {
            docSurfaceView.update();
        }
    }

    /**
     * update desktop shared view
     */
    private void updateDesktopSharedView()
    {
        if (desktopSurfaceView != null)
        {
            desktopSurfaceView.update();
        }
    }

    /**
     * update desktop shared view
     */
    private void updateWbSharedView()
    {
        if (wbSurfaceView != null)
        {
            wbSurfaceView.update();
        }
    }


    /**
     * Conf msg notify as.
     *
     * @param msg       the msg
     * @param extendMsg the extend msg
     */
    private void confMsgNotifyAs(ConfMsg msg, ConfExtendMsg extendMsg)
    {
        int msgType = msg.getMsgType();
        int nValue1 = msg.getnValue1();
        int nValue2 = (int) msg.getnValue2();

        switch (msgType)
        {
            case ConfMsg.COMPT_MSG_AS_ON_SHARING_SESSION:
                TUPLogUtil.i(TAG, "COMPT_MSG_AS_ON_SHARING_SESSION");
                if (nValue1 == ConfDefines.AS_SESSION_OWNER)
                {
                    if (nValue2 == ConfDefines.AS_ACTION_ADD
                            || nValue2 == ConfDefines.AS_ACTION_MODIFY)
                    {
                        vcDataConfNotify.onDataShareResult(ConfDefines.IID_COMPONENT_AS, 1);
                        TUPLogUtil.i(TAG, "DATA_SHARE_START_EVENT AS");
                    }
                }
                updateDeskmsg = new Message();
                updateDeskmsg.what = UPDATE_DESKVIEW;
                videoHandler.sendMessage(updateDeskmsg);
                break;
            case ConfMsg.COMPT_MSG_AS_ON_SHARING_STATE:
                TUPLogUtil.i(TAG, "COMPT_MSG_AS_ON_SHARING_STATE");
                if (nValue2 == ConfDefines.AS_STATE_NULL)
                {
                    Message releaseDeskmsg = new Message();
                    releaseDeskmsg.what = RELEASE_DESKVIEW;
                    videoHandler.sendMessage(releaseDeskmsg);
                    TUPLogUtil.i(TAG, "RELEASE_DESKVIEW");
                    vcDataConfNotify.onDataShareResult(ConfDefines.IID_COMPONENT_AS, 0);
                }
                updateDeskmsg = new Message();
                updateDeskmsg.what = UPDATE_DESKVIEW;
                videoHandler.sendMessage(updateDeskmsg);
                break;
            default:
                if (msgType == ConfMsg.COMPT_MSG_AS_ON_SCREEN_SIZE
                        || msgType == ConfMsg.COMPT_MSG_AS_ON_SCREEN_DATA)
                {
                    updateDeskmsg = new Message();
                    updateDeskmsg.what = UPDATE_DESKVIEW;
                    videoHandler.sendMessage(updateDeskmsg);
                }
                break;
        }

    }


}
