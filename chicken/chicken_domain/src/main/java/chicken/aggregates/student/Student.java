package chicken.aggregates.student;

import chicken.aggregates.config.InternshipConfig;
import chicken.aggregates.exam.Exam;
import chicken.aggregates.exceptions.AlreadyExemptedException;
import chicken.aggregates.exceptions.InvalidHolidaySegmentationException;
import chicken.aggregates.exceptions.InvalidTimeBlockException;
import chicken.aggregates.exceptions.NumberOfHolidayBlocksExceededException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Student {

    private final String githubHandle;
    private final Set<ExamReference> exams;
    private final Set<Holiday> holidays;
    private Long id;

    public Student(Long id, String githubHandle) {
        this.id = id;
        this.githubHandle = githubHandle;
        exams = new HashSet<>();
        holidays = new HashSet<>();
    }

    public Student(Long id, String githubHandle, Set<ExamReference> exams, Set<Holiday> holidays) {
        this.id = id;
        this.githubHandle = githubHandle;
        this.exams = exams;
        this.holidays = holidays;
    }

    public long getRemainingHoliday(int maxHolidays) {
        return maxHolidays - getHolidayDurationSum();
    }

    public long getHolidayDurationSum() {
        return holidays.stream().mapToLong(h -> h.getTimespan().getDuration()).sum();
    }

    public void addHoliday(LocalDate date, LocalTime start, LocalTime end, InternshipConfig config)
        throws RuntimeException {
        LocalTime newStart = start;
        LocalTime newEnd = end;

        Set<Holiday> toRemove = new HashSet<>();

        long remainingHoliday = getRemainingHoliday(config.getMaxHolidays());

        for (Holiday h : holidays) {
            if (h.getTimespan().isOverlap(date, newStart, newEnd)) {
                toRemove.add(h);
                remainingHoliday += h.getTimespan().getDuration();

                if (start.isAfter(h.getStart())) {
                    newStart = h.getStart();
                }

                if (end.isBefore(h.getEnd())) {
                    newEnd = h.getEnd();
                }
            }
        }

        Holiday newHoliday = new Holiday(null, new Timespan(date, newStart, newEnd));

        Set<Holiday> nonOverlappingHolidaysSameDate = getNonOverlappingHolidaysSameDate(date,
            toRemove);

        if (nonOverlappingHolidaysSameDate.size() >= 2) {
            throw new NumberOfHolidayBlocksExceededException();
        }

        if (!areOnStartOrEnd(newHoliday, nonOverlappingHolidaysSameDate, config)) {
            throw new InvalidHolidaySegmentationException();
        }

        if (!isValidTimeBlock(newHoliday, nonOverlappingHolidaysSameDate)) {
            String message;

            if (toRemove.size() > 0) {
                message = "Trying to combine your holidays on same day, exceeded 2,5 hours. Please book 4 hours instead";
            } else {
                message = "One can only book up to 2,5 hours or 4 hours";
            }

            throw new InvalidTimeBlockException(message);
        }

        if (newHoliday.isValidHoliday(remainingHoliday, config)) {
            toRemove.forEach(this::removeHoliday);
            holidays.add(newHoliday);
        }

    }

    private boolean isValidTimeBlock(Holiday newHoliday,
        Set<Holiday> nonOverlappingHolidaysSameDate) {
        long holidayDurationSum = nonOverlappingHolidaysSameDate.stream()
            .map(h -> h.getTimespan().getDuration())
            .reduce(0L, Long::sum) + newHoliday.getTimespan().getDuration();

        return holidayDurationSum <= 150 || holidayDurationSum == 240;
    }

    private boolean areOnStartOrEnd(Holiday newHoliday, Set<Holiday> nonOverlappingHolidaysSameDate,
        InternshipConfig config) {
        return nonOverlappingHolidaysSameDate.stream()
            .allMatch(h -> h.isOnStartOrEnd(config) && newHoliday.isOnStartOrEnd(config));
    }

    private Set<Holiday> getNonOverlappingHolidaysSameDate(LocalDate date, Set<Holiday> toRemove) {
        return holidays.stream()
            .filter(h -> h.getDate().isEqual(date) && !(toRemove.contains(h)))
            .collect(Collectors.toSet());
    }

    public void removeHoliday(Holiday holiday) {
        holidays.remove(holiday);
    }

    public void removeExam(Exam exam) {
        exams.remove(new ExamReference(exam.getId()));

        Set<Holiday> toRemove = new HashSet<>();
        holidays.stream().filter(h -> h.getDate().isEqual(exam.getDate())).forEach(toRemove::add);
        toRemove.forEach(this::removeHoliday);
    }

    public void addExam(Exam exam) {

        LocalTime examStart = exam.getExemptionTime().start();
        LocalTime examEnd = exam.getExemptionTime().end();

        for (Holiday h : holidays) {
            if (h.getTimespan().isOverlap(exam.getDate(), examStart, examEnd)) {

                holidays.remove(h);

                Timespan preTimespan = new Timespan(exam.getDate(), h.getStart(), examStart);
                Timespan postTimespan = new Timespan(exam.getDate(), examEnd, h.getEnd());

                if (examStart.isAfter(h.getStart()) && examEnd.isBefore(h.getEnd())) {
                    holidays.add(new Holiday(null, preTimespan));
                    holidays.add(new Holiday(null, postTimespan));
                } else if (examStart.isAfter(h.getStart())) {
                    holidays.add(new Holiday(null, preTimespan));
                } else if (examEnd.isBefore(h.getEnd())) {
                    holidays.add(new Holiday(null, postTimespan));
                }
            }
        }
        exams.add(new ExamReference(exam.getId()));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGithubHandle() {
        return githubHandle;
    }

    public Set<Holiday> getHolidays() {
        return new HashSet<>(holidays);
    }

    public Set<ExamReference> getExams() {
        return new HashSet<>(exams);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Student student = (Student) o;
        return Objects.equals(id, student.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void addHoliday(LocalDate date, LocalTime start, LocalTime end, List<Exam> exams,
        InternshipConfig config) throws RuntimeException {
        Timespan timespan = new Timespan(date, start, end);

        Holiday holiday = new Holiday(null, timespan);
        boolean addHoliday = exams.stream()
            .noneMatch(exam -> exam.getExemptionTime().isOverlap(date, start, end));

        if (!addHoliday) {
            throw new AlreadyExemptedException();
        }

        if (holiday.isValidHoliday(getRemainingHoliday(config.getMaxHolidays()),
            config)) {
            holidays.add(holiday);
        }
    }
}