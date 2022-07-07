package chicken.web.forms.exam;

import java.time.LocalDate;
import java.time.LocalTime;
import javax.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

public class ExamForm {

    @NotNull(message = "Please enter an exam name")
    private String name;

    @NotNull(message = "Please enter a valid event id")
    private String eventId;

    @NotNull(message = "Please provide a value")
    private Boolean isPresence;

    @NotNull(message = "Please enter a date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @NotNull(message = "Please enter a start time")
    private LocalTime start;

    @NotNull(message = "Please enter an end time")
    private LocalTime end;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public Boolean getPresence() {
        return isPresence;
    }

    public void setPresence(Boolean presence) {
        isPresence = presence;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStart() {
        return start;
    }

    public void setStart(LocalTime start) {
        this.start = start;
    }

    public LocalTime getEnd() {
        return end;
    }

    public void setEnd(LocalTime end) {
        this.end = end;
    }
}
