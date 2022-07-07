package chicken.persistence.exam.dto;

import chicken.aggregates.student.Timespan;
import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("EXAM")
public record Exam(
    @Id
    Long id,
    boolean isPresence,
    String eventId,
    String name,
    LocalDate date,
    LocalTime start,
    LocalTime end
) {

    public chicken.aggregates.exam.Exam createExam() {
        return new chicken.aggregates.exam.Exam(
            id,
            isPresence,
            eventId,
            name,
            new Timespan(
                date,
                start,
                end
            )
        );
    }
}
