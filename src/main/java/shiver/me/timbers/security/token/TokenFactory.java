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

/**
 * @author Karl Bennett
 */
public interface TokenFactory<T> {

    /**
     * @return an authorised token generated from the supplied subject. Commonly the related accounts username.
     */
    String create(T subject) throws Exception;

    /**
     * @return the subject that was used to generate this token. Commonly the related accounts username.
     */
    T parse(String token) throws Exception;
}
