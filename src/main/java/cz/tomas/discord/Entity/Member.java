package cz.tomas.discord.Entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Represents a member.
 */

@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Guild guild;

    @Column(nullable = false)
    private LocalDateTime joinedAt;


    // --- Constructors ---
    private Member() {}
    
    /**
     * Creates a member.
     * @param user User
     * @param guild Guild
     */
    public Member(User user, Guild guild) {
        this.user = user;
        this.guild = guild;
        joinedAt = LocalDateTime.now();
        guild.addMember(this);
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
     * Get user.
     * @return User
     */
    public User getUser() {
        return user;
    }
    
    /**
     * Get guild.
     * @return guild
     */
    public Guild getGuild() {
        return guild;
    }
    
    /**
     * Get joined at.
     * @return LocalDateTime
     */
    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }
}
