package cz.tomas.discord.Service;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(long id) { super("User with ID " + id + " not found"); }
    
    public UserNotFoundException(String username) { super("User with username " + username + " not found"); }
}
