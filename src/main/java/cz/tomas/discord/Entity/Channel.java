package cz.tomas.discord.Entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

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
    public Channel() {}

    /**
     * Constructor
     * @param name The name of the channel
     * @param guild The guild where the channel is
     */
    public Channel(String name, Guild guild) {
        this.name = name;
        this.guild = guild;
        guild.addChannel(this);
    }


    // --- Getters and setters ---

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Guild getGuild() {
        return guild;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void addMessage(Message message) {
        messages.add(message);
    }

}
