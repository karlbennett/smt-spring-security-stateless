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

import org.springframework.security.core.Authentication;
import org.springframework.web.filter.GenericFilterBean;
import shiver.me.timbers.security.servlet.AuthenticationHttpServletBinder;
import shiver.me.timbers.security.servlet.HttpServletBinder;
import shiver.me.timbers.security.token.JwtTokenFactory;
import shiver.me.timbers.security.token.TokenFactory;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * This filter will check every request for the authorised token and if it finds it authorise the request.
 *
 * @author Karl Bennett
 */
public class StatelessAuthenticationFilter extends GenericFilterBean {

    private final HttpServletBinder<Authentication> httpServletBinder;
    private final SecurityContextHolder contextHolder;
    private final ExceptionMapper<ServletException> exceptionMapper;

    public StatelessAuthenticationFilter(String secret) {
        this(String.class, secret, new AuthenticatedAuthenticationConverter());
    }

    public <T> StatelessAuthenticationFilter(
        Class<T> type,
        String secret,
        AuthenticationConverter<T> authenticationConverter
    ) {
        this(new JwtTokenFactory<>(type, secret), authenticationConverter);
    }

    public <T> StatelessAuthenticationFilter(
        TokenFactory<T> tokenFactory,
        AuthenticationConverter<T> authenticationConverter
    ) {
        this(new AuthenticationHttpServletBinder<>(tokenFactory, authenticationConverter));
    }

    public StatelessAuthenticationFilter(HttpServletBinder<Authentication> httpServletBinder) {
        this(httpServletBinder, new StaticSecurityContextHolder(), null);
    }

    public StatelessAuthenticationFilter(
        HttpServletBinder<Authentication> httpServletBinder,
        SecurityContextHolder contextHolder,
        ExceptionMapper<ServletException> exceptionMapper
    ) {
        this.httpServletBinder = httpServletBinder;
        this.contextHolder = contextHolder;
        this.exceptionMapper = exceptionMapper;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
        throws IOException, ServletException {
        try {
            contextHolder.getContext().setAuthentication(httpServletBinder.retrieve((HttpServletRequest) request));
        } catch (Throwable e) {
            exceptionMapper.throwMapped(e);
        }
        filterChain.doFilter(request, response);
    }
}
