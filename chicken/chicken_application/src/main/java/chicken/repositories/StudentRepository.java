package chicken.repositories;

import chicken.aggregates.student.Student;
import java.util.Optional;

public interface StudentRepository {

    Optional<Student> getStudentById(Long id);

    Optional<Student> getStudentByGitHubHandle(String handle);

    Student save(Student student);
}
