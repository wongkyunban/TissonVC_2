package com.wong.tissonvc_2.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wong.tissonvc_2.R;
import com.wong.tissonvc_2.service.call.CallService;
import com.wong.tissonvc_2.service.call.VideoDeviceManager;
import com.wong.tissonvc_2.service.call.data.OneKeyJoinConfParam;
import com.wong.tissonvc_2.service.common.CallConstants.ModifyNoticeType;
import com.wong.tissonvc_2.service.common.CallConstants;
import com.wong.tissonvc_2.service.conf.ConferenceService;
import com.wong.tissonvc_2.service.login.LoginService;
import com.wong.tissonvc_2.service.login.data.LoginParams;
import com.wong.tissonvc_2.service.utils.TUPLogUtil;
import com.wong.tissonvc_2.service.utils.Tools;
import com.wong.tissonvc_2.service.TupNotify;
import com.wong.tissonvc_2.ui.customview.MenuBarContalPanel;
import com.wong.tissonvc_2.ui.customview.MenuBarContalPanel.MenuItemServer;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;



/**
 * The type Call fragment.
 * <p/>
 * CallFragment
 * Audio and video call view
 */
public class CallFragment extends Fragment implements TupNotify
{
    private static final String TAG = CallFragment.class.getSimpleName();
    /**
     * Protocol stack for 32 seconds timeout,
     * 25 seconds on the interface timeout
     */
    private static final int CANCLE_TIME = 25000;
    private static final Object RENDER_CHANGE_LOCK = new Object();

    private TextView numberAudioTV;
    private TextView hintAudioTV;

    /**
     * Remote video
     */
    private RelativeLayout remoteVideoView;
    /**
     * Local video
     */
    private RelativeLayout localVideoView;

    /**
     * Local video display area
     */
    private RelativeLayout localVideoLayout;


    /**
     * Audio call display area
     */
    private RelativeLayout audioCallLayout;

    /**
     * video call display area
     */
    private RelativeLayout videoChatLayout;

    /**
     * call notify
     */
    private String tipTxt;

    /**
     * Root layout
     */
    private ViewGroup rootViewGroup;

    /**
     * call handler
     */
    private Handler callHandler;

    /**
     * menu bar
     */
    private MenuBarContalPanel menuBarPanel;

    /**
     * MenuControlPanel Toolbar
     */
    private MenuItemServer menuItemServer;

    // Confirm and accept the upgrade dialog
    private AlertDialog dialog;

    /**
     * Eliminating prompt box timer
     */
    private Timer dismisDialogTimer;

    private Handler handler = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        rootViewGroup = (ViewGroup) inflater.inflate(R.layout.call_fraglayout, container, false);
        return rootViewGroup;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        LoginService.getInstance().registerTupNotify(this);
        initComponent();
        setRootViewListener();
    }

    /**
     * display call view
     *
     * @param callNumber  the call number
     * @param isVideoCall the is video call
     */
    public void showCallingLayout(final String callNumber, final boolean isVideoCall)
    {
        if (Tools.isStringEmpty(callNumber))
        {
            TUPLogUtil.i(TAG, "empty CallNumber return!!!");
            return;
        }

        CallActivity.getInstance().sendHandlerMessage(CallConstants.SHOW_CALL_LAYOUT, null);

        if (isVideoCall)
        {
            sendHandlerMessage(CallConstants.MSG_DIALCALL_VIDEO, callNumber);
        }
        else
        {
            sendHandlerMessage(CallConstants.MSG_DIALCALL_AUDIO, callNumber);
        }
        tipTxt = getTipTxt(true, isVideoCall, false, false, false);
        updateLayout(CallConstants.STATUS_CALLING, callNumber, isVideoCall, tipTxt);
    }

    public void showJoinConfLayout(OneKeyJoinConfParam joinConfParam)
    {
        CallActivity.getInstance().sendHandlerMessage(CallConstants.SHOW_CALL_LAYOUT, null);

        if (joinConfParam.isVideoJoinConf())
        {
            sendHandlerMessage(CallConstants.MSG_DIAL_VIDEO_JOIN_CONF, joinConfParam);
        }
        else
        {
            sendHandlerMessage(CallConstants.MSG_DIAL_AUDIO_JOIN_CONF, joinConfParam);
        }
        tipTxt = getTipTxt(true, joinConfParam.isVideoJoinConf(), false, false, false);
        updateLayout(CallConstants.STATUS_CALLING, joinConfParam.getAccessCode(), joinConfParam.isVideoJoinConf(), tipTxt);
    }


    private void initComponent()
    {
        initMenuItemServer();
        numberAudioTV = (TextView) getActivity().findViewById(R.id.tv_audio_number);
        hintAudioTV = (TextView) getActivity().findViewById(R.id.tv_audio_hint);

        audioCallLayout = (RelativeLayout) rootViewGroup.findViewById(R.id.audio_calllayout);

        videoChatLayout = (RelativeLayout) rootViewGroup.findViewById(R.id.video_chatlayout);

        localVideoLayout = (RelativeLayout) rootViewGroup.findViewById(R.id.local_layout);

        remoteVideoView = (RelativeLayout) rootViewGroup.findViewById(R.id.remote_videoview);

        localVideoView = (RelativeLayout) rootViewGroup.findViewById(R.id.local_videoview);

        initHandler();

    }

    private void initHandler()
    {
        if (null != callHandler)
        {
            TUPLogUtil.i(TAG, "the handler has init.");
            return;
        }
        callHandler = new Handler()
        {
            @Override
            public void dispatchMessage(Message msg)
            {
                TUPLogUtil.i(TAG, "what:" + msg.what);
                handlerMessageNotity(msg);
                handlerMessageView(msg);
                handlerMessageCall(msg);
                handlerMessageOperate(msg);
                super.dispatchMessage(msg);
            }
        };
    }

    private void initMenuItemServer()
    {
        if (null != menuItemServer)
        {
            TUPLogUtil.i(TAG, "the initMenuItemServer has init.");
            return;
        }
        menuItemServer = new MenuItemServer()
        {
            @Override
            public void setPip(boolean isPip)
            {
                TUPLogUtil.i(TAG, "close PIP " + isPip);
                if (isPip)
                {
                    openLocalVideo();
                }
                else
                {
                    closeLocalVideo();
                }
            }

            @Override
            public void showConfListView()
            {
                if (!ConferenceService.getInstance().isConfMode())
                {
                    Toast.makeText(getActivity(), "current not conf", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent it = new Intent(getActivity(), ConfCtrlActivity.class);
                startActivity(it);
            }

            @Override
            public void gotoShare()
            {
                Intent it = new Intent(getActivity(), ConfShareActivity.class);
                startActivity(it);
            }

        };
    }

    /**
     * Open Local Video
     */
    private void openLocalVideo()
    {
        localVideoLayout.setVisibility(View.VISIBLE);
        if (null != localVideoView.getChildAt(0))
        {
            localVideoView.getChildAt(0).setVisibility(View.VISIBLE);
        }
    }

    /**
     * Close Local Video
     */
    private void closeLocalVideo()
    {
        if (null != localVideoView.getChildAt(0))
        {
            localVideoView.getChildAt(0).setVisibility(View.GONE);
        }
        localVideoLayout.setVisibility(View.GONE);
    }

    /**
     * Send handler message.
     *
     * @param what   the what
     * @param object the object
     */
    public void sendHandlerMessage(int what, Object object)
    {
        if (callHandler == null)
        {
            TUPLogUtil.i(TAG, "sendHandlerMessage() callHandler is null");
            return;
        }
        Message msg = callHandler.obtainMessage(what, object);
        callHandler.sendMessage(msg);
    }

    private static final Object DOCLOCK = new Object();

    /**
     * notify handler
     */
    private void handlerMessageNotity(Message msg)
    {
        switch (msg.what)
        {

            case CallConstants.MSG_LOW_BW_UPDATE_FAIL:
                showToast(R.string.low_bw_update_fail);
                break;
            case CallConstants.MSG_CLOSE_VIDEO_FAIL:
                showToast(R.string.oper_failure);
                break;
            case CallConstants.CALL_END_NOTIFY:
                onCallClosed();
                break;
            case CallConstants.DATA_RECVING:
                synchronized (DOCLOCK)
                {
                    int callId = (int) msg.obj;
                    TUPLogUtil.i(TAG, "-------------CallConstants.DATA_RECVING");
                    synchronized (RENDER_CHANGE_LOCK)
                    {
                        VideoDeviceManager.getIns().openBFCPReceive(callId
                                , localVideoView, remoteVideoView);
                    }

                }
                break;
            case CallConstants.DATA_STOPPED:
                menuBarPanel.setPipTips(true);
                localVideoLayout.setVisibility(View.VISIBLE);
                addVideoView();
                break;

            default:
                break;
        }
    }

    private void showToast(int id)
    {
        Toast.makeText(getActivity(), getActivity().getString(id), Toast.LENGTH_SHORT).show();
    }

    /**
     * View handler
     */
    private void handlerMessageView(Message msg)
    {
        if (msg.obj instanceof String)
        {
            TUPLogUtil.i(TAG, "handlerMessageView receives:" + msg.what);
        }
        switch (msg.what)
        {
            case CallConstants.MSG_SHOW_AUDIOVIEW:
                if (!(msg.obj instanceof String))
                {
                    TUPLogUtil.i(TAG, "msg.obj is not instanceof String");
                    return;
                }
                showCallingLayout((String) msg.obj, false);
                break;
            case CallConstants.MSG_SHOW_VIDEOVIEW:
                if (!(msg.obj instanceof String))
                {
                    TUPLogUtil.i(TAG, "msg.obj is not instanceof String");
                    return;
                }
                showCallingLayout((String) msg.obj, true);
                break;

            case CallConstants.MSG_SHOW_VIDEO_JOIN_CONF_VIEW:
            case CallConstants.MSG_SHOW_AUDIO_JOIN_CONF_VIEW:
                showJoinConfLayout((OneKeyJoinConfParam) msg.obj);
                break;

            default:
                break;
        }
    }

    /**
     * call Handler
     */
    private void handlerMessageCall(Message msg)
    {
        if (msg.obj instanceof String)
        {
            TUPLogUtil.i(TAG, "handlerMessageCall receives:" + msg.what);
        }
        switch (msg.what)
        {
            case CallConstants.MSG_DIALCALL_AUDIO:
                if (!(msg.obj instanceof String))
                {
                    TUPLogUtil.i(TAG, "msg.obj is not instanceof String");
                    return;
                }
                CallService.getInstance().launchCall((String) msg.obj,
                        LoginParams.getInstance().getRegisterServerIp(), false);
                break;
            case CallConstants.MSG_DIALCALL_VIDEO:
                CallService.getInstance().launchCall((String) msg.obj,
                        LoginParams.getInstance().getRegisterServerIp(), true);
                break;
            case CallConstants.MSG_DIAL_AUDIO_JOIN_CONF:
            case CallConstants.MSG_DIAL_VIDEO_JOIN_CONF:
                CallService.getInstance().launchJoinConf((OneKeyJoinConfParam) msg.obj);
                break;

            case CallConstants.MSG_CALL_END_EVENT:
                CallService.getInstance().closeCallControl();
                break;
            case CallConstants.MSG_CALL_UPDATE_UI:
                TUPLogUtil.i(TAG, "MsgCallFragment.MSG_CALL_UPDATE_UI");
                updateCallLayout();
                break;
            case CallConstants.MSG_CALL_MODIFY_UI:
                TUPLogUtil.i(TAG, "MsgCallFragment.MSG_CALL_MODIFY_UI");
                ModifyNoticeType modifyType = (ModifyNoticeType) msg.obj;
                voipCallModify(modifyType);
                break;
            default:
                break;
        }
    }

    private void handlerMessageOperate(Message msg)
    {
        switch (msg.what)
        {
            case CallConstants.MSG_REMOTE_VIDEO_UPDATE:
                if (CallService.getInstance().isVideoCall())
                {
                    menuBarPanel.changeMode(MenuBarContalPanel.Mode.VIDEO_AUDIO_SWITCH);
                }
                else
                {
                    menuBarPanel.changeMode(MenuBarContalPanel.Mode.AUDIO_CALL);
                }
                if (null != msg.obj && null != CallService.getInstance().getRemoteCallView())
                {
                    cleanRemoteFrame((Boolean) msg.obj);
                }
                break;
            default:
                break;
        }
    }

    private void cleanRemoteFrame(boolean isClean)
    {
        final SurfaceView remoteVV = CallService.getInstance().getRemoteCallView();
        if (null == remoteVV)
        {
            TUPLogUtil.i(TAG, "remote view is null; return;");
            return;
        }
        synchronized (RENDER_CHANGE_LOCK)
        {
            if (isClean)
            {
                remoteVV.setBackgroundColor(Color.BLACK);
            }
            else
            {
                callHandler.postDelayed(new ChangeViewBackgroudRunnable(remoteVV), 1000);
            }
            TUPLogUtil.i(TAG, "now remote has close [" + isClean + ']');
        }
    }

    /**
     * get prompt information
     *
     * @param isCalling
     * @param isRef
     * @param isCallToVideo
     * @return information
     */
    private String getTipTxt(boolean isCalling, boolean isVideo,
                             boolean isRef, boolean isCallToVideo, boolean isAudioChat)
    {
        int showCallStringId = 0;
        if (isRef)
        {
            return ("Call forward");
        }
        else if (isCallToVideo)
        {
            return ("Switching to a video call...");
        }
        else if (isCalling)
        {
            return (isVideo ? "Video Call..." : "Dialing...");
        }
        else
        {
            return (isAudioChat ? "Audio Call..." : "Video chat...");
        }
    }

    /**
     * updateLayout
     *
     * @param voipState   the voip state
     * @param callNumber  the call number
     * @param isVideoCall the is video call
     * @param tipTxt      the tip txt
     */
    public void updateLayout(int voipState, String callNumber, boolean isVideoCall, String tipTxt)
    {
        if (null != menuBarPanel)
        {
            menuBarPanel.setNeedShow(true);
        }

        // open collection point service
        if (null != CallActivity.getInstance())
        {
            CallService.getInstance().renderCreate();
        }
        else
        {
            TUPLogUtil.e(TAG, " --- CallActivity instance is null ");
        }

        if (CallConstants.STATUS_CLOSE == voipState)
        {
            TUPLogUtil.i(TAG, "end hangup");
            return;
        }

        if (null != callNumber)
        {
            numberAudioTV.setText(callNumber);
        }

        if (null == menuBarPanel)
        {
            TUPLogUtil.i(TAG, "menuBarPanel is null , create it.");
            menuBarPanel = new MenuBarContalPanel(rootViewGroup, menuItemServer);
        }
        menuBarPanel.setRemoteNumber(callNumber);
        updateByState(callNumber, voipState, isVideoCall);
    }

    private void updateByState(String callNumber, int voipState, boolean isVideoCall)
    {

        TUPLogUtil.i(TAG, "updateByState() voipStateis:" + voipState);

        if (CallConstants.STATUS_CALLING == voipState)
        {
            showCallLayout(callNumber, isVideoCall, tipTxt);
            TUPLogUtil.i(TAG, "to talking state is video call =>" + isVideoCall);
            return;
        }
        if (CallConstants.STATUS_TALKING == voipState || CallConstants.STATUS_VIDEOING == voipState)
        {
            showChatLayout(callNumber, isVideoCall, tipTxt);
            TUPLogUtil.i(TAG, "to chat state is video chat =>" + isVideoCall);
        }
    }

    /**
     * On call closed.
     */
    private void onCallClosed()
    {
        TUPLogUtil.i(TAG, "onCallClosed enter.");

        localVideoView.removeAllViews();
        remoteVideoView.removeAllViews();

        localVideoLayout.setVisibility(View.VISIBLE);
        TUPLogUtil.i(TAG, "onCallClosed resetData.");
        resetData();


        CallService.getInstance().renderDestroy();


        TUPLogUtil.i(TAG, "onCallClosed sendHandlerMessage back to home.");
        if (null != CallActivity.getInstance())
        {
            CallActivity.getInstance().sendHandlerMessage(CallConstants.CALL_CLOSE_BACK_NOTIFY, null);
            CallActivity.getInstance().sendHandlerMessage(CallConstants.MSG_NOTIFY_CALLCLOSE, null);
        }
        if (null != menuBarPanel)
        {
            menuBarPanel.setNeedShow(false);
        }

        TUPLogUtil.i(TAG, "onCallClosed leave.");
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    /**
     * update audio route
     */
    public void updateAudioRoute()
    {
        if (null == menuBarPanel)
        {
            return;
        }
        menuBarPanel.onAudioRouteChange();
    }

    /**
     * call end reset data
     */
    private void resetData()
    {
        if (null != menuBarPanel)
        {
            menuBarPanel.resetData();
        }


        CallService.getInstance().removeView();

    }

    private void showChatLayout(String callNumber, boolean isVideoCall, String tiptxt)
    {

        if (isVideoCall)
        {
            if (CallService.getInstance().isVideoCall())
            {
                menuBarPanel.changeMode(MenuBarContalPanel.Mode.VIDEO_CALL);
            }
            else
            {
                menuBarPanel.changeMode(MenuBarContalPanel.Mode.VIDEO_AUDIO_SWITCH);
            }
        }
        else
        {
            menuBarPanel.changeMode(MenuBarContalPanel.Mode.AUDIO_CALL);
            menuBarPanel.resetVideoToAudioState();
            menuBarPanel.setPipTips(true);
        }

        if (isVideoCall)
        {
            showVideoChat();
            return;
        }
        showAudioChat(callNumber, tiptxt);
    }

    private void showAudioChat(String callNumber, String tiptxt)
    {
        TUPLogUtil.i(TAG, "enter showAudioChat");


        numberAudioTV.setText(callNumber);
        hintAudioTV.setText(tiptxt);

        audioCallLayout.setVisibility(View.VISIBLE);
        videoChatLayout.setVisibility(View.GONE);
    }

    private void showVideoChat()
    {
        TUPLogUtil.i(TAG, "showVideoChat()");
        localVideoLayout.setVisibility(View.VISIBLE);
        menuBarPanel.setPipTips(true);
        audioCallLayout.setVisibility(View.GONE);
        videoChatLayout.setVisibility(View.VISIBLE);

        menuBarPanel.show();
        if (CallService.getInstance().getVoipStatus() == CallConstants.STATUS_VIDEOING)
        {
            TUPLogUtil.i(TAG, "STATUS_VIDEOING addVideoView");
            addVideoView();
        }
    }

    private void addVideoView()
    {
        remoteVideoView.setVisibility(View.VISIBLE);

        View remoteVV = CallService.getInstance().getRemoteCallView();
        if (null != remoteVV && null == remoteVV.getParent())
        {
            CallService.getInstance().addRenderToContain(localVideoView, remoteVideoView);
        }
        TUPLogUtil.i(TAG, "localVideoView=" + localVideoView
                + "remoteVideoView=" + remoteVideoView + "isLocal=" + true);
    }

    /**
     * showCallLayout
     *
     * @param isVideoCall
     * @param tiptxt
     */
    private void showCallLayout(String callNumber, boolean isVideoCall, String tiptxt)
    {
        TUPLogUtil.i(TAG, "showCallLayout()");
        if (isVideoCall)
        {
            audioCallLayout.setVisibility(View.GONE);
            videoChatLayout.setVisibility(View.VISIBLE);

            changeMode(MenuBarContalPanel.Mode.VIDEO_CALLING);

            if (menuBarPanel != null)
            {
                menuBarPanel.show();
            }
            return;
        }

        hintAudioTV.setText(tiptxt);

        if (null != callNumber)
        {
            numberAudioTV.setText(callNumber);
        }
        else
        {
            numberAudioTV.setText("");
        }

        audioCallLayout.setVisibility(View.VISIBLE);
        videoChatLayout.setVisibility(View.GONE);

        changeMode(MenuBarContalPanel.Mode.AUDIO_CALLING);
    }

    /**
     * changeMode
     *
     * @param mode
     */
    private void changeMode(MenuBarContalPanel.Mode mode)
    {
        if (null == menuBarPanel)
        {
            TUPLogUtil.i(TAG, "menuBarPanel is   null  return");
            return;
        }
        menuBarPanel.changeMode(mode);
    }

    /**
     * voipCallModify
     *
     * @param modifyType the modify type
     */
    private void voipCallModify(ModifyNoticeType modifyType)
    {
        TUPLogUtil.i(TAG, "voipCallModify modifytype:" + modifyType);
        switch (modifyType)
        {
            case VoiceToVideo:
                voiceToVideo();
                updateCallLayout();
                break;
            case VideoToVoice:
                updateCallLayout();
                break;
            case ModifyRequestFalied:
                modifyRequestFalied();
                break;
            case ModifyRequestCancel:
                modifyRequestCancel();
                break;
            case defaultType:
                updateCallLayout();
                break;
            default:
                break;

        }
    }

    /**
     * updateCallLayout
     */
    private void updateCallLayout()
    {
        TUPLogUtil.i(TAG, "updateCallLayout()");
        if (null != CallActivity.getInstance())
        {
            CallService.getInstance().renderCreate();
        }
        else
        {
            TUPLogUtil.e(TAG, " --- CallActivity instance is null");
        }

        if (null == menuBarPanel)
        {
            menuBarPanel = new MenuBarContalPanel(rootViewGroup, menuItemServer);
        }
        menuBarPanel.dismissPopupWindow();
        int voipStatus = CallService.getInstance().getVoipStatus();
        TUPLogUtil.i(TAG, "voipStatus is:" + voipStatus);
        String callNumber = CallService.getInstance().getCallNumber();
        boolean isVideo = CallService.getInstance().isVideoCall();
        updateAudioRoute();
        boolean isCalling = false;
        boolean isRefer = false;
        boolean isCallToVideo = false;
        boolean isAudioChat = false;
        if (CallConstants.STATUS_VIDEOINIT == voipStatus)
        {
            isCallToVideo = true;
        }
        if (CallConstants.STATUS_TALKING == voipStatus)
        {
            isAudioChat = true;
        }
        tipTxt = getTipTxt(isCalling, isVideo, isRefer, isCallToVideo, isAudioChat);

        switch (voipStatus)
        {

            case CallConstants.STATUS_TALKING:
                updateLayout(CallConstants.STATUS_TALKING, callNumber, false, tipTxt);
                CallActivity.getInstance().sendHandlerMessage(CallConstants.SHOW_CALL_LAYOUT, null);
                break;

            case CallConstants.STATUS_CLOSE:
                updateLayout(CallConstants.STATUS_CLOSE, callNumber, false, tipTxt);
                if (null != CallActivity.getInstance())
                {
                    CallActivity.getInstance().sendHandlerMessage(
                            CallConstants.CALL_CLOSE_BACK_NOTIFY, null);
                }
                break;
            case CallConstants.STATUS_CALLING:
                TUPLogUtil.i(TAG, "CallStatus.STATUS_CALLING:");
                updateLayout(CallConstants.STATUS_CALLING, callNumber, isVideo, tipTxt);
                CallActivity.getInstance().sendHandlerMessage(CallConstants.SHOW_CALL_LAYOUT, null);
                break;
            case CallConstants.STATUS_VIDEOINIT:
                updateLayout(CallConstants.STATUS_VIDEOINIT, callNumber, false, tipTxt);
                CallActivity.getInstance().sendHandlerMessage(CallConstants.SHOW_CALL_LAYOUT, null);
                break;
            case CallConstants.STATUS_VIDEOING:
                updateLayout(CallConstants.STATUS_VIDEOING, callNumber, true, tipTxt);
                CallActivity.getInstance().sendHandlerMessage(CallConstants.SHOW_CALL_LAYOUT, null);
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroy()
    {

        TUPLogUtil.i(TAG, "onDestroy start!~");
        CallService.getInstance().renderDestroy();
        if (null != handler)
        {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
        if (null != callHandler)
        {
            callHandler.removeCallbacksAndMessages(null);
            callHandler = null;
        }
        LoginService.getInstance().unregisterTupNotify(this);
        super.onDestroy();
    }

    /**
     * setRootViewListener
     */
    private void setRootViewListener()
    {
        rootViewGroup.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                TUPLogUtil.i(TAG, "rootView is clicked");
                if (null == menuBarPanel)
                {
                    return;
                }
                menuBarPanel.showAndGone();
                TUPLogUtil.i(TAG, "show menuBar");
            }
        });
    }

    /**
     * Pop-up call to upgrade the video to the failure
     * of the dialog box to ask whether to try again later
     */
    public void modifyRequestFalied()
    {
        final CallActivity callActivity = CallActivity.getInstance();
        if (callActivity == null)
        {
            return;
        }
        callActivity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                TUPLogUtil.i(TAG, "modifyRequestFalied alert!~");

                DialogInterface.OnClickListener retry = new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        handler.postAtFrontOfQueue(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                CallService.getInstance().upgradeVideo();
                            }
                        });
                    }
                };

                DialogInterface.OnClickListener ok = new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(callActivity);
                builder.setTitle(callActivity.getString(R.string.msg_tip));
                builder.setMessage(callActivity.getString(R.string.video_upgrade_failure_try_again));
                builder.setPositiveButton(callActivity.getString(R.string.retry), retry);
                builder.setNegativeButton(callActivity.getString(R.string.ok), ok);
                dialog = builder.create();
                dialog.show();
            }
        });
    }


    /**
     * Call to upgrade video notification
     */
    private void voiceToVideo()
    {
        final CallActivity callActivity = CallActivity.getInstance();
        if (callActivity == null)
        {
            return;
        }
        callActivity.runOnUiThread(new Runnable()
        {

            @Override
            public void run()
            {
                TUPLogUtil.i(TAG, "voiceToVideo alert dialog!~");
                DialogInterface.OnClickListener ok = new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        cancelDisDiaTimer();
                        Executors.newSingleThreadExecutor().execute(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                if (null == Looper.myLooper())
                                {
                                    Looper.prepare();
                                }
                                CallService.getInstance().agreeUpgradeVideoControl();
                            }
                        });
                    }
                };
                DialogInterface.OnClickListener cancel = new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        cancelDisDiaTimer();
                        CallService.getInstance().rejectUpgradeVideo();
                    }
                };
                DialogInterface.OnDismissListener dismiss = new DialogInterface.OnDismissListener()
                {
                    @Override
                    public void onDismiss(DialogInterface dialog)
                    {
                        if (dismisDialogTimer != null)
                        {
                            cancelDisDiaTimer();
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(callActivity);
                builder.setTitle(callActivity.getString(R.string.msg_tip));
                builder.setMessage(callActivity.getString(R.string.ntf_upgrade_videocall));
                builder.setPositiveButton(callActivity.getString(R.string.accept), ok);
                builder.setNegativeButton(callActivity.getString(R.string.refuse), cancel);
                builder.setOnDismissListener(dismiss);
                dialog = builder.create();
                dialog.show();

                startDisDiaTimer();

            }
        });
    }

    private void cancelDisDiaTimer()
    {
        if (dismisDialogTimer != null)
        {
            dismisDialogTimer.cancel();
            dismisDialogTimer = null;
        }
    }

    private void startDisDiaTimer()
    {
        cancelDisDiaTimer();

        dismisDialogTimer = new Timer("Dismis Dialog");
        DismisDialogTimerTask dismisDialogTimerTask = new DismisDialogTimerTask();
        dismisDialogTimer.schedule(dismisDialogTimerTask, CANCLE_TIME);
    }

    /**
     * Respond to each other to cancel the upgrade video operation
     */
    public void modifyRequestCancel()
    {
        TUPLogUtil.i(TAG, "modifyRequestCancel");
        final CallActivity callActivity = CallActivity.getInstance();
        if (null == callActivity)
        {
            return;
        }

        if (null == dialog)
        {
            return;
        }
        if (dialog != null && dialog.isShowing() && !callActivity.isFinishing())
        {
            dialog.dismiss();
        }

        showToast(R.string.cancel_video_update);
    }

    /**
     * On register result.
     *
     * @param registerResult the register result
     * @param errorCode      the error code
     */
    @Override
    public void onRegisterNotify(int registerResult, int errorCode)
    {
    }

    @Override
    public void onSMCLogin(int smcAuthorizeResult, String errorReason)
    {

    }


    @Override
    public void onCallNotify(int code, Object object)
    {
        switch (code)
        {
            case CallConstants.MSG_CALL_UPDATE_UI:
                boolean answer = (boolean) object;
                sendHandlerMessage(code, answer);
                break;
            case CallConstants.MSG_CALL_MODIFY_UI:
                ModifyNoticeType noticeType = (ModifyNoticeType) object;
                sendHandlerMessage(code, noticeType);
                break;
            case CallConstants.MSG_REMOTE_VIDEO_UPDATE:
                boolean isRemoteVideoClose = (boolean) object;
                sendHandlerMessage(code, isRemoteVideoClose);
                break;
            case CallConstants.CALL_END_NOTIFY:
                sendHandlerMessage(code, null);
                break;
            case CallConstants.MSG_CLOSE_VIDEO_FAIL:
                sendHandlerMessage(code, null);
                break;
            case CallConstants.MSG_LOW_BW_UPDATE_FAIL:
                sendHandlerMessage(code, null);
                break;

            case CallConstants.DATA_RECVING:
                sendHandlerMessage(code, (int) object);
                break;

            case CallConstants.DATA_STOPPED:
                sendHandlerMessage(code, (int) object);
                break;

            default:
                break;
        }

    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
    }


    private class DismisDialogTimerTask extends TimerTask
    {
        @Override
        public void run()
        {
            if (null != dialog)
            {
                dialog.dismiss();
            }
            CallService.getInstance().rejectUpgradeVideo();
            TUPLogUtil.i(TAG, "dialog time out disAgreeUpg");
        }
    }

    private static class ChangeViewBackgroudRunnable implements Runnable
    {
        private SurfaceView remoteVV;

        /**
         * Instantiates a new Change view backgroud runnable.
         *
         * @param remoteVVVar the remote vv var
         */
        ChangeViewBackgroudRunnable(SurfaceView remoteVVVar)
        {
            remoteVV = remoteVVVar;
        }

        @Override
        public void run()
        {
            remoteVV.setBackgroundColor(Color.alpha(0));
        }
    }

}
