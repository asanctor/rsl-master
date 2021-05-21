package rsl.core.coremodel;

import com.google.common.base.Objects;
import rsl.core.RSL;


import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REFRESH;


/**
 * @author asanctor
 *
 * The Preference class is basically the java Pair class (had to be made in order to be able to store preferences in db)
 * The JPA annotations (e.g. @Entity) tell the persistance layer that Preferences are to be stored.
 *
 * Methods {@code equals} and {@code hashCode} are implemented so that Preferences are compareable. This allows them
 * to be sorted/grouped/compared using standard Java sorting/grouping methods. It can also be required by some JPA
 * providers for internal management of stored objects.
 *
 */
@Entity
public class Preference<K, V> {

    // every object in the database has a unique ID
    @Id // tell JPA "id" is the field that contains the object's unique id
    @GeneratedValue // tell JPA to automatically generate the id for us
    private long dbID;

    private final K key;
    private final V value;

    // list of users that use that have this preference
    @ManyToMany(fetch=FetchType.LAZY, cascade={PERSIST, REFRESH, MERGE})
    private Set<RslUser> users = new HashSet<>();

    public Preference(K key, V value) {
        this.key = key;
        this.value = value;
    }

    //not sure this is the way to do this
    public Preference(K key, V value, Boolean save) {
        this.key = key;
        this.value = value;
        if(save) { this.save(); }
    }

    @SuppressWarnings("unchecked")
    public <T extends Preference> T save()
    {
        RSL.getDB().save(this);
        return (T)this;
    }

    public void refresh()
    {
        RSL.getDB().refresh(this);
    }


    public K getKey() {
       return key;
    }

    public V getValue() {
        return value;
    }

    public long getDatabaseID() {
        return dbID;
    }

    public Set<RslUser> getUsers() {
        return users;
    }

    public void setUsers(Set<RslUser> users) {
        this.users = users;
        this.refresh();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Preference)) {
            return false;
        }
        Preference<?, ?> p = (Preference<?, ?>) o;
        return Objects.equal(p.key, key) && Objects.equal(p.value, value);
    }

    @Override
    public int hashCode() {
        return (key == null ? 0 : key.hashCode()) ^ (value == null ? 0 : value.hashCode());
    }

    public String toString() {
        return "["+getKey()+","+getValue()+"]";
    }

}
