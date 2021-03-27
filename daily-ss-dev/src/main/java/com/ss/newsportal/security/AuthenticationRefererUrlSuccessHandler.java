package com.ss.newsportal.security;

import org.springframework.core.log.LogMessage;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class AuthenticationRefererUrlSuccessHandler extends AbstractAuthenticationTargetUrlRequestHandler
        implements AuthenticationSuccessHandler {

    public static final String REFERER_URL = "AuthenticationRefererUrlSuccessHandler.REFERER_URL";

    public AuthenticationRefererUrlSuccessHandler() {
    }

    public AuthenticationRefererUrlSuccessHandler(String defaultTargetUrl) {
        setDefaultTargetUrl(defaultTargetUrl);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        handle(request, response, authentication);
        clearAuthenticationAttributes(request);
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response) {
        String targetUrl = null;

        HttpSession session = request.getSession(false);
        if (session != null) {
            String refererUrl = (String) session.getAttribute(REFERER_URL);
            if (refererUrl != null) {
                targetUrl = refererUrl;
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug(LogMessage.format("Using url %s from referer attribute", targetUrl));
                }
            }
        }

        if (targetUrl == null) {
            targetUrl = getDefaultTargetUrl();
            if (this.logger.isDebugEnabled()) {
                this.logger.debug(LogMessage.format("Using default target url %s", targetUrl));
            }
        }

        return targetUrl;
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
            session.removeAttribute(REFERER_URL);
        }
    }
}
