package chicken.persistence.student.dto;

import chicken.aggregates.student.Timespan;
import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("HOLIDAY")
public record Holiday(
    @Id
    Long id,
    LocalDate date,
    LocalTime start,
    LocalTime end
) {

    public chicken.aggregates.student.Holiday createHoliday() {
        return new chicken.aggregates.student.Holiday(
            id,
            new Timespan(
                date,
                start,
                end
            )
        );
    }
}
