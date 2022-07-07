package chicken;

import chicken.config.ClockConfigImpl;
import chicken.config.InternshipConfigImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles({"database-test", "test"})
class ChickenSpringApplicationTests {

    @Autowired
    InternshipConfigImpl internshipConfig;

    @Autowired
    ClockConfigImpl clockConfig;

    @Test
    void contextLoads() {
    }

}
