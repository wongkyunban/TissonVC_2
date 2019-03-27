package com.wong.tissonvc_2.service.notify;

import com.huawei.common.PersonalContact;

import java.util.List;

public interface LdapNotify
{
    /**
     * On ldap search result.
     *
     * @param pageNo     the page no
     * @param list       the list
     * @param isLastPage the is last page
     */
    void onLdapSearchResult(int pageNo, List<PersonalContact> list, boolean isLastPage);
}
