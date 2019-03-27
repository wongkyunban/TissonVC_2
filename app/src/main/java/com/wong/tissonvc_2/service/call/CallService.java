package com.wong.tissonvc_2.service.call;

import android.content.res.Configuration;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.wong.tissonvc_2.service.TupEventHandler;
import com.wong.tissonvc_2.service.TupEventMgr;
import com.wong.tissonvc_2.service.call.data.CallCommandParams;
import com.wong.tissonvc_2.service.call.data.CallSession;
import com.wong.tissonvc_2.service.call.data.OneKeyJoinConfParam;
import com.wong.tissonvc_2.service.call.data.SessionBean;
import com.wong.tissonvc_2.service.call.data.VideoCaps;
import com.wong.tissonvc_2.service.common.CallConstants;
import com.wong.tissonvc_2.service.login.data.LoginParams;
import com.wong.tissonvc_2.service.utils.TUPLogUtil;
import com.wong.tissonvc_2.service.utils.Tools;
import com.wong.tissonvc_2.ui.utils.LayoutUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import common.BFCPSetupType;
import common.FloorCtrl;
import common.TransferType;
import common.TupBool;
import common.TupCallParam;
import common.VideoWndType;
import object.BFCPParam;
import object.BFCPPortRange;
import object.TupCallCfgAudioVideo;
import object.TupCallCfgBFCP;
import object.TupCallCfgMedia;
import object.TupCallCfgSIP;
import object.TupDevice;
import object.VideoRenderInfo;
import object.VideoWndInfo;
import tupsdk.TupCall;
import tupsdk.TupCallManager;

/**
 * The type Tup call mgr.
 * <p/>
 * CallService
 * tup call manager Call control management
 */
public final class CallService
{
    private static final String TAG = CallService.class.getSimpleName();
    /**
     * Call operation lock
     */
    private static final Object LOCK_CALL_OPERATION = new Object();
    private static CallService callService;
    private static final int SIP_PORT = 5060;
    private static final int AUDIO_AEC = 1;
    private static final int VIDEO_REMOTE_TYPE = 0;
    private static final int VIDEO_LOCAL_TYPE = 1;
    private static final int VIDEO_OTHER_TYPE = 2;

    private TupCallManager tupCallManager;
    private LoginParams loginParams = LoginParams.getInstance();
    private Map<String, CallSession> calls = new ConcurrentHashMap();
    private TupCallCfgAudioVideo tpCllCfgAdVd = new TupCallCfgAudioVideo();
    private boolean isCallCoimg = false;

    private int startMediaPlayResult = 0;

    public int getStartMediaPlayResult()
    {
        return startMediaPlayResult;
    }

    public void setStartMediaPlayResult(int startMediaPlayResult)
    {
        this.startMediaPlayResult = startMediaPlayResult;
    }

    /**
     * VOIP voice interface state, the default initial state that hangs off
     */
    private int voipStatus = CallConstants.STATUS_CLOSE;

    // Save calls in ID
    private String comingCallID = null;

    // save the callid and callsession corresponding to map
    private HashMap<String, SessionBean> callSessionMap = new HashMap<>(0);

    /**
     * Call ID
     */
    private String currentCallID = null;

    /**
     * Video update request has been rejected
     */
    private boolean isCanceled = false;

    /**
     * Is a video call
     */
    private boolean isVideoCall = false;

    /**
     * Current call number
     */
    private String callNumber = "";

    /**
     * Local microphone mute
     */
    private boolean microphoneMute = false;

    /**
     * Create or update window type local window
     */
    private int typeLocal = 1;

    /**
     * Create or update window type - remote window
     */
    private int typeRemote = 0;

    /**
     * Supported audio route list
     */
    private List<Integer> supportAudioRouteList = new ArrayList<>(0);

    private int playHandle = -1;

    private List<TupDevice> videoDevices = new ArrayList<TupDevice>();
    private List<TupDevice> micDevices = new ArrayList<TupDevice>();
    private List<TupDevice> speakDevices = new ArrayList<TupDevice>();

    /**
     * Instantiates a new Tup call mgr.
     */
    private CallService()
    {
        this.tupCallManager = new TupCallManager(TupEventHandler.getTupEventHandler(),
                TupEventMgr.getTupContext());
        TupCallEventManager.getTupCallEventManager();
    }

    /**
     * Singleton pattern
     *
     * @return the instance
     */
    public static synchronized CallService getInstance()
    {
        if (callService == null)
        {
            callService = new CallService();
            TUPLogUtil.i(TAG, "ins is null, create new ins.");
        }
        return callService;
    }


    public int getRegState()
    {
        return tupCallManager.getRegState();
    }

    public int getReasonCode()
    {
        return tupCallManager.getReasonCode();
    }

    public String getUserNum()
    {
        return tupCallManager.getUserNum();
    }

    public void setCfgBFCP(TupCallCfgBFCP cfgBFCP)
    {
        tupCallManager.setCfgBFCP(cfgBFCP);
    }

    /**
     * tupSetCfgMedia 81
     *
     * @param isOpen
     */
    public void setAssistStreamEnable(boolean isOpen)
    {
        tupCallManager.setAssistStreamEnable(isOpen);
    }

    /**
     * tupSetCfgMedia 82
     *
     * @param value
     */
    public void setAssistStreamDataCaptureFunction(int value)
    {
        tupCallManager.setAssistStreamDataCaptureFunction(value);
    }

    public int startAssistData(int value)
    {
        return tupCallManager.startAssistData(value);
    }

    public int stopAssistData(int value)
    {
        return tupCallManager.stopAssistData(value);
    }

    /**
     * tupSetCfgMedia 83
     *
     * @param var1
     */
    public void enableCorporate_directory(TupBool var1)
    {
        tupCallManager.enableCorporate_directory(var1);
    }

    /**
     * tupSetCfgMedia 85
     *
     * @param var1
     */
    public void enablePrecence(TupBool var1)
    {
        tupCallManager.enablePrecence(var1);
    }

    public int dataControl(int callId, int operation, int module)
    {
        return tupCallManager.dataControl(callId, operation, module);
    }

    /**
     * Gets support audio route.
     *
     * @param num the num
     * @return the support audio route
     */
    public Integer getSupportAudioRoute(int num)
    {
        return supportAudioRouteList.get(num);
    }

    /**
     * Gets support audio route list.
     *
     * @return the support audio route list
     */
    public List<Integer> getSupportAudioRouteList()
    {
        return supportAudioRouteList;
    }

    /**
     * Add support audio route.
     *
     * @param num               the num
     * @param supportAudioRoute the support audio route
     */
    public void addSupportAudioRoute(int num, Integer supportAudioRoute)
    {
        supportAudioRouteList.add(num, supportAudioRoute);
    }

    /**
     * Remove support audio route.
     *
     * @param supportAudioRoute the support audio route
     */
    public void removeSupportAudioRoute(Integer supportAudioRoute)
    {
        supportAudioRouteList.remove(supportAudioRoute);
    }

    /**
     * Gets coming call id.
     *
     * @return the coming call id
     */
    public String getComingCallID()
    {
        return this.comingCallID;
    }

    /**
     * Sets coming call id.
     *
     * @param comingCallID the coming call id
     */
    public void setComingCallID(String comingCallID)
    {
        this.comingCallID = comingCallID;
    }

    /**
     * Put session bean.
     *
     * @param callID      the call id
     * @param sessionBean the session bean
     */
    public void putSessionBean(String callID, SessionBean sessionBean)
    {
        callSessionMap.put(callID, sessionBean);
    }

    /**
     * Gets call session map.
     *
     * @return the call session map
     */
    public HashMap<String, SessionBean> getCallSessionMap()
    {
        return callSessionMap;
    }

    /**
     * Gets simple call session.
     *
     * @param callID the call id
     * @return the simple call session
     */
    public SessionBean getSimpleCallSession(String callID)
    {
        return callSessionMap.get(callID);
    }

    /**
     * Gets current call id.
     *
     * @return the current call id
     */
    public String getCurrentCallID()
    {
        return currentCallID;
    }

    /**
     * Sets current call id.
     *
     * @param currentCallID the current call id
     */
    public void setCurrentCallID(String currentCallID)
    {
        this.currentCallID = currentCallID;
    }

    /**
     * Is call closed boolean.
     *
     * @return the boolean
     */
    public boolean isCallClosed()
    {
        return (currentCallID == null);
    }

    /**
     * Sets is canceled.
     *
     * @param isCanceled the is canceled
     */
    public void setIsCanceled(boolean isCanceled)
    {
        this.isCanceled = isCanceled;
    }

    /**
     * Set whether to be a video call
     *
     * @param isVideocall the is videocall
     */
    public void setVideoCall(boolean isVideocall)
    {
        this.isVideoCall = isVideocall;
    }

    /**
     * Gets whether for a video call
     *
     * @return the isVideoCall
     */
    public boolean isVideoCall()
    {
        return isVideoCall;
    }

    /**
     * Gets call number.
     *
     * @return the call number
     */
    public String getCallNumber()
    {
        return callNumber;
    }

    /**
     * Sets call number.
     *
     * @param callNumber the call number
     */
    public void setCallNumber(String callNumber)
    {
        this.callNumber = callNumber;
    }

    /**
     * Is microphone mute boolean.
     *
     * @return the boolean
     */
    public boolean isMicrophoneMute()
    {
        return microphoneMute;
    }

    /**
     * Sets microphone mute.
     *
     * @param microphoneMute the microphone mute
     */
    public void setMicrophoneMute(boolean microphoneMute)
    {
        this.microphoneMute = microphoneMute;
    }


    /**
     * Sets play handle.
     *
     * @param playHandle the play handle
     */
    public void setPlayHandle(int playHandle)
    {
        this.playHandle = playHandle;
    }

    /**
     * Gets is call coming.
     *
     * @return the is call coming
     */
    public boolean getIsCallComing()
    {
        return this.isCallCoimg;
    }

    /**
     * Sets is call coming.
     *
     * @param isCallCoimg the is call coimg
     */
    public void setIsCallComing(boolean isCallCoimg)
    {
        this.isCallCoimg = isCallCoimg;
    }

    /**
     * Gets voip status.
     *
     * @return the voip status
     */
    public int getVoipStatus()
    {
        return voipStatus;
    }

    /**
     * Sets voip status.
     *
     * @param voipStatus the voip status
     */
    public void setVoipStatus(int voipStatus)
    {
        this.voipStatus = voipStatus;
    }


    /**
     * Remove call session.
     *
     * @param callId the call id
     */
    public void removeCallSession(String callId)
    {
        if (this.calls != null && !TextUtils.isEmpty(callId) && this.calls.containsKey(callId))
        {
            this.calls.remove(callId);
        }
    }

    /**
     * Put call session.
     *
     * @param callId      the call id
     * @param callSession the call session
     */
    public void putCallSession(String callId, CallSession callSession)
    {
        if (this.calls != null && !TextUtils.isEmpty(callId))
        {
            this.calls.put(callId, callSession);
        }
    }

    /**
     * Gets call session.
     *
     * @param callId the call id
     * @return the call session
     */
    public CallSession getCallSession(String callId)
    {
        if (this.calls != null && !TextUtils.isEmpty(callId))
        {
            return this.calls.get(callId);
        }
        return null;
    }

    /**
     * Clear all call session.
     */
    public void clearAllCallSession()
    {
        if (this.calls != null)
        {
            this.calls.clear();
        }
    }

    /**
     * init tup call service
     */
    public void tupCallInit()
    {
        //load so
        tupCallManager.loadLibForTE();
        //start tup log
        String logFilePath = Environment.getExternalStorageDirectory() + File.separator + "VCLOG";
        TUPLogUtil.i(TAG, "start tup log.");
        isLogPathExist(logFilePath);
        tupCallManager.logStart(3, 5 * 1024, 4, logFilePath);
//        tupCallManager.logStop();
//        tupCallManager.hmeLogInfo(0,0,0,0);
        //set android environment
        tupCallManager.setAndroidObjects();

        //call init
        tupCallManager.callInit();

        //get video mic speak device infos
        getDeviceInfos();
    }

    /**
     * unInit tup call service
     */
    public void tupCallUninit()
    {
        //call uninit
        tupCallManager.callUninit();
    }

    /**
     * Gets tup call manager.
     *
     * @return the tup call manager
     */
    public TupCallManager getTupCallManager()
    {
        return tupCallManager;
    }

    /**
     * operateVideoWindow
     *
     * @param type    the type
     * @param index   the index
     * @param callId  the call id
     * @param display the display
     * @return String.valueOf(iRet) string
     */
    public String operateVideoWindow(int type, int index, String callId, int display)
    {
        int iRet;
        if (null == callId)
        {
            iRet = this.createVideoWindow(type, index, display);
        }
        else
        {
            iRet = this.updateVideoWindow(type, index, callId, display);
        }

        return String.valueOf(iRet);
    }

    /**
     * Set local video handle (front, rear camera)
     *
     * @param index front camera   1 Rear camera   0
     * @return ret video index
     */
    public int setMediaVideoIndex(int index)
    {
        int ret = this.tupCallManager.mediaSetVideoIndex(index);
        return ret;
    }

    /**
     * Sets media mic index.
     *
     * @param micIndex the mic index
     * @return the media mic index
     */
    public int setMediaMicIndex(int micIndex)
    {
        int ret = tupCallManager.mediaSetMicIndex(micIndex);
        return ret;
    }

    /**
     * Sets media speak index.
     *
     * @param speakIndex the speak index
     * @return the media speak index
     */
    public int setMediaSpeakIndex(int speakIndex)
    {
        int ret = tupCallManager.mediaSetSpeakIndex(speakIndex);
        return ret;
    }

    /**
     * Sets media speak volume.
     *
     * @param deviceType the device type
     * @param intvolume  the intvolume
     * @return the media speak volume
     */
    public int setMediaSpeakVolume(int deviceType, int intvolume)
    {
        int ret = tupCallManager.mediaSetSpeakVolume(deviceType, intvolume);
        return ret;
    }

    /**
     * Gets media speak volume.
     *
     * @return the media speak volume
     */
    public int getMediaSpeakVolume()
    {
        int ret = tupCallManager.mediaGetSpeakVolume();
        return ret;
    }

    /**
     * setOrientParams
     *
     * @param caps the VideoCaps config
     */
    public void setOrientParams(VideoCaps caps)
    {
        TupCallCfgAudioVideo tpCallCfgAudioVideo = new TupCallCfgAudioVideo();
        tpCallCfgAudioVideo.setVideoCaptureRotation(caps.getCameraRotation());
        tpCallCfgAudioVideo.setVideoDisplayRotation(caps.getLocalRoate(), VideoWndType.local);
        tpCallCfgAudioVideo.setVideoDisplayRotation(caps.getRemoteRoate(), VideoWndType.remote);

        TUPLogUtil.i(TAG, "caps.getCameraRotation()" + caps.getCameraRotation());
        TUPLogUtil.i(TAG, "caps.getLocalRoate()" + caps.getLocalRoate());
        TUPLogUtil.i(TAG, "caps.getRemoteRoate()" + caps.getRemoteRoate());
        setTpCallCfgAudioVideo(tpCallCfgAudioVideo);
    }

    /**
     * setVideoRenderInfo
     *
     * @param caps the VideoCaps config
     * @param type the VideoWndType config
     */
    public void setVideoRenderInfo(VideoCaps caps, VideoWndType type)
    {
        TupCallCfgMedia cfgMedia = new TupCallCfgMedia();
        VideoRenderInfo vRI = new VideoRenderInfo();
        vRI.setRederType(type);
        if (type == VideoWndType.local)
        {
            vRI.setUlDisplaytype(2);
            if (caps == null)
            {
                vRI.setUlMirrortype(2);
            }
            else
            {
                vRI.setUlMirrortype(caps.getMirrorType());
            }
        }
        else if (type == VideoWndType.remote)
        {
            if (caps == null)
            {
                vRI.setUlDisplaytype(1);
            }
            else
            {
                vRI.setUlDisplaytype(caps.getDisplayType());
            }

            vRI.setUlMirrortype(0);
        }

        cfgMedia.setVideoRenderInfo(vRI);
        setMediaParams(cfgMedia);
    }

    /**
     * Sets video capture file.
     *
     * @param callId the call id
     * @param path   the path
     * @return the video capture file
     */
    public int setVideoCaptureFile(int callId, String path)
    {
        int ret = this.tupCallManager.setVideoCaptureFile(callId, path);
        return ret;
    }

    /**
     * Notification call interface refresh
     *
     * @param answer the answer
     */
    public void notifyCallViewUpdate(boolean answer)
    {
        TUPLogUtil.i(TAG, "notifyCallViewUpdate()");
        TupEventMgr.onCallEventNotify(CallConstants.MSG_CALL_UPDATE_UI, answer);
    }

    /**
     * make call
     *
     * @param param the param
     * @return CallSession call session
     */
    public CallSession call(final CallCommandParams param)
    {
        if (param == null)
        {
            TUPLogUtil.i(TAG, "param is null");
            return null;
        }
        VideoCaps vcaps = null;
        String toNum = this.getToNumber(param);
        TUPLogUtil.i(TAG, "CallNumber----" + toNum);
        final TupCall call = this.makeCall(toNum, param.isVideo());
        if (call != null)
        {
            final CallSession callSession = new CallSession(this, call);
            this.currentCallID = String.valueOf(call.getCallId());
            if (param.isVideo())
            {
                if (isVideoCall)
                {
                    vcaps = VideoDeviceManager.getIns().initCallVideo();
                    setVoipStatus(CallConstants.STATUS_VIDEOINIT);
                    operateVideoWindow(typeLocal, vcaps.getPlaybackLocal(), currentCallID,
                            VideoCaps.DisplayType.DISPLAY_TYPE_CLIPPING);
                    operateVideoWindow(typeRemote, vcaps.getPlaybackRemote(), currentCallID,
                            VideoCaps.DisplayType.DISPLAY_TYPE_BORDER);
                }
                param.setCaps(vcaps);
                callSession.setRenderCaps(param);
            }
            this.calls.put(String.valueOf(call.getCallId()), callSession);
            return callSession;
        }
        else
        {
            return null;
        }
    }

    public CallSession joinConf(OneKeyJoinConfParam param)
    {
        if (param == null)
        {
            TUPLogUtil.i(TAG, "param is null");
            return null;
        }
        VideoCaps vcaps = null;

        int isVideo = param.isVideoJoinConf() ? 1 : 0;

        final TupCall call = this.tupCallManager.makeAccessReservedConfCall(isVideo, param.getConfID(), param.getAccessCode(), param.getConfPaswd());
        if (call != null)
        {
            final CallSession callSession = new CallSession(this, call);
            this.currentCallID = String.valueOf(call.getCallId());
            if (param.isVideoJoinConf())
            {
                if (isVideoCall)
                {
                    vcaps = VideoDeviceManager.getIns().initCallVideo();
                    setVoipStatus(CallConstants.STATUS_VIDEOINIT);
                    operateVideoWindow(typeLocal, vcaps.getPlaybackLocal(), currentCallID,
                            VideoCaps.DisplayType.DISPLAY_TYPE_CLIPPING);
                    operateVideoWindow(typeRemote, vcaps.getPlaybackRemote(), currentCallID,
                            VideoCaps.DisplayType.DISPLAY_TYPE_BORDER);
                }
                //param.setCaps(vcaps);
                callSession.setVideoCaps(vcaps);
            }
            this.calls.put(String.valueOf(call.getCallId()), callSession);
            return callSession;
        }
        else
        {
            return null;
        }
    }

    /**
     * Video control int.
     *
     * @param callId    the call id
     * @param operation the operation
     * @param module    the module
     * @return the int
     */
    public int videoControl(int callId, int operation, int module)
    {
        return this.tupCallManager.vedioControl(callId, operation, module);
    }

    /**
     * Sets audio route.
     *
     * @param audioSwitch the audio switch
     * @return the audio route
     */
    public boolean setAudioRoute(int audioSwitch)
    {
        return this.tupCallManager.setMobileAudioRoute(audioSwitch) == 0;
    }

    /**
     * Gets audio route.
     *
     * @return the audio route
     */
    public int getAudioRoute()
    {
        int rote = this.tupCallManager.getMobileAudioRoute();
        return rote;
    }

    /**
     * hangUp
     *
     * @param param the param
     * @return strRet string
     */
    public String callHangUp(CallCommandParams param)
    {
        CallSession callSession = null;
        String strRet = null;
        if (param.getCallID() == null)
        {
            return null;
        }
        callSession = this.calls.get(param.getCallID());
        strRet = callSession.hangup();

        return strRet;
    }

    /**
     * answer
     *
     * @param param the param
     * @return strRet string
     */
    public String answer(CallCommandParams param)
    {
        CallSession callSession = null;
        String strRet = null;
        if (param.getCallID() == null)
        {
            return null;
        }
        callSession = this.calls.get(param.getCallID());
        strRet = callSession.answer(param);

        return strRet;
    }

    /**
     * updateToVideo
     *
     * @param param the param
     * @return strRet string
     */
    public String callUpdateToVideo(CallCommandParams param)
    {
        CallSession callSession = null;
        String strRet = null;
        if (param.getCallID() == null)
        {
            return null;
        }
        callSession = this.calls.get(param.getCallID());
        strRet = callSession.updateToVideo(param);

        return strRet;
    }

    /**
     * rejectVideoUpdate
     *
     * @param param the param
     * @return strRet string
     */
    public String callRejectVideoUpdate(CallCommandParams param)
    {
        CallSession callSession = null;
        String strRet = null;
        if (param.getCallID() == null)
        {
            return null;
        }
        callSession = this.calls.get(param.getCallID());
        strRet = callSession.disagreeVideoUpdate();

        return strRet;
    }

    /**
     * agreeVideoUpdate
     *
     * @param param the param
     * @return strRet string
     */
    public String callAgreeVideoUpdate(CallCommandParams param)
    {
        CallSession callSession = null;
        String strRet = null;
        if (param.getCallID() == null)
        {
            return null;
        }
        callSession = this.calls.get(param.getCallID());
        strRet = callSession.agreeVideoUpdate(param);
        return strRet;
    }


    /**
     * Sets video orient.
     *
     * @param callID      the call id
     * @param cameraIndex the camera index
     * @return the video orient
     */
    public boolean setVideoOrient(int callID, int cameraIndex)
    {
        int result = 0;
        int choice = 0;
        final TupCall tupCall = new TupCall();
        tupCall.setCallId(callID);

        Configuration configuration = TupEventMgr.getTupContext().getResources().getConfiguration();
        boolean isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT;
        if (isPortrait)
        {
            choice = 1;
        }
        else
        {
            choice = 2;
        }

        if (cameraIndex == VideoDeviceManager.FRONT_CAMERA)
        {
            result = tupCallManager.setMboileVideoOrient(callID, cameraIndex, choice, 3, 0, 2);
//            if (isPortrait)
//            {
//                tupCall.setCaptureRotation(cameraIndex, 3);
//            }
        }

        if (cameraIndex == VideoDeviceManager.BACK_CAMERA)
        {
            result = tupCallManager.setMboileVideoOrient(callID, cameraIndex, choice, 1, 0, 2);
//            if (isPortrait)
//            {
//                tupCall.setCaptureRotation(cameraIndex, 1);
//            }
        }

        return (result == 0);
    }

    /**
     * closeVideo
     *
     * @param param the param
     * @return strRet string
     */
    public String callCloseVideo(CallCommandParams param)
    {
        CallSession callSession = null;
        String strRet = null;
        if (param.getCallID() == null)
        {
            return null;
        }
        callSession = this.calls.get(param.getCallID());
        strRet = callSession.closeVideo();

        return strRet;
    }

    /**
     * alertingCall
     *
     * @param param the param
     * @return strRet string
     */
    public String alertingCall(CallCommandParams param)
    {
        CallSession callSession = null;
        String strRet = null;
        if (param.getCallID() == null)
        {
            return null;
        }
        callSession = this.calls.get(param.getCallID());
        strRet = callSession.alertingCall();

        return strRet;
    }




    /**
     * Local microphone mute
     *
     * @param isRefer the is refer
     * @param isMute  the is mute
     * @return true /false
     */
    public boolean setLocalMute(boolean isRefer, boolean isMute)
    {
        boolean result = false;
        if (isRefer)
        {
            result = mute(isMute, 0);
        }
        else
        {
            result = mute(!isMicrophoneMute(), 0);
        }
        if (result)
        {
            setMicrophoneMute(isMute);
            return true;
        }
        return false;
    }

    /**
     * Mute / mute
     *
     * @param mute the mute
     * @param type the type
     * @return true /false
     */
    public boolean mute(boolean mute, int type)
    {
        CallSession callSession = null;
        String strRet = null;
        if (TextUtils.isEmpty(currentCallID))
        {
            return false;
        }

        CallCommandParams param = new CallCommandParams();
        param.setCallID(currentCallID);
        param.setMuteType(type);
        param.setNeedMute(mute);

        callSession = this.calls.get(currentCallID);
        strRet = callSession.mute(param);
        boolean bRet = parseString(strRet);
        TUPLogUtil.i(TAG, "mute ismuteAction:" + mute);
        return bRet;
    }

    /**
     * Speaker mute
     *
     * @param isMute the is mute
     * @return true /false
     */
    public boolean oratorMute(boolean isMute)
    {
        boolean result = mute(isMute, 1);
        return result;
    }

    /**
     * Gets the current support for the first route
     * that is being used by the router
     *
     * @return the audio route list
     */
    public List<Integer> getAudioRouteList()
    {
        if (callService == null)
        {
            TUPLogUtil.e(TAG, "CallLogic is null !");
            return null;
        }
        List<Integer> audioRouteList = new ArrayList<Integer>(supportAudioRouteList);
        return audioRouteList;
    }

    /**
     * launchCall
     *
     * @param fromPhone   the from phone
     * @param domain      the domain
     * @param isVideoCall the is video call
     */
    public synchronized void launchCall(String fromPhone, String domain, boolean isVideoCall)
    {
        String callCodeString = null;
        if (CallConstants.STATUS_CLOSE != getVoipStatus())
        {
            TUPLogUtil.e(TAG, "dialCall() failed --- getVoipStatus not in close status");
            return;
        }
        TUPLogUtil.i(TAG, "tophone:" + fromPhone + ",domain:" + domain);

        setVideoCall(isVideoCall);
        setVoipStatus(isVideoCall ? CallConstants.STATUS_VIDEOINIT : CallConstants.STATUS_CALLING);

        CallCommandParams params = new CallCommandParams();
        params.setCallNumber(fromPhone);
        params.setDomain(domain);
        params.setVideo(isVideoCall);

        CallSession callsession = call(params);

        processCallSession(fromPhone, isVideoCall, callsession);
    }

    public synchronized void launchJoinConf(OneKeyJoinConfParam joinConfParam)
    {
        String callCodeString = null;

        if (CallConstants.STATUS_CLOSE != getVoipStatus())
        {
            TUPLogUtil.e(TAG, "dialCall() failed --- getVoipStatus not in close status");
            return;
        }
        //TUPLogUtil.i(TAG, "tophone:" + fromPhone + ",domain:" + domain);

        boolean isVideoCall = joinConfParam.isVideoJoinConf();

        setVideoCall(isVideoCall);
        setVoipStatus(isVideoCall ? CallConstants.STATUS_VIDEOINIT : CallConstants.STATUS_CALLING);

//        CallCommandParams params = new CallCommandParams();
//        params.setCallNumber(fromPhone);
//        params.setDomain(domain);
//        params.setVideo(isVideoCall);

        CallSession callsession = joinConf(joinConfParam);

        processCallSession(joinConfParam.getAccessCode(), isVideoCall, callsession);
    }

    /**
     * Process call session.
     *
     * @param fromPhone   the from phone
     * @param isVideoCall the is video call
     * @param callsession the callsession
     */
    public void processCallSession(String fromPhone, boolean isVideoCall, CallSession callsession)
    {
        String callCodeString = null;
        String callid = null;
        if (null == callsession)
        {
            TUPLogUtil.e(TAG, "callsession is null");
            return;
        }

        callid = callsession.getCallID();

        boolean isNullofCallID = (TextUtils.isEmpty(callid) || isFail(callid));
        if (isNullofCallID)
        {
            TUPLogUtil.i(TAG, "dialcall excute callcommand fail!callid=" + callid);
            setCallNumber(fromPhone);
            reset();
            callCodeString = callid;
        }
        else
        {
            resetAudioRoute(isVideoCall);

            this.currentCallID = callid;
            TUPLogUtil.i(TAG, "diallcall: callID=" + currentCallID
                    + ",isVideoCall=" + isVideoCall);
            callCodeString = CallConstants.CALL_SUCCESS;
        }

        if ((TextUtils.isEmpty(callCodeString) || isFail(callCodeString)))
        {
            setVoipStatus(CallConstants.STATUS_CLOSE);
        }
    }

    private boolean isFail(String retCode)
    {
        return retCode == null || retCode.equals("-1") || retCode.equals("-2") || retCode.equals("-7");
    }

    /**
     * Clear temporary data
     */
    public void reset()
    {
        setCallNumber("");
        setVoipStatus(CallConstants.STATUS_CLOSE);
        setVideoCall(false);
    }

    /**
     * Reset Data
     */
    public void resetData()
    {
        setCurrentCallID(null);
        setMicrophoneMute(false);
    }

    /**
     * Reset audio routing,
     * set the underlying default using the speaker
     *
     * @param isVideo the is video
     */
    public void resetAudioRoute(boolean isVideo)
    {
        TUPLogUtil.i(TAG, "resetAudioRoute");
        if (1 >= getSupportAudioRouteList().size())
        {
            TUPLogUtil.i(TAG, "only one route");
            return;
        }

        boolean hasEarphone = (getSupportAudioRouteList().contains(CallConstants.TYPE_EARPHONE));
        int resetRoute = (((!LayoutUtil.isPhone()) || isVideo) && !hasEarphone)
                ? CallConstants.TYPE_LOUD_SPEAKER : CallConstants.TYPE_AUTO;
        if (setAudioRoute(resetRoute))
        {
            TUPLogUtil.i(TAG, "route has been reset to " + resetRoute);
            refreshAudioRoute();
        }
    }

    /**
     * refreshAudioRoute
     */
    public void refreshAudioRoute()
    {
        int route = getAudioRoute();
        TUPLogUtil.i(TAG, "refreshAudioRoute route:" + route);

        switch (route)
        {
            case CallConstants.TYPE_TELRECEIVER:
                if (LayoutUtil.isPhone())
                {
                    modSupportAudioRouteList(CallConstants.TYPE_TELRECEIVER);
                }
                else
                {
                    modSupportAudioRouteList(CallConstants.TYPE_LOUD_SPEAKER);
                }
                break;
            case CallConstants.TYPE_LOUD_SPEAKER:
                modSupportAudioRouteList(CallConstants.TYPE_LOUD_SPEAKER);
                break;
            case CallConstants.TYPE_EARPHONE:
                modSupportAudioRouteList(route);
                break;
            default:
                break;
        }
        if (CallConstants.TYPE_LOUD_SPEAKER != getSupportAudioRoute(0))
        {
            if (getSupportAudioRouteList().contains(CallConstants.TYPE_TELRECEIVER))
            {
                removeSupportAudioRoute(CallConstants.TYPE_TELRECEIVER);
                addSupportAudioRoute(0, CallConstants.TYPE_TELRECEIVER);
            }
            if (getSupportAudioRouteList().contains(CallConstants.TYPE_EARPHONE))
            {
                removeSupportAudioRoute(CallConstants.TYPE_EARPHONE);
                addSupportAudioRoute(0, CallConstants.TYPE_EARPHONE);
            }
        }
        TUPLogUtil.i(TAG, "getAudioRoute:" + route);

    }

    /**
     * closeCall
     *
     * @return true /false
     */
    public synchronized boolean closeCall()
    {
        TUPLogUtil.i(TAG, "closeCall exec ");
        synchronized (LOCK_CALL_OPERATION)
        {
            TUPLogUtil.i(TAG, "closeCall enter.callId->" + currentCallID);
            if (TextUtils.isEmpty(currentCallID))
            {
                TUPLogUtil.e(TAG, "currentCallID is null, notify call end.closeCall leave.");
                return false;
            }

            CallCommandParams closeParam = new CallCommandParams();
            closeParam.setCallID(currentCallID);

            // Release video data
            clearVideoSurface();
            delRecordMapBycallID(currentCallID);
            reset();
            resetData();
            String strRet = callHangUp(closeParam);
            TUPLogUtil.i(TAG, "hangup the call " + strRet);
            boolean bRet = parseString(strRet);
            TUPLogUtil.i(TAG, "closeCall leave.");
            return bRet;
        }
    }

    /**
     * Clear video data
     */
    public void clearVideoSurface()
    {
        int voipStatus = getVoipStatus();
        TUPLogUtil.i(TAG, "clearVideoSurface() - voipStatus ->" + voipStatus);
        boolean voipStatusIsTrue = (voipStatus == CallConstants.STATUS_VIDEOING
                || voipStatus == CallConstants.STATUS_VIDEOACCEPT
                || voipStatus == CallConstants.STATUS_VIDEOINIT);
        if (voipStatusIsTrue)
        {
            TUPLogUtil.i(TAG, "clearVideoSurface()");
            VideoDeviceManager.getIns().clearCallVideo();
        }
    }

    /**
     * Del record map bycall id.
     *
     * @param callid the callid
     */
    public void delRecordMapBycallID(String callid)
    {
        delCallSessionMapByCallID(callid);
    }

    /**
     * Del call session map by call id.
     *
     * @param callid the callid
     */
    public void delCallSessionMapByCallID(String callid)
    {
        if (getCallSessionMap().containsKey(callid))
        {
            getCallSessionMap().remove(callid);
        }
    }

    /**
     * Force to close all calls for cancellation
     */
    public void forceCloseCall()
    {
        TUPLogUtil.i(TAG, "forceCloseCall exec ");

        if (!TextUtils.isEmpty(currentCallID))
        {
            TUPLogUtil.i(TAG, "forceCloseCall currentCallID");
            closeCall();
        }
        if (callSessionMap.size() <= 0)
        {
            return;
        }
        Set<String> keySet = callSessionMap.keySet();
        HashSet<String> setContain = new HashSet<String>(0);
        setContain.addAll(keySet);
        Iterator<String> itor = setContain.iterator();
        String key = null;
        boolean hasNext = itor.hasNext();
        while (hasNext)
        {
            key = itor.next();
            if (key.equalsIgnoreCase(comingCallID))
            {
                TUPLogUtil.i(TAG, "comingCallID" + comingCallID);
                stopMediaPlay();
                rejectCall(comingCallID);
            }
            else
            {
                closeInnerCall(key);
            }
            hasNext = itor.hasNext();
        }
        setContain.clear();
        callSessionMap.clear();
        clearAllCallSession();
    }

    /**
     * Reject incoming call
     *
     * @param callId the call id
     * @return true /false
     */
    public boolean rejectCall(String callId)
    {
        TUPLogUtil.i(TAG, "rejectCall()");
        comingCallID = null;
        boolean bRet = false;
        if (TextUtils.isEmpty(callId))
        {
            return bRet;
        }
        CallCommandParams param = new CallCommandParams();
        param.setCallID(callId);
        String sRet = callHangUp(param);
        bRet = parseString(sRet);
        comingCallID = null;
        delCallSessionMapByCallID(callId);

        stopMediaPlay();

        TUPLogUtil.i(TAG, "callId=" + callId);
        return bRet;
    }

    /**
     * Receive a call, answer a call, including audio and video calls
     *
     * @param callid  the callid
     * @param isVideo the is video
     * @return true :success，false:false
     */
    public boolean answerCall(String callid, boolean isVideo)
    {
        TupEventMgr.onCallEventNotify(CallConstants.MSG_CALL_UPDATE_UI, true);
        comingCallID = null;
        VideoCaps caps = null;
        boolean ret = false;

        if (isVideo)
        {
            caps = VideoDeviceManager.getIns().initCallVideo();
        }
        if (callid.isEmpty())
        {
            return ret;
        }
        resetAudioRoute(isVideo);
        CallCommandParams param = new CallCommandParams();
        param.setCallID(callid);
        param.setVideo(isVideo);
        if (isVideo && null != caps)
        {
            param.setCaps(caps);
            operateVideoWindow(typeLocal, caps.getPlaybackLocal(), callid,
                    VideoCaps.DisplayType.DISPLAY_TYPE_CLIPPING);
            operateVideoWindow(typeRemote, caps.getPlaybackRemote(), callid,
                    VideoCaps.DisplayType.DISPLAY_TYPE_BORDER);
        }
        TUPLogUtil.i(TAG, "callid:" + callid + ",callAnswer:" + ret);
        String sRet = answer(param);

        this.currentCallID = callid;
        SessionBean session = callSessionMap.get(callid);
        comingCallID = null;
        ret = parseString(sRet);
        if (!ret || null == session)
        {
            resetData();
            return ret;
        }
        setCallNumber(session.getCallerNumber());
        setVideoCall(isVideo);
        if (isVideo)
        {
            setVoipStatus(CallConstants.STATUS_VIDEOACCEPT);
        }
        else
        {
            setVoipStatus(CallConstants.STATUS_TALKING);
        }

        TUPLogUtil.i(TAG, "callId=" + callid + "isVideo=" + isVideo);

        stopMediaPlay();

        return ret;
    }

    /**
     * closeCallControl
     */
    public synchronized void closeCallControl()
    {
        TUPLogUtil.i(TAG, "closeCall()");
        String currentCallID = getCurrentCallID();
        if (TextUtils.isEmpty(currentCallID))
        {
            TupEventMgr.onCallEventNotify(CallConstants.CALL_END_NOTIFY, null);
            return;
        }
        TupEventMgr.onCallEventNotify(CallConstants.CALL_END_NOTIFY, null);
        setVoipStatus(CallConstants.STATUS_CLOSE);
    }

    /**
     * closeVideo
     *
     * @return true /false
     */
    public boolean closeVideo()
    {
        boolean ret = false;
        if (TextUtils.isEmpty(currentCallID))
        {
            return ret;
        }
        CallCommandParams params = new CallCommandParams();
        params.setCallID(this.currentCallID);
        String sRet = callCloseVideo(params);
        ret = parseString(sRet);
        TUPLogUtil.i(TAG, "close Video ret-> " + ret);

        if (!ret)
        {
            TupEventMgr.onCallEventNotify(CallConstants.MSG_CLOSE_VIDEO_FAIL, null);
        }
        return ret;
    }

    /**
     * Agree with video upgrade
     *
     * @return true :success,false;false
     */
    public boolean agreeUpgradeVideoControl()
    {
        TUPLogUtil.i(TAG, "agreeUpgradeVideo exec");
        boolean ret = false;
        VideoCaps caps = VideoDeviceManager.getIns().initCallVideo();
        TUPLogUtil.i(TAG, "getCameraRotation -> " + caps.getCameraRotation());

        if (TextUtils.isEmpty(currentCallID))
        {
            return ret;
        }
        CallCommandParams callCommandParams = new CallCommandParams();
        callCommandParams.setCaps(caps);
        callCommandParams.setCallID(currentCallID);
        String sRet = callAgreeVideoUpdate(callCommandParams);

        ret = parseString(sRet);
        if (ret)
        {
            setVoipStatus(CallConstants.STATUS_VIDEOING);
            resetAudioRoute(true);
            TupEventMgr.onCallEventNotify(CallConstants.MSG_CALL_MODIFY_UI,
                    CallConstants.ModifyNoticeType.defaultType);
        }
        return ret;
    }

    /**
     * Reject video upgrade
     *
     * @return true / false
     */
    public boolean rejectUpgradeVideo()
    {
        if (isCanceled)
        {
            TUPLogUtil.i(TAG, "rejectUpgradeVideo: isCanceled");
            return false;
        }

        boolean ret = false;
        if (TextUtils.isEmpty(currentCallID))
        {
            return ret;
        }
        CallCommandParams param = new CallCommandParams();
        param.setCallID(currentCallID);
        String sRet = callRejectVideoUpdate(param);
        ret = parseString(sRet);
        isCanceled = true;
        TUPLogUtil.i(TAG, "disAgreeUpgradeVideo:");
        return ret;
    }

    /**
     * upgradeVideo
     *
     * @return true /false
     */
    public boolean upgradeVideo()
    {
        boolean ret = false;

        VideoCaps caps = VideoDeviceManager.getIns().initCallVideo();

        if (null == caps || TextUtils.isEmpty(currentCallID))
        {
            return ret;
        }

        CallCommandParams params = new CallCommandParams();
        params.setCallID(currentCallID);
        params.setCaps(caps);
        String sRet = callUpdateToVideo(params);
        ret = parseString(sRet);
        TUPLogUtil.i(TAG, "upgrade Video Success " + ret);
        if (ret)
        {
            setVoipStatus(CallConstants.STATUS_VIDEOINIT);
            notifyCallViewUpdate(false);
        }
        else
        {
            setVoipStatus(CallConstants.STATUS_TALKING);
            TupEventMgr.onCallEventNotify(CallConstants.MSG_LOW_BW_UPDATE_FAIL, null);
        }
        return ret;
    }

    /**

     * @param code the code
     * @return true /false
     */
    public void redial(String code)
    {
        CallSession callSession = null;
        boolean isEmpty = (TextUtils.isEmpty(currentCallID) || TextUtils.isEmpty(code));
        if (isEmpty)
        {
            return;
        }
        TUPLogUtil.i(TAG, "redial:" + code);
        CallCommandParams param = new CallCommandParams();
        param.setDialCode(code);
        callSession = this.calls.get(currentCallID);
        callSession.redial(param);
    }

    /**
     * Camera rotation angle setting
     *
     * @param cameraRotation the camera rotation
     * @param localRotation  the local rotation
     */
    public void setCameraDegree(int cameraRotation, int localRotation)
    {
        if (currentCallID == null)
        {
            return;
        }
        CallCommandParams params = new CallCommandParams();
        CallSession callSession = null;
        VideoCaps caps = VideoDeviceManager.getIns().getCaps();
        caps.setCameraRotation(cameraRotation % 4);
        caps.setLocalRoate(localRotation % 4);
        params.setCaps(caps);
        params.setCallID(currentCallID);
        callSession = this.calls.get(currentCallID);
        callSession.cameraRotation(params);
    }

    /**
     * controlRenderVideo
     *
     * @param renderModule the render module
     * @param isStart      the is start
     * @return true /false
     */
    public boolean controlRenderVideo(int renderModule, boolean isStart)
    {
        int mediaSwitch = isStart ? CallConstants.VIDEO_CONTROL_START : CallConstants.VIDEO_CONTROL_STOP;
        TUPLogUtil.i(TAG, "mediaSwitch->" + mediaSwitch);
        int iRet = videoControl(Tools.stringToInt(currentCallID), mediaSwitch, renderModule);

        return TupCallParam.CALL_TUP_RESULT.TUP_SUCCESS == iRet;
    }

    /**
     * local camera switch
     *
     * @param isCloseAction the is close action
     * @return true /false
     */
    public boolean localCamera(boolean isCloseAction)
    {
        CallSession callSession = null;
        String strRet = null;
        boolean ret = false;
        VideoCaps caps = VideoDeviceManager.getIns().getCaps();
        caps.setIsCloseLocalCamera(isCloseAction);

        if (TextUtils.isEmpty(currentCallID))
        {
            return ret;
        }

        CallCommandParams param = new CallCommandParams();
        param.setCallID(currentCallID);
        param.setCaps(caps);
        callSession = this.calls.get(param.getCallID());
        strRet = callSession.localCameraControl(param);

        boolean bRet = parseString(strRet);
        TUPLogUtil.i(TAG, "close local camera isCloseAction -> "
                + isCloseAction + " isSuccess -> " + bRet);
        return bRet;
    }


    public boolean localCamera(boolean isCloseAction, String path)
    {
        CallSession callSession = null;
        String strRet = null;
        boolean ret = false;
        VideoCaps caps = VideoDeviceManager.getIns().getCaps();
        caps.setIsCloseLocalCamera(isCloseAction);

        if (TextUtils.isEmpty(currentCallID))
        {
            return ret;
        }

        CallCommandParams param = new CallCommandParams();
        param.setCallID(currentCallID);
        param.setCaps(caps);
        callSession = this.calls.get(param.getCallID());
        strRet = callSession.localCameraControl(param, path);

        boolean bRet = parseString(strRet);
        TUPLogUtil.i(TAG, "close local camera isCloseAction -> "
                + isCloseAction + " isSuccess -> " + bRet + ",--path:" + path);
        return bRet;
    }


    /**
     * Open or close the main collection
     *
     * @param isStart the is start
     * @return true /false
     */
    public boolean controlVideoCapture(boolean isStart)
    {
        int mediaSwitch = isStart ? CallConstants.VIDEO_CONTROL_START : CallConstants.VIDEO_CONTROL_STOP;
        TUPLogUtil.i(TAG, "mediaSwitch->" + mediaSwitch);
        int iRet = videoControl(Tools.stringToInt(currentCallID), mediaSwitch, CallConstants.MMV_SWITCH_CAMERA);

        return TupCallParam.CALL_TUP_RESULT.TUP_SUCCESS == iRet;
    }

    /**
     * openLocalPreview
     * Add the local video screen to the interface layout
     *
     * @param localVideoHandle the localVideo handle
     */
    public void openLocalPreview(int localVideoHandle)
    {
        TUPLogUtil.i(TAG, "openLocalPreview()");
        String val = localVideoHandle + "";
        if (null != val && !"".equals(val))
        {
            int openPreviewResult = openPreview(localVideoHandle, VideoDeviceManager.FRONT_CAMERA);
            if (TupCallParam.CALL_TUP_RESULT.TUP_SUCCESS == openPreviewResult)
            {
                TUPLogUtil.i(TAG, "openPreview is success");
            }
            else
            {
                TUPLogUtil.i(TAG, "openPreview is fail");
            }
            TUPLogUtil.i(TAG, "openPreviewResult" + "|" + openPreviewResult);
        }
    }

    /**
     * Open preview int.
     *
     * @param handle the handle
     * @param index  the index
     * @return the int
     */
    public int openPreview(int handle, int index)
    {
        TUPLogUtil.i(TAG, "handle" + "|" + handle);
        TUPLogUtil.i(TAG, "index" + "|" + index);
        return tupCallManager.openPreview(handle, index);
    }

    /**
     * Close local preview.
     */
    public void closeLocalPreview()
    {
        tupCallManager.closePreview();
    }


    /**
     * Gets device infos.
     */
    public void getDeviceInfos()
    {
        videoDevices = getDeviceInfo(TupCallParam.CALL_E_DEVICE_TYPE.CALL_E_CALL_DEVICE_VIDEO);
        micDevices = getDeviceInfo(TupCallParam.CALL_E_DEVICE_TYPE.CALL_E_CALL_DEVICE_MIC);
        speakDevices = getDeviceInfo(TupCallParam.CALL_E_DEVICE_TYPE.CALL_E_CALL_DEVICE_SPEAK);
    }

    /**
     * Gets device info.
     *
     * @param deviceType the device type
     * @return the device info
     */
    public List<TupDevice> getDeviceInfo(int deviceType)
    {
        String str = deviceType + "";
        if (TextUtils.isEmpty(str))
        {
            TUPLogUtil.e(TAG, "deviceType is null");
        }
        return tupCallManager.tupGetDevices(deviceType);
    }

    /**
     * Start media play int.
     *
     * @param loops     the loops
     * @param mediaFile the media file
     * @return the int
     */
    public int startMediaPlay(int loops, String mediaFile)
    {
        TUPLogUtil.i(TAG, "-------startMediaPlay");
        if (startMediaPlayResult > 0)
        {
            TUPLogUtil.i(TAG, "-------startMediaPlay startMediaPlayResult=" + startMediaPlayResult);
            return 1;
        }
        String val = loops + "";
        TUPLogUtil.i(TAG, "loops" + "|" + loops + ",mediaFile" + "|" + mediaFile);
        if (null != mediaFile && !"".equals(val))
        {
            startMediaPlayResult = this.tupCallManager.mediaStartplay(loops, mediaFile);
        }
        TUPLogUtil.i(TAG, "startMediaPlayResult" + "|" + startMediaPlayResult);
        return startMediaPlayResult;
    }

    /**
     * Stop media play.
     */
    public void stopMediaPlay()
    {
        TUPLogUtil.i(TAG, "-------stopMediaPlay playHandle=" + playHandle);
        if (playHandle < 0)
        {
            return;
        }
        String val = playHandle + "";
        int stopResult = 0;
        TUPLogUtil.i(TAG, "stopMediaPlay" + "|" + playHandle);
        if (!"".equals(val))
        {
            stopResult = tupCallManager.mediaStopplay(playHandle);
            TUPLogUtil.i(TAG, "stopResult->" + stopResult);
        }
        playHandle = -1;
    }


    /**
     * Gets android version.
     *
     * @return the android version
     */
    public int getAndroidVersion()
    {
        return Build.VERSION.SDK_INT;
    }

    /**
     * set Audio config and video config
     */
    private void configAudioAndVideo()
    {
        // 降噪参数ANR设置，取值1,2,3,4
        tpCllCfgAdVd.setAudioAnr(2);

        // 回声消除参数AEC设置，默认开启
        tpCllCfgAdVd.setAudioAec(1);

        // 自动增益补偿功能AGC设置，根据参数下发确定
        tpCllCfgAdVd.setAudioAgc(1);

        // 视频编码质量，默认设置 15，不可修改
        tpCllCfgAdVd.setVideoCoderQuality(15);

        // 视频关键帧间隔，默认设置 5，不可修改
        tpCllCfgAdVd.setVideoKeyframeinterval(5);

        // 初始化网络丢包百分率，用于设置给HME控制fec初始冗余，默认设置 100，不可修改
        tpCllCfgAdVd.setVideoNetLossRate(5);

        // SIP呼叫是否启用2833模式,取值为1使用2833模式、0使用dtmf模式，默认为1
        tpCllCfgAdVd.setAudioDtmfMode(1);

        // 设置抗丢包冗余
        tpCllCfgAdVd.setVideoErrorcorrecting(TupBool.TUP_TRUE);

        tpCllCfgAdVd.setVideoForceSingleH264Pt(TupBool.TUP_TRUE);

        // 设置视频编码优先级，目前默认设置 106
        tpCllCfgAdVd.setVideoCodec("106");

        // 设置默认分辨率，最小分辨率到SQCIF
        tpCllCfgAdVd.setVideoFramesize(8, 1, 11);

        // 设置默认码率、最大带宽
        tpCllCfgAdVd.setVideoDatarate(512, 0, 0, 512);

        // 设置默认帧率和最小帧率
        tpCllCfgAdVd.setVideoFramerate(25, 10);

        // 设置视频Ars信息
        tpCllCfgAdVd.setVideoArs(1, 1, 0, 1, 1, 1);

        tupCallManager.setCfgAudioAndVideo(tpCllCfgAdVd);
        tupCallManager.setMboileVideoOrient(0, 1, 1, 0, 0, 0);
    }


    /**
     * Tup config.
     */
    public void tupConfig()
    {
        configAudioAndVideo();
        configSip();
        configMeida();

        TupCallCfgBFCP bfcp = new TupCallCfgBFCP();
        BFCPParam bparam = new BFCPParam();
        bparam.setTransType(TransferType.UDP);
        bparam.setFloorCtrl(FloorCtrl.SEND_AND_RECEIVE);
        bparam.setUiSetup(BFCPSetupType.ACTIVEANDPASSITIVE);
        bfcp.setBfcpParam(bparam);

        BFCPPortRange bfcpport = new BFCPPortRange();
        bfcpport.setMinPort(5060 + 10);
        bfcpport.setMaxPort(5060 + 30);
        bfcp.setBfcpPortRange(bfcpport);

        bfcp.setDataCodec("106");
        bfcp.setDataArq(TupBool.TUP_FALSE);
        bfcp.setDataArs(0, 0, 1, 1, 1, 1);
        bfcp.setDataErrorcorrecting(TupBool.TUP_TRUE);
        bfcp.setDataKeyframeinterval(10);
        bfcp.setBFCPDataNetLossRate(100);
        setCfgBFCP(bfcp);
    }

    /**
     * Add audio routing Pad default: Phone receiver, speaker: speaker
     */
    public void addDefaultAudioRoute()
    {
        supportAudioRouteList.clear();
        supportAudioRouteList.add(CallConstants.TYPE_LOUD_SPEAKER);
        if (LayoutUtil.isPhone())
        {
            supportAudioRouteList.add(CallConstants.TYPE_TELRECEIVER);
        }
    }

    /**
     * Set SIP related config parameters
     */
    private void configSip()
    {
        TupCallCfgSIP tupCallCfgSIP = new TupCallCfgSIP();

        // ip port
        tupCallCfgSIP.setServerRegPrimary(loginParams.getRegisterServerIp(),
                Tools.stringToInt(loginParams.getServerPort()));
        tupCallCfgSIP.setServerProxyPrimary(loginParams.getProxyServerIp(),
                Tools.stringToInt(loginParams.getServerPort()));

        // localip
        tupCallCfgSIP.setNetAddress(loginParams.getLocalIpAddress());
        TUPLogUtil.i(TAG, "localIpAddress->" + loginParams.getLocalIpAddress());
        tupCallCfgSIP.setSipSessionTimerEnable(TupBool.TUP_TRUE);
        tupCallCfgSIP.setEnvUseagent(loginParams.getUserAgent());
        tupCallCfgSIP.setEnvProductType(TupCallParam.CALL_E_PRODUCT_TYPE.CALL_E_PRODUCT_TYPE_MOBILE);
        tupCallCfgSIP.setRegSub(TupBool.TUP_TRUE);
        tupCallCfgSIP.setSipSupport100rel(TupBool.TUP_TRUE);
//        enableCorporate_directory(TupBool.TUP_TRUE);
        tupCallCfgSIP.setSipTransMode(loginParams.getTransportMode());
        if (loginParams.getTransportMode() == 1)
        {
            tupCallCfgSIP.setSipPort(SIP_PORT + 1);
        }
        else
        {
            tupCallCfgSIP.setSipPort(SIP_PORT);
        }
        tupCallManager.setCfgSIP(tupCallCfgSIP);
    }

    /**
     * Set global media related config
     */
    private void configMeida()
    {
        TupCallCfgMedia tupCallCfgMedia = new TupCallCfgMedia();
        tupCallCfgMedia.setEnableBFCP(TupBool.TUP_TRUE);
        switch (loginParams.getSrtpMode())
        {
            case 0:
                tupCallCfgMedia.setMediaSrtpMode(TupCallParam.CALL_E_SRTP_MODE.CALL_E_SRTP_MODE_DISABLE);
                break;
            case 1:
                tupCallCfgMedia.setMediaSrtpMode(TupCallParam.CALL_E_SRTP_MODE.CALL_E_SRTP_MODE_OPTION);
                break;
            case 2:
                tupCallCfgMedia.setMediaSrtpMode(TupCallParam.CALL_E_SRTP_MODE.CALL_E_SRTP_MODE_FORCE);
                break;
            default:
                break;
        }
        tupCallManager.setCfgMedia(tupCallCfgMedia);
        setAssistStreamEnable(true);
        setAssistStreamDataCaptureFunction(1);

    }

    /**
     * Set global media related confign parameters
     *
     * @param cfgMediaParam
     */
    private void setMediaParams(TupCallCfgMedia cfgMediaParam)
    {
        this.tupCallManager.setCfgMedia(cfgMediaParam);
    }

    private void setTpCallCfgAudioVideo(TupCallCfgAudioVideo tpCallCfgAudioVideo)
    {
        this.tupCallManager.setCfgAudioAndVideo(tpCallCfgAudioVideo);
    }

    /**
     * isLogPathExist
     *
     * @param logFilePath
     */
    private void isLogPathExist(String logFilePath)
    {
        File file = new File(logFilePath);
        if (!file.exists() && !file.isDirectory())
        {
            file.mkdir();
        }
    }

    /**
     * Update video window
     *
     * @param type
     * @param index
     * @param callIdStr
     * @param displayType Y
     */
    private int updateVideoWindow(int type, int index, String callIdStr, int displayType)
    {
        VideoWndType vType = VideoWndType.local;
        switch (type)
        {
            case VIDEO_REMOTE_TYPE:
                vType = VideoWndType.remote;
                break;
            case VIDEO_LOCAL_TYPE:
                vType = VideoWndType.local;
                break;
            case 3:
                vType = VideoWndType.bfcp;
                break;
            default:
                vType = VideoWndType.local;
                break;
        }

        int callId = Tools.stringToInt(callIdStr);
        VideoWndInfo vInfo = new VideoWndInfo();
        vInfo.setUlDisplayType(displayType);
        vInfo.setUlRender(index);
        vInfo.setVideowndType(vType);
        return this.tupCallManager.updateVideoWindow(vInfo, callId);
    }

    /**
     * createVideoWindow
     */
    private int createVideoWindow(int type, int index, int displayType)
    {
        return this.tupCallManager.createVideoWindow(type, index, displayType);
    }

    /**
     * Get the number of the calling party from the call data set
     *
     * @param param
     * @return strNumber
     */
    private String getToNumber(CallCommandParams param)
    {
        String domain = param.getDomain();
        if (TextUtils.isEmpty(domain))
        {
            domain = this.loginParams.getDomain();
        }

        String outgoingAccessCode = this.loginParams.getOutgoingAccessCode();
        String countryCode = this.loginParams.getCountryCode();
        String strNumber = null;
        String strTo = param.getCallNumber();
        boolean isIpCall = Tools.isIPAddress(strTo);
        if (isIpCall)
        {
            strNumber = strTo;
            if (!strTo.contains(":"))
            {
                strNumber = strTo + ":" + "5060";
            }
        }
        else if (strTo.indexOf("+") == 0)
        {
            if (strTo.contains("@"))
            {
                strNumber = "sip:" + outgoingAccessCode + strTo;
            }
            else
            {
                strNumber = "sip:" + outgoingAccessCode + strTo + "@" + domain;
            }
        }
        else if (TextUtils.isEmpty(countryCode))
        {
            if (strTo.contains("@"))
            {
                strNumber = "sip:" + outgoingAccessCode + strTo;
            }
            else
            {
                strNumber = "sip:" + outgoingAccessCode + strTo + "@" + domain;
            }
        }
        else if (strTo.contains("@"))
        {
            strNumber = "sip:" + outgoingAccessCode + countryCode + Tools.remove(strTo, 0, '0');
        }
        else
        {
            strNumber = "sip:" + outgoingAccessCode + countryCode
                    + Tools.remove(strTo, 0, '0') + "@" + domain;
        }

        return strNumber;
    }


    /**
     * make call
     *
     * @param toNumber Caller number
     * @param isVideo  is video call
     * @return TupCall
     */
    private TupCall makeCall(String toNumber, boolean isVideo)
    {
        TUPLogUtil.i(TAG, "make call ----" + toNumber);
        return isVideo ? this.tupCallManager.makeVideoCall(toNumber)
                : this.tupCallManager.makeCall(toNumber);
    }

    /**
     * modSupportAudioRouteList
     *
     * @param route
     */
    private void modSupportAudioRouteList(int route)
    {
        if (getSupportAudioRouteList().contains(route))
        {
            removeSupportAudioRoute(route);
        }
        addSupportAudioRoute(0, route);
    }

    /**
     * Delete hold in call
     *
     * @param callid
     * @return true/false
     */
    private boolean closeInnerCall(String callid)
    {
        String sRet = "";
        CallCommandParams parm = new CallCommandParams();
        parm.setCallID(callid);
        sRet = callHangUp(parm);
        TUPLogUtil.i(TAG, "closeCall:" + sRet);
        boolean bRet = parseString(sRet);
        return bRet;
    }

    /**
     * parseString
     *
     * @param retStr
     * @return ture/false
     */
    private boolean parseString(String retStr)
    {
        boolean bRet = false;
        if (null != retStr && "0".equals(retStr))
        {
            bRet = true;
        }
        return bRet;
    }

    public void renderCreate()
    {
        VideoDeviceManager.getIns().onCreate();
    }

    public void renderDestroy()
    {
        if (null != VideoDeviceManager.getIns())
        {
            VideoDeviceManager.getIns().onDestroy();
            TUPLogUtil.i(TAG, "onCallClosed destroy.");
        }
    }

    public SurfaceView getRemoteCallView()
    {
        return VideoDeviceManager.getIns().getRemoteCallView();
    }

    public VideoCaps initCallVideo()
    {
        return VideoDeviceManager.getIns().initCallVideo();
    }

    public boolean isInitCallVideo()
    {
        return VideoDeviceManager.getIns().isInit();
    }

    public int getVideoHandle()
    {
        return VideoDeviceManager.getIns().getVideoHandle();
    }

    public SurfaceView getLocalHideView()
    {
        return VideoDeviceManager.getIns().getLocalHideView();
    }

    public void addViewToContainer(View videoView, ViewGroup videoContain)
    {
        VideoDeviceManager.getIns().addViewToContainer(videoView, videoContain);
    }

    public void clearCallVideo()
    {
        VideoDeviceManager.getIns().clearCallVideo();
    }

    public void removeView()
    {
        VideoDeviceManager.getIns().removeView();
    }

    public void addRenderToContain(ViewGroup localViewContain,
                                   ViewGroup remoteViewContain)
    {
        VideoDeviceManager.getIns().addRenderToContain(localViewContain, remoteViewContain);
    }

    public int getCameraCapacty(int cameraIndex)
    {
        return VideoDeviceManager.getIns().getCameraCapacty(cameraIndex);
    }

    public void setCameraIndex(int cameraIndex)
    {
        VideoDeviceManager.getIns().getCaps().setCameraIndex(cameraIndex);
    }

}
