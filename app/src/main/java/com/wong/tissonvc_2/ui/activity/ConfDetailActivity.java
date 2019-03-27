package com.wong.tissonvc_2.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.wong.tissonvc_2.R;
import com.wong.tissonvc_2.service.conf.ConferenceService;
import com.wong.tissonvc_2.service.utils.TUPLogUtil;
import com.huawei.tup.confctrl.sdk.TupConfVCAttendeeInfo;

import java.util.ArrayList;
import java.util.List;

public class ConfDetailActivity extends Activity
{
    private List<TupConfVCAttendeeInfo> attends = new ArrayList<>();
    private ConfDetailAdapter mAdapter;
    private ListView confDetailLv;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conf_detail);
        attends = ConferenceService.getInstance().getAttendeeInfoList();
        confDetailLv = (ListView) findViewById(R.id.attends_detail);
        mAdapter = new ConfDetailAdapter(attends, this);
        confDetailLv.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        TUPLogUtil.i("ConfDetailActivity", "onCreate----------size:" + attends.size());
    }


    class ConfDetailAdapter extends BaseAdapter
    {
        List<TupConfVCAttendeeInfo> mAttends;
        ConfDetailActivity context;

        public ConfDetailAdapter(List<TupConfVCAttendeeInfo> attends, ConfDetailActivity context)
        {
            super();
            this.mAttends = attends;
            this.context = context;
        }

        @Override
        public int getCount()
        {
            return mAttends.size();
        }

        @Override
        public Object getItem(int position)
        {
            return mAttends.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            ViewHolder holder = null;
            if (convertView == null)
            {
                convertView = View.inflate(context, R.layout.item_attends_detail, null);
                holder = new ViewHolder();
                holder.siteName = (TextView) convertView.findViewById(R.id.siteName);
                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }

            TupConfVCAttendeeInfo attendeeInfo = mAttends.get(position);
            holder.siteName.setText(attendeeInfo.getSiteName());
            return convertView;
        }

        class ViewHolder
        {
            TextView siteName;
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
    }
}
