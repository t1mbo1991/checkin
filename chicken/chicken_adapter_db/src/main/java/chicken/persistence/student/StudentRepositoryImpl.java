package chicken.persistence.student;

import chicken.aggregates.student.Student;
import chicken.persistence.student.datarepo.DbStudentRepository;
import chicken.persistence.student.dto.ExamReference;
import chicken.persistence.student.dto.Holiday;
import chicken.repositories.StudentRepository;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;


@Repository
public class StudentRepositoryImpl implements StudentRepository {

    private final DbStudentRepository studentRepository;

    public StudentRepositoryImpl(DbStudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id)
            .map(chicken.persistence.student.dto.Student::createStudent);
    }

    @Override
    public Optional<Student> getStudentByGitHubHandle(String handle) {
        return studentRepository.findStudentByGitHubHandle(handle)
            .map(chicken.persistence.student.dto.Student::createStudent);
    }

    @Override
    public Student save(Student student) {
        return studentRepository.save(
            new chicken.persistence.student.dto.Student(
                student.getId(),
                student.getGithubHandle(),
                student.getExams().stream()
                    .map(e -> new ExamReference(e.id(), student.getId()))
                    .collect(Collectors.toSet()),
                student.getHolidays().stream()
                    .map(h -> new Holiday(
                        h.getId(),
                        h.getDate(),
                        h.getStart(),
                        h.getEnd()
                    )).collect(Collectors.toSet())
            )
        ).createStudent();
    }
}
