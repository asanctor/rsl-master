package rsl.core.coremodel;


import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REFRESH;

/**
 * @author asanctor
 *
 * The RslGroup respresents the RSL Group type, it groups RSL users and RSL groups together.
 * The JPA annotations (e.g. @Entity) tell the persistance layer that groups (and derived classes) are to be stored.
 *
 * Methods {@code equals} and {@code hashCode} are implemented in RslUser so that RslGroups are compareable. This allows
 * RslGroups to be sorted/grouped/compared using standard Java sorting/grouping methods. It can also be required by some
 * JPA providers for internal management of stored objects.
 *
 */
@Entity
public class RslGroup extends RslUser {

    @Column(unique = true, nullable = false)
    private String code;
    private String name;

    // list of users or groups belonging to this group
    @ManyToMany(fetch= FetchType.LAZY, cascade={PERSIST, REFRESH, MERGE})
    private Set<RslUser> members = new HashSet<>();

    public RslGroup(){ }

    public RslGroup(Boolean save)
    {
        super(save);
    }

    // GETTERS AND SETTERS
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<RslUser> getMembers() { return members; }

    void addMember(RslUser member){
        members.add(member);
    }

    public void setMembers(Set<RslUser> members) {
        this.members = members;
    }


}
