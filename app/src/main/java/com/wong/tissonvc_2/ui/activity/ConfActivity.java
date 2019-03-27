package com.wong.tissonvc_2.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.wong.tissonvc_2.R;
import com.wong.tissonvc_2.service.conf.ConferenceService;
import com.huawei.tup.confctrl.CCAddAttendeeInfo;
import com.huawei.tup.confctrl.CCIPAddr;
import com.huawei.tup.confctrl.ConfctrlConfEnvType;
import com.huawei.tup.confctrl.ConfctrlIPVersion;
import com.huawei.tup.confctrl.sdk.TupConfBookVcOnPremiseConfInfo;

import java.util.ArrayList;
import java.util.List;

public class ConfActivity extends Activity
{
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conf);

        tv = (TextView) findViewById(R.id.confTV);

        ConferenceService.getInstance().setConfType(ConfctrlConfEnvType.CONFCTRL_E_CONF_ENV_ON_PREMISE_VC);
//        ConferenceService.getInstance().setConfType(ConfctrlConfEnvType.CONFCTRL_E_CONF_ENV_HOSTED_VC);
        ConferenceService.getInstance().setConfServer("172.22.11.228", 5060);
        ConferenceService.getInstance().setAuthAccountInfo("01051211", "Huawei@123");

        TupConfBookVcOnPremiseConfInfo confInfo = new TupConfBookVcOnPremiseConfInfo();
        confInfo.setSitenumber(1); //会场数目

        CCAddAttendeeInfo attendeeInfo = new CCAddAttendeeInfo();
        CCIPAddr ccipAddr = new CCIPAddr();
        ccipAddr.setIp("172.22.11.228");
        ccipAddr.setIpVer(ConfctrlIPVersion.CC_IP_V4);
        attendeeInfo.setTerminalIpAddr(ccipAddr);
        attendeeInfo.setNumber("10086");
        List<CCAddAttendeeInfo> lists = new ArrayList<>();
        lists.add(attendeeInfo);
        confInfo.setCcAddterminalInfo(lists);

        confInfo.setSitecallType(0); //主叫呼集类型
        //会场信息结构指针
//        confInfo.setTerminalDataRate(5120); //带宽值QB_V2
        confInfo.setPwdLen(6); //会议密码长度
        confInfo.setConfName("WAHAHA"); //会议名
        confInfo.setPucPwd("123456"); //会议密码指针
        confInfo.setSitecallMode(1); //CC_SITE_CALL_MODE_REPORT
        confInfo.setHasDataConf(0);
        confInfo.setRoleLabel(1);
        int ret = ConferenceService.getInstance().bookOnPremiseReservedConf(confInfo);
        tv.setText("bookOnPremiseReservedConf result:" + ret);


    }
}
