package chicken.web.leader;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Secured("ROLE_LEADER")
@RequestMapping("/leader")
public class LeaderController {

    @GetMapping()
    public String index(Model model) {
        model.addAttribute("role", "leader");
        return "example";
    }

}
