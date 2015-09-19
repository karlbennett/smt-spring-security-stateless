package shiver.me.timbers.security.web.spring;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import shiver.me.timbers.security.spring.ServletExceptionMapper;

import javax.servlet.ServletException;

import static org.hamcrest.Matchers.is;

/**
 * @author Karl Bennett
 */
public class ServletExceptionMapperTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private ServletExceptionMapper mapper;

    @Before
    public void setUp() {
        mapper = new ServletExceptionMapper();
    }

    @Test
    public void Can_map_checked_exception_to_a_servlet_exception() throws ServletException {

        // Given
        final Exception exception = new Exception();
        expectedException.expect(ServletException.class);
        expectedException.expectCause(is(exception));

        // When
        mapper.throwMapped(exception);
    }

    @Test
    public void Can_rethrow_servlet_exception() throws ServletException {

        // Given
        final ServletException exception = new ServletException();
        expectedException.expect(is(exception));

        // When
        mapper.throwMapped(exception);
    }

    @Test
    public void Can_rethrow_runtime_exception() throws ServletException {

        // Given
        final RuntimeException exception = new RuntimeException();
        expectedException.expect(is(exception));

        // When
        mapper.throwMapped(exception);
    }

    @Test
    public void Can_rethrow_error() throws ServletException {

        // Given
        final Error error = new Error();
        expectedException.expect(is(error));

        // When
        mapper.throwMapped(error);
    }
}