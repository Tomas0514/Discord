package cz.tomas.discord.Service;

import cz.tomas.discord.Entity.Channel;
import cz.tomas.discord.Entity.DirectMessage;
import cz.tomas.discord.Entity.Message;
import cz.tomas.discord.Entity.User;
import cz.tomas.discord.Repository.DirectMessageRepository;
import cz.tomas.discord.Repository.MessageRepository;
import cz.tomas.discord.Service.Exceptions.DirectMessageNotFoundException;
import cz.tomas.discord.Service.Exceptions.MessageNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MessageService {
    
    protected final MessageRepository messageRepository;
    protected final DirectMessageRepository dMessageRepository;
    
    public MessageService(MessageRepository messageRepository, DirectMessageRepository dMessageRepository) {
        this.messageRepository = messageRepository;
        this.dMessageRepository = dMessageRepository;
    }
    
    public Message getMessage(long id) {
        return messageRepository.findById(id).orElseThrow(() -> new MessageNotFoundException(id));
    }
    
    public DirectMessage getDirectMessage(long id) {
        return dMessageRepository.findById(id).orElseThrow(() -> new DirectMessageNotFoundException(id));
    }
}
