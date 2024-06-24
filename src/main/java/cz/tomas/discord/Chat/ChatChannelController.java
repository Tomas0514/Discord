package cz.tomas.discord.Chat;

import cz.tomas.discord.Entity.Channel;
import cz.tomas.discord.Entity.Guild;
import cz.tomas.discord.Repository.ChannelRepository;
import cz.tomas.discord.Service.GuildService;
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
public class ChatChannelController {
    
    final GuildService guildService;
    final ChannelRepository channelRepository;
    private final SimpMessageSendingOperations messagingTemplate;
    
    public ChatChannelController(GuildService guildService, ChannelRepository channelRepository, SimpMessageSendingOperations messagingTemplate) {
        this.guildService = guildService;
        this.channelRepository = channelRepository;
        this.messagingTemplate = messagingTemplate;
    }
    
    @MessageMapping("/chat.createChannel")
    public void createChannel(@Payload ChatChannel channel) {
        
        if (channel.getType() != GuildType.CHANNEL) { throw new Error("Invalid guild type"); }
        if (channel.getActionType() != ActionType.CREATE) { throw new Error("Invalid action type"); }
        long guildId = channel.getGuildId();
        String destination = "/topic/guild/" + guildId;
        Guild guild = guildService.getGuild(guildId);
        
        Channel chnl = channelRepository.save(new Channel(channel.getName(), guild));
        channel.setId(chnl.getId());
        
        System.out.println(formatMsToISO(new Date().getTime()) + "  CHANNEL CREATE [" + destination + "] Guild: " + guild.getName() + ": " + channel.getName());
        
        messagingTemplate.convertAndSend(destination, channel);
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
