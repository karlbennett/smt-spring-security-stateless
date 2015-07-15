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

package shiver.me.timbers.security;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;

import static io.jsonwebtoken.SignatureAlgorithm.HS512;

/**
 * This is a very basic JWT token implementation, it doesn't even set an expiration date for the token so it will be
 * valid for ever.
 *
 * @author Karl Bennett
 */
public class BasicJwtTokenFactory implements TokenFactory {

    private final String secret;
    private final JwtBuilder jwtBuilder;
    private final JwtParser jwtParser;

    public BasicJwtTokenFactory(String secret) {
        this(secret, Jwts.builder(), Jwts.parser());
    }

    public BasicJwtTokenFactory(String secret, JwtBuilder jwtBuilder, JwtParser jwtParser) {
        this.secret = secret;
        this.jwtBuilder = jwtBuilder;
        this.jwtParser = jwtParser;
    }

    @Override
    public String create(String subject) {
        return jwtBuilder.setSubject(subject).signWith(HS512, secret).compact();
    }

    @Override
    public String parse(String token) {
        return jwtParser.setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
    }
}
