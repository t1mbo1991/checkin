package chicken.aggregates.exceptions;

public class InvalidStartTimeException extends RuntimeException {

    public InvalidStartTimeException() {
        super("Is not a full quarter hour");
    }
}
