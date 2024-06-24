package cz.tomas.discord.Chat;


import java.time.LocalDateTime;

public class ChatMessage {
    
    private long id;
    
    private String content;
    
    private String author;
    
    private long channelId;
    
    private long groupId;
    
    private ChatMessageType type;
    
    private ActionType actionType;
    
    private long timestamp;
    
    /**
     * In milliseconds
      */
    private int timezoneOffset;
    
    
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
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    public long getChannelId() {
        return channelId;
    }
    
    public void setChannelId(long channelId) {
        this.channelId = channelId;
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
    
    public int getTimezoneOffset() {
        return timezoneOffset;
    }
    
    public void setTimezoneOffset(int timezoneOffset) {
        this.timezoneOffset = timezoneOffset;
    }
    
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public ActionType getActionType() {
        return actionType;
    }
    
    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }
}
