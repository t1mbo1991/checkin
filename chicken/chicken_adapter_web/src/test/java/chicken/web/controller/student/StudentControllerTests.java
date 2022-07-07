package chicken.web.controller.student;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import chicken.aggregates.config.InternshipConfig;
import chicken.aggregates.student.Student;
import chicken.appservices.exam.ExamService;
import chicken.appservices.student.StudentService;
import chicken.web.forms.holiday.HolidayForm;
import chicken.web.security.AuthentificationTemplates;
import java.util.HashSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"web", "web-test"})
public class StudentControllerTests {

    @Autowired
    MockMvc mvc;

    @MockBean
    StudentService studentService;

    @MockBean
    ExamService examService;

    @MockBean
    InternshipConfig internshipConfig;

    @BeforeEach
    void setup() {
        when(studentService.getByGitHubHandle(anyString()))
            .thenReturn(new Student(1L, "SebastianStudent", new HashSet<>(), new HashSet<>()));
    }

    @Test
    @DisplayName("Students home page is exemption time dashboard")
    void test_1() throws Exception {
        mvc.perform(get("/student").session(AuthentificationTemplates.studentSession()))
            .andExpect(status().isOk())
            .andExpect(view().name("index"))
            .andExpect(model().attribute("handle", "SebastianStudent"))
            .andExpect(model().attribute("exams", containsInAnyOrder()))
            .andExpect(model().attribute("holidays", containsInAnyOrder()))
            .andExpect(model().attribute("holidaySum", 0L))
            .andExpect(model().attribute("remainingHoliday", 0L));
    }

    @Test
    @DisplayName("Student bookholiday provides a holiday form")
    void test_2() throws Exception {
        mvc.perform(get("/student/bookholiday").session(AuthentificationTemplates.studentSession()))
            .andExpect(status().isOk())
            .andExpect(view().name("bookholiday"))
            .andExpect(model().attribute("holidayForm", instanceOf(HolidayForm.class)));
    }
}
