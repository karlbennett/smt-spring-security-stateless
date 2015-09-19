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

package shiver.me.timbers.security.web.spring;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InOrder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import shiver.me.timbers.security.servlet.HttpServletBinder;
import shiver.me.timbers.security.spring.ExceptionMapper;
import shiver.me.timbers.security.spring.SecurityContextHolder;
import shiver.me.timbers.security.spring.StatelessAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

public class StatelessAuthenticationFilterTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private HttpServletBinder<Authentication> httpServletBinder;
    private SecurityContextHolder contextHolder;
    private ExceptionMapper<ServletException> exceptionMapper;
    private StatelessAuthenticationFilter filter;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        httpServletBinder = mock(HttpServletBinder.class);
        contextHolder = mock(SecurityContextHolder.class);
        exceptionMapper = mock(ExceptionMapper.class);
        filter = new StatelessAuthenticationFilter(httpServletBinder, contextHolder, exceptionMapper);
    }

    @Test
    public void Can_authenticate_request() throws Exception {

        final HttpServletRequest request = mock(HttpServletRequest.class);
        final ServletResponse response = mock(ServletResponse.class);
        final FilterChain filterChain = mock(FilterChain.class);

        final Authentication authentication = mock(Authentication.class);
        final SecurityContext securityContext = mock(SecurityContext.class);

        // Given
        given(contextHolder.getContext()).willReturn(securityContext);
        given(httpServletBinder.retrieve(request)).willReturn(authentication);

        // When
        filter.doFilter(request, response, filterChain);

        // Then
        final InOrder order = inOrder(securityContext, filterChain);
        order.verify(securityContext).setAuthentication(authentication);
        order.verify(filterChain).doFilter(request, response);
        verifyZeroInteractions(exceptionMapper);
    }

    @Test
    public void Can_map_thrown_exception() throws Exception {

        final HttpServletRequest request = mock(HttpServletRequest.class);
        final ServletResponse response = mock(ServletResponse.class);
        final FilterChain filterChain = mock(FilterChain.class);

        final SecurityContext securityContext = mock(SecurityContext.class);

        final Exception exception = new Exception();

        // Given
        given(contextHolder.getContext()).willReturn(securityContext);
        given(httpServletBinder.retrieve(request)).willThrow(exception);

        // When
        filter.doFilter(request, response, filterChain);

        // Then
        verify(exceptionMapper).throwMapped(exception);
    }

    @Test
    public void Can_map_thrown_error() throws Exception {

        final HttpServletRequest request = mock(HttpServletRequest.class);
        final ServletResponse response = mock(ServletResponse.class);
        final FilterChain filterChain = mock(FilterChain.class);

        final SecurityContext securityContext = mock(SecurityContext.class);

        final Error error = new Error();

        // Given
        given(contextHolder.getContext()).willReturn(securityContext);
        given(httpServletBinder.retrieve(request)).willThrow(error);

        // When
        filter.doFilter(request, response, filterChain);

        // Then
        verify(exceptionMapper).throwMapped(error);
    }
}
