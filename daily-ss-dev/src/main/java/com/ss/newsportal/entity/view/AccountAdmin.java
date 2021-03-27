package com.ss.newsportal.entity.view;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.ZonedDateTime;


@Getter
@Setter
@Entity
@Table(name = "account_admin")
public class AccountAdmin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
