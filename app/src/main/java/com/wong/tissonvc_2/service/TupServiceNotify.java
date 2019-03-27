package com.wong.tissonvc_2.service;


import object.KickOutInfo;
import object.OnLineState;
import object.TupRegisterResult;
import tupsdk.TupCall;

/**
 * The interface Tup service notify.
 */
public interface TupServiceNotify
{

    /**
     * On register result.
     *
     * @param regRet the reg ret
     */
    void onRegisterResult(TupRegisterResult regRet);

    /**
     * On be kicked out.
     *
     * @param kickOutInfo the kick out info
     */
    void onBeKickedOut(KickOutInfo kickOutInfo);

    /**
     * On call coming.
     *
     * @param call the call
     */
    void onCallComing(TupCall call);

    /**
     * On call going.
     *
     * @param tupCall the tup call
     */
    void onCallGoing(TupCall tupCall);

    /**
     * On call ring back.
     *
     * @param tupCall the tup call
     */
    void onCallRingBack(TupCall tupCall);

    /**
     * On call connected.
     *
     * @param call the call
     */
    void onCallConnected(TupCall call);

    /**
     * On call add video.
     *
     * @param call the call
     */
    void onCallAddVideo(TupCall call);

    /**
     * On call del video.
     *
     * @param call the call
     */
    void onCallDelVideo(TupCall call);

    /**
     * On call viedo result.
     *
     * @param call the call
     */
    void onCallViedoResult(TupCall call);

    /**
     * On call refresh view.
     *
     * @param call the call
     */
    void onCallRefreshView(TupCall call);

    /**
     * On call ended.
     *
     * @param call the call
     */
    void onCallEnded(TupCall call);

    /**
     * On call hold success.
     *
     * @param tupCall the tup call
     */
    void onCallHoldSuccess(TupCall tupCall);

    /**
     * On call hold failed.
     *
     * @param tupCall the tup call
     */
    void onCallHoldFailed(TupCall tupCall);

    /**
     * On call un hold success.
     *
     * @param tupCall the tup call
     */
    void onCallUnHoldSuccess(TupCall tupCall);

    /**
     * On call un hold failed.
     *
     * @param tupCall the tup call
     */
    void onCallUnHoldFailed(TupCall tupCall);

    /**
     * On call bld transfer success.
     *
     * @param tupCall the tup call
     */
    void onCallBldTransferSuccess(TupCall tupCall);

    /**
     * On call bld transfer failed.
     *
     * @param tupCall the tup call
     */
    void onCallBldTransferFailed(TupCall tupCall);

    /**
     * On set ipt service suc.
     *
     * @param i the
     */
    void onSetIptServiceSuc(int i);

    /**
     * On set ipt service fal.
     *
     * @param i the
     */
    void onSetIptServiceFal(int i);

    /**
     * On data ready.
     *
     * @param var1 the var 1
     * @param var2 the var 2
     */
    void onDataReady(int var1, int var2);

    /**
     * On bfcp reinited.
     *
     * @param var1 the var 1
     */
    void onBFCPReinited(int var1);

    /**
     * On data sending.
     *
     * @param var1 the var 1
     */
    void onDataSending(int var1);

    /**
     * On data receiving.
     *
     * @param var1 the var 1
     */
    void onDataReceiving(int var1);

    /**
     * On data stopped.
     *
     * @param var1 the var 1
     */
    void onDataStopped(int var1);

    /**
     * On data start err.
     *
     * @param var1 the var 1
     * @param var2 the var 2
     */
    void onDataStartErr(int var1, int var2);

    /**
     * On line state notify.
     *
     * @param var1 the var 1
     */
    void onLineStateNotify(OnLineState var1);

    /**
     * On data framesize change.
     *
     * @param var1 the var 1
     */
    void onDataFramesizeChange(TupCall var1);

}
