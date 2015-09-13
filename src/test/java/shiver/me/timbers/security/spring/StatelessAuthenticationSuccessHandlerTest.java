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

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import shiver.me.timbers.security.servlet.HttpServletBinder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static shiver.me.timbers.data.random.RandomStrings.someAlphaString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class StatelessAuthenticationSuccessHandlerTest {

    private HttpServletBinder<Authentication> httpServletBinder;
    private SimpleUrlAuthenticationSuccessHandler delegate;
    private StatelessAuthenticationSuccessHandler handler;
    private ExceptionMapper exceptionMapper;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        httpServletBinder = mock(HttpServletBinder.class);
        delegate = mock(SimpleUrlAuthenticationSuccessHandler.class);
        exceptionMapper = mock(ExceptionMapper.class);
        handler = new StatelessAuthenticationSuccessHandler(httpServletBinder, delegate, exceptionMapper);
    }

    @Test
    public void Can_create_a_stateless_authentication_success_handler_with_just_a_secret() {
        new StatelessAuthenticationSuccessHandler(someString(), "/" + someAlphaString());
    }

    @Test
    public void Can_add_successful_authentication_to_the_response() throws Exception {

        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final Authentication authentication = mock(Authentication.class);

        // When
        handler.onAuthenticationSuccess(request, response, authentication);

        // Then
        final InOrder order = inOrder(httpServletBinder, delegate);
        order.verify(httpServletBinder).add(response, authentication);
        order.verify(delegate).onAuthenticationSuccess(request, response, authentication);
    }

    @Test
    public void Can_map_thrown_exception() throws Exception {

        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final Authentication authentication = mock(Authentication.class);

        final Exception exception = new Exception();

        // Given
        willThrow(exception).given(httpServletBinder).add(response, authentication);

        // When
        handler.onAuthenticationSuccess(request, response, authentication);

        // Then
        verify(exceptionMapper).throwMapped(exception);
    }

    @Test
    public void Can_map_thrown_error() throws Exception {

        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final Authentication authentication = mock(Authentication.class);

        final Error error = new Error();

        // Given
        willThrow(error).given(httpServletBinder).add(response, authentication);

        // When
        handler.onAuthenticationSuccess(request, response, authentication);

        // Then
        verify(exceptionMapper).throwMapped(error);
    }
}
