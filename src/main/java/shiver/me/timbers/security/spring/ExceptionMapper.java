package shiver.me.timbers.security.spring;

/**
 * @author Karl Bennett
 */
public interface ExceptionMapper<E extends Exception> {

    void throwMapped(Throwable throwable) throws E;
}
