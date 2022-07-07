package chicken.config;

import chicken.aggregates.config.InternshipConfig;
import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InternshipConfigImpl implements InternshipConfig {

    @Value("${config.maxHolidays}")
    private int maxHolidays;

    @Value("${config.startDate}")
    private String startDate;

    @Value("${config.endDate}")
    private String endDate;

    @Value("${config.startTime}")
    private String startTime;

    @Value("${config.endTime}")
    private String endTime;

    @Override
    public int getMaxHolidays() {
        return maxHolidays;
    }

    @Override
    public LocalDate getStartDate() {
        return LocalDate.parse(startDate);
    }

    @Override
    public LocalDate getEndDate() {
        return LocalDate.parse(endDate);
    }

    @Override
    public LocalTime getStartTime() {
        return LocalTime.parse(startTime);
    }

    @Override
    public LocalTime getEndTime() {
        return LocalTime.parse(endTime);
    }
}
