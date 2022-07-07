package chicken.persistence.student.datarepo;

import chicken.persistence.student.dto.Student;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface DbStudentRepository extends CrudRepository<Student, Long> {

    Optional<Student> findStudentByGitHubHandle(String gitHubHandle);
}
