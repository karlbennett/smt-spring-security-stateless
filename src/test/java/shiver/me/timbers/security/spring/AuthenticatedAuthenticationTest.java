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

import javax.security.auth.Subject;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static shiver.me.timbers.data.random.RandomBooleans.someBoolean;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class AuthenticatedAuthenticationTest {

    @Test
    public void Can_create_an_authenticated_authentication() {

        // Given
        final String principal = someString();

        // When
        final AuthenticatedAuthentication actual = new AuthenticatedAuthentication(principal);

        // Then
        assertThat(actual.getPrincipal().toString(), equalTo(principal));
        assertThat(actual.getName(), equalTo(principal));
        assertThat(actual.getAuthorities(), empty());
        assertThat(actual.getCredentials().toString(), isEmptyString());
        assertThat(actual.getDetails(), nullValue());
        assertThat(actual.implies(new Subject()), equalTo(false));
    }

    @Test
    public void Can_create_set_authentication_state() {

        // Given
        final Boolean authenticated = someBoolean();

        // When
        final AuthenticatedAuthentication actual = new AuthenticatedAuthentication(someString());
        actual.setAuthenticated(authenticated);

        // Then
        assertThat(actual.isAuthenticated(), equalTo(authenticated));
    }
}