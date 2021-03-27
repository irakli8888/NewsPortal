package com.ss.newsportal.repository;

import com.ss.newsportal.entity.Account;
import com.ss.newsportal.entity.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    VerificationCode findByToken(String token);

    VerificationCode findByAccount(Account user);
}
