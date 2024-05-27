package cz.tomas.discord.Entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a group/friendship.
 * Is the same as {@link Guild}, but instead of members it has friends.
 * Has a build in "channel".
 */

@Entity
@Table(name = "group_discord")
public class Group {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Friend> friends = new ArrayList<>();
    
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<DirectMessage> messages = new ArrayList<>();
    
    
    // -- Constructor ---
    public Group () {}
    
    
    // --- Getters and setters
    
    /**
     * Get id.
     * @return Id
     */
    public long getId() {
        return id;
    }
    
    /**
     * Get friends.
     * @return List of {@link Friend}s aka members
     */
    public List<Friend> getFriends() {
        return friends;
    }
    
    /**
     * Returns the other user in a group.
     * If there's more than 2 users in group returns Null.
     * @param user User
     * @return User or Null
     */
    public User getTheOtherOne(User user) {
        List<User> members = new ArrayList<>();
        friends.forEach(friend -> members.add(friend.getUser()));
        if (members.size() > 2) {
            return null;
        }
        if (members.get(0) == user) {
            return members.get(1);
        }
        return members.get(0);
    }
    
    /**
     * Get name of the other user in a group or stream of all users.
     * @param user User
     * @return Name
     */
    public String getName(User user) {
        List<User> members = new ArrayList<>();
        friends.forEach(friend -> members.add(friend.getUser()));
        if (members.size() > 2) {
            return members.stream()
                    .map(User::getUsername)
                    .collect(Collectors.joining(", "));
        }
        if (members.get(0) == user) { return members.get(1).getUsername(); }
        return members.get(0).getUsername();
    }
    
    /**
     * Adds a friend.
     * @param friend Friend
     */
    public void addFriend(Friend friend) {
        friends.add(friend);
    }
    
    /**
     * Get messages.
     * @return List of {@link DirectMessage}s
     */
    public List<DirectMessage> getMessages() {
        return messages;
    }
    
    /**
     * Adds a message.
     * @param message DirectMessage
     */
    public void addMessage(DirectMessage message) {
        messages.add(message);
    }
}
