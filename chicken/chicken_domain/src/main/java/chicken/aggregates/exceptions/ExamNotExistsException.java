package chicken.aggregates.exceptions;

public class ExamNotExistsException extends RuntimeException {

    public ExamNotExistsException(Long examId) {
        super("Exam with " + examId + " does not exist");
    }
}
