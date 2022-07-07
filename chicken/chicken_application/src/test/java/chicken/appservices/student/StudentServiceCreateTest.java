package chicken.appservices.student;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import chicken.aggregates.config.ClockConfig;
import chicken.aggregates.config.InternshipConfig;
import chicken.aggregates.exceptions.StudentAlreadyExiststException;
import chicken.aggregates.student.Student;
import chicken.repositories.ExamRepository;
import chicken.repositories.StudentRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class StudentServiceCreateTest {

    ExamRepository examRepo;
    StudentRepository studentRepo;
    ClockConfig clockConfig;
    InternshipConfig internshipConfig;

    StudentService studentService;

    @BeforeEach
    void setup() {
        examRepo = mock(ExamRepository.class);
        studentRepo = mock(StudentRepository.class);
        clockConfig = mock(ClockConfig.class);
        internshipConfig = mock(InternshipConfig.class);

        studentService = new StudentService(examRepo, studentRepo, clockConfig, internshipConfig);

        when(clockConfig.getSystemDefaultClock()).thenReturn(
            Clock.fixed(Instant.parse("2022-03-02T10:15:00.00Z"), ZoneId.of("Europe/Paris")));
    }

    @Test
    @DisplayName("IllegalArgumentException thrown if gitHubHandle is null")
    void test_1() {
        //Act
        assertThrows(IllegalArgumentException.class,
            () -> studentService.createStudent(null));
    }

    @Test
    @DisplayName("StudentAlreadyExistsException thrown if student with gitHubHandle foo already exists")
    void test_2() {
        //Arrange
        when(studentRepo.getStudentByGitHubHandle("foo")).thenReturn(
            Optional.of(new Student(1234L, "foo")));
        //Act
        assertThrows(StudentAlreadyExiststException.class,
            () -> studentService.createStudent("foo"));
    }

    @Test
    @DisplayName("if student with gitHubHandle foo does not exist, new student gets saved in Repo")
    void test_3() {
        //Arrange
        when(studentRepo.getStudentByGitHubHandle(anyString())).thenReturn(Optional.empty());
        //Act
        studentService.createStudent("foo");
        //Assert
        ArgumentCaptor<Student> captor = ArgumentCaptor.forClass(Student.class);
        verify(studentRepo).save(captor.capture());
        assertThat(captor.getValue().getGithubHandle()).isEqualTo("foo");
    }

}
