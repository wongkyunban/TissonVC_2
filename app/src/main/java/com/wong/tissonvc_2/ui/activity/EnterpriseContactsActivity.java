package com.wong.tissonvc_2.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.common.PersonalContact;
import com.wong.tissonvc_2.R;
import com.wong.tissonvc_2.service.contacts.ContactService;
import com.wong.tissonvc_2.service.notify.LdapNotify;
import com.wong.tissonvc_2.service.utils.TUPLogUtil;

import java.util.ArrayList;
import java.util.List;

public class EnterpriseContactsActivity extends BaseActivity implements LdapNotify
{
    private final static String TAG = "EnterpriseContactsActivity";
    private List<PersonalContact> enterpriseContactsLists = new ArrayList<PersonalContact>();
    private ListView enterpriselvContacts;
    private EnterpriseContactAdapter mAdapter;
    private EditText enterpriseSearchEt;
    private Button enterpriseSearchBtn;
    private Button localContactBtn;
    private int type = 0;

    private String currentKeyWord = "";
    private String nextKeyWord = "";
    private int num = 0;
    private boolean moreFlag = true;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enterprise_contacts);
        if (ContactService.getInstance().isLdap())
        {
            type = 1;
            ContactService.getInstance().registerLdapNotify(this);
        }
        else
        {
            TUPLogUtil.i(TAG, "type=" + type);
        }
        initView();
    }

    @Override
    public void clearData()
    {

    }


    private void initView()
    {
        enterpriselvContacts = (ListView) findViewById(R.id.enterpriselvContacts);
        enterpriseSearchEt = (EditText) findViewById(R.id.searchEnterpriseContactsEt);
        enterpriseSearchBtn = (Button) findViewById(R.id.searchEnterpriseContactsBtn);
        localContactBtn = (Button) findViewById(R.id.localContactBtn);

        localContactBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent contactsIntent = new Intent(EnterpriseContactsActivity.this, ContactsActivity.class);
                startActivity(contactsIntent);
            }
        });

        enterpriseSearchBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (type == 1)
                {
                    String keyWord = enterpriseSearchEt.getText().toString().trim();
                    currentKeyWord = keyWord;
                    if (currentKeyWord.equals(nextKeyWord))
                    {
                        ++num;
                    }
                    else
                    {
                        enterpriseContactsLists.clear();
                        moreFlag = true;
                    }
                    if (!moreFlag)
                    {
                        num = 0;
                        Toast.makeText(EnterpriseContactsActivity.this, "All Contacts ready", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    int searchRet = ContactService.getInstance().searchLdapContacts(keyWord, num);
                    TUPLogUtil.d("Contacts", "EnterpriseContactsActivity searchRet:" + searchRet + ",num:" + num);
                    mAdapter.notifyDataSetChanged();
                    nextKeyWord = currentKeyWord;
                }

            }
        });

        mAdapter = new EnterpriseContactAdapter(enterpriseContactsLists, this);
        enterpriselvContacts.setAdapter(mAdapter);
    }


    @Override
    public void onLdapSearchResult(final int iSeqNo, final List<PersonalContact> searchResultList, final boolean bLastPageFlag)
    {
        TUPLogUtil.d("Contacts", "EnterpriseContactsActivity onLdapSearchResult bLastPageFlag:" + bLastPageFlag + ",iSeqNo:" + iSeqNo);
        EnterpriseContactsActivity.this.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                enterpriseContactsLists.addAll(searchResultList);
                mAdapter.notifyDataSetChanged();
                if (bLastPageFlag)
                {
                    moreFlag = false;
                }

            }
        });
    }

    class EnterpriseContactAdapter extends BaseAdapter
    {
        List<PersonalContact> contacts;
        EnterpriseContactsActivity context;

        public EnterpriseContactAdapter(List<PersonalContact> contacts, EnterpriseContactsActivity context)
        {
            super();
            this.contacts = contacts;
            this.context = context;
        }

        @Override
        public int getCount()
        {
            return contacts.size();
        }

        @Override
        public Object getItem(int i)
        {
            return contacts.get(i);
        }

        @Override
        public long getItemId(int i)
        {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            ViewHolder holder = null;
            if (convertView == null)
            {
                convertView = View.inflate(context, R.layout.item_enterprisecontacts, null);
                holder = new ViewHolder();
                holder.tvName = (TextView) convertView.findViewById(R.id.tventerpriseName);
                holder.detailBtn = (Button) convertView.findViewById(R.id.erterpriseContactsDetailBtn);
                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.detailBtn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Intent intent = new Intent(EnterpriseContactsActivity.this, DetailContactsActivity.class);
                    intent.putExtra("pc", contacts.get(position));
                    startActivity(intent);
                }
            });

            PersonalContact contact = contacts.get(position);
            holder.tvName.setText(contact.getName());
            return convertView;
        }


        class ViewHolder
        {
            TextView tvName;
            Button detailBtn;
        }


    }

}
