package com.wong.tissonvc_2.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.wong.tissonvc_2.R;
import com.wong.tissonvc_2.service.login.LoginService;
import com.wong.tissonvc_2.service.login.data.LoginParams;
import com.wong.tissonvc_2.service.notify.VCChangePasswordNotify;
import com.wong.tissonvc_2.service.utils.TUPLogUtil;
import com.huawei.tup.login.LoginChangePwdParam;
import com.huawei.tup.login.LoginProtocolType;
import com.huawei.tup.login.LoginServerType;

public class ChangePasswordActivity extends BaseActivity implements VCChangePasswordNotify
{

    private EditText oldPassEt;
    private EditText newPassEt;
    private EditText confirmPassEt;
    private Button changePassBtn;
    private int result = -1;
    private LoginParams loginParams = LoginParams.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        LoginService.getInstance().registerVCChangePasswordNotify(this);
        oldPassEt = (EditText) findViewById(R.id.oldPass);
        newPassEt = (EditText) findViewById(R.id.newPass);
        confirmPassEt = (EditText) findViewById(R.id.confirmPass);
        changePassBtn = (Button) findViewById(R.id.changePasswordBtn);
        changePassBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String oldPass = oldPassEt.getText().toString().trim();
                String newPass = newPassEt.getText().toString().trim();
                String confirmPass = confirmPassEt.getText().toString().trim();

                if (!oldPass.equals(loginParams.getVoipPassword()))
                {
                    Toast.makeText(ChangePasswordActivity.this, "password incorrect", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!newPass.equals(confirmPass))
                {
                    Toast.makeText(ChangePasswordActivity.this, "two passwords are inconsistent ", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    LoginChangePwdParam lc = new LoginChangePwdParam();
                    lc.setAccount(loginParams.getSipImpi());
                    lc.setNumber(loginParams.getSipImpi());
                    lc.setProtocol(LoginProtocolType.LOGIN_D_PROTOCOL_TYPE_SIP);
                    lc.setNewPassword(newPass);
                    lc.setServer(loginParams.getRegisterServerIp());
                    lc.setOldPassword(oldPass);
                    lc.setPort(443);
                    lc.setServerType(LoginServerType.LOGIN_E_SERVER_TYPE_SMC);
                    result = LoginService.getInstance().changeRegisterPassword(lc);
                    TUPLogUtil.i("ChangePasswordActivity", "-----account:" + lc.getAccount() + ",server:" + lc.getServer());
                    if (result != 0)
                    {
                        Toast.makeText(ChangePasswordActivity.this, "change password fail，reasonCode=" + result, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public void clearData()
    {

    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent();
        intent.putExtra("result", -1);
        ChangePasswordActivity.this.setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    @Override
    public void onPasswordChangeResult(int ret)
    {
        TUPLogUtil.e("ChangePass","onPasswordChangeResult:"+result);
        result = ret;
        Intent intent = new Intent();
        intent.putExtra("result", result);
        ChangePasswordActivity.this.setResult(RESULT_OK, intent);
        ChangePasswordActivity.this.finish();
    }
}
