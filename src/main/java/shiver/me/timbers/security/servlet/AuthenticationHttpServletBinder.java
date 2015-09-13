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
import shiver.me.timbers.security.spring.AuthenticationConverter;
import shiver.me.timbers.security.token.TokenFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Karl Bennett
 */
public class AuthenticationHttpServletBinder<T> implements HttpServletBinder<Authentication> {

    private final HttpServletBinder<T> httpServletBinder;
    private final AuthenticationConverter<T> authenticationConverter;

    public AuthenticationHttpServletBinder(
        TokenFactory<T> tokenFactory,
        AuthenticationConverter<T> authenticationConverter
    ) {
        this(new XAuthTokenHttpServletBinder<>(tokenFactory), authenticationConverter);
    }

    public AuthenticationHttpServletBinder(
        HttpServletBinder<T> httpServletBinder,
        AuthenticationConverter<T> authenticationConverter
    ) {
        this.httpServletBinder = httpServletBinder;
        this.authenticationConverter = authenticationConverter;
    }

    @Override
    public void add(HttpServletResponse response, Authentication authentication) throws Exception {
        httpServletBinder.add(response, authenticationConverter.convert(authentication));
    }

    @Override
    public Authentication retrieve(HttpServletRequest request) throws Exception {
        return authenticationConverter.convert(httpServletBinder.retrieve(request));
    }
}
