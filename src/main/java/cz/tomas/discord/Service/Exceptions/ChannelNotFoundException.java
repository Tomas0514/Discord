package cz.tomas.discord.Service.Exceptions;

public class ChannelNotFoundException extends RuntimeException {
    
    public ChannelNotFoundException(long channelId) { super("Channel with ID " + channelId + " not found"); }
}
