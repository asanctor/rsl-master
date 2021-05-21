package rsl.core.coremodel;

import rsl.core.RSL;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REFRESH;

/**
 * @author asanctor
 *
 * The UserEntitySharing class is used to store the sharing relationship between Users and Entities.
 * For example User X shares Entity A with User Y
 * The JPA annotations (e.g. @Entity) tell the persistance layer that elements of this class are to be stored.
 *
 */
@Entity
public class UserEntitySharing {

    // every object in the database has a unique ID
    @Id // tell JPA "id" is the field that contains the object's unique id
    @GeneratedValue // tell JPA to automatically generate the id for us
    private long dbID;

    // list of sharing users that share one or more entities to other users
    @ManyToMany(fetch= FetchType.LAZY, cascade={PERSIST, REFRESH, MERGE})
    private Set<RslUser> sharingUsers = new HashSet<>();

    // list of entities that the sharing users are sharing
    @ManyToMany(fetch=FetchType.LAZY, cascade={PERSIST, REFRESH, MERGE})
    private Set<RslEntity> sharedEntities = new HashSet<>();

    // list of users that received these entities
    @ManyToMany(fetch=FetchType.LAZY, cascade={PERSIST, REFRESH, MERGE})
    private Set<RslUser> receivingUsers = new HashSet<>();

    public UserEntitySharing(){
        this(false);
    }

    public UserEntitySharing(Boolean save){
        if(save){
            this.save();
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends UserEntitySharing> T save()
    {
        RSL.getDB().save(this);
        return (T)this;
    }

    public void refresh()
    {
        RSL.getDB().refresh(this);
    }

    public long getDatabaseID() {
        return dbID;
    }

    public Set<RslUser> getSharingUsers() {
        return sharingUsers;
    }

    public Set<RslEntity> getSharedEntities() {
        return sharedEntities;
    }

    public Set<RslUser> getReceivingUsers() {
        return receivingUsers;
    }

    public Set<RslEntity> getSharedEntitiesOfType(Class<?> type) {
        return sharedEntities.stream().filter(l -> l.getClass() == type).collect(Collectors.toSet());
    }

    void addSharingUsers(RslUser user){
        sharingUsers.add(user);
    }

    void removeSharingUsers(RslUser user) {
        sharingUsers.remove(user);
    }

    void addSharedEntities(RslEntity entity){
        sharedEntities.add(entity);
    }

    void removeSharedEntities(RslEntity entity) {
        sharedEntities.remove(entity);
    }

    void addReceivingUsers(RslUser user){
        receivingUsers.add(user);
    }

    void removeReceivingUsers(RslUser user) {
        receivingUsers.remove(user);
    }

}
