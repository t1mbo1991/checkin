package chicken.aggregates.exceptions;

public class HolidayTimeExceededException extends RuntimeException {

    public HolidayTimeExceededException(long duration, long remainingTime) {
        super("Holiday with duration " + duration + " exceeds remaining time of " + remainingTime);
    }

}
