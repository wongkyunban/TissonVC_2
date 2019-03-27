package com.wong.tissonvc_2.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.huawei.common.PersonalContact;
import com.wong.tissonvc_2.R;
import com.wong.tissonvc_2.service.contacts.ContactService;

public class UpdateContactsActivity extends BaseActivity
{
    private PersonalContact pc;
    private String uname, phone, mobilephone, officephone, email, address;
    private EditText updateName;
    private EditText updateNum, updateMobilePhone, updateOfficePhone;
    private EditText updateEmail, updateAddress;
    private Button updateBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_contacts);
        pc = (PersonalContact) getIntent().getSerializableExtra("pc");
        uname = pc.getName();
        phone = pc.getNumberOne();
        mobilephone = pc.getMobilePhone();
        officephone = pc.getOfficePhone();
        email = pc.getEmail();
        address = pc.getAddress();
        initView();
    }

    @Override
    public void clearData()
    {

    }

    private void initView()
    {
        updateName = (EditText) findViewById(R.id.updatenameEt);
        updateName.setText(uname);
        updateNum = (EditText) findViewById(R.id.updatenumEt);
        updateNum.setText(phone);
        updateMobilePhone = (EditText) findViewById(R.id.updatemobilephoneEt);
        updateMobilePhone.setText(mobilephone);
        updateOfficePhone = (EditText) findViewById(R.id.updatetelephoneEt);
        updateOfficePhone.setText(officephone);
        updateEmail = (EditText) findViewById(R.id.updateemailEt);
        updateEmail.setText(email);
        updateAddress = (EditText) findViewById(R.id.updateaddressEt);
        updateAddress.setText(address);

        updateBtn = (Button) findViewById(R.id.updateContactsBtn);
        updateBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String newName = updateName.getText().toString();
                if ("".equals(newName) || newName == null)
                {
                    Toast.makeText(UpdateContactsActivity.this, "newName can't be null", Toast.LENGTH_SHORT).show();
                    return;
                }
                pc.setName(newName);
                pc.setNumberOne(updateNum.getText().toString());
                pc.setMobilePhone(updateMobilePhone.getText().toString());
                pc.setOfficePhone(updateOfficePhone.getText().toString());
                pc.setEmail(updateEmail.getText().toString());
                pc.setAddress(updateAddress.getText().toString());
                int ret = ContactService.getInstance().modifyLocalContact(pc);
                if (ret == 0)
                {
                    finish();
                } else
                {
                    Toast.makeText(UpdateContactsActivity.this, "result:" + ret, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

}
