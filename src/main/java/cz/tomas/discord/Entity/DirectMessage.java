package cz.tomas.discord.Entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * Represents a direct message.
 * Is the same as a normal {@link Message}, but instead of a channel it has a group.
 */

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
    private long timestamp;


    // --- Constructors ---
    private DirectMessage() {}

    /**
     * Creates and sends a new direct message.
     *
     * @param group The group where the message is
     * @param content The content of the message
     * @param author  The author of the message
     */
    public DirectMessage(Group group, String content, User author) {
        this.group = group;
        this.content = content;
        this.author = author;
        timestamp = new Date().getTime();
        group.addMessage(this);
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
     * Get group.
     * @return {@link Group}
     */
    public Group getGroup() {
        return group;
    }
    
    /**
     * Get content.
     * @return Content
     */
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
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
     * @return The number of milliseconds since January 1, 1970, 00:00:00 GMT
     */
    public long getTimestamp() {
        return timestamp;
    }
}
