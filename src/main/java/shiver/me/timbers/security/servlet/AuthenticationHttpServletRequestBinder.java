/*
 *    Copyright 2015 Karl Bennett
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package shiver.me.timbers.security.servlet;

import org.springframework.security.core.Authentication;
import shiver.me.timbers.security.spring.AuthenticatedAuthenticationFactory;
import shiver.me.timbers.security.spring.AuthenticationFactory;
import shiver.me.timbers.security.token.TokenFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Karl Bennett
 */
public class AuthenticationHttpServletRequestBinder implements HttpServletRequestBinder<Authentication> {

    private final HttpServletRequestBinder<String> httpServletRequestBinder;
    private final AuthenticationFactory authenticationFactory;

    public AuthenticationHttpServletRequestBinder(TokenFactory tokenFactory) {
        this(tokenFactory, new AuthenticatedAuthenticationFactory());
    }

    public AuthenticationHttpServletRequestBinder(
        TokenFactory tokenFactory,
        AuthenticationFactory authenticationFactory
    ) {
        this(new XAuthTokenHttpServletRequestBinder(tokenFactory), authenticationFactory);
    }

    public AuthenticationHttpServletRequestBinder(
        HttpServletRequestBinder<String> httpServletRequestBinder,
        AuthenticationFactory authenticationFactory
    ) {
        this.httpServletRequestBinder = httpServletRequestBinder;
        this.authenticationFactory = authenticationFactory;
    }

    @Override
    public void add(HttpServletResponse response, Authentication authentication) {
        httpServletRequestBinder.add(response, authentication.getName());
    }

    @Override
    public Authentication retrieve(HttpServletRequest request) {

        final String username = httpServletRequestBinder.retrieve(request);

        if (username != null) {
            return authenticationFactory.create(username);
        }

        return null;
    }
}
