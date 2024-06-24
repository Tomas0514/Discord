package cz.tomas.discord.Chat;

public class ChatChannel {
    
    private long id;
    
    private long guildId;
    
    private String name;
    
    private GuildType type;
    
    private ActionType actionType;
    
    
    
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public long getGuildId() {
        return guildId;
    }
    
    public void setGuildId(long guildId) {
        this.guildId = guildId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public ActionType getActionType() {
        return actionType;
    }
    
    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }
    
    public GuildType getType() {
        return type;
    }
    
    public void setType(GuildType type) {
        this.type = type;
    }
}
