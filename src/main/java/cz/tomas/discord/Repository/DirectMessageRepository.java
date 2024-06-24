package cz.tomas.discord.Repository;

import cz.tomas.discord.Entity.DirectMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DirectMessageRepository extends JpaRepository<DirectMessage, Long> {
}
