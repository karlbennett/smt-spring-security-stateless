package shiver.me.timbers.security.spring;

import javax.servlet.ServletException;

/**
 * @author Karl Bennett
 */
public class ServletExceptionMapper implements ExceptionMapper<ServletException> {

    @Override
    public void throwMapped(Throwable throwable) throws ServletException {
        if (throwable instanceof ServletException) {
            throw (ServletException) throwable;
        }

        if (throwable instanceof RuntimeException) {
            throw (RuntimeException) throwable;
        }

        if (throwable instanceof Error) {
            throw (Error) throwable;
        }

        throw new ServletException(throwable);
    }
}
