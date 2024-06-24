package cz.tomas.discord.Service;

import cz.tomas.discord.Entity.Guild;
import cz.tomas.discord.Repository.GuildRepository;
import cz.tomas.discord.Service.Exceptions.GuildNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class GuildService {
    
    protected final GuildRepository guildRepository;
    
    public GuildService(GuildRepository guildRepository) {
        this.guildRepository = guildRepository;
    }
    
    /**
     * Finds a guild by id or throws a {@link GuildNotFoundException}.
     * @param id Guild's id
     * @return {@link Guild}
     */
    public Guild getGuild(long id) {
        return guildRepository.findById(id).orElseThrow(() -> new GuildNotFoundException(id));
    }
}
