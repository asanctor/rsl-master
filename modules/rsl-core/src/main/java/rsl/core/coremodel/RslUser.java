package rsl.core.coremodel;

import rsl.core.RSL;
import rsl.util.Random;

import javax.jdo.annotations.Unique;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REFRESH;

/**
 * @author asanctor
 *
 * The RslUser respresents the RSL User type and acts as a superclass for groups. It holds user
 * information including its access rights to entities and shared entities. The JPA annotations (e.g. @Entity) tell
 * the persistance layer that users (and derived classes) are to be stored.
 *
 * Methods {@code equals} and {@code hashCode} are implemented so that RslUsers are compareable. This allows RslUsers
 * to be sorted/grouped/compared using standard Java sorting/grouping methods. It can also be required by some JPA
 * providers for internal management of stored objects.
 *
 * Note: mostly the same class as RslEntity
 */
@Entity
public class RslUser {

    // every object in the database has a unique ID
    @Id // tell JPA "id" is the field that contains the object's unique id
    @GeneratedValue // tell JPA to automatically generate the id for us
    private long dbID;

    @Unique
    private long id;

    @Column(unique = true)
    private String username = "";

    private String password = "";
    private String firstName = "";
    private String lastName = "";
    private String email = "";

    //next five properties have been added for allowing security
    private String role = ""; //ADMIN or USER for example

    private boolean accountVerified; //to check whether the user is active, not active

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade={PERSIST, REFRESH, MERGE})
    private Set<SecureToken> tokens = new HashSet<>();             //for email verification tokens or other

    private int failedLoginAttempts;                               //to block login attempas after a certain number of trials
    private boolean loginDisabled;

    @OneToMany(fetch=FetchType.LAZY, mappedBy="creator", cascade={PERSIST, REFRESH, MERGE})
    private Set<RslEntity> createdEntities = new HashSet<>();

    //EntityEditorTimestamp: association between user and entity, where user is editor of entity,
    // made to hold a timestamp of this association
    @OneToMany(fetch=FetchType.LAZY, mappedBy="editor", cascade={PERSIST, REFRESH, MERGE})
    private Set<EntityEditorTimestamp> editedEntities = new HashSet<EntityEditorTimestamp>();

    // list of entities to which the user has access to
    @ManyToMany(fetch=FetchType.LAZY, cascade={PERSIST, REFRESH, MERGE})
    private Set<RslEntity> accessibleEntities = new HashSet<>();

    // list of entities to which the user has no access to
    @ManyToMany(fetch=FetchType.LAZY, cascade={PERSIST, REFRESH, MERGE})
    private Set<RslEntity> inaccessibleEntities = new HashSet<>();

    // list of pairs containing the user's preferences => cannot be done using properties prefixed with "userpref:" since user not an entity
    @ManyToMany(fetch=FetchType.LAZY, cascade={PERSIST, REFRESH, MERGE})
    private  Set<Preference> preferences = new HashSet<>();

    // groups to which user or group belongs to
    @ManyToMany(fetch=FetchType.LAZY, cascade={PERSIST, REFRESH, MERGE})
    private Set<RslGroup> groups = new HashSet<>();

    //TODO: add share relationship, not sure this is correct
    // list of all entities that this person is sharing, also contains the user to which it is shared
    @ManyToMany(fetch=FetchType.LAZY, mappedBy="sharingUsers", cascade={PERSIST, REFRESH, MERGE})
    private Set<UserEntitySharing> sharedEntities = new HashSet<>();

    // list of all entities that this person received together with the user that shared the entity
    @ManyToMany(fetch=FetchType.LAZY, mappedBy="receivingUsers", cascade={PERSIST, REFRESH, MERGE})
    private Set<UserEntitySharing> receivedEntities = new HashSet<>();

    public RslUser()
    {
        this(false);
    }

    public RslUser(Boolean save)
    {
        this.id = Random.randomInt(0, Integer.MAX_VALUE - 1);
        if(save)
        {
            this.save();
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends RslUser> T save()
    {
        RSL.getDB().save(this);
        return (T)this;
    }

    public void refresh()
    {
        RSL.getDB().refresh(this);
    }


    // ---------------SETTERS AND GETTERS---------------------

    public String getUsername() {
        return username;
    }

    public void setUsername(String name) {
        RSL.getDB().startTransaction();
        this.username = name;
        RSL.getDB().endTransaction();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isAccountVerified() {
        return accountVerified;
    }

    public void setAccountVerified(boolean enabled) {
        this.accountVerified = enabled;
    }

    public Set getTokens() {
        return tokens;
    }

    public void setTokens(Set tokens) {
        this.tokens = tokens;
    }

    public int getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public void setFailedLoginAttempts(int failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }

    public boolean isLoginDisabled() {
        return loginDisabled;
    }

    public void setLoginDisabled(boolean loginDisabled) {
        this.loginDisabled = loginDisabled;
    }
// ------------------------------------

    public long getDatabaseID() {
        return dbID;
    }

    public long getID() {
        return id;
    }


    public Set<RslEntity> getAccessibleEntities() {
        return accessibleEntities;
    }

    public Set<RslEntity> getAccessibleEntitiesOfType(Class<?> type) {
        return accessibleEntities.stream().filter(l -> l.getClass() == type).collect(Collectors.toSet());
    }

    public Set<RslEntity> getInaccessibleEntities() {
        return inaccessibleEntities;
    }

    public Set<RslEntity> getInaccessibleEntitiesOfType(Class<?> type) {
        return inaccessibleEntities.stream().filter(l -> l.getClass() == type).collect(Collectors.toSet());
    }


    public boolean addAccessibleEntity(RslEntity entity){
        RSL.getDB().startTransaction();
        boolean added = this.accessibleEntities.add(entity);
        System.out.println("writing to db");
        entity.addAccessiblebyUser(this);
        RSL.getDB().endTransaction();
        return added;
    }

    public boolean addInaccessibleEntity(RslEntity entity){
        RSL.getDB().startTransaction();
        boolean added = this.inaccessibleEntities.add(entity);
        entity.addInaccessiblebyUser(this);
        RSL.getDB().endTransaction();
        return added;
    }

    public void removeAccessibleEntity(RslEntity entity) {
        accessibleEntities.remove(entity);
        this.refresh();
    }

    public void removeInaccessibleEntiy(RslEntity entity) {
        inaccessibleEntities.remove(entity);
    }


    public Set<Preference> getPreferences() { return preferences; }

    public void addPreferencePair(Preference<String,String> preference){ preferences.add(preference); this.refresh(); }

    public void removePreferencePair(Preference<String,String> preference) { preferences.remove(preference); this.refresh();}

    public String getTypeName() {
        return this.getClass().getSimpleName();
    }

    public Set<RslEntity> getCreatedEntities() {
        return createdEntities;
    }

    public void addCreatedEntity(RslEntity entity) {
        entity.setCreator(this);
        this.refresh();
    }

    public void setCreatedEntities(Set<RslEntity> entities) {
        this.createdEntities = createdEntities;
    }


    public Set<EntityEditorTimestamp> getEditedEntities() { return editedEntities; }

    public void addEditedEntity(EntityEditorTimestamp editedEntities) {
        editedEntities.setEditor(this);
        this.refresh();
    }

    public void setEditedEntities(Set<EntityEditorTimestamp> editedEntities) { this.editedEntities = editedEntities; }

    public Set<RslGroup> getGroups() { return groups; }

    public void addGroup(RslGroup group){
        groups.add(group);
    }

    public void removeGroup(RslGroup group) {
        groups.remove(group);
    }

    //TODO: add getters and setters for sharing relationship, also add this relation in entity?

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RslUser)) return false;
        RslUser person = (RslUser) o;
        return id == person.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


}
