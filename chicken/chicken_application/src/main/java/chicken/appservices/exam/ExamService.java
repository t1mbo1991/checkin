package chicken.appservices.exam;

import chicken.aggregates.config.ClockConfig;
import chicken.aggregates.config.InternshipConfig;
import chicken.aggregates.exam.Exam;
import chicken.aggregates.exceptions.DateInPastException;
import chicken.aggregates.exceptions.ExamAlreadyExistsException;
import chicken.aggregates.exceptions.DateOutOfBoundsException;
import chicken.aggregates.exceptions.InvalidEventException;
import chicken.aggregates.exceptions.RequestFailedException;
import chicken.aggregates.student.Timespan;
import chicken.repositories.ExamRepository;
import chicken.stereotypes.ApplicationService;
import chicken.utils.ExamWebCrawler;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@ApplicationService
public class ExamService {

    private final ExamRepository examRepository;
    private final InternshipConfig internshipConfig;
    private final ClockConfig clockConfig;

    public ExamService(ExamRepository examRepository, InternshipConfig internshipConfig, ClockConfig clockConfig) {
        this.examRepository = examRepository;
        this.internshipConfig = internshipConfig;
        this.clockConfig = clockConfig;
    }

    public List<Exam> getAllExams() {
        return examRepository.getAllExams();
    }

    public Exam createExam(Boolean isPresence, String eventID, String name, LocalDate date,
        LocalTime startTime, LocalTime endTime) throws RuntimeException {
        if (isPresence == null || eventID == null || name == null || date == null
            || startTime == null || endTime == null) {
            throw new IllegalArgumentException("no arguments must be null");
        }

        Timespan timespan = new Timespan(date, startTime, endTime);
        boolean isValidId;
        try {
            isValidId = ExamWebCrawler.isValidId(eventID, name);
        } catch (Exception e) {
            throw new RequestFailedException();
        }
        if (!isValidId) {
            throw new InvalidEventException();
        }

        if (date.compareTo(internshipConfig.getEndDate()) >= 0) {
            throw new DateOutOfBoundsException("Is not in internship timeframe");
        }

        if (date.compareTo(LocalDate.now(clockConfig.getSystemDefaultClock())) <= 0) {
            throw new DateInPastException("You cannot create an exam in the past");
        }

        Exam exam = new Exam(null, isPresence, eventID, name, timespan);
        List<Exam> allExams = getAllExams();
        boolean anySameEvent = allExams.stream().anyMatch(e -> e.isSameEvent(exam));
        if (anySameEvent) {
            throw new ExamAlreadyExistsException();
        }

        return examRepository.save(exam);
    }
}
