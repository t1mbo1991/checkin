package chicken.persistence.student;

import static org.assertj.core.api.Assertions.assertThat;

import chicken.aggregates.student.ExamReference;
import chicken.aggregates.student.Holiday;
import chicken.aggregates.student.Student;
import chicken.aggregates.student.Timespan;
import chicken.persistence.student.datarepo.DbStudentRepository;
import chicken.repositories.StudentRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

@Sql({
    "classpath:db/data.sql"
})
@DataJdbcTest
@ActiveProfiles("test")
public class StudentRepositoryTests {

    @Autowired
    private DbStudentRepository dbStudentRepository;

    private StudentRepository studentRepository;

    @BeforeEach
    void setup() {
        studentRepository = new StudentRepositoryImpl(dbStudentRepository);
    }

    @Test
    @DisplayName("Get student by id 1")
    void test_1() {
        // Arrange
        Timespan timespan = new Timespan(
            LocalDate.of(2022, 3, 23),
            LocalTime.of(9, 30),
            LocalTime.of(10, 0)
        );

        Set<ExamReference> examReferences = Set.of(new ExamReference(1L), new ExamReference(2L));
        Set<Holiday> holidays = Set.of(new Holiday(1L, timespan));

        Student student = new Student(
            1L,
            "tiemotm",
            examReferences,
            holidays
        );

        // Act
        Optional<Student> result = studentRepository.getStudentById(1L);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(student);
    }

    @Test
    @DisplayName("Get student by gitHubHandle")
    void test_2() {
        // Arrange
        Timespan timespan = new Timespan(
            LocalDate.of(2022, 3, 23),
            LocalTime.of(10, 30),
            LocalTime.of(11, 0)
        );

        Set<ExamReference> examReferences = Set.of(new ExamReference(2L));
        Set<Holiday> holidays = Set.of(new Holiday(2L, timespan));

        Student student = new Student(2L, "jatsc104", examReferences, holidays);

        // Act
        Optional<Student> result = studentRepository.getStudentByGitHubHandle("jatsc104");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(student);
    }

    @Test
    @DisplayName("Save new Student")
    void test_3() {
        //Arrange
        Student student = new Student(null, "foo");

        //Act
        Student resultStudent = studentRepository.save(student);

        //Assert
        assertThat(resultStudent).isNotNull();
        assertThat(studentRepository.getStudentById(resultStudent.getId())).isPresent();
    }

    @Test
    @DisplayName("Update Student with Id 1")
    void test_4() {
        //Arrange
        Student student = new Student(1L, "foo");

        //Act
        Student resultStudent = studentRepository.save(student);

        //Assert
        assertThat(resultStudent).isNotNull();
        assertThat(studentRepository.getStudentById(resultStudent.getId())).isPresent();
        assertThat(resultStudent.getGithubHandle()).isEqualTo("foo");
    }
}
