package chicken.aggregates.exceptions;

public class NumberOfHolidayBlocksExceededException extends RuntimeException {

    public NumberOfHolidayBlocksExceededException() {
        super("Only two blocks of holidays on the same day are allowed");
    }
}
