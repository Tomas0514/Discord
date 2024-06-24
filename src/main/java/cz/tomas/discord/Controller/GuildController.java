package cz.tomas.discord.Controller;

import cz.tomas.discord.Entity.*;
import cz.tomas.discord.Repository.GroupRepository;
import cz.tomas.discord.Repository.GuildRepository;
import cz.tomas.discord.Repository.UserRepository;
import cz.tomas.discord.Service.*;
import cz.tomas.discord.Service.Exceptions.ChannelNotFoundException;
import cz.tomas.discord.Service.Exceptions.GroupNotFoundException;
import cz.tomas.discord.Service.Exceptions.GuildNotFoundException;
import cz.tomas.discord.Service.Exceptions.UserNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/guild")
public class GuildController {
    
    protected final UserRepository userRepository;
    protected final GuildRepository guildRepository;
    protected final GroupRepository groupRepository;
    protected final UserService userService;
    protected final GuildService guildService;
    protected final GroupService groupService;
    protected final ChannelService channelService;

    public GuildController(UserRepository userRepository, GuildRepository guildRepository, GroupRepository groupRepository, UserService userService, GuildService guildService, GroupService groupService, ChannelService channelService) {
        this.userRepository = userRepository;
        this.guildRepository = guildRepository;
        this.groupRepository = groupRepository;
        this.userService = userService;
        this.guildService = guildService;
        this.groupService = groupService;
        this.channelService = channelService;
    }
    
    @GetMapping
    public String redirect() {
        return "redirect:/guild/@me";
    }
    
    @GetMapping("/@me")
    public String me(Model model) {
        final User user = userService.getClientUser();
        final List<Guild> guilds = user.getGuilds();
        final List<Friend> friends = user.getFriends();

        model.addAttribute("friends", friends);
        model.addAttribute("guilds", guilds);
        model.addAttribute("user", user);
        return "user";
    }

    @GetMapping("/@me/@pending")
    public String pending(Model model) {
        final User user = userService.getClientUser();
        final List<Guild> guilds = user.getGuilds();
        final List<Friend> friends = user.getFriends();
        final List<User> requests = user.getRequests();

        model.addAttribute("requests", requests);
        model.addAttribute("friends", friends);
        model.addAttribute("guilds", guilds);
        model.addAttribute("user", user);
        return "pending";
    }

    @PostMapping("/@me/@pending/action")
    public String request(@RequestParam long id, @RequestParam boolean action) {
        final User user = userService.getClientUser();
        final User friend = userService.getUser(id);

        if (action && !user.getFriendsAsUsers().contains(friend)) {
            final Group group = new Group();
            user.joinGroup(group);
            friend.joinGroup(group);
            groupRepository.save(group);
        }

        if (user.getRequests().contains(friend)) {
            user.removeRequest(friend);
        }

        userRepository.save(user);
        userRepository.save(friend);

        return "redirect:/guild/@me/@pending";
    }

    @GetMapping("/@me/@addFriend")
    public String addFriend(@RequestParam boolean success, Model model) {
        final User user = userService.getClientUser();
        final List<Guild> guilds = user.getGuilds();
        final List<Friend> friends = user.getFriends();

        model.addAttribute("allUsers", userRepository.findAll());
        model.addAttribute("success", success);
        model.addAttribute("friends", friends);
        model.addAttribute("guilds", guilds);
        model.addAttribute("user", user);
        return "addFriend";
    }

    @PostMapping("/@me/@addFriend/add")
    public String add(@RequestParam String username, RedirectAttributes redirectAttributes) {
        final User user = userService.getClientUser();
        String message = "Friend request sent!";
        boolean success = false;
        try {
            final User friend = userService.getUser(username);
            if (user.getFriendsAsUsers().contains(friend)) {
                message = "You're already friends with that user.";
            } else if (friend.getRequests().contains(user)) {
                message = "You're already sent a friend request to that user.";
            } else if (user == friend) {
                message = "You cannot add yourself.";
            } else {
                user.sendRequest(friend);
                userRepository.save(friend);
                success = true;
            }

        } catch (UserNotFoundException ex) {
            message = "Hm, that didn't work. Double-check that the username is correct.";
        }

        redirectAttributes.addAttribute("success", success);
        redirectAttributes.addAttribute("message", message);
        return "redirect:/guild/@me/@addFriend";
    }
    
    @PreAuthorize("@securityService.isInGroup(#groupId)")
    @GetMapping("/@me/{groupId}")
    public String friend(@PathVariable long groupId, Model model) {
        final User user = userService.getClientUser();
        final List<Guild> guilds = user.getGuilds();
        final List<Friend> friends = user.getFriends();
        final Group group = groupService.getGroup(groupId);
        
        model.addAttribute("group", group);
        model.addAttribute("friends", friends);
        model.addAttribute("guilds", guilds);
        model.addAttribute("user", user);
        return "friend";
    }
    
    @PreAuthorize("@securityService.isInGuild(#guildId)")
    @GetMapping("/{guildId}")
    public String guild(@PathVariable long guildId, Model model) {
        final User user = userService.getClientUser();
        final Guild guild = guildService.getGuild(guildId);
        
        model.addAttribute("guilds", user.getGuilds());
        model.addAttribute("guild", guild);
        model.addAttribute("user", user);
        model.addAttribute("channel", guild.getChannels().get(0));
        return "guild";
    }
    
    @PreAuthorize("@securityService.isInGuild(#guildId)")
    @GetMapping("/{guildId}/{channelId}")
    public String channel(@PathVariable long guildId, @PathVariable long channelId, Model model) {
        final User user = userService.getClientUser();
        final Guild guild = guildService.getGuild(guildId);
        final Channel channel = channelService.getChannel(channelId);
        
        model.addAttribute("guilds", user.getGuilds());
        model.addAttribute("guild", guild);
        model.addAttribute("user", user);
        model.addAttribute("channel", channel);
        return "guild";
    }
    
    @GetMapping("/@explore")
    public String explore(Model model) {
        final User user = userService.getClientUser();
        final List<Guild> guilds = user.getGuilds();
        
        model.addAttribute("guilds", guilds);
        model.addAttribute("user", user);
        return "explore";
    }
    
    
    @ExceptionHandler({UserNotFoundException.class, GuildNotFoundException.class, ChannelNotFoundException.class, GroupNotFoundException.class})
    public String handleError() {
        return "redirect:/guild/@me";
    }
}
