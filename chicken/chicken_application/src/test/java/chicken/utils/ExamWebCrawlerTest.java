package chicken.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ExamWebCrawlerTest {

    @Test
    @DisplayName("Theoretische Informatik with ID 225622 returns False")
    void test_2() throws Exception {
        assertThat(ExamWebCrawler.isValidId("225622", "Theoretische Informatik")).isFalse();
    }

    @Test
    @DisplayName("Theoretische Informatik with ID 225641 returns True")
    void test_1() throws Exception {
        assertThat(ExamWebCrawler.isValidId("225641", "Theoretische Informatik")).isTrue();
    }
}