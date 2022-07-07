package chicken.aggregates.exceptions;

public class RequestFailedException extends RuntimeException {

    public RequestFailedException() {
        super("Verification failed");
    }
}
