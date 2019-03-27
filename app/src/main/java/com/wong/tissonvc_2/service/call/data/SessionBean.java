package com.wong.tissonvc_2.service.call.data;

import java.io.Serializable;

/**
 * The type Session bean.
 * <p/>
 * SessionBean
 * Session entity class
 */
public class SessionBean implements Serializable
{
    private String callID;
    private String callerNumber;
    private String callerDisplayname;
    private String releaseReason;
    private String calleeNumber;
    private String operation;
    private boolean isVideoCall;
    private int isRemoteVideoState;
    private String reasonHeader;
    private int videoModifyState;
    private int callType;

    /**
     * Instantiates a new Session bean.
     */
    public SessionBean()
    {
    }

    /**
     * Gets video modify state.
     *
     * @return the video modify state
     */
    public int getVideoModifyState()
    {
        return this.videoModifyState;
    }

    /**
     * Sets video modify state.
     *
     * @param videoModifyState the video modify state
     */
    public void setVideoModifyState(int videoModifyState)
    {
        this.videoModifyState = videoModifyState;
    }

    /**
     * Gets operation.
     *
     * @return the operation
     */
    public String getOperation()
    {
        return this.operation;
    }

    /**
     * Sets operation.
     *
     * @param operation the operation
     */
    public void setOperation(String operation)
    {
        this.operation = operation;
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
     * Gets caller number.
     *
     * @return the caller number
     */
    public String getCallerNumber()
    {
        return this.callerNumber;
    }

    /**
     * Sets caller number.
     *
     * @param callerNumber the caller number
     */
    public void setCallerNumber(String callerNumber)
    {
        this.callerNumber = callerNumber;
    }

    /**
     * Gets release reason.
     *
     * @return the release reason
     */
    public String getReleaseReason()
    {
        return this.releaseReason;
    }

    /**
     * Sets release reason.
     *
     * @param releaseReason the release reason
     */
    public void setReleaseReason(String releaseReason)
    {
        this.releaseReason = releaseReason;
    }

    /**
     * Gets callee number.
     *
     * @return the callee number
     */
    public String getCalleeNumber()
    {
        return this.calleeNumber;
    }

    /**
     * Sets callee number.
     *
     * @param calleeNumber the callee number
     */
    public void setCalleeNumber(String calleeNumber)
    {
        this.calleeNumber = calleeNumber;
    }

    /**
     * Is video call boolean.
     *
     * @return the boolean
     */
    public boolean isVideoCall()
    {
        return this.isVideoCall;
    }

    /**
     * Sets video call.
     *
     * @param isVideoCall the is video call
     */
    public void setVideoCall(boolean isVideoCall)
    {
        this.isVideoCall = isVideoCall;
    }

    /**
     * Gets reason header.
     *
     * @return the reason header
     */
    public String getReasonHeader()
    {
        return this.reasonHeader;
    }

    /**
     * Sets reason header.
     *
     * @param reasonHeader the reason header
     */
    public void setReasonHeader(String reasonHeader)
    {
        this.reasonHeader = reasonHeader;
    }

    /**
     * Gets remote video state.
     *
     * @return the remote video state
     */
    public int getRemoteVideoState()
    {
        return this.isRemoteVideoState;
    }

    /**
     * Sets remote video state.
     *
     * @param videoState the video state
     */
    public void setRemoteVideoState(int videoState)
    {
        this.isRemoteVideoState = videoState;
    }

    /**
     * Gets caller displayname.
     *
     * @return the caller displayname
     */
    public String getCallerDisplayname()
    {
        return this.callerDisplayname;
    }

    /**
     * Sets caller displayname.
     *
     * @param callerDisplayname the caller displayname
     */
    public void setCallerDisplayname(String callerDisplayname)
    {
        this.callerDisplayname = callerDisplayname;
    }

    /**
     * Gets call type.
     *
     * @return the call type
     */
    public int getCallType()
    {
        return this.callType;
    }

    /**
     * Sets call type.
     *
     * @param callType the call type
     */
    public void setCallType(int callType)
    {
        this.callType = callType;
    }

}