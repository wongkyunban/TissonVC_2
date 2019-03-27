package com.wong.tissonvc_2.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.huawei.common.PersonalContact;
import com.wong.tissonvc_2.R;
import com.wong.tissonvc_2.service.contacts.ContactService;

public class AddContactsActivity extends BaseActivity
{

    private EditText name;
    private EditText num;
    private EditText mobilephone;
    private EditText telephone;
    private EditText email;
    private EditText address;
    private Button addContacts;
    private String uname;
    private String phone;
    private String mphone;
    private String officephone;
    private String uemail;
    private String uaddress;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contacts);
        name = (EditText) findViewById(R.id.nameEt);
        num = (EditText) findViewById(R.id.numEt);
        mobilephone = (EditText) findViewById(R.id.mobilephoneEt);
        telephone = (EditText) findViewById(R.id.telephoneEt);
        email = (EditText) findViewById(R.id.emailEt);
        address = (EditText) findViewById(R.id.addressEt);
        addContacts = (Button) findViewById(R.id.addContactsBtn);
        addContacts.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                uname = name.getText().toString();
                if (uname == null || 0 == uname.length())
                {
                    Toast.makeText(AddContactsActivity.this, "name cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                phone = num.getText().toString();
                mphone = mobilephone.getText().toString();
                officephone = telephone.getText().toString();
                uemail = email.getText().toString();
                uaddress = address.getText().toString();
                PersonalContact pc = new PersonalContact();
                pc.setName(uname);
                pc.setNumberOne(phone);
                pc.setMobilePhone(mphone);
                pc.setOfficePhone(officephone);
                pc.setEmail(uemail);
                pc.setAddress(uaddress);
                int ret = ContactService.getInstance().addLocalContact(pc);
                if (ret == 0)
                {
                    finish();
                } else
                {
                    Toast.makeText(AddContactsActivity.this, "result:" + ret, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void clearData()
    {

    }

}
