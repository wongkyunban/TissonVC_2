package com.wong.tissonvc_2.service.call.data;



import com.wong.tissonvc_2.service.call.CallService;
import com.wong.tissonvc_2.service.utils.TUPLogUtil;

import java.util.HashMap;

/**
 * The type Video caps.
 * <p/>
 * VideoCaps
 * Video data class
 */
public class VideoCaps
{
    private static final String TAG = VideoCaps.class.getSimpleName();

    public static final HashMap<Integer, Integer> FRAME_SIZE_MAP = new HashMap() {
        {
            this.put(Integer.valueOf(1), Integer.valueOf(1));
            this.put(Integer.valueOf(2), Integer.valueOf(6));
            this.put(Integer.valueOf(3), Integer.valueOf(2));
            this.put(Integer.valueOf(4), Integer.valueOf(7));
            this.put(Integer.valueOf(6), Integer.valueOf(3));
            this.put(Integer.valueOf(5), Integer.valueOf(13));
            this.put(Integer.valueOf(7), Integer.valueOf(14));
            this.put(Integer.valueOf(8), Integer.valueOf(8));
            this.put(Integer.valueOf(10), Integer.valueOf(4));
            this.put(Integer.valueOf(9), Integer.valueOf(15));
            this.put(Integer.valueOf(11), Integer.valueOf(9));
            this.put(Integer.valueOf(12), Integer.valueOf(10));
            this.put(Integer.valueOf(13), Integer.valueOf(5));
            this.put(Integer.valueOf(14), Integer.valueOf(11));
            this.put(Integer.valueOf(15), Integer.valueOf(12));
        }
    };

    private static final int DEFAULT_VALUE = 0;
    private static final int MAX_RATIO_VALUE = 16;
    private static final int MIX_RATIO_VALUE = 4;
    private static final int PREVIEW_OR_LOW_VERSION_TYPE = 2;
    private static final int DEFAULT_TYPE = 1;
    private static final int MIRROR_TYPE = 2;
    private static final int LOCAL_VERSION_INDEX = 11;
    private static final float DEFAULT_FLOAT_VALUE = 0.0F;
    private static final float RATIO_VALUE = 1.5555556F;
    private static int screenRatio;
    private int cameraIndex = 0;
    private int cameraRotation = 0;
    private int playbackLocal = 255;
    private int playbackRemote = 255;
    private boolean isVideoPreview = false;
    private int remoteRoate = 0;
    private int localRoate = 0;
    private boolean isCloseLocalCamera = false;

    /**
     * init a new Video caps.
     */
    public VideoCaps()
    {
    }

    /**
     * Sets screen ratio.
     *
     * @param w the w
     * @param h the h
     */
    public static void setScreenRatio(int w, int h)
    {
        if (h != DEFAULT_VALUE && w != DEFAULT_VALUE)
        {
            float ratio = w > h ? (float) w / (float) h : (float) h / (float) w;
            screenRatio = (ratio - RATIO_VALUE > DEFAULT_FLOAT_VALUE)
                    ? (MAX_RATIO_VALUE) : (MIX_RATIO_VALUE);
            TUPLogUtil.i(TAG, "the screen w:h=" + w + ":" + h
                    + " the result ratio=" + screenRatio);
        }
    }

    /**
     * Gets display type.
     *
     * @return the display type
     */
    public int getDisplayType()
    {
        byte displayType;
        if (this.isVideoPreview)
        {
            displayType = PREVIEW_OR_LOW_VERSION_TYPE;
        }
        else if (CallService.getInstance().getAndroidVersion() < LOCAL_VERSION_INDEX)
        {
            displayType = PREVIEW_OR_LOW_VERSION_TYPE;
        }
        else
        {
            displayType = DEFAULT_TYPE;
        }

        return displayType;
    }

    /**
     * Gets camera index.
     *
     * @return the camera index
     */
    public int getCameraIndex()
    {
        return this.cameraIndex;
    }

    /**
     * Sets camera index.
     *
     * @param cameraIndex the camera index
     */
    public void setCameraIndex(int cameraIndex)
    {
        this.cameraIndex = cameraIndex;
    }

    /**
     * Gets camera rotation.
     *
     * @return the camera rotation
     */
    public int getCameraRotation()
    {
        return this.cameraRotation;
    }

    /**
     * Sets camera rotation.
     *
     * @param cameraRotation the camera rotation
     */
    public void setCameraRotation(int cameraRotation)
    {
        this.cameraRotation = cameraRotation;
    }

    /**
     * Gets playback local.
     *
     * @return the playback local
     */
    public int getPlaybackLocal()
    {
        return this.playbackLocal;
    }

    /**
     * Sets playback local.
     *
     * @param playbackLocal the playback local
     */
    public void setPlaybackLocal(int playbackLocal)
    {
        this.playbackLocal = playbackLocal;
    }

    /**
     * Gets playback remote.
     *
     * @return the playback remote
     */
    public int getPlaybackRemote()
    {
        return this.playbackRemote;
    }

    /**
     * Sets playback remote.
     *
     * @param playbackRemote the playback remote
     */
    public void setPlaybackRemote(int playbackRemote)
    {
        this.playbackRemote = playbackRemote;
    }

    /**
     * Gets mirror type.
     *
     * @return the mirror type
     */
    public int getMirrorType()
    {
        byte mirrorType = DEFAULT_VALUE;
        if (DEFAULT_TYPE == this.cameraIndex)
        {
            mirrorType = MIRROR_TYPE;
        }

        return mirrorType;
    }

    /**
     * Gets remote roate.
     *
     * @return the remote roate
     */
    public int getRemoteRoate()
    {
        return this.remoteRoate;
    }

    /**
     * Gets local roate.
     *
     * @return the local roate
     */
    public int getLocalRoate()
    {
        return this.localRoate;
    }

    /**
     * Sets local roate.
     *
     * @param localRoate the local roate
     */
    public void setLocalRoate(int localRoate)
    {
        this.localRoate = localRoate;
    }

    /**
     * Is close local camera boolean.
     *
     * @return the boolean
     */
    public boolean isCloseLocalCamera()
    {
        return this.isCloseLocalCamera;
    }

    /**
     * Sets is close local camera.
     *
     * @param isCloseLocalCamera the is close local camera
     */
    public void setIsCloseLocalCamera(Boolean isCloseLocalCamera)
    {
        this.isCloseLocalCamera = isCloseLocalCamera.booleanValue();
    }

    /**
     * The type Display type.
     */
    public static final class DisplayType
    {
        /**
         * The constant DISPLAY_TYPE_BORDER.
         */
        public static final int DISPLAY_TYPE_BORDER = 1;
        /**
         * The constant DISPLAY_TYPE_CLIPPING.
         */
        public static final int DISPLAY_TYPE_CLIPPING = 2;

        private DisplayType()
        {
        }
    }
}

