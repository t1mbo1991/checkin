package chicken.aggregates.exceptions;

public class InvalidTimeBlockException extends RuntimeException {

    public InvalidTimeBlockException(String message) {
        super(message);
    }
}
