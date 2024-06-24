package cz.tomas.discord.Repository;

import cz.tomas.discord.Entity.Channel;
import cz.tomas.discord.Entity.Message;
import cz.tomas.discord.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
