package chicken.aggregates.student;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class HolidayTests {

    Holiday holiday;

    @BeforeEach()
    void setup() {
        holiday = new Holiday(null, new Timespan(
            LocalDate.of(2022, 3, 15),
            LocalTime.of(10, 30),
            LocalTime.of(12, 0)
        ));
    }

    @Test
    @DisplayName("new start is after old one and new end is before old one")
    void test_1() {
        boolean isOverlap = holiday.getTimespan().isOverlap(
            LocalDate.of(2022, 3, 15),
            LocalTime.of(11, 0),
            LocalTime.of(11, 30)
        );

        assertThat(isOverlap).isTrue();
    }

    @Test
    @DisplayName("new start is after old one but before old end and new end is after old one")
    void test_2() {
        boolean isOverlap = holiday.getTimespan().isOverlap(
            LocalDate.of(2022, 3, 15),
            LocalTime.of(11, 0),
            LocalTime.of(12, 30)
        );

        assertThat(isOverlap).isTrue();
    }

    @Test
    @DisplayName("new start is before old one and new end is before old one but after old start")
    void test_3() {
        boolean isOverlap = holiday.getTimespan().isOverlap(
            LocalDate.of(2022, 3, 15),
            LocalTime.of(10, 0),
            LocalTime.of(11, 0)
        );

        assertThat(isOverlap).isTrue();
    }

    @Test
    @DisplayName("new start is before old one and new end is after old one")
    void test_4() {
        boolean isOverlap = holiday.getTimespan().isOverlap(
            LocalDate.of(2022, 3, 15),
            LocalTime.of(10, 0),
            LocalTime.of(12, 30)
        );

        assertThat(isOverlap).isTrue();
    }

    @Test
    @DisplayName("new timespan is before old one, no overlap")
    void test_5() {
        boolean isOverlap = holiday.getTimespan().isOverlap(
            LocalDate.of(2022, 3, 15),
            LocalTime.of(9, 30),
            LocalTime.of(10, 0)
        );

        assertThat(isOverlap).isFalse();
    }

    @Test
    @DisplayName("new timespan is after old one, no overlap")
    void test_6() {
        boolean isOverlap = holiday.getTimespan().isOverlap(
            LocalDate.of(2022, 3, 15),
            LocalTime.of(12, 30),
            LocalTime.of(13, 30)
        );

        assertThat(isOverlap).isFalse();
    }

    @Test
    @DisplayName("new timespan end is old start is treated as overlap")
    void test_7() {
        boolean isOverlap = holiday.getTimespan().isOverlap(
            LocalDate.of(2022, 3, 15),
            LocalTime.of(9, 30),
            LocalTime.of(10, 30)
        );

        assertThat(isOverlap).isTrue();
    }

    @Test
    @DisplayName("new timespan is on different date, no overlap")
    void test_8() {
        boolean isOverlap = holiday.getTimespan().isOverlap(
            LocalDate.of(2022, 3, 16),
            LocalTime.of(9, 30),
            LocalTime.of(10, 30)
        );

        assertThat(isOverlap).isFalse();
    }
}
