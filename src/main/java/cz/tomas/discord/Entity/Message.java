package cz.tomas.discord.Entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

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
    public Message() {}

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

    public long getId() {
        return id;
    }

    public Channel getChannel() {
        return channel;
    }

    public String getContent() {
        return content;
    }

    public User getAuthor() {
        return author;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
