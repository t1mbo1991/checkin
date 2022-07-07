package chicken.aggregates.student;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import chicken.aggregates.config.InternshipConfig;
import chicken.aggregates.exam.Exam;
import chicken.aggregates.exceptions.DateOutOfBoundsException;
import chicken.aggregates.exceptions.HolidayEndOutOfBoundsException;
import chicken.aggregates.exceptions.HolidayStartOutOfBoundsException;
import chicken.aggregates.exceptions.HolidayTimeExceededException;
import chicken.aggregates.exceptions.InvalidEndTimeException;
import chicken.aggregates.exceptions.InvalidHolidaySegmentationException;
import chicken.aggregates.exceptions.InvalidStartTimeException;
import chicken.aggregates.exceptions.InvalidTimeBlockException;
import chicken.aggregates.exceptions.NumberOfHolidayBlocksExceededException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class StudentTests {

    private InternshipConfig config;
    private Student student;

    @BeforeEach
    void beforeEach() {
        student = new Student(null, "ronny123");

        config = mock(InternshipConfig.class);

        when(config.getMaxHolidays()).thenReturn(240);
        when(config.getStartDate()).thenReturn(LocalDate.of(2022, 3, 7));
        when(config.getEndDate()).thenReturn(LocalDate.of(2022, 3, 25));
        when(config.getStartTime()).thenReturn(LocalTime.of(9, 30));
        when(config.getEndTime()).thenReturn(LocalTime.of(13, 30));
    }

    @Test
    @DisplayName("Holiday is added to student")
    void test_1() {
        // Arrange
        Timespan timespan = new Timespan(
            LocalDate.of(2022, 3, 14),
            LocalTime.of(9, 30),
            LocalTime.of(10, 30)
        );
        Holiday holiday = new Holiday(null, timespan);
        Set<Holiday> result = Set.of(holiday);

        // Act
        student.addHoliday(
            LocalDate.of(2022, 3, 14),
            LocalTime.of(9, 30),
            LocalTime.of(10, 30),
            config
        );

        // Assert
        assertThat(student.getHolidays()).containsExactlyInAnyOrderElementsOf(result);
    }

    @Test
    @DisplayName("Adding no holiday results in 240 minutes remaining")
    void test_2() {
        //Act
        long remainingHoliday = student.getRemainingHoliday(config.getMaxHolidays());

        //Assert
        assertThat(remainingHoliday).isEqualTo(240);
    }

    @Test
    @DisplayName("Adding 1 hour of holiday results in 180 minutes remaining")
    void test_3() {
        // Arrange
        Timespan timespan = new Timespan(
            LocalDate.of(2022, 3, 9),
            LocalTime.of(9, 30),
            LocalTime.of(10, 30)
        );
        Holiday holiday = new Holiday(null, timespan);

        student.addHoliday(
            LocalDate.of(2022, 3, 9),
            LocalTime.of(9, 30),
            LocalTime.of(10, 30),
            config
        );

        //Act
        long remainingHoliday = student.getRemainingHoliday(config.getMaxHolidays());

        //Assert
        assertThat(remainingHoliday).isEqualTo(180);
    }

    @Test
    @DisplayName("Exam is added to student")
    void test_4() {
        // Arrange
        Timespan timespan = new Timespan(
            LocalDate.of(2022, 3, 9),
            LocalTime.of(9, 30),
            LocalTime.of(10, 30)
        );
        Exam exam = new Exam(1L, true, "2", "Funktionale Programmierung", timespan);
        Set<ExamReference> examReferences = Set.of(new ExamReference(1L));

        // Act
        student.addExam(exam);

        // Assert
        assertThat(student.getExams()).containsExactlyInAnyOrderElementsOf(examReferences);
    }

    @Test
    @DisplayName("booking holiday after internship forbidden (date)")
    void test_5() {
        assertThrows(DateOutOfBoundsException.class, () ->
            student.addHoliday(
                LocalDate.of(2022, 4, 11),
                LocalTime.of(12, 0),
                LocalTime.of(13, 0),
                config
            )
        );
    }

    @Test
    @DisplayName("booking holiday before internship forbidden (date)")
    void test_6() {
        assertThrows(DateOutOfBoundsException.class, () ->
            student.addHoliday(
                LocalDate.of(2022, 2, 10),
                LocalTime.of(12, 0),
                LocalTime.of(13, 0),
                config
            )
        );
    }

    @Test
    @DisplayName("booking holiday on weekends forbidden")
    void test_7() {
        assertThrows(DateOutOfBoundsException.class, () ->
            student.addHoliday(
                LocalDate.of(2022, 3, 19),
                LocalTime.of(12, 0),
                LocalTime.of(13, 0),
                config
            )
        );
    }

    @Test
    @DisplayName("booking holiday after internship forbidden (time)")
    void test_8() {
        assertThrows(HolidayEndOutOfBoundsException.class, () ->
            student.addHoliday(
                LocalDate.of(2022, 3, 11),
                LocalTime.of(12, 0),
                LocalTime.of(14, 0),
                config
            )
        );
    }

    @Test
    @DisplayName("booking holiday before internship forbidden (time)")
    void test_9() {
        assertThrows(HolidayStartOutOfBoundsException.class, () ->
            student.addHoliday(
                LocalDate.of(2022, 3, 11),
                LocalTime.of(9, 0),
                LocalTime.of(13, 0),
                config
            )
        );
    }

    @Test
    @DisplayName("booking holiday with starting time not a multiple of 15 minutes results in InvalidTimeException")
    void test_10() {
        assertThrows(InvalidStartTimeException.class, () ->
            student.addHoliday(
                LocalDate.of(2022, 3, 14),
                LocalTime.of(12, 20),
                LocalTime.of(13, 0),
                config
            )
        );
    }

    @Test
    @DisplayName("booking holiday with end time not a multiple of 15 minutes results in InvalidTimeException")
    void test_11() {
        assertThrows(InvalidEndTimeException.class, () ->
            student.addHoliday(
                LocalDate.of(2022, 3, 14),
                LocalTime.of(12, 0),
                LocalTime.of(13, 5),
                config
            )
        );
    }

    @Test
    @DisplayName("booking holiday with 3 hours duration results in InvalidTimeBlockException")
    void test_12() {
        assertThrows(InvalidTimeBlockException.class, () ->
            student.addHoliday(
                LocalDate.of(2022, 3, 14),
                LocalTime.of(9, 30),
                LocalTime.of(12, 30),
                config
            )
        );
    }

    @Test
    @DisplayName("booking holiday overlap at end")
    void test_13() {
        // Arrange
        Set<Holiday> resultHolidays = Set.of(new Holiday(null, new Timespan(
            LocalDate.of(2022, 3, 15),
            LocalTime.of(9, 30),
            LocalTime.of(10, 30)
        )));

        student.addHoliday(
            LocalDate.of(2022, 3, 15),
            LocalTime.of(9, 30),
            LocalTime.of(10, 0),
            config
        );

        // Act
        student.addHoliday(
            LocalDate.of(2022, 3, 15),
            LocalTime.of(10, 0),
            LocalTime.of(10, 30),
            config
        );

        // Assert
        assertThat(student.getHolidays()).hasSize(1)
            .containsExactlyInAnyOrderElementsOf(resultHolidays);
    }

    @Test
    @DisplayName("booking holiday connecting old ones")
    void test_14() {
        // Arrange
        Set<Holiday> resultHolidays = Set.of(new Holiday(null, new Timespan(
            LocalDate.of(2022, 3, 15),
            LocalTime.of(9, 30),
            LocalTime.of(13, 30)
        )));

        student.addHoliday(
            LocalDate.of(2022, 3, 15),
            LocalTime.of(9, 30),
            LocalTime.of(10, 0),
            config
        );
        student.addHoliday(
            LocalDate.of(2022, 3, 15),
            LocalTime.of(13, 0),
            LocalTime.of(13, 30),
            config
        );

        // Act
        student.addHoliday(
            LocalDate.of(2022, 3, 15),
            LocalTime.of(10, 0),
            LocalTime.of(13, 0),
            config
        );

        // Assert
        assertThat(student.getHolidays()).hasSize(1)
            .containsExactlyInAnyOrderElementsOf(resultHolidays);
    }

    @Test
    @DisplayName("booking overlapping holiday which is invalid throws InvalidTimeBlockException")
    void test_15() {
        // Arrange
        student.addHoliday(
            LocalDate.of(2022, 3, 15),
            LocalTime.of(9, 30),
            LocalTime.of(10, 30),
            config
        );

        // Act and Assert
        assertThrows(InvalidTimeBlockException.class, () ->
            student.addHoliday(
                LocalDate.of(2022, 3, 15),
                LocalTime.of(10, 0),
                LocalTime.of(12, 30),
                config
            )
        );
    }

    @Test
    @DisplayName("booking more than 4 hours of holiday throws HolidayTimeExceededException")
    void test_16() {
        // Arrange
        student.addHoliday(
            LocalDate.of(2022, 3, 15),
            LocalTime.of(9, 30),
            LocalTime.of(13, 30),
            config
        );

        // Act and Assert
        assertThrows(HolidayTimeExceededException.class, () ->
            student.addHoliday(
                LocalDate.of(2022, 3, 16),
                LocalTime.of(10, 0),
                LocalTime.of(12, 30),
                config
            )
        );
    }

    @Test
    @DisplayName("adding two non overlapping holidays each exclusively on end or start")
    void test_17() {
        // Arrange

        Set<Holiday> resultHolidays = Set.of(
            new Holiday(null, new Timespan(
                LocalDate.of(2022, 3, 15),
                LocalTime.of(9, 30),
                LocalTime.of(10, 30)
            )),
            new Holiday(null, new Timespan(
                LocalDate.of(2022, 3, 15),
                LocalTime.of(12, 30),
                LocalTime.of(13, 30)
            ))
        );

        student.addHoliday(
            LocalDate.of(2022, 3, 15),
            LocalTime.of(9, 30),
            LocalTime.of(10, 30),
            config
        );

        // Act
        student.addHoliday(
            LocalDate.of(2022, 3, 15),
            LocalTime.of(12, 30),
            LocalTime.of(13, 30),
            config
        );

        // Assert
        assertThat(student.getHolidays()).hasSize(2)
            .containsExactlyInAnyOrderElementsOf(resultHolidays);
    }

    @Test
    @DisplayName("booking two non-overlapping holidays with one on start and one in the middle throws InvalidHolidaySegmentationException")
    void test_18() {
        // Arrange
        student.addHoliday(
            LocalDate.of(2022, 3, 15),
            LocalTime.of(9, 30),
            LocalTime.of(10, 30),
            config
        );

        // Act and Assert
        assertThrows(InvalidHolidaySegmentationException.class, () ->
            student.addHoliday(
                LocalDate.of(2022, 3, 15),
                LocalTime.of(11, 0),
                LocalTime.of(12, 0),
                config
            )
        );
    }

    @Test
    @DisplayName("adding a third holiday connecting two valid holidays on the same day")
    void test_19() {
        // Arrange

        Set<Holiday> resultHolidays = Set.of(
            new Holiday(null, new Timespan(
                LocalDate.of(2022, 3, 15),
                LocalTime.of(9, 30),
                LocalTime.of(13, 30)
            ))
        );

        student.addHoliday(
            LocalDate.of(2022, 3, 15),
            LocalTime.of(9, 30),
            LocalTime.of(10, 30),
            config
        );

        student.addHoliday(
            LocalDate.of(2022, 3, 15),
            LocalTime.of(12, 30),
            LocalTime.of(13, 30),
            config
        );

        // Act
        student.addHoliday(
            LocalDate.of(2022, 3, 15),
            LocalTime.of(10, 30),
            LocalTime.of(12, 30),
            config
        );

        // Assert
        assertThat(student.getHolidays()).hasSize(1)
            .containsExactlyInAnyOrderElementsOf(resultHolidays);
    }

    @Test
    @DisplayName("adding a third holiday in between not connecting two valid holidays on the same day throws NumberOfHolidayBlocksExceededException")
    void test_20() {
        // Arrange
        student.addHoliday(
            LocalDate.of(2022, 3, 15),
            LocalTime.of(9, 30),
            LocalTime.of(10, 30),
            config
        );

        student.addHoliday(
            LocalDate.of(2022, 3, 15),
            LocalTime.of(12, 30),
            LocalTime.of(13, 30),
            config
        );

        // Act and Assert

        assertThrows(NumberOfHolidayBlocksExceededException.class, () ->
            student.addHoliday(
                LocalDate.of(2022, 3, 15),
                LocalTime.of(11, 0),
                LocalTime.of(12, 0),
                config
            )
        );

    }

    @Test
    @DisplayName("booking two holidays with only 60 minutes in between results in InvalidTimeBlockException")
    void test_21() {
        // Arrange
        student.addHoliday(
            LocalDate.of(2022, 3, 15),
            LocalTime.of(9, 30),
            LocalTime.of(10, 30),
            config
        );

        // Act and Assert

        assertThrows(InvalidTimeBlockException.class, () ->
            student.addHoliday(
                LocalDate.of(2022, 3, 15),
                LocalTime.of(11, 30),
                LocalTime.of(13, 30),
                config
            )
        );

    }

    @Test
    @DisplayName("Adding exam within holiday timespan cuts holiday into two new holidays")
    void test_22() {
        //Arrange
        Timespan examTimespan = new Timespan(
            LocalDate.of(2022, 3, 17),
            LocalTime.of(11, 0),
            LocalTime.of(11, 30));

        Timespan preHolidayTimespan = new Timespan(
            LocalDate.of(2022, 3, 17),
            LocalTime.of(9, 30),
            LocalTime.of(10, 30));

        Timespan postHolidayTimespan = new Timespan(
            LocalDate.of(2022, 3, 17),
            LocalTime.of(11, 30),
            LocalTime.of(13, 30));

        Set<Holiday> holidays = Set.of(
            new Holiday(null, preHolidayTimespan),
            new Holiday(null, postHolidayTimespan)
        );

        Exam exam = new Exam(null, false, "222111", "Theo Info", examTimespan);
        student.addHoliday(
            LocalDate.of(2022, 3, 17),
            LocalTime.of(9, 30),
            LocalTime.of(13, 30),
            config);

        //Act
        student.addExam(exam);

        //Assert
        assertThat(student.getHolidays()).hasSize(2).containsExactlyInAnyOrderElementsOf(holidays);
    }

    @Test
    @DisplayName("Adding exam overlapping at the end cuts holiday at the end")
    void test_23() {
        //Arrange
        Timespan examTimespan = new Timespan(
            LocalDate.of(2022, 3, 17),
            LocalTime.of(12, 0),
            LocalTime.of(13, 30));

        Timespan preHolidayTimespan = new Timespan(
            LocalDate.of(2022, 3, 17),
            LocalTime.of(9, 30),
            LocalTime.of(10, 0));

        Set<Holiday> holidays = Set.of(
            new Holiday(null, preHolidayTimespan)
        );

        Exam exam = new Exam(null, true, "222111", "Theo Info", examTimespan);

        student.addHoliday(
            LocalDate.of(2022, 3, 17),
            LocalTime.of(9, 30),
            LocalTime.of(12, 0),
            config);

        //Act
        student.addExam(exam);

        //Assert
        assertThat(student.getHolidays()).hasSize(1).containsExactlyInAnyOrderElementsOf(holidays);
    }

    @Test
    @DisplayName("Adding exam overlapping at the start cuts holiday at the start")
    void test_24() {
        //Arrange
        Timespan examTimespan = new Timespan(
            LocalDate.of(2022, 3, 17),
            LocalTime.of(9, 30),
            LocalTime.of(11, 0));

        Timespan postHolidayTimespan = new Timespan(
            LocalDate.of(2022, 3, 17),
            LocalTime.of(13, 0),
            LocalTime.of(13, 30));

        Set<Holiday> holidays = Set.of(
            new Holiday(null, postHolidayTimespan)
        );

        Exam exam = new Exam(null, true, "222111", "Theo Info", examTimespan);

        student.addHoliday(
            LocalDate.of(2022, 3, 17),
            LocalTime.of(9, 30),
            LocalTime.of(13, 30),
            config);

        //Act
        student.addExam(exam);

        //Assert
        assertThat(student.getHolidays()).hasSize(1).containsExactlyInAnyOrderElementsOf(holidays);
    }

    @Test
    @DisplayName("Adding exam exceeding whole holiday timespan deletes holiday")
    void test_25() {
        //Arrange
        Timespan examTimespan = new Timespan(
            LocalDate.of(2022, 3, 17),
            LocalTime.of(9, 30),
            LocalTime.of(12, 0));

        Exam exam = new Exam(null, true, "222111", "Theo Info", examTimespan);

        student.addHoliday(
            LocalDate.of(2022, 3, 17),
            LocalTime.of(10, 0),
            LocalTime.of(12, 30),
            config);

        //Act
        student.addExam(exam);

        //Assert
        assertThat(student.getHolidays()).hasSize(0);
    }

    @Test
    @DisplayName("Holiday is left unchanged by exam that takes place at the same day at a non-intervening time")
    void test_26() {
        //Arrange
        Timespan examTimespan = new Timespan(
            LocalDate.of(2022, 3, 17),
            LocalTime.of(12, 30),
            LocalTime.of(13, 30));

        Timespan holidayTimespan = new Timespan(
            LocalDate.of(2022, 3, 17),
            LocalTime.of(9, 30),
            LocalTime.of(10, 30));

        Set<Holiday> holidays = Set.of(
            new Holiday(null, holidayTimespan)
        );

        Exam exam = new Exam(null, false, "222111", "Theo Info", examTimespan);

        student.addHoliday(
            LocalDate.of(2022, 3, 17),
            LocalTime.of(9, 30),
            LocalTime.of(10, 30),
            config);

        //Act
        student.addExam(exam);

        //Assert
        assertThat(student.getHolidays()).hasSize(1).containsExactlyInAnyOrderElementsOf(holidays);
    }

    @Test
    @DisplayName("Holidays are deleted if exam on same day is canceled")
    void test_27() {
        //Arrange
        Timespan examTimespan = new Timespan(
            LocalDate.of(2022, 3, 17),
            LocalTime.of(12, 30),
            LocalTime.of(13, 30));

        Timespan holidayTimespan = new Timespan(
            LocalDate.of(2022, 3, 17),
            LocalTime.of(9, 30),
            LocalTime.of(10, 30));

        Set<Holiday> holidays = new HashSet<>();
        holidays.add(new Holiday(1L, holidayTimespan));

        Set<Exam> exams = Set.of(new Exam(1L, false, "222111", "Theo Info", examTimespan));
        Set<ExamReference> examReferences = new HashSet<>();
        examReferences.add(new ExamReference(1L));

        student = new Student(null, "foo", examReferences, holidays);

        // Act
        student.removeExam(new Exam(1L, false, "222111", "Theo Info", examTimespan));

        //Assert
        assertThat(student.getHolidays()).isEmpty();
    }
}
