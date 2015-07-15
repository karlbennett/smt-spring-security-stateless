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

package shiver.me.timbers.security.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import org.junit.Before;
import org.junit.Test;

import static io.jsonwebtoken.SignatureAlgorithm.HS512;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class BasicJwtTokenFactoryTest {

    private String secret;
    private JwtBuilder jwtBuilder;
    private BasicJwtTokenFactory factory;
    private JwtParser jwtParser;

    @Before
    public void setUp() {
        secret = someString();
        jwtBuilder = mock(JwtBuilder.class);
        jwtParser = mock(JwtParser.class);

        factory = new BasicJwtTokenFactory(secret, jwtBuilder, jwtParser);
    }

    @Test
    public void Can_create_a_basic_jwt_token_factory_with_just_a_secret() {
        new BasicJwtTokenFactory(secret);
    }

    @Test
    public void Can_create_a_token_from_an_authentication() {

        final String username = someString();
        final JwtBuilder signWithJwtBuilder = mock(JwtBuilder.class);
        final JwtBuilder compactJwtBuilder = mock(JwtBuilder.class);

        final String expected = someString();

        // Given
        given(jwtBuilder.setSubject(username)).willReturn(signWithJwtBuilder);
        given(signWithJwtBuilder.signWith(HS512, secret)).willReturn(compactJwtBuilder);
        given(compactJwtBuilder.compact()).willReturn(expected);

        // When
        final String actual = factory.create(username);

        // Then
        assertThat(actual, equalTo(expected));
    }

    @Test
    public void Can_create_an_authentication_from_a_token() {

        final String token = someString();

        final JwtParser signingKeyJwtParser = mock(JwtParser.class);
        @SuppressWarnings("unchecked")
        final Jws<Claims> jws = mock(Jws.class);
        final Claims body = mock(Claims.class);
        final String expected = someString();

        // Given
        given(jwtParser.setSigningKey(secret)).willReturn(signingKeyJwtParser);
        given(signingKeyJwtParser.parseClaimsJws(token)).willReturn(jws);
        given(jws.getBody()).willReturn(body);
        given(body.getSubject()).willReturn(expected);

        // When
        final String actual = factory.parse(token);

        // Then
        assertThat(actual, equalTo(expected));
    }
}
