package unittests.rsl.persistence;

import org.junit.*;
import rsl.persistence.PersistenceFactory;
import rsl.persistence.PersistenceQuery;
import rsl.persistence.RslPersistence;
import rsl.util.Log;
import rsl.util.Random;
import rsl.util.TempDir;
import unittests.rsl.persistence.DataClasses.Point;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class PersistenceQueryTests {

    private static String dbPath;
    private static RslPersistence db;

    @BeforeClass
    public static void setUp() {
        dbPath = TempDir.makeTempFile(Random.randomString(12, true), ".objectdb");
        db = PersistenceFactory.getObjectDBPersistance(dbPath);
    }

    @Before
    public void emptyDB()
    {
        db.emptyDatabase();
    }

    @AfterClass
    public static void tearDown() {
        db.close();
        try {
            Files.delete(Paths.get(dbPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void selectByCoordinateTest() {
        Point p1 = new Point(2, 3);
        db.save(p1);

        PersistenceQuery query = new PersistenceQuery("SELECT p FROM Point p WHERE p.x = :x");
        query.setParameter("x", 2);

        List results = db.executeQuery(query);
        Assert.assertEquals(1, results.size());

    }

    @Test
    public void selectByMultipleCoordinateTest() {
        Point p1 = new Point(2, 3);
        Point p2 = new Point(2, 5);
        db.save(p1);
        db.save(p2);

        PersistenceQuery query = new PersistenceQuery("SELECT p FROM Point p WHERE p.x = :x OR p.y = :y");
        query.setParameter("x", 2);
        query.setParameter("y", 5);

        List results = db.executeQuery(query);
        Assert.assertEquals(2, results.size());

    }





}

