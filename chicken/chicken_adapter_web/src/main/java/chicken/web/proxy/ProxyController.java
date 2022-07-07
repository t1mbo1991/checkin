package chicken.web.proxy;

import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProxyController {

    @GetMapping("/")
    public String redirect(Authentication auth) {
        List<String> roles = auth.getAuthorities().stream()
            .map(Object::toString).toList();
        if (roles.contains("ROLE_ADMIN")) {
            return "redirect:/admin";
        } else if (roles.contains("ROLE_LEADER")) {
            return "redirect:/leader";
        } else {
            return "redirect:/student";
        }
    }
}
