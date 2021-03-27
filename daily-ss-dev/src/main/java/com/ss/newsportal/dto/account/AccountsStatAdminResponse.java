package com.ss.newsportal.dto.account;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class AccountsStatAdminResponse {

    private long countTotal;
    private long countAnonymous;
    private long countRegister;

    private List<AccountAdminDto> accounts;
}
