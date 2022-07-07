package chicken.persistence.student.dto;

import org.springframework.data.relational.core.mapping.Table;

@Table("STUDENT_EXAM")
public record ExamReference(
    Long exam,
    Long student
) {

}
