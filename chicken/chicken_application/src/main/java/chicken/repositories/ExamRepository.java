package chicken.repositories;

import chicken.aggregates.exam.Exam;
import java.util.List;
import java.util.Optional;

public interface ExamRepository {

    List<Exam> getAllExams();

    Optional<Exam> getExamById(Long id);

    Exam save(Exam exam);

    List<Exam> getAllExamsByStudentId(Long id);
}
