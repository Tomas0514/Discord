package cz.tomas.discord.Security;

import cz.tomas.discord.Entity.*;
import cz.tomas.discord.Service.GroupService;
import cz.tomas.discord.Service.GuildService;
import cz.tomas.discord.Service.UserService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {
    
    private final UserService userService;
    private final GuildService guildService;
    private final GroupService groupService;
    
    public SecurityService(UserService userService, GuildService guildService, GroupService groupService) {
        this.userService = userService;
        this.guildService = guildService;
        this.groupService = groupService;
    }
    
    public boolean isDiscord() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(new SimpleGrantedAuthority("ROLE_DISCORD"));
    }
    
    public boolean isInGuild(long guildId) {
        final User user = userService.getClientUser();
        final Guild guild = guildService.getGuild(guildId);
        
        return guild.getMembers().stream()
                .map(Member::getUser)
                .anyMatch(u -> u == user);
    }
    
    public boolean isInGroup(long groupId) {
        final User user = userService.getClientUser();
        final Group group = groupService.getGroup(groupId);
    
        return group.getFriends().stream()
                .map(Friend::getUser)
                .anyMatch(u -> u == user);
    }
}
