package chicken.aggregates.config;

import java.time.Clock;

public interface ClockConfig {

    Clock getSystemDefaultClock();
}
