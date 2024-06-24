package cz.tomas.discord.Chat;

public class ChatGuild {
    
    private long id;
    private String owner;
    private String name;
    private GuildType type;
    private ActionType actionType;
    
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
    
    public GuildType getType() {
        return type;
    }
    
    public void setType(GuildType type) {
        this.type = type;
    }
    
    public ActionType getActionType() {
        return actionType;
    }
    
    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }
    
    public String getOwner() {
        return owner;
    }
    
    public void setOwner(String owner) {
        this.owner = owner;
    }
}
