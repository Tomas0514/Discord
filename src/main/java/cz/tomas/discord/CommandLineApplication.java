package cz.tomas.discord;

import cz.tomas.discord.Entity.*;
import cz.tomas.discord.Repository.GroupRepository;
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
    private final GroupRepository groupRepository;
    private final UserService userService;

    public CommandLineApplication(GuildRepository guildRepository, UserRepository userRepository, GroupRepository groupRepository, UserService userService) {
        this.guildRepository = guildRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.userService = userService;
    }

    @Override
    public void run(String... args) {
        createTestingData();
    }
    
    private void createTestingData() {
        userService.createUser("admin", "", List.of("DISCORD"));
    
        final var guild1 = new Guild("Project");
        final var guild2 = new Guild("Test Guild");
        final var channel1 = new Channel("Chat"     , guild1);
        final var channel2 = new Channel("Memes"    , guild1);
        final var channel3 = new Channel("Sus"      , guild1);
        final var channel4 = new Channel("Minecraft", guild2);
        final var channel5 = new Channel("General"  , guild2);
        final var channel6 = new Channel("Cat Pics" , guild2);
        final var channel7 = new Channel("gcsfdlbhjmlcmhbkgflmbhkgmlbhjgamlbhjfdbhjlcgfdgcabhcafdhbjmagcmjhbfd", guild1);
    
        final var John = userService.createUser("John"   , "", List.of("USER"));
        final var Alice = userService.createUser("Alice"  , "", List.of("USER"));
        final var Lukas = userService.createUser("Lukas"  , "", List.of("USER"));
        final var Charlot = userService.createUser("Charlot", "", List.of("USER"));
        final var Max = userService.createUser("Max"    , "", List.of("USER"));
        final var Patrick = userService.createUser("Patrick", "", List.of("USER"));
        final var ThatGuy = userService.createUser("ThatOneGuyThatHasAReallyLongName", "", List.of("USER"));
    
        final var group1 = new Group();
        final var group2 = new Group();
    
        John.joinGroup(group1);
        Alice.joinGroup(group1);
        Lukas.joinGroup(group1);
        John.joinGroup(group2);
        Alice.joinGroup(group2);
    
        new DirectMessage(group1, "Hello guys!", John);
        new DirectMessage(group1, "Why am i here", Lukas);
        new DirectMessage(group1, "We wanna play minecraft", Alice);
        new DirectMessage(group1, "-----!!!IMPOSTOR!!!-----", ThatGuy);
    
        new DirectMessage(group2, "u free rn?", John);
        new DirectMessage(group2, "Not rn, gimme 5 min", Alice);
        new DirectMessage(group2, "Ok", John);
    
        John.sendRequest(Max);
        Charlot.sendRequest(John);
        Lukas.sendRequest(John);
    
        groupRepository.save(group1);
        groupRepository.save(group2);

//        final Group testGroup = new Group();
//        for (int i = 0; i < 16; i++) {
//            user5.sendFriendRequest(user1);
//            user1.createFriendship(user6, testGroup);
//            user1.createFriendship(user6, testGroup);
//            user1.createFriendship(user6, testGroup);
//            user1.joinGuild(guild1);
//            user1.joinGuild(guild1);
//            user1.joinGuild(guild1);
//            new Channel("Chat a " + i, guild1);
//            new Channel("Chat b " + i, guild1);
//            new Channel("Chat c " + i, guild1);
//        }
//        groupRepository.save(testGroup);
    
        userRepository.save(John);
        userRepository.save(Alice);
        userRepository.save(Lukas);
        userRepository.save(Max);
    
        John.joinGuild(guild1);
        Alice.joinGuild(guild1);
        Lukas.joinGuild(guild2);
        Charlot.joinGuild(guild2);
        Max.joinGuild(guild1);
        Max.joinGuild(guild2);
        ThatGuy.joinGuild(guild1);
        ThatGuy.joinGuild(guild2);
    
        new Message(channel1, "Hello", John);
        new Message(channel1, "Hi", Alice);
    
        new Message(channel2, "Testing...", Alice);
    
        new Message(channel3, "New Guild!", Lukas);
        for (int i = 0; i < 10; i++) {
            new Message(channel3, "Alice SUS!", Max);
            new Message(channel3, "NO, Max is SUS! I saw him went!", Alice);
        }
    
        new Message(channel4, "I have full diamond, NOOBs!", Max);
        new Message(channel4, "LOL, I had full diamond like a week ago, noob.", Lukas);
        new Message(channel4, "I already have 2 netherite armor and tool sets.", Charlot);
        new Message(channel4, "NOOB, LOL, cringe, L + ratio, get better", Charlot);
    
        new Message(channel5, "My grandma died yesterday :(", Max);
        new Message(channel5, "L + ratio", Charlot);
        new Message(channel5, "She should have been better", Lukas);
    
        new Message(channel6, "Look at my cat :D", Max);
        new Message(channel6, "I refuse", Charlot);
        new Message(channel6, "Why?", Max);
        new Message(channel6, "Because my cat is better", Charlot);
        new Message(channel6, "Ha, ha, ha, these peasants don't know what a pretty cat looks like", Lukas);
    
        guildRepository.save(guild1);
        guildRepository.save(guild2);
    }
}
