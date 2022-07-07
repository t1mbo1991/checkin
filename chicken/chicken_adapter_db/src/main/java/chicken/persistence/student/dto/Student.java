package chicken.persistence.student.dto;

import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;


@Table("STUDENT")
public record Student(
    @Id
    Long id,
    String gitHubHandle,
    Set<ExamReference> examReferences,
    Set<Holiday> holidays
) {

    public chicken.aggregates.student.Student createStudent() {
        return new chicken.aggregates.student.Student(
            id,
            gitHubHandle,
            examReferences.stream()
                .map(e -> new chicken.aggregates.student.ExamReference(e.exam()))
                .collect(Collectors.toSet()),
            holidays.stream().map(Holiday::createHoliday).collect(Collectors.toSet())
        );
    }
}
