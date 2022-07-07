package chicken.config;

import chicken.aggregates.config.ClockConfig;
import java.time.Clock;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClockConfigImpl implements ClockConfig {

    @Override
    public Clock getSystemDefaultClock() {
        return Clock.systemDefaultZone();
    }
}
