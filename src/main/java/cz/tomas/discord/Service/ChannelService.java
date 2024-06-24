package cz.tomas.discord.Service;

import cz.tomas.discord.Entity.Channel;
import cz.tomas.discord.Repository.ChannelRepository;
import cz.tomas.discord.Service.Exceptions.ChannelNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ChannelService {
    
    protected final ChannelRepository channelRepository;
    
    public ChannelService(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }
    
    public Channel getChannel(long id) {
        return channelRepository.findById(id).orElseThrow(() -> new ChannelNotFoundException(id));
    }
}
