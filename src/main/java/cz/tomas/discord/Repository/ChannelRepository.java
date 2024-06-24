package cz.tomas.discord.Repository;

import cz.tomas.discord.Entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChannelRepository extends JpaRepository<Channel, Long> {
}
