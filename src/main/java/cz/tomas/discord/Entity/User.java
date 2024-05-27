package cz.tomas.discord.Entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user.
 */

@Entity
@Table(name = "users_discord")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true, length = 32)
    private String username;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Member> members = new ArrayList<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Friend> friends = new ArrayList<>();
    
    @ManyToMany()
    private List<User> requests = new ArrayList<>();

    
    // --- Constructors ---
    private User() {}
    
    /**
     * Creates a user.
     * @param username User's username
     */
    public User(String username) {
        this.username = username;
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
     * Get username.
     * @return Username
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Get members.
     * @return List of {@link Member}s
     */
    public List<Member> getMembers() {
        return members;
    }
    
    /**
     * Get guilds.
     * @return List of {@link Guild}s
     */
    public List<Guild> getGuilds() {
        List<Guild> guilds = new ArrayList<>();
        members.forEach(member -> guilds.add(member.getGuild()));
        return guilds;
    }
    
    /**
     * Joins a guild.
     * @param guild Guild
     */
    public void joinGuild(Guild guild) {
        new Member(this, guild);
    }
    
    /**
     * Get requests.
     * @return List of {@link User}s
     */
    public List<User> getRequests() {
        return requests;
    }
    
    /**
     * Adds a request.
     * @param user User
     */
    private void addRequest(User user) {
        requests.add(user);
    }
    
    /**
     * Removes a request.
     * @param user User
     */
    public void removeRequest(User user) {
        requests.remove(user);
    }
    
    /**
     * Sends a request to a user.
     * @param friend User
     */
    public void sendRequest(User friend) {
        friend.addRequest(this);
    }
    
    /**
     * Get friends.
     * @return List of {@link Friend}s
     */
    public List<Friend> getFriends() {
        return friends;
    }
    
    /**
     * Get friends as users.
     * @return List of {@link User}s
     */
    public List<User> getFriendsAsUsers() {
        List<User> users = new ArrayList<>();
        for (Friend friend : friends) {
            users.add(friend.getGroup().getTheOtherOne(this));
        }
        return users;
    }
    
    /**
     * Adds a friend.
     * @param friend Friend
     */
    public void addFriend(Friend friend) {
        friends.add(friend);
    }
    
    /**
     * Joins a group.
     * @param group Group
     */
    public void joinGroup(Group group) {
        new Friend(this, group);
    }
    
    public String getAvatarColor() {
        String[] colors = {"#2196F3", "#32c787", "#00BCD4", "#ff5652", "#ffc107", "#ff85af", "#FF9800", "#39bbb0"};
        
        int hash = 0;
        for (int i = 0; i < username.length(); i++) {
            hash = 31 * hash + username.charAt(i);
        }
        int index = Math.abs(hash % colors.length);
        return colors[index];
    }
}
