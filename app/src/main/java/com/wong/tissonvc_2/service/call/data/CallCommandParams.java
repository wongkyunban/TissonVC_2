package com.wong.tissonvc_2.service.call.data;

/**
 * The type Call command params.
 * <p/>
 * CallCommandParams
 * Call related attributes
 */
public class CallCommandParams
{
    private String callID;
    private int muteType;
    private boolean isNeedMute;
    private String dialCode;
    private boolean isVideo;
    private String callNumber;
    private String domain;
    private VideoCaps caps = null;

    /**
     * Instantiates a new Call command params.
     */
    public CallCommandParams()
    {
    }

    /**
     * Gets caps.
     *
     * @return the caps
     */
    public VideoCaps getCaps()
    {
        return this.caps;
    }

    /**
     * Sets caps.
     *
     * @param caps the caps
     */
    public void setCaps(VideoCaps caps)
    {
        this.caps = caps;
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
     * Sets call id.
     *
     * @param callID the call id
     */
    public void setCallID(String callID)
    {
        this.callID = callID;
    }

    /**
     * Gets mute type.
     *
     * @return the mute type
     */
    public int getMuteType()
    {
        return this.muteType;
    }

    /**
     * Sets mute type.
     *
     * @param muteType the mute type
     */
    public void setMuteType(int muteType)
    {
        this.muteType = muteType;
    }

    /**
     * Is need mute boolean.
     *
     * @return the boolean
     */
    public boolean isNeedMute()
    {
        return this.isNeedMute;
    }

    /**
     * Sets need mute.
     *
     * @param isMute the is mute
     */
    public void setNeedMute(boolean isMute)
    {
        this.isNeedMute = isMute;
    }

    /**
     * Gets dial code.
     *
     * @return the dial code
     */
    public String getDialCode()
    {
        return this.dialCode;
    }

    /**
     * Sets dial code.
     *
     * @param dialCode the dial code
     */
    public void setDialCode(String dialCode)
    {
        this.dialCode = dialCode;
    }

    /**
     * Is video boolean.
     *
     * @return the boolean
     */
    public boolean isVideo()
    {
        return this.isVideo;
    }

    /**
     * Sets video.
     *
     * @param isVideo the is video
     */
    public void setVideo(boolean isVideo)
    {
        this.isVideo = isVideo;
    }

    /**
     * Gets call number.
     *
     * @return the call number
     */
    public String getCallNumber()
    {
        return this.callNumber;
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
     * Gets domain.
     *
     * @return the domain
     */
    public String getDomain()
    {
        return this.domain;
    }

    /**
     * Sets domain.
     *
     * @param domain the domain
     */
    public void setDomain(String domain)
    {
        this.domain = domain;
    }


}