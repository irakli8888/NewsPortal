package com.ss.newsportal.config;

import com.ss.newsportal.security.AuthenticationRefererUrlSuccessHandler;
import com.ss.newsportal.security.ReplaceUserFilter;
import com.ss.newsportal.service.auth.ApplicationUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Configuration
@Profile("with-replace-user")
public class ReplaceUserConfiguration {
    private final ApplicationUserDetailsService userDetailsService;
    private final AuthenticationRefererUrlSuccessHandler authenticationSuccessHandler;

    @Bean
    public ReplaceUserFilter replaceUserFilter() {
        ReplaceUserFilter replaceUserFilter = new ReplaceUserFilter();
        replaceUserFilter.setReplaceUserUrl("/replace_user");
        replaceUserFilter.setUserDetailsService(userDetailsService);
        replaceUserFilter.setSuccessHandler(authenticationSuccessHandler);
        replaceUserFilter.setRefererUrlAttributeName(AuthenticationRefererUrlSuccessHandler.REFERER_URL);
        return replaceUserFilter;
    }

    public List<String> replaceUsernameList() {
        return List.of("WRU-user", "WRU-author", "WRU-admin");
    }

    @Bean
    @Qualifier("replaceUserList")
    public List<UserDetails> replaceUserList() {
        return replaceUsernameList().stream()
                .map(userDetailsService::loadUserByUsername)
                .collect(Collectors.toList());
    }
}
