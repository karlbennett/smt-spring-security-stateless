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
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import org.junit.Before;
import org.junit.Test;
import org.msgpack.MessagePack;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static io.jsonwebtoken.SignatureAlgorithm.HS512;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.mockito.Mockito.verify;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class JwtTokenParserTest {

    private Class<TestType> type;
    private String secret;
    private JwtBuilder jwtBuilder;
    private JwtParser jwtParser;
    private MessagePack messagePack;
    private Base64 base64;

    private JwtTokenParser<TestType> factory;
    private Dates dates;

    @Before
    public void setUp() {
        type = TestType.class;
        secret = someString();
        jwtBuilder = mock(JwtBuilder.class);
        jwtParser = mock(JwtParser.class);
        messagePack = mock(MessagePack.class);
        base64 = mock(Base64.class);

        dates = mock(Dates.class);
        factory = new JwtTokenParser<>(type, secret, jwtBuilder, jwtParser, messagePack, base64, dates);
    }

    @Test
    public void Can_create_a_basic_jwt_token_factory_with_just_a_secret() {
        new JwtTokenParser<>(Object.class, secret);
    }

    @Test
    public void Can_create_a_token_from_an_entity() throws IOException {

        final TestType entity = mock(TestType.class);

        final byte[] bytes = {};
        final String entityString = someString();
        final JwtBuilder entityJwtBuilder = mock(JwtBuilder.class);
        final JwtBuilder signWithJwtBuilder = mock(JwtBuilder.class);

        final String expected = someString();

        // Given
        given(messagePack.write(entity)).willReturn(bytes);
        given(base64.encode(bytes)).willReturn(entityString);
        given(jwtBuilder.claim("entity", entityString)).willReturn(entityJwtBuilder);
        given(entityJwtBuilder.signWith(HS512, secret)).willReturn(signWithJwtBuilder);
        given(signWithJwtBuilder.compact()).willReturn(expected);

        // When
        final String actual = factory.create(entity);

        // Then
        assertThat(actual, equalTo(expected));
    }

    @Test(expected = IllegalArgumentException.class)
    public void Can_fail_to_create_a_token_from_an_entity() throws IOException {

        final TestType subject = mock(TestType.class);

        // Given
        given(messagePack.write(subject)).willThrow(new IOException());

        // When
        factory.create(subject);
    }

    @Test
    public void Can_parse_an_entity_from_a_token() throws IOException {

        final String token = someString();

        final JwtParser signingKeyJwtParser = mock(JwtParser.class);
        @SuppressWarnings("unchecked")
        final Jws<Claims> jws = mock(Jws.class);
        final Claims body = mock(Claims.class);
        final String entityString = someString();
        final byte[] bytes = {};

        final TestType expected = mock(TestType.class);

        // Given
        given(jwtParser.setSigningKey(secret)).willReturn(signingKeyJwtParser);
        given(signingKeyJwtParser.parseClaimsJws(token)).willReturn(jws);
        given(jws.getBody()).willReturn(body);
        given(body.get("entity")).willReturn(entityString);
        given(base64.decode(entityString)).willReturn(bytes);
        given(messagePack.read(bytes, type)).willReturn(expected);

        // When
        final TestType actual = factory.parse(token);

        // Then
        assertThat(actual, equalTo(expected));
    }

    @Test(expected = IllegalArgumentException.class)
    public void Can_fail_to_parse_an_entity_from_a_token() throws IOException {

        final String token = someString();

        final JwtParser signingKeyJwtParser = mock(JwtParser.class);
        @SuppressWarnings("unchecked")
        final Jws<Claims> jws = mock(Jws.class);
        final Claims body = mock(Claims.class);
        final String entityString = someString();
        final byte[] bytes = {};

        // Given
        given(jwtParser.setSigningKey(secret)).willReturn(signingKeyJwtParser);
        given(signingKeyJwtParser.parseClaimsJws(token)).willReturn(jws);
        given(jws.getBody()).willReturn(body);
        given(body.get("entity")).willReturn(entityString);
        given(base64.decode(entityString)).willReturn(bytes);
        given(messagePack.read(bytes, type)).willThrow(new IOException());

        // When
        factory.parse(token);
    }

    @Test
    public void Can_configure_all_created_tokens_to_have_an_expiration_date() throws IOException {

        final Long duration = someLong();
        final TimeUnit unit = someEnum(TimeUnit.class);
        final TestType entity = mock(TestType.class);

        final byte[] bytes = {};
        final String entityString = someString();
        final JwtBuilder entityJwtBuilder = mock(JwtBuilder.class);
        final JwtBuilder signWithJwtBuilder = mock(JwtBuilder.class);
        final Date expiry = mock(Date.class);

        final String expected = someString();

        // Given
        given(messagePack.write(entity)).willReturn(bytes);
        given(base64.encode(bytes)).willReturn(entityString);
        given(jwtBuilder.claim("entity", entityString)).willReturn(entityJwtBuilder);
        given(entityJwtBuilder.signWith(HS512, secret)).willReturn(signWithJwtBuilder);
        given(dates.nowPlus(duration, unit)).willReturn(expiry);
        given(signWithJwtBuilder.compact()).willReturn(expected);

        // When
        final String actual = factory.willExpireAfter(duration, unit).create(entity);

        // Then
        verify(signWithJwtBuilder).setExpiration(expiry);
        assertThat(actual, equalTo(expected));
    }

    @Test(expected = IllegalArgumentException.class)
    public void Cannot_configure_an_expiration_date_with_only_a_duration() throws IOException {

        // When
        factory.willExpireAfter(someLong(), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void Cannot_configure_an_expiration_date_with_only_a_unit() throws IOException {

        final TimeUnit unit = someEnum(TimeUnit.class);

        // When
        factory.willExpireAfter(null, unit);
    }

    @Test
    public void Can_fail_to_parse_an_entity_from_a_token_that_has_expired() throws IOException {

        final String token = someString();

        final JwtParser signingKeyJwtParser = mock(JwtParser.class);

        // Given
        given(jwtParser.setSigningKey(secret)).willReturn(signingKeyJwtParser);
        given(signingKeyJwtParser.parseClaimsJws(token))
            .willThrow(new ExpiredJwtException(mock(Header.class), mock(Claims.class), someString()));

        // When
        final TestType actual = factory.parse(token);

        // Then
        assertThat(actual, nullValue());
    }

    private interface TestType {
    }
}
