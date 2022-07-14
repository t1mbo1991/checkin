package chicken.appservices.exam;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import chicken.aggregates.config.ClockConfig;
import chicken.aggregates.config.InternshipConfig;
import chicken.aggregates.exam.Exam;
import chicken.aggregates.exceptions.ExamAlreadyExistsException;
import chicken.aggregates.exceptions.InvalidEventException;
import chicken.aggregates.student.Timespan;
import chicken.repositories.ExamRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class ExamServiceTest {

    private ExamRepository examRepository;
    private ExamService examService;

    @BeforeEach
    void setup() {
        examRepository = mock(ExamRepository.class);
        InternshipConfig internshipConfig = mock(InternshipConfig.class);
        ClockConfig clockConfig = mock(ClockConfig.class);
        examService = new ExamService(examRepository, internshipConfig, clockConfig);

        when(clockConfig.getSystemDefaultClock()).thenReturn(
            Clock.fixed(Instant.parse("2022-03-02T10:15:00.00Z"), ZoneId.of("Europe/Paris")));

        when(internshipConfig.getMaxHolidays()).thenReturn(240);
        when(internshipConfig.getStartDate()).thenReturn(LocalDate.of(2022, 3, 7));
        when(internshipConfig.getEndDate()).thenReturn(LocalDate.of(2022, 3, 25));
        when(internshipConfig.getStartTime()).thenReturn(LocalTime.of(9, 30));
        when(internshipConfig.getEndTime()).thenReturn(LocalTime.of(13, 30));
    }

    @Test
    @DisplayName("Creating Exam with invalid combination of name and eventID throws exception")
    void test_1() {
        assertThrows(InvalidEventException.class, () -> examService.createExam(
            true,
            "225642",
            "Theoretische Informatik",
            LocalDate.of(2022, 3, 17),
            LocalTime.of(9, 30),
            LocalTime.of(10, 30)));
    }

    @Test
    @DisplayName("It is not possible to create two exams with same id and timespan")
    void test_2() {
        Timespan timespan = new Timespan(LocalDate.of(2022, 3, 17),
            LocalTime.of(9, 30),
            LocalTime.of(10, 30));
        when(examRepository.getAllExams()).thenReturn(List.of(new Exam(null, true,
            "225641",
            "Theoretische Informatik",
            timespan)));

        assertThrows(ExamAlreadyExistsException.class, () -> examService.createExam(
            true,
            "225641",
            "Theoretische Informatik",
            LocalDate.of(2022, 3, 17),
            LocalTime.of(9, 30),
            LocalTime.of(10, 30)));
    }

    @Test
    @DisplayName("Single Exam can be created")
    void test_3() {
        //Arrange + Act
        examService.createExam(true,
            "225641",
            "Theoretische Informatik",
            LocalDate.of(2022, 3, 17),
            LocalTime.of(9, 30),
            LocalTime.of(10, 30));

        //Assert
        ArgumentCaptor<Exam> captor = ArgumentCaptor.forClass(Exam.class);
        verify(examRepository).save(captor.capture());
        assertThat(captor.getValue().getEventId()).isEqualTo("225641");
    }

    @Test
    @DisplayName("Two different Exams can be created")
    void test_4() {
        //Arrange + Act
        Timespan timespan = new Timespan(
            LocalDate.of(2022, 3, 17),
            LocalTime.of(9, 30),
            LocalTime.of(10, 30));

        when(examRepository.getAllExams()).thenReturn(List.of(new Exam(null,
            true,
            "225641",
            "Theoretische Informatik",
            timespan
        )));

        examService.createExam(true,
            "224041",
            "Hardwarenahe Programmierung",
            LocalDate.of(2022, 3, 17),
            LocalTime.of(9, 30),
            LocalTime.of(10, 30));

        //Assert
        ArgumentCaptor<Exam> captor = ArgumentCaptor.forClass(Exam.class);
        verify(examRepository).save(captor.capture());
        assertThat(captor.getValue().getEventId()).isEqualTo("224041");
    }
}
