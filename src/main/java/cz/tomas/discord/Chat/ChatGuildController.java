package cz.tomas.discord.Chat;

import cz.tomas.discord.Entity.Guild;
import cz.tomas.discord.Entity.Member;
import cz.tomas.discord.Entity.User;
import cz.tomas.discord.Repository.GuildRepository;
import cz.tomas.discord.Service.GuildService;
import cz.tomas.discord.Service.UserService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Date;

@Controller
public class ChatGuildController {
    
    final GuildRepository guildRepository;
    final UserService userService;
    final GuildService guildService;
    private final SimpMessageSendingOperations messagingTemplate;
    
    public ChatGuildController(GuildRepository guildRepository, UserService userService, GuildService guildService, SimpMessageSendingOperations messagingTemplate) {
        this.guildRepository = guildRepository;
        this.userService = userService;
        this.guildService = guildService;
        this.messagingTemplate = messagingTemplate;
    }
    
    @MessageMapping("/chat.renameGuild")
    public void renameGuild(@Payload ChatGuild guild, java.security.Principal user) {
        final User author = userService.getUser(user.getName());
        
        if (guild.getType() != GuildType.GUILD) { throw new Error("Invalid guild type"); }
        if (guild.getActionType() != ActionType.RENAME) { throw new Error("Invalid action type"); }
        String destination = "/topic/guild/"+guild.getId();
        

        Guild g = guildService.getGuild(guild.getId());
        final String old = g.getName();
        
        g.setName(guild.getName());
        guildRepository.save(g);
        
        System.out.println(formatMsToISO(new Date().getTime()) + "  GUILD RENAME [" + destination + "] " + old + " => " + guild.getName());
        
        messagingTemplate.convertAndSend(destination, guild);
    }
    
    @MessageMapping("/chat.deleteGuild")
    public void deleteGuild(@Payload ChatGuild guild, java.security.Principal user) {
        final User author = userService.getUser(user.getName());
        
        if (guild.getType() != GuildType.GUILD) { throw new Error("Invalid guild type"); }
        if (guild.getActionType() != ActionType.DELETE) { throw new Error("Invalid action type"); }
        String destination = "/topic/guild/"+guild.getId();
        
        
        Guild g = guildService.getGuild(guild.getId());
        guildRepository.delete(g);
        
        System.out.println(formatMsToISO(new Date().getTime()) + "  GUILD DELETE [" + destination + "] ID: " + guild.getId() + " | " + guild.getName());
        
        messagingTemplate.convertAndSend(destination, guild);
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
