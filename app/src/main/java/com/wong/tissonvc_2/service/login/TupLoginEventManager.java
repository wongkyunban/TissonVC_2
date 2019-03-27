package com.wong.tissonvc_2.service.login;

import android.util.Log;

import com.wong.tissonvc_2.service.TupEventHandler;
import com.wong.tissonvc_2.service.TupEventMgr;
import com.wong.tissonvc_2.service.TupServiceNotifyImpl;
import com.wong.tissonvc_2.service.common.CallConstants;
import com.wong.tissonvc_2.service.common.CallConstants.State;
import com.wong.tissonvc_2.service.utils.TUPLogUtil;

import common.TupBool;
import common.TupCallParam;
import object.KickOutInfo;
import object.TupRegisterResult;


/**
 * The type Tup register event manager.
 */
public final class TupLoginEventManager extends TupServiceNotifyImpl
{
    /**
     * The constant TAG.
     */
    private static final String TAG = TupLoginEventManager.class.getSimpleName();
    /**
     * The constant tupLoginEventManager.
     */
    private static TupLoginEventManager tupLoginEventManager = new TupLoginEventManager();

    /**
     * Instantiates a new Tup login event manager.
     */
    private TupLoginEventManager()
    {
        TupEventHandler.getTupEventHandler().registerTupServiceNotify(this);
    }

    /**
     * Gets tup event manager.
     *
     * @return the tup event manager
     */
    public static TupLoginEventManager getTupLoginEventManager()
    {
        return tupLoginEventManager;
    }

    @Override
    public void onRegisterResult(TupRegisterResult tupRegisterResult)
    {
        if (null == tupRegisterResult)
        {
            TUPLogUtil.e(TAG, "tupRegisterResult is null");
            return;
        }
        int regState = tupRegisterResult.getRegState();
        int errorCode = tupRegisterResult.getReasonCode();
        Log.e(TAG,"---------errorCode:"+errorCode);
        onRegisterEvent(regState, errorCode);
    }

    @Override
    public void onBeKickedOut(KickOutInfo kickOutInfo)
    {
        if (null == kickOutInfo)
        {
            TUPLogUtil.e(TAG, "kickOutInfo is null");
            return;
        }
        handleTupKickedOut(kickOutInfo);
    }

    /**
     * On register event.
     *
     * @param code      the code
     * @param errorCode the error code
     */
    private void onRegisterEvent(int code, int errorCode)
    {
        TUPLogUtil.i(TAG, "code" + "|" + code + "errorCode" + "|" + errorCode);
        switch (code)
        {
            case TupCallParam.CALL_E_REG_STATE.CALL_E_REG_STATE_UNREGISTER:
                TUPLogUtil.i(TAG, "errorCode" + "|" + errorCode);
                onLoginResult(State.UNREGISTER, errorCode);
                TUPLogUtil.i(TAG, "unregister.");
                break;

            case TupCallParam.CALL_E_REG_STATE.CALL_E_REG_STATE_REGISTERING:
                onLoginResult(State.REGISTERING, errorCode);
                TUPLogUtil.i(TAG, "registering.");
                break;

            case TupCallParam.CALL_E_REG_STATE.CALL_E_REG_STATE_DEREGISTERING:
                onLoginResult(State.DEREGISTERING, errorCode);
                TUPLogUtil.i(TAG, "deregistering.");
                break;

            case TupCallParam.CALL_E_REG_STATE.CALL_E_REG_STATE_REGISTERED:
                TUPLogUtil.i(TAG, "register success.");
                onLoginResult(State.REGISTERED, TupCallParam.CALL_TUP_RESULT.TUP_SUCCESS);
                break;

            case TupCallParam.CALL_E_REG_STATE.CALL_E_REG_STATE_BUTT:
                onLoginResult(State.BUTT, errorCode);
                TUPLogUtil.i(TAG, "login out success.");
                break;

            default:
                break;
        }
    }

    /**
     * Handle tup kicked out.
     *
     * @param kickOutInfo the kick out info
     */
    private void handleTupKickedOut(KickOutInfo kickOutInfo)
    {
        TupBool isKickOff = kickOutInfo.getIsKickOff();
        TUPLogUtil.i(TAG, "isKickOff->" + isKickOff);

        if (TupBool.TUP_TRUE.equals(isKickOff))
        {
            TupEventMgr.onRegisterEventNotify(CallConstants.CALL_LOGOUT_NOTIFY,
                    TupCallParam.CALL_TUP_RESULT.TUP_SUCCESS);
        }
    }

    /**
     * onLoginResult.
     *
     * @param status    the status
     * @param errorCode the error code
     */
    private void onLoginResult(State status, int errorCode)
    {
        TUPLogUtil.i(TAG, "run onLoginResult");
        if (status == null)
        {
            TUPLogUtil.e(TAG, "status is null");
            return;
        }

        TUPLogUtil.i(TAG, "menu value" + status.ordinal());

        if (status == State.REGISTERED)
        {
            TupEventMgr.onRegisterEventNotify(
                    TupCallParam.CALL_E_REG_STATE.CALL_E_REG_STATE_REGISTERED, errorCode);
        }
        else if (status == State.UNREGISTER)
        {
            TupEventMgr.onRegisterEventNotify(
                    TupCallParam.CALL_E_REG_STATE.CALL_E_REG_STATE_UNREGISTER, errorCode);
        }
        else if (status == State.REGISTERING)
        {
            TupEventMgr.onRegisterEventNotify(
                    TupCallParam.CALL_E_REG_STATE.CALL_E_REG_STATE_REGISTERING, errorCode);
        }
        else if (status == State.DEREGISTERING)
        {
            TupEventMgr.onRegisterEventNotify(
                    TupCallParam.CALL_E_REG_STATE.CALL_E_REG_STATE_DEREGISTERING, errorCode);
        }
        else if (status == State.BUTT)
        {
            TupEventMgr.onRegisterEventNotify(
                    TupCallParam.CALL_E_REG_STATE.CALL_E_REG_STATE_BUTT, errorCode);
        }

    }


}
