package com.wong.tissonvc_2.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.wong.tissonvc_2.R;
import com.wong.tissonvc_2.service.conf.DataConfService;
import com.wong.tissonvc_2.service.notify.VCDataConfNotify;
import com.wong.tissonvc_2.service.utils.TUPLogUtil;
import com.huawei.meeting.ConfInfo;

public class ConfShareActivity extends Activity implements VCDataConfNotify
{
    private Button leaveConfBtn;
    private RelativeLayout fileSharedLayout;
    private RelativeLayout desktopSharedLayout;
    private RelativeLayout wbSharedLayout;
    private String TAG = ConfShareActivity.class.getSimpleName();


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_conf_share);
        DataConfService.getInstance().registerVCDataConfNotify(this);

        fileSharedLayout = (RelativeLayout) findViewById(R.id.fileSharedLayout); // sharedView
        desktopSharedLayout = (RelativeLayout) findViewById(R.id.desktopSharedLayout); // sharedView
        wbSharedLayout = (RelativeLayout) findViewById(R.id.wbSharedLayout); // sharedView

        leaveConfBtn = (Button) findViewById(R.id.leaveConf);
        leaveConfBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });

        DataConfService.getInstance().setSharedViewContainer(ConfShareActivity.this
                , fileSharedLayout, 1);

        DataConfService.getInstance().setSharedViewContainer(ConfShareActivity.this
                , desktopSharedLayout, 2);

        DataConfService.getInstance().setSharedViewContainer(ConfShareActivity.this
                , wbSharedLayout, 512);

    }

    @Override
    public void onGetDataConfParamsResult(ConfInfo confInfo)
    {

    }

    @Override
    public void onDataShareResult(int shareVal, int shareStatus)
    {
        String sharedType;
        String sharedState;
        if ("1".equals(shareVal))
        {
            sharedType = "Document sharing";
        }
        else if ("2".equals(shareVal))
        {
            sharedType = "Application sharing";
        }
        else if ("512".equals(shareVal))
        {
            sharedType = "wb sharing";
        }
        else
        {
            sharedType = "";
        }

        if ("1".equals(shareStatus))
        {
            sharedState = "begin !";
            if ("1".equals(shareVal))
            {
                DataConfService.getInstance().setSharedViewContainer(ConfShareActivity.this
                        , fileSharedLayout, 1);
            }
            else if ("2".equals(shareVal))
            {
                DataConfService.getInstance().setSharedViewContainer(ConfShareActivity.this
                        , desktopSharedLayout, 2);
            }
            else if ("512".equals(shareVal))
            {
                DataConfService.getInstance().setSharedViewContainer(ConfShareActivity.this
                        , wbSharedLayout, 512);
            }
        }
        else
        {
            sharedState = "end !";
        }

        TUPLogUtil.i(TAG, "onDataShareResult sharedType=" + sharedType + ",sharedState=" + sharedState);

    }
}
