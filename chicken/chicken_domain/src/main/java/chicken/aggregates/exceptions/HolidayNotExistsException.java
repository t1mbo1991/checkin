package chicken.aggregates.exceptions;

public class HolidayNotExistsException extends RuntimeException {

    public HolidayNotExistsException(Long holidayId) {
        super("Holiday with id " + holidayId + " does not exist");
    }
}
