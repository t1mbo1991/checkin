package chicken.aggregates.exceptions;

public class InvalidEndTimeException extends RuntimeException {

    public InvalidEndTimeException() {
        super("Is not a full quarter hour");
    }
}
