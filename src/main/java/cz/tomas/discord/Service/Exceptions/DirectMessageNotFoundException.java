package cz.tomas.discord.Service.Exceptions;

public class DirectMessageNotFoundException extends RuntimeException {
    
    public DirectMessageNotFoundException(long id) { super("Direct message with ID " + id + " not found"); }
}
