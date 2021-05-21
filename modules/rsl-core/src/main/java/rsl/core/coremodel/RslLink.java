package rsl.core.coremodel;

import rsl.core.RSL;
import rsl.util.Log;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static javax.persistence.CascadeType.*;

/**
 * @author rroels
 *
 * The RslLink respresents the RSL Link type and implements the link-related functionality. Link classes generated
 * from a schema will inherit from this class directly or indirectly.
 */
@Entity
public class RslLink extends RslEntity {

    @ManyToMany(fetch=FetchType.LAZY, cascade={PERSIST, REFRESH, MERGE})
    private Set<RslEntity> sources = new HashSet<>();

    @ManyToMany(fetch=FetchType.LAZY, cascade={PERSIST, REFRESH, MERGE})
    private Set<RslEntity> targets = new HashSet<>();

    public RslLink(){

    }

    public RslLink(Boolean save) {
        super(save);
    }

    protected RslLink(RslEntity source, RslEntity target)
    {
        RSL.getDB().startTransaction();
        addSource(source);
        addTarget(target);
        RSL.getDB().endTransaction();
    }

    protected RslLink(List<RslEntity> sources, RslEntity target)
    {
        RSL.getDB().startTransaction();
        addSources(sources);
        addTarget(target);
        RSL.getDB().endTransaction();
    }

    protected RslLink(RslEntity source, List<RslEntity> targets)
    {
        RSL.getDB().startTransaction();
        addSource(source);
        addTargets(targets);
        RSL.getDB().endTransaction();
    }

    protected RslLink(List<RslEntity> sources, List<RslEntity> targets)
    {
        RSL.getDB().startTransaction();
        addSources(sources);
        addTargets(targets);
        RSL.getDB().endTransaction();
    }

    public Set<RslEntity> getSources()
    {
        return sources;
    }

    public Set<RslEntity> getSourcesOfType(Class<?> type)
    {
        return sources.stream().filter(s -> s.getClass() == type).collect(Collectors.toSet());
    }

    public Set<RslEntity> getTargets()
    {
        return targets;
    }

    public Set<RslEntity> getTargetsOfType(Class<?> type)
    {
        return targets.stream().filter(s -> s.getClass() == type).collect(Collectors.toSet());
    }



    public boolean addSource(RslEntity source)
    {
        RSL.getDB().startTransaction();
        boolean added = this.sources.add(source);
        source.addOutgoingLink(this);
        RSL.getDB().endTransaction();
        return added;
    }

    public boolean removeSource(RslEntity source)
    {
        RSL.getDB().startTransaction();
        boolean removed = this.sources.remove(source);
        source.removeOutgoingLink(this);
        RSL.getDB().endTransaction();
        return removed;
    }

    public void addSources(List<RslEntity> sources)
    {
        RSL.getDB().startTransaction();
        for(RslEntity ent: sources)
        {
            addSource(ent);
        }
        RSL.getDB().endTransaction();
    }

    public void removeSources(List<RslEntity> sources)
    {
        RSL.getDB().startTransaction();
        for(RslEntity ent: sources)
        {
            removeSource(ent);
        }
        RSL.getDB().endTransaction();
    }

    public boolean addTarget(RslEntity target)
    {
        RSL.getDB().startTransaction();
        boolean added = this.targets.add(target);
        target.addIncomingLink(this);
        RSL.getDB().endTransaction();
        return added;
    }

    public boolean addTargets(List<RslEntity> targets)
    {
        boolean result = true;
        RSL.getDB().startTransaction();
        for(RslEntity ent: targets)
        {
            result = result && addTarget(ent);
        }
        RSL.getDB().endTransaction();
        return result;
    }

    public boolean removeTarget(RslEntity target)
    {
        RSL.getDB().startTransaction();
        boolean removed = this.targets.remove(target);
        target.removeIncomingLink(this);
        RSL.getDB().endTransaction();
        return removed;
    }

    public boolean removeTargets(List<RslEntity> targets)
    {
        boolean result = true;
        RSL.getDB().startTransaction();
        for(RslEntity ent: targets)
        {
            result = result && removeTarget(ent);
        }
        RSL.getDB().endTransaction();
        return result;
    }




}
