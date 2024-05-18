package cz.tomas.discord.Entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class DirectMessage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Group group;

    @Column(nullable = false, length = 2048)
    private String content;

    @ManyToOne
    private User author;

    @Column
    private LocalDateTime timestamp;


    // --- Constructors ---
    public DirectMessage() {}

    /**
     * Creates and sends a new direct message
     *
     * @param group The group where the message is
     * @param content The content of the message
     * @param author  The author of the message
     */
    public DirectMessage(Group group, String content, User author) {
        this.group = group;
        this.content = content;
        this.author = author;
        timestamp = LocalDateTime.now();
        group.addMessage(this);
    }


    // --- Getters and setters ---

    public long getId() {
        return id;
    }

    public Group getGroup() {
        return group;
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
