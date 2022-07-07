package chicken.aggregates.exam;

import chicken.aggregates.student.Timespan;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public class Exam {

    private final String eventId;
    private final String name;
    private Long id;
    private Boolean isPresence;
    private Timespan timespan;

    public Exam(Long id) {
        this.id = id;
        this.isPresence = null;
        this.eventId = null;
        this.name = null;
        this.timespan = null;
    }

    public Exam(Long id, Boolean isPresence, String eventId, String name, Timespan timespan) {
        this.id = id;
        this.isPresence = isPresence;
        this.eventId = eventId;
        this.name = name;
        this.timespan = timespan;
    }

    public Timespan getExemptionTime() {
        if (isPresence) {
            return new Timespan(timespan.date(), timespan.start().minusHours(2),
                timespan.end().plusHours(2));
        } else {
            return new Timespan(timespan.date(), timespan.start().minusMinutes(30), timespan.end());
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getPresence() {
        return isPresence;
    }

    public void setPresence(Boolean presence) {
        isPresence = presence;
    }

    public String getEventId() {
        return eventId;
    }

    public String getName() {
        return name;
    }

    public Timespan getTimespan() {
        return timespan;
    }

    public void setTimespan(Timespan timespan) {
        this.timespan = timespan;
    }

    public LocalDate getDate() {
        return timespan.date();
    }

    public LocalTime getStart() {
        return timespan.start();
    }

    public LocalTime getEnd() {
        return timespan.end();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Exam exam = (Exam) o;
        return Objects.equals(id, exam.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public boolean isSameEvent(Exam exam) {
        return Objects.equals(eventId, exam.getEventId()) && timespan.equals(exam.getTimespan());
    }

    @Override
    public String toString() {
        return name + " (" + getDate() + ", " + getStart() + " - " + getEnd() + ")";
    }
}
