package unittests.rsl.persistence;

import org.junit.*;
import rsl.persistence.PersistenceFactory;
import rsl.persistence.RslPersistence;
import rsl.util.Random;
import rsl.util.TempDir;
import unittests.rsl.persistence.DataClasses.GraphNode;
import unittests.rsl.persistence.DataClasses.Point;
import unittests.rsl.persistence.DataClasses.PointCollection;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class CascadeTests {

    private static String dbPath;
    private static RslPersistence db;

    @BeforeClass
    public static void setUp()
    {
        dbPath = TempDir.makeTempFile(Random.randomString(12, true), ".objectdb");
        db = PersistenceFactory.getObjectDBPersistance(dbPath);
    }

    @Before
    public void clearDatabase()
    {
        db.emptyDatabase();
    }

    @AfterClass
    public static void tearDown()
    {
        db.close();
        try {
            Files.delete(Paths.get(dbPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void CascadingRelationshipSaveFromRoot()
    {

        PointCollection col = new PointCollection();
        db.save(col);

        Point p1 = new Point(0,0);
        db.startTransaction();
        col.addPoint(p1);
        db.endTransaction();

        db.startTransaction();
        Point p2 = new Point(1,1);
        col.addPoint(p2);
        db.endTransaction();

        db.startTransaction();
        Point p3 = new Point(2,2);
        Point p4 = new Point(3,3);
        col.addPoint(p3);
        col.addPoint(p4);
        db.endTransaction();

        Point p5 = new Point(4,4);
        Point p6 = new Point(5,5);
        db.startTransaction();
        col.addPoint(p5);
        col.addPoint(p6);
        db.endTransaction();

        long count = db.countAll();

        // 6 points and 1 collection object
        Assert.assertEquals(7, count);

    }

    @Test
    public void CascadingRelationshipSaveFromLeaf()
    {

        PointCollection col = new PointCollection();

        Point p1 = new Point(0,0);
        db.save(p1);

        db.startTransaction();
        col.addPoint(p1);
        db.endTransaction();

        long count = db.countAll();

        // 1 points and 1 collection object
        Assert.assertEquals(2, count);

    }


    @Test
    public void NodeChain1()
    {

        GraphNode n1 = new GraphNode();
        db.save(n1);

        GraphNode n2 = new GraphNode();
        GraphNode n3 = new GraphNode();
        n2.addOutgoingRelation(n3);

        db.startTransaction();
        n1.addOutgoingRelation(n2);
        db.endTransaction();

        long count = db.countAll();

        Assert.assertEquals(3, count);

    }

    @Test
    public void NodeChain2()
    {

        GraphNode n1 = new GraphNode();

        GraphNode n2 = new GraphNode();
        GraphNode n3 = new GraphNode();
        n2.addOutgoingRelation(n3);
        db.save(n2);

        db.startTransaction();
        n1.addOutgoingRelation(n2);
        db.endTransaction();

        long count = db.countAll();

        Assert.assertEquals(3, count);

    }





}
