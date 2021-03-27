package com.ss.newsportal.service.account;

import com.ss.newsportal.dto.account.AccountsStatAdminResponse;


public interface AccountAdminService {
    AccountsStatAdminResponse getAccountsAdmin(String search);
}
