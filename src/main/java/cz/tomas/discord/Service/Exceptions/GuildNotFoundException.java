package cz.tomas.discord.Service.Exceptions;

public class GuildNotFoundException extends RuntimeException {
    
    public GuildNotFoundException(long id) { super("Guild with ID " + id + " not found"); }
}
