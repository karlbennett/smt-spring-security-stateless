/*
 *    Copyright 2015 Karl Bennett
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package shiver.me.timbers.security.spring;

import org.junit.Test;
import org.mockito.InOrder;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import shiver.me.timbers.security.servlet.HttpServletRequestBinder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class StatelessAuthenticationSuccessHandlerTest {

    @Test
    public void Can_create_a_stateless_authentication_success_handler_with_just_a_secret() {
        new StatelessAuthenticationSuccessHandler(someString());
    }

    @Test
    public void Can_add_successful_authentication_to_the_response() throws IOException, ServletException {

        @SuppressWarnings("unchecked")
        final HttpServletRequestBinder<Authentication> authenticationFactory = mock(HttpServletRequestBinder.class);
        final SimpleUrlAuthenticationSuccessHandler delegate = mock(SimpleUrlAuthenticationSuccessHandler.class);

        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final Authentication authentication = mock(Authentication.class);

        // When
        new StatelessAuthenticationSuccessHandler(authenticationFactory, delegate)
            .onAuthenticationSuccess(request, response, authentication);

        // Then
        final InOrder order = inOrder(authenticationFactory, delegate);
        order.verify(authenticationFactory).add(response, authentication);
        order.verify(delegate).onAuthenticationSuccess(request, response, authentication);
    }
}
