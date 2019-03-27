package com.wong.tissonvc_2.service.conf;


/**
 * The type User info.
 */
public class UserInfo
{
    /**
     * userId（Soft terminal number）
     */
    private String userId;
    /**
     * The User name.
     */
    private String userName;

    /**
     * Gets user name.
     *
     * @return the user name
     */
    public String getUserName()
    {
        return userName;
    }

    /**
     * Sets user name.
     *
     * @param userName the user name
     */
    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    /**
     * The Is host.
     */
    private boolean isHost = false;
    /**
     * The Is speaker.
     */
    private boolean isSpeaker = false;

    /**
     * Instantiates a new User info.
     */
    public UserInfo()
    {
    }

    /**
     * Instantiates a new User info.
     *
     * @param userId the user id
     */
    public UserInfo(String userId)
    {
        this.userId = userId;
    }

    /**
     * Instantiates a new User info.
     *
     * @param userId    the user id
     * @param isHost    the is host
     * @param isSpeaker the is speaker
     */
    public UserInfo(String userId, boolean isHost, boolean isSpeaker)
    {
        this.userId = userId;
        this.isHost = isHost;
        this.isSpeaker = isSpeaker;
    }

    /**
     * Gets user id.
     *
     * @return the user id
     */
    public String getUserId()
    {
        return userId;
    }

    /**
     * Sets user id.
     *
     * @param userId the user id
     */
    public void setUserId(String userId)
    {
        this.userId = userId;
    }


    /**
     * Is host boolean.
     *
     * @return the boolean
     */
    public boolean isHost()
    {
        return isHost;
    }

    /**
     * Sets host.
     *
     * @param host the host
     */
    public void setHost(boolean host)
    {
        isHost = host;
    }

    /**
     * Is speaker boolean.
     *
     * @return the boolean
     */
    public boolean isSpeaker()
    {
        return isSpeaker;
    }

    /**
     * Sets speaker.
     *
     * @param speaker the speaker
     */
    public void setSpeaker(boolean speaker)
    {
        isSpeaker = speaker;
    }

}
