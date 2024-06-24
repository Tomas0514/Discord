package cz.tomas.discord.Service.Exceptions;

public class UserAlreadyExistsException extends RuntimeException {
    
    public UserAlreadyExistsException(String username) { super("User with username " + username + " already exists"); }
}
