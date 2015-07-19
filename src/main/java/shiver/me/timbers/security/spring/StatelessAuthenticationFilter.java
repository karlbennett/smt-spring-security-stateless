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
import shiver.me.timbers.security.servlet.AuthenticationHttpServletRequestBinder;
import shiver.me.timbers.security.servlet.HttpServletRequestBinder;
import shiver.me.timbers.security.token.BasicJwtTokenFactory;
import shiver.me.timbers.security.token.TokenFactory;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author Karl Bennett
 */
public class StatelessAuthenticationFilter extends GenericFilterBean {

    private final HttpServletRequestBinder<Authentication> authenticationFactory;
    private final SecurityContextHolder contextHolder;

    public StatelessAuthenticationFilter(String secret) {
        this(new BasicJwtTokenFactory(secret));
    }

    public StatelessAuthenticationFilter(TokenFactory tokenFactory) {
        this(new AuthenticationHttpServletRequestBinder(tokenFactory), new StaticSecurityContextHolder());
    }

    public StatelessAuthenticationFilter(
        HttpServletRequestBinder<Authentication> authenticationFactory,
        SecurityContextHolder contextHolder
    ) {
        this.authenticationFactory = authenticationFactory;
        this.contextHolder = contextHolder;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
        throws IOException, ServletException {
        contextHolder.getContext().setAuthentication(authenticationFactory.retrieve((HttpServletRequest) request));
        filterChain.doFilter(request, response);
    }
}
