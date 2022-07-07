package chicken.web.forms.holiday;

import java.time.LocalDate;
import java.time.LocalTime;
import javax.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

public class HolidayForm {

    @NotNull(message = "Please enter a date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @NotNull(message = "Please enter a Time")
    private LocalTime start;

    @NotNull(message = "Please enter a Time")
    private LocalTime end;


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
