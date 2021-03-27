package com.ss.newsportal.security;

import org.springframework.core.log.LogMessage;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.*;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collection;
import java.util.UUID;

public class ReplaceUserFilter extends GenericFilterBean {
    public static final String REPLACE_USER_USERNAME_KEY = "username";
    public static final String DEFAULT_ANONYMOUS_PRINCIPAL = "anonymousUser";

    private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource =
            new WebAuthenticationDetailsSource();

    private RequestMatcher replaceUserMatcher = createMatcher("/replace_user");

    private String usernameParameter = REPLACE_USER_USERNAME_KEY;

    private String refererUrlAttributeName = null;

    private String successUrl;

    private String failureUrl;

    private UserDetailsService userDetailsService;

    private UserDetailsChecker userDetailsChecker = new AccountStatusUserDetailsChecker();

    private AuthenticationSuccessHandler successHandler;

    private AuthenticationFailureHandler failureHandler;

    private String anonymousKey;

    private Object anonymousPrincipal = DEFAULT_ANONYMOUS_PRINCIPAL;

    private Collection<? extends GrantedAuthority> anonymousAuthorities =
            AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS");


    @Override
    public void afterPropertiesSet() throws ServletException {
        Assert.notNull(this.userDetailsService, "userDetailsService must be specified");
        Assert.isTrue(this.successHandler != null || this.successUrl != null,
                "You must set either a successHandler or the targetUrl");

        if (this.successUrl != null) {
            Assert.isNull(this.successHandler, "You cannot set both successHandler and successUrl");
            this.successHandler = new SimpleUrlAuthenticationSuccessHandler(this.successUrl);
        }

        if (this.failureHandler == null) {
            this.failureHandler = (this.failureUrl != null)
                    ? new SimpleUrlAuthenticationFailureHandler(this.failureUrl)
                    : new SimpleUrlAuthenticationFailureHandler();
        } else {
            Assert.isNull(this.failureUrl, "You cannot set both a failureUrl and a failureHandler");
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // check for replace or exit request
        if (requiresReplaceUser(request)) {
            // if set, attempt replace
            try {
                Authentication targetUser = attemptReplaceUser(request);
                // update the current context to the new target user
                SecurityContextHolder.getContext().setAuthentication(targetUser);

                // save referer url to session attribute for redirect to previous page
                // by custom AuthenticationSuccessHandler
                saveRefererUrlToSession(request);

                // redirect to target url
                this.successHandler.onAuthenticationSuccess(request, response, targetUser);
            } catch (AuthenticationException ex) {
                this.logger.debug("Failed to replace user", ex);
                this.failureHandler.onAuthenticationFailure(request, response, ex);
            }
            return;
        }

        chain.doFilter(request, response);
    }


    /**
     * Attempt to replace user. If the user does not exist or is not active,
     * return null.
     *
     * @return The new <code>Authentication</code> request if successfully replaced to
     * another user, <code>null</code> otherwise.
     * @throws UsernameNotFoundException   If the target user is not found.
     * @throws LockedException             if the account is locked.
     * @throws DisabledException           If the target user is disabled.
     * @throws AccountExpiredException     If the target user account is expired.
     * @throws CredentialsExpiredException If the target user credentials are expired.
     */
    protected Authentication attemptReplaceUser(HttpServletRequest request) throws AuthenticationException {
        AbstractAuthenticationToken targetUserRequest;
        String username = request.getParameter(this.usernameParameter);
        username = (username != null) ? username : "";
        this.logger.debug(LogMessage.format("Attempt to replace to user [%s]", username));

        // assumed blank username using for replace auth with AnonymousAuth
        if (username.isBlank()) {
            targetUserRequest = createReplaceUserAnonymousToken(request);
        } else {
            UserDetails targetUser = this.userDetailsService.loadUserByUsername(username);
            this.userDetailsChecker.check(targetUser);
            // OK, create the target user token
            targetUserRequest = createReplaceUserUsernamePasswordToken(request, targetUser);
            this.logger.debug(LogMessage.format("Replace User Token [%s]", targetUserRequest));
        }

        return targetUserRequest;
    }

    /**
     * Create a new user token for target user.
     *
     * @param request    The http servlet request.
     * @param targetUser The target user
     * @return The authentication token
     */
    private UsernamePasswordAuthenticationToken createReplaceUserUsernamePasswordToken(
            HttpServletRequest request,
            UserDetails targetUser) {

        UsernamePasswordAuthenticationToken targetUserToken =
                new UsernamePasswordAuthenticationToken(targetUser, null, targetUser.getAuthorities());
        targetUserToken.setDetails(this.authenticationDetailsSource.buildDetails(request));

        return targetUserToken;
    }

    /**
     * Create a new anonymous token.
     *
     * @param request The http servlet request.
     * @return The authentication token
     */
    private AnonymousAuthenticationToken createReplaceUserAnonymousToken(HttpServletRequest request) {
        AnonymousAuthenticationToken anonymousToken =
                new AnonymousAuthenticationToken(getAnonymousKey(), this.anonymousPrincipal, this.anonymousAuthorities);
        anonymousToken.setDetails(this.authenticationDetailsSource.buildDetails(request));

        return anonymousToken;
    }

    private void saveRefererUrlToSession(HttpServletRequest request) {
        if (this.refererUrlAttributeName != null) {
            HttpSession session = request.getSession(false);
            String refererUrl = request.getHeader("referer");
            if (session != null && refererUrl != null) {
                session.setAttribute(this.refererUrlAttributeName, refererUrl);
                if (logger.isDebugEnabled()) {
                    logger.debug(LogMessage.format("Saving referer url [%s] to current session", refererUrl));
                }
            }
        }
    }

    /**
     * Checks the request URI for the presence of <tt>replaceUserUrl</tt>.
     *
     * @param request The http servlet request
     * @return <code>true</code> if the request requires a replace, <code>false</code>
     * otherwise.
     * @see #setReplaceUserUrl(String)
     */
    protected boolean requiresReplaceUser(HttpServletRequest request) {
        return this.replaceUserMatcher.matches(request);
    }

    /**
     * Sets the authentication data access object.
     *
     * @param userDetailsService The <tt>UserDetailService</tt> which will be used to load
     *                           information for the user that is being replaced to.
     */
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * Set the URL to respond to replace user processing. This is a shortcut for
     * {@link #setReplaceUserMatcher(RequestMatcher)}
     *
     * @param replaceUserUrl The replace user URL.
     */
    public void setReplaceUserUrl(String replaceUserUrl) {
        Assert.isTrue(UrlUtils.isValidRedirectUrl(replaceUserUrl),
                "replaceUserUrl cannot be empty and must be a valid redirect URL");
        this.replaceUserMatcher = createMatcher(replaceUserUrl);
    }

    /**
     * Set the matcher to respond to replace user processing.
     *
     * @param replaceUserMatcher The replace user matcher.
     */
    public void setReplaceUserMatcher(RequestMatcher replaceUserMatcher) {
        Assert.notNull(replaceUserMatcher, "replaceUserMatcher cannot be null");
        this.replaceUserMatcher = replaceUserMatcher;
    }

    /**
     * Sets the URL to go to after a successful replace user request. Use
     * {@link #setSuccessHandler(AuthenticationSuccessHandler) setSuccessHandler} instead
     * if you need more customized behaviour.
     *
     * @param successUrl The target url.
     */
    public void setSuccessUrl(String successUrl) {
        this.successUrl = successUrl;
    }

    /**
     * Used to define custom behaviour on a successful replace user.
     * <p>
     * Can be used instead of setting <tt>successUrl</tt>.
     */
    public void setSuccessHandler(AuthenticationSuccessHandler successHandler) {
        Assert.notNull(successHandler, "successHandler cannot be null");
        this.successHandler = successHandler;
    }

    /**
     * Sets the URL to which a user should be redirected if the replace fails. For example,
     * this might happen because the account they are attempting to replace to is invalid
     * (the user doesn't exist, account is locked etc).
     * <p>
     * If not set, an error message will be written to the response.
     * <p>
     * Use {@link #setFailureHandler(AuthenticationFailureHandler) failureHandler} instead
     * if you need more customized behaviour.
     *
     * @param failureUrl the url to redirect to.
     */
    public void setFailureUrl(String failureUrl) {
        Assert.isTrue(UrlUtils.isValidRedirectUrl(failureUrl), "failureUrl must be a valid redirect URL");
        this.failureUrl = failureUrl;
    }

    /**
     * Used to define custom behaviour when a replace fails.
     * <p>
     * Can be used instead of setting <tt>failureUrl</tt>.
     */
    public void setFailureHandler(AuthenticationFailureHandler failureHandler) {
        Assert.notNull(failureHandler, "failureHandler cannot be null");
        this.failureHandler = failureHandler;
    }

    /**
     * Sets the {@link UserDetailsChecker} that is called on the target user whenever the
     * user is replaced.
     *
     * @param userDetailsChecker the {@link UserDetailsChecker} that checks the status of
     *                           the user that is being replaced to. Defaults to
     *                           {@link AccountStatusUserDetailsChecker}.
     */
    public void setUserDetailsChecker(UserDetailsChecker userDetailsChecker) {
        this.userDetailsChecker = userDetailsChecker;
    }

    /**
     * Allows the parameter containing the username to be customized.
     *
     * @param usernameParameter the parameter name. Defaults to {@code username}
     */
    public void setUsernameParameter(String usernameParameter) {
        this.usernameParameter = usernameParameter;
    }

    public void setRefererUrlAttributeName(String refererUrlAttributeName) {
        this.refererUrlAttributeName = refererUrlAttributeName;
    }

    protected String getAnonymousKey() {
        if (this.anonymousKey == null) {
            this.anonymousKey = UUID.randomUUID().toString();
        }

        return this.anonymousKey;
    }

    private static RequestMatcher createMatcher(String pattern) {
        return new AntPathRequestMatcher(pattern, "POST", true, new UrlPathHelper());
    }
}
