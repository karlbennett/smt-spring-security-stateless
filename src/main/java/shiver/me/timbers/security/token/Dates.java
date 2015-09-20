package shiver.me.timbers.security.token;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author Karl Bennett
 */
public interface Dates {

    Date nowPlus(Long duration, TimeUnit unit);
}
