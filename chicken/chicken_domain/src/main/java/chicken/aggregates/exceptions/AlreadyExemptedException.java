package chicken.aggregates.exceptions;

public class AlreadyExemptedException extends RuntimeException {
    public AlreadyExemptedException() {
        super("You are already exempted");
    }
}
