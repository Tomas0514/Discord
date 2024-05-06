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

    public void joinGuild(Guild guild) {
        new Member(this, guild);
    }
}
