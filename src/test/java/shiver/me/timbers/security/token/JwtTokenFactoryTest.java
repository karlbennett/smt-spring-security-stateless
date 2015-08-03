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

package shiver.me.timbers.security.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import org.junit.Before;
import org.junit.Test;
import org.msgpack.MessagePack;

import java.io.IOException;

import static io.jsonwebtoken.SignatureAlgorithm.HS512;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class JwtTokenFactoryTest {

    private Class<TestType> type;
    private String secret;
    private JwtBuilder jwtBuilder;
    private JwtParser jwtParser;
    private MessagePack messagePack;
    private Base64 base64;

    private JwtTokenFactory<TestType> factory;

    @Before
    public void setUp() {
        type = TestType.class;
        secret = someString();
        jwtBuilder = mock(JwtBuilder.class);
        jwtParser = mock(JwtParser.class);
        messagePack = mock(MessagePack.class);
        base64 = mock(Base64.class);

        factory = new JwtTokenFactory<>(type, secret, jwtBuilder, jwtParser, messagePack, base64);
    }

    @Test
    public void Can_create_a_basic_jwt_token_factory_with_just_a_secret() {
        new JwtTokenFactory<>(Object.class, secret);
    }

    @Test
    public void Can_create_a_token_from_a_subject() throws IOException {

        final TestType subject = mock(TestType.class);
        final byte[] bytes = {};
        final String subjectString = someString();
        final JwtBuilder signWithJwtBuilder = mock(JwtBuilder.class);
        final JwtBuilder compactJwtBuilder = mock(JwtBuilder.class);

        final String expected = someString();

        // Given
        given(messagePack.write(subject)).willReturn(bytes);
        given(base64.encode(bytes)).willReturn(subjectString);
        given(jwtBuilder.setSubject(subjectString)).willReturn(signWithJwtBuilder);
        given(signWithJwtBuilder.signWith(HS512, secret)).willReturn(compactJwtBuilder);
        given(compactJwtBuilder.compact()).willReturn(expected);

        // When
        final String actual = factory.create(subject);

        // Then
        assertThat(actual, equalTo(expected));
    }

    @Test(expected = IllegalArgumentException.class)
    public void Can_fail_to_create_a_token_from_a_subject() throws IOException {

        final TestType subject = mock(TestType.class);

        // Given
        given(messagePack.write(subject)).willThrow(new IOException());

        // When
        factory.create(subject);
    }

    @Test
    public void Can_parse_a_subject_from_a_token() throws IOException {

        final String token = someString();

        final JwtParser signingKeyJwtParser = mock(JwtParser.class);
        @SuppressWarnings("unchecked")
        final Jws<Claims> jws = mock(Jws.class);
        final Claims body = mock(Claims.class);
        final String subjectString = someString();
        final byte[] bytes = {};

        final TestType expected = mock(TestType.class);

        // Given
        given(jwtParser.setSigningKey(secret)).willReturn(signingKeyJwtParser);
        given(signingKeyJwtParser.parseClaimsJws(token)).willReturn(jws);
        given(jws.getBody()).willReturn(body);
        given(body.getSubject()).willReturn(subjectString);
        given(base64.decode(subjectString)).willReturn(bytes);
        given(messagePack.read(bytes, type)).willReturn(expected);

        // When
        final TestType actual = factory.parse(token);

        // Then
        assertThat(actual, equalTo(expected));
    }

    @Test(expected = IllegalArgumentException.class)
    public void Can_fail_to_parse_a_subject_from_a_token() throws IOException {

        final String token = someString();

        final JwtParser signingKeyJwtParser = mock(JwtParser.class);
        @SuppressWarnings("unchecked")
        final Jws<Claims> jws = mock(Jws.class);
        final Claims body = mock(Claims.class);
        final String subjectString = someString();
        final byte[] bytes = {};

        // Given
        given(jwtParser.setSigningKey(secret)).willReturn(signingKeyJwtParser);
        given(signingKeyJwtParser.parseClaimsJws(token)).willReturn(jws);
        given(jws.getBody()).willReturn(body);
        given(body.getSubject()).willReturn(subjectString);
        given(base64.decode(subjectString)).willReturn(bytes);
        given(messagePack.read(bytes, type)).willThrow(new IOException());

        // When
        factory.parse(token);
    }

    private interface TestType {
    }
}
