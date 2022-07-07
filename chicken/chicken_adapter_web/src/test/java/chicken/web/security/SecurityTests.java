package chicken.web.security;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import chicken.aggregates.config.InternshipConfig;
import chicken.aggregates.student.Student;
import chicken.appservices.exam.ExamService;
import chicken.appservices.student.StudentService;
import java.util.HashSet;
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
public class SecurityTests {

    @Autowired
    MockMvc mvc;

    @MockBean
    StudentService studentService;

    @MockBean
    ExamService examService;

    @MockBean
    InternshipConfig internshipConfig;

    @Test
    @DisplayName("Without login user is redirected")
    void test_1() throws Exception {
        mvc.perform(get("/"))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("Leader can access /leader")
    void test_2() throws Exception {
        mvc.perform(get("/leader").session(AuthentificationTemplates.leaderSession()))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Admin can access /admin")
    void test_3() throws Exception {
        mvc.perform(get("/admin").session(AuthentificationTemplates.adminSession()))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Student cannot access /leader")
    void test_4() throws Exception {
        mvc.perform(get("/leader").session(AuthentificationTemplates.studentSession()))
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Student cannot access /admin")
    void test_5() throws Exception {
        mvc.perform(get("/admin").session(AuthentificationTemplates.studentSession()))
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Admin cannot access /leader")
    void test_6() throws Exception {
        mvc.perform(get("/leader").session(AuthentificationTemplates.adminSession()))
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Leader cannot access /student")
    void test_7() throws Exception {
        mvc.perform(get("/student").session(AuthentificationTemplates.leaderSession()))
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Admin cannot access /student")
    void test_8() throws Exception {
        mvc.perform(get("/student").session(AuthentificationTemplates.adminSession()))
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Student can access /student")
    void test_9() throws Exception {
        when(studentService.getByGitHubHandle(anyString())).thenReturn(
            new Student(1L, "foo", new HashSet<>(), new HashSet<>()));
        mvc.perform(get("/student").session(AuthentificationTemplates.studentSession()))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Leader cannot access /admin")
    void test_10() throws Exception {
        mvc.perform(get("/admin").session(AuthentificationTemplates.leaderSession()))
            .andExpect(status().isForbidden());
    }
}
