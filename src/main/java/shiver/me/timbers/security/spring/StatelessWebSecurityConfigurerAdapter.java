/*
 * Copyright 2015 Karl Bennett
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package shiver.me.timbers.security.spring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import shiver.me.timbers.security.servlet.AuthenticationHttpServletBinder;
import shiver.me.timbers.security.servlet.XAuthTokenHttpServletBinder;
import shiver.me.timbers.security.token.JwtTokenParser;
import shiver.me.timbers.security.token.TokenParser;

import javax.servlet.ServletException;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

/**
 * Provides a convenient base class for configuring the Spring Security stateless components. The implementation allows
 * customization by overriding methods.
 *
 * @author Karl Bennett
 */
@EnableWebMvcSecurity
public class StatelessWebSecurityConfigurerAdapter<T> extends WebSecurityConfigurerAdapter {

    @Value("${spring.stateless.security.secret}")
    private String secret;

    private boolean customTokenParser = true;
    private boolean customXAuthTokenHttpServletBinder = true;

    @Override
    protected final void configure(HttpSecurity http) throws Exception {

        final TokenParser<T> tokenParser = tokenParser(secret);
        final XAuthTokenHttpServletBinder<T> xAuthTokenHttpServletBinder = xAuthTokenHttpServletBinder(tokenParser);
        final AuthenticationHttpServletBinder<T> authenticationHttpServletBinder = authenticationHttpServletBinder(
            xAuthTokenHttpServletBinder,
            authenticationConverter()
        );
        final ExceptionMapper<ServletException> exceptionMapper = servletExceptionExceptionMapper();

        if (!customTokenParser) {
            configure((JwtTokenParser) tokenParser);
        }
        if (!customXAuthTokenHttpServletBinder) {
            configure(xAuthTokenHttpServletBinder);
        }

        final StatelessAuthenticationSuccessHandler statelessAuthenticationSuccessHandler =
            statelessAuthenticationSuccessHandler(
                authenticationHttpServletBinder,
                simpleUrlAuthenticationSuccessHandler(defaultSuccessUrl()),
                exceptionMapper
            );
        final StatelessAuthenticationFilter statelessAuthenticationFilter = statelessAuthenticationFilter(
            authenticationHttpServletBinder,
            exceptionMapper
        );

        // Make Spring Security stateless. This means no session will be created by Spring Security, nor will it use any
        // previously existing session.
        http.sessionManagement().sessionCreationPolicy(STATELESS);
        // The CSRF prevention is disabled because it requires the session, which of course is not available in a
        // stateless application. It also greatly complicates the requirements for the sign in POST request.
        http.csrf().disable();
        // Override the sign in success handler with the stateless implementation.
        http.formLogin().successHandler(statelessAuthenticationSuccessHandler);
        // Add our stateless authentication filter before the default sign in filter. The default sign in filter is
        // still used for the initial sign in, but once a user is authenticated we need to by pass it.
        http.addFilterBefore(statelessAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        configureFurther(http);
    }

    /**
     * Developers should override this method when changing the instance of {@link AuthenticationHttpServletBinder}.
     */
    protected AuthenticationHttpServletBinder<T> authenticationHttpServletBinder(
        XAuthTokenHttpServletBinder<T> xAuthTokenHttpServletBinder,
        AuthenticationConverter<T> authenticationConverter
    ) {
        return new AuthenticationHttpServletBinder<>(xAuthTokenHttpServletBinder, authenticationConverter);
    }

    /**
     * Developers should override this method when changing the instance of {@link ExceptionMapper}.
     * The default instance is {@link ServletExceptionMapper}.
     */
    protected ExceptionMapper<ServletException> servletExceptionExceptionMapper() {
        return new ServletExceptionMapper();
    }

    /**
     * Developers should override this method when changing the instance of {@link StatelessAuthenticationSuccessHandler}.
     */
    protected StatelessAuthenticationSuccessHandler statelessAuthenticationSuccessHandler(
        AuthenticationHttpServletBinder<T> httpServletBinder,
        SimpleUrlAuthenticationSuccessHandler simpleUrlAuthenticationSuccessHandler,
        ExceptionMapper<ServletException> exceptionMapper
    ) {
        return new StatelessAuthenticationSuccessHandler(
            httpServletBinder,
            simpleUrlAuthenticationSuccessHandler,
            exceptionMapper
        );
    }

    /**
     * Developers should override this method when changing the instance of {@link StatelessAuthenticationFilter}.
     */
    protected StatelessAuthenticationFilter statelessAuthenticationFilter(
        AuthenticationHttpServletBinder<T> httpServletBinder,
        ExceptionMapper<ServletException> exceptionMapper
    ) {
        return new StatelessAuthenticationFilter(httpServletBinder, contextHolder(), exceptionMapper);
    }

    /**
     * Developers should override this method when changing the instance of {@link XAuthTokenHttpServletBinder}.
     */
    protected XAuthTokenHttpServletBinder<T> xAuthTokenHttpServletBinder(TokenParser<T> tokenParser) {
        customXAuthTokenHttpServletBinder = false;
        return new XAuthTokenHttpServletBinder<>(tokenParser);
    }

    /**
     * Developers should override this method when changing the instance of {@link TokenParser}.
     * The default instance is {@link JwtTokenParser}.
     */
    @SuppressWarnings("unchecked")
    protected TokenParser<T> tokenParser(String secret) {
        customTokenParser = false;
        return (TokenParser<T>) new JwtTokenParser<>(String.class, secret);
    }

    /**
     * Developers should override this method when changing the instance of {@link AuthenticationConverter}.
     * The default instance is {@link AuthenticatedAuthenticationConverter}.
     */
    @SuppressWarnings("unchecked")
    protected AuthenticationConverter<T> authenticationConverter() {
        return (AuthenticationConverter<T>) new AuthenticatedAuthenticationConverter();
    }

    /**
     * Developers should override this method when changing the instance of {@link SimpleUrlAuthenticationSuccessHandler}.
     */
    protected SimpleUrlAuthenticationSuccessHandler simpleUrlAuthenticationSuccessHandler(String defaultTargetUrl) {
        return new SimpleUrlAuthenticationSuccessHandler(defaultTargetUrl);
    }

    /**
     * Developers should override this method when the URL path that the user will be redirected to after a successful
     * login.
     */
    protected String defaultSuccessUrl() {
        return "/";
    }

    /**
     * Developers should override this method when changing the instance of {@link SecurityContextHolder}.
     * The default instance is {@link StaticSecurityContextHolder}.
     */
    protected SecurityContextHolder contextHolder() {
        return new StaticSecurityContextHolder();
    }

    /**
     * Override this method to configure the default {@link JwtTokenParser} instance.
     */
    protected void configure(JwtTokenParser tokenParser) {
    }

    /**
     * Override this method to configure the default {@link XAuthTokenHttpServletBinder} instance.
     */
    protected void configure(XAuthTokenHttpServletBinder<T> xAuthTokenHttpServletBinder) {
    }

    /**
     * Override this method to further configure the {@link HttpSecurity}. This is where you would setup the
     * login/logout pages, logout url, and so on.
     * NOTE: Do not use the {@code http.formLogin().defaultSuccessUrl(...)} method. Doing this will completely override
     * the stateless authentication. Override the {@link #defaultSuccessUrl()} method instead.
     */
    protected void configureFurther(HttpSecurity http) throws Exception {
    }
}
