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
import org.springframework.security.core.Authentication;
import shiver.me.timbers.security.spring.AuthenticationFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class AuthenticationHttpServletRequestBinderTest {

    private HttpServletRequestBinder<String> httpServletRequestBinder;
    private AuthenticationFactory authenticationFactory;
    private AuthenticationHttpServletRequestBinder binder;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        httpServletRequestBinder = mock(HttpServletRequestBinder.class);
        authenticationFactory = mock(AuthenticationFactory.class);
        binder = new AuthenticationHttpServletRequestBinder(httpServletRequestBinder, authenticationFactory);
    }

    @Test
    public void Can_add_an_authentication_to_a_response() {

        final Authentication authentication = mock(Authentication.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);

        final String subject = someString();

        // Given
        given(authentication.getName()).willReturn(subject);

        // When
        binder.add(response, authentication);

        // Then
        verify(httpServletRequestBinder).add(response, subject);
    }

    @Test
    public void Can_retrieve_an_authentication_from_a_request() {

        final HttpServletRequest request = mock(HttpServletRequest.class);
        final String subject = someString();

        final Authentication expected = mock(Authentication.class);

        // Given
        given(httpServletRequestBinder.retrieve(request)).willReturn(subject);
        given(authenticationFactory.create(subject)).willReturn(expected);

        // When
        final Authentication actual = binder.retrieve(request);

        // Then
        assertThat(actual, equalTo(expected));
    }

    @Test
    public void Return_no_authentication_if_no_subject_found() {

        final HttpServletRequest request = mock(HttpServletRequest.class);

        // Given
        given(httpServletRequestBinder.retrieve(request)).willReturn(null);

        // When
        final Authentication actual = binder.retrieve(request);

        // Then
        assertThat(actual, nullValue());
        verifyZeroInteractions(authenticationFactory);
    }
}
