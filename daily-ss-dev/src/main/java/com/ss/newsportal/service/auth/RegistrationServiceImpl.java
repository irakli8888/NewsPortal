package com.ss.newsportal.service.auth;

import com.ss.newsportal.MailSenderHelper;
import com.ss.newsportal.dto.RegisterDto;
import com.ss.newsportal.entity.Account;
import com.ss.newsportal.entity.Person;
import com.ss.newsportal.entity.Role;
import com.ss.newsportal.entity.VerificationCode;
import com.ss.newsportal.exception.RegistrationUniqueConstraintsException;
import com.ss.newsportal.repository.AccountRepository;
import com.ss.newsportal.repository.PersonRepository;
import com.ss.newsportal.repository.RoleRepository;
import com.ss.newsportal.repository.VerificationCodeRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class RegistrationServiceImpl implements RegistrationService {
    private final AccountRepository accountRepository;
    private final PersonRepository personRepository;
    private final RoleRepository roleRepository;
    private final VerificationCodeRepository verificationCodeRepository;
    private final PasswordEncoder passwordEncoder;

    private final MailSenderHelper mailSenderService;

    private Logger logger = LoggerFactory.getLogger(RegistrationServiceImpl.class);

    @Override
    public void registerAccount(RegisterDto registerDto) {
//        костыльная проверка на уникальность username и email
        checkUniqueConstraints(registerDto);

        Role userRole = roleRepository.findByName(Role.Names.USER);

        Account account = new Account();
        account.setUsername(registerDto.getUsername());
        account.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        account.setDateTimeLastInput(ZonedDateTime.now());
        account.setLoginAttemptInput((short) 0);
        account.setStatusBan(false);
        account.setEmailVerified(false);
        account.getRoles().add(userRole);
        accountRepository.save(account);

        Person person = new Person();
        person.setAccount(account);
        person.setEmail(registerDto.getEmail());
        person.setLastName(registerDto.getLastName());
        person.setFirstName(registerDto.getFirstName());
        person.setMiddleName(registerDto.getMiddleName());
        person.setDateOfBirth(registerDto.getDateOfBirth());
        personRepository.save(person);

        String token = generateToken();

        VerificationCode code = new VerificationCode();
        code.setAccount(account);
        code.setToken(token);
        verificationCodeRepository.save(code);

        logger.debug("Подтвердите регистрацию для пользователя '" +
                account.getUsername() +
                "' по ссылке " +
                "http://localhost:8080/activate/" +
                token
        );

//        TODO: найти почтовый сервис, который не будет банить исходящие письма с одинаковым
//         содержанием (яндекс помечает как спам)
//        mailSenderService.sendEmailConfirmationCode(person, "localhost:8080", token);
    }

    @Override
    public Account activateAccount(String token) {
        VerificationCode code = verificationCodeRepository.findByToken(token);

        if (code == null) {
//            bad token
            return null;
        } else if (code.getCreatedAt().isAfter(ZonedDateTime.now().plusDays(2))) {
//            expired token
//            удалить токен и связанный с ним неактивированный аккаунт
            verificationCodeRepository.delete(code);
            accountRepository.delete(code.getAccount());

            return null;
        } else {
//            good token
//            активировать аккаунт и удалить токен
            Account account = code.getAccount();
            account.setEmailVerified(true);
            accountRepository.save(account);

            verificationCodeRepository.delete(code);

            return account;
        }
    }

    private void checkUniqueConstraints(RegisterDto registerDto) {
        List<String> errors = new ArrayList<>();

        requireUsernameIsAvailable(registerDto, errors);
        requireEmailIsAvailable(registerDto, errors);

        if (!errors.isEmpty()) {
            throw new RegistrationUniqueConstraintsException(errors);
        }
    }

    private void requireUsernameIsAvailable(RegisterDto registerDto, List<String> errors) {
        if (accountRepository.findByUsername(registerDto.getUsername()) != null) {
            errors.add("errorUsername");
        }
    }

    private void requireEmailIsAvailable(RegisterDto registerDto, List<String> errors) {
        if (personRepository.findByEmail(registerDto.getEmail()) != null) {
            errors.add("errorEmail");
        }
    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }
}
