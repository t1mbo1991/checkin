package chicken.appservices.student;

import chicken.aggregates.config.ClockConfig;
import chicken.aggregates.config.InternshipConfig;
import chicken.aggregates.exam.Exam;
import chicken.aggregates.exceptions.DateInPastException;
import chicken.aggregates.exceptions.ExamNotExistsException;
import chicken.aggregates.exceptions.HolidayNotExistsException;
import chicken.aggregates.exceptions.StudentAlreadyExiststException;
import chicken.aggregates.exceptions.StudentNotExistsException;
import chicken.aggregates.student.ExamReference;
import chicken.aggregates.student.Holiday;
import chicken.aggregates.student.Student;
import chicken.repositories.ExamRepository;
import chicken.repositories.StudentRepository;
import chicken.stereotypes.ApplicationService;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Bean;

@ApplicationService
public class StudentService {

    private final ExamRepository examRepository;
    private final StudentRepository studentRepository;
    private final InternshipConfig internshipConfig;
    private final ClockConfig clockConfig;

    public StudentService(
        ExamRepository examRepository,
        StudentRepository studentRepository,
        ClockConfig clockConfig,
        InternshipConfig config
    ) {
        this.examRepository = examRepository;
        this.studentRepository = studentRepository;
        this.clockConfig = clockConfig;
        this.internshipConfig = config;
    }

    public Student getByGitHubHandle(String gitHubHandle) throws StudentNotExistsException {
        Optional<Student> student = studentRepository.getStudentByGitHubHandle(gitHubHandle);

        if (student.isEmpty()) {
            throw new StudentNotExistsException(gitHubHandle);
        }

        return student.get();
    }

    public List<Exam> getExamsForStudentById(Long id) {
        return examRepository.getAllExamsByStudentId(id);
    }

    public Student createStudent(String gitHubHandle) {
        if (gitHubHandle == null) {
            throw new IllegalArgumentException("gitHubHandle must not be null");
        } else {
            Optional<Student> checkStudent = studentRepository.getStudentByGitHubHandle(
                gitHubHandle);
            if (checkStudent.isPresent()) {
                throw new StudentAlreadyExiststException(gitHubHandle);
            }
            Student newStudent = new Student(null, gitHubHandle);
            return studentRepository.save(newStudent);
        }
    }

    public void bookHoliday(String gitHubHandle, LocalDate date, LocalTime start, LocalTime end)
        throws RuntimeException {
        if (gitHubHandle == null || date == null || start == null || end == null) {
            throw new IllegalArgumentException("no arguments must be null");
        } else if (end.compareTo(start) <= 0) {
            throw new IllegalArgumentException("Start time must be before end time");
        } else if (date.compareTo(LocalDate.now(clockConfig.getSystemDefaultClock())) <= 0) {
            throw new DateInPastException("You cannot book holiday in the past or on the same day");
        }

        Student student = getByGitHubHandle(gitHubHandle);

        List<Exam> exams = getExamsForStudentById(student.getId()).stream()
            .filter(e -> e.getDate().isEqual(date))
            .collect(Collectors.toList());

        if (exams.size() == 0) {
            student.addHoliday(date, start, end, internshipConfig);
        } else {
            student.addHoliday(date, start, end, exams, internshipConfig);
        }

        studentRepository.save(student);
    }

    public void bookExam(String gitHubHandle, Long examId) throws RuntimeException {
        if (gitHubHandle == null || examId == null) {
            throw new IllegalArgumentException("no arguments must be null");
        }

        Optional<Exam> exam = examRepository.getExamById(examId);
        if (exam.isEmpty()) {
            throw new ExamNotExistsException(examId);
        } else if (
            exam.get().getDate().compareTo(LocalDate.now(clockConfig.getSystemDefaultClock()))
                <= 0) {
            throw new DateInPastException("You cannot book an exam in the past or on the same day");
        }

        Student student = getByGitHubHandle(gitHubHandle);

        student.addExam(exam.get());
        studentRepository.save(student);
    }

    public void cancelHoliday(String gitHubHandle, Long holidayId) {
        Student student = getByGitHubHandle(gitHubHandle);

        Optional<Holiday> holiday = student.getHolidays().stream()
            .filter(h -> h.getId().equals(holidayId)).findFirst();

        if (holiday.isEmpty()) {
            throw new HolidayNotExistsException(holidayId);
        } else if (
            holiday.get().getDate().compareTo(LocalDate.now(clockConfig.getSystemDefaultClock()))
                <= 0) {
            throw new DateInPastException(
                "You can cancel a holiday only until the day before, please contact a tutor");
        }

        student.removeHoliday(holiday.get());
        studentRepository.save(student);
    }

    public void cancelExam(String gitHubHandle, Long examId) {
        Student student = getByGitHubHandle(gitHubHandle);

        Optional<ExamReference> examReference = student.getExams().stream()
            .filter(e -> e.id().equals(examId)).findFirst();

        if (examReference.isEmpty()) {
            throw new ExamNotExistsException(examId);
        }

        Optional<Exam> exam = examRepository.getExamById(examReference.get().id());

        if (exam.isPresent()
            && exam.get().getDate().compareTo(LocalDate.now(clockConfig.getSystemDefaultClock()))
            <= 0) {
            throw new DateInPastException(
                "You can cancel an exam only until the day before, please contact a tutor");
        }

        student.removeExam(exam.get());
        studentRepository.save(student);
    }

    @Bean
    Clock clock() {
        return Clock.systemDefaultZone();
    }
}
