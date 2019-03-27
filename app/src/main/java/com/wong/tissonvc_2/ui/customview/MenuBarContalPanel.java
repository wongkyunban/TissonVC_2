package com.wong.tissonvc_2.ui.customview;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wong.tissonvc_2.R;
import com.wong.tissonvc_2.service.call.CallService;
import com.wong.tissonvc_2.service.common.CallConstants;
import com.wong.tissonvc_2.service.utils.TUPLogUtil;
import com.wong.tissonvc_2.service.utils.Tools;
import com.wong.tissonvc_2.ui.activity.CallActivity;
import com.wong.tissonvc_2.ui.utils.LayoutUtil;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * The type Menu bar contal panel.
 * <p>
 * MenuBarContalPanel
 * Call control function button menu
 */
public class MenuBarContalPanel implements OnClickListener, VideoMenuBar.MenuItemServer
{
    private static final String TAG = MenuBarContalPanel.class.getSimpleName();
    private static final int REFRESH_UI_OPERATE_LOCAL_CAMERA = 0x0002;
    private static final int HALF_ALPHA = 127;
    private static final float MORE_POP_DISTANCE = 6.666F;
    private static final int NOT_ALPHA = 255;
    private static final int STRING_LEN = 300;
    private static final Object MENULOCK = new Object();

    private Mode menuMode;

    private MenuItemServer menuItemServer;

    private PopupWindow recallPopWindow;

    private PopupWindow morePopWindow;

    /**
     * more view
     */
    private ViewGroup moreView;

    private LinearLayout closePip;

    private ImageView closePipImg;

    private LinearLayout switchAudio;

    private ImageView switchAudioImg;

    private VideoMenuBar menuBar;

    private LinearLayout switchCamera;

    private ImageView switchCameraImg;

    private RelativeLayout speakerControl;

    private ImageView speakerControlImg;

    private boolean isDone = false;

    private TextView remoteNumberView;

    private View remoteNumberLayout;

    private TextView showTimeView;

    private TextView showTimeView1;

    private long autoTime = 0;

    private boolean isCount = false;

    private boolean isRun = false;

    private View rootView;

    private ScheduledExecutorService service;

    private ExecutorService operPool;

    private int[][] micClickRes = new int[1][2];

    private int[][] videoClickRes = new int[1][2];

    private int[][] audioRouteRes = new int[1][2];

    private int[][] outputClickRes = new int[1][2];

    private int[][] audioScreenRes = new int[1][2];

    private int[][] callHoldRes = new int[1][2];

    private LinearLayout closeCamera;


    private int videoIndex = 1;

    private Handler timeHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            if (null == showTimeView)
            {
                return;
            }
            if (null == showTimeView1)
            {
                return;
            }

            if (isCount)
            {
                showTimeView.setVisibility(View.VISIBLE);
                showTimeView.setText(formatTimeFString(autoTime));

                if (menuMode != Mode.AUDIO_CALL)
                {
                    showTimeView1.setVisibility(View.VISIBLE);
                    showTimeView1.setText(formatTimeFString(autoTime));
                }
                else
                {
                    showTimeView1.setVisibility(View.GONE);
                }
            }
            else
            {
                showTimeView.setVisibility(View.INVISIBLE);
                showTimeView1.setVisibility(View.GONE);
            }
        }

    };

    private Handler handlerUI = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            if (null == menuItemServer)
            {
                return;
            }
            if (REFRESH_UI_OPERATE_LOCAL_CAMERA == msg.what)
            {
                boolean isClose = (Boolean) msg.obj;
                refreshMenuItemLocalCamera(isClose);

                TUPLogUtil.i(TAG, "handle refresh UI operate camera.");
            }
        }

    };

    /**
     * Constructor
     *
     * @param rootView          the root view
     * @param menuItemServerVar the menu item server var
     */
    public MenuBarContalPanel(View rootView, MenuItemServer menuItemServerVar)
    {
        this.menuItemServer = menuItemServerVar;
        this.rootView = rootView;

        initResId();
        init();
    }

    /**
     * setRemoteNumber
     *
     * @param remoteNumberVar the remote number var
     */
    public void setRemoteNumber(String remoteNumberVar)
    {
        LayoutUtil.setEndEllipse(remoteNumberView, remoteNumberVar, STRING_LEN);
    }

    /**
     * onAudioRouteChange
     */
    public void onAudioRouteChange()
    {
        refreshAudioRouteItem();
        TUPLogUtil.i(TAG, "onAudioRouteChange refreshAudioRouteItem");
    }

    /**
     * setPipTips
     *
     * @param isOpen the is open
     */
    public void setPipTips(boolean isOpen)
    {
        TextView closePipTxt = (TextView) moreView.findViewById(R.id.close_pip_txt);
        if (!isOpen)
        {
            closePip.setTag(null);
            closePipTxt.setText(R.string.show_video_window);
            closePipImg.setImageDrawable(rootView.getResources().getDrawable(
                    R.drawable.te_state_open_pip));
        }
        else
        {
            closePip.setTag(CallConstants.MENU_CLICK_EVENT);
            closePipTxt.setText(R.string.hide_video_window);
            closePipImg.setImageDrawable(rootView.getResources().getDrawable(
                    R.drawable.te_state_close_pip));
        }
        closePipImg.getDrawable().setAlpha(NOT_ALPHA);
    }

    /**
     * showAndGone
     */
    public void showAndGone()
    {
        menuBar.showAndGone();
    }

    /**
     * show menu
     */
    public void show()
    {

        if (menuBar.getMenuBar().getVisibility() == View.VISIBLE)
        {
            menuBar.getMenuBar().setVisibility(View.GONE);
        }
        menuBar.showAndGone();
    }


    /**
     * changeMode
     *
     * @param modeVar         the mode var
     */
    public void changeMode(Mode modeVar)
    {
        menuBar.resetAllMenuItems();
        this.menuMode = modeVar;
        isDone = false;
        switch (menuMode)
        {
            case VIDEO_CALL:

                isCount = true;
                videoCallMode();
                break;
            case VIDEO_CALLING:

                isCount = false;
                videoCallingMode();
                break;
            case VIDEO_AUDIO_SWITCH:

                isCount = true;
                videoCallMode();
                break;
            case AUDIO_CALL:

                isCount = true;
                audioCallMode();
                break;
            case AUDIO_CALLING:

                isCount = false;
                audioCallingMode();
                break;
            default:
                TUPLogUtil.i(TAG, "no menu mode");
                isRun = false;
                break;
        }

        if (!isRun)
        {
            if (isCount)
            {
                isRun = true;
                startTimer();
            }
        }
        boolean show = (menuMode != Mode.VIDEO_CALLING) && (menuMode != Mode.VIDEO_CALL);
        if (show)
        {
            showAndGone();
        }

        refreshAudioRouteItem();
    }

    /**
     * setNeedShow
     *
     * @param isNeedShow the is need show
     */
    public void setNeedShow(boolean isNeedShow)
    {
        menuBar.setNeedShow(isNeedShow);
    }

    /**
     * resetVideoToAudioState
     */
    public void resetVideoToAudioState()
    {
        isDone = false;

        refreshMenuItemLocalCamera(false);
    }

    /**
     * resetData
     */
    public void resetData()
    {
        resetMIC();
        resetTime();
        resetSpeaker();
        isDone = false;
        showTimeView.setVisibility(View.INVISIBLE);
        showTimeView1.setVisibility(View.GONE);

        TextView closePipTxt = (TextView) moreView.findViewById(R.id.close_pip_txt);
        closePip.setTag(CallConstants.MENU_CLICK_EVENT);
        closePipTxt.setText(R.string.hide_video_window);
        closePipImg.setImageDrawable(rootView.getResources().getDrawable(
                R.drawable.te_state_close_pip));

        refreshMenuItemLocalCamera(false);
        TUPLogUtil.i(TAG, "reset menu data");
    }


    /**
     * Is run boolean.
     *
     * @return the boolean
     */
    public boolean isRun()
    {
        return isRun;
    }

    /**
     * setRun
     *
     * @param isRun the is run
     */
    public void setRun(boolean isRun)
    {
        this.isRun = isRun;
    }

    private void initResId()
    {
        // load Icon
        micClickRes[0][0] = R.drawable.te_phone_menu_mic;
        micClickRes[0][1] = R.drawable.te_phone_menu_close_mic;

        videoClickRes[0][0] = R.drawable.te_state_camera;
        videoClickRes[0][1] = R.drawable.te_state_close_camera;

        outputClickRes[0][0] = R.drawable.te_phone_menu_speaker;
        outputClickRes[0][1] = R.drawable.te_phone_menu_close_speaker;

        audioScreenRes[0][0] = R.drawable.te_state_audio_minimum;
        audioScreenRes[0][1] = R.drawable.te_state_audio_maximum;

        audioRouteRes[0][0] = R.drawable.te_state_menu_route_loudspeaker;
        audioRouteRes[0][1] = R.drawable.te_state_menu_route_earpiece;

        callHoldRes[0][0] = R.drawable.tup_call_hold;
        callHoldRes[0][1] = R.drawable.tup_call_unhold;

    }

    private void init()
    {
        operPool = Executors.newSingleThreadExecutor();
        menuBar = new VideoMenuBar(rootView);

        menuBar.setAutoHidden(true);

        menuBar.setItemServer(this);

        remoteNumberView = (TextView) rootView.findViewById(R.id.remote_number);
        showTimeView = (TextView) rootView.findViewById(R.id.audio_time);


        showTimeView1 = (TextView) rootView.findViewById(R.id.audio_time1);
        remoteNumberLayout = rootView.findViewById(R.id.remote_number_layout);

        initMorePopWindow();
    }

    /**
     * initMorePopWindow
     */
    private void initMorePopWindow()
    {
        // more view
        LinearLayout grouplayout = new LinearLayout(rootView.getContext());
        moreView = (ViewGroup) ((LayoutInflater) rootView.getContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE)).inflate(
                R.layout.video_call_menu_popupwindow, grouplayout, false);
        // closeCamera
        closeCamera = (LinearLayout) moreView.findViewById(R.id.close_camera);
        closeCamera.setOnClickListener(this);
        ImageView closeCameraImg = (ImageView) moreView.findViewById(R.id.close_camera_img);
        closeCameraImg.setImageDrawable(rootView.getResources().getDrawable(videoClickRes[0][1]));
        // closePip
        closePip = (LinearLayout) moreView.findViewById(R.id.close_pip);
        closePip.setOnClickListener(this);
        closePip.setTag(CallConstants.MENU_CLICK_EVENT);

        closePipImg = (ImageView) moreView.findViewById(R.id.close_pip_img);
        closePipImg.setImageDrawable(rootView.getResources().getDrawable(
                R.drawable.te_state_close_pip));
        // switchAudio
        switchAudio = (LinearLayout) moreView.findViewById(R.id.switch_audio);
        switchAudio.setOnClickListener(this);

        switchAudioImg = (ImageView) moreView.findViewById(R.id.switch_audio_img);
        switchAudioImg.setImageDrawable(rootView.getResources().getDrawable(
                R.drawable.te_state_video_switch_audio));
        // switchCamera
        switchCamera = (LinearLayout) moreView.findViewById(R.id.switch_camera);
        switchCamera.setOnClickListener(this);
        switchCameraImg = (ImageView) moreView.findViewById(R.id.switch_camera_img);
        switchCameraImg.setImageDrawable(rootView.getResources().getDrawable(
                R.drawable.te_state_switch_camera));



        // speakerControl
        speakerControl = (RelativeLayout) moreView.findViewById(R.id.video_speaker);
        if (null != speakerControl)
        {
            speakerControl.setTag(true);
            speakerControl.setOnClickListener(this);
        }
        speakerControlImg = (ImageView) moreView.findViewById(R.id.video_speaker_img);
        if (null != speakerControlImg)
        {
            speakerControlImg.setImageDrawable(rootView.getResources().getDrawable(
                    R.drawable.te_phone_more_open_speaker));
        }
        switchCameraImg.setImageBitmap(Tools.readBitMap(rootView.getContext(),
                R.drawable.te_state_switch_camera));

        if (morePopWindow == null)
        {
            morePopWindow = new PopupWindow(rootView.getContext())
            {
                @Override
                public void showAsDropDown(View anchor, int xoff, int yoff)
                {
                    super.showAsDropDown(anchor, xoff, yoff);
                    menuBar.getMenuItems(VideoMenuBar.MORE).setSelected(true);
                }

                @Override
                public void dismiss()
                {
                    super.dismiss();
                    menuBar.getMenuItems(VideoMenuBar.MORE).setSelected(false);
                }

            };
        }
        moreView.measure(0, 0);
        morePopWindow.setWidth(moreView.getMeasuredWidth());
        morePopWindow.setHeight(moreView.getMeasuredHeight());
        morePopWindow.setBackgroundDrawable(new BitmapDrawable());
        morePopWindow.setOutsideTouchable(true);
        morePopWindow.setFocusable(true);
        morePopWindow.setContentView(moreView);
    }

    @Override
    public void onClick(View v)
    {

        if (menuBar != null)
        {
            menuBar.coverTime();
        }
        else
        {
            TUPLogUtil.i(TAG, "menuBar is null!");
            return;
        }

        int id = v.getId();
        if (id == R.id.close_camera)
        {
            ImageView closeCameraImg = (ImageView) moreView.findViewById(R.id.close_camera_img);
            operateCamera(closeCameraImg);
        }
        else if (id == R.id.close_pip)
        {
            closePipUI();
        }
        else if (id == R.id.switch_audio)
        {
            videoToAudio(v);
        }
        else if (id == R.id.switch_camera)
        {
            switchCamera(switchCameraImg);
        }

    }

    /**
     * closePip
     */
    private void closePipUI()
    {
        TextView closePipTxt = (TextView) moreView.findViewById(R.id.close_pip_txt);
        if (CallConstants.MENU_CLICK_EVENT.equals(closePip.getTag()))
        {
            menuItemServer.setPip(false);
            closePip.setTag(null);
            closePipTxt.setText(R.string.show_video_window);
            closePipImg.setImageDrawable(rootView.getResources().getDrawable(
                    R.drawable.te_state_open_pip));
        }
        else
        {
            menuItemServer.setPip(true);
            closePip.setTag(CallConstants.MENU_CLICK_EVENT);
            closePipTxt.setText(R.string.hide_video_window);
            closePipImg.setImageDrawable(rootView.getResources().getDrawable(
                    R.drawable.te_state_close_pip));
        }
        closePipImg.getDrawable().setAlpha(NOT_ALPHA);
    }

    private void refreshAudioRouteItem()
    {
        List<Integer> audioRouteList = CallService.getInstance().getAudioRouteList();
        if (audioRouteList.size() <= 1 || CallConstants.TYPE_EARPHONE == audioRouteList.get(0))
        {

        }
        else
        {
            int curAudioRoute = audioRouteList.get(0);
            int resId = 0;
            TUPLogUtil.i(TAG, "Handset switch -> " + curAudioRoute);
            switch (curAudioRoute)
            {
                case CallConstants.TYPE_LOUD_SPEAKER:
                    resId = audioRouteRes[0][0];
                    break;

                case CallConstants.TYPE_TELRECEIVER:
                    resId = audioRouteRes[0][1];
                    break;

                default:
                    break;
            }
        }
    }

    /**
     * Video call mode
     */
    public void videoCallMode()
    {
        menuBar.getMenuItems(VideoMenuBar.AUDIO_VIDEO).setEnabled(true);
        menuBar.getMenuItemsImg(VideoMenuBar.AUDIO_VIDEO).getDrawable().setAlpha(NOT_ALPHA);

        menuBar.getMenuItems(VideoMenuBar.REDIAL_BOARD).setEnabled(true);
        menuBar.getMenuItemsImg(VideoMenuBar.REDIAL_BOARD).getDrawable().setAlpha(NOT_ALPHA);


        remoteNumberLayout.setVisibility(View.GONE);
        menuBar.setMenuItemVisible(VideoMenuBar.HANG_UP, View.VISIBLE);

        menuBar.isVideoAudioGONE();
        menuBar.setMenuItemVisible(VideoMenuBar.MIC, View.VISIBLE);
        menuBar.setMenuItemVisible(VideoMenuBar.SPEAKER, View.VISIBLE);
        menuBar.setMenuItemVisible(VideoMenuBar.SWITCH_AUDIO_ROUTE, View.VISIBLE);
        menuBar.setMenuItemVisible(VideoMenuBar.REDIAL_BOARD, View.VISIBLE);


        moreView.measure(0, 0);
        if (null != morePopWindow)
        {
            morePopWindow.setHeight(moreView.getMeasuredHeight());
            if (morePopWindow.isShowing())
            {
                morePopWindow.dismiss();
            }
        }
        menuBar.setMenuItemVisible(VideoMenuBar.MORE, View.VISIBLE);
        menuBar.setMenuItemVisible(VideoMenuBar.AUDIO_VIDEO, View.GONE);

        switchAudioImg.getDrawable().setAlpha(NOT_ALPHA);
        switchAudio.setEnabled(true);

        closePipImg.getDrawable().setAlpha(NOT_ALPHA);
        closePip.setEnabled(true);

        menuBar.setAutoHidden(false);
        TUPLogUtil.i(TAG, "now videoCallMode");
    }

    private void videoCallingMode()
    {
        videoCallMode();
        menuBar.getMenuItems(VideoMenuBar.REDIAL_BOARD).setEnabled(false);
        menuBar.getMenuItemsImg(VideoMenuBar.REDIAL_BOARD).getDrawable().setAlpha(HALF_ALPHA);

        switchAudioImg.getDrawable().setAlpha(HALF_ALPHA);
        switchAudio.setEnabled(false);

        closePipImg.getDrawable().setAlpha(HALF_ALPHA);
        closePip.setEnabled(false);
        menuBar.setItemLineVisibility(VideoMenuBar.REDIAL_BOARD, View.VISIBLE);
        TUPLogUtil.i(TAG, "now video--->videoCallingMode");
    }

    private void audioCallMode()
    {
        menuBar.getMenuItems(VideoMenuBar.AUDIO_VIDEO).setEnabled(true);
        menuBar.getMenuItemsImg(VideoMenuBar.AUDIO_VIDEO).getDrawable().setAlpha(NOT_ALPHA);

        menuBar.getMenuItems(VideoMenuBar.REDIAL_BOARD).setEnabled(true);
        menuBar.getMenuItemsImg(VideoMenuBar.REDIAL_BOARD).getDrawable().setAlpha(NOT_ALPHA);


        remoteNumberLayout.setVisibility(View.GONE);

        menuBar.setMenuItemVisible(VideoMenuBar.AUDIO_VIDEO, View.VISIBLE);
        menuBar.setMenuItemVisible(VideoMenuBar.REDIAL_BOARD, View.VISIBLE);
        menuBar.setMenuItemVisible(VideoMenuBar.MIC, View.VISIBLE);
        menuBar.setMenuItemVisible(VideoMenuBar.SPEAKER, View.VISIBLE);
        menuBar.setMenuItemVisible(VideoMenuBar.SWITCH_AUDIO_ROUTE, View.VISIBLE);

        menuBar.setMenuItemVisible(VideoMenuBar.MORE, View.GONE);


        menuBar.setMenuItemVisible(VideoMenuBar.HANG_UP, View.VISIBLE);

        menuBar.setAutoHidden(false);
        TUPLogUtil.i(TAG, "audioCallMode()");
    }

    private void audioCallingMode()
    {
        audioCallMode();

        menuBar.getMenuItems(VideoMenuBar.AUDIO_VIDEO).setEnabled(false);
        menuBar.getMenuItemsImg(VideoMenuBar.AUDIO_VIDEO).getDrawable().setAlpha(HALF_ALPHA);

        menuBar.getMenuItems(VideoMenuBar.REDIAL_BOARD).setEnabled(false);
        menuBar.getMenuItemsImg(VideoMenuBar.REDIAL_BOARD).getDrawable().setAlpha(HALF_ALPHA);

        TUPLogUtil.i(TAG, "audioCallingMode()");
    }

    @Override
    public void showMoreOpre(View view)
    {
        if (morePopWindow != null && !morePopWindow.isShowing())
        {
            int distance = Float.valueOf(MORE_POP_DISTANCE * LayoutUtil.getInstance().
                    getScreenPXScale()).intValue();
            if (!switchAudio.isEnabled())
            {
                switchAudioImg.getDrawable().setAlpha(HALF_ALPHA);
                switchAudio.setEnabled(false);
            }
            else
            {
                switchAudioImg.getDrawable().setAlpha(NOT_ALPHA);
                switchAudio.setEnabled(true);
            }

            morePopWindow.showAsDropDown(view, 0, distance);
        }

    }

    private void operateCamera(final ImageView view)
    {
        boolean operate = isDone
                || (CallConstants.STATUS_VIDEOING != CallService.getInstance().getVoipStatus()
                && CallConstants.STATUS_VIDEOINIT != CallService.getInstance().getVoipStatus());
        if (operate)
        {
            TUPLogUtil.i(TAG, "last close video click was not readly");
            return;
        }

        isDone = true;
        operPool.execute(new Runnable()
        {
            @Override
            public void run()
            {
                synchronized (MENULOCK)
                {
                    boolean bHasCloseCamera = CallConstants.MENU_CLICK_EVENT.equals(view.getTag());
//                    String bmpPath = Environment.getExternalStorageDirectory() + File.separator + "CameraBlack.BMP";
                    CallService.getInstance().localCamera(!bHasCloseCamera);
                    TUPLogUtil.i(TAG, "operate camera, isClose: " + !bHasCloseCamera);
                    Message msg = new Message();
                    msg.what = REFRESH_UI_OPERATE_LOCAL_CAMERA;
                    msg.obj = !bHasCloseCamera;
                    handlerUI.sendMessage(msg);
                    isDone = false;
                }
            }
        });
    }

    @Override
    public void closeMIC(ImageView view)
    {
        TUPLogUtil.i(TAG, "closeMIC is clicked()");
        if (isDone)
        {
            TUPLogUtil.i(TAG, "other click not readly");
            return;
        }
        isDone = true;

        if (CallConstants.MENU_CLICK_EVENT.equals(view.getTag()))
        {
            view.setImageResource(micClickRes[0][0]);
            view.setTag("");

            CallService.getInstance().setLocalMute(true, false);
            TUPLogUtil.i(TAG, "open local MIC Success");
            isDone = false;
        }
        else
        {
            view.setImageResource(micClickRes[0][1]);
            view.setTag(CallConstants.MENU_CLICK_EVENT);

            CallService.getInstance().setLocalMute(true, true);
            TUPLogUtil.i(TAG, "close local MIC Success");
            isDone = false;
        }
    }

    /**
     * closeSpeaker
     *
     * @param view the view
     */
    @Override
    public void closeSpeaker(ImageView view)
    {
        if (isDone)
        {
            TUPLogUtil.i(TAG, "other click not readly");
            return;
        }
        isDone = true;

        if (CallConstants.MENU_CLICK_EVENT.equals(view.getTag()))
        {
            view.setImageResource(outputClickRes[0][0]);
            view.setTag("");

            CallService.getInstance().oratorMute(false);
            TUPLogUtil.i(TAG, "open local Speaker");
            isDone = false;
        }
        else
        {
            view.setImageResource(outputClickRes[0][1]);
            view.setTag(CallConstants.MENU_CLICK_EVENT);

            CallService.getInstance().oratorMute(true);
            TUPLogUtil.i(TAG, "close local Speaker");
            isDone = false;
        }
    }

    /**
     * switchAudioRoute
     *
     * @param view the view
     */
    @Override
    public void switchAudioRoute(ImageView view)
    {
        boolean isSwitchSuccess = false;
        TUPLogUtil.i(TAG, "switchAudioRoute is clicked()");
        if (isDone)
        {
            TUPLogUtil.i(TAG, "other click not readly");
            return;
        }
        isDone = true;
        int audioRoute = CallService.getInstance().getAudioRoute();
        TUPLogUtil.i(TAG, "audioRoute is" + audioRoute);
        if (audioRoute == CallConstants.TYPE_LOUD_SPEAKER)
        {
            view.setImageResource(audioRouteRes[0][1]);
            isSwitchSuccess = CallService.getInstance().setAudioRoute(CallConstants.TYPE_TELRECEIVER);
            TUPLogUtil.i(TAG, "set telReceiver Success" + isSwitchSuccess);
            isDone = false;
        }
        else
        {
            view.setImageResource(audioRouteRes[0][0]);
            isSwitchSuccess = CallService.getInstance().setAudioRoute(CallConstants.TYPE_LOUD_SPEAKER);
            TUPLogUtil.i(TAG, "set speaker Success" + isSwitchSuccess);
            isDone = false;
        }
    }



    /**
     * switchCamera
     *
     * @param view the view
     */
    @Override
    public void switchCamera(final View view)
    {
        if (CallService.getInstance().getCameraCapacty(CallConstants.BACK_CAMERA)
                == CallConstants.CAMERA_NON
                || CallService.getInstance().getCameraCapacty(CallConstants.FRONT_CAMERA)
                == CallConstants.CAMERA_NON)
        {
            Toast.makeText(rootView.getContext(), rootView.getContext().getString(R.string.camera_bad),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (isDone || (CallConstants.STATUS_VIDEOING != CallService.getInstance().getVoipStatus()
                && CallConstants.STATUS_VIDEOINIT != CallService.getInstance().getVoipStatus()))
        {
            TUPLogUtil.i(TAG, "other click not readly or camera is closed");
            return;
        }

        isDone = true;
        operPool.execute(new Runnable()
        {
            @Override
            public void run()
            {
                synchronized (MENULOCK)
                {
                    if (videoIndex == CallConstants.BACK_CAMERA)
                    {
                        videoIndex = CallConstants.FRONT_CAMERA;
                        CallService.getInstance().setCameraIndex(CallConstants.FRONT_CAMERA);
                    }
                    else
                    {
                        videoIndex = CallConstants.BACK_CAMERA;
                        CallService.getInstance().setCameraIndex(CallConstants.BACK_CAMERA);
                    }
                    TUPLogUtil.i(TAG, "videoIndex->" + videoIndex);
                    String currentCallID = CallService.getInstance().getCurrentCallID();
                    boolean result = CallService.getInstance().setVideoOrient(
                            Integer.parseInt(currentCallID), videoIndex);

                    TUPLogUtil.i(TAG, "result->" + result);
                    if (result)
                    {
                        TUPLogUtil.i(TAG, "switch local camera Success");
                    }
                    isDone = false;
                }
            }
        });
    }

    /**
     * videoToAudio
     *
     * @param view the view
     */
    @Override
    public void videoToAudio(View view)
    {

        if (isDone)
        {
            TUPLogUtil.i(TAG, "other click not readly");
            return;
        }
        isDone = true;

        if (CallConstants.STATUS_TALKING == CallService.getInstance().getVoipStatus())
        {
            operPool.execute(new Runnable()
            {
                @Override
                public void run()
                {
                    if (null == Looper.myLooper())
                    {
                        Looper.prepare();
                    }
                    synchronized (MENULOCK)
                    {
                        CallService.getInstance().upgradeVideo();
                        TUPLogUtil.i(TAG, "upgradevideo");
                        isDone = false;
                    }
                }
            });
        }
        else if (CallConstants.STATUS_VIDEOING == CallService.getInstance().getVoipStatus())
        {
            operPool.execute(new Runnable()
            {
                @Override
                public void run()
                {
                    synchronized (MENULOCK)
                    {
                        boolean result = CallService.getInstance().closeVideo();
                        isDone = false;
                        if (!result)
                        {
                            return;
                        }
                        TUPLogUtil.i(TAG, "video -- > audio");
                    }
                }
            });
        }
        else
        {
            isDone = false;
        }
    }

    /**
     * audioRecall
     *
     * @param view the view
     */
    @Override
    public void audioRecall(View view)
    {
        recallPopWindow = new RecallPopWindow(rootView.getContext(), menuBar.
                getMenuItems(VideoMenuBar.REDIAL_BOARD))
        {
            @Override
            public void showAsDropDown(View anchor, int xoff, int yoff)
            {
                super.showAsDropDown(anchor, xoff, yoff);
                menuBar.getMenuItems(VideoMenuBar.REDIAL_BOARD).setSelected(true);
            }

            @Override
            public void dismiss()
            {
                super.dismiss();
                menuBar.getMenuItems(VideoMenuBar.REDIAL_BOARD).setSelected(false);
            }
        };
        int distance = Float.valueOf((MORE_POP_DISTANCE * LayoutUtil.getInstance().
                getScreenPXScale())).intValue();

        recallPopWindow.showAsDropDown(menuBar.getMenuItems(VideoMenuBar.REDIAL_BOARD),
                (-recallPopWindow.getWidth()) / 2, 0);
    }

    /**
     * endVideoCall
     */
    @Override
    public void endVideoCall()
    {
        TUPLogUtil.i(TAG, "endVideoCall send end call request.");
        CallActivity.getInstance().getCallFragment().sendHandlerMessage(CallConstants.MSG_CALL_END_EVENT, null);
    }

    @Override
    public void dismissPopupWindow()
    {
        if (null != recallPopWindow)
        {
            recallPopWindow.dismiss();
        }

        if (null != morePopWindow)
        {
            morePopWindow.dismiss();
        }
    }

    @Override
    public void dismissMorePopWindow()
    {
        if (null != morePopWindow)
        {
            morePopWindow.dismiss();
        }
    }

    @Override
    public void showConfList()
    {
        menuItemServer.showConfListView();
    }

    @Override
    public void gotoShare()
    {
        menuItemServer.gotoShare();
    }


    /**
     * resetMIC
     */
    private void resetMIC()
    {
        menuBar.getMenuItemsImg(VideoMenuBar.MIC).setImageResource(micClickRes[0][0]);
        operPool.execute(new Runnable()
        {
            @Override
            public void run()
            {
                CallService.getInstance().setLocalMute(true, false);
            }
        });
    }

    /**
     * resetSpeaker
     */
    private void resetSpeaker()
    {
        menuBar.getMenuItemsImg(VideoMenuBar.SPEAKER).setImageResource(outputClickRes[0][0]);
        operPool.execute(new Runnable()
        {
            @Override
            public void run()
            {
                CallService.getInstance().oratorMute(false);
            }
        });
    }

    /**
     * formatTimeFString
     *
     * @param longTime
     * @return String
     */
    private String formatTimeFString(long longTime)
    {
        String time = "%2d:%2d:%2d";
        int hour = parseLongToInt(longTime / (60 * 60));
        int min = parseLongToInt((longTime - hour * (60L * 60)) / 60);
        int sec = parseLongToInt(longTime % 60);
        time = String.format(time, hour, min, sec);

        return time.replace(' ', '0');
    }

    private int parseLongToInt(long value)
    {
        return Long.valueOf(value).intValue();
    }

    /**
     * resetTime
     */
    private void resetTime()
    {
        if (null != service)
        {
            service.shutdown();
            service = null;
        }
        autoTime = 0;
        isCount = false;
        showTimeView.setText("");
        showTimeView1.setText("");

        setRun(false);
    }

    private void refreshMenuItemLocalCamera(boolean bIsClose)
    {
        TUPLogUtil.i(TAG, "refreshMenuItemLocalCamera enter: " + bIsClose);

        ImageView view = (ImageView) moreView.findViewById(R.id.close_camera_img);
        TextView closeCameraTxt = (TextView) moreView.findViewById(R.id.close_camera_txt);

        TUPLogUtil.i(TAG, "refreshMenuItemLocalCamera bIsClose: " + bIsClose);

        switchCamera.setEnabled(!bIsClose);
        switchCameraImg.getDrawable().setAlpha(!bIsClose ? NOT_ALPHA : HALF_ALPHA);

        if (!bIsClose)
        {
            view.setImageDrawable(rootView.getResources().getDrawable(videoClickRes[0][1]));
            view.setTag("");
            closeCameraTxt.setText(R.string.close_local_camera);
        }
        else
        {
            view.setImageDrawable(rootView.getResources().getDrawable(videoClickRes[0][0]));
            view.setTag(CallConstants.MENU_CLICK_EVENT);
            closeCameraTxt.setText(R.string.open_local_camera);
        }

        TUPLogUtil.i(TAG, "refreshMenuItemLocalCamera leave.");

    }

    /**
     * clearData
     */
    public void clearData()
    {
        this.isRun = false;
        timeHandler = null;
        handlerUI = null;
        recallPopWindow = null;
        morePopWindow = null;
        remoteNumberView = null;
        remoteNumberLayout = null;
        showTimeView = null;
        showTimeView1 = null;
        micClickRes = null;
        videoClickRes = null;
        audioRouteRes = null;
        outputClickRes = null;
        audioScreenRes = null;
        rootView = null;
        closePip = null;
        switchAudio = null;
        switchAudioImg = null;
        closePipImg = null;
        if (null != menuBar)
        {
            menuBar.clearData();
        }
    }

    /**
     * startTimer
     */
    private void startTimer()
    {
        if (null == service)
        {
            service = Executors.newScheduledThreadPool(1);
        }
        service.scheduleAtFixedRate(new Runnable()
        {
            @Override
            public void run()
            {
                ++autoTime;
                timeHandler.sendEmptyMessage(0);
            }
        }, 1, 1, TimeUnit.SECONDS);
    }


    /**
     * The interface Menu item server.
     */
    public interface MenuItemServer
    {
        /**
         * Closed and open pip
         *
         * @param isPip the is pip
         */
        void setPip(boolean isPip);


        void showConfListView();

        void gotoShare();
    }

    /**
     * Menu mode
     */
    public enum Mode
    {
        /**
         * video call
         */
        VIDEO_CALL,

        /**
         * video calling
         */
        VIDEO_CALLING,

        /**
         * video audio switch
         */
        VIDEO_AUDIO_SWITCH,

        /**
         * audio calling
         */
        AUDIO_CALLING,

        /**
         * audio call
         */
        AUDIO_CALL,

    }
}
