package chicken.aggregates.exceptions;

public class DateOutOfBoundsException extends RuntimeException {

    public DateOutOfBoundsException(String message) {
        super(message);
    }
}
