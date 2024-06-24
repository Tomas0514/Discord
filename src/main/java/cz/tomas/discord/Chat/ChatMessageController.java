package cz.tomas.discord.Chat;

import cz.tomas.discord.Entity.*;
import cz.tomas.discord.Repository.*;
import cz.tomas.discord.Service.*;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Date;

@Controller
public class ChatMessageController {
    
    final UserService userService;
    final GroupService groupService;
    final MessageService messageService;
    final MessageRepository messageRepository;
    final DirectMessageRepository dMessageRepository;
    final ChannelService channelService;
    private final SimpMessageSendingOperations messagingTemplate;
    
    
    public ChatMessageController(UserService userService, GroupService groupService, MessageService messageService, SimpMessageSendingOperations messagingTemplate, MessageRepository messageRepository, DirectMessageRepository dMessageRepository, ChannelService channelService) {
        this.userService = userService;
        this.groupService = groupService;
        this.messageService = messageService;
        this.messageRepository = messageRepository;
        this.messagingTemplate = messagingTemplate;
        this.dMessageRepository = dMessageRepository;
        this.channelService = channelService;
    }
    
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage message) {
        final User author = userService.getUser(message.getAuthor());
        String destination;
        
        if (message.getActionType() != ActionType.SEND) { throw new Error("Invalid action type"); }
        //if (author != userService.getClientUser()) { throw new Error("Users don't match up"); }
        if (message.getType() == ChatMessageType.GUILD) {
            final long channelId = message.getChannelId();
            final Channel channel = channelService.getChannel(channelId);
            destination = "/topic/guild/" + channel.getGuild().getId() + "/" + channelId;
            
            Message msg = messageRepository.save(new Message(channel, message.getContent(), author));
            
            message.setId(msg.getId());
            message.setTimestamp(msg.getTimestamp());
            
        } else if (message.getType() == ChatMessageType.DIRECT) {
            final long groupId = message.getGroupId();
            final Group group = groupService.getGroup(groupId);
            destination = "/topic/group/" + groupId;
            
            DirectMessage msg = dMessageRepository.save(new DirectMessage(group, message.getContent(), author));
            
            message.setId(msg.getId());
            message.setTimestamp(msg.getTimestamp());
            
        } else {
            throw new Error("Invalid message received");
        }
        
        
        System.out.println(formatMsToISO(message.getTimestamp()) + "  " + message.getType() + " MESSAGE [" + destination + "] " + message.getAuthor() + ": " + message.getContent());
        
        messagingTemplate.convertAndSend(destination, message);
    }
    
    
    @MessageMapping("/chat.editMessage")
    public void editMessage(@Payload ChatMessage msg) {
        String destination;
        String old;
        
        if (msg.getActionType() != ActionType.EDIT) { throw new Error("Invalid action type"); }
        if (msg.getType() == ChatMessageType.GUILD) {
            final Message message = messageService.getMessage(msg.getId());
            final Channel channel = message.getChannel();
            destination = "/topic/guild/" + channel.getGuild().getId() + "/" + channel.getId();
            old = message.getContent();
            
            message.setContent(msg.getContent());
            messageRepository.save(message);
            
            msg.setTimestamp(message.getTimestamp());
        } else if (msg.getType() == ChatMessageType.DIRECT) {
            final DirectMessage message = messageService.getDirectMessage(msg.getId());
            destination = "/topic/group/" + message.getGroup().getId();
            old = message.getContent();
            
            message.setContent(msg.getContent());
            dMessageRepository.save(message);
        } else {
            throw new Error("Invalid edit request");
        }
        
        System.out.println(formatMsToISO(new Date().getTime()) + "  " + msg.getType() + " EDIT [" + destination + "] " + msg.getAuthor() + ": ID: " + msg.getId() + ": " + old + "\n" + " ".repeat(65) + "=> " + msg.getContent());
        
        messagingTemplate.convertAndSend(destination, msg);
    }
    
    @MessageMapping("/chat.deleteMessage")
    public void deleteMessage(@Payload ChatMessage msg) {
        String destination;
        
        if (msg.getActionType() != ActionType.DELETE) { throw new Error("Invalid action type"); }
        if (msg.getType() == ChatMessageType.GUILD) {
            final Message message = messageService.getMessage(msg.getId());
            final Channel channel = message.getChannel();
            destination = "/topic/guild/" + channel.getGuild().getId() + "/" + channel.getId();
            
            messageRepository.delete(message);
            
        } else if (msg.getType() == ChatMessageType.DIRECT) {
            final DirectMessage message = messageService.getDirectMessage(msg.getId());
            destination = "/topic/group/" + message.getGroup().getId();
            
            dMessageRepository.delete(message);
        } else {
            throw new Error("Invalid delete request");
        }
        
        System.out.println(formatMsToISO(new Date().getTime()) + "  " + msg.getType() + " DELETE [" + destination + "] " + msg.getAuthor() + ": ID: " + msg.getId());
        
        messagingTemplate.convertAndSend(destination, msg);
    }
    
    private String formatMsToISO(long ms) {
        Instant instant = Instant.ofEpochMilli(ms);
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .append(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                .appendOffsetId()
                .toFormatter()
                .withZone(ZoneId.systemDefault());
        return formatter.format(instant);
    }
}
