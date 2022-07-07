package chicken.web.student;

import chicken.aggregates.config.InternshipConfig;
import chicken.aggregates.exam.Exam;
import chicken.aggregates.exceptions.AlreadyExemptedException;
import chicken.aggregates.exceptions.DateInPastException;
import chicken.aggregates.exceptions.ExamAlreadyExistsException;
import chicken.aggregates.exceptions.ExamNotExistsException;
import chicken.aggregates.exceptions.DateOutOfBoundsException;
import chicken.aggregates.exceptions.HolidayEndOutOfBoundsException;
import chicken.aggregates.exceptions.HolidayStartOutOfBoundsException;
import chicken.aggregates.exceptions.HolidayTimeExceededException;
import chicken.aggregates.exceptions.InvalidEndTimeException;
import chicken.aggregates.exceptions.InvalidEventException;
import chicken.aggregates.exceptions.InvalidHolidaySegmentationException;
import chicken.aggregates.exceptions.InvalidStartTimeException;
import chicken.aggregates.exceptions.InvalidTimeBlockException;
import chicken.aggregates.exceptions.NumberOfHolidayBlocksExceededException;
import chicken.aggregates.exceptions.RequestFailedException;
import chicken.aggregates.exceptions.StudentNotExistsException;
import chicken.aggregates.student.Student;
import chicken.appservices.exam.ExamService;
import chicken.appservices.student.StudentService;
import chicken.web.forms.exam.ExamForm;
import chicken.web.forms.holiday.HolidayForm;
import java.security.Principal;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Secured("ROLE_USER")
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;
    private final ExamService examService;
    private final InternshipConfig config;
    private final Logger logger = LoggerFactory.getLogger(StudentController.class);


    public StudentController(StudentService studentService, ExamService examService,
        InternshipConfig config) {
        this.studentService = studentService;
        this.examService = examService;
        this.config = config;
    }

    @GetMapping()
    public String index(Model model, @ModelAttribute("handle") String handle) {
        Student student;

        try {
            student = studentService.getByGitHubHandle(handle);
        } catch (StudentNotExistsException e) {
            student = studentService.createStudent(handle);
        }

        model.addAttribute("holidays", student.getHolidays());
        model.addAttribute("exams", studentService.getExamsForStudentById(student.getId()));
        model.addAttribute("holidaySum", student.getHolidayDurationSum());
        model.addAttribute("remainingHoliday", student.getRemainingHoliday(config.getMaxHolidays()));
        return "index";
    }

    @PostMapping("/cancelholiday")
    public String cancelHoliday(@RequestParam("holidayId") Long holidayId, RedirectAttributes attr,
        @ModelAttribute("handle") String handle) {
        try {
            studentService.cancelHoliday(handle, holidayId);
        } catch (DateInPastException e) {
            attr.addFlashAttribute("examError", e.getMessage());
            logger.info(handle + " tried to cancel holiday with id " +  holidayId + ": " + e.getMessage());
            return "redirect:/student";
        }
        logger.info(handle + " canceled holiday with id " +  holidayId);
        return "redirect:/student";
    }

    @PostMapping("/cancelexam")
    public String cancelExam(@RequestParam("examId") Long examId, RedirectAttributes attr,
        @ModelAttribute("handle") String handle) {
        try {
            studentService.cancelExam(handle, examId);
        } catch (DateInPastException e) {
            attr.addFlashAttribute("examError", e.getMessage());
            logger.info(handle + " tried to cancel exam with id " +  examId + ": " + e.getMessage());
            return "redirect:/student";
        }
        logger.info(handle + " canceled exam with id " +  examId);
        return "redirect:/student";
    }

    @GetMapping("/bookholiday")
    public String showBookHoliday(Model model) {
        if (!model.containsAttribute("holidayForm")) {
            model.addAttribute("holidayForm", new HolidayForm());
        }
        return "bookholiday";
    }

    @PostMapping("/bookholiday")
    public String bookHoliday(@Valid @ModelAttribute("holidayForm") HolidayForm holidayForm,
        BindingResult bindingResult, RedirectAttributes attr,
        @ModelAttribute("handle") String handle) {
        if (bindingResult.hasErrors()) {
            attr.addFlashAttribute("org.springframework.validation.BindingResult.holidayForm",
                bindingResult);
            attr.addFlashAttribute("holidayForm", holidayForm);
            return "redirect:/student/bookholiday";
        } else {
            try {
                studentService.bookHoliday(handle, holidayForm.getDate(), holidayForm.getStart(),
                    holidayForm.getEnd());
            } catch (
                DateInPastException
                    | NumberOfHolidayBlocksExceededException
                    | InvalidHolidaySegmentationException
                    | DateOutOfBoundsException
                    | AlreadyExemptedException e
            ) {
                FieldError error = new FieldError("holidayForm", "date", e.getMessage());
                bindingResult.addError(error);
                attr.addFlashAttribute("org.springframework.validation.BindingResult.holidayForm",
                    bindingResult);
                attr.addFlashAttribute("holidayForm", holidayForm);
                logger.info(handle + " tried to book holiday at " +
                    holidayForm.getDate() + " from " +
                    holidayForm.getStart() + " to " +
                    holidayForm.getEnd() + ": " + e.getMessage());
                return "redirect:/student/bookholiday";
            } catch (
                InvalidStartTimeException
                    | HolidayStartOutOfBoundsException e
            ) {
                FieldError error = new FieldError("holidayForm", "start", e.getMessage());
                bindingResult.addError(error);
                attr.addFlashAttribute("org.springframework.validation.BindingResult.holidayForm",
                    bindingResult);
                attr.addFlashAttribute("holidayForm", holidayForm);
                logger.info(handle + " tried to book holiday at " +
                    holidayForm.getDate() + " from " +
                    holidayForm.getStart() + " to " +
                    holidayForm.getEnd() + ": " + e.getMessage());
                return "redirect:/student/bookholiday";
            } catch (
                InvalidEndTimeException
                    | InvalidTimeBlockException
                    | HolidayEndOutOfBoundsException
                    | HolidayTimeExceededException
                    | IllegalArgumentException e
            ) {
                FieldError error = new FieldError("holidayForm", "end", e.getMessage());
                bindingResult.addError(error);
                attr.addFlashAttribute("org.springframework.validation.BindingResult.holidayForm",
                    bindingResult);
                attr.addFlashAttribute("holidayForm", holidayForm);
                logger.info(handle + " tried to book holiday at " +
                    holidayForm.getDate() + " from " +
                    holidayForm.getStart() + " to " +
                    holidayForm.getEnd() + ": " + e.getMessage());
                return "redirect:/student/bookholiday";
            }
            logger.info(handle + " booked holiday at " +
                    holidayForm.getDate() + " from " +
                    holidayForm.getStart() + " to " +
                    holidayForm.getEnd());
        }
        return "redirect:/student";
    }

    @GetMapping("/bookexam")
    public String showBookExam(Model model) {
        model.addAttribute("exams", examService.getAllExams());
        return "bookexam";
    }

    @PostMapping("/bookexam")
    public String bookExam(@RequestParam(value = "examId", required = false) Long examId,
        RedirectAttributes attr, @ModelAttribute("handle") String handle) {
        if (examId == null) {
            attr.addFlashAttribute("examError", "Please select an exam");
            return "redirect:/student/bookexam";
        }

        try {
            studentService.bookExam(handle, examId);
        } catch (DateInPastException | ExamNotExistsException e) {
            attr.addFlashAttribute("examError", e.getMessage());
            logger.info(handle + " tried to book exam with id " + examId + ": " + e.getMessage());
            return "redirect:/student/bookexam";
        }
        logger.info(handle + " booked exam with id " +  examId);
        return "redirect:/student";
    }

    @GetMapping("/addexam")
    public String showAddExam(Model model) {
        if (!model.containsAttribute("examForm")) {
            model.addAttribute("examForm", new ExamForm());
        }
        return "addexam";
    }

    @PostMapping("/addexam")
    public String addExam(@Valid ExamForm examForm, BindingResult bindingResult,
        RedirectAttributes attr, @ModelAttribute("handle") String handle) {
        if (bindingResult.hasErrors()) {
            attr.addFlashAttribute("org.springframework.validation.BindingResult.examForm",
                bindingResult);
            attr.addFlashAttribute("examForm", examForm);
            return "redirect:/student/addexam";
        } else {
            Exam exam;

            try {
                exam = examService.createExam(examForm.getPresence(), examForm.getEventId(),
                    examForm.getName(), examForm.getDate(), examForm.getStart(), examForm.getEnd());
            }
            catch (
                DateOutOfBoundsException
                | DateInPastException e
            ) {
                FieldError error = new FieldError("examForm", "date", e.getMessage());
                bindingResult.addError(error);
                attr.addFlashAttribute("org.springframework.validation.BindingResult.examForm",
                    bindingResult);
                attr.addFlashAttribute("examForm", examForm);
                logger.info(handle + " tried to register exam "
                    + examForm.getName()
                    + " at " + examForm.getDate()
                    + " from " + examForm.getStart()
                    + " to " + examForm.getEnd()
                    + ": " + e.getMessage()
                );
                return "redirect:/student/addexam";
            }
            catch (
                ExamAlreadyExistsException
                    | InvalidEventException
                    | RequestFailedException e) {
                FieldError error = new FieldError("examForm", "eventId", e.getMessage());
                bindingResult.addError(error);
                attr.addFlashAttribute("org.springframework.validation.BindingResult.examForm",
                    bindingResult);
                attr.addFlashAttribute("examForm", examForm);
                logger.info(handle + " tried to register exam "
                    + examForm.getName()
                    + " at " + examForm.getDate()
                    + " from " + examForm.getStart()
                    + " to " + examForm.getEnd()
                    + ": " + e.getMessage()
                );
                return "redirect:/student/addexam";
            }

            attr.addFlashAttribute("exam", exam);
            logger.info(handle + " registered exam "
                + examForm.getName()
                + " at " + examForm.getDate()
                + " from " + examForm.getStart()
                + " to " + examForm.getEnd()
            );
            return "redirect:/student/examconfirmation";
        }

    }

    @GetMapping("/examconfirmation")
    public String showExamBookingConfirmation(Model model) {
        return "examconfirm";
    }

    @ModelAttribute("handle")
    String handle(Principal user) {
        return user.getName();
    }
}
