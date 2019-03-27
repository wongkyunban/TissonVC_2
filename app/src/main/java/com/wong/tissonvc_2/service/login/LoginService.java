package com.wong.tissonvc_2.service.login;

import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.wong.tissonvc_2.service.TupEventMgr;
import com.wong.tissonvc_2.service.TupNotify;
import com.wong.tissonvc_2.service.call.CallService;
import com.wong.tissonvc_2.service.contacts.ContactService;
import com.wong.tissonvc_2.service.login.data.LoginParams;
import com.wong.tissonvc_2.service.notify.VCChangePasswordNotify;
import com.wong.tissonvc_2.service.utils.TUPLogUtil;
import com.wong.tissonvc_2.ui.application.TUPApplication;
import com.huawei.tup.TUPInterfaceService;
import com.huawei.tup.login.LoginAuthInfo;
import com.huawei.tup.login.LoginAuthServerInfo;
import com.huawei.tup.login.LoginAuthType;
import com.huawei.tup.login.LoginAuthorizeParam;
import com.huawei.tup.login.LoginAuthorizeResult;
import com.huawei.tup.login.LoginChangePwdParam;
import com.huawei.tup.login.LoginEuaInfo;
import com.huawei.tup.login.LoginLogLevel;
import com.huawei.tup.login.LoginServerType;
import com.huawei.tup.login.LoginSingleServerInfo;
import com.huawei.tup.login.LoginSipInfo;
import com.huawei.tup.login.LoginSmcAuthorizeResult;
import com.huawei.tup.login.LoginStgInfo;
import com.huawei.tup.login.LoginUportalAuthorizeResult;
import com.huawei.tup.login.LoginVerifyMode;
import com.huawei.tup.login.sdk.TupLoginErrorID;
import com.huawei.tup.login.sdk.TupLoginManager;
import com.huawei.tup.login.sdk.TupLoginNotifyBase;
import com.huawei.tup.login.sdk.TupLoginOptResult;

import java.io.File;
import java.util.List;

import common.TupBool;
import common.TupCallParam;
import tupsdk.TupCallManager;

/**
 * The type Login service.
 */
public class LoginService extends TupLoginNotifyBase
{
    /**
     * The constant TAG.
     */
    private static final String TAG = LoginService.class.getSimpleName();

    private static final int VC_HOSTED = 1;
    private static final int VC_SMC = 0;

    /**
     * The constant ins.
     */
    private static LoginService ins;
    /**
     * The Tup login manager.
     */
    private TupLoginManager tupLoginManager;

    /**
     * The Tup call manager.
     */
    private TupCallManager tupCallManager;
    /**
     * The Login params.
     */
    private LoginParams loginParams = LoginParams.getInstance();
    /**
     * The Register handler.
     */
    private Handler registerHandler = new Handler();
    /**
     * The Ip address.
     */
    private String ipAddress;

    /**
     * The Change password notify.
     */
    private VCChangePasswordNotify changePasswordNotify;

    /**
     * The Register runnable.
     */
    private Runnable registerRunnable = new Runnable()
    {
        public void run()
        {
            processRegister();
        }
    };


    public int getVcType()
    {
        return vcType;
    }

    public void setVcType(int vcType)
    {
        this.vcType = vcType;
    }

    private int vcType = VC_SMC;


    private LoginAuthorizeResult hostedLoginResult = null;

    public LoginAuthorizeResult getHostedLoginResult()
    {
        return hostedLoginResult;
    }

    /**
     * Gets ip address.
     *
     * @return the ip address
     */
    public String getIpAddress()
    {
        return ipAddress;
    }

    /**
     * Sets ip address.
     *
     * @param ipAddress the ip address
     */
    public void setIpAddress(String ipAddress)
    {
        this.ipAddress = ipAddress;
    }

    /**
     * Instantiates a new Login service.
     */
    private LoginService()
    {

    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public synchronized static LoginService getInstance()
    {
        if (ins == null)
        {
            ins = new LoginService();
        }
        return ins;
    }

    /**
     * Register vc change password notify.
     *
     * @param notify the notify
     */
    public void registerVCChangePasswordNotify(VCChangePasswordNotify notify)
    {
        changePasswordNotify = notify;
    }

    /**
     * Init int.
     *
     * @param tupService the tup service
     * @return the int
     */
    public int init(TUPInterfaceService tupService)
    {
        // init tupLoginManager
        tupLoginManager = TupLoginManager.getIns(this, TUPApplication.getApplication());
        // init uportal login log
        String path = Environment.getExternalStorageDirectory() + File.separator + "VCLOG";
        int fileCount = 1;
        int maxLogSize = 5120;
        tupLoginManager.setLogParam(LoginLogLevel.LOGIN_E_LOG_INFO, maxLogSize, fileCount, path);
        // init login
        tupLoginManager.setCertPath("");
        tupLoginManager.setVerifyMode(LoginVerifyMode.LOGIN_E_VERIFY_MODE_NONE);
        return tupLoginManager.loginInit(tupService);
    }

    /**
     * Change register password int.
     *
     * @param var1 the var 1
     * @return the int
     */
    public int changeRegisterPassword(LoginChangePwdParam var1)
    {
        return tupLoginManager.changeRegisterPassword(var1);
    }


    /**
     * Un init int.
     *
     * @return the int
     */
    public int unInit()
    {
        return tupLoginManager.loginUninit();
    }

    /**
     * Register tup notify.
     *
     * @param tupNotify the tup notify
     */
    public void registerTupNotify(TupNotify tupNotify)
    {
        TupEventMgr.registerTupNotify(tupNotify);
    }

    /**
     * Unregister tup notify.
     *
     * @param tupNotify the tup notify
     */
    public void unregisterTupNotify(TupNotify tupNotify)
    {
        TupEventMgr.unregisterTupNotify(tupNotify);
    }


    /**
     * Uportal login boolean.
     *
     * @param userName   the user name
     * @param password   the password
     * @param serverUrl  the server url
     * @param serverPort the server port
     * @return the boolean
     */
    public boolean authorize(String userName, String password, String serverUrl, int serverPort)
    {
        LoginAuthInfo authInfo = new LoginAuthInfo();
        authInfo.setUserName(userName);
        authInfo.setPassword(password);
        LoginAuthServerInfo serverInfo = new LoginAuthServerInfo();
        serverInfo.setServerType(LoginServerType.LOGIN_E_SERVER_TYPE_SMC);
        serverInfo.setServerUrl(serverUrl);
        serverInfo.setServerPort(serverPort);
        serverInfo.setServerVersion(""); //必须
        serverInfo.setProxyUrl(serverUrl);
        serverInfo.setProxyPort(serverPort);

        LoginAuthorizeParam authorizeParam = new LoginAuthorizeParam();
        authorizeParam.setAuthInfo(authInfo);
        authorizeParam.setAuthServer(serverInfo);
        authorizeParam.setUserAgent("TUP VC");
        authorizeParam.setUserTiket(""); //必须
        vcType = VC_SMC;
        int vcLoginResult = tupLoginManager.authorize(authorizeParam);
        TUPLogUtil.i(TAG, "vcLoginResult->" + vcLoginResult);
        return vcLoginResult == 0;
    }


    public boolean authorizeHosted(String userName, String password, String serverUrl, int serverPort)
    {
        LoginAuthInfo authInfo = new LoginAuthInfo();
        authInfo.setUserName(userName);
        authInfo.setPassword(password);
        LoginAuthServerInfo serverInfo = new LoginAuthServerInfo();
        serverInfo.setServerType(LoginServerType.LOGIN_E_SERVER_TYPE_MEDIAX);
        serverInfo.setServerUrl(serverUrl);
        serverInfo.setServerPort(serverPort);
        serverInfo.setServerVersion("V6R6C00"); //必须
        serverInfo.setProxyUrl(serverUrl);
        serverInfo.setProxyPort(serverPort);

        LoginAuthorizeParam authorizeParam = new LoginAuthorizeParam();
        authorizeParam.setAuthInfo(authInfo);
        authorizeParam.setAuthServer(serverInfo);
        authorizeParam.setUserAgent("WEB");
        authorizeParam.setUserTiket(""); //必须
        vcType = VC_HOSTED;
        int vcLoginResult = tupLoginManager.authorize(authorizeParam);
        TUPLogUtil.i(TAG, "-----vcLoginResult->" + vcLoginResult);
        return vcLoginResult == 0;
    }


    /**
     * Login.
     */
    public void login()
    {
        LoginParams.getInstance().initData();
        tupCallManager = CallService.getInstance().getTupCallManager();
        TupLoginEventManager.getTupLoginEventManager();
        register();
    }


    /**
     * Logout.
     */
    public void logout()
    {
        ContactService.getInstance().setLdap(false);
//        ContactService.getInstance().stopLdapContactsServer();
        CallService.getInstance().forceCloseCall();
        unRegister();
        TupEventMgr.clearTupNotify();
        hostedLoginResult = null;
    }

    /**
     * sip register
     */
    public void register()
    {
        TUPLogUtil.i(TAG, "register enter.");
        startRegister();
        CallService.getInstance().addDefaultAudioRoute();
    }

    /**
     * sip unregister
     */
    public void unRegister()
    {
        stopRegister();
        registerHandler.post(new Runnable()
        {
            public void run()
            {
                processUnRegister();
            }
        });
    }

    /**
     * process register
     */
    private void processRegister()
    {
        TUPLogUtil.i(TAG, "processRegister");
        boolean bRet = true;
        tupCallManager.setTelNum(loginParams.getSipNumber());
        tupCallManager.enableCorporate_directory(TupBool.TUP_TRUE); //新加设置
        CallService.getInstance().tupConfig();
        int ret = registerVoip();
        if (ret == TupCallParam.CALL_TUP_RESULT.TUP_SUCCESS)
        {
            bRet = true;
        }
        else
        {
            bRet = false;
        }
        TUPLogUtil.i(TAG, "bRet" + bRet);
        if (!bRet)
        {
            CallService.getInstance().clearAllCallSession();
        }
    }

    /**
     * processUnRegister
     */
    private void processUnRegister()
    {
        TUPLogUtil.i(TAG, "processUnRegister.");

        boolean bRet = true;
        int ret = this.unRegistVoip();
        if (TupCallParam.CALL_TUP_RESULT.TUP_SUCCESS == ret)
        {
            bRet = true;
        }
        else
        {
            bRet = false;
        }

        if (!bRet)
        {
            CallService.getInstance().clearAllCallSession();
        }
    }

    /**
     * start register
     */
    private void startRegister()
    {
        registerHandler.post(this.registerRunnable);
    }

    /**
     * stop register
     */
    private void stopRegister()
    {
        registerHandler.removeCallbacks(this.registerRunnable);
    }

    /**
     * Register voip int.
     *
     * @return the int
     */
    public int registerVoip()
    {
        int registerResult = 0;
        TUPLogUtil.i(TAG, "call sipAccount" + "----" + this.loginParams.getVoipNumber());
        TUPLogUtil.i(TAG, "call sipUri" + "----" + this.loginParams.getSipURI());
        TUPLogUtil.i(TAG, "call sipImpi" + "----" + this.loginParams.getSipImpi());

        registerResult = tupCallManager.callRegister(this.loginParams.getSipImpi(),
                this.loginParams.getSipImpi(), this.loginParams.getVoipPassword());

//        registerResult = tupCallManager.callRegister("+865121001",
//                "+865121001", "UBKvnu77");

        return registerResult;
    }

    /**
     * Un regist voip int.
     *
     * @return the int
     */
    public int unRegistVoip()
    {
        return this.tupCallManager.callDeregister();
    }


    @Override
    public void onAuthorizeResult(int i, TupLoginOptResult tupLoginOptResult, LoginSmcAuthorizeResult loginSmcAuthorizeResult)
    {

        int result;
        String errorMessage;
        if (tupLoginOptResult == null || loginSmcAuthorizeResult == null)
        {
            TUPLogUtil.e(TAG, "LoginSmcAuthorizeResult null");
            TUPLogUtil.e(TAG, "LoginSmcAuthorizeResult result=" + tupLoginOptResult.getOptResult());
        }
        else if (tupLoginOptResult.getOptResult() != TupLoginErrorID.LOGIN_E_ERR_SUCCESS)
        {
            result = tupLoginOptResult.getOptResult();
            TUPLogUtil.e(TAG, "LoginSmcAuthorizeResult fail,result=" + result);
        }
        else
        {
            int stg_num = loginSmcAuthorizeResult.getStgNum();
            String password = loginSmcAuthorizeResult.getPassword();
            List<LoginSingleServerInfo> stg_servers = loginSmcAuthorizeResult.getStgServers();
            TUPLogUtil.i(TAG, "LoginSmcAuthorizeResultsuccess,stg_num=" + stg_num + ",pass=" + password
                    + ",smcnum=" + loginSmcAuthorizeResult.getSmcNum());
            TUPLogUtil.i(TAG, "LoginSmcAuthorizeResult success");

            List<LoginSingleServerInfo> smc_servers = loginSmcAuthorizeResult.getSmcServers();
            for (LoginSingleServerInfo serverInfo : smc_servers)
            {
                TUPLogUtil.i(TAG, "---------------server_uri:" + serverInfo.getServerUri());
            }
            TupEventMgr.onSMCLoginNotify(TupCallParam.CALL_TUP_RESULT.TUP_SUCCESS, "");


//            String name = loginSmcAuthorizeResult.getName(); //stg's account
//            String password = loginSmcAuthorizeResult.getPassword(); //stg's password
//            List<LoginSingleServerInfo> smc_servers = loginSmcAuthorizeResult.getSmcServers();
//            for (LoginSingleServerInfo serverInfo : smc_servers)
//            {
//                TUPLogUtil.i(TAG, "smc servers-----server_uri:" + serverInfo.getServerUri()
//                            +",server_port:"+serverInfo.getServerPort());
//            }
//            List<LoginSingleServerInfo> stg_servers = loginSmcAuthorizeResult.getStgServers();
//            for (LoginSingleServerInfo serverInfo : stg_servers)
//            {
//                TUPLogUtil.i(TAG, "stg servers-----server_uri:" + serverInfo.getServerUri()
//                        +",server_port:"+serverInfo.getServerPort());
//            }
//
//            List<LoginSingleServerInfo> sbc_servers = loginSmcAuthorizeResult.getSbcServers();
//            for (LoginSingleServerInfo serverInfo : sbc_servers)
//            {
//                TUPLogUtil.i(TAG, "sbc servers-----server_uri:" + serverInfo.getServerUri()
//                        +",server_port:"+serverInfo.getServerPort());
//            }
//            List<LoginSingleServerInfo> sbc_out_servers = loginSmcAuthorizeResult.getSbcOutServers();
//            for (LoginSingleServerInfo serverInfo : sbc_out_servers)
//            {
//                TUPLogUtil.i(TAG, "sbc_out servers-----server_uri:" + serverInfo.getServerUri()
//                        +",server_port:"+serverInfo.getServerPort());
//            }


        }
    }


    @Override
    public void onPasswordChangeResult(TupLoginOptResult tupLoginOptResult)
    {
        TUPLogUtil.i(TAG, "--------onPasswordChangeResult:" + tupLoginOptResult.getOptResult());
        changePasswordNotify.onPasswordChangeResult(tupLoginOptResult.getOptResult());
    }


    @Override
    public void onAuthorizeResult(int i, TupLoginOptResult tupLoginOptResult, LoginUportalAuthorizeResult loginUportalAuthorizeResult)
    {
        Log.e(TAG, "-------------loginUportalAuthorizeResult " + tupLoginOptResult.getOptResult());
    }

    @Override
    public void onAuthorizeResult(int i, TupLoginOptResult tupLoginOptResult, LoginAuthorizeResult loginAuthorizeResult)
    {
        int result = tupLoginOptResult.getOptResult();
        Log.e(TAG, "-------------loginAuthorizeResult " + result);
        if (0 == result)
        {
            hostedLoginResult = loginAuthorizeResult;
            String auth_token = loginAuthorizeResult.getAuthToken();
            LoginSipInfo sip_info = loginAuthorizeResult.getSipInfo();
            LoginSingleServerInfo group_info = loginAuthorizeResult.getGroupInfo();
            LoginStgInfo stg_info = loginAuthorizeResult.getStgInfo();
            String media_type = loginAuthorizeResult.getMediaType();
            LoginEuaInfo eua_info = loginAuthorizeResult.getEuaInfo();
            String user_name = loginAuthorizeResult.getUserName();
            LoginSingleServerInfo auth_serinfo = loginAuthorizeResult.getAuthSerinfo();
            LoginSingleServerInfo eab_info = loginAuthorizeResult.getEabInfo();
            Log.e(TAG,"----auth_token:"+auth_token+",media_type:"+media_type+",user_name:"+user_name
                    +",,transport_mode:"+sip_info.getTransportMode()+",sip_url:"+sip_info.getSipUrl()
                    +",user_number:"+sip_info.getAuthInfo().getUserNumber()+",password:"+sip_info.getAuthInfo().getPassword()
                    +",user_name:"+sip_info.getAuthInfo().getUserName());
            String server_uri = auth_serinfo.getServerUri();
            int server_port = auth_serinfo.getServerPort();
            Log.e(TAG,"------server_uri:"+server_uri+",server_port:"+server_port);

            TupEventMgr.onSMCLoginNotify(TupCallParam.CALL_TUP_RESULT.TUP_SUCCESS, "");
        }
    }
}
