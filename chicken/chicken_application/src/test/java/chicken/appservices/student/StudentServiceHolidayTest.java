package chicken.appservices.student;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import chicken.aggregates.config.ClockConfig;
import chicken.aggregates.config.InternshipConfig;
import chicken.aggregates.exceptions.DateInPastException;
import chicken.aggregates.exceptions.DateOutOfBoundsException;
import chicken.aggregates.exceptions.HolidayEndOutOfBoundsException;
import chicken.aggregates.exceptions.HolidayStartOutOfBoundsException;
import chicken.aggregates.exceptions.InvalidEndTimeException;
import chicken.aggregates.exceptions.InvalidStartTimeException;
import chicken.aggregates.exceptions.InvalidTimeBlockException;
import chicken.aggregates.student.Holiday;
import chicken.aggregates.student.Student;
import chicken.aggregates.student.Timespan;
import chicken.repositories.ExamRepository;
import chicken.repositories.StudentRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class StudentServiceHolidayTest {

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

        when(studentRepo.getStudentByGitHubHandle(anyString())).thenReturn(
            Optional.of(new Student(1L, "foo")));

        when(internshipConfig.getMaxHolidays()).thenReturn(240);
        when(internshipConfig.getStartDate()).thenReturn(LocalDate.of(2022, 3, 7));
        when(internshipConfig.getEndDate()).thenReturn(LocalDate.of(2022, 3, 25));
        when(internshipConfig.getStartTime()).thenReturn(LocalTime.of(9, 30));
        when(internshipConfig.getEndTime()).thenReturn(LocalTime.of(13, 30));
    }

    @Test
    @DisplayName("booking holiday after internship forbidden (date)")
    void test_1() {
        assertThrows(DateOutOfBoundsException.class, () ->
            studentService.bookHoliday(
                "foo",
                LocalDate.of(2022, 4, 11),
                LocalTime.of(12, 0),
                LocalTime.of(13, 0)
            )
        );
    }

    @Test
    @DisplayName("booking holiday before internship forbidden (date)")
    void test_2() {
        assertThrows(DateOutOfBoundsException.class, () ->
            studentService.bookHoliday(
                "foo",
                LocalDate.of(2022, 3, 3),
                LocalTime.of(12, 0),
                LocalTime.of(13, 0)
            )
        );
    }

    @Test
    @DisplayName("booking holiday on weekends forbidden")
    void test_3() {
        assertThrows(DateOutOfBoundsException.class, () ->
            studentService.bookHoliday(
                "foo",
                LocalDate.of(2022, 3, 19),
                LocalTime.of(12, 0),
                LocalTime.of(13, 0)
            )
        );
    }

    @Test
    @DisplayName("booking holiday after internship forbidden (time)")
    void test_4() {
        assertThrows(HolidayEndOutOfBoundsException.class, () ->
            studentService.bookHoliday(
                "foo",
                LocalDate.of(2022, 3, 11),
                LocalTime.of(12, 0),
                LocalTime.of(14, 0)
            )
        );
    }

    @Test
    @DisplayName("booking holiday before internship forbidden (time)")
    void test_5() {
        assertThrows(HolidayStartOutOfBoundsException.class, () ->
            studentService.bookHoliday(
                "foo",
                LocalDate.of(2022, 3, 11),
                LocalTime.of(9, 0),
                LocalTime.of(13, 0)
            )
        );
    }

    @Test
    @DisplayName("booking holiday end before start forbidden (time)")
    void test_6() {
        assertThrows(IllegalArgumentException.class, () ->
            studentService.bookHoliday(
                "foo",
                LocalDate.of(2022, 3, 11),
                LocalTime.of(13, 0),
                LocalTime.of(10, 0)
            )
        );
    }

    @Test
    @DisplayName("booking empty (null) holiday forbidden")
    void test_7() {
        assertThrows(IllegalArgumentException.class, () ->
            studentService.bookHoliday(
                "foo",
                null,
                LocalTime.of(13, 0),
                null
            )
        );
    }

    @Test
    @DisplayName("booking holiday with starting time not a multiple of 15 minutes results in InvalidTimeException")
    void test_8() {
        assertThrows(InvalidStartTimeException.class, () ->
            studentService.bookHoliday(
                "foo",
                LocalDate.of(2022, 3, 14),
                LocalTime.of(12, 20),
                LocalTime.of(13, 0)
            )
        );
    }

    @Test
    @DisplayName("booking holiday with end time not a multiple of 15 minutes results in InvalidTimeException")
    void test_9() {
        assertThrows(InvalidEndTimeException.class, () ->
            studentService.bookHoliday(
                "foo",
                LocalDate.of(2022, 3, 14),
                LocalTime.of(12, 0),
                LocalTime.of(13, 5)
            )
        );
    }

    @Test
    @DisplayName("booking holiday with 3 hours duration results in InvalidTimeBlockException")
    void test_10() {
        assertThrows(InvalidTimeBlockException.class, () ->
            studentService.bookHoliday(
                "foo",
                LocalDate.of(2022, 3, 14),
                LocalTime.of(9, 30),
                LocalTime.of(12, 30)
            )
        );
    }

    @Test
    @DisplayName("booking holiday with 2,5 hours duration is allowed")
    void test_11() {
        // Arrange
        Holiday holiday = new Holiday(
            null,
            new Timespan(
                LocalDate.of(2022, 3, 14),
                LocalTime.of(9, 30),
                LocalTime.of(12, 0)
            )
        );
        Set<Holiday> holidays = Set.of(holiday);

        // Act
        studentService.bookHoliday(
            "foo",
            LocalDate.of(2022, 3, 14),
            LocalTime.of(9, 30),
            LocalTime.of(12, 0)
        );

        // Assert
        ArgumentCaptor<Student> captor = ArgumentCaptor.forClass(Student.class);
        verify(studentRepo).save(captor.capture());
        Student result = captor.getValue();
        assertThat(result.getRemainingHoliday(internshipConfig.getMaxHolidays())).isEqualTo(90);
        assertThat(result.getHolidays()).hasSize(1).containsExactlyInAnyOrderElementsOf(holidays);
    }

    @Test
    @DisplayName("booking holiday with 4 hours at start of internship duration is allowed")
    void test_12() {
        // Arrange
        Holiday holiday = new Holiday(
            null,
            new Timespan(
                LocalDate.of(2022, 3, 7),
                LocalTime.of(9, 30),
                LocalTime.of(13, 30)
            )
        );
        Set<Holiday> holidays = Set.of(holiday);

        // Act
        studentService.bookHoliday(
            "foo",
            LocalDate.of(2022, 3, 7),
            LocalTime.of(9, 30),
            LocalTime.of(13, 30)
        );

        // Assert
        ArgumentCaptor<Student> captor = ArgumentCaptor.forClass(Student.class);
        verify(studentRepo).save(captor.capture());
        Student result = captor.getValue();
        assertThat(result.getRemainingHoliday(internshipConfig.getMaxHolidays())).isEqualTo(0);
        assertThat(result.getHolidays()).hasSize(1).containsExactlyInAnyOrderElementsOf(holidays);
    }

    @Test
    @DisplayName("booking holiday with half an hour at end of internship duration is allowed")
    void test_13() {
        // Arrange
        Holiday holiday = new Holiday(
            null,
            new Timespan(
                LocalDate.of(2022, 3, 25),
                LocalTime.of(9, 45),
                LocalTime.of(10, 15)
            )
        );
        Set<Holiday> holidays = Set.of(holiday);

        // Act
        studentService.bookHoliday(
            "foo",
            LocalDate.of(2022, 3, 25),
            LocalTime.of(9, 45),
            LocalTime.of(10, 15)
        );

        // Assert
        ArgumentCaptor<Student> captor = ArgumentCaptor.forClass(Student.class);
        verify(studentRepo).save(captor.capture());
        Student result = captor.getValue();
        assertThat(result.getRemainingHoliday(internshipConfig.getMaxHolidays())).isEqualTo(210);
        assertThat(result.getHolidays()).hasSize(1).containsExactlyInAnyOrderElementsOf(holidays);
    }

    @Test
    @DisplayName("booking two holidays of half an hour on different days is allowed")
    void test_14() {
        // Arrange
        Holiday holiday1 = new Holiday(
            null,
            new Timespan(
                LocalDate.of(2022, 3, 7),
                LocalTime.of(9, 45),
                LocalTime.of(10, 15)
            )
        );
        Holiday holiday2 = new Holiday(
            null,
            new Timespan(
                LocalDate.of(2022, 3, 8),
                LocalTime.of(9, 45),
                LocalTime.of(10, 15)
            )
        );
        Set<Holiday> holidays = Set.of(holiday1, holiday2);

        // Act
        studentService.bookHoliday(
            "foo",
            LocalDate.of(2022, 3, 7),
            LocalTime.of(9, 45),
            LocalTime.of(10, 15)
        );
        studentService.bookHoliday(
            "foo",
            LocalDate.of(2022, 3, 7),
            LocalTime.of(9, 45),
            LocalTime.of(10, 15)
        );

        // Assert
        ArgumentCaptor<Student> captor = ArgumentCaptor.forClass(Student.class);
        verify(studentRepo, times(2)).save(captor.capture());
    }


    @Test
    @DisplayName("Holiday is cancelled")
    void test_15() {
        //Arrange
        Timespan timespan1 = new Timespan(
            LocalDate.of(2022, 3, 18),
            LocalTime.of(10, 30),
            LocalTime.of(11, 30));
        Timespan timespan2 = new Timespan(
            LocalDate.of(2022, 3, 18),
            LocalTime.of(12, 30),
            LocalTime.of(13, 30));

        Holiday holiday1 = new Holiday(1L, timespan1);
        Holiday holiday2 = new Holiday(2L, timespan2);

        Set<Holiday> holidays = new HashSet<>();
        holidays.add(holiday1);
        holidays.add(holiday2);

        Student student = new Student(1L, "bernd", null, holidays);

        when(studentRepo.getStudentByGitHubHandle(anyString())).thenReturn(Optional.of(student));

        Set<Holiday> result = Set.of(new Holiday(2L, new Timespan(
            LocalDate.of(2022, 3, 18),
            LocalTime.of(12, 30),
            LocalTime.of(13, 30))
        ));

        //Act
        studentService.cancelHoliday("bernd", 1L);
        //Assert
        assertThat(student.getHolidays()).containsExactlyInAnyOrderElementsOf(result);
    }

    @Test
    @DisplayName("Cancel holiday in the past throws DateInPastException")
    void test_16() {
        // Arrange
        Timespan timespan = new Timespan(
            LocalDate.of(2022, 3, 18),
            LocalTime.of(10, 30),
            LocalTime.of(11, 30));

        Set<Holiday> holidays = Set.of(new Holiday(1L, timespan));

        Student student = new Student(1L, "bernd", null, holidays);

        when(studentRepo.getStudentByGitHubHandle(anyString())).thenReturn(Optional.of(student));

        when(clockConfig.getSystemDefaultClock()).thenReturn(
            Clock.fixed(Instant.parse("2022-03-19T10:15:00.00Z"), ZoneId.of("Europe/Paris")));

        // Act and Assert
        assertThrows(DateInPastException.class, () ->
            studentService.cancelHoliday("bernd", 1L)
        );
    }

    @Test
    @DisplayName("Cancel holiday on same day throws DateInPastException")
    void test_17() {
        // Arrange
        Timespan timespan = new Timespan(
            LocalDate.of(2022, 3, 18),
            LocalTime.of(10, 30),
            LocalTime.of(11, 30));

        Set<Holiday> holidays = Set.of(new Holiday(1L, timespan));

        Student student = new Student(1L, "bernd", null, holidays);

        when(studentRepo.getStudentByGitHubHandle(anyString())).thenReturn(Optional.of(student));

        when(clockConfig.getSystemDefaultClock()).thenReturn(
            Clock.fixed(Instant.parse("2022-03-18T10:15:00.00Z"), ZoneId.of("Europe/Paris")));

        // Act and Assert
        assertThrows(DateInPastException.class, () ->
            studentService.cancelHoliday("bernd", 1L)
        );
    }
}
