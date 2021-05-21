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
public class PointCollection {

    @Id
    @GeneratedValue
    long id;

    // Persistent Fields:
    private String name;

    @ManyToMany(cascade={PERSIST, REFRESH, MERGE})
    private Set<Point> points = new HashSet<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addPoint(Point p)
    {
        points.add(p);
        p.addPointCollection(this);
    }

    public Set<Point> getPoints() {
        return points;
    }

    public void setPoints(Set<Point> points) {
        this.points = points;
    }
}
