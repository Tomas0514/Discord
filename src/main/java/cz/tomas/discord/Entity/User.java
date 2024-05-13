package cz.tomas.discord.Entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

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
    
    @ManyToMany()
    private List<User> friends = new ArrayList<>();
    
    @ManyToMany()
    private List<User> requests = new ArrayList<>();

    private User() {}

    public User(String username) {
        this.username = username;
    }


    // Getters and functions

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public List<Member> getMembers() {
        return members;
    }
    
    public List<Guild> getGuilds() {
        List<Guild> guilds = new ArrayList<>();
        members.forEach(member -> guilds.add(member.getGuild()));
        return guilds;
    }

    public void joinGuild(Guild guild) {
        new Member(this, guild);
    }
    
    public List<User> getRequests() {
        return requests;
    }
    
    private void addFriendRequest(User user) {
        requests.add(user);
    }
    
    public void removeFriendRequest(User user) {
        requests.remove(user);
    }
    
    public void sendFriendRequest(User friend) {
        friend.addFriendRequest(this);
    }
    
    public List<User> getFriends() {
        return friends;
    }
    
    private void addFriend(User friend) {
        friends.add(friend);
    }
    
    public void createFriendship(User friend) {
        friends.add(friend);
        friend.addFriend(this);
    }
}
