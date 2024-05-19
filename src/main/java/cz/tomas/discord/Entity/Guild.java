package cz.tomas.discord.Entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a guild.
 */

@Entity
@Table(name = "guilds")
public class Guild {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 32)
    private String name;

    @OneToMany(mappedBy = "guild", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Channel> channels = new ArrayList<>();

    @OneToMany(mappedBy = "guild", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Member> members = new ArrayList<>();


    // --- Constructors ---
    private Guild() {}
    
    /**
     * Creates a guild.
     * @param name The name of the guild
     */
    public Guild(String name) {
        this.name = name;
    }


    // --- Getters and setters ---
    
    /**
     * Get Id.
     * @return Id
     */
    public long getId() {
        return id;
    }
    
    /**
     * Get name.
     * @return Name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Get channels.
     * @return List of {@link Channel}s
     */
    public List<Channel> getChannels() {
        return channels;
    }
    
    /**
     * Adds a channel.
     * @param channel Channel
     */
    public void addChannel(Channel channel) {
        channels.add(channel);
    }
    
    /**
     * Get members.
     * @return List of {@link Member}s
     */
    public List<Member> getMembers() {
        return members;
    }
    
    /**
     * Adds a member.
     * @param member Member
     */
    public void addMember(Member member) {
        members.add(member);
    }
}
