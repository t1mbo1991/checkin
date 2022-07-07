package chicken.aggregates.config;

import java.time.LocalDate;
import java.time.LocalTime;

public interface InternshipConfig {

    int getMaxHolidays();

    LocalDate getStartDate();

    LocalDate getEndDate();

    LocalTime getStartTime();

    LocalTime getEndTime();
}
