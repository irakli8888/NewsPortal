package com.ss.newsportal.dto.account;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;


@Getter
@Setter
public class AccountAdminDto {

    private Long id;

    private String username;

    private String profile;

    private long countFeedback;

    private ZonedDateTime dateTimeLastInput;

    private boolean statusBan;

    private String role;

    private long countNews;

    private long countLikes;

    private long countDisLikes;
}
