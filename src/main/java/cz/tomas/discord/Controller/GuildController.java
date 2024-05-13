package cz.tomas.discord.Controller;

import cz.tomas.discord.Entity.Channel;
import cz.tomas.discord.Entity.Guild;
import cz.tomas.discord.Entity.Message;
import cz.tomas.discord.Entity.User;
import cz.tomas.discord.Repository.GuildRepository;
import cz.tomas.discord.Repository.UserRepository;
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
    
    protected final UserRepository userRepository;
    protected final GuildRepository guildRepository;
    protected final UserService userService;
    protected final GuildService guildService;

    public GuildController(UserRepository userRepository, GuildRepository guildRepository, UserService userService, GuildService guildService) {
        this.userRepository = userRepository;
        this.guildRepository = guildRepository;
        this.userService = userService;
        this.guildService = guildService;
    }
    
    @GetMapping
    public String redirect() {
        return "redirect:/guild/@me";
    }
    
    @GetMapping("/@me")
    public String me(Model model) {
        final User user = userService.getUser(SecurityContextHolder.getContext().getAuthentication().getName());
        final List<Guild> guilds = user.getGuilds();
        final List<User> friends = user.getFriends();
        
        model.addAttribute("friends", friends);
        model.addAttribute("guilds", guilds);
        return "user";
    }
    
    @GetMapping("/@me/@pending")
    public String pending(Model model) {
        final User user = userService.getUser(SecurityContextHolder.getContext().getAuthentication().getName());
        final List<Guild> guilds = user.getGuilds();
        final List<User> friends = user.getFriends();
        final List<User> requests = user.getRequests();
    
        model.addAttribute("requests", requests);
        model.addAttribute("friends", friends);
        model.addAttribute("guilds", guilds);
        return "pending";
    }
    
    @PostMapping("/@me/@pending/action")
    public String request(@RequestParam long id, @RequestParam boolean action) {
        final User user = userService.getUser(SecurityContextHolder.getContext().getAuthentication().getName());
        final User friend = userService.getUser(id);
        
        if (action && !user.getFriends().contains(friend)) {
            user.createFriendship(friend);
        }
        
        if (user.getRequests().contains(friend)) {
            user.removeFriendRequest(friend);
        }
        
        userRepository.save(user);
        userRepository.save(friend);
        
        return "redirect:/guild/@me/@pending";
    }
    
    @GetMapping("/@me/{friendId}")
    public String friend(@PathVariable long friendId, Model model) {
        final User user = userService.getUser(SecurityContextHolder.getContext().getAuthentication().getName());
        final List<Guild> guilds = user.getGuilds();
        
        final User friend = userService.getUser(friendId);
        
        model.addAttribute("friend", friend);
        model.addAttribute("guilds", guilds);
        return "user";
    }

    @GetMapping("/{guildId}")
    public String guild(@PathVariable long guildId, Model model) {
        final User user = userService.getUser(SecurityContextHolder.getContext().getAuthentication().getName());
        final Guild guild = guildService.getGuild(guildId);
        
        model.addAttribute("guilds", user.getGuilds());
        model.addAttribute("guild", guild);
        model.addAttribute("channel", guild.getChannels().get(0));
        return "guild";
    }
    
    @GetMapping("/{guildId}/{channelId}")
    public String channel(@PathVariable long guildId, @PathVariable long channelId, Model model) {
        final User user = userService.getUser(SecurityContextHolder.getContext().getAuthentication().getName());
        final Guild guild = guildService.getGuild(guildId);
        final Channel channel = guildService.getChannel(guild, channelId);
        
        model.addAttribute("guilds", user.getGuilds());
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
