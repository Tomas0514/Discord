package cz.tomas.discord.Service;

public class ChannelNotFoundException extends RuntimeException {
    
    public ChannelNotFoundException(long guildId, long channelId) { super("Channel with ID " + channelId + " in guild with ID " + guildId + " not found"); }
}
