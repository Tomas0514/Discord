package cz.tomas.discord.Entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a channel.
 */

@Entity
public class Channel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    private Guild guild;

    @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();


    // --- Constructors ---
    private Channel() {}

    /**
     * Creates a channel.
     * @param name The name of the channel
     * @param guild The guild where the channel is
     */
    public Channel(String name, Guild guild) {
        this.name = name;
        this.guild = guild;
        guild.addChannel(this);
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
     * Set name.
     * @param name New name
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Get guild.
     * @return {@link Guild}
     */
    public Guild getGuild() {
        return guild;
    }
    
    /**
     * Get messages.
     * @return List of {@link Message}s
     */
    public List<Message> getMessages() {
        return messages;
    }
    
    /**
     * Adds a message.
     * @param message Message
     */
    public void addMessage(Message message) {
        messages.add(message);
    }

}
