package com.ss.newsportal.service.auth;

import com.ss.newsportal.dto.RegisterDto;
import com.ss.newsportal.entity.Account;
import com.ss.newsportal.exception.RegistrationUniqueConstraintsException;

public interface RegistrationService {
    /**
     * Регистрирует нового пользователя на портале. Может выбрасывать {@link RegistrationUniqueConstraintsException}
     * с ошибками, в случае нарушения юникальности ключевых полей.
     * При успешной регистрации сохраняет токен в сущности {@link com.ss.newsportal.entity.VerificationCode} необходимый
     * для активации зарегистрированного аккаунта.
     *
     * @param registerDto информация о новом пользователе
     */
    void registerAccount(RegisterDto registerDto);

    /**
     * Активирует аккаунт связанный с переданным токеном.
     *
     * @param token токен созданный при регистрации
     * @return аккаунт, который связан с токеном, или null если получен невалидный и истекший токен
     */
    Account activateAccount(String token);
}
