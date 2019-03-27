package com.wong.tissonvc_2.ui.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.huawei.common.PersonalContact;
import com.wong.tissonvc_2.R;

public class DetailContactsActivity extends BaseActivity
{

    private TextView name;
    private TextView num;
    private TextView mobilephone;
    private TextView officephone;
    private TextView email;
    private TextView address;
    private PersonalContact pc;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_contacts);
        pc = (PersonalContact) getIntent().getSerializableExtra("pc");
        initView();
    }

    @Override
    public void clearData()
    {

    }

    private void initView()
    {
        name = (TextView) findViewById(R.id.detailnameEt);
        name.setText(pc.getName());
        num = (TextView) findViewById(R.id.detailnumEt);
        num.setText(pc.getNumberOne());
        mobilephone = (TextView) findViewById(R.id.detailmobilephoneEt);
        mobilephone.setText(pc.getMobilePhone());
        officephone = (TextView) findViewById(R.id.detailtelephoneEt);
        officephone.setText(pc.getOfficePhone());
        email = (TextView) findViewById(R.id.detailemailEt);
        email.setText(pc.getEmail());
        address = (TextView) findViewById(R.id.detailaddressEt);
        address.setText(pc.getAddress());
    }

}
