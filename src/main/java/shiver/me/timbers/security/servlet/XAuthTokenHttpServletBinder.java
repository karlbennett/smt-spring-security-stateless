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

import shiver.me.timbers.security.token.TokenFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Karl Bennett
 */
public class XAuthTokenHttpServletBinder<T> implements HttpServletBinder<T> {

    private static final String X_AUTH_TOKEN = "X-AUTH-TOKEN";
    private final TokenFactory<T> tokenFactory;

    public XAuthTokenHttpServletBinder(TokenFactory<T> tokenFactory) {
        this.tokenFactory = tokenFactory;
    }

    @Override
    public void add(HttpServletResponse response, T subject) throws Exception {

        final String token = tokenFactory.create(subject);

        response.addHeader(X_AUTH_TOKEN, token);
        final Cookie cookie = new Cookie(X_AUTH_TOKEN, token);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    @Override
    public T retrieve(HttpServletRequest request) throws Exception {

        final String cookieToken = findToken(request);

        if (cookieToken != null) {
            return tokenFactory.parse(cookieToken);
        }

        return null;
    }

    private static String findToken(HttpServletRequest request) {

        final String headerToken = request.getHeader(X_AUTH_TOKEN);

        if (headerToken != null) {
            return headerToken;
        }

        final Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (X_AUTH_TOKEN.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }
}
