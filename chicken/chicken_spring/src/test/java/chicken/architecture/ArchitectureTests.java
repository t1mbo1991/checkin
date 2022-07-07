package chicken.architecture;

import static com.tngtech.archunit.library.Architectures.onionArchitecture;

import chicken.ChickenSpringApplication;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packagesOf = ChickenSpringApplication.class)
public class ArchitectureTests {

    @ArchTest
    ArchRule onionTest = onionArchitecture()
        .domainModels("chicken.aggregates..")
        .domainServices("chicken.aggregates..")
        .applicationServices("chicken.appservices..")
        .adapter("web", "chicken.web..")
        .adapter("persistence", "chicken.persistence..")
        .adapter("infrastructure", "chicken..");
}
