package chicken.aggregates.exceptions;

public class InvalidEventException extends RuntimeException {

    public InvalidEventException() {
        super("EventID does not exist or name does not match");
    }
}
