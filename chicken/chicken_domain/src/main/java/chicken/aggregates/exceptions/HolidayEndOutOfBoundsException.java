package chicken.aggregates.exceptions;

public class HolidayEndOutOfBoundsException extends RuntimeException {

    public HolidayEndOutOfBoundsException() {
        super("Holiday end is not in internship timeframe");
    }
}
