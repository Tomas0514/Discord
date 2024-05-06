package cz.tomas.discord.Controller;

import cz.tomas.discord.Entity.Guild;
import cz.tomas.discord.Entity.User;
import cz.tomas.discord.Repository.GuildRepository;
import cz.tomas.discord.Repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class IndexController {

    protected final GuildRepository guildRepository;
    protected final UserRepository userRepository;

    public IndexController(GuildRepository guildRepository, UserRepository userRepository) {
        this.guildRepository = guildRepository;
        this.userRepository = userRepository;
    }


    @GetMapping
    public String test(Model model) {
        final List<Guild> guilds = guildRepository.findAll();
        final List<User> users = userRepository.findAll();


        model.addAttribute("guilds", guilds);
        model.addAttribute("users", users);
        return "index";
    }
}
