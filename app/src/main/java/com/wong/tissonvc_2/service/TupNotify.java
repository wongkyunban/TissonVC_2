package com.wong.tissonvc_2.service;


/**
 * The interface Tup notify.
 */
public interface TupNotify
{
    /**
     * On register notify.
     *
     * @param registerResult the register result
     * @param errorCode      the error code
     */
    void onRegisterNotify(int registerResult, int errorCode);

    /**
     * On uportal login.
     *
     * @param smcAuthorizeResult the smc login result
     * @param errorReason        the error reason
     */
    void onSMCLogin(int smcAuthorizeResult, String errorReason);

    /**
     * On call notify
     *
     * @param code   the code
     * @param object the object
     */
    void onCallNotify(int code, Object object);



}
