package com.wong.tissonvc_2.service.contacts;



import com.huawei.common.CallRecordInfo;
import com.huawei.common.PersonalContact;
import com.wong.tissonvc_2.service.login.data.LoginParams;
import com.wong.tissonvc_2.service.notify.LdapNotify;
import com.wong.tissonvc_2.service.utils.TUPLogUtil;
import com.wong.tissonvc_2.ui.application.TUPApplication;
import com.huawei.tupcontacts.TupContactsManager;
import com.huawei.tupcontacts.TupContactsNotify;
import com.huawei.tupcontacts.TupLdapContactsCfg;

import java.util.List;

import object.NetAddress;

/**
 * The type Contact service.
 */
public class ContactService implements TupContactsNotify
{
    /**
     * The constant TAG.
     */
    private static final String TAG = ContactService.class.getSimpleName();
    /**
     * The constant ins.
     */
    private static ContactService ins;

    /**
     * The Is ldap.
     */
    private boolean isLdap = false;

    /**
     * Is ldap boolean.
     *
     * @return the boolean
     */
    public boolean isLdap()
    {
        return isLdap;
    }

    /**
     * The Ldap notify.
     */
    private LdapNotify ldapNotify;

    /**
     * Sets ldap.
     *
     * @param ldap the ldap
     */
    public void setLdap(boolean ldap)
    {
        isLdap = ldap;
    }

    /**
     * The Tup contacts manager.
     */
    private TupContactsManager tupContactsManager;
    /**
     * The Account.
     */
    private String account = LoginParams.getInstance().getSipImpi()
            + "@" + LoginParams.getInstance().getRegisterServerIp();

    /**
     * Instantiates a new Contact service.
     */
    private ContactService()
    {

    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public synchronized static ContactService getInstance()
    {
        if (ins == null)
        {
            ins = new ContactService();
        }
        return ins;
    }

    /**
     * set contact log params
     *
     * @param log_level  the log level
     * @param maxsize_kb the maxsize kb
     * @param file_count the file count
     * @param log_path   the log path
     */
    public void setLogParam(int log_level, int maxsize_kb, int file_count, String log_path)
    {
        tupContactsManager = TupContactsManager.getIns(TUPApplication.getApplication(), this);
        tupContactsManager.setLogParam(log_level, maxsize_kb, file_count, log_path);
    }

    /**
     * Register ldap notify.
     *
     * @param notify the notify
     */
    public void registerLdapNotify(LdapNotify notify)
    {
        ldapNotify = notify;
    }

    /**
     * Start server.
     */
    public void startServer(String account)
    {
        boolean r1 = startLocalContactServer(account);
        boolean r2 = startCallRecordServer(account);
        boolean r3 = startFtpContactServer(account);
        TUPLogUtil.i(TAG, "r1=" + r1   + ",r3=");
    }

    /**
     * Start local contact server boolean.
     *
     * @return the boolean
     */
    public boolean startLocalContactServer(String account)
    {
        return tupContactsManager.startLocalContactServer(account);
    }

    /**
     * Start call record server boolean.
     *
     * @return the boolean
     */
    public boolean startCallRecordServer(String account)
    {
        return tupContactsManager.startCallRecordServer(account);
    }

    /**
     * Start ftp contact server boolean.
     *
     * @return the boolean
     */
    public boolean startFtpContactServer(String account)
    {
        return tupContactsManager.startFtpContactServer(account);
    }

    /**
     * Sets cert file dir.
     *
     * @param certFileDir the cert file dir
     */
    public void setCertFileDir(String certFileDir)
    {
        tupContactsManager.setCertFileDir(certFileDir);
    }

    /**
     * Download ftp contacts.
     *
     * @param downloadInfo the download info
     */
    public void downloadFtpContacts(NetAddress downloadInfo)
    {
        tupContactsManager.downloadFtpContacts(downloadInfo);
    }

    /**
     * Sets ldap config.
     *
     * @param config the config
     * @return the ldap config
     */
    public int setLdapConfig(TupLdapContactsCfg config)
    {
        return tupContactsManager.setLdapConfig(config);
    }

    /**
     * Start ldap contacts server boolean.
     *
     * @return the boolean
     */
    public boolean startLdapContactsServer()
    {
        return tupContactsManager.startLdapContactsServer();
    }

    /**
     * Stop ldap contacts server.
     */
    public void stopLdapContactsServer()
    {
        if (tupContactsManager != null)
        {
            tupContactsManager.stopLdapContactsServer();
        }
    }

    /**
     * Search ldap contacts int.
     *
     * @param keyWord the key word
     * @param pageNo  the page no
     * @return the int
     */
    public int searchLdapContacts(String keyWord, int pageNo)
    {
        return tupContactsManager.searchLdapContacts(keyWord, pageNo);
    }


    /**
     * Gets local all contacts.
     *
     * @return the local all contacts
     */
    public List<PersonalContact> getLocalAllContacts()
    {
        return tupContactsManager.getLocalAllContacts();
    }

    /**
     * Search local contacts list.
     *
     * @param keyWord the key word
     * @return the list
     */
    public List<PersonalContact> searchLocalContacts(String keyWord)
    {
        return tupContactsManager.searchLocalContacts(keyWord);
    }

    /**
     * Add local contact int.
     *
     * @param contact the contact
     * @return the int
     */
    public int addLocalContact(PersonalContact contact)
    {
        return tupContactsManager.addLocalContact(contact);
    }

    /**
     * Del local contact int.
     *
     * @param pc the pc
     * @return the int
     */
    public int delLocalContact(PersonalContact pc)
    {
        return tupContactsManager.delLocalContact(pc);
    }

    /**
     * Modify local contact int.
     *
     * @param pc the pc
     * @return the int
     */
    public int modifyLocalContact(PersonalContact pc)
    {
        return tupContactsManager.modifyLocalContact(pc);
    }

    /**
     * Export local contacts boolean.
     *
     * @param filePath    the file path
     * @param contactType the contact type
     * @return the boolean
     */
    public boolean exportLocalContacts(String filePath, int contactType)
    {
        return tupContactsManager.exportLocalContacts(filePath, contactType);
    }

    /**
     * Import local contacts int.
     *
     * @param filePath    the file path
     * @param contactType the contact type
     * @return the int
     */
    public int importLocalContacts(String filePath, int contactType)
    {
        return tupContactsManager.importLocalContacts(filePath, contactType);
    }

    /**
     * Gets call records.
     *
     * @return the call records
     */
    public List<CallRecordInfo> getCallRecords()
    {
        return tupContactsManager.getCallRecords();
    }

    /**
     * Insert call record int.
     *
     * @param callRecordInfo the call record info
     * @return the int
     */
    public int insertCallRecord(CallRecordInfo callRecordInfo)
    {
        return tupContactsManager.insertCallRecord(callRecordInfo);
    }

    /**
     * Modify call record int.
     *
     * @param callRecordInfo the call record info
     * @return the int
     */
    public int modifyCallRecord(CallRecordInfo callRecordInfo)
    {
        return tupContactsManager.modifyCallRecord(callRecordInfo);
    }

    /**
     * Delete call record by id int.
     *
     * @param callRecordId the call record id
     * @return the int
     */
    public int deleteCallRecordById(int callRecordId)
    {
        return tupContactsManager.deleteCallRecordById(callRecordId);
    }

    /**
     * Del call record by record type int.
     *
     * @param type the type
     * @return the int
     */
    public int delCallRecordByRecordType(CallRecordInfo.RecordType type)
    {
        return tupContactsManager.delCallRecordByRecordType(type);
    }


    @Override
    public void onCallRecordAdded(List<CallRecordInfo> list)
    {
        TUPLogUtil.i(TAG, "onCallRecordAdded----------------------");
    }

    @Override
    public void onCallRecordRemoved(List<CallRecordInfo> list)
    {
        TUPLogUtil.i(TAG, "onCallRecordRemoved----------------------");
    }

    @Override
    public void onCallRecordClear(CallRecordInfo.RecordType recordType)
    {
        TUPLogUtil.i(TAG, "onCallRecordClear----------------------");
    }

    @Override
    public void onCallRecordModied(List<CallRecordInfo> list)
    {
        TUPLogUtil.i(TAG, "onCallRecordModied----------------------");
    }

    @Override
    public void onExportSuccessful(int i, int i1)
    {

    }

    @Override
    public void onImportSuccessful(int i, int i1)
    {

    }

    @Override
    public void onImportEnd(List<PersonalContact> list, int i, int i1, int i2)
    {

    }

    @Override
    public void onContactAdded(List<PersonalContact> list)
    {
        TUPLogUtil.i(TAG, "onContactAdded----------------------");
    }

    @Override
    public void onContactRemoved(List<PersonalContact> list)
    {
        TUPLogUtil.i(TAG, "onContactRemoved----------------------");
    }

    @Override
    public void onContactModified(List<PersonalContact> list)
    {
        TUPLogUtil.i(TAG, "onContactModified----------------------");
    }

    @Override
    public void onEnterpriseContactAnalyDone(List<PersonalContact> list)
    {

    }

    @Override
    public void beforeEnterpriseUpdate()
    {
        TUPLogUtil.i(TAG, "----------------beforeEnterpriseUpdate");
    }

    @Override
    public void afterEnterpriseUpdate()
    {
        TUPLogUtil.i(TAG, "----------------afterEnterpriseUpdate");
    }

    @Override
    public void onLdapSearchResult(int i, List<PersonalContact> list, boolean b)
    {
        TUPLogUtil.i(TAG, "-------onLdapSearchResult------i=" + i + ",--b=" + b + ",--size:" + list.size());
        ldapNotify.onLdapSearchResult(i,list,b);
    }
}
