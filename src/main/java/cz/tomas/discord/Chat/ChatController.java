package cz.tomas.discord.Chat;

import cz.tomas.discord.Entity.DirectMessage;
import cz.tomas.discord.Entity.Group;
import cz.tomas.discord.Entity.Guild;
import cz.tomas.discord.Entity.Message;
import cz.tomas.discord.Repository.GroupRepository;
import cz.tomas.discord.Repository.GuildRepository;
import cz.tomas.discord.Service.GroupService;
import cz.tomas.discord.Service.GuildService;
import cz.tomas.discord.Service.UserService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {
    
    final UserService userService;
    final GuildService guildService;
    final GuildRepository guildRepository;
    final GroupService groupService;
    final GroupRepository groupRepository;
    private final SimpMessageSendingOperations messagingTemplate;
    
    
    public ChatController(UserService userService, GuildService guildService, GuildRepository guildRepository, GroupService groupService, GroupRepository groupRepository, SimpMessageSendingOperations messagingTemplate) {
        this.userService = userService;
        this.guildService = guildService;
        this.guildRepository = guildRepository;
        this.groupService = groupService;
        this.groupRepository = groupRepository;
        this.messagingTemplate = messagingTemplate;
    }
    
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage message) {
        String destination;
        
        if (message.getType() == ChatMessageType.GUILD) {
            final long guildId = message.getGuildId();
            final long channelId = message.getChannelId();
            destination = "/topic/guild/" + guildId + "/" + channelId;
            final Guild guild = guildService.getGuild(guildId);
            
            new Message(guildService.getChannel(guild, channelId), message.getContent(), userService.getUser(message.getAuthor()));
            guildRepository.save(guild);
            
        } else if (message.getType() == ChatMessageType.DIRECT) {
            final long groupId = message.getGroupId();
            destination = "/topic/group/" + groupId;
            final Group group = groupService.getGroup(groupId);
            
            new DirectMessage(group, message.getContent(), userService.getUser(message.getAuthor()));
            groupRepository.save(group);
        } else {
            throw new RuntimeException("Invalid message received");
        }
        
        
        System.out.println(message.getTimestamp() + "  [" + message.getType() + " MESSAGE] [" + destination + "] " + message.getAuthor() + ": " + message.getContent());
        
        messagingTemplate.convertAndSend(destination, message);
    }
}
