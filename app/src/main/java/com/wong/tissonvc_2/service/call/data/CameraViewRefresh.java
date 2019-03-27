package com.wong.tissonvc_2.service.call.data;

import java.io.Serializable;

/**
 * The type Camera view refresh.
 * <p/>
 * CameraViewRefresh
 * Camera screen refresh related properties
 */
public class CameraViewRefresh implements Serializable
{
    private static final long serialVersionUID = 1L;
    private int mediaType;
    private int viewType;

    /**
     * Instantiates a new Camera view refresh.
     */
    public CameraViewRefresh()
    {
    }

    /**
     * Gets media type.
     *
     * @return the media type
     */
    public int getMediaType()
    {
        return this.mediaType;
    }

    /**
     * Sets media type.
     *
     * @param mediaType the media type
     */
    public void setMediaType(int mediaType)
    {
        this.mediaType = mediaType;
    }

    /**
     * Gets view type.
     *
     * @return the view type
     */
    public int getViewType()
    {
        return this.viewType;
    }

    /**
     * Sets view type.
     *
     * @param viewType the view type
     */
    public void setViewType(int viewType)
    {
        this.viewType = viewType;
    }

    /**
     * toString
     *
     * @return the mediaType ,the viewType
     */
    public String toString()
    {
        return "media-type:" + this.mediaType + " view-type:" + this.viewType;
    }
}
