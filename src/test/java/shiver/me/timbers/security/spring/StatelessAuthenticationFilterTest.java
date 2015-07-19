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

import org.junit.Test;
import org.mockito.InOrder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import shiver.me.timbers.security.servlet.HttpServletRequestBinder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class StatelessAuthenticationFilterTest {


    @Test
    public void Can_create_a_stateless_authentication_filter_with_just_a_secret() {
        new StatelessAuthenticationFilter(someString());
    }

    @Test
    public void Can_authenticate_request() throws IOException, ServletException {

        @SuppressWarnings("unchecked")
        final HttpServletRequestBinder<Authentication> authenticationFactory = mock(HttpServletRequestBinder.class);
        final SecurityContextHolder contextHolder = mock(SecurityContextHolder.class);

        final HttpServletRequest request = mock(HttpServletRequest.class);
        final ServletResponse response = mock(ServletResponse.class);
        final FilterChain filterChain = mock(FilterChain.class);

        final Authentication authentication = mock(Authentication.class);
        final SecurityContext securityContext = mock(SecurityContext.class);

        // Given
        given(authenticationFactory.retrieve(request)).willReturn(authentication);
        given(contextHolder.getContext()).willReturn(securityContext);

        // When
        new StatelessAuthenticationFilter(authenticationFactory, contextHolder).doFilter(request, response, filterChain);

        // Then
        final InOrder order = inOrder(securityContext, filterChain);
        order.verify(securityContext).setAuthentication(authentication);
        order.verify(filterChain).doFilter(request, response);
    }
}
