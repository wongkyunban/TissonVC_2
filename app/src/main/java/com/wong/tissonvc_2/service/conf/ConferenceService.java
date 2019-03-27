
package com.wong.tissonvc_2.service.conf;


import android.os.Environment;
import android.util.Log;

import com.wong.tissonvc_2.service.login.LoginService;
import com.wong.tissonvc_2.service.login.data.LoginParams;
import com.wong.tissonvc_2.service.notify.VCConfCtrlNotify;
import com.wong.tissonvc_2.service.notify.VCConfNotify;
import com.wong.tissonvc_2.service.notify.VCDataConfNotify;
import com.wong.tissonvc_2.service.utils.TUPLogUtil;
import com.wong.tissonvc_2.ui.application.TUPApplication;
import com.huawei.meeting.ConfInfo;
import com.huawei.tup.TUPInterfaceService;
import com.huawei.tup.confctrl.ConfctrlConfEnvType;
import com.huawei.tup.confctrl.ConfctrlIPVersion;
import com.huawei.tup.confctrl.ConfctrlSiteCallLanguageType;
import com.huawei.tup.confctrl.ConfctrlSiteCallTerminalType;
import com.huawei.tup.confctrl.sdk.TupConfAccessInfo;
import com.huawei.tup.confctrl.sdk.TupConfAttendeeOptResult;
import com.huawei.tup.confctrl.sdk.TupConfBaseAttendeeInfo;
import com.huawei.tup.confctrl.sdk.TupConfBookVcHostedConfInfo;
import com.huawei.tup.confctrl.sdk.TupConfBookVcOnPremiseConfInfo;
import com.huawei.tup.confctrl.sdk.TupConfCreateInstantConfResult;
import com.huawei.tup.confctrl.sdk.TupConfDataConfParamsGetReq;
import com.huawei.tup.confctrl.sdk.TupConfECAttendeeInfo;
import com.huawei.tup.confctrl.sdk.TupConfInfo;
import com.huawei.tup.confctrl.sdk.TupConfManager;
import com.huawei.tup.confctrl.sdk.TupConfNotify;
import com.huawei.tup.confctrl.sdk.TupConfOptResult;
import com.huawei.tup.confctrl.sdk.TupConfSpeakerInfo;
import com.huawei.tup.confctrl.sdk.TupConfVCAttendeeInfo;
import com.huawei.tup.confctrl.sdk.TupConfVCTerminalInfo;
import com.huawei.tup.confctrl.sdk.TupConfVMRInfo;
import com.huawei.tup.confctrl.sdk.TupConfctrlDataconfParams;
import com.huawei.tup.confctrl.sdk.TupConference;

import java.util.ArrayList;
import java.util.List;

import object.DataConfParam;

/**
 * The type Conference service.
 */
public class ConferenceService implements TupConfNotify
{
    /**
     * The constant TAG.
     */
    private static final String TAG = ConferenceService.class.getSimpleName();
    /**
     * The constant ins.
     */
    private static ConferenceService ins;

    /**
     * TupConfManager
     */
    private TupConfManager tupConfManager;

    /**
     * TupConference
     */
    private TupConference tupConference;

    public void setGetDataFlag(boolean getDataFlag)
    {
        this.getDataFlag = getDataFlag;
    }

    private boolean getDataFlag = false;

    public void setDataConfParam(DataConfParam dataConfParam)
    {
        this.dataConfParam = dataConfParam;
    }

    private DataConfParam dataConfParam = null;


    /**
     * VCConfNotify
     * onBookReservedConfResult
     */
    private VCConfNotify vcConfNotify;

    /**
     * VCConfCtrlNotify
     * onRequestChairmanResult onReleaseChairmanResult... and so on
     */
    private VCConfCtrlNotify vcConfCtrlNotify;

    /**
     * VCDataConfNotify
     * onGetDataConfParamsResult onDataShareResult
     */
    private VCDataConfNotify vcDataConfNotify;

    /**
     * chairman flag
     */
    private boolean isChairman = false;

    /**
     * The Is conf mode.
     */
    private boolean isConfMode = false;

    /**
     * The Is add.
     */
    private boolean isAdd = false;

    /**
     * The Attendee info list.
     */
    private List<TupConfVCAttendeeInfo> attendeeInfoList = new ArrayList<>();

    /**
     * Is conf mode boolean.
     *
     * @return the boolean
     */
    public boolean isConfMode()
    {
        return isConfMode;
    }

    /**
     * Sets attendee info list.
     *
     * @param attendeeInfoList the attendee info list
     */
    public void setAttendeeInfoList(List<TupConfVCAttendeeInfo> attendeeInfoList)
    {
        this.attendeeInfoList = attendeeInfoList;
    }

    /**
     * Sets conf mode.
     *
     * @param confMode the conf mode
     */
    public void setConfMode(boolean confMode)
    {
        isConfMode = confMode;
    }

    /**
     * Instantiates a new Conference service.
     */
    private ConferenceService()
    {

    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public synchronized static ConferenceService getInstance()
    {
        if (ins == null)
        {
            ins = new ConferenceService();
        }
        return ins;
    }

    /**
     * register notify
     *
     * @param notify the notify
     */
    public void registerVCConfNotify(VCConfNotify notify)
    {
        vcConfNotify = notify;
    }

    /**
     * Register vc conf ctrl notify.
     *
     * @param notify the notify
     */
    public void registerVCConfCtrlNotify(VCConfCtrlNotify notify)
    {
        vcConfCtrlNotify = notify;
    }

    /**
     * Register vc data conf notify.
     *
     * @param notify the notify
     */
    public void registerVCDataConfNotify(VCDataConfNotify notify)
    {
        vcDataConfNotify = notify;
    }


    /**
     * Gets attendee info list.
     *
     * @return the attendee info list
     */
    public List<TupConfVCAttendeeInfo> getAttendeeInfoList()
    {
        return attendeeInfoList;
    }


    /**
     * Gets conf by conf handle.
     *
     * @param handle handle
     * @return TupConference conf by conf handle
     */
    public TupConference getConfByConfHandle(int handle)
    {
        return tupConfManager.getConfByConfHandle(handle);
    }

    /**
     * log params
     *
     * @param level   the log level
     * @param maxsize file maxsize kb
     * @param counts  the file count
     * @param path    the log path
     */
    public void setLogParam(int level, int maxsize, int counts, String path)
    {
        tupConfManager.setLogParam(level, maxsize, counts, path);
    }

    /**
     * init params
     *
     * @param batchUpdate         the batch update
     * @param connectCall         the connect call
     * @param waitMsgpThread      the wait msgp thread
     * @param saveParticipantList the save participant list
     */
    public void setInitParam(int batchUpdate, int connectCall
            , int waitMsgpThread, int saveParticipantList)
    {
        tupConfManager.setInitParam(batchUpdate, connectCall, waitMsgpThread, saveParticipantList);
    }

    /**
     * init conf
     *
     * @param tupInterfaceService the tup interface service
     * @return int
     */
    public int confInit(TUPInterfaceService tupInterfaceService)
    {
        tupConfManager = TupConfManager.getIns(ConferenceService.this, TUPApplication.getApplication());
        setLogParam(3, 2 * 1024, 2, Environment.getExternalStorageDirectory().toString() + "/VCLOG");
        setInitParam(0, 1, 1, 1);

        int ret = tupConfManager.confInit(tupInterfaceService);
        TUPLogUtil.i(TAG, "confInit result:" + ret);
        return ret;
    }

    /**
     * set conf env type
     *
     * @param confEnvType the conf env type
     * @return conf type
     */
    public int setConfType(ConfctrlConfEnvType confEnvType)
    {
        return tupConfManager.setConfType(confEnvType);
    }

    /**
     * set conf server
     *
     * @param serverAddr server addr
     * @param port       server port
     * @return conf server
     */
    public int setConfServer(String serverAddr, int port)
    {
        return tupConfManager.setConfServer(serverAddr, port);
    }


    public int setAuthToken(String token)
    {
        return tupConfManager.setAuthToken(token);
    }


    /**
     * set account  auth  info
     *
     * @param account  account
     * @param password pwd
     * @return auth account info
     */
    public int setAuthAccountInfo(String account, String password)
    {
        return tupConfManager.setAuthAccountInfo(account, password);
    }

    /**
     * start conference interface
     *
     * @param var1 TupConfBookVcOnPremiseConfInfo
     * @return int
     */
    public int bookOnPremiseReservedConf(TupConfBookVcOnPremiseConfInfo var1)
    {
        return tupConfManager.bookReservedConf(var1);
    }


    public int bookReservedConf(TupConfBookVcHostedConfInfo var1)
    {
        return tupConfManager.bookReservedConf(var1);
    }


    /**
     * accessConf   create conf handle
     *
     * @param tupConfAccessInfo tupConfAccessInfo
     * @return int
     */
    public int accessConf(TupConfAccessInfo tupConfAccessInfo)
    {
        if (tupConference == null)
        {
            tupConference = new TupConference();
        }
        int ret = tupConference.accessConf(tupConfAccessInfo);
        TUPLogUtil.i(TAG, "-------accessConf result=" + ret);
        return ret;

    }

    /**
     * destroyConf
     *
     * @return int
     */
    public int destroyConf()
    {
        if (tupConference == null)
        {
            return -1;
        }
        return tupConference.destroyConf();
    }

    /**
     * getConfHandle
     *
     * @return conf handle
     */
    public int getConfHandle()
    {
        if (tupConference == null)
        {
            return -1;
        }
        return tupConference.getConfHandle();
    }


    /**
     * Request chairman int.
     *
     * @param password the password
     * @param number   the number
     * @return the int
     */
    public int requestChairman(String password, String number)
    {
        int ret = tupConference.requestChairman(password, number);
        TUPLogUtil.i(TAG, "requestChairman -------result:" + ret);
        return ret;
    }

    /**
     * Release chairman int.
     *
     * @param number the number
     * @return the int
     */
    public int releaseChairman(String number)
    {
        return tupConference.releaseChairman(number);
    }


    /**
     * Is attended boolean.
     *
     * @param number the number
     * @return the boolean
     */
    private boolean isAttended(String number)
    {
        for (TupConfVCAttendeeInfo attendeeInfo : attendeeInfoList)
        {
            if (number.equals(attendeeInfo.getSiteName()))
            {
                return true;
            }
        }
        return false;

    }


    /**
     * Add vc attendee int.
     *
     * @param num the num
     * @return the int
     */
    public int addVCAttendee(String num)
    {
        if (!isChairman)
        {
            return -1;
        }

        TupConfVCTerminalInfo tupConfVCTerminalInfo = new TupConfVCTerminalInfo();
        tupConfVCTerminalInfo.setTerminalID(num);
        tupConfVCTerminalInfo.setTerminalIDLength(num.length());
        tupConfVCTerminalInfo.setPucNumber(num);
        tupConfVCTerminalInfo.setNumberLen(num.length());
        String uri = num + "@" + LoginParams.getInstance().getRegisterServerIp();

        tupConfVCTerminalInfo.setUdwSiteBandwidth(1920);
        List<String> tels = new ArrayList<>();
        tels.add(num);
        tupConfVCTerminalInfo.setTelNum(tels);
        tupConfVCTerminalInfo.setTerminalType(ConfctrlSiteCallTerminalType.CC_sip);
        tupConfVCTerminalInfo.setURI(uri);
        tupConfVCTerminalInfo.setURILen(uri.length());
        tupConfVCTerminalInfo.setIpType(ConfctrlIPVersion.CC_IP_V4);
        tupConfVCTerminalInfo.setLanguageType(ConfctrlSiteCallLanguageType.CC_sitecall_simpleChineseGB2312);
        List<TupConfVCTerminalInfo> lists = new ArrayList<>();
        lists.add(tupConfVCTerminalInfo);
        int ret = tupConference.addVCAttendee(lists);

        TUPLogUtil.i(TAG, "addVCAttendee ----" + num +
                "----------------------result:" + ret);

        return ret;
    }

    /**
     * Remove attendee int.
     *
     * @param var1 MT
     * @return int
     */
    public int removeAttendee(String var1)
    {
        if (!isChairman)
        {
            return -1;
        }
        int ret = tupConference.removeAttendee(getMTByNum(var1));

        TUPLogUtil.i(TAG, "removeAttendee ----------------------result:" + ret);

        return ret;
    }

    /**
     * Recall attendee int.
     *
     * @param var1 MT
     * @return the int
     */
    public int recallAttendee(String var1)
    {
        if (!isChairman)
        {
            return -1;
        }
        int ret = tupConference.recallAttendee(getMTByNum(var1));
        TUPLogUtil.i(TAG, "recallAttendee ----------------------result:" + ret);
        return ret;
    }


    /**
     * Gets mt by num.
     *
     * @param num the num
     * @return the mt by num
     */
    private String getMTByNum(String num)
    {
        String Mt = "0:1";
        for (TupConfVCAttendeeInfo a : attendeeInfoList)
        {
            if (num.equals(a.getSiteName()))
            {

                Mt = a.getMT();
            }
        }
        TUPLogUtil.i(TAG, "getMTByNum---------------------MT=" + Mt);
        return Mt;
    }


    /**
     * Hang up attendee int.
     *
     * @param var1 MT
     * @return the int
     */
    public int hangUpAttendee(String var1)
    {
        if (!isChairman)
        {
            return -1;
        }
        int ret = tupConference.hangUpAttendee(getMTByNum(var1));
        TUPLogUtil.i(TAG, "hangUpAttendee ----------------------result:" + ret);
        return ret;
    }

    /**
     * Mute attendee int.
     *
     * @param var1 MT
     * @param var2 the var 2
     * @return the int
     */
    public int muteAttendee(String var1, Boolean var2)
    {
        if (!isChairman || !isAttended(var1))
        {
            return -1;
        }
        int ret = tupConference.muteAttendee(getMTByNum(var1), var2);
        TUPLogUtil.i(TAG, "muteAttendee ----------------------result:" + ret);
        return ret;
    }

    /**
     * Watch attendee int.
     *
     * @param var1 MT
     * @return the int
     */
    public int watchAttendee(String var1)
    {
        if (!isAttended(var1))
        {
            return -1;
        }
        int ret = tupConference.watchAttendee(getMTByNum(var1));
        TUPLogUtil.i(TAG, "watchAttendee ----------------------result:" + ret);
        return ret;
    }


    /**
     * End conf int.
     *
     * @return the int
     */
    public int endConf()
    {
        int ret = tupConference.endConf();
        TUPLogUtil.i(TAG, "endConf ----------------------result:" + ret);
        if (ret == 0)
        {
            isConfMode = false;
        }

        DataConfService service = DataConfService.getInstance();
        if (service != null && service.getConf() != null)
        {
            service.terminateConf();
            service.releaseConf();
        }

        return ret;
    }


    /**
     * Broadcast attendee int.
     *
     * @param var1 MT
     * @param var2 the var 2
     * @return the int
     */
    public int broadcastAttendee(String var1, Boolean var2)
    {
        if (!isAttended(var1))
        {
            return -1;
        }
        return tupConference.broadcastAttendee(getMTByNum(var1), var2);
    }


    /**
     * postpone conf
     *
     * @param time minute
     * @return int
     */
    public int postponeConf(int time)
    {
        if (!isChairman)
        {
            return -1;
        }
        int ret = tupConference.rostponeConf(time);
        TUPLogUtil.i(TAG, "postponeConf ----------------------result:" + ret);
        return ret;
    }


    /**
     * Enter chairman password int.
     *
     * @param var1 password
     * @return the int
     */
    public int enterChairmanPassword(String var1)
    {
        return tupConference.enterChairmanPassword(var1);
    }


    /**
     * Gets data conf params.
     *
     * @param var1 TupConfDataConfParamsGetReq
     * @return the data conf params
     */
    public int getDataConfParams(TupConfDataConfParamsGetReq var1)
    {
        if (tupConference == null)
        {
            tupConference = new TupConference();
        }
        return tupConference.getDataConfParams(var1);
    }


    @Override
    public void onBookReservedConfResult(TupConfOptResult tupConfOptResult)
    {
        int ret = tupConfOptResult.getOptResult();
        TUPLogUtil.i(TAG, "onBookReservedConfResult1 ----------------------result:" + ret);
        vcConfNotify.onBookReservedConfResult(ret);
    }

    @Override
    public void onBookReservedConfResult(TupConfOptResult tupConfOptResult, TupConfInfo tupConfInfo)
    {
        Log.e(TAG, "----------onBookReservedConfResult2:" + tupConfOptResult.getOptResult());
    }

    @Override
    public void onCreateInstantConfResult(TupConfOptResult tupConfOptResult, TupConfCreateInstantConfResult tupConfCreateInstantConfResult)
    {
        Log.e(TAG, "onCreateInstantConfResult:" + tupConfOptResult.getOptResult());
    }

    @Override
    public void onConfWillTimeOutInd(TupConference tupConference)
    {
        TUPLogUtil.i(TAG, "onConfWillTimeOutInd ----------remainingTime:" + tupConference.getRemainingTime());
    }

    @Override
    public void onConfIncomingInd(TupConference tupConference, String s)
    {
        TUPLogUtil.i(TAG, "onConfIncomingInd ----------------------");
    }

    @Override
    public void onConfConnectedInd(TupConference tupConference)
    {
        //conf connect
        TUPLogUtil.i(TAG, "onConfConnectedInd ----------------------");
        isConfMode = true;

        if (!getDataFlag && (LoginService.getInstance().getVcType() == 1))
        {
            if (dataConfParam == null)
            {
                return;
            }
            TupConfDataConfParamsGetReq req = new TupConfDataConfParamsGetReq();
            req.setConfUrl(dataConfParam.getDataConfUrl());
            req.setConfId(dataConfParam.getDataConfId());
            req.setPassword(dataConfParam.getPassCode());
            req.setRandom(dataConfParam.getDataRandom());
            req.setType(3);
            try
            {
                int result = ConferenceService.getInstance().getDataConfParams(req);
                getDataFlag = true;
                TUPLogUtil.i(TAG, "----getDataConfParams result:" + result);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                TUPLogUtil.e(TAG, "----getDataConfParams exception:" + e.getMessage());
            }
        }


    }


    @Override
    public void onAttendeeListUpdateInd(TupConference tupConference, List<TupConfVCAttendeeInfo> list, int i)
    {
        /*
        * i
        * 0 Join 1 Leave 2 ADD 3 DEL 4 MUTE 5 MUTE_ALL 6 UNMUTE_ALL
         */

        TUPLogUtil.i(TAG, "onAttendeeListUpdateInd----------------------hasChairman:" + tupConference.isHasChairman()
                + ",num:" + list.get(0).getSiteName() + ",i:" + i + ",mt:" + list.get(0).getMT() + "--size:" + list.size());

        TupConfVCAttendeeInfo a = list.get(0);
        if (attendeeInfoList.size() == 0)
        {
            attendeeInfoList.add(a);
        }
        else
        {
            for (TupConfVCAttendeeInfo attendeeInfo : attendeeInfoList)
            {
                if (attendeeInfo.getMT().equals(a.getMT()))
                {
                    isAdd = false;
                    attendeeInfoList.remove(attendeeInfo);
                    attendeeInfoList.add(a);
                    if (i == 3) //3 DELETE
                    {
                        attendeeInfoList.remove(a);
                    }
                    break;
                }
                else
                {
                    isAdd = true;
                }

            }

            if (isAdd)
            {
                attendeeInfoList.add(a);
            }

        }
    }

    @Override
    public void onConfStatusUpdateInd(TupConfInfo tupConfInfo, List<TupConfECAttendeeInfo> list, int i)
    {

    }


    @Override
    public void onChairmanInfoInd(TupConference tupConference)
    {
        TUPLogUtil.i(TAG, "onChairmanInfoInd----------------------");
    }

    @Override
    public void onChairmanReleasedInd(TupConference tupConference, int i)
    {
        TUPLogUtil.i(TAG, "onChairmanReleasedInd----------------------");
    }

    @Override
    public void onBroadcastAttendeeResult(TupConfOptResult tupConfOptResult)
    {
        TUPLogUtil.i(TAG, "onBroadcastAttendeeResult----------------------result:" + tupConfOptResult.getOptResult());
    }

    @Override
    public void onCancelBroadcastAttendeeResult(TupConfOptResult tupConfOptResult)
    {
        TUPLogUtil.i(TAG, "onCancelBroadcastAttendeeResult----------------------result:" + tupConfOptResult.getOptResult());
    }

    @Override
    public void onBroadcastAttendeeInd(TupConference tupConference, TupConfBaseAttendeeInfo tupConfBaseAttendeeInfo)
    {
        TUPLogUtil.i(TAG, "onBroadcastAttendeeInd----------------------");
        vcConfCtrlNotify.onBroadcastAttendeeInd(tupConference, tupConfBaseAttendeeInfo);
    }

    @Override
    public void onCancelBroadcastAttendeeInd(TupConference tupConference, TupConfBaseAttendeeInfo tupConfBaseAttendeeInfo)
    {
        TUPLogUtil.i(TAG, "onCancelBroadcastAttendeeInd----------------------");
        vcConfCtrlNotify.onCancelBroadcastAttendeeInd(tupConference, tupConfBaseAttendeeInfo);
    }

    @Override
    public void onWatchAttendeeResult(TupConfOptResult tupConfOptResult)
    {
        TUPLogUtil.i(TAG, "onWatchAttendeeResult----------------------result:" + tupConfOptResult.getOptResult());
        vcConfCtrlNotify.onWatchAttendeeResult(tupConfOptResult);
    }

    @Override
    public void onMultiPicResult(TupConfOptResult tupConfOptResult)
    {
        Log.e(TAG, "onMultiPicResult----------------------");
    }

    @Override
    public void onAttendeeBroadcastedInd(TupConference tupConference, TupConfBaseAttendeeInfo tupConfBaseAttendeeInfo)
    {
        TUPLogUtil.i(TAG, "onAttendeeBroadcastedInd----------------------");
    }

    @Override
    public void onLocalBroadcastStatusInd(TupConference tupConference, boolean b)
    {
        TUPLogUtil.i(TAG, "onLocalBroadcastStatusInd----------------------");
    }

    @Override
    public void onConfInfoInd(TupConference tupConference)
    {
        Log.e(TAG, "onConfInfoInd----------------------");
    }

    @Override
    public void onEndConfInd(TupConference tupConference)
    {
        TUPLogUtil.i(TAG, "onEndConfInd----------------------");
    }

    @Override
    public void onBeTransToConfInd(TupConference tupConference, int i)
    {
        Log.e(TAG, "onBeTransToConfInd----------------------");
    }

    @Override
    public void onSpeakerListInd(TupConference tupConference, TupConfSpeakerInfo tupConfSpeakerInfo)
    {
        Log.e(TAG, "onSpeakerListInd----------------------");
    }

    @Override
    public void onEndConfResult(TupConfOptResult tupConfOptResult)
    {
        TUPLogUtil.i(TAG, "onEndConfResult----------------------result:" + tupConfOptResult.getOptResult());
        vcConfCtrlNotify.onEndConfResult(tupConfOptResult);
    }

    @Override
    public void onAddAttendeeResult(TupConfAttendeeOptResult tupConfAttendeeOptResult)
    {
        TUPLogUtil.i(TAG, "onAddAttendeeResult----------------------result:" + tupConfAttendeeOptResult.getOptResult());
        vcConfCtrlNotify.onAddAttendeeResult(tupConfAttendeeOptResult);
    }

    @Override
    public void onDelAttendeeResult(TupConfAttendeeOptResult tupConfAttendeeOptResult)
    {
        TUPLogUtil.i(TAG, "onDelAttendeeResult----------------------result:" + tupConfAttendeeOptResult.getOptResult());
        vcConfCtrlNotify.onDelAttendeeResult(tupConfAttendeeOptResult);
    }

    @Override
    public void onCallAttendeeResult(TupConfAttendeeOptResult tupConfAttendeeOptResult)
    {
        TUPLogUtil.i(TAG, "onCallAttendeeResult----------------------result:" + tupConfAttendeeOptResult.getOptResult());
        vcConfCtrlNotify.onCallAttendeeResult(tupConfAttendeeOptResult);
    }

    @Override
    public void onHangupAttendeeResult(TupConfAttendeeOptResult tupConfAttendeeOptResult)
    {
        TUPLogUtil.i(TAG, "onHangupAttendeeResult----------------------result:" + tupConfAttendeeOptResult.getOptResult());
        vcConfCtrlNotify.onHangupAttendeeResult(tupConfAttendeeOptResult);
    }

    @Override
    public void onReqChairmanResult(TupConfOptResult tupConfOptResult)
    {
        int result = tupConfOptResult.getOptResult();
        TUPLogUtil.i(TAG, "onReqChairmanResult----------------------result:" + result);
        if (result == 0)
        {
            isChairman = true;
        }
        vcConfCtrlNotify.onRequestChairmanResult(tupConfOptResult);

    }

    @Override
    public void onRealseChairmanResult(TupConfOptResult tupConfOptResult)
    {
        int result = tupConfOptResult.getOptResult();
        TUPLogUtil.i(TAG, "onRealseChairmanResult----------------------result:" + result);
        if (result == 0)
        {
            isChairman = false;
        }
        vcConfCtrlNotify.onReleaseChairmanResult(tupConfOptResult);

    }

    @Override
    public void onConfPostponeResult(TupConfOptResult tupConfOptResult)
    {
        TUPLogUtil.i(TAG, "onConfPostponeResult----------------------result:" + tupConfOptResult.getOptResult());
        vcConfCtrlNotify.onConfPostponeResult(tupConfOptResult);

    }

    @Override
    public void onMuteConfResult(TupConfOptResult tupConfOptResult, boolean b)
    {
        Log.e(TAG, "onMuteConfResult----------------------result:" + tupConfOptResult.getOptResult() + "," + b);
    }

    @Override
    public void onMuteAttendeeResult(TupConfAttendeeOptResult tupConfAttendeeOptResult, boolean b)
    {
        TUPLogUtil.i(TAG, "onMuteAttendeeResult---------------------result:" + tupConfAttendeeOptResult.getOptResult() + "," + b);
        vcConfCtrlNotify.onMuteAttendeeResult(tupConfAttendeeOptResult, b);
    }

    @Override
    public void onHandupResult(TupConfOptResult tupConfOptResult, boolean b)
    {
        Log.e(TAG, "onHandupResult----------------------");
    }

    @Override
    public void onHanddownAttendeeResult(TupConfAttendeeOptResult tupConfAttendeeOptResult)
    {
        Log.e(TAG, "onHanddownAttendeeResult----------------------");
    }

    @Override
    public void onTransToConfResult(TupConfOptResult tupConfOptResult, int i)
    {
        Log.e(TAG, "onTransToConfResult---------------------result:" + tupConfOptResult.getOptResult());
    }

    @Override
    public void onSetConfModeResult(TupConfOptResult tupConfOptResult)
    {
        Log.e(TAG, "onSetConfModeResult---------------------result:" + tupConfOptResult.getOptResult());
    }

    @Override
    public void onSubscribeConfResult(TupConfOptResult tupConfOptResult)
    {

    }

    @Override
    public void onLockConfResult(TupConfOptResult tupConfOptResult, boolean b)
    {

    }

    @Override
    public void onGetConfListResult(List<TupConfInfo> list, TupConfOptResult tupConfOptResult)
    {

    }

    @Override
    public void onGetConfInfoResult(TupConfInfo tupConfInfo, List<TupConfECAttendeeInfo> list, TupConfOptResult tupConfOptResult)
    {

    }

    @Override
    public void onUpgradeConfResult(TupConfOptResult tupConfOptResult)
    {
        Log.e(TAG, "onUpgradeConfResult---------------------result:" + tupConfOptResult.getOptResult());
    }

    @Override
    public void onGetVmrListResult(List<TupConfVMRInfo> list, TupConfOptResult tupConfOptResult)
    {

    }

    @Override
    public void onQuietAttendeeResult(TupConfAttendeeOptResult tupConfAttendeeOptResult)
    {

    }

    @Override
    public void onAuxtokenOwnerInd(TupConference tupConference, TupConfBaseAttendeeInfo tupConfBaseAttendeeInfo)
    {

    }

    @Override
    public void onAuxsendCmd(TupConference tupConference, boolean b)
    {

    }

    @Override
    public void onConfstateRecoedInd(TupConference tupConference)
    {

    }

    @Override
    public void onConfstateLockInd(TupConference tupConference)
    {

    }

    @Override
    public void onConfHallListInd(TupConference tupConference)
    {

    }

    @Override
    public void onGetDataConfParamsResult(TupConfOptResult tupConfOptResult, TupConfctrlDataconfParams tupConfctrlDataconfParams)
    {
        TUPLogUtil.i(TAG, "onGetDataConfParamsResult---------------------result:" + tupConfOptResult.getOptResult());

        String hostKey = tupConfctrlDataconfParams.getHostKey();
        String serverIp = tupConfctrlDataconfParams.getServerIp();
        String confId = tupConfctrlDataconfParams.getConfId();
        String userId = tupConfctrlDataconfParams.getUserId();
        String confName = tupConfctrlDataconfParams.getConfName();
        String userUri = tupConfctrlDataconfParams.getUserUri();
        String accessCode = tupConfctrlDataconfParams.getAccessCode();
        int userRole = tupConfctrlDataconfParams.getUserRole();
        String siteUrl = tupConfctrlDataconfParams.getSiteUrl();
        String siteId = tupConfctrlDataconfParams.getSiteId();
        String cryptKey = tupConfctrlDataconfParams.getCryptKey();
        String sbcServerAddress = tupConfctrlDataconfParams.getSbcServerAddress();
        String participantId = tupConfctrlDataconfParams.getParticipantId();
        String userName = tupConfctrlDataconfParams.getUserName();
        int m = tupConfctrlDataconfParams.getM();
        int t = tupConfctrlDataconfParams.getT();
        TUPLogUtil.i(TAG, "onGetDataConfParamsResult-------------serverIp:" + serverIp
                + ",userId=" + userId + ",userName=" + userName);


        ConfInfo confInfo = new ConfInfo();
        confInfo.setHostKey(hostKey);
        confInfo.setConfTitle(confName);
        confInfo.setUserId(Integer.parseInt(userId));
        confInfo.setSiteId(siteId);
        confInfo.setUserType(8);
        confInfo.setSiteUrl(siteUrl);

        if (LoginService.getInstance().getVcType() == 0)
        {
            try
            {
                serverIp = serverIp.substring(0, serverIp.indexOf(';'));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            TUPLogUtil.i(TAG, "onGetDataConfParamsResult-------------new serverIp:" + serverIp);
            confInfo.setSvrIp(serverIp);
        }
        else if (LoginService.getInstance().getVcType() == 1)
        {
            confInfo.setSvrIp(sbcServerAddress);
            confInfo.setSvrinterIp(serverIp);
        }


        confInfo.setConfKey(cryptKey);
        confInfo.setConfId(Integer.parseInt(confId));
        if (userName == null || "".equals(userName))
        {
            confInfo.setUserName(LoginParams.getInstance().getSipImpi());
        }
        else
        {
            confInfo.setUserName(userName);
        }
        confInfo.setUserUri(userUri);
        vcDataConfNotify.onGetDataConfParamsResult(confInfo);
    }


    @Override
    public void onEnterPasswordToBeChairman(TupConference tupConference)
    {

    }

    @Override
    public void onRequestConfRightResult(TupConfOptResult tupConfOptResult, TupConference tupConference)
    {

    }

    @Override
    public void onHoldConfResult(TupConfOptResult tupConfOptResult, boolean b)
    {

    }

    @Override
    public void onAddDataConfInd(TupConfOptResult tupConfOptResult, TupConfInfo tupConfInfo)
    {
        TUPLogUtil.i(TAG, "onAddDataConfInd---------------------result:" + tupConfOptResult.getOptResult());
    }
}
