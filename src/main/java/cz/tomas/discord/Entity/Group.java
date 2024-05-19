package cz.tomas.discord.Entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "group_discord")
public class Group {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Friend> friends = new ArrayList<>();
    
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DirectMessage> messages = new ArrayList<>();
    
    
    public Group () {}
    
    
    public long getId() {
        return id;
    }
    
    public List<Friend> getFriends() {
        return friends;
    }
    
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
    
    public void addFriend(Friend friend) {
        friends.add(friend);
    }
    
    public List<DirectMessage> getMessages() {
        return messages;
    }
    
    public void addMessage(DirectMessage message) {
        messages.add(message);
    }
}
