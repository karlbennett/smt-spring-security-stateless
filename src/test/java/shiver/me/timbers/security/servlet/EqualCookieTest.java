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

import org.junit.Test;

import javax.servlet.http.Cookie;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static shiver.me.timbers.data.random.RandomStrings.someAlphaNumericString;

public class EqualCookieTest {

    @Test
    public void Cookies_with_the_same_values_are_equal() {

        final String name = someAlphaNumericString();
        final String value = someAlphaNumericString();
        final String path = someAlphaNumericString();
        final EqualCookie expected = new EqualCookie(name, value, path);
        final Cookie cookie = new Cookie(name, value);

        // Given
        cookie.setPath(path);

        // When
        final EqualCookie actual = new EqualCookie(name, value, path);

        // Then
        assertThat(actual, equalTo(actual));
        assertThat(actual, equalTo(expected));
        assertThat(actual, equalTo(cookie));
        assertThat(new EqualCookie(name, null, path), equalTo(new EqualCookie(name, null, path)));

        assertThat(actual.hashCode(), equalTo(expected.hashCode()));
    }

    @Test
    public void Cookies_are_not_equal_to_other_classes() {

        // When
        final EqualCookie actual = new EqualCookie(
            someAlphaNumericString(), someAlphaNumericString(), someAlphaNumericString()
        );

        // Then
        assertThat(actual, not(equalTo(new Object())));
    }

    @Test
    public void Cookies_with_the_different_names_are_not_equal() {

        // Given
        final String name = someAlphaNumericString();
        final String value = someAlphaNumericString();
        final String path = someAlphaNumericString();

        final EqualCookie expected = new EqualCookie(someAlphaNumericString(), value, path);

        // When
        final EqualCookie actual = new EqualCookie(name, value, path);

        // Then
        assertThat(actual, not(equalTo(expected)));

        assertThat(actual.hashCode(), not(equalTo(expected.hashCode())));
    }

    @Test
    public void Cookies_with_the_different_values_are_not_equal() {

        // Given
        final String name = someAlphaNumericString();
        final String value = someAlphaNumericString();
        final String path = someAlphaNumericString();

        final EqualCookie expected = new EqualCookie(name, someAlphaNumericString(), path);

        // When
        final EqualCookie actual = new EqualCookie(name, value, path);

        // Then
        assertThat(actual, not(equalTo(expected)));
        assertThat(actual, not(equalTo(new EqualCookie(name, null, path))));
        assertThat(new EqualCookie(name, null, path), not(equalTo(actual)));

        assertThat(actual.hashCode(), not(equalTo(expected.hashCode())));
        assertThat(actual.hashCode(), not(equalTo(new EqualCookie(name, null, path).hashCode())));
    }

    @Test
    public void Cookies_with_the_different_paths_are_not_equal() {

        // Given
        final String name = someAlphaNumericString();
        final String value = someAlphaNumericString();
        final String path = someAlphaNumericString();

        final EqualCookie expected = new EqualCookie(name, value, someAlphaNumericString());

        // When
        final EqualCookie actual = new EqualCookie(name, value, path);

        // Then
        assertThat(actual, not(equalTo(expected)));
        assertThat(actual, not(equalTo(new EqualCookie(name, value, null))));
        assertThat(new EqualCookie(name, value, null), not(equalTo(actual)));

        assertThat(actual.hashCode(), not(equalTo(expected.hashCode())));
        assertThat(actual.hashCode(), not(equalTo(new EqualCookie(name, value, null).hashCode())));
    }
}
