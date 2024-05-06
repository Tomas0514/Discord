package cz.tomas.discord.Service;

import cz.tomas.discord.Entity.Channel;
import cz.tomas.discord.Entity.Guild;
import cz.tomas.discord.Repository.GuildRepository;
import org.springframework.stereotype.Service;

@Service
public class GuildService {
    
    protected final GuildRepository guildRepository;
    
    public GuildService(GuildRepository guildRepository) {
        this.guildRepository = guildRepository;
    }
    
    /**
     * Finds a guild by id or throws a {@link GuildNotFoundException}
     * @param id Guild's id
     * @return {@link Guild}
     */
    public Guild getGuild(long id) {
        return guildRepository.findById(id).orElseThrow(() -> new GuildNotFoundException(id));
    }
    
    /**
     * Find a channel in a guild by id or throws a {@link ChannelNotFoundException}
     * @param guild Guild
     * @param channelId Channel's id
     * @return {@link Channel}
     */
    public Channel getChannel(Guild guild, long channelId) {
        return guild.getChannels().stream()
                .filter(channel -> channel.getId() == channelId)
                .findFirst().orElseThrow(() -> new ChannelNotFoundException(guild.getId(), channelId));
    }
    
    /**
     * Find a channel in a guild by id or throws a {@link GuildNotFoundException} or {@link ChannelNotFoundException}
     * @param guildId Guild' id
     * @param channelId Channel's id
     * @return {@link Channel}
     */
    public Channel getChannel(long guildId, long channelId) {
        final Guild guild = getGuild(guildId);
        return guild.getChannels().stream()
                .filter(channel -> channel.getId() == channelId)
                .findFirst().orElseThrow(() -> new ChannelNotFoundException(guildId, channelId));
    }
}
