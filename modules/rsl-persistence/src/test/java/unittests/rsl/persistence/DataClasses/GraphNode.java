package unittests.rsl.persistence.DataClasses;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.HashSet;
import java.util.Set;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REFRESH;

@Entity
public class GraphNode {

    @Id
    @GeneratedValue
    long id;

    private int value = 0;


    @ManyToMany(cascade={PERSIST, REFRESH, MERGE})
    private Set<GraphNode> outgoingRelations = new HashSet<>();

    @ManyToMany(cascade={PERSIST, REFRESH, MERGE})
    private Set<GraphNode> incomingRelations = new HashSet<>();

    public void addOutgoingRelation(GraphNode n)
    {
        this.outgoingRelations.add(n);
        n.addIncomingRelation(this);
    }

    public void addIncomingRelation(GraphNode n)
    {
        incomingRelations.add(n);
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Set<GraphNode> getOutgoingRelations() {
        return outgoingRelations;
    }

    public void setOutgoingRelations(Set<GraphNode> outgoingRelations) {
        this.outgoingRelations = outgoingRelations;
    }

    public Set<GraphNode> getIncomingRelations() {
        return incomingRelations;
    }

    public void setIncomingRelations(Set<GraphNode> incomingRelations) {
        this.incomingRelations = incomingRelations;
    }
}
