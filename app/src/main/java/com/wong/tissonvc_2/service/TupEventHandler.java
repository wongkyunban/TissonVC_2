package com.wong.tissonvc_2.service;


import android.util.Log;

import com.wong.tissonvc_2.service.conf.ConferenceService;
import com.wong.tissonvc_2.service.contacts.ContactService;
import com.wong.tissonvc_2.service.login.LoginService;
import com.wong.tissonvc_2.service.utils.TUPLogUtil;
import com.huawei.tup.confctrl.ConfctrlConfEnvType;
import com.huawei.tup.confctrl.sdk.TupConfAccessInfo;
import com.huawei.tupcontacts.TupLdapContactsCfg;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import common.EUAType;
import common.TupCallBackBaseNotify;
import object.Conf;
import object.DataConfParam;
import object.DecodeSuccessInfo;
import object.KickOutInfo;
import object.NetAddress;
import object.OnLineState;
import object.TupRegisterResult;
import tupsdk.TupCall;

/**
 * The type Tup event handler.
 * <p/>
 * TupEventHandler
 * Tup Call Event handle and
 * distribute Callback Events handle
 */
public class TupEventHandler extends TupCallBackBaseNotify
{
    /**
     * The constant TAG.
     */
    private static final String TAG = TupEventHandler.class.getSimpleName();

    /**
     * The constant tupEventHandler.
     */
    private static TupEventHandler tupEventHandler = new TupEventHandler();

    /**
     * The constant tupCallServiceNotifies.
     */
    private static List<TupServiceNotify> tupCallServiceNotifies = new ArrayList<>();

    /**
     * Gets tup event handler.
     *
     * @return the tup event handler
     */
    public static TupEventHandler getTupEventHandler()
    {
        return tupEventHandler;
    }


    /**
     * The Is call connect.
     */
    private boolean isCallConnect = false;

    /**
     * The Has access.
     */
    private boolean hasAccess = false;


    /**
     * Register tup service notify.
     *
     * @param tupServiceNotify the tup service notify
     */
    public void registerTupServiceNotify(TupServiceNotify tupServiceNotify)
    {
        if (null == tupServiceNotify || tupCallServiceNotifies.contains(tupServiceNotify))
        {
            TUPLogUtil.e(TAG, "tupCallServiceNotify is null or is exist");
        }
        tupCallServiceNotifies.add(tupServiceNotify);
    }

    /* ---register event---  */

    /**
     * onRegisterResult
     *
     * @param regRet the TupRegisterResult
     */
    @Override
    public void onRegisterResult(TupRegisterResult regRet)
    {
        TUPLogUtil.i(TAG, "recv onRegisterNotify");
        Iterator iterator = this.tupCallServiceNotifies.iterator();
        while (iterator.hasNext())
        {
            TupServiceNotify listener = (TupServiceNotify) iterator.next();
            try
            {
                listener.onRegisterResult(regRet);
            }
            catch (Exception exception)
            {
                TUPLogUtil.e(TAG, "onRegisterResult exception:" + exception.toString());
            }
        }
    }

    /**
     * onBeKickedOut
     *
     * @param kickOutInfo the KickOutInfo
     */
    @Override
    public void onBeKickedOut(KickOutInfo kickOutInfo)
    {
        TUPLogUtil.i(TAG, "recv onBeKickedOut");
        Iterator iterator = this.tupCallServiceNotifies.iterator();
        while (iterator.hasNext())
        {
            TupServiceNotify listener = (TupServiceNotify) iterator.next();
            try
            {
                listener.onBeKickedOut(kickOutInfo);
            }
            catch (Exception exception)
            {
                TUPLogUtil.e(TAG, "onBeKickedOut exception:" + exception.toString());
            }
        }
    }

     /* ---Audio and video call events----  */

    /**
     * onCallComing
     *
     * @param call the TupCall
     */
    @Override
    public void onCallComing(TupCall call)
    {

        TUPLogUtil.i(TAG, "recv onCallComing======TupCall mediaType=" + call.getMediaType() + ",confMediaType="
                + call.getConfMediaType() + ",confTopologyType=" + call.getConfTopology());

        Iterator iterator = this.tupCallServiceNotifies.iterator();
        while (iterator.hasNext())
        {
            TupServiceNotify listener = (TupServiceNotify) iterator.next();
            try
            {
                listener.onCallComing(call);
            }
            catch (Exception exception)
            {
                TUPLogUtil.e(TAG, "onCallComing exception:" + exception.toString());
            }
        }
    }

    @Override
    public void onCallGoing(TupCall tupCall)
    {
        TUPLogUtil.i(TAG, "recv onCallGoing");
        Iterator iterator = this.tupCallServiceNotifies.iterator();
        while (iterator.hasNext())
        {
            TupServiceNotify listener = (TupServiceNotify) iterator.next();
            try
            {
                listener.onCallGoing(tupCall);
            }
            catch (Exception exception)
            {
                TUPLogUtil.e(TAG, "onCallGoing exception:" + exception.toString());
            }
        }
    }

    @Override
    public void onCallRingBack(TupCall tupCall)
    {
        TUPLogUtil.i(TAG, "recv onCallGoing");
        Iterator iterator = this.tupCallServiceNotifies.iterator();
        while (iterator.hasNext())
        {
            TupServiceNotify listener = (TupServiceNotify) iterator.next();
            try
            {
                listener.onCallRingBack(tupCall);
            }
            catch (Exception exception)
            {
                TUPLogUtil.e(TAG, "onCallGoing exception:" + exception.toString());
            }
        }
    }

    /**
     * onCallConnected
     *
     * @param call the TupCall
     */
    @Override
    public void onCallConnected(TupCall call)
    {
        isCallConnect = true;
        TUPLogUtil.i(TAG, "recv onCallConnected");
        Iterator iterator = this.tupCallServiceNotifies.iterator();
        while (iterator.hasNext())
        {
            TupServiceNotify listener = (TupServiceNotify) iterator.next();
            try
            {
                listener.onCallConnected(call);
            }
            catch (Exception exception)
            {
                TUPLogUtil.e(TAG, "onCallConnected exception:" + exception.toString());
            }
        }
    }

    /**
     * onCallAddVideo
     *
     * @param call the TupCall
     */
    @Override
    public void onCallAddVideo(TupCall call)
    {
        TUPLogUtil.i(TAG, "recv onCallAddVideo");
        Iterator iterator = this.tupCallServiceNotifies.iterator();
        while (iterator.hasNext())
        {
            TupServiceNotify listener = (TupServiceNotify) iterator.next();
            try
            {
                listener.onCallAddVideo(call);
            }
            catch (Exception exception)
            {
                TUPLogUtil.e(TAG, "onCallAddVideo exception:" + exception.toString());
            }
        }
    }

    /**
     * onCallDelVideo
     *
     * @param call the TupCall
     */
    @Override
    public void onCallDelVideo(TupCall call)
    {
        TUPLogUtil.i(TAG, "recv onCallDelVideo");
        Iterator iterator = this.tupCallServiceNotifies.iterator();
        while (iterator.hasNext())
        {
            TupServiceNotify listener = (TupServiceNotify) iterator.next();
            try
            {
                listener.onCallDelVideo(call);
            }
            catch (Exception exception)
            {
                TUPLogUtil.e(TAG, "onCallDelVideo exception:" + exception.toString());
            }
        }

    }

    /**
     * onCallViedoResult
     *
     * @param call the TupCall
     */
    @Override
    public void onCallViedoResult(TupCall call)
    {
        TUPLogUtil.i(TAG, "recv onCallViedoResult");
        Iterator iterator = this.tupCallServiceNotifies.iterator();
        while (iterator.hasNext())
        {
            TupServiceNotify listener = (TupServiceNotify) iterator.next();
            try
            {
                listener.onCallViedoResult(call);
            }
            catch (Exception exception)
            {
                TUPLogUtil.e(TAG, "onCallViedoResult exception:" + exception.toString());
            }
        }
    }

    /**
     * onCallRefreshView
     *
     * @param call the TupCall
     */
    @Override
    public void onCallRefreshView(TupCall call)
    {
        TUPLogUtil.i(TAG, "recv onCallRefreshView");
        Iterator iterator = this.tupCallServiceNotifies.iterator();
        while (iterator.hasNext())
        {
            TupServiceNotify listener = (TupServiceNotify) iterator.next();
            try
            {
                listener.onCallRefreshView(call);
            }
            catch (Exception exception)
            {
                TUPLogUtil.e(TAG, "onCallRefreshView exception:" + exception.toString());
            }
        }
    }

    /**
     * onCallEnded
     *
     * @param call the TupCall
     */
    @Override
    public void onCallEnded(TupCall call)
    {
        hasAccess = false;
        isCallConnect = false;

        ConferenceService.getInstance().setConfMode(false);
        TUPLogUtil.i(TAG, "recv onCallEnded");
        Iterator iterator = this.tupCallServiceNotifies.iterator();
        while (iterator.hasNext())
        {
            TupServiceNotify listener = (TupServiceNotify) iterator.next();
            try
            {
                listener.onCallEnded(call);
            }
            catch (Exception exception)
            {
                TUPLogUtil.e(TAG, "onCallEnded exception:" + exception.toString());
            }
        }
    }

    /* ---IPT added-value events----  */

    /**
     * onCallHoldSuccess
     *
     * @param tupCall the TupCall
     */
    @Override
    public void onCallHoldSuccess(TupCall tupCall)
    {
        TUPLogUtil.i(TAG, "recv onCallHoldSuccess");
        Iterator iterator = this.tupCallServiceNotifies.iterator();
        while (iterator.hasNext())
        {
            TupServiceNotify listener = (TupServiceNotify) iterator.next();
            try
            {
                listener.onCallHoldSuccess(tupCall);
            }
            catch (Exception exception)
            {
                TUPLogUtil.e(TAG, "onCallHoldSuccess exception:" + exception.toString());
            }
        }
    }

    /**
     * onCallHoldFailed
     *
     * @param tupCall the TupCall
     */
    @Override
    public void onCallHoldFailed(TupCall tupCall)
    {
        TUPLogUtil.i(TAG, "recv onCallHoldFailed");
        Iterator iterator = this.tupCallServiceNotifies.iterator();
        while (iterator.hasNext())
        {
            TupServiceNotify listener = (TupServiceNotify) iterator.next();
            try
            {
                listener.onCallHoldFailed(tupCall);
            }
            catch (Exception exception)
            {
                TUPLogUtil.e(TAG, "onCallHoldFailed exception:" + exception.toString());
            }
        }
    }

    /**
     * onCallUnHoldSuccess
     *
     * @param tupCall the TupCall
     */
    @Override
    public void onCallUnHoldSuccess(TupCall tupCall)
    {
        TUPLogUtil.i(TAG, "recv onCallUnHoldSuccess");
        Iterator iterator = this.tupCallServiceNotifies.iterator();
        while (iterator.hasNext())
        {
            TupServiceNotify listener = (TupServiceNotify) iterator.next();
            try
            {
                listener.onCallUnHoldSuccess(tupCall);
            }
            catch (Exception exception)
            {
                TUPLogUtil.e(TAG, "onCallUnHoldSuccess exception:" + exception.toString());
            }
        }
    }

    /**
     * onCallUnHoldFailed
     *
     * @param tupCall the TupCall
     */
    @Override
    public void onCallUnHoldFailed(TupCall tupCall)
    {
        TUPLogUtil.i(TAG, "recv onCallUnHoldFailed");
        Iterator iterator = this.tupCallServiceNotifies.iterator();
        while (iterator.hasNext())
        {
            TupServiceNotify listener = (TupServiceNotify) iterator.next();
            try
            {
                listener.onCallUnHoldFailed(tupCall);
            }
            catch (Exception exception)
            {
                TUPLogUtil.e(TAG, "onCallGoing exception:" + exception.toString());
            }
        }
    }

    /**
     * onCallBldTransferSuccess
     *
     * @param tupCall the TupCall
     */
    @Override
    public void onCallBldTransferSuccess(TupCall tupCall)
    {
        TUPLogUtil.i(TAG, "recv onCallBldTransferSuccess");
        Iterator iterator = this.tupCallServiceNotifies.iterator();
        while (iterator.hasNext())
        {
            TupServiceNotify listener = (TupServiceNotify) iterator.next();
            try
            {
                listener.onCallBldTransferSuccess(tupCall);
            }
            catch (Exception exception)
            {
                TUPLogUtil.e(TAG, "onCallBldTransferSuccess exception:" + exception.toString());
            }
        }
    }

    /**
     * onCallBldTransferFailed
     *
     * @param tupCall the TupCall
     */
    @Override
    public void onCallBldTransferFailed(TupCall tupCall)
    {
        TUPLogUtil.i(TAG, "recv onCallBldTransferFailed");
        Iterator iterator = this.tupCallServiceNotifies.iterator();
        while (iterator.hasNext())
        {
            TupServiceNotify listener = (TupServiceNotify) iterator.next();
            try
            {
                listener.onCallBldTransferFailed(tupCall);
            }
            catch (Exception exception)
            {
                TUPLogUtil.e(TAG, "onCallBldTransferFailed exception:" + exception.toString());
            }
        }
    }

    @Override
    public void onSetIptServiceSuc(int i)
    {
        TUPLogUtil.i(TAG, "recv onSetIptServiceSuc->" + i);
        Iterator iterator = this.tupCallServiceNotifies.iterator();
        while (iterator.hasNext())
        {
            TupServiceNotify listener = (TupServiceNotify) iterator.next();
            try
            {
                listener.onSetIptServiceSuc(i);
            }
            catch (Exception exception)
            {
                TUPLogUtil.e(TAG, "onSetIptServiceSuc exception:" + exception.toString());
            }
        }
    }

    @Override
    public void onSetIptServiceFal(int i)
    {
        TUPLogUtil.i(TAG, "recv onSetIptServiceSuc->" + i);
        Iterator iterator = this.tupCallServiceNotifies.iterator();
        while (iterator.hasNext())
        {
            TupServiceNotify listener = (TupServiceNotify) iterator.next();
            try
            {
                listener.onSetIptServiceFal(i);
            }
            catch (Exception exception)
            {
                TUPLogUtil.e(TAG, "onSetIptServiceFal exception:" + exception.toString());
            }
        }
    }


    @Override
    public void onDataReady(int callId, int bfcpRet)
    {
        TUPLogUtil.i(TAG, "onDataReady-----------------" + callId + "--" + callId);
    }

    @Override
    public void onBFCPReinited(int var1)
    {
        Log.e(TAG, "onBFCPReinited-----------------" + var1);
    }

    @Override
    public void onDataSending(int var1)
    {
        Log.e(TAG, "onDataSending-----------------" + var1);
    }

    @Override
    public void onDataReceiving(int callId)
    {
        Log.e(TAG, "onDataReceiving-----------------" + callId);
        Iterator iterator = tupCallServiceNotifies.iterator();
        while (iterator.hasNext())
        {
            TupServiceNotify listener = (TupServiceNotify) iterator.next();
            try
            {
                listener.onDataReceiving(callId);
            }
            catch (Exception exception)
            {
                TUPLogUtil.e(TAG, "onDataReceiving exception:" + exception.toString());
            }
        }
    }

    @Override
    public void onDataStopped(int var1)
    {
        Log.e(TAG, "onDataStopped-----------------" + var1);
        Iterator iterator = tupCallServiceNotifies.iterator();
        while (iterator.hasNext())
        {
            TupServiceNotify listener = (TupServiceNotify) iterator.next();
            try
            {
                listener.onDataStopped(var1);
            }
            catch (Exception exception)
            {
                TUPLogUtil.e(TAG, "onDataStopped exception:" + exception.toString());
            }
        }
    }

    @Override
    public void onDataStartErr(int var1, int var2)
    {
        Log.e(TAG, "onDataStartErr-----------------" + var1 + "--" + var2);
        Iterator iterator = tupCallServiceNotifies.iterator();
        while (iterator.hasNext())
        {
            TupServiceNotify listener = (TupServiceNotify) iterator.next();
            try
            {
                listener.onDataStartErr(var1, var2);
            }
            catch (Exception exception)
            {
                TUPLogUtil.e(TAG, "onDataStartErr exception:" + exception.toString());
            }
        }
    }

    @Override
    public void onLineStateNotify(OnLineState var1)
    {
    }

    @Override
    public void onDataFramesizeChange(TupCall var1)
    {
        TUPLogUtil.i(TAG, "onDataFramesizeChange-----------------");
    }

    @Override
    public void onDecodeSuccess(DecodeSuccessInfo var1)
    {
        TUPLogUtil.i(TAG, "onDecodeSuccess-----------------");
    }


    @Override
    public void onIdoOverBFCPSupport(int i, int i1)  //callId isBfcpIdo
    {
        TUPLogUtil.i(TAG, "onIdoOverBFCPSupport------------callId:" + i + ",isbfcpIdo=" + i1);
        if (!hasAccess && (i1 != 0) && isCallConnect)
        {
            //LoginParams.getInstance().getSipImpi(), LoginParams.getInstance().getVoipPassword()
            int type = LoginService.getInstance().getVcType();
            if (type == 1)
            {
                ConferenceService.getInstance().setConfType(ConfctrlConfEnvType.CONFCTRL_E_CONF_ENV_HOSTED_VC);
            }
            else if (type == 0)
            {
                ConferenceService.getInstance().setConfType(ConfctrlConfEnvType.CONFCTRL_E_CONF_ENV_ON_PREMISE_VC);
            }

            TupConfAccessInfo tupConfAccessInfo
                    = new TupConfAccessInfo(i, 0, "", "");
            ConferenceService.getInstance().accessConf(tupConfAccessInfo);
            hasAccess = true;
        }

    }

    @Override
    public void onNotifyNetAddress(NetAddress var1)
    {
        TUPLogUtil.i(TAG, "-----------------onNotifyNetAddress:address="
                + var1.getAddress() + ",username=" + var1.getUserName()
                + ",euaType=" + var1.getEuaType() + ",version=" + var1.getVersion());

        if (EUAType.CALL_E_EUA_TYPE_LDAP.equals(var1.getEuaType()))
        {
            if (!ContactService.getInstance().isLdap())
            {
                TupLdapContactsCfg tupLdapContactsCfg = new TupLdapContactsCfg();
                tupLdapContactsCfg.setBaseDN(var1.getDNValue());
                tupLdapContactsCfg.setServerAddr(var1.getAddress());
                tupLdapContactsCfg.setUserName(var1.getUserName());
                tupLdapContactsCfg.setPassword(var1.getPassword());
                int ret1 = ContactService.getInstance().setLdapConfig(tupLdapContactsCfg);

                boolean ret = ContactService.getInstance().startLdapContactsServer();
                TUPLogUtil.i(TAG, "---------ret1:" + ret1 + ",---ret:" + ret);
                if (ret1 == 0 && ret)
                {
                    ContactService.getInstance().setLdap(true);
                }

            }
        }

    }

    @Override
    public void onConfNotify(int i, Conf conf)
    {
        Log.e(TAG, "----------onConfNotify:conf=" + conf);
        DataConfParam dataConfParam = (DataConfParam) conf;
        ConferenceService.getInstance().setDataConfParam(dataConfParam);
    }
}
