package com.wong.tissonvc_2.service.call;

import android.text.TextUtils;

import com.huawei.common.CallRecordInfo;
import com.huawei.common.PersonalContact;
import com.wong.tissonvc_2.R;
import com.wong.tissonvc_2.service.TupEventHandler;
import com.wong.tissonvc_2.service.TupEventMgr;
import com.wong.tissonvc_2.service.TupServiceNotifyImpl;
import com.wong.tissonvc_2.service.call.data.CallCommandParams;
import com.wong.tissonvc_2.service.call.data.CallSession;
import com.wong.tissonvc_2.service.call.data.CameraViewRefresh;
import com.wong.tissonvc_2.service.call.data.SessionBean;
import com.wong.tissonvc_2.service.common.CallConstants;
import com.wong.tissonvc_2.service.conf.ConferenceService;
import com.wong.tissonvc_2.service.contacts.ContactService;
import com.wong.tissonvc_2.service.login.data.LoginParams;
import com.wong.tissonvc_2.service.utils.TUPLogUtil;
import com.wong.tissonvc_2.service.utils.Tools;
import com.huawei.tup.confctrl.sdk.TupConfDataConfParamsGetReq;
import com.huawei.tup.confctrl.sdk.TupConfInfo;
import com.huawei.tup.confctrl.sdk.TupConfVCAttendeeInfo;
import com.huawei.tup.confctrl.sdk.TupConference;

import java.util.ArrayList;
import java.util.Date;

import common.TupCallParam;
import common.VideoWndType;
import tupsdk.TupCall;


/**
 * The type Tup call event manager.
 */
public class TupCallEventManager extends TupServiceNotifyImpl
{
    private static final String TAG = TupCallEventManager.class.getSimpleName();
    private static final String BLANK_STRING = "";
    private static final String FORBIDDEN = "forbidden";
    private static final String NOT_FOUND = "not-found";
    private static final String NO_ANSWER = "no-answer";
    private static final String TEMP_UNVAILABLE = "temp-unvailable";
    private static final String BUSY = "busy";
    private static final String CANCELLED = "cancelled";
    private static final String MEIDA_NOT_ACCEPTABLE = "media-not-acceptable";
    private static final String REJECT = "reject";
    private static final String NETWORK_FAILURE = "network-failure";
    private static final String ADD = "add";
    private static final String DEL = "del";
    private static final String VOIP_UNAVAILABLE = "VoIP Unavailable";
    private static final String CAUSE_EQUAL_ONE = "cause=1";
    private static final int REASON_BLANK_STRING = 302;
    private static final int REASON_FORBIDDEN = 403;
    private static final int REASON_NOT_FOUND = 604;
    private static final int REASON_NO_ANSWER = 408;
    private static final int REASON_TEMP_UNVAILABLE = 480;
    private static final int REASON_BUSY = 486;
    private static final int REASON_CANCELLED = 487;
    private static final int REASON_MEIDA_NOT_ACCEPTABLE = 488;
    private static final int REASON_REJECT = 603;

    private long currCallId = -1;
    private int recordId = -1;

    private static TupCallEventManager tupCallEventManager = new TupCallEventManager();

    /**
     * Cause of failure
     */
    private String reasonText = null;

    private CallRecordInfo callRecordInfo;


    private TupCallEventManager()
    {
        TupEventHandler.getTupEventHandler().registerTupServiceNotify(this);
    }

    /**
     * Gets tup event manager.
     *
     * @return the tup event manager
     */
    public static TupCallEventManager getTupCallEventManager()
    {
        return tupCallEventManager;
    }


    /**
     * insert call record
     *
     * @param callRecordInfo callRecordInfo
     * @return int
     */
    public int insertCallRecords(CallRecordInfo callRecordInfo)
    {
        return ContactService.getInstance().insertCallRecord(callRecordInfo);
    }

    /**
     * update call record
     *
     * @param callRecordInfo callRecordInfo
     * @return int
     */
    public int updateCallRecords(CallRecordInfo callRecordInfo)
    {
        return ContactService.getInstance().modifyCallRecord(callRecordInfo);
    }

    /**
     * get recordId by callId
     *
     * @param callId callId
     * @return recordId
     */
    private int getRecordIdByCallId(long callId)
    {
        if (currCallId == callId)
        {
            return recordId;
        }
        else
        {
            return -1;
        }

    }


    @Override
    public void onCallComing(TupCall call)
    {
        handleCallComing(call);
        currCallId = call.getCallId();
        callRecordInfo = new CallRecordInfo();
        String callerNum = call.getTelNumber();
        PersonalContact pc = new PersonalContact();
        String toNum = call.getFromNumber();
        String number = toNum;

        TUPLogUtil.i(TAG, "onCallComing------isFocus:" + call.getIsFocus()
                + ",serverConfID:" + call.getServerConfID() + ",serverConfType:" + call.getServerConfType()
                + ",numberOne:" + number + ",callerNum=" + callerNum);
        pc.setNumberOne(number);

        if (call.getCallType() == 1)
        {
            callRecordInfo.setCallOutType(CallRecordInfo.DialType.VIDEO);
        }
        else
        {
            callRecordInfo.setCallOutType(CallRecordInfo.DialType.AUDIO);
        }
        callRecordInfo.setCallType(CallRecordInfo.RecordType.CALL_RECORD_IN);
        callRecordInfo.setPc(pc);
        callRecordInfo.setNumber(number);
        callRecordInfo.setCallStartTime(new Date());
        int ret = insertCallRecords(callRecordInfo);
        recordId = ret;
        TUPLogUtil.i(TAG, "onCallComing-------------------insertCallRecords,ret=" + ret);
    }

    @Override
    public void onCallGoing(TupCall tupCall)
    {

        handleCallGoing(tupCall);
        currCallId = tupCall.getCallId();
        callRecordInfo = new CallRecordInfo();

        PersonalContact pc = new PersonalContact();
        String toNum = tupCall.getToNumber();
        String number = toNum.substring(4, toNum.indexOf('@'));
        pc.setNumberOne(number);
        TUPLogUtil.i(TAG, "onCallGoing----------------------numberOne=" + number);


        if (tupCall.getIsviedo() == 0)
        {
            callRecordInfo.setCallOutType(CallRecordInfo.DialType.VIDEO);
        }
        else
        {
            callRecordInfo.setCallOutType(CallRecordInfo.DialType.AUDIO);
        }
        TUPLogUtil.i(TAG, "onCallGoing----------------------video=" + tupCall.getIsviedo());
        callRecordInfo.setCallType(CallRecordInfo.RecordType.CALL_RECORD_OUT);
        callRecordInfo.setPc(pc);
        callRecordInfo.setNumber(number);
        callRecordInfo.setCallStartTime(new Date());
        int ret = insertCallRecords(callRecordInfo);
        recordId = ret;
        TUPLogUtil.i(TAG, "onCallGoing----------------------insertCallRecords,ret=" + ret);
    }

    @Override
    public void onCallRingBack(TupCall tupCall)
    {
        handleCallRingBack(tupCall);
    }

    @Override
    public void onCallConnected(TupCall call)
    {
        handleCallConnect(call);
    }

    @Override
    public void onCallAddVideo(TupCall call)
    {
        handleCallAddVideo(call);
    }

    @Override
    public void onCallDelVideo(TupCall call)
    {
        handleCallDeleteVideo(call);
    }

    @Override
    public void onCallViedoResult(TupCall call)
    {
        handleCallVideoResult(call);
    }

    @Override
    public void onCallRefreshView(TupCall call)
    {
        handleRefreshView(call);
    }

    @Override
    public void onCallEnded(TupCall call)
    {
        if (callRecordInfo != null)
        {
            int ret = getRecordIdByCallId(call.getCallId());
            if (ret != -1)
            {
                long callTime = new Date().getTime() - callRecordInfo.getCallStartTime().getTime();
                TUPLogUtil.i(TAG, "--------------------callTime=" + callTime);
                callRecordInfo.setCallTime(callTime / 1000);

                callRecordInfo.setId(recordId);
                updateCallRecords(callRecordInfo);
            }

        }
        callRecordInfo = null;
        ConferenceService.getInstance().setConfMode(false);
        CallService.getInstance().setStartMediaPlayResult(0);
        CallService.getInstance().stopMediaPlay();
        handleCallEnd(call);
    }

    @Override
    public void onCallHoldSuccess(TupCall tupCall)
    {
        handleCallHoldSuccess(tupCall);
    }

    @Override
    public void onCallHoldFailed(TupCall tupCall)
    {
        handleCallHoldFailed(tupCall);
    }

    @Override
    public void onCallUnHoldSuccess(TupCall tupCall)
    {
        handleCallUnHoldSuccess(tupCall);
    }

    @Override
    public void onCallUnHoldFailed(TupCall tupCall)
    {
        handleCallUnHoldFailed(tupCall);
    }

    @Override
    public void onCallBldTransferSuccess(TupCall tupCall)
    {
        handleCallBlindTransferSuccess(tupCall);
    }

    @Override
    public void onCallBldTransferFailed(TupCall tupCall)
    {
        handleCallBlindTransferFailed(tupCall);
    }

    @Override
    public void onSetIptServiceSuc(int i)
    {
        handleSetIptServiceSuccess(i);
    }

    @Override
    public void onSetIptServiceFal(int i)
    {
        handleSetIptServiceFail(i);
    }


    @Override
    public void onDataReady(int var1, int var2)
    {
        TUPLogUtil.i(TAG, "onDataReady---------------bfcpret=" + var2);
    }

    @Override
    public void onDataReceiving(int var1)
    {
        TUPLogUtil.i(TAG, "onDataReceiving---------------callId=" + var1);
        TupEventMgr.onCallEventNotify(
                TupCallParam.CallEvent.CALL_E_EVT_DATA_RECVING, var1);
    }

    @Override
    public void onDataStartErr(int var1, int var2)
    {
        TUPLogUtil.i(TAG, "onDataStartErr---------------");
    }

    @Override
    public void onDataStopped(int var1)
    {
        TUPLogUtil.i(TAG, "onDataStopped---------------callId=" + var1);
        TupEventMgr.onCallEventNotify(
                TupCallParam.CallEvent.CALL_E_EVT_DATA_STOPPED, var1);
    }

    /**
     * Handle call coming.
     *
     * @param call the call
     */
    private void handleCallComing(TupCall call)
    {
        if (null == call)
        {
            TUPLogUtil.e(TAG, "call is null");
            return;
        }

        CallService.getInstance().setIsCallComing(true);
        String callId = String.valueOf(call.getCallId());
        boolean isVideo = call.getCallType() == 1;
        String callerNum = call.getTelNumber();
        TUPLogUtil.i(TAG, "callerNum is--" + callerNum);
        String calleeNum = call.getToNumber();
        TUPLogUtil.i(TAG, "calleeNum is--" + calleeNum);


        String callerDisplayName = call.getFromDisplayName();
        if (callerNum == null)
        {
            callerNum = "";
        }

        if (calleeNum == null)
        {
            calleeNum = "";
        }

        if (TextUtils.isEmpty(callerDisplayName))
        {
            callerDisplayName = callerNum;
        }

        CallSession callSession = new CallSession(CallService.getInstance(), call);
        CallService.getInstance().putCallSession(callId, callSession);
        TUPLogUtil.i(TAG, "call coming callid: " + callId + "isVideo: "
                + call.getCallType() + "callerNumber: " + call.getTelNumber()
                + "callerDisplayname: " + call.getFromDisplayName()
                + "calleeNumber: " + call.getToNumber()
                + "call.getRemoteURI() : " + call.getRemoteURI());
        String serverAddr = LoginParams.getInstance().getProxyServerIp();
        boolean beServerAddr = serverAddr != null && (!Tools.isIPAddress(serverAddr)
                || !LoginParams.getInstance().getDomain().equals(serverAddr));
        if (beServerAddr)
        {
            callerNum = callerNum + '@' + serverAddr;
        }

        SessionBean callComingSessionBean = new SessionBean();
        callComingSessionBean.setVideoCall(isVideo);
        callComingSessionBean.setCallID(callId);
        callComingSessionBean.setCallerNumber(callerNum);
        callComingSessionBean.setCallerDisplayname(callerDisplayName);
        callComingSessionBean.setCalleeNumber(calleeNum);

        processCallNtfComing(callComingSessionBean);
    }

    /**
     * Handle call going.
     *
     * @param call the call
     */
    private void handleCallGoing(TupCall call)
    {
        if (null == call)
        {
            TUPLogUtil.e(TAG, "call is null");
            return;
        }
        TupEventMgr.onCallEventNotify(TupCallParam.CallEvent.CALL_E_EVT_CALL_OUTGOING, call);
    }

    /**
     * Handle call ring back.
     *
     * @param call the call
     */
    private void handleCallRingBack(TupCall call)
    {
        if (null == call)
        {
            TUPLogUtil.e(TAG, "call is null");
            return;
        }
        int haveSDP = call.getHaveSDP();
        TUPLogUtil.i(TAG, "haveSDP->" + haveSDP);

        TupEventMgr.onCallEventNotify(TupCallParam.CallEvent.CALL_E_EVT_CALL_RINGBACK, call);
    }

    /**
     * Handle call connect.
     *
     * @param call the call
     */
    private void handleCallConnect(TupCall call)
    {
        if (null == call)
        {
            TUPLogUtil.e(TAG, "call is null");
            return;
        }

        TUPLogUtil.i(TAG, "-----confMediaType:" + call.getConfMediaType() + ",confTopologyType:" + call.getConfTopology()
                + ",serverConfID:" + call.getServerConfID());

        String passcode = call.getServerConfID();
        int confMode = call.getConfMediaType();

        if (passcode != null && !"".equals(passcode))
        {
            TupConfDataConfParamsGetReq req = new TupConfDataConfParamsGetReq();
            req.setPasscode(passcode);
            req.setSipNum(LoginParams.getInstance().getSipNumber());
            req.setConfUrl("https://" + LoginParams.getInstance().getRegisterServerIp() + ":443");
            req.setType(1);
            try
            {
                int result = ConferenceService.getInstance().getDataConfParams(req);
                TUPLogUtil.i(TAG, "----getDataConfParams result:" + result + ",callId=" + call.getCallId());
            }
            catch (Exception e)
            {
                e.printStackTrace();
                TUPLogUtil.e(TAG, "----getDataConfParams exception:" + e.getMessage());
            }

        }


        CallService.getInstance().stopMediaPlay();
        SessionBean callConnectSessionBean = new SessionBean();
        callConnectSessionBean.setCallID(String.valueOf(call.getCallId()));
        callConnectSessionBean.setVideoCall(call.getCallType() == 1);
        CallSession session = CallService.getInstance().getCallSession(
                String.valueOf(call.getCallId()));
        if (session != null && call.getCallType() == 1)
        {
            CallService.getInstance().setVideoOrient(call.getCallId(), VideoDeviceManager.FRONT_CAMERA);
            session.setVideoRenderInfo(VideoWndType.remote);
        }
        processCallNtfTalk(callConnectSessionBean);
    }

    /**
     * Handle call add video.
     *
     * @param call the call
     */
    private void handleCallAddVideo(TupCall call)
    {
        if (null == call)
        {
            TUPLogUtil.e(TAG, "call is null");
            return;
        }
        SessionBean addVideoSessionBean = new SessionBean();
        addVideoSessionBean.setCallID(String.valueOf(call.getCallId()));
        addVideoSessionBean.setOperation(ADD);

        processCallNtfModifyAlert(addVideoSessionBean);
    }

    /**
     * Handle call video result.
     *
     * @param call the call
     */
    private void handleCallVideoResult(TupCall call)
    {
        if (null == call)
        {
            TUPLogUtil.e(TAG, "call is null");
            return;
        }
        SessionBean videoResultsessionBean = new SessionBean();
        videoResultsessionBean.setCallID(String.valueOf(call.getCallId()));
        String addOpers = ADD;
        int iRet = call.getModifyVideoResult();
        int isVideo = call.getIsviedo();
        int callId = call.getCallId();
        TUPLogUtil.i(TAG, "iRet->" + iRet + ",isVideo->" + isVideo);
        TUPLogUtil.i(TAG, "callId->" + callId);
        if (iRet == 0)
        {
            if (isVideo == 0)
            {
                addOpers = DEL;
                videoResultsessionBean.setVideoModifyState(0);
            }
            else if (1 == isVideo)
            {
                addOpers = ADD;
                videoResultsessionBean.setVideoModifyState(1);
                CallService.getInstance().setVideoOrient(callId,
                        VideoDeviceManager.FRONT_CAMERA);
            }
        }
        else if (isVideo == 0)
        {
            addOpers = ADD;
            videoResultsessionBean.setVideoModifyState(0);
            CallService.getInstance().setVideoOrient(callId,
                    VideoDeviceManager.FRONT_CAMERA);
        }
        else if (1 == isVideo)
        {
            addOpers = DEL;
            videoResultsessionBean.setVideoModifyState(1);
        }
        videoResultsessionBean.setOperation(addOpers);
        videoResultsessionBean.setRemoteVideoState(0);

        processCallNtfModified(videoResultsessionBean);
    }

    /**
     * Handle call delete video.
     *
     * @param call the call
     */
    private void handleCallDeleteVideo(TupCall call)
    {
        if (null == call)
        {
            TUPLogUtil.e(TAG, "call is null");
            return;
        }
        call.replyDelVideo(1);
        SessionBean deleteVideosessionBean = new SessionBean();
        deleteVideosessionBean.setCallID(String.valueOf(call.getCallId()));
        String delOpers = DEL;
        deleteVideosessionBean.setOperation(delOpers);
        deleteVideosessionBean.setRemoteVideoState(0);
        deleteVideosessionBean.setVideoModifyState(0);

        processCallNtfModified(deleteVideosessionBean);
    }

    /**
     * Handle refresh view.
     *
     * @param call the call
     */
    private void handleRefreshView(TupCall call)
    {
        if (null == call)
        {
            TUPLogUtil.e(TAG, "call is null");
            return;
        }
        CameraViewRefresh data = new CameraViewRefresh();
        data.setMediaType(call.getMediaType());
        data.setViewType(call.getEvent());
        callRefreshView(data);
    }


    /**
     * Handle call end.
     *
     * @param call the call
     */
    private void handleCallEnd(TupCall call)
    {
        if (null == call)
        {
            TUPLogUtil.e(TAG, "call is null");
            return;
        }
        CallService.getInstance().setIsCallComing(false);
        SessionBean callEndSessionBean = new SessionBean();
        String callid = String.valueOf(call.getCallId());
        callEndSessionBean.setCallID(callid);
        int reasonCode = call.getReasonCode();
        String reason = getCallEndReason(reasonCode);

        callEndSessionBean.setReleaseReason(reason);
        processCallNtfEnded(callEndSessionBean);
        CallService.getInstance().removeCallSession(callid);
        processCallNtfClosed(callEndSessionBean);
    }

    /**
     * Handle call hold success.
     *
     * @param call the call
     */
    private void handleCallHoldSuccess(TupCall call)
    {
        TupEventMgr.onCallEventNotify(TupCallParam.CallEvent.CALL_E_EVT_CALL_HOLD_SUCCESS,
                TupCallParam.CALL_TUP_RESULT.TUP_SUCCESS);
    }

    /**
     * Handle call hold failed.
     *
     * @param call the call
     */
    public void handleCallHoldFailed(TupCall call)
    {
        TupEventMgr.onCallEventNotify(TupCallParam.CallEvent.CALL_E_EVT_CALL_HOLD_FAILED,
                TupCallParam.CALL_TUP_RESULT.TUP_FAIL);
    }

    /**
     * Handle call un hold success.
     *
     * @param call the call
     */
    private void handleCallUnHoldSuccess(TupCall call)
    {
        TupEventMgr.onCallEventNotify(TupCallParam.CallEvent.CALL_E_EVT_CALL_UNHOLD_SUCCESS,
                TupCallParam.CALL_TUP_RESULT.TUP_FAIL);
    }

    /**
     * Handle call un hold failed.
     *
     * @param call the call
     */
    private void handleCallUnHoldFailed(TupCall call)
    {
        TupEventMgr.onCallEventNotify(TupCallParam.CallEvent.CALL_E_EVT_CALL_UNHOLD_FAILED,
                TupCallParam.CALL_TUP_RESULT.TUP_FAIL);
    }

    /**
     * Handle call blind transfer success.
     *
     * @param call the call
     */
    private void handleCallBlindTransferSuccess(TupCall call)
    {
        TupEventMgr.onCallEventNotify(TupCallParam.CallEvent.CALL_E_EVT_CALL_BLD_TRANSFER_SUCCESS,
                TupCallParam.CALL_TUP_RESULT.TUP_SUCCESS);
    }

    /**
     * Handle call blind transfer failed.
     *
     * @param call the call
     */
    private void handleCallBlindTransferFailed(TupCall call)
    {
        TupEventMgr.onCallEventNotify(TupCallParam.CallEvent.CALL_E_EVT_CALL_BLD_TRANSFER_FAILED,
                TupCallParam.CALL_TUP_RESULT.TUP_FAIL);
    }

    /**
     * Handle set ipt service success.
     *
     * @param i the
     */
    private void handleSetIptServiceSuccess(int i)
    {
        TupEventMgr.onCallEventNotify(CallConstants.MSG_IPT_SERVICE_SUCCESS, i);
    }

    /**
     * Handle set ipt service fail.
     *
     * @param i the
     */
    private void handleSetIptServiceFail(int i)
    {
        TupEventMgr.onCallEventNotify(CallConstants.MSG_IPT_SERVICE_FAIL, i);
    }

    private String getCallEndReason(int reasonCode)
    {
        String reason = "";
        switch (reasonCode)
        {
            case REASON_BLANK_STRING:
                reason = BLANK_STRING;
                break;
            case REASON_FORBIDDEN:
                reason = FORBIDDEN;
                break;
            case REASON_NOT_FOUND:
                reason = NOT_FOUND;
                break;
            case REASON_NO_ANSWER:
                reason = NO_ANSWER;
                break;
            case REASON_TEMP_UNVAILABLE:
                reason = TEMP_UNVAILABLE;
                break;
            case REASON_BUSY:
                reason = BUSY;
                break;
            case REASON_CANCELLED:
                reason = CANCELLED;
                break;
            case REASON_MEIDA_NOT_ACCEPTABLE:
                reason = MEIDA_NOT_ACCEPTABLE;
                break;
            case REASON_REJECT:
                reason = REJECT;
                break;
            default:
                reason = NETWORK_FAILURE;
                break;
        }
        return reason;
    }

    /**
     * callComingNotify
     *
     * @param sessionBean
     */
    private void processCallNtfComing(SessionBean sessionBean)
    {
        if (sessionBean == null)
        {
            TUPLogUtil.e(TAG, "session is null.");
            return;
        }
        callNtfComing(sessionBean);

        int callType = CallConstants.COMING_AUDIO_CALL;

        if (sessionBean.isVideoCall())
        {
            callType = CallConstants.COMING_VIDEO_CALL;
        }
        sessionBean.setCallType(callType);

        TupEventMgr.onCallEventNotify(
                TupCallParam.CallEvent.CALL_E_EVT_CALL_INCOMMING, sessionBean);


    }

    /**
     * callNtfComing
     *
     * @param session
     */
    private void callNtfComing(SessionBean session)
    {
        TUPLogUtil.i(TAG, "callsession:" + session);
        if (session == null)
        {
            TUPLogUtil.i(TAG, "session is null");
            return;
        }
        TUPLogUtil.i(TAG, "setComingCallID" + "|" + session.getCallID());
        CallService.getInstance().setComingCallID(session.getCallID());
        CallCommandParams callCommandParams = new CallCommandParams();
        callCommandParams.setCallID(session.getCallID());

        CallService.getInstance().refreshAudioRoute();
        CallService.getInstance().putSessionBean(session.getCallID(), session);
        CallService.getInstance().alertingCall(callCommandParams);
    }

    /**
     * processCallNtfTalk
     *
     * @param sessionBean
     */
    private void processCallNtfTalk(SessionBean sessionBean)
    {
        if (sessionBean == null)
        {
            TUPLogUtil.e(TAG, "session is null.");
            return;
        }

        callNtfTalk(sessionBean);
        String callid = sessionBean.getCallID();
        if (TextUtils.isEmpty(callid))
        {
            TUPLogUtil.e(TAG, "processCallNtfTalk:callid is empty.");
            return;
        }

        if (!sessionBean.isVideoCall())
        {
            CallService.getInstance().setVoipStatus(CallConstants.STATUS_TALKING);
        }
        else
        {
            CallService.getInstance().setVoipStatus(CallConstants.STATUS_VIDEOING);
        }
        notifyCallActivityUpdateUI();
    }


    private void notifyCallActivityUpdateUI()
    {
        TupEventMgr.onCallEventNotify(CallConstants.MSG_CALL_UPDATE_UI, false);
    }

    /**
     * callNtfTalk
     *
     * @param session
     */
    private void callNtfTalk(SessionBean session)
    {
        if (session == null)
        {
            TUPLogUtil.e(TAG, "processCallNtfTalk:session data is null");
            return;
        }
        String callid = session.getCallID();
        if (TextUtils.isEmpty(callid))
        {
            return;
        }
        boolean notSameidAndlogicIsClose = (!callid.equals(
                CallService.getInstance().getCurrentCallID()) || CallConstants.STATUS_CLOSE
                == CallService.getInstance().getVoipStatus());
        if (notSameidAndlogicIsClose)
        {
            CallCommandParams param = new CallCommandParams();
            param.setCallID(callid);
            CallService.getInstance().callHangUp(param);
            return;
        }
        if (CallService.getInstance().getCallSessionMap().containsKey(callid))
        {
            SessionBean sessionOriginal = CallService.getInstance().getSimpleCallSession(callid);
            sessionOriginal.setVideoCall(session.isVideoCall());
            CallService.getInstance().putSessionBean(session.getCallID(), sessionOriginal);
        }
        else
        {
            CallService.getInstance().putSessionBean(callid, session);
        }

        if (session.isVideoCall())
        {
            CallService.getInstance().setVoipStatus(CallConstants.STATUS_VIDEOING);
        }
        else
        {
            int voipStatus = CallService.getInstance().getVoipStatus();
            if (CallConstants.STATUS_VIDEOINIT == voipStatus)
            {
                VideoDeviceManager.getIns().clearCallVideo();
            }

            CallService.getInstance().setVoipStatus(CallConstants.STATUS_TALKING);
        }
    }

    /**
     * processCallNtfModifyAlert
     *
     * @param sessionBean
     */
    private void processCallNtfModifyAlert(SessionBean sessionBean)
    {
        TUPLogUtil.i(TAG, "processCallNtfModifyAlert()");
        if (sessionBean == null)
        {
            TUPLogUtil.e(TAG, "session is null.");
            return;
        }
        boolean prepertyOfSessionBean = !isCurrentCall(sessionBean.getCallID());
        if (prepertyOfSessionBean)
        {
            return;
        }
        String oper = sessionBean.getOperation();
        int voipStatus = CallService.getInstance().getVoipStatus();
        if (ADD.equals(oper))
        {
            CallService.getInstance().setIsCanceled(false);
            boolean noCameraOrUntalking = (!VideoDeviceManager.getIns().isSupportVideo()
                    || CallConstants.STATUS_TALKING != voipStatus);
            if (noCameraOrUntalking)
            {
                CallCommandParams param = new CallCommandParams();
                param.setCallID(sessionBean.getCallID());
                CallService.getInstance().callRejectVideoUpdate(param);
                return;
            }
        }
        TupEventMgr.onCallEventNotify(CallConstants.MSG_CALL_MODIFY_UI,
                CallConstants.ModifyNoticeType.VoiceToVideo);
    }

    /**
     * isCurrentCall
     *
     * @param callid
     */
    private boolean isCurrentCall(String callid)
    {
        boolean ret = false;
        boolean isCurrentCallID = !TextUtils.isEmpty(callid) && !TextUtils.isEmpty(
                CallService.getInstance().getCurrentCallID()) && callid.equals(
                CallService.getInstance().getCurrentCallID());
        if (isCurrentCallID)
        {
            ret = true;
        }
        return ret;
    }

    /**
     * processCallNtfModified
     *
     * @param sessionBean
     */
    private void processCallNtfModified(final SessionBean sessionBean)
    {
        if (sessionBean == null || !isCurrentCall(sessionBean.getCallID()))
        {
            TUPLogUtil.e(TAG, "session is null or call is not currentCall.");
            return;
        }

        String oper = sessionBean.getOperation();
        int videoModifyState = sessionBean.getVideoModifyState();
        int voipStatus = CallService.getInstance().getVoipStatus();

        TUPLogUtil.i(TAG, "oper = " + oper + ",videoModifyState = " + videoModifyState
                + ",voipStatus = " + voipStatus);

        boolean isVideoClose = (0 == videoModifyState
                && CallConstants.STATUS_VIDEOING == voipStatus);
        boolean isActiveUpdateVideo = (1 == videoModifyState
                && CallConstants.STATUS_VIDEOINIT == voipStatus);
        boolean isVideoUpFailed = (0 == videoModifyState
                && CallConstants.STATUS_VIDEOINIT == voipStatus);
        boolean isVideoUpCanceled = (0 == videoModifyState
                && CallConstants.STATUS_TALKING == voipStatus);

        if (isVideoClose)
        {
            CallService.getInstance().setVideoCall(false);
            CallService.getInstance().clearVideoSurface();
            CallService.getInstance().setVoipStatus(CallConstants.STATUS_TALKING);
            TupEventMgr.onCallEventNotify(CallConstants.MSG_CALL_MODIFY_UI,
                    CallConstants.ModifyNoticeType.VideoToVoice);
        }
        else if (isActiveUpdateVideo)
        {
            if (sessionBean.getRemoteVideoState() != 0)
            {
                boolean isRemoteVideoClose = (sessionBean.getRemoteVideoState() == 1);
                TupEventMgr.onCallEventNotify(CallConstants.MSG_REMOTE_VIDEO_UPDATE, isRemoteVideoClose);
            }
            TUPLogUtil.i(TAG, "Upgrade To Video Call");
            CallService.getInstance().setVoipStatus(CallConstants.STATUS_VIDEOING);
            notifyCallActivityUpdateUI();
        }
        else if (isVideoUpFailed)
        {
            CallService.getInstance().setVoipStatus(CallConstants.STATUS_TALKING);
            notifyCallActivityUpdateUI();
            TupEventMgr.onCallEventNotify(CallConstants.MSG_CALL_MODIFY_UI,
                    CallConstants.ModifyNoticeType.ModifyRequestFalied);
        }
        else if (CallConstants.AUDIO_ADD_VIDEO_EVENT.equals(oper))
        {
            if (sessionBean.getRemoteVideoState() != 0)
            {
                notifyCallActivityUpdateRemoteVideo(sessionBean.getRemoteVideoState() == 1);
            }
            notifyCallActivityUpdateUI();
        }
        else if (isVideoUpCanceled)
        {

            TupEventMgr.onCallEventNotify(CallConstants.MSG_CALL_MODIFY_UI,
                    CallConstants.ModifyNoticeType.ModifyRequestCancel);
        }
    }

    /**
     * End to turn off video notification
     *
     * @param isRemoteVideoClose
     */
    private void notifyCallActivityUpdateRemoteVideo(boolean isRemoteVideoClose)
    {
        TupEventMgr.onCallEventNotify(CallConstants.MSG_REMOTE_VIDEO_UPDATE, isRemoteVideoClose);
    }

    /**
     * callRefreshView
     *
     * @param data
     */
    private void callRefreshView(CameraViewRefresh data)
    {
        TUPLogUtil.i(TAG, "refresh view()");
        boolean cameraDataStatus = null != data;
        if (cameraDataStatus)
        {
            if (data.getMediaType() == 2 || data.getMediaType() == 1)
            {
                if (data.getViewType() == 1)
                {
                    refreshView(true);
                }

                if (data.getViewType() == 2)
                {
                    refreshView(false);
                }
            }
        }
    }

    /**
     * refreshView
     *
     * @param isAdd
     */
    private void refreshView(boolean isAdd)
    {
        TUPLogUtil.i(TAG, "refresh_view");
        VideoDeviceManager.getIns().refreshLocalHide(isAdd);
    }

    /**
     * processCallNtfEnded
     *
     * @param sessionBean
     */
    private void processCallNtfEnded(SessionBean sessionBean)
    {
        if (sessionBean == null)
        {
            TUPLogUtil.e(TAG, "session is null.");
            return;
        }
        String callid = sessionBean.getCallID();
        if (TextUtils.isEmpty(callid))
        {
            TUPLogUtil.e(TAG, "callid is null.");
            return;
        }

        String reason = sessionBean.getReleaseReason();
        TUPLogUtil.i(TAG, "exceedingly call close : " + reason);
        reasonText = setCloseReasonText(reason, sessionBean);
    }


    /**
     * setCloseReasonText
     *
     * @param reason
     * @param sessionBean
     * @return closeReason
     */
    private String setCloseReasonText(String reason, SessionBean sessionBean)
    {
        reasonText = null;
        if (TextUtils.isEmpty(reason))
        {
            reasonText = TupEventMgr.getTupContext().getString(R.string.callfailed);
        }
        else
        {
            reasonText = TupEventMgr.getTupContext().getString(R.string.callfailed);
            if (CANCELLED.equals(reason))
            {
                reasonText = TupEventMgr.getTupContext().getString(R.string.cancelled);
            }
            else if (NOT_FOUND.equals(reason))
            {
                reasonText = TupEventMgr.getTupContext().getString(R.string.inaccessible);
            }
            else if (FORBIDDEN.equals(reason))
            {
                reasonText = TupEventMgr.getTupContext().getString(R.string.inaccessible);
                if (checkHasHeader(sessionBean))
                {
                    reasonText = VOIP_UNAVAILABLE;
                }
            }

            else if (BUSY.equals(reason))
            {
                reasonText = TupEventMgr.getTupContext().getString(R.string.oppositebusying);
            }
            else if (REJECT.equals(reason))
            {
                reasonText = TupEventMgr.getTupContext().getString(R.string.bereject);
            }
            else if (NETWORK_FAILURE.equals(reason))
            {
                reasonText = TupEventMgr.getTupContext().getString(R.string.callfailed);
            }
            else if (NO_ANSWER.equals(reason))
            {
                reasonText = TupEventMgr.getTupContext().getString(R.string.voipstatusnoreply);
            }
            else if (TEMP_UNVAILABLE.equals(reason))
            {
                reasonText = TupEventMgr.getTupContext().getString(R.string.voipstatusnocnt);
            }
            else if (MEIDA_NOT_ACCEPTABLE.equals(reason))
            {
                reasonText = TupEventMgr.getTupContext().getString(R.string.errorcall);
            }
        }
        return reasonText;
    }

    /**
     * voip Call area check
     *
     * @param sessionBean
     * @return true/false
     */
    private boolean checkHasHeader(SessionBean sessionBean)
    {
        boolean hasHeader = false;
        String reasonHeader = sessionBean.getReasonHeader();
        hasHeader = !TextUtils.isEmpty(reasonHeader)
                && (reasonHeader.contains(VOIP_UNAVAILABLE)
                || reasonHeader.replaceAll(CallConstants.BLANK_MARK,
                BLANK_STRING).contains(CAUSE_EQUAL_ONE));
        return hasHeader;
    }

    /**
     * call close
     *
     * @param sessionBean
     */
    private void processCallNtfClosed(SessionBean sessionBean)
    {
        TUPLogUtil.i(TAG, "processCallNtfClosed()");
        if (sessionBean == null)
        {
            TUPLogUtil.e(TAG, "sessionBean is null.");
            return;
        }
        callNtfClosed(sessionBean);
        String callid = sessionBean.getCallID();
        if (TextUtils.isEmpty(callid))
        {
            TUPLogUtil.e(TAG, "callid is null.");
            return;
        }

        TUPLogUtil.i(TAG, "callid->" + callid);
        if (!TextUtils.isEmpty(callid))
        {
            if (!TextUtils.isEmpty(reasonText))
            {
                TUPLogUtil.i(TAG, "processCallNtfClosed reason:" + reasonText);

                notifyCallActivityUpdateUI();
            }
            notifyHomeActivityUpdateUI(reasonText);
            TupEventMgr.onCallEventNotify(CallConstants.CALL_END_NOTIFY, null);
            CallService.getInstance().setVoipStatus(CallConstants.STATUS_CLOSE);
        }
        else
        {
            if (null != CallService.getInstance().getComingCallID()
                    && callid.equals(CallService.getInstance().getComingCallID()))
            {
                TUPLogUtil.i(TAG, "callid is not null");
                TupEventMgr.onCallEventNotify(CallConstants.MSG_NOTIFY_CALLCLOSE, callid);
                CallService.getInstance().setComingCallID(null);
            }
        }

        ConferenceService.getInstance().destroyConf();
        ConferenceService.getInstance().setAttendeeInfoList(new ArrayList<TupConfVCAttendeeInfo>());
        ConferenceService.getInstance().setGetDataFlag(false);

        TUPLogUtil.i(TAG, "processCallNtfClosed leave.");
    }

    /**
     * notify Activity call closed
     */
    private void notifyHomeActivityUpdateUI(String reasonText)
    {
        TupEventMgr.onCallEventNotify(CallConstants.VOIP_CALL_HANG_UP, reasonText);
    }

    /**
     * call Closed
     *
     * @param session
     */
    private void callNtfClosed(SessionBean session)
    {
        TUPLogUtil.i(TAG, "processCallNtfClosed");
        if (session == null)
        {
            TUPLogUtil.e(TAG, "session is null");
            return;
        }

        String callid = session.getCallID();
        if (TextUtils.isEmpty(callid))
        {
            TUPLogUtil.e(TAG, "callid is null.");
            return;
        }
        TUPLogUtil.i(TAG, "processCallNtfClosed callid:" + callid);

        if (isCurrentCall(callid))
        {
            CallService.getInstance().stopMediaPlay();
            CallService.getInstance().delRecordMapBycallID(callid);
            CallService.getInstance().resetData();

            CallService.getInstance().clearVideoSurface();
            CallService.getInstance().reset();
        }
        else
        {
            if (null != CallService.getInstance().getComingCallID()
                    && callid.equals(CallService.getInstance().getComingCallID()))
            {
                if (CallService.getInstance().getCallSessionMap().containsKey(callid))
                {
                    CallService.getInstance().delCallSessionMapByCallID(callid);
                }
                TUPLogUtil.i(TAG, "ComingCall is closed by other! callid:" + callid);

                CallService.getInstance().setComingCallID(null);
            }
            else
            {
                CallService.getInstance().delRecordMapBycallID(callid);
            }
        }
        TUPLogUtil.i(TAG, "processCallNtfClosed leave.");
    }
}
