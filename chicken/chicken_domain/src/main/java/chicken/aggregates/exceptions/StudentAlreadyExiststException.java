package chicken.aggregates.exceptions;

public class StudentAlreadyExiststException extends RuntimeException {

    public StudentAlreadyExiststException(String gitHubHandle) {
        super("Student with" + gitHubHandle + " as githubhandle already exists");
    }
}
