package com.wong.tissonvc_2.service.notify;

import com.huawei.meeting.ConfInfo;


/**
 * The interface Vc data conf notify.
 */
public interface VCDataConfNotify
{
    /**
     * On get data conf params result.
     *
     * @param confInfo the conf info
     */
    void onGetDataConfParamsResult(ConfInfo confInfo);

    /**
     * On data share result.
     *
     * @param shareVal    the share val
     * @param shareStatus the share status
     */
    void onDataShareResult(int shareVal, int shareStatus);
}
