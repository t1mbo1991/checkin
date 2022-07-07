package chicken.web;

import static org.springframework.context.annotation.FilterType.ANNOTATION;

import chicken.stereotypes.ApplicationService;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@ComponentScan(
    includeFilters = {
        @ComponentScan.Filter(type = ANNOTATION, classes = {ApplicationService.class,
            Configuration.class}),
    }
)
public class TestConfig {

}
