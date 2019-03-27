package com.wong.tissonvc_2.ui.activity;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.huawei.application.BaseApp;
import com.huawei.tup.TUPInterfaceService;
import com.wong.tissonvc_2.R;
import com.wong.tissonvc_2.service.call.CallService;
import com.wong.tissonvc_2.service.common.CallConstants;
import com.wong.tissonvc_2.service.conf.ConferenceService;
import com.wong.tissonvc_2.service.contacts.ContactService;
import com.wong.tissonvc_2.service.login.LoginService;
import com.wong.tissonvc_2.service.login.data.LoginParams;
import com.wong.tissonvc_2.service.utils.CrashHandlerUtil;
import com.wong.tissonvc_2.service.utils.TUPLogUtil;
import com.wong.tissonvc_2.service.utils.Tools;
import com.wong.tissonvc_2.service.TupNotify;
import com.huawei.tup.login.LoginAuthorizeResult;
import com.huawei.utils.ZipUtil;
import com.wong.tissonvc_2.ui.application.TUPApplication;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import common.TupCallParam;

import static com.wong.tissonvc_2.ui.activity.LoginSettingActivity.TUP_SIP_TRANSPORT;
import static com.wong.tissonvc_2.ui.activity.LoginSettingActivity.TUP_SRTP;


/**
 * The type Login activity.
 * <p>
 * LoginActivity
 * Login information input box, login button
 */
public class LoginActivity extends BaseActivity implements TupNotify {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final String PROXY_SERVER = "10.184.95.81"; //VC6.0 SMC
    private static final String REGISTER_SERVER = "10.184.95.81";
    private static final String PORT = "5060";
    private static final String ACCOUNT = "20058";
    private static final String PASSWORD = "huawei123";


//    private static final String PROXY_SERVER = "172.22.11.228"; //VC6.0 MediaX
//    private static final String REGISTER_SERVER = "172.22.11.228";
//    private static final String PORT = "443";
//    private static final String ACCOUNT = "01051211";
//    private static final String PASSWORD = "Huawei@123";


    public static final String FILE_NAME = "TupCallLoginParams";
    private static final String TUP_PROXYSERVER = "tupProxyServer";
    private static final String TUP_REGSERVER = "tupRegisterServer";
    private static final String TUP_PORT = "tupPort";
    private static final String TUP_ACCOUNT = "tupAccount";
    private static final String TUP_PASSWORD = "tupPassword";
    private static final String FIRST_LOGIN_FLAG = "firstLogin";
    private static final String RING_FILE = "call_ring.wav";
    private static final int TOAST_FLAG = 1;
    private static final int FIRST_LOGIN = 0;
    private static final int ALREADY_LOGIN = 1;
    private static final int SMC_LOGIN_SUCCESS = 100;
    private static final int SMC_LOGIN_FAILED = 101;
    private static final Object LOCK = new Object();

    //wifi、4G、3G login
    private static final String NETWORK_COMMON = "common";

    private static LoginActivity instance;

    private String networkType = NETWORK_COMMON;
    private String proxyServer; // proxy server address
    private String regServer; // register server address
    private String serverPort; // server port
    private String sipURI; // sip uri
    private String sipNumber;  // sip number
    private String tupPassword;  //password
    private String ipAddress;
    private int firstLogin;

    private RadioGroup radioGroup;
    private int vcType = 0;

    private EditText proxyServerEditText;
    private EditText regServerEditText;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText serverPortEditText;
    private Button loginButton;
    private Application tupApplication;
    private ProgressDialog loginDialog;
    private Handler handler;
    private SharedPreferences sharedPreferences;
    private ImageView mLoginSettingBtn;

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            switch (id) {
                case R.id.btn_login:
                    showLoginDialog();
                    doLoginClicked();
                    break;
                default:
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        init();
        super.onCreate(savedInstanceState);




        TUPLogUtil.i(TAG, "onCreate()");
        setContentView(R.layout.activity_tup_login);
        LoginService.getInstance().registerTupNotify(this); //register notify
        instance = this;
        initSharedPreferences();
        initView();
        initHandler();
        tupApplication = getApplication();
        TUPLogUtil.i(TAG, "TUPInit is running");
    }

    @Override
    protected void onResume() {
        networkType = NETWORK_COMMON;
        super.onResume();
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static LoginActivity getInstance() {
        return instance;
    }

    private void initSharedPreferences() {
        sharedPreferences = getSharedPreferences(FILE_NAME, Activity.MODE_PRIVATE);
    }

    /*
     * widget init
     */
    private void initView() {
        mLoginSettingBtn = (ImageView) findViewById(R.id.iv_login_setting);
        proxyServerEditText = (EditText) findViewById(R.id.et_proxy_server_address);
        regServerEditText = (EditText) findViewById(R.id.et_register_server_address);
        usernameEditText = (EditText) findViewById(R.id.et_username);
        passwordEditText = (EditText) findViewById(R.id.et_password);
        serverPortEditText = (EditText) findViewById(R.id.et_server_port);
        radioGroup = (RadioGroup) findViewById(R.id.vcType);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.vc_smc:
                        vcType = 0;
                        break;
                    case R.id.vc_hosted:
                        vcType = 1;
                        break;
                    default:
                        break;
                }
            }
        });

        usernameEditText.setText(ACCOUNT);
        passwordEditText.setText(PASSWORD);
        proxyServerEditText.setText(PROXY_SERVER);
        regServerEditText.setText(REGISTER_SERVER);
        serverPortEditText.setText(PORT);


        if (null != sharedPreferences) {
            firstLogin = sharedPreferences.getInt(FIRST_LOGIN_FLAG, FIRST_LOGIN);
        } else {
            TUPLogUtil.e(TAG, "sharedPreferences is null");
        }

        loginButton = (Button) findViewById(R.id.btn_login);
        loginButton.setOnClickListener(listener);

        mLoginSettingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissLoginDialog();
                Intent intent = new Intent(LoginActivity.this, LoginSettingActivity.class);
                startActivity(intent);
            }
        });

    }

    /**
     * init handler
     */
    private void initHandler() {

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                TUPLogUtil.i(TAG, "what:" + msg.what);
                parallelHandleMessage(msg);
                super.handleMessage(msg);
            }
        };
    }

    /**
     * handle message
     *
     * @param msg
     */
    private void parallelHandleMessage(Message msg) {
        switch (msg.what) {
            case TOAST_FLAG:
                Toast.makeText(LoginActivity.this, ((String) msg.obj), Toast.LENGTH_SHORT).show();
                break;

            case SMC_LOGIN_SUCCESS:
                if (vcType == 1) {
                    processLogin();
                }
                Toast.makeText(LoginActivity.this, ((String) msg.obj), Toast.LENGTH_SHORT).show();
                break;

            case SMC_LOGIN_FAILED:
                dismissLoginDialog();
                Toast.makeText(LoginActivity.this, ((String) msg.obj), Toast.LENGTH_SHORT).show();
                break;

            default:
                break;
        }
    }

    private void onLoginSuccess() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (LOCK) {
                    Intent intent = new Intent(LoginActivity.this, CallActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    TUPLogUtil.i(TAG, "Login Success.");
                }
            }
        }).start();
    }

    private void showLoginDialog() {
        if (loginDialog == null) {
            loginDialog = new ProgressDialog(this);
            loginDialog.setCanceledOnTouchOutside(false);
            loginDialog.setTitle(getString(R.string.logining_msg));
            loginDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }
        loginDialog.show();
    }

    private void dismissLoginDialog() {
        if (loginDialog != null && loginDialog.isShowing()) {
            loginDialog.dismiss();
        }
    }

    private void doLoginClicked() {
        proxyServer = proxyServerEditText.getText().toString().trim();
        regServer = regServerEditText.getText().toString().trim();
        serverPort = serverPortEditText.getText().toString().trim();
        sipNumber = usernameEditText.getText().toString().trim();
        tupPassword = passwordEditText.getText().toString();

        if (null == sipNumber || null == tupPassword || sipNumber.equals("") || tupPassword.equals("")) {
            Toast.makeText(LoginActivity.this, getString(R.string.account_information_not_empty),
                    Toast.LENGTH_SHORT).show();
            dismissLoginDialog();
            return;
        }

        if (null == proxyServer || proxyServer.equals("")) {
            Toast.makeText(LoginActivity.this, getString(R.string.proxyServer_not_null),
                    Toast.LENGTH_LONG).show();
            dismissLoginDialog();
            return;
        }

        if (TextUtils.isEmpty(regServer)) {
            Toast.makeText(LoginActivity.this, getString(R.string.regServer_not_null),
                    Toast.LENGTH_LONG).show();
            dismissLoginDialog();
            return;
        }

        if (!regServer.equals("") && !sipNumber.equals("")) {
            String account = sipNumber;
            if (sipNumber.contains("@")) {
                String[] str = sipNumber.split("@");
                account = str[0];
            }
            sipURI = account + "@" + regServer;
        }

        getIpAddress();


        LoginService.getInstance().setIpAddress(ipAddress);

        ContactService.getInstance().startServer(sipNumber);

        if (vcType == 0) {
            TUPLogUtil.d(TAG, "---LOGIN_SMC");
            LoginService.getInstance().authorize(sipNumber,
                    tupPassword, regServer, Integer.parseInt(serverPort));
            //为了兼容V5R2，SIP注册过程不依赖SMC鉴权结果.
            processLogin();
        } else if (vcType == 1) {
            TUPLogUtil.d(TAG, "--LOGIN_Hosted");
            LoginService.getInstance().authorizeHosted(sipNumber,
                    tupPassword, regServer, Integer.parseInt(serverPort));
        }


        importHWCer();

        saveLoginParams();
        importRingFile();


    }

    private void importHWCer() {
        try {
            InputStream in = getAssets().open("sc_root.pem");
            String pathtrg = ZipUtil.getCanonicalPath(getFilesDir()) + '/' + "root_cert_use.pem";
            copyAssetsFile(in, pathtrg);
        } catch (IOException e) {
//            Log.e(TAG, "Progress get an IOException.");
        }
    }


    private void importRingFile() {
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);
        if (!sdCardExist) {
            TUPLogUtil.e(TAG, "sdcard is not exist");
            return;
        }

        TUPLogUtil.i(TAG, "import call ring file!~");
        try {
            String pathtrg = Environment.getExternalStorageDirectory() + File.separator + RING_FILE;
            InputStream in = getAssets().open(RING_FILE);
            copyAssetsFile(in, pathtrg);
        } catch (IOException e) {
            TUPLogUtil.e(TAG, "Progress get an IOException.");
        }
    }

    /**
     * Reset local ip.
     */
    private void getIpAddress() {
        if (Tools.isStringEmpty(networkType)) {
            return;
        }
        if (networkType.equals(NETWORK_COMMON)) {
            this.ipAddress = Tools.getLocalIp();
        }
    }

    private void saveLoginParams() {
        if (null != sharedPreferences) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(TUP_PROXYSERVER, proxyServer);
            editor.putString(TUP_REGSERVER, regServer);
            editor.putString(TUP_PORT, serverPort);
            editor.putString(TUP_ACCOUNT, sipNumber);
            editor.putString(TUP_PASSWORD, tupPassword);
            editor.putInt(FIRST_LOGIN_FLAG, ALREADY_LOGIN);
            editor.commit();
        } else {
            TUPLogUtil.e(TAG, "sharedPreferences is null");
        }
    }

    private void processLogin() {
        LoginAuthorizeResult hostedLoginResult = LoginService.getInstance().getHostedLoginResult();
        LoginParams loginParams = LoginParams.getInstance();
        if (hostedLoginResult == null) {
            loginParams.setProxyServerIp(proxyServer);
            loginParams.setRegisterServerIp(regServer);
            loginParams.setServerPort(serverPort);
            loginParams.setSipURI(sipURI);
            loginParams.setVoipNumber(sipNumber);
            loginParams.setVoipPassword(tupPassword);
            loginParams.setSipImpi(sipNumber);
        } else {
            if (vcType != 1) {
                Log.e(TAG, "-----current not hosted");
                return;
            }
            String proxyAddress = hostedLoginResult.getSipInfo().getProxyAddress();
            String ip = proxyAddress.substring(0, proxyAddress.indexOf(':'));
            String port = proxyAddress.substring(proxyAddress.indexOf(':') + 1);
            String name = hostedLoginResult.getSipInfo().getAuthInfo().getUserName();
            String pass = hostedLoginResult.getSipInfo().getAuthInfo().getPassword();
            String displayName = hostedLoginResult.getSipInfo().getDisplayName();
            String uri = name + "@" + ip;
            TUPLogUtil.i(TAG, "-------ip=" + ip + ",port=" + port + ",name=" + name
                    + pass + ",impi=" + displayName + ",uri=" + uri);


            loginParams.setProxyServerIp(ip);
            loginParams.setRegisterServerIp(ip);
            loginParams.setServerPort(port);
            loginParams.setSipURI(uri);
            loginParams.setVoipNumber(name);
            loginParams.setVoipPassword(pass);
            loginParams.setSipImpi(displayName);
        }

        loginParams.setSrtpMode(sharedPreferences.getInt(TUP_SRTP, 0));
        loginParams.setTransportMode(sharedPreferences.getInt(TUP_SIP_TRANSPORT, 0));
        loginParams.setLocalIpAddress(ipAddress);
        if (null == Looper.myLooper()) {
            Looper.prepare();
        }

        if (!Tools.isNetworkAvailable(tupApplication)) {
            TUPLogUtil.e(TAG, " network has been disconnected");
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                login(tupApplication);
            }
        }).run();

    }

    private boolean login(Context context) {

        TUPLogUtil.i(TAG, "login.");
        if (CallService.getInstance() == null) {
            TUPLogUtil.i(TAG, "login fail.");
            return false;
        } else if (Tools.isWifiOr3GAvailable(context)) {
            //sip register
            LoginService.getInstance().login();
            return true;
        } else {
            return false;
        }
    }

    /**
     * handleRequestError
     *
     * @param errorCode
     */
    private void handleRequestError(int errorCode, BaseActivity activity) {
        if (activity == null) {
            return;
        }
        String msg = null;
        switch (errorCode) {
            // 400 bad request
            case CallConstants.CALL_E_REASON_CODE_BADREQUEST:
                msg = activity.getString(R.string.bad_request);
                break;
            //402 payment required
            case CallConstants.CALL_E_REASON_CODE_PAYMENTREQUIRED:
                msg = activity.getString(R.string.payment_required);
                break;
            //403 forbidden
            case CallConstants.CALL_E_REASON_CODE_FORBIDDEN:
                msg = activity.getString(R.string.forbidden);
                break;
            //404 not found
            case CallConstants.CALL_E_REASON_CODE_NOTFOUND:
                msg = activity.getString(R.string.not_found);
                break;
            //405 method no allowed
            case CallConstants.CALL_E_REASON_CODE_METHODNOTALLOWED:
                msg = activity.getString(R.string.method_not_allowed);
                break;
            //406 not acceptable
            case CallConstants.CALL_E_REASON_CODE_RESNOTACCEPTABLE:
                msg = activity.getString(R.string.not_acceptable);
                break;
            //408 request timeout
            case CallConstants.CALL_E_REASON_CODE_REQUESTTIMEOUT:
                msg = activity.getString(R.string.request_timeout);
                break;
            //500 server internal error
            case CallConstants.CALL_E_REASON_CODE_SERVERINTERNALERROR:
                msg = activity.getString(R.string.server_internal_error);
                break;
            //501 not implemented
            case CallConstants.CALL_E_REASON_CODE_NOTIMPLEMENTED:
                msg = activity.getString(R.string.not_implemented);
                break;
            //502 bad gateway
            case CallConstants.CALL_E_REASON_CODE_BADGATEWAY:
                msg = activity.getString(R.string.bad_gateway);
                break;
            //503 service unavailable
            case CallConstants.CALL_E_REASON_CODE_SERVICEUNAVAILABLE:
                msg = activity.getString(R.string.service_unavailable);
                break;
            //504 server time-out
            case CallConstants.CALL_E_REASON_CODE_SERVERTIMEOUT:
                msg = activity.getString(R.string.server_time_out);
                break;
            //505 version not supported
            case CallConstants.CALL_E_REASON_CODE_VERSIONNOTSUPPORTED:
                msg = activity.getString(R.string.version_not_supported);
                break;
            default:
                break;
        }
        if (msg != null) {
            sendHandlerMessage(TOAST_FLAG, msg);
        }
    }

    private void sendHandlerMessage(int what, Object object) {
        if (handler == null) {
            return;
        }
        Message msg = handler.obtainMessage(what, object);
        handler.sendMessage(msg);
    }

    private void copyAssetsFile(InputStream in, String pathtrg) {
        BufferedInputStream inBuff = null;
        FileOutputStream output = null;
        BufferedOutputStream outBuffStream = null;
        byte[] b = new byte[1024 * 8];
        File filetrg = new File(pathtrg);
        try {
            if (!filetrg.exists()) {
                boolean isCreateSuccess = filetrg.createNewFile();
                if (!isCreateSuccess) {
                    return;
                }
            }
            inBuff = new BufferedInputStream(in);
            output = new FileOutputStream(filetrg);
            outBuffStream = new BufferedOutputStream(output);
            int inBuflen = inBuff.read(b);
            int i = 0; //
            boolean resultBool = inBuflen != -1;
            while (resultBool) {
                i++;
                outBuffStream.write(b, 0, inBuflen);
                if (i == 64) {
                    Thread.sleep(20);
                    i = 0;
                }
                inBuflen = inBuff.read(b);
                resultBool = (inBuflen != -1);
            }
            outBuffStream.flush();
        } catch (IOException e) {
            TUPLogUtil.e(TAG, "Progress get an IOException.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // close stream
            closeOutputStream(outBuffStream);
            closeOutputStream(output);
            closeInputStream(inBuff);
            closeInputStream(in);
            b = null;
        }

    }

    private void closeInputStream(InputStream iStream) {
        try {
            if (null != iStream) {
                iStream.close();
            }
        } catch (IOException e) {
            TUPLogUtil.e(TAG, "Progress get an IOException.");
        }
    }

    private void closeOutputStream(OutputStream oStream) {
        try {
            if (null != oStream) {
                oStream.close();
            }
        } catch (IOException e) {
            TUPLogUtil.e(TAG, "Progress get an IOException.");
        }
    }

    @Override
    protected void onDestroy() {
        TUPLogUtil.i(TAG, "onDestroy()");
        LoginService.getInstance().unregisterTupNotify(this);
        if (null != handler) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
        instance = null;
        super.onDestroy();

    }

    /**
     * clearData
     */
    @Override
    public void clearData() {
    }

    /**
     * On register result.
     *
     * @param registerResult the register result
     * @param errorCode      the error code
     */
    @Override
    public void onRegisterNotify(int registerResult, int errorCode) {
        switch (registerResult) {
            case TupCallParam.CALL_E_REG_STATE.CALL_E_REG_STATE_REGISTERED:
                TUPLogUtil.i(TAG, "register success");
                dismissLoginDialog();
                CallService.getInstance().renderCreate();
                onLoginSuccess();
                break;
            case TupCallParam.CALL_E_REG_STATE.CALL_E_REG_STATE_UNREGISTER:
                TUPLogUtil.i(TAG, "register fail");
                dismissLoginDialog();
                TUPLogUtil.i(TAG, "errorCode->" + errorCode);
                handleRequestError(errorCode, LoginActivity.this);
                break;
            default:
                break;
        }
    }

    @Override
    public void onSMCLogin(int smcLoginResult, String errorReason) {
        switch (smcLoginResult) {
            case TupCallParam.CALL_TUP_RESULT.TUP_SUCCESS:
                TUPLogUtil.i(TAG, "smc login success");
                sendHandlerMessage(SMC_LOGIN_SUCCESS, "login success!");
                break;

            case TupCallParam.CALL_TUP_RESULT.TUP_FAIL:
                TUPLogUtil.e(TAG, "smc login  fail");
                sendHandlerMessage(SMC_LOGIN_FAILED, errorReason);
                break;

            default:
                break;
        }
    }

    /**
     * On call notification.
     *
     * @param code   the code
     * @param object the object
     */
    @Override
    public void onCallNotify(int code, Object object) {
    }


//    ~~~~~~~~~~~~```

    private TUPInterfaceService tupInterfaceService;

    private void init() {
        CallService.getInstance().tupCallInit();
        tupInterfaceService = new TUPInterfaceService();
        tupInterfaceService.StartUpService();
        tupInterfaceService.SetAppPath(getApplicationInfo().dataDir + "/lib");
        LoginService.getInstance().init(tupInterfaceService);
        ConferenceService.getInstance().confInit(tupInterfaceService);

        ((TUPApplication) getApplication()).setTupInterfaceService(tupInterfaceService);
        ContactService.getInstance().setLogParam(3, 2 * 1024, 2
                , Environment.getExternalStorageDirectory().toString() + "/VCLOG/contacts");

    }

}
