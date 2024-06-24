package cz.tomas.discord.Service.Exceptions;

public class GroupNotFoundException extends RuntimeException {
    
    public GroupNotFoundException(long id) { super("Group with ID " + id + " not found"); }
}
