package cz.tomas.discord;

import cz.tomas.discord.Entity.Channel;
import cz.tomas.discord.Entity.Guild;
import cz.tomas.discord.Entity.Message;
import cz.tomas.discord.Repository.*;
import cz.tomas.discord.Repository.GuildRepository;
import cz.tomas.discord.Repository.UserRepository;
import cz.tomas.discord.Service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommandLineApplication implements CommandLineRunner {

    private final GuildRepository guildRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public CommandLineApplication(GuildRepository guildRepository, UserRepository userRepository, UserService userService) {
        this.guildRepository = guildRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Override
    public void run(String... args) {
        userService.createUser("admin", "", List.of("DISCORD"));
        
        final var guild1 = new Guild("Project");
        final var guild2 = new Guild("Test Guild");
        final var channel1 = new Channel("Chat"     , guild1);
        final var channel2 = new Channel("Memes"    , guild1);
        final var channel3 = new Channel("Sus"      , guild1);
        final var channel4 = new Channel("Minecraft", guild2);
        final var channel5 = new Channel("General"  , guild2);
        final var channel6 = new Channel("Cat Pics" , guild2);

        final var user1 = userService.createUser("John"   , "", List.of("USER"));
        final var user2 = userService.createUser("Alice"  , "", List.of("USER"));
        final var user3 = userService.createUser("Lukas"  , "", List.of("USER"));
        final var user4 = userService.createUser("Charlot", "", List.of("USER"));
        final var user5 = userService.createUser("Max"    , "", List.of("USER"));
        final var user6 = userService.createUser("Patrick", "", List.of("USER"));

        user1.joinGuild(guild1);
        user2.joinGuild(guild1);
        user3.joinGuild(guild2);
        user4.joinGuild(guild2);
        user5.joinGuild(guild1);
        user5.joinGuild(guild2);

        new Message(channel1, "Hello", user1);
        new Message(channel1, "Hi", user2);
        
        new Message(channel2, "Testing...", user2);

        new Message(channel3, "New Guild!", user3);
        for (int i = 0; i < 10; i++) {
            new Message(channel3, "Alice SUS!", user5);
            new Message(channel3, "NO, Max is SUS! I saw him went!", user2);
        }
        
        new Message(channel4, "I have full diamond, NOOBs!", user5);
        new Message(channel4, "LOL, I had full diamond like a week ago, noob.", user3);
        new Message(channel4, "I already have 2 netherite armor and tool sets.", user4);
        new Message(channel4, "NOOB, LOL, cringe, L + ratio, get better", user4);
        
        new Message(channel5, "My grandma died yesterday :(", user5);
        new Message(channel5, "L + ratio", user4);
        new Message(channel5, "She should have been better", user3);
        
        new Message(channel6, "Look at my cat :D", user5);
        new Message(channel6, "I refuse", user4);
        new Message(channel6, "Why?", user5);
        new Message(channel6, "Because my cat is better", user4);
        new Message(channel6, "Ha, ha, ha, these peasants don't know what a pretty cat looks like", user3);

        guildRepository.save(guild1);
        guildRepository.save(guild2);
    }
}
