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
public class AuthenticationNameHttpServletBinder implements HttpServletBinder<Authentication> {

    private final HttpServletBinder<String> httpServletBinder;
    private final AuthenticationFactory<String> authenticationFactory;

    public AuthenticationNameHttpServletBinder(TokenFactory<String> tokenFactory) {
        this(tokenFactory, new AuthenticatedAuthenticationFactory());
    }

    public AuthenticationNameHttpServletBinder(
        TokenFactory<String> tokenFactory,
        AuthenticationFactory<String> authenticationFactory
    ) {
        this(new XAuthTokenHttpServletBinder<>(tokenFactory), authenticationFactory);
    }

    public AuthenticationNameHttpServletBinder(
        HttpServletBinder<String> httpServletBinder,
        AuthenticationFactory<String> authenticationFactory
    ) {
        this.httpServletBinder = httpServletBinder;
        this.authenticationFactory = authenticationFactory;
    }

    @Override
    public void add(HttpServletResponse response, Authentication authentication) {
        httpServletBinder.add(response, authentication.getName());
    }

    @Override
    public Authentication retrieve(HttpServletRequest request) {

        final String subject = httpServletBinder.retrieve(request);

        if (subject != null) {
            return authenticationFactory.create(subject);
        }

        return null;
    }
}
