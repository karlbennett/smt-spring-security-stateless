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

import org.junit.Before;
import org.junit.Test;

import javax.xml.bind.DatatypeConverter;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class DataConverterBase64Test {

    private DataConverterBase64 base64;

    @Before
    public void setUp() {
        base64 = new DataConverterBase64();
    }

    @Test
    public void Can_encode_into_base64() {

        // Given
        final byte[] bytes = someString().getBytes();

        // When
        final String actual = base64.encode(bytes);

        // Then
        assertThat(actual, equalTo(DatatypeConverter.printBase64Binary(bytes)));
    }

    @Test
    public void Can_decode_from_base64() {

        // Given
        final byte[] bytes = someString().getBytes();

        // When
        final byte[] actual = base64.decode(DatatypeConverter.printBase64Binary(bytes));

        // Then
        assertThat(actual, equalTo(bytes));
    }
}