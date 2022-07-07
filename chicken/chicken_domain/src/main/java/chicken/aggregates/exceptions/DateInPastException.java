package chicken.aggregates.exceptions;

public class DateInPastException extends RuntimeException {

    public DateInPastException(String message) {
        super(message);
    }
}
