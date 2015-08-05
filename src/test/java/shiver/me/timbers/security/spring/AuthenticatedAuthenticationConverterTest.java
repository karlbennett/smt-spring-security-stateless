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
import org.springframework.security.core.Authentication;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.unitils.reflectionassert.ReflectionAssert.assertPropertyReflectionEquals;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class AuthenticatedAuthenticationConverterTest {

    private AuthenticatedAuthenticationConverter authenticationConverter;

    @Before
    public void setUp() {
        authenticationConverter = new AuthenticatedAuthenticationConverter();
    }

    @Test
    public void Can_convert_an_authentication_into_a_target() {

        final Authentication authentication = mock(Authentication.class);

        final String expected = someString();

        // Given
        given(authentication.getName()).willReturn(expected);

        // When
        final String actual = authenticationConverter.convert(authentication);

        // Then
        assertThat(actual, is(expected));
    }

    @Test
    public void Can_convert_an_target_into_authentication() {

        // Given
        final String target = someString();

        // When
        final Authentication actual = authenticationConverter.convert(target);

        // Then
        assertThat(actual, instanceOf(AuthenticatedAuthentication.class));
        assertPropertyReflectionEquals("principal", target, actual);
        assertPropertyReflectionEquals("authenticated", true, actual);
    }
}