package com.wong.tissonvc_2.service.common;

/**
 * The type Call constants.
 * <p/>
 * CallConstants
 * Constant and enumeration data
 * types for call related events
 */
public final class CallConstants
{
    /**
     * The constant CALL_SUCCESS.
     */
    public static final String CALL_SUCCESS = "0";

    /**
     * The constant TYPE_AUTO.
     */
    public static final int TYPE_AUTO = 0;
    /**
     * The constant TYPE_TELRECEIVER.
     */
    public static final int TYPE_TELRECEIVER = 0;
    /**
     * The constant TYPE_EARPHONE.
     */
    public static final int TYPE_EARPHONE = 4;
    /**
     * The constant TYPE_LOUD_SPEAKER.
     */
    public static final int TYPE_LOUD_SPEAKER = 1;
    /**
     * The constant CALL_CLOSE_BACK_NOTIFY.
     * call close
     */
    public static final int CALL_CLOSE_BACK_NOTIFY = 1145;

    /**
     * The constant CALL_END_NOTIFY.
     */
    public static final int CALL_END_NOTIFY = 141;

    /**
     * The constant CALL_LOGOUT_NOTIFY.
     */
    public static final int CALL_LOGOUT_NOTIFY = 253;

    /**
     * The constant STATUS_CLOSE.
     * Hang up state
     */
    public static final int STATUS_CLOSE = 0;

    /**
     * The constant STATUS_TALKING.
     * DedicateMode-ActiveMode
     */
    public static final int STATUS_TALKING = 1;

    /**
     * The constant STATUS_CALLING.
     * Call status
     */
    public static final int STATUS_CALLING = 3;

    /**
     * The constant STATUS_VIDEOINIT.
     * Video call initialization
     */
    public static final int STATUS_VIDEOINIT = 8;

    /**
     * The constant STATUS_VIDEOING.
     * Video call
     */
    public static final int STATUS_VIDEOING = 9;

    /**
     * The constant STATUS_VIDEOACCEPT.
     * Video call to accept
     */
    public static final int STATUS_VIDEOACCEPT = 10;

    /**
     * The constant COMING_AUDIO_CALL.
     * CallComing
     */
    public static final int COMING_AUDIO_CALL = 0;

    /**
     * The constant COMING_VIDEO_CALL.
     * Video call
     */
    public static final int COMING_VIDEO_CALL = 4;

    /**
     * The constant MSG_NOTIFY_CALLCLOSE.
     * This message is used to notify the caller
     */
    public static final int MSG_NOTIFY_CALLCLOSE = 211;

    /**
     * The constant BLANK_MARK.
     */
    public static final String BLANK_MARK = " ";

    /**
     * The constant VOIP_CALL_HANG_UP.
     * Notify the main interface to hang up
     */
    public static final int VOIP_CALL_HANG_UP = 31;

    /**
     * data receive
     */
    public static final int DATA_RECVING = 32;

    /**
     * data stop
     */
    public static final int DATA_STOPPED = 34;


    /**
     * The constant SHOW_CALL_LAYOUT.
     * Display outgoing interface
     */
    public static final int SHOW_CALL_LAYOUT = 9;

    /**
     * The constant VIDEO_SWITCH_LOCAL
     */
    public static final int MMV_SWITCH_LOCAL = 2;
    /**
     * The constant VIDEO_SWITCH_CAMERA
     */
    public static final int MMV_SWITCH_CAMERA = 4;
    /**
     * The constant VIDEO_CONTROL_START.
     */
    public static final int VIDEO_CONTROL_START = 4;
    /**
     * The constant VIDEO_CONTROL_STOP.
     */
    public static final int VIDEO_CONTROL_STOP = 8;

    public static final int MSG_SHOW_VIDEO_JOIN_CONF_VIEW = 2001;

    public static final int MSG_SHOW_AUDIO_JOIN_CONF_VIEW = 2002;

    public static final int MSG_DIAL_VIDEO_JOIN_CONF = 2003;

    public static final int MSG_DIAL_AUDIO_JOIN_CONF = 2004;

    /**
     * video
     */
    public static final int MSG_SHOW_VIDEOVIEW = 3001;

    /**
     * audio
     */
    public static final int MSG_SHOW_AUDIOVIEW = 3002;


    /**
     * End call
     */
    public static final int MSG_CALL_END_EVENT = 3003;

    /**
     * Call interface refresh
     */
    public static final int MSG_CALL_UPDATE_UI = 3005;

    /**
     * Call change notification
     */
    public static final int MSG_CALL_MODIFY_UI = 3006;

    /**
     * Video call
     */
    public static final int MSG_DIALCALL_VIDEO = 3007;
    /**
     * Audio call
     */
    public static final int MSG_DIALCALL_AUDIO = 3008;

    /**
     * Turn off video failed
     */
    public static final int MSG_CLOSE_VIDEO_FAIL = 3009;

    /**
     * To open or close the video to the end
     */
    public static final int MSG_REMOTE_VIDEO_UPDATE = 3010;

    /**
     * Low bandwidth upgrade failed
     */
    public static final int MSG_LOW_BW_UPDATE_FAIL = 3011;

    /**
     * Refresh local view
     */
    public static final int MSG_IPT_SERVICE_SUCCESS = 3020;
    /**
     * Refresh remote view
     */
    public static final int MSG_IPT_SERVICE_FAIL = 3021;

    /**
     * 400 bad request
     */
    public static final int CALL_E_REASON_CODE_BADREQUEST = 400;

    /**
     * 402 payment required
     */
    public static final int CALL_E_REASON_CODE_PAYMENTREQUIRED = 402;

    /**
     * 403 forbidden
     */
    public static final int CALL_E_REASON_CODE_FORBIDDEN = 403;

    /**
     * 404 not found
     */
    public static final int CALL_E_REASON_CODE_NOTFOUND = 404;

    /**
     * 405 method no allowed
     */
    public static final int CALL_E_REASON_CODE_METHODNOTALLOWED = 405;

    /**
     * 408 request timeout
     */
    public static final int CALL_E_REASON_CODE_RESNOTACCEPTABLE = 406;
    /**
     * 406 not acceptable
     */
    public static final int CALL_E_REASON_CODE_REQUESTTIMEOUT = 408;

    /**
     * 501 not implemented
     */
    public static final int CALL_E_REASON_CODE_SERVERINTERNALERROR = 500;
    /**
     * 500 server internal error
     */
    public static final int CALL_E_REASON_CODE_NOTIMPLEMENTED = 501;

    /**
     * 502 bad gateway
     */
    public static final int CALL_E_REASON_CODE_BADGATEWAY = 502;

    /**
     * 503 service unavailable
     */
    public static final int CALL_E_REASON_CODE_SERVICEUNAVAILABLE = 503;

    /**
     * 504 server time-out
     */
    public static final int CALL_E_REASON_CODE_SERVERTIMEOUT = 504;

    /**
     * 505 version not supported
     */
    public static final int CALL_E_REASON_CODE_VERSIONNOTSUPPORTED = 505;

    private CallConstants()
    {
    }

    /**
     * The type Num.
     */
    public static final class Num
    {
        /**
         * The constant ZERO.
         */
        public static final int ZERO = 0;
        /**
         * The constant ONE.
         */
        public static final int ONE = 1;
        /**
         * The constant TWO.
         */
        public static final int TWO = 2;
        /**
         * The constant THREE.
         */
        public static final int THREE = 3;
        /**
         * The constant FOUR.
         */
        public static final int FOUR = 4;
        /**
         * The constant FIVE.
         */
        public static final int FIVE = 5;
        /**
         * The constant SIX.
         */
        public static final int SIX = 6;
        /**
         * The constant SEVEN.
         */
        public static final int SEVEN = 7;
        /**
         * The constant EIGHT.
         */
        public static final int EIGHT = 8;
        /**
         * The constant NINE.
         */
        public static final int NINE = 9;
        /**
         * The constant TEN.
         */
        public static final int TEN = 10;
        /**
         * The constant ELEVEN.
         */
        public static final int ELEVEN = 11;
        /**
         * The constant TWELVE.
         */
        public static final int TWELVE = 12;

        /**
         * Instantiates a new Num.
         */
        private Num()
        {
        }
    }


    /**
     * The constant COMING_VIEW_TYPE.
     * Receive message prompt type
     */
    public static final String COMING_VIEW_TYPE = "comingType";

    /**
     * The constant MENU_CLICK_EVENT.
     */
    public static final String MENU_CLICK_EVENT = "menu_click_event";

    /**
     * The constant VOIP_CALLID.
     * Session ID for Intent to pass data identification
     */
    public static final String VOIP_CALLID = "callID";

    /**
     * The constant VOIP_CALLNUMBER.
     * On the side number, call number,
     * used for Intent transfer data identification
     */
    public static final String VOIP_CALLNUMBER = "callInNumber";

    /**
     * The constant VOIP_CALL_DISPLAY_NAME.
     * Call the nickname
     */
    public static final String VOIP_CALL_DISPLAY_NAME = "callInDisplayname";

    /**
     * Voice call add video
     */
    public static final String AUDIO_ADD_VIDEO_EVENT = "mod";

    /**
     * The enum State.
     */
    public enum State
    {
        /**
         * Registed state.
         */
        REGISTERED,
        /**
         * Unregister state.
         */
        UNREGISTER,

        /**
         * Registering state.
         */
        REGISTERING,

        /**
         * Deregistering state.
         */
        DEREGISTERING,

        /**
         * Butt state.
         */
        BUTT;

        /**
         * State
         */
        State()
        {
        }
    }

    /**
     * The enum Modify notice type.
     */
    public enum ModifyNoticeType
    {
        /**
         * Default type
         */
        defaultType,
        /**
         * Voice to video
         */
        VoiceToVideo,
        /**
         * Video to voice
         */
        VideoToVoice,
        /**
         * Change request failed
         */
        ModifyRequestFalied,
        /**
         * Cancel each other to upgrade
         * the video operation
         */
        ModifyRequestCancel;

        /**
         * ModifyNoticeType
         */
        ModifyNoticeType()
        {
        }


    }

    /**
     * The constant CAMERA_NON.
     */
    public static final int CAMERA_NON = -1;
    /**
     * The constant CAMERA_NORMAL.
     */
    public static final int CAMERA_NORMAL = 0;
    /**
     * Rear camera
     */
    public static final int BACK_CAMERA = 0;
    /**
     * front camera
     */
    public static final int FRONT_CAMERA = 1;

}
