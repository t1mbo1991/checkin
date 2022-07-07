package chicken.aggregates.exam;

import static org.assertj.core.api.Assertions.assertThat;

import chicken.aggregates.student.Timespan;
import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ExamTest {

    @Test
    @DisplayName("Exam from 11am to 12am in presence results in exemption time from 9am to 2pm")
    void test_1() {
        // Arrange
        Timespan timespan = new Timespan(
            LocalDate.of(2022, 3, 9),
            LocalTime.of(11, 0),
            LocalTime.of(12, 0)
        );
        Exam exam = new Exam(null, true, "1", "Programmierung", timespan);
        Timespan result = new Timespan(
            LocalDate.of(2022, 3, 9),
            LocalTime.of(9, 0),
            LocalTime.of(14, 0)
        );

        // Act
        Timespan exemptionTime = exam.getExemptionTime();

        //Assert
        assertThat(exemptionTime).isEqualTo(result);
    }

    @Test
    @DisplayName("Online Exam from 11am to 12am results in exemption time from 10:30am to 12am")
    void test_2() {
        // Arrange
        Timespan timespan = new Timespan(
            LocalDate.of(2022, 3, 9),
            LocalTime.of(11, 0),
            LocalTime.of(12, 0)
        );
        Exam exam = new Exam(null, false, "1", "Programmierung", timespan);
        Timespan result = new Timespan(
            LocalDate.of(2022, 3, 9),
            LocalTime.of(10, 30),
            LocalTime.of(12, 0)
        );

        // Act
        Timespan exemptionTime = exam.getExemptionTime();

        //Assert
        assertThat(exemptionTime).isEqualTo(result);
    }


    @Test
    @DisplayName("Exams with different ID and same Timespan are treated as different Event")
    void test_3() {
        //Arrange
        Timespan examTimespan = new Timespan(
            LocalDate.of(2022, 3, 17),
            LocalTime.of(9, 30),
            LocalTime.of(10, 30));
        Exam exam1 = new Exam(null, true, "222112", "Theo Info", examTimespan);
        Exam exam2 = new Exam(null, true, "222111", "Theo Info", examTimespan);

        //Act
        boolean sameEvent = exam2.isSameEvent(exam1);

        //Arrange
        assertThat(sameEvent).isFalse();
    }

    @Test
    @DisplayName("Exams with same ID and different Timespan are treated as different Event")
    void test_4() {
        //Arrange
        Timespan examTimespan = new Timespan(
            LocalDate.of(2022, 3, 17),
            LocalTime.of(9, 30),
            LocalTime.of(10, 30));
        Timespan examTimespan2 = new Timespan(
            LocalDate.of(2022, 3, 17),
            LocalTime.of(10, 30),
            LocalTime.of(11, 30));
        Exam exam1 = new Exam(null, true, "222111", "Theo Info", examTimespan);
        Exam exam2 = new Exam(null, true, "222111", "Theo Info", examTimespan2);

        //Act
        boolean sameEvent = exam2.isSameEvent(exam1);

        //Arrange
        assertThat(sameEvent).isFalse();
    }

    @Test
    @DisplayName("Exams with same ID and Timespan are treated as same Event")
    void test_5() {
        //Arrange
        Timespan examTimespan = new Timespan(
            LocalDate.of(2022, 3, 17),
            LocalTime.of(9, 30),
            LocalTime.of(10, 30));

        Exam exam1 = new Exam(null, true, "222111", "Theo Info", examTimespan);
        Exam exam2 = new Exam(null, true, "222111", "Theo Info", examTimespan);

        //Act
        boolean sameEvent = exam2.isSameEvent(exam1);

        //Arrange
        assertThat(sameEvent).isTrue();
    }
}
