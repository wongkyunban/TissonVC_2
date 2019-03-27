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

import com.huawei.common.PersonalContact;
import com.wong.tissonvc_2.R;
import com.wong.tissonvc_2.service.contacts.ContactService;

import java.util.List;

public class ContactsActivity extends BaseActivity
{

    private List<PersonalContact> allContacts;
    private List<PersonalContact> currentsContacts;

    private List<PersonalContact> mContacts;
    ListView mlvContact;
    ContactAdapter mAdapter;
    private Button addBtn;
    private Button searchBtn;
    private EditText searchEt;
    private Button callRecordsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        initData();
        initView();
    }

    private void initView()
    {
        mlvContact = (ListView) findViewById(R.id.lvContact);
        addBtn = (Button) findViewById(R.id.addContacts);
        searchEt = (EditText) findViewById(R.id.searchLocalContactsEt);
        searchBtn = (Button) findViewById(R.id.searchLocalContactsBtn);
        callRecordsBtn = (Button) findViewById(R.id.callRecords);

        callRecordsBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent callRecordsIntent = new Intent(ContactsActivity.this,CallRecordsActivity.class);
                startActivity(callRecordsIntent);
            }
        });


        mAdapter = new ContactAdapter(mContacts, this);
        mlvContact.setAdapter(mAdapter);
        addBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(ContactsActivity.this, AddContactsActivity.class);
                startActivity(intent);
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String searchStr = searchEt.getText().toString();
                if ("".equals(searchStr) || searchStr == null)
                {
                    currentsContacts = allContacts;
                }
                else
                {
                    currentsContacts = ContactService.getInstance().searchLocalContacts(searchStr);
                }
                mAdapter = new ContactAdapter(currentsContacts, ContactsActivity.this);
                mlvContact.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }
        });

    }





    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public void clearData()
    {

    }


    private void initData()
    {
        mContacts = ContactService.getInstance().getLocalAllContacts();
        allContacts = mContacts;
    }


    class ContactAdapter extends BaseAdapter
    {

        List<PersonalContact> contacts;
        ContactsActivity context;
        PersonalContact pc;

        public ContactAdapter(List<PersonalContact> contacts, ContactsActivity context)
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
                convertView = View.inflate(context, R.layout.item_contact, null);
                holder = new ViewHolder();
                holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
                holder.updateBtn = (Button) convertView.findViewById(R.id.updateContactsBtn);
                holder.delBtn = (Button) convertView.findViewById(R.id.deleteContactsBtn);
                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.delBtn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    pc = contacts.get(position);
                    ContactService.getInstance().delLocalContact(pc);
                    notifyDataSetChanged();
                }

            });

            holder.updateBtn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Intent intent = new Intent(ContactsActivity.this, UpdateContactsActivity.class);
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
            Button updateBtn, delBtn;
        }


    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mContacts = ContactService.getInstance().getLocalAllContacts();
        allContacts = mContacts;
        mAdapter.notifyDataSetChanged();
    }
}
