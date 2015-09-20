package shiver.me.timbers.security.spring;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import shiver.me.timbers.security.servlet.XAuthTokenHttpServletBinder;
import shiver.me.timbers.security.token.JwtTokenFactory;
import shiver.me.timbers.security.token.TokenFactory;

import java.util.HashMap;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class StatelessWebSecurityConfigurerAdapterTest {

    private HttpSecurity http;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        http = new HttpSecurity(
            mock(ObjectPostProcessor.class),
            mock(AuthenticationManagerBuilder.class),
            new HashMap<Class<Object>, Object>()
        );
    }

    @Test
    public void Can_configure_the_http_security() throws Exception {
        // When
        new StatelessWebSecurityConfigurerAdapter().configure(http);
    }

    @Test
    public void Can_configure_the_token_factory() throws Exception {

        // Given
        final boolean[] configured = {false};

        // When
        new StatelessWebSecurityConfigurerAdapter() {
            @Override
            protected void configure(JwtTokenFactory tokenFactory) {
                configured[0] = true;
            }
        }.configure(http);

        // Then
        assertThat(configured[0], is(true));
    }

    @Test
    public void Cannot_configure_the_token_factory_if_a_custom_version_has_been_provided() throws Exception {

        // Given
        final boolean[] configured = {false};

        // When
        new StatelessWebSecurityConfigurerAdapter() {
            @Override
            protected TokenFactory tokenFactory(String secret) {
                return mock(TokenFactory.class);
            }

            @Override
            protected void configure(JwtTokenFactory tokenFactory) {
                configured[0] = true;
            }
        }.configure(http);

        // Then
        assertThat(configured[0], is(false));
    }

    @Test
    public void Can_configure_the_xauth_token_http_servlet_binder() throws Exception {

        // Given
        final boolean[] configured = {false};

        // When
        new StatelessWebSecurityConfigurerAdapter() {
            @Override
            protected void configure(XAuthTokenHttpServletBinder xAuthTokenHttpServletBinder) {
                configured[0] = true;
            }
        }.configure(http);

        // Then
        assertThat(configured[0], is(true));
    }

    @Test
    public void Cannot_configure_the_xauth_token_http_servlet_binder_if_a_custom_version_has_been_provided()
        throws Exception {

        // Given
        final boolean[] configured = {false};

        // When
        new StatelessWebSecurityConfigurerAdapter() {
            @Override
            protected XAuthTokenHttpServletBinder xAuthTokenHttpServletBinder(TokenFactory tokenFactory) {
                return mock(XAuthTokenHttpServletBinder.class);
            }

            @Override
            protected void configure(XAuthTokenHttpServletBinder xAuthTokenHttpServletBinder) {
                configured[0] = true;
            }
        }.configure(http);

        // Then
        assertThat(configured[0], is(false));
    }

    @Test
    public void Can_further_configure_the_thhp_security() throws Exception {

        // Given
        final boolean[] configured = {false};

        // When
        new StatelessWebSecurityConfigurerAdapter() {
            @Override
            protected void configureFurther(HttpSecurity http) throws Exception {
                configured[0] = true;
            }
        }.configure(http);

        // Then
        assertThat(configured[0], is(true));
    }
}