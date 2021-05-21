package rsl.core.coremodel;


import org.apache.commons.lang3.reflect.MethodUtils;
import rsl.core.RSL;
import rsl.util.Log;
import rsl.util.Random;

import javax.jdo.annotations.Unique;

import javax.persistence.*;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static javax.persistence.CascadeType.*;

/**
 * @author rroels
 * edited by:
 * @author asanctor
 *
 * The RslEntity represents the RSL Entity type and acts as a superclass for RSL resources, links and selectors. The
 * JPA annotations (e.g. @Entity) tell the persistance layer that entities (and derived classes) are to be stored.
 *
 * Methods {@code equals} and {@code hashCode} are implemented so that RslEntity are compareable. This allows RslEntity
 * to be sorte/grouped/compared using standard Java sorting/grouping methods. It can also be required by some JPA
 * providers for internal management of stored objects.
 *
 * NOTE: CHANGED ID TO INTEGER TO AVOID PROBLEMS WITH LONG IDS IN JAVASCRIPT
 */
@Entity
public class RslEntity {

    // every object in the database has a unique ID
    @Id // tell JPA "id" is the field that contains the object's unique id
    @GeneratedValue // tell JPA to automatically generate the id for us
    private long dbID;

    @Unique
    private int id;

    private String name = "";

    // user that has created the entity
    @ManyToOne
    private RslUser creator = null;

    // Unix Timestamp value (milliseconds since 1970) that contains the moment of creation
    private long creationTimestamp = 0;

    // users that have changed the entity over time
    @OneToMany(fetch=FetchType.LAZY, mappedBy="entity", cascade={PERSIST, REFRESH, MERGE})
    private Set<EntityEditorTimestamp> editors = new HashSet<EntityEditorTimestamp>();

    // list of links that use this entity as a source
    @ManyToMany(fetch=FetchType.LAZY, cascade={PERSIST, REFRESH, MERGE})
    private Set<RslLink> outgoingLinks = new HashSet<>();

    // list of links that use this entity as a target
    @ManyToMany(fetch=FetchType.LAZY, cascade={PERSIST, REFRESH, MERGE})
    private Set<RslLink> incomingLinks = new HashSet<>();

    // list of users that shared this entity
    // not sure this is useful...
    @ManyToMany(fetch=FetchType.LAZY, cascade={PERSIST, REFRESH, MERGE})
    private Set<UserEntitySharing> sharedByTo = new HashSet<>();

    // list of users that don't have access to this entity
    @ManyToMany(fetch=FetchType.LAZY, cascade={PERSIST, REFRESH, MERGE})
    private Set<RslUser> inaccessibleByUsers = new HashSet<>();

    // list of users that have access to this entity
    @ManyToMany(fetch=FetchType.LAZY, cascade={PERSIST, REFRESH, MERGE})
    private Set<RslUser> accessibleByUsers = new HashSet<>();

    public RslEntity()
    {
        this(false);
    }

    public RslEntity(Boolean save)
    {
        this.id = Random.randomInt(0, Integer.MAX_VALUE - 1);
        if(save)
        {
            this.save();
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends RslEntity> T save()
    {
        RSL.getDB().save(this);
        return (T)this;
    }

    public void refresh()
    {
        RSL.getDB().refresh(this);
    }


    // ------------------------------------

    public String getName() {
        return name;
    }

    public void setName(String name) {
        RSL.getDB().startTransaction();
        this.name = name;
        RSL.getDB().endTransaction();
    }

    public long getDatabaseID() {
        return dbID;
    }

    public long getID() {
        return id;
    }


    public RslUser getCreator() {
        return creator;
    }

    public void setCreator(RslUser creator) {
        RSL.getDB().startTransaction();
        this.creator = creator;
        RSL.getDB().endTransaction();
        creator.refresh();
    }

    public long getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(long creationTimestamp) {
        RSL.getDB().startTransaction();
        this.creationTimestamp = creationTimestamp;
        RSL.getDB().endTransaction();
    }


    public Set<EntityEditorTimestamp> getEditors() { return editors; }

    public void addEditors(EntityEditorTimestamp editors){
        editors.setEntity(this);
        this.refresh();
    }

    public Set<RslUser> getInaccessiblebyUsers() {
        return inaccessibleByUsers;
    }

    public void setInaccessiblebyUsers(Set<RslUser> users) {
        this.inaccessibleByUsers = users;
        this.refresh();
    }
    public void addInaccessiblebyUser(RslUser user) {
        this.inaccessibleByUsers.add(user);
        this.refresh();
    }

    public Set<RslUser> getAccessiblebyUsers() {
        return accessibleByUsers;
    }

    public void setAccessiblebyUsers(Set<RslUser> users) {
        this.accessibleByUsers = users;
        this.refresh();
    }

    public void addAccessiblebyUser(RslUser user) {
        this.accessibleByUsers.add(user);
        this.refresh();
    }

    public void setEditors(Set<EntityEditorTimestamp> editors) { this.editors = editors; }


    public Set<RslLink> getOutgoingLinks() {
        return outgoingLinks;
    }
    public Set<RslLink> getOutgoingLinksOfType(Class<?> type) {
        return outgoingLinks.stream().filter(l -> l.getClass() == type).collect(Collectors.toSet());
    }
    void addOutgoingLink(RslLink link){
        outgoingLinks.add(link);
    }
    void removeOutgoingLink(RslLink link) { outgoingLinks.remove(link); }


    public Set<RslLink> getIncomingLinks() {
        return incomingLinks;
    }
    public Set<RslLink> getIncomingLinksOfType(Class<?> type) {
        return incomingLinks.stream().filter(l -> l.getClass() == type).collect(Collectors.toSet());
    }
    void addIncomingLink(RslLink link){ incomingLinks.add(link); }
    void removeIncomingLink(RslLink link) {
        incomingLinks.remove(link);
    }


    public String getTypeName()
    {
        return this.getClass().getSimpleName();
    }


    public Set<UserEntitySharing> getSharedByTo() { return sharedByTo; }
    void addSharedByTo(UserEntitySharing item){
        sharedByTo.add(item);
    }
    void removeSharedByTo(UserEntitySharing item) {
        sharedByTo.remove(item);
    }

    /**
     * If RslEntity's have the same ID they are considered equal.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RslEntity)) return false;

        RslEntity that = (RslEntity) o;

        return id == that.id;
    }

    /**
     * Generated by IntelliJ, don't know why it chooses the number 32 to be part of the hash, but seems to be a
     * standard approach to use these magic numbers in the hash generator.
     */
    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    public void setField(String fieldName, Object... arguments)
    {
        if(fieldName == null || fieldName.equals("")){return;}
        String methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        invokeMethod(methodName, arguments);
    }

    public <T> T getField(String fieldName)
    {
        if(fieldName == null || fieldName.equals("")){return null;}
        String methodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        return invokeMethod(methodName, new Object[0]);
    }

    @SuppressWarnings("unchecked")
    public <T> T invokeMethod(String name, Object... arguments)
    {
        Class<?> argClasses[] = Arrays.stream(arguments).map(Object::getClass).toArray(size -> new Class<?>[ size ] );
        Method method = null;
        T result = null;
        RSL.getDB().startTransaction();
        try {
            method = MethodUtils.getMatchingAccessibleMethod(this.getClass(), name, argClasses);
            result = (T) method.invoke(this, arguments);
        } catch (Exception e ) {
            String[] classTypeNames = Arrays.stream(argClasses).map(Class::getSimpleName).toArray(String[]::new);
            Log.warning("Method not found: " + name + "(" + String.join(", ", classTypeNames) + ")");
            result = null;
        } finally {
            RSL.getDB().endTransaction();
        }
        return result;
    }

}