package com.wong.tissonvc_2.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.wong.tissonvc_2.R;
import com.wong.tissonvc_2.service.call.CallService;
import com.wong.tissonvc_2.service.common.CallConstants;
import com.wong.tissonvc_2.service.utils.TUPLogUtil;
import com.wong.tissonvc_2.ui.utils.LayoutUtil;

import java.io.File;


/**
 * The type Call coming activity.
 * <p/>
 * CallComingActivity
 * call inComing answer activity
 * audio /video call coming
 */
public class CallComingActivity extends BaseActivity
{
    private static final String TAG = CallComingActivity.class.getSimpleName();

    private static final int DELAYTIME = 100000;
    private static final int LOOPS = 0;
    private static final String RING_FILE = "call_ring.wav";

    private static CallComingActivity instance;

    private String callId;

    private Button acceptVideoButton;

    private Button acceptAudioButton;

    private Button rejectButton;

    private View callComingBackground;

    private TextView incomingNameTextView;

    private TextView incomingNumberTextView;

    private int incomingType = 0;

    private String incomingNumber = "";

    private String incomingDisplayname = "";

    private Handler handler = new Handler();

    private String filePath = "";

    private Runnable endRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            finish();
        }
    };

    private Runnable callRejectTask = new Runnable()
    {
        @Override
        public void run()
        {
            cancelCallRejectTask();
            rejectVoipPhone();
        }
    };

    private OnClickListener onClickListener = new OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            int id = view.getId();
            switch (id)
            {
                case R.id.callaccept:
                    acceptVoipPhone(false);
                    break;
                case R.id.accept:
                    acceptVoipPhone(true);
                    break;
                case R.id.refuse:
                    rejectVoipPhone();
                    break;
                default:
                    break;
            }
        }
    };


    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static CallComingActivity getInstance()
    {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // Set on the lock screen
        LayoutUtil.setFrontToLock(this);
        filePath = Environment.getExternalStorageDirectory() + File.separator + RING_FILE;
        File file = new File(filePath);
        if (file.exists())
        {
            int result = CallService.getInstance().startMediaPlay(LOOPS, filePath);
            CallService.getInstance().setPlayHandle(result);
        }

        Intent intent = getIntent();
        incomingNumber = intent.getStringExtra(CallConstants.VOIP_CALLNUMBER);
        incomingDisplayname = intent.getStringExtra(CallConstants.VOIP_CALL_DISPLAY_NAME);
        callId = intent.getStringExtra(CallConstants.VOIP_CALLID);
        incomingType = intent.getIntExtra(CallConstants.COMING_VIEW_TYPE, -1);
        initComponent();
        initCallComing();
        instance = this;
    }

    private void initComponent()
    {
        if (CallConstants.COMING_AUDIO_CALL == incomingType)
        {
            this.setContentView(R.layout.activity_audio_call_coming);
        }
        if (CallConstants.COMING_VIDEO_CALL == incomingType)
        {
            this.setContentView(R.layout.activity_video_call_coming);
        }
        // Caller name
        incomingNameTextView = (TextView) findViewById(R.id.incoming_name);

        // Caller ID
        incomingNumberTextView = (TextView) findViewById(R.id.incoming_number);

        callComingBackground = (View) findViewById(R.id.call_coming_background);
        ImageView callComingImageHead = (ImageView) findViewById(R.id.img_incoming_head);

        acceptAudioButton = (Button) findViewById(R.id.callaccept);
        acceptVideoButton = (Button) findViewById(R.id.accept);
        rejectButton = (Button) findViewById(R.id.refuse);

        if (null != callComingImageHead)
        {
            callComingImageHead.setImageDrawable(getResources().
                    getDrawable(R.drawable.te_call_coming_out_head_photo));
        }
    }

    private void initCallComing()
    {
        String callInName = null;
        String callInNumber = null;

        if (CallConstants.COMING_VIDEO_CALL != incomingType
                && CallConstants.COMING_AUDIO_CALL != incomingType)
        {
            return;
        }
        callComingBackground.setBackgroundDrawable(getResources().
                getDrawable(R.drawable.te_pad_callcoming_background));

        if (!incomingDisplayname.equals(incomingNumber))
        {
            callInName = incomingDisplayname;
            callInNumber = incomingNumber;
        }
        else
        {
            callInName = incomingNumber;
            callInNumber = "";
        }

        callInNumber = incomingNumber;

        int isShowIncomingNumberView = callId.isEmpty() ? View.GONE : View.VISIBLE;

        incomingNumberTextView.setVisibility(isShowIncomingNumberView);
        incomingNameTextView.setText(callInName);
        LayoutUtil.setViewEndEllipse(incomingNameTextView);
        incomingNumberTextView.setText(callInNumber);
        LayoutUtil.setViewEndEllipse(incomingNumberTextView);
        TUPLogUtil.i(TAG, "initDate...");
        acceptAudioButton.setOnClickListener(onClickListener);
        acceptVideoButton.setOnClickListener(onClickListener);
        rejectButton.setOnClickListener(onClickListener);
        cancelCallRejectTask();
        handler.postDelayed(callRejectTask, DELAYTIME);
        TUPLogUtil.i(TAG, "set callRejectTimer");
    }

    private void acceptVoipPhone(final boolean isVideo)
    {
        TUPLogUtil.i(TAG, "accept...isVideo=" + isVideo);
        acceptVideoButton.setClickable(false);
        acceptAudioButton.setClickable(false);
        rejectButton.setClickable(false);
        receiveCall(isVideo);
        finish();
    }

    /**
     * rejectVoipPhone
     */
    private void rejectVoipPhone()
    {
        TUPLogUtil.i(TAG, "rejectVoipPhone()");
        acceptVideoButton.setClickable(false);
        acceptAudioButton.setClickable(false);
        rejectButton.setClickable(false);
        CallService.getInstance().rejectCall(callId);
        new Thread()
        {
            public void run()
            {
                handler.post(endRunnable);
            }
        }.start();

    }

    /**
     * receiveCall
     */
    private void receiveCall(boolean isVideo)
    {
        TUPLogUtil.i(TAG, "receiveCall()");
        if (null == Looper.myLooper())
        {
            Looper.prepare();
        }
        CallService.getInstance().answerCall(callId, isVideo);
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        incomingType = intent.getIntExtra(CallConstants.COMING_VIEW_TYPE, -1);
        TUPLogUtil.i(TAG, "onNewIntent() type:" + incomingType);
        incomingNumber = intent.getStringExtra(CallConstants.VOIP_CALLNUMBER);
        callId = intent.getStringExtra(CallConstants.VOIP_CALLID);
        TUPLogUtil.i(TAG, "onNewIntent() callId:" + callId);
        incomingDisplayname = intent.getStringExtra(CallConstants.VOIP_CALL_DISPLAY_NAME);
        if (incomingType == CallConstants.COMING_AUDIO_CALL)
        {
            return;
        }
        super.onNewIntent(intent);
    }

    @Override
    public void finish()
    {
        TUPLogUtil.i(TAG, "callComing finish");
        cancelCallRejectTask();
        CallService.getInstance().stopMediaPlay();
        if (null != handler)
        {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
        super.finish();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        instance = null;
    }

    @Override
    public void clearData()
    {

    }


    private void cancelCallRejectTask()
    {
        if (null != handler)
        {
            handler.removeCallbacks(callRejectTask);
        }
    }

}
