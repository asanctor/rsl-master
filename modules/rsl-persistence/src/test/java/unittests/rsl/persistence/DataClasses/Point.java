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
public class Point {

    @Id
    @GeneratedValue
    long id;

    // Persistent Fields:
    private int x;
    private int y;

    @ManyToMany(cascade={PERSIST, REFRESH, MERGE})
    private Set<PointCollection> pointCollections = new HashSet<>();

    // Constructor:
    public Point (int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Accessor Methods:
    public int getX() { return this.x; }
    public int getY() { return this.y; }

    // Accessor Methods:
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }

    // String Representation:
    @Override
    public String toString() {
        return String.format("(%d, %d)", this.x, this.y);
    }


    public Set<PointCollection> getPointCollections() {
        return pointCollections;
    }

    public void addPointCollection(PointCollection pc)
    {
        pointCollections.add(pc);
    }

    public void setPointCollections(Set<PointCollection> pointCollections) {
        this.pointCollections = pointCollections;
    }

}
