package chicken.aggregates.student;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public record Timespan(LocalDate date, LocalTime start, LocalTime end) {

    boolean isOverlap(LocalDate date, LocalTime newStart, LocalTime newEnd) {
        return
            isOnSameDate(date) &&
                (
                    startIsWithinTimespan(newStart) ||
                        endIsWithinTimespan(newEnd) ||
                        isWithin(newStart, newEnd)
                );
    }

    private boolean isWithin(LocalTime newStart, LocalTime newEnd) {
        return (start.isAfter(newStart) || start.equals(newStart)) && end.isBefore(newEnd)
            || end.equals(newEnd);
    }

    private boolean endIsWithinTimespan(LocalTime newEnd) {
        return (start.isBefore(newEnd) || start.equals(newEnd)) && (end.isAfter(newEnd));
    }

    private boolean isOnSameDate(LocalDate newDate) {
        return date.isEqual(newDate);
    }

    private boolean startIsWithinTimespan(LocalTime newStart) {
        return start.isBefore(newStart) && end.isAfter(newStart) || end.equals(newStart);
    }

    public long getDuration() {
        return ChronoUnit.MINUTES.between(start, end);
    }

    @Override
    public String toString() {
        return date.toString() + ", " + start.toString() + " - " + end.toString();
    }
}
