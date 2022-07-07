package chicken.aggregates.exceptions;

public class InvalidHolidaySegmentationException extends RuntimeException {

    public InvalidHolidaySegmentationException() {
        super(
            "In case of two holiday blocks on the same day one must be at the start and one at the end");
    }

}
