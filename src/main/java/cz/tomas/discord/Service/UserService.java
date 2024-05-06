package cz.tomas.discord.Service;

import cz.tomas.discord.Entity.Guild;
import cz.tomas.discord.Entity.User;
import cz.tomas.discord.Repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    
    protected final UserRepository userRepository;
    protected final UserDetailsManager userDetailsManager;
    protected final PasswordEncoder passwordEncoder;
    
    public UserService(UserRepository userRepository, UserDetailsManager userDetailsManager, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userDetailsManager = userDetailsManager;
        this.passwordEncoder = passwordEncoder;
    }
    
    /**
     * Create a user or throws an {@link UserAlreadyExistsException}
     * @param username Username
     * @param password Password
     * @param roles An array of strings
     * @return {@link User}
     */
    public User createUser(String username, String password, List<String> roles) {
        if (userDetailsManager.userExists(username) || userRepository.findByUsername(username).isPresent()) {
            throw new UserAlreadyExistsException(username);
        }
        userDetailsManager.createUser(org.springframework.security.core.userdetails.User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .roles(roles.toArray(new String[0])).build());
        final User user = new User(username);
        userRepository.save(user);
        return user;
    }
    
    /**
     * Deletes and creates a user
     * @param username Username
     * @param password password
     * @param roles An array of strings
     * @return {@link User}
     */
    public User updateUser(String username, String password, List<String> roles) {
        deleteUser(username);
        return createUser(username, password, roles);
    }
    
    /**
     * Deletes a user
     * @param username Username
     */
    public void deleteUser(String username) {
        userDetailsManager.deleteUser(username);
        userRepository.findByUsername(username).ifPresent(userToDelete -> userRepository.deleteById(userToDelete.getId()));
    }
    
    /**
     * Finds a user by username or throws an {@link UserNotFoundException}
     * @param username User's username
     * @return {@link User}
     */
    public User getUser(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
    }
    
    /**
     * Finds a user by id or throws an {@link UserNotFoundException}
     * @param id User's id
     * @return {@link User}
     */
    public User getUser(long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }
    
    /**
     * Returns all {@link Guild}s of a {@link User}
     * @param user User
     * @return List of {@link Guild}s
     */
    public List<Guild> getUsersGuilds(User user) {
        List<Guild> guilds = new ArrayList<>();
        user.getMembers().forEach(member -> guilds.add(member.getGuild()));
        return guilds;
    }
    
    /**
     * Returns all {@link Guild}s of a {@link User}
     * @param id User's id
     * @return List of {@link Guild}s
     */
    public List<Guild> getUsersGuilds(long id) {
        final User user = getUser(id);
        List<Guild> guilds = new ArrayList<>();
        user.getMembers().forEach(member -> guilds.add(member.getGuild()));
        return guilds;
    }
}
