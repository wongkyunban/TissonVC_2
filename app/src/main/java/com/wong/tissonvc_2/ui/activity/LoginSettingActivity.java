package com.wong.tissonvc_2.ui.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.wong.tissonvc_2.R;

import static com.wong.tissonvc_2.ui.activity.LoginActivity.FILE_NAME;

public class LoginSettingActivity extends BaseActivity implements View.OnClickListener
{
    private CheckBox mVpnCheckBox;
    private RadioGroup mSrtpGroup;
    private RadioGroup mSipTransportGroup;
    private boolean mIsVpn;
    private int mSrtpMode = 0;
    private int mSipTransport = 0;
    private SharedPreferences mSharedPreferences;

    public static final String TUP_VPN = "tupVpn";
    public static final String TUP_SRTP = "tupSrtp";
    public static final String TUP_SIP_TRANSPORT = "tupSipTransport";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeData();
        initializeComposition();
    }

    private void initView()
    {
        mVpnCheckBox = (CheckBox) findViewById(R.id.check_vpn_connect);
        mSrtpGroup = (RadioGroup)findViewById(R.id.rg_srtp);
        mSipTransportGroup = (RadioGroup)findViewById(R.id.rg_sip_transport);

        Button rightButton = (Button) findViewById(R.id.right_btn);
        Button leftButton = (Button) findViewById(R.id.back);

        rightButton.setOnClickListener(this);
        leftButton.setOnClickListener(this);

        mVpnCheckBox.setChecked(mSharedPreferences.getBoolean(TUP_VPN, false));
        mSrtpGroup.check(getSrtpGroupCheckedId(mSharedPreferences.getInt(TUP_SRTP, 0)));
        mSipTransportGroup.check(getSipTransportGroupCheckedId(mSharedPreferences.getInt(TUP_SIP_TRANSPORT, 0)));
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.right_btn:
                mIsVpn = mVpnCheckBox.isChecked();
                mSrtpMode = getSrtpMode(mSrtpGroup.getCheckedRadioButtonId());
                mSipTransport = getSipTransportMode(mSipTransportGroup.getCheckedRadioButtonId());

                saveLoginSetting(mIsVpn);
                saveSecuritySetting(mSrtpMode, mSipTransport);
                Toast.makeText(this, "save success", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case R.id.check_vpn_connect:
                if (mVpnCheckBox.isChecked())
                {
                    mVpnCheckBox.setChecked(true);
                }
                else
                {
                    mVpnCheckBox.setChecked(false);
                }
                break;
            case R.id.back:
                mVpnCheckBox.setChecked(mSharedPreferences.getBoolean(TUP_VPN, false));
                mSrtpGroup.check(getSrtpGroupCheckedId(mSharedPreferences.getInt(TUP_SRTP, 0)));
                mSipTransportGroup.check(getSipTransportGroupCheckedId(mSharedPreferences.getInt(TUP_SIP_TRANSPORT, 0)));
                finish();
                break;
            default:
                break;
        }
    }

    public void initializeComposition()
    {
        setContentView(R.layout.activity_login_setting);
        initView();
        mVpnCheckBox.setOnClickListener(this);

        mSrtpGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId)
                {
                    case R.id.rb_srtp_mandatory:
                        mSrtpMode = 2;
                        break;
                    case R.id.rb_srtp_optional:
                        mSrtpMode = 1;
                        break;
                    case R.id.rb_srtp_disable:
                        mSrtpMode = 0;
                        break;
                    default:
                        break;
                }
            }
        });

        mSipTransportGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId)
                {
                    case R.id.rb_sip_transport_udp:
                        mSipTransport = 0;
                        break;
                    case R.id.rb_sip_transport_tls:
                        mSipTransport = 1;
                        break;
                    case R.id.rb_sip_transport_tcp:
                        mSipTransport = 2;
                        break;
                    default:
                        break;
                }
            }
        });
    }

    public void initializeData()
    {
        mSharedPreferences = getSharedPreferences(FILE_NAME, Activity.MODE_PRIVATE);
    }

    private void saveLoginSetting(boolean isVpn)
    {
        mSharedPreferences.edit().putBoolean(TUP_VPN, isVpn)
                .commit();
    }

    private void saveSecuritySetting(int srtpMode, int sipTransport)
    {
        mSharedPreferences.edit().putInt(TUP_SRTP, srtpMode)
                .putInt(TUP_SIP_TRANSPORT, sipTransport)
                .commit();
    }

    private int getSrtpGroupCheckedId(int srtpMode) {
        int id = R.id.rb_srtp_disable;
        switch (srtpMode) {
            case 0:
                id = R.id.rb_srtp_disable;
                break;
            case 1:
                id = R.id.rb_srtp_optional;
                break;
            case 2:
                id = R.id.rb_srtp_mandatory;
                break;
            default:
                break;
        }
        return id;
    }

    private int getSipTransportGroupCheckedId(int sipTransport) {
        int id = R.id.rb_sip_transport_udp;
        switch (sipTransport) {
            case 0:
                id = R.id.rb_sip_transport_udp;
                break;
            case 1:
                id = R.id.rb_sip_transport_tls;
                break;
            case 2:
                id = R.id.rb_sip_transport_tcp;
                break;
            default:
                break;
        }
        return id;
    }

    private int getSrtpMode(int checkedId) {
        int srtpMode = 0;
        switch (checkedId)
        {
            case R.id.rb_srtp_mandatory:
                srtpMode = 2;
                break;
            case R.id.rb_srtp_optional:
                srtpMode = 1;
                break;
            case R.id.rb_srtp_disable:
                srtpMode = 0;
                break;
            default:
                break;
        }
        return srtpMode;
    }

    private int getSipTransportMode(int checkedId) {
        int sipTransport = 0;
        switch (checkedId)
        {
            case R.id.rb_sip_transport_udp:
                sipTransport = 0;
                break;
            case R.id.rb_sip_transport_tls:
                sipTransport = 1;
                break;
            case R.id.rb_sip_transport_tcp:
                sipTransport = 2;
                break;
            default:
                break;
        }
        return sipTransport;
    }

    @Override
    public void clearData() {

    }
}
