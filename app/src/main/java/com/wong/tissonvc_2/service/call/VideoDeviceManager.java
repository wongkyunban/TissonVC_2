package com.wong.tissonvc_2.service.call;

import android.content.Context;
import android.hardware.Camera;
import android.os.Handler;
import android.text.TextUtils;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.wong.tissonvc_2.service.TupEventMgr;
import com.wong.tissonvc_2.service.call.data.VideoCaps;
import com.wong.tissonvc_2.service.utils.TUPLogUtil;
import com.huawei.videoengine.ViERenderer;

import java.util.HashMap;
import java.util.Map;

import common.VideoWndType;

/**
 * The type Video handler.
 * <p/>
 * VideoDeviceManager
 * video handler Open local video.
 * Remote video. Clear video data
 */
public final class VideoDeviceManager
{
    /**
     * The constant CAMERA_NON.
     */
    private static final int CAMERA_NON = -1;
    /**
     * The constant CAMERA_NORMAL.
     */
    private static final int CAMERA_NORMAL = 0;
    /**
     * Rear camera
     */
    public static final int BACK_CAMERA = 0;

    /**
     * front camera
     */
    public static final int FRONT_CAMERA = 1;
    private static final String TAG = VideoDeviceManager.class.getSimpleName();
    private static final Object LOCK = new Object();

    /**
     * control lock
     */
    private static final Object RENDER_CHANGE_LOCK = new Object();

    private static VideoDeviceManager ins;

    private Context context = null;

    private Handler uiHandler = null;

    /**
     * Camera capability
     */
    private Map<Integer, Integer> cameraCapacity = new HashMap<Integer, Integer>(0);

    private int cameraIndex = FRONT_CAMERA;

    private SurfaceView localHideView;

    /**
     * Local video screen
     */
    private SurfaceView localCallView;

    /**
     * Remote video picture
     */
    private SurfaceView remoteCallView;

    /**
     * Remote video
     */
    private RelativeLayout remoteVideoView;

    /**
     * Local video view handle
     */
    private int localCallIndex;

    private int curLocalIndex;

    /**
     * Remote video view handle
     */
    private int remoteCallIndex;

    /*
     * The current use of the remote window handle
     */
    private int curUseRemoteRenderIndex;


    /**
     * local bfcp view
     */
    private SurfaceView localBfcpView;

    /**
     * remote bfcp view
     */
    private SurfaceView remoteBfcpView;

    /**
     * Remote auxiliary stream video view handle
     */
    private int remoteBfcpIndex;

    /**
     * Local secondary stream video view handle
     */
    private int localBfcpIndex;

    /**
     * Camera number
     */
    private int numberOfCameras;

    // Add video parameters
    private VideoCaps videoCaps = new VideoCaps();
    // bfcp caps
    private VideoCaps dataCaps = new VideoCaps();

    private boolean isInit = false;

    private boolean isRenderServerInit = false;

    private boolean isNeedAdd = false;

    private WindowManager mWindowManager;

    private WindowManager.LayoutParams wmParams;

    private ViewGroup localHideViewGroup;


    private VideoDeviceManager()
    {
        context = TupEventMgr.getTupContext();
        if (context == null)
        {
            throw new NullPointerException("BaseApp not initialated");
        }

        uiHandler = new Handler(context.getMainLooper());

        numberOfCameras = Camera.getNumberOfCameras();
        cameraCapacity.put(FRONT_CAMERA, CAMERA_NORMAL);
        cameraCapacity.put(BACK_CAMERA, CAMERA_NORMAL);
    }

    /**
     * Gets ins.
     *
     * @return the ins
     */
    public static synchronized VideoDeviceManager getIns()
    {
        if (ins == null)
        {
            ins = new VideoDeviceManager();
        }
        return ins;
    }

    /**
     * Gets local hide view.
     *
     * @return the local hide view
     */
    public SurfaceView getLocalHideView()
    {
        return localHideView;
    }

    /**
     * Sets local hide view.
     *
     * @param localHideView the local hide view
     */
    public void setLocalHideView(SurfaceView localHideView)
    {
        this.localHideView = localHideView;
    }

    /**
     * Gets local call view.
     *
     * @return the local call view
     */
    public SurfaceView getLocalCallView()
    {
        return localCallView;
    }

    /**
     * Sets local call view.
     *
     * @param lv the lv
     */
    public void setLocalCallView(SurfaceView lv)
    {
        this.localCallView = lv;
    }

    /**
     * Gets remote call view.
     *
     * @return the remote call view
     */
    public SurfaceView getRemoteCallView()
    {
        return remoteCallView;
    }

    /**
     * Sets remote call view.
     *
     * @param rv the rv
     */
    public void setRemoteCallView(SurfaceView rv)
    {
        this.remoteCallView = rv;
    }

    /**
     * Gets caps.
     *
     * @return the caps
     */
    public VideoCaps getCaps()
    {
        return videoCaps;
    }

    /**
     * is support video calls
     *
     * @return the boolean
     */
    public boolean isSupportVideo()
    {
        return numberOfCameras > 0;
    }

    /**
     * Gets video handle.
     *
     * @return the video handle
     */
    public int getVideoHandle()
    {
        return curLocalIndex;
    }

    /**
     * initCallVideo
     *
     * @return the video caps
     */
    public VideoCaps initCallVideo()
    {
        if (isInit)
        {
            return videoCaps;
        }

        isInit = true;
        TUPLogUtil.i(TAG, "Init Call Video");
        videoCaps = new VideoCaps();
        dataCaps = new VideoCaps();
        localHideView = ViERenderer.createLocalRenderer(context);
        localHideView.setZOrderOnTop(false);

        // Get  local video view
        localCallView = ViERenderer.createRenderer(context, false);
        // Gets  remote video view
        remoteCallView = ViERenderer.createRenderer(context, false);

        // Get  remote bfcp view
        remoteBfcpView = ViERenderer.createRenderer(context, false);
        // Get  local bfcp view
        localBfcpView = ViERenderer.createRenderer(context, false);

        localBfcpView.setZOrderOnTop(false);

        remoteBfcpView.setZOrderOnTop(false);

        remoteCallView.setZOrderOnTop(false);
        localCallView.setZOrderOnTop(false);
        localCallView.setZOrderMediaOverlay(true);

        localCallIndex = ViERenderer.getIndexOfSurface(localCallView);
        curLocalIndex = localCallIndex;

        remoteCallIndex = ViERenderer.getIndexOfSurface(remoteCallView);
        curUseRemoteRenderIndex = remoteCallIndex;


        remoteBfcpIndex = ViERenderer.getIndexOfSurface(remoteBfcpView);

        localBfcpIndex = ViERenderer.getIndexOfSurface(localBfcpView);


        // Get a local video handle
        cameraIndex = numberOfCameras > 1 ? FRONT_CAMERA : BACK_CAMERA;
        if (cameraCapacity.get(BACK_CAMERA) == CAMERA_NON)
        {
            cameraIndex = FRONT_CAMERA;
        }
        else if (cameraCapacity.get(FRONT_CAMERA) == CAMERA_NON)
        {
            cameraIndex = BACK_CAMERA;
        }
        curUseRemoteRenderIndex = remoteCallIndex;

        videoCaps.setCameraIndex(cameraIndex);
        videoCaps.setPlaybackLocal(curLocalIndex);
        videoCaps.setPlaybackRemote(curUseRemoteRenderIndex);
        int camOrieantation = 0;
        videoCaps.setCameraRotation(camOrieantation);

        dataCaps.setPlaybackLocal(localBfcpIndex);
        dataCaps.setPlaybackRemote(remoteBfcpIndex);

        CallService.getInstance().setOrientParams(videoCaps);
        CallService.getInstance().setVideoRenderInfo(videoCaps, VideoWndType.local);
        CallService.getInstance().setVideoRenderInfo(videoCaps, VideoWndType.remote);
        String callId = CallService.getInstance().getCurrentCallID();
        if (TextUtils.isEmpty(callId))
        {
            callId = "0";
        }
        CallService.getInstance().setVideoOrient(Integer.parseInt(callId), FRONT_CAMERA);

        return videoCaps;
    }

    /**
     * Clear data
     */
    public void clearCallVideo()
    {
        TUPLogUtil.i(TAG, "clearCallVideo() enter");

        uiHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                isInit = false;
                clearHMERendr();

                // Reset close camera flag;
                videoCaps.setIsCloseLocalCamera(false);
                cameraIndex = FRONT_CAMERA;
                // Reset camera for lead;
                videoCaps.setCameraIndex(cameraIndex);

                // Release local video data
                ViERenderer.freeLocalRenderResource();
                ViERenderer.setSurfaceNullFromIndex(remoteCallIndex);
                ViERenderer.setSurfaceNullFromIndex(localCallIndex);
                if (localCallView != null)
                {
                    ViERenderer.setSurfaceNull(localCallView);
                    localCallView.setVisibility(View.GONE);
                    localCallView = null;
                }
                if (remoteCallView != null)
                {
                    // Release remote video data
                    ViERenderer.setSurfaceNull(remoteCallView);
                    remoteCallView.setVisibility(View.GONE);
                    remoteCallView = null;
                }
                if (localHideView != null)
                {
                    localHideView.setVisibility(View.GONE);
                    localHideView = null;
                }

                if (localBfcpView != null)
                {
                    ViERenderer.setSurfaceNull(localBfcpView);
                    localBfcpView.setVisibility(View.GONE);
                    localBfcpView = null;
                }
                if (remoteBfcpView != null)
                {
                    ViERenderer.setSurfaceNull(remoteBfcpView);
                    remoteBfcpView.setVisibility(View.GONE);
                    remoteBfcpView = null;
                }

            }

            /**
             * Ringing crash, clear HMERender
             */
            private void clearHMERendr()
            {
                addViewToContainer(localCallView, remoteVideoView);
                if (null != remoteVideoView)
                {
                    remoteVideoView.removeAllViews();
                }
                addViewToContainer(remoteCallView, remoteVideoView);
                if (null != remoteVideoView)
                {
                    remoteVideoView.removeAllViews();
                }

                addViewToContainer(localBfcpView, remoteVideoView);
                if (null != remoteVideoView)
                {
                    remoteVideoView.removeAllViews();
                }
                addViewToContainer(remoteBfcpView, remoteVideoView);
                if (null != remoteVideoView)
                {
                    remoteVideoView.removeAllViews();
                }

            }
        });
    }

    /**
     * Add the local and remote video
     * images to the interface layout.
     *
     * @param localViewContain  the local view contain
     * @param remoteViewContain the remote view contain
     */
    public void addRenderToContain(ViewGroup localViewContain,
            ViewGroup remoteViewContain)
    {
        TUPLogUtil.i(TAG, "addRenderToContain()");
        synchronized (RENDER_CHANGE_LOCK)
        {
            setRemoteVideoView((RelativeLayout) remoteViewContain);
            localViewContain.removeAllViews();
            remoteViewContain.removeAllViews();
            SurfaceView localVV = getLocalCallView();
            SurfaceView remoteVV = getRemoteCallView();

            if (null == localVV || null == remoteVV)
            {
                return;
            }
            remoteVV.setZOrderMediaOverlay(false);
            localVV.setZOrderMediaOverlay(true);
            addViewToContainer(remoteVV, remoteViewContain);
            addViewToContainer(localVV, localViewContain);

        }
    }

    private SurfaceView getRemoteBfcpView()
    {
        return remoteBfcpView;
    }

    public boolean openBFCPReceive(int callId,ViewGroup localVideoView, ViewGroup remoteVideoView)
    {
        TUPLogUtil.i(TAG,"-----------------openBFCPReceive---");
        SurfaceView localVV = getLocalCallView();
        SurfaceView remoteVV = getRemoteCallView();
        SurfaceView remoteBfcpView = getRemoteBfcpView();
        boolean bRet = (null == localVV || null == remoteVV || null == remoteBfcpView);
        if (bRet)
        {
            TUPLogUtil.e(TAG,"-----------------bRet is true--------------");
            return false;
        }

        remoteVideoView.removeAllViews();
        // add
        int index = ViERenderer.getIndexOfSurface(remoteBfcpView);
        CallService.getInstance().operateVideoWindow(3,index,callId+"",1);
        addViewToContainer(remoteBfcpView, remoteVideoView);
        if (localVideoView.getChildAt(0) != localVV)
        {
            addViewToContainer(localVV, localVideoView);
        }
        localVV.setVisibility(View.GONE);
        remoteVV.setVisibility(View.GONE);

        if (null != remoteBfcpView)
        {
            remoteBfcpView.setVisibility(View.VISIBLE);
            TUPLogUtil.i(TAG,"----------------remoteBfcpView show");
            return true;
        }
        return false;
    }


    /**
     * refreshLocalHide
     *
     * @param isAdd1 the is add 1
     */
    public void refreshLocalHide(boolean isAdd1)
    {
        final boolean isAdd = isAdd1;

        uiHandler.post(new Runnable()
        {

            @Override
            public void run()
            {
                View localHI = getLocalHideView();
                if (localHI == null)
                {
                    TUPLogUtil.i(TAG, "localHI is null");
                    return;
                }
                if (!isAdd)
                {
                    removeView(localHI);
                }
                else
                {
                    addView(localHI);

                    if (null != getRemoteCallView() && null != getLocalCallView())
                    {
                        getRemoteCallView().postInvalidate();
                        getLocalCallView().postInvalidate();
                    }
                }

            }
        });
    }

    /**
     * Is init boolean.
     *
     * @return the boolean
     */
    public boolean isInit()
    {
        return isInit;
    }

    /**
     * Sets remote video view.
     *
     * @param remoteVideoView the remote video view
     */
    public void setRemoteVideoView(RelativeLayout remoteVideoView)
    {
        this.remoteVideoView = remoteVideoView;
    }

    /**
     * Ability to access the camera
     *
     * @param cameraIndex the camera index
     * @return -1,0,1
     */
    public int getCameraCapacty(int cameraIndex)
    {
        return cameraCapacity.get(cameraIndex);
    }

    /**
     * removeView
     *
     * @param viewVar the view var
     */
    private void removeView(View viewVar)
    {
        synchronized (LOCK)
        {
            if (null == viewVar || null == localHideViewGroup)
            {
                return;
            }
            if (null == localHideViewGroup.getParent() || null == viewVar.getParent())
            {
                if (isNeedAdd)
                {
                    startCameraStream();
                }
                TUPLogUtil.i(TAG, "first add view");
                return;
            }
            localHideViewGroup.removeView(viewVar);
            mWindowManager.removeViewImmediate(localHideViewGroup);
            TUPLogUtil.i(TAG, "local hide view removed");

            if (isNeedAdd)
            {
                startCameraStream();
            }
        }
    }

    /**
     * addView
     *
     * @param viewVar the view var
     */
    public void addView(View viewVar)
    {
        synchronized (LOCK)
        {
            if (null == viewVar)
            {
                TUPLogUtil.i(TAG, "view is null");
                return;
            }
            if (null == localHideViewGroup)
            {
                TUPLogUtil.i(TAG, "parent is null  new a parent");
                localHideViewGroup = new LinearLayout(TupEventMgr.getTupContext());
            }
            if (null != viewVar.getParent())
            {
                stopCameraStream();
                isNeedAdd = true;
                return;
            }
            localHideViewGroup.addView(viewVar);
            if (null == localHideViewGroup.getParent())
            {
                mWindowManager.addView(localHideViewGroup, wmParams);
            }
            TUPLogUtil.i(TAG, "local hide view add");
            isNeedAdd = false;
        }
    }

    /**
     * Remove collection point
     */
    public void removeView()
    {
        synchronized (LOCK)
        {
            if (null == localHideViewGroup)
            {
                return;
            }
            TUPLogUtil.i(TAG, "remove local hide view");
            localHideViewGroup.removeAllViews();
            if (null != localHideViewGroup.getParent())
            {
                mWindowManager.removeViewImmediate(localHideViewGroup);
            }
            localHideViewGroup = null;
        }
    }

    /**
     * On create.
     */
    public void onCreate()
    {
        TUPLogUtil.i(TAG, "LocalHideRenderServer onCreate()");
        isNeedAdd = false;
        localHideViewGroup = new LinearLayout(TupEventMgr.getTupContext());
        this.mWindowManager = (WindowManager) TupEventMgr.getTupContext().getSystemService(
                Context.WINDOW_SERVICE);
        wmParams = new WindowManager.LayoutParams();
        wmParams.type = 2003;
        wmParams.gravity = 51;
        wmParams.flags = 520;
        wmParams.format = 1;
        wmParams.width = 1;
        wmParams.height = 1;
        wmParams.x = -1;
        wmParams.y = -1;

        TUPLogUtil.i(TAG, "start local hide service");
    }

    /**
     * On destroy.
     */
    public void onDestroy()
    {
        TUPLogUtil.e(TAG, "LocalHideRender onDestroy()");
        if (!CallService.getInstance().isCallClosed())
        {
            TUPLogUtil.i(TAG, "currentCallID is not NULL ");
            CallService.getInstance().forceCloseCall();
        }

        if (null != ins && !ins.isRenderServerInit)
        {
            return;
        }
        TUPLogUtil.i(TAG, "local hide service destroy");
        if (null != ins)
        {
            ins.isRenderServerInit = false;
        }
        if (null == localHideViewGroup)
        {
            localHideViewGroup = null;
            mWindowManager = null;
            return;
        }
        localHideViewGroup.removeAllViews();
        if (null != localHideViewGroup.getParent())
        {
            mWindowManager.removeViewImmediate(localHideViewGroup);
        }
        localHideViewGroup = null;
        mWindowManager = null;
    }

    /**
     * addViewToContainer
     *
     * @param videoView    the video view
     * @param videoContain the video contain
     */
    public void addViewToContainer(View videoView, ViewGroup videoContain)
    {
        if (null == videoView || null == videoContain)
        {
            TUPLogUtil.i(TAG, "addViewToContainer(),some view is null");
            return;
        }
        ViewGroup container = (ViewGroup) videoView.getParent();
        videoView.setVisibility(View.GONE);
        videoContain.removeAllViews();
        if (null == container)
        {
            TUPLogUtil.i(TAG, "No Parent");
            videoContain.addView(videoView);
        }
        else if (!container.equals(videoContain))
        {
            container.removeView(videoView);
            TUPLogUtil.i(TAG, "Diferent Parent");
            videoContain.addView(videoView);
        }
        else
        {
            TUPLogUtil.i(TAG, "Same Parent");
        }
        videoContain.setVisibility(View.VISIBLE);
        videoView.setVisibility(View.VISIBLE);
    }

    /**
     * Open camera data
     */
    private void startCameraStream()
    {
        synchronized (LOCK)
        {
            TUPLogUtil.i(TAG, "startCameraStream");
            CallService.getInstance().controlVideoCapture(true);
        }
    }

    /**
     * Close camera data
     */
    private void stopCameraStream()
    {
        synchronized (LOCK)
        {
            TUPLogUtil.i(TAG, "stopCameraStream");
            CallService.getInstance().controlVideoCapture(false);
        }
    }


}
