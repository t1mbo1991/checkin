package chicken.aggregates.student;

import chicken.aggregates.config.InternshipConfig;
import chicken.aggregates.exceptions.DateOutOfBoundsException;
import chicken.aggregates.exceptions.HolidayEndOutOfBoundsException;
import chicken.aggregates.exceptions.HolidayStartOutOfBoundsException;
import chicken.aggregates.exceptions.HolidayTimeExceededException;
import chicken.aggregates.exceptions.InvalidEndTimeException;
import chicken.aggregates.exceptions.InvalidStartTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public class Holiday {

    private final Timespan timespan;
    private Long id;

    public Holiday(Long id, Timespan timespan) {
        this.id = id;
        this.timespan = timespan;
    }

    public LocalDate getDate() {
        return timespan.date();
    }

    public LocalTime getStart() {
        return timespan.start();
    }

    public LocalTime getEnd() {
        return timespan.end();
    }

    public Timespan getTimespan() {
        return timespan;
    }

    public Long getId() {
        return id;
    }

    void setId(Long id) {
        this.id = id;
    }

    boolean isValidHoliday(long remainingHoliday, InternshipConfig config) throws RuntimeException {
        if (!isOnInternshipBusinessDay(timespan.date(), config.getStartDate(),
            config.getEndDate())) {
            throw new DateOutOfBoundsException("Is not in internship timeframe or on a weekend");
        } else if (!isInInternshipTimeFrame(timespan.start(), config.getStartTime(),
            config.getEndTime())) {
            throw new HolidayStartOutOfBoundsException();
        } else if (!isInInternshipTimeFrame(timespan.end(), config.getStartTime(),
            config.getEndTime())) {
            throw new HolidayEndOutOfBoundsException();
        } else if (!isValidTime(timespan.start())) {
            throw new InvalidStartTimeException();
        } else if (!isValidTime(timespan.end())) {
            throw new InvalidEndTimeException();
        } else if (timespan.getDuration() > remainingHoliday) {
            throw new HolidayTimeExceededException(
                timespan.getDuration(),
                remainingHoliday);
        } else {
            return true;
        }
    }

    private boolean isOnInternshipBusinessDay(LocalDate date, LocalDate startDate,
        LocalDate endDate) {
        return date.compareTo(startDate) >= 0 && date.compareTo(endDate) <= 0
            && !isWeekend(date);
    }

    private boolean isWeekend(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }

    private boolean isInInternshipTimeFrame(LocalTime time, LocalTime startTime,
        LocalTime endTime) {
        return time.compareTo(startTime) >= 0 && time.compareTo(endTime) <= 0;
    }

    private boolean isValidTime(LocalTime time) {
        return time.getMinute() % 15 == 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Holiday holiday = (Holiday) o;
        return Objects.equals(id, holiday.id) && Objects.equals(timespan, holiday.timespan);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, timespan);
    }

    public boolean isOnStartOrEnd(InternshipConfig config) {

        return (getStart().compareTo(config.getStartTime()) == 0
            || getEnd().compareTo(config.getEndTime()) == 0);
    }
}
