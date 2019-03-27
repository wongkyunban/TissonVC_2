package com.wong.tissonvc_2.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.wong.tissonvc_2.R;
import com.wong.tissonvc_2.service.conf.ConferenceService;
import com.wong.tissonvc_2.service.conf.DataConfService;
import com.wong.tissonvc_2.service.login.data.LoginParams;
import com.wong.tissonvc_2.service.notify.VCConfCtrlNotify;
import com.huawei.tup.confctrl.sdk.TupConfBaseAttendeeInfo;
import com.huawei.tup.confctrl.sdk.TupConfOptResult;
import com.huawei.tup.confctrl.sdk.TupConference;

public class ConfCtrlActivity extends Activity implements VCConfCtrlNotify
{
    private static final int REQUEST_CHAIRMAN_MSG = 1;
    private static final int RELEASE_CHAIRMAN_MSG = 2;
    private static final int POSTPONE_CONF_MSG = 3;
    private static final int HANGUP_ATTENDEE_MSG = 4;
    private static final int RECALL_ATTENDEE_MSG = 5;
    private static final int END_CONF_MSG = 6;
    private static final int ADD_ATTENDEE_MSG = 7;
    private static final int DELETE_ATTENDEE_MSG = 8;
    private static final int MUTE_ATTENDEE_MSG = 9;
    private static final int BROADCAST_ATTENDEE_MSG = 10;
    private static final int CANCEL_BROADCAST_ATTENDEE_MSG = 11;
    private static final int WATCH_ATTENDEE_MSG = 12;


    private Button applyChairmanBtn;
    private Button releaseChairmanBtn;
    private EditText attendNumET;
    private EditText chairmanPwdEt;
    private Button addAttendBtn;
    private Button deleteAttendBtn;
    private Button recallAttendBtn;
    private Button muteAttendBtn;
    private Button watchAttendBtn;
    private Button hangupAttendBtn;
    private Button endConfBtn;
    private Button delayConfBtn;

    private Button broadcastConfBtn;
    private Button unbroadcastConfBtn;

    private Button setPresentBtn;

    private Button confDetailBtn;

    private int i = 0;

    private LoginParams loginParams = LoginParams.getInstance();

    private ConferenceService service = ConferenceService.getInstance();

    private Handler handler = new MyHandler();


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conf_ctrl);
        service.registerVCConfCtrlNotify(this);
        initView();
    }

    private void initView()
    {
        attendNumET = (EditText) findViewById(R.id.attendNum);
        attendNumET.setText("01051221");
        chairmanPwdEt = (EditText) findViewById(R.id.chairmanPwd);


        applyChairmanBtn = (Button) findViewById(R.id.applyChairman);
        applyChairmanBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String pwd = chairmanPwdEt.getText().toString().trim();
                service.requestChairman(pwd, loginParams.getSipImpi());
            }
        });


        releaseChairmanBtn = (Button) findViewById(R.id.releaseChairman);
        releaseChairmanBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                service.releaseChairman(loginParams.getSipImpi());
            }
        });

        addAttendBtn = (Button) findViewById(R.id.addAttendBtn);
        addAttendBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                service.addVCAttendee(attendNumET.getText().toString().trim());
            }
        });

        deleteAttendBtn = (Button) findViewById(R.id.delAttend);
        deleteAttendBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                service.removeAttendee(attendNumET.getText().toString().trim());
            }
        });

        recallAttendBtn = (Button) findViewById(R.id.recallAttended);
        recallAttendBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                service.recallAttendee(attendNumET.getText().toString().trim());
            }
        });

        muteAttendBtn = (Button) findViewById(R.id.muteAttended);
        muteAttendBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (++i % 2 == 1)
                {
                    service.muteAttendee(attendNumET.getText().toString().trim(), true);
                }
                else
                {
                    service.muteAttendee(attendNumET.getText().toString().trim(), false);
                }

            }
        });

        watchAttendBtn = (Button) findViewById(R.id.watchAttended);
        watchAttendBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                service.watchAttendee(attendNumET.getText().toString().trim());
            }
        });

        hangupAttendBtn = (Button) findViewById(R.id.hangUpAttended);
        hangupAttendBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                service.hangUpAttendee(attendNumET.getText().toString().trim());
            }
        });


        endConfBtn = (Button) findViewById(R.id.endConf);
        endConfBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int ret = service.endConf();
                if (ret == 0)
                {
                    finish();
                }
            }
        });

        delayConfBtn = (Button) findViewById(R.id.delayConf);
        delayConfBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                service.postponeConf(15); //minute
            }
        });

        broadcastConfBtn = (Button) findViewById(R.id.broadcastConf);
        broadcastConfBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                service.broadcastAttendee(attendNumET.getText().toString().trim(), true);
            }
        });

        unbroadcastConfBtn = (Button) findViewById(R.id.unbroadcastConf);
        unbroadcastConfBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                service.broadcastAttendee(attendNumET.getText().toString().trim(), false);
            }
        });

        setPresentBtn = (Button) findViewById(R.id.setPresent);
        if (!DataConfService.getInstance().isDataConf())
        {
            setPresentBtn.setVisibility(View.GONE);
        }
        setPresentBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                DataConfService.getInstance().setRole(
                        attendNumET.getText().toString().trim(), 2);
            }
        });

        confDetailBtn = (Button) findViewById(R.id.confDetail);

        confDetailBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent confIntent = new Intent(ConfCtrlActivity.this, ConfDetailActivity.class);
                startActivity(confIntent);
            }
        });


    }


    class MyHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case REQUEST_CHAIRMAN_MSG:
                    if (msg.arg1 == 0)
                    {
                        showToast("request chairman success!");
                    }
                    else
                    {
                        showToast("request chairman fail,errorCode:" + msg.arg1);
                    }
                    break;

                case RELEASE_CHAIRMAN_MSG:
                    if (msg.arg1 == 0)
                    {
                        showToast("release chairman success!");
                    }
                    else
                    {
                        showToast("release chairman fail,errorCode:" + msg.arg1);
                    }
                    break;


                case POSTPONE_CONF_MSG:
                    if (msg.arg1 == 0)
                    {
                        showToast("postpone conf success!");
                    }
                    else
                    {
                        showToast("postpone conf fail,errorCode:" + msg.arg1);
                    }
                    break;

                case HANGUP_ATTENDEE_MSG:
                    if (msg.arg1 == 0)
                    {
                        showToast("hangup attendee success!");
                    }
                    else
                    {
                        showToast("hangup attendee fail,errorCode:" + msg.arg1);
                    }
                    break;
                case RECALL_ATTENDEE_MSG:
                    if (msg.arg1 == 0)
                    {
                        showToast("recall attendee success!");
                    }
                    else
                    {
                        showToast("recall attendee fail,errorCode:" + msg.arg1);
                    }
                    break;

                case ADD_ATTENDEE_MSG:
                    if (msg.arg1 == 0)
                    {
                        showToast("add attendee success!");
                    }
                    else
                    {
                        showToast("add attendee fail,errorCode:" + msg.arg1);
                    }
                    break;
                case DELETE_ATTENDEE_MSG:
                    if (msg.arg1 == 0)
                    {
                        showToast("delete attendee success!");
                    }
                    else
                    {
                        showToast("delete attendee fail,errorCode:" + msg.arg1);
                    }
                    break;


                case END_CONF_MSG:
                    if (msg.arg1 == 0)
                    {
                        showToast("end conf success!");
                        finish();
                    }
                    else
                    {
                        showToast("end conf fail,errorCode:" + msg.arg1);
                    }
                    break;

                case MUTE_ATTENDEE_MSG:
                    if (msg.arg1 == 0)
                    {
                        showToast("mute attendee success!");
                    }
                    else
                    {
                        showToast("mute attendee fail,errorCode:" + msg.arg1);
                    }
                    break;

                case BROADCAST_ATTENDEE_MSG:
                    if (msg.arg1 == 0)
                    {
                        showToast("broadcast attendee success!");
                    }
                    else
                    {
                        showToast("broadcast attendee fail,errorCode:" + msg.arg1);
                    }
                    break;
                case CANCEL_BROADCAST_ATTENDEE_MSG:
                    if (msg.arg1 == 0)
                    {
                        showToast("cancel broadcast attendee success!");
                    }
                    else
                    {
                        showToast("cancel broadcast attendee fail,errorCode:" + msg.arg1);
                    }
                    break;
                case WATCH_ATTENDEE_MSG:
                    if (msg.arg1 == 0)
                    {
                        showToast("watch attendee success!");
                    }
                    else
                    {
                        showToast("watch attendee fail,errorCode:" + msg.arg1);
                    }
                    break;


                default:
                    break;
            }
        }
    }

    private void sendMessage(int what, int ret)
    {
        Message message = new Message();
        message.what = what;
        message.arg1 = ret;
        handler.sendMessage(message);
    }

    private void showToast(String message)
    {
        Toast.makeText(ConfCtrlActivity.this, message, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onRequestChairmanResult(TupConfOptResult result)
    {
        sendMessage(REQUEST_CHAIRMAN_MSG, result.getOptResult());
    }

    @Override
    public void onReleaseChairmanResult(TupConfOptResult result)
    {
        sendMessage(RELEASE_CHAIRMAN_MSG, result.getOptResult());
    }

    @Override
    public void onConfPostponeResult(TupConfOptResult result)
    {
        sendMessage(POSTPONE_CONF_MSG, result.getOptResult());
    }


    @Override
    public void onMuteAttendeeResult(TupConfOptResult result, boolean b)
    {
        sendMessage(MUTE_ATTENDEE_MSG, result.getOptResult());
    }

    @Override
    public void onEndConfResult(TupConfOptResult result)
    {
        sendMessage(END_CONF_MSG, result.getOptResult());
    }

    @Override
    public void onHangupAttendeeResult(TupConfOptResult result)
    {
        sendMessage(HANGUP_ATTENDEE_MSG, result.getOptResult());
    }

    @Override
    public void onAddAttendeeResult(TupConfOptResult result)
    {
        sendMessage(ADD_ATTENDEE_MSG, result.getOptResult());
    }

    @Override
    public void onDelAttendeeResult(TupConfOptResult result)
    {
        sendMessage(DELETE_ATTENDEE_MSG, result.getOptResult());
    }

    @Override
    public void onWatchAttendeeResult(TupConfOptResult result)
    {
        sendMessage(WATCH_ATTENDEE_MSG, result.getOptResult());
    }

    @Override
    public void onCallAttendeeResult(TupConfOptResult result)
    {
        sendMessage(RECALL_ATTENDEE_MSG, result.getOptResult());
    }

    @Override
    public void onBroadcastAttendeeInd(TupConference tupConference, TupConfBaseAttendeeInfo tupConfBaseAttendeeInfo)
    {

    }

    @Override
    public void onCancelBroadcastAttendeeInd(TupConference tupConference, TupConfBaseAttendeeInfo tupConfBaseAttendeeInfo)
    {

    }

}
