package com.wong.tissonvc_2.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.huawei.common.CallRecordInfo;
import com.wong.tissonvc_2.R;
import com.wong.tissonvc_2.service.contacts.ContactService;
import com.wong.tissonvc_2.service.utils.TUPLogUtil;

import java.util.List;

public class CallRecordsActivity extends Activity
{
    private List<CallRecordInfo> callRecordInfoList;
    private ListView listView;
    private CallRecordsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_records);
        listView = (ListView) findViewById(R.id.lvCallRecord);

        callRecordInfoList = ContactService.getInstance().getCallRecords();
        TUPLogUtil.i("CallRecordsActivity", "--------------size:" + callRecordInfoList.size());
        adapter = new CallRecordsAdapter(callRecordInfoList, this);
        listView.setAdapter(adapter);
    }

    class CallRecordsAdapter extends BaseAdapter
    {
        List<CallRecordInfo> records;
        CallRecordsActivity context;
        CallRecordInfo callRecordInfo;

        private CallRecordsAdapter(List<CallRecordInfo> records, CallRecordsActivity context)
        {
            super();
            this.records = records;
            this.context = context;
        }

        @Override
        public int getCount()
        {
            return records.size();
        }

        @Override
        public Object getItem(int position)
        {
            return records.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            ViewHolder holder = null;
            if (convertView == null)
            {
                convertView = View.inflate(context, R.layout.item_callrecords, null);
                holder = new ViewHolder();
                holder.tvCallRecordsName = (TextView) convertView.findViewById(R.id.callRecordsName);
                holder.tvCallRecordsType = (TextView) convertView.findViewById(R.id.callRecordsType);
                holder.tvCallSourceType = (TextView) convertView.findViewById(R.id.callSourceType);
                holder.delBtn = (Button) convertView.findViewById(R.id.deleteCallRecordsBtn);
                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }


            holder.delBtn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    callRecordInfo = records.get(position);
                    ContactService.getInstance().deleteCallRecordById(callRecordInfo.getId());
                    notifyDataSetChanged();
                }
            });


            CallRecordInfo callRecord = records.get(position);
            holder.tvCallRecordsName.setText(callRecord.getNumber());

            if (CallRecordInfo.DialType.AUDIO == callRecord.getCallOutType())
            {
                holder.tvCallRecordsType.setText("AUDIO");
            }

            if (CallRecordInfo.RecordType.CALL_RECORD_IN == callRecord.getCallType())
            {
                holder.tvCallSourceType.setText("in");
            }
            return convertView;
        }

        class ViewHolder
        {
            TextView tvCallRecordsName, tvCallRecordsType, tvCallSourceType;
            Button delBtn;
        }


    }
}


