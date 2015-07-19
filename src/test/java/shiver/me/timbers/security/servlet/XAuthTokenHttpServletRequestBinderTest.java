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
import static shiver.me.timbers.data.random.RandomStrings.someAlphaString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class XAuthTokenHttpServletRequestBinderTest {

    private static final String X_AUTH_TOKEN = "X-AUTH-TOKEN";

    private TokenFactory tokenFactory;
    private XAuthTokenHttpServletRequestBinder binder;

    @Before
    public void setUp() {
        tokenFactory = mock(TokenFactory.class);
        binder = new XAuthTokenHttpServletRequestBinder(tokenFactory);
    }

    @Test
    public void Can_add_a_token_to_a_response() {

        final HttpServletResponse response = mock(HttpServletResponse.class);

        final String subject = someString();
        final String token = someString();

        // Given
        given(tokenFactory.create(subject)).willReturn(token);

        // When
        binder.add(response, subject);

        // Then
        verify(response).addHeader(X_AUTH_TOKEN, token);
        verify(response).addCookie(new EqualCookie(X_AUTH_TOKEN, token));
    }

    @Test
    public void Can_retrieve_an_authentication_from_a_request_header() {

        final HttpServletRequest request = mock(HttpServletRequest.class);

        final String token = someString();
        final String expected = someString();

        // Given
        given(request.getHeader(X_AUTH_TOKEN)).willReturn(token);
        given(request.getCookies()).willReturn(null);
        given(tokenFactory.parse(token)).willReturn(expected);

        // When
        final String actual = binder.retrieve(request);

        // Then
        assertThat(actual, equalTo(expected));
    }

    @Test
    public void Can_retrieve_an_authentication_from_a_request_cookie() {

        final HttpServletRequest request = mock(HttpServletRequest.class);

        final String token = someString();
        final String expected = someString();

        // Given
        given(request.getHeader(X_AUTH_TOKEN)).willReturn(null);
        given(request.getCookies()).willReturn(new Cookie[]{
            new Cookie(someAlphaString(), someAlphaString()),
            new Cookie(X_AUTH_TOKEN, token)
        });
        given(tokenFactory.parse(token)).willReturn(expected);

        // When
        final String actual = binder.retrieve(request);

        // Then
        assertThat(actual, equalTo(expected));
    }

    @Test
    public void Return_no_authentication_if_no_token_supplied() {

        final HttpServletRequest request = mock(HttpServletRequest.class);

        // Given
        given(request.getHeader(X_AUTH_TOKEN)).willReturn(null);
        given(request.getCookies()).willReturn(null);

        // When
        final String actual = binder.retrieve(request);

        // Then
        assertThat(actual, nullValue());
        verifyZeroInteractions(tokenFactory);
    }

    @Test
    public void Return_no_authentication_if_no_token_supplied_in_cookies() {

        final HttpServletRequest request = mock(HttpServletRequest.class);

        // Given
        given(request.getHeader(X_AUTH_TOKEN)).willReturn(null);
        given(request.getCookies()).willReturn(new Cookie[0]);

        // When
        final String actual = binder.retrieve(request);

        // Then
        assertThat(actual, nullValue());
        verifyZeroInteractions(tokenFactory);
    }
}
