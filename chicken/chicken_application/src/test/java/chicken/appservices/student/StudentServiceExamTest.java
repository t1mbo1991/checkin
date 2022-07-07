package chicken.appservices.student;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import chicken.aggregates.config.ClockConfig;
import chicken.aggregates.config.InternshipConfig;
import chicken.aggregates.exam.Exam;
import chicken.aggregates.exceptions.DateInPastException;
import chicken.aggregates.exceptions.ExamNotExistsException;
import chicken.aggregates.exceptions.StudentNotExistsException;
import chicken.aggregates.student.ExamReference;
import chicken.aggregates.student.Student;
import chicken.aggregates.student.Timespan;
import chicken.repositories.ExamRepository;
import chicken.repositories.StudentRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class StudentServiceExamTest {

    ExamRepository examRepo;
    StudentRepository studentRepo;
    ClockConfig clockConfig;
    InternshipConfig config;

    StudentService studentService;

    @BeforeEach
    void setup() {
        examRepo = mock(ExamRepository.class);
        studentRepo = mock(StudentRepository.class);
        clockConfig = mock(ClockConfig.class);
        config = mock(InternshipConfig.class);

        studentService = new StudentService(examRepo, studentRepo, clockConfig, config);

        when(clockConfig.getSystemDefaultClock()).thenReturn(
            Clock.fixed(Instant.parse("2022-03-02T10:15:00.00Z"), ZoneId.of("Europe/Paris")));
    }

    @Test
    @DisplayName("IllegalArgumentException thrown if gitHubHandle")
    void test_1() {
        //Act
        assertThrows(IllegalArgumentException.class,
            () -> studentService.bookExam(null, 1L));
    }

    @Test
    @DisplayName("ExamNotExistsException thrown if examID is not present in database")
    void test_2() {
        //Act
        assertThrows(ExamNotExistsException.class,
            () -> studentService.bookExam("bernd", 1L));
    }

    @Test
    @DisplayName("StudentNotExists thrown if gitHubHandle is not present in database")
    void test_3() {
        //Arrange
        Timespan timespan = new Timespan(
            LocalDate.of(2022, 3, 18),
            LocalTime.of(9, 30),
            LocalTime.of(10, 30)
        );
        when(examRepo.getExamById(anyLong())).thenReturn(
            Optional.of(new Exam(1L, true, "2444556", "Programmierung", timespan)));
        //Act
        assertThrows(StudentNotExistsException.class,
            () -> studentService.bookExam("bernd", 1L));
    }

    @Test
    @DisplayName("Exam id 1 is added to student")
    void test_4() {
        //Arrange
        Timespan timespan = new Timespan(
            LocalDate.of(2022, 3, 18),
            LocalTime.of(9, 30),
            LocalTime.of(10, 30)
        );

        Set<ExamReference> refs = Set.of(new ExamReference(1L));

        when(examRepo.getExamById(anyLong())).thenReturn(
            Optional.of(new Exam(1L, true, "2444556", "Programmierung", timespan)));
        when(studentRepo.getStudentByGitHubHandle(anyString())).thenReturn(
            Optional.of(new Student(1L, "bernd")));
        //Act
        studentService.bookExam("bernd", 1L);
        //Assert
        ArgumentCaptor<Student> captor = ArgumentCaptor.forClass(Student.class);
        verify(studentRepo).save(captor.capture());
        assertThat(captor.getValue().getGithubHandle()).isEqualTo("bernd");
        assertThat(captor.getValue().getExams()).hasSize(1)
            .containsExactlyInAnyOrderElementsOf(refs);
    }

    @Test
    @DisplayName("Canceling exam in past throws DateInPastException")
    void test_5() {
        //Arrange
        Timespan timespan = new Timespan(
            LocalDate.of(2022, 3, 18),
            LocalTime.of(9, 30),
            LocalTime.of(10, 30)
        );

        when(examRepo.getExamById(anyLong())).thenReturn(
            Optional.of(new Exam(1L, true, "2444556", "Programmierung", timespan)));
        when(studentRepo.getStudentByGitHubHandle(anyString())).thenReturn(
            Optional.of(new Student(1L, "bernd")));

        studentService.bookExam("bernd", 1L);

        when(clockConfig.getSystemDefaultClock()).thenReturn(
            Clock.fixed(Instant.parse("2022-03-19T10:15:00.00Z"), ZoneId.of("Europe/Paris")));

        // Act and Assert
        assertThrows(DateInPastException.class, () ->
            studentService.cancelExam("bernd", 1L)
        );
    }

    @Test
    @DisplayName("Canceling exam on same day throws DateInPastException")
    void test_6() {
        //Arrange
        Timespan timespan = new Timespan(
            LocalDate.of(2022, 3, 18),
            LocalTime.of(9, 30),
            LocalTime.of(10, 30)
        );

        when(examRepo.getExamById(anyLong())).thenReturn(
            Optional.of(new Exam(1L, true, "2444556", "Programmierung", timespan)));
        when(studentRepo.getStudentByGitHubHandle(anyString())).thenReturn(
            Optional.of(new Student(1L, "bernd")));

        studentService.bookExam("bernd", 1L);

        when(clockConfig.getSystemDefaultClock()).thenReturn(
            Clock.fixed(Instant.parse("2022-03-18T10:15:00.00Z"), ZoneId.of("Europe/Paris")));

        // Act and Assert
        assertThrows(DateInPastException.class, () ->
            studentService.cancelExam("bernd", 1L)
        );
    }
}
