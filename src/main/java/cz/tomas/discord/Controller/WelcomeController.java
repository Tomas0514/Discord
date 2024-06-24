package cz.tomas.discord.Controller;

import cz.tomas.discord.Service.Exceptions.UserAlreadyExistsException;
import cz.tomas.discord.Service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/welcome")
public class WelcomeController {
    
    private final UserService userService;
    
    public WelcomeController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping
    public String welcome() {
        return "welcome";
    }
    
    @PostMapping
    public String welcome(@RequestParam String username, @RequestParam String password, Model model) {
        userService.createUser(username, password, List.of("USER"));
        
        return "redirect:/";
    }
    
    @ExceptionHandler({UserAlreadyExistsException.class})
    public String handleError(Model model, Throwable ex) {
        model.addAttribute("error", ex.getMessage());
        return "welcome";
    }
}
