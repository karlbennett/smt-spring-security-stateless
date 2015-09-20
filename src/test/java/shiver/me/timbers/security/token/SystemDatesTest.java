package shiver.me.timbers.security.token;

import org.junit.Test;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomLongs.someLong;

public class SystemDatesTest {

    @Test
    public void Can_create_a_date_a_specific_time_in_the_future() {

        // Given
        final Long duration = someLong();
        final TimeUnit unit = someEnum(TimeUnit.class);
        final Date expected = new Date(System.currentTimeMillis() + unit.toMillis(duration));

        // When
        final Date actual = new SystemDates().nowPlus(duration, unit);

        // Then
        assertThat(actual, allOf(greaterThanOrEqualTo(expected), lessThan(new Date(expected.getTime() + 500L))));
    }
}