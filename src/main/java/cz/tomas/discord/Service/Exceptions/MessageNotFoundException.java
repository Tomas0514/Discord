package cz.tomas.discord.Service.Exceptions;

import cz.tomas.discord.Entity.Channel;
import cz.tomas.discord.Entity.User;

public class MessageNotFoundException extends RuntimeException {
    public MessageNotFoundException(long id) { super("Message with ID " + id + " not found"); }
    
    public MessageNotFoundException(Channel channel, User user, long timestamp) {
        super(
            "Message in channel with ID " + channel.getId() +
            " by user with ID " + user.getId() +
            " at timestamp " + timestamp + " not found"
        );
    }
}
