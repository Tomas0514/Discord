package cz.tomas.discord.Entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

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


    public Member() {}

    public Member(User user, Guild guild) {
        this.user = user;
        this.guild = guild;
        joinedAt = LocalDateTime.now();
        guild.addMember(this);
    }

    public User getUser() {
        return user;
    }

    public Guild getGuild() {
        return guild;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }
}
