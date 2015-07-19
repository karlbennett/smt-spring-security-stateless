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
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import shiver.me.timbers.security.servlet.AuthenticationHttpServletBinder;
import shiver.me.timbers.security.servlet.HttpServletBinder;
import shiver.me.timbers.security.token.BasicJwtTokenFactory;
import shiver.me.timbers.security.token.TokenFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Karl Bennett
 */
public class StatelessAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final HttpServletBinder<Authentication> httpServletBinder;
    private final SimpleUrlAuthenticationSuccessHandler delegate;

    public StatelessAuthenticationSuccessHandler(String secret, String defaultTargetUri) {
        this(new BasicJwtTokenFactory(secret), defaultTargetUri);
    }

    public StatelessAuthenticationSuccessHandler(TokenFactory tokenFactory, String defaultTargetUri) {
        this(new AuthenticationHttpServletBinder(tokenFactory), new SimpleUrlAuthenticationSuccessHandler(defaultTargetUri));
    }

    public StatelessAuthenticationSuccessHandler(
        HttpServletBinder<Authentication> httpServletBinder,
        SimpleUrlAuthenticationSuccessHandler delegate
    ) {
        this.httpServletBinder = httpServletBinder;
        this.delegate = delegate;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        httpServletBinder.add(response, authentication);

        delegate.onAuthenticationSuccess(request, response, authentication);
    }
}
