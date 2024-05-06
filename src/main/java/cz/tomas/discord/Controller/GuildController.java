package cz.tomas.discord.Controller;

import cz.tomas.discord.Entity.Channel;
import cz.tomas.discord.Entity.Guild;
import cz.tomas.discord.Entity.Message;
import cz.tomas.discord.Entity.User;
import cz.tomas.discord.Repository.GuildRepository;
import cz.tomas.discord.Service.GuildService;
import cz.tomas.discord.Service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/guild")
public class GuildController {
    
    protected final GuildRepository guildRepository;
    protected final UserService userService;
    protected final GuildService guildService;

    public GuildController(GuildRepository guildRepository, UserService userService, GuildService guildService) {
        this.guildRepository = guildRepository;
        this.userService = userService;
        this.guildService = guildService;
    }
    
    @GetMapping
    public String me(Model model) {
        User user = userService.getUser(SecurityContextHolder.getContext().getAuthentication().getName());
        final List<Guild> guilds = userService.getUsersGuilds(user);
        
        model.addAttribute("guilds", guilds);
        return "user";
    }

    @GetMapping("/{guildId}")
    public String guild(@PathVariable long guildId, Model model) {
        User user = userService.getUser(SecurityContextHolder.getContext().getAuthentication().getName());
        final Guild guild = guildService.getGuild(guildId);
        
        model.addAttribute("guilds", userService.getUsersGuilds(user));
        model.addAttribute("guild", guild);
        model.addAttribute("channel", guild.getChannels().get(0));
        return "guild";
    }
    
    @GetMapping("/{guildId}/{channelId}")
    public String channel(@PathVariable long guildId, @PathVariable long channelId, Model model) {
        User user = userService.getUser(SecurityContextHolder.getContext().getAuthentication().getName());
        final Guild guild = guildService.getGuild(guildId);
        final Channel channel = guildService.getChannel(guild, channelId);
        
        model.addAttribute("guilds", userService.getUsersGuilds(user));
        model.addAttribute("guild", guild);
        model.addAttribute("channel", channel);
        return "guild";
    }
    
    @PostMapping("/{guildId}/{channelId}/send")
    public String send(@RequestParam String content, @PathVariable long guildId, @PathVariable long channelId) {
        final Channel channel = guildService.getChannel(guildId, channelId);
        final User author = userService.getUser(SecurityContextHolder.getContext().getAuthentication().getName());
        
        new Message(channel, content, author);
        guildRepository.save(guildService.getGuild(guildId));
        
        return "redirect:/guild/{guildId}/{channelId}";
    }
}
