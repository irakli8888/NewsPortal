package com.ss.newsportal.security;

import com.ss.newsportal.entity.Account;
import com.ss.newsportal.entity.VerificationCode;
import com.ss.newsportal.repository.AccountRepository;
import com.ss.newsportal.repository.VerificationCodeRepository;
import com.ss.newsportal.service.auth.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeleteUnverifiedEmails {

    private final VerificationCodeRepository verificationCodeRepository;
    private final AccountRepository accountRepository;
    private Logger logger = LoggerFactory.getLogger(RegistrationService.class);

    @Scheduled(cron = "0 0 * * * *")
    public void cleaner() {

        List<VerificationCode> listOfCodes = verificationCodeRepository.findAll();
        for(VerificationCode verificationCode : listOfCodes) {
            if(ChronoUnit.HOURS.between(ZonedDateTime.now(), verificationCode.getCreatedAt()) >= 48) {
                Account account = verificationCode.getAccount();
                verificationCodeRepository.delete(verificationCode);
                accountRepository.delete(account);
                logger.debug("Функция чистки кодов подтверждения отработала успешно");
            }
        }
        listOfCodes.clear();
    }
}
