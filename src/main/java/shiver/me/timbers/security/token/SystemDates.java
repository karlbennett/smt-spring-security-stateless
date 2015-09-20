package shiver.me.timbers.security.token;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author Karl Bennett
 */
public class SystemDates implements Dates {

    @Override
    public Date nowPlus(Long duration, TimeUnit unit) {
        return new Date(System.currentTimeMillis() + unit.toMillis(duration));
    }
}
