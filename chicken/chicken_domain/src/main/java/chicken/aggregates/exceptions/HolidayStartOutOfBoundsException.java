package chicken.aggregates.exceptions;

public class HolidayStartOutOfBoundsException extends RuntimeException {

    public HolidayStartOutOfBoundsException() {
        super("Holiday start is not in internship timeframe");
    }
}
