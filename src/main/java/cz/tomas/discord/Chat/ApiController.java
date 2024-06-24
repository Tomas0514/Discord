package cz.tomas.discord.Chat;

import cz.tomas.discord.Entity.*;
import cz.tomas.discord.Repository.DirectMessageRepository;
import cz.tomas.discord.Repository.GuildRepository;
import cz.tomas.discord.Repository.UserRepository;
import cz.tomas.discord.Service.ChannelService;
import cz.tomas.discord.Service.Exceptions.GuildNotFoundException;
import cz.tomas.discord.Service.GroupService;
import cz.tomas.discord.Service.GuildService;
import cz.tomas.discord.Service.UserService;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ApiController {
    
    protected final GuildService guildService;
    protected final UserService userService;
    protected final ChannelService channelService;
    protected final GroupService groupService;
    protected final UserRepository userRepository;
    protected final GuildRepository guildRepository;
    protected final SimpMessageSendingOperations messagingTemplate;
    
    public ApiController(GuildService guildService, UserService userService, ChannelService channelService, GroupService groupService, UserRepository userRepository, GuildRepository guildRepository, SimpMessageSendingOperations messagingTemplate) {
        this.guildService = guildService;
        this.userService = userService;
        this.channelService = channelService;
        this.groupService = groupService;
        this.userRepository = userRepository;
        this.guildRepository = guildRepository;
        this.messagingTemplate = messagingTemplate;
    }
    
    
    @GetMapping(value = "/api/guild/channel/{channelId}/getMessages/{num}")
    public List<MessageJSON> getMessages(@PathVariable int num, @PathVariable long channelId) {
        if (num < 0) { throw new Error("Invalid api request. Parameter 'num' must be positive."); }
        if (num == 0) { return new ArrayList<>(); }
        num = Math.min(num, 200);
        final Channel channel = channelService.getChannel(channelId);
        
        List<Message> messages = channel.getMessages();
        int size = messages.size();
        
        if (size <= num) {
            return this.convertM(messages);
        } else {
            return this.convertM(messages.subList(size - num, size));
        }

    }
    
    private List<MessageJSON> convertM(List<Message> messages) {
        return messages.stream()
                .map(message -> {
                    MessageJSON msg = new MessageJSON();
                    msg.set(message);
                    return msg;
                })
                .collect(Collectors.toList());
    }
    
    private List<DirectMessageJSON> convertDM(List<DirectMessage> messages) {
        return messages.stream()
                .map(message -> {
                    DirectMessageJSON msg = new DirectMessageJSON();
                    msg.set(message);
                    return msg;
                })
                .collect(Collectors.toList());
    }
    
    static class MessageJSON {
        private long id;
        private long channel;
        private String content;
        private UserJSON author;
        private long timestamp;
        
        public void set(Message message) {
            this.id = message.getId();
            this.channel = message.getChannel().getId();
            this.content = message.getContent();
            UserJSON author = new UserJSON();
            author.set(message.getAuthor());
            this.author = author;
            this.timestamp = message.getTimestamp();
        }
        
        public long getId() {
            return id;
        }
        
        public void setId(long id) {
            this.id = id;
        }
        
        public long getChannel() {
            return channel;
        }
        
        public void setChannel(long channel) {
            this.channel = channel;
        }
        
        public String getContent() {
            return content;
        }
        
        public void setContent(String content) {
            this.content = content;
        }
        
        public UserJSON getAuthor() {
            return author;
        }
        
        public void setAuthor(UserJSON author) {
            this.author = author;
        }
        
        public long getTimestamp() {
            return timestamp;
        }
        
        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }
    
    static class DirectMessageJSON {
        private long id;
        private long group;
        private String content;
        private UserJSON author;
        private long timestamp;
        
        public void set(DirectMessage message) {
            this.id = message.getId();
            this.group = message.getGroup().getId();
            this.content = message.getContent();
            UserJSON author = new UserJSON();
            author.set(message.getAuthor());
            this.author = author;
            this.timestamp = message.getTimestamp();
        }
        
        public long getId() {
            return id;
        }
        
        public void setId(long id) {
            this.id = id;
        }
        
        public String getContent() {
            return content;
        }
        
        public void setContent(String content) {
            this.content = content;
        }
        
        public UserJSON getAuthor() {
            return author;
        }
        
        public void setAuthor(UserJSON author) {
            this.author = author;
        }
        
        public long getTimestamp() {
            return timestamp;
        }
        
        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
        
        public long getGroup() {
            return group;
        }
        
        public void setGroup(long group) {
            this.group = group;
        }
    }
    
    static class UserJSON {
        private long id;
        private String username;
        
        public void set(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
        }
        
        public long getId() {
            return id;
        }
        
        public void setId(long id) {
            this.id = id;
        }
        
        public String getUsername() {
            return username;
        }
        
        public void setUsername(String username) {
            this.username = username;
        }
    }
    
    
    
    @GetMapping(value = "/api/getGuilds/{num}")
    public List<GuildJSON> getGuilds(@PathVariable int num) {
        if (num < 0) { throw new Error("Invalid api request. Parameter 'num' must be positive."); }
        if (num == 0) { return new ArrayList<>(); }
        num = Math.min(num, 200);
        
        List<Guild> guilds = guildRepository.findAll();
        int size = guilds.size();
        
        if (size <= num) {
            return convertG(guilds);
        } else {
            return convertG(guilds.subList(size - num, size));
        }
    }
    
    private List<GuildJSON> convertG(List<Guild> guilds) {
        return guilds.stream()
                .map(guild -> {
                    GuildJSON g = new GuildJSON();
                    g.set(guild);
                    return g;
                })
                .collect(Collectors.toList());
    }
    
    static class GuildJSON {
        private long id;
        private String name;
        private int memberCount;
        
        public void set(Guild guild) {
            this.id = guild.getId();
            this.name = guild.getName();
            this.memberCount = guild.getMembers().size();
        }
        
        public long getId() {
            return id;
        }
        
        public void setId(long id) {
            this.id = id;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public long getMemberCount() {
            return memberCount;
        }
    }
    
    
    @PostMapping("/api/guild/create")
    public String createGuild(@RequestBody CreateGuildBody body) {
        // final User author = userService.getUser(user.getName());
        
        final User owner = userService.getClientUser();
        
        Guild g = new Guild(owner, body.getName());
        
        g = guildRepository.save(g);
        
        body.setId(g.getId());
        messagingTemplate.convertAndSend("/topic/guild", body);
        
        return "/guild/"+g.getId();
        
        // System.out.println(formatMsToISO(new Date().getTime()) + "  GUILD CREATE [" + destination + "] Guild: " + guild.getName());
        
        
    }
    
    static class CreateGuildBody {
        private String name;
        private long id;
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public long getId() {
            return id;
        }
        
        public void setId(long id) {
            this.id = id;
        }
    }
    
    
    @GetMapping(value = "/api/joinGuild/{guildId}")
    public String joinGuild(@PathVariable long guildId) {
        final User sender = userService.getClientUser();
        Guild guild;
        
        try {
            guild = guildService.getGuild(guildId);
        } catch (GuildNotFoundException e) {
            return "GuildNotFoundException";
        }
        
        final boolean canJoin = true; // Preparing for future
        
        System.out.println(sender.getUsername() + "  " + guild.getName());
        
        if (canJoin) {
            sender.joinGuild(guild);
            userRepository.save(sender);
            return "/guild/" + guildId;
        }
        
        return "CannotJoinException";
    }
    
    
    @GetMapping(value = "/api/group/{groupId}/getMessages/{num}")
    public List<DirectMessageJSON> getDirectMessages(@PathVariable int num, @PathVariable long groupId) {
        if (num < 0) { throw new Error("Invalid api request. Parameter 'num' must be positive."); }
        if (num == 0) { return new ArrayList<>(); }
        num = Math.min(num, 200);
        final Group group = groupService.getGroup(groupId);
        
        List<DirectMessage> messages = group.getMessages();
        int size = messages.size();
        
        if (size <= num) {
            return this.convertDM(messages);
        } else {
            return this.convertDM(messages.subList(size - num, size));
        }
        
    }
}
