package com.wong.tissonvc_2.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wong.tissonvc_2.R;
import com.wong.tissonvc_2.service.conf.ConferenceService;
import com.wong.tissonvc_2.service.login.LoginService;
import com.wong.tissonvc_2.service.login.data.LoginParams;
import com.wong.tissonvc_2.service.notify.VCConfNotify;
import com.wong.tissonvc_2.service.utils.TUPLogUtil;
import com.wong.tissonvc_2.service.utils.Tools;
import com.huawei.tup.confctrl.CCAddAttendeeInfo;
import com.huawei.tup.confctrl.CCIPAddr;
import com.huawei.tup.confctrl.ConfctrlAttendeeMediax;
import com.huawei.tup.confctrl.ConfctrlAttendeeType;
import com.huawei.tup.confctrl.ConfctrlConfEnvType;
import com.huawei.tup.confctrl.ConfctrlConfRole;
import com.huawei.tup.confctrl.ConfctrlConfType;
import com.huawei.tup.confctrl.ConfctrlConfWarningTone;
import com.huawei.tup.confctrl.ConfctrlEncryptMode;
import com.huawei.tup.confctrl.ConfctrlIPVersion;
import com.huawei.tup.confctrl.ConfctrlLanguage;
import com.huawei.tup.confctrl.ConfctrlReminderType;
import com.huawei.tup.confctrl.ConfctrlSiteCallTerminalType;
import com.huawei.tup.confctrl.ConfctrlTimezone;
import com.huawei.tup.confctrl.ConfctrlUserType;
import com.huawei.tup.confctrl.ConfctrlVideoProtocol;
import com.huawei.tup.confctrl.sdk.TupConfBookVcHostedConfInfo;
import com.huawei.tup.confctrl.sdk.TupConfBookVcOnPremiseConfInfo;
import com.huawei.tup.login.LoginAuthorizeResult;

import java.util.ArrayList;
import java.util.List;


public class CreateConfActivity extends Activity implements VCConfNotify
{
    private TextView tv;
    private LoginParams loginParams = LoginParams.getInstance();
    private int vctype = LoginService.getInstance().getVcType();
    private Button createConfBtn;
    private Button addPatBtn;
    private Button delPatBtn;
    private Button searchPatBtn;
    private Button addDataConfBtn;
    private EditText participantNumEt;
    private EditText confNameEt;
    private EditText confPwdEt;
    private TextView patTv;
    private LoginAuthorizeResult hostedLoginResult = LoginService.getInstance().getHostedLoginResult();

    private List<CCAddAttendeeInfo> lists = new ArrayList<>();
    private List<String> nums = new ArrayList<>();

    private List<ConfctrlAttendeeMediax> attendees = new ArrayList<>();

    private int hostedMediaType = 4;

    private int dataValue = 0;

    private ConferenceService conferenceService;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createconf);

        conferenceService = ConferenceService.getInstance();

        if (vctype == 0)
        {
            nums.add(loginParams.getSipImpi());
            lists.add(numToAttendee(loginParams.getSipImpi()));
        }
        else if (vctype == 1)
        {
            String userNum = hostedLoginResult.getSipInfo().getAuthInfo().getUserName();
            nums.add(userNum);
            attendees.add(numToHostedAttendee(userNum));
        }


        tv = (TextView) findViewById(R.id.confTV);
        patTv = (TextView) findViewById(R.id.patsTV);
        participantNumEt = (EditText) findViewById(R.id.patNumEt);
        participantNumEt.setText("+865121102");
        confNameEt = (EditText) findViewById(R.id.confName);
        confPwdEt = (EditText) findViewById(R.id.confPwd);
        createConfBtn = (Button) findViewById(R.id.createConfBtn);
        addPatBtn = (Button) findViewById(R.id.addPatBtn);
        delPatBtn = (Button) findViewById(R.id.delPatBtn);
        searchPatBtn = (Button) findViewById(R.id.searchPatBtn);
        addDataConfBtn = (Button) findViewById(R.id.addDataConfBtn);
        addDataConfBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String dataString = addDataConfBtn.getText().toString().toString();
                if ("addData".equals(dataString))
                {
                    dataValue = 2;
                    hostedMediaType = 4 | 16;
                    Toast.makeText(CreateConfActivity.this, "choose data conf", Toast.LENGTH_SHORT).show();
                    addDataConfBtn.setText("delData");
                }
                else if ("delData".equals(dataString))
                {
                    dataValue = 0;
                    hostedMediaType = 4;
                    Toast.makeText(CreateConfActivity.this, "cancel data conf", Toast.LENGTH_SHORT).show();
                    addDataConfBtn.setText("addData");
                }
            }
        });

        conferenceService.registerVCConfNotify(this);


        if (vctype == 0)
        {
            conferenceService.setConfType(ConfctrlConfEnvType.CONFCTRL_E_CONF_ENV_ON_PREMISE_VC);
            conferenceService.setConfServer(loginParams.getRegisterServerIp(), Integer.parseInt(loginParams.getServerPort()));
            conferenceService.setAuthAccountInfo(loginParams.getSipImpi(), loginParams.getVoipPassword());
        }
        else if (vctype == 1)
        {
            conferenceService.setConfType(ConfctrlConfEnvType.CONFCTRL_E_CONF_ENV_HOSTED_VC); //HOSTED
            String ip = hostedLoginResult.getAuthSerinfo().getServerUri();
            int port = hostedLoginResult.getAuthSerinfo().getServerPort();
            conferenceService.setConfServer(ip, port);
            conferenceService.setAuthToken(hostedLoginResult.getAuthToken());
        }


        addPatBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String num = participantNumEt.getText().toString().trim();
                if ("".equals(num) || num.length() == 0)
                {
                    return;
                }
                if (!nums.contains(num))
                {
                    nums.add(num);
                    if (vctype == 0)
                    {
                        lists.add(numToAttendee(num));
                    }
                    else if (vctype == 1)
                    {
                        attendees.add(numToHostedAttendee(num));
                    }

                }

            }
        });


        delPatBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String num = participantNumEt.getText().toString().trim();
                if ("".equals(num) || num.length() == 0)
                {
                    return;
                }
                if (nums.contains(num) && !(loginParams.getSipImpi().equals(num)))
                {
                    nums.remove(num);

                    if (vctype == 0)
                    {
                        lists.remove(numToAttendee(num));
                    }
                    else if (vctype == 1)
                    {
                        attendees.remove(numToHostedAttendee(num));
                    }
                }
            }
        });


        searchPatBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                StringBuilder sb = new StringBuilder();
                for (String num : nums)
                {
                    sb.append(num);
                    sb.append(" ");
                }

                patTv.setText("participants：" + sb);

            }
        });


        createConfBtn.setOnClickListener(new View.OnClickListener()

                                         {
                                             @Override
                                             public void onClick(View v)
                                             {

                                                 String confName = confNameEt.getText().toString().trim();
                                                 String confPwd = confPwdEt.getText().toString().trim();


                                                 if ("".equals(confName) || confName.length() == 0)
                                                 {
                                                     Toast.makeText(CreateConfActivity.this, "confName is null", Toast.LENGTH_SHORT).show();
                                                     return;
                                                 }
                                                 if (vctype == 0)
                                                 {
                                                     String number = loginParams.getSipImpi();
                                                     TupConfBookVcOnPremiseConfInfo confInfo = new TupConfBookVcOnPremiseConfInfo();
                                                     confInfo.setSitecallType(0);

                                                     confInfo.setServerAddr(new CCIPAddr(loginParams.getRegisterServerIp(), ConfctrlIPVersion.CC_IP_V4));
                                                     confInfo.setLocalAddr(new CCIPAddr(Tools.getLocalIp(), ConfctrlIPVersion.CC_IP_V4));
                                                     CCAddAttendeeInfo attendeeInfo = new CCAddAttendeeInfo();
                                                     CCIPAddr ccipAddr = new CCIPAddr();
                                                     ccipAddr.setIp(Tools.getLocalIp());
                                                     ccipAddr.setIpVer(ConfctrlIPVersion.CC_IP_V4);
                                                     attendeeInfo.setTerminalIpAddr(ccipAddr);
                                                     attendeeInfo.setTerminalType(ConfctrlSiteCallTerminalType.CC_sip);
                                                     attendeeInfo.setSiteBandwidth(1920);
                                                     attendeeInfo.setNumber(number);
                                                     attendeeInfo.setNumberLen(number.length());
                                                     attendeeInfo.setTerminalId(number);
                                                     attendeeInfo.setTerminalIdLength(number.length());
                                                     attendeeInfo.setUri(number + "@" + loginParams.getRegisterServerIp());
                                                     confInfo.setCcAddterminalInfo(lists);

                                                     confInfo.setSitenumber(lists.size());
                                                     confInfo.setPwdLen(6); //max length is 6
                                                     confInfo.setConfName(confName);
                                                     confInfo.setConfNameLen(confName.length());
                                                     confInfo.setPucPwd(confPwd);
                                                     confInfo.setSitecallMode(0); //CC_SITE_CALL_MODE_REPORT
                                                     confInfo.setHasDataConf(dataValue);
                                                     confInfo.setVideoProto(ConfctrlVideoProtocol.CC_VIDEO_PROTO_BUTT);
                                                     //confInfo.setTerminalDataRate(640);
                                                     confInfo.setTerminalDataRate(19200);
                                                     int ret = conferenceService.bookOnPremiseReservedConf(confInfo);
                                                     TUPLogUtil.i("ReservedConf", "result=" + ret);
                                                 }
                                                 else if (vctype == 1)
                                                 {
                                                     TupConfBookVcHostedConfInfo hostedConfInfo = new TupConfBookVcHostedConfInfo();
                                                     hostedConfInfo.setConfType(ConfctrlConfType.CONFCTRL_E_CONF_TYPE_NORMAL);
                                                     hostedConfInfo.setSubject(confName);
                                                     hostedConfInfo.setMediaType(hostedMediaType);
                                                     hostedConfInfo.setAllowInvite(1);
                                                     hostedConfInfo.setAutoInvite(1);
                                                     hostedConfInfo.setAllowVideoControl(1);
                                                     hostedConfInfo.setTimezone(ConfctrlTimezone.CONFCTRL_E_TIMEZONE_BEIJING);
                                                     hostedConfInfo.setConfLen(2 * 60 * 60);
                                                     hostedConfInfo.setWelcomeVoiceEnable(ConfctrlConfWarningTone.CONFCTRL_E_CONF_WARNING_TONE_DEFAULT);
                                                     hostedConfInfo.setEnterPrompt(ConfctrlConfWarningTone.CONFCTRL_E_CONF_WARNING_TONE_DEFAULT);
                                                     hostedConfInfo.setLeavePrompt(ConfctrlConfWarningTone.CONFCTRL_E_CONF_WARNING_TONE_DEFAULT);
                                                     hostedConfInfo.setConfFilter(1);
                                                     hostedConfInfo.setRecordFlag(0);
                                                     hostedConfInfo.setAutoProlong(0);
                                                     hostedConfInfo.setMultiStreamFlag(0);
                                                     hostedConfInfo.setReminder(ConfctrlReminderType.CONFCTRL_E_REMINDER_TYPE_NONE);
                                                     hostedConfInfo.setLanguage(ConfctrlLanguage.CONFCTRL_E_LANGUAGE_EN_US);
                                                     hostedConfInfo.setConfEncryptMode(ConfctrlEncryptMode.CONFCTRL_E_ENCRYPT_MODE_NONE);
                                                     hostedConfInfo.setUserType(ConfctrlUserType.CONFCTRL_E_USER_TYPE_MOBILE);
                                                     hostedConfInfo.setAttendee(attendees);
                                                     hostedConfInfo.setNumOfAttendee(attendees.size());
                                                     hostedConfInfo.setSize(attendees.size());
                                                     int ret = conferenceService.bookReservedConf(hostedConfInfo);
                                                     Log.e("CreateConfActivity", "--------hostedConfInfo:" + ret);
                                                 }
                                             }
                                         }

        );


    }


    private CCAddAttendeeInfo numToAttendee(String num)
    {
        CCAddAttendeeInfo attendeeInfo = new CCAddAttendeeInfo();
        attendeeInfo.setTerminalType(ConfctrlSiteCallTerminalType.CC_sip);
        attendeeInfo.setSiteBandwidth(1920);
        attendeeInfo.setNumber(num);
        attendeeInfo.setNumberLen(num.length());
        attendeeInfo.setTerminalId(num);
        attendeeInfo.setTerminalIdLength(num.length());
        attendeeInfo.setUri(num + "@" + loginParams.getRegisterServerIp());

        //规避SMC下创会崩溃问题
        attendeeInfo.setNationCode("");
        attendeeInfo.setInternationCode("");
        return attendeeInfo;
    }


    private ConfctrlAttendeeMediax numToHostedAttendee(String userNum)
    {
        ConfctrlAttendeeMediax attendee = new ConfctrlAttendeeMediax();
        attendee.setNumber("sip:" + userNum + "@huawei.com");
        attendee.setName(userNum);
        attendee.setRole(ConfctrlConfRole.CONFCTRL_E_CONF_ROLE_ATTENDEE);
        attendee.setType(ConfctrlAttendeeType.CONFCTRL_E_ATTENDEE_TYPE_NORMAL);
        return attendee;
    }


    @Override
    public void onBookReservedConfResult(int result)
    {
        if (result == 0)
        {
            finish();
        }
        else
        {
            Toast.makeText(CreateConfActivity.this, "create conf failed,reason:" + result, Toast.LENGTH_SHORT).show();
        }
    }
}
