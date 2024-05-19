package cz.tomas.discord.Entity;

import jakarta.persistence.*;

/**
 * Represents a friend
 * Is the same as a {@link Member}, but instead of a guild it has a group
 */

@Entity
public class Friend {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    @ManyToOne
    private User user;
    
    @ManyToOne
    private Group group;
    
    
    // --- Constructors ---
    private Friend() {}
    
    /**
     * Creates a friend.
     * @param user User
     * @param group Group
     */
    public Friend(User user, Group group) {
        this.user = user;
        this.group = group;
        user.addFriend(this);
        group.addFriend(this);
    }
    
    
    // --- Getters and setters ---
    
    /**
     * Get Id
     * @return Id
     */
    public long getId() {
        return id;
    }
    
    /**
     * Get user
     * @return {@link User}
     */
    public User getUser() {
        return user;
    }
    
    /**
     * Get group
     * @return {@link Group}
     */
    public Group getGroup() {
        return group;
    }
}
