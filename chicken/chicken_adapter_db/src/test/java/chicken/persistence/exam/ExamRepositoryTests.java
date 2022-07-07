package chicken.persistence.exam;

import static org.assertj.core.api.Assertions.assertThat;

import chicken.aggregates.exam.Exam;
import chicken.aggregates.student.Timespan;
import chicken.persistence.exam.datarepo.DbExamRepository;
import chicken.repositories.ExamRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
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
public class ExamRepositoryTests {

    @Autowired
    private DbExamRepository dbExamRepository;

    private ExamRepository examRepository;

    @BeforeEach
    void setup() {
        examRepository = new ExamRepositoryImpl(dbExamRepository);
    }

    @Test
    @DisplayName("List of all exams is loaded")
    void test_1() {
        // Arrange
        Timespan timespan1 = new Timespan(
            LocalDate.of(2022, 3, 17),
            LocalTime.of(13, 0),
            LocalTime.of(14, 0)
        );
        Timespan timespan2 = new Timespan(
            LocalDate.of(2022, 3, 17),
            LocalTime.of(8, 0),
            LocalTime.of(10, 30)
        );

        List<Exam> exams = List.of(
            new Exam(1L, false, "123", "Propra", timespan1),
            new Exam(2L, false, "1234", "Propra 2", timespan2),
            new Exam(3L, true, "223", "Aldat", timespan2));

        // Act
        List<Exam> result = examRepository.getAllExams();

        // Assert
        assertThat(result).containsExactlyInAnyOrderElementsOf(exams);
    }

    @Test
    @DisplayName("Get exam by id 1")
    void test_2() {
        // Arrange
        Timespan timespan = new Timespan(
            LocalDate.of(2022, 3, 17),
            LocalTime.of(13, 0),
            LocalTime.of(14, 0)
        );
        Exam exam = new Exam(1L, false, "123", "Propra", timespan);

        // Act
        Optional<Exam> result = examRepository.getExamById(1L);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(exam);
    }

    @Test
    @DisplayName("Save exam with already used event id")
    void test_3() {
        // Arrange
        Timespan timespan = new Timespan(
            LocalDate.of(2022, 3, 17),
            LocalTime.of(12, 0),
            LocalTime.of(14, 0)
        );
        Exam exam = new Exam(null, false, "123", "Propra Praktikum", timespan);

        // Act
        Exam result = examRepository.save(exam);

        // Assert
        assertThat(result).isNotNull();
        assertThat(examRepository.getExamById(result.getId())).isPresent();
    }

    @Test
    @DisplayName("Get all exams by student id")
    void test_4() {
        // Arrange
        Timespan timespan1 = new Timespan(
            LocalDate.of(2022, 3, 22),
            LocalTime.of(13, 0),
            LocalTime.of(14, 0)
        );
        Timespan timespan2 = new Timespan(
            LocalDate.of(2022, 3, 23),
            LocalTime.of(8, 0),
            LocalTime.of(10, 30)
        );

        List<Exam> exams = List.of(
            new Exam(1L, false, "123", "Propra", timespan1),
            new Exam(2L, false, "1234", "Propra 2", timespan2)
        );

        // Act
        List<Exam> result = examRepository.getAllExamsByStudentId(1L);

        // Assert
        assertThat(result).containsExactlyInAnyOrderElementsOf(exams);
    }
}
