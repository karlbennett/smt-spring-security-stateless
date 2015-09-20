<!---
Copyright 2015 Karl Bennett

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
-->
smt-spring-security-stateless
===========
[![Build Status](https://travis-ci.org/shiver-me-timbers/smt-spring-security-stateless.svg?branch=master)](https://travis-ci.org/shiver-me-timbers/smt-spring-security-stateless) [![Coverage Status](https://coveralls.io/repos/shiver-me-timbers/smt-spring-security-stateless/badge.svg?branch=master&service=github)](https://coveralls.io/github/shiver-me-timbers/smt-spring-security-stateless?branch=master) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.shiver-me-timbers/smt-spring-security-stateless/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.shiver-me-timbers/smt-spring-security-stateless/)

The purpose of this library is to provide a simple way of configuring Spring Security to be stateless.

### Maven

```xml
<dependencies>
    <dependency>
        <groupId>com.github.shiver-me-timbers</groupId>
        <artifactId>smt-spring-security-stateless</artifactId>
        <version>2.0</version>
    </dependency>
</dependencies>
```

### Usage

To register and customise the stateless configuration you must create a configuration class that extends the
[`StatelessWebSecurityConfigurerAdapter`](src/main/java/shiver/me/timbers/security/spring/StatelessWebSecurityConfigurerAdapter.java).

```java
@Configuration
public class SecurityConfiguration extends StatelessWebSecurityConfigurerAdapter {
}
```

Any further Spring Security configuration must be done in this configuration class through overriding the
`configureFurther(HttpSecurity)` method. Configuring Spring Security in the traditional way by extending the
`WebSecurityConfigurerAdapter` class and overriding its `configure(HttpSecurity)` method will break the stateless
configuration.

```java
@Configuration
public class SecurityConfiguration extends StatelessWebSecurityConfigurerAdapter {
    @Override
    protected void configureFurther(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().authenticated();
        http.formLogin().loginPage("/signIn").permitAll();
        http.logout().logoutUrl("/signOut").logoutSuccessUrl("/");
    }
}
```

The stateless configuration it's self can be customised by overriding the other `configure(...)` and component methods
in the `StatelessWebSecurityConfigurerAdapter` class.

### Examples

A couple examples have been provide to show how to use this library.

#### [smt-spring-security-stateless-basic](https://github.com/shiver-me-timbers/smt-spring-security-stateless-examples/tree/master/smt-spring-security-stateless-basic)

This example shows how to use the library with it's default configuration.

#### [smt-spring-security-stateless-advanced](https://github.com/shiver-me-timbers/smt-spring-security-stateless-examples/tree/master/smt-spring-security-stateless-advanced)

This example shows how you can customise the library to generate your own tokens and authentications.