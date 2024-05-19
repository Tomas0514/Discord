package cz.tomas.discord.Service;

import cz.tomas.discord.Entity.Group;
import cz.tomas.discord.Repository.GroupRepository;
import org.springframework.stereotype.Service;

@Service
public class GroupService {
    
    protected final GroupRepository groupRepository;
    
    public GroupService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }
    
    /**
     * Finds a group by id or throws a {@link GroupNotFoundException}.
     * @param id Group's id
     * @return {@link Group}
     */
    public Group getGroup(long id) {
        return groupRepository.findById(id).orElseThrow(() -> new GroupNotFoundException(id));
    }
}
