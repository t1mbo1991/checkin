package chicken.web.admin;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Secured("ROLE_ADMIN")
@RequestMapping("/admin")
public class AdminController {

    @GetMapping()
    public String index(Model model) {
        model.addAttribute("role", "admin");
        return "example";
    }

}
