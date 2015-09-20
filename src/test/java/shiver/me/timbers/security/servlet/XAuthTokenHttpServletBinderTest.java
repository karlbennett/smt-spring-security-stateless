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

import org.junit.Before;
import org.junit.Test;
import shiver.me.timbers.security.token.TokenFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static shiver.me.timbers.data.random.RandomStrings.someAlphaNumericString;
import static shiver.me.timbers.data.random.RandomStrings.someAlphaString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class XAuthTokenHttpServletBinderTest {

    private static final String X_AUTH_TOKEN = "X-AUTH-TOKEN";

    private TokenFactory<Object> tokenFactory;
    private XAuthTokenHttpServletBinder<Object> binder;
    private Object token;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        tokenFactory = mock(TokenFactory.class);
        token = new Object();
        binder = new XAuthTokenHttpServletBinder<>(tokenFactory);
    }

    @Test
    public void Can_add_a_token_to_a_response() throws Exception {

        final HttpServletResponse response = mock(HttpServletResponse.class);

        final String tokenString = someString();

        // Given
        given(tokenFactory.create(token)).willReturn(tokenString);

        // When
        binder.add(response, token);

        // Then
        verify(response).addHeader(X_AUTH_TOKEN, tokenString);
        verify(response).addCookie(new EqualCookie(X_AUTH_TOKEN, tokenString, "/"));
    }

    @Test
    public void Can_configure_the_cookies_path() throws Exception {

        final String path = someAlphaNumericString();
        final HttpServletResponse response = mock(HttpServletResponse.class);

        final String tokenString = someString();

        // Given
        given(tokenFactory.create(token)).willReturn(tokenString);

        // When
        binder.withCookiePath(path);
        binder.add(response, token);

        // Then
        verify(response).addHeader(X_AUTH_TOKEN, tokenString);
        verify(response).addCookie(new EqualCookie(X_AUTH_TOKEN, tokenString, path));
    }

    @Test
    public void Can_retrieve_an_authentication_from_a_request_header() throws Exception {

        final HttpServletRequest request = mock(HttpServletRequest.class);
        final String token = someString();

        final Object expected = new Object();

        // Given
        given(request.getHeader(X_AUTH_TOKEN)).willReturn(token);
        given(request.getCookies()).willReturn(null);
        given(tokenFactory.parse(token)).willReturn(expected);

        // When
        final Object actual = binder.retrieve(request);

        // Then
        assertThat(actual, equalTo(expected));
    }

    @Test
    public void Can_retrieve_an_authentication_from_a_request_cookie() throws Exception {

        final HttpServletRequest request = mock(HttpServletRequest.class);
        final String token = someString();

        final Object expected = new Object();

        // Given
        given(request.getHeader(X_AUTH_TOKEN)).willReturn(null);
        given(request.getCookies()).willReturn(new Cookie[]{
            new Cookie(someAlphaString(), someAlphaString()),
            new Cookie(X_AUTH_TOKEN, token)
        });
        given(tokenFactory.parse(token)).willReturn(expected);

        // When
        final Object actual = binder.retrieve(request);

        // Then
        assertThat(actual, equalTo(expected));
    }

    @Test
    public void Return_no_authentication_if_no_token_supplied() throws Exception {

        final HttpServletRequest request = mock(HttpServletRequest.class);

        // Given
        given(request.getHeader(X_AUTH_TOKEN)).willReturn(null);
        given(request.getCookies()).willReturn(null);

        // When
        final Object actual = binder.retrieve(request);

        // Then
        assertThat(actual, nullValue());
        verifyZeroInteractions(tokenFactory);
    }

    @Test
    public void Return_no_authentication_if_no_token_supplied_in_cookies() throws Exception {

        final HttpServletRequest request = mock(HttpServletRequest.class);

        // Given
        given(request.getHeader(X_AUTH_TOKEN)).willReturn(null);
        given(request.getCookies()).willReturn(new Cookie[0]);

        // When
        final Object actual = binder.retrieve(request);

        // Then
        assertThat(actual, nullValue());
        verifyZeroInteractions(tokenFactory);
    }
}
