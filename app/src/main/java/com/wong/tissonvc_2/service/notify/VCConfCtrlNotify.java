package com.wong.tissonvc_2.service.notify;

import com.huawei.tup.confctrl.sdk.TupConfBaseAttendeeInfo;
import com.huawei.tup.confctrl.sdk.TupConfOptResult;
import com.huawei.tup.confctrl.sdk.TupConference;


/**
 * The interface Vc conf ctrl notify.
 */
public interface VCConfCtrlNotify
{
    /**
     * On request chairman result.
     *
     * @param result the result
     */
    void onRequestChairmanResult(TupConfOptResult result);

    /**
     * On release chairman result.
     *
     * @param result the result
     */
    void onReleaseChairmanResult(TupConfOptResult result);

    /**
     * On conf postpone result.
     *
     * @param result the result
     */
    void onConfPostponeResult(TupConfOptResult result);

    /**
     * On mute attendee result.
     *
     * @param result the result
     * @param b      the b
     */
    void onMuteAttendeeResult(TupConfOptResult result, boolean b);

    /**
     * On end conf result.
     *
     * @param result the result
     */
    void onEndConfResult(TupConfOptResult result);

    /**
     * On hangup attendee result.
     *
     * @param result the result
     */
    void onHangupAttendeeResult(TupConfOptResult result);

    /**
     * On add attendee result.
     *
     * @param result the result
     */
    void onAddAttendeeResult(TupConfOptResult result);

    /**
     * On del attendee result.
     *
     * @param result the result
     */
    void onDelAttendeeResult(TupConfOptResult result);

    /**
     * On watch attendee result.
     *
     * @param result the result
     */
    void onWatchAttendeeResult(TupConfOptResult result);

    /**
     * On call attendee result.
     *
     * @param result the result
     */
    void onCallAttendeeResult(TupConfOptResult result);

    /**
     * On broadcast attendee ind.
     *
     * @param tupConference           the tup conference
     * @param tupConfBaseAttendeeInfo the tup conf base attendee info
     */
    void onBroadcastAttendeeInd(TupConference tupConference, TupConfBaseAttendeeInfo tupConfBaseAttendeeInfo);

    /**
     * On cancel broadcast attendee ind.
     *
     * @param tupConference           the tup conference
     * @param tupConfBaseAttendeeInfo the tup conf base attendee info
     */
    void onCancelBroadcastAttendeeInd(TupConference tupConference, TupConfBaseAttendeeInfo tupConfBaseAttendeeInfo);




}
