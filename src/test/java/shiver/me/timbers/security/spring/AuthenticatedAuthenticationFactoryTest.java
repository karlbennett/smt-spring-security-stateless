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
import org.springframework.security.core.Authentication;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class AuthenticatedAuthenticationFactoryTest {

    @Test
    public void Can_create_an_authenticated_authetication() {

        // Given
        final String subject = someString();

        // When
        final Authentication actual = new AuthenticatedAuthenticationFactory().create(subject);

        // Then
        assertThat(actual.getPrincipal().toString(), equalTo(subject));
        assertThat(actual.isAuthenticated(), equalTo(true));
    }
}