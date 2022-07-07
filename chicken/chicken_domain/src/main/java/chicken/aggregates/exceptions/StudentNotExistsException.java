package chicken.aggregates.exceptions;

public class StudentNotExistsException extends RuntimeException {

    public StudentNotExistsException(String gitHubHandle) {
        super("Student with gitHubHandle " + gitHubHandle + " does not exist");
    }
}
