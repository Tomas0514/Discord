package cz.tomas.discord.Entity;

import jakarta.persistence.*;

@Entity
public class Friend {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    @ManyToOne
    private User user;
    
    @ManyToOne
    private Group group;
    
    
    private Friend() {}
    
    public Friend(User user, Group group) {
        this.user = user;
        this.group = group;
        user.addFriend(this);
        group.addFriend(this);
    }
    
    
    public long getId() {
        return id;
    }
    
    public User getUser() {
        return user;
    }
    
    public Group getGroup() {
        return group;
    }
}
