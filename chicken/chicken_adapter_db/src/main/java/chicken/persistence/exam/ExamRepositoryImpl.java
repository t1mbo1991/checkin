package chicken.persistence.exam;

import chicken.aggregates.exam.Exam;
import chicken.persistence.exam.datarepo.DbExamRepository;
import chicken.repositories.ExamRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class ExamRepositoryImpl implements ExamRepository {

    private final DbExamRepository examRepository;

    public ExamRepositoryImpl(DbExamRepository examRepository) {
        this.examRepository = examRepository;
    }

    @Override
    public List<Exam> getAllExams() {
        return examRepository.findAll().stream()
            .map(chicken.persistence.exam.dto.Exam::createExam)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<Exam> getExamById(Long id) {
        return examRepository.findById(id)
            .map(chicken.persistence.exam.dto.Exam::createExam);
    }

    @Override
    public Exam save(Exam exam) {
        return examRepository.save(
            new chicken.persistence.exam.dto.Exam(
                exam.getId(),
                exam.getPresence(),
                exam.getEventId(),
                exam.getName(),
                exam.getDate(),
                exam.getStart(),
                exam.getEnd()
            )
        ).createExam();
    }

    @Override
    public List<Exam> getAllExamsByStudentId(Long id) {
        return examRepository.findAllByStudentId(id).stream()
            .map(chicken.persistence.exam.dto.Exam::createExam)
            .collect(Collectors.toList());
    }
}
