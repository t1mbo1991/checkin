package chicken.aggregates.exceptions;

public class ExamAlreadyExistsException extends RuntimeException {

    public ExamAlreadyExistsException() {
        super("Exam already exists");
    }
}
