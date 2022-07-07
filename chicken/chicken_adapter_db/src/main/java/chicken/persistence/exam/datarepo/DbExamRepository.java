package chicken.persistence.exam.datarepo;

import chicken.persistence.exam.dto.Exam;
import java.util.List;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface DbExamRepository extends CrudRepository<Exam, Long> {

    @Override
    List<Exam> findAll();

    @Query("""
        SELECT EXAM.* FROM EXAM
        JOIN STUDENT_EXAM ON EXAM.id = STUDENT_EXAM.exam
        WHERE STUDENT_EXAM.student = :id;
        """)
    List<Exam> findAllByStudentId(@Param("id") Long id);

}
