package cz.tomas.discord.Chat;


import java.time.LocalDateTime;

public class ChatMessage {
    
    private String content;
    
    private String author;
    
    private long channelId;
    
    private long guildId;
    
    private long groupId;
    
    private ChatMessageType type;
    
    private LocalDateTime timestamp;
    
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public long getChannelId() {
        return channelId;
    }
    
    public void setChannelId(long channelId) {
        this.channelId = channelId;
    }
    
    public long getGuildId() {
        return guildId;
    }
    
    public void setGuildId(long guildId) {
        this.guildId = guildId;
    }
    
    public long getGroupId() {
        return groupId;
    }
    
    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }
    
    public ChatMessageType getType() {
        return type;
    }
    
    public void setType(ChatMessageType type) {
        this.type = type;
    }
}
