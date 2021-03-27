package com.ss.newsportal.mapper.account;

import com.ss.newsportal.dto.account.AccountAdminDto;
import com.ss.newsportal.entity.view.AccountAdmin;
import com.ss.newsportal.mapper.ParentMapper;
import org.mapstruct.Mapper;


@Mapper
public interface AccountAdminMapper extends ParentMapper<AccountAdminDto, AccountAdmin> {
}
