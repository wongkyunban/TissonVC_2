package com.wong.tissonvc_2.service.call.data;

import com.wong.tissonvc_2.service.call.CallService;
import com.wong.tissonvc_2.service.call.VideoDeviceManager;
import com.wong.tissonvc_2.service.conf.DataConfService;
import com.wong.tissonvc_2.service.utils.TUPLogUtil;
import com.wong.tissonvc_2.service.utils.Tools;

import common.TupCallParam;
import common.VideoWndType;
import object.VideoRenderInfo;
import tupsdk.TupCall;


/**
 * The type Call session.
 * <p/>
 * CallSession
 * Upgrade for video calls, down to audio calls,
 * incoming calls, hang up calls and so on
 */
public class CallSession
{
    private static final String TAG = CallSession.class.getSimpleName();
    private static final String STAR_FLAG = "*";
    private static final String JING_FLAG = "#";
    private static final String VIDEO_CAPTURE_FILE_PATH = "black";

    private static final int AUDIO_CALL = 0;
    private static final int VIDEO_CALL = 1;
    private static final int MIC_MUTE = 0;
    private static final int SPEAKER_MUTE = 1;
    private static final int MUTE_STATE = 1;
    private static final int DIAL_CODE_TEN = 10;
    private static final int DIAL_CODE_ELEVEN = 11;
    private static final int DISPLAY_TYPE = 2;
    private static final int MIRROR_TYPE = 0;
    private static final int LOCAL_VIDEO_TYPE = 1;
    private static final int REMOTE_VIDEO_TYPE = 0;
    private static final int LOCAL_VIDEO_DISPLAY = 2;
    private static final int REMOTE_VIDEO_DISPLAY = 1;

    private CallService tupManager = null;
    private String callID = null;
    private VideoCaps videoCaps = null;
    private TupCall tupCall;


    /**
     * constructor.

     *
     * @param tupManager the tup manager
     * @param tupCall    the tup call
     */
    public CallSession(CallService tupManager, TupCall tupCall)
    {
        this.tupManager = tupManager;
        this.tupCall = tupCall;
        this.callID = String.valueOf(tupCall.getCallId());
        TUPLogUtil.i(TAG, "CallSession callID is " + this.callID + ",calleeNumber is " + tupCall.getToNumber()
                + ",callerNumber is " + tupCall.getTelNumber());
    }

    /**
     * Gets call id.
     *
     * @return the call id
     */
    public String getCallID()
    {
        return this.callID;
    }

    /**
     * setRenderCaps.
     *
     * @param param the param
     */
    public void setRenderCaps(CallCommandParams param)
    {
        this.videoCaps = param.getCaps();
        TUPLogUtil.i(TAG, "videoCaps is " + videoCaps);
    }

    /**
     * updateToVideo.
     *
     * @param param the param
     * @return strRet string
     */
    public String updateToVideo(CallCommandParams param)
    {
        this.setRenderCaps(param);
        this.updateVideoWindow();
        String strRet = this.sessionModify(true);
        return strRet;
    }

    /**
     * closeVideo.
     *
     * @return strRet string
     */
    public String closeVideo()
    {
        String strRet = this.sessionModify(false);
        return strRet;
    }

    /**
     * sessionModify.
     *
     * @param video
     * @return String.valueOf(iRet)
     */
    private String sessionModify(boolean video)
    {
        int iRet = 0;
        if (video)
        {
            iRet = this.tupCall.addVideo();
        }
        else
        {
            iRet = this.tupCall.delVideo();
        }

        TUPLogUtil.i(TAG, "sessionModify iRet: " + iRet);
        return String.valueOf(iRet);
    }

    /**
     * answer.
     *
     * @param param the param
     * @return String.valueOf(iRet) string
     */
    public String answer(CallCommandParams param)
    {
        boolean isVideo = param.isVideo();
        int iVideoCall = 0;
        if (isVideo)
        {
            iVideoCall = VIDEO_CALL;
            this.setRenderCaps(param);
            CallService.getInstance().setVideoOrient(Integer.parseInt(param.getCallID()),
                    VideoDeviceManager.FRONT_CAMERA);
        }

        int iRet = this.tupCall.acceptCall(iVideoCall);
        return String.valueOf(iRet);
    }

    /**
     * agreeVideoUpdate.
     *
     * @param param the param
     * @return String.valueOf(iRet) string
     */
    public String agreeVideoUpdate(CallCommandParams param)
    {
        int iRet = this.tupCall.replyAddVideo(VIDEO_CALL);
        if (TupCallParam.CALL_TUP_RESULT.TUP_SUCCESS == iRet)
        {
            this.setRenderCaps(param);
            this.updateVideoWindow();
        }

        return String.valueOf(iRet);
    }

    /**
     * disagreeVideoUpdate.
     *
     * @return String.valueOf(iRet) string
     */
    public String disagreeVideoUpdate()
    {
        int iRet = this.tupCall.replyAddVideo(AUDIO_CALL);
        return String.valueOf(iRet);
    }

    /**
     * hangup.
     *
     * @return String.valueOf(iRet) string
     */
    public String hangup()
    {
        int iRet = this.tupCall.endCall();
        boolean ret = TupCallParam.CALL_TUP_RESULT.TUP_SUCCESS == iRet;
        if (ret)
        {
            this.tupManager.removeCallSession(this.callID);
        }
        if (null != this.videoCaps)
        {
            this.videoCaps.setIsCloseLocalCamera(false);
        }
        TUPLogUtil.i(TAG, "hangup ret:" + iRet);

        DataConfService service = DataConfService.getInstance();
        if (service != null && service.getConf() != null)
        {
            service.leaveConf();
            service.releaseConf();
        }

        return String.valueOf(iRet);
    }

    /**
     * mute.
     *
     * @param param the param
     * @return String.valueOf(iRet) string
     */
    public String mute(CallCommandParams param)
    {
        int type = param.getMuteType();
        boolean mute = param.isNeedMute();
        int iRet = 0;
        byte iOn = 0;
        if (mute)
        {
            iOn = MUTE_STATE;
        }

        if (MIC_MUTE == type)
        {
            iRet = this.tupCall.mediaMuteMic(iOn);
        }
        else if (SPEAKER_MUTE == type)
        {
            iRet = this.tupCall.mediaMuteSpeak(iOn);
        }
        return String.valueOf(iRet);
    }

    /**
     * redial.
     *
     * @param param the param
     * @return String.valueOf(iRet) string
     */
    public String redial(CallCommandParams param)
    {
        String tone = param.getDialCode();
        int dialCode = Tools.stringToInt(tone);
        if (STAR_FLAG.equals(tone))
        {
            dialCode = DIAL_CODE_TEN;
        }
        else if (JING_FLAG.equals(tone))
        {
            dialCode = DIAL_CODE_ELEVEN;
        }

        int iRet = this.tupCall.sendDTMF(dialCode);
        return String.valueOf(iRet);
    }

    /**
     * localCameraControl.
     *
     * @param param the param
     * @return result string
     */
    public String localCameraControl(CallCommandParams param)
    {
        this.videoCaps = param.getCaps();
        int iRet = 0;
        String result = "";

        if (this.videoCaps == null)
        {
            TUPLogUtil.e(TAG,"localCameraControl videoCaps is null.");
            return result;
        }

        if (this.videoCaps.isCloseLocalCamera())
        {
            iRet = this.tupManager.setVideoCaptureFile(Integer.parseInt(param.getCallID()),
                    VIDEO_CAPTURE_FILE_PATH);
//            CallService.getInstance().controlRenderVideo(CallConstants.MMV_SWITCH_LOCAL
//                    | CallConstants.MMV_SWITCH_CAMERA, false);
        }
        else
        {
//            CallService.getInstance().controlRenderVideo(CallConstants.MMV_SWITCH_LOCAL
//                    | CallConstants.MMV_SWITCH_CAMERA, true);
            tupManager.setVideoOrient(Integer.parseInt(param.getCallID()),
                    VideoDeviceManager.FRONT_CAMERA);
        }
        result = String.valueOf(iRet);
        return result;
    }

    /**
     * reload closeCamera method
     *
     * @param param
     * @param path  the path of bmp picture
     * @return
     */
    public String localCameraControl(CallCommandParams param, String path)
    {
        this.videoCaps = param.getCaps();
        int iRet = 0;
        String result = "";
        if (this.videoCaps.isCloseLocalCamera())
        {
            iRet = this.tupManager.setVideoCaptureFile(Integer.parseInt(param.getCallID()),
                    path);
        }
        else
        {
            tupManager.setVideoOrient(Integer.parseInt(param.getCallID()),
                    VideoDeviceManager.FRONT_CAMERA);
        }
        result = String.valueOf(iRet);
        return result;
    }


    /**
     * cameraRotation.
     *
     * @param param the param
     */
    public void cameraRotation(CallCommandParams param)
    {
        videoCaps = param.getCaps();
        tupCall.setCaptureRotation(this.videoCaps.getCameraIndex(),
                this.videoCaps.getCameraRotation());
        tupCall.setDisplayRotation(VideoWndType.local, this.videoCaps.getLocalRoate());
        tupCall.setDisplayRotation(VideoWndType.remote, this.videoCaps.getRemoteRoate());
        setVideoRenderInfo(VideoWndType.local);
    }


    /**
     * setVideoRenderInfo.
     *
     * @param type the type
     */
    public void setVideoRenderInfo(VideoWndType type)
    {
        VideoRenderInfo setVRI = new VideoRenderInfo();
        setVRI.setRederType(type);
        if (VideoWndType.local == type)
        {
            setVRI.setUlDisplaytype(DISPLAY_TYPE);
            setVRI.setUlMirrortype(this.videoCaps.getMirrorType());
        }
        else if (VideoWndType.remote == type)
        {
            setVRI.setUlDisplaytype(this.videoCaps.getDisplayType());
            setVRI.setUlMirrortype(MIRROR_TYPE);
        }
        this.tupCall.setMobileVideoRender(setVRI);
    }

    /**
     * updateVideoWindow.
     */
    private void updateVideoWindow()
    {
        String callId = String.valueOf(this.tupCall.getCallId());
        this.tupManager.operateVideoWindow(LOCAL_VIDEO_TYPE,
                this.videoCaps.getPlaybackLocal(), callId, LOCAL_VIDEO_DISPLAY);
        this.tupManager.operateVideoWindow(REMOTE_VIDEO_TYPE,
                this.videoCaps.getPlaybackRemote(), callId, REMOTE_VIDEO_DISPLAY);
    }

    /**
     * alertingCall.
     *
     * @return String.valueOf(iRet) string
     */
    public String alertingCall()
    {
        if (null == this.tupCall)
        {
            TUPLogUtil.e(TAG, "tupCall is null, return");
            return String.valueOf(TupCallParam.CALL_TUP_RESULT.TUP_FAIL);
        }
        else
        {
            int iRet = this.tupCall.alertingCall();
            return String.valueOf(iRet);
        }
    }

    public void setVideoCaps(VideoCaps videoCaps) {
        this.videoCaps = videoCaps;
    }

}