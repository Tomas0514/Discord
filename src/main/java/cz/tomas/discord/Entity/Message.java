package cz.tomas.discord.Entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Represents a message.
 */

@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Channel channel;

    @Column(nullable = false, length = 2048)
    private String content;

    @ManyToOne
    private User author;

    @Column
    private LocalDateTime timestamp;


    // --- Constructors ---
    private Message() {}

    /**
     * Creates and sends a new message
     *
     * @param channel The channel where the message is
     * @param content The content of the message
     * @param author  The author of the message
     */
    public Message(Channel channel, String content, User author) {
        this.channel = channel;
        this.content = content;
        this.author = author;
        timestamp = LocalDateTime.now();
        channel.addMessage(this);
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
     * Get channel.
     * @return Channel
     */
    public Channel getChannel() {
        return channel;
    }
    
    /**
     * Get context.
     * @return Context
     */
    public String getContent() {
        return content;
    }
    
    /**
     * Get author.
     * @return {@link User}
     */
    public User getAuthor() {
        return author;
    }
    
    /**
     * Get timestamp.
     * @return LocalDateTime
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
